package com.jagex.game.runetek5.plugin.api.events

import kotlin.jvm.JvmField

/** See [Events.KEY_RELEASED]: the client's key code, with the modifier flags. One reused instance. */
class KeyReleased {
	@JvmField
	var keyCode: Int = 0

	@JvmField
	var modifiers: Int = 0
}
