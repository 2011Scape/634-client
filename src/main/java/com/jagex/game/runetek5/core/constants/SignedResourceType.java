package com.jagex.game.runetek5.core.constants;

/**
 * Not part of the original client, which passes the kind of a SignLink request as an int. The kinds
 * are the 667 client's names, but OPEN_CACHE_FILE (12), which it leaves unnamed: SignLink opens that
 * file in the game's cache directory, where PREFERENCES opens one at the top of it.
 */
public enum SignedResourceType {

	SOCKET,

	START_THREAD,

	HOSTNAME,

	OPEN_STREAM,

	FULLSCREEN_MODES,

	FULLSCREEN_ENTER,

	FULLSCREEN_EXIT,

	METHOD,

	FIELD,

	OPEN_CACHE_FILE,

	PREFERENCES,

	MOVE_MOUSE,

	UPDATE_MOUSE_COMPONENT,

	OPEN_PAGE,

	UPDATE_MOUSE_CURSOR,

	GET_CLIPBOARD_CONTENTS,

	SET_CLIPBOARD_CONTENTS,

	ADDRESS,

	SOCKET_PROXY
}
