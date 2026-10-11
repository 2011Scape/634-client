package com.jagex.game.runetek5.config.material.texture.operation;

import com.jagex.game.runetek5.graphics.EnvironmentLight;
import com.jagex.game.runetek5.config.material.texture.TextureOperation;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.ClientTrigger;
import com.jagex.game.runetek5.config.material.texture.TextureRaster;

@OriginalClass("client!rn")
public final class TextureOpNoise extends TextureOperation {

	static {
		new ClientTrigger("", 73);
	}

	@OriginalMember(owner = "client!rn", name = "<init>", descriptor = "()V")
	public TextureOpNoise() {
		super(0, true);
	}

	@OriginalMember(owner = "client!rn", name = "d", descriptor = "(III)I")
	private int hashNoise(@OriginalArg(0) int y, @OriginalArg(1) int x) {
		@Pc(15) int seed = x + y * 57;
		@Pc(21) int hash = seed ^ seed << 1;
		return 4096 - ((hash * 15731 * hash + 789221) * hash + 1376312589 & Integer.MAX_VALUE) / 262144;
	}

	@OriginalMember(owner = "client!rn", name = "c", descriptor = "(II)[I")
	@Override
	public int[] monochromeOutput(@OriginalArg(0) int row) {
		@Pc(11) int[] output = super.monochromeImageCache.get(row);
		if (super.monochromeImageCache.dirty) {
			@Pc(19) int verticalPos = TextureRaster.verticalTable[row];
			for (@Pc(21) int i = 0; i < EnvironmentLight.textureOpWidth; i++) {
				output[i] = this.hashNoise(verticalPos, EnvironmentLight.horizontalTable[i]) % 4096;
			}
		}
		return output;
	}
}
