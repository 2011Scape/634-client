package com.jagex.game.runetek5.plugin.api

import kotlin.jvm.JvmField

/**
 * An entry a [PanelOverlay] or an [InfoBox] adds to the menu when the player shift-right-clicks it,
 * RuneLite's OverlayMenuEntry: [option] the verb, [target] what it is on (shown in the menu's target
 * colour), and [action] what it does. Since API 12.
 */
class OverlayMenuEntry(
	@JvmField val option: String,
	@JvmField val target: String,
	@JvmField val action: Task,
)
