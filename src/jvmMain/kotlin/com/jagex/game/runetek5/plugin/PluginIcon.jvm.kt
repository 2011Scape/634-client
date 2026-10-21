package com.jagex.game.runetek5.plugin

import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import javax.imageio.ImageIO

/* Not part of the original client: the JVM side of decodePngPixels, through ImageIO. */
@Suppress("TooGenericExceptionCaught", "SwallowedException") // any failure to decode means "no icon", nothing more
actual fun decodePngPixels(data: ByteArray): PluginIcon? {
	try {
		val image: BufferedImage = ImageIO.read(ByteArrayInputStream(data)) ?: return null
		val width: Int = image.width
		val height: Int = image.height
		if (width <= 0 || height <= 0 || width > PluginIcon.MAX_SIZE || height > PluginIcon.MAX_SIZE) {
			return null
		}
		val pixels: IntArray = IntArray(width * height)
		image.getRGB(0, 0, width, height, pixels, 0, width)
		return PluginIcon(width, height, pixels)
	} catch (error: Exception) {
		return null
	}
}
