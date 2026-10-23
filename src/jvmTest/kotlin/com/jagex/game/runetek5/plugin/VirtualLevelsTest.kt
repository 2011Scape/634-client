package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.VirtualLevels
import kotlin.test.Test
import kotlin.test.assertEquals

/** Not part of the original client: Virtual Levels' levels past the last (VirtualLevels). */
class VirtualLevelsTest {

	@Test
	fun levelsGoOnPastNinetyNine() {
		assertEquals(99, VirtualLevels.virtualLevel(13_034_431, 99))
		assertEquals(100, VirtualLevels.virtualLevel(14_391_160, 99))
		assertEquals(120, VirtualLevels.virtualLevel(104_273_167, 99))
		assertEquals(126, VirtualLevels.virtualLevel(200_000_000, 99))
	}

	@Test
	fun neverBelowTheRealLevel() {
		// Dungeoneering at 120 from the server, its experience short of the 120 line by a rounding
		assertEquals(120, VirtualLevels.virtualLevel(104_273_166, 120))
		assertEquals(50, VirtualLevels.virtualLevel(101_333, 50))
	}
}
