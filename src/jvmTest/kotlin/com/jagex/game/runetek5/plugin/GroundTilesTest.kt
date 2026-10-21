package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.GroundTileJson
import com.jagex.game.runetek5.client.features.GroundTiles
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/** Not part of the original client: the ground tiles' regions and RuneLite's JSON for them (GroundTiles, GroundTileJson). */
class GroundTilesTest {

	private val runeLiteExport: String = """[{"regionId":12850,"regionX":20,"regionY":37,"z":0,"color":"#FFFFFF00","label":"Home"},{"regionId":12850,"regionX":21,"regionY":37,"z":1,"color":"#80FF0000"}]"""

	@Test
	fun aRegionIsSixtyFourTilesSquareNumberedAsRuneLiteDoes() {
		assertEquals(12850, GroundTiles.regionOf(3220, 3237), "Lumbridge")
		val marker: GroundTiles.Marker = GroundTiles.Marker(12850, 20, 37, 0, 0, null)
		assertEquals(3220, marker.worldX)
		assertEquals(3237, marker.worldY)
	}

	@Test
	fun runeLitesExportReadsAndWritesBackTheSame() {
		val markers: List<GroundTiles.Marker> = assertNotNull(GroundTileJson.parse(runeLiteExport))
		assertEquals(2, markers.size)
		assertEquals("Home", markers[0].label)
		assertEquals(0xFFFFFF00.toInt(), markers[0].colour)
		assertNull(markers[1].label)
		assertEquals(0x80FF0000.toInt(), markers[1].colour)
		assertEquals(1, markers[1].z)
		assertEquals(runeLiteExport, GroundTileJson.format(markers))
	}

	@Test
	fun spacesUnknownFieldsAndOpaqueColoursAreTaken() {
		val text: String = """ [ { "regionId" : 12850, "regionX": 1, "regionY": 2, "z": 0, "color": "#ff0000", "label": "a \"quoted\" A", "extra": true, "other": null } ] """
		val markers: List<GroundTiles.Marker> = assertNotNull(GroundTileJson.parse(text))
		assertEquals(1, markers.size)
		assertEquals(0xFFFF0000.toInt(), markers[0].colour)
		assertEquals("a \"quoted\" A", markers[0].label)
		assertEquals("""[{"regionId":12850,"regionX":1,"regionY":2,"z":0,"color":"#FFFF0000","label":"a \"quoted\" A"}]""", GroundTileJson.format(markers))
	}

	@Test
	fun whatIsNotAnArrayOfTilesIsNothing() {
		assertNull(GroundTileJson.parse(""))
		assertNull(GroundTileJson.parse("hello"))
		assertNull(GroundTileJson.parse("{\"regionId\":1}"))
		assertNull(GroundTileJson.parse("[{\"regionId\":1,\"nested\":{}}]"), "nothing deeper than a flat object")
		assertEquals(0, assertNotNull(GroundTileJson.parse("[]")).size)
		assertEquals(0, assertNotNull(GroundTileJson.parse("[{\"regionId\":12850,\"regionX\":99,\"regionY\":2,\"z\":0}]")).size, "a tile outside its region is skipped")
	}

	@Test
	fun coloursParseBothWaysAndNamesHaveValues() {
		assertEquals(0xFFFFFF00.toInt(), GroundTileJson.parseColour("#FFFFFF00"))
		assertEquals(0xFF00FF00.toInt(), GroundTileJson.parseColour("00ff00"))
		assertNull(GroundTileJson.parseColour("#12345"))
		assertNull(GroundTileJson.parseColour("#GGGGGG"))
		assertEquals("#80FF0000", GroundTileJson.formatColour(0x80FF0000.toInt()))
		assertEquals(0xFFFF0000.toInt(), GroundTiles.colourNamed("red"))
		assertEquals(0xFFFFFF00.toInt(), GroundTiles.colourNamed("no such colour"), "yellow when unknown")
	}
}
