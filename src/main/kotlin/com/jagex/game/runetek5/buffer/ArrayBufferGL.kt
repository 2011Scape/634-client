package com.jagex.game.runetek5.buffer

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!hca")
interface ArrayBufferGL {

	@OriginalMember(owner = "client!hca", name = "a", descriptor = "(Z)I")
	fun getId(): Int

	@OriginalMember(owner = "client!hca", name = "a", descriptor = "(II[BI)V")
	fun putArray(@OriginalArg(0) stride: Int, @OriginalArg(1) size: Int, @OriginalArg(2) data: ByteArray?)

	@OriginalMember(owner = "client!hca", name = "a", descriptor = "(B)J")
	fun getAddress(): Long

	@OriginalMember(owner = "client!hca", name = "b", descriptor = "(B)I")
	fun getStride(): Int
}
