package com.jagex.game.runetek5.toolkit.opengl.buffer

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.buffer.ElementsBufferGL
import com.jagex.game.runetek5.graphics.gl.GlToolkit

@OriginalClass("client!wv")
class GlElementsArrayBuffer : GlBuffer, ElementsBufferGL {

	@OriginalMember(owner = "client!wv", name = "d", descriptor = "I")
	private var indexType: Int

	@OriginalMember(owner = "client!wv", name = "<init>", descriptor = "(Lclient!qo;I[BI)V")
	constructor(@OriginalArg(0) glToolkit: GlToolkit?, @OriginalArg(1) indexType: Int, @OriginalArg(2) data: ByteArray?, @OriginalArg(3) initialCapacity: Int) : super(glToolkit, data, initialCapacity) {
		this.indexType = indexType
	}

	@OriginalMember(owner = "client!wv", name = "a", descriptor = "(I)I")
	override fun getId(): Int {
		return 0
	}

	@OriginalMember(owner = "client!wv", name = "a", descriptor = "(B)J")
	override fun getAddress(): Long {
		return super.buffer!!.getAddress()
	}

	@OriginalMember(owner = "client!wv", name = "b", descriptor = "(I)I")
	override fun getIndexType(): Int {
		return this.indexType
	}

	@OriginalMember(owner = "client!wv", name = "a", descriptor = "([BIII)V")
	override fun putElements(@OriginalArg(0) data: ByteArray?, @OriginalArg(3) requiredSize: Int) {
		this.put(data, requiredSize)
		this.indexType = 5123
	}
}
