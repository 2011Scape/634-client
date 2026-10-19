package com.jagex.game.runetek5.plugin.api

/**
 * The version of this API. A plugin's jar names the version it was built against
 * (`Plugin-Api-Version` in its manifest); the client refuses a plugin built against a newer one.
 *
 * The number stays the same while changes are additive (a new event, a new method with a default
 * body) and goes up when a signature changes or something is removed, so a plugin built against
 * version N runs on every client whose API is version N or higher.
 */
object PluginApi {
	const val VERSION: Int = 1
}
