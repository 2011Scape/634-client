package com.jagex.game.runetek5.network.packet

import com.jagex.game.runetek5.cache.node.Node
import com.jagex.game.runetek5.buffer.BitPacket
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.ClientPacket
import com.jagex.game.runetek5.WorldConnectionInfo
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.tools.Isaac
import org.openrs2.deob.annotation.OriginalArg
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

@OriginalClass("client!wm")
class TcpPacket : Node() {

	@OriginalMember(owner = "client!wm", name = "o", descriptor = "Lclient!ua;")
	@JvmField
	var buffer: BitPacket? = null

	@OriginalMember(owner = "client!wm", name = "q", descriptor = "Lclient!ej;")
	@JvmField
	var packet: ClientPacket? = null

	@OriginalMember(owner = "client!wm", name = "r", descriptor = "I")
	@JvmField
	var capacity: Int = 0

	@OriginalMember(owner = "client!wm", name = "u", descriptor = "I")
	@JvmField
	var position: Int = 0

	@OriginalMember(owner = "client!wm", name = "a", descriptor = "(B)V")
	fun push() {
		if (TcpPacket.OUTGOING_PACKETS.size > TcpPacket.index) {
			TcpPacket.OUTGOING_PACKETS[TcpPacket.index++] = this
		}
	}

	companion object {
		@OriginalMember(owner = "client!gi", name = "c", descriptor = "Lclient!pc;")
		@JvmField
		var CURRENT_REQUEST: WorldConnectionInfo? = null
		@OriginalMember(owner = "client!ph", name = "u", descriptor = "I")
		@JvmField
		var index: Int = 0
		@OriginalMember(owner = "client!pt", name = "d", descriptor = "[Lclient!wm;")
		@JvmField
		val OUTGOING_PACKETS: Array<TcpPacket?> = arrayOfNulls<TcpPacket>(300)
		@OriginalMember(owner = "client!wia", name = "a", descriptor = "(B)Lclient!wm;")
		@JvmStatic
		fun createPacket(): TcpPacket {
			return if (TcpPacket.index == 0) TcpPacket() else TcpPacket.OUTGOING_PACKETS[--TcpPacket.index]!!
		}
		@OriginalMember(owner = "client!rea", name = "a", descriptor = "(Z)Lclient!wm;")
		@JvmStatic
		fun createLoginPacket(): TcpPacket {
			@Pc(6) val packet: TcpPacket = TcpPacket.createPacket()
			packet.position = 0
			packet.packet = null
			packet.buffer = BitPacket(5000)
			return packet
		}
		@OriginalMember(owner = "client!naa", name = "T", descriptor = "Lclient!ku;")
		@JvmField
		var isaac: Isaac? = null

		@OriginalMember(owner = "client!em", name = "a", descriptor = "(Lclient!ej;Lclient!ku;I)Lclient!wm;")
		@JvmStatic
		fun createPacket(@OriginalArg(0) packet: ClientPacket?, @OriginalArg(1) isaac: Isaac?): TcpPacket {
			@Pc(8) val tcpPacket: TcpPacket = TcpPacket.createPacket()
			tcpPacket.position = packet!!.size
			tcpPacket.packet = packet
			if (tcpPacket.position == -1) {
				tcpPacket.buffer = BitPacket(260)
			} else if (tcpPacket.position == -2) {
				tcpPacket.buffer = BitPacket(10000)
			} else if (tcpPacket.position <= 18) {
				tcpPacket.buffer = BitPacket(20)
			} else if (tcpPacket.position <= 98) {
				tcpPacket.buffer = BitPacket(100)
			} else {
				tcpPacket.buffer = BitPacket(260)
			}
			tcpPacket.buffer!!.setRandom(isaac)
			tcpPacket.buffer!!.startPacket(tcpPacket.packet!!.getOpcode())
			tcpPacket.capacity = 0
			return tcpPacket
		}
		@OriginalMember(owner = "client!gf", name = "a", descriptor = "(IIILclient!wm;I)V")
		@JvmStatic
		fun writeInterfaceOptionPayload(@OriginalArg(0) idHash: Int, @OriginalArg(1) slotId: Int, @OriginalArg(2) itemId: Int, @OriginalArg(3) packet: TcpPacket?) {
			packet!!.buffer!!.writeInt(idHash)
			packet.buffer!!.writeShort(itemId)
			packet.buffer!!.writeShort(slotId)
		}
	}
}
