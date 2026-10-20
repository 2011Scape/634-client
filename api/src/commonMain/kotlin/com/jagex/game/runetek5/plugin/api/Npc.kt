package com.jagex.game.runetek5.plugin.api

interface Npc : Actor {
	/** The npc's type (config) id, after any transformation the player's state applies to it. */
	fun typeId(): Int

	/** The npc's name as shown, or null. */
	fun name(): String?

	/** The combat level, 0 for an npc without one. */
	fun combatLevel(): Int

	/** The npc's index on this world. */
	fun index(): Int

	/** The icon over the npc's head, an index into the head icons sprite group, or -1 for none. */
	fun headIcon(): Int

	/** The npc's type as it shows now, or null once the npc has left the scene. */
	fun type(): NpcDefinition?
}
