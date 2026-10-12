package com.jagex.game.runetek5.config.invtype

import org.openrs2.deob.annotation.OriginalMember

object LoginDisallowReason {

	@OriginalMember(owner = "client!pv", name = "x", descriptor = "I")
	@JvmField
	var loginDisallowReason: Int = -1
}
