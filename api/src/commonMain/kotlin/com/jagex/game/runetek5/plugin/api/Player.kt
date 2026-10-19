package com.jagex.game.runetek5.plugin.api

interface Player : Actor {
	fun name(): String?

	fun combatLevel(): Int

	/** The player's index on this world, 0 to 2047. */
	fun index(): Int

	fun isLocalPlayer(): Boolean
}
