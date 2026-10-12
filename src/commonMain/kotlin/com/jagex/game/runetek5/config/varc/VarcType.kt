package com.jagex.game.runetek5.config.varc

import com.jagex.game.runetek5.buffer.Packet
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.utils.Utils
import kotlin.jvm.JvmField

@OriginalClass("client!pa")
class VarcType {

	@OriginalMember(owner = "client!pa", name = "d", descriptor = "C")
	@JvmField
	var typeChar: Char = '\u0000'

	@OriginalMember(owner = "client!pa", name = "g", descriptor = "I")
	@JvmField
	var persistenceType: Int = 1

	@OriginalMember(owner = "client!pa", name = "a", descriptor = "(Lclient!cea;I)V")
	fun decode(@OriginalArg(0) packet: Packet?) {
		while (true) {
			@Pc(5) val opcode: Int = packet!!.readUByte()
			if (opcode == 0) {
				return
			}
			this.decode(opcode, packet)
		}
	}

	@OriginalMember(owner = "client!pa", name = "a", descriptor = "(IILclient!cea;)V")
	private fun decode(@OriginalArg(1) opcode: Int, @OriginalArg(2) packet: Packet?) {
		if (opcode == 1) {
			this.typeChar = Utils.cp1252ToChar(packet!!.readByte())
		} else if (opcode == 2) {
			this.persistenceType = 0
		}
	}
}
