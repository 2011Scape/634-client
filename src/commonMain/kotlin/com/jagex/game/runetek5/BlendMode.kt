package com.jagex.game.runetek5

import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!pn")
class BlendMode {

	@OriginalMember(owner = "client!pn", name = "toString", descriptor = "()Ljava/lang/String;")
	override fun toString(): String {
		throw IllegalStateException()
	}
}
