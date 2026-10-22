package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.PluginImage
import kotlin.jvm.JvmStatic
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.sqrt

/**
 * Not part of the original client: an XP globe's disc and rings as smooth pictures, as Java2D draws
 * RuneLite's with anti-aliasing (the client's polygons have none, and their edges step). Each pixel is
 * sampled 4 by 4 times and covered in proportion; the pictures are made again only when the globe's size,
 * colours or progress change.
 */
class XpGlobeImages {

	/** The disc, drawn under the icon. */
	var under: PluginImage? = null
		private set

	/** The hover's darkening, drawn over the icon when the globe is hovered. */
	var dark: PluginImage? = null
		private set

	/** The outline ring and the progress arc over it, drawn over the icon. */
	var over: PluginImage? = null
		private set

	private var underKey: String = ""

	private var overKey: String = ""

	/**
	 * The pictures for a globe [orb] across in a square [canvas] wide (the globe centred in it): the disc in
	 * [background], the darkening, the [outlineWidth] ring in [outline] and the [arcWidth] arc in [arc] from
	 * [startDegrees] through [sweepDegrees] (Arc2D's: 0 at three o'clock, positive anticlockwise).
	 */
	fun update(orb: Int, canvas: Int, background: Int, outline: Int, outlineWidth: Int, arc: Int, arcWidth: Int, startDegrees: Double, sweepDegrees: Double) {
		val newUnderKey: String = "$orb/$canvas/$background"
		if (newUnderKey != underKey || under == null) {
			underKey = newUnderKey
			under = disc(orb, canvas, background)
			dark = disc(orb, canvas, DARK)
		}
		// the arc's sweep to a tenth of a degree: finer would make a picture for every experience drop
		val sweep: Double = kotlin.math.round(sweepDegrees * 10.0) / 10.0
		val newOverKey: String = "$orb/$canvas/$outline/$outlineWidth/$arc/$arcWidth/$startDegrees/$sweep"
		if (newOverKey != overKey || over == null) {
			overKey = newOverKey
			over = rings(orb, canvas, outline, outlineWidth, arc, arcWidth, startDegrees, sweep)
		}
	}

	companion object {
		/** The hover's darkening, RuneLite's (0, 0, 0, 180). */
		private const val DARK: Int = 180 shl 24

		private const val SAMPLES: Int = 4

		/** A disc [orb] across centred in [canvas], in [colour]. */
		@JvmStatic
		fun disc(orb: Int, canvas: Int, colour: Int): PluginImage {
			val pixels = IntArray(canvas * canvas)
			val c: Double = canvas / 2.0
			val r: Double = orb / 2.0
			val fill: Int = colour
			for (py in 0 until canvas) {
				for (px in 0 until canvas) {
					var hits: Int = 0
					for (sy in 0 until SAMPLES) {
						for (sx in 0 until SAMPLES) {
							val dx: Double = px + (sx + 0.5) / SAMPLES - c
							val dy: Double = py + (sy + 0.5) / SAMPLES - c
							if (dx * dx + dy * dy <= r * r) {
								hits++
							}
						}
					}
					pixels[py * canvas + px] = cover(fill, hits)
				}
			}
			return PluginImage(canvas, canvas, pixels)
		}

		/** The outline ring (whole) and the arc over it, each as wide as given about the circle [orb] across. */
		@JvmStatic
		fun rings(orb: Int, canvas: Int, outline: Int, outlineWidth: Int, arc: Int, arcWidth: Int, startDegrees: Double, sweepDegrees: Double): PluginImage {
			val pixels = IntArray(canvas * canvas)
			val c: Double = canvas / 2.0
			val r: Double = orb / 2.0
			for (py in 0 until canvas) {
				for (px in 0 until canvas) {
					var ringHits: Int = 0
					var arcHits: Int = 0
					for (sy in 0 until SAMPLES) {
						for (sx in 0 until SAMPLES) {
							val dx: Double = px + (sx + 0.5) / SAMPLES - c
							val dy: Double = py + (sy + 0.5) / SAMPLES - c
							val d: Double = sqrt(dx * dx + dy * dy)
							if (kotlin.math.abs(d - r) <= outlineWidth / 2.0) {
								ringHits++
							}
							if (kotlin.math.abs(d - r) <= arcWidth / 2.0 && inSweep(dx, dy, startDegrees, sweepDegrees)) {
								arcHits++
							}
						}
					}
					pixels[py * canvas + px] = over(cover(arc, arcHits), cover(outline, ringHits))
				}
			}
			return PluginImage(canvas, canvas, pixels)
		}

		/** Whether the point ([dx], [dy]) from the centre (screen y down) lies in the arc from [start] through [sweep] degrees. */
		@JvmStatic
		fun inSweep(dx: Double, dy: Double, start: Double, sweep: Double): Boolean {
			if (sweep >= 360.0 || sweep <= -360.0) {
				return true
			}
			if (sweep == 0.0) {
				return false
			}
			// Arc2D's angle: anticlockwise from three o'clock, the screen's y down
			val angle: Double = atan2(-dy, dx) * 180.0 / PI
			// how far from the start in the sweep's direction, 0 to 360
			val along: Double = if (sweep > 0.0) angle - start else start - angle
			val wrapped: Double = ((along % 360.0) + 360.0) % 360.0
			return wrapped <= kotlin.math.abs(sweep)
		}

		/** [argb] with its alpha scaled by [hits] of the 16 samples. */
		private fun cover(argb: Int, hits: Int): Int {
			if (hits == 0) {
				return 0
			}
			val alpha: Int = (argb ushr 24) * hits / (SAMPLES * SAMPLES)
			return (alpha shl 24) or (argb and 0xFFFFFF)
		}

		/** [top] over [bottom], both unpremultiplied ARGB. */
		@JvmStatic
		fun over(top: Int, bottom: Int): Int {
			val ta: Int = top ushr 24
			if (ta == 255) {
				return top
			}
			if (ta == 0) {
				return bottom
			}
			val ba: Int = bottom ushr 24
			val outA: Int = ta + ba * (255 - ta) / 255
			if (outA == 0) {
				return 0
			}
			var argb: Int = outA shl 24
			for (shift in intArrayOf(16, 8, 0)) {
				val t: Int = (top shr shift) and 0xFF
				val b: Int = (bottom shr shift) and 0xFF
				val channel: Int = (t * ta + b * ba * (255 - ta) / 255) / outA
				argb = argb or (channel.coerceIn(0, 255) shl shift)
			}
			return argb
		}
	}
}
