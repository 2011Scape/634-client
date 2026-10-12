package com.jagex.game.runetek5.cache.node.lru

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.entity.ProjectileAnimation

@OriginalClass("client!jh")
class ProjectileAnimationNode @OriginalMember(owner = "client!jh", name = "<init>", descriptor = "(Lclient!to;)V") constructor(@OriginalArg(0) projectileAnimation: ProjectileAnimation?) : CacheableNode() {

	@OriginalMember(owner = "client!jh", name = "x", descriptor = "Lclient!to;")
	@JvmField
	val projectileAnimation: ProjectileAnimation? = projectileAnimation
}
