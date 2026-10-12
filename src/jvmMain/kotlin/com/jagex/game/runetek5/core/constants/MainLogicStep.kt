package com.jagex.game.runetek5.core.constants

/**
 * Not part of the original client, which keeps the client's main state as an int from 0 to 14; the
 * id is that int. Steps 1 to 11 carry the 667 client's names; the others are named for what the
 * client does in them.
 */
enum class MainLogicStep {

	/** Before loading starts. */
	STEP_STARTUP,

	STEP_LOADING_1,

	STEP_LOADING_2,

	STEP_LOGIN_SCREEN,

	STEP_LOGIN_SCREEN_MAP_BUILD,

	STEP_LOGGING_IN_FROM_LOGINSCREEN_TO_LOBBY,

	STEP_LOGGING_IN_FROM_LOGINSCREEN_TO_GAME,

	STEP_LOBBY_SCREEN,

	STEP_LOBBY_SCREEN_MAP_BUILD,

	STEP_LOGGING_IN_FROM_LOBBYSCREEN_TO_GAME,

	STEP_GAME_SCREEN,

	STEP_GAME_SCREEN_MAP_BUILD,

	/** Logged out of the game to the lobby: logging in to the lobby while the game still shows. */
	STEP_LOGGING_IN_FROM_GAME_TO_LOBBY,

	/** The connection to the world was lost: logging in again with the reconnect request. */
	STEP_RECONNECTING,

	/** The cache could not be fetched; the client stops drawing. */
	STEP_ERROR;

	/** The step's number in the original client. */
	fun id(): Int {
		return this.ordinal
	}
}
