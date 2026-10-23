package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.Overlay
import com.jagex.game.runetek5.plugin.api.Player
import com.jagex.game.runetek5.plugin.api.Plugin
import com.jagex.game.runetek5.plugin.api.PluginApi
import com.jagex.game.runetek5.plugin.api.PluginConfig
import com.jagex.game.runetek5.plugin.api.PluginContext
import com.jagex.game.runetek5.plugin.api.PluginDescriptor
import com.jagex.game.runetek5.plugin.api.SettingsSchema
import com.jagex.game.runetek5.plugin.api.Skill
import com.jagex.game.runetek5.plugin.api.Widget
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.GameTick
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic
import kotlin.math.ceil

/**
 * Not part of the original client: RuneLite's Run Energy. With the mouse over the run orb a tooltip gives
 * your weight, the run time your energy has left (or the energy itself, when the orb shows the time
 * instead) and the time to get back to 100%; "Replace orb text" puts the run time left on the orb.
 *
 * Void's numbers, not OSRS's: running drains 67 + 67 x weight / 64 of 10,000 a tick (weight 0 to 64 kg,
 * Agility no help), and the energy comes back each tick you do not run by an amount set by your Agility
 * level, from 27 to 157 walking, 168 to 310 resting and 240 to 400 resting by a musician (Energy). The
 * Ring of endurance, the graceful outfit and stamina potions are OSRS-only. A built-in plugin
 * (BuiltinPlugins, docs/PLUGINS.md).
 */
object RunEnergy : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("runenergy", "Run Energy", "1", PluginApi.VERSION, "Shows how long your run energy lasts and how long it takes to come back, over the run orb.", "Pixel")

	/** The plugin manager's switch. */
	@JvmField var enabled: Boolean = true

	const val RUN_ORB: Int = 750
	const val RUN_ORB_BUTTON: Int = 3
	const val RUN_ORB_TEXT: Int = 5

	/** Void's movement varp: walk 0, run 1, rest 3, music 4. */
	const val MOVEMENT_VARP: Int = 173
	const val RESTING: Int = 3
	const val MUSIC: Int = 4

	const val MAX_ENERGY: Int = 10000

	@JvmField var replaceOrbText: Boolean = false

	/** The text the orb had when the plugin took it over, put back once done. */
	private var orbTextReplaced: Boolean = false

	private var context: PluginContext? = null

	/** RuneLite's RunEnergyOverlay: the tooltip over the run orb, before the frame's tooltips are drawn. */
	private val overlay: Overlay = Overlay { _, _ -> if (enabled) context?.client()?.let { tooltip(it) } }

	private val tickListener: EventListener<GameTick> = EventListener { _ -> if (enabled) context?.client()?.let { updateOrbText(it) } }

	private val settingListener: EventListener<SettingChanged> = EventListener { event ->
		if (event.pluginId == DESCRIPTOR.id) {
			context?.config()?.let { readSettings(it) }
			context?.client()?.let { if (!enabled || !replaceOrbText) resetOrbText(it) }
		}
	}

	private val toggle: CommandHandler = CommandHandler { _, _ ->
		replaceOrbText = !replaceOrbText
		context?.config()?.setBoolean("replaceOrbText", replaceOrbText)
		context?.config()?.save()
		context?.client()?.let { if (!replaceOrbText) resetOrbText(it) }
		context?.client()?.consolePrint(if (replaceOrbText) "The run orb shows the run time left." else "The run orb shows the run energy.")
	}

	override fun start(context: PluginContext) {
		this.context = context
		val s: SettingsSchema = context.settings()
		// RuneLite's RunEnergyConfig, but the Ring of endurance message (OSRS-only)
		s.toggle("enabled", "Run Energy", true)
		s.toggle("replaceOrbText", "Replace orb text with run time left", false)
		readSettings(context.config())
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		context.events().subscribe(Events.GAME_TICK, tickListener)
		context.addOverlay(overlay)
		context.registerCommand("runenergy", toggle)
	}

	override fun stop() {
		context?.client()?.let { resetOrbText(it) }
		context = null
	}

	@JvmStatic
	fun readSettings(config: PluginConfig) {
		enabled = config.getBoolean("enabled", true)
		replaceOrbText = config.getBoolean("replaceOrbText", false)
	}

	/** Void's Energy.getDrainAmount: the energy (of 10,000) a tick of running costs at [weight] kg. */
	@JvmStatic
	fun drainPerTick(weight: Int): Int {
		val w: Int = weight.coerceIn(0, MAX_WEIGHT)
		return DRAIN_BASE + DRAIN_BASE * w / MAX_WEIGHT
	}

	/** Void's Energy.getRestoreAmount: the energy a tick gives back at [agility], walking, resting ([movement] 3) or by a musician (4). */
	@JvmStatic
	fun restorePerTick(agility: Int, movement: Int): Int {
		val level: Int = agility.coerceIn(1, MAX_LEVEL)
		return when (movement) {
			RESTING -> interpolate(level, REST_LOW, REST_HIGH)
			MUSIC -> interpolate(level, MUSIC_LOW, MUSIC_HIGH)
			else -> interpolate(level, WALK_LOW, WALK_HIGH)
		}
	}

	/** Void's Interpolation.interpolate over Agility 1 to 99. */
	private fun interpolate(level: Int, low: Int, high: Int): Int = (low * (MAX_LEVEL - level) + high * (level - 1)) / (MAX_LEVEL - 1)

	/** RuneLite's getEstimatedRunTimeRemaining, on Void's drain: "m:ss", or whole seconds and "s" for the orb. */
	@JvmStatic
	fun runTimeRemaining(energyPercent: Int, weight: Int, inSeconds: Boolean): String {
		val ticksLeft: Double = ceil(energyPercent * (MAX_ENERGY / PERCENT).toDouble() / drainPerTick(weight))
		val secondsLeft: Double = ticksLeft * TICK_MILLIS / 1000.0
		if (inSeconds) {
			return secondsLeft.toInt().toString() + "s"
		}
		return clock(secondsLeft.toInt())
	}

	/** RuneLite's getEstimatedRecoverTimeRemaining, on Void's restore: seconds to 100%, -1 while running somewhere. */
	@JvmStatic
	fun recoverTimeRemaining(energyPercent: Int, agility: Int, movement: Int, running: Boolean): Int {
		if (running) {
			return -1
		}
		val missing: Int = MAX_ENERGY - energyPercent * (MAX_ENERGY / PERCENT)
		val ticks: Double = ceil(missing.toDouble() / restorePerTick(agility, movement))
		return (ticks * TICK_MILLIS / 1000.0).toInt()
	}

	@JvmStatic
	fun clock(seconds: Int): String {
		val minutes: Int = seconds / 60
		val rest: Int = seconds % 60
		return minutes.toString() + ":" + (if (rest < 10) "0" else "") + rest
	}

	/** RuneLite's RunEnergyOverlay.render: the tooltip when the mouse is over the run orb. */
	@JvmStatic
	fun tooltipText(client: ClientApi): String {
		val energy: Int = client.runEnergy()
		val weight: Int = client.weight()
		val text: StringBuilder = StringBuilder()
		text.append("Weight: ").append(weight).append(" kg</br>")
		if (replaceOrbText) {
			text.append("Run Energy: ").append(energy).append('%')
		} else {
			text.append("Run Time Remaining: ").append(runTimeRemaining(energy, weight, false))
		}
		val player: Player? = client.localPlayer()
		val running: Boolean = player != null && player.isRunning() && client.destinationTileX() >= 0
		val until100: Int = recoverTimeRemaining(energy, client.skillLevel(Skill.AGILITY), client.varp(MOVEMENT_VARP), running)
		if (until100 > 0) {
			text.append("</br>100% Energy In: ").append(clock(until100))
		}
		return text.toString()
	}

	private fun tooltip(client: ClientApi) {
		if (client.gameState() !== GameState.LOGGED_IN) {
			return
		}
		val orb: Widget = client.widget(RUN_ORB, RUN_ORB_BUTTON) ?: return
		if (orb.isHidden() || orb.screenX() <= 0) {
			return
		}
		val x: Int = client.mouseX() - orb.screenX()
		val y: Int = client.mouseY() - orb.screenY()
		if (x >= 0 && y >= 0 && x < orb.width() && y < orb.height()) {
			client.addTooltip(tooltipText(client))
		}
	}

	/** RuneLite's onScriptPostFired(ORBS_UPDATE_RUNENERGY): the orb's number becomes the seconds left, kept so each cycle. */
	@JvmStatic
	fun updateOrbText(client: ClientApi) {
		if (!replaceOrbText || client.gameState() !== GameState.LOGGED_IN) {
			return
		}
		val text: Widget = client.widget(RUN_ORB, RUN_ORB_TEXT) ?: return
		val wanted: String = runTimeRemaining(client.runEnergy(), client.weight(), true)
		if (text.text() != wanted) {
			text.setText(wanted)
		}
		orbTextReplaced = true
	}

	/** RuneLite's resetRunOrbText: the energy back on the orb. */
	@JvmStatic
	fun resetOrbText(client: ClientApi) {
		if (!orbTextReplaced) {
			return
		}
		orbTextReplaced = false
		client.widget(RUN_ORB, RUN_ORB_TEXT)?.setText(client.runEnergy().toString())
	}

	private const val MAX_WEIGHT: Int = 64
	private const val DRAIN_BASE: Int = 67
	private const val MAX_LEVEL: Int = 99
	private const val PERCENT: Int = 100
	private const val TICK_MILLIS: Int = 600
	private const val WALK_LOW: Int = 27
	private const val WALK_HIGH: Int = 157
	private const val REST_LOW: Int = 168
	private const val REST_HIGH: Int = 310
	private const val MUSIC_LOW: Int = 240
	private const val MUSIC_HIGH: Int = 400
}
