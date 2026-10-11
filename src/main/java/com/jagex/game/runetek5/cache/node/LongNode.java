package com.jagex.game.runetek5.cache.node;

import com.jagex.game.runetek5.cache.node.Node;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!ij")
public final class LongNode extends Node {

	@OriginalMember(owner = "client!ij", name = "q", descriptor = "J")
	public long value;

	@OriginalMember(owner = "client!ij", name = "<init>", descriptor = "()V")
	private LongNode() {
	}

	@OriginalMember(owner = "client!ij", name = "<init>", descriptor = "(J)V")
	public LongNode(@OriginalArg(0) long value) {
		this.value = value;
	}
}
