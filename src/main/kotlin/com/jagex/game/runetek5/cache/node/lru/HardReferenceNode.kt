package com.jagex.game.runetek5.cache.node.lru

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!pf")
class HardReferenceNode @OriginalMember(owner = "client!pf", name = "<init>", descriptor = "(Ljava/lang/Object;I)V") constructor(@OriginalArg(0) object2: Any?, @OriginalArg(1) capacityRequirement: Int) : AbstractLRUNode(capacityRequirement) {

	@OriginalMember(owner = "client!pf", name = "D", descriptor = "Ljava/lang/Object;")
	private val `object`: Any? = object2

	@OriginalMember(owner = "client!pf", name = "d", descriptor = "(I)Z")
	override fun isSoft(): Boolean {
		return false
	}

	@OriginalMember(owner = "client!pf", name = "a", descriptor = "(I)Ljava/lang/Object;")
	override fun get(): Any? {
		return this.`object`
	}
}
