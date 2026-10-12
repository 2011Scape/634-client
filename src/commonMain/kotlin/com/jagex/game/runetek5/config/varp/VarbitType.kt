package com.jagex.game.runetek5.config.varp

import com.jagex.game.runetek5.buffer.Packet
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmField

@OriginalClass("client!bia")
class VarbitType {

	@OriginalMember(owner = "client!bia", name = "e", descriptor = "I")
	@JvmField
	var startBit: Int = 0

	@OriginalMember(owner = "client!bia", name = "h", descriptor = "I")
	@JvmField
	var configIndex: Int = 0

	@OriginalMember(owner = "client!bia", name = "i", descriptor = "I")
	@JvmField
	var endBit: Int = 0

	@OriginalMember(owner = "client!bia", name = "a", descriptor = "(ILclient!cea;)V")
	fun decode(@OriginalArg(1) packet: Packet?) {
		while (true) {
			@Pc(13) val opcode: Int = packet!!.readUByte()
			if (opcode == 0) {
				return
			}
			this.decode(opcode, packet)
		}
	}

	@OriginalMember(owner = "client!bia", name = "a", descriptor = "(ILclient!cea;B)V")
	private fun decode(@OriginalArg(0) opcode: Int, @OriginalArg(1) packet: Packet?) {
		if (opcode == 1) {
			this.configIndex = packet!!.readUShort()
			this.startBit = packet.readUByte()
			this.endBit = packet.readUByte()
		}
	}
}
