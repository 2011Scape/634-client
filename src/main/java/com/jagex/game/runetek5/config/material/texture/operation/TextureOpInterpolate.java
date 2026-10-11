package com.jagex.game.runetek5.config.material.texture.operation;

import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.graphics.EnvironmentLight;
import com.jagex.game.runetek5.config.material.texture.TextureOperation;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!uia")
public final class TextureOpInterpolate extends TextureOperation {

	@OriginalMember(owner = "client!uia", name = "<init>", descriptor = "()V")
	public TextureOpInterpolate() {
		super(3, false);
	}

	@OriginalMember(owner = "client!uia", name = "d", descriptor = "(II)[[I")
	@Override
	public int[][] getColorOutput(@OriginalArg(0) int y) {
		@Pc(16) int[][] output = super.colorImageCache.get(y);
		if (super.colorImageCache.dirty) {
			@Pc(28) int[] blendFactor = this.getChildMonochromeOutput(y, 2);
			@Pc(34) int[][] input0 = this.getChildColorOutput(y, 0);
			@Pc(40) int[][] input1 = this.getChildColorOutput(y, 1);
			@Pc(44) int[] outputRed = output[0];
			@Pc(48) int[] outputGreen = output[1];
			@Pc(52) int[] outputBlue = output[2];
			@Pc(56) int[] input0Red = input0[0];
			@Pc(60) int[] input0Green = input0[1];
			@Pc(64) int[] input0Blue = input0[2];
			@Pc(68) int[] input1Red = input1[0];
			@Pc(72) int[] input1Green = input1[1];
			@Pc(76) int[] input1Blue = input1[2];
			for (@Pc(78) int i = 0; i < EnvironmentLight.textureOpWidth; i++) {
				@Pc(84) int blend = blendFactor[i];
				if (blend == 4096) {
					outputRed[i] = input0Red[i];
					outputGreen[i] = input0Green[i];
					outputBlue[i] = input0Blue[i];
				} else if (blend == 0) {
					outputRed[i] = input1Red[i];
					outputGreen[i] = input1Green[i];
					outputBlue[i] = input1Blue[i];
				} else {
					@Pc(97) int inverseBlend = 4096 - blend;
					outputRed[i] = input1Red[i] * inverseBlend + input0Red[i] * blend >> 12;
					outputGreen[i] = input1Green[i] * inverseBlend + input0Green[i] * blend >> 12;
					outputBlue[i] = input0Blue[i] * blend + input1Blue[i] * inverseBlend >> 12;
				}
			}
		}
		return output;
	}

	@OriginalMember(owner = "client!uia", name = "c", descriptor = "(II)[I")
	@Override
	public int[] monochromeOutput(@OriginalArg(0) int y) {
		@Pc(16) int[] output = super.monochromeImageCache.get(y);
		if (super.monochromeImageCache.dirty) {
			@Pc(28) int[] input0 = this.getChildMonochromeOutput(y, 0);
			@Pc(34) int[] input1 = this.getChildMonochromeOutput(y, 1);
			@Pc(42) int[] blendFactor = this.getChildMonochromeOutput(y, 2);
			for (@Pc(44) int i = 0; i < EnvironmentLight.textureOpWidth; i++) {
				@Pc(50) int blend = blendFactor[i];
				if (blend == 4096) {
					output[i] = input0[i];
				} else if (blend == 0) {
					output[i] = input1[i];
				} else {
					output[i] = blend * input0[i] + input1[i] * (4096 - blend) >> 12;
				}
			}
		}
		return output;
	}

	@OriginalMember(owner = "client!uia", name = "a", descriptor = "(Lclient!cea;II)V")
	@Override
	public void decode(@OriginalArg(0) Packet packet, @OriginalArg(1) int opcode) {
		if (opcode == 0) {
			super.monochrome = packet.readUByte() == 1;
		}
	}
}
