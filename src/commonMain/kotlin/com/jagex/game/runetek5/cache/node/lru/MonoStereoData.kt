package com.jagex.game.runetek5.cache.node.lru

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField

@OriginalClass("client!lm")
class MonoStereoData @OriginalMember(owner = "client!lm", name = "<init>", descriptor = "([[SD)V") constructor(@OriginalArg(0) samples: Array<ShortArray?>?, @OriginalArg(1) timestamp: Double) : CacheableNode() {

	@OriginalMember(owner = "client!lm", name = "y", descriptor = "D")
	@JvmField
	var timestamp: Double = 0.0

	@OriginalMember(owner = "client!lm", name = "E", descriptor = "[[S")
	@JvmField
	val samples: Array<ShortArray?>?

	init {
		this.timestamp = timestamp
		this.samples = samples
	}

	@OriginalMember(owner = "client!lm", name = "a", descriptor = "(I)J")
	fun getCacheKey(): Long {
		return ((this.samples!!.size shl 0) or this.samples[0]!!.size).toLong()
	}
}
