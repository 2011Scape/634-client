package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.ScreenMarkers
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/** Not part of the original client: Screen Markers' boxes drawn and kept (ScreenMarkers). */
class ScreenMarkersTest {

	@BeforeTest
	fun fresh() {
		ScreenMarkers.markers.clear()
		ScreenMarkers.drawing = false
		ScreenMarkers.dragging = false
		ScreenMarkers.defaultBorder = ScreenMarkers.PALETTE[0]
		ScreenMarkers.defaultFill = 0
		ScreenMarkers.defaultThickness = 3
	}

	@AfterTest
	fun tidy() = fresh()

	@Test
	fun aBoxIsTheDragWhicheverWay() {
		assertContentEquals(intArrayOf(10, 20, 30, 40), ScreenMarkers.rect(10, 20, 40, 60))
		assertContentEquals(intArrayOf(10, 20, 30, 40), ScreenMarkers.rect(40, 60, 10, 20))
	}

	@Test
	fun aDragMakesAMarkerAndATapDoesNot() {
		ScreenMarkers.drawing = true
		assertNull(ScreenMarkers.finish(100, 100, 103, 140), "too narrow")
		assertEquals(false, ScreenMarkers.drawing)
		val m = assertNotNull(ScreenMarkers.finish(200, 150, 120, 90))
		assertEquals("Marker 1", m.name)
		assertEquals(120, m.x)
		assertEquals(90, m.y)
		assertEquals(80, m.width)
		assertEquals(60, m.height)
		assertEquals(3, m.thickness)
		assertEquals(2, assertNotNull(ScreenMarkers.finish(0, 0, 50, 50)).id)
	}

	@Test
	fun theMarkersAreKept() {
		ScreenMarkers.finish(0, 0, 50, 50)!!.name = "Bank | booth; here"
		ScreenMarkers.markers[0].labelled = true
		ScreenMarkers.markers[0].fill = 0x40FF0000
		val text: String = ScreenMarkers.encode(ScreenMarkers.markers)
		val back: ArrayList<ScreenMarkers.Marker> = ArrayList()
		ScreenMarkers.decode(text + ";broken|entry", back)
		assertEquals(1, back.size)
		assertEquals("Bank   booth  here", back[0].name)
		assertEquals(true, back[0].labelled)
		assertEquals(0x40FF0000, back[0].fill)
		assertEquals(50, back[0].height)
		assertEquals(text, ScreenMarkers.encode(back))
	}
}
