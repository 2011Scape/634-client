package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.XpGlobes
import com.jagex.game.runetek5.client.features.XpGlobesOverlay
import com.jagex.game.runetek5.client.features.XpTrackerState
import com.jagex.game.runetek5.plugin.api.PanelComponent
import com.jagex.game.runetek5.plugin.api.Skill
import kotlin.math.abs
import kotlin.math.hypot
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Not part of the original client: XP Globes against RuneLite's rules (XpGlobesPlugin, XpGlobesOverlay). */
class XpGlobesTest {

	private var now: Long = 1_000_000L

	@BeforeTest
	fun setUp() {
		XpGlobes.reset()
		XpGlobes.clock = { now }
		XpGlobes.enabled = true
		XpGlobes.hideMaxed = false
		XpGlobes.showVirtualLevel = false
		XpGlobes.xpOrbDuration = 10
	}

	@AfterTest
	fun tearDown() {
		XpGlobes.reset()
	}

	/** The login's update, then a gain. */
	private fun gain(skill: Int, from: Long, to: Long) {
		XpGlobes.statChanged(skill, from)
		XpGlobes.statChanged(skill, to)
	}

	@Test
	fun theFirstUpdateOnlyPrimes() {
		XpGlobes.statChanged(Skill.ATTACK, 1000L)
		assertTrue(XpGlobes.globes.isEmpty())
		// no rise: a drain or boost
		XpGlobes.statChanged(Skill.ATTACK, 1000L)
		assertTrue(XpGlobes.globes.isEmpty())
		XpGlobes.statChanged(Skill.ATTACK, 1040L)
		assertEquals(listOf(Skill.ATTACK), XpGlobes.globes.map { it.skill })
	}

	@Test
	fun globesAreInSkillOrderAndAtMostFive() {
		val skills: IntArray = intArrayOf(Skill.MAGIC, Skill.ATTACK, Skill.COOKING, Skill.DEFENCE, Skill.FISHING, Skill.STRENGTH)
		for (skill in skills) {
			now += 100L
			gain(skill, 0L, 100L)
		}
		// the oldest (Magic, first in) went when the sixth came
		assertEquals(5, XpGlobes.globes.size)
		assertTrue(XpGlobes.globes.none { it.skill == Skill.MAGIC })
		val order: List<Int> = XpGlobes.globes.map { it.skill }
		assertEquals(order.sorted(), order)
	}

	@Test
	fun globesGoAfterTheDuration() {
		gain(Skill.ATTACK, 0L, 100L)
		now += 9_999L
		XpGlobes.tick()
		assertEquals(1, XpGlobes.globes.size)
		now += 2L
		XpGlobes.tick()
		assertTrue(XpGlobes.globes.isEmpty())
	}

	@Test
	fun maxedSkillsAndVirtualLevels() {
		val level99: Long = XpTrackerState.xpForLevel(99).toLong()
		val level110: Long = XpTrackerState.xpForLevel(110).toLong()
		gain(Skill.ATTACK, level110, level110 + 50L)
		assertEquals(99, XpGlobes.globes[0].currentLevel)
		XpGlobes.reset()
		XpGlobes.showVirtualLevel = true
		gain(Skill.ATTACK, level110, level110 + 50L)
		assertEquals(110, XpGlobes.globes[0].currentLevel)
		// Dungeoneering is not maxed until 120
		XpGlobes.reset()
		XpGlobes.hideMaxed = true
		gain(Skill.DUNGEONEERING, level110, level110 + 50L)
		assertEquals(110, XpGlobes.globes[0].currentLevel)
		gain(Skill.ATTACK, level99, level99 + 50L)
		assertTrue(XpGlobes.globes.none { it.skill == Skill.ATTACK })
	}

	@Test
	fun progressIsRuneLites() {
		assertEquals(50.0, XpGlobesOverlay.skillProgress(100, 150L, 200))
		assertEquals(0.0, XpGlobesOverlay.skillProgress(100, 100L, 200))
	}

	@Test
	fun theArcStartsAtTheTopAndGoesClockwise() {
		val xs = IntArray(200)
		val ys = IntArray(200)
		// a quarter from the top (90 degrees), backwards: to three o'clock
		val n: Int = XpGlobesOverlay.arcPoints(50.0, 50.0, 21.0, 19.0, 90.0, -90.0, xs, ys)
		assertTrue(n >= 6)
		// first outer point at the top, last outer point at the right
		assertEquals(50, xs[0])
		assertEquals(29, ys[0])
		assertEquals(71, xs[n / 2 - 1])
		assertEquals(50, ys[n / 2 - 1])
		for (i in 0 until n / 2) {
			assertTrue(abs(hypot(xs[i] - 50.0, ys[i] - 50.0) - 21.0) < 1.0)
		}
		assertEquals(0, XpGlobesOverlay.arcPoints(50.0, 50.0, 21.0, 19.0, 90.0, 0.0, xs, ys))
	}

	@Test
	fun tooltipLinesFollowTheSettings() {
		gain(Skill.ATTACK, 0L, 40L)
		val globe: XpGlobes.XpGlobe = XpGlobes.globes[0]
		val panel = PanelComponent()
		XpGlobesOverlay.tooltipLines(panel, globe, XpGlobes.state(Skill.ATTACK), XpTrackerState.xpForLevel(2))
		val labels: List<String?> = panel.children.map { (it as PanelComponent.LineComponent).left }
		assertEquals("Attack", labels[0])
		assertTrue(labels.containsAll(listOf("Current XP:", "XP left:", "Time left:")))
		panel.children.clear()
		XpGlobes.showXpLeft = false
		XpGlobes.showTimeTilGoal = false
		XpGlobesOverlay.tooltipLines(panel, globe, XpGlobes.state(Skill.ATTACK), XpTrackerState.xpForLevel(2))
		val fewer: List<String?> = panel.children.map { (it as PanelComponent.LineComponent).left }
		assertTrue("XP left:" !in fewer && "Time left:" !in fewer)
		XpGlobes.showXpLeft = true
		XpGlobes.showTimeTilGoal = true
	}
}
