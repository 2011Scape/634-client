package com.jagex.game.runetek5.cache.node.impl

import com.jagex.game.runetek5.entity.ComparableGraphNode
import java.lang.ref.SoftReference
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!ml")
class KeyedSoftReferenceNode @OriginalMember(owner = "client!ml", name = "<init>", descriptor = "(Lclient!we;Ljava/lang/Object;I)V") constructor(@OriginalArg(0) cacheKey: ComparableGraphNode?, @OriginalArg(1) `object`: Any?, @OriginalArg(2) size: Int) : SpriteRenderNode(cacheKey, size) {

	@OriginalMember(owner = "client!ml", name = "A", descriptor = "Ljava/lang/ref/SoftReference;")
	private val softReference: SoftReference<Any?> = SoftReference<Any?>(`object`)

	@OriginalMember(owner = "client!ml", name = "d", descriptor = "(I)Ljava/lang/Object;")
	override fun get(): Any? {
		return this.softReference.get()
	}

	@OriginalMember(owner = "client!ml", name = "e", descriptor = "(B)Z")
	override fun isSoft(): Boolean {
		return true
	}
}
