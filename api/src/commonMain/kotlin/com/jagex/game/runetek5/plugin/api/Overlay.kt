package com.jagex.game.runetek5.plugin.api

/**
 * Draws on the game view, once a frame, after the scene and the client's own overlays and before
 * the interfaces (the minimap, the orbs and the chat box cover what is drawn under them). [view] is
 * the game view's place on the screen; [g] draws in screen coordinates.
 */
fun interface Overlay {
	/** Draws this frame; the clip and the font are the client's again afterwards. */
	fun draw(g: Graphics, view: Viewport)
}
