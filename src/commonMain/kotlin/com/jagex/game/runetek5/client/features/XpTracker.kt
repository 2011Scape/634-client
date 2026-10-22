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
import com.jagex.game.runetek5.plugin.api.Skill
import com.jagex.game.runetek5.plugin.api.events.ClientTick
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.GameStateChanged
import com.jagex.game.runetek5.plugin.api.events.MenuEntryAdded
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import com.jagex.game.runetek5.plugin.api.events.StatChanged
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: RuneLite's XP Tracker. Each skill trained this session is tracked
 * as RuneLite tracks it (XpTrackerState): experience gained, per hour, left to the next level, actions
 * done, left and per hour, and the time to level; with the total over every skill. Its window
 * (XpTrackerScreen: `::xp`, or Open in the plugin manager) is RuneLite's side panel, a box a skill with
 * RuneLite's menu (Reset, Reset others, Reset/hr, Pause, Add to canvas); a skill added to the canvas is
 * a small box on the game view (XpTrackerOverlay), as RuneLite's XP info box; the Stats tab's skills
 * have Add to canvas in their menus too. Skills pause by hand, at logout and after a time without
 * experience, and the tracking is kept between sessions per account.
 *
 * A built-in plugin (BuiltinPlugins, docs/PLUGINS.md).
 */
object XpTracker : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("xptracker", "XP Tracker", "1", PluginApi.VERSION, "RuneLite's XP Tracker: experience gained and per hour, actions and time to level for every skill you train, in a window of its own (::xp) and as boxes on the game view.", "Pixel")

	/** RuneLite's XpPanelLabel: what a label shows. */
	@JvmField
	val LABELS: Array<String> = arrayOf("TTL", "XP Gained", "XP/hr", "XP Left", "Actions", "Actions/hr", "Actions Done")

	const val LABEL_TTL: Int = 0
	const val LABEL_XP_GAINED: Int = 1
	const val LABEL_XP_HOUR: Int = 2
	const val LABEL_XP_LEFT: Int = 3
	const val LABEL_ACTIONS_LEFT: Int = 4
	const val LABEL_ACTIONS_HOUR: Int = 5
	const val LABEL_ACTIONS_DONE: Int = 6

	/** RuneLite's XpProgressBarLabel. */
	@JvmField
	val BAR_LABELS: Array<String> = arrayOf("Percentage", "Time to level", "Hours to level")

	const val BAR_PERCENTAGE: Int = 0
	const val BAR_TIME_TO_LEVEL: Int = 1
	const val BAR_HOURS_TO_LEVEL: Int = 2

	/** RuneLite's Experience.MAX_REAL_LEVEL, for Hide maxed skills. */
	private const val MAX_REAL_LEVEL: Int = 99

	/** The Stats tab and its skills' components (Void's skill.ifaces.toml, [stats] 320). */
	const val STATS_TAB: Int = 320

	private val STATS_COMPONENTS: IntArray = intArrayOf(
		200, 28, 11, 193, 52, 76, 93, 68, 165, 101, 44, 172, 84, 179, 186, 36, 19, 60, 118, 126, 110, 142, 134, 150, 158,
	)

	/** The skills' icons, the Stats tab's sprites, by skill. */
	@JvmField
	val ICONS: IntArray = intArrayOf(197, 199, 198, 203, 200, 201, 202, 212, 214, 208, 211, 213, 207, 210, 209, 205, 204, 206, 216, 217, 215, 220, 221, 222, 3032)

	/** RuneLite's SkillColor, by skill; Summoning and Dungeoneering, which it lacks, in their own colours. */
	@JvmField
	val COLOURS: IntArray = intArrayOf(
		0x9b2007, 0x6277be, 0x04955a, 0x837e7e, 0x6d9017, 0x9f9323, 0x3250c1, 0x702386, 0x348c25, 0x038d7d, 0x6a84a4, 0xbd7819,
		0x976e4d, 0x6c6b52, 0x5d8fa7, 0x078509, 0x3a3c89, 0x6c3457, 0x646464, 0x65983f, 0xaa8d1a, 0x5c5941, 0x82745f, 0x3c9999, 0x8a4b2a,
	)

	/** The total's index beside the skills'. */
	const val OVERALL: Int = Skill.COUNT

	/** Server ticks after login before experience counts: the login sends every skill's experience (RuneLite's initializeTracker). */
	private const val INITIALIZE_CYCLES: Int = 60

	private const val SECOND: Long = 1000L

	private const val SAVE_EVERY: Long = 60_000L

	@JvmField var enabled: Boolean = true
	@JvmField var saveState: Boolean = true
	@JvmField var hideMaxed: Boolean = false
	@JvmField var pauseOnLogout: Boolean = true
	@JvmField var intermediateLevelMarkers: Boolean = false
	@JvmField var pauseSkillAfter: Int = 0
	@JvmField var resetSkillRateAfter: Int = 0
	@JvmField var skillTabOverlayMenuOptions: Boolean = true
	@JvmField var onScreenDisplayMode: Int = LABEL_XP_GAINED
	@JvmField var onScreenDisplayModeBottom: Int = LABEL_XP_HOUR
	@JvmField val panelLabels: IntArray = intArrayOf(LABEL_XP_GAINED, LABEL_XP_LEFT, LABEL_XP_HOUR, LABEL_ACTIONS_LEFT)
	@JvmField var progressBarLabel: Int = BAR_PERCENTAGE
	@JvmField var progressBarTooltipLabel: Int = BAR_TIME_TO_LEVEL
	@JvmField var prioritizeRecentXpSkills: Boolean = false

	/** Each skill's tracking (null until its first update), then the total's. */
	@JvmField
	val states: Array<XpTrackerState?> = arrayOfNulls<XpTrackerState?>(Skill.COUNT + 1)

	/** The skills the window lists, in the order they were first trained (or last, with prioritizeRecentXpSkills). */
	@JvmField
	val order: ArrayList<Int> = ArrayList()

	/** The skills on the canvas, in the order added. */
	@JvmField
	val canvas: ArrayList<Int> = ArrayList()

	private var context: PluginContext? = null

	private var loginCycle: Int = -1

	private var initialized: Boolean = false

	private var profile: String? = null

	private var lastSecond: Long = 0L

	private var lastSave: Long = 0L

	private var canvasAction: Int = -1

	private val overlay: Overlay = Overlay { g, view -> XpTrackerOverlay.draw(g, view) }

	private val settingListener: EventListener<SettingChanged> = EventListener { event ->
		if (event.pluginId == DESCRIPTOR.id) {
			context?.config()?.let { readSettings(it) }
			XpTrackerScreen.refresh()
		}
	}

	private val statListener: EventListener<StatChanged> = EventListener { event -> context?.client()?.let { statChanged(it, event) } }

	private val tickListener: EventListener<ClientTick> = EventListener { event -> context?.client()?.let { cycle(it, event.cycle) } }

	private val stateListener: EventListener<GameStateChanged> = EventListener { event -> context?.client()?.let { stateChanged(it, event.state) } }

	private val menuListener: EventListener<MenuEntryAdded> = EventListener { event -> context?.client()?.let { menuEntryAdded(it, event) } }

	private val command: CommandHandler = CommandHandler { _, args ->
		val context: PluginContext = this.context ?: return@CommandHandler
		if (args.isNotEmpty() && args[0].equals("toggle", ignoreCase = true)) {
			enabled = !enabled
			context.config().setBoolean("enabled", enabled)
			context.config().save()
			context.client().consolePrint(if (enabled) "XP tracker on." else "XP tracker off.")
			return@CommandHandler
		}
		XpTrackerScreen.toggle()?.let { context.client().consolePrint(it) }
	}

	override fun start(context: PluginContext) {
		this.context = context
		val s = context.settings()
		s.toggle("enabled", "XP tracker", true)
		s.toggle("saveState", "Save between sessions", true)
		s.toggle("hideMaxed", "Hide maxed skills", false)
		s.toggle("logoutPausing", "Pause on logout", true)
		s.toggle("intermediateLevelMarkers", "Show intermediate level markers", false)
		s.number("pauseSkillAfter", "Auto pause after (minutes)", 0, 0, 1440, 1)
		s.number("resetSkillRateAfter", "Auto reset after (minutes)", 0, 0, 1440, 1)
		s.toggle("skillTabOverlayMenuOptions", "Add skill tab canvas menu option", true)
		s.choice("onScreenDisplayMode", "On-screen tracker display mode (top)", LABELS[LABEL_XP_GAINED], LABELS)
		s.choice("onScreenDisplayModeBottom", "On-screen tracker display mode (bottom)", LABELS[LABEL_XP_HOUR], LABELS)
		s.choice("xpPanelLabel1", "Top-left XP info label", LABELS[LABEL_XP_GAINED], LABELS)
		s.choice("xpPanelLabel2", "Top-right XP info label", LABELS[LABEL_XP_LEFT], LABELS)
		s.choice("xpPanelLabel3", "Bottom-left XP info label", LABELS[LABEL_XP_HOUR], LABELS)
		s.choice("xpPanelLabel4", "Bottom-right XP info label", LABELS[LABEL_ACTIONS_LEFT], LABELS)
		s.choice("progressBarLabel", "Progress bar label", BAR_LABELS[BAR_PERCENTAGE], BAR_LABELS)
		s.choice("progressBarTooltipLabel", "Tooltip label", BAR_LABELS[BAR_TIME_TO_LEVEL], BAR_LABELS)
		s.toggle("prioritizeRecentXpSkills", "Move recently trained skills to top", false)
		readSettings(context.config())
		canvasAction = context.registerMenuAction(MenuAction { _, _, widgetId, _ -> skillOfComponent(widgetId).let { if (it >= 0) toggleCanvas(it) } })
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		context.events().subscribe(Events.STAT_CHANGED, statListener)
		context.events().subscribe(Events.CLIENT_TICK, tickListener)
		context.events().subscribe(Events.GAME_STATE_CHANGED, stateListener)
		context.events().subscribe(Events.MENU_ENTRY_ADDED, menuListener)
		context.addOverlay(overlay)
		context.registerCommand("xp", command)
		PluginManagerScreen.registerPanel(DESCRIPTOR.id) { XpTrackerScreen.open() }
		if (context.client().gameState() === GameState.LOGGED_IN) {
			stateChanged(context.client(), GameState.LOGGED_IN)
		}
	}

	override fun stop() {
		PluginManagerScreen.unregisterPanel(DESCRIPTOR.id)
		XpTrackerScreen.close()
		save()
		context = null
		clear()
		profile = null
	}

	private fun choice(config: PluginConfig, key: String, options: Array<String>, default: Int): Int {
		val i: Int = options.indexOf(config.get(key, options[default]))
		return if (i < 0) default else i
	}

	@JvmStatic
	fun readSettings(config: PluginConfig) {
		enabled = config.getBoolean("enabled", true)
		saveState = config.getBoolean("saveState", true)
		hideMaxed = config.getBoolean("hideMaxed", false)
		pauseOnLogout = config.getBoolean("logoutPausing", true)
		intermediateLevelMarkers = config.getBoolean("intermediateLevelMarkers", false)
		pauseSkillAfter = config.getInt("pauseSkillAfter", 0)
		resetSkillRateAfter = config.getInt("resetSkillRateAfter", 0)
		skillTabOverlayMenuOptions = config.getBoolean("skillTabOverlayMenuOptions", true)
		onScreenDisplayMode = choice(config, "onScreenDisplayMode", LABELS, LABEL_XP_GAINED)
		onScreenDisplayModeBottom = choice(config, "onScreenDisplayModeBottom", LABELS, LABEL_XP_HOUR)
		panelLabels[0] = choice(config, "xpPanelLabel1", LABELS, LABEL_XP_GAINED)
		panelLabels[1] = choice(config, "xpPanelLabel2", LABELS, LABEL_XP_LEFT)
		panelLabels[2] = choice(config, "xpPanelLabel3", LABELS, LABEL_XP_HOUR)
		panelLabels[3] = choice(config, "xpPanelLabel4", LABELS, LABEL_ACTIONS_LEFT)
		progressBarLabel = choice(config, "progressBarLabel", BAR_LABELS, BAR_PERCENTAGE)
		progressBarTooltipLabel = choice(config, "progressBarTooltipLabel", BAR_LABELS, BAR_TIME_TO_LEVEL)
		prioritizeRecentXpSkills = config.getBoolean("prioritizeRecentXpSkills", false)
		canvas.clear()
		for (part in config.get("canvas", "").split(',')) {
			val skill: Int = part.trim().toIntOrNull() ?: continue
			if (skill in 0 until Skill.COUNT && !canvas.contains(skill)) {
				canvas.add(skill)
			}
		}
	}

	/**
	 * The canvas skills drawn: only those the window lists, so each box has its row to remove it from. A
	 * canvas skill not tracked this session (gains reset, or none since a restart) keeps its place and
	 * shows again once it gains experience.
	 */
	@JvmStatic
	fun canvasShown(): List<Int> = canvas.filter { order.contains(it) && states[it] != null }

	private fun clear() {
		for (i in states.indices) {
			states[i] = null
		}
		order.clear()
		initialized = false
		loginCycle = -1
	}

	@JvmStatic
	fun context(): PluginContext? = context

	// --- experience ---

	private fun now(): Long = lang.currentTimeMillis()

	private fun totalXp(client: ClientApi): Long {
		var total: Long = 0L
		for (skill in 0 until Skill.COUNT) {
			total += client.skillXp(skill)
		}
		return total
	}

	/** RuneLite's onStatChanged: a skill's experience, counted once the login's own update is over. */
	@JvmStatic
	fun statChanged(client: ClientApi, event: StatChanged) {
		if (!enabled || event.skill < 0 || event.skill >= Skill.COUNT || !initialized) {
			return
		}
		if (hideMaxed && event.level >= MAX_REAL_LEVEL) {
			states[event.skill] = null
			order.remove(event.skill)
			canvas.remove(event.skill)
			XpTrackerScreen.refresh()
			return
		}
		updateSkill(event.skill, event.newXp.toLong())
		updateSkill(OVERALL, totalXp(client))
		XpTrackerScreen.refresh()
	}

	/** RuneLite's XpState.updateSkill: started from this experience the first time, or again when it went down (a negative lamp). */
	@JvmStatic
	fun updateSkill(skill: Int, currentXp: Long) {
		val state: XpTrackerState? = states[skill]
		if (state == null || state.currentXp() > currentXp) {
			val fresh: XpTrackerState = XpTrackerState(currentXp)
			fresh.updateGoals(currentXp)
			states[skill] = fresh
			return
		}
		if (!state.update(currentXp, now())) {
			return
		}
		state.updateGoals(currentXp)
		if (skill != OVERALL) {
			if (prioritizeRecentXpSkills) {
				order.remove(skill)
				order.add(0, skill)
			} else if (!order.contains(skill)) {
				order.add(skill)
			}
		}
	}

	private fun stateChanged(client: ClientApi, state: GameState) {
		if (state === GameState.LOGGED_IN) {
			loginCycle = client.clientCycle()
			initialized = false
			for (s in states) {
				s?.pausedLogout = false
			}
			return
		}
		if (initialized) {
			save()
			if (pauseOnLogout) {
				for (s in states) {
					s?.pausedLogout = true
				}
			}
		}
		initialized = false
	}

	/** Each client cycle: the login's wait, then once a second RuneLite's tickSkillTimes, and once a minute the save. */
	private fun cycle(client: ClientApi, cycle: Int) {
		if (!enabled || client.gameState() !== GameState.LOGGED_IN) {
			return
		}
		if (!initialized) {
			if (loginCycle >= 0 && cycle - loginCycle >= INITIALIZE_CYCLES) {
				initialize(client)
			}
			return
		}
		val now: Long = now()
		if (lastSecond == 0L) {
			lastSecond = now
			return
		}
		if (now - lastSecond < SECOND) {
			return
		}
		val delta: Long = now - lastSecond
		lastSecond = now
		tickSkills(client, delta, now)
		if (now - lastSave >= SAVE_EVERY) {
			lastSave = now
			save()
		}
		XpTrackerScreen.refresh()
	}

	@JvmStatic
	fun tickSkills(client: ClientApi, delta: Long, now: Long) {
		for (skill in 0..OVERALL) {
			val state: XpTrackerState = states[skill] ?: continue
			val xp: Long = if (skill == OVERALL) totalXp(client) else client.skillXp(skill).toLong()
			state.tickPause(xp, pauseSkillAfter, now)
			if (!state.isPaused()) {
				state.tick(delta)
			}
			if (resetSkillRateAfter > 0 && state.lastChangeMillis != 0L && now - state.lastChangeMillis >= resetSkillRateAfter * 60_000L) {
				state.resetPerHour(now)
			}
		}
	}

	/** After the login's update: the saved tracking for this account, or every skill started from what it has now. */
	private fun initialize(client: ClientApi) {
		initialized = true
		lastSecond = now()
		lastSave = lastSecond
		val name: String? = client.localPlayer()?.name()?.lowercase()
		if (name != profile) {
			clear()
			initialized = true
			profile = name
			if (saveState) {
				restore()
			}
		}
		for (skill in 0 until Skill.COUNT) {
			val xp: Long = client.skillXp(skill).toLong()
			val state: XpTrackerState? = states[skill]
			// RuneLite's check for experience gained while logged out: a skill whose total moved starts again
			if (state == null || state.currentXp() != xp) {
				val fresh: XpTrackerState = XpTrackerState(xp)
				fresh.updateGoals(xp)
				states[skill] = fresh
			}
		}
		val total: Long = totalXp(client)
		val overall: XpTrackerState? = states[OVERALL]
		if (overall == null || overall.currentXp() != total) {
			states[OVERALL] = XpTrackerState(total).also { it.updateGoals(total) }
		}
		XpTrackerScreen.refresh()
	}

	// --- what the window and the menus do ---

	@JvmStatic
	fun resetSkill(skill: Int) {
		val client: ClientApi = context?.client() ?: return
		val xp: Long = if (skill == OVERALL) totalXp(client) else client.skillXp(skill).toLong()
		states[skill] = XpTrackerState(xp).also { it.updateGoals(xp) }
		order.remove(skill)
		canvas.remove(skill)
		saveCanvas()
	}

	@JvmStatic
	fun resetOthers(skill: Int) {
		for (other in ArrayList(order)) {
			if (other != skill) {
				resetSkill(other)
			}
		}
	}

	@JvmStatic
	fun resetAll() {
		for (skill in ArrayList(order)) {
			resetSkill(skill)
		}
		resetSkill(OVERALL)
	}

	@JvmStatic
	fun resetPerHour(skill: Int) {
		states[skill]?.resetPerHour(now())
	}

	@JvmStatic
	fun resetAllPerHour() {
		for (skill in 0..OVERALL) {
			states[skill]?.resetPerHour(now())
		}
	}

	@JvmStatic
	fun pause(skill: Int, pause: Boolean) {
		val state: XpTrackerState = states[skill] ?: return
		if (pause) {
			state.pausedManual = true
		} else {
			state.unpause(now())
		}
	}

	@JvmStatic
	fun pauseAll(pause: Boolean) {
		for (skill in 0..OVERALL) {
			pause(skill, pause)
		}
	}

	@JvmStatic
	fun toggleCanvas(skill: Int) {
		if (!canvas.remove(skill)) {
			canvas.add(skill)
		}
		saveCanvas()
		XpTrackerScreen.refresh()
	}

	private fun saveCanvas() {
		val config: PluginConfig = context?.config() ?: return
		config.set("canvas", canvas.joinToString(","))
		config.save()
	}

	/** The skill of a Stats tab component, -1 for none. */
	@JvmStatic
	fun skillOfComponent(widgetId: Int): Int {
		if (widgetId ushr 16 != STATS_TAB) {
			return -1
		}
		return STATS_COMPONENTS.indexOf(widgetId and 0xFFFF)
	}

	/** RuneLite's skill tab canvas option: Add to canvas or Remove from canvas on a skill of the Stats tab. */
	private fun menuEntryAdded(client: ClientApi, event: MenuEntryAdded) {
		if (!enabled || !skillTabOverlayMenuOptions || event.target !== MenuTarget.WIDGET) {
			return
		}
		val skill: Int = skillOfComponent(event.widgetId)
		if (skill < 0 || states[skill] == null) {
			return
		}
		client.addMenuEntry((if (canvas.contains(skill)) "Remove from canvas" else "Add to canvas") + " <col=ff9040>" + Skill.name(skill), canvasAction)
	}

	// --- RuneLite's labels ---

	/** A label's value, RuneLite's XpPanelLabel: numbers short as the game writes them, N/A for actions not known yet. */
	@JvmStatic
	fun labelValue(label: Int, state: XpTrackerState): String {
		return when (label) {
			LABEL_TTL -> state.timeTillLevel(XpTrackerState.SHORT)
			LABEL_XP_GAINED -> format(state.totalXpGained())
			LABEL_XP_HOUR -> format(state.xpPerHour())
			LABEL_XP_LEFT -> format(state.xpRemaining())
			LABEL_ACTIONS_LEFT -> format(state.actionsRemaining())
			LABEL_ACTIONS_HOUR -> format(state.actionsPerHour())
			else -> format(state.actions)
		}
	}

	private fun format(value: Int): String = if (value == Int.MAX_VALUE) "N/A" else GroundItemRules.quantityToRSDecimalStack(value, true)

	/** The progress bar's label, RuneLite's XpProgressBarLabel. */
	@JvmStatic
	fun barValue(label: Int, state: XpTrackerState): String {
		return when (label) {
			BAR_TIME_TO_LEVEL -> state.timeTillLevel(XpTrackerState.DAYS)
			BAR_HOURS_TO_LEVEL -> state.timeTillLevel(XpTrackerState.HOURS)
			else -> twoDecimals(state.progress()) + "%"
		}
	}

	/** RuneLite's TWO_DECIMAL_FORMAT ("0.00"). */
	@JvmStatic
	fun twoDecimals(value: Double): String {
		val hundredths: Long = kotlin.math.round(value * 100.0).toLong()
		return (hundredths / 100).toString() + "." + (hundredths % 100).toString().padStart(2, '0')
	}

	// --- kept between sessions ---

	/** RuneLite's XpSave, as JSON: each tracked skill's start, gains and time, and the total's. */
	private fun save() {
		if (!saveState) {
			return
		}
		val owner: String = profile ?: return
		val config: PluginConfig = context?.config() ?: return
		val out: StringBuilder = StringBuilder("{\"skills\":{")
		var first: Boolean = true
		for (skill in 0 until Skill.COUNT) {
			val state: XpTrackerState = states[skill]?.takeIf { it.totalXpGained() > 0 } ?: continue
			if (!first) {
				out.append(',')
			}
			first = false
			out.append('"').append(Skill.name(skill).uppercase()).append("\":")
			single(out, state)
		}
		out.append("}")
		states[OVERALL]?.let {
			out.append(",\"overall\":")
			single(out, it)
		}
		out.append('}')
		config.set("$owner.xpsave", out.toString())
		config.save()
	}

	private fun single(out: StringBuilder, state: XpTrackerState) {
		val s: LongArray = state.save()
		out.append("{\"startXp\":").append(s[0]).append(",\"xpGainedBeforeReset\":").append(s[1]).append(",\"xpGainedSinceReset\":").append(s[2]).append(",\"time\":").append(s[3]).append('}')
	}

	private fun restore() {
		val owner: String = profile ?: return
		val config: PluginConfig = context?.config() ?: return
		val text: String = config.get("$owner.xpsave", "")
		if (text.isEmpty()) {
			return
		}
		for (skill in 0..OVERALL) {
			val key: String = if (skill == OVERALL) "\"overall\":" else "\"" + Skill.name(skill).uppercase() + "\":"
			val at: Int = text.indexOf(key)
			if (at < 0) {
				continue
			}
			val body: String = text.substring(at + key.length, text.indexOf('}', at) + 1)
			val values: LongArray = longArrayOf(field(body, "startXp"), field(body, "xpGainedBeforeReset"), field(body, "xpGainedSinceReset"), field(body, "time"))
			val state: XpTrackerState = XpTrackerState(values[0])
			state.restore(values)
			state.updateGoals(state.currentXp())
			states[skill] = state
			if (skill != OVERALL && state.totalXpGained() > 0) {
				order.add(skill)
			}
		}
	}

	private fun field(body: String, name: String): Long {
		val key: String = "\"$name\":"
		val at: Int = body.indexOf(key)
		if (at < 0) {
			return 0L
		}
		var end: Int = at + key.length
		while (end < body.length && (body[end].isDigit() || body[end] == '-')) {
			end++
		}
		return body.substring(at + key.length, end).toLongOrNull() ?: 0L
	}
}
