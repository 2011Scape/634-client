package com.jagex.game.runetek5.config.material.texture;

import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.core.util.Arrays;
import com.jagex.game.runetek5.graphics.EnvironmentLight;
import com.jagex.game.runetek5.config.material.texture.TextureOperation;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!jia")
public final class TextureOpFlip extends TextureOperation {

	@OriginalMember(owner = "client!jia", name = "I", descriptor = "Z")
	private boolean mirrorHorizontally = true;

	@OriginalMember(owner = "client!jia", name = "P", descriptor = "Z")
	private boolean mirrorVertically = true;

	@OriginalMember(owner = "client!jia", name = "<init>", descriptor = "()V")
	public TextureOpFlip() {
		super(1, false);
	}

	@OriginalMember(owner = "client!jia", name = "d", descriptor = "(II)[[I")
	@Override
	public int[][] getColorOutput(@OriginalArg(0) int count) {
		@Pc(11) int[][] output = super.colorImageCache.get(count);
		if (super.colorImageCache.dirty) {
			@Pc(29) int[][] input = this.getChildColorOutput(this.mirrorVertically ? TextureOperation.columnEnd - count : count, 0);
			@Pc(33) int[] inputRed = input[0];
			@Pc(37) int[] inputGreen = input[1];
			@Pc(41) int[] inputBlue = input[2];
			@Pc(45) int[] outputRed = output[0];
			@Pc(49) int[] outputGreen = output[1];
			@Pc(53) int[] outputBlue = output[2];
			@Pc(58) int x;
			if (this.mirrorHorizontally) {
				for (x = 0; x < EnvironmentLight.textureOpWidth; x++) {
					outputRed[x] = inputRed[TextureRaster.widthMask - x];
					outputGreen[x] = inputGreen[TextureRaster.widthMask - x];
					outputBlue[x] = inputBlue[TextureRaster.widthMask - x];
				}
			} else {
				for (x = 0; x < EnvironmentLight.textureOpWidth; x++) {
					outputRed[x] = inputRed[x];
					outputGreen[x] = inputGreen[x];
					outputBlue[x] = inputBlue[x];
				}
			}
		}
		return output;
	}

	@OriginalMember(owner = "client!jia", name = "a", descriptor = "(Lclient!cea;II)V")
	@Override
	public void decode(@OriginalArg(0) Packet packet, @OriginalArg(1) int opcode) {
		if (opcode == 0) {
			this.mirrorHorizontally = packet.readUByte() == 1;
		} else if (opcode == 1) {
			this.mirrorVertically = packet.readUByte() == 1;
		} else if (opcode == 2) {
			super.monochrome = packet.readUByte() == 1;
		}
	}

	@OriginalMember(owner = "client!jia", name = "c", descriptor = "(II)[I")
	@Override
	public int[] monochromeOutput(@OriginalArg(0) int row) {
		@Pc(16) int[] output = super.monochromeImageCache.get(row);
		if (super.monochromeImageCache.dirty) {
			@Pc(35) int[] input = this.getChildMonochromeOutput(this.mirrorVertically ? TextureOperation.columnEnd - row : row, 0);
			if (this.mirrorHorizontally) {
				for (@Pc(48) int i = 0; i < EnvironmentLight.textureOpWidth; i++) {
					output[i] = input[TextureRaster.widthMask - i];
				}
			} else {
				Arrays.copy(input, 0, output, 0, EnvironmentLight.textureOpWidth);
			}
		}
		return output;
	}
}
