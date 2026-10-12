package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.client.input.console.Console

/**
 * Keys the developer console handles with `client.console`: Up and Down recall earlier commands,
 * as Page Up and Page Down already do, and Ctrl with Left, Right, Backspace or Delete moves or deletes
 * a word at a time.
 *
 * This class is not part of the original client.
 */
object ConsoleEditing {

	private const val UP: Int = 98
	private const val DOWN: Int = 99
	private const val LEFT: Int = 96
	private const val RIGHT: Int = 97
	private const val BACKSPACE: Int = 85
	private const val DELETE: Int = 101
	private const val CTRL: Int = 0x4

	/** Handles one key; returns whether it was handled, so the console skips its own handling. */
	@JvmStatic
	fun handle(key: Int, modifiers: Int): Boolean {
		if (key == UP) {
			if (Console.historyIndex < Console.displayedOutput!!.size) {
				Console.historyIndex++
				Console.adjustCommandString()
				Console.commandLength = Console.commandString!!.length
			}
			return true
		}
		if (key == DOWN) {
			if (Console.historyIndex > 0) {
				Console.historyIndex--
				Console.adjustCommandString()
				Console.commandLength = Console.commandString!!.length
			}
			return true
		}
		if ((modifiers and CTRL) == 0) {
			return false
		}
		val text: String? = Console.commandString
		val cursor: Int = Console.commandLength
		if (key == LEFT) {
			Console.commandLength = previousWord(text, cursor)
			return true
		}
		if (key == RIGHT) {
			Console.commandLength = nextWord(text, cursor)
			return true
		}
		if (key == BACKSPACE) {
			val start: Int = previousWord(text, cursor)
			Console.commandString = text!!.substring(0, start) + text.substring(cursor)
			Console.commandLength = start
			return true
		}
		if (key == DELETE) {
			val end: Int = nextWord(text, cursor)
			Console.commandString = text!!.substring(0, cursor) + text.substring(end)
			return true
		}
		return false
	}

	/** The start of the word before the cursor, skipping the spaces right before it. */
	private fun previousWord(text: String?, cursor: Int): Int {
		var i: Int = cursor
		while (i > 0 && text!![i - 1] == ' ') {
			i--
		}
		while (i > 0 && text!![i - 1] != ' ') {
			i--
		}
		return i
	}

	/** The end of the word after the cursor, and the spaces after it. */
	private fun nextWord(text: String?, cursor: Int): Int {
		var i: Int = cursor
		while (i < text!!.length && text[i] != ' ') {
			i++
		}
		while (i < text!!.length && text[i] == ' ') {
			i++
		}
		return i
	}
}
