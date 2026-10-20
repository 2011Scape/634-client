package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.plugin.api.CollisionFlags
import com.jagex.game.runetek5.scene.CollisionFlag
import kotlin.test.Test
import kotlin.test.assertEquals

/** Not part of the original client: the API's copy of the movement flags must match the client's (CollisionFlags, CollisionFlag). */
class CollisionFlagsTest {

	@Test
	fun theApisBitsAreTheClients() {
		assertEquals(CollisionFlag.WALL_NORTH, CollisionFlags.WALL_NORTH)
		assertEquals(CollisionFlag.WALL_EAST, CollisionFlags.WALL_EAST)
		assertEquals(CollisionFlag.WALL_SOUTH, CollisionFlags.WALL_SOUTH)
		assertEquals(CollisionFlag.WALL_WEST, CollisionFlags.WALL_WEST)
		assertEquals(CollisionFlag.BLOCKWALK, CollisionFlags.FLOOR)
	}
}
