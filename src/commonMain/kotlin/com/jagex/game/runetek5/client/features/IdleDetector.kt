package com.jagex.game.runetek5.client.features

/**
 * Not part of the original client: what the idle notifier (IdleNotifier) decides each client
 * cycle, kept apart from the client so that a test can drive it. The plugin feeds it the events it
 * hears ([animationChanged], [interactingChanged], [hitsplatOnPlayer], the input) and what it
 * reads each cycle ([cycle]); back come the bits of what to say this cycle ([ANIMATION] and the
 * rest). The rules are RuneLite's Idle Notifier's, with its milliseconds and server ticks turned
 * into client cycles of 20 ms:
 *
 * - A player who is pressing the mouse, or clicked or typed a moment ago, is not idle: the idle
 *   timers reset and nothing of them is said, though the thresholds still are.
 * - Animation idle: a skilling animation ([IdleAnimations]) that stopped [delayCycles] ago. Any
 *   other animation forgets the one watched.
 * - Interaction idle: a target that was an attackable npc or a fishing spot, gone [delayCycles]
 *   ago and no hit splat on the player for [COMBAT_HOLD_CYCLES]; [OUT_OF_COMBAT] after a fight.
 * - Movement idle: the same tile for [delayCycles] with no animation, once per stop.
 * - Input idle: no mouse or keyboard for [inputIdleCycles], once.
 * - Thresholds: life points, prayer points and run energy at or under theirs, run energy and
 *   special attack energy back at theirs, each once until the value crosses back.
 */
class IdleDetector {

	/** Cycles a skilling animation, a target or the position must be still before the word. */
	var delayCycles: Int = DEFAULT_DELAY_SECONDS * CYCLES_PER_SECOND

	var animationIdle: Boolean = true

	var interactionIdle: Boolean = true

	var movementIdle: Boolean = false

	/** Cycles without mouse or keyboard before [INPUT_IDLE]; 0 for never. */
	var inputIdleCycles: Int = 0

	/** Life points at or under which [HITPOINTS] is said; 0 for never. */
	var hitpointsThreshold: Int = 0

	/** Prayer points at or under which [PRAYER] is said; 0 for never. */
	var prayerThreshold: Int = 0

	/** Run energy (0 to 100) at or under which [LOW_ENERGY] is said; 0 for never. */
	var lowEnergyThreshold: Int = 0

	/** Run energy (0 to 100) at or over which [HIGH_ENERGY] is said; 0 for never. */
	var highEnergyThreshold: Int = 0

	/** Special attack energy in tenths of a percent (0 to 1000) at or over which [SPEC] is said; 0 for never. */
	var specThreshold: Int = 0

	/** The cycle count, this detector's own. */
	private var now: Int = 0

	/** The skilling animation watched, or -1. */
	private var lastAnimation: Int = -1

	/** The cycle the player was last seen in [lastAnimation], or -1 for not since the timers reset. */
	private var lastAnimating: Int = -1

	/** The target watched: [INTERACT_COMBAT] or [INTERACT_FISHING], or [INTERACT_NONE]. */
	private var lastInteract: Int = INTERACT_NONE

	/** The cycle the player was last seen with a target, or -1 for not since the timers reset. */
	private var lastInteracting: Int = -1

	private var lastInteractWasCombat: Boolean = false

	/** Cycles left of the hold a hit splat on the player puts on the interaction idle. */
	private var combatHold: Int = 0

	private var lastTileX: Int = -1

	private var lastTileY: Int = -1

	/** The cycle the player last moved. */
	private var lastMoving: Int = 0

	/** Whether this stop has yet to be said. */
	private var notifyPosition: Boolean = false

	private var cyclesSinceClick: Int = NEVER

	private var cyclesSinceKey: Int = NEVER

	private var cyclesSinceMouseMove: Int = NEVER

	private var inputIdleSaid: Boolean = false

	/** Each threshold is armed once its value has been on the right side of it. */
	private var hitpointsArmed: Boolean = false

	private var prayerArmed: Boolean = false

	private var lowEnergyArmed: Boolean = false

	private var highEnergyArmed: Boolean = false

	private var lastSpec: Int = FULL_SPEC

	/** The local player's animation was set to [id]. */
	fun animationChanged(id: Int) {
		if (IdleAnimations.contains(id)) {
			resetTimers(id, lastInteract != INTERACT_NONE)
			lastAnimation = id
			lastAnimating = now
		} else {
			lastAnimation = -1
			lastAnimating = -1
		}
	}

	/** The local player's target changed to one of [kind]: [INTERACT_NONE] for none. */
	fun interactingChanged(kind: Int) {
		if (kind != INTERACT_NONE) {
			lastInteract = INTERACT_NONE
		} else {
			lastInteracting = now
		}
		if (kind == INTERACT_COMBAT || kind == INTERACT_FISHING) {
			resetTimers(lastAnimation, false)
			lastInteract = kind
			lastInteracting = now
			lastInteractWasCombat = kind == INTERACT_COMBAT
		}
	}

	/** A hit splat landed on the local player: the fight is on for a while yet. */
	fun hitsplatOnPlayer() {
		combatHold = COMBAT_HOLD_CYCLES
	}

	fun mousePressed() {
		cyclesSinceClick = 0
	}

	fun keyPressed() {
		cyclesSinceKey = 0
	}

	fun mouseMoved() {
		cyclesSinceMouseMove = 0
	}

	/**
	 * A client cycle, given the local player's animation (-1 for none), whether it has a target,
	 * its tile, whether a mouse button is held, its life points and prayer points (current and
	 * base), its run energy (0 to 100) and its special attack energy (0 to 1000). Returns the bits
	 * of what to say.
	 */
	fun cycle(animationId: Int, hasTarget: Boolean, tileX: Int, tileY: Int, mouseHeld: Boolean, hitpoints: Int, baseHitpoints: Int, prayer: Int, basePrayer: Int, energy: Int, spec: Int): Int {
		now++
		if (cyclesSinceClick < NEVER) {
			cyclesSinceClick++
		}
		if (cyclesSinceKey < NEVER) {
			cyclesSinceKey++
		}
		if (cyclesSinceMouseMove < NEVER) {
			cyclesSinceMouseMove++
		}
		if (combatHold > 0) {
			combatHold--
		}
		var bits: Int = 0
		if (checkLow(hitpointsThreshold, hitpoints, baseHitpoints, hitpointsArmed)) {
			bits = bits or HITPOINTS
		}
		hitpointsArmed = armLow(hitpointsThreshold, hitpoints, baseHitpoints, hitpointsArmed)
		if (checkLow(prayerThreshold, prayer, basePrayer, prayerArmed)) {
			bits = bits or PRAYER
		}
		prayerArmed = armLow(prayerThreshold, prayer, basePrayer, prayerArmed)
		if (checkLow(lowEnergyThreshold, energy, FULL_ENERGY, lowEnergyArmed)) {
			bits = bits or LOW_ENERGY
		}
		lowEnergyArmed = armLow(lowEnergyThreshold, energy, FULL_ENERGY, lowEnergyArmed)
		if (highEnergyThreshold > 0) {
			if (energy >= highEnergyThreshold) {
				if (highEnergyArmed) {
					bits = bits or HIGH_ENERGY
				}
				highEnergyArmed = false
			} else {
				highEnergyArmed = true
			}
		}
		if (specThreshold > 0 && lastSpec < specThreshold && spec >= specThreshold && spec - lastSpec <= SPEC_STEP) {
			bits = bits or SPEC
		}
		lastSpec = spec
		if (mouseHeld || cyclesSinceClick < CLICK_ACTIVE_CYCLES || cyclesSinceKey < KEY_ACTIVE_CYCLES) {
			resetTimers(animationId, hasTarget)
			return bits
		}
		if (inputIdleCycles > 0) {
			var idle: Int = now // input never seen: idle since the start (login)
			if (cyclesSinceClick < idle) {
				idle = cyclesSinceClick
			}
			if (cyclesSinceKey < idle) {
				idle = cyclesSinceKey
			}
			if (cyclesSinceMouseMove < idle) {
				idle = cyclesSinceMouseMove
			}
			if (idle >= inputIdleCycles) {
				if (!inputIdleSaid) {
					inputIdleSaid = true
					bits = bits or INPUT_IDLE
				}
			} else {
				inputIdleSaid = false
			}
		}
		if (animationIdle && checkAnimationIdle(animationId)) {
			bits = bits or ANIMATION
		}
		if (movementIdle && checkMovementIdle(animationId, tileX, tileY) && (bits and ANIMATION) == 0) {
			bits = bits or MOVEMENT
		}
		if (interactionIdle && checkInteractionIdle(hasTarget)) {
			bits = bits or (if (lastInteractWasCombat) OUT_OF_COMBAT else INTERACTION)
		}
		return bits
	}

	/** Back to the start: at logout, and when the plugin stops. */
	fun reset() {
		now = 0
		lastAnimation = -1
		lastAnimating = -1
		lastInteract = INTERACT_NONE
		lastInteracting = -1
		lastInteractWasCombat = false
		combatHold = 0
		lastTileX = -1
		lastTileY = -1
		lastMoving = 0
		notifyPosition = false
		cyclesSinceClick = NEVER
		cyclesSinceKey = NEVER
		cyclesSinceMouseMove = NEVER
		inputIdleSaid = false
		hitpointsArmed = false
		prayerArmed = false
		lowEnergyArmed = false
		highEnergyArmed = false
		lastSpec = FULL_SPEC
	}

	/**
	 * The idle timers start again, as when the player does something: the stamps go, and the
	 * animation or target watched goes too when it is no longer the player's ([animationId], [hasTarget]).
	 */
	fun resetTimers(animationId: Int, hasTarget: Boolean) {
		lastAnimating = -1
		lastInteracting = -1
		if (lastAnimation != animationId) {
			lastAnimation = -1
		}
		if (!hasTarget) {
			lastInteract = INTERACT_NONE
		}
	}

	/** Whether [current] of [base] is at or under [threshold] while [armed]; the threshold 0 is off, and so is one the base cannot be over. */
	private fun checkLow(threshold: Int, current: Int, base: Int, armed: Boolean): Boolean {
		return threshold > 0 && base > threshold && current <= threshold && armed
	}

	/** The arming of a low threshold after this cycle: armed by a value over it, disarmed by one at or under it. */
	private fun armLow(threshold: Int, current: Int, base: Int, armed: Boolean): Boolean {
		if (threshold <= 0 || base <= threshold) {
			return armed
		}
		return current > threshold
	}

	private fun checkAnimationIdle(animationId: Int): Boolean {
		if (lastAnimation == -1) {
			return false
		}
		if (animationId == -1) {
			if (lastAnimating != -1 && now - lastAnimating >= delayCycles) {
				lastAnimation = -1
				lastAnimating = -1
				lastInteracting = now // the interaction idle, if any, is not said right behind this
				return true
			}
		} else {
			lastAnimating = now
		}
		return false
	}

	private fun checkMovementIdle(animationId: Int, tileX: Int, tileY: Int): Boolean {
		if (lastTileX == -1 && lastTileY == -1) {
			lastTileX = tileX
			lastTileY = tileY
			lastMoving = now
			notifyPosition = true
			return false
		}
		if (lastTileX == tileX && lastTileY == tileY) {
			if (notifyPosition && animationId == -1 && now - lastMoving >= delayCycles) {
				notifyPosition = false
				return lastAnimation == -1 // not when the stop is the end of an animation
			}
		} else {
			notifyPosition = true
			lastTileX = tileX
			lastTileY = tileY
			lastMoving = now
		}
		return false
	}

	private fun checkInteractionIdle(hasTarget: Boolean): Boolean {
		if (lastInteract == INTERACT_NONE) {
			return false
		}
		if (!hasTarget) {
			if (lastInteracting != -1 && now - lastInteracting >= delayCycles && combatHold == 0) {
				lastInteract = INTERACT_NONE
				lastInteracting = -1
				lastAnimating = now // the animation idle, if any, is not said right behind this
				return true
			}
		} else {
			lastInteracting = now
		}
		return false
	}

	companion object {
		const val ANIMATION: Int = 1

		const val INTERACTION: Int = 2

		const val OUT_OF_COMBAT: Int = 4

		const val MOVEMENT: Int = 8

		const val INPUT_IDLE: Int = 16

		const val HITPOINTS: Int = 32

		const val PRAYER: Int = 64

		const val LOW_ENERGY: Int = 128

		const val HIGH_ENERGY: Int = 256

		const val SPEC: Int = 512

		const val INTERACT_NONE: Int = 0

		const val INTERACT_COMBAT: Int = 1

		const val INTERACT_FISHING: Int = 2

		const val INTERACT_OTHER: Int = 3

		/** A client cycle, and so a GameTick, is 20 ms. */
		const val CYCLES_PER_SECOND: Int = 50

		const val DEFAULT_DELAY_SECONDS: Int = 5

		/** The hold a hit splat puts on the interaction idle: the slowest monster's attack, 8 server ticks of 30 cycles. */
		const val COMBAT_HOLD_CYCLES: Int = 8 * 30

		/** A click within the last second makes the player active. */
		const val CLICK_ACTIVE_CYCLES: Int = CYCLES_PER_SECOND

		/** A key within the last 10 server ticks makes the player active. */
		const val KEY_ACTIVE_CYCLES: Int = 10 * 30

		/** Special attack energy comes back 100 (10%) at a time; a bigger jump is a reset, not a restoration. */
		const val SPEC_STEP: Int = 100

		const val FULL_SPEC: Int = 1000

		const val FULL_ENERGY: Int = 100

		/** A count of cycles since input that was never seen, and stays there. */
		private const val NEVER: Int = Int.MAX_VALUE
	}
}
