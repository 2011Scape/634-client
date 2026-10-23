package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.InventoryTags
import com.jagex.game.runetek5.plugin.api.PluginConfig
import java.lang.reflect.Proxy
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** Not part of the original client: Inventory Tags' tags and the outline and fill (API 30) they draw. */
class InventoryTagsTest {

	@AfterTest
	fun tidy() {
		InventoryTags.tags.clear()
	}

	@Test
	fun theOutlineIsTheClearPixelsBesideTheShape() {
		// a 3 by 3 picture with its middle pixel set
		val pixels = IntArray(9)
		pixels[4] = -0x1
		val out: IntArray = ClientApiImpl.outlinePixels(pixels, 3, 3, 0xFFFF0000.toInt())
		val red = 0xFFFF0000.toInt()
		assertEquals(listOf(0, red, 0, red, 0, red, 0, red, 0), out.toList())
	}

	@Test
	fun theFillIsTheShapeInTheColour() {
		val out: IntArray = ClientApiImpl.fillPixels(intArrayOf(0, 0x01000000, -0x1), 0x8000FF00.toInt())
		assertEquals(listOf(0, 0x8000FF00.toInt(), 0x8000FF00.toInt()), out.toList())
	}

	@Test
	fun theTagsAreReadFromTheSettings() {
		val values = mapOf("tag_995" to (0xFF00FF00.toInt()).toString(), "showTagOutline" to "true", "other" to "1")
		val config: PluginConfig = Proxy.newProxyInstance(PluginConfig::class.java.classLoader, arrayOf(PluginConfig::class.java)) { _, method, args ->
			when (method.name) {
				"keys" -> values.keys.toTypedArray()
				"getColour", "getInt" -> values[args[0] as String]?.toInt() ?: args[1]
				"getBoolean" -> values[args[0] as String]?.toBoolean() ?: args[1]
				"get" -> values[args[0] as String] ?: args[1]
				else -> null
			}
		} as PluginConfig
		InventoryTags.readSettings(config)
		assertEquals(mapOf(995 to 0xFF00FF00.toInt()), InventoryTags.tags.toMap())
	}

	@Test
	fun theOfferedColoursAreTheUsedOnesThenDefaultsNotTheItemsOwn() {
		InventoryTags.tags[1] = 0xFF123456.toInt()
		val offered: IntArray = InventoryTags.offeredColours(0xFFFF0000.toInt())
		assertEquals(0xFF123456.toInt(), offered[0])
		assertFalse(0xFFFF0000.toInt() in offered)
		assertTrue(offered.size == InventoryTags.MAX_OFFERED)
	}
}
