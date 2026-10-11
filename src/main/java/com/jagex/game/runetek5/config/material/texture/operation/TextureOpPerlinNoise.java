package com.jagex.game.runetek5.config.material.texture.operation;

import com.jagex.game.runetek5.EnvironmentContext;
import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.graphics.EnvironmentLight;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.config.material.texture.TextureOperation;
import com.jagex.game.runetek5.config.material.texture.TextureRaster;
import com.jagex.game.runetek5.utils.ArrayUtils;

@OriginalClass("client!jq")
public final class TextureOpPerlinNoise extends TextureOperation {

	@OriginalMember(owner = "client!jq", name = "T", descriptor = "[S")
	private short[] stepMultipliers;

	@OriginalMember(owner = "client!jq", name = "W", descriptor = "[S")
	private short[] amplitudeMultipliers;

	@OriginalMember(owner = "client!jq", name = "J", descriptor = "I")
	public int noiseAmplitude = 1638;

	@OriginalMember(owner = "client!jq", name = "M", descriptor = "[B")
	private byte[] noiseArray = new byte[512];

	@OriginalMember(owner = "client!jq", name = "U", descriptor = "Z")
	public boolean useFogEffect = true;

	@OriginalMember(owner = "client!jq", name = "K", descriptor = "I")
	public int numNoiseSteps = 4;

	@OriginalMember(owner = "client!jq", name = "Q", descriptor = "I")
	public int randomSeed = 0;

	@OriginalMember(owner = "client!jq", name = "Y", descriptor = "I")
	public int texCoordScaleY = 4;

	@OriginalMember(owner = "client!jq", name = "S", descriptor = "I")
	public int texCoordScaleX = 4;

	@OriginalMember(owner = "client!jq", name = "<init>", descriptor = "()V")
	public TextureOpPerlinNoise() {
		super(0, true);
	}

	@OriginalMember(owner = "client!jq", name = "a", descriptor = "(Lclient!cea;II)V")
	@Override
	public void decode(@OriginalArg(0) Packet packet, @OriginalArg(1) int opcode) {
		if (opcode == 0) {
			this.useFogEffect = packet.readUByte() == 1;
		} else if (opcode == 1) {
			this.numNoiseSteps = packet.readUByte();
		} else if (opcode == 2) {
			this.noiseAmplitude = packet.readShort();
			if (this.noiseAmplitude < 0) {
				this.stepMultipliers = new short[this.numNoiseSteps];
				for (@Pc(70) int i = 0; i < this.numNoiseSteps; i++) {
					this.stepMultipliers[i] = (short) packet.readShort();
				}
			}
		} else if (opcode == 3) {
			this.texCoordScaleX = this.texCoordScaleY = packet.readUByte();
		} else if (opcode == 4) {
			this.randomSeed = packet.readUByte();
		} else if (opcode == 5) {
			this.texCoordScaleX = packet.readUByte();
		} else if (opcode == 6) {
			this.texCoordScaleY = packet.readUByte();
		}
	}

	@OriginalMember(owner = "client!jq", name = "c", descriptor = "(II)[I")
	@Override
	public int[] monochromeOutput(@OriginalArg(0) int y) {
		@Pc(11) int[] output = super.monochromeImageCache.get(y);
		if (super.monochromeImageCache.dirty) {
			this.computeNoiseRow(y, output);
		}
		return output;
	}

	@OriginalMember(owner = "client!jq", name = "e", descriptor = "(I)V")
	@Override
	public void postDecode() {
		this.noiseArray = ArrayUtils.getPermutationTable(this.randomSeed);
		this.computeMultipliers();
		for (@Pc(19) int i = this.numNoiseSteps - 1; i >= 1; i--) {
			@Pc(31) short stepMultiplier = this.stepMultipliers[i];
			if (stepMultiplier > 8) {
				return;
			}
			if (stepMultiplier < -8) {
				return;
			}
			this.numNoiseSteps--;
		}
	}

	@OriginalMember(owner = "client!jq", name = "b", descriptor = "(B)V")
	private void computeMultipliers() {
		@Pc(27) int i;
		if (this.noiseAmplitude > 0) {
			this.amplitudeMultipliers = new short[this.numNoiseSteps];
			this.stepMultipliers = new short[this.numNoiseSteps];
			for (i = 0; i < this.numNoiseSteps; i++) {
				this.stepMultipliers[i] = (short) (Math.pow((double) ((float) this.noiseAmplitude / 4096.0F), (double) i) * 4096.0D);
				this.amplitudeMultipliers[i] = (short) Math.pow(2.0D, (double) i);
			}
		} else if (this.stepMultipliers != null && this.numNoiseSteps == this.stepMultipliers.length) {
			this.amplitudeMultipliers = new short[this.numNoiseSteps];
			for (i = 0; i < this.numNoiseSteps; i++) {
				this.amplitudeMultipliers[i] = (short) Math.pow(2.0D, (double) i);
			}
		}
	}

	@OriginalMember(owner = "client!jq", name = "a", descriptor = "(I[IB)V")
	public void computeNoiseRow(@OriginalArg(0) int y, @OriginalArg(1) int[] pixels) {
		@Pc(12) int scaledY = TextureRaster.verticalTable[y] * this.texCoordScaleY;
		@Pc(112) int scaledX;
		@Pc(126) int noiseValue;
		@Pc(42) int octaveScale;
		@Pc(27) short stepMultiplier;
		@Pc(103) int x;
		@Pc(48) int local48;
		@Pc(55) int rangeX;
		@Pc(62) int rangeY;
		@Pc(101) int lerpY;
		@Pc(66) int gridY;
		@Pc(70) int nextGridY;
		@Pc(97) int noiseRowA;
		@Pc(88) int noiseRowB;
		if (this.numNoiseSteps == 1) {
			octaveScale = this.amplitudeMultipliers[0] << 12;
			stepMultiplier = this.stepMultipliers[0];
			int octaveY = octaveScale * scaledY >> 12;
			rangeX = octaveScale * this.texCoordScaleX >> 12;
			rangeY = this.texCoordScaleY * octaveScale >> 12;
			gridY = octaveY >> 12;
			nextGridY = gridY + 1;
			@Pc(376) int fracY = octaveY & 0xFFF;
			if (nextGridY >= rangeY) {
				nextGridY = 0;
			}
			noiseRowB = this.noiseArray[nextGridY & 0xFF] & 0xFF;
			lerpY = EnvironmentContext.FADE_TABLE[fracY];
			noiseRowA = this.noiseArray[gridY & 0xFF] & 0xFF;
			if (this.useFogEffect) {
				for (x = 0; x < EnvironmentLight.textureOpWidth; x++) {
					scaledX = this.texCoordScaleX * EnvironmentLight.horizontalTable[x];
					noiseValue = this.computeNoise(noiseRowB, fracY, rangeX, scaledX * octaveScale >> 12, lerpY, noiseRowA);
					@Pc(475) int weightedNoise = stepMultiplier * noiseValue >> 12;
					pixels[x] = (weightedNoise >> 1) + 2048;
				}
			} else {
				for (x = 0; x < EnvironmentLight.textureOpWidth; x++) {
					scaledX = this.texCoordScaleX * EnvironmentLight.horizontalTable[x];
					noiseValue = this.computeNoise(noiseRowB, fracY, rangeX, octaveScale * scaledX >> 12, lerpY, noiseRowA);
					pixels[x] = noiseValue * stepMultiplier >> 12;
				}
			}
			return;
		}
		stepMultiplier = this.stepMultipliers[0];
		if (stepMultiplier > 8 || stepMultiplier < -8) {
			octaveScale = this.amplitudeMultipliers[0] << 12;
			int octaveY = octaveScale * scaledY >> 12;
			rangeX = this.texCoordScaleX * octaveScale >> 12;
			rangeY = this.texCoordScaleY * octaveScale >> 12;
			gridY = octaveY >> 12;
			nextGridY = gridY + 1;
			if (nextGridY >= rangeY) {
				nextGridY = 0;
			}
			octaveY &= 0xFFF;
			noiseRowB = this.noiseArray[nextGridY & 0xFF] & 0xFF;
			noiseRowA = this.noiseArray[gridY & 0xFF] & 0xFF;
			lerpY = EnvironmentContext.FADE_TABLE[octaveY];
			for (x = 0; x < EnvironmentLight.textureOpWidth; x++) {
				scaledX = EnvironmentLight.horizontalTable[x] * this.texCoordScaleX;
				noiseValue = this.computeNoise(noiseRowB, octaveY, rangeX, octaveScale * scaledX >> 12, lerpY, noiseRowA);
				pixels[x] = stepMultiplier * noiseValue >> 12;
			}
		}
		for (@Pc(140) int i = 1; i < this.numNoiseSteps; i++) {
			stepMultiplier = this.stepMultipliers[i];
			if (stepMultiplier > 8 || stepMultiplier < -8) {
				octaveScale = this.amplitudeMultipliers[i] << 12;
				int octaveY = octaveScale * scaledY >> 12;
				rangeX = this.texCoordScaleX * octaveScale >> 12;
				rangeY = this.texCoordScaleY * octaveScale >> 12;
				gridY = octaveY >> 12;
				nextGridY = gridY + 1;
				octaveY &= 0xFFF;
				if (nextGridY >= rangeY) {
					nextGridY = 0;
				}
				noiseRowA = this.noiseArray[gridY & 0xFF] & 0xFF;
				lerpY = EnvironmentContext.FADE_TABLE[octaveY];
				noiseRowB = this.noiseArray[nextGridY & 0xFF] & 0xFF;
				if (this.useFogEffect && this.numNoiseSteps - 1 == i) {
					for (x = 0; x < EnvironmentLight.textureOpWidth; x++) {
						scaledX = EnvironmentLight.horizontalTable[x] * this.texCoordScaleX;
						noiseValue = this.computeNoise(noiseRowB, octaveY, rangeX, scaledX * octaveScale >> 12, lerpY, noiseRowA);
						noiseValue = pixels[x] + (noiseValue * stepMultiplier >> 12);
						pixels[x] = (noiseValue >> 1) + 2048;
					}
				} else {
					for (x = 0; x < EnvironmentLight.textureOpWidth; x++) {
						scaledX = this.texCoordScaleX * EnvironmentLight.horizontalTable[x];
						noiseValue = this.computeNoise(noiseRowB, octaveY, rangeX, scaledX * octaveScale >> 12, lerpY, noiseRowA);
						pixels[x] += stepMultiplier * noiseValue >> 12;
					}
				}
			}
		}
	}

	@OriginalMember(owner = "client!jq", name = "a", descriptor = "(IIIIIZI)I")
	private int computeNoise(@OriginalArg(0) int noiseRowB, @OriginalArg(1) int fracY, @OriginalArg(2) int rangeX, @OriginalArg(3) int coordX, @OriginalArg(4) int lerpY, @OriginalArg(6) int noiseRowA) {
		@Pc(9) int cellX = coordX >> 12;
		@Pc(13) int nextCellX = cellX + 1;
		@Pc(17) int cellIndexX = cellX & 0xFF;
		@Pc(21) int fracX = coordX & 0xFFF;
		if (nextCellX >= rangeX) {
			nextCellX = 0;
		}
		nextCellX &= 0xFF;
		@Pc(38) int negFracX = fracX - 4096;
		@Pc(42) int negFracY = fracY - 4096;
		@Pc(51) int gradientType = this.noiseArray[noiseRowA + cellIndexX] & 0x3;
		@Pc(55) int lerpX = EnvironmentContext.FADE_TABLE[fracX];
		@Pc(73) int gradientA;
		if (gradientType > 1) {
			gradientA = gradientType == 2 ? fracX - fracY : -fracY + -fracX;
		} else {
			gradientA = gradientType == 0 ? fracX + fracY : fracY - fracX;
		}
		gradientType = this.noiseArray[noiseRowA + nextCellX] & 0x3;
		@Pc(113) int gradientB;
		if (gradientType <= 1) {
			gradientB = gradientType == 0 ? negFracX + fracY : fracY + -negFracX;
		} else {
			gradientB = gradientType == 2 ? negFracX - fracY : -fracY + -negFracX;
		}
		gradientType = this.noiseArray[noiseRowB + cellIndexX] & 0x3;
		@Pc(155) int lerpRowA = gradientA + (lerpX * (gradientB - gradientA) >> 12);
		if (gradientType > 1) {
			gradientA = gradientType == 2 ? fracX - negFracY : -fracX - negFracY;
		} else {
			gradientA = gradientType == 0 ? fracX + negFracY : -fracX + negFracY;
		}
		gradientType = this.noiseArray[noiseRowB + nextCellX] & 0x3;
		if (gradientType > 1) {
			gradientB = gradientType == 2 ? negFracX - negFracY : -negFracX - negFracY;
		} else {
			gradientB = gradientType == 0 ? negFracX + negFracY : negFracY + -negFracX;
		}
		@Pc(239) int lerpRowB = ((gradientB - gradientA) * lerpX >> 12) + gradientA;
		return lerpRowA + (lerpY * (lerpRowB - lerpRowA) >> 12);
	}
	@OriginalMember(owner = "client!al", name = "a", descriptor = "(BIIIFIZI)[I")
	public static int[] generateNoiseRow(@OriginalArg(4) float amplitude) {
		@Pc(6) int[] noiseRow = new int[2048];
		@Pc(10) TextureOpPerlinNoise textureOp34 = new TextureOpPerlinNoise();
		textureOp34.noiseAmplitude = (int) (amplitude * 4096.0F);
		textureOp34.texCoordScaleY = 8;
		textureOp34.numNoiseSteps = 4;
		textureOp34.useFogEffect = true;
		textureOp34.texCoordScaleX = 8;
		textureOp34.randomSeed = 35;
		textureOp34.postDecode();
		TextureRaster.setDimensions(1, 2048);
		textureOp34.computeNoiseRow(0, noiseRow);
		return noiseRow;
	}
}
