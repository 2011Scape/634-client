package com.jagex.game.runetek5.client.window

import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client, which passes the window mode as an int: 1 for a fixed size
 * window, 2 for a resizable one, 3 for full screen. The id is that int, as the screen size
 * preference stores it, scripts read and set it, and the login packet sends it.
 */
enum class WindowModeType(@JvmField val id: Int) {

	FIXED(1),

	RESIZABLE(2),

	FULLSCREEN(3);

	companion object {
		/**
		 * The mode of an id. The screen size preference only holds 1 to 3, and the script that sets the
		 * mode only passes 1 or 2.
		 */
		@JvmStatic
		fun fromId(id: Int): WindowModeType {
			for (mode in values()) {
				if (mode.id == id) {
					return mode
				}
			}
			throw IllegalArgumentException("window mode " + id)
		}
	}
}
