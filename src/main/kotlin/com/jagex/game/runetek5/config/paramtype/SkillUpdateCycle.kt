package com.jagex.game.runetek5.config.paramtype

import org.openrs2.deob.annotation.OriginalMember

object SkillUpdateCycle {

	@OriginalMember(owner = "client!dc", name = "d", descriptor = "I")
	@JvmField
	var skillUpdateCycle: Int = 0

	@OriginalMember(owner = "client!rc", name = "i", descriptor = "[I")
	@JvmField
	val recentSkillUpdates: IntArray = IntArray(32)
}
