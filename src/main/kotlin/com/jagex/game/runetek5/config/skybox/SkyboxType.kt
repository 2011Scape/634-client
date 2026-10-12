package com.jagex.game.runetek5.config.skybox

import com.jagex.game.runetek5.buffer.Packet
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!wia")
class SkyboxType {

	@OriginalMember(owner = "client!wia", name = "g", descriptor = "[I")
	@JvmField
	var sunDefinitionIds: IntArray? = null

	@OriginalMember(owner = "client!wia", name = "i", descriptor = "I")
	@JvmField
	var defaultSunIndex: Int = -1

	@OriginalMember(owner = "client!wia", name = "c", descriptor = "I")
	@JvmField
	var materialId: Int = -1

	@OriginalMember(owner = "client!wia", name = "a", descriptor = "(Lclient!cea;B)V")
	fun decode(@OriginalArg(0) packet: Packet?) {
		while (true) {
			@Pc(9) val opcode: Int = packet!!.readUByte()
			if (opcode == 0) {
				return
			}
			this.decode(packet, opcode)
		}
	}

	@OriginalMember(owner = "client!wia", name = "a", descriptor = "(ILclient!cea;I)V")
	private fun decode(@OriginalArg(1) packet: Packet?, @OriginalArg(2) opcode: Int) {
		if (opcode == 1) {
			this.materialId = packet!!.readUShort()
		} else if (opcode == 2) {
			this.sunDefinitionIds = IntArray(packet!!.readUByte())
			@Pc(34) var i: Int = 0
			while (i < this.sunDefinitionIds!!.size) {
				this.sunDefinitionIds!![i] = packet.readUShort()
				i++
			}
		} else if (opcode == 3) {
			this.defaultSunIndex = packet!!.readUByte()
		}
	}
}
