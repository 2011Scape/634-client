package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.Graphics
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
import com.jagex.game.runetek5.plugin.api.events.FrameDraw
import com.jagex.game.runetek5.plugin.api.events.GameTick
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic
import kotlin.math.PI
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Not part of the original client: RuneLite's Regeneration Meter. A ring round the life points orb fills
 * up to the next heal, and the special attack bar on the combat tab fills along its foot to the next
 * tenth of energy (634 has no special attack orb).
 *
 * Void's timers, not OSRS's: the life points heal every 10 ticks (6 seconds) from the moment they fall
 * below the maximum (HitpointRestoration; the prayers, resting and the regen bracelet heal more each time,
 * not sooner), so the ring starts again when they fall from full and each time they rise; the special
 * attack energy gains a tenth every 50 ticks while below full (SpecialAttackEnergy). Over the maximum (a
 * brew, say) the ring empties instead. The Lightbearer is OSRS-only. A built-in plugin (BuiltinPlugins,
 * docs/PLUGINS.md).
 */
object RegenMeter : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("regenmeter", "Regeneration Meter", "1", PluginApi.VERSION, "Shows the time to your next life points heal round the orb, and to the next special attack energy on the combat tab.", "Pixel")

	/** Void's HitpointRestoration: every 6 seconds. */
	const val HP_REGEN_TICKS: Int = 10

	/** Void's SpecialAttackEnergy: a tenth every 50 ticks. */
	const val SPEC_REGEN_TICKS: Int = 50

	const val SPECIAL_VARP: Int = 300
	const val MAX_SPECIAL: Int = 1000

	const val HEALTH_ORB: Int = 748
	const val HEALTH_ORB_CIRCLE: Int = 3
	const val COMBAT_TAB: Int = 884
	const val SPECIAL_BAR: Int = 4

	@JvmField var showHitpoints: Boolean = true
	@JvmField var showSpecial: Boolean = true
	@JvmField var showWhenNoChange: Boolean = false
	@JvmField var notifyBeforeHpRegenDuration: Int = 0

	@JvmField var ticksSinceHpRegen: Int = 0
	@JvmField var ticksSinceSpecRegen: Int = 0
	@JvmField var hitpointsPercentage: Double = 0.0
	@JvmField var specialPercentage: Double = 0.0

	/** The life points and energy of the last tick, -1 before the first. */
	@JvmField var lastLifePoints: Int = -1
	@JvmField var lastSpecial: Int = -1

	@JvmField var lastNotification: String? = null

	/** For the smoke test: the rings and bars the last frame drew. */
	@JvmField var drawnLastFrame: Int = 0

	private var lastTick: Int = -1

	private var context: PluginContext? = null

	private val tickListener: EventListener<GameTick> = EventListener { event ->
		val tick: Int = event.serverTick / CYCLES_PER_TICK
		if (tick != lastTick) {
			lastTick = tick
			context?.client()?.let { tick(it) }
		}
	}

	/** RuneLite's RegenMeterOverlay, over the interfaces. */
	private val frameListener: EventListener<FrameDraw> = EventListener { event -> context?.client()?.let { draw(event.graphics, it) } }

	private val settingListener: EventListener<SettingChanged> = EventListener { event ->
		if (event.pluginId == DESCRIPTOR.id) {
			context?.config()?.let { readSettings(it) }
		}
	}

	private val toggle: CommandHandler = CommandHandler { _, _ ->
		val on: Boolean = !(showHitpoints || showSpecial)
		showHitpoints = on
		showSpecial = on
		context?.config()?.setBoolean("showHitpoints", on)
		context?.config()?.setBoolean("showSpecial", on)
		context?.config()?.save()
		context?.client()?.consolePrint(if (on) "Regeneration meter on." else "Regeneration meter off.")
	}

	override fun start(context: PluginContext) {
		this.context = context
		val s: SettingsSchema = context.settings()
		// RuneLite's RegenMeterConfig, in its order
		s.toggle("showHitpoints", "Show hitpoints regen", true)
		s.toggle("showSpecial", "Show spec. attack regen", true)
		s.toggle("showWhenNoChange", "Show at full hitpoints", false)
		s.number("notifyBeforeHpRegenDuration", "Hitpoint notification (seconds)", 0, 0, 6, 1)
		readSettings(context.config())
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		context.events().subscribe(Events.GAME_TICK, tickListener)
		context.events().subscribe(Events.FRAME_DRAW, frameListener)
		context.registerCommand("regenmeter", toggle)
	}

	override fun stop() {
		context = null
		lastLifePoints = -1
		lastSpecial = -1
		lastTick = -1
	}

	@JvmStatic
	fun readSettings(config: PluginConfig) {
		showHitpoints = config.getBoolean("showHitpoints", true)
		showSpecial = config.getBoolean("showSpecial", true)
		showWhenNoChange = config.getBoolean("showWhenNoChange", false)
		notifyBeforeHpRegenDuration = config.getInt("notifyBeforeHpRegenDuration", 0)
	}

	/** RuneLite's onGameTick, with Void's timers kept in step by the life points and energy themselves. */
	@JvmStatic
	fun tick(client: ClientApi) {
		val special: Int = client.varp(SPECIAL_VARP)
		if (lastSpecial < 0 || special >= MAX_SPECIAL || lastSpecial < special) {
			// the first tick, full, or a tenth just came: the timer starts its 50 ticks again
			ticksSinceSpecRegen = 0
		} else {
			ticksSinceSpecRegen = (ticksSinceSpecRegen + 1) % SPEC_REGEN_TICKS
		}
		lastSpecial = special
		specialPercentage = ticksSinceSpecRegen / SPEC_REGEN_TICKS.toDouble()

		val lp: Int = StatusBars.lifePoints(client)
		val max: Int = client.skillBaseLevel(Skill.CONSTITUTION) * LP_PER_LEVEL
		val fellFromFull: Boolean = lastLifePoints >= max && lp < max
		val healed: Boolean = lastLifePoints in 0 until lp
		if (lastLifePoints < 0 || fellFromFull || healed) {
			ticksSinceHpRegen = 0
		} else {
			ticksSinceHpRegen = (ticksSinceHpRegen + 1) % HP_REGEN_TICKS
		}
		lastLifePoints = lp
		hitpointsPercentage = ticksSinceHpRegen / HP_REGEN_TICKS.toDouble()
		if (lp == max && !showWhenNoChange) {
			hitpointsPercentage = 0.0
		} else if (lp > max) {
			// a boost wearing off: shown going down
			hitpointsPercentage = 1 - hitpointsPercentage
		}
		if (notifyBeforeHpRegenDuration > 0 && lp in 1 until max && shouldNotify()) {
			lastNotification = "Your next hitpoint will regenerate soon!"
			client.notify("Your next hitpoint will regenerate soon!")
		}
	}

	/** RuneLite's shouldNotifyHpRegenThisTick: the tick the set seconds before the heal falls on (the earlier one between two). */
	@JvmStatic
	fun shouldNotify(): Boolean {
		val ticksBefore: Int = HP_REGEN_TICKS - ticksSinceHpRegen
		val notifyTick: Int = ceil(notifyBeforeHpRegenDuration * 1000.0 / TICK_MILLIS).toInt()
		return ticksBefore == notifyTick
	}

	private fun draw(g: Graphics, client: ClientApi) {
		drawnLastFrame = 0
		if (client.gameState() !== GameState.LOGGED_IN) {
			return
		}
		if (showHitpoints && hitpointsPercentage > 0) {
			val orb: Widget? = client.widget(HEALTH_ORB, HEALTH_ORB_CIRCLE)
			if (orb != null && !orb.isHidden() && orb.width() > 0) {
				ring(g, orb.screenX() + orb.width() / 2, orb.screenY() + orb.height() / 2, orb.width() / 2 - 1, hitpointsPercentage, HITPOINTS_COLOUR)
				drawnLastFrame++
			}
		}
		if (showSpecial && specialPercentage > 0) {
			val bar: Widget? = client.widget(COMBAT_TAB, SPECIAL_BAR)
			if (bar != null && !bar.isHidden() && bar.width() > 0) {
				val width: Int = (bar.width() * specialPercentage).toInt()
				g.fillRect(bar.screenX(), bar.screenY() + bar.height() - 2, width, 2, SPECIAL_COLOUR)
				drawnLastFrame++
			}
		}
	}

	/** RuneLite's arc: from the top, clockwise, [percent] of the way round, 2 pixels wide, as 2-pixel squares. */
	@JvmStatic
	fun ring(g: Graphics, cx: Int, cy: Int, radius: Int, percent: Double, argb: Int) {
		val steps: Int = maxOf(1, (RING_STEPS * percent).toInt())
		for (i in 0..steps) {
			val angle: Double = 2 * PI * percent * i / steps
			val x: Int = cx + (sin(angle) * radius).roundToInt()
			val y: Int = cy - (cos(angle) * radius).roundToInt()
			g.fillRect(x - 1, y - 1, 2, 2, argb)
		}
	}

	private const val CYCLES_PER_TICK: Int = 30
	private const val TICK_MILLIS: Int = 600
	private const val LP_PER_LEVEL: Int = 10
	private const val RING_STEPS: Int = 120

	/** RuneLite's colours, brighter(0x9B0703) and brighter(0x1E95B0). */
	private const val HITPOINTS_COLOUR: Int = 0xFFDD0A04.toInt()
	private const val SPECIAL_COLOUR: Int = 0xFF2AD4FB.toInt()
}
