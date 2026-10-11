package com.jagex.game.runetek5.cache.node;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!cv")
public final class StringNode extends Node {

	@OriginalMember(owner = "client!cv", name = "p", descriptor = "Ljava/lang/String;")
	public String value;

	@OriginalMember(owner = "client!cv", name = "<init>", descriptor = "()V")
	private StringNode() {
	}

	@OriginalMember(owner = "client!cv", name = "<init>", descriptor = "(Ljava/lang/String;)V")
	public StringNode(@OriginalArg(0) String string) {
		this.value = string;
	}
}
