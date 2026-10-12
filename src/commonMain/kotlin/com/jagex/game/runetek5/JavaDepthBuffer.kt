package com.jagex.game.runetek5

import com.jagex.game.runetek5.DepthBuffer
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField

@OriginalClass("client!ru")
class JavaDepthBuffer @OriginalMember(owner = "client!ru", name = "<init>", descriptor = "(II)V") constructor(@OriginalArg(0) height: Int, @OriginalArg(1) width: Int) : DepthBuffer {

	@OriginalMember(owner = "client!ru", name = "e", descriptor = "I")
	@JvmField
	val width: Int

	@OriginalMember(owner = "client!ru", name = "a", descriptor = "I")
	@JvmField
	val height: Int

	@OriginalMember(owner = "client!ru", name = "g", descriptor = "[F")
	@JvmField
	val depths: FloatArray

	init {
		this.width = width
		this.height = height
		this.depths = FloatArray(height * width)
	}
}
