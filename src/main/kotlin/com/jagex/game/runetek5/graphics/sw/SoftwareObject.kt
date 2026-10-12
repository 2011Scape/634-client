package com.jagex.game.runetek5.graphics.sw

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!e")
interface SoftwareObject {

	@OriginalMember(owner = "client!e", name = "w", descriptor = "(Z)V")
	fun w(@OriginalArg(0) arg0: Boolean)
}
