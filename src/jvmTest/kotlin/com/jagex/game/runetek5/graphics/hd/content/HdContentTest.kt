package com.jagex.game.runetek5.graphics.hd.content

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

/**
 * Not part of the original client: 634HD's environments.json, its fog fields (117HD's fog depth and
 * ground fog) and the choice of environment by tile; and the shipped file, hand-written entries and
 * the ones generated from 117HD's environments.json (marked source), against its schema.
 */
class HdContentTest {

    private val schema: String? = HdContent::class.java.getResourceAsStream("/634hd/data/environments.schema.json")?.use { String(it.readBytes(), Charsets.UTF_8) }

    private fun parse(text: String, warnings: MutableList<String> = ArrayList()): List<HdContent.EnvironmentDef> {
        assertNotNull(schema)
        return HdContent.parseEnvironments(text, schema, warnings)
    }

    private val sample: String = """
        {
          "environments": [
            { "name": "Default", "fog": { "depth": 6.5 } },
            { "name": "Town", "area": { "regions": [13105] }, "fog": { "colour": "#c7b79b", "depth": 25 } },
            {
              "name": "Desert", "source": "117HD KHARIDIAN_DESERT",
              "area": { "bounds": [[3201, 3070, 3518, 3134], [3344, 3135, 3352, 3156]], "levels": [0] },
              "fog": { "colour": "#c7b79b", "end": 22 },
              "groundFog": { "start": -800, "end": -2000, "opacity": 0.5 }
            }
          ]
        }
    """.trimIndent()

    @Test
    fun theFogFieldsAreRead() {
        val warnings = ArrayList<String>()
        val list = parse(sample, warnings)
        assertEquals(emptyList(), warnings)
        assertEquals(3, list.size)
        val default = list[0]
        assertTrue(default.isDefault)
        assertEquals(6.5f, default.fogDepth)
        assertNull(default.fogColour)
        assertEquals(0.0f, default.groundFogOpacity)
        val town = list[1]
        assertEquals(25.0f, town.fogDepth)
        assertEquals(0xc7 / 255.0f, town.fogColour!![0])
        assertTrue(town.fogEnd.isNaN())
        assertNull(town.source)
        val desert = list[2]
        assertEquals("117HD KHARIDIAN_DESERT", desert.source)
        assertTrue(desert.fogDepth.isNaN())
        assertEquals(22.0f, desert.fogEnd)
        assertEquals(-800.0f, desert.groundFogStart)
        assertEquals(-2000.0f, desert.groundFogEnd)
        assertEquals(0.5f, desert.groundFogOpacity)
        assertEquals(listOf(3201, 3070, 3518, 3156), desert.box!!.toList())
    }

    @Test
    fun theFirstAreaHoldingTheTileWinsElseTheDefault() {
        val list = parse(sample)
        // the town's region 13105 is x 3264..3327, y 3136..3199; the desert's second box reaches into it
        assertSame(list[1], HdContent.environmentAt(list, 3293, 3180, 0))
        assertSame(list[2], HdContent.environmentAt(list, 3300, 3100, 0))
        assertSame(list[2], HdContent.environmentAt(list, 3345, 3140, 0))
        // the desert's levels filter; the town's do not
        assertSame(list[0], HdContent.environmentAt(list, 3300, 3100, 1))
        assertSame(list[1], HdContent.environmentAt(list, 3293, 3180, 2))
        assertSame(list[0], HdContent.environmentAt(list, 3000, 3000, 0))
    }

    @Test
    fun anOldStyleEntryIsSkippedWithAWarning() {
        val warnings = ArrayList<String>()
        val list = parse(
            """{ "environments": [ { "name": "Old", "heightFog": { "density": 0.1, "top": -200 } }, { "name": "Older", "fog": { "start": 8 } }, { "name": "New" } ] }""",
            warnings,
        )
        assertEquals(listOf("New"), list.map { it.name })
        assertEquals(2, warnings.size)
        assertTrue(warnings[0].contains("heightFog"), warnings[0])
        assertTrue(warnings[1].contains("start"), warnings[1])
    }

    @Test
    fun theShippedFileConforms() {
        val text = HdContent::class.java.getResourceAsStream("/634hd/data/environments.json")!!.use { String(it.readBytes(), Charsets.UTF_8) }
        val warnings = ArrayList<String>()
        val list = parse(text, warnings)
        assertEquals(emptyList(), warnings)
        assertTrue(list.size > 100, "the imported environments are present: " + list.size)
        val generated = list.filter { it.source != null }
        assertTrue(generated.all { it.source!!.startsWith("117HD ") })
        assertTrue(list.indexOfFirst { it.source != null } > list.indexOfLast { it.source == null }, "hand-written entries come first")
        // Al Kharid (the hand entry, its regions down to y 3072) has the desert haze; further south it is 117HD's
        val town = HdContent.environmentAt(list, 3293, 3180, 0)!!
        assertEquals("Al Kharid", town.name)
        assertEquals(25.0f, town.fogDepth)
        assertEquals("Al Kharid", HdContent.environmentAt(list, 3304, 3100, 0)!!.name)
        assertEquals("117HD KHARIDIAN_DESERT", HdContent.environmentAt(list, 3400, 3100, 0)!!.source)
        assertEquals("117HD KHARIDIAN_DESERT_MID", HdContent.environmentAt(list, 3304, 3060, 0)!!.source)
        assertEquals("117HD KHARIDIAN_DESERT_DEEP", HdContent.environmentAt(list, 3250, 2900, 0)!!.source)
    }
}
