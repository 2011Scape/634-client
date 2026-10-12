package com.jagex.game.runetek5.buffer

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!ni")
interface ElementsBufferGL {

	@OriginalMember(owner = "client!ni", name = "a", descriptor = "(I)I")
	fun getId(): Int

	@OriginalMember(owner = "client!ni", name = "b", descriptor = "(I)I")
	fun getIndexType(): Int

	@OriginalMember(owner = "client!ni", name = "a", descriptor = "([BIII)V")
	fun putElements(@OriginalArg(0) data: ByteArray?, @OriginalArg(3) size: Int)

	@OriginalMember(owner = "client!ni", name = "a", descriptor = "(B)J")
	fun getAddress(): Long
}
