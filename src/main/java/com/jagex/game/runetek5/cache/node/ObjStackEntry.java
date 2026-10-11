package com.jagex.game.runetek5.cache.node;

import com.jagex.game.runetek5.cache.node.Node;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!be")
public final class ObjStackEntry extends Node {

	@OriginalMember(owner = "client!be", name = "t", descriptor = "I")
	public final int id;

	@OriginalMember(owner = "client!be", name = "r", descriptor = "I")
	public int count;

	@OriginalMember(owner = "client!be", name = "<init>", descriptor = "(II)V")
	public ObjStackEntry(@OriginalArg(0) int id, @OriginalArg(1) int count) {
		this.id = id;
		this.count = count;
	}
}
