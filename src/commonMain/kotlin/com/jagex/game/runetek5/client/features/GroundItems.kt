package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.core.constants.MiniMenuAction
import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.ItemDefinition
import com.jagex.game.runetek5.plugin.api.KeyCode
import com.jagex.game.runetek5.plugin.api.MenuAction
import com.jagex.game.runetek5.plugin.api.MenuEntry
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
import com.jagex.game.runetek5.plugin.api.events.GameStateChanged
import com.jagex.game.runetek5.plugin.api.events.GroundItem
import com.jagex.game.runetek5.plugin.api.events.KeyPressed
import com.jagex.game.runetek5.plugin.api.events.KeyReleased
import com.jagex.game.runetek5.plugin.api.events.MenuBuilt
import com.jagex.game.runetek5.plugin.api.events.MenuEntryAdded
import com.jagex.game.runetek5.plugin.api.events.MenuOptionClicked
import com.jagex.game.runetek5.plugin.api.events.MouseClicked
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: RuneLite's Ground Items, option for option. Every item on the
 * ground gets its name, quantity and Grand Exchange and High Alchemy values over its tile, coloured by
 * the value tier it reaches or the lists the player keeps (highlighted, hidden: names with `>N`/`<N`
 * and `*`); hold the hotkey (Alt) to see hidden items too and click the boxes after a name to hide or
 * highlight it, double-tap it to hide them all. The right-click menu shows the colours and the
 * quantities, sinks hidden items and merges identical entries; Shift and right-click an item for a
 * colour of its own (Color, with the client's colour picker). Despawn timers count down the player's
 * own drops, kills' loot and items put on tables; lootbeams mark the valuable piles; a notification
 * says when a highlighted or valuable drop appears.
 *
 * A built-in plugin (BuiltinPlugins, docs/PLUGINS.md); the drawing is in GroundItemsOverlay.
 */
object GroundItems : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("grounditems", "Ground Items", "1", PluginApi.VERSION, "RuneLite's Ground Items: names, quantities and Grand Exchange and High Alchemy values over every item on the ground, coloured by value tier and your highlight and hide lists (hold Alt to edit them), despawn timers, lootbeams and drop notifications.", "Pixel")

	const val COINS: Int = 995

	// where an item came from, for its despawn timer (RuneLite's LootType)
	const val LOOT_UNKNOWN: Int = 0
	const val LOOT_DROPPED: Int = 1
	const val LOOT_TABLE: Int = 2
	const val LOOT_PVM: Int = 3
	const val LOOT_PVP: Int = 4

	// the settings' choices, in RuneLite's order and words
	@JvmField
	val TIERS: Array<String> = arrayOf("Off", "Low", "Medium", "High", "Insane")

	@JvmField
	val PRICE_MODES: Array<String> = arrayOf("High Alchemy", "Grand Exchange", "Both", "Off")

	@JvmField
	val ITEM_HIGHLIGHT_MODES: Array<String> = arrayOf("None", "Overlay", "Right-click menu", "Both")

	@JvmField
	val MENU_HIGHLIGHT_MODES: Array<String> = arrayOf("Menu option", "Menu item name", "Both")

	@JvmField
	val VALUE_MODES: Array<String> = arrayOf("High Alchemy", "Grand Exchange", "Highest")

	@JvmField
	val OWNERSHIP_MODES: Array<String> = arrayOf("All", "Takeable", "Drops")

	@JvmField
	val TIMER_MODES: Array<String> = arrayOf("Off", "Pie", "Ticks", "Seconds")

	@JvmField
	val BEAM_STYLES: Array<String> = arrayOf("Light", "Modern")

	const val PRICE_HA: Int = 0
	const val PRICE_GE: Int = 1
	const val PRICE_BOTH: Int = 2
	const val PRICE_OFF: Int = 3

	const val HIGHLIGHT_NONE: Int = 0
	const val HIGHLIGHT_OVERLAY: Int = 1
	const val HIGHLIGHT_MENU: Int = 2
	const val HIGHLIGHT_BOTH: Int = 3

	const val MENU_OPTION: Int = 0
	const val MENU_NAME: Int = 1
	const val MENU_BOTH: Int = 2

	const val VALUE_HA: Int = 0
	const val VALUE_GE: Int = 1
	const val VALUE_HIGHEST: Int = 2

	const val OWNERSHIP_ALL: Int = 0
	const val OWNERSHIP_TAKEABLE: Int = 1
	const val OWNERSHIP_DROPS: Int = 2

	const val TIMER_OFF: Int = 0
	const val TIMER_PIE: Int = 1
	const val TIMER_TICKS: Int = 2
	const val TIMER_SECONDS: Int = 3

	const val BEAM_LIGHT: Int = 0
	const val BEAM_MODERN: Int = 1

	/** The settings key of an item's own colour: this plus its id (RuneLite's highlight_<id>). */
	const val ITEM_COLOUR_PREFIX: String = "highlight_"

	private const val DEFAULT_HIDDEN: String = "Vial, Ashes, Coins, Bones, Bucket, Jug, Seaweed"

	private const val MAX_LIST: Int = 2000

	private const val WHITE: Int = -0x1
	private const val GRAY: Int = 0xFF808080.toInt()
	private const val RED: Int = 0xFFFF0000.toInt()
	private const val GREEN: Int = 0xFF00FF00.toInt()
	private const val BLUE: Int = 0xFF0000FF.toInt()
	private const val YELLOW: Int = 0xFFFFFF00.toInt()
	private const val MAGENTA: Int = 0xFFFF00FF.toInt()

	/** Client cycles (20 ms) in a minute, and the despawn times RuneLite counted before the server told it (312776c5f). */
	const val CYCLES_PER_MINUTE: Int = 3000
	const val DESPAWN_LOOT: Int = 2 * CYCLES_PER_MINUTE
	const val DESPAWN_DROP: Int = 3 * CYCLES_PER_MINUTE
	const val DESPAWN_TABLE: Int = 10 * CYCLES_PER_MINUTE
	const val DESPAWN_INSTANCE: Int = 30 * CYCLES_PER_MINUTE
	const val VISIBLE_AFTER: Int = CYCLES_PER_MINUTE

	/** How long the server's re-sent items may take after the scene moves, before what it did not send again is dropped. */
	private const val REBUILD_CYCLES: Int = 60

	/** One item (a stack of one id) on one tile, in world tiles; its colour and verdict worked out on demand and kept until something changes. */
	class Item(@JvmField val itemId: Int, @JvmField val worldX: Int, @JvmField val worldY: Int, @JvmField val level: Int) {
		@JvmField var quantity: Int = 0
		@JvmField var name: String = ""
		@JvmField var gePrice: Int = 0
		@JvmField var haPrice: Int = 0
		@JvmField var tradeable: Boolean = false
		@JvmField var stackable: Boolean = false
		@JvmField var height: Int = 0
		@JvmField var spawnCycle: Int = -1
		@JvmField var lootType: Int = LOOT_UNKNOWN
		@JvmField var ownership: Int = GroundItem.OWNERSHIP_NONE

		/** False from a scene move until the server sends the item again. */
		@JvmField var confirmed: Boolean = true
		@JvmField var judged: Boolean = false
		@JvmField var highlighted: Boolean = false
		@JvmField var hidden: Boolean = false
		@JvmField var colour: Int = WHITE

		/** Its place in its tile's stack of labels this frame. */
		@JvmField var offset: Int = 0

		fun geTotal(): Long = gePrice.toLong() * quantity

		fun haTotal(): Long = haPrice.toLong() * quantity

		/** RuneLite's reset(): judged again when next drawn. */
		fun reset() {
			judged = false
		}
	}

	@JvmField
	var enabled: Boolean = true

	// the settings, as RuneLite's GroundItemsConfig names them
	@JvmField var highlightedList: ArrayList<GroundItemRules.Threshold> = ArrayList()
	@JvmField var hiddenList: ArrayList<GroundItemRules.Threshold> = ArrayList()
	@JvmField var highlightedCsv: String = ""
	@JvmField var hiddenCsv: String = DEFAULT_HIDDEN
	@JvmField var showHighlightedOnly: Boolean = false
	@JvmField var dontHideUntradeables: Boolean = true
	@JvmField var showMenuItemQuantities: Boolean = true
	@JvmField var recolorMenuHiddenItems: Boolean = false
	@JvmField var deprioritizeHiddenItems: Boolean = false
	@JvmField var highlightTiles: Boolean = false
	@JvmField var notifyHighlightedDrops: Boolean = false
	@JvmField var notifyTier: Int = 0
	@JvmField var priceDisplayMode: Int = PRICE_BOTH
	@JvmField var itemHighlightMode: Int = HIGHLIGHT_BOTH
	@JvmField var menuHighlightMode: Int = MENU_NAME
	@JvmField var valueCalculationMode: Int = VALUE_HIGHEST
	@JvmField var hideUnderValue: Int = 0
	@JvmField var defaultColour: Int = WHITE
	@JvmField var highlightedColour: Int = 0xFFAA00FF.toInt()
	@JvmField var hiddenColour: Int = GRAY
	@JvmField var lowValueColour: Int = 0xFF66B2FF.toInt()
	@JvmField var lowValuePrice: Int = 20_000
	@JvmField var mediumValueColour: Int = 0xFF99FF99.toInt()
	@JvmField var mediumValuePrice: Int = 100_000
	@JvmField var highValueColour: Int = 0xFFFF9600.toInt()
	@JvmField var highValuePrice: Int = 1_000_000
	@JvmField var insaneValueColour: Int = 0xFFFF66B2.toInt()
	@JvmField var insaneValuePrice: Int = 10_000_000
	@JvmField var ownershipFilterMode: Int = OWNERSHIP_ALL
	@JvmField var doubleTapDelay: Int = 250
	@JvmField var collapseEntries: Boolean = false
	@JvmField var groundItemTimers: Int = TIMER_OFF
	@JvmField var textOutline: Boolean = false
	@JvmField var showLootbeamForHighlighted: Boolean = false
	@JvmField var showLootbeamTier: Int = 3
	@JvmField var lootbeamStyle: Int = BEAM_MODERN
	@JvmField var hotkey: Int = KeyCode.ALT

	/** The value tiers that are on, highest first: their prices and colours (RuneLite's priceChecks). */
	@JvmField var tierPrices: IntArray = IntArray(0)
	@JvmField var tierColours: IntArray = IntArray(0)

	/** Every item on the ground the client knows of, in the order they appeared. */
	@JvmField
	val items: ArrayList<Item> = ArrayList()

	private val byKey: HashMap<Long, Item> = HashMap()

	/** Whether the hotkey is held, and whether everything is hidden (a double tap). */
	@JvmField var hotKeyPressed: Boolean = false
	@JvmField var hideAll: Boolean = false

	private var hotkeyDown: Boolean = false
	private var lastPress: Long = -1L

	/** The boxes under the mouse this frame while the hotkey is held, and their items (RuneLite's textBoxBounds and the rest). */
	@JvmField val textBox: IntArray = IntArray(4)
	@JvmField var textBoxItem: Item? = null
	@JvmField val hiddenBox: IntArray = IntArray(4)
	@JvmField var hiddenBoxItem: Item? = null
	@JvmField val highlightBox: IntArray = IntArray(4)
	@JvmField var highlightBoxItem: Item? = null

	/** The player's recent drops (RuneLite's droppedItemQueue of 16) and the item last used on a location. */
	private val droppedItems: ArrayList<Int> = ArrayList()
	private var usingItem: Int = -1
	private var lastUsedItem: Int = -1

	/** An item taken off a tile this cycle, so that a change of amount (gone and back at once) keeps its timer. */
	private var removedKey: Long = -1L
	private var removedCycle: Int = -1
	private var removedSpawnCycle: Int = -1
	private var removedLootType: Int = LOOT_UNKNOWN
	private var removedOwnership: Int = GroundItem.OWNERSHIP_NONE

	private var sceneBaseX: Int = -1
	private var sceneBaseY: Int = -1
	private var rebuildCycle: Int = -1

	/** The colours the Color submenu offered for each item, by item id: the k-th entry's colour. */
	private val offeredColours: HashMap<Int, IntArray> = HashMap()

	private var context: PluginContext? = null

	private var resetColourAction: Int = -1
	private var pickColourAction: Int = -1
	private val colourActions: IntArray = IntArray(5)

	private val attribution: LootAttribution = LootAttribution { kill -> lootReceived(kill) }

	private val overlay: Overlay = Overlay { g, view -> GroundItemsOverlay.draw(g, view) }

	private val settingListener: EventListener<SettingChanged> = EventListener { event ->
		if (event.pluginId == DESCRIPTOR.id) {
			context?.config()?.let { readSettings(it) }
		}
	}

	private val spawnListener: EventListener<GroundItem> = EventListener { event -> context?.client()?.let { spawned(it, event) } }

	private val despawnListener: EventListener<GroundItem> = EventListener { event -> context?.client()?.let { despawned(it, event) } }

	private val npcDespawnListener: EventListener<com.jagex.game.runetek5.plugin.api.Npc> = EventListener { npc -> context?.client()?.let { attribution.npcDespawned(it, npc) } }

	private val playerDespawnListener: EventListener<Player> = EventListener { player -> context?.client()?.let { attribution.playerDespawned(it, player) } }

	private val tickListener: EventListener<ClientTick> = EventListener { event -> context?.client()?.let { cycle(it, event.cycle) } }

	private val stateListener: EventListener<GameStateChanged> = EventListener { event ->
		if (event.state !== GameState.LOGGED_IN) {
			clear()
		}
	}

	private val clickListener: EventListener<MenuOptionClicked> = EventListener { event -> optionClicked(event) }

	private val menuAddedListener: EventListener<MenuEntryAdded> = EventListener { event -> context?.client()?.let { menuEntryAdded(it, event) } }

	private val menuBuiltListener: EventListener<MenuBuilt> = EventListener { _ -> context?.client()?.let { GroundItemsMenu.menuBuilt(it) } }

	private val keyPressedListener: EventListener<KeyPressed> = EventListener { event ->
		if (enabled && hotkey != 0 && event.keyCode == hotkey && !hotkeyDown) {
			hotkeyDown = true
			hotkeyPressed()
		}
	}

	private val keyReleasedListener: EventListener<KeyReleased> = EventListener { event ->
		if (hotkey != 0 && event.keyCode == hotkey) {
			hotkeyDown = false
			hotkeyReleased()
		}
	}

	private val mouseListener: EventListener<MouseClicked> = EventListener { event -> mouseClicked(event) }

	private val command: CommandHandler = CommandHandler { _, _ ->
		val context: PluginContext = this.context ?: return@CommandHandler
		enabled = !enabled
		context.config().setBoolean("enabled", enabled)
		context.config().save()
		context.client().consolePrint(if (enabled) "Ground items on." else "Ground items off.")
	}

	override fun start(context: PluginContext) {
		this.context = context
		declareSettings(context.settings())
		readSettings(context.config())
		resetColourAction = context.registerMenuAction(MenuAction { _, _, _, id -> unsetItemColour(id) })
		pickColourAction = context.registerMenuAction(MenuAction { _, _, _, id -> pickItemColour(id) })
		for (k in colourActions.indices) {
			colourActions[k] = context.registerMenuAction(MenuAction { _, _, _, id -> offeredColour(id, k) })
		}
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		context.events().subscribe(Events.ITEM_SPAWNED, spawnListener)
		context.events().subscribe(Events.ITEM_DESPAWNED, despawnListener)
		context.events().subscribe(Events.NPC_DESPAWNED, npcDespawnListener)
		context.events().subscribe(Events.PLAYER_DESPAWNED, playerDespawnListener)
		context.events().subscribe(Events.CLIENT_TICK, tickListener)
		context.events().subscribe(Events.GAME_STATE_CHANGED, stateListener)
		context.events().subscribe(Events.MENU_OPTION_CLICKED, clickListener)
		context.events().subscribe(Events.MENU_ENTRY_ADDED, menuAddedListener)
		context.events().subscribe(Events.MENU_BUILT, menuBuiltListener)
		context.events().subscribe(Events.KEY_PRESSED, keyPressedListener)
		context.events().subscribe(Events.KEY_RELEASED, keyReleasedListener)
		context.events().subscribe(Events.MOUSE_CLICKED, mouseListener)
		context.addOverlay(overlay)
		context.registerCommand("grounditems", command)
		// the items already on the ground when the plugin starts
		val client: ClientApi = context.client()
		if (client.gameState() === GameState.LOGGED_IN) {
			client.groundItems { sceneX, sceneY, level, itemId, amount -> spawned(client, GroundItem(itemId, amount, sceneX, sceneY, level)) }
		}
	}

	override fun stop() {
		context = null
		clear()
	}

	private fun declareSettings(s: SettingsSchema) {
		s.toggle("enabled", "Ground items", true)
		s.text("highlightedItems", "Highlighted items", "", MAX_LIST)
		s.text("hiddenItems", "Hidden items", DEFAULT_HIDDEN, MAX_LIST)
		s.toggle("showHighlightedOnly", "Show highlighted items only", false)
		s.toggle("dontHideUntradeables", "Do not hide untradeables", true)
		s.toggle("showMenuItemQuantities", "Show menu item quantities", true)
		s.toggle("recolorMenuHiddenItems", "Recolour menu hidden items", false)
		s.toggle("deprioritizeHiddenItems", "Deprioritise menu hidden items", false)
		s.toggle("highlightTiles", "Highlight tiles", false)
		s.toggle("notifyHighlightedDrops", "Notify for highlighted drops", false)
		s.choice("notifyTier", "Notify tier", TIERS[0], TIERS)
		s.choice("priceDisplayMode", "Price display mode", PRICE_MODES[PRICE_BOTH], PRICE_MODES)
		s.choice("itemHighlightMode", "Item highlight mode", ITEM_HIGHLIGHT_MODES[HIGHLIGHT_BOTH], ITEM_HIGHLIGHT_MODES)
		s.choice("menuHighlightMode", "Menu highlight mode", MENU_HIGHLIGHT_MODES[MENU_NAME], MENU_HIGHLIGHT_MODES)
		s.choice("highlightValueCalculation", "Highlight value calculation", VALUE_MODES[VALUE_HIGHEST], VALUE_MODES)
		s.number("hideUnderValue", "Hide under value", 0, 0, Int.MAX_VALUE, 1000)
		s.colour("defaultColor", "Default items colour", WHITE, true)
		s.colour("highlightedColor", "Highlighted items colour", 0xFFAA00FF.toInt(), true)
		s.colour("hiddenColor", "Hidden items colour", GRAY, true)
		s.colour("lowValueColor", "Low value items colour", 0xFF66B2FF.toInt(), true)
		s.number("lowValuePrice", "Low value price", 20_000, 0, Int.MAX_VALUE, 1000)
		s.colour("mediumValueColor", "Medium value items colour", 0xFF99FF99.toInt(), true)
		s.number("mediumValuePrice", "Medium value price", 100_000, 0, Int.MAX_VALUE, 1000)
		s.colour("highValueColor", "High value items colour", 0xFFFF9600.toInt(), true)
		s.number("highValuePrice", "High value price", 1_000_000, 0, Int.MAX_VALUE, 1000)
		s.colour("insaneValueColor", "Insane value items colour", 0xFFFF66B2.toInt(), true)
		s.number("insaneValuePrice", "Insane value price", 10_000_000, 0, Int.MAX_VALUE, 1000)
		s.choice("ownershipFilterMode", "Ownership filter", OWNERSHIP_MODES[OWNERSHIP_ALL], OWNERSHIP_MODES)
		s.number("doubleTapDelay", "Double-tap delay (ms)", 250, 0, 5000, 50)
		s.toggle("collapseEntries", "Collapse ground item menu", false)
		s.choice("groundItemTimers", "Despawn timer", TIMER_MODES[TIMER_OFF], TIMER_MODES)
		s.toggle("textOutline", "Text outline", false)
		s.toggle("showLootbeamForHighlighted", "Highlighted item lootbeams", false)
		s.choice("showLootbeamTier", "Lootbeam tier", TIERS[3], TIERS)
		s.choice("lootbeamStyle", "Lootbeam style", BEAM_STYLES[BEAM_MODERN], BEAM_STYLES)
		s.keybind("hotkey", "Hotkey", KeyCode.ALT)
	}

	private fun choice(config: PluginConfig, key: String, options: Array<String>, default: Int): Int {
		val index: Int = options.indexOf(config.get(key, options[default]))
		return if (index < 0) default else index
	}

	/** Reads every setting and judges every item again (RuneLite's onConfigChanged and reset()). */
	@JvmStatic
	fun readSettings(config: PluginConfig) {
		enabled = config.getBoolean("enabled", true)
		highlightedCsv = config.get("highlightedItems", "")
		hiddenCsv = config.get("hiddenItems", DEFAULT_HIDDEN)
		highlightedList = GroundItemRules.parseList(highlightedCsv)
		hiddenList = GroundItemRules.parseList(hiddenCsv)
		showHighlightedOnly = config.getBoolean("showHighlightedOnly", false)
		dontHideUntradeables = config.getBoolean("dontHideUntradeables", true)
		showMenuItemQuantities = config.getBoolean("showMenuItemQuantities", true)
		recolorMenuHiddenItems = config.getBoolean("recolorMenuHiddenItems", false)
		deprioritizeHiddenItems = config.getBoolean("deprioritizeHiddenItems", false)
		highlightTiles = config.getBoolean("highlightTiles", false)
		notifyHighlightedDrops = config.getBoolean("notifyHighlightedDrops", false)
		notifyTier = choice(config, "notifyTier", TIERS, 0)
		priceDisplayMode = choice(config, "priceDisplayMode", PRICE_MODES, PRICE_BOTH)
		itemHighlightMode = choice(config, "itemHighlightMode", ITEM_HIGHLIGHT_MODES, HIGHLIGHT_BOTH)
		menuHighlightMode = choice(config, "menuHighlightMode", MENU_HIGHLIGHT_MODES, MENU_NAME)
		valueCalculationMode = choice(config, "highlightValueCalculation", VALUE_MODES, VALUE_HIGHEST)
		hideUnderValue = config.getInt("hideUnderValue", 0)
		defaultColour = config.getColour("defaultColor", WHITE)
		highlightedColour = config.getColour("highlightedColor", 0xFFAA00FF.toInt())
		hiddenColour = config.getColour("hiddenColor", GRAY)
		lowValueColour = config.getColour("lowValueColor", 0xFF66B2FF.toInt())
		lowValuePrice = config.getInt("lowValuePrice", 20_000)
		mediumValueColour = config.getColour("mediumValueColor", 0xFF99FF99.toInt())
		mediumValuePrice = config.getInt("mediumValuePrice", 100_000)
		highValueColour = config.getColour("highValueColor", 0xFFFF9600.toInt())
		highValuePrice = config.getInt("highValuePrice", 1_000_000)
		insaneValueColour = config.getColour("insaneValueColor", 0xFFFF66B2.toInt())
		insaneValuePrice = config.getInt("insaneValuePrice", 10_000_000)
		ownershipFilterMode = choice(config, "ownershipFilterMode", OWNERSHIP_MODES, OWNERSHIP_ALL)
		doubleTapDelay = config.getInt("doubleTapDelay", 250)
		collapseEntries = config.getBoolean("collapseEntries", false)
		groundItemTimers = choice(config, "groundItemTimers", TIMER_MODES, TIMER_OFF)
		textOutline = config.getBoolean("textOutline", false)
		showLootbeamForHighlighted = config.getBoolean("showLootbeamForHighlighted", false)
		showLootbeamTier = choice(config, "showLootbeamTier", TIERS, 3)
		lootbeamStyle = choice(config, "lootbeamStyle", BEAM_STYLES, BEAM_MODERN)
		hotkey = config.getInt("hotkey", KeyCode.ALT)
		buildTiers()
		for (item in items) {
			item.reset()
		}
	}

	private fun buildTiers() {
		val prices: ArrayList<Int> = ArrayList()
		val colours: ArrayList<Int> = ArrayList()
		val all: IntArray = intArrayOf(insaneValuePrice, highValuePrice, mediumValuePrice, lowValuePrice)
		val allColours: IntArray = intArrayOf(insaneValueColour, highValueColour, mediumValueColour, lowValueColour)
		for (i in all.indices) {
			if (all[i] > 0) {
				prices.add(all[i])
				colours.add(allColours[i])
			}
		}
		tierPrices = prices.toIntArray()
		tierColours = colours.toIntArray()
	}

	/** The price a tier stands for (RuneLite's HighlightTier.getValueFromTier); 0 for Off. */
	@JvmStatic
	fun tierPrice(tier: Int): Int {
		return when (tier) {
			1 -> lowValuePrice
			2 -> mediumValuePrice
			3 -> highValuePrice
			4 -> insaneValuePrice
			else -> 0
		}
	}

	// --- the items on the ground ---

	@JvmStatic
	fun key(worldX: Int, worldY: Int, level: Int, itemId: Int): Long {
		return (LootAttribution.tileKey(worldX, worldY, level) shl 20) or (itemId.toLong() and 0xFFFFF)
	}

	@JvmStatic
	fun itemAt(worldX: Int, worldY: Int, level: Int, itemId: Int): Item? {
		return byKey[key(worldX, worldY, level, itemId)]
	}

	private fun clear() {
		items.clear()
		byKey.clear()
		attribution.clear()
		droppedItems.clear()
		usingItem = -1
		lastUsedItem = -1
		hotKeyPressed = false
		hotkeyDown = false
		hideAll = false
		sceneBaseX = -1
		sceneBaseY = -1
		rebuildCycle = -1
		textBoxItem = null
		hiddenBoxItem = null
		highlightBoxItem = null
	}

	/** The scene moved (a map build): every item waits to be sent again, and the ones that are not are dropped after [REBUILD_CYCLES]. */
	private fun checkScene(client: ClientApi) {
		val baseX: Int = client.sceneBaseX()
		val baseY: Int = client.sceneBaseY()
		if (baseX == sceneBaseX && baseY == sceneBaseY) {
			return
		}
		if (sceneBaseX >= 0) {
			for (item in items) {
				item.confirmed = false
			}
			rebuildCycle = client.clientCycle()
		}
		sceneBaseX = baseX
		sceneBaseY = baseY
	}

	@JvmStatic
	fun spawned(client: ClientApi, event: GroundItem) {
		checkScene(client)
		attribution.itemSpawned(client, event)
		val worldX: Int = client.sceneBaseX() + event.sceneX
		val worldY: Int = client.sceneBaseY() + event.sceneY
		val k: Long = key(worldX, worldY, event.level, event.itemId)
		val existing: Item? = byKey[k]
		if (existing != null && !existing.confirmed) {
			// the server sending it again after the scene moved: the same item, its timer kept
			existing.confirmed = true
			existing.quantity = event.amount
			existing.reset()
			return
		}
		val now: Int = client.clientCycle()
		val built: Item = build(client, event, worldX, worldY, now)
		if (existing != null) {
			existing.quantity += built.quantity // the spawn time stays the oldest
			existing.reset()
		} else {
			if (removedKey == k && removedCycle == now) {
				// the amount changed (gone and back in one cycle): the same item, its timer kept
				built.spawnCycle = removedSpawnCycle
				built.lootType = removedLootType
				built.ownership = removedOwnership
			}
			items.add(built)
			byKey[k] = built
		}
		if (shouldDisplay(built.ownership)) {
			notifyHighlightedItem(client, built)
		}
	}

	private fun build(client: ClientApi, event: GroundItem, worldX: Int, worldY: Int, now: Int): Item {
		val item: Item = Item(event.itemId, worldX, worldY, event.level)
		item.quantity = event.amount
		val type: ItemDefinition? = client.item(event.itemId)
		item.name = type?.name() ?: ""
		item.stackable = type?.isStackable() ?: false
		item.tradeable = type?.isTradeable() ?: false
		if (event.itemId == COINS) {
			item.gePrice = 1
			item.haPrice = 1
		} else {
			item.gePrice = maxOf(0, client.itemPrice(event.itemId))
			item.haPrice = client.itemHaPrice(event.itemId)
		}
		item.height = client.groundItemHeight(event.sceneX, event.sceneY, event.level)
		item.spawnCycle = now
		item.ownership = event.ownership
		val player: Player? = client.localPlayer()
		val onPlayer: Boolean = player != null && client.sceneBaseX() + player.tileX() == worldX && client.sceneBaseY() + player.tileY() == worldY
		if (onPlayer && droppedItems.remove(event.itemId)) {
			item.lootType = LOOT_DROPPED
			item.ownership = GroundItem.OWNERSHIP_SELF
		} else if (event.itemId == lastUsedItem && item.height > 0) {
			item.lootType = LOOT_TABLE
			item.ownership = GroundItem.OWNERSHIP_SELF
		}
		return item
	}

	@JvmStatic
	fun despawned(client: ClientApi, event: GroundItem) {
		val worldX: Int = client.sceneBaseX() + event.sceneX
		val worldY: Int = client.sceneBaseY() + event.sceneY
		val k: Long = key(worldX, worldY, event.level, event.itemId)
		val item: Item = byKey[k] ?: return
		if (item.quantity <= event.amount) {
			items.remove(item)
			byKey.remove(k)
			removedKey = k
			removedCycle = client.clientCycle()
			removedSpawnCycle = item.spawnCycle
			removedLootType = item.lootType
			removedOwnership = item.ownership
		} else {
			item.quantity -= event.amount
			// which of the stacks was taken is not known: the timer is not either
			item.spawnCycle = -1
			item.reset()
		}
	}

	/** A kill's loot (LootAttribution): the items it left count as the player's own, with a loot timer. */
	private fun lootReceived(kill: LootAttribution.Kill) {
		val type: Int = if (kill.type == LootAttribution.PLAYER) LOOT_PVP else LOOT_PVM
		for (stack in kill.items) {
			val item: Item = itemAt(stack.worldX, stack.worldY, stack.level, stack.itemId) ?: continue
			item.lootType = type
			item.ownership = GroundItem.OWNERSHIP_SELF
		}
	}

	private fun cycle(client: ClientApi, now: Int) {
		attribution.cycle(now)
		if (client.gameState() !== GameState.LOGGED_IN) {
			return
		}
		checkScene(client)
		if (rebuildCycle >= 0 && now - rebuildCycle > REBUILD_CYCLES) {
			rebuildCycle = -1
			val gone: ArrayList<Item> = ArrayList()
			for (item in items) {
				if (!item.confirmed) {
					gone.add(item)
				}
			}
			for (item in gone) {
				items.remove(item)
				byKey.remove(key(item.worldX, item.worldY, item.level, item.itemId))
			}
		}
		if (hotKeyPressed && !client.isFocused()) {
			hotKeyPressed = false // RuneLite's onFocusChanged
			hotkeyDown = false
		}
	}

	private fun optionClicked(event: MenuOptionClicked) {
		val option: String = event.option ?: return
		if (option == "Drop" && event.itemId >= 0) {
			droppedItems.add(event.itemId)
			while (droppedItems.size > 16) {
				droppedItems.removeAt(0)
			}
		} else if (option == "Use" && event.itemId >= 0) {
			usingItem = event.itemId
		} else if (event.clickType % 2000 == MiniMenuAction.TGT_LOC && usingItem >= 0) {
			lastUsedItem = usingItem
		}
	}

	// --- judging an item ---

	/** RuneLite's getValueByMode. */
	@JvmStatic
	fun valueByMode(ge: Long, ha: Long): Long {
		return when (valueCalculationMode) {
			VALUE_GE -> ge
			VALUE_HA -> ha
			else -> maxOf(ge, ha)
		}
	}

	const val VERDICT_NONE: Int = 0
	const val VERDICT_HIGHLIGHTED: Int = 1
	const val VERDICT_HIDDEN: Int = 2

	/** RuneLite's isHiddenOrHighlighted: an exact highlight, an exact hide, a wildcard highlight, a wildcard hide. */
	@JvmStatic
	fun hiddenOrHighlighted(item: Item): Int {
		val hl: Int = GroundItemRules.matches(highlightedList, item.name, item.quantity)
		if (hl == GroundItemRules.EXACT) {
			return VERDICT_HIGHLIGHTED
		}
		val hi: Int = GroundItemRules.matches(hiddenList, item.name, item.quantity)
		if (hi == GroundItemRules.EXACT) {
			return VERDICT_HIDDEN
		}
		if (hl == GroundItemRules.WILDCARD) {
			return VERDICT_HIGHLIGHTED
		}
		if (hi == GroundItemRules.WILDCARD) {
			return VERDICT_HIDDEN
		}
		return VERDICT_NONE
	}

	/** RuneLite's getHighlighted: the colour that marks the item, or null when nothing does. */
	@JvmStatic
	fun highlightColour(item: Item): Int? {
		val own: Int? = itemColour(item.itemId)
		val verdict: Int = hiddenOrHighlighted(item)
		if (verdict == VERDICT_HIGHLIGHTED) {
			return own ?: highlightedColour
		}
		if (verdict == VERDICT_HIDDEN) {
			return null
		}
		if (own != null) {
			return own
		}
		val price: Long = valueByMode(item.geTotal(), item.haTotal())
		for (i in tierPrices.indices) {
			if (price > tierPrices[i]) {
				return tierColours[i]
			}
		}
		return null
	}

	/** RuneLite's isHidden. */
	@JvmStatic
	fun isHidden(item: Item): Boolean {
		val verdict: Int = hiddenOrHighlighted(item)
		val canBeHidden: Boolean = item.geTotal() > 0 || item.tradeable || !dontHideUntradeables
		val under: Boolean = item.geTotal() < hideUnderValue && item.haTotal() < hideUnderValue
		return verdict == VERDICT_HIDDEN || (verdict != VERDICT_HIGHLIGHTED && canBeHidden && under)
	}

	/** RuneLite's updateItemColor: the verdict and colour, kept until the item or the settings change. */
	@JvmStatic
	fun judge(item: Item) {
		if (item.judged) {
			return
		}
		val highlight: Int? = highlightColour(item)
		val hidden: Boolean = isHidden(item)
		item.highlighted = highlight != null
		item.hidden = hidden
		item.colour = highlight ?: if (hidden) hiddenColour else defaultColour
		item.judged = true
	}

	/** RuneLite's shouldDisplayItem; 634 has no ironmen, so Takeable leaves out nothing. */
	@JvmStatic
	fun shouldDisplay(ownership: Int): Boolean {
		return when (ownershipFilterMode) {
			OWNERSHIP_DROPS -> ownership == GroundItem.OWNERSHIP_SELF || ownership == GroundItem.OWNERSHIP_GROUP
			else -> true
		}
	}

	private fun notifyHighlightedItem(client: ClientApi, item: Item) {
		val verdict: Int = hiddenOrHighlighted(item)
		val highlighted: Boolean = notifyHighlightedDrops && verdict == VERDICT_HIGHLIGHTED
		val valuable: Boolean = notifyTier != 0 && valueByMode(item.geTotal(), item.haTotal()) > tierPrice(notifyTier) && verdict != VERDICT_HIDDEN
		val dropType: String = if (highlighted) "highlighted" else if (valuable) "valuable" else return
		val text: StringBuilder = StringBuilder("You received a ").append(dropType).append(" drop: ").append(item.name)
		if (item.quantity > 1) {
			text.append(" (").append(GroundItemRules.quantityToStackSize(item.quantity.toLong())).append(')')
		}
		if (!client.notify(text.toString())) {
			client.chatMessage(text.toString())
		}
	}

	// --- the lists and the colours ---

	@JvmStatic
	fun itemColour(itemId: Int): Int? {
		val config: PluginConfig = context?.config() ?: return null
		val text: String = config.get(ITEM_COLOUR_PREFIX + itemId, "")
		return if (text.isEmpty()) null else config.getColour(ITEM_COLOUR_PREFIX + itemId, WHITE)
	}

	private fun setItemColour(itemId: Int, argb: Int) {
		val config: PluginConfig = context?.config() ?: return
		config.setColour(ITEM_COLOUR_PREFIX + itemId, argb)
		config.save()
		readSettings(config)
	}

	private fun unsetItemColour(itemId: Int) {
		val config: PluginConfig = context?.config() ?: return
		config.remove(ITEM_COLOUR_PREFIX + itemId)
		config.save()
		readSettings(config)
	}

	private fun pickItemColour(itemId: Int) {
		val context: PluginContext = this.context ?: return
		context.pickColour(itemColour(itemId) ?: WHITE, true) { argb -> setItemColour(itemId, argb) }
	}

	private fun offeredColour(itemId: Int, k: Int) {
		val colours: IntArray = offeredColours[itemId] ?: return
		if (k < colours.size) {
			setItemColour(itemId, colours[k])
		}
	}

	/** RuneLite's updateList: the item comes off the other list and is toggled on this one; both are saved. */
	@JvmStatic
	fun updateList(name: String, hiddenList: Boolean) {
		val hidden: ArrayList<String> = GroundItemRules.splitCsv(hiddenCsv)
		val highlighted: ArrayList<String> = GroundItemRules.splitCsv(highlightedCsv)
		if (hiddenList) {
			highlighted.removeAll { it.equals(name, ignoreCase = true) }
		} else {
			hidden.removeAll { it.equals(name, ignoreCase = true) }
		}
		val target: ArrayList<String> = if (hiddenList) hidden else highlighted
		if (!target.removeAll { it.equals(name, ignoreCase = true) }) {
			target.add(name)
		}
		hiddenCsv = GroundItemRules.joinCsv(hidden)
		highlightedCsv = GroundItemRules.joinCsv(highlighted)
		val config: PluginConfig? = context?.config()
		if (config != null) {
			config.set("hiddenItems", hiddenCsv)
			config.set("highlightedItems", highlightedCsv)
			config.save()
			readSettings(config)
		} else {
			this.hiddenList = GroundItemRules.parseList(hiddenCsv)
			highlightedList = GroundItemRules.parseList(highlightedCsv)
			for (item in items) {
				item.reset()
			}
		}
	}

	/** Shift and right-click on an item: the Color submenu (RuneLite's, for the Examine entry). */
	private fun menuEntryAdded(client: ClientApi, event: MenuEntryAdded) {
		if (!enabled || event.target !== MenuTarget.GROUND_ITEM || !client.isKeyHeld(KeyCode.SHIFT)) {
			return
		}
		val itemId: Int = event.id
		val name: String = client.itemName(itemId) ?: return
		val submenu: Int = client.addSubmenu("Color <col=ff9040>" + name)
		if (submenu < 0) {
			return
		}
		val own: Int? = itemColour(itemId)
		if (own != null) {
			client.addSubmenuEntry(submenu, "Reset", resetColourAction)
		}
		client.addSubmenuEntry(submenu, "Pick", pickColourAction)
		val colours: ArrayList<Int> = ArrayList()
		for (item in items) {
			judge(item)
			if (!colours.contains(item.colour) && colours.size < 5) {
				colours.add(item.colour)
			}
		}
		for (c in intArrayOf(RED, GREEN, BLUE, YELLOW, MAGENTA)) {
			if (!colours.contains(c) && colours.size < 5) {
				colours.add(c)
			}
		}
		val offered: ArrayList<Int> = ArrayList()
		for (c in colours) {
			if (c != own) {
				offered.add(c)
			}
		}
		offeredColours[itemId] = offered.toIntArray()
		for (k in offered.indices) {
			client.addSubmenuEntry(submenu, colourTag(offered[k]) + "Color", colourActions[k])
		}
	}

	@JvmStatic
	fun colourTag(argb: Int): String {
		return "<col=" + (argb and 0xFFFFFF).toString(16).padStart(6, '0') + ">"
	}

	// --- the hotkey and the boxes ---

	/** RuneLite's GroundItemHotkeyListener.hotkeyPressed: a double tap hides everything, the next press shows it again. */
	private fun hotkeyPressed() {
		val now: Long = lang.currentTimeMillis()
		if (hideAll) {
			hideAll = false
			hotKeyPressed = true
			lastPress = -1L
		} else if (lastPress >= 0 && !hotKeyPressed && doubleTapDelay > 0 && now - lastPress < doubleTapDelay) {
			hideAll = true
			lastPress = -1L
		} else {
			hotKeyPressed = true
			lastPress = now
		}
	}

	private fun hotkeyReleased() {
		hotKeyPressed = false
		textBoxItem = null
		hiddenBoxItem = null
		highlightBoxItem = null
	}

	private fun inside(box: IntArray, x: Int, y: Int): Boolean {
		return x >= box[0] && y >= box[1] && x <= box[0] + box[2] && y <= box[1] + box[3]
	}

	/** RuneLite's GroundItemMouseAdapter: while the hotkey is held, a click on a box or a name edits the lists, and the game never sees it. */
	private fun mouseClicked(event: MouseClicked) {
		if (!enabled || !hotKeyPressed) {
			return
		}
		val x: Int = event.x
		val y: Int = event.y
		if (event.button == 0) {
			val hidden: Item? = hiddenBoxItem
			val highlight: Item? = highlightBoxItem
			val text: Item? = textBoxItem
			if (hidden != null && inside(hiddenBox, x, y)) {
				updateList(hidden.name, true)
				event.consume()
			} else if (highlight != null && inside(highlightBox, x, y)) {
				updateList(highlight.name, false)
				event.consume()
			} else if (text != null && inside(textBox, x, y)) {
				updateList(text.name, false)
				event.consume()
			}
		} else if (event.button == 2) {
			val text: Item? = textBoxItem
			if (text != null && inside(textBox, x, y)) {
				updateList(text.name, true)
				event.consume()
			}
		}
	}

	// --- for the tests and the smoke run ---

	@JvmStatic
	fun context(): PluginContext? = context

	/** The despawn cycle of [item] as RuneLite counted it before the server told it (312776c5f), or -1 when it cannot be known. */
	@JvmStatic
	fun despawnCycle(item: Item, inInstance: Boolean): Int {
		if (item.lootType != LOOT_PVM && item.lootType != LOOT_DROPPED && item.lootType != LOOT_TABLE) {
			return -1
		}
		if (item.spawnCycle < 0) {
			return -1
		}
		val lifetime: Int = if (inInstance) {
			DESPAWN_INSTANCE
		} else {
			when (item.lootType) {
				LOOT_DROPPED -> DESPAWN_DROP
				LOOT_TABLE -> DESPAWN_TABLE
				else -> DESPAWN_LOOT
			}
		}
		return item.spawnCycle + lifetime
	}

	/** Green while only the player sees it (the first minute, or always in an instance), yellow once it is public; null with no timer. */
	@JvmStatic
	fun timerColour(item: Item, now: Int, inInstance: Boolean): Int? {
		val despawn: Int = despawnCycle(item, inInstance)
		if (despawn < 0 || now < item.spawnCycle || now > despawn) {
			return null
		}
		return if (inInstance || now < item.spawnCycle + VISIBLE_AFTER) GREEN else YELLOW
	}

	/** The menu entries of a ground item the plugin treats (RuneLite's GROUND_ITEM_*_OPTION and WIDGET_TARGET_ON_GROUND_ITEM). */
	@JvmStatic
	fun isGroundItemOption(clickType: Int): Boolean {
		val type: Int = clickType % 2000
		return type == MiniMenuAction.OPOBJ1 || type == MiniMenuAction.OPOBJ2 || type == MiniMenuAction.OPOBJ3 || type == MiniMenuAction.OPOBJ4 || type == MiniMenuAction.OPOBJ5 || type == MiniMenuAction.TGT_OBJ
	}

	@JvmStatic
	fun isGroundItemExamine(clickType: Int): Boolean {
		return clickType % 2000 == MiniMenuAction.OPOBJ6
	}

	/** The item a ground item entry is about, found by its tile (param0, param1 in the scene) and id. */
	@JvmStatic
	fun itemOf(client: ClientApi, entry: MenuEntry): Item? {
		val level: Int = client.localPlayer()?.level() ?: return null
		return itemAt(client.sceneBaseX() + entry.param0(), client.sceneBaseY() + entry.param1(), level, entry.entityUid().toInt())
	}
}
