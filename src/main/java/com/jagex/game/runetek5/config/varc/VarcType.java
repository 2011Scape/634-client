package com.jagex.game.runetek5.config.varc;

import com.jagex.game.runetek5.buffer.Packet;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.utils.Utils;

@OriginalClass("client!pa")
public final class VarcType {

	@OriginalMember(owner = "client!pa", name = "d", descriptor = "C")
	public char typeChar;

	@OriginalMember(owner = "client!pa", name = "g", descriptor = "I")
	public int persistenceType = 1;

	@OriginalMember(owner = "client!pa", name = "a", descriptor = "(Lclient!cea;I)V")
	public void decode(@OriginalArg(0) Packet packet) {
		while (true) {
			@Pc(5) int opcode = packet.readUByte();
			if (opcode == 0) {
				return;
			}
			this.decode(opcode, packet);
		}
	}

	@OriginalMember(owner = "client!pa", name = "a", descriptor = "(IILclient!cea;)V")
	private void decode(@OriginalArg(1) int opcode, @OriginalArg(2) Packet packet) {
		if (opcode == 1) {
			this.typeChar = Utils.cp1252ToChar(packet.readByte());
		} else if (opcode == 2) {
			this.persistenceType = 0;
		}
	}
}
