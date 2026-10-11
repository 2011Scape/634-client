package com.jagex.game.runetek5.scene;

public final class CollisionFlag {

	public static final int WALL_NORTH = 0x2;

	public static final int WALL_EAST = 0x8;

	public static final int WALL_SOUTH = 0x20;

	public static final int WALL_WEST = 0x80;

	public static final int BLOCKWALK = 0x200000;

	public static final int WALL_NORTH_BREAKROUTEFINDING = 0x800000;

	public static final int WALL_EAST_BREAKROUTEFINDING = 0x2000000;

	public static final int WALL_SOUTH_BREAKROUTEFINDING = 0x8000000;

	public static final int WALL_WEST_BLOCK_BREAKROUTEFINDING = 0x20000000;

	private CollisionFlag() {
		/* empty */
	}
}
