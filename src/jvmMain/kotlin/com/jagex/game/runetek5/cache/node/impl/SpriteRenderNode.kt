package com.jagex.game.runetek5.cache.node.impl

import com.jagex.game.runetek5.cache.node.lru.CacheableNode
import com.jagex.game.runetek5.entity.ComparableGraphNode
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!rd")
abstract class SpriteRenderNode @OriginalMember(owner = "client!rd", name = "<init>", descriptor = "(Lclient!we;I)V") constructor(@OriginalArg(0) cacheKey: ComparableGraphNode?, @OriginalArg(1) size: Int) : CacheableNode() {

	@OriginalMember(owner = "client!rd", name = "z", descriptor = "Lclient!we;")
	@JvmField
	val cacheKey: ComparableGraphNode? = cacheKey

	@OriginalMember(owner = "client!rd", name = "w", descriptor = "I")
	@JvmField
	val size: Int = size

	@OriginalMember(owner = "client!rd", name = "d", descriptor = "(I)Ljava/lang/Object;")
	abstract fun get(): Any?

	@OriginalMember(owner = "client!rd", name = "e", descriptor = "(B)Z")
	abstract fun isSoft(): Boolean
}
