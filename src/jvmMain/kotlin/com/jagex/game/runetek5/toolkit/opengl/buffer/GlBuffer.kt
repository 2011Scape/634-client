package com.jagex.game.runetek5.toolkit.opengl.buffer

import jaclib.memory.Buffer
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.graphics.gl.GlToolkit

@OriginalClass("client!nf")
open class GlBuffer {

	@OriginalMember(owner = "client!nf", name = "b", descriptor = "Lclient!qo;")
	private val toolkit: GlToolkit?

	@OriginalMember(owner = "client!nf", name = "a", descriptor = "Lclient!jaclib/memory/Buffer;")
	@JvmField
	var buffer: Buffer?

	@OriginalMember(owner = "client!nf", name = "<init>", descriptor = "(Lclient!qo;[BI)V")
	constructor(@OriginalArg(0) glToolkit: GlToolkit?, @OriginalArg(1) data: ByteArray?, @OriginalArg(2) initialCapacity: Int) {
		this.toolkit = glToolkit
		this.buffer = this.toolkit!!.nativeHeap!!.a(initialCapacity, false)
		if (data != null) {
			this.buffer!!.a(data, 0, 0, initialCapacity)
		}
	}

	@OriginalMember(owner = "client!nf", name = "<init>", descriptor = "(Lclient!qo;Lclient!jaclib/memory/Buffer;)V")
	constructor(@OriginalArg(0) glToolkit: GlToolkit?, @OriginalArg(1) buffer: Buffer?) {
		this.toolkit = glToolkit
		this.buffer = buffer
	}

	@OriginalMember(owner = "client!nf", name = "a", descriptor = "([BI)V")
	fun put(@OriginalArg(0) data: ByteArray?, @OriginalArg(1) requiredSize: Int) {
		if (this.buffer == null || this.buffer!!.getSize() < requiredSize) {
			this.buffer = this.toolkit!!.nativeHeap!!.a(requiredSize, false)
		}
		this.buffer!!.a(data, 0, 0, requiredSize)
	}
}
