package com.jagex.game.runetek5

import com.jagex.game.runetek5.client.login.LoginProt
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!qa")
class LoginProtTable {

	companion object {

		@OriginalMember(owner = "client!qa", name = "s", descriptor = "[Lclient!hv;")
		private val OUTGOING_LOGIN_PACKETS: Array<LoginProt?> = arrayOfNulls<LoginProt>(32)

		init {
			@Pc(95) val packets: Array<LoginProt?>? = LoginProt.loginProtValues()
			@Pc(97) var i: Int = 0
			while (i < packets!!.size) {
				OUTGOING_LOGIN_PACKETS[packets!![i]!!.id] = packets!![i]
				i++
			}
		}
	}
}
