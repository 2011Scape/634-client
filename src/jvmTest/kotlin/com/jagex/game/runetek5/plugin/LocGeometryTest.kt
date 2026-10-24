package com.jagex.game.runetek5.plugin

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Not part of the original client: a shape sampled coarse then finer along its edge, and the outline's fade (LocGeometry). */
class LocGeometryTest {

	/** A disc of radius 40 round (60, 60). */
	private fun disc(x: Int, y: Int): Boolean = (x - 60) * (x - 60) + (y - 60) * (y - 60) <= 40 * 40

	@Test
	fun theEdgeIsSampledAgainFiner() {
		var picks: Int = 0
		val mask: LocGeometry.Mask = LocGeometry.sampleShape(0, 0, 120, 120, 400, 6000) { x, y ->
			picks++
			disc(x, y)
		}
		assertTrue(mask.step < 4, "refined to " + mask.step + " px")
		assertTrue(picks < 120 * 120 / 2, "only the edge sampled again: " + picks + " picks")
		// every cell agrees with the disc at its sample point, inside, outside and on the edge
		var wrong: Int = 0
		for (row in 0 until mask.rows) {
			for (column in 0 until mask.columns) {
				if (mask.hit(column, row) != disc(column * mask.step, row * mask.step)) {
					wrong++
				}
			}
		}
		assertEquals(0, wrong)
	}

	@Test
	fun aSmallShapeIsSampledOnce() {
		val mask: LocGeometry.Mask = LocGeometry.sampleShape(0, 0, 20, 20, 1000, 1000) { x, y -> x < 10 && y < 10 }
		assertEquals(1, mask.step)
		assertEquals(100, mask.hits.count { it })
	}

	@Test
	fun theOutlineFadesOutward() {
		val alpha: Int = 0x90
		// width 4, feather 4: the pixel next to the shape strongest, none past the width
		val near: Int = LocGeometry.outlineAlpha(1f, 1, 4, 4, alpha)
		val far: Int = LocGeometry.outlineAlpha(16f, 1, 4, 4, alpha)
		assertTrue(near > far && far > 0, "near " + near + ", far " + far)
		assertEquals(0, LocGeometry.outlineAlpha(36f, 1, 4, 4, alpha))
		// no feather: full all the way
		assertEquals(alpha, LocGeometry.outlineAlpha(9f, 1, 4, 0, alpha))
		assertEquals(0, LocGeometry.outlineAlpha(Float.MAX_VALUE, 1, 4, 0, alpha))
	}
}
