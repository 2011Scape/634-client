package com.jagex.game.runetek5.cache.node

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!mj")
class PingRequest : Node {

	@OriginalMember(owner = "client!mj", name = "q", descriptor = "I")
	@Volatile
	@JvmField
	var ping: Int = -1

	@OriginalMember(owner = "client!mj", name = "r", descriptor = "Ljava/lang/String;")
	@JvmField
	val address: String?

	@OriginalMember(owner = "client!mj", name = "<init>", descriptor = "(Ljava/lang/String;)V")
	constructor(@OriginalArg(0) string: String?) {
		this.address = string
	}
}
