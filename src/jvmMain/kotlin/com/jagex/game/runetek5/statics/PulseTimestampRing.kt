package com.jagex.game.runetek5.statics

import org.openrs2.deob.annotation.OriginalMember

object PulseTimestampRing {

	@OriginalMember(owner = "client!dr", name = "h", descriptor = "[J")
	@JvmField
	val PULSE_TIMES: LongArray = LongArray(32)

	@OriginalMember(owner = "client!tda", name = "e", descriptor = "I")
	@JvmField
	var pulseTimesIndex: Int = 0
}
