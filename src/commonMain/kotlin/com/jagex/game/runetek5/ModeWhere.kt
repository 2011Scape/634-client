package com.jagex.game.runetek5

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

@OriginalClass("client!fh")
class ModeWhere {

	@OriginalMember(owner = "client!fh", name = "a", descriptor = "I")
	@JvmField
	val id: Int

	@OriginalMember(owner = "client!fh", name = "<init>", descriptor = "(Ljava/lang/String;I)V")
	constructor(@OriginalArg(0) string: String?, @OriginalArg(1) id: Int) {
		this.id = id
	}

	@OriginalMember(owner = "client!fh", name = "toString", descriptor = "()Ljava/lang/String;")
	override fun toString(): String {
		throw IllegalStateException()
	}

	companion object {
		@OriginalMember(owner = "client!mo", name = "b", descriptor = "Lclient!fh;")
		@JvmField
		val aClass107_6: ModeWhere = ModeWhere("WTWIP", 3)
		@OriginalMember(owner = "client!eca", name = "i", descriptor = "Lclient!fh;")
		@JvmField
		val GAME: ModeWhere = ModeWhere("WTI", 5)

		@OriginalMember(owner = "client!hs", name = "j", descriptor = "Lclient!fh;")
		@JvmField
		val MODE_WHERE_WTQA: ModeWhere = ModeWhere("WTQA", 2)

		@OriginalMember(owner = "client!gia", name = "a", descriptor = "(BI)Lclient!fh;")
		@JvmStatic
		fun forId(@OriginalArg(1) id: Int): ModeWhere? {
			@Pc(13) val modeWheres: Array<ModeWhere?> = ModeWhere.values()
			@Pc(15) var i: Int = 0
			while (i < modeWheres.size) {
				@Pc(21) val modeWhere: ModeWhere? = modeWheres[i]
				if (modeWhere!!.id == id) {
					return modeWhere
				}
				i++
			}
			return null
		}
		@OriginalMember(owner = "client!v", name = "b", descriptor = "(B)[Lclient!fh;")
		@JvmStatic
		fun values(): Array<ModeWhere?> {
			return arrayOf(ModeWhere.WTRC, ModeWhere.aClass107_4, ModeWhere.MODE_WHERE_WTQA, ModeWhere.aClass107_6, ModeWhere.aClass107_5, ModeWhere.GAME)
		}
		@OriginalMember(owner = "client!qc", name = "O", descriptor = "Lclient!fh;")
		@JvmField
		val WTRC: ModeWhere = ModeWhere("LIVE", 0)

		@OriginalMember(owner = "client!ks", name = "b", descriptor = "Lclient!fh;")
		@JvmField
		val aClass107_5: ModeWhere = ModeWhere("LOCAL", 4)

		@OriginalMember(owner = "client!kl", name = "u", descriptor = "Lclient!fh;")
		@JvmField
		val aClass107_4: ModeWhere = ModeWhere("WTRC", 1)
		@OriginalMember(owner = "client!cia", name = "a", descriptor = "(Lclient!fh;I)Z")
		@JvmStatic
		fun isHostedTestEnvironment(@OriginalArg(0) modeWhere: ModeWhere?): Boolean {
			return ModeWhere.aClass107_4 === modeWhere || ModeWhere.MODE_WHERE_WTQA === modeWhere || ModeWhere.aClass107_6 === modeWhere || modeWhere === ModeWhere.GAME
		}
	}
}
