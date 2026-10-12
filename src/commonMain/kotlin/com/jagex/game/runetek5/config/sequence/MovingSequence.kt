package com.jagex.game.runetek5.config.sequence

import kotlin.jvm.JvmField

/**
 * Not part of the original client: fields the jar keeps loose on their owner, grouped in one
 * class.
 */
class MovingSequence {

	@JvmField
	var hasType: Boolean = false

	@JvmField
	var sequenceId: Int = -1

	@JvmField
	var currentFrameDuration: Int = 0

	@JvmField
	var currentFrameId: Int = 0

	@JvmField
	var nextFrameId: Int = -1
}
