package com.jagex.game.runetek5.graphics.texture.water;

import java.util.Random;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.ClientState;

@OriginalClass("client!la")
public final class PerlinNoiseGenerator extends NoiseGenerator {

	@OriginalMember(owner = "client!la", name = "i", descriptor = "[I")
	private final int[] permutations = new int[512];

	@OriginalMember(owner = "client!la", name = "<init>", descriptor = "(I)V")
	public PerlinNoiseGenerator(@OriginalArg(0) int seed) {
		@Pc(13) Random random = new Random((long) seed);
		for (@Pc(15) int i = 0; i < 256; i++) {
			this.permutations[i] = this.permutations[i + 256] = i;
		}
		for (@Pc(37) int j = 0; j < 256; j++) {
			@Pc(44) int randomIndex = random.nextInt() & 0xFF;
			@Pc(49) int swap = this.permutations[randomIndex];
			this.permutations[randomIndex] = this.permutations[randomIndex + 256] = this.permutations[j];
			this.permutations[j] = this.permutations[j + 256] = swap;
		}
	}

	@OriginalMember(owner = "client!la", name = "a", descriptor = "(IIFFIFIFII[F)V")
	@Override
	public void generate(@OriginalArg(2) float scaleX, @OriginalArg(3) float scaleZ, @OriginalArg(4) int offset, @OriginalArg(5) float amplitude, @OriginalArg(7) float scaleY, @OriginalArg(9) int layer, @OriginalArg(10) float[] output) {
		@Pc(13) int xRange = (int) (scaleX * (float) 128 - 1.0F);
		@Pc(17) int xMask = xRange & 0xFF;
		@Pc(25) int yRange = (int) ((float) 128 * scaleY - 1.0F);
		@Pc(29) int yMask = yRange & 0xFF;
		@Pc(37) int zRange = (int) ((float) 16 * scaleZ - 1.0F);
		@Pc(41) int zMask = zRange & 0xFF;
		@Pc(46) float layerZ = (float) layer * scaleZ;
		@Pc(49) int z0 = (int) layerZ;
		@Pc(53) int z1 = z0 + 1;
		@Pc(58) float zFrac = layerZ - (float) z0;
		@Pc(63) float zInvFrac = (float) 1 - zFrac;
		@Pc(67) float zFade = PerlinNoiseGenerator.fade(zFrac);
		@Pc(71) int z1Wrapped = z1 & zMask;
		@Pc(75) int z0Wrapped = z0 & zMask;
		@Pc(80) int hash0 = this.permutations[z0Wrapped];
		@Pc(85) int hash1 = this.permutations[z1Wrapped];
		for (@Pc(87) int i = 0; i < 128; i++) {
			@Pc(94) float yPos = scaleY * (float) i;
			@Pc(97) int y0 = (int) yPos;
			@Pc(101) int y1 = y0 + 1;
			@Pc(107) float yFrac = (float) -y0 + yPos;
			@Pc(112) float yInvFrac = 1.0F - yFrac;
			@Pc(118) float yFade = PerlinNoiseGenerator.fade(yFrac);
			@Pc(122) int y0Wrapped = y0 & yMask;
			@Pc(126) int y1Wrapped = y1 & yMask;
			@Pc(133) int hash00 = this.permutations[hash0 + y0Wrapped];
			@Pc(140) int hash01 = this.permutations[hash0 + y1Wrapped];
			@Pc(147) int hash10 = this.permutations[y0Wrapped + hash1];
			@Pc(155) int hash11 = this.permutations[y1Wrapped + hash1];
			for (@Pc(157) int j = 0; j < 128; j++) {
				@Pc(164) float xPos = scaleX * (float) j;
				@Pc(167) int x0 = (int) xPos;
				@Pc(171) int x1 = x0 + 1;
				@Pc(176) float xFrac = xPos - (float) x0;
				@Pc(180) float xInvFrac = 1.0F - xFrac;
				@Pc(184) int x0Wrapped = x0 & xMask;
				@Pc(188) int x1Wrapped = x1 & xMask;
				@Pc(194) float xFade = PerlinNoiseGenerator.fade(xFrac);
				output[offset++] = amplitude * PerlinNoiseGenerator.lerp(PerlinNoiseGenerator.lerp(PerlinNoiseGenerator.lerp(PerlinNoiseGenerator.gradientDot(yInvFrac, this.permutations[hash00 + x0Wrapped] & 0x7, zInvFrac, xInvFrac), PerlinNoiseGenerator.gradientDot(yInvFrac, this.permutations[x1Wrapped + hash00] & 0x7, zInvFrac, xFrac), xFade), PerlinNoiseGenerator.lerp(PerlinNoiseGenerator.gradientDot(yFrac, this.permutations[x0Wrapped + hash01] & 0x7, zInvFrac, xInvFrac), PerlinNoiseGenerator.gradientDot(yFrac, this.permutations[hash01 + x1Wrapped] & 0x7, zInvFrac, xFrac), xFade), yFade), PerlinNoiseGenerator.lerp(PerlinNoiseGenerator.lerp(PerlinNoiseGenerator.gradientDot(yInvFrac, this.permutations[hash10 + x0Wrapped] & 0x7, zFrac, xInvFrac), PerlinNoiseGenerator.gradientDot(yInvFrac, this.permutations[hash10 + x1Wrapped] & 0x7, zFrac, xFrac), xFade), PerlinNoiseGenerator.lerp(PerlinNoiseGenerator.gradientDot(yFrac, this.permutations[hash11 + x0Wrapped] & 0x7, zFrac, xInvFrac), PerlinNoiseGenerator.gradientDot(yFrac, this.permutations[hash11 + x1Wrapped] & 0x7, zFrac, xFrac), xFade), yFade), zFade);
			}
		}
	}
	@OriginalMember(owner = "client!ci", name = "a", descriptor = "(FZFF)F")
	public static float lerp(@OriginalArg(0) float start, @OriginalArg(2) float end, @OriginalArg(3) float t) {
		return start + (end - start) * t;
	}

	@OriginalMember(owner = "client!eca", name = "a", descriptor = "(IF)F")
	public static float fade(@OriginalArg(1) float t) {
		return (t * (t * 6.0F - 15.0F) + 10.0F) * t * t * t;
	}
	@OriginalMember(owner = "client!vi", name = "a", descriptor = "(FIFIF)F")
	public static float gradientDot(@OriginalArg(0) float y, @OriginalArg(1) int hash, @OriginalArg(2) float z, @OriginalArg(4) float x) {
		@Pc(17) float[] gradient = PerlinNoiseGenerator.PERLIN_GRADIENTS[hash];
		return gradient[1] * y + x * gradient[0] + gradient[2] * z;
	}
	@OriginalMember(owner = "client!dw", name = "b", descriptor = "[[F")
	public static final float[][] PERLIN_GRADIENTS = new float[][] { { -0.333333F, -0.333333F, -0.333333F }, { 0.333333F, -0.333333F, -0.333333F }, { -0.333333F, 0.333333F, -0.333333F }, { 0.333333F, 0.333333F, -0.333333F }, { -0.333333F, -0.333333F, 0.333333F }, { 0.333333F, -0.333333F, 0.333333F }, { -0.333333F, 0.333333F, 0.333333F }, { 0.333333F, 0.333333F, 0.333333F } };
}
