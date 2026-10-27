package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.PluginWorldMap
import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.Graphics
import com.jagex.game.runetek5.plugin.api.Viewport
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/**
 * Not part of the original client: how the Bot Nav Editor draws the graph (BotNavEditor). In the
 * game view each node is a ring on the ground under a pin; a walk is a line with an arrow each way,
 * an action edge a line with one arrow and, near the player, a label of what it does
 * (`Climb-over -> Stile (3197, 3276, 0)`); areas are dashed outlines, the one under the mouse or
 * selected in pink with its name. The minimap and the world map get the edges and nodes too.
 */
object BotNavOverlay {

	private const val TILE: Int = 512

	private const val BLACK: Int = 0xFF000000.toInt()

	private const val WHITE: Int = 0xFFFFFFFF.toInt()

	private const val SELECTED: Int = 0xFFFFE040.toInt()

	private const val HIGHLIGHT: Int = 0xFFFF4FD8.toInt()

	private const val REQUIRES: Int = 0xFFFFE040.toInt()

	private const val LABEL_BACKGROUND: Int = 0xC0000000.toInt()

	private const val PREVIEW: Int = 0xC0FFFFFF.toInt()

	/** How near the mouse (in pixels) an edge's line must be to be hovered. */
	private const val HOVER_PIXELS: Int = 6

	private const val PIN_HEIGHT: Int = 200

	private const val RING_POINTS: Int = 14

	private const val HEAD_POINTS: Int = 10

	private val SIDES: IntArray = intArrayOf(-1, 1)

	/** The edge under the mouse last frame, for the menu. */
	@JvmField
	var hoveredEdge: BotNavEdge? = null

	/** For the smoke test: what the last frame drew. */
	@JvmField
	var drawnNodes: Int = 0

	@JvmField
	var drawnEdges: Int = 0

	@JvmField
	var drawnAreas: Int = 0

	@JvmField
	var drawnLabels: Int = 0

	@JvmField
	var drawnMinimap: Int = 0

	@JvmField
	var drawnWorldMap: Int = 0

	private val point: IntArray = IntArray(2)

	private val other: IntArray = IntArray(2)

	private val area: IntArray = IntArray(4)

	private val ringX: IntArray = IntArray(RING_POINTS)

	private val ringY: IntArray = IntArray(RING_POINTS)

	private val clipped: IntArray = IntArray(4)

	// --- the game view ---

	@JvmStatic
	fun drawScene(g: Graphics, view: Viewport) {
		drawnNodes = 0
		drawnEdges = 0
		drawnAreas = 0
		drawnLabels = 0
		hoveredEdge = null
		val client: ClientApi = BotNavEditor.context()?.client() ?: return
		val graph: BotNavGraph = BotNavEditor.graph ?: return
		if (!BotNavEditor.active(client)) {
			return
		}
		val scene = Scene(client, BotNavEditor.level(client))
		if (BotNavEditor.showAreas) {
			drawAreas(g, view, client, graph, scene)
		}
		val mouseX: Int = client.mouseX()
		val mouseY: Int = client.mouseY()
		var nearest: Int = HOVER_PIXELS * HOVER_PIXELS + 1
		for (edge in graph.edges()) {
			if (!shown(edge.state, scene) || !drawEdge(g, view, client, scene, edge)) {
				continue
			}
			drawnEdges++
			val d: Int = distanceSquared(mouseX, mouseY, lastLine[0], lastLine[1], lastLine[2], lastLine[3])
			if (d < nearest) {
				nearest = d
				hoveredEdge = edge
			}
		}
		for (tile in graph.nodes().keys) {
			drawNode(g, view, client, scene, tile, false)
		}
		for (tile in graph.pendingNodes) {
			drawNode(g, view, client, scene, tile, true)
		}
		if (BotNavEditor.showLabels && BotNavEditor.showActions) {
			drawLabels(g, view, client, graph, scene)
		}
		drawMode(g, view, client, scene)
		val hovered: BotNavEdge? = hoveredEdge
		if (hovered != null && g.fontReady()) {
			client.addTooltip(BotNavEditor.edgeName(hovered) + (if (hovered.state.isWalk) " (walk)" else " (cost " + hovered.state.weight() + ")"))
		}
	}

	/** Whether the settings show the edge, and an end of it is in the scene. */
	private fun shown(state: BotNavEdgeState, scene: Scene): Boolean {
		val kindShown: Boolean = if (state.isWalk) BotNavEditor.showWalks else BotNavEditor.showActions
		return kindShown && (scene.has(state.from) || scene.has(state.to))
	}

	/** The scene's place in the world and the level drawn. */
	private class Scene(client: ClientApi, @JvmField val level: Int) {
		@JvmField
		val baseX: Int = client.sceneBaseX()

		@JvmField
		val baseY: Int = client.sceneBaseY()

		@JvmField
		val size: Int = client.sceneSize()

		fun has(tile: Int): Boolean {
			val x: Int = BotNavTile.x(tile) - baseX
			val y: Int = BotNavTile.y(tile) - baseY
			return BotNavTile.level(tile) == level && x in 0 until size && y in 0 until size
		}

		/** Whether a point in world tiles (corners included) is inside the scene. */
		fun hasPoint(x: Int, y: Int): Boolean = x - baseX in 0..size && y - baseY in 0..size
	}

	/** The last edge's line on the screen, for the hover test. */
	private val lastLine: IntArray = IntArray(4)

	/** A tile's middle on the screen, with the view's offset; false off the screen or the scene. */
	private fun project(client: ClientApi, view: Viewport, scene: Scene, tile: Int, height: Int, out: IntArray): Boolean {
		val x: Int = BotNavTile.x(tile) - scene.baseX
		val y: Int = BotNavTile.y(tile) - scene.baseY
		if (x !in 0 until scene.size || y !in 0 until scene.size || !client.projectTile(x, y, scene.level, height, out)) {
			return false
		}
		out[0] += view.x
		out[1] += view.y
		return true
	}

	private fun drawEdge(g: Graphics, view: Viewport, client: ClientApi, scene: Scene, edge: BotNavEdge): Boolean {
		val state: BotNavEdgeState = edge.state
		val selected: Boolean = edge === BotNavEditor.selectedEdge
		val colour: Int = if (selected) SELECTED else if (state.isWalk) BotNavEditor.colourWalk else BotNavEditor.colourAction
		// an edge to another level ends at the stairs on this one
		val start: Int = if (scene.has(state.from)) state.from else state.to
		var end: Int = if (start == state.from) state.to else state.from
		val crossLevel: Boolean = BotNavTile.level(end) != scene.level
		if (crossLevel) {
			end = objectTile(state) ?: start
		}
		if (!project(client, view, scene, start, 0, point)) {
			return false
		}
		val x1: Int = point[0]
		val y1: Int = point[1]
		var x2: Int = x1
		var y2: Int = y1
		if (end != start && project(client, view, scene, BotNavTile.pack(BotNavTile.x(end), BotNavTile.y(end), scene.level), 0, other)) {
			x2 = other[0]
			y2 = other[1]
		}
		lastLine[0] = x1
		lastLine[1] = y1
		lastLine[2] = x2
		lastLine[3] = y2
		val width: Int = if (selected || edge === hoveredEdge) 3 else 2
		g.thickLine(x1, y1, x2, y2, width, colour)
		if (state.isWalk) {
			arrow(g, x1, y1, x2, y2, 0.35, colour)
			arrow(g, x2, y2, x1, y1, 0.35, colour)
		} else if (start == state.from) {
			arrow(g, x1, y1, x2, y2, 0.6, colour)
		} else {
			arrow(g, x2, y2, x1, y1, 0.6, colour)
		}
		if (crossLevel) {
			diamond(g, x2, y2, colour)
			if (g.fontReady()) {
				val there: Int = BotNavTile.level(if (start == state.from) state.to else state.from)
				val text: String = (if (start == state.from) "to " else "from ") + "level " + there
				g.textCentered(x2, y2 - 8, text, colour, BLACK)
			}
		}
		if (state.requires != null && state.requires.items.isNotEmpty() && g.fontReady()) {
			g.textCentered((x1 + x2) / 2, (y1 + y2) / 2 - 4, "req", REQUIRES, BLACK)
		}
		return true
	}

	/** An arrowhead [at] of the way from (x1, y1) to (x2, y2), pointing at the second. */
	private fun arrow(g: Graphics, x1: Int, y1: Int, x2: Int, y2: Int, at: Double, colour: Int) {
		if (x1 == x2 && y1 == y2) {
			return
		}
		val x: Int = x1 + ((x2 - x1) * at).toInt()
		val y: Int = y1 + ((y2 - y1) * at).toInt()
		val angle: Double = atan2((y2 - y1).toDouble(), (x2 - x1).toDouble())
		for (side in SIDES) {
			val a: Double = angle + PI + side * 0.5
			g.thickLine(x, y, x + (cos(a) * 8).toInt(), y + (sin(a) * 8).toInt(), 2, colour)
		}
	}

	private fun diamond(g: Graphics, x: Int, y: Int, colour: Int) {
		ringX[0] = x
		ringY[0] = y - 6
		ringX[1] = x + 6
		ringY[1] = y
		ringX[2] = x
		ringY[2] = y + 6
		ringX[3] = x - 6
		ringY[3] = y
		g.fillPolygon(ringX, ringY, 4, colour)
		g.polygon(ringX, ringY, 4, BLACK)
	}

	/** The tile of the edge's first object action, when it has one with x and y. */
	@JvmStatic
	fun objectTile(state: BotNavEdgeState): Int? {
		val fields: TomlTable = firstObject(state) ?: return null
		val x: Long = fields.int("x") ?: return null
		val y: Long = fields.int("y") ?: return null
		return BotNavTile.pack(x.toInt(), y.toInt(), BotNavTile.level(state.from))
	}

	private fun firstObject(state: BotNavEdgeState): TomlTable? {
		for (action in state.actions?.items.orEmpty()) {
			val table: TomlTable = action as? TomlTable ?: continue
			table.table("object")?.let { return it }
		}
		return null
	}

	/** What an action edge does, as a label: `Climb-over -> Stile (3197, 3276, 0)`, or the first action's type and option. */
	@JvmStatic
	fun label(client: ClientApi?, state: BotNavEdgeState): String? {
		val obj: TomlTable? = firstObject(state)
		if (obj != null) {
			val id: String = obj.string("id") ?: "?"
			val numeric: Int? = BotNavEditor.objectIds[id]
			val name: String = (if (numeric != null) client?.locName(numeric) else null) ?: id
			val tile: Int = objectTile(state) ?: state.from
			return (obj.string("option") ?: "?") + " -> " + name + " " + BotNavTile.format(tile)
		}
		val first: TomlTable = state.actions?.items?.firstOrNull() as? TomlTable ?: return null
		val type: String = first.entries.keys.firstOrNull() ?: return null
		if (type == "tile") {
			return null
		}
		val fields: TomlTable? = first.table(type)
		val option: String? = fields?.string("option")
		val id: String? = fields?.string("id")
		return type + (if (option != null) ": $option" else "") + (if (id != null) " -> $id" else "")
	}

	private fun drawLabels(g: Graphics, view: Viewport, client: ClientApi, graph: BotNavGraph, scene: Scene) {
		if (!g.fontReady()) {
			return
		}
		val player = client.localPlayer() ?: return
		val px: Int = scene.baseX + player.tileX()
		val py: Int = scene.baseY + player.tileY()
		val shown: HashSet<String> = HashSet()
		for (edge in graph.edges()) {
			val state: BotNavEdgeState = edge.state
			val near: Boolean = maxOf(kotlin.math.abs(BotNavTile.x(state.from) - px), kotlin.math.abs(BotNavTile.y(state.from) - py)) <= BotNavEditor.labelDistance
			if (!state.isWalk && near && scene.has(state.from) && drawLabel(g, view, client, scene, state, shown)) {
				drawnLabels++
			}
		}
	}

	/** One action edge's label at its object; false when another edge already wrote the same, or it is off the screen. */
	private fun drawLabel(g: Graphics, view: Viewport, client: ClientApi, scene: Scene, state: BotNavEdgeState, shown: HashSet<String>): Boolean {
		val text: String = label(client, state) ?: return false
		val at: Int = objectTile(state) ?: state.from
		// both ways through a stile share the object: one label for the pair
		if (!shown.add(text) || !project(client, view, scene, at, 60, point)) {
			return false
		}
		g.textCentered(point[0], point[1], text, BotNavEditor.colourAction, BLACK)
		return true
	}

	private fun drawNode(g: Graphics, view: Viewport, client: ClientApi, scene: Scene, tile: Int, pending: Boolean) {
		if (!scene.has(tile)) {
			return
		}
		val x: Int = BotNavTile.x(tile) - scene.baseX
		val y: Int = BotNavTile.y(tile) - scene.baseY
		val colour: Int = when {
			tile == BotNavEditor.selectedNode -> SELECTED
			pending -> WHITE
			else -> BotNavEditor.colourNode
		}
		// the ring: a circle on the ground, a third of a tile across
		var count = 0
		for (i in 0 until RING_POINTS) {
			val a: Double = 2 * PI * i / RING_POINTS
			val sx: Int = x * TILE + TILE / 2 + (cos(a) * TILE * 0.36).toInt()
			val sy: Int = y * TILE + TILE / 2 + (sin(a) * TILE * 0.36).toInt()
			if (!client.projectPoint(sx, sy, scene.level, 0, point)) {
				return
			}
			ringX[count] = view.x + point[0]
			ringY[count] = view.y + point[1]
			count++
		}
		for (i in 0 until count) {
			val j: Int = (i + 1) % count
			g.thickLine(ringX[i], ringY[i], ringX[j], ringY[j], 2, colour)
		}
		// the pin: a stem from the ground and a head
		if (!project(client, view, scene, tile, 0, point) || !project(client, view, scene, tile, PIN_HEIGHT, other)) {
			return
		}
		g.thickLine(point[0], point[1], other[0], other[1], 2, colour)
		// a filled polygon, not fillCircle: the HD toolkit draws no small circles
		head(g, other[0], other[1], 6, BLACK)
		head(g, other[0], other[1], 5, colour)
		head(g, other[0] - 1, other[1] - 2, 2, WHITE)
		drawnNodes++
	}

	private val headX: IntArray = IntArray(HEAD_POINTS)

	private val headY: IntArray = IntArray(HEAD_POINTS)

	private fun head(g: Graphics, x: Int, y: Int, radius: Int, colour: Int) {
		for (i in 0 until HEAD_POINTS) {
			val a: Double = 2 * PI * i / HEAD_POINTS
			headX[i] = x + (cos(a) * radius).toInt()
			headY[i] = y + (sin(a) * radius).toInt()
		}
		g.fillPolygon(headX, headY, HEAD_POINTS, colour)
	}

	// --- areas ---

	private fun drawAreas(g: Graphics, view: Viewport, client: ClientApi, graph: BotNavGraph, scene: Scene) {
		val hoveredX: Int = scene.baseX + client.hoveredTileX()
		val hoveredY: Int = scene.baseY + client.hoveredTileY()
		val under: BotNavArea? = graph.areaAt(hoveredX, hoveredY, scene.level)
		for (area in graph.areas()) {
			val state: BotNavAreaState = area.state
			val level: Int = state.level()
			if (level >= 0 && level != scene.level) {
				continue
			}
			val highlighted: Boolean = area === BotNavEditor.selectedArea || area === under
			if (outline(g, view, client, scene, state, if (highlighted) HIGHLIGHT else BotNavEditor.colourArea, highlighted)) {
				drawnAreas++
				if (highlighted) {
					nameTag(g, view, client, scene, state)
				}
			}
		}
	}

	/** The area's outline through its corners, as half-tile dashes (solid when [solid]); false when none of it is in the scene. */
	private fun outline(g: Graphics, view: Viewport, client: ClientApi, scene: Scene, state: BotNavAreaState, colour: Int, solid: Boolean): Boolean {
		val xs: IntArray
		val ys: IntArray
		val px: IntArray = state.xs()
		val py: IntArray = state.ys()
		if (px.size != py.size || px.size < 2) {
			return false
		}
		if (px.size == 2) {
			// a rectangle holds both corner tiles whole
			val x0: Int = minOf(px[0], px[1])
			val x1: Int = maxOf(px[0], px[1]) + 1
			val y0: Int = minOf(py[0], py[1])
			val y1: Int = maxOf(py[0], py[1]) + 1
			xs = intArrayOf(x0, x1, x1, x0)
			ys = intArrayOf(y0, y0, y1, y1)
		} else {
			xs = px
			ys = py
		}
		if (xs.max() < scene.baseX || ys.max() < scene.baseY || xs.min() > scene.baseX + scene.size || ys.min() > scene.baseY + scene.size) {
			return false
		}
		var drew = false
		for (i in xs.indices) {
			val j: Int = (i + 1) % xs.size
			val steps: Int = maxOf(1, 2 * maxOf(kotlin.math.abs(xs[j] - xs[i]), kotlin.math.abs(ys[j] - ys[i])))
			var previous = false
			for (s in 0..steps) {
				// in scene units: a corner is a tile's south-west point
				val wx: Double = xs[i] + (xs[j] - xs[i]) * s.toDouble() / steps
				val wy: Double = ys[i] + (ys[j] - ys[i]) * s.toDouble() / steps
				val inside: Boolean = scene.hasPoint(wx.toInt(), wy.toInt()) && client.projectPoint(((wx - scene.baseX) * TILE).toInt().coerceIn(0, scene.size * TILE - 1), ((wy - scene.baseY) * TILE).toInt().coerceIn(0, scene.size * TILE - 1), scene.level, 0, point)
				if (inside && previous && (solid || s % 2 == 1)) {
					g.thickLine(other[0], other[1], view.x + point[0], view.y + point[1], 2, colour)
					drew = true
				}
				if (inside) {
					other[0] = view.x + point[0]
					other[1] = view.y + point[1]
				}
				previous = inside
			}
		}
		return drew
	}

	/** The area's name in a box at its first corner on the screen. */
	private fun nameTag(g: Graphics, view: Viewport, client: ClientApi, scene: Scene, state: BotNavAreaState) {
		if (!g.fontReady()) {
			return
		}
		val xs: IntArray = state.xs()
		val ys: IntArray = state.ys()
		for (i in xs.indices) {
			if (i >= ys.size) {
				return
			}
			val tile: Int = BotNavTile.pack(xs[i], ys[i], scene.level)
			if (project(client, view, scene, tile, 0, point)) {
				val text: String = state.name
				val width: Int = g.textWidth(text) + 6
				val height: Int = g.fontHeight() + 4
				g.fillRect(point[0] - width / 2, point[1] - height, width, height, LABEL_BACKGROUND)
				g.drawRect(point[0] - width / 2, point[1] - height, width, height, HIGHLIGHT)
				g.textCentered(point[0], point[1] - 4, text, WHITE, BLACK)
				return
			}
		}
	}

	/** What the next click does: a line from the edge's start or the node moved, the rectangle or polygon so far. */
	private fun drawMode(g: Graphics, view: Viewport, client: ClientApi, scene: Scene) {
		val hovered: Int = BotNavTile.pack(scene.baseX + client.hoveredTileX(), scene.baseY + client.hoveredTileY(), scene.level)
		when (BotNavEditor.mode) {
			BotNavEditor.MODE_EDGE, BotNavEditor.MODE_MOVE_NODE -> {
				if (project(client, view, scene, BotNavEditor.modeTile, 0, point) && project(client, view, scene, hovered, 0, other)) {
					g.thickLine(point[0], point[1], other[0], other[1], 2, PREVIEW)
					arrow(g, point[0], point[1], other[0], other[1], 0.6, PREVIEW)
				}
			}
			BotNavEditor.MODE_AREA_RECT -> {
				val first: Int = BotNavEditor.modeTile
				val xs = intArrayOf(minOf(BotNavTile.x(first), BotNavTile.x(hovered)), maxOf(BotNavTile.x(first), BotNavTile.x(hovered)))
				val ys = intArrayOf(minOf(BotNavTile.y(first), BotNavTile.y(hovered)), maxOf(BotNavTile.y(first), BotNavTile.y(hovered)))
				outline(g, view, client, scene, BotNavAreaState("", TomlTable(LinkedHashMap())).withPoints(xs, ys), PREVIEW, true)
			}
			BotNavEditor.MODE_AREA_POLYGON -> {
				val points: ArrayList<Int> = ArrayList(BotNavEditor.polygon)
				points.add(hovered)
				for (i in 0 until points.size - 1) {
					if (project(client, view, scene, points[i], 0, point) && project(client, view, scene, points[i + 1], 0, other)) {
						g.thickLine(point[0], point[1], other[0], other[1], 2, PREVIEW)
					}
				}
			}
		}
	}

	// --- the minimap and the world map ---

	@JvmStatic
	fun drawMinimap(g: Graphics) {
		drawnMinimap = 0
		val client: ClientApi = BotNavEditor.context()?.client() ?: return
		val graph: BotNavGraph = BotNavEditor.graph ?: return
		if (!BotNavEditor.active(client) || !BotNavEditor.drawOnMinimap || !client.minimapArea(area)) {
			return
		}
		val scene = Scene(client, BotNavEditor.level(client))
		g.clip(area[0], area[1], area[2], area[3])
		for (edge in graph.edges()) {
			val state: BotNavEdgeState = edge.state
			if (!scene.has(state.from) || !scene.has(state.to)) {
				continue
			}
			if (minimapPoint(client, scene, state.from, point) && minimapPoint(client, scene, state.to, other)) {
				g.line(point[0], point[1], other[0], other[1], if (state.isWalk) BotNavEditor.colourWalk else BotNavEditor.colourAction)
				drawnMinimap++
			}
		}
		for (tile in graph.nodes().keys) {
			if (scene.has(tile) && minimapPoint(client, scene, tile, point)) {
				g.fillRect(point[0] - 1, point[1] - 1, 3, 3, BotNavEditor.colourNode)
			}
		}
		g.unclip()
	}

	private fun minimapPoint(client: ClientApi, scene: Scene, tile: Int, out: IntArray): Boolean =
		client.projectMinimap(BotNavTile.x(tile) - scene.baseX, BotNavTile.y(tile) - scene.baseY, out)

	@JvmStatic
	fun drawWorldMap(g: Graphics) {
		drawnWorldMap = 0
		val client: ClientApi = BotNavEditor.context()?.client() ?: return
		val graph: BotNavGraph = BotNavEditor.graph ?: return
		if (!BotNavEditor.active(client) || !BotNavEditor.drawOnWorldMap || !client.worldMapArea(area)) {
			return
		}
		g.clip(area[0], area[1], area[2], area[3])
		for (edge in graph.edges()) {
			val state: BotNavEdgeState = edge.state
			if (!worldMapPoint(state.from, point) || !worldMapPoint(state.to, other)) {
				continue
			}
			if (clipLine(point[0], point[1], other[0], other[1])) {
				g.line(clipped[0], clipped[1], clipped[2], clipped[3], if (state.isWalk) BotNavEditor.colourWalk else BotNavEditor.colourAction)
				drawnWorldMap++
			}
		}
		if (BotNavEditor.showAreas) {
			for (a in graph.areas()) {
				worldMapArea(g, a.state, if (a === BotNavEditor.selectedArea) HIGHLIGHT else BotNavEditor.colourArea)
			}
		}
		g.unclip()
	}

	private fun worldMapArea(g: Graphics, state: BotNavAreaState, colour: Int) {
		val xs: IntArray = state.xs()
		val ys: IntArray = state.ys()
		if (xs.size != ys.size || xs.size < 2) {
			return
		}
		val level: Int = maxOf(0, state.level())
		val cx: IntArray
		val cy: IntArray
		if (xs.size == 2) {
			cx = intArrayOf(xs[0], xs[1] + 1, xs[1] + 1, xs[0])
			cy = intArrayOf(ys[0], ys[0], ys[1] + 1, ys[1] + 1)
		} else {
			cx = xs
			cy = ys
		}
		for (i in cx.indices) {
			val j: Int = (i + 1) % cx.size
			if (PluginWorldMap.project(cx[i], cy[i], level, point, false) && PluginWorldMap.project(cx[j], cy[j], level, other, false) && clipLine(point[0], point[1], other[0], other[1])) {
				g.line(clipped[0], clipped[1], clipped[2], clipped[3], colour)
			}
		}
	}

	private fun worldMapPoint(tile: Int, out: IntArray): Boolean = PluginWorldMap.project(BotNavTile.x(tile), BotNavTile.y(tile), BotNavTile.level(tile), out, false)

	/** Cuts a line to the world map's box (Liang-Barsky) into [clipped]; false when none of it is inside. */
	private fun clipLine(x1: Int, y1: Int, x2: Int, y2: Int): Boolean {
		val left: Double = area[0].toDouble()
		val top: Double = area[1].toDouble()
		val right: Double = (area[0] + area[2]).toDouble()
		val bottom: Double = (area[1] + area[3]).toDouble()
		val dx: Double = (x2 - x1).toDouble()
		val dy: Double = (y2 - y1).toDouble()
		var t0 = 0.0
		var t1 = 1.0
		val p: DoubleArray = doubleArrayOf(-dx, dx, -dy, dy)
		val q: DoubleArray = doubleArrayOf(x1 - left, right - x1, y1 - top, bottom - y1)
		for (i in 0 until 4) {
			if (p[i] == 0.0) {
				if (q[i] < 0) {
					return false
				}
				continue
			}
			val r: Double = q[i] / p[i]
			if (p[i] < 0) {
				t0 = maxOf(t0, r)
			} else {
				t1 = minOf(t1, r)
			}
		}
		if (t0 > t1) {
			return false
		}
		clipped[0] = (x1 + t0 * dx).toInt()
		clipped[1] = (y1 + t0 * dy).toInt()
		clipped[2] = (x1 + t1 * dx).toInt()
		clipped[3] = (y1 + t1 * dy).toInt()
		return true
	}

	/** The square of the distance from (px, py) to the segment. */
	@JvmStatic
	fun distanceSquared(px: Int, py: Int, x1: Int, y1: Int, x2: Int, y2: Int): Int {
		val dx: Double = (x2 - x1).toDouble()
		val dy: Double = (y2 - y1).toDouble()
		val length: Double = dx * dx + dy * dy
		val t: Double = if (length == 0.0) 0.0 else (((px - x1) * dx + (py - y1) * dy) / length).coerceIn(0.0, 1.0)
		val cx: Double = x1 + t * dx - px
		val cy: Double = y1 + t * dy - py
		val d: Double = cx * cx + cy * cy
		return if (d > Int.MAX_VALUE) Int.MAX_VALUE else d.toInt()
	}
}
