package com.jagex.game.runetek5.cache.node.impl

import com.jagex.game.runetek5.entity.ComparableGraphNode
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!ct")
class KeyedHardReferenceNode @OriginalMember(owner = "client!ct", name = "<init>", descriptor = "(Lclient!we;Ljava/lang/Object;I)V") constructor(@OriginalArg(0) cacheKey: ComparableGraphNode?, @OriginalArg(1) object2: Any?, @OriginalArg(2) size: Int) : SpriteRenderNode(cacheKey, size) {

	@OriginalMember(owner = "client!ct", name = "B", descriptor = "Ljava/lang/Object;")
	private val `object`: Any? = object2

	@OriginalMember(owner = "client!ct", name = "d", descriptor = "(I)Ljava/lang/Object;")
	override fun get(): Any? {
		return this.`object`
	}

	@OriginalMember(owner = "client!ct", name = "e", descriptor = "(B)Z")
	override fun isSoft(): Boolean {
		return false
	}
}
