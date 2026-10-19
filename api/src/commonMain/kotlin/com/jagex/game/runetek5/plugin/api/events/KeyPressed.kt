package com.jagex.game.runetek5.plugin.api.events

import kotlin.jvm.JvmField

/** See [Events.KEY_PRESSED]: the client's key code and the character, with the modifier flags. One reused instance. */
class KeyPressed {
	@JvmField
	var keyCode: Int = 0

	@JvmField
	var keyChar: Char = '\u0000'

	@JvmField
	var modifiers: Int = 0
}
