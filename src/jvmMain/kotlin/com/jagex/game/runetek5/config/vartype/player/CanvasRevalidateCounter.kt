package com.jagex.game.runetek5.config.vartype.player

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

object CanvasRevalidateCounter {

	@OriginalMember(owner = "client!ow", name = "d", descriptor = "I")
	@JvmField
	var revalidateCounter: Int = 500
}
