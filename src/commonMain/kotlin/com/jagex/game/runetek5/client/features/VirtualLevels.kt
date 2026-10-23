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
import com.jagex.game.runetek5.plugin.api.Skill
import com.jagex.game.runetek5.plugin.api.Widget
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.GameTick
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: RuneLite's Virtual Levels. A skill's experience past its last level
 * (99, Dungeoneering's 120) goes on counting levels, to 126 at 200 million: the Stats tab shows them as
 * the skill's level, and its total adds them up when "Virtual total level" is on.
 *
 * The Stats tab's scripts write the levels each time they change; the plugin writes its own over them
 * each tick while the tab is drawn, and the game's back once switched off. A built-in plugin
 * (BuiltinPlugins, docs/PLUGINS.md).
 */
object VirtualLevels : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("virtuallevels", "Virtual Levels", "1", PluginApi.VERSION, "Shows levels past 99 (and Dungeoneering's 120) in the Stats tab, from your experience.", "Pixel")

	const val STATS_TAB: Int = 320

	/** The Stats tab's "Total Lvl: N" (320:201). */
	const val TOTAL: Int = 201

	/** Each skill's base level text is three components before its mouse-over box (320:197 for Attack's 320:200). */
	const val BASE_OFFSET: Int = 3

	@JvmField var enabled: Boolean = true
	@JvmField var virtualTotalLevel: Boolean = true

	private var written: Boolean = false
	private var context: PluginContext? = null

	private val tickListener: EventListener<GameTick> = EventListener { _ -> context?.client()?.let { update(it) } }

	private val settingListener: EventListener<SettingChanged> = EventListener { event ->
		if (event.pluginId == DESCRIPTOR.id) {
			context?.config()?.let { readSettings(it) }
			context?.client()?.let { if (!enabled) restore(it) }
		}
	}

	private val toggle: CommandHandler = CommandHandler { _, _ ->
		enabled = !enabled
		context?.config()?.setBoolean("enabled", enabled)
		context?.config()?.save()
		context?.client()?.let { if (!enabled) restore(it) }
		context?.client()?.consolePrint(if (enabled) "Virtual levels on." else "Virtual levels off.")
	}

	override fun start(context: PluginContext) {
		this.context = context
		val s: SettingsSchema = context.settings()
		s.toggle("enabled", "Virtual Levels", true)
		// RuneLite's VirtualLevelsConfig
		s.toggle("virtualTotalLevel", "Virtual total level", true)
		readSettings(context.config())
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		context.events().subscribe(Events.GAME_TICK, tickListener)
		context.registerCommand("virtuallevels", toggle)
	}

	override fun stop() {
		context?.client()?.let { restore(it) }
		context = null
	}

	@JvmStatic
	fun readSettings(config: PluginConfig) {
		enabled = config.getBoolean("enabled", true)
		virtualTotalLevel = config.getBoolean("virtualTotalLevel", true)
	}

	/** The level [xp] is, virtual levels past the real ones counted, never less than [real]. */
	@JvmStatic
	fun virtualLevel(xp: Int, real: Int): Int = maxOf(real, XpTrackerState.levelForXp(xp.toLong()))

	/** RuneLite's skillTabBaseLevel and skillTabTotalLevel: the virtual levels written over the tab's. */
	@JvmStatic
	fun update(client: ClientApi) {
		if (!enabled || client.gameState() !== GameState.LOGGED_IN) {
			return
		}
		val total: Widget = client.widget(STATS_TAB, TOTAL) ?: return
		if (!total.isDrawnThisFrame()) {
			return
		}
		var sum: Int = 0
		for (skill in 0 until Skill.COUNT) {
			val real: Int = client.skillBaseLevel(skill)
			val level: Int = virtualLevel(client.skillXp(skill), real)
			sum += if (virtualTotalLevel) level else real
			val base: Widget = client.widget(STATS_TAB, XpTracker.STATS_COMPONENTS[skill] - BASE_OFFSET) ?: continue
			if (level > real) {
				setIfChanged(base, level.toString())
				written = true
			}
		}
		if (virtualTotalLevel) {
			setIfChanged(total, "Total Lvl: " + sum)
			written = true
		}
	}

	private fun setIfChanged(widget: Widget, text: String) {
		if (widget.text() != text) {
			widget.setText(text)
		}
	}

	/** The real levels and total back. */
	@JvmStatic
	fun restore(client: ClientApi) {
		if (!written) {
			return
		}
		written = false
		var sum: Int = 0
		for (skill in 0 until Skill.COUNT) {
			val real: Int = client.skillBaseLevel(skill)
			sum += real
			client.widget(STATS_TAB, XpTracker.STATS_COMPONENTS[skill] - BASE_OFFSET)?.setText(real.toString())
		}
		client.widget(STATS_TAB, TOTAL)?.setText("Total Lvl: " + sum)
	}
}
