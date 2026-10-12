package com.jagex.game.runetek5

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmField

object LastRedrawTime {

	@OriginalMember(owner = "client!kw", name = "I", descriptor = "J")
	@JvmField
	var lastRedrawTimeMillis: Long = 0L
}
