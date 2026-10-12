package com.jagex.game.runetek5.toolkit

import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField

object OpenGlToolkit {
	@OriginalMember(owner = "client!cha", name = "e", descriptor = "[F")
	@JvmField
	val LIGHT_MATRIX: FloatArray = FloatArray(4)

	@OriginalMember(owner = "client!sfa", name = "e", descriptor = "[F")
	@JvmField
	val BUFFER_4F: FloatArray = FloatArray(4)
}
