package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.InventoryValue
import kotlin.test.Test
import kotlin.test.assertEquals

/** Not part of the original client: the number format of the inventory value overlay. */
class InventoryValueTest {

	@Test
	fun thousandsGetCommas() {
		assertEquals("0", InventoryValue.withCommas(0L))
		assertEquals("999", InventoryValue.withCommas(999L))
		assertEquals("1,000", InventoryValue.withCommas(1000L))
		assertEquals("12,345,678", InventoryValue.withCommas(12345678L))
		assertEquals("2,147,483,648", InventoryValue.withCommas(2147483648L))
	}
}
