package com.jagex.game.runetek5.plugin.api.events

import kotlin.jvm.JvmField

/** See [Events.GAME_TICK]. One reused instance. */
class GameTick {
	@JvmField
	var cycle: Int = 0

	@JvmField
	var serverTick: Int = 0
}
