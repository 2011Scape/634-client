package com.jagex.game.runetek5.plugin.api

/**
 * The corner of the game view a [PanelOverlay] stacks in, RuneLite's OverlayPosition without its
 * DYNAMIC, TOOLTIP and DETACHED (an [Overlay] that is not a panel draws where it likes). The panels of a
 * corner stack away from it in turn: down from the top corners, right along the bottom left, left along
 * the bottom right, and up from [ABOVE_CHATBOX_RIGHT]. On the fixed game screen [ABOVE_CHATBOX_RIGHT] is
 * [BOTTOM_RIGHT] and [CANVAS_TOP_RIGHT] is [TOP_RIGHT]. Since API 12.
 */
enum class OverlayPosition {
	TOP_LEFT,
	TOP_CENTER,
	TOP_RIGHT,
	BOTTOM_LEFT,
	BOTTOM_RIGHT,
	ABOVE_CHATBOX_RIGHT,
	CANVAS_TOP_RIGHT,
}
