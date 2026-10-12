package com.jagex.game.runetek5

import org.openrs2.deob.annotation.OriginalMember

object GlxShaderCompileStatus {

	@OriginalMember(owner = "client!gd", name = "J", descriptor = "[I")
	@JvmField
	val SHADER_PARAMS: IntArray = IntArray(2)
}
