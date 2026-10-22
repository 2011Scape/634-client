package com.jagex.game.runetek5.plugin.api

/**
 * What a moved [PanelOverlay]'s place is measured from, RuneLite's OverlayOrigin: [AUTO] picks
 * [OverlayOriginX] and [OverlayOriginY] from where the panel was dropped (the nearest side of the game
 * view), [MANUAL] keeps the ones the player chose (the Overlay Origin menu), and [SIDEPANEL], [CHATBOX] and
 * [MINIMAP] follow that part of the game screen wherever the screen puts it. Since API 12.
 */
enum class OverlayOrigin {
	AUTO,
	MANUAL,
	SIDEPANEL,
	CHATBOX,
	MINIMAP,
}

/** The across part of an [OverlayOrigin]: a side of the game view, or of the chat box. Since API 12. */
enum class OverlayOriginX {
	LEFT,
	CENTER,
	RIGHT,
	HUD_CONTAINER_LEFT,
	HUD_CONTAINER_CENTER,
	HUD_CONTAINER_RIGHT,
	CHATBOX_LEFT,
	CHATBOX_RIGHT,
}

/** The down part of an [OverlayOrigin]: a side of the game view, or the chat box's top. Since API 12. */
enum class OverlayOriginY {
	TOP,
	CENTER,
	BOTTOM,
	HUD_CONTAINER_TOP,
	HUD_CONTAINER_BOTTOM,
	CHATBOX_TOP,
}
