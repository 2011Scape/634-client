package com.jagex.game.runetek5.plugin.api.events

import kotlin.jvm.JvmField

/**
 * See [Events.MENU_BUILT]: the right-click menu for what is under the mouse is complete, with
 * [entryCount] entries, and nothing has been done with it yet. While a listener runs, the
 * [ClientApi][com.jagex.game.runetek5.plugin.api.ClientApi] menu methods read and edit it. One
 * reused instance.
 */
class MenuBuilt {
	@JvmField
	var entryCount: Int = 0
}
