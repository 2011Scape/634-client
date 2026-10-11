package com.jagex.game.runetek5.config.param;

import com.jagex.game.runetek5.buffer.Packet;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.utils.Utils;

@OriginalClass("client!uaa")
public final class ParamType {

	@OriginalMember(owner = "client!uaa", name = "a", descriptor = "I")
	public int defaultInt;

	@OriginalMember(owner = "client!uaa", name = "c", descriptor = "Ljava/lang/String;")
	public String typeName;

	@OriginalMember(owner = "client!uaa", name = "d", descriptor = "C")
	private char typeChar;

	@OriginalMember(owner = "client!uaa", name = "f", descriptor = "Z")
	public boolean autoDisable = true;

	@OriginalMember(owner = "client!uaa", name = "a", descriptor = "(Lclient!cea;B)V")
	public void decode(@OriginalArg(0) Packet packet) {
		while (true) {
			@Pc(13) int opcode = packet.readUByte();
			if (opcode == 0) {
				return;
			}
			this.decode(packet, opcode);
		}
	}

	@OriginalMember(owner = "client!uaa", name = "a", descriptor = "(Z)Z")
	public boolean isString() {
		return this.typeChar == 's';
	}

	@OriginalMember(owner = "client!uaa", name = "a", descriptor = "(ZLclient!cea;I)V")
	private void decode(@OriginalArg(1) Packet packet, @OriginalArg(2) int opcode) {
		if (opcode == 1) {
			this.typeChar = Utils.cp1252ToChar(packet.readByte());
		} else if (opcode == 2) {
			this.defaultInt = packet.readInt();
		} else if (opcode == 4) {
			this.autoDisable = false;
		} else if (opcode == 5) {
			this.typeName = packet.readString();
		}
	}
}
