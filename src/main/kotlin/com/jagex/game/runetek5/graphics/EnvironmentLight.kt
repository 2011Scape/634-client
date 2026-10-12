package com.jagex.game.runetek5.graphics

import org.openrs2.deob.annotation.OriginalMember

object EnvironmentLight {

	@OriginalMember(owner = "client!hr", name = "Q", descriptor = "I")
	@JvmField
	var textureOpWidth: Int = 0

	@OriginalMember(owner = "client!ql", name = "q", descriptor = "[I")
	@JvmField
	var horizontalTable: IntArray? = null
}
