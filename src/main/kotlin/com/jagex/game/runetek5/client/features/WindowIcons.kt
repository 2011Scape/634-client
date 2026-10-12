package com.jagex.game.runetek5.client.features

import java.awt.Frame
import java.awt.Image
import java.net.URL
import java.util.ArrayList
import javax.imageio.ImageIO

/**
 * Not part of the original client: the client's windows carry Void's icon (icons/icon-*.png), in
 * every size the desktop may ask for, instead of the JVM's default.
 */
object WindowIcons {

	private val SIZES: IntArray = intArrayOf(16, 32, 64, 128, 256)

	private var icons: MutableList<Image?>? = null

	/** Gives the frame Void's icon; a size that cannot be read is left out. */
	@JvmStatic
	fun apply(frame: Frame?) {
		if (icons == null) {
			icons = ArrayList<Image?>()
			var i: Int = 0
			while (i < SIZES.size) {
				val resource: URL? = WindowIcons::class.java.getResource("/icons/icon-" + SIZES[i] + ".png")
				if (resource == null) {
					i++
					continue
				}
				try {
					icons!!.add(ImageIO.read(resource))
				} catch (ex: Exception) {
					/* leave this size out */
				}
				i++
			}
		}
		if (!icons!!.isEmpty()) {
			frame!!.setIconImages(icons)
		}
	}
}
