package com.jagex.game.runetek5

import org.openrs2.deob.annotation.OriginalMember

object VarpDomain {

	@OriginalMember(owner = "client!gia", name = "A", descriptor = "[I")
	@JvmField
	val projectedDirection: IntArray = IntArray(3)
}
