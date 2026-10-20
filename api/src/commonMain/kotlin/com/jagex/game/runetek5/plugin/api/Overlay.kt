package com.jagex.game.runetek5.plugin.api

/**
 * Draws on the game view, once a frame, after the scene and the client's own overlays and before
 * the interfaces. [view] is the game view's place on the screen; [g] draws in screen coordinates. On
 * the fixed game screen the whole view is clear; on the resizable one the minimap and the orbs cover
 * its top-right corner, the chat box its bottom-left, and the tabs its bottom-right, so the top-left
 * is the safe corner on both.
 */
fun interface Overlay {
	/** Draws this frame; the clip and the font are the client's again afterwards. */
	fun draw(g: Graphics, view: Viewport)
}
