package com.jagex.game.runetek5.cache.node;

import com.jagex.game.runetek5.cache.node.Node;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!za")
public abstract class MemoryPool extends Node {

	@OriginalMember(owner = "client!za", name = "<init>", descriptor = "()V")
	public MemoryPool() {
	}
}
