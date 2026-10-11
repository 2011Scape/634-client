package com.jagex.game.runetek5;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.config.material.texture.TextureTypeList;

public final class BrightnessTableBuilder {

	@OriginalMember(owner = "client!vq", name = "a", descriptor = "(DB)V")
	public static void setBrightness(@OriginalArg(0) double currentBrightness) {
		if (currentBrightness == TextureTypeList.currentBrightness) {
			return;
		}
		for (@Pc(11) int i = 0; i < 256; i++) {
			@Pc(23) int value = (int) (Math.pow((double) i / 255.0D, currentBrightness) * 255.0D);
			TextureTypeList.BRIGHTNESS_TABLE[i] = value > 255 ? 255 : value;
		}
		TextureTypeList.currentBrightness = currentBrightness;
	}
}
