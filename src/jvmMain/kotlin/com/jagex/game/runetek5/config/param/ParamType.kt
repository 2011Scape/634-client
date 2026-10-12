package com.jagex.game.runetek5.config.param

import com.jagex.game.runetek5.buffer.Packet
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.utils.Utils

@OriginalClass("client!uaa")
class ParamType {

	@OriginalMember(owner = "client!uaa", name = "a", descriptor = "I")
	@JvmField
	var defaultInt: Int = 0

	@OriginalMember(owner = "client!uaa", name = "c", descriptor = "Ljava/lang/String;")
	@JvmField
	var typeName: String? = null

	@OriginalMember(owner = "client!uaa", name = "d", descriptor = "C")
	private var typeChar: Char = '\u0000'

	@OriginalMember(owner = "client!uaa", name = "f", descriptor = "Z")
	@JvmField
	var autoDisable: Boolean = true

	@OriginalMember(owner = "client!uaa", name = "a", descriptor = "(Lclient!cea;B)V")
	fun decode(@OriginalArg(0) packet: Packet?) {
		while (true) {
			@Pc(13) val opcode: Int = packet!!.readUByte()
			if (opcode == 0) {
				return
			}
			this.decode(packet, opcode)
		}
	}

	@OriginalMember(owner = "client!uaa", name = "a", descriptor = "(Z)Z")
	fun isString(): Boolean {
		return this.typeChar == 's'
	}

	@OriginalMember(owner = "client!uaa", name = "a", descriptor = "(ZLclient!cea;I)V")
	private fun decode(@OriginalArg(1) packet: Packet?, @OriginalArg(2) opcode: Int) {
		if (opcode == 1) {
			this.typeChar = Utils.cp1252ToChar(packet!!.readByte())
		} else if (opcode == 2) {
			this.defaultInt = packet!!.readInt()
		} else if (opcode == 4) {
			this.autoDisable = false
		} else if (opcode == 5) {
			this.typeName = packet!!.readString()
		}
	}
}
