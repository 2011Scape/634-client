package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.ObjectIndicators
import com.jagex.game.runetek5.client.features.ObjectPointJson
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/** Not part of the original client: Object Markers' saved form and styles against RuneLite's (ObjectIndicators, ObjectPointJson). */
class ObjectIndicatorsTest {

	private val runeLiteExport: String = """[{"id":1276,"name":"Tree","regionId":12850,"regionX":20,"regionY":37,"z":0,"color":"#FFFFFF00","fillColor":"#32FF0000","hull":true,"tile":false},{"id":1530,"name":"Door","regionId":12850,"regionX":21,"regionY":38,"z":1}]"""

	@AfterTest
	fun tidy() {
		ObjectIndicators.highlightHull = true
		ObjectIndicators.highlightOutline = false
		ObjectIndicators.highlightClickbox = false
		ObjectIndicators.highlightTile = false
	}

	@Test
	fun runeLitesMarkersReadAndWriteBackTheSame() {
		val points: List<ObjectIndicators.ObjectPoint> = assertNotNull(ObjectPointJson.parse(runeLiteExport))
		assertEquals(2, points.size)
		val tree: ObjectIndicators.ObjectPoint = points[0]
		assertEquals(1276, tree.id)
		assertEquals("Tree", tree.name)
		assertEquals(3220, tree.worldX())
		assertEquals(3237, tree.worldY())
		assertEquals(0xFFFFFF00.toInt(), tree.borderColour)
		assertEquals(0x32FF0000, tree.fillColour)
		assertEquals(true, tree.hull)
		assertEquals(false, tree.tile)
		assertNull(tree.outline)
		assertNull(points[1].borderColour)
		assertEquals(runeLiteExport, ObjectPointJson.format(points))
		assertNotNull(ObjectPointJson.parse(""" [ { "z" : 0, "regionY": 2, "regionX": 1, "regionId": 1, "name": "A", "id": 5, "extra": {"a": [1, 2]} } ] """), "any order, unknown fields skipped")
		assertNull(ObjectPointJson.parse("{}"))
	}

	@Test
	fun aMarkersOwnStylesReplaceTheSettings() {
		val p: ObjectIndicators.ObjectPoint = ObjectIndicators.ObjectPoint(1, "Tree", 12850, 0, 0, 0)
		assertEquals(ObjectIndicators.HF_HULL, ObjectIndicators.flags(p), "the settings: hull only")
		p.outline = true
		p.tile = true
		assertEquals(ObjectIndicators.HF_OUTLINE or ObjectIndicators.HF_TILE, ObjectIndicators.flags(p))
		p.outline = false
		p.tile = false
		ObjectIndicators.highlightClickbox = true
		assertEquals(ObjectIndicators.HF_HULL or ObjectIndicators.HF_CLICKBOX, ObjectIndicators.flags(p), "all off falls back to the settings, as RuneLite's zero flags do")
		assertEquals(12850, ObjectIndicators.regionOf(3220, 3237))
	}
}
