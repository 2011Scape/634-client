package com.jagex.game.runetek5.graphics.safe

import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField

object PureJavaTexture {

	@OriginalMember(owner = "client!mg", name = "q", descriptor = "[I")
	@JvmField
	var colorsArgb: IntArray? = null
}
