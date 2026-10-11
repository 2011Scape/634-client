package com.jagex.game.runetek5.cache.node.impl;

import com.jagex.game.runetek5.cache.node.Node;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!oia")
public final class ShortNode extends Node {

	@OriginalMember(owner = "client!oia", name = "q", descriptor = "S")
	public short value;

	@OriginalMember(owner = "client!oia", name = "<init>", descriptor = "()V")
	private ShortNode() {
	}

	@OriginalMember(owner = "client!oia", name = "<init>", descriptor = "(S)V")
	public ShortNode(@OriginalArg(0) short shortValue) {
		this.value = shortValue;
	}
}
