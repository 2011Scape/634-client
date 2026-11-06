package com.jagex.game.runetek5.client.features

import ClientCommandProcessor
import com.jagex.game.runetek5.plugin.api.ChatType
import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.Font
import com.jagex.game.runetek5.plugin.api.Graphics
import com.jagex.game.runetek5.plugin.PluginImage
import com.jagex.game.runetek5.plugin.api.Image
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
import com.jagex.game.runetek5.plugin.api.events.MinimapDraw
import com.jagex.game.runetek5.plugin.api.events.MouseWheel
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
object Companions : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("companions", "Companions", "1", PluginApi.VERSION, "Shows your companions, their health and orders, and lets you command them, give them items and see where they are.", "Pixel")

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

	/** The wage coffers' variables, one a slot, after the slot blocks (owned_bot_<slot>_wages). */
	const val WAGES: Int = 2201

	/**
	 * Whole amounts, [EQUIPMENT] + [INVENTORY] a slot: a worn then carried item's amount when its packed variable
	 * can't hold it (over 65,535), else unset (owned_bot_<slot>_amount_<n>).
	 */
	const val AMOUNT_VARCS: Int = 2205

	/** The server's OwnedBotStatus, by ordinal. */
	@JvmField
	val STATUS_NAMES: Array<String> = arrayOf("Away", "Standing by", "Working", "Banking", "Following", "Fighting", "Low health", "Fled", "Stopped", "Dead", "Holding", "Restoring")

	@JvmField
	val PROFILES: Array<String> = arrayOf("passive", "melee", "ranged", "mage")

	@JvmField
	val EQUIPMENT_NAMES: Array<String> = arrayOf("hat", "cape", "amulet", "weapon", "chest", "shield", "", "legs", "", "hands", "feet", "", "ring", "ammo")

	/** The line the server prints for `::bot tasks`, and the prefix of every line about a bot. */
	const val TASKS_PREFIX: String = "[BotTasks] "
	const val TASK_ITEMS_PREFIX: String = "[BotTaskItems] "
	const val LOADOUTS_PREFIX: String = "[BotLoadouts] "

	/** Submenus the presets sit under in the roster's and the loadout's menus. */
	const val LOAD_PRESET: String = "Load preset"
	const val GEAR: String = "Gear"
	const val COMBAT: String = "Combat"
	const val WAGES_MENU: String = "Wages"

	/** What the Wages submenu offers to pay in, besides all the coins carried and a typed amount. */
	@JvmField
	val WAGE_AMOUNTS: IntArray = intArrayOf(10_000, 100_000, 1_000_000)
	const val DELETE_PRESET: String = "Delete preset"

	/** The amounts a stack is given or taken in, the bank's choices. */
	@JvmField
	val AMOUNTS: Array<String> = arrayOf("1", "5", "10", "All")
	const val ALL: String = "All"

	/** The entry in the loadout window that makes a weapon the bot's special attack weapon. */
	const val SPECIAL: String = "Special attack"

	/** What a world-order entry's label puts before the bot's name, and after it for a tile. */
	@JvmField
	val ORDER_PREFIXES: Array<String> = arrayOf("Send ", "Attack with ", "Pick up with ")
	const val HERE: String = " here"
	const val BOT_PREFIX: String = "[Companion "

	/** What the plugin sends in `::bot ui`: a character the chat box can't type, so only a plugin opts in. */
	const val HANDSHAKE: Char = '\u0001'

	/** The idle notifier's ding, for a bot in trouble. */
	const val ALERT_SOUND: Int = 2266
	const val FLASH_CYCLES: Int = 100

	/** The 634 inventory and the bank's item components, for Give entries. */
	const val INVENTORY_INTERFACE: Int = 149
	const val BANK_INTERFACE: Int = 762

	/** The spellbook tabs: modern, ancient, lunar, dungeoneering. */
	@JvmField
	val SPELLBOOKS: IntArray = intArrayOf(192, 193, 430, 950)
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
		/** The item id of the weapon it switches to for its special attack, 0 for none. */
		@JvmField val spec: Int,
		/** The component id of the spell it autocasts, 0 for none, in the book [spellBook]. */
		@JvmField val spell: Int,
		/** The interface id of the spellbook [spell] is in: the bot opens that book itself, whatever the player's. */
		@JvmField val spellBook: Int,
		@JvmField val equipment: IntArray,
		@JvmField val inventory: IntArray,
		/** Coins in the slot's wage coffer, paid in for tasks and drained as the bot gathers. */
		@JvmField val wages: Int = 0,
		/** Whole amounts of stacks too big for their packed variable, worn then carried; 0 where the packed one holds it. */
		@JvmField val amounts: IntArray = IntArray(0),
	) {
		/** The amount of the worn item in equipment slot [index], whole even past 65,535. */
		fun equipmentAmount(index: Int): Int = amounts.getOrNull(index)?.takeIf { it > 0 } ?: itemAmount(equipment[index])

		/** The amount of the carried item in inventory slot [index], whole even past 65,535. */
		fun inventoryAmount(index: Int): Int = amounts.getOrNull(EQUIPMENT + index)?.takeIf { it > 0 } ?: itemAmount(inventory[index])

		fun statusName(): String = STATUS_NAMES.getOrNull(if (online) status else 0) ?: "?"
	}

	/** The roster as the variables say; [version] 0 means the server has not answered the handshake. */
	class State(@JvmField val bots: List<Bot>, @JvmField val version: Int) {
		fun active(): Boolean = version > 0
		fun bot(slot: Int): Bot? = bots.getOrNull(slot)
	}

	/** A task of the server's catalogue: grouped by [skill] and [location], needing [level] in that skill. */
	class TaskEntry(
		@JvmField val index: Int,
		@JvmField val id: String,
		@JvmField val skill: String,
		@JvmField val level: Int,
		@JvmField val location: String,
		@JvmField val members: Boolean,
		@JvmField val label: String,
	) {
		/** What the bot fetches from the owner's bank for it, as the server encodes it: `id/id:amount,id:amount`. */
		@JvmField
		var needs: String = ""

		/** Coins in wages for each item the task gathers, the dearest of them; 0 when tasks are free. */
		@JvmField
		var wage: Int = 0
	}

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

	/** The roster folded to its title bar; its menu then offers every bot's orders under the bot's own submenu. */
	@JvmField var collapsed: Boolean = false

	private var context: PluginContext? = null

	internal val client: ClientApi?
		get() = context?.client()
	private var cycle: Int = 0
	private var handshakeDue: Int = -1
	private val flash: IntArray = IntArray(SLOTS)
	private val giveActions: IntArray = IntArray(SLOTS) { -1 }
	private val giveBankActions: IntArray = IntArray(SLOTS) { -1 }

	/** One a slot: the spell right-clicked becomes that bot's autocast (or stops being it). */
	private val spellActions: IntArray = IntArray(SLOTS) { -1 }
	private var giveAmount: String = ""
	private var sendAction: Int = -1
	private var attackAction: Int = -1
	private var pickupAction: Int = -1

	/** The bot a world-order entry named ("Send Woody here", a submenu's "Woody"), or "all"; "" when none, read as the click arrives. */
	private var orderTarget: String = ""
	/** The rows the task picker shows at once, and each window's background opacity in percent: the player's settings. */
	@JvmField var taskRows: Int = TASK_ROWS
	@JvmField var rosterOpacity: Int = 100
	@JvmField var taskOpacity: Int = 100
	@JvmField var loadoutOpacity: Int = 100
	@JvmField var callKey: Int = 0
	@JvmField var followKey: Int = 0
	@JvmField var stopKey: Int = 0

	/** A key a slot: opens that companion's loadout, or closes it when it is the one open. */
	@JvmField val loadoutKeys: IntArray = IntArray(SLOTS)
	private val point: IntArray = IntArray(2)
	private val area: IntArray = IntArray(4)
	private val size: Size = Size()
	private val rowTops: ArrayList<Int> = ArrayList()
	/** The bot drawn on each row: not every slot has one, a dead unnamed slot takes no row. */
	private val rowBots: ArrayList<Bot> = ArrayList()
	private val rowHeights: ArrayList<Int> = ArrayList()
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
					slot, name, alive, online, hp ushr 16, hp and 0xFFFF, prayer ushr 16, prayer and 0xFFFF, varc(base + STATUS) and 0xFF,
					order and 0xFF, order ushr 8 and 0xF, order ushr 12 and 0xF == 1,
					if (hidden) -1 else tile ushr 14 and 0x3FFF, if (hidden) -1 else tile and 0x3FFF, if (hidden) 0 else tile ushr 28 and 0x3,
					varc(base + ENERGY) and 0xFF, varc(base + ENERGY) ushr 8, varc(base + STATUS) ushr 8 and 0xFFFF, SPELLBOOKS[varc(base + STATUS) ushr 24 and 0x3], equipment, inventory,
					varc(WAGES + slot), IntArray(EQUIPMENT + INVENTORY) { varc(AMOUNT_VARCS + slot * (EQUIPMENT + INVENTORY) + it) },
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

	/**
	 * A `[BotTasks] index|skill|level|location|members|label[|wage]` line, or null for the heading and anything else.
	 * The server names no id (a chat line wraps at the box's width); the task is given back by its index. The wage,
	 * coins an item, is left off by a server without wages.
	 */
	@JvmStatic
	fun parseTask(text: String): TaskEntry? {
		if (!text.startsWith(TASKS_PREFIX)) {
			return null
		}
		val parts: List<String> = text.substring(TASKS_PREFIX.length).split('|')
		if (parts.size != 6 && parts.size != 7) {
			return null
		}
		val index: Int = parts[0].toIntOrNull() ?: return null
		val level: Int = parts[2].toIntOrNull() ?: return null
		val task = TaskEntry(index, index.toString(), parts[1], level, parts[3], parts[4] == "1", parts[5])
		task.wage = parts.getOrNull(6)?.toIntOrNull() ?: 0
		return task
	}

	/** [value] with a comma between each three digits, as the game writes an amount: 12,345. */
	@JvmStatic
	fun groupDigits(value: Int): String {
		val digits: String = kotlin.math.abs(value.toLong()).toString()
		val grouped: StringBuilder = StringBuilder()
		for ((i, digit) in digits.withIndex()) {
			if (i > 0 && (digits.length - i) % 3 == 0) {
				grouped.append(',')
			}
			grouped.append(digit)
		}
		return if (value < 0) "-$grouped" else grouped.toString()
	}

	/**
	 * Coins as the roster shows them in a narrow column, five characters at most: in full under 10,000, then K, then
	 * M (one decimal under 10M), then B with one decimal.
	 */
	@JvmStatic
	fun shortCoins(coins: Int): String = when {
		coins < 10_000 -> groupDigits(coins)
		coins < 1_000_000 -> (coins / 1_000).toString() + "K"
		coins < 10_000_000 -> tenths(coins / 100_000) + "M"
		coins < 1_000_000_000 -> (coins / 1_000_000).toString() + "M"
		else -> tenths(coins / 100_000_000) + "B"
	}

	/** [tenths] of a unit written with one decimal: 15 is "1.5". */
	private fun tenths(tenths: Int): String = (tenths / 10).toString() + "." + (tenths % 10).toString()

	/** A `[BotTaskItems] index|id/id:amount,...` line as the index and the encoded needs, or null for anything else. */
	@JvmStatic
	fun parseTaskItems(text: String): Pair<Int, String>? {
		if (!text.startsWith(TASK_ITEMS_PREFIX)) {
			return null
		}
		val body: String = text.substring(TASK_ITEMS_PREFIX.length)
		val bar: Int = body.indexOf('|')
		if (bar <= 0) {
			return null
		}
		val index: Int = body.substring(0, bar).toIntOrNull() ?: return null
		return index to body.substring(bar + 1)
	}

	/**
	 * The tooltip's lines for [needs] (the server's `id/id:amount,...`), an item a line, "3 x " before more than one;
	 * a choice of items is "At least <the first>", the server sending the cheapest first; named by [name].
	 */
	@JvmStatic
	fun needLines(needs: String, name: (Int) -> String?): List<String> {
		val lines: ArrayList<String> = ArrayList()
		for (need in needs.split(',')) {
			// an empty need has no ids and so no names
			val ids: String = need.substringBefore(':')
			val amount: Int = need.substringAfter(':', "1").toIntOrNull() ?: 1
			val names: List<String> = ids.split('/').mapNotNull { it.toIntOrNull() }.mapNotNull { name(it) }
			if (names.isEmpty()) {
				continue
			}
			lines.add((if (amount > 1) "$amount x " else "") + (if (names.size > 1) "at least " + names[0] else names[0]))
		}
		return lines
	}

	/** The skills with tasks, in the catalogue's order. */
	@JvmStatic
	fun taskSkills(): List<String> {
		val skills: ArrayList<String> = ArrayList()
		for (task in tasks) {
			if (task.skill !in skills) {
				skills.add(task.skill)
			}
		}
		return skills
	}

	/** The picker's rows for [skill]: a location heading (a String) before each place's tasks (TaskEntry), in catalogue order. */
	@JvmStatic
	fun taskRows(skill: String): List<Any> {
		val rows: ArrayList<Any> = ArrayList()
		var location: String? = null
		for (task in tasks) {
			if (task.skill != skill) {
				continue
			}
			if (task.location != location) {
				location = task.location
				rows.add(if (location.isEmpty()) "Elsewhere" else location)
			}
			rows.add(task)
		}
		return rows
	}

	/** "woodcutting" as a tab reads: "Woodcutting". */
	@JvmStatic
	fun skillName(skill: String): String = skill.replaceFirstChar { it.uppercase() }

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

	/** The slot (0-3) a `[Companion n] ...` line is about, or -1. */
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
	// ---- listeners ---------------------------------------------------------------------------------------

	private val settingListener: EventListener<SettingChanged> = EventListener { event ->
		if (event.pluginId == DESCRIPTOR.id) {
			context?.config()?.let { readSettings(it) }
		}
	}

	/** `::companions [on|off]` (or `::ownedbots`) turns the plugin on or off (or over), as the other built-in plugins' commands do. */
	private val toggle: CommandHandler = CommandHandler { _, args ->
		if (args.getOrNull(0).equals("map", ignoreCase = true)) {
			// ::companions map: what the minimap drawing sees, a line a companion
			val client: ClientApi = context?.client() ?: return@CommandHandler
			val shown: Boolean = client.minimapArea(area)
			client.consolePrint("minimap shown=" + shown + " area=" + area.joinToString(",") + " active=" + state.active() + " showOnMap=" + showOnMap + " local level=" + (client.localPlayer()?.level() ?: -1) + " base=" + client.sceneBaseX() + "," + client.sceneBaseY())
			for (bot in state.bots) {
				val projected: Boolean = bot.x >= 0 && client.projectMinimap(bot.x - client.sceneBaseX(), bot.y - client.sceneBaseY(), point)
				client.consolePrint(bot.name + " online=" + bot.online + " at=" + bot.x + "," + bot.y + "," + bot.level + " projected=" + projected + " point=" + point[0] + "," + point[1])
			}
			return@CommandHandler
		}
		val config: PluginConfig = context?.config() ?: return@CommandHandler
		val on: Boolean = when (args.firstOrNull()?.lowercase()) {
			"on" -> true
			"off" -> false
			else -> !enabled
		}
		config.setBoolean("enabled", on)
		config.save()
		readSettings(config)
		context?.client()?.consolePrint(if (enabled) "Companions enabled." else "Companions disabled.")
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
			CompanionsLoadout.tick(state.bot(selected))
		}
	}

	private val chatListener: EventListener<ChatMessage> = EventListener { event ->
		// only the server's own game lines: a player typing "[Companion 1] ..." in public chat is just chat
		if (!enabled || (event.type != ChatType.GAME && event.type != ChatType.GAME_FILTERED)) {
			return@EventListener
		}
		val text: String = ClientFeatures.plainText(event.message ?: return@EventListener)
		val needs: Pair<Int, String>? = parseTaskItems(text)
		if (needs != null) {
			tasks.firstOrNull { it.index == needs.first }?.needs = needs.second
			if (hideTaskLines) {
				event.consume()
			}
			return@EventListener
		}
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

	/** The minimap was drawn: the dots go on now, under the orbs and frame the game paints over it afterwards. */
	private val minimapListener: EventListener<MinimapDraw> = EventListener { event ->
		val client: ClientApi = context?.client() ?: return@EventListener
		if (enabled && showOnMap && state.active()) {
			drawMinimap(event.graphics, client)
		}
	}

	private val frameListener: EventListener<FrameDraw> = EventListener { event ->
		val client: ClientApi = context?.client() ?: return@EventListener
		if (enabled && showOnMap && state.active()) {
			drawWorldMap(event.graphics, client)
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
		if (event.target === MenuTarget.WIDGET && (event.widgetId ushr 16) in SPELLBOOKS) {
			spellEntries(client, event)
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
		val submenu: Int = client.addSubmenu(if (online.size == 1) "Give to " + online[0].name else "Give to companion")
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
			} else {
				for (amount in AMOUNTS) {
					val label: String = if (amount == ALL) "$ALL ($quantity)" else amount
					client.addSubmenuEntry(submenu, if (online.size == 1) label else bot.name + ": " + label, action)
				}
			}
		}
	}

	/**
	 * On a spell of the player's own spellbook that can be autocast: "Autocast for <bot>" for one bot out, a
	 * submenu of the bots for more; a bot already on that spell gets "Clear" instead.
	 */
	private fun spellEntries(client: ClientApi, event: MenuEntryAdded) {
		val widget = client.widgetById(event.widgetId) ?: return
		var autocast = false
		for (i in 0 until widget.actionCount()) {
			if (widget.action(i)?.contains("Autocast", ignoreCase = true) == true) {
				autocast = true
			}
		}
		if (!autocast) {
			return
		}
		val online: List<Bot> = state.bots.filter { it.online }
		if (online.isEmpty()) {
			return
		}
		val component: Int = event.widgetId and 0xFFFF
		val book: Int = event.widgetId ushr 16
		if (online.size == 1) {
			val bot: Bot = online[0]
			val action: Int = spellActions[bot.slot]
			if (action >= 0) {
				client.addMenuEntry((if (bot.spell == component && bot.spellBook == book) "Clear spell for " else "Autocast for ") + bot.name, action)
			}
			return
		}
		val submenu: Int = client.addSubmenu("Autocast for companion")
		if (submenu < 0) {
			return
		}
		for (bot in online) {
			val action: Int = spellActions[bot.slot]
			if (action >= 0) {
				client.addSubmenuEntry(submenu, if (bot.spell == component && bot.spellBook == book) bot.name + " (clear)" else bot.name, action)
			}
		}
	}

	/** The name of the spell with [component] in the spellbook interface [book], or null. */
	@JvmStatic
	fun spellName(client: ClientApi, book: Int, component: Int): String? {
		val name: String? = client.widgetById((book shl 16) or component)?.name()
		return if (name.isNullOrEmpty()) null else ClientFeatures.plainText(name)
	}

	/**
	 * Orders on the world: "Send <bot> here" on a tile, "Attack with <bot>" on an npc that can be attacked,
	 * "Pick up with <bot>" on a ground item; one entry for one bot, a submenu (each bot, then All) for more.
	 */
	private fun worldEntries(client: ClientApi, event: MenuEntryAdded) {
		val online: List<Bot> = state.bots.filter { it.online }
		// not through one of the plugin's own panels: their menus are their own
		if (online.isEmpty() || overPanel(client.mouseX(), client.mouseY())) {
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
				val npc = client.npcByIndex(event.id) ?: return
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
		val submenu: Int = client.addSubmenu(verb + "companion" + here)
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

	private fun overPanel(x: Int, y: Int): Boolean = listOf(panel, CompanionsLoadout.overlay, picker).any { overlay ->
		overlay.boundsWidth > 0 && x >= overlay.boundsX && y >= overlay.boundsY && x < overlay.boundsX + overlay.boundsWidth && y < overlay.boundsY + overlay.boundsHeight
	}

	/** The amount on a give entry's label is read just before the entry's action runs with only the item id. */
	private val optionListener: EventListener<MenuOptionClicked> = EventListener { event ->
		if (event.clickType in giveActions || event.clickType in giveBankActions) {
			giveAmount = amountOf(event.option ?: "")
		} else if (event.clickType == sendAction || event.clickType == attackAction || event.clickType == pickupAction) {
			orderTarget = botOf(event.option ?: "")
		}
	}

	/**
	 * The slot (as the command wants it, from 1) or "all" a world-order entry names: "Send Woody here", "Attack with
	 * Woody", "Special attack Woody", a submenu's "Woody" or "All". The name is matched whole, so "Woody" never
	 * stands for "Woody2".
	 */
	@JvmStatic
	fun botOf(option: String): String {
		var text: String = ClientFeatures.plainText(option).trim()
		for (prefix in ORDER_PREFIXES) {
			if (text.startsWith(prefix, ignoreCase = true)) {
				text = text.substring(prefix.length)
				break
			}
		}
		if (text.endsWith(HERE, ignoreCase = true)) {
			text = text.substring(0, text.length - HERE.length)
		}
		text = text.trim()
		if (text.equals(ALL, ignoreCase = true)) {
			return "all"
		}
		for (bot in state.bots) {
			if (bot.name.isNotEmpty() && text.equals(bot.name, ignoreCase = true)) {
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

	/** Hotkeys for the orders that matter mid-fight and for each companion's loadout; a matched key never reaches the game. */
	private val keyListener: EventListener<KeyPressed> = EventListener { event ->
		if (onKey(event.keyCode)) {
			event.consume()
		}
	}

	/** A key went down: the order or loadout it is bound to, true when it was one. */
	@JvmStatic
	fun onKey(keyCode: Int): Boolean {
		if (!enabled || !state.active() || keyCode == 0) {
			return false
		}
		for (slot in 0 until SLOTS) {
			if (loadoutKeys[slot] != keyCode) {
				continue
			}
			if (state.bot(slot)?.alive != true) {
				return true
			}
			if (CompanionsLoadout.open && selected == slot) CompanionsLoadout.close() else CompanionsLoadout.show(slot)
			return true
		}
		val order: String = when (keyCode) {
			callKey -> "call all"
			followKey -> "follow all"
			stopKey -> "stop all"
			else -> return false
		}
		command("bot $order")
		return true
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

	/**
	 * [bot]'s orders: from its row's menu, or with [submenu] (its slot, "1.") from the folded roster's, where the
	 * presets, a submenu of their own on the row, are plain "Load <preset>" entries.
	 */
	private fun botEntries(entries: ArrayList<OverlayMenuEntry>, bot: Bot, submenu: String) {
		val target: String = "<col=" + (colour(bot.slot) and 0xFFFFFF).toString(16) + ">" + bot.name
		fun add(option: String, action: Task, entryTarget: String = target, group: String = submenu) {
			entries.add(OverlayMenuEntry(option, entryTarget, action, group))
		}
		if (!bot.alive) {
			// a dead bot: only where to replace it
			add("Recruit a replacement", Task { context?.client()?.consolePrint("Adventurers for hire wait at the Servants' Guild in East Ardougne.") }, "<col=ff9040>" + bot.name)
			return
		}
		val slot: Int = bot.slot + 1
		if (!bot.online) {
			add("Summon", Task { command("bot summon $slot") })
			return
		}
		// the loadout first: it is what a left click on the row opens, and the first entry is the hover text; then
		// the orders given every minute, in the order they are reached for: Call (which has the bot follow,
		// teleporting it over only when it is not near), hold, stand down, restoring, and the task picker
		add("Loadout", Task { CompanionsLoadout.show(bot.slot) })
		add("Call", Task { command("bot call $slot") })
		add("Hold position", Task { command("bot guard $slot") })
		add("Stand down", Task { command("bot stop $slot") })
		add("Restore", Task { command("bot restore $slot") })
		add("Task...", Task { selected = bot.slot; tasksOpen = true; CompanionsLoadout.useFrom = -1 })
		// the rarer gear and combat orders fold into a submenu each; the folded roster already holds the bot in a
		// submenu of its own, and a submenu cannot nest, so there they stay flat under it
		val gear: String = if (submenu.isEmpty()) GEAR else submenu
		val combat: String = if (submenu.isEmpty()) COMBAT else submenu
		for (preset in presets) {
			add("Load " + preset.first, Task { command("bot loadout load $slot " + preset.first) }, group = gear)
		}
		add("Save gear as preset...", Task { command("bot loadout save $slot") }, group = gear)
		add("Bank everything", Task { command("bot bank $slot") }, group = gear)
		add(if (bot.aggressive) "Set passive" else "Set aggressive", Task { command("bot aggression $slot " + if (bot.aggressive) "passive" else "aggressive") }, group = combat)
		// every profile listed, the one in use marked, so the choice is one click and the current one is in view
		for (profile in PROFILES) {
			if (profile == PROFILES.getOrNull(bot.profile)) {
				add("Profile: $profile (current)", Task {}, group = combat)
			} else {
				add("Profile: $profile", Task { command("bot combat $slot $profile") }, group = combat)
			}
		}
		if (bot.spec > 0) {
			add("Clear special attack", Task { command("bot spec $slot none") }, "<col=ff9040>" + (context?.client()?.itemName(bot.spec) ?: "weapon"), group = combat)
		}
		if (bot.spell != 0) {
			add("Clear autocast", Task { command("bot spell $slot none") }, "<col=ff9040>" + (context?.client()?.let { spellName(it, bot.spellBook, bot.spell) } ?: "spell"), group = combat)
		}
		// wages for its tasks, in hand shown in the submenu's label; the coffer stays with the slot
		val wages: String = if (submenu.isEmpty()) WAGES_MENU + " (" + shortCoins(bot.wages) + ")" else submenu
		for (amount in WAGE_AMOUNTS) {
			add("Pay " + groupDigits(amount) + " coins", Task { command("bot pay $slot $amount") }, group = wages)
		}
		add("Pay all coins", Task { command("bot pay $slot all") }, group = wages)
		add("Pay amount...", Task { command("bot pay $slot") }, group = wages)
		add("Dismiss", Task { command("bot dismiss $slot") })
	}

	// ---- panels ------------------------------------------------------------------------------------------

	/** The roster: one row a slot, with the bot's name, what it is doing and its hitpoints and prayer. */
	@JvmField
	val panel: PanelOverlay = object : PanelOverlay("CompanionsPanel") {
		init {
			resizable = true
			menuOnRightClick = true
		}

		override fun menuEntries(): List<OverlayMenuEntry> {
			val entries: ArrayList<OverlayMenuEntry> = ArrayList()
			if (collapsed) {
				// folded: every bot's orders, each under a submenu named as its row is
				for (bot in state.bots) {
					if (bot.alive || bot.name.isNotEmpty()) {
						botEntries(entries, bot, (bot.slot + 1).toString() + ".")
					}
				}
			} else {
				val bot: Bot? = hovered(this)
				if (bot != null && (bot.alive || bot.name.isNotEmpty())) {
					botEntries(entries, bot, "")
					return entries
				}
			}
			// off a bot's row (the title, a dead slot, the empty part): what concerns every bot
			if (state.bots.any { it.online }) {
				entries.add(OverlayMenuEntry("Call all", "Bots", Task { command("bot call all") }))
				entries.add(OverlayMenuEntry("Hold position, all", "Bots", Task { command("bot guard all") }))
				entries.add(OverlayMenuEntry("Stand down, all", "Bots", Task { command("bot stop all") }))
				entries.add(OverlayMenuEntry("Restore all", "Bots", Task { command("bot restore all") }))
			}
			entries.add(OverlayMenuEntry("Refresh", "Companions", Task { handshake() }))
			entries.add(OverlayMenuEntry("Configure", "Companions", Task { PluginManagerScreen.openSettings(DESCRIPTOR.id) }))
			return entries
		}

		/** A left click on the title's button folds or unfolds the roster; on a living bot's row it opens its equipment and inventory. */
		override fun onClick(x: Int, y: Int, button: Int): Boolean {
			if (button != 0) {
				return false
			}
			if (onFoldButton(this, x, y)) {
				setCollapsed(!collapsed)
				return true
			}
			// a left click on the roster stays on it (nothing walks the player through the window); a bot that
			// is out opens its loadout, one that is away is summoned
			val bot: Bot = rowAt(y) ?: return true
			if (bot.alive && bot.online) {
				CompanionsLoadout.show(bot.slot)
			} else if (bot.alive) {
				command("bot summon " + (bot.slot + 1))
			}
			return true
		}

		override fun render(g: Graphics, view: Viewport): Size? = drawPanel(g, this)
	}

	/** The task catalogue: hover a task and give it to the selected bot. */
	@JvmField
	val picker: PanelOverlay = object : PanelOverlay("CompanionsTasks") {
		init {
			// under the game's interfaces, as the roster is: the bank or the settings draw over it and keep the clicks
			menuOnRightClick = true
		}

		override fun menuEntries(): List<OverlayMenuEntry> {
			val entries: ArrayList<OverlayMenuEntry> = ArrayList()
			val bot: Bot = state.bot(selected) ?: return entries
			val task: TaskEntry? = hoveredTask(this)
			if (task != null) {
				entries.add(OverlayMenuEntry("Assign to " + bot.name, "<col=ff9040>" + task.label, Task { command("bot task " + (bot.slot + 1) + " " + task.id); tasksOpen = false }))
			}
			entries.add(OverlayMenuEntry("Close", "Tasks", Task { tasksOpen = false }))
			entries.add(OverlayMenuEntry("Reload list", "Tasks", Task { tasks.clear(); command("bot tasks") }))
			return entries
		}

		/**
		 * A left click: the close button shuts the picker; the scrollbar's thumb starts a drag and its track pages;
		 * a tab shows that skill; a task gives it to the bot, as its menu entry does. A left click anywhere else on
		 * the window is kept too, so nothing walks the player through it, as the game's own windows keep theirs.
		 */
		override fun onClick(x: Int, y: Int, button: Int): Boolean {
			val bot: Bot = state.bot(selected) ?: return true
			if (button != 0) {
				return false
			}
			if (onCloseButton(x, y)) {
				tasksOpen = false
				return true
			}
			if (scrollRows > 0 && x >= scrollX && x < scrollX + SCROLL_W && y >= taskTop && y < taskTop + scrollTrack) {
				val thumbTop: Int = thumbTop()
				if (y >= thumbTop && y < thumbTop + thumbHeight) {
					scrollDrag = y - thumbTop
				} else {
					taskScroll = (taskScroll + if (y < thumbTop) -visibleRows else visibleRows).coerceIn(0, scrollRows)
				}
				return true
			}
			val tab: String? = tabAt(x, y)
			if (tab != null) {
				if (tab != taskTab) {
					taskScroll = 0
				}
				taskTab = tab
				return true
			}
			val task: TaskEntry = taskAt(x, y) ?: return true
			command("bot task " + (bot.slot + 1) + " " + task.id)
			tasksOpen = false
			return true
		}

		override fun render(g: Graphics, view: Viewport): Size? = drawTasks(g, this, view)
	}

	override fun start(context: PluginContext) {
		this.context = context
		val s: SettingsSchema = context.settings()
		s.toggle("enabled", "Companions", false)
		s.toggle("sounds", "Sound when a companion is in trouble", true)
		s.toggle("showOnMap", "Show companions on the minimap and the world map", true)
		s.toggle("showTags", "Tag your companions in the world", true)
		s.toggle("hideTaskLines", "Hide the task list lines from the chat", true)
		s.number("taskRows", "Task picker rows", TASK_ROWS, 4, 30, 1)
		s.number("rosterOpacity", "Roster background opacity (%)", 100, 0, 100, 5)
		s.number("taskOpacity", "Task picker background opacity (%)", 100, 0, 100, 5)
		s.number("loadoutOpacity", "Loadout background opacity (%)", 100, 0, 100, 5)
		s.keybind("callKey", "Call all companions", 0)
		s.keybind("followKey", "Follow me, all companions", 0)
		s.keybind("stopKey", "Stand down, all companions", 0)
		for (slot in 0 until SLOTS) {
			s.keybind("loadoutKey" + (slot + 1), "Loadout of companion " + (slot + 1), 0)
		}
		readSettings(context.config())
		for (slot in 0 until SLOTS) {
			giveActions[slot] = context.registerMenuAction(MenuAction { _, _, _, id -> give(slot, id, false) })
			giveBankActions[slot] = context.registerMenuAction(MenuAction { _, _, _, id -> give(slot, id, true) })
			// a widget click carries the component's packed id as its second coordinate: the book and the spell
			spellActions[slot] = context.registerMenuAction(MenuAction { _, _, widget, _ -> command("bot spell " + (slot + 1) + " " + (widget ushr 16) + ":" + (widget and 0xFFFF)) })
		}
		// one action a kind of world order: which bot (or all) comes from the entry's label, read as the click arrives
		sendAction = context.registerMenuAction(MenuAction { _, sceneX, sceneY, _ -> worldOrder("goto", (sceneX + context.client().sceneBaseX()).toString() + " " + (sceneY + context.client().sceneBaseY())) })
		attackAction = context.registerMenuAction(MenuAction { _, _, _, id -> worldOrder("attack", id.toString()) })
		pickupAction = context.registerMenuAction(MenuAction { _, sceneX, sceneY, id -> worldOrder("pickup", (sceneX + context.client().sceneBaseX()).toString() + " " + (sceneY + context.client().sceneBaseY()) + " " + id) })
		context.events().subscribe(Events.KEY_PRESSED, keyListener)
		context.events().subscribe(Events.MOUSE_WHEEL, wheelListener)
		context.registerCommand("companions", toggle)
		context.registerCommand("ownedbots", toggle)
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		context.events().subscribe(Events.GAME_STATE_CHANGED, stateListener)
		context.events().subscribe(Events.GAME_TICK, tickListener)
		context.events().subscribe(Events.SERVER_TICK, serverTickListener)
		context.events().subscribe(Events.CHAT_MESSAGE, chatListener)
		context.events().subscribe(Events.VIEWPORT_DRAW, viewportListener)
		context.events().subscribe(Events.FRAME_DRAW, frameListener)
		context.events().subscribe(Events.MINIMAP_DRAW, minimapListener)
		context.events().subscribe(Events.MENU_ENTRY_ADDED, menuListener)
		context.events().subscribe(Events.MENU_OPTION_CLICKED, optionListener)
		context.addOverlay(panel)
		context.addOverlay(CompanionsLoadout.overlay)
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
		taskRows = config.getInt("taskRows", TASK_ROWS).coerceIn(4, 30)
		rosterOpacity = config.getInt("rosterOpacity", 100).coerceIn(0, 100)
		taskOpacity = config.getInt("taskOpacity", 100).coerceIn(0, 100)
		loadoutOpacity = config.getInt("loadoutOpacity", 100).coerceIn(0, 100)
		callKey = config.getInt("callKey", 0)
		followKey = config.getInt("followKey", 0)
		stopKey = config.getInt("stopKey", 0)
		for (slot in 0 until SLOTS) {
			loadoutKeys[slot] = config.getInt("loadoutKey" + (slot + 1), 0)
		}
		collapsed = config.getBoolean("collapsed", false)
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
		// by cache id (the server takes either); from the bank the bot fetches it at a booth; without an amount the server asks how many of a stack
		command("bot give " + (slot + 1) + " " + itemId + (if (amount.isNotEmpty()) " $amount" else "") + (if (bank) " bank" else ""))
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
			if (y >= rowTops[i] && y < rowTops[i] + rowHeights[i]) {
				return rowBots.getOrNull(i)
			}
		}
		return null
	}

	private fun setCollapsed(value: Boolean) {
		collapsed = value
		val config: PluginConfig = context?.config() ?: return
		config.setBoolean("collapsed", value)
		config.save()
	}

	/** The fold button at the title's right, where the game's windows have their close button. */
	private fun onFoldButton(overlay: PanelOverlay, x: Int, y: Int): Boolean {
		val left: Int = overlay.boundsWidth - BUTTON_RIGHT - BUTTON_SIZE
		return overlay.boundsWidth > 0 && x >= left && x < left + BUTTON_SIZE && y >= BUTTON_TOP && y < BUTTON_TOP + BUTTON_SIZE
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
		// a row with bars (a bot that is out) is taller than one without (away, dead)
		fun rowHeightOf(bot: Bot): Int = if (bot.online) botHeight else line + ROW_GAP
		val height: Int = if (collapsed) COLLAPSED_HEIGHT else TOP + PAD + (if (hint) line else 0) + rows.sumOf { rowHeightOf(it) } + PAD + EDGE
		window(g, width, height, "Companions (" + current.bots.count { it.alive } + "/" + SLOTS + ")", 0, rosterOpacity)
		val overButton: Boolean = onFoldButton(overlay, client.mouseX() - overlay.boundsX, client.mouseY() - overlay.boundsY)
		val button: Int = if (collapsed) (if (overButton) EXPAND_HOVER else EXPAND) else (if (overButton) COLLAPSE_HOVER else COLLAPSE)
		g.sprite(button, width - BUTTON_RIGHT - BUTTON_SIZE, BUTTON_TOP)
		rowTops.clear()
		rowBots.clear()
		rowHeights.clear()
		if (collapsed) {
			return size.set(width, height)
		}
		var y: Int = TOP + PAD
		if (hint) {
			g.text(EDGE + PAD, y + line - 2, if (!current.active()) "No answer from the server" else "Recruit an Adventurer at the Servants' Guild", HINT, BLACK)
			y += line
		}
		for (bot in rows) {
			rowTops.add(y)
			rowBots.add(bot)
			rowHeights.add(rowHeightOf(bot))
			drawBot(g, bot, y, width, line)
			y += rowHeightOf(bot)
		}
		return size.set(width, height)
	}

	private fun drawBot(g: Graphics, bot: Bot, top: Int, width: Int, line: Int) {
		val left: Int = EDGE + PAD
		val right: Int = width - EDGE - PAD
		val flashing: Boolean = cycle - flash[bot.slot] < FLASH_CYCLES && (cycle / 10) % 2 == 0
		if (flashing) {
			g.fillRect(left - 2, top - 1, right - left + 4, line + statLine + GAP, FLASH)
		}
		// a dark band under the row: the text reads over whatever the scene has behind the stone
		g.fillRect(left - 2, top - 1, right - left + 4, (if (bot.online) line + statLine + GAP else line + ROW_GAP) - 1, ROW_BACK)
		val nameColour: Int = if (!bot.alive) DEAD else if (!bot.online) AWAY else colour(bot.slot)
		g.font(Font.BOLD)
		val name: String = (bot.slot + 1).toString() + ". " + bot.name.ifEmpty { "-" }
		g.text(left, top + line - 2, name, nameColour, BLACK)
		val nameRight: Int = left + g.textWidth(name)
		g.font(Font.SMALL)
		val status: String = if (!bot.alive) "Dead" else if (!bot.online) "Away" else {
			val task: TaskEntry? = if (bot.task != NO_TASK) tasks.firstOrNull { it.index == bot.task } else null
			if (task != null && bot.status == 2) task.label.substringBefore(" (") else bot.statusName()
		}
		val statusColour: Int = statusColour(bot)
		val statusText: String = fit(g, status, right - nameRight - STATUS_GAP - DOT_SIZE - DOT_GAP)
		g.textRight(right, top + line - 2, statusText, statusColour, BLACK)
		// a dot in the status's colour before it: the state at a glance, even before the word is read
		val dotX: Int = right - g.textWidth(statusText) - DOT_GAP - DOT_SIZE
		g.fillRect(dotX, top + line - 2 - DOT_SIZE, DOT_SIZE, DOT_SIZE, statusColour)
		g.drawRect(dotX, top + line - 2 - DOT_SIZE, DOT_SIZE, DOT_SIZE, BLACK)
		if (bot.online) {
			// the two bars, then the wage coffer at the right end: a coin and the amount, as wide as it needs
			val coins: String = shortCoins(bot.wages)
			val coinsWidth: Int = g.textWidth(coins)
			val column: Int = BAR_ICON + COIN_GAP + coinsWidth
			val half: Int = (right - left - GAP * 3 - column) / 2
			stat(g, left, top + line, half, bot.hp, bot.hpMax, HP_COLOUR, HP_ICON, bot.hp.toString())
			stat(g, left + half + GAP, top + line, half, bot.prayer, bot.prayerMax, PRAYER_COLOUR, PRAYER_ICON, bot.prayer.toString())
			val coin: Image? = barIcon(COIN_ICON)
			if (coin != null) {
				g.image(coin, right - column, top + line + 1 + (statLine - 1 - BAR_ICON) / 2)
			}
			g.textRight(right, top + line + statLine - 3, coins, wageColour(bot), BLACK)
		}
	}

	/** Gold with wages in hand; amber under ten items' worth of the bot's task; red when there is nothing to pay it. */
	private fun wageColour(bot: Bot): Int {
		val task: TaskEntry? = if (bot.task != NO_TASK) tasks.firstOrNull { it.index == bot.task } else null
		return when {
			bot.wages <= 0 -> PVP
			task != null && task.wage > 0 && bot.wages < task.wage * WAGE_WARN_ITEMS -> STATUS_WAIT
			else -> COINS
		}
	}

	/** [text] cut to [room] pixels in the current font, ending in ".." when cut. */
	internal fun fit(g: Graphics, text: String, room: Int): String {
		if (g.textWidth(text) <= room) {
			return text
		}
		var end: Int = text.length
		while (end > 0 && g.textWidth(text.substring(0, end).trimEnd() + "..") > room) {
			end--
		}
		return if (end == 0) "" else text.substring(0, end).trimEnd() + ".."
	}

	/** The colour a status reads in: green at work, white following or guarding, red in trouble, amber restoring, grey away or dead. */
	private fun statusColour(bot: Bot): Int = when {
		!bot.alive -> DEAD
		!bot.online -> AWAY
		bot.status == 2 || bot.status == 3 -> STATUS_GOOD
		bot.status == 5 || bot.status in 6..9 -> PVP
		bot.status == 11 -> STATUS_WAIT
		else -> STATUS_COLOUR
	}

	/** A bar of [value] out of [max] in [colour], the [icon] ([HP_ICON] or [PRAYER_ICON]) at its left end and [label] centred in the rest. */
	private fun stat(g: Graphics, x: Int, y: Int, width: Int, value: Int, max: Int, colour: Int, icon: Int, label: String) {
		val height: Int = statLine - 1
		g.fillRect(x, y + 1, width, height, BAR_BACK)
		if (max > 0) {
			g.fillRect(x, y + 1, width * value.coerceIn(0, max) / max, height, colour)
		}
		g.drawRect(x, y + 1, width, height, BAR_LINE)
		// the icon, 12 by 12, inset inside the bar and clipped to it, so it never sits on the border
		val image: Image? = barIcon(icon)
		g.clip(x + 1, y + 2, width - 2, height - 2)
		if (image != null) {
			g.image(image, x + BAR_ICON_INSET, y + 1 + (height - BAR_ICON) / 2)
		}
		g.unclip()
		val iconRoom: Int = BAR_ICON_INSET + BAR_ICON + 2
		g.textCentered(x + iconRoom + (width - iconRoom) / 2, y + height - 2, label, WHITE, BLACK)
	}

	/**
	 * The game's stone window: the windows' tiling stone (297) under everything, the worn-equipment screen's
	 * frame (corners 1123/1125/826/827, the title band 1124, edges 821/823/822) over it, and [title] in the band.
	 */
	internal fun window(g: Graphics, width: Int, height: Int, title: String, backing: Int = 0, opacity: Int = 100) {
		g.clip(0, 0, width, height)
		// the stone at the window's opacity: the cache's sprite as it is at 100, else a copy of it with its alpha
		// scaled (a sprite cannot be drawn see-through, a plugin image can)
		val stone: Image? = if (opacity >= 100) null else stoneAt(opacity)
		var y: Int = 0
		if (opacity > 0) {
			while (y < height) {
				var x: Int = 0
				while (x < width) {
					if (stone != null) g.image(stone, x, y) else g.sprite(STONE, x, y)
					x += STONE_W
				}
				y += STONE_H
			}
		}
		if (backing != 0 && opacity > 0) {
			// a tint over the stone, under the frame and title: darker for the text, the grain still through it; it
			// fades with the stone
			val alpha: Int = ((backing ushr 24) * opacity / 100) shl 24
			g.fillRect(0, 0, width, height, alpha or (backing and 0xFFFFFF))
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
		if (height >= CORNER * 2) {
			g.sprite(FRAME_TOP_LEFT, 0, 0)
			g.sprite(FRAME_TOP_RIGHT, width - CORNER, 0)
			g.sprite(FRAME_BOTTOM_LEFT, 0, height - CORNER)
			g.sprite(FRAME_BOTTOM_RIGHT, width - CORNER, height - CORNER)
		} else {
			// too short for whole corners (the folded roster): the bottom corners below the title band, the top ones in it
			g.clip(0, TOP, width, height - TOP)
			g.sprite(FRAME_BOTTOM_LEFT, 0, height - CORNER)
			g.sprite(FRAME_BOTTOM_RIGHT, width - CORNER, height - CORNER)
			g.clip(0, 0, width, TOP)
			g.sprite(FRAME_TOP_LEFT, 0, 0)
			g.sprite(FRAME_TOP_RIGHT, width - CORNER, 0)
			g.unclip()
		}
		g.font(Font.BOLD)
		g.textCentered(width / 2, TITLE_BASELINE, title, TITLE, BLACK)
		g.font(Font.SMALL)
	}

	/** The task under the mouse, or null. */
	private fun hoveredTask(overlay: PanelOverlay): TaskEntry? {
		val client: ClientApi = context?.client() ?: return null
		return taskAt(client.mouseX() - overlay.boundsX, client.mouseY() - overlay.boundsY)
	}

	/** The task at ([px], [py]) from the picker's top-left corner, or null (a heading, the scrollbar or nothing there). */
	private fun taskAt(px: Int, py: Int): TaskEntry? {
		val y: Int = py - taskTop
		if (px < EDGE || px >= rowsRight || y < 0 || taskLine <= 0 || y >= visibleRows * taskLine) {
			return null
		}
		return rowsShown.getOrNull(taskScroll + y / taskLine) as? TaskEntry
	}

	/** The skill tab at ([px], [py]), or null. */
	private fun tabAt(px: Int, py: Int): String? {
		for (i in tabsShown.indices) {
			if (px >= tabLefts[i] && px < tabLefts[i] + tabWidths[i] && py >= tabTops[i] && py < tabTops[i] + tabLine) {
				return tabsShown[i]
			}
		}
		return null
	}

	private fun onCloseButton(x: Int, y: Int): Boolean {
		val left: Int = taskWidth - CLOSE_RIGHT - CLOSE_SIZE
		return taskWidth > 0 && x >= left && x < left + CLOSE_SIZE && y >= CLOSE_TOP && y < CLOSE_TOP + CLOSE_SIZE
	}

	/** The mouse wheel over the picker scrolls its tasks. */
	private val wheelListener: EventListener<MouseWheel> = EventListener { event ->
		val client: ClientApi = context?.client() ?: return@EventListener
		if (!enabled || !tasksOpen || scrollRows <= 0) {
			return@EventListener
		}
		val x: Int = client.mouseX() - picker.boundsX
		val y: Int = client.mouseY() - picker.boundsY
		if (x < 0 || y < 0 || x >= picker.boundsWidth || y >= picker.boundsHeight || modalOpen(client)) {
			return@EventListener
		}
		taskScroll = (taskScroll + event.rotation).coerceIn(0, scrollRows)
		event.consume()
	}

	/** Whether a modal game interface (the bank, the settings) is open, drawn over the picker. */
	private fun modalOpen(client: ClientApi): Boolean {
		var modal = false
		client.openInterfaces { _, _, openType -> if (openType == 0) modal = true }
		return modal
	}

	/** The skill whose tasks the picker shows; the first with tasks until chosen. */
	@JvmField
	var taskTab: String = ""

	/** The first row shown, when there are more than fit. */
	@JvmField
	var taskScroll: Int = 0
	private var taskTop: Int = 0
	private var taskLine: Int = 0
	private var tabLine: Int = 0
	private var taskWidth: Int = 0
	private var rowsRight: Int = 0
	private var visibleRows: Int = 0
	private var scrollRows: Int = 0
	private var scrollX: Int = 0
	private var scrollTrack: Int = 0
	private var thumbHeight: Int = 0
	private var scrollDrag: Int = -1
	private val rowsShown: ArrayList<Any> = ArrayList()
	private val tabsShown: ArrayList<String> = ArrayList()
	private val tabLefts: ArrayList<Int> = ArrayList()
	private val tabTops: ArrayList<Int> = ArrayList()
	private val tabWidths: ArrayList<Int> = ArrayList()

	private fun thumbTop(): Int = taskTop + if (scrollRows <= 0) 0 else (scrollTrack - thumbHeight) * taskScroll / scrollRows

	/**
	 * The picker: a tab a skill across the top, named in full and wrapped to the view's width, and under the
	 * chosen one its tasks grouped by place, each with the level it needs; the bot's own task in the title's
	 * colour. Never taller than the view: what doesn't fit scrolls, by the wheel or the bar's thumb.
	 */
	private fun drawTasks(g: Graphics, overlay: PanelOverlay, view: Viewport): Size? {
		val bot: Bot = state.bot(selected) ?: return null
		if (!enabled || !tasksOpen || !g.fontReady()) {
			return null
		}
		val client: ClientApi? = context?.client()
		val mouseX: Int = (client?.mouseX() ?: 0) - overlay.boundsX
		val mouseY: Int = (client?.mouseY() ?: 0) - overlay.boundsY
		val line: Int = g.fontHeight()
		taskLine = line
		tabLine = line + TAB_PAD * 2
		val maxWidth: Int = maxOf(TASK_MIN_WIDTH, view.width - VIEW_MARGIN * 2)
		val maxHeight: Int = maxOf(TOP + tabLine + PAD + line * 3 + PAD + EDGE, view.height - VIEW_MARGIN * 2)
		tabsShown.clear()
		tabsShown.addAll(taskSkills())
		if (taskTab !in tabsShown) {
			taskTab = tabsShown.firstOrNull() ?: ""
		}
		// the tabs, all as wide as the widest name, so a second row lines up under the first; as many rows as the
		// view's width asks
		tabLefts.clear()
		tabTops.clear()
		tabWidths.clear()
		var tabWidth: Int = 0
		for (skill in tabsShown) {
			tabWidth = maxOf(tabWidth, g.textWidth(skillName(skill)) + TAB_PAD * 2)
		}
		var x: Int = EDGE + PAD
		var tabTop: Int = TOP
		var tabsWidth: Int = 0
		for (skill in tabsShown) {
			val width: Int = tabWidth
			if (x > EDGE + PAD && x + width + PAD + EDGE > maxWidth) {
				x = EDGE + PAD
				tabTop += tabLine + TAB_GAP
			}
			tabLefts.add(x)
			tabTops.add(tabTop)
			tabWidths.add(width)
			x += width + TAB_GAP
			tabsWidth = maxOf(tabsWidth, x - TAB_GAP + PAD + EDGE)
		}
		taskTop = if (tabsShown.isEmpty()) TOP else tabTop + tabLine + PAD
		rowsShown.clear()
		rowsShown.addAll(taskRows(taskTab))
		// a set number of rows under the tabs (fewer only in a view too short for them), whatever the tab holds: the
		// window keeps one size from tab to tab, and the rest scrolls with a bar at the right
		val rowsAll: Int = if (rowsShown.isEmpty()) 1 else rowsShown.size
		visibleRows = minOf(taskRows, maxOf(1, (maxHeight - taskTop - PAD - EDGE) / line))
		scrollRows = rowsAll - visibleRows
		if (scrollRows <= 0) {
			taskScroll = 0
			scrollDrag = -1
		} else {
			taskScroll = taskScroll.coerceIn(0, scrollRows)
		}
		// the bar's room is kept whether or not this tab needs one, so the rows' edge stays put
		val bar: Int = SCROLL_W + SCROLL_GAP
		val width: Int = minOf(maxWidth, maxOf(tabsWidth, TASK_MIN_WIDTH))
		taskWidth = width
		rowsRight = width - EDGE - PAD - bar
		scrollX = width - EDGE - PAD - SCROLL_W
		scrollTrack = visibleRows * line
		thumbHeight = if (scrollRows > 0) maxOf(THUMB_MIN, scrollTrack * visibleRows / rowsAll) else 0
		// a drag of the thumb follows the mouse while the button is down
		if (scrollDrag >= 0) {
			if (client != null && client.isMouseButtonDown(0)) {
				val room: Int = scrollTrack - thumbHeight
				if (room > 0) {
					taskScroll = ((mouseY - scrollDrag - taskTop) * scrollRows + room / 2) / room
					taskScroll = taskScroll.coerceIn(0, scrollRows)
				}
			} else {
				scrollDrag = -1
			}
		}
		val height: Int = taskTop + visibleRows * line + PAD + EDGE
		window(g, width, height, "Task for " + bot.name, WINDOW_BACK, taskOpacity)
		val overClose: Boolean = onCloseButton(mouseX, mouseY)
		g.sprite(if (overClose) CLOSE_HOVER else CLOSE, width - CLOSE_RIGHT - CLOSE_SIZE, CLOSE_TOP)
		if (tasks.isEmpty()) {
			g.text(EDGE + PAD, taskTop + line - 2, "No task list yet (right-click: Reload list)", HINT, BLACK)
			return size.set(width, height)
		}
		for ((i, skill) in tabsShown.withIndex()) {
			val left: Int = tabLefts[i]
			val top: Int = tabTops[i]
			val active: Boolean = skill == taskTab
			val over: Boolean = mouseY >= top && mouseY < top + tabLine && mouseX >= left && mouseX < left + tabWidths[i]
			if (active || over) {
				g.fillRect(left, top, tabWidths[i], tabLine, if (active) TAB_ACTIVE else HOVER)
			}
			g.drawRect(left, top, tabWidths[i], tabLine, BAR_LINE)
			if (active) {
				// the chosen tab carries the title's colour as an underline, as the game's own tabs mark the open one
				g.fillRect(left, top + tabLine - 2, tabWidths[i], 2, TITLE)
			}
			g.textCentered(left + tabWidths[i] / 2, top + TAB_PAD + line - 2, skillName(skill), if (active) TITLE else if (over) WHITE else TASK_COLOUR, BLACK)
		}
		val hovered: TaskEntry? = hoveredTask(overlay)
		for (i in 0 until visibleRows) {
			val row: Any = rowsShown.getOrNull(taskScroll + i) ?: break
			val y: Int = taskTop + i * line
			if (row is TaskEntry) {
				val current: Boolean = row.index == bot.task
				if (row === hovered) {
					g.fillRect(EDGE + PAD, y, rowsRight - EDGE - PAD, line, HOVER)
				} else if (current) {
					g.fillRect(EDGE + PAD, y, rowsRight - EDGE - PAD, line, CURRENT_BACK)
				}
				if (current) {
					// the task it is on: a mark in the margin, in its own colour, not the headings'
					g.fillRect(EDGE + PAD + 2, y + line / 2 - 2, 4, 4, CURRENT)
				}
				val colour: Int = if (row === hovered) WHITE else if (current) CURRENT else TASK_COLOUR
				val levelText: String = "Lvl " + row.level
				val tag: Int = if (row.members) MEMBERS_GAP + g.textWidth("members") else 0
				val label: String = fit(g, row.label, rowsRight - LEVEL_GAP - g.textWidth(levelText) - tag - (EDGE + PAD + TASK_INDENT))
				g.text(EDGE + PAD + TASK_INDENT, y + line - 2, label, colour, BLACK)
				if (row.members) {
					// members' tasks: a dim tag after the name rather than a longer name
					g.text(EDGE + PAD + TASK_INDENT + g.textWidth(label) + MEMBERS_GAP, y + line - 2, "members", HINT, BLACK)
				}
				g.textRight(rowsRight, y + line - 2, levelText, if (row === hovered) WHITE else HINT, BLACK)
			} else {
				g.text(EDGE + PAD, y + line - 2, row.toString(), TITLE, BLACK)
				// a rule under each place's name, the row's width
				g.fillRect(EDGE + PAD, y + line - 1, rowsRight - EDGE - PAD, 1, RULE)
			}
		}
		if (scrollRows > 0) {
			g.fillRect(scrollX, taskTop, SCROLL_W, scrollTrack, BAR_BACK)
			g.drawRect(scrollX, taskTop, SCROLL_W, scrollTrack, BAR_LINE)
			val thumbTop: Int = thumbTop()
			val overThumb: Boolean = scrollDrag >= 0 || (mouseX >= scrollX && mouseX < scrollX + SCROLL_W && mouseY >= thumbTop && mouseY < thumbTop + thumbHeight)
			g.fillRect(scrollX + 1, thumbTop + 1, SCROLL_W - 2, thumbHeight - 2, if (overThumb) THUMB_HOVER else THUMB)
			g.drawRect(scrollX + 1, thumbTop + 1, SCROLL_W - 2, thumbHeight - 2, BAR_LINE)
		}
		if (hovered != null && (hovered.needs.isNotEmpty() || hovered.wage > 0) && client != null) {
			drawNeeds(g, hovered, mouseX, mouseY, width, height, line, client)
		}
		return size.set(width, height)
	}

	/** The tooltip under the mouse: what the bot fetches from the owner's bank for the hovered task. */
	private fun drawNeeds(g: Graphics, task: TaskEntry, mouseX: Int, mouseY: Int, width: Int, height: Int, line: Int, client: ClientApi) {
		val needs: List<String> = needLines(task.needs) { client.itemName(it) }
		val wage: String? = if (task.wage > 0) "Wages: " + groupDigits(task.wage) + " coins an item" else null
		val lines: List<String> = if (wage == null) needs else needs + wage
		if (lines.isEmpty()) {
			return
		}
		val title: String = if (needs.isEmpty()) WAGES_TITLE else NEEDS_TITLE
		var tipWidth: Int = g.textWidth(title)
		for (text in lines) {
			tipWidth = maxOf(tipWidth, g.textWidth(text))
		}
		tipWidth += TIP_PAD * 2
		val tipHeight: Int = (lines.size + 1) * line + TIP_PAD * 2
		// beside the mouse, kept inside the window: left of it when there is no room to the right, above when none below
		var x: Int = mouseX + TIP_OFFSET
		if (x + tipWidth > width - EDGE) {
			x = maxOf(EDGE, mouseX - TIP_OFFSET - tipWidth)
		}
		var y: Int = mouseY + TIP_OFFSET
		if (y + tipHeight > height - EDGE) {
			y = maxOf(TOP, mouseY - TIP_OFFSET - tipHeight)
		}
		g.fillRect(x, y, tipWidth, tipHeight, TIP_BACK)
		g.drawRect(x, y, tipWidth, tipHeight, BAR_LINE)
		g.text(x + TIP_PAD, y + TIP_PAD + line - 2, title, TITLE, BLACK)
		for ((i, text) in lines.withIndex()) {
			g.text(x + TIP_PAD, y + TIP_PAD + (i + 2) * line - 2, text, if (text === wage) COINS else WHITE, BLACK)
		}
	}

	/** A tag over each of the owner's bots in view, in its slot's colour. */
	private fun drawTags(g: Graphics, client: ClientApi) {
		if (!g.fontReady()) {
			return
		}
		for (i in 0 until client.playerCount()) {
			val player: Player? = client.player(i)
			val bot: Bot? = player?.name()?.let { name -> state.bots.firstOrNull { it.online && it.name.equals(name, ignoreCase = true) } }
			if (player == null || bot == null || !player.isDrawnThisFrame() || !client.projectActor(player, player.overheadHeight() + TAG_RISE, point)) {
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
			// squares, not circles: the HD toolkit draws a circle as a smoothed point, which many drivers drop,
			// while a filled rectangle is a quad everywhere
			g.fillRect(point[0] - DOT, point[1] - DOT, DOT * 2 + 1, DOT * 2 + 1, BLACK)
			g.fillRect(point[0] - DOT + 1, point[1] - DOT + 1, DOT * 2 - 1, DOT * 2 - 1, colour(bot.slot))
		}
		g.unclip()
	}

	/** Each bot on the world map while it is shown: its dot where the map draws its tile, its tag under. */
	private fun drawWorldMap(g: Graphics, client: ClientApi) {
		if (!client.worldMapArea(area)) {
			return
		}
		g.clip(area[0], area[1], area[2], area[3])
		g.font(Font.SMALL)
		for (bot in state.bots) {
			if (!bot.online || bot.x < 0 || !client.projectWorldMap(bot.x, bot.y, bot.level, point)) {
				continue
			}
			g.fillCircle(point[0], point[1], MAP_DOT, BLACK)
			g.fillCircle(point[0], point[1], MAP_DOT - 1, colour(bot.slot))
			g.textCentered(point[0], point[1] + MAP_DOT + MAP_LABEL_RISE, tag(bot), colour(bot.slot), BLACK)
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
	private const val STATUS_GAP: Int = 6
	/** The title band ([TOP]), the bottom edge ([EDGE]) and a pixel between. */
	private const val COLLAPSED_HEIGHT: Int = 30
	private const val COLLAPSE: Int = 2556
	private const val COLLAPSE_HOVER: Int = 2557
	private const val EXPAND: Int = 2554
	private const val EXPAND_HOVER: Int = 2555
	private const val BUTTON_SIZE: Int = 16
	private const val BUTTON_RIGHT: Int = 10
	private const val BUTTON_TOP: Int = 3
	private const val PANEL_WIDTH: Int = 250
	private const val TASK_INDENT: Int = 10
	private const val LEVEL_GAP: Int = 14
	private const val TASK_MIN_WIDTH: Int = 240

	/** The rows the task picker shows at once: its height does not follow a tab's content. */
	private const val TASK_ROWS: Int = 12
	private const val TAB_PAD: Int = 3
	private const val TAB_GAP: Int = 2
	private const val TAB_ACTIVE: Int = 0xFF5A4A32.toInt()
	private const val VIEW_MARGIN: Int = 4
	private const val SCROLL_W: Int = 12
	private const val SCROLL_GAP: Int = 4
	private const val THUMB_MIN: Int = 16
	private const val THUMB: Int = 0xFF6E5A3A.toInt()
	private const val THUMB_HOVER: Int = 0xFF8C7450.toInt()
	private const val CLOSE: Int = 831
	private const val CLOSE_HOVER: Int = 832
	private const val CLOSE_SIZE: Int = 16
	private const val CLOSE_RIGHT: Int = 10
	private const val CLOSE_TOP: Int = 3
	private const val NEEDS_TITLE: String = "Needs from your bank:"
	private const val WAGES_TITLE: String = "Paid for from its wages:"

	/** The space between the coin and its amount, and the items' worth under which the amount turns amber. */
	private const val COIN_GAP: Int = 1
	private const val WAGE_WARN_ITEMS: Int = 10
	private const val TIP_PAD: Int = 4
	private const val TIP_OFFSET: Int = 12
	private const val TIP_BACK: Int = 0xE0000000.toInt()

	// the game's stone window (the worn-equipment screen's, interface 549)
	// the stone the game's windows tile (88x60, made to repeat): the side tabs' see-through stone (1131) is one shaded
	// image and showed its edges as lines wherever it repeated
	/** The stone with its alpha scaled to [opacity] percent, built once per value asked for (each window has its own). */
	private fun stoneAt(opacity: Int): Image? {
		val made: Image? = stones[opacity]
		if (made != null) {
			return made
		}
		val client: ClientApi = context?.client() ?: return null
		val source: PluginImage = client.spriteImage(STONE) as? PluginImage ?: return null
		val pixels: IntArray = IntArray(source.pixels.size)
		for (i in pixels.indices) {
			val argb: Int = source.pixels[i]
			pixels[i] = (((argb ushr 24) * opacity / 100) shl 24) or (argb and 0xFFFFFF)
		}
		val image: Image = client.createImage(source.width, source.height, pixels) ?: return null
		stones[opacity] = image
		return image
	}

	private val stones: HashMap<Int, Image> = HashMap()

	/**
	 * The bars' icons, 12 by 12 pixel art of the plugin's own: the orbs' heart and prayer symbol (sprites 1197 and
	 * 1198) are 15 and 20 pixels and come out as a blob and a wedge scaled to a bar. Each is a mask (# for a pixel)
	 * coloured at first use: a one-pixel outline round the edge, the fill, a darker shade over the bottom-right half,
	 * and a highlight where given. The icons sit [BAR_ICON_INSET] in from the bar's left edge.
	 */
	private const val HP_ICON: Int = 0
	private const val PRAYER_ICON: Int = 1
	private const val COIN_ICON: Int = 2
	private const val BAR_ICON: Int = 12
	private const val BAR_ICON_INSET: Int = 2
	private val BAR_ICON_ART: Array<IconArt> = arrayOf(
		IconArt(
			"............",
			"..###..###..",
			".##########.",
			"############",
			"############",
			"############",
			".##########.",
			"..########..",
			"...######...",
			"....####....",
			".....##.....",
			"............",
			fill = 0xFFE81010.toInt(), shade = 0xFFA00808.toInt(), outline = 0xFF200000.toInt(), highlight = intArrayOf(3, 2, 2, 3),
		),
		IconArt(
			".....##.....",
			".....##.....",
			"....####....",
			"....####....",
			"...######...",
			".##########.",
			"############",
			"...######...",
			"....####....",
			"....####....",
			".....##.....",
			".....##.....",
			fill = 0xFFF4F4F4.toInt(), shade = 0xFFB8B8B8.toInt(), outline = 0xFF202020.toInt(),
		),
		IconArt(
			"............",
			"....####....",
			"..########..",
			".##########.",
			".##########.",
			"############",
			"############",
			".##########.",
			".##########.",
			"..########..",
			"....####....",
			"............",
			fill = 0xFFFFD700.toInt(), shade = 0xFFC08A00.toInt(), outline = 0xFF3A2400.toInt(), highlight = intArrayOf(4, 3, 3, 4, 5, 2),
		),
	)

	/** A bar icon's mask and colours; [highlight] is x, y pairs of pixels left white. */
	private class IconArt(vararg val rows: String, val fill: Int, val shade: Int, val outline: Int, val highlight: IntArray = IntArray(0)) {
		fun pixels(): IntArray {
			val size: Int = rows.size
			fun on(x: Int, y: Int): Boolean = x in 0 until size && y in 0 until size && rows[y][x] == '#'
			val out = IntArray(size * size)
			for (y in 0 until size) {
				for (x in 0 until size) {
					if (!on(x, y)) {
						continue
					}
					val edge: Boolean = !on(x + 1, y) || !on(x - 1, y) || !on(x, y + 1) || !on(x, y - 1)
					out[y * size + x] = if (edge) outline else if (x + y >= size + 1) shade else fill
				}
			}
			var i = 0
			while (i + 1 < highlight.size) {
				val x: Int = highlight[i]
				val y: Int = highlight[i + 1]
				if (on(x, y)) {
					out[y * size + x] = WHITE
				}
				i += 2
			}
			return out
		}
	}

	private val barIcons: Array<Image?> = arrayOfNulls(3)

	/** The bar icon [which] as an image, made once from its pixels. */
	private fun barIcon(which: Int): Image? {
		val made: Image? = barIcons[which]
		if (made != null) {
			return made
		}
		val image: Image? = context?.client()?.createImage(BAR_ICON, BAR_ICON, BAR_ICON_ART[which].pixels())
		barIcons[which] = image
		return image
	}

	private const val STONE: Int = 297
	private const val STONE_W: Int = 88
	private const val STONE_H: Int = 60
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
	/** The world map's dot is bigger than the minimap's, the map being coarser, and its tag sits this far under it. */
	private const val MAP_DOT: Int = 4
	private const val MAP_LABEL_RISE: Int = 11
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
	private const val COINS: Int = 0xFFFFD700.toInt()
	private const val DEAD: Int = 0xFF806060.toInt()
	private const val AWAY: Int = 0xFF909090.toInt()
	private const val FLASH: Int = 0x60FF3030
	internal const val HOVER: Int = 0x40FFFFFF
	private const val BAR_LINE: Int = 0xFF3A3630.toInt()
	internal const val ROW_BACK: Int = 0x66000000
	internal const val WINDOW_BACK: Int = 0x70000000
	private const val ROW_GAP: Int = 4
	private const val RULE: Int = 0x80FF981F.toInt()
	private const val CURRENT: Int = 0xFF8CE68C.toInt()
	private const val CURRENT_BACK: Int = 0x2A8CE68C
	private const val STATUS_GOOD: Int = 0xFF8CE68C.toInt()
	private const val STATUS_WAIT: Int = 0xFFE6C85A.toInt()
	private const val DOT_SIZE: Int = 5
	private const val DOT_GAP: Int = 4
	private const val MEMBERS_GAP: Int = 6

	private val PALETTE: IntArray = intArrayOf(0xFFFF9F1C.toInt(), 0xFF4CC9F0.toInt(), 0xFF9EF01A.toInt(), 0xFFFF5DA2.toInt())
}
