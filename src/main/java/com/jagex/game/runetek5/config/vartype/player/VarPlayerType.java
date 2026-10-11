package com.jagex.game.runetek5.config.vartype.player;

import com.jagex.game.runetek5.buffer.Packet;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!ow")
public final class VarPlayerType {

	@OriginalMember(owner = "client!ow", name = "b", descriptor = "I")
	public int clientCode = 0;

	@OriginalMember(owner = "client!ow", name = "a", descriptor = "(Lclient!cea;II)V")
	private void decode(@OriginalArg(0) Packet packet, @OriginalArg(1) int opcode) {
		if (opcode == 5) {
			this.clientCode = packet.readUShort();
		}
	}

	@OriginalMember(owner = "client!ow", name = "a", descriptor = "(Lclient!cea;Z)V")
	public void decode(@OriginalArg(0) Packet packet) {
		while (true) {
			@Pc(5) int opcode = packet.readUByte();
			if (opcode == 0) {
				return;
			}
			this.decode(packet, opcode);
		}
	}
}
