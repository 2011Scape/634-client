package com.jagex.game.runetek5.plugin

import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: a plugin's icon as ARGB pixels, for the plugin manager's rows.
 * A built-in's is the client's own resource `/plugins/<id>.png`; a jar's is the PNG its manifest's
 * `Plugin-Icon` names. Shown at [SIZE] square, scaled when the image is another size; up to
 * [MAX_SIZE] square is accepted.
 */
class PluginIcon(@JvmField val width: Int, @JvmField val height: Int, @JvmField val pixels: IntArray) {

	companion object {
		/** The size the manager shows an icon at. */
		const val SIZE: Int = 25

		const val MAX_SIZE: Int = 64

		/** The icon in [png], or null when there is none, it is not a PNG the platform can decode, or it is larger than [MAX_SIZE]. */
		@JvmStatic
		fun decode(png: ByteArray?): PluginIcon? {
			if (png == null || png.isEmpty()) {
				return null
			}
			val icon: PluginIcon = decodePngPixels(png) ?: return null
			if (icon.width <= 0 || icon.height <= 0 || icon.width > MAX_SIZE || icon.height > MAX_SIZE || icon.pixels.size < icon.width * icon.height) {
				return null
			}
			return icon
		}
	}
}

/** The ARGB pixels of the PNG [data], or null when the platform cannot decode it (the browser has no synchronous decoder). */
expect fun decodePngPixels(data: ByteArray): PluginIcon?
