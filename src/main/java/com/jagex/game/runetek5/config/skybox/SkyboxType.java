package com.jagex.game.runetek5.config.skybox;

import com.jagex.game.runetek5.buffer.Packet;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!wia")
public final class SkyboxType {

	@OriginalMember(owner = "client!wia", name = "g", descriptor = "[I")
	public int[] sunDefinitionIds;

	@OriginalMember(owner = "client!wia", name = "i", descriptor = "I")
	public int defaultSunIndex = -1;

	@OriginalMember(owner = "client!wia", name = "c", descriptor = "I")
	public int materialId = -1;

	@OriginalMember(owner = "client!wia", name = "a", descriptor = "(Lclient!cea;B)V")
	public void decode(@OriginalArg(0) Packet packet) {
		while (true) {
			@Pc(9) int opcode = packet.readUByte();
			if (opcode == 0) {
				return;
			}
			this.decode(packet, opcode);
		}
	}

	@OriginalMember(owner = "client!wia", name = "a", descriptor = "(ILclient!cea;I)V")
	private void decode(@OriginalArg(1) Packet packet, @OriginalArg(2) int opcode) {
		if (opcode == 1) {
			this.materialId = packet.readUShort();
		} else if (opcode == 2) {
			this.sunDefinitionIds = new int[packet.readUByte()];
			for (@Pc(34) int i = 0; i < this.sunDefinitionIds.length; i++) {
				this.sunDefinitionIds[i] = packet.readUShort();
			}
		} else if (opcode == 3) {
			this.defaultSunIndex = packet.readUByte();
		}
	}
}
