package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.AnimationFilter
import com.jagex.game.runetek5.plugin.api.Plugin
import com.jagex.game.runetek5.plugin.api.PluginApi
import com.jagex.game.runetek5.plugin.api.PluginConfig
import com.jagex.game.runetek5.plugin.api.PluginContext
import com.jagex.game.runetek5.plugin.api.PluginDescriptor
import com.jagex.game.runetek5.plugin.api.SettingsSchema
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: RuneLite's Animation Smoothing. Blends every sequence between its keyframes
 * each client cycle (the client's tweening, which the cache turns on only for some sequences and ::tween forces
 * for all), through PluginContext.setAnimationFilter (API 34), for players, NPCs, objects and interface models.
 *
 * Some sequences look wrong blended, as RuneLite found for OSRS: a loop whose last keyframe turns back to the
 * first spins backwards between them. RuneLite's list is of OSRS ids; [EXCLUDED] is 634's, and the player can add
 * more in the settings. A built-in plugin (BuiltinPlugins, docs/PLUGINS.md).
 */
object AnimationSmoothing : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("animationsmoothing", "Animation Smoothing", "1", PluginApi.VERSION, "Shows smoother player, NPC and object animations.", "Pixel")

	/** The plugin manager's switch. */
	@JvmField var enabled: Boolean = false

	/** 634 sequences that look wrong blended. */
	@JvmField
	val EXCLUDED: IntArray = intArrayOf()

	/** The player's own exclusions, from the settings. */
	@JvmField var excludedByPlayer: IntArray = IntArray(0)

	private const val MAX_TEXT: Int = 500

	private var context: PluginContext? = null

	private val filter: AnimationFilter = AnimationFilter { id -> shouldSmooth(id) }

	private val settingListener: EventListener<SettingChanged> = EventListener { event ->
		if (event.pluginId == DESCRIPTOR.id) {
			context?.let { readSettings(it.config()) }
			refresh()
		}
	}

	override fun start(context: PluginContext) {
		this.context = context
		val s: SettingsSchema = context.settings()
		s.toggle("enabled", "Animation Smoothing", false)
		s.text("excluded", "Animations not smoothed (ids, comma separated)", "", MAX_TEXT)
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		readSettings(context.config())
		refresh()
	}

	override fun stop() {
		context = null
	}

	/** Sets the filter again, so the client asks it afresh, or takes it back when off. */
	@JvmStatic
	fun refresh() {
		context?.setAnimationFilter(if (enabled) filter else null)
	}

	@JvmStatic
	fun readSettings(config: PluginConfig) {
		enabled = config.getBoolean("enabled", false)
		excludedByPlayer = parseIds(config.get("excluded", ""))
	}

	/** RuneLite's isAnimationInterpolatable. */
	@JvmStatic
	fun shouldSmooth(sequenceId: Int): Boolean = enabled && sequenceId !in EXCLUDED && sequenceId !in excludedByPlayer

	/** The ids in a comma (or space) separated list; anything not a number is skipped. */
	@JvmStatic
	fun parseIds(text: String): IntArray = text.split(',', ' ').mapNotNull { it.trim().toIntOrNull() }.toIntArray()
}
