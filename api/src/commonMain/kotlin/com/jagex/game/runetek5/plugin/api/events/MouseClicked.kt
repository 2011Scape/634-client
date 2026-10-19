package com.jagex.game.runetek5.plugin.api.events

import kotlin.jvm.JvmField

/** See [Events.MOUSE_CLICKED]: where on the screen, and which button (0 left, 1 middle, 2 right). One reused instance. */
class MouseClicked {
	@JvmField
	var x: Int = 0

	@JvmField
	var y: Int = 0

	@JvmField
	var button: Int = 0
}
