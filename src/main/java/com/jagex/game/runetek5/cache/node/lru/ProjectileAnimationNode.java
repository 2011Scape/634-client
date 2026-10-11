package com.jagex.game.runetek5.cache.node.lru;

import com.jagex.game.runetek5.cache.node.lru.CacheableNode;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.entity.ProjectileAnimation;

@OriginalClass("client!jh")
public final class ProjectileAnimationNode extends CacheableNode {

	@OriginalMember(owner = "client!jh", name = "x", descriptor = "Lclient!to;")
	public final ProjectileAnimation projectileAnimation;

	@OriginalMember(owner = "client!jh", name = "<init>", descriptor = "(Lclient!to;)V")
	public ProjectileAnimationNode(@OriginalArg(0) ProjectileAnimation projectileAnimation) {
		this.projectileAnimation = projectileAnimation;
	}
}
