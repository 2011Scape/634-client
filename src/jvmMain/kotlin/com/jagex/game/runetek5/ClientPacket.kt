package com.jagex.game.runetek5

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!ej")
class ClientPacket @OriginalMember(owner = "client!ej", name = "<init>", descriptor = "(II)V") constructor(@OriginalArg(0) opcode: Int, @OriginalArg(1) size: Int) {

	@OriginalMember(owner = "client!ej", name = "e", descriptor = "I")
	private val opcode: Int = opcode

	@OriginalMember(owner = "client!ej", name = "b", descriptor = "I")
	@JvmField
	val size: Int = size

	@OriginalMember(owner = "client!ej", name = "toString", descriptor = "()Ljava/lang/String;")
	override fun toString(): String {
		throw IllegalStateException()
	}

	@OriginalMember(owner = "client!ej", name = "a", descriptor = "(I)I")
	fun getOpcode(): Int {
		return this.opcode
	}
}
