package com.jagex.game.runetek5.js5

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember

object ModelTexCoordFlag {

	@OriginalMember(owner = "client!nw", name = "a", descriptor = "(IBI)Z")
	@JvmStatic
	fun hasTextureCoordinates(@OriginalArg(0) preferenceFlags: Int, @OriginalArg(2) functionMask: Int): Boolean {
		return (functionMask and 0x800) != 0
	}
}
