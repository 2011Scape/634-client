package com.jagex.game.runetek5

import org.openrs2.deob.annotation.OriginalMember

object D3DEnvironmentMappedWaterMatrix {

	@OriginalMember(owner = "client!gg", name = "i", descriptor = "[F")
	@JvmField
	val MATRIX_BUFFER: FloatArray = FloatArray(16)
}
