package com.jagex.game.runetek5.graphics.model.billboard;

import com.jagex.game.runetek5.buffer.Packet;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!qj")
public final class BillboardType {

	@OriginalMember(owner = "client!qj", name = "b", descriptor = "Z")
	public boolean stationary = false;

	@OriginalMember(owner = "client!qj", name = "k", descriptor = "Z")
	public boolean hasUid = false;

	@OriginalMember(owner = "client!qj", name = "m", descriptor = "I")
	public int blendType = 1;

	@OriginalMember(owner = "client!qj", name = "f", descriptor = "I")
	public int size2d = 64;

	@OriginalMember(owner = "client!qj", name = "j", descriptor = "I")
	public int size3d = 64;

	@OriginalMember(owner = "client!qj", name = "e", descriptor = "I")
	public int materialId = -1;

	@OriginalMember(owner = "client!qj", name = "n", descriptor = "I")
	public int shape = 2;

	@OriginalMember(owner = "client!qj", name = "a", descriptor = "(IILclient!cea;B)V")
	private void decode(@OriginalArg(0) int opcode, @OriginalArg(1) int id, @OriginalArg(2) Packet packet) {
		if (opcode == 1) {
			this.materialId = packet.readUShort();
			if (this.materialId == 65535) {
				this.materialId = -1;
				return;
			}
		} else if (opcode == 2) {
			this.size2d = packet.readUShort() + 1;
			this.size3d = packet.readUShort() + 1;
		} else if (opcode == 3) {
			packet.readByte();
			return;
		} else if (opcode == 4) {
			this.shape = packet.readUByte();
			return;
		} else {
			if (opcode == 5) {
				this.blendType = packet.readUByte();
			} else if (opcode == 6) {
				this.stationary = true;
				return;
			} else if (opcode == 7) {
				this.hasUid = true;
				return;
			}
			return;
		}
	}

	@OriginalMember(owner = "client!qj", name = "a", descriptor = "(ILclient!cea;B)V")
	public void decode(@OriginalArg(0) int id, @OriginalArg(1) Packet packet) {
		while (true) {
			@Pc(17) int opcode = packet.readUByte();
			if (opcode == 0) {
				return;
			}
			this.decode(opcode, id, packet);
		}
	}
}
