package com.jagex.game.runetek5.toolkit.opengl.buffer

import jaclib.memory.Buffer
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.buffer.ArrayBufferGL
import com.jagex.game.runetek5.graphics.gl.GlToolkit

@OriginalClass("client!cw")
class GlArrayBuffer : GlBuffer, ArrayBufferGL {

	@OriginalMember(owner = "client!cw", name = "c", descriptor = "I")
	private var stride: Int

	@OriginalMember(owner = "client!cw", name = "<init>", descriptor = "(Lclient!qo;I[BI)V")
	constructor(@OriginalArg(0) glToolkit: GlToolkit?, @OriginalArg(1) stride: Int, @OriginalArg(2) buffer: ByteArray?, @OriginalArg(3) initialCapacity: Int) : super(glToolkit, buffer, initialCapacity) {
		this.stride = stride
	}

	@OriginalMember(owner = "client!cw", name = "<init>", descriptor = "(Lclient!qo;ILclient!jaclib/memory/Buffer;)V")
	constructor(@OriginalArg(0) glToolkit: GlToolkit?, @OriginalArg(1) stride: Int, @OriginalArg(2) buffer: Buffer?) : super(glToolkit, buffer) {
		this.stride = stride
	}

	@OriginalMember(owner = "client!cw", name = "a", descriptor = "(B)J")
	override fun getAddress(): Long {
		return super.buffer!!.getAddress()
	}

	@OriginalMember(owner = "client!cw", name = "b", descriptor = "(B)I")
	override fun getStride(): Int {
		return this.stride
	}

	@OriginalMember(owner = "client!cw", name = "a", descriptor = "(II[BI)V")
	override fun putArray(@OriginalArg(0) stride: Int, @OriginalArg(1) requiredSize: Int, @OriginalArg(2) data: ByteArray?) {
		this.put(data, requiredSize)
		this.stride = stride
	}

	@OriginalMember(owner = "client!cw", name = "a", descriptor = "(Z)I")
	override fun getId(): Int {
		return 0
	}
}
