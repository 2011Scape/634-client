package com.jagex.game.runetek5.config.rsinterface.cs2

import com.jagex.game.runetek5.ClientPacket
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.config.objtype.ObjTypeList
import org.openrs2.deob.annotation.OriginalArg

object CS2Executor {
	@OriginalMember(owner = "client!lk", name = "D", descriptor = "[I")
	@JvmField
	val projectionBuffer: IntArray = IntArray(3)

	@OriginalMember(owner = "client!aba", name = "C", descriptor = "Lclient!ej;")
	@JvmField
	val FRIENDCHAT_JOIN_LEAVE: ClientPacket = ClientPacket(50, -1)

	@OriginalMember(owner = "client!aj", name = "a", descriptor = "(ZLjava/lang/String;Ljava/lang/String;II)V")
	@JvmStatic
	fun queryItemParamString(@OriginalArg(0) stockMarketOnly: Boolean, @OriginalArg(1) string: String?, @OriginalArg(2) string2: String?, @OriginalArg(3) paramId: Int) {
		ObjTypeList.queryItem(true, string2, -1, paramId, stockMarketOnly, string)
	}
}
