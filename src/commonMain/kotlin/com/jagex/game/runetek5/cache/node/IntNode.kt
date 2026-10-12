package com.jagex.game.runetek5.cache.node

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField

@OriginalClass("client!ri")
class IntNode : Node {

	@OriginalMember(owner = "client!ri", name = "p", descriptor = "I")
	@JvmField
	var value: Int = 0

	@OriginalMember(owner = "client!ri", name = "<init>", descriptor = "()V")
	private constructor() {
	}

	@OriginalMember(owner = "client!ri", name = "<init>", descriptor = "(I)V")
	constructor(@OriginalArg(0) value: Int) {
		this.value = value
	}
}
