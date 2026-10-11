package com.jagex.game.runetek5.graphics.texture;

import com.jagex.game.runetek5.cache.node.Node;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.graphics.safe.PureJavaTexture;

@OriginalClass("client!mg")
public final class SafeModeTexture2D extends Node {

	@OriginalMember(owner = "client!mg", name = "o", descriptor = "I")
	public int cycle;

	@OriginalMember(owner = "client!mg", name = "r", descriptor = "Z")
	public boolean isAnimated;

	@OriginalMember(owner = "client!mg", name = "s", descriptor = "I")
	public final int materialId;

	@OriginalMember(owner = "client!mg", name = "p", descriptor = "I")
	private final int totalColorCount;

	@OriginalMember(owner = "client!mg", name = "t", descriptor = "[I")
	private int[] colorArgb;

	@OriginalMember(owner = "client!mg", name = "<init>", descriptor = "(II[IZ)V")
	public SafeModeTexture2D(@OriginalArg(0) int materialId, @OriginalArg(1) int totalColorCount, @OriginalArg(2) int[] argbColor, @OriginalArg(3) boolean shouldBlur) {
		this.materialId = materialId;
		this.totalColorCount = totalColorCount;
		this.colorArgb = argbColor;
		if (shouldBlur) {
			@Pc(16) int[] alphaSums = new int[this.totalColorCount];
			@Pc(20) int[] redSums = new int[this.totalColorCount];
			@Pc(24) int[] greenSums = new int[this.totalColorCount];
			@Pc(28) int[] blueSums = new int[this.totalColorCount];
			if (PureJavaTexture.colorsArgb == null || PureJavaTexture.colorsArgb.length != this.colorArgb.length) {
				PureJavaTexture.colorsArgb = new int[this.colorArgb.length];
			}
			@Pc(44) int width = this.totalColorCount;
			@Pc(47) int height = this.totalColorCount;
			@Pc(51) int maxX = width - 1;
			@Pc(55) int maxY = height - 1;
			@Pc(59) int pixelCount = width * height;
			@Pc(62) int leadingIndex = width;
			@Pc(63) int trailingIndex = width;
			@Pc(68) int local68;
			for (@Pc(65) int i = 2; i >= 0; i--) {
				for (int x = maxX; x >= 0; x--) {
					leadingIndex--;
					@Pc(75) int colour = this.colorArgb[leadingIndex];
					alphaSums[x] += colour >> 24 & 0xFF;
					redSums[x] += colour >> 16 & 0xFF;
					greenSums[x] += colour >> 8 & 0xFF;
					blueSums[x] += colour & 0xFF;
				}
				if (leadingIndex == 0) {
					leadingIndex = pixelCount;
				}
			}
			@Pc(129) int outIndex = pixelCount;
			for (@Pc(131) int j = maxY; j >= 0; j--) {
				@Pc(134) int trailingX = 1;
				@Pc(136) int leadingX = 1;
				@Pc(139) int greenSum = 0;
				@Pc(141) int blueSum = 0;
				@Pc(143) int redSum = 0;
				@Pc(144) int alphaSum = 0;
				for (int tap = 2; tap >= 0; tap--) {
					leadingX--;
					alphaSum += alphaSums[leadingX];
					redSum += redSums[leadingX];
					greenSum += greenSums[leadingX];
					blueSum += blueSums[leadingX];
					if (leadingX == 0) {
						leadingX = width;
					}
				}
				for (int x = maxX; x >= 0; x--) {
					leadingX--;
					trailingX--;
					@Pc(188) int alpha = alphaSum / 9;
					@Pc(192) int red = redSum / 9;
					@Pc(196) int green = greenSum / 9;
					@Pc(200) int blue = blueSum / 9;
					outIndex--;
					PureJavaTexture.colorsArgb[outIndex] = alpha << 24 | red << 16 | green << 8 | blue;
					alphaSum += alphaSums[leadingX] - alphaSums[trailingX];
					redSum += redSums[leadingX] - redSums[trailingX];
					blueSum += blueSums[leadingX] - blueSums[trailingX];
					greenSum += greenSums[leadingX] - greenSums[trailingX];
					if (leadingX == 0) {
						leadingX = width;
					}
					if (trailingX == 0) {
						trailingX = width;
					}
				}
				for (int x = maxX; x >= 0; x--) {
					leadingIndex--;
					@Pc(277) int enteringColour = this.colorArgb[leadingIndex];
					trailingIndex--;
					@Pc(283) int leavingColour = this.colorArgb[trailingIndex];
					alphaSums[x] += (enteringColour >> 24 & 0xFF) - (leavingColour >> 24 & 0xFF);
					redSums[x] += (enteringColour >> 16 & 0xFF) - (leavingColour >> 16 & 0xFF);
					greenSums[x] += (enteringColour >> 8 & 0xFF) - (leavingColour >> 8 & 0xFF);
					blueSums[x] += (enteringColour & 0xFF) - (leavingColour & 0xFF);
				}
				if (leadingIndex == 0) {
					leadingIndex = pixelCount;
				}
				if (trailingIndex == 0) {
					trailingIndex = pixelCount;
				}
			}
			@Pc(364) int[] previousColours = this.colorArgb;
			this.colorArgb = PureJavaTexture.colorsArgb;
			PureJavaTexture.colorsArgb = previousColours;
		}
	}

	@OriginalMember(owner = "client!mg", name = "c", descriptor = "(II)V")
	public void animate(@OriginalArg(0) int offsetX, @OriginalArg(1) int offsetY) {
		if (offsetX == 0 && offsetY == 0) {
			return;
		}
		if (PureJavaTexture.colorsArgb == null || PureJavaTexture.colorsArgb.length != this.colorArgb.length) {
			PureJavaTexture.colorsArgb = new int[this.colorArgb.length];
		}
		@Pc(21) int length = this.colorArgb.length;
		@Pc(28) int maskX = this.totalColorCount - 1;
		@Pc(33) int rowOffset = this.totalColorCount * offsetY;
		@Pc(37) int mask = length - 1;
		for (@Pc(39) int i = 0; i < length; i += this.totalColorCount) {
			@Pc(46) int srcRowStart = i + rowOffset & mask;
			for (@Pc(48) int j = 0; j < this.totalColorCount; j++) {
				@Pc(53) int dstIndex = i + j;
				@Pc(61) int srcIndex = srcRowStart + (j + offsetX & maskX);
				PureJavaTexture.colorsArgb[dstIndex] = this.colorArgb[srcIndex];
			}
		}
		@Pc(84) int[] previousColours = this.colorArgb;
		this.colorArgb = PureJavaTexture.colorsArgb;
		PureJavaTexture.colorsArgb = previousColours;
	}

	@OriginalMember(owner = "client!mg", name = "b", descriptor = "()[I")
	public int[] getArgbColor() {
		return this.colorArgb;
	}
}
