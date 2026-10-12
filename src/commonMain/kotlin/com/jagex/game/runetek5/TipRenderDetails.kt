package com.jagex.game.runetek5

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

@OriginalClass("client!ad")
class TipRenderDetails @OriginalMember(owner = "client!ad", name = "<init>", descriptor = "(II)V") constructor(@OriginalArg(0) id: Int, @OriginalArg(1) version: Int) {

	@OriginalMember(owner = "client!ad", name = "d", descriptor = "I")
	@JvmField
	val version: Int

	init {
		this.version = version
	}

	@OriginalMember(owner = "client!ad", name = "toString", descriptor = "()Ljava/lang/String;")
	override fun toString(): String {
		throw IllegalStateException()
	}

	companion object {
		@OriginalMember(owner = "client!hu", name = "b", descriptor = "Lclient!ad;")
		@JvmField
		val aClass6_5: TipRenderDetails = TipRenderDetails(6, 1)

		@OriginalMember(owner = "client!gq", name = "P", descriptor = "Lclient!ad;")
		@JvmField
		val aClass6_4: TipRenderDetails = TipRenderDetails(4, 1)

		@OriginalMember(owner = "client!hi", name = "a", descriptor = "(I)[Lclient!ad;")
		@JvmStatic
		fun values(): Array<TipRenderDetails?> {
			return arrayOf(TipRenderDetails.aClass6_11, TipRenderDetails.aClass6_12, TipRenderDetails.aClass6_2, TipRenderDetails.OPPLAYER5, TipRenderDetails.aClass6_4, TipRenderDetails.aClass6_10, TipRenderDetails.aClass6_5, TipRenderDetails.aClass6_7, TipRenderDetails.aClass6_6, TipRenderDetails.aClass6_9)
		}
		@OriginalMember(owner = "client!ch", name = "b", descriptor = "Lclient!ad;")
		@JvmField
		val aClass6_2: TipRenderDetails = TipRenderDetails(2, 2)

		@OriginalMember(owner = "client!oh", name = "b", descriptor = "Lclient!ad;")
		@JvmField
		val aClass6_6: TipRenderDetails = TipRenderDetails(8, 1)
		@OriginalMember(owner = "client!qf", name = "k", descriptor = "Lclient!ad;")
		@JvmField
		val OPPLAYER5: TipRenderDetails = TipRenderDetails(3, 2)

		@OriginalMember(owner = "client!rp", name = "Q", descriptor = "Lclient!ad;")
		@JvmField
		val aClass6_10: TipRenderDetails = TipRenderDetails(5, 1)

		@OriginalMember(owner = "client!sb", name = "g", descriptor = "Lclient!ad;")
		@JvmField
		val aClass6_11: TipRenderDetails = TipRenderDetails(0, 1)

		@OriginalMember(owner = "client!cea", name = "ab", descriptor = "Lclient!ad;")
		@JvmField
		val aClass6_12: TipRenderDetails = TipRenderDetails(1, 2)

		@OriginalMember(owner = "client!qga", name = "j", descriptor = "Lclient!ad;")
		@JvmField
		val aClass6_9: TipRenderDetails = TipRenderDetails(9, 2)
		@OriginalMember(owner = "client!po", name = "u", descriptor = "Lclient!ad;")
		@JvmField
		val aClass6_7: TipRenderDetails = TipRenderDetails(7, 2)
	}
}
