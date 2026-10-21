package com.jagex.game.runetek5.plugin

import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/** Not part of the original client: PluginIcon.decode takes a PNG of up to 64x64 and nothing else. */
class PluginIconTest {

	private fun png(width: Int, height: Int): ByteArray {
		val image: BufferedImage = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
		image.setRGB(0, 0, 0xFF123456.toInt())
		val out: ByteArrayOutputStream = ByteArrayOutputStream()
		ImageIO.write(image, "png", out)
		return out.toByteArray()
	}

	@Test
	fun aSmallPngDecodesToItsPixels() {
		val icon: PluginIcon = assertNotNull(PluginIcon.decode(png(PluginIcon.SIZE, PluginIcon.SIZE)))
		assertEquals(PluginIcon.SIZE, icon.width)
		assertEquals(PluginIcon.SIZE, icon.height)
		assertEquals(0xFF123456.toInt(), icon.pixels[0])
		assertEquals(0, icon.pixels[1], "transparent black elsewhere")
	}

	@Test
	fun theLargestAcceptedIsMaxSizeSquare() {
		assertNotNull(PluginIcon.decode(png(PluginIcon.MAX_SIZE, PluginIcon.MAX_SIZE)))
		assertNull(PluginIcon.decode(png(PluginIcon.MAX_SIZE + 1, 1)))
		assertNull(PluginIcon.decode(png(1, PluginIcon.MAX_SIZE + 1)))
	}

	@Test
	fun whatIsNotAPngIsNoIcon() {
		assertNull(PluginIcon.decode(null))
		assertNull(PluginIcon.decode(ByteArray(0)))
		assertNull(PluginIcon.decode("just text".toByteArray()))
		assertNull(PluginIcon.decode(ByteArray(100) { 0x89.toByte() }))
	}
}
