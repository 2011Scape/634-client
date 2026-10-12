package com.jagex.game.runetek5.entity

/**
 * Not part of the original client: fields the jar keeps loose on their owner, grouped in one
 * class.
 */
class PathingEntitySpotAnimation {

	@JvmField
	var height: Int = 0

	@JvmField
	var startCycle: Int = 0

	@JvmField
	var rotation: Int = 0

	@JvmField
	var currentFrameId: Int = 0

	@JvmField
	var spotAnimationId: Int = -1

	@JvmField
	var adjustmentType: Int = -1

	@JvmField
	var currentFrameDuration: Int = 0

	@JvmField
	var currentLoopCount: Int = 0

	@JvmField
	var nextFrameId: Int = -1
}
