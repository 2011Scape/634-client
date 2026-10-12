package com.jagex.game.runetek5.config.rsinterface

import com.jagex.game.runetek5.config.rsinterface.ComponentSurface
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField

@OriginalClass("client!nba")
class JavaClippingMask : ComponentSurface {

	@OriginalMember(owner = "client!nba", name = "e", descriptor = "[I")
	@JvmField
	val lineWidths: IntArray?

	@OriginalMember(owner = "client!nba", name = "f", descriptor = "[I")
	@JvmField
	val lineOffsets: IntArray?

	@OriginalMember(owner = "client!nba", name = "<init>", descriptor = "(II[I[I)V")
	constructor(@OriginalArg(0) width: Int, @OriginalArg(1) height: Int, @OriginalArg(2) lineWidths: IntArray?, @OriginalArg(3) lineOffsets: IntArray?) : super() {
		this.lineWidths = lineWidths
		this.lineOffsets = lineOffsets
	}
}
