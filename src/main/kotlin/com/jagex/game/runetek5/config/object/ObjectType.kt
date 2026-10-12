package com.jagex.game.runetek5.config.`object`

import org.openrs2.deob.annotation.OriginalMember

object ObjectType {
	@OriginalMember(owner = "client!mj", name = "s", descriptor = "[S")
	@JvmField
	var locRecolorPalette: ShortArray? = ShortArray(256)
}
