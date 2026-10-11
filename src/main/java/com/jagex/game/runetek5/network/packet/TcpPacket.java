package com.jagex.game.runetek5.network.packet;

import com.jagex.game.runetek5.cache.node.Node;
import com.jagex.game.runetek5.buffer.BitPacket;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.ClientPacket;
import com.jagex.game.runetek5.EntityHoverMenuOptions;
import com.jagex.game.runetek5.WorldConnectionInfo;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.tools.Isaac;
import org.openrs2.deob.annotation.OriginalArg;

@OriginalClass("client!wm")
public final class TcpPacket extends Node {

	@OriginalMember(owner = "client!wm", name = "o", descriptor = "Lclient!ua;")
	public BitPacket buffer;

	@OriginalMember(owner = "client!wm", name = "q", descriptor = "Lclient!ej;")
	public ClientPacket packet;

	@OriginalMember(owner = "client!wm", name = "r", descriptor = "I")
	public int capacity;

	@OriginalMember(owner = "client!wm", name = "u", descriptor = "I")
	public int position;

	@OriginalMember(owner = "client!wm", name = "a", descriptor = "(B)V")
	public void push() {
		if (TcpPacket.OUTGOING_PACKETS.length > TcpPacket.index) {
			TcpPacket.OUTGOING_PACKETS[TcpPacket.index++] = this;
		}
	}
	@OriginalMember(owner = "client!gi", name = "c", descriptor = "Lclient!pc;")
	public static WorldConnectionInfo CURRENT_REQUEST;
	@OriginalMember(owner = "client!ph", name = "u", descriptor = "I")
	public static int index = 0;
	@OriginalMember(owner = "client!pt", name = "d", descriptor = "[Lclient!wm;")
	public static final TcpPacket[] OUTGOING_PACKETS = new TcpPacket[300];
	@OriginalMember(owner = "client!wia", name = "a", descriptor = "(B)Lclient!wm;")
	public static TcpPacket createPacket() {
		return TcpPacket.index == 0 ? new TcpPacket() : TcpPacket.OUTGOING_PACKETS[--TcpPacket.index];
	}
	@OriginalMember(owner = "client!rea", name = "a", descriptor = "(Z)Lclient!wm;")
	public static TcpPacket createLoginPacket() {
		@Pc(6) TcpPacket packet = TcpPacket.createPacket();
		packet.position = 0;
		packet.packet = null;
		packet.buffer = new BitPacket(5000);
		return packet;
	}
	@OriginalMember(owner = "client!naa", name = "T", descriptor = "Lclient!ku;")
	public static Isaac isaac;

	@OriginalMember(owner = "client!em", name = "a", descriptor = "(Lclient!ej;Lclient!ku;I)Lclient!wm;")
	public static TcpPacket createPacket(@OriginalArg(0) ClientPacket packet, @OriginalArg(1) Isaac isaac) {
		@Pc(8) TcpPacket tcpPacket = TcpPacket.createPacket();
		tcpPacket.position = packet.size;
		tcpPacket.packet = packet;
		if (tcpPacket.position == -1) {
			tcpPacket.buffer = new BitPacket(260);
		} else if (tcpPacket.position == -2) {
			tcpPacket.buffer = new BitPacket(10000);
		} else if (tcpPacket.position <= 18) {
			tcpPacket.buffer = new BitPacket(20);
		} else if (tcpPacket.position <= 98) {
			tcpPacket.buffer = new BitPacket(100);
		} else {
			tcpPacket.buffer = new BitPacket(260);
		}
		tcpPacket.buffer.setRandom(isaac);
		tcpPacket.buffer.startPacket(tcpPacket.packet.getOpcode());
		tcpPacket.capacity = 0;
		return tcpPacket;
	}
	@OriginalMember(owner = "client!gf", name = "a", descriptor = "(IIILclient!wm;I)V")
	public static void writeInterfaceOptionPayload(@OriginalArg(0) int idHash, @OriginalArg(1) int slotId, @OriginalArg(2) int itemId, @OriginalArg(3) TcpPacket packet) {
		packet.buffer.writeInt(idHash);
		packet.buffer.writeShort(itemId);
		packet.buffer.writeShort(slotId);
	}
}
