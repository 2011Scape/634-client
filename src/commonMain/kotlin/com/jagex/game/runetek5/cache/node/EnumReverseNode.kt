package com.jagex.game.runetek5.cache.node

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField

@OriginalClass("client!wl")
class EnumReverseNode : Node {

	@OriginalMember(owner = "client!wl", name = "q", descriptor = "Ljava/lang/String;")
	@JvmField
	var value: String? = null

	@OriginalMember(owner = "client!wl", name = "<init>", descriptor = "()V")
	private constructor() {
	}

	@OriginalMember(owner = "client!wl", name = "<init>", descriptor = "(Ljava/lang/String;I)V")
	constructor(@OriginalArg(0) string: String?, @OriginalArg(1) key: Int) {
		this.value = string
	}
}
