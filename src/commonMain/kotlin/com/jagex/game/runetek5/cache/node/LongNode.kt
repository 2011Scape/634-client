package com.jagex.game.runetek5.cache.node

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField

@OriginalClass("client!ij")
class LongNode : Node {

	@OriginalMember(owner = "client!ij", name = "q", descriptor = "J")
	@JvmField
	var value: Long = 0L

	@OriginalMember(owner = "client!ij", name = "<init>", descriptor = "()V")
	private constructor() {
	}

	@OriginalMember(owner = "client!ij", name = "<init>", descriptor = "(J)V")
	constructor(@OriginalArg(0) value: Long) {
		this.value = value
	}
}
