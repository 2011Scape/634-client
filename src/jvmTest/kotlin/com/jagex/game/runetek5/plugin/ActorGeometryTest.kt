package com.jagex.game.runetek5.plugin

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Not part of the original client: an actor's projected triangles filled into a silhouette mask (ActorGeometry). */
class ActorGeometryTest {

	private fun hits(mask: LocGeometry.Mask): Int = mask.hits.count { it }

	@Test
	fun aTriangleFillsAboutHalfItsBox() {
		// a right triangle 40 by 40, either winding
		for (points in listOf(intArrayOf(10, 10, 50, 10, 10, 50), intArrayOf(10, 10, 10, 50, 50, 10))) {
			val mask: LocGeometry.Mask = assertNotNull(ActorGeometry.rasterise(points, 1, ActorGeometry.MAX_CELLS))
			assertEquals(1, mask.step)
			val box: Int = mask.columns * mask.rows
			val ratio: Double = hits(mask).toDouble() / box
			assertTrue(ratio in 0.45..0.56, "filled " + ratio + " of the box")
			assertTrue(mask.hit(1, 1), "near the right angle")
			assertFalse(mask.hit(mask.columns - 2, mask.rows - 2), "across the hypotenuse")
		}
	}

	@Test
	fun twoTrianglesMakeASquareAndASilhouetteIsNotItsBox() {
		val square: IntArray = intArrayOf(0, 0, 30, 0, 30, 30, 0, 0, 30, 30, 0, 30)
		val mask: LocGeometry.Mask = assertNotNull(ActorGeometry.rasterise(square, 2, ActorGeometry.MAX_CELLS))
		assertEquals(31 * 31, hits(mask), "every centre 0 to 30 inside, edges included")
		// an L of two squares: its box is a third empty
		val l: IntArray = square + intArrayOf(30, 0, 60, 0, 60, 30, 30, 0, 60, 30, 30, 30) + intArrayOf(0, 30, 30, 30, 30, 60, 0, 30, 30, 60, 0, 60)
		val shape: LocGeometry.Mask = assertNotNull(ActorGeometry.rasterise(l, 6, ActorGeometry.MAX_CELLS))
		val ratio: Double = hits(shape).toDouble() / (shape.columns * shape.rows)
		assertTrue(ratio < 0.8, "an L fills " + ratio + " of its box")
		val xs = IntArray(64)
		val ys = IntArray(64)
		assertTrue(LocGeometry.hull(shape, xs, ys) >= 4)
	}

	@Test
	fun aBigShapeGetsCoarserCellsAndNothingIsNothing() {
		val big: IntArray = intArrayOf(0, 0, 400, 0, 0, 400)
		val mask: LocGeometry.Mask = assertNotNull(ActorGeometry.rasterise(big, 1, ActorGeometry.MAX_CELLS))
		assertTrue(mask.step > 1)
		assertTrue(mask.columns * mask.rows <= ActorGeometry.MAX_CELLS)
		assertNull(ActorGeometry.rasterise(IntArray(0), 0, ActorGeometry.MAX_CELLS))
		// a flat triangle fills nothing but does not fail
		val flat: LocGeometry.Mask = assertNotNull(ActorGeometry.rasterise(intArrayOf(0, 0, 10, 10, 20, 20), 1, ActorGeometry.MAX_CELLS))
		assertEquals(0, hits(flat))
	}
}
