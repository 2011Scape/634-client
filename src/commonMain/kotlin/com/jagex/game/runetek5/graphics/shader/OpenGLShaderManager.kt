package com.jagex.game.runetek5.graphics.shader

import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField

object OpenGLShaderManager {
	@OriginalMember(owner = "client!qga", name = "l", descriptor = "[I")
	@JvmField
	val BUFFER: IntArray = IntArray(2)
}
