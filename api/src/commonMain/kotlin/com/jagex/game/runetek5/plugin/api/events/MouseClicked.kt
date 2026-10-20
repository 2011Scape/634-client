package com.jagex.game.runetek5.plugin.api.events

import kotlin.jvm.JvmField

/** See [Events.MOUSE_CLICKED]: where on the screen, and which button (0 left, 1 middle, 2 right). [consume] keeps the press from the game (nothing is clicked, no menu opens); the button still counts as held while it is down, so the camera turns with the middle button and a drag goes on. One reused instance. */
class MouseClicked {
	@JvmField
	var x: Int = 0

	@JvmField
	var y: Int = 0

	@JvmField
	var button: Int = 0

	@JvmField
	var consumed: Boolean = false

	/** Keeps the click from the game. */
	fun consume() {
		consumed = true
	}
}
