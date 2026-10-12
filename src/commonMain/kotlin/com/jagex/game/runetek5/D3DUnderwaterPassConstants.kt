package com.jagex.game.runetek5

import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField

object D3DUnderwaterPassConstants {

	@OriginalMember(owner = "client!fl", name = "j", descriptor = "[F")
	@JvmField
	val VECTOR_BUFFER: FloatArray = FloatArray(4)

	@OriginalMember(owner = "client!fl", name = "t", descriptor = "[F")
	@JvmField
	val MATRIX_BUFFER: FloatArray = FloatArray(16)
}
