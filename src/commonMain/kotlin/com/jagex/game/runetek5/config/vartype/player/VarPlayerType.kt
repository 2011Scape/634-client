package com.jagex.game.runetek5.config.vartype.player

import com.jagex.game.runetek5.buffer.Packet
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmField

@OriginalClass("client!ow")
class VarPlayerType {

	@OriginalMember(owner = "client!ow", name = "b", descriptor = "I")
	@JvmField
	var clientCode: Int = 0

	@OriginalMember(owner = "client!ow", name = "a", descriptor = "(Lclient!cea;II)V")
	private fun decode(@OriginalArg(0) packet: Packet?, @OriginalArg(1) opcode: Int) {
		if (opcode == 5) {
			this.clientCode = packet!!.readUShort()
		}
	}

	@OriginalMember(owner = "client!ow", name = "a", descriptor = "(Lclient!cea;Z)V")
	fun decode(@OriginalArg(0) packet: Packet?) {
		while (true) {
			@Pc(5) val opcode: Int = packet!!.readUByte()
			if (opcode == 0) {
				return
			}
			this.decode(packet, opcode)
		}
	}
}
