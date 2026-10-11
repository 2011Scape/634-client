package com.jagex.game.runetek5.config.varp;

import com.jagex.game.runetek5.buffer.Packet;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!bia")
public final class VarbitType {

	@OriginalMember(owner = "client!bia", name = "e", descriptor = "I")
	public int startBit;

	@OriginalMember(owner = "client!bia", name = "h", descriptor = "I")
	public int configIndex;

	@OriginalMember(owner = "client!bia", name = "i", descriptor = "I")
	public int endBit;

	@OriginalMember(owner = "client!bia", name = "a", descriptor = "(ILclient!cea;)V")
	public void decode(@OriginalArg(1) Packet packet) {
		while (true) {
			@Pc(13) int opcode = packet.readUByte();
			if (opcode == 0) {
				return;
			}
			this.decode(opcode, packet);
		}
	}

	@OriginalMember(owner = "client!bia", name = "a", descriptor = "(ILclient!cea;B)V")
	private void decode(@OriginalArg(0) int opcode, @OriginalArg(1) Packet packet) {
		if (opcode == 1) {
			this.configIndex = packet.readUShort();
			this.startBit = packet.readUByte();
			this.endBit = packet.readUByte();
		}
	}
}
