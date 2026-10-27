package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.LocDefinition
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: what the Bot Nav Editor's menus and panel do to the graph
 * (BotNavEditor). Every change goes through [BotNavGraph]'s edits, so it can be undone.
 */
object BotNavEdits {

	private fun graph(): BotNavGraph? = BotNavEditor.graph

	@JvmStatic
	fun undo(client: ClientApi?) {
		val graph: BotNavGraph = graph() ?: return
		BotNavEditor.report(client, if (graph.undo()) "Undone." else "Nothing to undo.")
		dropRemovedSelection()
	}

	@JvmStatic
	fun redo(client: ClientApi?) {
		val graph: BotNavGraph = graph() ?: return
		BotNavEditor.report(client, if (graph.redo()) "Redone." else "Nothing to redo.")
		dropRemovedSelection()
	}

	/** An undo can take away what was selected: an edge or area added, or a file replaced. */
	private fun dropRemovedSelection() {
		val graph: BotNavGraph = graph() ?: return
		val edge: BotNavEdge? = BotNavEditor.selectedEdge
		if (edge != null && (edge.removed || !edge.file.edges.contains(edge) || !graph.files.contains(edge.file))) {
			BotNavEditor.select(BotNavEditor.selectedNode, null, BotNavEditor.selectedArea)
		}
		val area: BotNavArea? = BotNavEditor.selectedArea
		if (area != null && (area.removed || !area.file.areas.contains(area) || !graph.files.contains(area.file))) {
			BotNavEditor.select(BotNavEditor.selectedNode, BotNavEditor.selectedEdge, null)
		}
	}

	// --- nodes and edges ---

	@JvmStatic
	fun addNode(client: ClientApi, tile: Int) {
		val graph: BotNavGraph = graph() ?: return
		graph.addPendingNode(tile)
		BotNavEditor.select(tile, null, null)
		BotNavEditor.report(client, "Node at " + BotNavTile.format(tile) + ": it is saved once an edge joins it.")
	}

	@JvmStatic
	fun deleteNode(client: ClientApi, tile: Int) {
		val graph: BotNavGraph = graph() ?: return
		val count: Int = graph.nodes()[tile]?.size ?: 0
		graph.removeNode(tile)
		BotNavEditor.select(-1, null, null)
		BotNavEditor.report(client, "Deleted the node at " + BotNavTile.format(tile) + " and its " + count + " edge(s).")
	}

	@JvmStatic
	fun moveNodeTo(client: ClientApi, tile: Int) {
		val graph: BotNavGraph = graph() ?: return
		val from: Int = BotNavEditor.modeTile
		BotNavEditor.cancelMode()
		if (from < 0 || from == tile) {
			return
		}
		graph.moveNode(from, tile)
		BotNavEditor.select(tile, null, null)
		BotNavEditor.report(client, "Moved the node to " + BotNavTile.format(tile) + ".")
	}

	/** Ends the edge started at the mode's tile: a walk both ways, or a one-way edge to fill with actions in the panel. */
	@JvmStatic
	fun finishEdge(client: ClientApi, tile: Int, action: Boolean) {
		val graph: BotNavGraph = graph() ?: return
		val from: Int = BotNavEditor.modeTile
		BotNavEditor.cancelMode()
		if (from < 0 || from == tile) {
			BotNavEditor.report(client, "An edge needs two different tiles.")
			return
		}
		val file: BotNavFile = BotNavEditor.edgesFileFor(graph, from)
		val actions: TomlArray? = if (action) template(if (BotNavTile.level(from) != BotNavTile.level(tile)) "stairs" else "walk", from, tile) else null
		val edge: BotNavEdge = graph.addEdge(file, BotNavEdgeState(from, tile, if (action) BotNavEditor.manhattan(from, tile).coerceAtLeast(1) else 0, actions, null, null, LinkedHashMap()))
		graph.pendingNodes.remove(from)
		graph.pendingNodes.remove(tile)
		BotNavEditor.select(-1, edge, null)
		BotNavEditor.report(client, (if (action) "Action edge" else "Walk edge") + " added to " + file.path + (if (action) ": set its actions in the panel, or Shift right-click the object." else "."))
	}

	@JvmStatic
	fun reverse(client: ClientApi?, edge: BotNavEdge) {
		val graph: BotNavGraph = graph() ?: return
		graph.changeEdge(edge, edge.state.with(from = edge.state.to, to = edge.state.from))
		BotNavEditor.report(client, "Reversed " + BotNavEditor.edgeName(edge) + ".")
	}

	@JvmStatic
	fun deleteEdge(client: ClientApi?, edge: BotNavEdge) {
		val graph: BotNavGraph = graph() ?: return
		graph.removeEdge(edge)
		if (BotNavEditor.selectedEdge === edge) {
			BotNavEditor.select(-1, null, null)
		}
		BotNavEditor.report(client, "Deleted " + BotNavEditor.edgeName(edge) + ".")
	}

	/** A walk (both ways, no actions) becomes one way with a walk action, and back. */
	@JvmStatic
	fun toggleOneWay(client: ClientApi?, edge: BotNavEdge) {
		val graph: BotNavGraph = graph() ?: return
		val state: BotNavEdgeState = edge.state
		if (state.isWalk) {
			graph.changeEdge(edge, state.with(actions = template("walk", state.from, state.to), cost = state.weight().coerceAtLeast(1)))
			BotNavEditor.report(client, "One way now: " + BotNavEditor.edgeName(edge) + ".")
		} else {
			graph.changeEdge(edge, state.with(actions = null))
			BotNavEditor.report(client, "A walk both ways now (its actions are gone): " + BotNavEditor.edgeName(edge) + ".")
		}
	}

	/** Puts a template's actions on the edge, after the ones it has. */
	@JvmStatic
	fun addTemplate(client: ClientApi?, edge: BotNavEdge, name: String) {
		val graph: BotNavGraph = graph() ?: return
		val state: BotNavEdgeState = edge.state
		val actions: ArrayList<TomlValue> = ArrayList(state.actions?.items ?: emptyList())
		actions.addAll(template(name, state.from, state.to).items)
		graph.changeEdge(edge, state.with(actions = TomlArray(actions), cost = if (state.cost > 0) state.cost else state.weight().coerceAtLeast(1)))
		BotNavEditor.report(client, "Added a " + name + " to " + BotNavEditor.edgeName(edge) + ".")
	}

	/**
	 * The shapes Void's files use (its web editor's templates): an object then a walk through it for a
	 * door, an object whose success is the other level for stairs, a plain walk.
	 */
	@JvmStatic
	fun template(name: String, from: Int, to: Int): TomlArray {
		val actions: ArrayList<TomlValue> = ArrayList()
		when (name) {
			"door" -> {
				actions.add(action("object", table("option" to TomlString("Open"), "id" to TomlString(""), "x" to int(BotNavTile.x(to)), "y" to int(BotNavTile.y(to)), "success" to table("object" to table("id" to TomlString(""), "x" to int(BotNavTile.x(to)), "y" to int(BotNavTile.y(to)))))))
				actions.add(walk(to, 1))
			}
			"stairs" -> {
				val up: Boolean = BotNavTile.level(to) > BotNavTile.level(from)
				actions.add(action("object", table("option" to TomlString(if (up) "Climb-up" else "Climb-down"), "id" to TomlString(""), "x" to int(BotNavTile.x(from)), "y" to int(BotNavTile.y(from)), "success" to table("tile" to table("level" to int(BotNavTile.level(to)))))))
			}
			"object" -> {
				actions.add(action("object", table("option" to TomlString(""), "id" to TomlString(""), "x" to int(BotNavTile.x(to)), "y" to int(BotNavTile.y(to)), "success" to table("tile" to tileTable(to, 1)))))
				actions.add(walk(to, 1))
			}
			else -> actions.add(walk(to, 0))
		}
		return TomlArray(actions)
	}

	private fun int(value: Int): TomlInt = TomlInt(value.toLong())

	private fun table(vararg entries: Pair<String, TomlValue>): TomlTable {
		val map: LinkedHashMap<String, TomlValue> = LinkedHashMap()
		for ((key, value) in entries) {
			map[key] = value
		}
		return TomlTable(map)
	}

	private fun action(type: String, fields: TomlTable): TomlTable = table(type to fields)

	/** `{ x, y, radius }` with the level when it is not 0, as an object's success tile. */
	private fun tileTable(tile: Int, radius: Int): TomlTable {
		val entries: LinkedHashMap<String, TomlValue> = LinkedHashMap(BotNavTile.table(tile).entries)
		if (radius > 0) {
			entries["radius"] = int(radius)
		}
		return TomlTable(entries)
	}

	private fun walk(to: Int, radius: Int): TomlTable = action("tile", tileTable(to, radius))

	/**
	 * Sets the selected edge's object action from an object Shift right-clicked in the game: its
	 * option, Void's name for it (objects.tsv) and its tile; a walk to the edge's end follows on the
	 * same level, and stairs to another level end where the object sends the player.
	 */
	@JvmStatic
	fun useObject(client: ClientApi, locId: Int, x: Int, y: Int, op: Int) {
		val graph: BotNavGraph = graph() ?: return
		val edge: BotNavEdge = BotNavEditor.selectedEdge ?: return
		val type: LocDefinition = client.locType(locId) ?: return
		val option: String = type.action(op) ?: return
		val name: String? = BotNavEditor.objectNames[locId] ?: BotNavEditor.objectNames[client.resolveLoc(locId)]
		val state: BotNavEdgeState = edge.state
		val crossLevel: Boolean = BotNavTile.level(state.from) != BotNavTile.level(state.to)
		val success: TomlTable = if (crossLevel) table("tile" to table("level" to int(BotNavTile.level(state.to)))) else table("tile" to tileTable(state.to, 1))
		val fields: TomlTable = table("option" to TomlString(option), "id" to TomlString(name ?: locId.toString()), "x" to int(x), "y" to int(y), "success" to success)
		val actions: ArrayList<TomlValue> = ArrayList()
		actions.add(action("object", fields))
		if (!crossLevel) {
			actions.add(walk(state.to, 0))
		}
		graph.changeEdge(edge, state.with(actions = TomlArray(actions), cost = if (state.cost > 0) state.cost else state.weight().coerceAtLeast(1)))
		BotNavEditor.report(client, "$option " + (name ?: "object $locId (not in Void's object list: name it in the panel)") + " is now the edge's action.")
	}

	// --- areas ---

	@JvmStatic
	fun deleteArea(client: ClientApi?, area: BotNavArea) {
		val graph: BotNavGraph = graph() ?: return
		graph.removeArea(area)
		if (BotNavEditor.selectedArea === area) {
			BotNavEditor.select(-1, null, null)
		}
		BotNavEditor.report(client, "Deleted area " + area.state.name + ".")
	}

	@JvmStatic
	fun finishRectangle(client: ClientApi, tile: Int) {
		val first: Int = BotNavEditor.modeTile
		BotNavEditor.cancelMode()
		if (first < 0) {
			return
		}
		val xs = intArrayOf(minOf(BotNavTile.x(first), BotNavTile.x(tile)), maxOf(BotNavTile.x(first), BotNavTile.x(tile)))
		val ys = intArrayOf(minOf(BotNavTile.y(first), BotNavTile.y(tile)), maxOf(BotNavTile.y(first), BotNavTile.y(tile)))
		newArea(client, first, xs, ys)
	}

	@JvmStatic
	fun finishPolygon(client: ClientApi, tile: Int) {
		val points: ArrayList<Int> = ArrayList(BotNavEditor.polygon)
		BotNavEditor.cancelMode()
		if (points.lastOrNull() != tile) {
			points.add(tile)
		}
		if (points.size < 3) {
			BotNavEditor.report(client, "A polygon needs three points or more.")
			return
		}
		newArea(client, points[0], IntArray(points.size) { BotNavTile.x(points[it]) }, IntArray(points.size) { BotNavTile.y(points[it]) })
	}

	private fun newArea(client: ClientApi, at: Int, xs: IntArray, ys: IntArray) {
		val graph: BotNavGraph = graph() ?: return
		var name = "new_area"
		var n = 2
		while (graph.area(name) != null) {
			name = "new_area_" + n++
		}
		val entries: LinkedHashMap<String, TomlValue> = LinkedHashMap()
		entries["level"] = int(BotNavTile.level(at))
		val file: BotNavFile = BotNavEditor.areasFileFor(graph, at)
		val area: BotNavArea = graph.addArea(file, BotNavAreaState(name, TomlTable(entries)).withPoints(xs, ys))
		BotNavEditor.select(-1, null, area)
		BotNavEditor.report(client, "Area $name added to " + file.path + ": name it in the panel.")
	}

	/** The index of the selected area's point (corner of a rectangle) on the tile, or -1. */
	@JvmStatic
	fun pointAt(area: BotNavArea, tile: Int): Int {
		val xs: IntArray = area.state.xs()
		val ys: IntArray = area.state.ys()
		if (area.state.isRectangle()) {
			// the four corners: 0 (x0, y0), 1 (x1, y0), 2 (x1, y1), 3 (x0, y1)
			for (corner in 0 until 4) {
				val x: Int = xs[if (corner == 1 || corner == 2) 1 else 0]
				val y: Int = ys[if (corner >= 2) 1 else 0]
				if (x == BotNavTile.x(tile) && y == BotNavTile.y(tile)) {
					return corner
				}
			}
			return -1
		}
		for (i in xs.indices) {
			if (i < ys.size && xs[i] == BotNavTile.x(tile) && ys[i] == BotNavTile.y(tile)) {
				return i
			}
		}
		return -1
	}

	@JvmStatic
	fun movePointTo(client: ClientApi, tile: Int) {
		val graph: BotNavGraph = graph() ?: return
		val area: BotNavArea = BotNavEditor.selectedArea ?: return
		val i: Int = BotNavEditor.movingPoint
		BotNavEditor.cancelMode()
		val xs: IntArray = area.state.xs()
		val ys: IntArray = area.state.ys()
		if (area.state.isRectangle()) {
			xs[if (i == 1 || i == 2) 1 else 0] = BotNavTile.x(tile)
			ys[if (i >= 2) 1 else 0] = BotNavTile.y(tile)
			val x0: Int = minOf(xs[0], xs[1])
			val y0: Int = minOf(ys[0], ys[1])
			graph.changeArea(area, area.state.withPoints(intArrayOf(x0, maxOf(xs[0], xs[1])), intArrayOf(y0, maxOf(ys[0], ys[1]))))
		} else if (i in xs.indices && i in ys.indices) {
			xs[i] = BotNavTile.x(tile)
			ys[i] = BotNavTile.y(tile)
			graph.changeArea(area, area.state.withPoints(xs, ys))
		}
		BotNavEditor.report(client, "Moved a point of " + area.state.name + ".")
	}
}
