package com.jagex.game.runetek5.client.features

import ClientCommandProcessor
import com.jagex.game.runetek5.plugin.api.ChatType
import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.Font
import com.jagex.game.runetek5.plugin.api.Graphics
import com.jagex.game.runetek5.plugin.api.MenuAction
import com.jagex.game.runetek5.plugin.api.MenuTarget
import com.jagex.game.runetek5.plugin.api.OverlayMenuEntry
import com.jagex.game.runetek5.plugin.api.PanelOverlay
import com.jagex.game.runetek5.plugin.api.Player
import com.jagex.game.runetek5.plugin.api.Plugin
import com.jagex.game.runetek5.plugin.api.PluginApi
import com.jagex.game.runetek5.plugin.api.PluginConfig
import com.jagex.game.runetek5.plugin.api.PluginContext
import com.jagex.game.runetek5.plugin.api.PluginDescriptor
import com.jagex.game.runetek5.plugin.api.SettingsSchema
import com.jagex.game.runetek5.plugin.api.Size
import com.jagex.game.runetek5.plugin.api.Task
import com.jagex.game.runetek5.plugin.api.Viewport
import com.jagex.game.runetek5.plugin.api.events.ChatMessage
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.FrameDraw
import com.jagex.game.runetek5.plugin.api.events.GameStateChanged
import com.jagex.game.runetek5.plugin.api.events.GameTick
import com.jagex.game.runetek5.plugin.api.events.KeyPressed
import com.jagex.game.runetek5.plugin.api.events.MenuEntryAdded
import com.jagex.game.runetek5.plugin.api.events.MenuOptionClicked
import com.jagex.game.runetek5.plugin.api.events.ServerTick
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import com.jagex.game.runetek5.plugin.api.events.ViewportDraw
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: a panel for the bots a player owns on a Void server with player-owned bots
 * (Void's content/bot/owned/OwnedBots). The server shows the owner each bot's name, health, prayer, status,
 * order, tile, run energy, worn and carried items in client variables (owned.varcs.toml, ids [BASE] to [BASE] +
 * [SLOTS] * [FIELDS]) once the plugin has announced itself with `::bot ui` and a character no chat box can type;
 * the plugin reads them and sends only `::bot` commands, the same ones a player types. A vanilla client is never
 * sent the variables.
 *
 * Off by default, as Party was: on a server without the feature `::bot` is an unknown command.
 */
object OwnedBots : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("owned-bots", "Owned Bots", "1", PluginApi.VERSION, "Shows the bots you own, their health and orders, and lets you command them, give them items and see where they are.", "Pixel")

	/** The first of the server's variables (owned_bots_meta). */
	const val BASE: Int = 2000
	const val SLOTS: Int = 4
	const val FIELDS: Int = 50
	const val EQUIPMENT: Int = 14
	const val INVENTORY: Int = 28
	const val NO_TASK: Int = 255

	/** Field offsets inside a slot's block. */
	const val NAME_HI: Int = 0
	const val NAME_LO: Int = 1
	const val HP: Int = 2
	const val PRAYER: Int = 3
	const val STATUS: Int = 4
	const val TASK: Int = 5
	const val TILE: Int = 6
	const val ENERGY: Int = 7
	const val EQUIP: Int = 8
	const val INV: Int = 22

	/** The server's OwnedBotStatus, by ordinal. */
	@JvmField
	val STATUS_NAMES: Array<String> = arrayOf("Away", "Idle", "Working", "Banking", "Following", "Fighting", "Low health", "Fled", "Stopped", "Dead", "Holding")

	@JvmField
	val PROFILES: Array<String> = arrayOf("passive", "melee", "ranged", "mage")

	@JvmField
	val EQUIPMENT_NAMES: Array<String> = arrayOf("hat", "cape", "amulet", "weapon", "chest", "shield", "", "legs", "", "hands", "feet", "", "ring", "ammo")

	/** The line the server prints for `::bot tasks`, and the prefix of every line about a bot. */
	const val TASKS_PREFIX: String = "[BotTasks] "
	const val LOADOUTS_PREFIX: String = "[BotLoadouts] "

	/** Submenus the presets sit under in the roster's and the loadout's menus. */
	const val LOAD_PRESET: String = "Load preset"
	const val DELETE_PRESET: String = "Delete preset"

	/** The amounts a stack is given or taken in, the bank's choices. */
	@JvmField
	val AMOUNTS: Array<String> = arrayOf("1", "5", "10", "All")
	const val ALL: String = "All"
	const val BOT_PREFIX: String = "[Bot "

	/** What the plugin sends in `::bot ui`: a character the chat box can't type, so only a plugin opts in. */
	const val HANDSHAKE: Char = '\u0001'

	/** The idle notifier's ding, for a bot in trouble. */
	const val ALERT_SOUND: Int = 2266
	const val FLASH_CYCLES: Int = 100

	/** The 634 inventory and the bank's item components, for Give entries. */
	const val INVENTORY_INTERFACE: Int = 149
	const val BANK_INTERFACE: Int = 762
	const val BANK_ITEMS: Int = 93

	@JvmField var enabled: Boolean = false
	@JvmField var sounds: Boolean = true
	@JvmField var showOnMap: Boolean = true
	@JvmField var showTags: Boolean = true
	@JvmField var hideTaskLines: Boolean = true

	/** One bot as the server shows it. A dead or unbought slot has [alive] false; [online] false means away. */
	class Bot(
		@JvmField val slot: Int,
		@JvmField val name: String,
		@JvmField val alive: Boolean,
		@JvmField val online: Boolean,
		@JvmField val hp: Int,
		@JvmField val hpMax: Int,
		@JvmField val prayer: Int,
		@JvmField val prayerMax: Int,
		@JvmField val status: Int,
		@JvmField val task: Int,
		@JvmField val profile: Int,
		@JvmField val aggressive: Boolean,
		@JvmField val x: Int,
		@JvmField val y: Int,
		@JvmField val level: Int,
		@JvmField val run: Int,
		@JvmField val equipment: IntArray,
		@JvmField val inventory: IntArray,
	) {
		fun statusName(): String = STATUS_NAMES.getOrNull(if (online) status else 0) ?: "?"
	}

	/** The roster as the variables say; [version] 0 means the server has not answered the handshake. */
	class State(@JvmField val bots: List<Bot>, @JvmField val version: Int) {
		fun active(): Boolean = version > 0
		fun bot(slot: Int): Bot? = bots.getOrNull(slot)
	}

	class TaskEntry(@JvmField val index: Int, @JvmField val id: String, @JvmField val label: String)

	@JvmField
	var state: State = State(emptyList(), 0)

	/** The server's task catalogue, from `::bot tasks` at login. */
	@JvmField
	val tasks: ArrayList<TaskEntry> = ArrayList()

	/** The owner's loadout presets, from `::bot loadout list`: name to the items worn. */
	@JvmField
	val presets: ArrayList<Pair<String, String>> = ArrayList()

	/** The slot whose loadout and task picker show. */
	@JvmField var selected: Int = 0
	@JvmField var tasksOpen: Boolean = false

	private var context: PluginContext? = null

	internal val client: ClientApi?
		get() = context?.client()
	private var cycle: Int = 0
	private var handshakeDue: Int = -1
	private val flash: IntArray = IntArray(SLOTS)
	private val giveActions: IntArray = IntArray(SLOTS) { -1 }
	private val giveBankActions: IntArray = IntArray(SLOTS) { -1 }
	private var giveAmount: String = ""
	private var sendAction: Int = -1
	private var attackAction: Int = -1
	private var pickupAction: Int = -1

	/** The bot a world-order entry named ("Send Woody here", a submenu's "Woody"), or "all"; "" when none, read as the click arrives. */
	private var orderTarget: String = ""
	@JvmField var callKey: Int = 0
	@JvmField var followKey: Int = 0
	@JvmField var stopKey: Int = 0
	private val point: IntArray = IntArray(2)
	private val area: IntArray = IntArray(4)
	private val size: Size = Size()
	private val rowTops: ArrayList<Int> = ArrayList()
	private var rowHeight: Int = 0
	private var statLine: Int = 14

	/** For the tests, which cannot move the mouse: the row taken as hovered, -1 for the mouse's. */
	@JvmField
	var hoveredRowForTest: Int = -1

	// ---- reading the server --------------------------------------------------------------------------------

	/**
	 * The roster from the server's variables, [raw] reading one. A variable the server never set reads -1 in this
	 * client, and the server never sends a zero (a value equal to the default is cleared, not transmitted), so -1
	 * is read as 0: an empty item slot, a short name's high half, a bot with no stats yet.
	 */
	@JvmStatic
	fun read(raw: (Int) -> Int): State {
		val varc: (Int) -> Int = { id -> raw(id).let { if (it == -1) 0 else it } }
		val meta: Int = varc(BASE)
		val version: Int = meta ushr 12
		if (version == 0) {
			return State(emptyList(), 0)
		}
		val aliveMask: Int = meta ushr 4 and 0xF
		val onlineMask: Int = meta and 0xF
		val bots: ArrayList<Bot> = ArrayList(SLOTS)
		for (slot in 0 until SLOTS) {
			val base: Int = BASE + 1 + slot * FIELDS
			val alive: Boolean = aliveMask and (1 shl slot) != 0
			val online: Boolean = onlineMask and (1 shl slot) != 0
			val name: String = name(varc(base + NAME_HI), varc(base + NAME_LO)) ?: ""
			val hp: Int = varc(base + HP)
			val prayer: Int = varc(base + PRAYER)
			val order: Int = varc(base + TASK)
			val tile: Int = raw(base + TILE)
			val hidden: Boolean = tile == -1 || tile == 0 || !online
			val equipment = IntArray(EQUIPMENT) { varc(base + EQUIP + it) }
			val inventory = IntArray(INVENTORY) { varc(base + INV + it) }
			bots.add(
				Bot(
					slot, name, alive, online, hp ushr 16, hp and 0xFFFF, prayer ushr 16, prayer and 0xFFFF, varc(base + STATUS),
					order and 0xFF, order ushr 8 and 0xF, order ushr 12 and 0xF == 1,
					if (hidden) -1 else tile ushr 14 and 0x3FFF, if (hidden) -1 else tile and 0x3FFF, if (hidden) 0 else tile ushr 28 and 0x3,
					varc(base + ENERGY), equipment, inventory,
				),
			)
		}
		return State(bots, version)
	}

	/** An item id and amount packed by the server: id << 16 | amount. */
	@JvmStatic
	fun itemId(packed: Int): Int = if (packed == 0) -1 else packed ushr 16

	@JvmStatic
	fun itemAmount(packed: Int): Int = packed and 0xFFFF

	/** A `[BotTasks] index|id|label` line, or null for the heading and anything else. */
	@JvmStatic
	fun parseTask(text: String): TaskEntry? {
		if (!text.startsWith(TASKS_PREFIX)) {
			return null
		}
		val parts: List<String> = text.substring(TASKS_PREFIX.length).split('|')
		if (parts.size != 3) {
			return null
		}
		val index: Int = parts[0].toIntOrNull() ?: return null
		return TaskEntry(index, parts[1], parts[2])
	}

	/** A `[BotLoadouts] name|item,item` line as name to items, or null for the heading and anything else. */
	@JvmStatic
	fun parsePreset(text: String): Pair<String, String>? {
		if (!text.startsWith(LOADOUTS_PREFIX)) {
			return null
		}
		val body: String = text.substring(LOADOUTS_PREFIX.length)
		val bar: Int = body.indexOf('|')
		if (bar <= 0) {
			return null
		}
		return body.substring(0, bar) to body.substring(bar + 1)
	}

	/** The slot (0-3) a `[Bot n] ...` line is about, or -1. */
	@JvmStatic
	fun alertSlot(text: String): Int {
		if (!text.startsWith(BOT_PREFIX)) {
			return -1
		}
		val end: Int = text.indexOf(']', BOT_PREFIX.length)
		if (end == -1) {
			return -1
		}
		val slot: Int = (text.substring(BOT_PREFIX.length, end).toIntOrNull() ?: return -1) - 1
		return if (slot in 0 until SLOTS) slot else -1
	}

	/** A line worth a sound: the bot is dying, dead, fled or had to stop. */
	@JvmStatic
	fun isUrgent(text: String): Boolean = text.contains("nearly dead") || text.contains("has died") || text.contains(" stopped:") || text.contains("health")

	/** A base-37 name from its two halves; null for 0. */
	@JvmStatic
	fun name(hi: Int, lo: Int): String? {
		var value: Long = (hi.toLong() shl 32) or (lo.toLong() and 0xFFFFFFFFL)
		if (value <= 0L) {
			return null
		}
		val chars = CharArray(12)
		var length = 0
		while (value != 0L && length < chars.size) {
			chars[length++] = BASE37[(value % 37).toInt()]
			value /= 37
		}
		val builder = StringBuilder()
		var start = true
		for (i in length - 1 downTo 0) {
			val c: Char = chars[i]
			if (c == '_') {
				builder.append(' ')
				start = true
			} else {
				builder.append(if (start) c.uppercaseChar() else c)
				start = false
			}
		}
		return builder.toString().trim().ifEmpty { null }
	}

	/** An item name as a `::bot` command wants it: Void's string id, near enough ("Raw shark" -> raw_shark). */
	@JvmStatic
	fun commandName(name: String): String = name.lowercase().replace(' ', '_').filter { it in 'a'..'z' || it in '0'..'9' || it == '_' }

	// ---- listeners ---------------------------------------------------------------------------------------

	private val settingListener: EventListener<SettingChanged> = EventListener { event ->
		if (event.pluginId == DESCRIPTOR.id) {
			context?.config()?.let { readSettings(it) }
		}
	}

	/** `::ownedbots [on|off]` turns the plugin on or off (or over), as the other built-in plugins' commands do. */
	private val toggle: CommandHandler = CommandHandler { _, args ->
		val config: PluginConfig = context?.config() ?: return@CommandHandler
		val on: Boolean = when (args.firstOrNull()?.lowercase()) {
			"on" -> true
			"off" -> false
			else -> !enabled
		}
		config.setBoolean("enabled", on)
		config.save()
		readSettings(config)
		context?.client()?.consolePrint(if (enabled) "Owned bots enabled." else "Owned bots disabled.")
	}

	private val stateListener: EventListener<GameStateChanged> = EventListener { event ->
		if (event.state === GameState.LOGGED_IN) {
			// a couple of ticks in, so the login has settled and the chat box is there for the task list
			handshakeDue = cycle + 60
		} else {
			state = State(emptyList(), 0)
			tasks.clear()
			presets.clear()
		}
	}

	private val tickListener: EventListener<GameTick> = EventListener { _ ->
		val client: ClientApi = context?.client() ?: return@EventListener
		cycle++
		if (!enabled || client.gameState() !== GameState.LOGGED_IN) {
			state = State(emptyList(), 0)
			return@EventListener
		}
		if (handshakeDue in 0..cycle) {
			handshakeDue = -1
			handshake()
		}
	}

	/** The variables change on server ticks; reading them every client cycle would only allocate. */
	private val serverTickListener: EventListener<ServerTick> = EventListener { _ ->
		val client: ClientApi = context?.client() ?: return@EventListener
		if (enabled && client.gameState() === GameState.LOGGED_IN) {
			state = read { client.varcInt(it) }
			OwnedBotsLoadout.tick(state.bot(selected))
		}
	}

	private val chatListener: EventListener<ChatMessage> = EventListener { event ->
		// only the server's own game lines: a player typing "[Bot 1] ..." in public chat is just chat
		if (!enabled || (event.type != ChatType.GAME && event.type != ChatType.GAME_FILTERED)) {
			return@EventListener
		}
		val text: String = ClientFeatures.plainText(event.message ?: return@EventListener)
		val task: TaskEntry? = parseTask(text)
		if (task != null || text.startsWith(TASKS_PREFIX)) {
			if (task != null) {
				tasks.removeAll { it.index == task.index }
				tasks.add(task)
				tasks.sortBy { it.index }
			}
			if (hideTaskLines) {
				event.consume()
			}
			return@EventListener
		}
		if (text.startsWith(LOADOUTS_PREFIX)) {
			// the heading starts a fresh list
			val preset: Pair<String, String>? = parsePreset(text)
			if (preset == null) {
				presets.clear()
			} else {
				presets.removeAll { it.first == preset.first }
				presets.add(preset)
			}
			if (hideTaskLines) {
				event.consume()
			}
			return@EventListener
		}
		val slot: Int = alertSlot(text)
		if (slot >= 0) {
			flash[slot] = cycle
			if (sounds && isUrgent(text)) {
				context?.client()?.playSound(ALERT_SOUND, 255)
			}
		}
	}

	private val viewportListener: EventListener<ViewportDraw> = EventListener { event ->
		val client: ClientApi = context?.client() ?: return@EventListener
		if (enabled && showTags && state.active()) {
			drawTags(event.graphics, client)
		}
	}

	private val frameListener: EventListener<FrameDraw> = EventListener { event ->
		val client: ClientApi = context?.client() ?: return@EventListener
		if (enabled && showOnMap && state.active()) {
			drawMinimap(event.graphics, client)
		}
	}

	/** Give-to-bot entries on the player's own inventory and, with the bank open, on the bank's items: one entry for one bot, a submenu for more. */
	private val menuListener: EventListener<MenuEntryAdded> = EventListener { event ->
		val client: ClientApi = context?.client() ?: return@EventListener
		if (!enabled || !state.active()) {
			return@EventListener
		}
		if (event.target === MenuTarget.TILE || event.target === MenuTarget.NPC || event.target === MenuTarget.GROUND_ITEM) {
			worldEntries(client, event)
			return@EventListener
		}
		if (event.target !== MenuTarget.WIDGET || event.itemId < 0) {
			return@EventListener
		}
		val interfaceId: Int = event.widgetId ushr 16
		val component: Int = event.widgetId and 0xFFFF
		val bank: Boolean = interfaceId == BANK_INTERFACE && component == BANK_ITEMS
		if (!bank && interfaceId != INVENTORY_INTERFACE) {
			return@EventListener
		}
		val online: List<Bot> = state.bots.filter { it.online }
		if (online.isEmpty()) {
			return@EventListener
		}
		// a stack offers how many, as the bank does, instead of the server asking
		val slotWidget = client.widgetById(event.widgetId)?.child(event.slot)
		val quantity: Int = if (slotWidget != null && slotWidget.itemId() == event.itemId) slotWidget.itemQuantity() else 1
		val stack: Boolean = quantity > 1
		// one bot and one item: a plain entry; otherwise a submenu, a line a bot (and an amount)
		if (online.size == 1 && !stack) {
			val bot: Bot = online[0]
			val action: Int = if (bank) giveBankActions[bot.slot] else giveActions[bot.slot]
			if (action >= 0) {
				client.addMenuEntry("Give to " + bot.name, action)
			}
			return@EventListener
		}
		val submenu: Int = client.addSubmenu(if (online.size == 1) "Give to " + online[0].name else "Give to bot")
		if (submenu < 0) {
			return@EventListener
		}
		for (bot in online) {
			val action: Int = if (bank) giveBankActions[bot.slot] else giveActions[bot.slot]
			if (action < 0) {
				continue
			}
			if (!stack) {
				client.addSubmenuEntry(submenu, bot.name, action)
				continue
			}
			for (amount in AMOUNTS) {
				val label: String = if (amount == ALL) "$ALL ($quantity)" else amount
				client.addSubmenuEntry(submenu, if (online.size == 1) label else bot.name + ": " + label, action)
			}
		}
	}

	/**
	 * Orders on the world: "Send <bot> here" on a tile, "Attack with <bot>" on an npc that can be attacked,
	 * "Pick up with <bot>" on a ground item; one entry for one bot, a submenu (each bot, then All) for more.
	 */
	private fun worldEntries(client: ClientApi, event: MenuEntryAdded) {
		val online: List<Bot> = state.bots.filter { it.online }
		if (online.isEmpty()) {
			return
		}
		val action: Int
		val verb: String
		val one: Boolean = online.size == 1
		when (event.target) {
			MenuTarget.TILE -> {
				action = sendAction
				verb = "Send "
			}
			MenuTarget.NPC -> {
				val npc = client.npc(event.id) ?: return
				val type = npc.type() ?: return
				if ((0 until 5).none { type.action(it).equals("Attack", ignoreCase = true) }) {
					return
				}
				action = attackAction
				verb = "Attack with "
			}
			else -> {
				action = pickupAction
				verb = "Pick up with "
			}
		}
		if (action < 0) {
			return
		}
		val here: String = if (event.target === MenuTarget.TILE) " here" else ""
		if (one) {
			client.addMenuEntry(verb + online[0].name + here, action)
			return
		}
		val submenu: Int = client.addSubmenu(verb + "bot" + here)
		if (submenu < 0) {
			return
		}
		for (bot in online) {
			client.addSubmenuEntry(submenu, bot.name, action)
		}
		if (event.target !== MenuTarget.GROUND_ITEM) {
			client.addSubmenuEntry(submenu, ALL, action)
		}
	}

	/** The amount on a give entry's label is read just before the entry's action runs with only the item id. */
	private val optionListener: EventListener<MenuOptionClicked> = EventListener { event ->
		if (event.clickType in giveActions || event.clickType in giveBankActions) {
			giveAmount = amountOf(event.option ?: "")
		} else if (event.clickType == sendAction || event.clickType == attackAction || event.clickType == pickupAction) {
			orderTarget = botOf(event.option ?: "")
		}
	}

	/** The slot (as the command wants it, from 1) or "all" a world-order entry names: "Send Woody here", "Attack with bot" child "Woody", "All". */
	@JvmStatic
	fun botOf(option: String): String {
		val text: String = ClientFeatures.plainText(option)
		if (text.equals(ALL, ignoreCase = true) || text.contains(" all ", ignoreCase = true) || text.endsWith(" all", ignoreCase = true)) {
			return "all"
		}
		for (bot in state.bots) {
			if (bot.name.isNotEmpty() && text.contains(bot.name, ignoreCase = true)) {
				return (bot.slot + 1).toString()
			}
		}
		return ""
	}

	private fun worldOrder(kind: String, args: String) {
		val target: String = orderTarget
		orderTarget = ""
		if (target.isNotEmpty()) {
			command("bot $kind $target $args")
		}
	}

	/** Hotkeys for the orders that matter mid-fight; a matched key never reaches the game. */
	private val keyListener: EventListener<KeyPressed> = EventListener { event ->
		if (!enabled || !state.active() || event.keyCode == 0) {
			return@EventListener
		}
		val order: String = when (event.keyCode) {
			callKey -> "call all"
			followKey -> "follow all"
			stopKey -> "stop all"
			else -> return@EventListener
		}
		command("bot $order")
		event.consume()
	}

	/** The amount a give entry's label carries, "5", "all" (from "All (12)" or "Woody: All (12)"), or "" for one. */
	@JvmStatic
	fun amountOf(option: String): String {
		val text: String = ClientFeatures.plainText(option).substringAfterLast(": ").trim()
		if (text.startsWith(ALL, ignoreCase = true)) {
			return "all"
		}
		return text.toIntOrNull()?.toString() ?: ""
	}

	// ---- panels ------------------------------------------------------------------------------------------

	/** The roster: one row a slot, with the bot's name, what it is doing and its hitpoints and prayer. */
	@JvmField
	val panel: PanelOverlay = object : PanelOverlay("OwnedBotsPanel") {
		init {
			resizable = true
			menuOnRightClick = true
		}

		override fun menuEntries(): List<OverlayMenuEntry> {
			val entries: ArrayList<OverlayMenuEntry> = ArrayList()
			val bot: Bot? = hovered(this)
			if (bot != null && !bot.alive && bot.name.isNotEmpty()) {
				// a dead bot's row: only where to replace it
				entries.add(OverlayMenuEntry("Replace at a Bot Broker", "<col=ff9040>" + bot.name, Task { context?.client()?.consolePrint("Bot Brokers stand in Lumbridge, Varrock and Falador; a dead bot's slot is bought again there.") }))
				return entries
			}
			if (bot != null && bot.alive) {
				val slot: Int = bot.slot + 1
				val target: String = "<col=" + (colour(bot.slot) and 0xFFFFFF).toString(16) + ">" + bot.name
				if (!bot.online) {
					entries.add(OverlayMenuEntry("Summon", target, Task { command("bot summon $slot") }))
				} else {
					entries.add(OverlayMenuEntry("Follow", target, Task { command("bot follow $slot") }))
					entries.add(OverlayMenuEntry("Stop", target, Task { command("bot stop $slot") }))
					entries.add(OverlayMenuEntry("Hold", target, Task { command("bot hold $slot") }))
					entries.add(OverlayMenuEntry("Call", target, Task { command("bot call $slot") }))
					entries.add(OverlayMenuEntry("Task...", target, Task { selected = bot.slot; tasksOpen = true; OwnedBotsLoadout.useFrom = -1 }))
					entries.add(OverlayMenuEntry("Loadout", target, Task { OwnedBotsLoadout.show(bot.slot) }))
					for (preset in presets) {
						entries.add(OverlayMenuEntry(preset.first, target, Task { command("bot loadout load $slot " + preset.first) }, LOAD_PRESET))
					}
					entries.add(OverlayMenuEntry("Save gear as preset...", target, Task { command("bot loadout save $slot") }))
					entries.add(OverlayMenuEntry("Bank items", target, Task { command("bot bank $slot") }))
					entries.add(OverlayMenuEntry(if (bot.aggressive) "Set passive" else "Set aggressive", target, Task { command("bot aggression $slot " + if (bot.aggressive) "passive" else "aggressive") }))
					entries.add(OverlayMenuEntry("Profile: " + PROFILES[(bot.profile + 1) % PROFILES.size], target, Task { command("bot combat $slot " + PROFILES[(bot.profile + 1) % PROFILES.size]) }))
					entries.add(OverlayMenuEntry("Dismiss", target, Task { command("bot dismiss $slot") }))
				}
				return entries
			}
			// off a bot's row (the title, a dead slot, the empty part): what concerns every bot
			if (state.bots.any { it.online }) {
				entries.add(OverlayMenuEntry("Follow all", "Bots", Task { command("bot follow all") }))
				entries.add(OverlayMenuEntry("Stop all", "Bots", Task { command("bot stop all") }))
				entries.add(OverlayMenuEntry("Hold all", "Bots", Task { command("bot hold all") }))
				entries.add(OverlayMenuEntry("Call all", "Bots", Task { command("bot call all") }))
			}
			entries.add(OverlayMenuEntry("Refresh", "Bots", Task { handshake() }))
			entries.add(OverlayMenuEntry("Configure", "Bots", Task { PluginManagerScreen.openSettings(DESCRIPTOR.id) }))
			return entries
		}

		/** A left click on a living bot's row opens its equipment and inventory. */
		override fun onClick(x: Int, y: Int, button: Int): Boolean {
			val bot: Bot = rowAt(y) ?: return false
			if (button != 0 || !bot.alive || !bot.online) {
				return false
			}
			OwnedBotsLoadout.show(bot.slot)
			return true
		}

		override fun render(g: Graphics, view: Viewport): Size? = drawPanel(g, this)
	}

	/** The task catalogue: hover a task and give it to the selected bot. */
	@JvmField
	val picker: PanelOverlay = object : PanelOverlay("OwnedBotsTasks") {
		init {
			menuOnRightClick = true
		}

		override fun menuEntries(): List<OverlayMenuEntry> {
			val entries: ArrayList<OverlayMenuEntry> = ArrayList()
			val bot: Bot = state.bot(selected) ?: return entries
			val task: TaskEntry? = hoveredTask(this)
			if (task != null) {
				entries.add(OverlayMenuEntry("Assign to " + bot.name, "<col=ff9040>" + task.label, Task { command("bot task " + (bot.slot + 1) + " " + task.id); tasksOpen = false }))
			}
			entries.add(OverlayMenuEntry("Reload list", "Tasks", Task { tasks.clear(); command("bot tasks") }))
			entries.add(OverlayMenuEntry("Close", "Tasks", Task { tasksOpen = false }))
			return entries
		}

		/** A left click on a task gives it to the bot, as its menu entry does. */
		override fun onClick(x: Int, y: Int, button: Int): Boolean {
			val bot: Bot = state.bot(selected) ?: return false
			val task: TaskEntry = taskAt(x, y) ?: return false
			if (button != 0) {
				return false
			}
			command("bot task " + (bot.slot + 1) + " " + task.id)
			tasksOpen = false
			return true
		}

		override fun render(g: Graphics, view: Viewport): Size? = drawTasks(g, this)
	}

	override fun start(context: PluginContext) {
		this.context = context
		val s: SettingsSchema = context.settings()
		s.toggle("enabled", "Owned bots", false)
		s.toggle("sounds", "Sound when a bot is in trouble", true)
		s.toggle("showOnMap", "Show bots on the minimap", true)
		s.toggle("showTags", "Tag your bots in the world", true)
		s.toggle("hideTaskLines", "Hide the task list lines from the chat", true)
		s.keybind("callKey", "Call all bots", 0)
		s.keybind("followKey", "Follow me, all bots", 0)
		s.keybind("stopKey", "Stop all bots", 0)
		readSettings(context.config())
		for (slot in 0 until SLOTS) {
			giveActions[slot] = context.registerMenuAction(MenuAction { _, _, _, id -> give(slot, id, false) })
			giveBankActions[slot] = context.registerMenuAction(MenuAction { _, _, _, id -> give(slot, id, true) })
		}
		// one action a kind of world order: which bot (or all) comes from the entry's label, read as the click arrives
		sendAction = context.registerMenuAction(MenuAction { _, sceneX, sceneY, _ -> worldOrder("goto", (sceneX + context.client().sceneBaseX()).toString() + " " + (sceneY + context.client().sceneBaseY())) })
		attackAction = context.registerMenuAction(MenuAction { _, _, _, id -> worldOrder("attack", id.toString()) })
		pickupAction = context.registerMenuAction(MenuAction { _, sceneX, sceneY, id -> worldOrder("pickup", (sceneX + context.client().sceneBaseX()).toString() + " " + (sceneY + context.client().sceneBaseY()) + " " + commandName(context.client().itemName(id) ?: return@MenuAction)) })
		context.events().subscribe(Events.KEY_PRESSED, keyListener)
		context.registerCommand("ownedbots", toggle)
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		context.events().subscribe(Events.GAME_STATE_CHANGED, stateListener)
		context.events().subscribe(Events.GAME_TICK, tickListener)
		context.events().subscribe(Events.SERVER_TICK, serverTickListener)
		context.events().subscribe(Events.CHAT_MESSAGE, chatListener)
		context.events().subscribe(Events.VIEWPORT_DRAW, viewportListener)
		context.events().subscribe(Events.FRAME_DRAW, frameListener)
		context.events().subscribe(Events.MENU_ENTRY_ADDED, menuListener)
		context.events().subscribe(Events.MENU_OPTION_CLICKED, optionListener)
		context.addOverlay(panel)
		context.addOverlay(OwnedBotsLoadout.overlay)
		context.addOverlay(picker)
		if (context.client().gameState() === GameState.LOGGED_IN) {
			handshakeDue = cycle + 1
		}
	}

	override fun stop() {
		context = null
	}

	@JvmStatic
	fun readSettings(config: PluginConfig) {
		val was: Boolean = enabled
		enabled = config.getBoolean("enabled", false)
		sounds = config.getBoolean("sounds", true)
		showOnMap = config.getBoolean("showOnMap", true)
		showTags = config.getBoolean("showTags", true)
		hideTaskLines = config.getBoolean("hideTaskLines", true)
		callKey = config.getInt("callKey", 0)
		followKey = config.getInt("followKey", 0)
		stopKey = config.getInt("stopKey", 0)
		if (enabled && !was && context?.client()?.gameState() === GameState.LOGGED_IN) {
			handshakeDue = cycle + 1
		}
	}

	/** Tells the server this client can take the variables, and asks for the task list. */
	private fun handshake() {
		command("bot ui " + HANDSHAKE + PluginApi.VERSION)
		if (tasks.isEmpty()) {
			command("bot tasks")
		}
		command("bot loadout list")
	}

	private fun give(slot: Int, itemId: Int, bank: Boolean) {
		val amount: String = giveAmount
		giveAmount = ""
		val client: ClientApi = context?.client() ?: return
		val name: String = client.itemName(itemId) ?: return
		// from the bank the bot fetches it at a booth; without an amount the server asks how many of a stack
		command("bot give " + (slot + 1) + " " + commandName(name) + (if (amount.isNotEmpty()) " $amount" else "") + (if (bank) " bank" else ""))
	}

	internal fun command(text: String) {
		ClientCommandProcessor.sendCheat(text, false, false)
	}

	// ---- drawing -----------------------------------------------------------------------------------------

	private fun hovered(overlay: PanelOverlay): Bot? {
		if (hoveredRowForTest >= 0) {
			return state.bot(hoveredRowForTest)
		}
		val client: ClientApi = context?.client() ?: return null
		val x: Int = client.mouseX() - overlay.boundsX
		if (x < 0 || x >= overlay.boundsWidth) {
			return null
		}
		return rowAt(client.mouseY() - overlay.boundsY)
	}

	/** The bot whose row is [y] pixels down the roster, or null. */
	private fun rowAt(y: Int): Bot? {
		for (i in rowTops.indices) {
			if (y >= rowTops[i] && y < rowTops[i] + rowHeight) {
				return state.bot(i)
			}
		}
		return null
	}

	private fun drawPanel(g: Graphics, overlay: PanelOverlay): Size? {
		val current: State = state
		val client: ClientApi = context?.client() ?: return null
		if (!enabled || !g.fontReady() || client.gameState() !== GameState.LOGGED_IN) {
			return null
		}
		val line: Int = g.fontHeight()
		val width: Int = if (overlay.preferredWidth > 0) overlay.preferredWidth else PANEL_WIDTH
		statLine = line + 2
		val botHeight: Int = line + statLine + GAP
		val rows: List<Bot> = current.bots.filter { it.alive || it.name.isNotEmpty() }
		val hint: Boolean = !current.active() || rows.isEmpty()
		val height: Int = TOP + PAD + (if (hint) line else 0) + rows.size * botHeight + PAD + EDGE
		window(g, width, height, "Bots (" + current.bots.count { it.alive } + "/" + SLOTS + ")")
		var y: Int = TOP + PAD
		if (hint) {
			g.text(EDGE + PAD, y + line - 2, if (!current.active()) "No answer from the server" else "Visit a Bot Broker", HINT, BLACK)
			y += line
		}
		rowTops.clear()
		for (bot in rows) {
			rowTops.add(y)
			drawBot(g, bot, y, width, line)
			y += botHeight
		}
		rowHeight = botHeight
		return size.set(width, height)
	}

	private fun drawBot(g: Graphics, bot: Bot, top: Int, width: Int, line: Int) {
		val left: Int = EDGE + PAD
		val right: Int = width - EDGE - PAD
		val flashing: Boolean = cycle - flash[bot.slot] < FLASH_CYCLES && (cycle / 10) % 2 == 0
		if (flashing) {
			g.fillRect(left - 2, top - 1, right - left + 4, line + statLine + GAP, FLASH)
		}
		val nameColour: Int = if (!bot.alive) DEAD else if (!bot.online) AWAY else colour(bot.slot)
		g.font(Font.BOLD)
		g.text(left, top + line - 2, (bot.slot + 1).toString() + ". " + bot.name.ifEmpty { "-" }, nameColour, BLACK)
		g.font(Font.SMALL)
		val status: String = if (!bot.alive) "Dead" else if (!bot.online) "Away" else {
			val task: TaskEntry? = if (bot.task != NO_TASK) tasks.firstOrNull { it.index == bot.task } else null
			if (task != null && bot.status == 2) task.label.substringBefore(" (") else bot.statusName()
		}
		g.textRight(right, top + line - 2, status, if (bot.status in 6..9 && bot.online) PVP else STATUS_COLOUR, BLACK)
		if (bot.online) {
			val half: Int = (right - left - GAP) / 2
			stat(g, left, top + line, half, bot.hp, bot.hpMax, HP_COLOUR, "HP " + bot.hp)
			stat(g, left + half + GAP, top + line, half, bot.prayer, bot.prayerMax, PRAYER_COLOUR, "Pray " + bot.prayer)
		}
	}

	private fun stat(g: Graphics, x: Int, y: Int, width: Int, value: Int, max: Int, colour: Int, label: String) {
		val height: Int = statLine - 1
		g.fillRect(x, y + 1, width, height, BAR_BACK)
		if (max > 0) {
			g.fillRect(x, y + 1, width * value.coerceIn(0, max) / max, height, colour)
		}
		g.drawRect(x, y + 1, width, height, BAR_LINE)
		g.textCentered(x + width / 2, y + height - 2, label, WHITE, BLACK)
	}

	/**
	 * The game's stone window: the resizable side tabs' see-through stone (1131) under everything, the worn-equipment screen's
	 * frame (corners 1123/1125/826/827, the title band 1124, edges 821/823/822) over it, and [title] in the band.
	 */
	internal fun window(g: Graphics, width: Int, height: Int, title: String) {
		g.clip(0, 0, width, height)
		var y: Int = 0
		while (y < height) {
			var x: Int = 0
			while (x < width) {
				g.sprite(STONE, x, y)
				x += STONE_W
			}
			y += STONE_H
		}
		g.clip(CORNER, 0, width - CORNER * 2, height)
		// the edge strips carry a 13 pixel offset in the cache (their place inside the screen's own layer)
		var x: Int = CORNER
		while (x < width - CORNER) {
			g.sprite(FRAME_TOP, x, 0)
			g.sprite(FRAME_BOTTOM, x, height - EDGE - EDGE_OFFSET)
			x += CORNER
		}
		g.clip(0, CORNER, width, height - CORNER * 2)
		y = CORNER
		while (y < height - CORNER) {
			g.sprite(FRAME_LEFT, -EDGE_OFFSET, y)
			g.sprite(FRAME_RIGHT, width - EDGE - EDGE_OFFSET, y)
			y += CORNER
		}
		g.unclip()
		g.sprite(FRAME_TOP_LEFT, 0, 0)
		g.sprite(FRAME_TOP_RIGHT, width - CORNER, 0)
		g.sprite(FRAME_BOTTOM_LEFT, 0, height - CORNER)
		g.sprite(FRAME_BOTTOM_RIGHT, width - CORNER, height - CORNER)
		g.font(Font.BOLD)
		g.textCentered(width / 2, TITLE_BASELINE, title, TITLE, BLACK)
		g.font(Font.SMALL)
	}

	/** The task under the mouse: the list runs down [taskRows] rows a column, [TASK_COLUMN] wide. */
	private fun hoveredTask(overlay: PanelOverlay): TaskEntry? {
		val client: ClientApi = context?.client() ?: return null
		return taskAt(client.mouseX() - overlay.boundsX, client.mouseY() - overlay.boundsY)
	}

	/** The task at ([px], [py]) from the picker's top-left corner, or null. */
	private fun taskAt(px: Int, py: Int): TaskEntry? {
		val x: Int = px - EDGE - PAD
		val y: Int = py - taskTop
		if (x < 0 || y < 0 || taskLine <= 0 || taskRows <= 0) {
			return null
		}
		val column: Int = x / TASK_COLUMN
		val row: Int = y / taskLine
		if (row >= taskRows) {
			return null
		}
		return tasks.getOrNull(column * taskRows + row)
	}

	private var taskTop: Int = 0
	private var taskLine: Int = 0
	private var taskRows: Int = 0

	private fun drawTasks(g: Graphics, overlay: PanelOverlay): Size? {
		val bot: Bot = state.bot(selected) ?: return null
		if (!enabled || !tasksOpen || !g.fontReady()) {
			return null
		}
		val line: Int = g.fontHeight()
		taskLine = line
		taskTop = TOP + PAD
		// two columns, so the whole list fits the game view
		taskRows = (tasks.size + 1) / 2
		val columns: Int = if (tasks.size > 1) 2 else 1
		val width: Int = EDGE + PAD + TASK_COLUMN * columns + PAD + EDGE
		val height: Int = taskTop + (if (tasks.isEmpty()) 1 else taskRows) * line + PAD + EDGE
		window(g, width, height, "Task for " + bot.name)
		if (tasks.isEmpty()) {
			g.text(EDGE + PAD, taskTop + line - 2, "No task list yet (right-click: Reload list)", HINT, BLACK)
		}
		val hovered: TaskEntry? = hoveredTask(overlay)
		for ((i, task) in tasks.withIndex()) {
			val x: Int = EDGE + PAD + (i / taskRows) * TASK_COLUMN
			val y: Int = taskTop + (i % taskRows) * line
			if (task === hovered) {
				g.fillRect(x - 2, y, TASK_COLUMN, line, HOVER)
			}
			g.text(x, y + line - 2, task.label, if (task === hovered) WHITE else if (task.index == bot.task) TITLE else TASK_COLOUR, BLACK)
		}
		return size.set(width, height)
	}

	/** A tag over each of the owner's bots in view, in its slot's colour. */
	private fun drawTags(g: Graphics, client: ClientApi) {
		if (!g.fontReady()) {
			return
		}
		for (i in 0 until client.playerCount()) {
			val player: Player = client.player(i) ?: continue
			val name: String = player.name() ?: continue
			val bot: Bot = state.bots.firstOrNull { it.online && it.name.equals(name, ignoreCase = true) } ?: continue
			if (!player.isDrawnThisFrame() || !client.projectActor(player, player.overheadHeight() + TAG_RISE, point)) {
				continue
			}
			g.textCentered(point[0], point[1], tag(bot), colour(bot.slot), BLACK)
		}
	}

	private fun drawMinimap(g: Graphics, client: ClientApi) {
		if (!client.minimapArea(area)) {
			return
		}
		g.clip(area[0], area[1], area[2], area[3])
		val level: Int = client.localPlayer()?.level() ?: 0
		for (bot in state.bots) {
			if (!bot.online || bot.x < 0 || bot.level != level || !client.projectMinimap(bot.x - client.sceneBaseX(), bot.y - client.sceneBaseY(), point)) {
				continue
			}
			g.fillCircle(point[0], point[1], DOT, BLACK)
			g.fillCircle(point[0], point[1], DOT - 1, colour(bot.slot))
		}
		g.unclip()
	}

	/** The tag over a bot in the world: its name and slot, "Jimmy [1]". */
	@JvmStatic
	fun tag(bot: Bot): String = bot.name + " [" + (bot.slot + 1) + "]"

	/** A slot's colour: orange, sky blue, lime, pink. */
	@JvmStatic
	fun colour(slot: Int): Int = PALETTE[slot.coerceIn(0, PALETTE.size - 1)]

	private const val BASE37: String = "_abcdefghijklmnopqrstuvwxyz0123456789"
	private const val GAP: Int = 2
	private const val PANEL_WIDTH: Int = 190
	private const val TASK_COLUMN: Int = 222

	// the game's stone window (the worn-equipment screen's, interface 549)
	// the resizable side tabs' see-through stone (746's tab backgrounds), alpha 153 in the sprite itself
	private const val STONE: Int = 1131
	private const val STONE_W: Int = 190
	private const val STONE_H: Int = 261
	private const val FRAME_TOP_LEFT: Int = 1123
	private const val FRAME_TOP: Int = 1124
	private const val FRAME_TOP_RIGHT: Int = 1125
	private const val FRAME_BOTTOM_LEFT: Int = 826
	private const val FRAME_BOTTOM_RIGHT: Int = 827
	private const val FRAME_LEFT: Int = 821
	private const val FRAME_RIGHT: Int = 823
	private const val FRAME_BOTTOM: Int = 822
	private const val CORNER: Int = 32
	internal const val EDGE: Int = 7
	internal const val EDGE_OFFSET: Int = 13
	internal const val TOP: Int = 22
	private const val TITLE_BASELINE: Int = 17
	internal const val PAD: Int = 8
	internal const val CONTENT_Y: Int = TOP + PAD
	private const val DOT: Int = 3
	private const val TAG_RISE: Int = 12
	internal const val WHITE: Int = -0x1
	internal const val BLACK: Int = 0xFF000000.toInt()
	internal const val TITLE: Int = 0xFFFF981F.toInt()
	internal const val HINT: Int = 0xFFB0A890.toInt()
	private const val BAR_BACK: Int = 0xC0000000.toInt()
	private const val HP_COLOUR: Int = 0xFFD01010.toInt()
	private const val PRAYER_COLOUR: Int = 0xFF20A8C0.toInt()
	private const val STATUS_COLOUR: Int = 0xFFD8D8D8.toInt()
	private const val TASK_COLOUR: Int = 0xFFC8C8C8.toInt()
	private const val PVP: Int = 0xFFFF3030.toInt()
	private const val DEAD: Int = 0xFF806060.toInt()
	private const val AWAY: Int = 0xFF909090.toInt()
	private const val FLASH: Int = 0x60FF3030
	internal const val HOVER: Int = 0x40FFFFFF
	private const val BAR_LINE: Int = 0xFF3A3630.toInt()

	private val PALETTE: IntArray = intArrayOf(0xFFFF9F1C.toInt(), 0xFF4CC9F0.toInt(), 0xFF9EF01A.toInt(), 0xFFFF5DA2.toInt())
}
