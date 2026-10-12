package com.jagex.game.runetek5.cache.node.lru

import java.lang.ref.SoftReference
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!eia")
class LRUSoftReference @OriginalMember(owner = "client!eia", name = "<init>", descriptor = "(Ljava/lang/Object;I)V") constructor(@OriginalArg(0) `object`: Any?, @OriginalArg(1) capacity: Int) : AbstractLRUNode(capacity) {

	@OriginalMember(owner = "client!eia", name = "D", descriptor = "Ljava/lang/ref/SoftReference;")
	private val softReference: SoftReference<Any?> = SoftReference<Any?>(`object`)

	@OriginalMember(owner = "client!eia", name = "d", descriptor = "(I)Z")
	override fun isSoft(): Boolean {
		return true
	}

	@OriginalMember(owner = "client!eia", name = "a", descriptor = "(I)Ljava/lang/Object;")
	override fun get(): Any? {
		return this.softReference.get()
	}
}
