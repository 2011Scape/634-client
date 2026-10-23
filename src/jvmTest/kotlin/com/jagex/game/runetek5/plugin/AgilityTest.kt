package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.Agility
import com.jagex.game.runetek5.plugin.api.Skill
import com.jagex.game.runetek5.plugin.api.events.LocChanged
import com.jagex.game.runetek5.plugin.api.events.StatChanged
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Not part of the original client: Agility's laps from Void's lap bonuses, and its obstacles (Agility). */
class AgilityTest {

	private var now: Long = 1_000_000L

	private var xp: Int = 100_000

	@BeforeTest
	fun fresh() {
		Agility.session = null
		Agility.found.clear()
		Agility.clock = { now }
	}

	@AfterTest
	fun tidy() {
		fresh()
		Agility.clock = { System.currentTimeMillis() }
	}

	private fun gain(amount: Int) {
		Agility.statChanged(StatChanged(Skill.AGILITY, xp, xp + amount, 50))
		xp += amount
	}

	/** One Gnome Stronghold lap as Void gives it, whole experience: 7, 8, 5, 7, 5, 8, then the bonus and the pipe. */
	private fun gnomeLap() {
		for (amount in intArrayOf(7, 8, 5, 7, 5, 8)) {
			gain(amount)
		}
		gain(39)
		gain(7)
	}

	@Test
	fun theLapBonusSaysWhichCourse() {
		assertEquals("Gnome Stronghold", Agility.courseForBonus(39)!!.name)
		assertEquals("Barbarian Outpost", Agility.courseForBonus(47)!!.name)
		assertEquals("Ape Atoll", Agility.courseForBonus(200)!!.name)
		assertNull(Agility.courseForBonus(22))
	}

	@Test
	fun lapsAreCountedAndTimed() {
		gnomeLap()
		assertEquals(1, Agility.session!!.totalLaps)
		now += 40_000L
		gnomeLap()
		assertEquals(2, Agility.session!!.totalLaps)
		// one lap in 40 seconds: 90 an hour
		assertEquals(90, Agility.session!!.lapsPerHour)
	}

	@Test
	fun lapsToGoalUseTheMeasuredLapAfterTheFirst() {
		gnomeLap()
		// 100,046 xp at level 50 (101,333 for 51): 1,287 to go at the table's 86.5: 15 laps
		assertEquals(15, Agility.session!!.lapsTillGoal)
		gnomeLap()
		// the lap measured from bonus to bonus: 7 + 40 + 39 = 86
		assertEquals(86, Agility.session!!.measuredLapXp)
	}

	@Test
	fun anotherCourseStartsAnotherSession() {
		gnomeLap()
		gain(200)
		assertEquals("Ape Atoll", Agility.session!!.course.name)
		assertEquals(1, Agility.session!!.totalLaps)
	}

	@Test
	fun obstaclesComeAndGoWithTheirLocations() {
		val event = LocChanged()
		event.sceneX = 10
		event.sceneY = 20
		event.oldId = -1
		event.newId = 2295
		Agility.locChanged(event)
		assertEquals(1, Agility.found.size)
		event.oldId = 2295
		event.newId = 1
		Agility.locChanged(event)
		assertEquals(0, Agility.found.size)
	}

	@Test
	fun theMouseInsideTheClickbox() {
		val xs = intArrayOf(0, 10, 10, 0)
		val ys = intArrayOf(0, 0, 10, 10)
		assertTrue(Agility.inside(xs, ys, 4, 5, 5))
		assertFalse(Agility.inside(xs, ys, 4, 15, 5))
	}
}
