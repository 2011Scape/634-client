package com.jagex.game.runetek5;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!daa")
public final class VertexAttribute {

	@OriginalMember(owner = "client!daa", name = "g", descriptor = "I")
	private final int componentCount;

	@OriginalMember(owner = "client!daa", name = "f", descriptor = "Lclient!baa;")
	private final DataType actionFormat;

	@OriginalMember(owner = "client!daa", name = "e", descriptor = "I")
	public final int slot;

	@OriginalMember(owner = "client!daa", name = "d", descriptor = "I")
	public final int pointerOffsetStep;

	@OriginalMember(owner = "client!daa", name = "<init>", descriptor = "(IILclient!baa;)V")
	public VertexAttribute(@OriginalArg(0) int slot, @OriginalArg(1) int componentCount, @OriginalArg(2) DataType actionFormat) {
		this.componentCount = componentCount;
		this.actionFormat = actionFormat;
		this.slot = slot;
		this.pointerOffsetStep = this.actionFormat.bytesPerEntry * this.componentCount;
		if (this.slot >= 16) {
			throw new RuntimeException();
		}
	}

	@OriginalMember(owner = "client!daa", name = "toString", descriptor = "()Ljava/lang/String;")
	@Override
	public String toString() {
		throw new IllegalStateException();
	}
}
