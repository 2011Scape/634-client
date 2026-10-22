package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.Minimap
import com.jagex.game.runetek5.client.features.TileIndicators
import com.jagex.game.runetek5.client.features.TileOutline
import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.Graphics
import com.jagex.game.runetek5.plugin.api.PluginConfig
import com.jagex.game.runetek5.plugin.api.Viewport
import java.lang.reflect.Proxy
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** Not part of the original client: Tile Indicators against RuneLite's rules, and the walk destination of API 15. */
class TileIndicatorsTest {

	/** The drawing calls made, by name. */
	private val calls: ArrayList<String> = ArrayList()

	private var onScreen: Boolean = true

	private val g: Graphics = Proxy.newProxyInstance(Graphics::class.java.classLoader, arrayOf(Graphics::class.java)) { _, method, _ ->
		calls.add(method.name)
		null
	} as Graphics

	private val client: ClientApi = Proxy.newProxyInstance(ClientApi::class.java.classLoader, arrayOf(ClientApi::class.java)) { _, method, args ->
		when (method.name) {
			"tilePolygon" -> {
				val xs: IntArray = args[4] as IntArray
				val ys: IntArray = args[5] as IntArray
				for (i in 0 until 4) {
					xs[i] = i * 10
					ys[i] = i * 5
				}
				onScreen
			}
			else -> null
		}
	} as ClientApi

	private val view: Viewport = Viewport().also {
		it.x = 4
		it.y = 4
	}

	private val xs: IntArray = IntArray(4)

	private val ys: IntArray = IntArray(4)

	@AfterTest
	fun reset() {
		Minimap.minimapFlagX = -1
		Minimap.minimapFlagY = -1
	}

	@Test
	fun fillThenBorderInTheView() {
		assertTrue(TileOutline.draw(g, view, client, 10, 10, 0, 1, TileIndicators.GRAY, TileIndicators.FILL, 2, xs, ys))
		assertEquals(listOf("fillPolygon", "thickLine", "thickLine", "thickLine", "thickLine"), calls)
		// the polygon is view-relative: the view's place is added
		assertEquals(4, xs[0])
		assertEquals(14, xs[1])
	}

	@Test
	fun aColourWithNoAlphaIsNotDrawn() {
		// RuneLite's hovered tile: an invisible border, a light fill
		TileOutline.draw(g, view, client, 10, 10, 0, 1, TileIndicators.NONE, TileIndicators.FILL, 2, xs, ys)
		assertEquals(listOf("fillPolygon"), calls)
		calls.clear()
		TileOutline.draw(g, view, client, 10, 10, 0, 1, TileIndicators.CYAN, 0, 0, xs, ys)
		assertTrue(calls.isEmpty())
	}

	@Test
	fun offTheScreenDrawsNothing() {
		onScreen = false
		assertFalse(TileOutline.draw(g, view, client, 10, 10, 0, 1, TileIndicators.GRAY, TileIndicators.FILL, 2, xs, ys))
		assertTrue(calls.isEmpty())
	}

	@Test
	fun defaultsAreRuneLites() {
		val empty: PluginConfig = Proxy.newProxyInstance(PluginConfig::class.java.classLoader, arrayOf(PluginConfig::class.java)) { _, method, args ->
			if (method.name.startsWith("get")) args[1] else null
		} as PluginConfig
		TileIndicators.readSettings(empty)
		assertTrue(TileIndicators.highlightDestinationTile)
		assertFalse(TileIndicators.highlightHoveredTile || TileIndicators.highlightCurrentTile)
		assertEquals(0xFF808080.toInt(), TileIndicators.destinationColour)
		assertEquals(50 shl 24, TileIndicators.hoveredFill)
		assertEquals(0, TileIndicators.hoveredColour)
		assertEquals(2, TileIndicators.currentWidth)
	}

	@Test
	fun theDestinationIsTheMinimapFlag() {
		assertEquals(-1, ClientApiImpl.destinationTileX())
		Minimap.minimapFlagX = 52
		Minimap.minimapFlagY = 40
		assertEquals(52, ClientApiImpl.destinationTileX())
		assertEquals(40, ClientApiImpl.destinationTileY())
		// a cleared flag a region change moved, or one outside the scene, is no destination
		Minimap.minimapFlagX = 7
		Minimap.minimapFlagY = -9
		assertEquals(-1, ClientApiImpl.destinationTileX())
		Minimap.minimapFlagX = 500
		Minimap.minimapFlagY = 40
		assertEquals(-1, ClientApiImpl.destinationTileY())
	}
}
