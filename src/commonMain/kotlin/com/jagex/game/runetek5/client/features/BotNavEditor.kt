package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.PluginPaths
import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.KeyCode
import com.jagex.game.runetek5.plugin.api.LocDefinition
import com.jagex.game.runetek5.plugin.api.MenuAction
import com.jagex.game.runetek5.plugin.api.MenuTarget
import com.jagex.game.runetek5.plugin.api.Overlay
import com.jagex.game.runetek5.plugin.api.Plugin
import com.jagex.game.runetek5.plugin.api.PluginApi
import com.jagex.game.runetek5.plugin.api.PluginConfig
import com.jagex.game.runetek5.plugin.api.PluginContext
import com.jagex.game.runetek5.plugin.api.PluginDescriptor
import com.jagex.game.runetek5.plugin.api.events.ClientTick
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.FrameDraw
import com.jagex.game.runetek5.plugin.api.events.KeyPressed
import com.jagex.game.runetek5.plugin.api.events.KeyTyped
import com.jagex.game.runetek5.plugin.api.events.MenuEntryAdded
import com.jagex.game.runetek5.plugin.api.events.MinimapDraw
import com.jagex.game.runetek5.plugin.api.events.MouseClicked
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: an editor for Void's bot navigation graph, for the developers of
 * the server. It draws the graph's nodes, its edges (cyan walks both ways, orange one-way actions such
 * as a stile's Climb-over) and its named areas (green dashed outlines) over the game, on the minimap
 * and on the world map, and edits them from the Shift right-click menu of a tile or an object and from
 * its panel, where an edge's cost, actions and requirements are typed as Void's TOML.
 *
 * It starts from a copy of Void's `*.nav-edges.toml` and `*.areas.toml` bundled with the client
 * (made by ~/634-bot-nav-tools/sync.py); the files edited are kept in `.634-client/bot-nav/work` and
 * `::botnav export` writes them to `.634-client/bot-nav/export`, laid out as Void's `data/` folder, to
 * be copied into a Void checkout (and `::reload nav` there). Only with the developer commands on
 * (`--dev-commands`), and off until switched on.
 */
object BotNavEditor : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("botnav", "Bot Nav Editor", "1", PluginApi.VERSION, "For server developers: see and edit Void's bot navigation graph and areas in the game (Shift right-click a tile), then ::botnav export the files for the server.", "Pixel")

	private const val BUNDLE_FILE: String = "/bot-nav/nav.txt.gz"

	private const val OBJECTS_FILE: String = "/bot-nav/objects.tsv.gz"

	/** Where new edges and areas go when nothing near them says which file. */
	const val DEFAULT_EDGES_FILE: String = "bot/editor.nav-edges.toml"

	const val DEFAULT_AREAS_FILE: String = "area/editor.areas.toml"

	/** How near (in tiles) an edge or area must be for a new one to go in its file. */
	private const val NEAR_FILE: Int = 64

	private const val FETCH_CYCLES: Int = 1500

	private const val DATA_NONE: Int = 0

	private const val DATA_WAITING: Int = 1

	private const val DATA_DECODING: Int = 2

	private const val DATA_READY: Int = 3

	private const val DATA_FAILED: Int = 4

	/** The client's key codes (CanvasKeyRecorder.REMAPPED_KEYS). */
	const val KEY_ENTER: Int = 84

	const val KEY_BACKSPACE: Int = 85

	const val KEY_ESCAPE: Int = 13

	/** What a click on a tile does next. */
	const val MODE_NONE: Int = 0

	const val MODE_EDGE: Int = 1

	const val MODE_MOVE_NODE: Int = 2

	const val MODE_AREA_RECT: Int = 3

	const val MODE_AREA_POLYGON: Int = 4

	const val MODE_MOVE_POINT: Int = 5

	@JvmField
	var enabled: Boolean = false

	@JvmField
	var showAreas: Boolean = true

	@JvmField
	var showWalks: Boolean = true

	@JvmField
	var showActions: Boolean = true

	@JvmField
	var showLabels: Boolean = true

	/** How far from the player (in tiles) action labels are drawn. */
	@JvmField
	var labelDistance: Int = 20

	@JvmField
	var drawOnMinimap: Boolean = true

	@JvmField
	var drawOnWorldMap: Boolean = true

	@JvmField
	var colourWalk: Int = 0xFF4CB8FF.toInt()

	@JvmField
	var colourAction: Int = 0xFFFF9A3C.toInt()

	@JvmField
	var colourArea: Int = 0xFF3CE05A.toInt()

	@JvmField
	var colourNode: Int = 0xFF2FB4FF.toInt()

	@JvmField
	var graph: BotNavGraph? = null

	/** Void's object names to their ids (objects.tsv), and back. */
	@JvmField
	var objectIds: Map<String, Int> = emptyMap()

	@JvmField
	var objectNames: Map<Int, String> = emptyMap()

	private var dataState: Int = DATA_NONE

	private var dataWait: Int = 0

	/** The graph's version when its files were last saved. */
	private var savedVersion: Int = 0

	@JvmField
	var mode: Int = MODE_NONE

	/** The edge's start (MODE_EDGE), the node being moved (MODE_MOVE_NODE) or the rectangle's first corner (MODE_AREA_RECT). */
	@JvmField
	var modeTile: Int = -1

	/** The polygon's points so far (MODE_AREA_POLYGON), packed tiles. */
	@JvmField
	val polygon: ArrayList<Int> = ArrayList()

	/** The point being moved (MODE_MOVE_POINT) of [selectedArea]. */
	@JvmField
	var movingPoint: Int = -1

	@JvmField
	var selectedNode: Int = -1

	@JvmField
	var selectedEdge: BotNavEdge? = null

	@JvmField
	var selectedArea: BotNavArea? = null

	/** What the menu was built over: the edge and area under the mouse then. */
	private var menuEdge: BotNavEdge? = null

	private var menuArea: BotNavArea? = null

	/** The last message for the panel's footer (an error or what was done). */
	@JvmField
	var status: String = ""

	@JvmField
	var statusError: Boolean = false

	private var context: PluginContext? = null

	private val actions: HashMap<String, Int> = HashMap()

	private val objectActions: IntArray = IntArray(5) { -1 }

	private val overlay: Overlay = Overlay { g, view -> BotNavOverlay.drawScene(g, view) }

	private val minimapListener: EventListener<MinimapDraw> = EventListener { event -> BotNavOverlay.drawMinimap(event.graphics) }

	private val frameListener: EventListener<FrameDraw> = EventListener { event -> BotNavOverlay.drawWorldMap(event.graphics) }

	private val tickListener: EventListener<ClientTick> = EventListener { _ -> context?.client()?.let { tick(it) } }

	private val settingListener: EventListener<SettingChanged> = EventListener { event ->
		if (event.pluginId == DESCRIPTOR.id) {
			context?.config()?.let { readSettings(it) }
		}
	}

	private val menuListener: EventListener<MenuEntryAdded> = EventListener { event -> context?.client()?.let { menu(it, event) } }

	private val clickListener: EventListener<MouseClicked> = EventListener { event -> BotNavPanel.clicked(event) }

	private val keyTypedListener: EventListener<KeyTyped> = EventListener { event -> BotNavPanel.typed(event) }

	private val keyPressedListener: EventListener<KeyPressed> = EventListener { event -> keyPressed(event) }

	private val command: CommandHandler = CommandHandler { _, args -> context?.client()?.let { command(it, args) } }

	override fun start(context: PluginContext) {
		this.context = context
		readSettings(context.config())
		context.settings().toggle("enabled", "Bot nav editor", false)
		context.settings().toggle("showAreas", "Show areas", true)
		context.settings().toggle("showWalks", "Show walk edges", true)
		context.settings().toggle("showActions", "Show action edges", true)
		context.settings().toggle("showLabels", "Show action labels", true)
		context.settings().number("labelDistance", "Label distance (tiles)", 20, 1, 104, 1)
		context.settings().toggle("drawOnMinimap", "Draw on the minimap", true)
		context.settings().toggle("drawOnWorldMap", "Draw on the world map", true)
		context.settings().colour("colourWalk", "Walk edge colour", 0xFF4CB8FF.toInt(), false)
		context.settings().colour("colourAction", "Action edge colour", 0xFFFF9A3C.toInt(), false)
		context.settings().colour("colourArea", "Area colour", 0xFF3CE05A.toInt(), false)
		context.settings().colour("colourNode", "Node colour", 0xFF2FB4FF.toInt(), false)
		registerActions(context)
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		context.events().subscribe(Events.MENU_ENTRY_ADDED, menuListener)
		context.events().subscribe(Events.CLIENT_TICK, tickListener)
		context.events().subscribe(Events.MINIMAP_DRAW, minimapListener)
		context.events().subscribe(Events.FRAME_DRAW, frameListener)
		context.events().subscribe(Events.MOUSE_CLICKED, clickListener)
		context.events().subscribe(Events.KEY_TYPED, keyTypedListener)
		context.events().subscribe(Events.KEY_PRESSED, keyPressedListener)
		context.addOverlay(overlay)
		context.addOverlay(BotNavPanel.panel)
		context.registerCommand("botnav", command)
	}

	override fun stop() {
		context = null
		graph = null
		dataState = DATA_NONE
		cancelMode()
		select(-1, null, null)
		BotNavPanel.stopEditing()
	}

	@JvmStatic
	fun context(): PluginContext? = context

	private fun readSettings(config: PluginConfig) {
		enabled = config.getBoolean("enabled", false)
		showAreas = config.getBoolean("showAreas", true)
		showWalks = config.getBoolean("showWalks", true)
		showActions = config.getBoolean("showActions", true)
		showLabels = config.getBoolean("showLabels", true)
		labelDistance = config.getInt("labelDistance", 20).coerceIn(1, 104)
		drawOnMinimap = config.getBoolean("drawOnMinimap", true)
		drawOnWorldMap = config.getBoolean("drawOnWorldMap", true)
		colourWalk = config.getColour("colourWalk", 0xFF4CB8FF.toInt())
		colourAction = config.getColour("colourAction", 0xFFFF9A3C.toInt())
		colourArea = config.getColour("colourArea", 0xFF3CE05A.toInt())
		colourNode = config.getColour("colourNode", 0xFF2FB4FF.toInt())
		if (!enabled) {
			cancelMode()
			BotNavPanel.stopEditing()
		}
	}

	/** Shown and loaded: what drawing and the menus ask. */
	@JvmStatic
	fun active(client: ClientApi): Boolean = enabled && dataState == DATA_READY && client.gameState() === GameState.LOGGED_IN

	@JvmStatic
	fun level(client: ClientApi): Int = client.localPlayer()?.level() ?: 0

	@JvmStatic
	fun manhattan(a: Int, b: Int): Int = kotlin.math.abs(BotNavTile.x(a) - BotNavTile.x(b)) + kotlin.math.abs(BotNavTile.y(a) - BotNavTile.y(b))

	// --- loading ---

	private fun tick(client: ClientApi) {
		if (!enabled) {
			return
		}
		when (dataState) {
			DATA_NONE -> {
				lang.requestResource(BUNDLE_FILE)
				lang.requestResource(OBJECTS_FILE)
				dataState = DATA_WAITING
				dataWait = 0
			}
			DATA_WAITING -> awaitData(client)
			DATA_READY -> {
				val graph: BotNavGraph = graph ?: return
				if (graph.version != savedVersion) {
					saveWorkingCopies(graph)
				}
			}
		}
	}

	private fun awaitData(client: ClientApi) {
		val bundle: ByteArray? = lang.readResource(BUNDLE_FILE)
		val objects: ByteArray? = lang.readResource(OBJECTS_FILE)
		if (bundle == null || objects == null) {
			if (++dataWait > FETCH_CYCLES) {
				dataState = DATA_FAILED
				client.consolePrint("Bot Nav Editor could not fetch its data.")
			}
			return
		}
		val context: PluginContext = this.context ?: return
		dataState = DATA_DECODING
		context.startThread("botnav") { decode(context, bundle, objects) }
	}

	@Suppress("TooGenericExceptionCaught") // a damaged bundle must leave the editor empty, not take the client down
	private fun decode(context: PluginContext, bundle: ByteArray, objects: ByteArray) {
		try {
			val loaded = BotNavGraph()
			val problems: ArrayList<String> = ArrayList()
			val bundled: List<Pair<String, String>> = BotNavGraph.splitBundle(ShortestPathGzip.gunzip(bundle).decodeToString())
			val working: Map<String, String> = readWorkingCopies()
			for ((path, text) in bundled) {
				loaded.files.add(readFile(path, text, working[path], problems) ?: continue)
			}
			for ((path, text) in working) {
				if (loaded.file(path) == null) {
					loaded.files.add(readFile(path, text, null, problems) ?: continue)
					loaded.files.last().fromWorkingCopy = true
				}
			}
			val ids: HashMap<String, Int> = HashMap()
			val names: HashMap<Int, String> = HashMap()
			for (line in ShortestPathGzip.gunzip(objects).decodeToString().lineSequence()) {
				val tab: Int = line.indexOf('\t')
				val id: Int = (if (tab > 0) line.substring(tab + 1).trim().toIntOrNull() else null) ?: continue
				val name: String = line.substring(0, tab)
				ids[name] = id
				names.getOrPut(id) { name }
			}
			context.invokeLater {
				if (dataState == DATA_DECODING) {
					graph = loaded
					savedVersion = loaded.version
					objectIds = ids
					objectNames = names
					dataState = DATA_READY
					for (problem in problems) {
						context.client().consolePrint(problem)
					}
					val edgeCount: Int = loaded.edges().count()
					context.client().consolePrint("Bot nav: " + loaded.files.size + " files, " + edgeCount + " edges, " + loaded.areas().count() + " areas" + (if (working.isEmpty()) "." else ", " + working.size + " from the working copy."))
				}
			}
		} catch (error: Exception) {
			context.invokeLater {
				dataState = DATA_FAILED
				context.client().consolePrint("Bot Nav Editor could not read its data: " + error.message)
			}
		}
	}

	/** One file: the working copy's text when it has one that reads, else the bundled text. */
	private fun readFile(path: String, bundled: String, working: String?, problems: ArrayList<String>): BotNavFile? {
		if (working != null) {
			try {
				val file = BotNavFile(path, working)
				file.fromWorkingCopy = true
				return file
			} catch (error: TomlException) {
				problems.add("Bot nav: the working copy of $path does not read (" + error.message + "); using the bundled one.")
			}
		}
		return try {
			BotNavFile(path, bundled)
		} catch (error: TomlException) {
			problems.add("Bot nav: $path does not read: " + error.message)
			null
		}
	}

	// --- the files on disk ---

	private fun folder(name: String): io.File? {
		val root: io.File = PluginPaths.root() ?: return null
		return io.File(root.getPath() + "/bot-nav/" + name)
	}

	/** Every file of the working copy, by its path under Void's data folder. */
	private fun readWorkingCopies(): Map<String, String> {
		val files: HashMap<String, String> = HashMap()
		val work: io.File = folder("work") ?: return files
		collect(work, "", files, 0)
		return files
	}

	private fun collect(dir: io.File, prefix: String, out: HashMap<String, String>, depth: Int) {
		if (depth > 8) {
			return
		}
		for (child in dir.listFiles() ?: return) {
			val path: String = prefix + child.getName()
			if (child.isDirectory()) {
				collect(child, "$path/", out, depth + 1)
			} else if (path.endsWith(BotNavFile.EDGES_SUFFIX) || path.endsWith(BotNavFile.AREAS_SUFFIX)) {
				val bytes: ByteArray = PluginPaths.readBytes(child) ?: continue
				out[path] = bytes.decodeToString()
			}
		}
	}

	private fun saveWorkingCopies(graph: BotNavGraph) {
		savedVersion = graph.version
		val work: io.File = folder("work") ?: return
		for (file in graph.changedFiles()) {
			if (PluginPaths.writeText(io.File(work.getPath() + "/" + file.path), file.render())) {
				file.fromWorkingCopy = true
			}
		}
	}

	/** Writes every file edited (now or in an earlier session) to the export folder, and this session's changed lines to the clipboard. */
	@JvmStatic
	fun export(client: ClientApi) {
		val graph: BotNavGraph = graph ?: return
		saveWorkingCopies(graph)
		val edited: List<BotNavFile> = graph.files.filter { it.dirty || it.fromWorkingCopy }
		if (edited.isEmpty()) {
			report(client, "Nothing edited to export.")
			return
		}
		val clipboard: StringBuilder = StringBuilder()
		for (file in edited) {
			val lines: String = file.changedText()
			if (lines.isNotEmpty()) {
				clipboard.append("# ").append(file.path).append('\n').append(lines)
			}
		}
		if (clipboard.isNotEmpty()) {
			client.setClipboard(clipboard.toString())
		}
		val export: io.File? = folder("export")
		if (export == null) {
			report(client, "No folder to export to: " + edited.size + " file(s) changed, the changed lines are on the clipboard.")
			return
		}
		var written = 0
		for (file in edited) {
			if (PluginPaths.writeText(io.File(export.getPath() + "/" + file.path), file.render())) {
				written++
			}
		}
		report(client, "Exported $written file(s) to " + export.getPath() + " (copy into Void's data folder, then ::reload nav).")
		for (file in edited) {
			client.consolePrint("  " + file.path)
		}
	}

	/** Puts a file back as bundled, or every file for "all". */
	private fun revert(client: ClientApi, path: String) {
		val graph: BotNavGraph = graph ?: return
		val bundle: ByteArray = lang.readResource(BUNDLE_FILE) ?: return
		val bundled: Map<String, String> = BotNavGraph.splitBundle(ShortestPathGzip.gunzip(bundle).decodeToString()).toMap()
		val paths: List<String> = if (path == "all") graph.files.filter { it.dirty || it.fromWorkingCopy }.map { it.path } else listOf(path)
		val work: io.File? = folder("work")
		var reverted = 0
		for (p in paths) {
			val file: BotNavFile = graph.file(p) ?: continue
			work?.let { io.File(it.getPath() + "/" + p).delete() }
			val text: String? = bundled[p]
			if (text == null) {
				graph.files.remove(file)
			} else {
				graph.replace(BotNavFile(p, text))
			}
			reverted++
		}
		graph.clearHistory()
		graph.version++
		savedVersion = graph.version
		select(-1, null, null)
		report(client, if (reverted == 0) "No edited file $path (::botnav files lists them)." else "Reverted $reverted file(s).")
	}

	/** Replaces a file with the clipboard's TOML (a newer copy from Void). */
	private fun import(client: ClientApi, path: String) {
		val graph: BotNavGraph = graph ?: return
		if (!path.endsWith(BotNavFile.EDGES_SUFFIX) && !path.endsWith(BotNavFile.AREAS_SUFFIX)) {
			report(client, "The path must end " + BotNavFile.EDGES_SUFFIX + " or " + BotNavFile.AREAS_SUFFIX + ", as under Void's data folder.")
			return
		}
		val text: String = client.clipboard() ?: return report(client, "The clipboard is empty.")
		val file: BotNavFile = try {
			BotNavFile(path, text)
		} catch (error: TomlException) {
			return report(client, "The clipboard does not read as $path: " + error.message)
		}
		graph.replace(file)
		file.dirty = true
		select(-1, null, null)
		report(client, "Imported $path: " + file.edges.size + " edges, " + file.areas.size + " areas.")
	}

	// --- selection and modes ---

	@JvmStatic
	fun select(node: Int, edge: BotNavEdge?, area: BotNavArea?) {
		selectedNode = node
		selectedEdge = edge
		selectedArea = area
		BotNavPanel.stopEditing()
	}

	@JvmStatic
	fun cancelMode() {
		mode = MODE_NONE
		modeTile = -1
		movingPoint = -1
		polygon.clear()
	}

	@JvmStatic
	fun report(client: ClientApi?, text: String) {
		status = text
		statusError = false
		client?.chatMessage(text)
	}

	/** A field that did not take what was typed: said on the panel only. */
	@JvmStatic
	fun fail(text: String) {
		status = text
		statusError = true
	}

	/** The file new edges near [tile] go in: the file of the nearest edge within [NEAR_FILE] tiles, else [DEFAULT_EDGES_FILE]. */
	@JvmStatic
	fun edgesFileFor(graph: BotNavGraph, tile: Int): BotNavFile {
		var best: BotNavFile? = null
		var bestDistance: Int = NEAR_FILE + 1
		for (edge in graph.edges()) {
			for (end in intArrayOf(edge.state.from, edge.state.to)) {
				if (BotNavTile.level(end) != BotNavTile.level(tile)) {
					continue
				}
				val d: Int = maxOf(kotlin.math.abs(BotNavTile.x(end) - BotNavTile.x(tile)), kotlin.math.abs(BotNavTile.y(end) - BotNavTile.y(tile)))
				if (d < bestDistance) {
					bestDistance = d
					best = edge.file
				}
			}
		}
		return best ?: fileOf(graph, DEFAULT_EDGES_FILE, "edges = [\n]\n")
	}

	/** The file new areas around [tile] go in: the file of the smallest area holding it, else [DEFAULT_AREAS_FILE]. */
	@JvmStatic
	fun areasFileFor(graph: BotNavGraph, tile: Int): BotNavFile {
		val area: BotNavArea? = graph.areaAt(BotNavTile.x(tile), BotNavTile.y(tile), BotNavTile.level(tile))
		return area?.file ?: fileOf(graph, DEFAULT_AREAS_FILE, "")
	}

	private fun fileOf(graph: BotNavGraph, path: String, empty: String): BotNavFile {
		graph.file(path)?.let { return it }
		val file = BotNavFile(path, empty)
		graph.files.add(file)
		return file
	}

	// --- the menus ---

	private fun registerActions(context: PluginContext) {
		actions.clear()
		fun tile(name: String, run: (ClientApi, Int) -> Unit) {
			actions[name] = context.registerMenuAction(MenuAction { _, sceneX, sceneY, _ ->
				val client: ClientApi = context.client()
				if (graph != null) {
					run(client, BotNavTile.pack(client.sceneBaseX() + sceneX, client.sceneBaseY() + sceneY, level(client)))
				}
			})
		}
		tile("addNode") { client, t -> BotNavEdits.addNode(client, t) }
		tile("selectNode") { _, t -> select(t, null, null) }
		tile("deleteNode") { client, t -> BotNavEdits.deleteNode(client, t) }
		tile("startEdge") { _, t ->
			cancelMode()
			mode = MODE_EDGE
			modeTile = t
		}
		tile("finishWalk") { client, t -> BotNavEdits.finishEdge(client, t, false) }
		tile("finishAction") { client, t -> BotNavEdits.finishEdge(client, t, true) }
		tile("startMove") { _, t ->
			cancelMode()
			mode = MODE_MOVE_NODE
			modeTile = t
		}
		tile("moveHere") { client, t -> BotNavEdits.moveNodeTo(client, t) }
		tile("cancel") { _, _ -> cancelMode() }
		tile("editEdge") { _, _ -> menuEdge?.let { select(-1, it, null) } }
		tile("reverseEdge") { client, _ -> menuEdge?.let { BotNavEdits.reverse(client, it) } }
		tile("deleteEdge") { client, _ -> menuEdge?.let { BotNavEdits.deleteEdge(client, it) } }
		tile("selectArea") { _, _ -> menuArea?.let { select(-1, null, it) } }
		tile("deleteArea") { client, _ -> menuArea?.let { BotNavEdits.deleteArea(client, it) } }
		tile("startRect") { _, t ->
			cancelMode()
			mode = MODE_AREA_RECT
			modeTile = t
		}
		tile("finishRect") { client, t -> BotNavEdits.finishRectangle(client, t) }
		tile("startPolygon") { _, t ->
			cancelMode()
			mode = MODE_AREA_POLYGON
			polygon.add(t)
		}
		tile("addPoint") { _, t -> polygon.add(t) }
		tile("closePolygon") { client, t -> BotNavEdits.finishPolygon(client, t) }
		tile("movePoint") { _, t ->
			val area: BotNavArea = selectedArea ?: return@tile
			val i: Int = BotNavEdits.pointAt(area, t)
			if (i >= 0) {
				cancelMode()
				mode = MODE_MOVE_POINT
				movingPoint = i
			}
		}
		tile("pointHere") { client, t -> BotNavEdits.movePointTo(client, t) }
		for (op in 0 until 5) {
			objectActions[op] = context.registerMenuAction(MenuAction { _, sceneX, sceneY, id ->
				val client: ClientApi = context.client()
				BotNavEdits.useObject(client, id, client.sceneBaseX() + sceneX, client.sceneBaseY() + sceneY, op)
			})
		}
	}

	private fun entry(client: ClientApi, submenu: Int, label: String, action: String) {
		client.addSubmenuEntry(submenu, label, actions[action] ?: return)
	}

	/** Shift and a right click: the "Bot nav" submenu of a tile (or of an object, to make it an edge's action). */
	private fun menu(client: ClientApi, event: MenuEntryAdded) {
		val graph: BotNavGraph = graph ?: return
		if (!active(client) || !client.isKeyHeld(KeyCode.SHIFT)) {
			return
		}
		if (event.target === MenuTarget.LOC) {
			locMenu(client, event)
			return
		}
		if (event.target !== MenuTarget.TILE) {
			return
		}
		val tile: Int = BotNavTile.pack(client.sceneBaseX() + event.sceneX, client.sceneBaseY() + event.sceneY, event.level)
		menuEdge = BotNavOverlay.hoveredEdge
		menuArea = graph.areaAt(BotNavTile.x(tile), BotNavTile.y(tile), BotNavTile.level(tile))
		val submenu: Int = client.addSubmenu("Bot nav")
		if (submenu < 0) {
			return
		}
		when (mode) {
			MODE_EDGE -> {
				entry(client, submenu, "Finish walk edge here", "finishWalk")
				entry(client, submenu, "Finish action edge here", "finishAction")
			}
			MODE_MOVE_NODE -> entry(client, submenu, "Move node here", "moveHere")
			MODE_AREA_RECT -> entry(client, submenu, "Finish area here", "finishRect")
			MODE_AREA_POLYGON -> {
				entry(client, submenu, "Add polygon point", "addPoint")
				if (polygon.size >= 2) {
					entry(client, submenu, "Close polygon here", "closePolygon")
				}
			}
			MODE_MOVE_POINT -> entry(client, submenu, "Move area point here", "pointHere")
		}
		if (mode != MODE_NONE) {
			entry(client, submenu, "Cancel", "cancel")
			return
		}
		if (graph.isNode(tile)) {
			entry(client, submenu, "Select node", "selectNode")
			entry(client, submenu, "Start edge from node", "startEdge")
			entry(client, submenu, "Move node", "startMove")
			entry(client, submenu, "Delete node and its edges", "deleteNode")
		} else {
			entry(client, submenu, "Add node", "addNode")
			entry(client, submenu, "Start edge here", "startEdge")
		}
		val edge: BotNavEdge? = menuEdge
		if (edge != null) {
			entry(client, submenu, "Edit edge " + edgeName(edge), "editEdge")
			entry(client, submenu, "Reverse edge", "reverseEdge")
			entry(client, submenu, "Delete edge", "deleteEdge")
		}
		val area: BotNavArea? = menuArea
		if (area != null) {
			entry(client, submenu, "Edit area " + area.state.name, "selectArea")
			entry(client, submenu, "Delete area " + area.state.name, "deleteArea")
		}
		val selected: BotNavArea? = selectedArea
		if (selected != null && BotNavEdits.pointAt(selected, tile) >= 0) {
			entry(client, submenu, "Move area point", "movePoint")
		}
		entry(client, submenu, "New rectangle area from here", "startRect")
		entry(client, submenu, "New polygon area from here", "startPolygon")
	}

	/** An object's options, each to set as the selected edge's object action. */
	private fun locMenu(client: ClientApi, event: MenuEntryAdded) {
		if (selectedEdge == null) {
			return
		}
		val type: LocDefinition = client.locType(event.id) ?: return
		var submenu: Int = -1
		for (op in 0 until 5) {
			val option: String = type.action(op) ?: continue
			if (submenu < 0) {
				submenu = client.addSubmenu("Bot nav: edge action")
				if (submenu < 0) {
					return
				}
			}
			client.addSubmenuEntry(submenu, "$option " + (type.name() ?: "object"), objectActions[op])
		}
	}

	/** The edge's comment, or its ends. */
	@JvmStatic
	fun edgeName(edge: BotNavEdge): String {
		val comment: String? = edge.state.comment
		if (!comment.isNullOrEmpty()) {
			return comment
		}
		return BotNavTile.format(edge.state.from) + " -> " + BotNavTile.format(edge.state.to)
	}

	// --- keys and commands ---

	private fun keyPressed(event: KeyPressed) {
		if (BotNavPanel.keyPressed(event)) {
			return
		}
		val client: ClientApi = context?.client() ?: return
		if (!active(client)) {
			return
		}
		if (event.keyCode == KEY_ESCAPE && mode != MODE_NONE) {
			cancelMode()
			event.consume()
			return
		}
		if (!client.isKeyHeld(KeyCode.CONTROL)) {
			return
		}
		val c: Char = event.keyChar.lowercaseChar()
		if (c == 'z' || event.keyChar == '\u001a') {
			BotNavEdits.undo(client)
			event.consume()
		} else if (c == 'y' || event.keyChar == '\u0019') {
			BotNavEdits.redo(client)
			event.consume()
		}
	}

	/** `::botnav` on/off, `export`, `files`, `revert <path|all>`, `import <path>`, `undo`, `redo`, `find <area>`. */
	private fun command(client: ClientApi, args: Array<String>) {
		val context: PluginContext = this.context ?: return
		val what: String = if (args.isEmpty()) "" else args[0].lowercase()
		if (what == "" || what == "on" || what == "off") {
			enabled = if (what == "") !enabled else what == "on"
			context.config().setBoolean("enabled", enabled)
			context.config().save()
			client.consolePrint(if (enabled) "Bot nav editor on." else "Bot nav editor off.")
			return
		}
		val graph: BotNavGraph = graph ?: return client.consolePrint("The bot nav editor is not loaded: ::botnav on.")
		val rest: String = args.drop(1).joinToString(" ")
		when (what) {
			"export" -> export(client)
			"files" -> {
				val edited: List<BotNavFile> = graph.files.filter { it.dirty || it.fromWorkingCopy }
				client.consolePrint(if (edited.isEmpty()) "No files edited." else edited.size.toString() + " file(s) edited:")
				for (file in edited) {
					client.consolePrint("  " + file.path)
				}
			}
			"revert" -> if (rest.isEmpty()) client.consolePrint("::botnav revert <path>|all") else revert(client, rest)
			"import" -> if (rest.isEmpty()) client.consolePrint("::botnav import <path under data/> (the file's TOML on the clipboard)") else import(client, rest)
			"undo" -> BotNavEdits.undo(client)
			"redo" -> BotNavEdits.redo(client)
			"find" -> find(client, graph, rest)
			else -> client.consolePrint("::botnav [on|off|export|files|revert <path>|import <path>|undo|redo|find <area>]")
		}
	}

	private fun find(client: ClientApi, graph: BotNavGraph, name: String) {
		val area: BotNavArea = graph.area(name) ?: return client.consolePrint("No area $name.")
		val xs: IntArray = area.state.xs()
		val ys: IntArray = area.state.ys()
		if (xs.isEmpty() || ys.isEmpty()) {
			return
		}
		val x: Int = (xs.min() + xs.max()) / 2
		val y: Int = (ys.min() + ys.max()) / 2
		val level: Int = maxOf(0, area.state.level())
		client.setHintArrow(x, y, level)
		select(-1, null, area)
		client.consolePrint(area.state.name + " is around " + BotNavTile.format(BotNavTile.pack(x, y, level)) + " (" + area.file.path + ").")
	}

	// --- for the smoke test ---

	/** Whether the data is loaded. */
	@JvmStatic
	fun loaded(): Boolean = dataState == DATA_READY

	/** Switches the editor on without the settings (the smoke test has none). */
	@JvmStatic
	fun switchOn() {
		enabled = true
	}
}
