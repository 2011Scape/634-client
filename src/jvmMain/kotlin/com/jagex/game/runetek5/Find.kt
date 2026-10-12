package com.jagex.game.runetek5

import org.openrs2.deob.annotation.OriginalMember

object Find {

	@OriginalMember(owner = "client!kda", name = "Wb", descriptor = "I")
	@JvmField
	var menuBottomBorderSpriteId: Int = 0
	@OriginalMember(owner = "client!ks", name = "h", descriptor = "I")
	@JvmField
	var findResultCount: Int = 0

	@OriginalMember(owner = "client!qq", name = "h", descriptor = "[S")
	@JvmField
	var queryResults: ShortArray? = null
}
