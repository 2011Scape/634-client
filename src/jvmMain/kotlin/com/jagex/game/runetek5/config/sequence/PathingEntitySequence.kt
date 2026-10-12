package com.jagex.game.runetek5.config.sequence

import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!qca")
class PathingEntitySequence {

	@OriginalMember(owner = "client!qca", name = "a", descriptor = "I")
	@JvmField
	var currentLoopCount: Int = 0

	@OriginalMember(owner = "client!qca", name = "d", descriptor = "I")
	@JvmField
	var speed: Int = 0

	@OriginalMember(owner = "client!qca", name = "g", descriptor = "I")
	@JvmField
	var currentFrameId: Int = 0

	@OriginalMember(owner = "client!qca", name = "j", descriptor = "I")
	@JvmField
	var sequenceId: Int = 0

	@OriginalMember(owner = "client!qca", name = "k", descriptor = "I")
	@JvmField
	var nextFrameId: Int = 0

	@OriginalMember(owner = "client!qca", name = "l", descriptor = "I")
	@JvmField
	var currentFrameDuration: Int = 0
}
