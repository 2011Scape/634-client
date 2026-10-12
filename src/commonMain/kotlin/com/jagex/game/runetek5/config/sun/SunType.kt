package com.jagex.game.runetek5.config.sun

import com.jagex.game.runetek5.buffer.Packet
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmField

@OriginalClass("client!iea")
class SunType {

	@OriginalMember(owner = "client!iea", name = "b", descriptor = "I")
	@JvmField
	var actionType: Int = 0

	@OriginalMember(owner = "client!iea", name = "c", descriptor = "I")
	@JvmField
	var positionX: Int = 0

	@OriginalMember(owner = "client!iea", name = "f", descriptor = "I")
	@JvmField
	var positionY: Int = 0

	@OriginalMember(owner = "client!iea", name = "h", descriptor = "I")
	@JvmField
	var materialId: Int = 0

	@OriginalMember(owner = "client!iea", name = "k", descriptor = "Z")
	@JvmField
	var invertPosition: Boolean = false

	@OriginalMember(owner = "client!iea", name = "l", descriptor = "I")
	@JvmField
	var positionZ: Int = 0

	@OriginalMember(owner = "client!iea", name = "e", descriptor = "I")
	@JvmField
	var positionOffsetStep: Int = 8

	@OriginalMember(owner = "client!iea", name = "g", descriptor = "I")
	@JvmField
	var displayColor: Int = 16777215

	@OriginalMember(owner = "client!iea", name = "a", descriptor = "(ILclient!cea;I)V")
	private fun decode(@OriginalArg(1) packet: Packet, @OriginalArg(2) opcode: Int) {
		if (opcode == 1) {
			this.positionOffsetStep = packet.readUShort()
		} else if (opcode == 2) {
			this.invertPosition = true
		} else if (opcode == 3) {
			this.positionX = packet.readShort()
			this.positionY = packet.readShort()
			this.positionZ = packet.readShort()
		} else if (opcode == 4) {
			this.actionType = packet.readUByte()
		} else if (opcode == 5) {
			this.materialId = packet.readUShort()
		} else if (opcode == 6) {
			this.displayColor = packet.readUMedium()
		}
	}

	@OriginalMember(owner = "client!iea", name = "a", descriptor = "(Lclient!cea;B)V")
	fun decode(@OriginalArg(0) packet: Packet) {
		while (true) {
			@Pc(13) val opcode: Int = packet.readUByte()
			if (opcode == 0) {
				return
			}
			this.decode(packet, opcode)
		}
	}
}
