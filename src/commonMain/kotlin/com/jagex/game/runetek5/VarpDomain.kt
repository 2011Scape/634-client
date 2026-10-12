package com.jagex.game.runetek5

import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField

object VarpDomain {

	@OriginalMember(owner = "client!gia", name = "A", descriptor = "[I")
	@JvmField
	val projectedDirection: IntArray = IntArray(3)
}
