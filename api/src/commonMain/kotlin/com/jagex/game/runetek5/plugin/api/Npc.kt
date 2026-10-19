package com.jagex.game.runetek5.plugin.api

interface Npc : Actor {
	/** The npc's type (config) id, after any transformation the player's state applies to it. */
	fun typeId(): Int

	fun name(): String?

	/** The combat level, 0 for an npc without one. */
	fun combatLevel(): Int

	/** The npc's index on this world. */
	fun index(): Int
}
