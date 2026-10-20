package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.Actor
import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.Overlay
import com.jagex.game.runetek5.plugin.api.Plugin
import com.jagex.game.runetek5.plugin.api.PluginApi
import com.jagex.game.runetek5.plugin.api.PluginContext
import com.jagex.game.runetek5.plugin.api.PluginDescriptor
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.GameStateChanged
import com.jagex.game.runetek5.plugin.api.events.GameTick
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: once the player has stood still, out of animation and without
 * a target for a while, a line in the chat says so and a red frame flashes around the game view, so
 * that a player who looks away while skilling or fighting knows the work has stopped. On only with
 * `client.idleNotifier=true` or after `::idle`; `::idle <seconds>` sets the wait (15 by default), and
 * both are kept in the plugin's settings.
 *
 * A built-in plugin (BuiltinPlugins, docs/PLUGINS.md) on API 2: it reads the local player's
 * animation, target and position each client cycle (GameTick, 20 ms), which API 1 could not say.
 */
object IdleNotifier : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("idle", "Idle notifier", "1", PluginApi.VERSION, "A chat line and a red frame once you have been idle for a while (::idle turns it on and off, ::idle <seconds> sets the wait).", "634-client")

	private const val DEFAULT_SECONDS: Int = 15

	private const val MIN_SECONDS: Int = 2

	private const val MAX_SECONDS: Int = 600

	/** How many client cycles the frame shows (50 a second). */
	private const val FLASH_CYCLES: Int = 100

	/** A client cycle, and so a GameTick, is 20 ms. */
	private const val CYCLE_MILLIS: Int = 20

	private const val FRAME_COLOUR: Int = 0xFFE03030.toInt()

	private const val FRAME_WIDTH: Int = 3

	@JvmField
	var enabled: Boolean = false

	private var waitSeconds: Int = DEFAULT_SECONDS

	private var idleTicks: Int = 0

	private var notified: Boolean = false

	private var lastX: Int = -1

	private var lastZ: Int = -1

	/** The client cycle the frame shows until. */
	private var flashUntil: Int = 0

	/** How many times the player was told, for the smoke test. */
	@JvmField
	var notifications: Int = 0

	private var context: PluginContext? = null

	private val tickListener: EventListener<GameTick> = EventListener { _ -> tick() }

	private val stateListener: EventListener<GameStateChanged> = EventListener { event ->
		if (event.previous === GameState.LOGGED_IN) {
			reset()
		}
	}

	private val overlay: Overlay = Overlay { g, view ->
		val client: ClientApi = context?.client() ?: return@Overlay
		if (client.clientCycle() < flashUntil) {
			for (i in 0 until FRAME_WIDTH) {
				g.drawRect(view.x + i, view.y + i, view.width - 2 * i, view.height - 2 * i, FRAME_COLOUR)
			}
		}
	}

	private val command: CommandHandler = CommandHandler { _, args -> command(args) }

	/** The plugin manager changed a setting: read them back and arm the notifier again. */
	private val settingListener: EventListener<SettingChanged> = EventListener { event ->
		val context: PluginContext? = this.context
		if (event.pluginId == DESCRIPTOR.id && context != null) {
			enabled = context.config().getBoolean("enabled", enabled)
			waitSeconds = clampSeconds(context.config().getInt("seconds", waitSeconds))
			notified = false
		}
	}

	override fun start(context: PluginContext) {
		this.context = context
		val default: Boolean = lang.getBoolean("client.idleNotifier")
		enabled = context.config().getBoolean("enabled", default)
		waitSeconds = clampSeconds(context.config().getInt("seconds", DEFAULT_SECONDS))
		context.settings().toggle("enabled", "Idle notifier", default)
		context.settings().number("seconds", "Seconds idle before the warning", DEFAULT_SECONDS, MIN_SECONDS, MAX_SECONDS, 1)
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		context.events().subscribe(Events.GAME_TICK, tickListener)
		context.events().subscribe(Events.GAME_STATE_CHANGED, stateListener)
		context.addOverlay(overlay)
		context.registerCommand("idle", command)
	}

	override fun stop() {
		context = null
		reset()
	}

	private fun clampSeconds(seconds: Int): Int {
		return if (seconds < MIN_SECONDS) MIN_SECONDS else if (seconds > MAX_SECONDS) MAX_SECONDS else seconds
	}

	private fun command(args: Array<String>) {
		val context: PluginContext = this.context ?: return
		notified = false // whatever was said before, the next idle spell is announced
		if (args.isEmpty()) {
			enabled = !enabled
			context.config().setBoolean("enabled", enabled)
			context.config().save()
			context.client().consolePrint(if (enabled) "Idle notifier on: " + waitSeconds + " seconds." else "Idle notifier off.")
			return
		}
		val seconds: Int? = args[0].toIntOrNull()
		if (seconds == null) {
			context.client().consolePrint("Usage: idle (on or off), or idle <seconds>")
			return
		}
		waitSeconds = clampSeconds(seconds)
		enabled = true
		context.config().setInt("seconds", waitSeconds)
		context.config().setBoolean("enabled", true)
		context.config().save()
		context.client().consolePrint("Idle notifier on: " + waitSeconds + " seconds.")
	}

	/** Each client cycle: idle is still, out of animation and without a target. */
	@JvmStatic
	fun tick() {
		val client: ClientApi = context?.client() ?: return
		if (client.gameState() !== GameState.LOGGED_IN) {
			return
		}
		val player: Actor = client.localPlayer() ?: return
		val moved: Boolean = player.x() != lastX || player.z() != lastZ
		lastX = player.x()
		lastZ = player.z()
		if (moved || player.animationId() != -1 || player.interacting() != null) {
			idleTicks = 0
			notified = false
			return
		}
		idleTicks++
		if (notified || idleTicks * CYCLE_MILLIS < waitSeconds * 1000) {
			return
		}
		notified = true
		if (!enabled) {
			return
		}
		notifications++
		flashUntil = client.clientCycle() + FLASH_CYCLES
		client.chatMessage("You have been idle for " + waitSeconds + " seconds.")
		context?.log("idle for " + waitSeconds + " seconds at cycle " + client.clientCycle())
	}

	private fun reset() {
		idleTicks = 0
		notified = false
		lastX = -1
		lastZ = -1
		flashUntil = 0
	}
}
