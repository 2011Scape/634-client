package com.jagex.game.runetek5.vardomain

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!dd")
interface VarDomain {

	@OriginalMember(owner = "client!dd", name = "a", descriptor = "(IB)I")
	fun getVarValueInt(@OriginalArg(0) id: Int): Int

	@OriginalMember(owner = "client!dd", name = "a", descriptor = "(II)I")
	fun getVarBitValue(@OriginalArg(0) id: Int): Int
}
