package com.jagex.game.runetek5.config.material.texture.operation;

import com.jagex.game.runetek5.graphics.EnvironmentLight;
import com.jagex.game.runetek5.config.material.texture.TextureOperation;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!oi")
public final class TextureOpGreyscale extends TextureOperation {

	@OriginalMember(owner = "client!oi", name = "<init>", descriptor = "()V")
	public TextureOpGreyscale() {
		super(1, true);
	}

	@OriginalMember(owner = "client!oi", name = "c", descriptor = "(II)[I")
	@Override
	public int[] monochromeOutput(@OriginalArg(0) int row) {
		@Pc(13) int[] output = super.monochromeImageCache.get(row);
		if (super.monochromeImageCache.dirty) {
			@Pc(31) int[][] source = this.getChildColorOutput(row, 0);
			@Pc(35) int[] sourceRed = source[0];
			@Pc(39) int[] sourceGreen = source[1];
			@Pc(43) int[] sourceBlue = source[2];
			for (@Pc(45) int x = 0; x < EnvironmentLight.textureOpWidth; x++) {
				output[x] = (sourceRed[x] + sourceGreen[x] + sourceBlue[x]) / 3;
			}
		}
		return output;
	}
}
