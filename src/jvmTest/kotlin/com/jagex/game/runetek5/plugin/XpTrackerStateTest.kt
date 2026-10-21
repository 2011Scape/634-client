package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.GroundItemRules
import com.jagex.game.runetek5.client.features.XpTracker
import com.jagex.game.runetek5.client.features.XpTrackerState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** Not part of the original client: the XP Tracker's sums against RuneLite's (XpTrackerState, XpTracker's labels). */
class XpTrackerStateTest {

	@Test
	fun theExperienceTableIsRuneScapes() {
		assertEquals(0, XpTrackerState.xpForLevel(1))
		assertEquals(83, XpTrackerState.xpForLevel(2))
		assertEquals(6_517_253, XpTrackerState.xpForLevel(92))
		assertEquals(13_034_431, XpTrackerState.xpForLevel(99))
		assertEquals(188_884_740, XpTrackerState.xpForLevel(126))
		assertEquals(98, XpTrackerState.levelForXp(13_034_430))
		assertEquals(99, XpTrackerState.levelForXp(13_034_431))
		assertEquals(126, XpTrackerState.levelForXp(200_000_000))
	}

	@Test
	fun ratesUseAtLeastAMinuteAndActionsLeftComeFromTheLastTen() {
		val state: XpTrackerState = XpTrackerState(0)
		state.updateGoals(0)
		assertFalse(state.update(0, 1000), "no change, no action")
		assertTrue(state.update(10, 1000))
		assertEquals(83, state.endLevelExp)
		assertEquals(Int.MAX_VALUE, XpTrackerState(5).actionsRemaining(), "N/A before an action")
		assertEquals(8, state.actionsRemaining(), "73 left at 10 an action")
		state.tick(30_000)
		assertEquals(600, state.xpPerHour(), "10 in the minute's floor")
		state.tick(90_000)
		assertEquals(300, state.xpPerHour(), "10 in two minutes")
		assertEquals("14:36", state.timeTillLevel(XpTrackerState.SHORT), "73 left at 10 per 120 s: 876 s")
	}

	@Test
	fun timeToLevelFormatsAsRuneLitesDo() {
		val state: XpTrackerState = XpTrackerState(0)
		state.updateGoals(0)
		assertEquals("∞", state.timeTillLevel(XpTrackerState.SHORT), "no rate")
		state.update(1, 0)
		state.tick(3600_000L * 50)
		state.updateGoals(1)
		assertEquals("170 days 20:00:00", state.timeTillLevel(XpTrackerState.DAYS), "82 left at 1 per 50 hours")
		assertEquals("4100 hours 00:00", state.timeTillLevel(XpTrackerState.HOURS))
		assertEquals("4100:00:00", state.timeTillLevel(XpTrackerState.SHORT))
	}

	@Test
	fun resetPerHourKeepsTheGainsAndPausingStopsTheClock() {
		val state: XpTrackerState = XpTrackerState(1000)
		state.update(1100, 0)
		state.resetPerHour(5)
		assertEquals(100, state.totalXpGained())
		assertEquals(0, state.xpPerHour())
		state.tick(10_000)
		assertEquals(0L, state.skillTime, "no clock without a gain since the reset")
		state.update(1150, 10)
		state.tickPause(1150, 1, 1000)
		state.tickPause(1150, 1, 61_000)
		assertTrue(state.isPaused(), "a minute without a change")
		state.tickPause(1160, 1, 62_000)
		assertFalse(state.isPaused(), "experience clears it")
		val restored: XpTrackerState = XpTrackerState(0)
		restored.restore(state.save())
		assertEquals(state.currentXp(), restored.currentXp())
	}

	@Test
	fun labelsAreWrittenAsTheGameWritesThem() {
		assertEquals("9999", GroundItemRules.quantityToRSDecimalStack(9999, true))
		assertEquals("12.3K", GroundItemRules.quantityToRSDecimalStack(12_345, true))
		assertEquals("1.235M", GroundItemRules.quantityToRSDecimalStack(1_234_567, true))
		assertEquals("1.2M", GroundItemRules.quantityToRSDecimalStack(1_234_567, false))
		val state: XpTrackerState = XpTrackerState(0)
		assertEquals("N/A", XpTracker.labelValue(XpTracker.LABEL_ACTIONS_LEFT, state))
		assertEquals("50.00", XpTracker.twoDecimals(50.0))
		assertEquals(-1, XpTracker.skillOfComponent(5))
		assertEquals(0, XpTracker.skillOfComponent((320 shl 16) or 200), "Attack")
		assertEquals(24, XpTracker.skillOfComponent((320 shl 16) or 158), "Dungeoneering")
	}
}
