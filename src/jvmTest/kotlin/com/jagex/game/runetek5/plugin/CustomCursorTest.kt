package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.CustomCursor
import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.Image
import java.lang.reflect.Proxy
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/** Not part of the original client: Custom Cursor's pictures from the client's resources (CustomCursor.pictures). */
class CustomCursorTest {

	private val client: ClientApi = Proxy.newProxyInstance(ClientApi::class.java.classLoader, arrayOf(ClientApi::class.java)) { _, method, args ->
		when (method.name) {
			"createImage" -> PluginImage(args[0] as Int, args[1] as Int, args[2] as IntArray)
			else -> null
		}
	} as ClientApi

	@Test
	fun theSixPicturesAreRuneLitesInItsOrder() {
		val pictures: Array<Image>? = CustomCursor.pictures(client)
		assertNotNull(pictures)
		assertEquals(6, pictures.size)
		assertEquals(32, pictures[0].width())
		assertEquals(29, pictures[5].width())
		assertEquals(27, pictures[5].height())
		// the gold cursor's tip is drawn, its far corner clear
		val gold = pictures[0] as PluginImage
		assertEquals(0, gold.pixels[31 * 32 + 31] ushr 24)
	}

	@Test
	fun theStylesAreRuneLitesEight() {
		assertEquals(8, CustomCursor.STYLES.size)
		assertEquals("Equipped weapon", CustomCursor.STYLES[CustomCursor.EQUIPPED_WEAPON])
		assertEquals("Custom image", CustomCursor.STYLES[CustomCursor.CUSTOM_IMAGE])
	}
}
