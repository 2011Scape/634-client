package com.jagex.game.runetek5.cache.node

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!sha")
class MultiLocEntry : Node {

	@OriginalMember(owner = "client!sha", name = "o", descriptor = "[S")
	@JvmField
	val locIds: ShortArray?

	@OriginalMember(owner = "client!sha", name = "p", descriptor = "[B")
	@JvmField
	val locShapes: ByteArray?

	@OriginalMember(owner = "client!sha", name = "<init>", descriptor = "([S[B)V")
	constructor(@OriginalArg(0) locIds: ShortArray?, @OriginalArg(1) locShapes: ByteArray?) {
		this.locIds = locIds
		this.locShapes = locShapes
	}
}
