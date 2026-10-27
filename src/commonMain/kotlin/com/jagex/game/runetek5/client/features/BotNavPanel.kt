package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.Graphics
import com.jagex.game.runetek5.plugin.api.KeyCode
import com.jagex.game.runetek5.plugin.api.PanelOverlay
import com.jagex.game.runetek5.plugin.api.Size
import com.jagex.game.runetek5.plugin.api.Viewport
import com.jagex.game.runetek5.plugin.api.events.KeyPressed
import com.jagex.game.runetek5.plugin.api.events.KeyTyped
import com.jagex.game.runetek5.plugin.api.events.MouseClicked
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: the Bot Nav Editor's panel (BotNavEditor). It shows what is
 * selected, the edge, area or node, and edits it: click a field to type its value (an edge's
 * actions and requirements as Void's TOML, e.g. `[{ tile = { x = 1, y = 2 } }]`; Ctrl+V pastes,
 * Enter keeps it, Escape leaves it), or a button to reverse, delete or add a door, stairs, object
 * or walk action. Undo, Redo and Export sit at its foot.
 */
object BotNavPanel {

	private const val WIDTH: Int = 330

	private const val PAD: Int = 5

	private const val MAX_TEXT: Int = 4000

	private const val WHITE: Int = 0xFFFFFFFF.toInt()

	private const val GREY: Int = 0xFFB0B0B0.toInt()

	private const val YELLOW: Int = 0xFFFFE040.toInt()

	private const val RED: Int = 0xFFFF6060.toInt()

	private const val BLACK: Int = 0xFF000000.toInt()

	private const val HOVER: Int = 0x30FFFFFF

	private const val BUTTON: Int = 0xFF3A3A3A.toInt()

	private const val BUTTON_EDGE: Int = 0xFF707070.toInt()

	/** A place on the panel that does something when clicked. */
	private class Hit(@JvmField val x: Int, @JvmField val y: Int, @JvmField val width: Int, @JvmField val height: Int, @JvmField val action: () -> Unit)

	private val hits: ArrayList<Hit> = ArrayList()

	/** The field being typed (its key), the text so far, and what keeps it. */
	@JvmField
	var editing: String? = null

	private val text: StringBuilder = StringBuilder()

	private var apply: ((String) -> String?)? = null

	private val size: Size = Size(0, 0)

	@JvmField
	val panel: PanelOverlay = object : PanelOverlay("Bot nav editor") {
		override fun render(g: Graphics, view: Viewport): Size? {
			hits.clear()
			val client: ClientApi = BotNavEditor.context()?.client() ?: return null
			if (!BotNavEditor.active(client) || !g.fontReady()) {
				return null
			}
			// the background first, as tall as the last frame's panel: the layout learns its height as it draws
			g.fillRect(0, 0, WIDTH, lastHeight, background(backgroundColour))
			val layout = Layout(g, client, boundsX, boundsY)
			layout.draw()
			lastHeight = layout.y + PAD
			g.drawRect(0, 0, WIDTH, lastHeight, BUTTON_EDGE)
			return size.set(WIDTH, lastHeight)
		}
	}

	/** The panel's height last frame. */
	private var lastHeight: Int = 0

	/** The panel colour of the Overlays settings ([standard]) at the plugin's own background opacity (0 to 100 %). */
	@JvmStatic
	fun background(standard: Int): Int {
		val alpha: Int = (255 * BotNavEditor.panelOpacity + 50) / 100
		return (alpha shl 24) or (standard and 0xFFFFFF)
	}

	@JvmStatic
	fun stopEditing() {
		editing = null
		apply = null
		text.setLength(0)
	}

	private fun startEditing(key: String, value: String, keep: (String) -> String?) {
		editing = key
		text.setLength(0)
		text.append(value)
		apply = keep
	}

	// --- input ---

	@JvmStatic
	fun clicked(event: MouseClicked) {
		val client: ClientApi = BotNavEditor.context()?.client() ?: return
		if (!BotNavEditor.active(client) || panel.boundsWidth == 0) {
			return
		}
		val x: Int = event.x - panel.boundsX
		val y: Int = event.y - panel.boundsY
		if (x < 0 || y < 0 || x >= panel.boundsWidth || y >= panel.boundsHeight || event.button != 0) {
			return
		}
		event.consume()
		for (hit in hits) {
			if (x >= hit.x && y >= hit.y && x < hit.x + hit.width && y < hit.y + hit.height) {
				hit.action()
				return
			}
		}
	}

	@JvmStatic
	fun typed(event: KeyTyped) {
		if (editing == null) {
			return
		}
		event.consume()
		val c: Char = event.keyChar
		if (c >= ' ' && c != '\u007f' && text.length < MAX_TEXT) {
			text.append(c)
		}
	}

	/** Enter, Escape, Backspace and Ctrl+V while a field is typed; true when the key was the panel's. */
	@JvmStatic
	fun keyPressed(event: KeyPressed): Boolean {
		val key: String = editing ?: return false
		val client: ClientApi? = BotNavEditor.context()?.client()
		event.consume()
		when (event.keyCode) {
			BotNavEditor.KEY_ENTER -> {
				val error: String? = apply?.invoke(text.toString().trim())
				if (error == null) {
					stopEditing()
				} else {
					BotNavEditor.fail("$key: $error")
				}
			}
			BotNavEditor.KEY_ESCAPE -> stopEditing()
			BotNavEditor.KEY_BACKSPACE -> if (text.isNotEmpty()) text.setLength(text.length - 1)
			else -> {
				val ctrl: Boolean = client != null && client.isKeyHeld(KeyCode.CONTROL)
				if ((ctrl && event.keyChar.lowercaseChar() == 'v') || event.keyChar == '\u0016') {
					val pasted: String = client?.clipboard() ?: ""
					for (c in pasted) {
						if (text.length < MAX_TEXT) {
							text.append(if (c < ' ') ' ' else c)
						}
					}
				}
			}
		}
		return true
	}

	// --- the layout, made anew each frame ---

	private class Layout(val g: Graphics, val client: ClientApi, val screenX: Int, val screenY: Int) {
		@JvmField
		var y: Int = PAD

		val line: Int = g.fontHeight() + 3

		fun draw() {
			val graph: BotNavGraph = BotNavEditor.graph ?: return
			text(PAD, "Bot nav editor", YELLOW)
			val changed: Int = graph.files.count { it.dirty || it.fromWorkingCopy }
			g.textRight(WIDTH - PAD, y - 3, if (changed == 0) "no edits" else "$changed file(s) edited", GREY, BLACK)
			modeLine()
			val edge: BotNavEdge? = BotNavEditor.selectedEdge
			val area: BotNavArea? = BotNavEditor.selectedArea
			when {
				edge != null -> edge(graph, edge)
				area != null -> area(graph, area)
				BotNavEditor.selectedNode >= 0 -> node(graph, BotNavEditor.selectedNode)
				else -> {
					text(PAD, "Shift right-click a tile: Bot nav.", GREY)
					text(PAD, "Shift right-click an object to make it", GREY)
					text(PAD, "the selected edge's action.", GREY)
				}
			}
			y += 3
			var x: Int = PAD
			x = button(x, "Undo", graph.canUndo()) { BotNavEdits.undo(client) }
			x = button(x, "Redo", graph.canRedo()) { BotNavEdits.redo(client) }
			x = button(x, "Export", true) { BotNavEditor.export(client) }
			if (BotNavEditor.selectedNode >= 0 || edge != null || area != null) {
				button(x, "Deselect", true) { BotNavEditor.select(-1, null, null) }
			}
			y += line + 4
			val status: String = BotNavEditor.status
			if (status.isNotEmpty()) {
				wrapped(status, if (BotNavEditor.statusError) RED else GREY)
			}
		}

		private fun modeLine() {
			val what: String = when (BotNavEditor.mode) {
				BotNavEditor.MODE_EDGE -> "Edge from " + BotNavTile.format(BotNavEditor.modeTile) + ": Shift right-click its end"
				BotNavEditor.MODE_MOVE_NODE -> "Moving " + BotNavTile.format(BotNavEditor.modeTile) + ": Shift right-click where"
				BotNavEditor.MODE_AREA_RECT -> "Area from " + BotNavTile.format(BotNavEditor.modeTile) + ": Shift right-click its corner"
				BotNavEditor.MODE_AREA_POLYGON -> "Polygon, " + BotNavEditor.polygon.size + " point(s): add more, then close"
				BotNavEditor.MODE_MOVE_POINT -> "Moving an area point: Shift right-click where"
				else -> return
			}
			wrapped("$what (Esc cancels).", YELLOW)
		}

		private fun edge(graph: BotNavGraph, edge: BotNavEdge) {
			val state: BotNavEdgeState = edge.state
			text(PAD, (if (state.isWalk) "Walk edge (both ways)" else "Action edge (one way)") + " in " + edge.file.path, WHITE)
			field("from", BotNavTile.format(state.from), coordinates(state.from)) { value -> moveEnd(graph, edge, value, true) }
			field("to", BotNavTile.format(state.to), coordinates(state.to)) { value -> moveEnd(graph, edge, value, false) }
			val cost: String = if (state.cost > 0) state.cost.toString() else "none (" + state.weight() + ")"
			field("cost", cost, if (state.cost > 0) state.cost.toString() else "") { value ->
				val n: Int? = if (value.isEmpty()) 0 else value.toIntOrNull()
				if (n == null || n < 0) "expected a number" else {
					graph.changeEdge(edge, edge.state.with(cost = n))
					null
				}
			}
			field("comment", state.comment ?: "", state.comment ?: "") { value ->
				graph.changeEdge(edge, edge.state.with(comment = value.ifEmpty { null }))
				null
			}
			val actions: List<TomlValue> = state.actions?.items.orEmpty()
			field("actions", if (actions.isEmpty()) "none (walk)" else actions[0].format(), state.actions?.format() ?: "[]") { value -> array(value) { a -> graph.changeEdge(edge, edge.state.with(actions = a)) } }
			for (i in 1 until actions.size) {
				field("", actions[i].format(), state.actions?.format() ?: "[]", "actions") { value -> array(value) { a -> graph.changeEdge(edge, edge.state.with(actions = a)) } }
			}
			field("requires", state.requires?.format() ?: "none", state.requires?.format() ?: "[]") { value -> array(value) { a -> graph.changeEdge(edge, edge.state.with(requires = a)) } }
			for ((key, value) in state.extra) {
				text(PAD, "$key = " + value.format(), RED)
			}
			val label: String? = BotNavOverlay.label(client, state)
			if (label != null) {
				text(PAD, label, BotNavEditor.colourAction)
			}
			y += 2
			var x: Int = PAD
			x = button(x, "Reverse", true) { BotNavEdits.reverse(client, edge) }
			x = button(x, if (state.isWalk) "One way" else "Both ways", true) { BotNavEdits.toggleOneWay(client, edge) }
			button(x, "Delete", true) { BotNavEdits.deleteEdge(client, edge) }
			y += line + 4
			x = PAD
			text(x, "Add:", GREY)
			y -= line
			x += g.textWidth("Add:") + 6
			for (name in TEMPLATES) {
				x = button(x, ClientPage.capital(name), true) { BotNavEdits.addTemplate(client, edge, name) }
			}
			y += line + 4
		}

		private fun area(graph: BotNavGraph, area: BotNavArea) {
			val state: BotNavAreaState = area.state
			text(PAD, (if (state.isRectangle()) "Rectangle area" else "Polygon area, " + state.xs().size + " points") + " in " + area.file.path, WHITE)
			field("name", state.name, state.name) { value ->
				val other: BotNavArea? = graph.area(value)
				if (!value.matches(NAME)) "expected letters, digits and _" else if (other != null && other !== area) "$value is taken" else {
					graph.changeArea(area, BotNavAreaState(value, area.state.table))
					null
				}
			}
			val level: Int = state.level()
			field("level", if (level < 0) "every level" else level.toString(), if (level < 0) "" else level.toString()) { value ->
				val n: Int? = if (value.isEmpty()) -1 else value.toIntOrNull()
				if (n == null || n < -1 || n > 3) "expected 0 to 3, or nothing for every level" else {
					val entries: LinkedHashMap<String, TomlValue> = LinkedHashMap(area.state.table.entries)
					if (n < 0) entries.remove("level") else entries["level"] = TomlInt(n.toLong())
					graph.changeArea(area, BotNavAreaState(area.state.name, BotNavAreaState.reorder(entries)))
					null
				}
			}
			field("x", state.xs().joinToString(", "), state.xs().joinToString(", ")) { value -> points(graph, area, value, true) }
			field("y", state.ys().joinToString(", "), state.ys().joinToString(", ")) { value -> points(graph, area, value, false) }
			field("tags", state.tags().joinToString(", ").ifEmpty { "none" }, state.tags().joinToString(", ")) { value ->
				val names: List<String> = value.split(',').map { it.trim() }.filter { it.isNotEmpty() }
				if (names.any { !it.matches(NAME) }) "expected names split by commas" else {
					val entries: LinkedHashMap<String, TomlValue> = LinkedHashMap(area.state.table.entries)
					if (names.isEmpty()) entries.remove("tags") else entries["tags"] = TomlArray(ArrayList(names.map { TomlString(it) }))
					graph.changeArea(area, BotNavAreaState(area.state.name, BotNavAreaState.reorder(entries)))
					null
				}
			}
			val others: LinkedHashMap<String, TomlValue> = LinkedHashMap()
			for ((key, value) in state.table.entries) {
				if (key != "x" && key != "y" && key != "level" && key != "tags") {
					others[key] = value
				}
			}
			field("other", if (others.isEmpty()) "none" else TomlTable(others).format(), TomlTable(others).format()) { value ->
				val parsed: TomlValue = try {
					BotNavToml.parseValue(value.ifEmpty { "{}" })
				} catch (error: TomlException) {
					return@field error.message
				}
				if (parsed !is TomlTable || parsed.entries.keys.any { it == "x" || it == "y" || it == "level" || it == "tags" }) "expected a table such as { bot_capacity = 2 }" else {
					val entries: LinkedHashMap<String, TomlValue> = LinkedHashMap()
					for (key in arrayOf("x", "y", "level", "tags")) {
						area.state.table.entries[key]?.let { entries[key] = it }
					}
					entries.putAll(parsed.entries)
					graph.changeArea(area, BotNavAreaState(area.state.name, BotNavAreaState.reorder(entries)))
					null
				}
			}
			text(PAD, "Shift right-click a corner tile to move it.", GREY)
			y += 2
			button(PAD, "Delete", true) { BotNavEdits.deleteArea(client, area) }
			y += line + 4
		}

		private fun node(graph: BotNavGraph, tile: Int) {
			val edges: List<BotNavEdge> = graph.nodes()[tile].orEmpty()
			text(PAD, "Node " + BotNavTile.format(tile) + (if (edges.isEmpty()) ", no edges yet (not saved)" else ", " + edges.size + " edge(s):"), WHITE)
			for (edge in edges.take(10)) {
				val top: Int = y
				text(PAD + 6, BotNavEditor.edgeName(edge), if (edge.state.isWalk) BotNavEditor.colourWalk else BotNavEditor.colourAction)
				hits.add(Hit(0, top, WIDTH, line) { BotNavEditor.select(-1, edge, null) })
				hover(top)
			}
		}

		/** A label and its value; clicking it types [raw] into the field [key], kept by [keep] (an error, or null). */
		private fun field(label: String, shown: String, raw: String, key: String = label, keep: (String) -> String?) {
			val top: Int = y
			// an edge's later actions are rows of the one field: the first row shows the typing
			val typing: Boolean = editing == key && label.isNotEmpty()
			if (label.isNotEmpty()) {
				g.text(PAD, y + line - 4, label, GREY, BLACK)
			}
			val x: Int = PAD + 62
			val room: Int = WIDTH - x - PAD
			if (typing) {
				g.fillRect(x - 2, top, room + 4, line, 0x60404000)
				g.text(x, y + line - 4, tail(text.toString() + "_", room), YELLOW, BLACK)
			} else {
				g.text(x, y + line - 4, fit(shown, room), WHITE, BLACK)
			}
			y += line
			hits.add(Hit(0, top, WIDTH, line) { startEditing(key, raw, keep) })
			hover(top)
		}

		private fun hover(top: Int) {
			val mx: Int = client.mouseX() - screenX
			val my: Int = client.mouseY() - screenY
			if (mx in 0 until WIDTH && my >= top && my < top + line) {
				g.fillRect(0, top, WIDTH, line, HOVER)
			}
		}

		private fun text(x: Int, text: String, colour: Int) {
			g.text(x, y + line - 4, fit(text, WIDTH - x - PAD), colour, BLACK)
			y += line
		}

		private fun wrapped(text: String, colour: Int) {
			var rest: String = text
			while (rest.isNotEmpty()) {
				var cut: Int = rest.length
				while (cut > 1 && g.textWidth(rest.substring(0, cut)) > WIDTH - 2 * PAD) {
					val space: Int = rest.lastIndexOf(' ', cut - 1)
					cut = if (space > 0) space else cut - 1
				}
				text(PAD, rest.substring(0, cut), colour)
				rest = rest.substring(cut).trimStart()
			}
		}

		/** A button at (x, y); the x after it. */
		private fun button(x: Int, label: String, on: Boolean, action: () -> Unit): Int {
			val width: Int = g.textWidth(label) + 10
			g.fillRect(x, y, width, line + 2, BUTTON)
			g.drawRect(x, y, width, line + 2, BUTTON_EDGE)
			g.textCentered(x + width / 2, y + line - 3, label, if (on) WHITE else GREY, BLACK)
			if (on) {
				hits.add(Hit(x, y, width, line + 2, action))
				val mx: Int = client.mouseX() - screenX
				val my: Int = client.mouseY() - screenY
				if (mx >= x && mx < x + width && my >= y && my < y + line + 2) {
					g.fillRect(x, y, width, line + 2, HOVER)
				}
			}
			return x + width + 4
		}

		private fun fit(text: String, room: Int): String {
			if (g.textWidth(text) <= room) {
				return text
			}
			var end: Int = text.length
			while (end > 0 && g.textWidth(text.substring(0, end) + "...") > room) {
				end--
			}
			return text.substring(0, end) + "..."
		}

		/** The end of [text] that fits, for typing: the cursor stays in sight. */
		private fun tail(text: String, room: Int): String {
			if (g.textWidth(text) <= room) {
				return text
			}
			var start = 0
			while (start < text.length && g.textWidth("..." + text.substring(start)) > room) {
				start++
			}
			return "..." + text.substring(start)
		}

		private fun moveEnd(graph: BotNavGraph, edge: BotNavEdge, value: String, from: Boolean): String? {
			val t: Int = tile(value) ?: return "expected x, y or x, y, level"
			graph.changeEdge(edge, if (from) edge.state.with(from = t) else edge.state.with(to = t))
			return null
		}

		private fun points(graph: BotNavGraph, area: BotNavArea, value: String, isX: Boolean): String? {
			val numbers: List<Int?> = value.split(',').map { it.trim().toIntOrNull() }
			val other: IntArray = if (isX) area.state.ys() else area.state.xs()
			if (numbers.any { it == null } || numbers.size != other.size) {
				return "expected " + other.size + " numbers split by commas"
			}
			val these = IntArray(numbers.size) { numbers[it]!! }
			graph.changeArea(area, if (isX) area.state.withPoints(these, other) else area.state.withPoints(other, these))
			return null
		}
	}

	/** The template buttons (BotNavEdits.template). */
	private val TEMPLATES: Array<String> = arrayOf("door", "stairs", "object", "walk")

	private val NAME: Regex = Regex("[a-z0-9_]+")

	private fun coordinates(tile: Int): String {
		val level: Int = BotNavTile.level(tile)
		return BotNavTile.x(tile).toString() + ", " + BotNavTile.y(tile) + (if (level != 0) ", $level" else "")
	}

	/** `x, y` or `x, y, level` (brackets allowed). */
	@JvmStatic
	fun tile(value: String): Int? {
		val parts: List<Int?> = value.trim('(', ')', ' ').split(',').map { it.trim().toIntOrNull() }
		if (parts.size !in 2..3 || parts.any { it == null }) {
			return null
		}
		val x: Int = parts[0]!!
		val y: Int = parts[1]!!
		val level: Int = if (parts.size == 3) parts[2]!! else 0
		if (x !in 0..0x3FFF || y !in 0..0x3FFF || level !in 0..3) {
			return null
		}
		return BotNavTile.pack(x, y, level)
	}

	/** A TOML array (nothing for none) given to [keep]; the error, or null. */
	private fun array(value: String, keep: (TomlArray?) -> Unit): String? {
		if (value.isEmpty() || value == "[]") {
			keep(null)
			return null
		}
		val parsed: TomlValue = try {
			BotNavToml.parseValue(value)
		} catch (error: TomlException) {
			return error.message
		}
		if (parsed !is TomlArray || parsed.items.any { it !is TomlTable }) {
			return "expected an array of tables, such as [{ tile = { x = 1, y = 2 } }]"
		}
		keep(parsed)
		return null
	}
}
