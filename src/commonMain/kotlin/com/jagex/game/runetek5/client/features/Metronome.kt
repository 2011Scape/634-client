package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.Plugin
import com.jagex.game.runetek5.plugin.api.PluginApi
import com.jagex.game.runetek5.plugin.api.PluginConfig
import com.jagex.game.runetek5.plugin.api.PluginContext
import com.jagex.game.runetek5.plugin.api.PluginDescriptor
import com.jagex.game.runetek5.plugin.api.SettingsSchema
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.ServerTick
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: RuneLite's Metronome. Every "Tick count" server ticks (the player
 * update the server sends each 600 ms, ServerTick, API 28) a sound plays, the tick and the tock taking
 * turns, each at its own volume (muted to high, RuneLite's steps). The sounds are 634's own short ones (a
 * plop and a knock), RuneLite's Grand Exchange plops being OSRS's. A built-in plugin (BuiltinPlugins,
 * docs/PLUGINS.md).
 */
object Metronome : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("metronome", "Metronome", "1", PluginApi.VERSION, "Plays a tick and a tock on the game's ticks, for timing your actions.", "Pixel")

	/** 634's short sounds: Void's pool_plop for the tick, knock_knock for the tock. */
	const val TICK_SOUND: Int = 1658
	const val TOCK_SOUND: Int = 1735

	/** RuneLite's SoundEffectVolume: muted, low, medium low, medium high, high. */
	@JvmField
	val VOLUMES: Array<String> = arrayOf("Muted", "Low", "Medium low", "Medium high", "High")

	/** Those steps on the client's 0 to 255. */
	@JvmField
	val VOLUME_LEVELS: IntArray = intArrayOf(0, 64, 128, 192, 255)

	@JvmField var enabled: Boolean = false
	@JvmField var tickCount: Int = 1
	@JvmField var tickVolume: Int = 3
	@JvmField var tockVolume: Int = 0

	/** The ticks since the last sound, and whether the tock is next. */
	@JvmField var ticksSince: Int = 0
	@JvmField var tockNext: Boolean = false

	/** For the smoke test and the tests: the last sound and volume played. */
	@JvmField var lastSound: Int = -1
	@JvmField var lastVolume: Int = -1

	private var context: PluginContext? = null

	private val tickListener: EventListener<ServerTick> = EventListener { _ -> context?.client()?.let { tick(it) } }

	private val settingListener: EventListener<SettingChanged> = EventListener { event ->
		if (event.pluginId == DESCRIPTOR.id) {
			context?.config()?.let { readSettings(it) }
		}
	}

	private val toggle: CommandHandler = CommandHandler { _, _ ->
		enabled = !enabled
		context?.config()?.setBoolean("enabled", enabled)
		context?.config()?.save()
		context?.client()?.consolePrint(if (enabled) "Metronome on." else "Metronome off.")
	}

	override fun start(context: PluginContext) {
		this.context = context
		val s: SettingsSchema = context.settings()
		s.toggle("enabled", "Metronome", false)
		// RuneLite's MetronomeConfig, in its order
		s.number("tickCount", "Tick count", 1, 1, 100, 1)
		s.choice("tickVolume", "Tick volume", VOLUMES[3], VOLUMES)
		s.choice("tockVolume", "Tock volume", VOLUMES[0], VOLUMES)
		readSettings(context.config())
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		context.events().subscribe(Events.SERVER_TICK, tickListener)
		context.registerCommand("metronome", toggle)
	}

	override fun stop() {
		context = null
	}

	@JvmStatic
	fun readSettings(config: PluginConfig) {
		enabled = config.getBoolean("enabled", false)
		tickCount = config.getInt("tickCount", 1).coerceAtLeast(1)
		tickVolume = volume(config.get("tickVolume", VOLUMES[3]), 3)
		tockVolume = volume(config.get("tockVolume", VOLUMES[0]), 0)
	}

	private fun volume(name: String, default: Int): Int {
		val i: Int = VOLUMES.indexOf(name)
		return if (i < 0) default else i
	}

	/** RuneLite's onGameTick: every tickCount ticks the tick or the tock, taking turns; a muted one is skipped, its turn still taken. */
	@JvmStatic
	fun tick(client: ClientApi) {
		if (!enabled || client.gameState() !== GameState.LOGGED_IN) {
			return
		}
		ticksSince++
		if (ticksSince < tickCount) {
			return
		}
		ticksSince = 0
		val tock: Boolean = tockNext
		tockNext = !tockNext
		val volume: Int = VOLUME_LEVELS[if (tock) tockVolume else tickVolume]
		if (volume > 0) {
			lastSound = if (tock) TOCK_SOUND else TICK_SOUND
			lastVolume = volume
			client.playSound(lastSound, volume)
		}
	}
}
