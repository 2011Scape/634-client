package com.jagex.game.runetek5.toolkit.companion

import com.jagex.game.runetek5.TextureCombineFunction
import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField

object TextureCombineMode {
	@OriginalMember(owner = "client!pga", name = "q", descriptor = "Lclient!sia;")
	@JvmField
	val MODULATE: TextureCombineFunction = TextureCombineFunction()
}
