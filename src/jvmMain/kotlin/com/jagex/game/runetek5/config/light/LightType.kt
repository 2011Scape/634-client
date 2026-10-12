package com.jagex.game.runetek5.config.light

import com.jagex.game.runetek5.buffer.Packet
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!u")
class LightType {

	@OriginalMember(owner = "client!u", name = "b", descriptor = "I")
	@JvmField
	var effect: Int = 0

	@OriginalMember(owner = "client!u", name = "a", descriptor = "I")
	@JvmField
	var ticker: Int = 2048

	@OriginalMember(owner = "client!u", name = "f", descriptor = "I")
	@JvmField
	var duration: Int = 2048

	@OriginalMember(owner = "client!u", name = "h", descriptor = "I")
	@JvmField
	var surrounding: Int = 0

	@OriginalMember(owner = "client!u", name = "a", descriptor = "(BLclient!cea;)V")
	fun decode(@OriginalArg(1) packet: Packet?) {
		while (true) {
			@Pc(17) val opcode: Int = packet!!.readUByte()
			if (opcode == 0) {
				return
			}
			this.decode(opcode, packet)
		}
	}

	@OriginalMember(owner = "client!u", name = "a", descriptor = "(IILclient!cea;)V")
	private fun decode(@OriginalArg(0) opcode: Int, @OriginalArg(2) packet: Packet?) {
		if (opcode == 1) {
			this.effect = packet!!.readUByte()
		} else if (opcode == 2) {
			this.duration = packet!!.readUShort()
		} else if (opcode == 3) {
			this.ticker = packet!!.readUShort()
			return
		} else if (opcode == 4) {
			this.surrounding = packet!!.readShort()
			return
		}
	}
}
