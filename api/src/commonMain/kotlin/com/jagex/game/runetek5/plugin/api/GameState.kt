package com.jagex.game.runetek5.plugin.api

/** Where the client is, in broad strokes: the states a plugin can tell apart. */
enum class GameState {
	/** Starting up: nothing to see yet. */
	LOADING,

	LOGIN_SCREEN,

	LOBBY,

	/** In the game, the local player and the scene exist. A map rebuild stays in this state. */
	LOGGED_IN,
}
