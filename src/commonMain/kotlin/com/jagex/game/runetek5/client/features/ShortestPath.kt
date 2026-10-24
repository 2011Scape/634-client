package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.KeyCode
import com.jagex.game.runetek5.plugin.api.Graphics
import com.jagex.game.runetek5.plugin.api.MenuAction
import com.jagex.game.runetek5.plugin.api.MenuTarget
import com.jagex.game.runetek5.plugin.api.Overlay
import com.jagex.game.runetek5.plugin.api.Player
import com.jagex.game.runetek5.plugin.api.Plugin
import com.jagex.game.runetek5.plugin.api.PluginApi
import com.jagex.game.runetek5.plugin.api.PluginConfig
import com.jagex.game.runetek5.plugin.api.PluginContext
import com.jagex.game.runetek5.plugin.api.PluginDescriptor
import com.jagex.game.runetek5.plugin.api.SettingsSchema
import com.jagex.game.runetek5.plugin.api.Viewport
import com.jagex.game.runetek5.plugin.api.events.ClientTick
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.FrameDraw
import com.jagex.game.runetek5.plugin.api.events.GameTick
import com.jagex.game.runetek5.plugin.api.events.MenuEntryAdded
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: RuneLite's Shortest Path (Skretzo's plugin hub plugin), recreated on
 * 634's map. Right-click the world map, or Shift and right-click a tile in the game, for **Set target**: the way there over
 * the paths, doors, stairs and ladders is drawn on the tiles, the minimap and the world map, and found
 * again when the player strays from it. The map and the transports come from Void (ShortestPathData),
 * not Old School's files, which are another world.
 */
object ShortestPath : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("shortestpath", "Shortest Path", "1", PluginApi.VERSION, "Draws the shortest path to a chosen tile, over doors, stairs and ladders, on the game, the minimap and the world map.", "Pixel")

	/** RuneLite's colours: the path red, a path still being found yellow, the transports' tiles cyan. */
	const val RED: Int = 0xFFFF0000.toInt()
	const val YELLOW: Int = 0xFFFFFF00.toInt()
	const val CYAN: Int = 0xFF00FFFF.toInt()
	private const val BLACK: Int = 0xFF000000.toInt()
	private const val WHITE: Int = -1

	/** Tiles the search visits in a client cycle (50 a second): a cross-country path takes a few. */
	private const val NODES_PER_CYCLE: Int = 40_000

	/** RuneLite's cut-off: past this many tiles the path goes to the nearest one reached. */
	private const val MAX_NODES: Int = 2_500_000

	@JvmField var enabled: Boolean = false
	@JvmField var useTransports: Boolean = true
	@JvmField var useAgilityShortcuts: Boolean = true
	@JvmField var drawTiles: Boolean = true
	@JvmField var drawMinimap: Boolean = true
	@JvmField var drawMap: Boolean = true
	@JvmField var drawTransports: Boolean = true
	@JvmField var colourPath: Int = RED
	@JvmField var colourCalculating: Int = YELLOW
	@JvmField var colourTransports: Int = CYAN
	@JvmField var reachedDistance: Int = 5
	@JvmField var recalculateDistance: Int = 10

	/** The map and transports, read the first time a target is set. */
	@JvmField var grid: ShortestPathGrid? = null
	@JvmField var transports: List<ShortestPathTransport> = emptyList()

	/** The world tile the path goes to ([ShortestPathFinder.pack]ed), -1 for none. */
	@JvmField var target: Int = -1

	/** The search running or finished for [target]. */
	@JvmField var finder: ShortestPathFinder? = null

	/** For the smoke test: what the last frame drew (scene tiles, minimap dots, world map segments). */
	@JvmField val drawnCounts: IntArray = IntArray(3)

	private var dataFailed: Boolean = false
	private var context: PluginContext? = null
	private var setTargetAction: Int = -1
	private var clearTargetAction: Int = -1

	private val xs: IntArray = IntArray(4)
	private val ys: IntArray = IntArray(4)
	private val point: IntArray = IntArray(2)
	private val previous: IntArray = IntArray(2)
	private val area: IntArray = IntArray(4)

	private val overlay: Overlay = Overlay { g, view -> drawScene(g, view) }

	private val settingListener: EventListener<SettingChanged> = EventListener { event ->
		if (event.pluginId == DESCRIPTOR.id) {
			context?.config()?.let { readSettings(it) }
			if (!enabled) {
				clearTarget()
			} else if (target != -1) {
				context?.client()?.let { restart(it) } // a transport switched on or off: find the way again
			}
		}
	}

	private val menuListener: EventListener<MenuEntryAdded> = EventListener { event ->
		if (!enabled) {
			return@EventListener
		}
		val client: ClientApi = context?.client() ?: return@EventListener
		// RuneLite's: on the world map always, on a tile in the game with Shift held (the walk menu stays as it was)
		if (event.target === MenuTarget.WORLD_MAP || event.target === MenuTarget.TILE && client.isKeyHeld(KeyCode.SHIFT)) {
			client.addMenuEntry("Set target", setTargetAction)
			if (target != -1) {
				client.addMenuEntry("Clear target", clearTargetAction)
			}
		}
	}

	private val clientTickListener: EventListener<ClientTick> = EventListener { _ ->
		val finder: ShortestPathFinder = this.finder ?: return@EventListener
		if (enabled && !finder.done) {
			finder.step(NODES_PER_CYCLE)
		}
	}

	private val gameTickListener: EventListener<GameTick> = EventListener { _ -> if (enabled) context?.client()?.let { tick(it) } }

	private val frameListener: EventListener<FrameDraw> = EventListener { event -> if (enabled) context?.client()?.let { drawWorldMap(event.graphics, it) } }

	/** `::path x y [level]` sets the target, `::path` alone clears it. */
	private val command: CommandHandler = CommandHandler { _, args ->
		val client: ClientApi = context?.client() ?: return@CommandHandler
		if (args.isEmpty() || args[0] == "clear") {
			clearTarget()
			client.consolePrint("Shortest path cleared.")
			return@CommandHandler
		}
		val x: Int? = args[0].toIntOrNull()
		val y: Int? = args.getOrNull(1)?.toIntOrNull()
		if (x == null || y == null) {
			client.consolePrint("Use: path <x> <y> [level], or path clear")
			return@CommandHandler
		}
		val level: Int = args.getOrNull(2)?.toIntOrNull() ?: client.localPlayer()?.level() ?: 0
		if (!enabled) {
			client.consolePrint("Shortest Path is off: switch it on in the plugin manager.")
			return@CommandHandler
		}
		setTarget(client, x, y, level)
		client.consolePrint("Finding the way to $x, $y, $level.")
	}

	override fun start(context: PluginContext) {
		this.context = context
		declareSettings(context.settings())
		readSettings(context.config())
		setTargetAction = context.registerMenuAction(MenuAction { kind, x, y, id -> menuSetTarget(kind, x, y, id) })
		clearTargetAction = context.registerMenuAction(MenuAction { _, _, _, _ -> clearTarget() })
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		context.events().subscribe(Events.MENU_ENTRY_ADDED, menuListener)
		context.events().subscribe(Events.CLIENT_TICK, clientTickListener)
		context.events().subscribe(Events.GAME_TICK, gameTickListener)
		context.events().subscribe(Events.FRAME_DRAW, frameListener)
		context.addOverlay(overlay)
		context.registerCommand("path", command)
	}

	override fun stop() {
		clearTarget()
		context = null
	}

	/** RuneLite's ShortestPathConfig, the parts 634 has. */
	private fun declareSettings(s: SettingsSchema) {
		s.toggle("enabled", "Shortest Path", false)
		s.toggle("useTransports", "Use transports (stairs, ladders)", true)
		s.toggle("useAgilityShortcuts", "Use agility shortcuts", true)
		s.toggle("drawTiles", "Draw path on tiles", true)
		s.toggle("drawMinimap", "Draw path on minimap", true)
		s.toggle("drawMap", "Draw path on world map", true)
		s.toggle("drawTransports", "Draw transports", true)
		s.colour("colourPath", "Path colour", RED, true)
		s.colour("colourPathCalculating", "Calculating colour", YELLOW, true)
		s.colour("colourTransports", "Transports colour", CYAN, true)
		s.number("reachedDistance", "Reached distance", 5, 0, 50, 1)
		s.number("recalculateDistance", "Recalculate distance", 10, 1, 50, 1)
	}

	@JvmStatic
	fun readSettings(config: PluginConfig) {
		enabled = config.getBoolean("enabled", false)
		useTransports = config.getBoolean("useTransports", true)
		useAgilityShortcuts = config.getBoolean("useAgilityShortcuts", true)
		drawTiles = config.getBoolean("drawTiles", true)
		drawMinimap = config.getBoolean("drawMinimap", true)
		drawMap = config.getBoolean("drawMap", true)
		drawTransports = config.getBoolean("drawTransports", true)
		colourPath = config.getColour("colourPath", RED)
		colourCalculating = config.getColour("colourPathCalculating", YELLOW)
		colourTransports = config.getColour("colourTransports", CYAN)
		reachedDistance = config.getInt("reachedDistance", 5).coerceIn(0, 50)
		recalculateDistance = config.getInt("recalculateDistance", 10).coerceIn(1, 50)
	}

	private fun menuSetTarget(kind: MenuTarget, x: Int, y: Int, id: Int) {
		val client: ClientApi = context?.client() ?: return
		if (kind === MenuTarget.WORLD_MAP) {
			setTarget(client, x, y, id)
		} else {
			setTarget(client, client.sceneBaseX() + x, client.sceneBaseY() + y, client.localPlayer()?.level() ?: 0)
		}
	}

	/** The map and transports from the jar (or the page's fetch); false while they are not there yet. */
	private fun loadData(client: ClientApi): Boolean {
		if (grid != null) {
			return true
		}
		if (dataFailed) {
			return false
		}
		val collision: ByteArray? = lang.readResource("/shortest-path/collision.gz")
		val table: ByteArray? = lang.readResource("/shortest-path/transports.tsv.gz")
		if (collision == null || table == null) {
			return false
		}
		@Suppress("TooGenericExceptionCaught") // a damaged file must leave the plugin without a map, not take the client down
		try {
			grid = ShortestPathGrid.decode(collision)
			transports = ShortestPathTransport.decode(table)
		} catch (error: Exception) {
			dataFailed = true
			client.consolePrint("Shortest Path could not read its map: " + error.message)
			return false
		}
		return true
	}

	@JvmStatic
	fun setTarget(client: ClientApi, x: Int, y: Int, level: Int) {
		target = ShortestPathFinder.pack(x, y, level)
		restart(client)
	}

	@JvmStatic
	fun clearTarget() {
		target = -1
		finder = null
		drawnCounts.fill(0)
	}

	/** Starts the search again from where the player stands. */
	private fun restart(client: ClientApi) {
		val player: Player = client.localPlayer() ?: return
		if (target == -1 || !loadData(client)) {
			finder = null
			return
		}
		val start: Int = ShortestPathFinder.pack(client.sceneBaseX() + player.serverTileX(), client.sceneBaseY() + player.serverTileY(), player.level())
		finder = ShortestPathFinder(grid!!, usableTransports(), emptyList(), start, target, MAX_NODES)
	}

	/** The transports this search may take, by the tile they start from. */
	private fun usableTransports(): Map<Int, List<ShortestPathTransport>> {
		val map = HashMap<Int, MutableList<ShortestPathTransport>>()
		for (transport in transports) {
			if (transport.anywhere || !usable(transport)) {
				continue
			}
			map.getOrPut(ShortestPathFinder.pack(transport.srcX, transport.srcY, transport.srcLevel)) { ArrayList() }.add(transport)
		}
		return map
	}

	private fun usable(transport: ShortestPathTransport): Boolean = when (transport.type) {
		"OBJECT" -> useTransports
		"SHORTCUT" -> useAgilityShortcuts
		else -> false
	}

	/** RuneLite's onGameTick: the target reached clears it, a player off the path finds it again. */
	private fun tick(client: ClientApi) {
		if (target == -1 || client.gameState() !== GameState.LOGGED_IN) {
			return
		}
		val player: Player = client.localPlayer() ?: return
		val here: Int = ShortestPathFinder.pack(client.sceneBaseX() + player.serverTileX(), client.sceneBaseY() + player.serverTileY(), player.level())
		if (ShortestPathFinder.distance(here, target) <= reachedDistance) {
			clearTarget()
			return
		}
		val finder: ShortestPathFinder? = this.finder
		if (finder == null) {
			restart(client) // the data had not come yet
			return
		}
		if (!finder.done) {
			return
		}
		var near = false
		for (node in finder.path) {
			if (ShortestPathFinder.distance(here, node) <= recalculateDistance) {
				near = true
				break
			}
		}
		if (!near) {
			restart(client)
		}
	}

	private fun colour(): Int = if (finder?.done == true) colourPath else colourCalculating

	/** The path's tiles on the game view, the transports' in their own colour with their names. */
	private fun drawScene(g: Graphics, view: Viewport) {
		drawnCounts[0] = 0
		val client: ClientApi = context?.client() ?: return
		val finder: ShortestPathFinder = this.finder ?: return
		if (!enabled || !finder.done || client.gameState() !== GameState.LOGGED_IN) {
			return
		}
		val player: Player = client.localPlayer() ?: return
		val level: Int = player.level()
		val baseX: Int = client.sceneBaseX()
		val baseY: Int = client.sceneBaseY()
		val size: Int = client.sceneSize()
		val path: IntArray = finder.path
		for (i in path.indices) {
			val node: Int = path[i]
			if (!inScene(node, level, baseX, baseY, size)) {
				continue
			}
			val sceneX: Int = ShortestPathFinder.x(node) - baseX
			val sceneY: Int = ShortestPathFinder.y(node) - baseY
			// the tile a transport leaves from is the one before the transport's own
			val transport: ShortestPathTransport? = if (i + 1 < path.size) finder.pathTransports[i + 1] else null
			val isTransport: Boolean = transport != null && drawTransports
			if (drawTiles || isTransport) {
				val border: Int = if (isTransport) colourTransports else colour()
				if (TileOutline.draw(g, view, client, sceneX, sceneY, level, 1, border, (0x40 shl 24) or (border and 0xFFFFFF), 1, xs, ys)) {
					drawnCounts[0]++
				}
			}
			if (isTransport && g.fontReady() && client.projectTile(sceneX, sceneY, level, 0, point)) {
				g.textCentered(view.x + point[0], view.y + point[1], transport!!.label, WHITE, BLACK)
			}
		}
	}

	/** The minimap's dots, then the world map's line: both drawn over the interfaces. */
	private fun drawWorldMap(g: Graphics, client: ClientApi) {
		drawnCounts[1] = 0
		drawnCounts[2] = 0
		val finder: ShortestPathFinder = this.finder ?: return
		if (!finder.done || client.gameState() !== GameState.LOGGED_IN) {
			return
		}
		if (drawMinimap) {
			drawMinimap(g, client, finder)
		}
		if (drawMap && client.worldMapArea(area)) {
			g.clip(area[0], area[1], area[2], area[3])
			val path: IntArray = finder.path
			var havePrevious = false
			for (i in path.indices) {
				val node: Int = path[i]
				if (!client.projectWorldMap(ShortestPathFinder.x(node), ShortestPathFinder.y(node), ShortestPathFinder.level(node), point)) {
					havePrevious = false
					continue
				}
				// a transport is a jump, not a line across the map
				if (havePrevious && finder.pathTransports[i] == null) {
					g.thickLine(previous[0], previous[1], point[0], point[1], 2, colourPath)
					drawnCounts[2]++
				}
				previous[0] = point[0]
				previous[1] = point[1]
				havePrevious = true
			}
			if (client.projectWorldMap(ShortestPathFinder.x(target), ShortestPathFinder.y(target), ShortestPathFinder.level(target), point)) {
				g.fillCircle(point[0], point[1], 3, colourPath)
			}
			g.unclip()
		}
	}

	private fun drawMinimap(g: Graphics, client: ClientApi, finder: ShortestPathFinder) {
		val player: Player = client.localPlayer() ?: return
		if (!client.minimapArea(area)) {
			return
		}
		val level: Int = player.level()
		val baseX: Int = client.sceneBaseX()
		val baseY: Int = client.sceneBaseY()
		val size: Int = client.sceneSize()
		g.clip(area[0], area[1], area[2], area[3])
		for (node in finder.path) {
			if (inScene(node, level, baseX, baseY, size) && client.projectMinimap(ShortestPathFinder.x(node) - baseX, ShortestPathFinder.y(node) - baseY, point)) {
				g.fillRect(point[0] - 1, point[1] - 1, 2, 2, colourPath)
				drawnCounts[1]++
			}
		}
		g.unclip()
	}

	/** Whether world tile [node] is on [level] and inside the loaded scene at ([baseX], [baseY]), [size] across. */
	private fun inScene(node: Int, level: Int, baseX: Int, baseY: Int, size: Int): Boolean {
		val sceneX: Int = ShortestPathFinder.x(node) - baseX
		val sceneY: Int = ShortestPathFinder.y(node) - baseY
		return ShortestPathFinder.level(node) == level && sceneX >= 0 && sceneY >= 0 && sceneX < size && sceneY < size
	}
}
