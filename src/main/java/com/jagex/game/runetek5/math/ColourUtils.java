package com.jagex.game.runetek5.math;

import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.Pc;

public final class ColourUtils {

	@OriginalMember(owner = "client!w", name = "g", descriptor = "I")
	public static int cameraLookAtPathIndex = -1;
	@OriginalMember(owner = "client!wf", name = "a", descriptor = "(IIII)I")
	public static int blendColours(@OriginalArg(0) int colour, @OriginalArg(2) int alpha, @OriginalArg(3) int fogColour) {
		@Pc(13) int inverseAlpha = 255 - alpha;
		@Pc(31) int blendedFog = ((fogColour & 0xFF00FF) * alpha & 0xFF00FF00 | (fogColour & 0xFF00) * alpha & 0xFF0000) >>> 8;
		return blendedFog + ((inverseAlpha * (colour & 0xFF00FF) & 0xFF00FF00 | inverseAlpha * (colour & 0xFF00) & 0xFF0000) >>> 8);
	}

	@OriginalMember(owner = "client!tfa", name = "a", descriptor = "(IBII)I")
	public static int packHsl(@OriginalArg(0) int lightness, @OriginalArg(2) int saturation, @OriginalArg(3) int hue) {
		if (lightness > 243) {
			saturation >>= 0x4;
		} else if (lightness > 217) {
			saturation >>= 0x3;
		} else if (lightness > 192) {
			saturation >>= 0x2;
		} else if (lightness > 179) {
			saturation >>= 0x1;
		}
		return (lightness >> 1) + (saturation >> 5 << 7) + ((hue >> 2 & 0x3F) << 10);
	}
}
