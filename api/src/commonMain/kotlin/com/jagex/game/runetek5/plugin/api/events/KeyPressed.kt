package com.jagex.game.runetek5.plugin.api.events

import kotlin.jvm.JvmField

/** See [Events.KEY_PRESSED]: the client's key code and the character, with the modifier flags. [consume] keeps the key from the game (it is not sent to the server, so a game hotkey does nothing); the typed character still reaches the interfaces, see [KeyTyped]. One reused instance. */
class KeyPressed {
	@JvmField
	var keyCode: Int = 0

	@JvmField
	var keyChar: Char = '\u0000'

	@JvmField
	var modifiers: Int = 0

	@JvmField
	var consumed: Boolean = false

	/** Keeps the key from the server. */
	fun consume() {
		consumed = true
	}
}
