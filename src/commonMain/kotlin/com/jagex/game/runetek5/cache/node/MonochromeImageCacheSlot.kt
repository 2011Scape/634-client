package com.jagex.game.runetek5.cache.node

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField

@OriginalClass("client!gba")
class MonochromeImageCacheSlot : Node {

	@OriginalMember(owner = "client!gba", name = "u", descriptor = "I")
	@JvmField
	val slot: Int

	@OriginalMember(owner = "client!gba", name = "o", descriptor = "I")
	@JvmField
	val id: Int

	@OriginalMember(owner = "client!gba", name = "<init>", descriptor = "(II)V")
	constructor(@OriginalArg(0) id: Int, @OriginalArg(1) slot: Int) {
		this.slot = slot
		this.id = id
	}
}
