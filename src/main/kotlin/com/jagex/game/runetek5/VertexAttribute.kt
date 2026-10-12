package com.jagex.game.runetek5

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!daa")
class VertexAttribute @OriginalMember(owner = "client!daa", name = "<init>", descriptor = "(IILclient!baa;)V") constructor(@OriginalArg(0) slot: Int, @OriginalArg(1) componentCount: Int, @OriginalArg(2) actionFormat: DataType?) {

	@OriginalMember(owner = "client!daa", name = "g", descriptor = "I")
	private val componentCount: Int

	@OriginalMember(owner = "client!daa", name = "f", descriptor = "Lclient!baa;")
	private val actionFormat: DataType?

	@OriginalMember(owner = "client!daa", name = "e", descriptor = "I")
	@JvmField
	val slot: Int

	@OriginalMember(owner = "client!daa", name = "d", descriptor = "I")
	@JvmField
	val pointerOffsetStep: Int

	init {
		this.componentCount = componentCount
		this.actionFormat = actionFormat
		this.slot = slot
		this.pointerOffsetStep = this.actionFormat!!.bytesPerEntry * this.componentCount
		if (this.slot >= 16) {
			throw RuntimeException()
		}
	}

	@OriginalMember(owner = "client!daa", name = "toString", descriptor = "()Ljava/lang/String;")
	override fun toString(): String {
		throw IllegalStateException()
	}
}
