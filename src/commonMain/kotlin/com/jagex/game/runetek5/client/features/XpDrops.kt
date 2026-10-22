package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.MenuAction
import com.jagex.game.runetek5.plugin.api.MenuTarget
import com.jagex.game.runetek5.plugin.api.Overlay
import com.jagex.game.runetek5.plugin.api.Plugin
import com.jagex.game.runetek5.plugin.api.PluginApi
import com.jagex.game.runetek5.plugin.api.PluginConfig
import com.jagex.game.runetek5.plugin.api.PluginContext
import com.jagex.game.runetek5.plugin.api.PluginDescriptor
import com.jagex.game.runetek5.plugin.api.SettingsSchema
import com.jagex.game.runetek5.plugin.api.Skill
import com.jagex.game.runetek5.plugin.api.Widget
import com.jagex.game.runetek5.plugin.api.events.ClientTick
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.GameStateChanged
import com.jagex.game.runetek5.plugin.api.events.MenuEntryAdded
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import com.jagex.game.runetek5.plugin.api.events.StatChanged
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic
import kotlin.math.pow

/**
 * Not part of the original client: XP drops as Old School RuneScape draws them, with RuneLite's XP Drop
 * plugin on top. RuneLite's plugin restyles the game's own drops, which 634 does not have, so the drops
 * themselves are made here, with the game's XP drop setup as settings: where they are (right by the
 * minimap, middle, left), their size, how long they stay and how fast they rise, their colour, whether
 * the skills gained in one tick share a drop, and an XP counter and progress bar for them to rise into.
 * RuneLite's options come on top: no skill icons, a standard colour of your own, and a colour for a drop
 * of a combat skill while a prayer of its kind is on (melee, ranged, magic: RuneLite's prayer colours,
 * 634's prayers and curses), and the last drop shown again after some ticks without one (RuneLite's fake
 * XP drops). Right-click the XP button by the minimap for Setup, which opens these settings.
 *
 * A built-in plugin (BuiltinPlugins, docs/PLUGINS.md); the drawing is in XpDropsOverlay.
 */
object XpDrops : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("xpdrops", "XP Drops", "2", PluginApi.VERSION, "Shows experience drops the Old School way, with adjustable position, size, speed and colour, grouping, a counter and a progress bar (set up from the XP button).", "Pixel")

	// OSRS's XP drop setup, its choices in its words
	@JvmField val POSITIONS: Array<String> = arrayOf("Right", "Middle", "Left")
	@JvmField val SIZES: Array<String> = arrayOf("Smallest", "Small", "Medium", "Large", "Largest")
	@JvmField val DURATIONS: Array<String> = arrayOf("Shortest", "Short", "Medium", "Long", "Longest")
	@JvmField val SPEEDS: Array<String> = arrayOf("Slower", "Default", "Faster")
	@JvmField val COLOUR_NAMES: Array<String> = arrayOf("White", "Lilac", "Cyan", "Jade", "Lime", "Yellow", "Orange", "Pink")
	@JvmField val COLOUR_VALUES: IntArray = intArrayOf(0xFFFFFF, 0xC8A2FF, 0x00FFFF, 0x00C878, 0x9BFF00, 0xFFFF00, 0xFF981F, 0xFF64DC)
	@JvmField val GROUPS: Array<String> = arrayOf("Individually", "Group drops")
	@JvmField val COUNTERS: Array<String> = arrayOf("None", "Total XP", "Most recent")
	@JvmField val PROGRESS_BARS: Array<String> = arrayOf("None", "Most recent") + Skill.NAMES

	const val POSITION_RIGHT: Int = 0
	const val POSITION_MIDDLE: Int = 1
	const val POSITION_LEFT: Int = 2

	const val COUNTER_NONE: Int = 0
	const val COUNTER_TOTAL: Int = 1
	const val COUNTER_RECENT: Int = 2

	/** How many client cycles (20 ms) a drop is up, by duration. */
	@JvmField val LIFETIMES: IntArray = intArrayOf(50, 75, 100, 150, 200)

	/** How far a drop rises over its life, in pixels, by speed. */
	@JvmField val RISES: IntArray = intArrayOf(30, 45, 70)

	/** Client cycles in a server tick: drops in one tick are grouped. */
	const val CYCLES_PER_TICK: Int = 30

	/** RuneLite's prayer colours' default, #1580AD. */
	const val PRAYER_DEFAULT: Int = 0xFF1580AD.toInt()

	const val TYPE_NONE: Int = 0
	const val TYPE_MELEE: Int = 1
	const val TYPE_RANGE: Int = 2
	const val TYPE_MAGIC: Int = 3

	/** 634's prayer book (Void: varp 1584, 0 the normal prayers, 1 the curses) and the active prayers of each (varps 1395 and 1582). */
	const val PRAYER_BOOK_VARP: Int = 1584
	const val PRAYERS_VARP: Int = 1395
	const val CURSES_VARP: Int = 1582

	/** The bits of varp 1395 by kind (Void's activated_prayers order). */
	private val MELEE_PRAYERS: IntArray = intArrayOf(1, 2, 4, 5, 10, 11, 25, 26) // burst of strength, clarity of thought, superhuman strength, improved reflexes, ultimate strength, incredible reflexes, chivalry, piety
	private val RANGE_PRAYERS: IntArray = intArrayOf(18, 20, 22, 29) // sharp eye, hawk eye, eagle eye, rigour
	private val MAGIC_PRAYERS: IntArray = intArrayOf(19, 21, 23, 28) // mystic will, mystic lore, mystic might, augury

	/** The bits of varp 1582 by kind (Void's activated_curses order). */
	private val MELEE_CURSES: IntArray = intArrayOf(10, 14, 19) // leech attack, leech strength, turmoil
	private val RANGE_CURSES: IntArray = intArrayOf(11) // leech ranged
	private val MAGIC_CURSES: IntArray = intArrayOf(12) // leech magic

	/** The XP button's own option, how its component is known in either game frame. */
	const val XP_BUTTON_OPTION: String = "Reset XP Total"

	private const val MAX_DROPS: Int = 12

	/** One drop: its skills (one, or several grouped), the experience, when it came and its colour. */
	class Drop(@JvmField val skills: ArrayList<Int>, @JvmField var amount: Int, @JvmField val cycle: Int, @JvmField var colour: Int)

	@JvmField var enabled: Boolean = true
	@JvmField var position: Int = POSITION_RIGHT
	@JvmField var size: Int = 2
	@JvmField var duration: Int = 2
	@JvmField var speed: Int = 1
	@JvmField var colour: Int = 0
	@JvmField var group: Boolean = false
	@JvmField var counter: Int = COUNTER_NONE
	@JvmField var progressBar: Int = 0
	@JvmField var hideSkillIcons: Boolean = false
	@JvmField var standardColour: Int? = null
	@JvmField var meleePrayerColour: Int = PRAYER_DEFAULT
	@JvmField var rangePrayerColour: Int = PRAYER_DEFAULT
	@JvmField var magePrayerColour: Int = PRAYER_DEFAULT
	@JvmField var fakeXpDropDelay: Int = 0

	/** The drops up now, oldest first. */
	@JvmField
	val drops: ArrayList<Drop> = ArrayList()

	/** Each skill's last experience (-1 before its first update since login, which only primes it). */
	@JvmField
	val lastXp: IntArray = IntArray(Skill.COUNT) { -1 }

	/** The skill gained last, and by how much: the counter's "Most recent" and RuneLite's fake drops. */
	@JvmField var lastSkill: Int = -1
	@JvmField var previousExpGained: Int = 0

	/** The client cycle of the last drop, real or fake, for RuneLite's fake drop delay. */
	private var lastDropCycle: Int = 0

	/** The drops drawn last frame and the newest drop's text, for the smoke test. */
	@JvmField var drawnLastFrame: Int = 0
	@JvmField var lastDropText: String? = null

	private var context: PluginContext? = null

	private var setupAction: Int = -1

	private val overlay: Overlay = Overlay { g, view -> XpDropsOverlay.draw(g, view) }

	private val statListener: EventListener<StatChanged> = EventListener { event -> statUpdated(context?.client(), event.skill, event.oldXp, event.newXp) }

	private val tickListener: EventListener<ClientTick> = EventListener { event -> context?.client()?.let { cycle(it, event.cycle) } }

	private val stateListener: EventListener<GameStateChanged> = EventListener { event ->
		if (event.previous === GameState.LOGGED_IN) {
			reset()
		}
	}

	private val settingListener: EventListener<SettingChanged> = EventListener { event ->
		if (event.pluginId == DESCRIPTOR.id) {
			context?.config()?.let { standardColourPicked(it, event.key); readSettings(it) }
		}
	}

	private val menuListener: EventListener<MenuEntryAdded> = EventListener { event -> context?.client()?.let { menuEntryAdded(it, event) } }

	private val toggle: CommandHandler = CommandHandler { _, _ ->
		enabled = !enabled
		context?.config()?.setBoolean("enabled", enabled)
		context?.config()?.save()
		context?.client()?.consolePrint(if (enabled) "XP drops enabled." else "XP drops disabled.")
	}

	override fun start(context: PluginContext) {
		this.context = context
		declareSettings(context.settings())
		readSettings(context.config())
		setupAction = context.registerMenuAction(MenuAction { _, _, _, _ -> PluginManagerScreen.openSettings(DESCRIPTOR.id)?.let { context.client().consolePrint(it) } })
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		context.events().subscribe(Events.STAT_CHANGED, statListener)
		context.events().subscribe(Events.CLIENT_TICK, tickListener)
		context.events().subscribe(Events.GAME_STATE_CHANGED, stateListener)
		context.events().subscribe(Events.MENU_ENTRY_ADDED, menuListener)
		context.addOverlay(overlay)
		context.registerCommand("xpdrops", toggle)
	}

	override fun stop() {
		context = null
		reset()
	}

	private fun declareSettings(s: SettingsSchema) {
		s.toggle("enabled", "XP drops", true)
		// OSRS's XP drop setup
		s.choice("position", "Position", POSITIONS[POSITION_RIGHT], POSITIONS)
		s.choice("size", "Size", SIZES[2], SIZES)
		s.choice("duration", "Duration", DURATIONS[2], DURATIONS)
		s.choice("speed", "Speed", SPEEDS[1], SPEEDS)
		s.choice("colour", "Colour", COLOUR_NAMES[0], COLOUR_NAMES)
		s.choice("group", "Group", GROUPS[0], GROUPS)
		s.choice("counter", "Counter", COUNTERS[COUNTER_NONE], COUNTERS)
		s.choice("progressBar", "Progress bar", PROGRESS_BARS[0], PROGRESS_BARS)
		// RuneLite's XP Drop
		s.toggle("hideSkillIcons", "Hide skill icons", false)
		s.toggle("useStandardColor", "Use a standard colour", false)
		s.colour("standardColor", "Standard colour", -0x1, false)
		s.colour("meleePrayerColor", "Melee prayer colour", PRAYER_DEFAULT, false)
		s.colour("rangePrayerColor", "Range prayer colour", PRAYER_DEFAULT, false)
		s.colour("magePrayerColor", "Mage prayer colour", PRAYER_DEFAULT, false)
		s.number("fakeXpDropDelay", "Fake XP drop delay (ticks)", 0, 0, 100, 1)
	}

	private fun choice(config: PluginConfig, key: String, options: Array<String>, default: Int): Int {
		val i: Int = options.indexOf(config.get(key, options[default]))
		return if (i < 0) default else i
	}

	/** Picking a standard colour turns it on, as setting RuneLite's (an empty setting there) does; the switch still turns it off. */
	@JvmStatic
	fun standardColourPicked(config: PluginConfig, key: String) {
		if (key == "standardColor" && !config.getBoolean("useStandardColor", false)) {
			config.setBoolean("useStandardColor", true)
			config.save()
		}
	}

	@JvmStatic
	fun readSettings(config: PluginConfig) {
		enabled = config.getBoolean("enabled", true)
		position = choice(config, "position", POSITIONS, POSITION_RIGHT)
		size = choice(config, "size", SIZES, 2)
		duration = choice(config, "duration", DURATIONS, 2)
		speed = choice(config, "speed", SPEEDS, 1)
		colour = choice(config, "colour", COLOUR_NAMES, 0)
		group = choice(config, "group", GROUPS, 0) == 1
		counter = choice(config, "counter", COUNTERS, COUNTER_NONE)
		progressBar = choice(config, "progressBar", PROGRESS_BARS, 0)
		hideSkillIcons = config.getBoolean("hideSkillIcons", false)
		standardColour = if (config.getBoolean("useStandardColor", false)) config.getColour("standardColor", -0x1) else null
		meleePrayerColour = config.getColour("meleePrayerColor", PRAYER_DEFAULT)
		rangePrayerColour = config.getColour("rangePrayerColor", PRAYER_DEFAULT)
		magePrayerColour = config.getColour("magePrayerColor", PRAYER_DEFAULT)
		fakeXpDropDelay = config.getInt("fakeXpDropDelay", 0)
	}

	/** Forgets every skill's experience and the drops up, at logout. */
	@JvmStatic
	fun reset() {
		lastXp.fill(-1)
		drops.clear()
		lastDropText = null
		lastSkill = -1
		previousExpGained = 0
	}

	@JvmStatic
	fun dropCount(): Int = drops.size

	@JvmStatic
	fun context(): PluginContext? = context

	// --- drops ---

	/** A skill's experience from the server: after its first update since login, a gain is a drop. */
	@JvmStatic
	fun statUpdated(client: ClientApi?, skill: Int, oldXp: Int, newXp: Int) {
		if (skill < 0 || skill >= Skill.COUNT) {
			return
		}
		val previous: Int = lastXp[skill]
		lastXp[skill] = newXp
		if (previous < 0) {
			return // the login's own update: no drop
		}
		val gained: Int = newXp - maxOf(previous, oldXp)
		if (!enabled || gained <= 0) {
			return
		}
		lastSkill = skill
		previousExpGained = gained
		addDrop(client, skill, gained, client?.clientCycle() ?: 0)
	}

	/** A drop of [amount] in [skill]: into this tick's drop when grouping, else a drop of its own. */
	@JvmStatic
	fun addDrop(client: ClientApi?, skill: Int, amount: Int, now: Int) {
		val newest: Drop? = drops.lastOrNull()
		if (group && newest != null && now - newest.cycle < CYCLES_PER_TICK) {
			if (!newest.skills.contains(skill)) {
				newest.skills.add(skill)
			}
			newest.amount += amount
			newest.colour = colourOf(client, newest.skills)
		} else {
			if (drops.size >= MAX_DROPS) {
				drops.removeAt(0)
			}
			val skills: ArrayList<Int> = arrayListOf(skill)
			drops.add(Drop(skills, amount, now, colourOf(client, skills)))
		}
		lastDropCycle = now
		lastDropText = "+" + GroundItemRules.withCommas(drops.last().amount.toLong())
	}

	/** Each cycle: expired drops go; RuneLite's fake drop when its delay has passed without one. */
	@JvmStatic
	fun cycle(client: ClientApi, now: Int) {
		val life: Int = LIFETIMES[duration]
		drops.removeAll { now - it.cycle >= life }
		if (!enabled || fakeXpDropDelay <= 0 || lastSkill < 0 || client.gameState() !== GameState.LOGGED_IN) {
			return
		}
		if (now - lastDropCycle >= fakeXpDropDelay * CYCLES_PER_TICK) {
			addDrop(client, lastSkill, previousExpGained, now)
		}
	}

	// --- colours ---

	/** The kind of combat a drop's skills are (RuneLite's order: Ranged, then Magic, then Attack, Strength or Defence). */
	@JvmStatic
	fun dropType(skills: List<Int>): Int {
		if (skills.contains(Skill.RANGED)) {
			return TYPE_RANGE
		}
		if (skills.contains(Skill.MAGIC)) {
			return TYPE_MAGIC
		}
		if (skills.contains(Skill.ATTACK) || skills.contains(Skill.STRENGTH) || skills.contains(Skill.DEFENCE)) {
			return TYPE_MELEE
		}
		return TYPE_NONE
	}

	/** Whether a prayer (or curse) of [type] is on now. */
	@JvmStatic
	fun prayerActive(client: ClientApi?, type: Int): Boolean {
		if (client == null || type == TYPE_NONE) {
			return false
		}
		val curses: Boolean = client.varp(PRAYER_BOOK_VARP) == 1
		val active: Int = client.varp(if (curses) CURSES_VARP else PRAYERS_VARP)
		val bits: IntArray = when (type) {
			TYPE_MELEE -> if (curses) MELEE_CURSES else MELEE_PRAYERS
			TYPE_RANGE -> if (curses) RANGE_CURSES else RANGE_PRAYERS
			else -> if (curses) MAGIC_CURSES else MAGIC_PRAYERS
		}
		for (bit in bits) {
			if (active and (1 shl bit) != 0) {
				return true
			}
		}
		return false
	}

	/** A drop's colour as RuneLite's processXpDrop picks it: its prayer's colour, else the standard colour, else the setup's colour. */
	@JvmStatic
	fun colourOf(client: ClientApi?, skills: List<Int>): Int {
		val type: Int = dropType(skills)
		if (prayerActive(client, type)) {
			return when (type) {
				TYPE_RANGE -> rangePrayerColour
				TYPE_MAGIC -> magePrayerColour
				else -> meleePrayerColour
			}
		}
		return standardColour ?: (-0x1000000 or COLOUR_VALUES[colour])
	}

	// --- the XP button ---

	/** Right-click on the XP button by the minimap: Setup, as Old School's XP drop setup comes from its orb. */
	private fun menuEntryAdded(client: ClientApi, event: MenuEntryAdded) {
		if (event.target !== MenuTarget.WIDGET) {
			return
		}
		val top: Int = event.widgetId ushr 16
		if (top != 548 && top != 746) {
			return
		}
		val widget: Widget = client.widgetById(event.widgetId) ?: return
		for (i in 0 until widget.actionCount()) {
			if (widget.action(i) == XP_BUTTON_OPTION) {
				client.addMenuEntry("Setup <col=ff9040>XP drops", setupAction)
				return
			}
		}
	}

	// --- experience levels, for the progress bar ---

	private val XP_TABLE: IntArray = buildTable()

	private fun buildTable(): IntArray {
		val table: IntArray = IntArray(127)
		var points: Int = 0
		for (level in 1 until 126) {
			points += kotlin.math.floor(level + 300.0 * 2.0.pow(level / 7.0)).toInt()
			table[level + 1] = points / 4
		}
		return table
	}

	@JvmStatic
	fun xpForLevel(level: Int): Int = XP_TABLE[level.coerceIn(1, 126)]

	@JvmStatic
	fun levelForXp(xp: Int): Int {
		var level: Int = 1
		while (level < 126 && XP_TABLE[level + 1] <= xp) {
			level++
		}
		return level
	}
}
