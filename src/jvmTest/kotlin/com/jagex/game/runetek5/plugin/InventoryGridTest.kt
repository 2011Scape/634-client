package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.InventoryGrid
import com.jagex.game.runetek5.plugin.api.Widget
import java.lang.reflect.Proxy
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** Not part of the original client: Inventory Grid's drag delay and backpack slots (InventoryGrid). */
class InventoryGridTest {

	private fun widget(id: Int, parentId: Int, slot: Int): Widget = Proxy.newProxyInstance(Widget::class.java.classLoader, arrayOf(Widget::class.java)) { _, method, _ ->
		when (method.name) {
			"id" -> id
			"parentId" -> parentId
			"slot" -> slot
			else -> null
		}
	} as Widget

	@AfterTest
	fun tidy() {
		InventoryGrid.dragDelay = 0
	}

	@Test
	fun theDragDelayIsInClientCycles() {
		assertTrue(InventoryGrid.delayPassed(0))
		InventoryGrid.dragDelay = 100
		assertFalse(InventoryGrid.delayPassed(4))
		assertTrue(InventoryGrid.delayPassed(5))
	}

	@Test
	fun onlyTheBackpacksSlotsCount() {
		val backpack: Int = 149 shl 16
		assertTrue(InventoryGrid.isBackpackSlot(widget(backpack, -1, 3)))
		assertTrue(InventoryGrid.isBackpackSlot(widget(backpack or 5, backpack, 3)))
		assertFalse(InventoryGrid.isBackpackSlot(widget(backpack, -1, -1)))
		assertFalse(InventoryGrid.isBackpackSlot(widget(762 shl 16 or 93, -1, 3)))
	}
}
