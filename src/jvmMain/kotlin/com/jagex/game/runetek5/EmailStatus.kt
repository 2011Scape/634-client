package com.jagex.game.runetek5

import org.openrs2.deob.annotation.OriginalMember

object EmailStatus {

	@OriginalMember(owner = "client!ru", name = "c", descriptor = "I")
	@JvmField
	var emailStatus: Int = 0
}
