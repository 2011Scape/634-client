package com.jagex.game.runetek5.client.features

import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.io.FileReader
import java.io.Reader
import java.util.Properties

/* Not part of the original client: the JVM side of FeaturePlatform. */

actual fun readPacketSizes(file: String, sizes: IntArray) {
	val properties: Properties = Properties()
	try {
		val reader: Reader = FileReader(file)
		try {
			properties.load(reader)
		} finally {
			reader.close()
		}
		val iterator: Iterator<Any?> = properties.keys.iterator()
		while (iterator.hasNext()) {
			val key: String? = iterator.next() as String?
			val opcode: Int = Integer.parseInt(key!!.trim { it <= ' ' })
			val size: Int = Integer.parseInt(properties.getProperty(key).trim { it <= ' ' })
			if (opcode < 0 || opcode >= sizes.size || size < -2) {
				throw IllegalArgumentException(key + "=" + size)
			}
			sizes[opcode] = size
		}
	} catch (ex: Exception) {
		System.err.println("client.packets: cannot use " + file + ": " + ex)
	}
}

actual fun scaleGraphics2D(graphics: Graphics?, scale: Double, whole: Boolean): Graphics? {
	if (graphics !is Graphics2D) {
		return graphics
	}
	graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, if (whole)
		RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR
	else
		RenderingHints.VALUE_INTERPOLATION_BILINEAR)
	graphics.scale(scale, scale)
	return graphics
}
