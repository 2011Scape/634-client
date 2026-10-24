package com.jagex.game.runetek5.graphics.model

import kotlin.jvm.JvmField

/**
 * Not part of the original client: a drawn model's triangles on the screen, as Model.projectFaces leaves
 * them, for the plugins' actor outlines (plugin.ActorGeometry). Each triangle is six ints, x and y of its
 * three corners, in the picking's screen space (Model.picked's); a triangle with a corner behind the
 * camera is left out, as picking leaves it out. One reused buffer that grows as needed.
 */
class FaceBuffer {
	@JvmField
	var points: IntArray = IntArray(6 * 1024)

	/** How many triangles [points] holds. */
	@JvmField
	var count: Int = 0

	fun clear() {
		count = 0
	}

	fun add(ax: Int, ay: Int, bx: Int, by: Int, cx: Int, cy: Int) {
		if ((count + 1) * 6 > points.size) {
			points = points.copyOf(points.size * 2)
		}
		val at: Int = count * 6
		points[at] = ax
		points[at + 1] = ay
		points[at + 2] = bx
		points[at + 3] = by
		points[at + 4] = cx
		points[at + 5] = cy
		count++
	}
}
