package com.jagex.game.runetek5.graphics.safe

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmStatic

object PureJavaFloorModel {

	@OriginalMember(owner = "client!cr", name = "c", descriptor = "(II)I")
	@JvmStatic
	fun brighten(@OriginalArg(0) color: Int, @OriginalArg(1) multiplier: Int): Int {
		@Pc(7) var red: Int = ((color and 0xFF0000) * multiplier) shr 23
		if (red < 2) {
			red = 2
		} else if (red > 253) {
			red = 253
		}
		@Pc(26) var green: Int = ((color and 0xFF00) * multiplier) shr 15
		if (green < 2) {
			green = 2
		} else if (green > 253) {
			green = 253
		}
		@Pc(45) var blue: Int = ((color and 0xFF) * multiplier) shr 7
		if (blue < 2) {
			blue = 2
		} else if (blue > 253) {
			blue = 253
		}
		return (red shl 16) or (green shl 8) or blue
	}
}
