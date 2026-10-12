package com.jagex.game.runetek5.core.constants

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!sk")
class ModeWhat @OriginalMember(owner = "client!sk", name = "<init>", descriptor = "(Ljava/lang/String;I)V") constructor(@OriginalArg(0) string: String?, @OriginalArg(1) id: Int) {

	@OriginalMember(owner = "client!sk", name = "f", descriptor = "I")
	@JvmField
	val id: Int

	init {
		this.id = id
	}

	@OriginalMember(owner = "client!sk", name = "toString", descriptor = "()Ljava/lang/String;")
	override fun toString(): String {
		throw IllegalStateException()
	}

	@OriginalMember(owner = "client!sk", name = "a", descriptor = "(I)I")
	fun getId(): Int {
		return this.id
	}

	companion object {

		@OriginalMember(owner = "client!rn", name = "K", descriptor = "Lclient!sk;")
		@JvmField
		val aClass317_3: ModeWhat = ModeWhat("RC", 1)

		@OriginalMember(owner = "client!sr", name = "a", descriptor = "(II)Lclient!sk;")
		@JvmStatic
		fun forId(@OriginalArg(1) id: Int): ModeWhat? {
			@Pc(8) val modeWhats: Array<ModeWhat?> = ModeWhat.values()
			@Pc(15) var i: Int = 0
			while (i < modeWhats.size) {
				@Pc(21) val modeWhat: ModeWhat? = modeWhats[i]
				if (modeWhat!!.id == id) {
					return modeWhat
				}
				i++
			}
			return null
		}

		@OriginalMember(owner = "client!baa", name = "l", descriptor = "Lclient!sk;")
		@JvmField
		val aClass317_1: ModeWhat = ModeWhat("LIVE", 0)

		@OriginalMember(owner = "client!vr", name = "d", descriptor = "Lclient!sk;")
		@JvmField
		val WIP: ModeWhat = ModeWhat("WIP", 2)

		@OriginalMember(owner = "client!su", name = "a", descriptor = "(I)[Lclient!sk;")
		@JvmStatic
		fun values(): Array<ModeWhat?> {
			return arrayOf<ModeWhat?>(ModeWhat.aClass317_1, ModeWhat.aClass317_3, ModeWhat.WIP)
		}
	}
}
