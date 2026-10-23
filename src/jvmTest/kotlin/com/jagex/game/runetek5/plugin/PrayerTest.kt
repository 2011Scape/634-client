package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.Prayer
import kotlin.test.Test
import kotlin.test.assertEquals

/** Not part of the original client: Prayer's time left on Void's drain (Prayer). */
class PrayerTest {

	@Test
	fun theDrainIsTheActivePrayersSum() {
		// Protect from Melee (bit 14) and Piety (bit 26)
		assertEquals(32, Prayer.drainPerTick(false, (1 shl 14) or (1 shl 26)))
		// Soul Split (18) and Turmoil (19)
		assertEquals(32, Prayer.drainPerTick(true, (1 shl 18) or (1 shl 19)))
		assertEquals(0, Prayer.drainPerTick(false, 0))
	}

	@Test
	fun theTimeLeftOnVoidsDrain() {
		// 99 points, Protect from Melee: a point every 5 ticks, 495 ticks, 297 seconds
		assertEquals(297.0, Prayer.secondsLeft(99, 12), 0.001)
		assertEquals("4:57", Prayer.formatTime(Prayer.secondsLeft(99, 12)))
		assertEquals(-1.0, Prayer.secondsLeft(99, 0))
		// Rapid Restore alone: 60 ticks a point
		assertEquals("59:24", Prayer.formatTime(Prayer.secondsLeft(99, 1)))
		assertEquals("1:00:00", Prayer.formatTime(3600.0))
	}

	@Test
	fun theIconsAreTheListsPlaces() {
		// Piety is 27th in the list, Rigour 28th, Turmoil the last curse
		assertEquals(950, Prayer.PRAYER_SPRITES[Prayer.PRAYERS[26].index])
		assertEquals("Rigour", Prayer.PRAYERS[29].name)
		assertEquals(2347, Prayer.CURSE_SPRITES[Prayer.CURSES[19].index])
		assertEquals(30, Prayer.PRAYERS.map { it.index }.toSet().size)
	}
}
