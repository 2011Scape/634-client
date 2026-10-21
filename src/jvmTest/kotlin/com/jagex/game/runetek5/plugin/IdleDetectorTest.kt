package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.IdleAnimations
import com.jagex.game.runetek5.client.features.IdleDetector
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** Not part of the original client: the idle notifier's rules, driven cycle by cycle (IdleDetector). */
class IdleDetectorTest {

	private val delay: Int = 10

	private val chop: Int = 879

	private fun detector(): IdleDetector {
		val detector: IdleDetector = IdleDetector()
		detector.delayCycles = delay
		detector.movementIdle = true
		return detector
	}

	/** A cycle with nothing pressed and every stat full. */
	private fun still(detector: IdleDetector, animationId: Int = -1, hasTarget: Boolean = false, tileX: Int = 10, tileY: Int = 10, mouseHeld: Boolean = false, hitpoints: Int = 990, prayer: Int = 99, energy: Int = 100, spec: Int = 1000): Int {
		return detector.cycle(animationId, hasTarget, tileX, tileY, mouseHeld, hitpoints, 990, prayer, 99, energy, spec)
	}

	/** Runs [count] still cycles and returns the bits they said, or'ed. */
	private fun run(detector: IdleDetector, count: Int, animationId: Int = -1, hasTarget: Boolean = false, tileX: Int = 10, tileY: Int = 10): Int {
		var bits: Int = 0
		for (i in 0 until count) {
			bits = bits or still(detector, animationId, hasTarget, tileX, tileY)
		}
		return bits
	}

	@Test
	fun aSkillingAnimationThatStoppedIsSaidOnceAtTheDelay() {
		val detector: IdleDetector = detector()
		detector.movementIdle = false
		detector.animationChanged(chop)
		assertEquals(0, run(detector, 3, animationId = chop), "still chopping")
		assertEquals(0, run(detector, delay - 1), "stopped, the delay not up")
		assertEquals(IdleDetector.ANIMATION, still(detector), "the delay up")
		assertEquals(0, run(detector, delay * 2), "said once")
	}

	@Test
	fun aServerResetOfTheAnimationIsItsEndNotAForgetting() {
		val detector: IdleDetector = detector()
		detector.movementIdle = false
		detector.animationChanged(621) // the net cast
		run(detector, 5, animationId = 621)
		detector.animationChanged(-1) // the server stops fishing: the spot moved or the backpack is full
		assertEquals(0, run(detector, delay - 1), "stopped, the delay not up")
		assertEquals(IdleDetector.ANIMATION, still(detector), "the delay after the reset")
		assertEquals(0, run(detector, delay * 2), "said once")
	}

	@Test
	fun anUnknownAnimationNeverArmsAndResumingRestartsTheWait() {
		val detector: IdleDetector = detector()
		detector.movementIdle = false
		detector.animationChanged(1234)
		assertEquals(0, run(detector, delay * 2), "an emote is not skilling")
		detector.animationChanged(chop)
		run(detector, delay - 2)
		assertEquals(0, run(detector, 2, animationId = chop), "chopping again")
		assertEquals(0, run(detector, delay - 1), "the wait started over")
		assertEquals(IdleDetector.ANIMATION, still(detector))
	}

	@Test
	fun aFightThatEndedIsOutOfCombatAndAFishingSpotIsIdle() {
		val detector: IdleDetector = detector()
		detector.movementIdle = false
		detector.interactingChanged(IdleDetector.INTERACT_COMBAT)
		run(detector, 3, hasTarget = true)
		detector.interactingChanged(IdleDetector.INTERACT_NONE)
		assertEquals(0, run(detector, delay - 1))
		assertEquals(IdleDetector.OUT_OF_COMBAT, still(detector))
		assertEquals(0, run(detector, delay * 2), "said once")

		detector.interactingChanged(IdleDetector.INTERACT_FISHING)
		run(detector, 3, hasTarget = true)
		detector.interactingChanged(IdleDetector.INTERACT_NONE)
		run(detector, delay - 1)
		assertEquals(IdleDetector.INTERACTION, still(detector))

		detector.interactingChanged(IdleDetector.INTERACT_OTHER)
		run(detector, 3, hasTarget = true)
		detector.interactingChanged(IdleDetector.INTERACT_NONE)
		assertEquals(0, run(detector, delay * 2), "a banker is not watched")
	}

	@Test
	fun aHitSplatHoldsTheOutOfCombatWord() {
		val detector: IdleDetector = detector()
		detector.movementIdle = false
		detector.interactingChanged(IdleDetector.INTERACT_COMBAT)
		run(detector, 3, hasTarget = true)
		detector.interactingChanged(IdleDetector.INTERACT_NONE)
		detector.hitsplatOnPlayer()
		assertEquals(0, run(detector, IdleDetector.COMBAT_HOLD_CYCLES - 1), "the hold is on")
		assertEquals(IdleDetector.OUT_OF_COMBAT, still(detector), "the hold is off and the delay long past")
	}

	@Test
	fun standingStillIsSaidOncePerStopAndNotWhileAnimating() {
		val detector: IdleDetector = detector()
		assertEquals(0, run(detector, delay), "the first cycle only records the tile")
		assertEquals(IdleDetector.MOVEMENT, still(detector))
		assertEquals(0, run(detector, delay * 2), "said once")
		for (i in 0 until delay * 2) {
			assertEquals(0, still(detector, tileX = 11 + i), "walking")
		}
		assertEquals(0, run(detector, delay * 2, tileX = 50, animationId = 1234), "an emote on the new tile")
		assertEquals(IdleDetector.MOVEMENT, run(detector, delay + 1, tileX = 50), "the emote over")
	}

	@Test
	fun theEndOfASkillingAnimationIsNotAlsoAStop() {
		val detector: IdleDetector = detector()
		run(detector, 2)
		detector.animationChanged(chop)
		run(detector, 3, animationId = chop)
		assertEquals(IdleDetector.ANIMATION, run(detector, delay * 2), "idle, not stopped")
	}

	@Test
	fun aClickOrAKeyAfterTheAnimationEndsTheIdleSpell() {
		val detector: IdleDetector = detector()
		detector.movementIdle = false
		detector.animationChanged(chop)
		run(detector, 2, animationId = chop)
		run(detector, 3)
		detector.mousePressed()
		assertEquals(0, run(detector, IdleDetector.CLICK_ACTIVE_CYCLES + delay * 3), "the player did something: not idle")

		detector.animationChanged(chop)
		run(detector, 2, animationId = chop)
		run(detector, 3)
		detector.keyPressed()
		assertEquals(0, run(detector, IdleDetector.KEY_ACTIVE_CYCLES + delay * 3), "the player typed something: not idle")
	}

	@Test
	fun aClickWhileStillAnimatingOnlyDelaysTheWord() {
		val detector: IdleDetector = detector()
		detector.movementIdle = false
		detector.animationChanged(chop)
		run(detector, 2, animationId = chop)
		detector.mousePressed()
		assertEquals(0, run(detector, IdleDetector.CLICK_ACTIVE_CYCLES, animationId = chop), "clicking while chopping")
		assertEquals(0, run(detector, 2, animationId = chop), "chopping on")
		assertEquals(0, run(detector, delay - 1), "stopped, the delay not up")
		assertEquals(IdleDetector.ANIMATION, still(detector))

		detector.animationChanged(chop)
		run(detector, 2, animationId = chop)
		for (i in 0 until delay * 3) {
			assertEquals(0, still(detector, animationId = chop, mouseHeld = true), "a held button while chopping")
		}
		assertEquals(0, run(detector, 2, animationId = chop), "chopping on")
		assertEquals(0, run(detector, delay - 1))
		assertEquals(IdleDetector.ANIMATION, still(detector))
	}

	@Test
	fun noInputSinceLoginCountsFromLogin() {
		val detector: IdleDetector = detector()
		detector.movementIdle = false
		detector.inputIdleCycles = 400
		assertEquals(0, still(detector), "the first cycle is not five minutes")
		assertEquals(0, run(detector, 398))
		assertEquals(IdleDetector.INPUT_IDLE, still(detector), "400 cycles after the start")
		assertEquals(0, run(detector, 800), "said once")
	}

	@Test
	fun noInputForAWhileIsSaidOnceAndArmedAgainByTheMouse() {
		val detector: IdleDetector = detector()
		detector.movementIdle = false
		detector.inputIdleCycles = 400
		detector.mouseMoved()
		detector.mousePressed()
		detector.keyPressed()
		assertEquals(0, run(detector, 399))
		assertEquals(IdleDetector.INPUT_IDLE, still(detector))
		assertEquals(0, run(detector, 800), "said once")
		detector.mouseMoved()
		assertEquals(0, run(detector, 399))
		assertEquals(IdleDetector.INPUT_IDLE, still(detector))
	}

	@Test
	fun lowLifePointsAreSaidOnceWhenCrossedAndArmedAgainAbove() {
		val detector: IdleDetector = detector()
		detector.movementIdle = false
		detector.hitpointsThreshold = 100
		assertEquals(0, still(detector, hitpoints = 50), "never seen above: not armed")
		assertEquals(0, still(detector, hitpoints = 500))
		assertEquals(IdleDetector.HITPOINTS, still(detector, hitpoints = 100))
		assertEquals(0, still(detector, hitpoints = 90), "said once")
		assertEquals(0, still(detector, hitpoints = 101))
		assertEquals(IdleDetector.HITPOINTS, still(detector, hitpoints = 60))
		assertEquals(0, detector.cycle(-1, false, 10, 10, false, 50, 100, 99, 99, 100, 1000), "a base at the threshold is never over it")
		detector.hitpointsThreshold = 0
		assertEquals(0, still(detector, hitpoints = 500) or still(detector, hitpoints = 10), "off")
	}

	@Test
	fun prayerAndEnergyFollowTheSameRule() {
		val detector: IdleDetector = detector()
		detector.movementIdle = false
		detector.prayerThreshold = 10
		detector.lowEnergyThreshold = 20
		detector.highEnergyThreshold = 100
		assertEquals(0, still(detector, prayer = 50, energy = 100))
		assertEquals(IdleDetector.PRAYER, still(detector, prayer = 10, energy = 100))
		assertEquals(IdleDetector.LOW_ENERGY, still(detector, prayer = 10, energy = 20))
		assertEquals(0, still(detector, prayer = 5, energy = 5), "both said")
		assertEquals(0, still(detector, prayer = 5, energy = 99), "not yet back")
		assertEquals(IdleDetector.HIGH_ENERGY, still(detector, prayer = 5, energy = 100))
		assertEquals(0, still(detector, prayer = 5, energy = 100), "said once")
	}

	@Test
	fun specialAttackEnergyIsSaidWhenItComesBackAStepAtATime() {
		val detector: IdleDetector = detector()
		detector.movementIdle = false
		detector.specThreshold = 1000
		assertEquals(0, still(detector, spec = 1000), "full from the start")
		assertEquals(0, still(detector, spec = 500))
		assertEquals(0, still(detector, spec = 900))
		assertEquals(IdleDetector.SPEC, still(detector, spec = 1000))
		assertEquals(0, still(detector, spec = 0))
		assertEquals(0, still(detector, spec = 1000), "a jump is a reset, not a restoration")
		detector.specThreshold = 500
		assertEquals(0, still(detector, spec = 400))
		assertEquals(IdleDetector.SPEC, still(detector, spec = 500))
	}

	@Test
	fun resetForgetsEverything() {
		val detector: IdleDetector = detector()
		detector.movementIdle = false
		detector.hitpointsThreshold = 100
		detector.animationChanged(chop)
		run(detector, 3, animationId = chop)
		still(detector, hitpoints = 500)
		detector.reset()
		var bits: Int = 0
		for (i in 0 until delay * 2) {
			bits = bits or still(detector, hitpoints = 50)
		}
		assertEquals(0, bits, "no animation watched, no threshold armed")
	}

	@Test
	fun theSkillingListHoldsItsIdsAndNoOthers() {
		assertTrue(IdleAnimations.contains(879), "a bronze hatchet")
		assertTrue(IdleAnimations.contains(12189), "a dragon pickaxe")
		assertTrue(IdleAnimations.contains(363), "mixing a potion")
		assertTrue(IdleAnimations.contains(13128), "a primal hatchet, Daemonheim")
		assertTrue(IdleAnimations.contains(12323), "the inferno adze on ivy")
		assertTrue(IdleAnimations.contains(466), "the spinning wheel")
		assertTrue(IdleAnimations.contains(1648), "the bone grinder")
		assertTrue(IdleAnimations.contains(8500), "infusing pouches")
		assertFalse(IdleAnimations.contains(7041), "a 2h chop is a fight")
		assertFalse(IdleAnimations.contains(8199), "fish rain is a familiar's special")
		assertFalse(IdleAnimations.contains(-1))
		assertFalse(IdleAnimations.contains(2716), "resting")
		assertFalse(IdleAnimations.contains(866))
	}
}
