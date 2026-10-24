package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.PriceTooltips
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Not part of the original client: Item Prices' tooltip text and where it shows (PriceTooltips). */
class PriceTooltipsTest {

	@AfterTest
	fun defaults() {
		PriceTooltips.showGEPrice = true
		PriceTooltips.showHAValue = true
		PriceTooltips.showAlchProfit = false
		PriceTooltips.showWhileAlching = true
		PriceTooltips.showEA = true
		PriceTooltips.hideInventory = false
	}

	@Test
	fun oneItemHasBothPricesWithoutEach() {
		assertEquals("GE: 15K gp</br>HA: 15.3K gp", PriceTooltips.stackValueText(1, 15_000, 15_360, 200))
	}

	@Test
	fun aStackHasItsTotalAndEach() {
		assertEquals("GE: 300K gp (300 ea)</br>HA: 1,000 gp (1 ea)", PriceTooltips.stackValueText(1_000, 300, 1, 200))
		PriceTooltips.showEA = false
		assertEquals("GE: 300K gp</br>HA: 1,000 gp", PriceTooltips.stackValueText(1_000, 300, 1, 200))
	}

	@Test
	fun noPriceNoTooltip() {
		assertNull(PriceTooltips.stackValueText(5, 0, 0, 200))
		assertEquals("HA: 30 gp (6 ea)", PriceTooltips.stackValueText(5, 0, 6, 200))
	}

	@Test
	fun alchProfitIsGreenOrRed() {
		PriceTooltips.showAlchProfit = true
		// 15,360 - 15,000 - a 200 nature rune: 160 each
		assertEquals("GE: 15K gp</br>HA: 15.3K gp</br>HA Profit: <col=00ff00>160<col=eeeeee> gp", PriceTooltips.stackValueText(1, 15_000, 15_360, 200))
		assertTrue(PriceTooltips.stackValueText(2, 20_000, 15_360, 200)!!.endsWith("HA Profit: <col=ff0000>-9,680<col=eeeeee> gp (-4,840 ea)"))
	}

	@Test
	fun theBackpackTheBankAndTheBanksBackpack() {
		assertEquals(PriceTooltips.BACKPACK_CONTAINER, PriceTooltips.containerOf(PriceTooltips.BACKPACK, false))
		assertEquals(PriceTooltips.BANK_CONTAINER, PriceTooltips.containerOf(PriceTooltips.BANK, false))
		assertEquals(PriceTooltips.BACKPACK_CONTAINER, PriceTooltips.containerOf((763 shl 16) or 0, false))
		assertNull(PriceTooltips.containerOf((387 shl 16) or 12, false))
	}

	@Test
	fun hiddenBackpackStillShowsWhileAlching() {
		PriceTooltips.hideInventory = true
		assertNull(PriceTooltips.containerOf(PriceTooltips.BACKPACK, false))
		assertEquals(PriceTooltips.BACKPACK_CONTAINER, PriceTooltips.containerOf(PriceTooltips.BACKPACK, true))
		assertEquals(PriceTooltips.BANK_CONTAINER, PriceTooltips.containerOf(PriceTooltips.BANK, false))
		PriceTooltips.showWhileAlching = false
		assertNull(PriceTooltips.containerOf(PriceTooltips.BACKPACK, true))
	}

	@Test
	fun highLevelAlchemyIsTheCastOnItsName() {
		assertTrue(PriceTooltips.isAlching("Cast", "<col=00ff00>High Level Alchemy</col> -> <col=ff9040>Rune scimitar"))
		assertFalse(PriceTooltips.isAlching("Cast", "<col=00ff00>Low Level Alchemy</col> -> <col=ff9040>Rune scimitar"))
		assertFalse(PriceTooltips.isAlching("Use", "Rune scimitar"))
	}
}
