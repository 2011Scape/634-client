package com.jagex.game.runetek5.config.meltype

import org.openrs2.deob.annotation.OriginalMember

object OccludedWallCount {

	@OriginalMember(owner = "client!ii", name = "a", descriptor = "I")
	@JvmField
	var occludedWallCount: Int = 0
}
