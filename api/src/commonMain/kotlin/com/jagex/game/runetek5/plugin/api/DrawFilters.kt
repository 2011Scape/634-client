package com.jagex.game.runetek5.plugin.api

/**
 * Says whether the client draws an actor (RuneLite's RenderableDrawListener): [PluginContext.addActorFilter].
 * Asked each frame for each actor in the scene, so it must be quick. Since API 32.
 */
fun interface ActorFilter {
	/**
	 * Whether [actor] is drawn: its model when [drawingUI] is false; its overhead text, hitsplats, health bar,
	 * overhead icons and the like when it is true. An actor whose model is not drawn is still asked for those.
	 */
	fun shouldDraw(actor: Actor, drawingUI: Boolean): Boolean
}

/** Says whether the client draws a projectile, by its spot animation: [PluginContext.addProjectileFilter]. Since API 32. */
fun interface ProjectileFilter {
	fun shouldDraw(graphicId: Int): Boolean
}

/**
 * Says whether the client smooths a sequence (RuneLite's animation interpolation filter):
 * [PluginContext.setAnimationFilter]. A smoothed sequence is blended between its keyframes each client cycle
 * instead of jumping from one to the next, for players, NPCs, objects and interface models alike. Sequences the
 * cache already marks as blended stay blended. The answer is kept per sequence until a filter is set or cleared,
 * so read the plugin's settings when they change and set the filter again. Since API 34.
 */
fun interface AnimationFilter {
	fun shouldSmooth(sequenceId: Int): Boolean
}
