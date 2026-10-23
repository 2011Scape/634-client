package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.CombatLevel
import kotlin.test.Test
import kotlin.test.assertEquals

/** Not part of the original client: Combat Level's numbers on Void's formulas (CombatLevel). */
class CombatLevelTest {

	/** Attack, strength, defence, constitution, ranged, magic, prayer, summoning. */
	private val fresh: IntArray = intArrayOf(1, 1, 1, 10, 1, 1, 1, 1)

	private val maxed: IntArray = intArrayOf(99, 99, 99, 99, 99, 99, 99, 99)

	@Test
	fun aNewPlayerIsThreeAndAMaxedOne138() {
		assertEquals(3.4, CombatLevel.precise(fresh, true), 1e-9)
		assertEquals(3, CombatLevel.level(fresh, true))
		assertEquals(138, CombatLevel.level(maxed, true))
		// summoning counts only on members' worlds
		assertEquals(126, CombatLevel.level(maxed, false))
	}

	@Test
	fun theLevelsToTheNext() {
		// 3.4: 0.6 to go, a level of attack or strength adds 1.3 / 4
		assertEquals(2, CombatLevel.levelsUntilNext(fresh, true, CombatLevel.ATT, CombatLevel.STR))
		assertEquals(3, CombatLevel.levelsUntilNext(fresh, true, CombatLevel.DEF, CombatLevel.HP))
		assertEquals(0, CombatLevel.levelsUntilNext(maxed, true, CombatLevel.PRAY))
	}

	@Test
	fun theWildernessLevelAndRange() {
		assertEquals(0, CombatLevel.wildernessLevel(3222, 3218))
		assertEquals(1, CombatLevel.wildernessLevel(3100, 3525))
		assertEquals(11, CombatLevel.wildernessLevel(3100, 3600))
		assertEquals(4, CombatLevel.wildernessLevel(3050, 9950))
		// level 100 at wilderness 10: 100 - (10 + 5 + 10) = 75; up while max - (10 + 5 + max / 10) <= 100
		assertEquals(75..127, CombatLevel.combatRange(100, 10))
	}

	@Test
	fun threePlacesWithoutTrailingZeros() {
		assertEquals("3.4", CombatLevel.format(3.4))
		assertEquals("138.35", CombatLevel.format(138.35))
		assertEquals("126", CombatLevel.format(126.0))
		assertEquals("87.125", CombatLevel.format(87.125))
	}
}
