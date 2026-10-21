package com.jagex.game.runetek5.plugin.api

/** What [PluginContext.pickColour] gives the colour the player picked, as ARGB. Since API 9. */
fun interface ColourCallback {
	fun picked(argb: Int)
}
