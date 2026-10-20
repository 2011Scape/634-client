package com.jagex.game.runetek5.plugin.api.events

import kotlin.jvm.JvmField

/** See [Events.MOUSE_WHEEL]: [rotation] is negative away from the player and positive towards. [consume] keeps it from the game (the zoom, the scroll bars). One reused instance. */
class MouseWheel {
	@JvmField
	var rotation: Int = 0

	@JvmField
	var consumed: Boolean = false

	fun consume() {
		consumed = true
	}
}
