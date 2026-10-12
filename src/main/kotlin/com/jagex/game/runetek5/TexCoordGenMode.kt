package com.jagex.game.runetek5

import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!bf")
class TexCoordGenMode {

	@OriginalMember(owner = "client!bf", name = "toString", descriptor = "()Ljava/lang/String;")
	override fun toString(): String {
		throw IllegalStateException()
	}

	companion object {
		@OriginalMember(owner = "client!oha", name = "a", descriptor = "Lclient!bf;")
		@JvmField
		val aClass31_3: TexCoordGenMode = TexCoordGenMode()

		@OriginalMember(owner = "client!ai", name = "u", descriptor = "Lclient!bf;")
		@JvmField
		val aClass31_1: TexCoordGenMode = TexCoordGenMode()
		@OriginalMember(owner = "client!ia", name = "k", descriptor = "Lclient!bf;")
		@JvmField
		val aClass31_2: TexCoordGenMode = TexCoordGenMode()

		@OriginalMember(owner = "client!uga", name = "b", descriptor = "Lclient!bf;")
		@JvmField
		val aClass31_4: TexCoordGenMode = TexCoordGenMode()
	}
}
