package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.InfoBox
import com.jagex.game.runetek5.plugin.api.InfoBoxPriority
import com.jagex.game.runetek5.plugin.api.Plugin
import com.jagex.game.runetek5.plugin.api.PluginApi
import com.jagex.game.runetek5.plugin.api.PluginConfig
import com.jagex.game.runetek5.plugin.api.PluginContext
import com.jagex.game.runetek5.plugin.api.PluginDescriptor
import com.jagex.game.runetek5.plugin.api.SettingsSchema
import com.jagex.game.runetek5.plugin.api.Skill
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.GameStateChanged
import com.jagex.game.runetek5.plugin.api.events.GameTick
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import com.jagex.game.runetek5.plugin.api.events.StatChanged
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: RuneLite's Boosts Information. The combat skills (and, if chosen, the
 * others) that are boosted above or drained below their level are shown as infoboxes with the skill's
 * icon, in a panel ("Attack 112/99"), or in a compact list, each in green, yellow (at most the boost
 * threshold) or red; with when the next boosted stat drops and the next drained one comes back, and a
 * notification when a boost falls to the threshold.
 *
 * The server (Void's LevelRestoration) moves every stat one level toward its base each minute (100 game
 * ticks); its Berserker curse holds a boost through one firing, which puts the next change 115 ticks after
 * the last. That stands in for RuneLite's Preserve, which 634 has not. Constitution, Prayer and Summoning
 * are points the server never restores this way, and are left out as RuneLite leaves out Hitpoints and
 * Prayer. A built-in plugin (BuiltinPlugins, docs/PLUGINS.md); the drawing is in BoostsOverlay.
 */
object Boosts : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("boosts", "Boosts Information", "1", PluginApi.VERSION, "Shows your boosted and drained combat and skill levels and the time to the next change, as a panel or infoboxes, and can warn when a boost runs low.", "Pixel")

	/** RuneLite's DisplayBoosts and DisplayChangeMode, as the settings show them. */
	@JvmField val DISPLAY_BOOSTS: Array<String> = arrayOf("None", "Combat", "Non Combat", "Both")
	@JvmField val CHANGE_MODES: Array<String> = arrayOf("Always", "Boosted", "Never")

	const val BOOSTS_NONE: Int = 0
	const val BOOSTS_COMBAT: Int = 1
	const val BOOSTS_NON_COMBAT: Int = 2
	const val BOOSTS_BOTH: Int = 3

	const val CHANGE_ALWAYS: Int = 0
	const val CHANGE_BOOSTED: Int = 1
	const val CHANGE_NEVER: Int = 2

	/** RuneLite's COMBAT set. */
	@JvmField
	val COMBAT: IntArray = intArrayOf(Skill.ATTACK, Skill.STRENGTH, Skill.DEFENCE, Skill.RANGED, Skill.MAGIC)

	/** RuneLite's SKILLING set with 634's skills: no Sailing, and Dungeoneering, which the server restores like the rest. */
	@JvmField
	val SKILLING: IntArray = intArrayOf(
		Skill.MINING, Skill.AGILITY, Skill.SMITHING, Skill.HERBLORE, Skill.FISHING, Skill.THIEVING, Skill.COOKING, Skill.CRAFTING,
		Skill.FIREMAKING, Skill.FLETCHING, Skill.WOODCUTTING, Skill.RUNECRAFTING, Skill.SLAYER, Skill.FARMING, Skill.CONSTRUCTION,
		Skill.HUNTER, Skill.DUNGEONEERING,
	)

	/** Void's restore_stats timer: a minute of game ticks; with Berserker held, a boost waits one firing more (15 ticks). */
	const val RESTORE_TICKS: Int = 100
	const val BERSERKER_TICKS: Int = 115

	/** A game tick of the server in client cycles, and a client cycle in milliseconds. */
	private const val CYCLES_PER_TICK: Int = 30
	private const val CYCLE_MILLIS: Int = 20

	/** The prayer book (0 normal, 1 curses) and the active curses (Void's prayer.varps.toml); Berserker is bit 5. */
	const val PRAYER_BOOK_VARP: Int = 1584
	const val CURSES_VARP: Int = 1582
	const val BERSERKER_BIT: Int = 5

	/** The active normal prayers; Rapid Restore (bit 6) brings a drained stat back two levels at a time. */
	const val PRAYERS_VARP: Int = 1395
	const val RAPID_RESTORE_BIT: Int = 6

	/** RuneLite's colours: drained (238, 51, 51), boosted at most the threshold (yellow), more (green). */
	const val DRAINED: Int = 0xFFEE3333.toInt()
	const val YELLOW: Int = 0xFFFFFF00.toInt()
	const val GREEN: Int = 0xFF00FF00.toInt()
	const val WHITE: Int = -0x1
	/** Java's Color.RED.brighter(). */
	const val RED_BRIGHTER: Int = 0xFFFF0000.toInt()

	@JvmField var enabled: Boolean = true
	@JvmField var displayBoosts: Int = BOOSTS_COMBAT
	@JvmField var relativeBoost: Boolean = false
	@JvmField var displayIndicators: Boolean = true
	@JvmField var displayPanel: Boolean = false
	@JvmField var compactDisplay: Boolean = false
	@JvmField var displayNextBuffChange: Int = CHANGE_BOOSTED
	@JvmField var displayNextDebuffChange: Int = CHANGE_NEVER
	@JvmField var boostThreshold: Int = 0
	@JvmField var notifyOnBoost: Boolean = false

	/** RuneLite's shownSkills and skillsToDisplay, as flags by skill. */
	@JvmField val shown: BooleanArray = BooleanArray(Skill.COUNT)
	@JvmField val toDisplay: BooleanArray = BooleanArray(Skill.COUNT)

	/** RuneLite's lastSkillLevels (-1 for not seen yet), and the client cycles (ClientApi.serverTick) of the last change each way, -1 for none. */
	private val lastLevels: IntArray = IntArray(Skill.COUNT) { -1 }
	@JvmField var lastChangeDown: Int = -1
	@JvmField var lastChangeUp: Int = -1
	@JvmField var changedDown: Boolean = false

	/** RuneLite's preserveBeenActive, for Berserker: held at some point of the current minute. */
	private var berserkerBeenActive: Boolean = false
	@JvmField var changedUp: Boolean = false

	/** The skills to display wait for the next cycle after a stat changes: the event comes before the client stores the level. */
	private var dirty: Boolean = false

	/** The notification sent, for the smoke test; the next one replaces it. With [quiet] (the smoke test) it is not shown. */
	@JvmField var lastNotification: String? = null
	@JvmField var quiet: Boolean = false

	/** The infoboxes, made once: RuneLite adds one per skill and the two timers, each deciding for itself whether it shows. */
	private val indicators: Array<BoostIndicator?> = arrayOfNulls<BoostIndicator?>(Skill.COUNT)
	private var debuffIndicator: StatChangeIndicator? = null
	private var buffIndicator: StatChangeIndicator? = null

	private var context: PluginContext? = null

	private val statListener: EventListener<StatChanged> = EventListener { event -> context?.client()?.let { statChanged(it, event) } }

	private val tickListener: EventListener<GameTick> = EventListener { _ -> context?.client()?.let { tick(it) } }

	private val stateListener: EventListener<GameStateChanged> = EventListener { event ->
		if (event.state !== GameState.LOGGED_IN) {
			// RuneLite clears the timers on the login screen and when hopping
			lastChangeDown = -1
			lastChangeUp = -1
		}
	}

	private val settingListener: EventListener<SettingChanged> = EventListener { event ->
		if (event.pluginId == DESCRIPTOR.id) {
			context?.config()?.let { readSettings(it) }
			configChanged()
		}
	}

	private val toggle: CommandHandler = CommandHandler { _, _ ->
		enabled = !enabled
		context?.config()?.setBoolean("enabled", enabled)
		context?.config()?.save()
		context?.client()?.consolePrint(if (enabled) "Boosts information enabled." else "Boosts information disabled.")
	}

	override fun start(context: PluginContext) {
		this.context = context
		val s: SettingsSchema = context.settings()
		s.toggle("enabled", "Boosts information", true)
		// RuneLite's BoostsConfig, in its order
		s.choice("displayBoosts", "Display boosts", DISPLAY_BOOSTS[BOOSTS_COMBAT], DISPLAY_BOOSTS)
		s.toggle("relativeBoost", "Show relative boosts", false)
		s.toggle("displayIndicators", "Display infoboxes", true)
		s.toggle("displayPanel", "Display panel", false)
		s.toggle("compactDisplay", "Compact display", false)
		s.choice("displayNextBuffChange", "Next buff change", CHANGE_MODES[CHANGE_BOOSTED], CHANGE_MODES)
		s.choice("displayNextDebuffChange", "Next debuff change", CHANGE_MODES[CHANGE_NEVER], CHANGE_MODES)
		s.number("boostThreshold", "Boost threshold", 0, 0, 99, 1)
		s.toggle("notifyOnBoost", "Notify on boost threshold", false)
		readSettings(context.config())
		updateShownSkills()
		lastLevels.fill(-1)
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		context.events().subscribe(Events.STAT_CHANGED, statListener)
		context.events().subscribe(Events.GAME_TICK, tickListener)
		context.events().subscribe(Events.GAME_STATE_CHANGED, stateListener)
		context.addOverlay(BoostsOverlay.panel)
		context.addOverlay(BoostsOverlay.compact)
		// RuneLite adds every infobox at start and lets each decide whether it renders
		val debuff: StatChangeIndicator = debuffIndicator ?: StatChangeIndicator(true).also { debuffIndicator = it }
		val buff: StatChangeIndicator = buffIndicator ?: StatChangeIndicator(false).also { buffIndicator = it }
		context.addInfoBox(debuff)
		context.addInfoBox(buff)
		for (skill in 0 until Skill.COUNT) {
			val indicator: BoostIndicator = indicators[skill] ?: BoostIndicator(skill).also { indicators[skill] = it }
			context.addInfoBox(indicator)
		}
		context.registerCommand("boosts", toggle)
	}

	override fun stop() {
		context = null
		reset()
	}

	/** RuneLite's shutDown: no timers, nothing to display. */
	@JvmStatic
	fun reset() {
		lastChangeDown = -1
		lastChangeUp = -1
		changedUp = false
		changedDown = false
		toDisplay.fill(false)
		lastLevels.fill(-1)
		dirty = false
		berserkerBeenActive = false
	}

	@JvmStatic
	fun context(): PluginContext? = context

	private fun choice(config: PluginConfig, key: String, options: Array<String>, default: Int): Int {
		val i: Int = options.indexOf(config.get(key, options[default]))
		return if (i < 0) default else i
	}

	@JvmStatic
	fun readSettings(config: PluginConfig) {
		enabled = config.getBoolean("enabled", true)
		displayBoosts = choice(config, "displayBoosts", DISPLAY_BOOSTS, BOOSTS_COMBAT)
		relativeBoost = config.getBoolean("relativeBoost", false)
		displayIndicators = config.getBoolean("displayIndicators", true)
		displayPanel = config.getBoolean("displayPanel", false)
		compactDisplay = config.getBoolean("compactDisplay", false)
		displayNextBuffChange = choice(config, "displayNextBuffChange", CHANGE_MODES, CHANGE_BOOSTED)
		displayNextDebuffChange = choice(config, "displayNextDebuffChange", CHANGE_MODES, CHANGE_NEVER)
		boostThreshold = config.getInt("boostThreshold", 0)
		notifyOnBoost = config.getBoolean("notifyOnBoost", false)
	}

	/** RuneLite's onConfigChanged: the skills shown again, and a timer that is never shown forgotten. */
	@JvmStatic
	fun configChanged() {
		updateShownSkills()
		if (displayNextBuffChange == CHANGE_NEVER) {
			lastChangeDown = -1
		}
		if (displayNextDebuffChange == CHANGE_NEVER) {
			lastChangeUp = -1
		}
		dirty = true
	}

	/** RuneLite's updateShownSkills. */
	@JvmStatic
	fun updateShownSkills() {
		shown.fill(false)
		if (displayBoosts == BOOSTS_COMBAT || displayBoosts == BOOSTS_BOTH) {
			for (skill in COMBAT) {
				shown[skill] = true
			}
		}
		if (displayBoosts == BOOSTS_NON_COMBAT || displayBoosts == BOOSTS_BOTH) {
			for (skill in SKILLING) {
				shown[skill] = true
			}
		}
	}

	private fun tracked(skill: Int): Boolean = COMBAT.contains(skill) || SKILLING.contains(skill)

	/**
	 * RuneLite's onStatChanged: a level one below the last is a boost wearing off, one above a drain
	 * coming back (two with Rapid Restore), each timing the next; and a boost falling to the threshold
	 * notifies. [event] has the new level; the client's own is stored after it.
	 */
	@JvmStatic
	fun statChanged(client: ClientApi, event: StatChanged) {
		val skill: Int = event.skill
		if (!enabled || skill < 0 || skill >= Skill.COUNT || !tracked(skill)) {
			return
		}
		val cur: Int = event.level
		val last: Int = lastLevels[skill]
		if (last != -1) {
			if (cur == last - 1) {
				// stat was previously boosted and has now decreased
				lastChangeDown = client.serverTick()
			}
			if (cur == last + 1 || (cur == last + 2 && rapidRestore(client))) {
				// stat was previously drained and has now increased
				lastChangeUp = client.serverTick()
			}
		}
		lastLevels[skill] = cur
		dirty = true
		if (!notifyOnBoost || last == -1) {
			return
		}
		val real: Int = client.skillBaseLevel(skill)
		val lastBoost: Int = last - real
		val boost: Int = cur - real
		if (boost <= boostThreshold && boostThreshold < lastBoost) {
			val text: String = Skill.name(skill) + " level is getting low!"
			lastNotification = text
			if (!quiet) {
				client.notify(text)
			}
		}
	}

	/** Each cycle in the game: the skills to display brought up to date, and a timer run out started again or dropped (RuneLite's onGameTick). */
	@JvmStatic
	fun tick(client: ClientApi) {
		if (dirty) {
			dirty = false
			updateBoostedStats(client)
		}
		if (changeDownTicks(client) <= 0 && lastChangeDown != -1) {
			if (displayNextBuffChange == CHANGE_ALWAYS) {
				lastChangeDown = client.serverTick()
			} else {
				lastChangeDown = -1
			}
		}
		if (changeUpTicks(client) <= 0 && lastChangeUp != -1) {
			if (displayNextDebuffChange == CHANGE_ALWAYS) {
				lastChangeUp = client.serverTick()
			} else {
				lastChangeUp = -1
			}
		}
	}

	/** RuneLite's updateBoostedStats: which shown skills differ from their level, and whether any is up or down. */
	@JvmStatic
	fun updateBoostedStats(client: ClientApi) {
		changedUp = false
		changedDown = false
		for (skill in 0 until Skill.COUNT) {
			toDisplay[skill] = false
			if (!shown[skill]) {
				continue
			}
			val boosted: Int = client.skillLevel(skill)
			val base: Int = client.skillBaseLevel(skill)
			if (boosted > base) {
				changedUp = true
			} else if (boosted < base) {
				changedDown = true
			}
			if (boosted != base) {
				toDisplay[skill] = true
			}
		}
	}

	@JvmStatic
	fun berserker(client: ClientApi): Boolean {
		return client.varp(PRAYER_BOOK_VARP) == 1 && (client.varp(CURSES_VARP) ushr BERSERKER_BIT) and 1 == 1
	}

	private fun rapidRestore(client: ClientApi): Boolean {
		return client.varp(PRAYER_BOOK_VARP) == 0 && (client.varp(PRAYERS_VARP) ushr RAPID_RESTORE_BIT) and 1 == 1
	}

	/** Game ticks since a change at client cycle [at]. */
	private fun ticksSince(client: ClientApi, at: Int): Int = (client.serverTick() - at) / CYCLES_PER_TICK

	/**
	 * RuneLite's getChangeDownTicks: the game ticks until the next boosted stat drops, or -1 when not shown.
	 * Void's Berserker holds the boosts through the firing at 100 ticks and drops them 15 later: held up to
	 * that firing (or since it), it puts the drop at 115, as RuneLite's Preserve puts it at 150; a firing at
	 * 100 that went by without a drop was Berserker's too.
	 */
	@JvmStatic
	fun changeDownTicks(client: ClientApi): Int {
		if (lastChangeDown == -1 || displayNextBuffChange == CHANGE_NEVER || (displayNextBuffChange == CHANGE_BOOSTED && !changedUp)) {
			return -1
		}
		val since: Int = ticksSince(client, lastChangeDown)
		if ((berserker(client) && (since < RESTORE_TICKS || berserkerBeenActive)) || since > RESTORE_TICKS) {
			berserkerBeenActive = true
			return BERSERKER_TICKS - since
		}
		berserkerBeenActive = false
		return RESTORE_TICKS - since
	}

	/**
	 * RuneLite's getChangeUpTicks: the game ticks until the next drained stat comes back, or -1 when not
	 * shown. With Berserker the server's timer fires 15 ticks after each restore without restoring, so the
	 * next is 115 ticks on.
	 */
	@JvmStatic
	fun changeUpTicks(client: ClientApi): Int {
		if (lastChangeUp == -1 || displayNextDebuffChange == CHANGE_NEVER || (displayNextDebuffChange == CHANGE_BOOSTED && !changedDown)) {
			return -1
		}
		val since: Int = ticksSince(client, lastChangeUp)
		return (if (berserker(client)) BERSERKER_TICKS else RESTORE_TICKS) - since
	}

	/** RuneLite's getChangeTime: [ticks] as whole seconds, less what has passed of the current tick; -1 stays -1. */
	@JvmStatic
	fun changeTime(client: ClientApi, ticks: Int, lastChange: Int): Int {
		if (ticks == -1) {
			return -1
		}
		val intoTick: Int = (client.serverTick() - lastChange) % CYCLES_PER_TICK
		return (ticks * CYCLES_PER_TICK * CYCLE_MILLIS - intoTick * CYCLE_MILLIS) / 1000
	}

	@JvmStatic
	fun changeDownSeconds(client: ClientApi): Int = changeTime(client, changeDownTicks(client), lastChangeDown)

	@JvmStatic
	fun changeUpSeconds(client: ClientApi): Int = changeTime(client, changeUpTicks(client), lastChangeUp)

	/** RuneLite's getTextColor for a boost of [boost] levels. */
	@JvmStatic
	fun colour(boost: Int): Int {
		if (boost < 0) {
			return DRAINED
		}
		return if (boost <= boostThreshold) YELLOW else GREEN
	}

	/** A boost as relative text: "+3", "-2", "0". */
	@JvmStatic
	fun relative(boost: Int): String = if (boost > 0) "+" + boost else boost.toString()

	/** The time text colour: red for the last ten ticks. */
	@JvmStatic
	fun timeColour(ticks: Int): Int = if (ticks < 10) RED_BRIGHTER else WHITE

	/** Whether the boosts show at all now. */
	@JvmStatic
	fun active(client: ClientApi): Boolean = enabled && client.gameState() === GameState.LOGGED_IN

	/** RuneLite's BoostIndicator: a skill's icon and its boosted level (or the boost) while it is one to display. */
	class BoostIndicator(@JvmField val skill: Int) : InfoBox("Boost " + Skill.name(skill)) {
		init {
			spriteId = SkillSprites.ICONS[skill]
			tooltip = Skill.name(skill) + " boost"
			priority = InfoBoxPriority.HIGH
		}

		override fun text(): String? {
			val client: ClientApi = context?.client() ?: return null
			if (!relativeBoost) {
				return client.skillLevel(skill).toString()
			}
			return relative(client.skillLevel(skill) - client.skillBaseLevel(skill))
		}

		override fun textColour(): Int {
			val client: ClientApi = context?.client() ?: return WHITE
			val boosted: Int = client.skillLevel(skill)
			val base: Int = client.skillBaseLevel(skill)
			if (boosted < base) {
				return DRAINED
			}
			return if (boosted - base <= boostThreshold) YELLOW else GREEN
		}

		override fun render(): Boolean {
			val client: ClientApi = context?.client() ?: return false
			return active(client) && toDisplay[skill] && displayIndicators
		}
	}

	/** RuneLite's StatChangeIndicator: the seconds to the next drained stat's return ([up]) or boosted stat's drop. */
	class StatChangeIndicator(@JvmField val up: Boolean) : InfoBox("BoostsPlugin_StatChangeIndicator") {
		init {
			image = if (up) BoostImages.debuffed() else BoostImages.buffed()
			tooltip = if (up) "Next debuff change" else "Next buff change"
			priority = InfoBoxPriority.MED
		}

		private fun ticks(client: ClientApi): Int = if (up) changeUpTicks(client) else changeDownTicks(client)

		override fun text(): String? {
			val client: ClientApi = context?.client() ?: return null
			val seconds: Int = if (up) changeUpSeconds(client) else changeDownSeconds(client)
			return (if (seconds in 0..9) "0" else "") + seconds
		}

		override fun textColour(): Int {
			val client: ClientApi = context?.client() ?: return WHITE
			return timeColour(ticks(client))
		}

		override fun render(): Boolean {
			val client: ClientApi = context?.client() ?: return false
			return active(client) && ticks(client) != -1 && displayIndicators
		}
	}
}
