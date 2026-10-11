package com.jagex.game.runetek5.config.material.texture.operation;

import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.graphics.EnvironmentLight;
import com.jagex.game.runetek5.config.material.texture.TextureOperation;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.config.material.texture.TextureRaster;

@OriginalClass("client!fd")
public final class TextureOpBoxBlur extends TextureOperation {

	@OriginalMember(owner = "client!fd", name = "O", descriptor = "I")
	private int radiusY = 1;

	@OriginalMember(owner = "client!fd", name = "R", descriptor = "I")
	private int radiusX = 1;

	@OriginalMember(owner = "client!fd", name = "<init>", descriptor = "()V")
	public TextureOpBoxBlur() {
		super(1, false);
	}

	@OriginalMember(owner = "client!fd", name = "a", descriptor = "(Lclient!cea;II)V")
	@Override
	public void decode(@OriginalArg(0) Packet packet, @OriginalArg(1) int opcode) {
		if (opcode == 0) {
			this.radiusX = packet.readUByte();
		} else if (opcode == 1) {
			this.radiusY = packet.readUByte();
		} else if (opcode == 2) {
			super.monochrome = packet.readUByte() == 1;
		}
	}

	@OriginalMember(owner = "client!fd", name = "c", descriptor = "(II)[I")
	@Override
	public int[] monochromeOutput(@OriginalArg(0) int row) {
		@Pc(11) int[] output = super.monochromeImageCache.get(row);
		if (super.monochromeImageCache.dirty) {
			@Pc(28) int kernelHeight = this.radiusY + this.radiusY + 1;
			@Pc(32) int verticalWeight = 65536 / kernelHeight;
			@Pc(40) int kernelWidth = this.radiusX + this.radiusX + 1;
			@Pc(44) int horizontalWeight = 65536 / kernelWidth;
			@Pc(47) int[][] blurredRows = new int[kernelHeight][];
			@Pc(68) int local68;
			for (@Pc(53) int i = row - this.radiusY; i <= row + this.radiusY; i++) {
				@Pc(63) int[] input = this.getChildMonochromeOutput(TextureOperation.columnEnd & i, 0);
				@Pc(66) int[] blurredRow = new int[EnvironmentLight.textureOpWidth];
				int rowSum = 0;
				for (@Pc(72) int j = -this.radiusX; j <= this.radiusX; j++) {
					rowSum += input[j & TextureRaster.widthMask];
				}
				@Pc(89) int x = 0;
				while (x < EnvironmentLight.textureOpWidth) {
					blurredRow[x] = horizontalWeight * rowSum >> 16;
					rowSum -= input[x - this.radiusX & TextureRaster.widthMask];
					x++;
					rowSum += input[x + this.radiusX & TextureRaster.widthMask];
				}
				blurredRows[i + this.radiusY - row] = blurredRow;
			}
			for (@Pc(146) int k = 0; k < EnvironmentLight.textureOpWidth; k++) {
				@Pc(150) int sum = 0;
				for (int kernelRow = 0; kernelRow < kernelHeight; kernelRow++) {
					sum += blurredRows[kernelRow][k];
				}
				output[k] = sum * verticalWeight >> 16;
			}
		}
		return output;
	}

	@OriginalMember(owner = "client!fd", name = "d", descriptor = "(II)[[I")
	@Override
	public int[][] getColorOutput(@OriginalArg(0) int count) {
		@Pc(18) int[][] output = super.colorImageCache.get(count);
		if (super.colorImageCache.dirty) {
			@Pc(30) int kernelHeight = this.radiusY + this.radiusY + 1;
			@Pc(34) int verticalWeight = 65536 / kernelHeight;
			@Pc(42) int kernelWidth = this.radiusX + this.radiusX + 1;
			@Pc(46) int horizontalWeight = 65536 / kernelWidth;
			@Pc(49) int[][][] blurredRows = new int[kernelHeight][][];
			@Pc(73) int local73;
			@Pc(75) int local75;
			for (@Pc(55) int i = count - this.radiusY; i <= this.radiusY + count; i++) {
				@Pc(65) int[][] input = this.getChildColorOutput(TextureOperation.columnEnd & i, 0);
				@Pc(69) int[][] blurredRow = new int[3][EnvironmentLight.textureOpWidth];
				@Pc(71) int redSum = 0;
				int greenSum = 0;
				int blueSum = 0;
				@Pc(79) int[] inputRed = input[0];
				@Pc(83) int[] inputGreen = input[1];
				@Pc(87) int[] inputBlue = input[2];
				for (@Pc(91) int j = -this.radiusX; j <= this.radiusX; j++) {
					@Pc(97) int sampleX = TextureRaster.widthMask & j;
					blueSum += inputBlue[sampleX];
					redSum += inputRed[sampleX];
					greenSum += inputGreen[sampleX];
				}
				@Pc(128) int[] blurredRed = blurredRow[0];
				@Pc(132) int[] blurredGreen = blurredRow[1];
				@Pc(136) int[] blurredBlue = blurredRow[2];
				@Pc(138) int x = 0;
				while (x < EnvironmentLight.textureOpWidth) {
					blurredRed[x] = redSum * horizontalWeight >> 16;
					blurredGreen[x] = greenSum * horizontalWeight >> 16;
					blurredBlue[x] = blueSum * horizontalWeight >> 16;
					@Pc(172) int trailingX = TextureRaster.widthMask & x - this.radiusX;
					redSum -= inputRed[trailingX];
					x++;
					greenSum -= inputGreen[trailingX];
					blueSum -= inputBlue[trailingX];
					@Pc(199) int leadingX = x + this.radiusX & TextureRaster.widthMask;
					blueSum += inputBlue[leadingX];
					greenSum += inputGreen[leadingX];
					redSum += inputRed[leadingX];
				}
				blurredRows[i + this.radiusY - count] = blurredRow;
			}
			@Pc(245) int[] outputRed = output[0];
			@Pc(249) int[] outputGreen = output[1];
			@Pc(253) int[] outputBlue = output[2];
			for (int column = 0; column < EnvironmentLight.textureOpWidth; column++) {
				int redSum = 0;
				@Pc(261) int greenSum = 0;
				@Pc(263) int blueSum = 0;
				for (@Pc(265) int k = 0; k < kernelHeight; k++) {
					@Pc(271) int[][] blurredRow = blurredRows[k];
					redSum += blurredRow[0][column];
					greenSum += blurredRow[1][column];
					blueSum += blurredRow[2][column];
				}
				outputRed[column] = redSum * verticalWeight >> 16;
				outputGreen[column] = greenSum * verticalWeight >> 16;
				outputBlue[column] = verticalWeight * blueSum >> 16;
			}
		}
		return output;
	}
}
