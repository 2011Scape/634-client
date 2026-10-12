package com.jagex.game.runetek5.client.login

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

@OriginalClass("client!hv")
class LoginProt @OriginalMember(owner = "client!hv", name = "<init>", descriptor = "(II)V") constructor(@OriginalArg(0) id: Int, @OriginalArg(1) size: Int) {

	@OriginalMember(owner = "client!hv", name = "a", descriptor = "I")
	@JvmField
	val id: Int

	init {
		this.id = id
	}

	@OriginalMember(owner = "client!hv", name = "toString", descriptor = "()Ljava/lang/String;")
	override fun toString(): String {
		throw IllegalStateException()
	}

	companion object {
		@OriginalMember(owner = "client!qa", name = "j", descriptor = "Lclient!hv;")
		@JvmField
		val aClass141_6: LoginProt = LoginProt(19, -2)

		@OriginalMember(owner = "client!qa", name = "m", descriptor = "Lclient!hv;")
		@JvmField
		val aClass141_9: LoginProt = LoginProt(22, -2)

		@OriginalMember(owner = "client!qa", name = "h", descriptor = "Lclient!hv;")
		@JvmField
		val aClass141_4: LoginProt = LoginProt(17, 0)

		@OriginalMember(owner = "client!qa", name = "l", descriptor = "Lclient!hv;")
		@JvmField
		val aClass141_8: LoginProt = LoginProt(21, 9)

		@OriginalMember(owner = "client!qa", name = "k", descriptor = "Lclient!hv;")
		@JvmField
		val aClass141_7: LoginProt = LoginProt(20, 6)

		@OriginalMember(owner = "client!qa", name = "d", descriptor = "Lclient!hv;")
		@JvmField
		val aClass141_1: LoginProt = LoginProt(14, 0)

		@OriginalMember(owner = "client!qa", name = "r", descriptor = "Lclient!hv;")
		@JvmField
		val aClass141_14: LoginProt = LoginProt(28, -2)

		@OriginalMember(owner = "client!qa", name = "g", descriptor = "Lclient!hv;")
		@JvmField
		val aClass141_3: LoginProt = LoginProt(16, -2)

		@OriginalMember(owner = "client!qa", name = "f", descriptor = "Lclient!hv;")
		@JvmField
		val aClass141_2: LoginProt = LoginProt(15, 4)

		@OriginalMember(owner = "client!qa", name = "p", descriptor = "Lclient!hv;")
		@JvmField
		val aClass141_12: LoginProt = LoginProt(26, 0)

		@OriginalMember(owner = "client!qa", name = "q", descriptor = "Lclient!hv;")
		@JvmField
		val aClass141_13: LoginProt = LoginProt(27, 0)

		@OriginalMember(owner = "client!qa", name = "o", descriptor = "Lclient!hv;")
		@JvmField
		val aClass141_11: LoginProt = LoginProt(24, -1)

		@OriginalMember(owner = "client!qa", name = "i", descriptor = "Lclient!hv;")
		@JvmField
		val aClass141_5: LoginProt = LoginProt(18, -2)

		@OriginalMember(owner = "client!qa", name = "n", descriptor = "Lclient!hv;")
		@JvmField
		val aClass141_10: LoginProt = LoginProt(23, 4)

		@OriginalMember(owner = "client!ce", name = "b", descriptor = "(B)[Lclient!hv;")
		@JvmStatic
		fun loginProtValues(): Array<LoginProt?>? {
			return arrayOf<LoginProt?>(LoginProt.aClass141_1, LoginProt.aClass141_2, LoginProt.aClass141_3, LoginProt.aClass141_4, LoginProt.aClass141_5, LoginProt.aClass141_6, LoginProt.aClass141_7, LoginProt.aClass141_8, LoginProt.aClass141_9, LoginProt.aClass141_10, LoginProt.aClass141_11, LoginProt.aClass141_12, LoginProt.aClass141_13, LoginProt.aClass141_14)
		}
	}
}
