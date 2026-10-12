package com.jagex.game.runetek5.core.datastruct.ref

import com.jagex.game.runetek5.ReferenceFactory
import com.jagex.game.runetek5.cache.node.lru.AbstractLRUNode
import com.jagex.game.runetek5.cache.node.lru.LRUSoftReference
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!vt")
class SoftReferenceFactory : ReferenceFactory() {

	@OriginalMember(owner = "client!vt", name = "a", descriptor = "(ILclient!qia;)Lclient!qia;")
	override fun createSoftReference(@OriginalArg(1) referenceNode: AbstractLRUNode?): AbstractLRUNode? {
		return LRUSoftReference(referenceNode!!.get(), referenceNode.capacityRequirement)
	}
}
