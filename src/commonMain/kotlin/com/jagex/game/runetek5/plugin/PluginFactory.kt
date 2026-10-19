package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.plugin.api.Plugin

/**
 * Not part of the original client: makes a plugin's object when the plugin is first turned on, so
 * that a plugin the player turned off runs no code at all, not even its class's initialiser.
 */
fun interface PluginFactory {
	fun create(): Plugin
}
