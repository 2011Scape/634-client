package com.jagex.game.runetek5.core.datastruct.ref.key

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmStatic

object ShadedMaskTest {

	@OriginalMember(owner = "client!rd", name = "a", descriptor = "(IBI)Z")
	@JvmStatic
	fun usesShading(@OriginalArg(0) flags: Int, @OriginalArg(2) functionMask: Int): Boolean {
		return (functionMask and 0x10000) != 0
	}
}
