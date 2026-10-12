package com.jagex.game.runetek5.camera

import org.openrs2.deob.annotation.OriginalMember

object Shake {

	const val DIRECTIONS: Int = 5

	@OriginalMember(owner = "client!uo", name = "T", descriptor = "[I")
	@JvmField
	val CAM_SHAKE_PHASE: IntArray = IntArray(5)

	@OriginalMember(owner = "client!ub", name = "i", descriptor = "[I")
	@JvmField
	val CAM_SHAKE_AMPLITUDE: IntArray = IntArray(5)

	@OriginalMember(owner = "client!ea", name = "p", descriptor = "[I")
	@JvmField
	val CAM_SHAKE_FREQUENCY: IntArray = IntArray(5)
}
