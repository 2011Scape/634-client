package com.jagex.game.runetek5

import org.openrs2.deob.annotation.OriginalMember

object Cursor {

	@OriginalMember(owner = "client!ep", name = "v", descriptor = "I")
	@JvmField
	var interaction: Int = 0
	@OriginalMember(owner = "client!fg", name = "c", descriptor = "I")
	@JvmField
	var interfaceCursorId: Int = -1
}
