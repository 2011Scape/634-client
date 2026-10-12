package com.jagex.game.runetek5

import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField

object UnloadedLocCount {

	@OriginalMember(owner = "client!oo", name = "l", descriptor = "I")
	@JvmField
	var unloadedLocCount: Int = 0
}
