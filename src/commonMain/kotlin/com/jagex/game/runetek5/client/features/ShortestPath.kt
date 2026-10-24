package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.KeyCode
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
import com.jagex.game.runetek5.plugin.api.events.ClientTick
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.FrameDraw
import com.jagex.game.runetek5.plugin.api.events.GameTick
import com.jagex.game.runetek5.plugin.api.events.ItemContainerChanged
import com.jagex.game.runetek5.plugin.api.events.KeyPressed
import com.jagex.game.runetek5.plugin.api.events.MenuEntryAdded
import com.jagex.game.runetek5.plugin.api.events.MenuOptionClicked
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: RuneLite's Shortest Path (Skretzo's plugin hub plugin), recreated on
 * 634's map. Right-click the world map, or Shift and right-click a tile in the game, for **Set Target**
 * (more targets with **Set Target 2**..., a start of your own with **Set Start**), or **Find closest**
 * on the world map for the nearest bank, altar or other map icon: the way there over the paths, doors,
 * stairs, ladders and shortcuts, by the teleports the player can cast or carries (or keeps in the bank,
 * fetched on the way), and by fairy rings, spirit trees, gliders, ships, carpets, portals, levers and
 * canoes, is drawn on the tiles, the minimap and the world map ([ShortestPathOverlay]), and found again
 * when the player strays from it or what is carried changes. The map and the transports come from Void
 * (ShortestPathData), not Old School's files, which are another world.
 */
object ShortestPath : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("shortestpath", "Shortest Path", "1", PluginApi.VERSION, "Draws the shortest path to a chosen tile, over doors, stairs, shortcuts, teleports, fairy rings and ships, on the game, the minimap and the world map.", "Pixel")

	/** RuneLite's colours (ShortestPathConfig): the path red, a path being found blue, an unreachable one purple. */
	const val RED: Int = 0xFFFF0000.toInt()
	const val BLUE: Int = 0xFF0000FF.toInt()
	const val PURPLE: Int = 0xFFC828F0.toInt()
	const val TRANSPORTS: Int = 0x8000FF00.toInt()
	const val COLLISION: Int = 0x800080FF.toInt()
	const val WHITE: Int = -1
	const val PULSE: Int = 0xFFFFAA00.toInt()
	const val BANK_PICKUP: Int = 0xFF00FFFF.toInt()

	/** The teleport items setting: none, those carried, those carried that are never used up, or all of them as if carried. */
	const val ITEMS_NONE: String = "None"
	const val ITEMS_CARRIED: String = "Inventory"
	const val ITEMS_CARRIED_PERM: String = "Inventory (perm)"
	const val ITEMS_ALL: String = "All"
	const val ITEMS_ALL_PERM: String = "All (perm)"

	/** RuneLite's path styles and tile counter. */
	val STYLES: Array<String> = arrayOf("Tiles", "Lines", "Arrow line", "Turn markers", "Tracer", "Sprite markers")
	val COUNTERS: Array<String> = arrayOf("Disabled", "Travelled", "Remaining")

	/** The bank's container. */
	private const val BANK: Int = 95

	/** Tiles the search visits in a client cycle (50 a second): a cross-country path takes a few. */
	private const val NODES_PER_CYCLE: Int = 40_000

	/** RuneLite's cut-off: past this many tiles the path goes to the nearest one reached. */
	private const val MAX_NODES: Int = 2_500_000

	/** The kinds of transport with a switch and a threshold each: the table's type, the settings' name. */
	@JvmField
	val KINDS: List<Pair<String, String>> = listOf(
		"SHORTCUT" to "AgilityShortcuts",
		"GRAPPLE_SHORTCUT" to "GrappleShortcuts",
		"BOAT" to "Boats",
		"CART" to "Boats",
		"CANOE" to "Canoes",
		"CHARTER_SHIP" to "CharterShips",
		"SHIP" to "Ships",
		"FAIRY_RING" to "FairyRings",
		"GLIDER" to "GnomeGliders",
		"MAGIC_CARPET" to "MagicCarpets",
		"SPIRIT_TREE" to "SpiritTrees",
		"LEVER" to "TeleportationLevers",
		"PORTAL" to "TeleportationPortals",
		"TELEPORT_NPC" to "TeleportationPortals",
		"TELEPORT_SPELL" to "TeleportationSpells",
		"TELEPORT_SPELL_HOME" to "TeleportationSpellsHome",
		"WILDERNESS_OBELISK" to "WildernessObelisks",
	)

	/** The settings' names with their labels and defaults, in RuneLite's order. */
	private val SWITCHES: List<Triple<String, String, Boolean>> = listOf(
		Triple("AgilityShortcuts", "agility shortcuts", true),
		Triple("GrappleShortcuts", "grapple shortcuts", true),
		Triple("Boats", "boats and carts", true),
		Triple("Canoes", "canoes", true),
		Triple("CharterShips", "charter ships", true),
		Triple("Ships", "ships", true),
		Triple("FairyRings", "fairy rings", true),
		Triple("GnomeGliders", "gnome gliders", true),
		Triple("MagicCarpets", "magic carpets", true),
		Triple("SpiritTrees", "spirit trees", true),
		Triple("TeleportationLevers", "teleportation levers", true),
		Triple("TeleportationPortals", "teleportation portals and wizards", true),
		Triple("TeleportationSpells", "teleportation spells", true),
		// Void's 30 minute wait between home teleports is not sent to the client: off unless the player says
		Triple("TeleportationSpellsHome", "home teleport", false),
		Triple("WildernessObelisks", "wilderness obelisks", true),
	)

	@JvmField var enabled: Boolean = false

	// display
	@JvmField var drawTiles: Boolean = true
	@JvmField var drawMinimap: Boolean = true
	@JvmField var drawMap: Boolean = true
	@JvmField var pathStyle: String = "Tiles"
	@JvmField var showTeleportPulse: Boolean = true
	@JvmField var showTileCounter: String = "Disabled"
	@JvmField var tileCounterStep: Int = 1
	@JvmField var showTransportInfo: Boolean = true
	@JvmField var showUnreachableText: Boolean = true
	@JvmField var unreachableText: String = "Unreachable"
	@JvmField var showBankPickupInfo: Boolean = true
	@JvmField var highlightBankPickupItems: Boolean = true
	@JvmField var highlightSpellbookSpells: Boolean = true
	@JvmField var highlightInventoryItems: Boolean = true

	// colours
	@JvmField var colourPath: Int = RED
	@JvmField var colourCalculating: Int = BLUE
	@JvmField var colourUnreachable: Int = PURPLE
	@JvmField var colourTransports: Int = TRANSPORTS
	@JvmField var colourCollisionMap: Int = COLLISION
	@JvmField var colourText: Int = WHITE
	@JvmField var colourTeleportPulse: Int = PULSE
	@JvmField var colourBankPickup: Int = BANK_PICKUP

	// hotkey and debug
	@JvmField var clearPathHotkey: Int = 0
	@JvmField var drawTransports: Boolean = false
	@JvmField var drawCollisionMap: Boolean = false
	@JvmField var drawDebugPanel: Boolean = false

	// routing
	@JvmField var avoidWilderness: Boolean = true
	@JvmField var useTransports: Boolean = true
	@JvmField var useTeleportationItems: String = ITEMS_CARRIED
	@JvmField var includeBankPath: Boolean = false
	@JvmField var currencyThreshold: Int = 100_000
	@JvmField var cancelInstead: Boolean = false
	@JvmField var recalculateDistance: Int = 10
	@JvmField var finishDistance: Int = 5
	@JvmField var unreachableDistance: Int = 5
	@JvmField var calculationCutoff: Int = 5
	@JvmField var costBankVisit: Int = 0
	@JvmField var costConsumableItems: Int = 0
	@JvmField var costPermanentItems: Int = 0
	@JvmField var blockedItems: Set<Int> = emptySet()

	/** By the settings' name: whether that kind is used, and how many tiles it must save. */
	@JvmField val uses: HashMap<String, Boolean> = HashMap()
	@JvmField val costs: HashMap<String, Int> = HashMap()

	/** The map and transports, read the first time a target is set. */
	@JvmField var grid: ShortestPathGrid? = null
	@JvmField var transports: List<ShortestPathTransport> = emptyList()

	/** The world map's icons by name ("Bank", "Altar"...): the tiles each is used from, for Find closest. */
	@JvmField var destinations: Map<String, IntArray> = emptyMap()

	/** Rune id to the staves that stand in for it, from the table. */
	private var staves: Map<Int, Set<Int>> = emptyMap()

	/** The bank's items as last seen, item id to amount, kept between sessions. */
	@JvmField var bank: Map<Int, Int> = emptyMap()

	/** The world tiles the path goes to (the nearest of them), [ShortestPathFinder.pack]ed. */
	@JvmField val targets: ArrayList<Int> = ArrayList()

	/** Where the path starts when the player chose (Set Start), -1 for where the player stands. */
	@JvmField var start: Int = -1

	/** The search running or finished for [targets]. */
	@JvmField var finder: ShortestPathFinder? = null

	/** The last search that finished, drawn (in the calculating colour) while a new one runs. */
	@JvmField var shown: ShortestPathFinder? = null

	/** How long the last search took, in client cycles, for the debug panel. */
	@JvmField var searchCycles: Int = 0

	/** For the smoke test: what the last frame drew (scene tiles, minimap dots, world map segments). */
	@JvmField val drawnCounts: IntArray = IntArray(3)

	private var dataFailed: Boolean = false
	@JvmField var context: PluginContext? = null
	private var setTargetAction: Int = -1
	private var addTargetAction: Int = -1
	private var setStartAction: Int = -1
	private var clearAction: Int = -1
	private var findClosestAction: Int = -1

	/** The Find closest entry clicked: its label is the icon's name. */
	private var clickedLabel: String? = null

	private val overlay: Overlay = Overlay { g, view -> context?.client()?.let { ShortestPathOverlay.drawScene(g, view, it) } }

	private val settingListener: EventListener<SettingChanged> = EventListener { event ->
		if (event.pluginId == DESCRIPTOR.id) {
			context?.config()?.let { readSettings(it) }
			if (!enabled) {
				clearTarget()
			} else if (targets.isNotEmpty()) {
				context?.client()?.let { restart(it) } // a transport switched on or off: find the way again
			}
		}
	}

	private val menuListener: EventListener<MenuEntryAdded> = EventListener { event ->
		if (enabled) {
			context?.client()?.let { menu(it, event) }
		}
	}

	private val clickListener: EventListener<MenuOptionClicked> = EventListener { event ->
		if (event.clickType == findClosestAction) {
			clickedLabel = event.option
		}
	}

	private val clientTickListener: EventListener<ClientTick> = EventListener { _ ->
		val finder: ShortestPathFinder = this.finder ?: return@EventListener
		if (enabled && !finder.done) {
			searchCycles++
			if (finder.step(NODES_PER_CYCLE)) {
				shown = finder
			}
		}
	}

	private val gameTickListener: EventListener<GameTick> = EventListener { _ -> if (enabled) context?.client()?.let { tick(it) } }

	private val frameListener: EventListener<FrameDraw> = EventListener { event -> if (enabled) context?.client()?.let { ShortestPathOverlay.drawInterfaces(event.graphics, it) } }

	/** RuneLite's: the backpack or the worn items changed what may be taken, so the way is found again; the bank is remembered. */
	private val itemListener: EventListener<ItemContainerChanged> = EventListener { event ->
		val client: ClientApi = context?.client() ?: return@EventListener
		if (event.containerId == BANK) {
			readBank(client)
		}
		val finder: ShortestPathFinder? = this.finder
		val id: Int = event.containerId
		if (enabled && targets.isNotEmpty() && finder != null && finder.done && (id == ShortestPathPlayer.INVENTORY || id == ShortestPathPlayer.EQUIPMENT)) {
			restart(client)
		}
	}

	private val keyListener: EventListener<KeyPressed> = EventListener { event ->
		if (enabled && clearPathHotkey != 0 && event.keyCode == clearPathHotkey && targets.isNotEmpty()) {
			clearTarget()
			event.consumed = true
		}
	}

	/** `::path x y [level]`, `::path add x y [level]`, `::path start x y [level]`, `::path closest <icon>`, `::path` alone clears. */
	private val command: CommandHandler = CommandHandler { _, args -> context?.client()?.let { command(it, args) } }

	override fun start(context: PluginContext) {
		this.context = context
		declareSettings(context.settings())
		readSettings(context.config())
		setTargetAction = context.registerMenuAction(MenuAction { kind, x, y, id -> menuTile(kind, x, y, id)?.let { setTarget(context.client(), it) } })
		addTargetAction = context.registerMenuAction(MenuAction { kind, x, y, id -> menuTile(kind, x, y, id)?.let { addTarget(context.client(), it) } })
		setStartAction = context.registerMenuAction(MenuAction { kind, x, y, id -> menuTile(kind, x, y, id)?.let { setStart(context.client(), it) } })
		clearAction = context.registerMenuAction(MenuAction { _, _, _, _ -> clearTarget() })
		findClosestAction = context.registerMenuAction(MenuAction { _, _, _, _ -> clickedLabel?.let { findClosest(context.client(), it) } })
		val events = context.events()
		events.subscribe(Events.SETTING_CHANGED, settingListener)
		events.subscribe(Events.MENU_ENTRY_ADDED, menuListener)
		events.subscribe(Events.MENU_OPTION_CLICKED, clickListener)
		events.subscribe(Events.CLIENT_TICK, clientTickListener)
		events.subscribe(Events.GAME_TICK, gameTickListener)
		events.subscribe(Events.ITEM_CONTAINER_CHANGED, itemListener)
		events.subscribe(Events.FRAME_DRAW, frameListener)
		events.subscribe(Events.KEY_PRESSED, keyListener)
		context.addOverlay(overlay)
		context.registerCommand("path", command)
	}

	override fun stop() {
		clearTarget()
		context = null
	}

	/** RuneLite's ShortestPathConfig and its sidebar's switches, the parts 634 has. */
	private fun declareSettings(s: SettingsSchema) {
		s.toggle("enabled", "Shortest Path", false)
		// display
		s.toggle("drawTiles", "Draw path on tiles", true)
		s.toggle("drawMinimap", "Draw path on minimap", true)
		s.toggle("drawMap", "Draw path on world map", true)
		s.choice("pathStyle", "Path style", "Tiles", STYLES)
		s.toggle("showTeleportPulse", "Teleport pulse", true)
		s.choice("showTileCounter", "Show tile counter", "Disabled", COUNTERS)
		s.number("tileCounterStep", "Tile counter step", 1, 1, 100, 1)
		s.toggle("showTransportInfo", "Show transport info", true)
		s.toggle("showUnreachableText", "Show unreachable text", true)
		s.text("unreachableText", "Unreachable text", "Unreachable", 40)
		s.toggle("showBankPickupInfo", "Show transport hint at pickup", true)
		s.toggle("highlightBankPickupItems", "Highlight bank pickup items", true)
		s.toggle("highlightSpellbookSpells", "Highlight spellbook spells", true)
		s.toggle("highlightInventoryItems", "Highlight inventory items", true)
		// colours
		s.colour("colourPath", "Path colour", RED, true)
		s.colour("colourPathCalculating", "Calculating colour", BLUE, true)
		s.colour("colourPathUnreachable", "Unreachable colour", PURPLE, true)
		s.colour("colourTransports", "Transports colour", TRANSPORTS, true)
		s.colour("colourCollisionMap", "Collision map colour", COLLISION, true)
		s.colour("colourText", "Text colour", WHITE, true)
		s.colour("colourTeleportPulse", "Teleport pulse colour", PULSE, true)
		s.colour("colourBankPickupHighlight", "Bank pickup highlight colour", BANK_PICKUP, true)
		s.keybind("clearPathHotkey", "Clear current path", 0)
		// routing
		s.toggle("avoidWilderness", "Avoid wilderness", true)
		s.toggle("useTransports", "Use transports (stairs, ladders)", true)
		for ((name, label, default) in SWITCHES) {
			s.toggle("use$name", "Use $label", default)
		}
		s.choice("useTeleportationItems", "Use teleportation items", ITEMS_CARRIED, arrayOf(ITEMS_NONE, ITEMS_CARRIED, ITEMS_CARRIED_PERM, ITEMS_ALL, ITEMS_ALL_PERM))
		s.text("blockedTeleportItems", "Blocked teleport items (item ids)", "", 200)
		s.toggle("includeBankPath", "Include path to bank", false)
		s.number("currencyThreshold", "Currency threshold", 100_000, 0, 10_000_000, 1000)
		s.toggle("cancelInstead", "Cancel instead of recalculating", false)
		s.number("recalculateDistance", "Recalculate distance (-1 never)", 10, -1, 50, 1)
		s.number("finishDistance", "Finish distance (-1 never)", 5, -1, 50, 1)
		s.number("unreachableTargetDistanceThreshold", "Unreachable target distance", 5, 0, 50, 1)
		s.number("calculationCutoff", "Calculation cutoff (ticks)", 5, 0, 100, 1)
		for ((name, label, _) in SWITCHES) {
			s.number("cost$name", "Threshold: $label (tiles)", 0, 0, 500, 1)
		}
		s.number("costConsumableTeleportationItems", "Threshold: consumable teleport items (tiles)", 0, 0, 500, 1)
		s.number("costNonConsumableTeleportationItems", "Threshold: permanent teleport items (tiles)", 0, 0, 500, 1)
		s.number("costBankVisit", "Threshold: bank visit (tiles)", 0, 0, 500, 1)
		// debug
		s.toggle("drawTransports", "Debug: draw transports", false)
		s.toggle("drawCollisionMap", "Debug: draw collision map", false)
		s.toggle("drawDebugPanel", "Debug: show debug panel", false)
	}

	@JvmStatic
	fun readSettings(config: PluginConfig) {
		enabled = config.getBoolean("enabled", false)
		drawTiles = config.getBoolean("drawTiles", true)
		drawMinimap = config.getBoolean("drawMinimap", true)
		drawMap = config.getBoolean("drawMap", true)
		pathStyle = config.get("pathStyle", "Tiles").takeIf { it in STYLES } ?: "Tiles"
		showTeleportPulse = config.getBoolean("showTeleportPulse", true)
		showTileCounter = config.get("showTileCounter", "Disabled").takeIf { it in COUNTERS } ?: "Disabled"
		tileCounterStep = config.getInt("tileCounterStep", 1).coerceIn(1, 100)
		showTransportInfo = config.getBoolean("showTransportInfo", true)
		showUnreachableText = config.getBoolean("showUnreachableText", true)
		unreachableText = config.get("unreachableText", "Unreachable")
		showBankPickupInfo = config.getBoolean("showBankPickupInfo", true)
		highlightBankPickupItems = config.getBoolean("highlightBankPickupItems", true)
		highlightSpellbookSpells = config.getBoolean("highlightSpellbookSpells", true)
		highlightInventoryItems = config.getBoolean("highlightInventoryItems", true)
		colourPath = config.getColour("colourPath", RED)
		colourCalculating = config.getColour("colourPathCalculating", BLUE)
		colourUnreachable = config.getColour("colourPathUnreachable", PURPLE)
		colourTransports = config.getColour("colourTransports", TRANSPORTS)
		colourCollisionMap = config.getColour("colourCollisionMap", COLLISION)
		colourText = config.getColour("colourText", WHITE)
		colourTeleportPulse = config.getColour("colourTeleportPulse", PULSE)
		colourBankPickup = config.getColour("colourBankPickupHighlight", BANK_PICKUP)
		clearPathHotkey = config.getInt("clearPathHotkey", 0)
		drawTransports = config.getBoolean("drawTransports", false)
		drawCollisionMap = config.getBoolean("drawCollisionMap", false)
		drawDebugPanel = config.getBoolean("drawDebugPanel", false)
		avoidWilderness = config.getBoolean("avoidWilderness", true)
		useTransports = config.getBoolean("useTransports", true)
		for ((name, _, default) in SWITCHES) {
			uses[name] = config.getBoolean("use$name", default)
			costs[name] = config.getInt("cost$name", 0).coerceIn(0, 500)
		}
		val items: String = config.get("useTeleportationItems", ITEMS_CARRIED)
		useTeleportationItems = if (items in arrayOf(ITEMS_NONE, ITEMS_CARRIED_PERM, ITEMS_ALL, ITEMS_ALL_PERM)) items else ITEMS_CARRIED
		blockedItems = config.get("blockedTeleportItems", "").split(',', ' ').mapNotNull { it.trim().toIntOrNull() }.toSet()
		includeBankPath = config.getBoolean("includeBankPath", false)
		currencyThreshold = config.getInt("currencyThreshold", 100_000).coerceIn(0, 10_000_000)
		cancelInstead = config.getBoolean("cancelInstead", false)
		recalculateDistance = config.getInt("recalculateDistance", 10).coerceIn(-1, 50)
		finishDistance = config.getInt("finishDistance", 5).coerceIn(-1, 50)
		unreachableDistance = config.getInt("unreachableTargetDistanceThreshold", 5).coerceIn(0, 50)
		calculationCutoff = config.getInt("calculationCutoff", 5).coerceIn(0, 100)
		costConsumableItems = config.getInt("costConsumableTeleportationItems", 0).coerceIn(0, 500)
		costPermanentItems = config.getInt("costNonConsumableTeleportationItems", 0).coerceIn(0, 500)
		costBankVisit = config.getInt("costBankVisit", 0).coerceIn(0, 500)
		bank = config.get("bankItems", "").split(',').mapNotNull { pair ->
			val id: Int? = pair.substringBefore(':').toIntOrNull()
			val amount: Int? = pair.substringAfter(':', "").toIntOrNull()
			if (id == null || amount == null) null else id to amount
		}.toMap()
	}

	/** RuneLite's MenuVerbs: Shift on a tile, the world map always, the minimap's Clear Path. */
	private fun menu(client: ClientApi, event: MenuEntryAdded) {
		val target: MenuTarget = event.target
		if (target === MenuTarget.MINIMAP) {
			if (targets.isNotEmpty()) {
				client.addMenuEntry("Clear Path", clearAction)
			}
			return
		}
		val tile: Boolean = target === MenuTarget.TILE && client.isKeyHeld(KeyCode.SHIFT)
		if (!tile && target !== MenuTarget.WORLD_MAP) {
			return
		}
		client.addMenuEntry("Set Target", setTargetAction)
		if (targets.isNotEmpty()) {
			client.addMenuEntry("Set Target " + (targets.size + 1), addTargetAction)
			client.addMenuEntry("Set Start", setStartAction)
			val here: Int = if (tile) ShortestPathFinder.pack(client.sceneBaseX() + event.sceneX, client.sceneBaseY() + event.sceneY, event.level) else -1
			if (!tile || shown?.path?.contains(here) == true) {
				client.addMenuEntry("Clear Path", clearAction)
			}
		}
		if (target === MenuTarget.WORLD_MAP && loadData(client) && destinations.isNotEmpty()) {
			val submenu: Int = client.addSubmenu("Find closest")
			for (name in destinations.keys.sorted()) {
				client.addSubmenuEntry(submenu, name, findClosestAction)
			}
		}
	}

	/** A menu entry's world tile: the world map gives one, a scene tile is on the player's level. */
	private fun menuTile(kind: MenuTarget, x: Int, y: Int, id: Int): Int? {
		val client: ClientApi = context?.client() ?: return null
		return if (kind === MenuTarget.WORLD_MAP) {
			ShortestPathFinder.pack(x, y, id)
		} else {
			ShortestPathFinder.pack(client.sceneBaseX() + x, client.sceneBaseY() + y, client.localPlayer()?.level() ?: 0)
		}
	}

	private fun command(client: ClientApi, args: Array<String>) {
		if (args.isEmpty() || args[0] == "clear") {
			clearTarget()
			client.consolePrint("Shortest path cleared.")
			return
		}
		if (!enabled) {
			client.consolePrint("Shortest Path is off: switch it on in the plugin manager.")
			return
		}
		if (args[0] == "closest") {
			val name: String = args.drop(1).joinToString(" ")
			if (!findClosest(client, name)) {
				client.consolePrint("No map icon called '$name'. Try: " + destinations.keys.sorted().joinToString(", "))
			}
			return
		}
		val verb: String = if (args[0] == "add" || args[0] == "start") args[0] else ""
		val rest: List<String> = if (verb.isEmpty()) args.toList() else args.drop(1)
		val x: Int? = rest.getOrNull(0)?.toIntOrNull()
		val y: Int? = rest.getOrNull(1)?.toIntOrNull()
		if (x == null || y == null) {
			client.consolePrint("Use: path <x> <y> [level], path add|start <x> <y> [level], path closest <icon>, or path clear")
			return
		}
		val tile: Int = ShortestPathFinder.pack(x, y, rest.getOrNull(2)?.toIntOrNull() ?: client.localPlayer()?.level() ?: 0)
		when (verb) {
			"add" -> addTarget(client, tile)
			"start" -> setStart(client, tile)
			else -> setTarget(client, tile)
		}
		client.consolePrint("Finding the way to $x, $y.")
	}

	/** The map, transports and destinations from the jar (or the page's fetch); false while they are not there yet. */
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
			staves = ShortestPathRules.staves(transports)
			val byName = HashMap<String, ArrayList<Int>>()
			for (row in transports) {
				if (row.type == "DEST") {
					byName.getOrPut(row.label) { ArrayList() }.add(ShortestPathFinder.pack(row.srcX, row.srcY, row.srcLevel))
				}
			}
			destinations = byName.mapValues { (_, tiles) -> IntArray(tiles.size) { tiles[it] } }
		} catch (error: Exception) {
			dataFailed = true
			client.consolePrint("Shortest Path could not read its map: " + error.message)
			return false
		}
		return true
	}

	@JvmStatic
	fun setTarget(client: ClientApi, x: Int, y: Int, level: Int) {
		setTarget(client, ShortestPathFinder.pack(x, y, level))
	}

	@JvmStatic
	fun setTarget(client: ClientApi, tile: Int) {
		targets.clear()
		targets.add(tile)
		start = -1
		shown = null
		restart(client)
	}

	/** Set Target 2...: one more place the path may end, the nearest taken. */
	@JvmStatic
	fun addTarget(client: ClientApi, tile: Int) {
		targets.add(tile)
		restart(client)
	}

	/** Set Start: the path from there, not from the player, and never found again as the player moves. */
	@JvmStatic
	fun setStart(client: ClientApi, tile: Int) {
		start = tile
		restart(client)
	}

	/** RuneLite's Find closest: every map icon called [name] a target, the nearest found. */
	@JvmStatic
	fun findClosest(client: ClientApi, name: String): Boolean {
		if (!loadData(client)) {
			return false
		}
		val tiles: IntArray = destinations.entries.firstOrNull { it.key.equals(name, ignoreCase = true) }?.value ?: return false
		targets.clear()
		tiles.forEach { targets.add(it) }
		start = -1
		shown = null
		restart(client)
		return true
	}

	@JvmStatic
	fun clearTarget() {
		targets.clear()
		start = -1
		finder = null
		shown = null
		drawnCounts.fill(0)
	}

	/** Where the search starts: the chosen start, or the player's tile. */
	@JvmStatic
	fun origin(client: ClientApi): Int {
		if (start != -1) {
			return start
		}
		val player: Player = client.localPlayer() ?: return -1
		return ShortestPathFinder.pack(client.sceneBaseX() + player.serverTileX(), client.sceneBaseY() + player.serverTileY(), player.level())
	}

	/** Starts the search again from [origin], with what the player has now. */
	private fun restart(client: ClientApi) {
		val origin: Int = origin(client)
		if (targets.isEmpty() || origin == -1 || !loadData(client)) {
			finder = null
			return
		}
		val items: String = useTeleportationItems
		val rules = ShortestPathRules(
			ShortestPathPlayer.read(client),
			staves,
			checkItems = items == ITEMS_CARRIED || items == ITEMS_CARRIED_PERM,
			coinLimit = currencyThreshold,
			permanentOnly = items == ITEMS_CARRIED_PERM || items == ITEMS_ALL_PERM,
			blocked = blockedItems,
			bank = bank,
		)
		val options = ShortestPathFinder.Options()
		val carried = HashMap<Int, MutableList<ShortestPathTransport>>()
		val fromBank = HashMap<Int, MutableList<ShortestPathTransport>>()
		val anywhere = ArrayList<ShortestPathTransport>()
		val bankAnywhere = ArrayList<ShortestPathTransport>()
		val banking: Boolean = includeBankPath && bank.isNotEmpty()
		for (transport in transports) {
			if (!usable(transport)) {
				continue
			}
			val now: Boolean = rules.meets(transport)
			val later: Boolean = banking && !now && rules.meets(transport, true)
			if (now || later) {
				val costed: ShortestPathTransport = costed(transport)
				if (transport.anywhere) {
					(if (now) anywhere else bankAnywhere).add(costed)
				} else {
					(if (now) carried else fromBank).getOrPut(ShortestPathFinder.pack(transport.srcX, transport.srcY, transport.srcLevel)) { ArrayList() }.add(costed)
				}
			}
		}
		options.transports = carried
		options.anywhere = anywhere
		options.bankTransports = fromBank
		options.bankAnywhere = bankAnywhere
		if (banking) {
			options.banks = destinations["Bank"]?.toHashSet() ?: emptySet()
			options.bankCost = costBankVisit
		}
		options.avoidWilderness = avoidWilderness
		options.maxNodes = MAX_NODES
		// RuneLite's cutoff is in game ticks of no progress; the search runs a slice a client cycle (30 a tick)
		options.cutoffSteps = calculationCutoff * 30
		finder = ShortestPathFinder(grid!!, origin, targets.toIntArray(), options)
		searchCycles = 0
	}

	/** The transport with its kind's threshold added: it must save that many more tiles than walking. */
	private fun costed(transport: ShortestPathTransport): ShortestPathTransport {
		val extra: Int = when (transport.type) {
			"TELEPORT_ITEM" -> if (ShortestPathRules.isUsedUp(transport)) costConsumableItems else costPermanentItems
			else -> costs[kindName(transport.type)] ?: 0
		}
		if (extra == 0) {
			return transport
		}
		return ShortestPathTransport(transport.type, transport.srcX, transport.srcY, transport.srcLevel, transport.dstX, transport.dstY, transport.dstLevel, transport.ticks + extra, transport.label, transport.requirements, transport.hint)
	}

	private fun kindName(type: String): String? = KINDS.firstOrNull { it.first == type }?.second

	/** RuneLite's switches, one for each kind of transport. */
	private fun usable(transport: ShortestPathTransport): Boolean = when (transport.type) {
		"OBJECT" -> useTransports
		"TELEPORT_ITEM" -> useTeleportationItems != ITEMS_NONE
		else -> kindName(transport.type)?.let { uses[it] } ?: false // STAFF and DEST rows are data, not a way across
	}

	/** The bank's items, remembered for a path that fetches a teleport from the bank. */
	private fun readBank(client: ClientApi) {
		val container = client.itemContainer(BANK) ?: return
		val items = HashMap<Int, Int>()
		for (slot in 0 until container.size()) {
			val id: Int = container.itemId(slot)
			if (id >= 0) {
				items[id] = (items[id] ?: 0) + container.amount(slot)
			}
		}
		if (items == bank) {
			return
		}
		bank = items
		context?.config()?.let { config ->
			config.set("bankItems", items.entries.joinToString(",") { "${it.key}:${it.value}" })
			config.save()
		}
	}

	/** RuneLite's onGameTick: the target reached clears it, a player off the path finds it again (or cancels it). */
	private fun tick(client: ClientApi) {
		if (targets.isEmpty() || client.gameState() !== GameState.LOGGED_IN) {
			return
		}
		val player: Player = client.localPlayer() ?: return
		val here: Int = ShortestPathFinder.pack(client.sceneBaseX() + player.serverTileX(), client.sceneBaseY() + player.serverTileY(), player.level())
		if (finishDistance >= 0 && targets.any { ShortestPathFinder.distance(here, it) <= finishDistance }) {
			clearTarget()
			return
		}
		val finder: ShortestPathFinder? = this.finder
		if (finder == null) {
			restart(client) // the data had not come yet
			return
		}
		if (!finder.done || start != -1 || recalculateDistance < 0) {
			return
		}
		if (finder.path.none { ShortestPathFinder.distance(here, it) <= recalculateDistance }) {
			if (cancelInstead) clearTarget() else restart(client)
		}
	}

	/** Whether the finished path stops short of every target by more than the unreachable distance. */
	@JvmStatic
	fun unreachable(finder: ShortestPathFinder): Boolean = finder.done && !finder.reached && finder.endDistance > unreachableDistance

	/** The path's colour: unreachable, calculating while a new search runs, or the path's own. */
	@JvmStatic
	fun colour(): Int {
		val shown: ShortestPathFinder = shown ?: return colourPath
		return when {
			finder !== shown -> colourCalculating
			unreachable(shown) -> colourUnreachable
			else -> colourPath
		}
	}
}
