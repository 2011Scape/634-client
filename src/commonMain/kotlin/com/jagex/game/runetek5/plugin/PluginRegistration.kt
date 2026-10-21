package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.plugin.api.Plugin
import com.jagex.game.runetek5.plugin.api.PluginDescriptor
import kotlin.jvm.JvmField

/**
 * Not part of the original client: one plugin the client knows about, built in or from a jar, and
 * where it is in its life. [plugin] is null until the plugin is first started, when [factory] makes
 * it; both are null when the jar could not be loaded ([error] says why).
 */
class PluginRegistration(
	@JvmField val descriptor: PluginDescriptor,
	@JvmField var plugin: Plugin?,
	@JvmField val builtIn: Boolean,
	/** Where it came from: the jar's file name, or "built in". */
	@JvmField val source: String,
) {

	@JvmField
	var state: Int = LOADED

	/** Makes [plugin] when it is first started; null for a built-in plugin and for a jar that could not be loaded. */
	@JvmField
	var factory: PluginFactory? = null

	/** What went wrong, for [FAILED]. */
	@JvmField
	var error: String? = null

	@JvmField
	var context: PluginContextImpl? = null

	/** The class loader of a plugin from a jar, for the platform side. */
	@JvmField
	var loader: Any? = null

	/** The plugin's icon for the manager's list, or null for none. */
	@JvmField
	var icon: PluginIcon? = null

	/** Whether the plugin exists or can be made: false for a jar the loader refused. */
	fun loadable(): Boolean {
		return plugin != null || factory != null
	}

	fun stateName(): String {
		return when (state) {
			LOADED -> "loaded"
			STARTED -> "on"
			STOPPED -> "off"
			FAILED -> "failed"
			else -> "?"
		}
	}

	companion object {
		/** Loaded and not yet started, or turned off by the player. */
		const val LOADED: Int = 0

		const val STARTED: Int = 1

		const val STOPPED: Int = 2

		/** Refused by the loader, or disabled after it threw. */
		const val FAILED: Int = 3
	}
}
