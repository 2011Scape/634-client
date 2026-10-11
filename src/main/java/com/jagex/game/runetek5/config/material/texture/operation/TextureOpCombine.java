package com.jagex.game.runetek5.config.material.texture.operation;

import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.graphics.EnvironmentLight;
import com.jagex.game.runetek5.config.material.texture.TextureOperation;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!pda")
public final class TextureOpCombine extends TextureOperation {

	@OriginalMember(owner = "client!pda", name = "M", descriptor = "I")
	private int blendMode = 6;

	@OriginalMember(owner = "client!pda", name = "<init>", descriptor = "()V")
	public TextureOpCombine() {
		super(2, false);
	}

	@OriginalMember(owner = "client!pda", name = "a", descriptor = "(Lclient!cea;II)V")
	@Override
	public void decode(@OriginalArg(0) Packet packet, @OriginalArg(1) int opcode) {
		if (opcode == 0) {
			this.blendMode = packet.readUByte();
		} else if (opcode == 1) {
			super.monochrome = packet.readUByte() == 1;
		}
	}

	@OriginalMember(owner = "client!pda", name = "d", descriptor = "(II)[[I")
	@Override
	public int[][] getColorOutput(@OriginalArg(0) int row) {
		@Pc(18) int[][] output = super.colorImageCache.get(row);
		if (super.colorImageCache.dirty) {
			@Pc(28) int[][] first = this.getChildColorOutput(row, 0);
			@Pc(34) int[][] second = this.getChildColorOutput(row, 1);
			@Pc(38) int[] outputRed = output[0];
			@Pc(42) int[] outputGreen = output[1];
			@Pc(46) int[] outputBlue = output[2];
			@Pc(50) int[] firstRed = first[0];
			@Pc(54) int[] firstGreen = first[1];
			@Pc(58) int[] firstBlue = first[2];
			@Pc(62) int[] secondRed = second[0];
			@Pc(66) int[] secondGreen = second[1];
			@Pc(70) int[] secondBlue = second[2];
			@Pc(73) int blendMode = this.blendMode;
			if (blendMode == 1) {
				for (blendMode = 0; blendMode < EnvironmentLight.textureOpWidth; blendMode++) {
					outputRed[blendMode] = secondRed[blendMode] + firstRed[blendMode];
					outputGreen[blendMode] = firstGreen[blendMode] + secondGreen[blendMode];
					outputBlue[blendMode] = firstBlue[blendMode] + secondBlue[blendMode];
				}
			} else if (blendMode == 2) {
				for (blendMode = 0; blendMode < EnvironmentLight.textureOpWidth; blendMode++) {
					outputRed[blendMode] = firstRed[blendMode] - secondRed[blendMode];
					outputGreen[blendMode] = firstGreen[blendMode] - secondGreen[blendMode];
					outputBlue[blendMode] = firstBlue[blendMode] - secondBlue[blendMode];
				}
			} else if (blendMode == 3) {
				for (blendMode = 0; blendMode < EnvironmentLight.textureOpWidth; blendMode++) {
					outputRed[blendMode] = secondRed[blendMode] * firstRed[blendMode] >> 12;
					outputGreen[blendMode] = secondGreen[blendMode] * firstGreen[blendMode] >> 12;
					outputBlue[blendMode] = firstBlue[blendMode] * secondBlue[blendMode] >> 12;
				}
			} else {
				@Pc(265) int secondRedValue;
				@Pc(273) int secondGreenValue;
				@Pc(269) int secondBlueValue;
				if (blendMode == 4) {
					for (blendMode = 0; blendMode < EnvironmentLight.textureOpWidth; blendMode++) {
						secondRedValue = secondRed[blendMode];
						secondBlueValue = secondBlue[blendMode];
						secondGreenValue = secondGreen[blendMode];
						outputRed[blendMode] = secondRedValue == 0 ? 4096 : (firstRed[blendMode] << 12) / secondRedValue;
						outputGreen[blendMode] = secondGreenValue == 0 ? 4096 : (firstGreen[blendMode] << 12) / secondGreenValue;
						outputBlue[blendMode] = secondBlueValue == 0 ? 4096 : (firstBlue[blendMode] << 12) / secondBlueValue;
					}
				} else if (blendMode == 5) {
					for (blendMode = 0; blendMode < EnvironmentLight.textureOpWidth; blendMode++) {
						outputRed[blendMode] = 4096 - ((4096 - secondRed[blendMode]) * (4096 - firstRed[blendMode]) >> 12);
						outputGreen[blendMode] = 4096 - ((4096 - secondGreen[blendMode]) * (4096 - firstGreen[blendMode]) >> 12);
						outputBlue[blendMode] = 4096 - ((4096 - secondBlue[blendMode]) * (4096 - firstBlue[blendMode]) >> 12);
					}
				} else if (blendMode == 6) {
					for (blendMode = 0; blendMode < EnvironmentLight.textureOpWidth; blendMode++) {
						secondRedValue = secondRed[blendMode];
						secondBlueValue = secondBlue[blendMode];
						secondGreenValue = secondGreen[blendMode];
						outputRed[blendMode] = secondRedValue >= 2048 ? 4096 - ((4096 - firstRed[blendMode]) * (-secondRedValue + 4096) >> 11) : firstRed[blendMode] * secondRedValue >> 11;
						outputGreen[blendMode] = secondGreenValue >= 2048 ? 4096 - ((4096 - secondGreenValue) * (4096 - firstGreen[blendMode]) >> 11) : secondGreenValue * firstGreen[blendMode] >> 11;
						outputBlue[blendMode] = secondBlueValue < 2048 ? secondBlueValue * firstBlue[blendMode] >> 11 : 4096 - ((4096 - firstBlue[blendMode]) * (-secondBlueValue + 4096) >> 11);
					}
				} else {
					@Pc(525) int firstRedValue;
					@Pc(521) int firstGreenValue;
					@Pc(517) int firstBlueValue;
					if (blendMode == 7) {
						for (blendMode = 0; blendMode < EnvironmentLight.textureOpWidth; blendMode++) {
							firstBlueValue = firstBlue[blendMode];
							firstGreenValue = firstGreen[blendMode];
							firstRedValue = firstRed[blendMode];
							outputRed[blendMode] = firstRedValue == 4096 ? 4096 : (secondRed[blendMode] << 12) / (4096 - firstRedValue);
							outputGreen[blendMode] = firstGreenValue == 4096 ? 4096 : (secondGreen[blendMode] << 12) / (4096 - firstGreenValue);
							outputBlue[blendMode] = firstBlueValue == 4096 ? 4096 : (secondBlue[blendMode] << 12) / (4096 - firstBlueValue);
						}
					} else if (blendMode == 8) {
						for (blendMode = 0; blendMode < EnvironmentLight.textureOpWidth; blendMode++) {
							firstGreenValue = firstGreen[blendMode];
							firstRedValue = firstRed[blendMode];
							firstBlueValue = firstBlue[blendMode];
							outputRed[blendMode] = firstRedValue == 0 ? 0 : 4096 - (4096 - secondRed[blendMode] << 12) / firstRedValue;
							outputGreen[blendMode] = firstGreenValue == 0 ? 0 : 4096 - (4096 - secondGreen[blendMode] << 12) / firstGreenValue;
							outputBlue[blendMode] = firstBlueValue == 0 ? 0 : 4096 - (4096 - secondBlue[blendMode] << 12) / firstBlueValue;
						}
					} else if (blendMode == 9) {
						for (blendMode = 0; blendMode < EnvironmentLight.textureOpWidth; blendMode++) {
							firstGreenValue = firstGreen[blendMode];
							secondGreenValue = secondGreen[blendMode];
							secondBlueValue = secondBlue[blendMode];
							secondRedValue = secondRed[blendMode];
							firstBlueValue = firstBlue[blendMode];
							firstRedValue = firstRed[blendMode];
							outputRed[blendMode] = firstRedValue < secondRedValue ? firstRedValue : secondRedValue;
							outputGreen[blendMode] = firstGreenValue < secondGreenValue ? firstGreenValue : secondGreenValue;
							outputBlue[blendMode] = secondBlueValue > firstBlueValue ? firstBlueValue : secondBlueValue;
						}
					} else if (blendMode == 10) {
						for (blendMode = 0; blendMode < EnvironmentLight.textureOpWidth; blendMode++) {
							firstGreenValue = firstGreen[blendMode];
							firstBlueValue = firstBlue[blendMode];
							secondGreenValue = secondGreen[blendMode];
							secondRedValue = secondRed[blendMode];
							secondBlueValue = secondBlue[blendMode];
							firstRedValue = firstRed[blendMode];
							outputRed[blendMode] = secondRedValue < firstRedValue ? firstRedValue : secondRedValue;
							outputGreen[blendMode] = secondGreenValue >= firstGreenValue ? secondGreenValue : firstGreenValue;
							outputBlue[blendMode] = firstBlueValue <= secondBlueValue ? secondBlueValue : firstBlueValue;
						}
					} else if (blendMode == 11) {
						for (blendMode = 0; blendMode < EnvironmentLight.textureOpWidth; blendMode++) {
							secondBlueValue = secondBlue[blendMode];
							firstGreenValue = firstGreen[blendMode];
							firstBlueValue = firstBlue[blendMode];
							secondRedValue = secondRed[blendMode];
							firstRedValue = firstRed[blendMode];
							secondGreenValue = secondGreen[blendMode];
							outputRed[blendMode] = secondRedValue < firstRedValue ? firstRedValue - secondRedValue : secondRedValue - firstRedValue;
							outputGreen[blendMode] = firstGreenValue <= secondGreenValue ? secondGreenValue - firstGreenValue : -secondGreenValue + firstGreenValue;
							outputBlue[blendMode] = firstBlueValue <= secondBlueValue ? secondBlueValue - firstBlueValue : -secondBlueValue + firstBlueValue;
						}
					} else if (blendMode == 12) {
						for (blendMode = 0; blendMode < EnvironmentLight.textureOpWidth; blendMode++) {
							firstRedValue = firstRed[blendMode];
							firstBlueValue = firstBlue[blendMode];
							secondBlueValue = secondBlue[blendMode];
							secondRedValue = secondRed[blendMode];
							firstGreenValue = firstGreen[blendMode];
							secondGreenValue = secondGreen[blendMode];
							outputRed[blendMode] = secondRedValue + firstRedValue - (firstRedValue * secondRedValue >> 11);
							outputGreen[blendMode] = firstGreenValue + secondGreenValue - (firstGreenValue * secondGreenValue >> 11);
							outputBlue[blendMode] = secondBlueValue + firstBlueValue - (firstBlueValue * secondBlueValue >> 11);
						}
					}
				}
			}
		}
		return output;
	}

	@OriginalMember(owner = "client!pda", name = "c", descriptor = "(II)[I")
	@Override
	public int[] monochromeOutput(@OriginalArg(0) int row) {
		@Pc(19) int[] output = super.monochromeImageCache.get(row);
		if (super.monochromeImageCache.dirty) {
			@Pc(29) int[] first = this.getChildMonochromeOutput(row, 0);
			@Pc(35) int[] second = this.getChildMonochromeOutput(row, 1);
			@Pc(38) int blendMode = this.blendMode;
			if (blendMode == 1) {
				for (blendMode = 0; blendMode < EnvironmentLight.textureOpWidth; blendMode++) {
					output[blendMode] = first[blendMode] + second[blendMode];
				}
			} else if (blendMode == 2) {
				for (blendMode = 0; blendMode < EnvironmentLight.textureOpWidth; blendMode++) {
					output[blendMode] = first[blendMode] - second[blendMode];
				}
			} else if (blendMode == 3) {
				for (blendMode = 0; blendMode < EnvironmentLight.textureOpWidth; blendMode++) {
					output[blendMode] = second[blendMode] * first[blendMode] >> 12;
				}
			} else {
				@Pc(171) int secondValue;
				if (blendMode == 4) {
					for (blendMode = 0; blendMode < EnvironmentLight.textureOpWidth; blendMode++) {
						secondValue = second[blendMode];
						output[blendMode] = secondValue == 0 ? 4096 : (first[blendMode] << 12) / secondValue;
					}
				} else if (blendMode == 5) {
					for (blendMode = 0; blendMode < EnvironmentLight.textureOpWidth; blendMode++) {
						output[blendMode] = 4096 - ((4096 - first[blendMode]) * (4096 - second[blendMode]) >> 12);
					}
				} else if (blendMode == 6) {
					for (blendMode = 0; blendMode < EnvironmentLight.textureOpWidth; blendMode++) {
						secondValue = second[blendMode];
						output[blendMode] = secondValue >= 2048 ? 4096 - ((4096 - secondValue) * (4096 - first[blendMode]) >> 11) : secondValue * first[blendMode] >> 11;
					}
				} else {
					@Pc(278) int firstValue;
					if (blendMode == 7) {
						for (blendMode = 0; blendMode < EnvironmentLight.textureOpWidth; blendMode++) {
							firstValue = first[blendMode];
							output[blendMode] = firstValue == 4096 ? 4096 : (second[blendMode] << 12) / (4096 - firstValue);
						}
					} else if (blendMode == 8) {
						for (blendMode = 0; blendMode < EnvironmentLight.textureOpWidth; blendMode++) {
							firstValue = first[blendMode];
							output[blendMode] = firstValue == 0 ? 0 : 4096 - (4096 - second[blendMode] << 12) / firstValue;
						}
					} else if (blendMode == 9) {
						for (blendMode = 0; blendMode < EnvironmentLight.textureOpWidth; blendMode++) {
							secondValue = second[blendMode];
							firstValue = first[blendMode];
							output[blendMode] = firstValue < secondValue ? firstValue : secondValue;
						}
					} else if (blendMode == 10) {
						for (blendMode = 0; blendMode < EnvironmentLight.textureOpWidth; blendMode++) {
							secondValue = second[blendMode];
							firstValue = first[blendMode];
							output[blendMode] = firstValue > secondValue ? firstValue : secondValue;
						}
					} else if (blendMode == 11) {
						for (blendMode = 0; blendMode < EnvironmentLight.textureOpWidth; blendMode++) {
							secondValue = second[blendMode];
							firstValue = first[blendMode];
							output[blendMode] = firstValue <= secondValue ? secondValue - firstValue : firstValue - secondValue;
						}
					} else if (blendMode == 12) {
						for (blendMode = 0; blendMode < EnvironmentLight.textureOpWidth; blendMode++) {
							secondValue = second[blendMode];
							firstValue = first[blendMode];
							output[blendMode] = firstValue + secondValue - (firstValue * secondValue >> 11);
						}
					}
				}
			}
		}
		return output;
	}
}
