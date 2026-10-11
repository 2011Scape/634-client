package com.jagex.game.runetek5.core.datastruct.ref;

import com.jagex.game.runetek5.ReferenceFactory;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.cache.node.lru.AbstractLRUNode;
import com.jagex.game.runetek5.cache.node.lru.LRUSoftReference;

@OriginalClass("client!vt")
public final class SoftReferenceFactory extends ReferenceFactory {

	@OriginalMember(owner = "client!vt", name = "a", descriptor = "(ILclient!qia;)Lclient!qia;")
	@Override
	public AbstractLRUNode createSoftReference(@OriginalArg(1) AbstractLRUNode referenceNode) {
		return new LRUSoftReference(referenceNode.get(), referenceNode.capacityRequirement);
	}
}
