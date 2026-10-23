package com.jagex.game.runetek5.plugin.api.events

import kotlin.jvm.JvmField

/**
 * See [Events.KEY_INPUT]: a key as the keyboard gave it, before the client reads it, RuneLite's
 * KeyListener. [type] is [PRESSED] (each press, a held key's repeats too), [RELEASED] or [TYPED] (a
 * character, [keyCode] -1). A listener may change [keyCode] (a client [KeyCode][com.jagex.game.runetek5.plugin.api.KeyCode]
 * code, 1 to 111) or [keyChar], and the client then reads that key instead, held, released and sent to the
 * server as such: a plugin remaps the player's own keys, it never makes up input. [consume] drops it. A
 * remapped press should have its release remapped the same way. One reused instance. Since API 20.
 */
class KeyInput {
	@JvmField
	var type: Int = PRESSED

	@JvmField
	var keyCode: Int = 0

	@JvmField
	var keyChar: Char = '\u0000'

	/** The client's modifier flags; bit 4 is Control. */
	@JvmField
	var modifiers: Int = 0

	@JvmField
	var consumed: Boolean = false

	/** Drops the key: the client never sees it. */
	fun consume() {
		consumed = true
	}

	companion object {
		const val PRESSED: Int = 0
		const val RELEASED: Int = 1
		const val TYPED: Int = 3
	}
}
