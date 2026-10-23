package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.Graphics
import com.jagex.game.runetek5.plugin.api.Overlay
import com.jagex.game.runetek5.plugin.api.Player
import com.jagex.game.runetek5.plugin.api.Plugin
import com.jagex.game.runetek5.plugin.api.PluginApi
import com.jagex.game.runetek5.plugin.api.PluginConfig
import com.jagex.game.runetek5.plugin.api.PluginContext
import com.jagex.game.runetek5.plugin.api.PluginDescriptor
import com.jagex.game.runetek5.plugin.api.SettingsSchema
import com.jagex.game.runetek5.plugin.api.Viewport
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.GameStateChanged
import com.jagex.game.runetek5.plugin.api.events.GameTick
import com.jagex.game.runetek5.plugin.api.events.LocChanged
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: RuneLite's Hunter. The traps you set (laid while you stand on or next
 * to their tile) are marked by state: an open trap with a pie of the time until it falls over, a trap with
 * something in it, a trap something is going into, and a sprung empty one, each in its colour.
 *
 * Void's traps and times: bird snares, box traps (and the magic box for implings), deadfalls and net traps
 * change their location's id with their state (its hunter objects), and one that catches nothing falls
 * over after 100 ticks, RuneLite's minute. Void's pitfalls show their state through variables, not
 * locations, and its rabbit snares are not set up yet, so neither is marked; the maniacal monkey's traps
 * and the crab traps are not on Void. A built-in plugin (BuiltinPlugins, docs/PLUGINS.md).
 */
object Hunter : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("hunterplugin", "Hunter", "1", PluginApi.VERSION, "Marks your hunter traps by state, with a timer on each open one.", "Pixel")

	/** The plugin manager's switch. */
	@JvmField var enabled: Boolean = true

	/** RuneLite's HunterTrap.State. */
	const val OPEN: Int = 0
	const val FULL: Int = 1
	const val EMPTY: Int = 2
	const val TRANSITION: Int = 3

	/** Void's trap locations by state (hunter.objs.toml). */
	@JvmField
	val OPEN_IDS: IntArray = intArrayOf(19175, 19187, 19206, 19223, 19678, 19650, 19662, 19670)

	@JvmField
	val TRANSITION_IDS: IntArray = intArrayOf(19188, 19213, 19211, 19207, 19209, 19225, 19674, 19655, 19658, 19666, 19676, 19657, 19660, 19668)

	@JvmField
	val FULL_IDS: IntArray = intArrayOf(19178, 19180, 19182, 19184, 19186, 28930, 19190, 28557, 28558, 28567, 28906, 19215, 19216, 19217, 19218, 19226, 19675, 19654, 19659, 19667)

	@JvmField
	val EMPTY_IDS: IntArray = intArrayOf(19174, 19192, 19219, 19224, 19677, 19656, 19661, 19669)

	/** RuneLite's TRAP_TIME, and Void's collapse: 100 ticks. */
	const val TRAP_MILLIS: Long = 60_000L

	@JvmField var hexColorOpenTrap: Int = YELLOW
	@JvmField var hexColorFullTrap: Int = GREEN
	@JvmField var hexColorEmptyTrap: Int = RED
	@JvmField var hexColorTransTrap: Int = ORANGE

	/** RuneLite's HunterTrap: a trap's world tile, level, state, and when it was set (or last changed state). */
	class Trap(@JvmField val x: Int, @JvmField val y: Int, @JvmField val level: Int, @JvmField var state: Int, @JvmField var placedOn: Long)

	@JvmField val traps: ArrayList<Trap> = ArrayList()

	/** For the smoke test: the traps the last frame drew. */
	@JvmField var drawnLastFrame: Int = 0

	@JvmField var clock: () -> Long = { lang.currentTimeMillis() }

	private val point: IntArray = IntArray(2)
	private var context: PluginContext? = null

	/** RuneLite's TrapOverlay, under the interfaces. */
	private val overlay: Overlay = Overlay { g, view -> if (enabled) context?.client()?.let { draw(g, view, it) } }

	private val locListener: EventListener<LocChanged> = EventListener { event -> if (enabled) context?.client()?.let { locChanged(it, event) } }

	private val tickListener: EventListener<GameTick> = EventListener { _ -> if (enabled) tick() }

	private val stateListener: EventListener<GameStateChanged> = EventListener { event ->
		if (event.state === GameState.LOGIN_SCREEN) {
			traps.clear()
		}
	}

	private val settingListener: EventListener<SettingChanged> = EventListener { event ->
		if (event.pluginId == DESCRIPTOR.id) {
			context?.config()?.let { readSettings(it) }
			if (!enabled) {
				traps.clear()
			}
		}
	}

	private val toggle: CommandHandler = CommandHandler { _, _ ->
		traps.clear()
		context?.client()?.consolePrint("Hunter traps forgotten.")
	}

	override fun start(context: PluginContext) {
		this.context = context
		val s: SettingsSchema = context.settings()
		// RuneLite's HunterConfig, in its order, but the maniacal monkey's notification (not on Void)
		s.toggle("enabled", "Hunter", true)
		s.colour("hexColorOpenTrap", "Open trap", YELLOW, true)
		s.colour("hexColorFullTrap", "Full trap", GREEN, true)
		s.colour("hexColorEmptyTrap", "Empty trap", RED, true)
		s.colour("hexColorTransTrap", "Transitioning trap", ORANGE, true)
		readSettings(context.config())
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		context.events().subscribe(Events.LOC_CHANGED, locListener)
		context.events().subscribe(Events.GAME_TICK, tickListener)
		context.events().subscribe(Events.GAME_STATE_CHANGED, stateListener)
		context.addOverlay(overlay)
		context.registerCommand("hunter", toggle)
	}

	override fun stop() {
		context = null
		traps.clear()
	}

	@JvmStatic
	fun readSettings(config: PluginConfig) {
		enabled = config.getBoolean("enabled", true)
		hexColorOpenTrap = config.getColour("hexColorOpenTrap", YELLOW)
		hexColorFullTrap = config.getColour("hexColorFullTrap", GREEN)
		hexColorEmptyTrap = config.getColour("hexColorEmptyTrap", RED)
		hexColorTransTrap = config.getColour("hexColorTransTrap", ORANGE)
	}

	/** The state of trap location [id], -1 for no trap. */
	@JvmStatic
	fun stateOf(id: Int): Int = when (id) {
		in OPEN_IDS -> OPEN
		in FULL_IDS -> FULL
		in EMPTY_IDS -> EMPTY
		in TRANSITION_IDS -> TRANSITION
		else -> -1
	}

	/** RuneLite's onGameObjectSpawned: a trap set beside you is yours; one of yours changing changes its state; gone, it is forgotten. */
	@JvmStatic
	fun locChanged(client: ClientApi, event: LocChanged) {
		val x: Int = event.sceneX + client.sceneBaseX()
		val y: Int = event.sceneY + client.sceneBaseY()
		val state: Int = stateOf(event.newId)
		val existing: Trap? = traps.firstOrNull { it.x == x && it.y == y && it.level == event.level }
		if (existing != null) {
			if (state < 0) {
				traps.remove(existing)
			} else if (state != existing.state) {
				existing.state = state
				existing.placedOn = clock()
			}
			return
		}
		if (state != OPEN) {
			return
		}
		val player: Player = client.localPlayer() ?: return
		val px: Int = player.tileX() + client.sceneBaseX()
		val py: Int = player.tileY() + client.sceneBaseY()
		if (player.level() == event.level && kotlin.math.abs(px - x) <= 1 && kotlin.math.abs(py - y) <= 1) {
			traps.add(Trap(x, y, event.level, OPEN, clock()))
		}
	}

	/** RuneLite's onGameTick: a trap left twice its time is forgotten. */
	@JvmStatic
	fun tick() {
		val now: Long = clock()
		traps.removeAll { now - it.placedOn > TRAP_MILLIS * 2 }
	}

	/** RuneLite's TrapOverlay: an open trap's pie of its time gone, the others a full circle in their colour. */
	private fun draw(g: Graphics, view: Viewport, client: ClientApi) {
		drawnLastFrame = 0
		if (traps.isEmpty() || client.gameState() !== GameState.LOGGED_IN) {
			return
		}
		val now: Long = clock()
		for (trap in traps) {
			val sceneX: Int = trap.x - client.sceneBaseX()
			val sceneY: Int = trap.y - client.sceneBaseY()
			if (!client.projectPoint(sceneX * TILE + TILE / 2, sceneY * TILE + TILE / 2, trap.level, 0, point)) {
				continue
			}
			val colour: Int = when (trap.state) {
				FULL -> hexColorFullTrap
				EMPTY -> hexColorEmptyTrap
				TRANSITION -> hexColorTransTrap
				else -> hexColorOpenTrap
			}
			val progress: Float = if (trap.state == OPEN) minOf(1f, (now - trap.placedOn).toFloat() / TRAP_MILLIS) else 1f
			ProgressPie.draw(g, view.x + point[0], view.y + point[1], progress, (PIE_ALPHA shl 24) or (colour and 0xFFFFFF), colour)
			drawnLastFrame++
		}
	}

	private const val TILE: Int = 512
	private const val PIE_ALPHA: Int = 90

	private const val YELLOW: Int = 0xFFFFFF00.toInt()
	private const val GREEN: Int = 0xFF00FF00.toInt()
	private const val RED: Int = 0xFFFF0000.toInt()
	private const val ORANGE: Int = 0xFFFFC800.toInt()
}
