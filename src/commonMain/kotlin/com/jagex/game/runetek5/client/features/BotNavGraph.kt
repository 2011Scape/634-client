package com.jagex.game.runetek5.client.features

import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: the Bot Nav Editor's copy of Void's bot navigation data, the
 * edges of its `*.nav-edges.toml` files and the areas of its `*.areas.toml` files, with the edits
 * made to them. Each file keeps its text as read; [BotNavFile.render] writes it back with only the
 * edges and areas that changed rewritten, so comments and layout survive (as Void's web editor does).
 *
 * The rules are Void's (NavigationGraph.loadGraph): an edge without actions is a walk both ways,
 * costing the Manhattan distance when it gives no cost; an edge with actions goes one way. Nodes
 * are not written anywhere: they are the tiles edges start and end on.
 */
object BotNavTile {
	@JvmStatic
	fun pack(x: Int, y: Int, level: Int): Int = (level shl 28) or (y shl 14) or x

	@JvmStatic
	fun x(tile: Int): Int = tile and 0x3FFF

	@JvmStatic
	fun y(tile: Int): Int = (tile shr 14) and 0x3FFF

	@JvmStatic
	fun level(tile: Int): Int = (tile shr 28) and 3

	@JvmStatic
	fun format(tile: Int): String = "(" + x(tile) + ", " + y(tile) + ", " + level(tile) + ")"

	/** `{ x = 1, y = 2 }`, with the level when it is not 0, as Void writes a tile. */
	@JvmStatic
	fun table(tile: Int): TomlTable {
		val entries: LinkedHashMap<String, TomlValue> = LinkedHashMap()
		entries["x"] = TomlInt(x(tile).toLong())
		entries["y"] = TomlInt(y(tile).toLong())
		if (level(tile) != 0) {
			entries["level"] = TomlInt(level(tile).toLong())
		}
		return TomlTable(entries)
	}

	/** A tile from `{ x, y, level }`; null when x or y is missing or out of range. */
	@JvmStatic
	fun of(table: TomlTable?): Int? {
		if (table == null) {
			return null
		}
		val x: Long = table.int("x") ?: return null
		val y: Long = table.int("y") ?: return null
		val level: Long = table.int("level") ?: 0
		if (x !in 0..0x3FFF || y !in 0..0x3FFF || level !in 0..3) {
			return null
		}
		return pack(x.toInt(), y.toInt(), level.toInt())
	}
}

/** What an edge holds, kept whole so an edit can be undone by putting the old one back. */
class BotNavEdgeState(
	@JvmField val from: Int,
	@JvmField val to: Int,
	/** The cost as written; 0 for none. */
	@JvmField val cost: Int,
	@JvmField val actions: TomlArray?,
	@JvmField val requires: TomlArray?,
	@JvmField val comment: String?,
	/** Keys Void does not know, kept so a rewrite does not drop them. */
	@JvmField val extra: LinkedHashMap<String, TomlValue>,
) {
	val isWalk: Boolean
		get() = actions == null || actions.items.isEmpty()

	/** The cost Void uses: the written one, or the Manhattan distance for a walk without one. */
	fun weight(): Int {
		if (cost > 0 || !isWalk) {
			return cost
		}
		return kotlin.math.abs(BotNavTile.x(from) - BotNavTile.x(to)) + kotlin.math.abs(BotNavTile.y(from) - BotNavTile.y(to))
	}

	fun with(from: Int = this.from, to: Int = this.to, cost: Int = this.cost, actions: TomlArray? = this.actions, requires: TomlArray? = this.requires, comment: String? = this.comment): BotNavEdgeState {
		return BotNavEdgeState(from, to, cost, actions, requires, comment, extra)
	}

	fun table(): TomlTable {
		val entries: LinkedHashMap<String, TomlValue> = LinkedHashMap()
		entries["from"] = BotNavTile.table(from)
		entries["to"] = BotNavTile.table(to)
		if (cost > 0) {
			entries["cost"] = TomlInt(cost.toLong())
		}
		if (actions != null && actions.items.isNotEmpty()) {
			entries["actions"] = actions
		}
		if (requires != null && requires.items.isNotEmpty()) {
			entries["requires"] = requires
		}
		entries.putAll(extra)
		return TomlTable(entries)
	}

	/** The edge's line as Void writes it, without the indent: `{ ... }, # comment`. */
	fun line(): String {
		val out: StringBuilder = StringBuilder()
		table().format(out)
		out.append(',')
		if (!comment.isNullOrEmpty()) {
			out.append(" # ").append(comment)
		}
		return out.toString()
	}

	companion object {
		@JvmStatic
		fun of(table: TomlTable, comment: String?): BotNavEdgeState {
			val from: Int = BotNavTile.of(table.table("from")) ?: throw TomlException("an edge needs from = { x, y }")
			val to: Int = BotNavTile.of(table.table("to")) ?: throw TomlException("an edge needs to = { x, y }")
			val extra: LinkedHashMap<String, TomlValue> = LinkedHashMap()
			for ((key, value) in table.entries) {
				if (key != "from" && key != "to" && key != "cost" && key != "actions" && key != "requires") {
					extra[key] = value
				}
			}
			return BotNavEdgeState(from, to, (table.int("cost") ?: 0).toInt(), table.array("actions"), table.array("requires"), comment, extra)
		}
	}
}

/** One edge of a file: what it holds now, and the text it came from when it came from the file. */
class BotNavEdge(@JvmField val file: BotNavFile, @JvmField var state: BotNavEdgeState) {
	/** Where its text was in the file's text, or null for one added since. */
	@JvmField
	var source: BotNavToml.EdgeSource? = null

	/** Whether it changed since its text was read: then it is written anew. */
	@JvmField
	var changed: Boolean = false

	@JvmField
	var removed: Boolean = false
}

/** What an area holds: its name and its table (x, y, level, tags and the rest). */
class BotNavAreaState(@JvmField val name: String, @JvmField val table: TomlTable) {
	/** The x of each point: two for a rectangle (its corners), more for a polygon. Shared: copy it to change it. */
	fun xs(): IntArray = xsCache

	fun ys(): IntArray = ysCache

	// read once: drawing asks every area for its points every frame; the arrays are shared, never changed
	private val xsCache: IntArray by lazy { ints("x") }

	private val ysCache: IntArray by lazy { ints("y") }

	/** The area's level, or -1 when it covers every level. */
	fun level(): Int = (table.int("level") ?: -1L).toInt()

	fun isRectangle(): Boolean = xs().size == 2 && ys().size == 2

	fun tags(): List<String> {
		val tags: TomlArray = table.array("tags") ?: return emptyList()
		return tags.items.mapNotNull { (it as? TomlString)?.value }
	}

	private fun ints(key: String): IntArray {
		val array: TomlArray = table.array(key) ?: return IntArray(0)
		return IntArray(array.items.size) { ((array.items[it] as? TomlInt)?.value ?: 0L).toInt() }
	}

	/** Whether the tile is in it, as Void's Rectangle/Cuboid/Polygon have it. */
	fun contains(x: Int, y: Int, level: Int): Boolean {
		val areaLevel: Int = level()
		if (areaLevel >= 0 && areaLevel != level) {
			return false
		}
		val xs: IntArray = xs()
		val ys: IntArray = ys()
		if (xs.size != ys.size || xs.size < 2) {
			return false
		}
		if (xs.size == 2) {
			return x in minOf(xs[0], xs[1])..maxOf(xs[0], xs[1]) && y in minOf(ys[0], ys[1])..maxOf(ys[0], ys[1])
		}
		// even-odd rule through the tile's middle
		var inside = false
		var j: Int = xs.size - 1
		val px: Double = x + 0.5
		val py: Double = y + 0.5
		for (i in xs.indices) {
			val yi: Int = ys[i]
			val yj: Int = ys[j]
			if ((yi > py) != (yj > py)) {
				val crossX: Double = xs[i] + (py - yi) * (xs[j] - xs[i]).toDouble() / (yj - yi).toDouble()
				if (px < crossX) {
					inside = !inside
				}
			}
			j = i
		}
		return inside
	}

	fun withPoints(xs: IntArray, ys: IntArray): BotNavAreaState {
		val entries: LinkedHashMap<String, TomlValue> = LinkedHashMap(table.entries)
		entries["x"] = TomlArray(ArrayList(xs.map { TomlInt(it.toLong()) }))
		entries["y"] = TomlArray(ArrayList(ys.map { TomlInt(it.toLong()) }))
		return BotNavAreaState(name, reorder(entries))
	}

	/** The area as Void writes it: `[name]` then a `key = value` line each. */
	fun text(): String {
		val out: StringBuilder = StringBuilder("[")
		BotNavToml.appendKey(out, name)
		out.append("]\n")
		for ((key, value) in table.entries) {
			BotNavToml.appendKey(out, key)
			out.append(" = ")
			value.format(out)
			out.append('\n')
		}
		return out.toString()
	}

	companion object {
		/** x, y and level first, as Void's files have them. */
		@JvmStatic
		fun reorder(entries: LinkedHashMap<String, TomlValue>): TomlTable {
			val ordered: LinkedHashMap<String, TomlValue> = LinkedHashMap()
			for (key in arrayOf("x", "y", "level")) {
				entries[key]?.let { ordered[key] = it }
			}
			for ((key, value) in entries) {
				if (!ordered.containsKey(key)) {
					ordered[key] = value
				}
			}
			return TomlTable(ordered)
		}
	}
}

class BotNavArea(@JvmField val file: BotNavFile, @JvmField var state: BotNavAreaState) {
	@JvmField
	var source: BotNavToml.AreaSource? = null

	@JvmField
	var changed: Boolean = false

	@JvmField
	var removed: Boolean = false
}

/**
 * One of Void's files, by its path under Void's `data/` folder: its text as read, and its edges or
 * areas as they are now (removed ones kept, marked, so an undo can bring them back where they were).
 */
class BotNavFile(@JvmField val path: String, @JvmField val text: String) {
	@JvmField
	val isEdges: Boolean = path.endsWith(EDGES_SUFFIX)

	@JvmField
	val edges: ArrayList<BotNavEdge> = ArrayList()

	@JvmField
	val areas: ArrayList<BotNavArea> = ArrayList()

	/** Where edges added since go. */
	private var insertAt: Int = text.length

	/** Edited since it was read. */
	@JvmField
	var dirty: Boolean = false

	/** The editor's working copy (not the bundled file) is what was read. */
	@JvmField
	var fromWorkingCopy: Boolean = false

	init {
		if (isEdges) {
			val parsed: BotNavToml.EdgesFile = BotNavToml.parseEdges(text)
			insertAt = parsed.insertAt
			for (source in parsed.edges) {
				val edge = BotNavEdge(this, BotNavEdgeState.of(source.table, source.comment))
				edge.source = source
				edges.add(edge)
			}
		} else {
			for (source in BotNavToml.parseAreas(text)) {
				val area = BotNavArea(this, BotNavAreaState(source.name, source.table))
				area.source = source
				areas.add(area)
			}
		}
	}

	/** The file as it is now: the text as read, the regions of changed, added and removed edges or areas rewritten. */
	fun render(): String {
		return if (isEdges) renderEdges() else renderAreas()
	}

	private fun renderEdges(): String {
		val out: StringBuilder = StringBuilder()
		var at = 0
		// an edge read without a comma that is no longer last needs one: write it anew
		var lastKept: BotNavEdge? = null
		for (edge in edges) {
			if (!edge.removed) {
				lastKept = edge
			}
		}
		val anyAdded: Boolean = edges.any { !it.removed && it.source == null }
		for (edge in edges) {
			val source: BotNavToml.EdgeSource = edge.source ?: continue
			out.append(text, at, source.regionStart)
			at = source.regionEnd
			appendEdge(out, edge, source, !source.comma && (edge !== lastKept || anyAdded))
		}
		out.append(text, at, insertAt)
		if (anyAdded && insertAt > 0 && text[insertAt - 1] != '\n') {
			out.append('\n')
		}
		for (edge in edges) {
			if (edge.source == null && !edge.removed) {
				out.append(INDENT).append(edge.state.line()).append('\n')
			}
		}
		out.append(text, insertAt, text.length)
		return out.toString()
	}

	/** An edge read from the text: as it was, written anew when it changed (or needs a comma now), or nothing when removed. */
	private fun appendEdge(out: StringBuilder, edge: BotNavEdge, source: BotNavToml.EdgeSource, needsComma: Boolean) {
		if (edge.removed) {
			return
		}
		if (edge.changed || needsComma) {
			out.append(INDENT).append(edge.state.line()).append('\n')
		} else {
			out.append(text, source.regionStart, source.regionEnd)
		}
	}

	private fun renderAreas(): String {
		val out: StringBuilder = StringBuilder()
		var at = 0
		for (area in areas) {
			val source: BotNavToml.AreaSource = area.source ?: continue
			out.append(text, at, source.regionStart)
			at = source.regionEnd
			if (!area.removed) {
				out.append(if (area.changed) area.state.text() else text.substring(source.regionStart, source.regionEnd))
			}
		}
		out.append(text, at, text.length)
		for (area in areas) {
			if (area.source == null && !area.removed) {
				if (out.isNotEmpty() && !out.endsWith("\n\n")) {
					out.append(if (out.endsWith("\n")) "\n" else "\n\n")
				}
				out.append(area.state.text())
			}
		}
		return out.toString()
	}

	/** The lines of edges and areas changed or added since the file was read, for the clipboard. */
	fun changedText(): String {
		val out: StringBuilder = StringBuilder()
		for (edge in edges) {
			if (!edge.removed && (edge.changed || edge.source == null)) {
				out.append(INDENT).append(edge.state.line()).append('\n')
			}
		}
		for (area in areas) {
			if (!area.removed && (area.changed || area.source == null)) {
				out.append(area.state.text()).append('\n')
			}
		}
		return out.toString()
	}

	companion object {
		const val EDGES_SUFFIX: String = ".nav-edges.toml"

		const val AREAS_SUFFIX: String = ".areas.toml"

		const val INDENT: String = "    "
	}
}

/** An edit the editor can take back. */
interface BotNavEdit {
	fun undo()

	fun redo()
}

/**
 * Every file, the edits made since the editor started (to undo), and the nodes placed but not yet
 * joined by an edge (they are written nowhere until one is).
 */
class BotNavGraph {
	@JvmField
	val files: ArrayList<BotNavFile> = ArrayList()

	@JvmField
	val pendingNodes: ArrayList<Int> = ArrayList()

	private val undoStack: ArrayList<BotNavEdit> = ArrayList()

	private val redoStack: ArrayList<BotNavEdit> = ArrayList()

	/** Each node and the edges at it, made again after each edit. */
	private var nodeIndex: HashMap<Int, ArrayList<BotNavEdge>>? = null

	/** Bumped by every change, so drawing can tell. */
	@JvmField
	var version: Int = 0

	fun file(path: String): BotNavFile? = files.firstOrNull { it.path == path }

	fun edges(): Sequence<BotNavEdge> = files.asSequence().flatMap { it.edges.asSequence() }.filter { !it.removed }

	fun areas(): Sequence<BotNavArea> = files.asSequence().flatMap { it.areas.asSequence() }.filter { !it.removed }

	fun edgeFiles(): List<BotNavFile> = files.filter { it.isEdges }

	fun areaFiles(): List<BotNavFile> = files.filter { !it.isEdges }

	/** The edges starting or ending at each node. */
	fun nodes(): HashMap<Int, ArrayList<BotNavEdge>> {
		nodeIndex?.let { return it }
		val index: HashMap<Int, ArrayList<BotNavEdge>> = HashMap()
		for (edge in edges()) {
			index.getOrPut(edge.state.from) { ArrayList() }.add(edge)
			if (edge.state.to != edge.state.from) {
				index.getOrPut(edge.state.to) { ArrayList() }.add(edge)
			}
		}
		nodeIndex = index
		return index
	}

	fun isNode(tile: Int): Boolean = nodes().containsKey(tile) || pendingNodes.contains(tile)

	/** The area named [name], if there is one. */
	fun area(name: String): BotNavArea? = areas().firstOrNull { it.state.name == name }

	/** The smallest area holding the tile (a hand-drawn pen before the city around it). */
	fun areaAt(x: Int, y: Int, level: Int): BotNavArea? {
		var best: BotNavArea? = null
		var bestSize: Long = Long.MAX_VALUE
		for (area in areas()) {
			if (!area.state.contains(x, y, level)) {
				continue
			}
			val xs: IntArray = area.state.xs()
			val ys: IntArray = area.state.ys()
			val size: Long = (xs.max() - xs.min() + 1).toLong() * (ys.max() - ys.min() + 1).toLong()
			if (size < bestSize) {
				bestSize = size
				best = area
			}
		}
		return best
	}

	/** Changed since it was read: the files to save and export. */
	fun changedFiles(): List<BotNavFile> = files.filter { it.dirty }

	private fun touched(file: BotNavFile) {
		file.dirty = true
		nodeIndex = null
		version++
	}

	/** Makes an edit and keeps it to undo. */
	fun apply(edit: BotNavEdit) {
		edit.redo()
		undoStack.add(edit)
		if (undoStack.size > UNDO_LIMIT) {
			undoStack.removeAt(0)
		}
		redoStack.clear()
	}

	fun canUndo(): Boolean = undoStack.isNotEmpty()

	fun canRedo(): Boolean = redoStack.isNotEmpty()

	fun undo(): Boolean {
		val edit: BotNavEdit = undoStack.removeLastOrNull() ?: return false
		edit.undo()
		redoStack.add(edit)
		return true
	}

	fun redo(): Boolean {
		val edit: BotNavEdit = redoStack.removeLastOrNull() ?: return false
		edit.redo()
		undoStack.add(edit)
		return true
	}

	fun clearHistory() {
		undoStack.clear()
		redoStack.clear()
	}

	/** Puts [file] in place of the file of its path (a revert or an import), or adds it. */
	fun replace(file: BotNavFile) {
		val i: Int = files.indexOfFirst { it.path == file.path }
		if (i >= 0) {
			files[i] = file
		} else {
			files.add(file)
		}
		clearHistory()
		nodeIndex = null
		version++
	}

	// --- the edits ---

	fun addEdge(file: BotNavFile, state: BotNavEdgeState): BotNavEdge {
		val edge = BotNavEdge(file, state)
		apply(object : BotNavEdit {
			override fun undo() {
				file.edges.remove(edge)
				touched(file)
			}

			override fun redo() {
				file.edges.add(edge)
				touched(file)
			}
		})
		return edge
	}

	fun changeEdge(edge: BotNavEdge, after: BotNavEdgeState) {
		apply(edgeChange(edge, after))
	}

	private fun edgeChange(edge: BotNavEdge, after: BotNavEdgeState): BotNavEdit {
		val before: BotNavEdgeState = edge.state
		val wasChanged: Boolean = edge.changed
		return object : BotNavEdit {
			override fun undo() {
				edge.state = before
				edge.changed = wasChanged
				touched(edge.file)
			}

			override fun redo() {
				edge.state = after
				edge.changed = true
				touched(edge.file)
			}
		}
	}

	fun removeEdge(edge: BotNavEdge) {
		apply(edgeRemoval(edge))
	}

	private fun edgeRemoval(edge: BotNavEdge): BotNavEdit {
		return object : BotNavEdit {
			override fun undo() {
				edge.removed = false
				touched(edge.file)
			}

			override fun redo() {
				edge.removed = true
				touched(edge.file)
			}
		}
	}

	/** Moves a node: every edge's end on [from] goes to [to], and object actions keep their place. */
	fun moveNode(from: Int, to: Int) {
		val edits: ArrayList<BotNavEdit> = ArrayList()
		for (edge in nodes()[from].orEmpty()) {
			val state: BotNavEdgeState = edge.state
			edits.add(edgeChange(edge, state.with(from = if (state.from == from) to else state.from, to = if (state.to == from) to else state.to)))
		}
		val pending: Int = pendingNodes.indexOf(from)
		edits.add(object : BotNavEdit {
			override fun undo() {
				if (pending >= 0) {
					pendingNodes[pending] = from
				}
				version++
			}

			override fun redo() {
				if (pending >= 0) {
					pendingNodes[pending] = to
				}
				version++
			}
		})
		apply(compound(edits))
	}

	/** Removes a node and every edge at it. */
	fun removeNode(tile: Int) {
		val edits: ArrayList<BotNavEdit> = ArrayList()
		for (edge in nodes()[tile].orEmpty()) {
			edits.add(edgeRemoval(edge))
		}
		val pending: Int = pendingNodes.indexOf(tile)
		if (pending >= 0) {
			edits.add(object : BotNavEdit {
				override fun undo() {
					pendingNodes.add(pending, tile)
					version++
				}

				override fun redo() {
					pendingNodes.removeAt(pending)
					version++
				}
			})
		}
		apply(compound(edits))
	}

	fun addPendingNode(tile: Int) {
		if (isNode(tile)) {
			return
		}
		apply(object : BotNavEdit {
			override fun undo() {
				pendingNodes.remove(tile)
				version++
			}

			override fun redo() {
				pendingNodes.add(tile)
				version++
			}
		})
	}

	fun addArea(file: BotNavFile, state: BotNavAreaState): BotNavArea {
		val area = BotNavArea(file, state)
		apply(object : BotNavEdit {
			override fun undo() {
				file.areas.remove(area)
				touched(file)
			}

			override fun redo() {
				file.areas.add(area)
				touched(file)
			}
		})
		return area
	}

	fun changeArea(area: BotNavArea, after: BotNavAreaState) {
		val before: BotNavAreaState = area.state
		val wasChanged: Boolean = area.changed
		apply(object : BotNavEdit {
			override fun undo() {
				area.state = before
				area.changed = wasChanged
				touched(area.file)
			}

			override fun redo() {
				area.state = after
				area.changed = true
				touched(area.file)
			}
		})
	}

	fun removeArea(area: BotNavArea) {
		apply(object : BotNavEdit {
			override fun undo() {
				area.removed = false
				touched(area.file)
			}

			override fun redo() {
				area.removed = true
				touched(area.file)
			}
		})
	}

	private fun compound(edits: List<BotNavEdit>): BotNavEdit {
		return object : BotNavEdit {
			override fun undo() {
				for (i in edits.indices.reversed()) {
					edits[i].undo()
				}
			}

			override fun redo() {
				for (edit in edits) {
					edit.redo()
				}
			}
		}
	}

	companion object {
		const val UNDO_LIMIT: Int = 100

		/** The bundle's file separator line: `### <path under data/>` (see ~/634-bot-nav-tools). */
		const val BUNDLE_SEPARATOR: String = "### "

		/** Splits the bundle into its files' paths and texts, in order. */
		@JvmStatic
		fun splitBundle(text: String): List<Pair<String, String>> {
			val files: ArrayList<Pair<String, String>> = ArrayList()
			var path: String? = null
			var start = 0
			var line = 0
			while (line < text.length) {
				var next: Int = text.indexOf('\n', line)
				next = if (next < 0) text.length else next + 1
				if (text.startsWith(BUNDLE_SEPARATOR, line)) {
					if (path != null) {
						files.add(path to text.substring(start, line))
					}
					path = text.substring(line + BUNDLE_SEPARATOR.length, next).trim()
					start = next
				}
				line = next
			}
			if (path != null) {
				files.add(path to text.substring(start))
			}
			return files
		}
	}
}
