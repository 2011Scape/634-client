package com.jagex.game.runetek5.cache.node.lru

import com.jagex.game.runetek5.buffer.Packet
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmField

@OriginalClass("client!pv")
class InvType : CacheableNode() {

	@OriginalMember(owner = "client!pv", name = "y", descriptor = "I")
	@JvmField
	var size: Int = 0

	@OriginalMember(owner = "client!pv", name = "a", descriptor = "(ILclient!cea;B)V")
	private fun decode(@OriginalArg(0) opcode: Int, @OriginalArg(1) packet: Packet?) {
		if (opcode == 2) {
			this.size = packet!!.readUShort()
		}
	}

	@OriginalMember(owner = "client!pv", name = "a", descriptor = "(BLclient!cea;)V")
	fun decode(@OriginalArg(1) packet: Packet?) {
		while (true) {
			@Pc(7) val opcode: Int = packet!!.readUByte()
			if (opcode == 0) {
				return
			}
			this.decode(opcode, packet)
		}
	}
}
