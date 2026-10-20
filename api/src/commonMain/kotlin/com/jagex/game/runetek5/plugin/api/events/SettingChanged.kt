package com.jagex.game.runetek5.plugin.api.events

import kotlin.jvm.JvmField

/**
 * See [Events.SETTING_CHANGED]: the plugin manager wrote setting [key] of plugin [pluginId] and
 * saved it. A plugin compares [pluginId] with its own descriptor's id and reads the value back from
 * its config.
 */
class SettingChanged(@JvmField val pluginId: String, @JvmField val key: String)
