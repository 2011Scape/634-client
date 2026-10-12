package com.jagex.game.runetek5.cache.node

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!cv")
class StringNode : Node {

	@OriginalMember(owner = "client!cv", name = "p", descriptor = "Ljava/lang/String;")
	@JvmField
	var value: String? = null

	@OriginalMember(owner = "client!cv", name = "<init>", descriptor = "()V")
	private constructor() {
	}

	@OriginalMember(owner = "client!cv", name = "<init>", descriptor = "(Ljava/lang/String;)V")
	constructor(@OriginalArg(0) string: String?) {
		this.value = string
	}
}
