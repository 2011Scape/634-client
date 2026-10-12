package com.jagex.game.runetek5.cache.node

import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!za")
abstract class MemoryPool : Node {

	@OriginalMember(owner = "client!za", name = "<init>", descriptor = "()V")
	constructor() {
	}
}
