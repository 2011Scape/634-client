package com.jagex.game.runetek5.statics

import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField

object OutPlayerIndexCount {

	@OriginalMember(owner = "client!cha", name = "d", descriptor = "I")
	@JvmField
	var NUM_OUT_PLAYER_INDICES: Int = 0
}
