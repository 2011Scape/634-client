package com.jagex.game.runetek5

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

@OriginalClass("client!oaa")
class QuickChatValueType @OriginalMember(owner = "client!oaa", name = "<init>", descriptor = "(IIII)V") constructor(@OriginalArg(0) id: Int, @OriginalArg(1) clientTransmitSize: Int, @OriginalArg(2) serverTransmitSize: Int, @OriginalArg(3) parameterCount: Int) {

	@OriginalMember(owner = "client!oaa", name = "d", descriptor = "I")
	@JvmField
	val serverTransmitSize: Int

	@OriginalMember(owner = "client!oaa", name = "f", descriptor = "I")
	@JvmField
	val clientTransmitSize: Int

	@OriginalMember(owner = "client!oaa", name = "b", descriptor = "I")
	@JvmField
	val id: Int

	@OriginalMember(owner = "client!oaa", name = "e", descriptor = "I")
	@JvmField
	val parameterCount: Int

	init {
		this.serverTransmitSize = serverTransmitSize
		this.clientTransmitSize = clientTransmitSize
		this.id = id
		this.parameterCount = parameterCount
	}

	@OriginalMember(owner = "client!oaa", name = "toString", descriptor = "()Ljava/lang/String;")
	override fun toString(): String {
		throw IllegalStateException()
	}

	companion object {
		@OriginalMember(owner = "client!jca", name = "h", descriptor = "Lclient!oaa;")
		@JvmField
		val aClass234_13: QuickChatValueType = QuickChatValueType(12, 0, 1, 0)

		@OriginalMember(owner = "client!kga", name = "f", descriptor = "Lclient!oaa;")
		@JvmField
		val aClass234_14: QuickChatValueType = QuickChatValueType(11, 0, 1, 2)

		@OriginalMember(owner = "client!cu", name = "v", descriptor = "Lclient!oaa;")
		@JvmField
		val aClass234_5: QuickChatValueType = QuickChatValueType(14, 0, 4, 1)

		@OriginalMember(owner = "client!za", name = "o", descriptor = "Lclient!oaa;")
		@JvmField
		val aClass234_26: QuickChatValueType = QuickChatValueType(10, 2, 2, 0)

		@OriginalMember(owner = "client!wha", name = "s", descriptor = "Lclient!oaa;")
		@JvmField
		val aClass234_25: QuickChatValueType = QuickChatValueType(9, 0, 4, 1)

		@OriginalMember(owner = "client!so", name = "i", descriptor = "Lclient!oaa;")
		@JvmField
		val aClass234_23: QuickChatValueType = QuickChatValueType(0, 2, 2, 1)

		@OriginalMember(owner = "client!da", name = "r", descriptor = "Lclient!oaa;")
		@JvmField
		val aClass234_24: QuickChatValueType = QuickChatValueType(2, 4, 4, 0)

		@OriginalMember(owner = "client!sda", name = "c", descriptor = "Lclient!oaa;")
		@JvmField
		val PACKET_28: QuickChatValueType = QuickChatValueType(4, 1, 1, 1)

		@OriginalMember(owner = "client!gh", name = "Qb", descriptor = "Lclient!oaa;")
		@JvmField
		val aClass234_11: QuickChatValueType = QuickChatValueType(7, 0, 1, 1)

		@OriginalMember(owner = "client!dp", name = "e", descriptor = "Lclient!oaa;")
		@JvmField
		val aClass234_20: QuickChatValueType = QuickChatValueType(1, 2, 2, 0)

		@OriginalMember(owner = "client!fba", name = "c", descriptor = "(II)Lclient!oaa;")
		@JvmStatic
		fun forId(@OriginalArg(0) id: Int): QuickChatValueType? {
			@Pc(8) val types: Array<QuickChatValueType?> = QuickChatValueType.values()
			@Pc(10) var i: Int = 0
			while (i < types.size) {
				if (id == types[i]!!.id) {
					return types[i]
				}
				i++
			}
			return null
		}

		@OriginalMember(owner = "client!bga", name = "Jh", descriptor = "Lclient!oaa;")
		@JvmField
		val aClass234_3: QuickChatValueType = QuickChatValueType(15, 0, 1, 0)

		@OriginalMember(owner = "client!lr", name = "G", descriptor = "Lclient!oaa;")
		@JvmField
		val aClass234_16: QuickChatValueType = QuickChatValueType(6, 0, 4, 2)

		@OriginalMember(owner = "client!mq", name = "Z", descriptor = "Lclient!oaa;")
		@JvmField
		var aClass234_18: QuickChatValueType? = QuickChatValueType(13, 0, 1, 0)

		@OriginalMember(owner = "client!mk", name = "a", descriptor = "(B)[Lclient!oaa;")
		@JvmStatic
		fun values(): Array<QuickChatValueType?> {
			return arrayOf<QuickChatValueType?>(QuickChatValueType.aClass234_23, QuickChatValueType.aClass234_20, QuickChatValueType.aClass234_24, QuickChatValueType.PACKET_28, QuickChatValueType.aClass234_16, QuickChatValueType.aClass234_11, QuickChatValueType.aClass234_7, QuickChatValueType.aClass234_25, QuickChatValueType.aClass234_26, QuickChatValueType.aClass234_14, QuickChatValueType.aClass234_13, QuickChatValueType.aClass234_18, QuickChatValueType.aClass234_5, QuickChatValueType.aClass234_3)
		}

		@OriginalMember(owner = "client!daa", name = "q", descriptor = "Lclient!oaa;")
		@JvmField
		val aClass234_7: QuickChatValueType = QuickChatValueType(8, 0, 4, 1)
	}
}
