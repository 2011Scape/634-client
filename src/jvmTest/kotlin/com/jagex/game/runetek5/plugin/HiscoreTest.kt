package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.Hiscore
import com.jagex.game.runetek5.client.features.HiscoreLookup
import com.jagex.game.runetek5.plugin.api.Skill
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/** Not part of the original client: the Hiscore panel's layout and the website's answer read whole (Hiscore, HiscoreLookup). */
class HiscoreTest {

	private val body: String = "{\"player\":\"pixel\",\"skills\":{\"overall\":{\"rank\":5,\"level\":1706,\"xp\":149276913}," +
		"\"attack\":{\"rank\":3,\"level\":99,\"xp\":15055504},\"defence\":{\"rank\":-1,\"level\":1,\"xp\":0}}}"

	@Test
	fun theGridIsTheStatsTab() {
		assertEquals((0 until Skill.COUNT).toList(), Hiscore.GRID.sorted())
		// Attack, Constitution, Mining across the top; Dungeoneering alone at the bottom
		assertEquals(listOf("Attack", "Constitution", "Mining"), Hiscore.GRID.take(3).map { Skill.name(it) })
		assertEquals("Dungeoneering", Skill.name(Hiscore.GRID.last()))
	}

	@Test
	fun theMouseFindsItsCell() {
		val top: Int = 20
		assertEquals(0, Hiscore.cellAt(5, top, top))
		assertEquals(4, Hiscore.cellAt(4 + Hiscore.CELL_WIDTH + 1, top + Hiscore.CELL_HEIGHT + 1, top))
		assertEquals(24, Hiscore.cellAt(5, top + 8 * Hiscore.CELL_HEIGHT, top))
		assertEquals(-1, Hiscore.cellAt(4 + Hiscore.CELL_WIDTH + 1, top + 8 * Hiscore.CELL_HEIGHT, top), "past Dungeoneering")
		assertEquals(-1, Hiscore.cellAt(5, top - 1, top))
	}

	@Test
	fun theAnswerIsReadWhole() {
		val result: HiscoreLookup.Result = assertNotNull(HiscoreLookup.parse(body))
		assertEquals(99, result.levels[0])
		assertEquals(3, result.ranks[0])
		assertEquals(15055504L, result.xp[0])
		assertEquals(-1, result.levels[1], "no rank")
		assertEquals(1706, result.overallLevel)
		assertEquals(5, result.overallRank)
		assertEquals(149276913L, result.overallXp)
		assertEquals("Attack: rank 3, 15,055,504 xp", Hiscore.tooltip(result, 0))
		assertEquals("Defence: not ranked", Hiscore.tooltip(result, 1))
	}
}
