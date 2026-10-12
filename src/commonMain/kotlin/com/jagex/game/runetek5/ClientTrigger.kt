package com.jagex.game.runetek5

import com.jagex.game.runetek5.trigger.TriggerType
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField

@OriginalClass("client!uw")
class ClientTrigger @OriginalMember(owner = "client!uw", name = "<init>", descriptor = "(Ljava/lang/String;I)V") constructor(@OriginalArg(0) string: String?, @OriginalArg(1) id: Int) : TriggerType {

	@OriginalMember(owner = "client!uw", name = "c", descriptor = "I")
	@JvmField
	val id: Int = id

	@OriginalMember(owner = "client!uw", name = "toString", descriptor = "()Ljava/lang/String;")
	override fun toString(): String {
		throw IllegalStateException()
	}

	companion object {
		@OriginalMember(owner = "client!de", name = "b", descriptor = "Lclient!uw;")
		@JvmField
		val aClass350_3: ClientTrigger = ClientTrigger("", 10)
		@OriginalMember(owner = "client!lj", name = "s", descriptor = "Lclient!uw;")
		@JvmField
		val aClass350_6: ClientTrigger = ClientTrigger("", 11)
		@OriginalMember(owner = "client!ti", name = "o", descriptor = "Lclient!uw;")
		@JvmField
		val aClass350_9: ClientTrigger = ClientTrigger("", 14)

		@OriginalMember(owner = "client!oda", name = "d", descriptor = "Lclient!uw;")
		@JvmField
		val aClass350_7: ClientTrigger = ClientTrigger("", 18)
		@OriginalMember(owner = "client!ar", name = "x", descriptor = "Lclient!uw;")
		@JvmField
		val aClass350_1: ClientTrigger = ClientTrigger("", 13)

		@OriginalMember(owner = "client!wi", name = "J", descriptor = "Lclient!uw;")
		@JvmField
		val aClass350_10: ClientTrigger = ClientTrigger("", 12)
	}
}
