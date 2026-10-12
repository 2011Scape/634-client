package com.jagex.game.runetek5

import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!ne")
class NanoTimer : AbstractNanoTimer {

	@OriginalMember(owner = "client!ne", name = "<init>", descriptor = "()V")
	constructor() : super() {
		lang.nanoTime()
	}

	@OriginalMember(owner = "client!ne", name = "b", descriptor = "(I)J")
	override fun nanoTime(): Long {
		return lang.nanoTime()
	}
}
