package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.Graphics
import kotlin.jvm.JvmStatic
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Not part of the original client: RuneLite's ProgressPieComponent, for the Woodcutting and Mining respawn
 * timers: a circle [DIAMETER] across, filled from the top anticlockwise as [progress] goes 0 to 1, with its
 * outline. The fill is a polygon, as a translucent filled circle draws nothing on the HD toolkit.
 */
object ProgressPie {

	const val DIAMETER: Int = 25

	private const val STEPS: Int = 48

	private val xs: IntArray = IntArray(STEPS + 2)
	private val ys: IntArray = IntArray(STEPS + 2)

	@JvmStatic
	fun draw(g: Graphics, x: Int, y: Int, progress: Float, fill: Int, border: Int) {
		val radius: Double = DIAMETER / 2.0
		val p: Float = progress.coerceIn(0f, 1f)
		val steps: Int = maxOf(1, (STEPS * p).toInt())
		if (p > 0f) {
			xs[0] = x
			ys[0] = y
			for (i in 0..steps) {
				// Java2D's arc: start at 90 degrees, a positive extent going anticlockwise on the screen
				val angle: Double = PI / 2.0 + 2.0 * PI * p * i / steps
				xs[i + 1] = x + kotlin.math.round(cos(angle) * radius).toInt()
				ys[i + 1] = y - kotlin.math.round(sin(angle) * radius).toInt()
			}
			g.fillPolygon(xs, ys, steps + 2, fill)
		}
		for (i in 0 until STEPS) {
			val a: Double = 2.0 * PI * i / STEPS
			val b: Double = 2.0 * PI * (i + 1) / STEPS
			g.line(x + kotlin.math.round(cos(a) * radius).toInt(), y - kotlin.math.round(sin(a) * radius).toInt(), x + kotlin.math.round(cos(b) * radius).toInt(), y - kotlin.math.round(sin(b) * radius).toInt(), border)
		}
	}
}
