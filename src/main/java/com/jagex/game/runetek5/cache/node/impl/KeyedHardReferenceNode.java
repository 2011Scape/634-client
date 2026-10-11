package com.jagex.game.runetek5.cache.node.impl;

import com.jagex.game.runetek5.entity.ComparableGraphNode;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!ct")
public final class KeyedHardReferenceNode extends SpriteRenderNode {

	@OriginalMember(owner = "client!ct", name = "B", descriptor = "Ljava/lang/Object;")
	private final Object object;

	@OriginalMember(owner = "client!ct", name = "<init>", descriptor = "(Lclient!we;Ljava/lang/Object;I)V")
	public KeyedHardReferenceNode(@OriginalArg(0) ComparableGraphNode cacheKey, @OriginalArg(1) Object object2, @OriginalArg(2) int size) {
		super(cacheKey, size);
		this.object = object2;
	}

	@OriginalMember(owner = "client!ct", name = "d", descriptor = "(I)Ljava/lang/Object;")
	@Override
	public Object get() {
		return this.object;
	}

	@OriginalMember(owner = "client!ct", name = "e", descriptor = "(B)Z")
	@Override
	public boolean isSoft() {
		return false;
	}
}
