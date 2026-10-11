package com.jagex.game.runetek5;

import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.cache.node.lru.LRUCache;

@OriginalClass("client!wh")
public abstract class EnvironmentSampler {
	@OriginalMember(owner = "client!gf", name = "e", descriptor = "Lclient!jr;")
	public static final LRUCache aClass169_27 = new LRUCache(8);
}
