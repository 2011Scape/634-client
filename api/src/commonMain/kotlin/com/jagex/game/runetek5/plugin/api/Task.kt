package com.jagex.game.runetek5.plugin.api

/** A piece of work for [PluginContext.invokeLater], [PluginContext.schedule] and [PluginContext.startThread]. */
fun interface Task {
	fun run()
}
