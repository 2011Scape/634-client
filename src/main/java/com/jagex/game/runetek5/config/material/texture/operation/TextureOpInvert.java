package com.jagex.game.runetek5.config.material.texture.operation;

import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.graphics.EnvironmentLight;
import com.jagex.game.runetek5.config.material.texture.TextureOperation;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!wi")
public final class TextureOpInvert extends TextureOperation {

	@OriginalMember(owner = "client!wi", name = "<init>", descriptor = "()V")
	public TextureOpInvert() {
		super(1, false);
	}

	@OriginalMember(owner = "client!wi", name = "d", descriptor = "(II)[[I")
	@Override
	public int[][] getColorOutput(@OriginalArg(0) int y) {
		@Pc(16) int[][] output = super.colorImageCache.get(y);
		if (super.colorImageCache.dirty) {
			@Pc(26) int[][] input = this.getChildColorOutput(y, 0);
			@Pc(30) int[] inputRed = input[0];
			@Pc(34) int[] inputGreen = input[1];
			@Pc(38) int[] inputBlue = input[2];
			@Pc(42) int[] outputRed = output[0];
			@Pc(46) int[] outputGreen = output[1];
			@Pc(50) int[] outputBlue = output[2];
			for (@Pc(52) int i = 0; i < EnvironmentLight.textureOpWidth; i++) {
				outputRed[i] = 4096 - inputRed[i];
				outputGreen[i] = 4096 - inputGreen[i];
				outputBlue[i] = 4096 - inputBlue[i];
			}
		}
		return output;
	}

	@OriginalMember(owner = "client!wi", name = "c", descriptor = "(II)[I")
	@Override
	public int[] monochromeOutput(@OriginalArg(0) int y) {
		@Pc(11) int[] output = super.monochromeImageCache.get(y);
		if (super.monochromeImageCache.dirty) {
			@Pc(29) int[] input = this.getChildMonochromeOutput(y, 0);
			for (@Pc(31) int i = 0; i < EnvironmentLight.textureOpWidth; i++) {
				output[i] = 4096 - input[i];
			}
		}
		return output;
	}

	@OriginalMember(owner = "client!wi", name = "a", descriptor = "(Lclient!cea;II)V")
	@Override
	public void decode(@OriginalArg(0) Packet packet, @OriginalArg(1) int opcode) {
		if (opcode == 0) {
			super.monochrome = packet.readUByte() == 1;
		}
	}
}
