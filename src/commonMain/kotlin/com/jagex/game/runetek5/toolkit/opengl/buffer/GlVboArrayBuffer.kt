package com.jagex.game.runetek5.toolkit.opengl.buffer

import jaclib.memory.Buffer
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.buffer.ArrayBufferGL
import com.jagex.game.runetek5.graphics.gl.GlToolkit

@OriginalClass("client!jt")
class GlVboArrayBuffer : AbstractArbBufferGL, ArrayBufferGL {

	@OriginalMember(owner = "client!jt", name = "m", descriptor = "I")
	private var _stride: Int

	@OriginalMember(owner = "client!jt", name = "<init>", descriptor = "(Lclient!qo;I[BIZ)V")
	constructor(@OriginalArg(0) glToolkit: GlToolkit?, @OriginalArg(1) stride: Int, @OriginalArg(2) data: ByteArray?, @OriginalArg(3) bufferSize: Int, @OriginalArg(4) streamDraw: Boolean) : super(glToolkit, 34962, data, bufferSize, streamDraw) {
		this._stride = stride
	}

	@OriginalMember(owner = "client!jt", name = "<init>", descriptor = "(Lclient!qo;ILclient!jaclib/memory/Buffer;IZ)V")
	constructor(@OriginalArg(0) glToolkit: GlToolkit?, @OriginalArg(1) _stride: Int, @OriginalArg(2) buffer: Buffer?, @OriginalArg(3) bufferSize: Int, @OriginalArg(4) streamDraw: Boolean) : super(glToolkit, 34962, buffer, bufferSize, streamDraw) {
		this._stride = _stride
	}

	@OriginalMember(owner = "client!jt", name = "b", descriptor = "(B)I")
	override fun getStride(): Int {
		return this._stride
	}

	@OriginalMember(owner = "client!jt", name = "d", descriptor = "(I)V")
	override fun setAsActiveBuffer() {
		super.toolkit!!.setArrayBuffer(this)
	}

	@OriginalMember(owner = "client!jt", name = "a", descriptor = "(II[BI)V")
	override fun putArray(@OriginalArg(0) _stride: Int, @OriginalArg(1) bufferSize: Int, @OriginalArg(2) data: ByteArray?) {
		this.put(data, bufferSize)
		this._stride = _stride
	}

	@OriginalMember(owner = "client!jt", name = "a", descriptor = "(Z)I")
	override fun getId(): Int {
		return super.id
	}

	@OriginalMember(owner = "client!jt", name = "a", descriptor = "(B)J")
	override fun getAddress(): Long {
		return 0L
	}
}
