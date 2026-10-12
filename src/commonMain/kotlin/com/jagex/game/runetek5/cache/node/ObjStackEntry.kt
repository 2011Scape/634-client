package com.jagex.game.runetek5.cache.node

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField

@OriginalClass("client!be")
class ObjStackEntry : Node {

	@OriginalMember(owner = "client!be", name = "t", descriptor = "I")
	@JvmField
	val id: Int

	@OriginalMember(owner = "client!be", name = "r", descriptor = "I")
	@JvmField
	var count: Int

	@OriginalMember(owner = "client!be", name = "<init>", descriptor = "(II)V")
	constructor(@OriginalArg(0) id: Int, @OriginalArg(1) count: Int) {
		this.id = id
		this.count = count
	}
}
