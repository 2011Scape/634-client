package com.jagex.game.runetek5.plugin.api

/** The bits of a tile's movement flags, [ClientApi.collisionFlags]. */
object CollisionFlags {
	/** A wall on that edge of the tile, which stops a walk across it. */
	const val WALL_NORTH: Int = 0x2

	const val WALL_EAST: Int = 0x8

	const val WALL_SOUTH: Int = 0x20

	const val WALL_WEST: Int = 0x80

	/** A location stands on the tile. */
	const val LOCATION: Int = 0x100

	/** A floor decoration that blocks the tile. */
	const val FLOOR_DECORATION: Int = 0x40000

	/** The floor itself blocks: water, a cliff. */
	const val FLOOR: Int = 0x200000

	/** Any of the bits that stop a walk onto the tile. */
	const val BLOCKED: Int = LOCATION or FLOOR_DECORATION or FLOOR
}
