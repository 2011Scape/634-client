package com.jagex.game.runetek5.plugin.api

import kotlin.jvm.JvmField
import kotlin.jvm.JvmOverloads

/**
 * An entry a [PanelOverlay] or an [InfoBox] adds to the menu when the player shift-right-clicks it,
 * RuneLite's OverlayMenuEntry: [option] the verb, [target] what it is on (shown in the menu's target
 * colour), and [action] what it does. Since API 12.
 *
 * Entries that share a [submenu] label are grouped under one entry of that label which opens them as a
 * submenu, each shown by its [option]; the menu entry's target is the first such entry's [target]. Since API 41.
 */
class OverlayMenuEntry @JvmOverloads constructor(
	@JvmField val option: String,
	@JvmField val target: String,
	@JvmField val action: Task,
	@JvmField val submenu: String = "",
)
