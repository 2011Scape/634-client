package com.jagex.game.runetek5.plugin.api

/** A location (object) type from the cache: a tree, a door, a bank booth. */
interface LocDefinition {
	fun id(): Int

	fun name(): String?

	/** The right-click option [i] (0 to 4), or null for none. */
	fun action(i: Int): String?

	/** The location's size in tiles, before its rotation. */
	fun sizeX(): Int

	fun sizeY(): Int

	fun isMembers(): Boolean

	/** The icon the location has on the minimap and the world map, or -1 for none. */
	fun mapIconId(): Int
}
