package com.jagex.game.runetek5.plugin.api

/**
 * When in the frame a [PanelOverlay] is drawn, RuneLite's OverlayLayer without the ones a panel has no
 * use for. Since API 45.
 */
enum class OverlayLayer {
	/** In the game view, after the scene: the game's interfaces (the bank, a dialogue) draw over it. The default. */
	UNDER_WIDGETS,

	/**
	 * After the game's interfaces, before the right-click menu: a window that stays in front of the bank, so the
	 * player sees what is taking their clicks. Its [PanelOverlay.onClick] keeps the game's interface under it
	 * from the click too.
	 */
	ABOVE_WIDGETS,
}
