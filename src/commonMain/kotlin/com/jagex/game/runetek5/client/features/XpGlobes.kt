package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.Plugin
import com.jagex.game.runetek5.plugin.api.PluginApi
import com.jagex.game.runetek5.plugin.api.PluginConfig
import com.jagex.game.runetek5.plugin.api.PluginContext
import com.jagex.game.runetek5.plugin.api.PluginDescriptor
import com.jagex.game.runetek5.plugin.api.SettingsSchema
import com.jagex.game.runetek5.plugin.api.Skill
import com.jagex.game.runetek5.plugin.api.events.ClientTick
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.GameStateChanged
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import com.jagex.game.runetek5.plugin.api.events.StatChanged
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: RuneLite's XP Globes. A skill that gains experience gets a globe at the
 * top of the game view: its icon on a disc, ringed by its progress to the next level in the skill's
 * colour; hovering one shows the percentage and a tooltip of its level, experience, actions and experience
 * left, experience an hour and the time to the level. Up to five, in skill order, each gone after the
 * duration set. The drawing is in XpGlobesOverlay.
 *
 * The rates are RuneLite's XpTrackerService's: the XP Tracker's when it is on, else this plugin's own
 * (XpTrackerState). 634: the most real level is 99, Dungeoneering's 120; virtual levels go to 126.
 */
object XpGlobes : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("xpglobes", "XP Globes", "1", PluginApi.VERSION, "Shows a globe for each skill you gain experience in with your progress to the next level; hover it for XP left, actions left, XP per hour and time to goal.", "Pixel")

	/** RuneLite's MAXIMUM_SHOWN_GLOBES. */
	const val MAXIMUM_SHOWN_GLOBES: Int = 5

	/** RuneLite's defaults. */
	const val ORANGE: Int = 0xFFFFC800.toInt()
	const val BLACK: Int = 0xFF000000.toInt()
	const val GREY_BACKGROUND: Int = (127 shl 24) or 0x808080
	const val DEFAULT_ARC_WIDTH: Int = 2
	const val DEFAULT_ORB_SIZE: Int = 40
	const val DEFAULT_DURATION: Int = 10

	private const val MAX_REAL_LEVEL: Int = 99
	private const val MAX_DUNGEONEERING_LEVEL: Int = 120

	/** A shown globe: RuneLite's XpGlobe. */
	class XpGlobe(@JvmField val skill: Int, @JvmField var currentXp: Long, @JvmField var currentLevel: Int, @JvmField var time: Long)

	@JvmField var enabled: Boolean = true
	@JvmField var enableTooltips: Boolean = true
	@JvmField var showXpLeft: Boolean = true
	@JvmField var showActionsLeft: Boolean = true
	@JvmField var showXpHour: Boolean = true
	@JvmField var showTimeTilGoal: Boolean = true
	@JvmField var hideMaxed: Boolean = false
	@JvmField var showVirtualLevel: Boolean = false
	@JvmField var enableCustomArcColor: Boolean = false
	@JvmField var progressArcColor: Int = ORANGE
	@JvmField var progressOrbOutLineColor: Int = BLACK
	@JvmField var progressOrbBackgroundColor: Int = GREY_BACKGROUND
	@JvmField var progressArcStrokeWidth: Int = DEFAULT_ARC_WIDTH
	@JvmField var xpOrbSize: Int = DEFAULT_ORB_SIZE
	@JvmField var xpOrbDuration: Int = DEFAULT_DURATION
	@JvmField var alignOrbsVertically: Boolean = false

	/** RuneLite's globeCache: by skill, the last values seen; null until the first update primes it. */
	private val globeCache: Array<XpGlobe?> = arrayOfNulls(Skill.COUNT)

	/** RuneLite's globes: the globes shown, in skill order. */
	@JvmField
	val globes: ArrayList<XpGlobe> = ArrayList()

	/** By skill: this plugin's own rates, for when the XP Tracker is off. */
	private val states: Array<XpTrackerState?> = arrayOfNulls(Skill.COUNT)

	/** The clock, in milliseconds; the tests set their own. */
	@JvmField
	var clock: () -> Long = { lang.currentTimeMillis() }

	private var lastTickMillis: Long = -1L

	private var context: PluginContext? = null

	private val statListener: EventListener<StatChanged> = EventListener { event -> statChanged(event.skill, event.newXp.toLong()) }

	private val tickListener: EventListener<ClientTick> = EventListener { tick() }

	private val stateListener: EventListener<GameStateChanged> = EventListener { event ->
		// RuneLite resets on hopping and logging in: here, whenever the game is left
		if (event.state !== GameState.LOGGED_IN) {
			reset()
		}
	}

	private val settingListener: EventListener<SettingChanged> = EventListener { event ->
		if (event.pluginId == DESCRIPTOR.id) {
			context?.config()?.let { readSettings(it) }
		}
	}

	private val toggle: CommandHandler = CommandHandler { _, _ ->
		val context: PluginContext = this.context ?: return@CommandHandler
		enabled = !enabled
		context.config().setBoolean("enabled", enabled)
		context.config().save()
		if (!enabled) {
			globes.clear()
		}
		context.client().consolePrint(if (enabled) "XP globes on." else "XP globes off.")
	}

	override fun start(context: PluginContext) {
		this.context = context
		declareSettings(context.settings())
		readSettings(context.config())
		context.events().subscribe(Events.STAT_CHANGED, statListener)
		context.events().subscribe(Events.CLIENT_TICK, tickListener)
		context.events().subscribe(Events.GAME_STATE_CHANGED, stateListener)
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		context.addOverlay(XpGlobesOverlay)
		context.registerCommand("xpglobes", toggle)
	}

	override fun stop() {
		context = null
		reset()
	}

	@JvmStatic
	fun context(): PluginContext? = context

	private fun declareSettings(s: SettingsSchema) {
		s.toggle("enabled", "XP globes", true)
		s.toggle("enableTooltips", "Enable tooltips", true)
		s.toggle("showXpLeft", "Show XP left", true)
		s.toggle("showActionsLeft", "Show actions left", true)
		s.toggle("showXpHour", "Show XP/hr", true)
		s.toggle("showTimeTilGoal", "Show time til goal", true)
		s.toggle("hideMaxed", "Hide maxed skills", false)
		s.toggle("showVirtualLevel", "Show virtual level", false)
		s.toggle("enableCustomArcColor", "Enable custom arc color", false)
		s.colour("Progress arc color", "Progress arc color", ORANGE, true)
		s.colour("Progress orb outline color", "Progress orb outline color", BLACK, true)
		s.colour("Progress orb background color", "Progress orb background color", GREY_BACKGROUND, true)
		s.number("Progress arc width", "Progress arc width (px)", DEFAULT_ARC_WIDTH, 1, 20, 1)
		s.number("Orb size", "Size of orbs (px)", DEFAULT_ORB_SIZE, 16, 120, 1)
		s.number("Orb duration", "Duration of orbs (seconds)", DEFAULT_DURATION, 1, 600, 1)
		// alignOrbsVertically is RuneLite's hidden setting: the overlay's Flip sets it
	}

	@JvmStatic
	fun readSettings(config: PluginConfig) {
		enabled = config.getBoolean("enabled", true)
		enableTooltips = config.getBoolean("enableTooltips", true)
		showXpLeft = config.getBoolean("showXpLeft", true)
		showActionsLeft = config.getBoolean("showActionsLeft", true)
		showXpHour = config.getBoolean("showXpHour", true)
		showTimeTilGoal = config.getBoolean("showTimeTilGoal", true)
		hideMaxed = config.getBoolean("hideMaxed", false)
		showVirtualLevel = config.getBoolean("showVirtualLevel", false)
		enableCustomArcColor = config.getBoolean("enableCustomArcColor", false)
		progressArcColor = config.getColour("Progress arc color", ORANGE)
		progressOrbOutLineColor = config.getColour("Progress orb outline color", BLACK)
		progressOrbBackgroundColor = config.getColour("Progress orb background color", GREY_BACKGROUND)
		progressArcStrokeWidth = config.getInt("Progress arc width", DEFAULT_ARC_WIDTH).coerceIn(1, 20)
		xpOrbSize = config.getInt("Orb size", DEFAULT_ORB_SIZE).coerceIn(16, 120)
		xpOrbDuration = config.getInt("Orb duration", DEFAULT_DURATION).coerceIn(1, 600)
		alignOrbsVertically = config.getBoolean("alignOrbsVertically", false)
	}

	/** RuneLite's Flip: the globes across or down, kept in the settings. */
	@JvmStatic
	fun flip() {
		alignOrbsVertically = !alignOrbsVertically
		val context: PluginContext = this.context ?: return
		context.config().setBoolean("alignOrbsVertically", alignOrbsVertically)
		context.config().save()
	}

	/** The highest real level of [skill]: 99, Dungeoneering's 120. */
	@JvmStatic
	fun maxRealLevel(skill: Int): Int = if (skill == Skill.DUNGEONEERING) MAX_DUNGEONEERING_LEVEL else MAX_REAL_LEVEL

	/** RuneLite's onStatChanged, with the experience the server sent ([xp]); the level is worked out from it (the event's is the boosted one). */
	@JvmStatic
	fun statChanged(skill: Int, xp: Long) {
		if (skill < 0 || skill >= Skill.COUNT) {
			return
		}
		val now: Long = clock()
		ownState(skill, xp, now)
		val cached: XpGlobe? = globeCache[skill]
		// a stat drained or boosted is not a change to the experience
		if (cached != null && cached.currentXp >= xp) {
			return
		}
		// the level from the experience: the event's is the boosted one
		var currentLevel: Int = minOf(XpTrackerState.levelForXp(xp), maxRealLevel(skill))
		if (currentLevel >= maxRealLevel(skill)) {
			if (hideMaxed) {
				return
			}
			if (showVirtualLevel) {
				currentLevel = XpTrackerState.levelForXp(xp)
			}
		}
		if (cached == null) {
			// not drawn: the first update of each skill (the login's) only sets the values up
			globeCache[skill] = XpGlobe(skill, xp, currentLevel, now)
			return
		}
		cached.currentXp = xp
		cached.currentLevel = currentLevel
		cached.time = now
		if (enabled) {
			addXpGlobe(cached)
		}
	}

	/** RuneLite's addXpGlobe: in skill order, once; the oldest goes when there are more than five. */
	private fun addXpGlobe(globe: XpGlobe) {
		if (globes.contains(globe)) {
			return
		}
		var index: Int = 0
		while (index < globes.size && globes[index].skill < globe.skill) {
			index++
		}
		globes.add(index, globe)
		if (globes.size > MAXIMUM_SHOWN_GLOBES) {
			globes.remove(globes.minByOrNull { it.time })
		}
	}

	/** Each client cycle: the globes older than the duration go (RuneLite checks each second), the own rates count time. */
	@JvmStatic
	fun tick() {
		val now: Long = clock()
		if (lastTickMillis >= 0L) {
			val delta: Long = now - lastTickMillis
			for (state in states) {
				state?.tick(delta)
			}
		}
		lastTickMillis = now
		if (globes.isNotEmpty()) {
			val expire: Long = now - xpOrbDuration * 1000L
			globes.removeAll { it.time < expire }
		}
	}

	private fun ownState(skill: Int, xp: Long, now: Long) {
		val state: XpTrackerState? = states[skill]
		if (state == null) {
			states[skill] = XpTrackerState(xp).also { it.updateGoals(xp) }
			return
		}
		state.update(xp, now)
		state.updateGoals(xp)
	}

	/** The rates for [skill]: the XP Tracker's when it tracks it, else this plugin's own. */
	@JvmStatic
	fun state(skill: Int): XpTrackerState? {
		if (XpTracker.enabled) {
			XpTracker.states[skill]?.let { return it }
		}
		return states[skill]
	}

	@JvmStatic
	fun reset() {
		globes.clear()
		globeCache.fill(null)
		states.fill(null)
		lastTickMillis = -1L
	}
}
