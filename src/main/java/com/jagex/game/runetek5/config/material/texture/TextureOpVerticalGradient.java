package com.jagex.game.runetek5.config.material.texture;

import com.jagex.game.runetek5.core.util.Arrays;
import com.jagex.game.runetek5.graphics.EnvironmentLight;
import com.jagex.game.runetek5.config.material.texture.TextureOperation;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!hl")
public final class TextureOpVerticalGradient extends TextureOperation {

	@OriginalMember(owner = "client!hl", name = "<init>", descriptor = "()V")
	public TextureOpVerticalGradient() {
		super(0, true);
	}

	@OriginalMember(owner = "client!hl", name = "c", descriptor = "(II)[I")
	@Override
	public int[] monochromeOutput(@OriginalArg(0) int y) {
		@Pc(14) int[] dest = super.monochromeImageCache.get(y);
		if (super.monochromeImageCache.dirty) {
			Arrays.setVariables(dest, 0, EnvironmentLight.textureOpWidth, TextureRaster.verticalTable[y]);
		}
		return dest;
	}
}
