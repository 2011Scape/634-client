package com.jagex.game.runetek5.cache.node.impl;

import com.jagex.game.runetek5.cache.node.lru.CacheableNode;
import com.jagex.game.runetek5.entity.ComparableGraphNode;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!rd")
public abstract class SpriteRenderNode extends CacheableNode {

	@OriginalMember(owner = "client!rd", name = "z", descriptor = "Lclient!we;")
	public final ComparableGraphNode cacheKey;

	@OriginalMember(owner = "client!rd", name = "w", descriptor = "I")
	public final int size;

	@OriginalMember(owner = "client!rd", name = "<init>", descriptor = "(Lclient!we;I)V")
	public SpriteRenderNode(@OriginalArg(0) ComparableGraphNode cacheKey, @OriginalArg(1) int size) {
		this.cacheKey = cacheKey;
		this.size = size;
	}

	@OriginalMember(owner = "client!rd", name = "d", descriptor = "(I)Ljava/lang/Object;")
	public abstract Object get();

	@OriginalMember(owner = "client!rd", name = "e", descriptor = "(B)Z")
	public abstract boolean isSoft();
}
