package com.jagex.game.runetek5.plugin.api

/** What a location on a tile is, for [LocVisitor]. */
object LocKind {
	/** A wall, fence or door along the tile's edge. */
	const val WALL: Int = 0

	/** A decoration on a wall: a torch, a painting. */
	const val WALL_DECORATION: Int = 1

	/** A decoration on the floor: a rug, a puddle. */
	const val GROUND_DECORATION: Int = 2

	/** An object standing on the tile: a tree, a booth, a rock. */
	const val INTERACTIVE: Int = 3
}
