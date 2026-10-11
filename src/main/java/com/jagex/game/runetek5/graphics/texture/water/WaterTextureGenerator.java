package com.jagex.game.runetek5.graphics.texture.water;

import com.jagex.game.runetek5.EnvironmentContext;
import java.util.Random;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.utils.Utils;

@OriginalClass("client!jp")
public abstract class WaterTextureGenerator {

	@OriginalMember(owner = "client!jp", name = "b", descriptor = "[S")
	private short[] octaveWeights;

	@OriginalMember(owner = "client!jp", name = "g", descriptor = "I")
	private int scaleZ = 4;

	@OriginalMember(owner = "client!jp", name = "d", descriptor = "I")
	public int octaveCount = 4;

	@OriginalMember(owner = "client!jp", name = "n", descriptor = "I")
	private int seed = 0;

	@OriginalMember(owner = "client!jp", name = "o", descriptor = "[S")
	private final short[] permutationTable = new short[512];

	@OriginalMember(owner = "client!jp", name = "k", descriptor = "I")
	private int scaleY = 4;

	@OriginalMember(owner = "client!jp", name = "l", descriptor = "I")
	private int scaleX = 4;

	@OriginalMember(owner = "client!jp", name = "<init>", descriptor = "(IIIII)V")
	public WaterTextureGenerator(@OriginalArg(0) int seed, @OriginalArg(1) int factorsOfTwo, @OriginalArg(2) int xFactor, @OriginalArg(3) int yFactor, @OriginalArg(4) int zFactor) {
		this.octaveCount = factorsOfTwo;
		this.scaleY = yFactor;
		this.scaleX = xFactor;
		this.seed = seed;
		this.scaleZ = zFactor;
		this.buildOctaveFrequencies();
		this.shufflePermutations();
	}

	@OriginalMember(owner = "client!jp", name = "a", descriptor = "(I)V")
	private void shufflePermutations() {
		@Pc(12) Random random = new Random((long) this.seed);
		for (@Pc(14) int index = 0; index < 255; index++) {
			this.permutationTable[index] = (short) index;
		}
		for (@Pc(28) int i = 0; i < 255; i++) {
			@Pc(35) int last = 255 - i;
			@Pc(40) int pick = Utils.randomInt(last, random);
			@Pc(45) short picked = this.permutationTable[pick];
			this.permutationTable[pick] = this.permutationTable[last];
			this.permutationTable[last] = this.permutationTable[last + 256] = picked;
		}
	}

	@OriginalMember(owner = "client!jp", name = "b", descriptor = "(I)V")
	private void buildOctaveFrequencies() {
		this.octaveWeights = new short[this.octaveCount];
		for (@Pc(12) int octave = 0; octave < this.octaveCount; octave++) {
			this.octaveWeights[octave] = (short) Math.pow(2.0D, (double) octave);
		}
	}

	@OriginalMember(owner = "client!jp", name = "c", descriptor = "(I)V")
	public abstract void emitTexel();

	@OriginalMember(owner = "client!jp", name = "a", descriptor = "(IIII)V")
	public final void renderTexels() {
		@Pc(8) int[] xCoords = new int[128];
		@Pc(11) int[] yCoords = new int[128];
		for (@Pc(13) int i = 0; i < 128; i++) {
			xCoords[i] = (i << 12) / 128;
		}
		@Pc(30) int[] zCoords = new int[16];
		for (@Pc(32) int j = 0; j < 128; j++) {
			yCoords[j] = (j << 12) / 128;
		}
		for (@Pc(52) int k = 0; k < 16; k++) {
			zCoords[k] = (k << 12) / 16;
		}
		this.reset();
		for (@Pc(71) int m = 0; m < 16; m++) {
			for (@Pc(75) int y = 0; y < 128; y++) {
				for (@Pc(79) int x = 0; x < 128; x++) {
					for (@Pc(83) int octave = 0; octave < this.octaveCount; octave++) {
						@Pc(92) int frequency = this.octaveWeights[octave] << 12;
						@Pc(100) int sampleZ = frequency * zCoords[m] >> 12;
						@Pc(107) int wrapZ = this.scaleZ * frequency >> 12;
						@Pc(114) int wrapY = this.scaleY * frequency >> 12;
						@Pc(122) int sampleX = xCoords[x] * frequency >> 12;
						@Pc(129) int wrapX = frequency * this.scaleX >> 12;
						@Pc(137) int sampleY = frequency * yCoords[y] >> 12;
						@Pc(142) int scaledX = sampleX * this.scaleX;
						@Pc(147) int scaledY = sampleY * this.scaleY;
						@Pc(152) int scaledZ = sampleZ * this.scaleZ;
						@Pc(156) int gridX = scaledX >> 12;
						@Pc(160) int gridX1 = gridX + 1;
						@Pc(164) int gridX0 = gridX & 0xFF;
						@Pc(168) int gridY = scaledY >> 12;
						@Pc(172) int gridY1 = gridY + 1;
						@Pc(176) int gridY0 = gridY & 0xFF;
						@Pc(180) int gridZ0 = scaledZ >> 12;
						@Pc(184) int gridZ1 = gridZ0 + 1;
						@Pc(188) int fracX = scaledX & 0xFFF;
						@Pc(192) int fracY = scaledY & 0xFFF;
						if (wrapY <= gridY1) {
							gridY1 = 0;
						} else {
							gridY1 &= 0xFF;
						}
						if (gridX1 >= wrapX) {
							gridX1 = 0;
						} else {
							gridX1 &= 0xFF;
						}
						gridZ0 &= 0xFF;
						if (wrapZ > gridZ1) {
							gridZ1 &= 0xFF;
						} else {
							gridZ1 = 0;
						}
						int fracZ = scaledZ & 0xFFF;
						@Pc(241) int farY = fracY - 4096;
						@Pc(246) short hashZ1 = this.permutationTable[gridZ1];
						@Pc(250) int farX = fracX - 4096;
						@Pc(254) int fadeZ = EnvironmentContext.FADE_TABLE[fracZ];
						@Pc(258) int fadeX = EnvironmentContext.FADE_TABLE[fracX];
						@Pc(262) int fadeY = EnvironmentContext.FADE_TABLE[fracY];
						@Pc(266) int farZ = fracZ - 4096;
						@Pc(271) short hashZ0 = this.permutationTable[gridZ0];
						@Pc(278) short hashY1Z0 = this.permutationTable[hashZ0 + gridY1];
						@Pc(285) short hashY0Z1 = this.permutationTable[hashZ1 + gridY0];
						@Pc(293) short hashY1Z1 = this.permutationTable[gridY1 + hashZ1];
						@Pc(300) short hashY0Z0 = this.permutationTable[hashZ0 + gridY0];
						@Pc(313) int grad0 = WaterTextureGenerator.gradient(this.permutationTable[gridX0 + hashY0Z0], fracZ, fracY, fracX);
						@Pc(325) int grad1 = WaterTextureGenerator.gradient(this.permutationTable[gridX1 + hashY0Z0], fracZ, fracY, farX);
						@Pc(336) int edgeY0Z0 = grad0 + ((grad1 - grad0) * fadeX >> 12);
						@Pc(348) int grad2 = WaterTextureGenerator.gradient(this.permutationTable[hashY1Z0 + gridX0], fracZ, farY, fracX);
						@Pc(360) int grad3 = WaterTextureGenerator.gradient(this.permutationTable[gridX1 + hashY1Z0], fracZ, farY, farX);
						@Pc(371) int edgeY1Z0 = grad2 + ((grad3 - grad2) * fadeX >> 12);
						@Pc(383) int grad4 = WaterTextureGenerator.gradient(this.permutationTable[hashY0Z1 + gridX0], farZ, fracY, fracX);
						@Pc(394) int faceZ0 = ((edgeY1Z0 - edgeY0Z0) * fadeY >> 12) + edgeY0Z0;
						@Pc(406) int grad5 = WaterTextureGenerator.gradient(this.permutationTable[hashY0Z1 + gridX1], farZ, fracY, farX);
						@Pc(416) int edgeY0Z1 = grad4 + (fadeX * (grad5 - grad4) >> 12);
						@Pc(429) int grad6 = WaterTextureGenerator.gradient(this.permutationTable[gridX0 + hashY1Z1], farZ, farY, fracX);
						@Pc(442) int grad7 = WaterTextureGenerator.gradient(this.permutationTable[gridX1 + hashY1Z1], farZ, farY, farX);
						@Pc(453) int edgeY1Z1 = ((grad7 - grad6) * fadeX >> 12) + grad6;
						@Pc(463) int faceZ1 = ((edgeY1Z1 - edgeY0Z1) * fadeY >> 12) + edgeY0Z1;
						this.accumulateOctave(faceZ0 + ((faceZ1 - faceZ0) * fadeZ >> 12), octave);
					}
					this.emitTexel();
				}
			}
		}
	}

	@OriginalMember(owner = "client!jp", name = "a", descriptor = "(Z)V")
	public abstract void reset();

	@OriginalMember(owner = "client!jp", name = "a", descriptor = "(III)V")
	public abstract void accumulateOctave(@OriginalArg(0) int noise, @OriginalArg(1) int octave);
	@OriginalMember(owner = "client!cda", name = "a", descriptor = "(ZIIII)I")
	public static int gradient(@OriginalArg(1) int hash, @OriginalArg(2) int z, @OriginalArg(3) int y, @OriginalArg(4) int x) {
		@Pc(7) int h = hash & 0xF;
		@Pc(21) int u = h < 8 ? x : y;
		@Pc(40) int v = h < 4 ? y : h == 12 || h == 14 ? x : z;
		return ((h & 0x1) == 0 ? u : -u) + ((h & 0x2) == 0 ? v : -v);
	}
	@OriginalMember(owner = "client!tu", name = "a", descriptor = "(II)I")
	public static int fade(@OriginalArg(1) int t) {
		@Pc(18) int t3 = t * (t * t >> 12) >> 12;
		@Pc(24) int sixTMinus15 = t * 6 - 61440;
		@Pc(33) int poly = (sixTMinus15 * t >> 12) + 40960;
		return poly * t3 >> 12;
	}
}
