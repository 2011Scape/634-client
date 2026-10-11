package com.jagex.game.runetek5.client.window;

/**
 * Not part of the original client, which passes the window mode as an int: 1 for a fixed size
 * window, 2 for a resizable one, 3 for full screen. The id is that int, as the screen size
 * preference stores it, scripts read and set it, and the login packet sends it.
 */
public enum WindowModeType {

	FIXED(1),

	RESIZABLE(2),

	FULLSCREEN(3);

	public final int id;

	WindowModeType(int id) {
		this.id = id;
	}

	/**
	 * The mode of an id. The screen size preference only holds 1 to 3, and the script that sets the
	 * mode only passes 1 or 2.
	 */
	public static WindowModeType fromId(int id) {
		for (WindowModeType mode : values()) {
			if (mode.id == id) {
				return mode;
			}
		}
		throw new IllegalArgumentException("window mode " + id);
	}
}
