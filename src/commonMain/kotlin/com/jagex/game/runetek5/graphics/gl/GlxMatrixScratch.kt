package com.jagex.game.runetek5.graphics.gl

import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField

object GlxMatrixScratch {

	@OriginalMember(owner = "client!sn", name = "a", descriptor = "[F")
	@JvmField
	val MATRIX_BUFFER: FloatArray = FloatArray(16)
}
