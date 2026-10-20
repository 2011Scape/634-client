package com.jagex.game.runetek5.plugin.api.events

import kotlin.jvm.JvmField

/**
 * See [Events.KEY_TYPED]: a character for the interfaces and the chat, with the key's code and the
 * modifier flags; a held key repeats it. [consume] keeps it from them. Not posted while the
 * developer console is open: what is typed there is the player's. One reused instance.
 */
class KeyTyped {
	@JvmField
	var keyCode: Int = 0

	@JvmField
	var keyChar: Char = '\u0000'

	@JvmField
	var modifiers: Int = 0

	@JvmField
	var consumed: Boolean = false

	fun consume() {
		consumed = true
	}
}
