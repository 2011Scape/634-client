package com.jagex.game.runetek5.plugin.api

/**
 * A plugin's own settings: name=value pairs, read when the plugin starts and written by [save].
 * Kept in the plugins folder under the plugin's id (docs/PLUGINS.md).
 */
interface PluginConfig {
	/** The setting [key], or [default] when it is not there. */
	fun get(key: String, default: String): String

	/** The setting [key] as a number, or [default] when it is not there or not a number. */
	fun getInt(key: String, default: Int): Int

	/** The setting [key] as true or false, or [default] when it is not there. */
	fun getBoolean(key: String, default: Boolean): Boolean

	/** Sets [key] in memory; [save] writes it. */
	fun set(key: String, value: String)

	fun setInt(key: String, value: Int)

	fun setBoolean(key: String, value: Boolean)

	/** Forgets [key]; [save] writes that too. */
	fun remove(key: String)

	/** Writes the settings; false when they cannot be written (no home folder, or a test run). */
	fun save(): Boolean
}
