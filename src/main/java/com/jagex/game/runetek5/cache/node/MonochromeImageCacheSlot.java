package com.jagex.game.runetek5.cache.node;

import com.jagex.game.runetek5.cache.node.Node;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!gba")
public final class MonochromeImageCacheSlot extends Node {

	@OriginalMember(owner = "client!gba", name = "u", descriptor = "I")
	public final int slot;

	@OriginalMember(owner = "client!gba", name = "o", descriptor = "I")
	public final int id;

	@OriginalMember(owner = "client!gba", name = "<init>", descriptor = "(II)V")
	public MonochromeImageCacheSlot(@OriginalArg(0) int id, @OriginalArg(1) int slot) {
		this.slot = slot;
		this.id = id;
	}
}
