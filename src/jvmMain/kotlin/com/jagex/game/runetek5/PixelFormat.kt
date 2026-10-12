package com.jagex.game.runetek5

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!wq")
class PixelFormat @OriginalMember(owner = "client!wq", name = "<init>", descriptor = "(I)V") constructor(@OriginalArg(0) componentCount: Int) {

	@OriginalMember(owner = "client!wq", name = "d", descriptor = "I")
	@JvmField
	val componentCount: Int

	init {
		this.componentCount = componentCount
	}

	@OriginalMember(owner = "client!wq", name = "toString", descriptor = "()Ljava/lang/String;")
	override fun toString(): String {
		throw IllegalStateException()
	}

	companion object {
		@OriginalMember(owner = "client!qk", name = "p", descriptor = "Lclient!wq;")
		@JvmField
		val aClass375_39: PixelFormat = PixelFormat(2)

		@OriginalMember(owner = "client!sl", name = "cb", descriptor = "Lclient!wq;")
		@JvmField
		val aClass375_44: PixelFormat = PixelFormat(1)

		@OriginalMember(owner = "client!cm", name = "h", descriptor = "Lclient!wq;")
		@JvmField
		val aClass375_49: PixelFormat = PixelFormat(1)

		@OriginalMember(owner = "client!bu", name = "p", descriptor = "Lclient!wq;")
		@JvmField
		val aClass375_38: PixelFormat = PixelFormat(3)
	}
}
