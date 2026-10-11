package com.jagex.game.runetek5.config.sun;

import com.jagex.game.runetek5.buffer.Packet;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!iea")
public final class SunType {

	@OriginalMember(owner = "client!iea", name = "b", descriptor = "I")
	public int actionType;

	@OriginalMember(owner = "client!iea", name = "c", descriptor = "I")
	public int positionX;

	@OriginalMember(owner = "client!iea", name = "f", descriptor = "I")
	public int positionY;

	@OriginalMember(owner = "client!iea", name = "h", descriptor = "I")
	public int materialId;

	@OriginalMember(owner = "client!iea", name = "k", descriptor = "Z")
	public boolean invertPosition;

	@OriginalMember(owner = "client!iea", name = "l", descriptor = "I")
	public int positionZ;

	@OriginalMember(owner = "client!iea", name = "e", descriptor = "I")
	public int positionOffsetStep = 8;

	@OriginalMember(owner = "client!iea", name = "g", descriptor = "I")
	public int displayColor = 16777215;

	@OriginalMember(owner = "client!iea", name = "a", descriptor = "(ILclient!cea;I)V")
	private void decode(@OriginalArg(1) Packet packet, @OriginalArg(2) int opcode) {
		if (opcode == 1) {
			this.positionOffsetStep = packet.readUShort();
		} else if (opcode == 2) {
			this.invertPosition = true;
		} else if (opcode == 3) {
			this.positionX = packet.readShort();
			this.positionY = packet.readShort();
			this.positionZ = packet.readShort();
		} else if (opcode == 4) {
			this.actionType = packet.readUByte();
		} else if (opcode == 5) {
			this.materialId = packet.readUShort();
		} else if (opcode == 6) {
			this.displayColor = packet.readUMedium();
		}
	}

	@OriginalMember(owner = "client!iea", name = "a", descriptor = "(Lclient!cea;B)V")
	public void decode(@OriginalArg(0) Packet packet) {
		while (true) {
			@Pc(13) int opcode = packet.readUByte();
			if (opcode == 0) {
				return;
			}
			this.decode(packet, opcode);
		}
	}
}
