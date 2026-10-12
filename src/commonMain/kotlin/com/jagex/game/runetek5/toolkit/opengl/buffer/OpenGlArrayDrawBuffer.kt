package com.jagex.game.runetek5.toolkit.opengl.buffer

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.buffer.ArrayBufferGL
import kotlin.jvm.JvmField

@OriginalClass("client!nh")
class OpenGlArrayDrawBuffer {

	@OriginalMember(owner = "client!nh", name = "a", descriptor = "S")
	@JvmField
	val type: Short

	@OriginalMember(owner = "client!nh", name = "c", descriptor = "B")
	@JvmField
	var offset: Byte

	@OriginalMember(owner = "client!nh", name = "e", descriptor = "B")
	@JvmField
	val size: Byte

	@OriginalMember(owner = "client!nh", name = "b", descriptor = "Lclient!hca;")
	@JvmField
	var buffer: ArrayBufferGL?

	@OriginalMember(owner = "client!nh", name = "<init>", descriptor = "(Lclient!hca;III)V")
	constructor(@OriginalArg(0) buffer: ArrayBufferGL?, @OriginalArg(1) type: Int, @OriginalArg(2) size: Int, @OriginalArg(3) offset: Int) {
		this.type = type.toShort()
		this.offset = offset.toByte()
		this.size = size.toByte()
		this.buffer = buffer
	}
}
