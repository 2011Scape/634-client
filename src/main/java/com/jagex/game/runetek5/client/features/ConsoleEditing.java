package com.jagex.game.runetek5.client.features;

import com.jagex.game.runetek5.UpdateZoneFullFollowsProt;
import com.jagex.game.runetek5.client.input.console.Console;

/**
 * Keys the developer console handles with {@code client.console}: Up and Down recall earlier commands,
 * as Page Up and Page Down already do, and Ctrl with Left, Right, Backspace or Delete moves or deletes
 * a word at a time.
 * <p>
 * This class is not part of the original client.
 */
public final class ConsoleEditing {

	private static final int UP = 98;
	private static final int DOWN = 99;
	private static final int LEFT = 96;
	private static final int RIGHT = 97;
	private static final int BACKSPACE = 85;
	private static final int DELETE = 101;
	private static final int CTRL = 0x4;

	private ConsoleEditing() {
		/* empty */
	}

	/** Handles one key; returns whether it was handled, so the console skips its own handling. */
	public static boolean handle(int key, int modifiers) {
		if (key == UP) {
			if (Console.historyIndex < Console.displayedOutput.length) {
				Console.historyIndex++;
				Console.adjustCommandString();
				Console.commandLength = Console.commandString.length();
			}
			return true;
		}
		if (key == DOWN) {
			if (Console.historyIndex > 0) {
				Console.historyIndex--;
				Console.adjustCommandString();
				Console.commandLength = Console.commandString.length();
			}
			return true;
		}
		if ((modifiers & CTRL) == 0) {
			return false;
		}
		String text = Console.commandString;
		int cursor = Console.commandLength;
		if (key == LEFT) {
			Console.commandLength = previousWord(text, cursor);
			return true;
		}
		if (key == RIGHT) {
			Console.commandLength = nextWord(text, cursor);
			return true;
		}
		if (key == BACKSPACE) {
			int start = previousWord(text, cursor);
			Console.commandString = text.substring(0, start) + text.substring(cursor);
			Console.commandLength = start;
			return true;
		}
		if (key == DELETE) {
			int end = nextWord(text, cursor);
			Console.commandString = text.substring(0, cursor) + text.substring(end);
			return true;
		}
		return false;
	}

	/** The start of the word before the cursor, skipping the spaces right before it. */
	private static int previousWord(String text, int cursor) {
		int i = cursor;
		while (i > 0 && text.charAt(i - 1) == ' ') {
			i--;
		}
		while (i > 0 && text.charAt(i - 1) != ' ') {
			i--;
		}
		return i;
	}

	/** The end of the word after the cursor, and the spaces after it. */
	private static int nextWord(String text, int cursor) {
		int i = cursor;
		while (i < text.length() && text.charAt(i) != ' ') {
			i++;
		}
		while (i < text.length() && text.charAt(i) == ' ') {
			i++;
		}
		return i;
	}
}
