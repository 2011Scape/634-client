package com.jagex.game.runetek5.config.`object`

import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField

object ObjectType {
	@OriginalMember(owner = "client!mj", name = "s", descriptor = "[S")
	@JvmField
	var locRecolorPalette: ShortArray? = ShortArray(256)
}
