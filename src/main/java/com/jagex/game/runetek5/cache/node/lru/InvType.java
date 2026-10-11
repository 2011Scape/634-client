package com.jagex.game.runetek5.cache.node.lru;

import com.jagex.game.runetek5.cache.node.lru.CacheableNode;
import com.jagex.game.runetek5.buffer.Packet;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!pv")
public final class InvType extends CacheableNode {

	@OriginalMember(owner = "client!pv", name = "y", descriptor = "I")
	public int size = 0;

	@OriginalMember(owner = "client!pv", name = "a", descriptor = "(ILclient!cea;B)V")
	private void decode(@OriginalArg(0) int opcode, @OriginalArg(1) Packet packet) {
		if (opcode == 2) {
			this.size = packet.readUShort();
		}
	}

	@OriginalMember(owner = "client!pv", name = "a", descriptor = "(BLclient!cea;)V")
	public void decode(@OriginalArg(1) Packet packet) {
		while (true) {
			@Pc(7) int opcode = packet.readUByte();
			if (opcode == 0) {
				return;
			}
			this.decode(opcode, packet);
		}
	}
}
