package com.jagex.game.runetek5.cache.node;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!ri")
public final class IntNode extends Node {

	@OriginalMember(owner = "client!ri", name = "p", descriptor = "I")
	public int value;

	@OriginalMember(owner = "client!ri", name = "<init>", descriptor = "()V")
	private IntNode() {
	}

	@OriginalMember(owner = "client!ri", name = "<init>", descriptor = "(I)V")
	public IntNode(@OriginalArg(0) int value) {
		this.value = value;
	}
}
