package com.jagex.game.runetek5.plugin.api

import kotlin.jvm.JvmStatic

/** The client's codes for the keys a plugin is likely to hold ([ClientApi.isKeyHeld], [events.KeyPressed.keyCode]). */
object KeyCode {
	const val SHIFT: Int = 81

	const val CONTROL: Int = 82

	const val ALT: Int = 86

	/**
	 * The name of key [code] as a keybind setting shows it ("Alt", "F5", "A"), or "Key N" for a code
	 * without one, "None" for 0. Since API 9.
	 */
	@JvmStatic
	fun name(code: Int): String {
		if (code == 0) {
			return "None"
		}
		if (code == SHIFT) {
			return "Shift"
		}
		if (code == CONTROL) {
			return "Ctrl"
		}
		if (code == ALT) {
			return "Alt"
		}
		for (i in NAMED.indices step 2) {
			if (NAMED[i] as Int == code) {
				return NAMED[i + 1] as String
			}
		}
		return "Key " + code
	}

	/** The client's codes (CanvasKeyRecorder.REMAPPED_KEYS) and their names, pairs one after the other. */
	private val NAMED: Array<Any> = arrayOf(
		1, "F1", 2, "F2", 3, "F3", 4, "F4", 5, "F5", 6, "F6", 7, "F7", 8, "F8", 9, "F9", 10, "F10", 11, "F11", 12, "F12",
		13, "Escape", 16, "1", 17, "2", 18, "3", 19, "4", 20, "5", 21, "6", 22, "7", 23, "8", 24, "9", 25, "0", 26, "-",
		27, "=", 28, "`", 32, "Q", 33, "W", 34, "E", 35, "R", 36, "T", 37, "Y", 38, "U", 39, "I", 40, "O", 41, "P", 42, "[",
		43, "]", 48, "A", 49, "S", 50, "D", 51, "F", 52, "G", 53, "H", 54, "J", 55, "K", 56, "L", 57, ";", 58, "'", 59, "#",
		64, "Z", 65, "X", 66, "C", 67, "V", 68, "B", 69, "N", 70, "M", 71, ",", 72, ".", 73, "/", 74, "\\", 80, "Tab",
		83, "Space", 84, "Enter", 85, "Backspace", 87, "Numpad +", 88, "Numpad -", 89, "Numpad *", 90, "Numpad /",
		96, "Left", 97, "Right", 98, "Up", 99, "Down", 100, "Insert", 101, "Delete", 102, "Home", 103, "End", 104, "Page Up",
		105, "Page Down", 219, "Numpad 5", 224, "Numpad 4", 225, "Numpad 6", 226, "Numpad 8", 227, "Numpad 2",
		228, "Numpad 0", 229, "Numpad .", 230, "Numpad 7", 231, "Numpad 1", 232, "Numpad 9", 233, "Numpad 3",
	)
}
