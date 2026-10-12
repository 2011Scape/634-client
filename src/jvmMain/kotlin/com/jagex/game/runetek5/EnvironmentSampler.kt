package com.jagex.game.runetek5

import com.jagex.game.runetek5.cache.node.lru.LRUCache
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!wh")
abstract class EnvironmentSampler {
	companion object {
		@OriginalMember(owner = "client!gf", name = "e", descriptor = "Lclient!jr;")
		@JvmField
		val aClass169_27: LRUCache = LRUCache(8)
	}
}
