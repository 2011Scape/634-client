package com.jagex.game.runetek5.cache.node

import com.jagex.game.runetek5.graphics.sw.SoftwareObject
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!ms")
class SoftwareObjectNode : Node() {

	@OriginalMember(owner = "client!ms", name = "o", descriptor = "Lclient!e;")
	@JvmField
	var `object`: SoftwareObject? = null
}
