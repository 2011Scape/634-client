package com.jagex.game.runetek5

import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField

object SunPositionZ {

	@OriginalMember(owner = "client!qv", name = "c", descriptor = "I")
	@JvmField
	var sunPositionZ: Int = -50
}
