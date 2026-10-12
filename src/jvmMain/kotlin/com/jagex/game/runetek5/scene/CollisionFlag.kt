package com.jagex.game.runetek5.scene

object CollisionFlag {

	const val WALL_NORTH: Int = 0x2

	const val WALL_EAST: Int = 0x8

	const val WALL_SOUTH: Int = 0x20

	const val WALL_WEST: Int = 0x80

	const val BLOCKWALK: Int = 0x200000

	const val WALL_NORTH_BREAKROUTEFINDING: Int = 0x800000

	const val WALL_EAST_BREAKROUTEFINDING: Int = 0x2000000

	const val WALL_SOUTH_BREAKROUTEFINDING: Int = 0x8000000

	const val WALL_WEST_BLOCK_BREAKROUTEFINDING: Int = 0x20000000
}
