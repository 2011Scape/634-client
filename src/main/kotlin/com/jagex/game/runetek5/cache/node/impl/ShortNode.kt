package com.jagex.game.runetek5.cache.node.impl

import com.jagex.game.runetek5.cache.node.Node
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!oia")
class ShortNode : Node {

	@OriginalMember(owner = "client!oia", name = "q", descriptor = "S")
	@JvmField
	var value: Short = 0

	@OriginalMember(owner = "client!oia", name = "<init>", descriptor = "()V")
	private constructor() {
	}

	@OriginalMember(owner = "client!oia", name = "<init>", descriptor = "(S)V")
	constructor(@OriginalArg(0) shortValue: Short) {
		this.value = shortValue
	}
}
