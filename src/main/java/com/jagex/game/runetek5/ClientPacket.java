package com.jagex.game.runetek5;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!ej")
public final class ClientPacket {

	@OriginalMember(owner = "client!ej", name = "e", descriptor = "I")
	private final int opcode;

	@OriginalMember(owner = "client!ej", name = "b", descriptor = "I")
	public final int size;

	@OriginalMember(owner = "client!ej", name = "<init>", descriptor = "(II)V")
	public ClientPacket(@OriginalArg(0) int opcode, @OriginalArg(1) int size) {
		this.opcode = opcode;
		this.size = size;
	}

	@OriginalMember(owner = "client!ej", name = "toString", descriptor = "()Ljava/lang/String;")
	@Override
	public String toString() {
		throw new IllegalStateException();
	}

	@OriginalMember(owner = "client!ej", name = "a", descriptor = "(I)I")
	public int getOpcode() {
		return this.opcode;
	}
}
