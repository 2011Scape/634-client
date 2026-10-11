package com.jagex.game.runetek5.config.light;

import com.jagex.game.runetek5.buffer.Packet;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!u")
public final class LightType {

	@OriginalMember(owner = "client!u", name = "b", descriptor = "I")
	public int effect = 0;

	@OriginalMember(owner = "client!u", name = "a", descriptor = "I")
	public int ticker = 2048;

	@OriginalMember(owner = "client!u", name = "f", descriptor = "I")
	public int duration = 2048;

	@OriginalMember(owner = "client!u", name = "h", descriptor = "I")
	public int surrounding = 0;

	@OriginalMember(owner = "client!u", name = "a", descriptor = "(BLclient!cea;)V")
	public void decode(@OriginalArg(1) Packet packet) {
		while (true) {
			@Pc(17) int opcode = packet.readUByte();
			if (opcode == 0) {
				return;
			}
			this.decode(opcode, packet);
		}
	}

	@OriginalMember(owner = "client!u", name = "a", descriptor = "(IILclient!cea;)V")
	private void decode(@OriginalArg(0) int opcode, @OriginalArg(2) Packet packet) {
		if (opcode == 1) {
			this.effect = packet.readUByte();
		} else if (opcode == 2) {
			this.duration = packet.readUShort();
		} else if (opcode == 3) {
			this.ticker = packet.readUShort();
			return;
		} else if (opcode == 4) {
			this.surrounding = packet.readShort();
			return;
		}
	}
}
