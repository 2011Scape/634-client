package com.jagex.game.runetek5.plugin.api

/**
 * A plugin's own settings: name=value pairs, read when the plugin starts and written by [save].
 * Kept in the plugins folder under the plugin's id (docs/PLUGINS.md).
 */
interface PluginConfig {
	fun get(key: String, default: String): String

	fun getInt(key: String, default: Int): Int

	fun getBoolean(key: String, default: Boolean): Boolean

	fun set(key: String, value: String)

	fun setInt(key: String, value: Int)

	fun setBoolean(key: String, value: Boolean)

	fun remove(key: String)

	/** Writes the settings; false when they cannot be written (no home folder, or a test run). */
	fun save(): Boolean
}
