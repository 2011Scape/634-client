package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.AntiDrag
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** Not part of the original client: when Anti Drag's delay applies (AntiDrag.wanted). */
class AntiDragTest {

	@BeforeTest
	fun fresh() {
		AntiDrag.enabled = true
		AntiDrag.onShiftOnly = true
		AntiDrag.disableOnCtrl = false
	}

	@AfterTest
	fun tidy() {
		AntiDrag.enabled = false
	}

	@Test
	fun onShiftOnlyItAppliesWhileShiftIsHeld() {
		assertTrue(AntiDrag.wanted(shift = true, control = false, focused = true))
		assertFalse(AntiDrag.wanted(shift = false, control = false, focused = true))
	}

	@Test
	fun otherwiseItAlwaysAppliesUnlessControlTurnsItOff() {
		AntiDrag.onShiftOnly = false
		assertTrue(AntiDrag.wanted(shift = false, control = true, focused = true))
		AntiDrag.disableOnCtrl = true
		assertFalse(AntiDrag.wanted(shift = false, control = true, focused = true))
		assertTrue(AntiDrag.wanted(shift = false, control = false, focused = true))
	}

	@Test
	fun neverWithoutFocusOrWhenOff() {
		assertFalse(AntiDrag.wanted(shift = true, control = false, focused = false))
		AntiDrag.enabled = false
		assertFalse(AntiDrag.wanted(shift = true, control = false, focused = true))
	}
}
