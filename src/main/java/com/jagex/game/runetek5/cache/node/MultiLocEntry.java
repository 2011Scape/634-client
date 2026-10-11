package com.jagex.game.runetek5.cache.node;

import com.jagex.game.runetek5.cache.node.Node;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!sha")
public final class MultiLocEntry extends Node {

	@OriginalMember(owner = "client!sha", name = "o", descriptor = "[S")
	public final short[] locIds;

	@OriginalMember(owner = "client!sha", name = "p", descriptor = "[B")
	public final byte[] locShapes;

	@OriginalMember(owner = "client!sha", name = "<init>", descriptor = "([S[B)V")
	public MultiLocEntry(@OriginalArg(0) short[] locIds, @OriginalArg(1) byte[] locShapes) {
		this.locIds = locIds;
		this.locShapes = locShapes;
	}
}
