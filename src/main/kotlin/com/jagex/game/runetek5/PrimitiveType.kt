package com.jagex.game.runetek5

import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!hj")
class PrimitiveType {

	@OriginalMember(owner = "client!hj", name = "toString", descriptor = "()Ljava/lang/String;")
	override fun toString(): String {
		throw IllegalStateException()
	}

	companion object {
		@OriginalMember(owner = "client!tfa", name = "b", descriptor = "Lclient!hj;")
		@JvmField
		val aClass134_6: PrimitiveType = PrimitiveType()

		@OriginalMember(owner = "client!ffa", name = "w", descriptor = "Lclient!hj;")
		@JvmField
		val aClass134_3: PrimitiveType = PrimitiveType()

		@OriginalMember(owner = "client!qm", name = "S", descriptor = "Lclient!hj;")
		@JvmField
		val aClass134_4: PrimitiveType = PrimitiveType()

		@OriginalMember(owner = "client!rda", name = "K", descriptor = "Lclient!hj;")
		@JvmField
		val aClass134_5: PrimitiveType = PrimitiveType()

		@OriginalMember(owner = "client!vd", name = "o", descriptor = "Lclient!hj;")
		@JvmField
		val aClass134_8: PrimitiveType = PrimitiveType()
	}
}
