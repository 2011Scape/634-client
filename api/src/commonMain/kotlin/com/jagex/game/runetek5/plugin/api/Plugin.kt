package com.jagex.game.runetek5.plugin.api

/**
 * What the client loads from a plugin jar: the class the jar's manifest names in `Plugin-Class`,
 * which needs a public constructor with no arguments.
 *
 * Both methods run on the game thread. [start] runs once the cache's types are loaded, before the
 * login screen (the toolkit's fonts may not exist yet: the first [ToolkitChanged][events.ToolkitChanged]
 * says when they do). After [stop] returns, every subscription, overlay, command and menu action the
 * plugin registered is removed for it.
 */
interface Plugin {
	fun start(context: PluginContext)

	fun stop() {
	}
}
