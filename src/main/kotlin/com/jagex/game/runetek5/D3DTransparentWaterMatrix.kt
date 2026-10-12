package com.jagex.game.runetek5

import org.openrs2.deob.annotation.OriginalMember

object D3DTransparentWaterMatrix {

	@OriginalMember(owner = "client!gaa", name = "i", descriptor = "[F")
	@JvmField
	val MATRIX_BUFFER: FloatArray = FloatArray(16)
}
