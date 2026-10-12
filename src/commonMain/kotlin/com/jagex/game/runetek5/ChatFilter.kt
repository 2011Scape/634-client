package com.jagex.game.runetek5

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

@OriginalClass("client!sh")
class ChatFilter @OriginalMember(owner = "client!sh", name = "<init>", descriptor = "(I)V") constructor(@OriginalArg(0) id: Int) {

	@OriginalMember(owner = "client!sh", name = "e", descriptor = "I")
	@JvmField
	val id: Int = id

	@OriginalMember(owner = "client!sh", name = "toString", descriptor = "()Ljava/lang/String;")
	override fun toString(): String {
		throw IllegalStateException()
	}

	companion object {
		@OriginalMember(owner = "client!mea", name = "o", descriptor = "Lclient!sh;")
		@JvmField
		val FRIENDS_ONLY: ChatFilter = ChatFilter(1)

		@OriginalMember(owner = "client!iea", name = "a", descriptor = "(BI)Lclient!sh;")
		@JvmStatic
		fun chatFilterForId(@OriginalArg(1) id: Int): ChatFilter? {
			@Pc(15) val filters: Array<ChatFilter?> = ChatFilter.values()
			for (@Pc(17) i in 0 until filters.size) {
				@Pc(23) val filter: ChatFilter? = filters[i]
				if (id == filter!!.id) {
					return filter
				}
			}
			return null
		}

		@OriginalMember(owner = "client!lh", name = "b", descriptor = "(I)[Lclient!sh;")
		@JvmStatic
		fun values(): Array<ChatFilter?> {
			return arrayOf(ChatFilter.aClass313_4, ChatFilter.FRIENDS_ONLY, ChatFilter.aClass313_2)
		}

		@OriginalMember(owner = "client!qk", name = "m", descriptor = "Lclient!sh;")
		@JvmField
		val aClass313_4: ChatFilter = ChatFilter(0)

		@OriginalMember(owner = "client!go", name = "i", descriptor = "Lclient!sh;")
		@JvmField
		val aClass313_2: ChatFilter = ChatFilter(2)
	}
}
