package com.jagex.game.runetek5.config.sequence

import com.jagex.game.runetek5.cache.node.lru.CacheableNode
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.entity.SpotAnimation

@OriginalClass("client!nk")
class PositionedGfx : CacheableNode {

	@OriginalMember(owner = "client!nk", name = "z", descriptor = "Lclient!lf;")
	@JvmField
	val objectGfx: SpotAnimation?

	@OriginalMember(owner = "client!nk", name = "<init>", descriptor = "(Lclient!lf;)V")
	constructor(@OriginalArg(0) spotAnimation: SpotAnimation?) : super() {
		this.objectGfx = spotAnimation
	}
}
