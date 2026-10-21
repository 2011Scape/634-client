package com.jagex.game.runetek5.plugin.api

/**
 * What a plugin lets the player set from the plugin manager (the Plugins window in the Settings
 * tab). Declare in [Plugin.start] through [PluginContext.settings]; the values live in the
 * plugin's [PluginConfig] under each [key], written and saved by the manager, and
 * [events.Events.SETTING_CHANGED] says when one changed. A key declared twice keeps its first entry.
 */
interface SettingsSchema {
	/** An on/off switch shown as [label]; [PluginConfig.getBoolean] reads it. */
	fun toggle(key: String, label: String, default: Boolean)

	/** A number from [min] to [max] the player steps by [step]; [PluginConfig.getInt] reads it. */
	fun number(key: String, label: String, default: Int, min: Int, max: Int, step: Int)

	/** One of [options], shown as [label]; [PluginConfig.get] reads the chosen option's text. */
	fun choice(key: String, label: String, default: String, options: Array<String>)

	/** A line of text of up to [maxLength] characters the player types into a box; [PluginConfig.get] reads it. Since API 7. */
	fun text(key: String, label: String, default: String, maxLength: Int)
}
