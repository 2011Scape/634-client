package com.jagex.game.runetek5.plugin.api

/** An npc type from the cache. */
interface NpcDefinition {
	fun id(): Int

	/** The name, "null" for an npc that has none. */
	fun name(): String?

	/** The right-click option [i] (0 to 4), or null for none. */
	fun action(i: Int): String?

	/** The npc's size in tiles along one side. */
	fun size(): Int

	/** The combat level, 0 for an npc without one. */
	fun combatLevel(): Int

	/** Whether the npc is drawn at all. */
	fun isVisible(): Boolean

	/** Whether the npc has a dot on the minimap. */
	fun isOnMinimap(): Boolean

	/** The icon over the npc's head, an index into the head icons sprite group, or -1 for none. */
	fun headIcon(): Int
}
