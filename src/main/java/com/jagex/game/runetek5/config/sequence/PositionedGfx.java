package com.jagex.game.runetek5.config.sequence;

import com.jagex.game.runetek5.cache.node.lru.CacheableNode;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.entity.SpotAnimation;

@OriginalClass("client!nk")
public final class PositionedGfx extends CacheableNode {

	@OriginalMember(owner = "client!nk", name = "z", descriptor = "Lclient!lf;")
	public final SpotAnimation objectGfx;

	@OriginalMember(owner = "client!nk", name = "<init>", descriptor = "(Lclient!lf;)V")
	public PositionedGfx(@OriginalArg(0) SpotAnimation spotAnimation) {
		this.objectGfx = spotAnimation;
	}
}
