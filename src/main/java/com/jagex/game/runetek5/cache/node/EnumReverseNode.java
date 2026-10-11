package com.jagex.game.runetek5.cache.node;

import com.jagex.game.runetek5.cache.node.Node;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!wl")
public final class EnumReverseNode extends Node {

	@OriginalMember(owner = "client!wl", name = "q", descriptor = "Ljava/lang/String;")
	public String value;

	@OriginalMember(owner = "client!wl", name = "<init>", descriptor = "()V")
	private EnumReverseNode() {
	}

	@OriginalMember(owner = "client!wl", name = "<init>", descriptor = "(Ljava/lang/String;I)V")
	public EnumReverseNode(@OriginalArg(0) String string, @OriginalArg(1) int key) {
		this.value = string;
	}
}
