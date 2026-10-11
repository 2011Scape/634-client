package com.jagex.game.runetek5.cache.node.impl;

import com.jagex.game.runetek5.entity.ComparableGraphNode;
import java.lang.ref.SoftReference;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!ml")
public final class KeyedSoftReferenceNode extends SpriteRenderNode {

	@OriginalMember(owner = "client!ml", name = "A", descriptor = "Ljava/lang/ref/SoftReference;")
	private final SoftReference softReference;

	@OriginalMember(owner = "client!ml", name = "<init>", descriptor = "(Lclient!we;Ljava/lang/Object;I)V")
	public KeyedSoftReferenceNode(@OriginalArg(0) ComparableGraphNode cacheKey, @OriginalArg(1) Object object, @OriginalArg(2) int size) {
		super(cacheKey, size);
		this.softReference = new SoftReference(object);
	}

	@OriginalMember(owner = "client!ml", name = "d", descriptor = "(I)Ljava/lang/Object;")
	@Override
	public Object get() {
		return this.softReference.get();
	}

	@OriginalMember(owner = "client!ml", name = "e", descriptor = "(B)Z")
	@Override
	public boolean isSoft() {
		return true;
	}
}
