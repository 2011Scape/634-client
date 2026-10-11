package com.jagex.game.runetek5.graphics.safe;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

public final class PureJavaFloorModel {

	@OriginalMember(owner = "client!cr", name = "c", descriptor = "(II)I")
	public static int brighten(@OriginalArg(0) int color, @OriginalArg(1) int multiplier) {
		@Pc(7) int red = (color & 0xFF0000) * multiplier >> 23;
		if (red < 2) {
			red = 2;
		} else if (red > 253) {
			red = 253;
		}
		@Pc(26) int green = (color & 0xFF00) * multiplier >> 15;
		if (green < 2) {
			green = 2;
		} else if (green > 253) {
			green = 253;
		}
		@Pc(45) int blue = (color & 0xFF) * multiplier >> 7;
		if (blue < 2) {
			blue = 2;
		} else if (blue > 253) {
			blue = 253;
		}
		return red << 16 | green << 8 | blue;
	}
}
