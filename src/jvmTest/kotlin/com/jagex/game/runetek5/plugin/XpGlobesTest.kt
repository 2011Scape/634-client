package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.XpGlobeImages
import com.jagex.game.runetek5.client.features.XpGlobes
import com.jagex.game.runetek5.client.features.XpGlobesOverlay
import com.jagex.game.runetek5.client.features.XpTrackerState
import com.jagex.game.runetek5.plugin.api.PanelComponent
import com.jagex.game.runetek5.plugin.api.Skill
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
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
		// a quarter from the top (90 degrees), backwards: through one o'clock to three o'clock, not past it
		assertTrue(XpGlobeImages.inSweep(1.0, -10.0, 90.0, -90.0))
		assertTrue(XpGlobeImages.inSweep(7.0, -7.0, 90.0, -90.0))
		assertFalse(XpGlobeImages.inSweep(-7.0, -7.0, 90.0, -90.0))
		assertFalse(XpGlobeImages.inSweep(7.0, 7.0, 90.0, -90.0))
		assertTrue(XpGlobeImages.inSweep(-7.0, 7.0, 0.0, 360.0))
		assertFalse(XpGlobeImages.inSweep(0.0, -10.0, 90.0, 0.0))
	}

	@Test
	fun theRingsAreSmooth() {
		val orb = 40
		val canvas = 46
		val image = XpGlobeImages.rings(orb, canvas, 0xFF000000.toInt(), 5, 0xFF00FF00.toInt(), 2, 90.0, -90.0)
		val c: Int = canvas / 2
		// on the circle at the top: the arc's green over the black ring
		assertEquals(0xFF00FF00.toInt(), image.pixels[(c - orb / 2) * canvas + c] or 0)
		// at the bottom the arc has not come yet: the ring alone
		assertEquals(0xFF000000.toInt(), image.pixels[(c + orb / 2) * canvas + c])
		// the middle is empty, and some edge pixels are partly covered (the smoothing)
		assertEquals(0, image.pixels[c * canvas + c])
		assertTrue(image.pixels.any { val a = it ushr 24; a in 1..254 })
		// over: a half-covered green over black
		assertEquals(0xFF008000.toInt() ushr 24, XpGlobeImages.over(0x8000FF00.toInt(), 0xFF000000.toInt()) ushr 24)
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
