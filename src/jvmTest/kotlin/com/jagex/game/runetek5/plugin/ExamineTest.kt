package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.Examine
import com.jagex.game.runetek5.client.features.ClientFeatures
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/** Not part of the original client: Examine's price line (Examine). */
class ExamineTest {

	private fun plain(line: String?): String? = line?.let { ClientFeatures.plainText(it) }

	@Test
	fun oneItem() {
		assertEquals("Price of Rune scimitar: GE average 15,000 HA value 15,360", plain(Examine.priceLine("Rune scimitar", 1, 15000, 15360, 1333)))
	}

	@Test
	fun aStackSaysEach() {
		assertEquals("Price of 1,000 x Feather: GE average 3,000 (3 ea) HA value 1,000 (1 ea)", plain(Examine.priceLine("Feather", 1000, 3, 1, 314)))
	}

	@Test
	fun coinsAndPricelessItemsGetNone() {
		assertNull(Examine.priceLine("Coins", 100, 1, 1, Examine.COINS))
		assertNull(Examine.priceLine("Pot", 1, -1, 0, 1931))
		assertEquals("Price of Pot: HA value 1", plain(Examine.priceLine("Pot", 1, -1, 1, 1931)))
	}

	@Test
	fun thousandsAreParted() {
		assertEquals("2,147,483,647", Examine.formatNumber(Int.MAX_VALUE.toLong()))
		assertEquals("999", Examine.formatNumber(999))
		assertEquals("-1,000", Examine.formatNumber(-1000))
	}
}
