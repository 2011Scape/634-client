package com.jagex.game.runetek5.config.material.texture.operation;

import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.graphics.EnvironmentLight;
import com.jagex.game.runetek5.config.material.texture.TextureOperation;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!vj")
public final class TextureOpHslAdjust extends TextureOperation {

	@OriginalMember(owner = "client!vj", name = "L", descriptor = "I")
	private int outputRed;

	@OriginalMember(owner = "client!vj", name = "O", descriptor = "I")
	private int lightness;

	@OriginalMember(owner = "client!vj", name = "S", descriptor = "I")
	private int outputBlue;

	@OriginalMember(owner = "client!vj", name = "W", descriptor = "I")
	private int outputGreen;

	@OriginalMember(owner = "client!vj", name = "ab", descriptor = "I")
	private int hue;

	@OriginalMember(owner = "client!vj", name = "bb", descriptor = "I")
	private int saturation;

	@OriginalMember(owner = "client!vj", name = "P", descriptor = "I")
	private int saturationAdjust = 0;

	@OriginalMember(owner = "client!vj", name = "Y", descriptor = "I")
	private int lightnessAdjust = 0;

	@OriginalMember(owner = "client!vj", name = "cb", descriptor = "I")
	private int hueAdjust = 0;

	@OriginalMember(owner = "client!vj", name = "<init>", descriptor = "()V")
	public TextureOpHslAdjust() {
		super(1, false);
	}

	@OriginalMember(owner = "client!vj", name = "d", descriptor = "(II)[[I")
	@Override
	public int[][] getColorOutput(@OriginalArg(0) int row) {
		@Pc(11) int[][] output = super.colorImageCache.get(row);
		if (super.colorImageCache.dirty) {
			@Pc(21) int[][] input = this.getChildColorOutput(row, 0);
			@Pc(25) int[] inRed = input[0];
			@Pc(29) int[] inGreen = input[1];
			@Pc(33) int[] inBlue = input[2];
			@Pc(37) int[] outRed = output[0];
			@Pc(41) int[] outGreen = output[1];
			@Pc(45) int[] outBlue = output[2];
			for (@Pc(47) int i = 0; i < EnvironmentLight.textureOpWidth; i++) {
				this.rgbToHsl(inRed[i], inGreen[i], inBlue[i]);
				this.saturation += this.saturationAdjust;
				this.hue += this.hueAdjust;
				this.lightness += this.lightnessAdjust;
				while (this.hue < 0) {
					this.hue += 4096;
				}
				if (this.saturation < 0) {
					this.saturation = 0;
				}
				while (this.hue > 4096) {
					this.hue -= 4096;
				}
				if (this.lightness < 0) {
					this.lightness = 0;
				}
				if (this.saturation > 4096) {
					this.saturation = 4096;
				}
				if (this.lightness > 4096) {
					this.lightness = 4096;
				}
				this.hslToRgb(this.lightness, this.hue, this.saturation);
				outRed[i] = this.outputRed;
				outGreen[i] = this.outputGreen;
				outBlue[i] = this.outputBlue;
			}
		}
		return output;
	}

	@OriginalMember(owner = "client!vj", name = "a", descriptor = "(IIBI)V")
	private void rgbToHsl(@OriginalArg(0) int red, @OriginalArg(1) int green, @OriginalArg(3) int blue) {
		@Pc(12) int maxRedGreen = green >= red ? green : red;
		@Pc(23) int minRedGreen = blue <= maxRedGreen ? maxRedGreen : blue;
		@Pc(34) int max = green > red ? red : green;
		@Pc(41) int min = blue >= max ? max : blue;
		this.lightness = (minRedGreen + min) / 2;
		@Pc(52) int range = minRedGreen - min;
		if (range > 0) {
			@Pc(66) int redDistance = (minRedGreen - red << 12) / range;
			@Pc(75) int greenDistance = (minRedGreen - green << 12) / range;
			@Pc(84) int blueDistance = (minRedGreen - blue << 12) / range;
			if (red == minRedGreen) {
				this.hue = min == green ? blueDistance + 20480 : -greenDistance + 4096;
			} else if (minRedGreen == green) {
				this.hue = blue == min ? redDistance + 4096 : -blueDistance + 12288;
			} else {
				this.hue = red == min ? greenDistance + 12288 : -redDistance + 20480;
			}
			this.hue /= 6;
		} else {
			this.hue = 0;
		}
		if (this.lightness > 0 && this.lightness < 4096) {
			this.saturation = (range << 12) / (this.lightness > 2048 ? 8192 - this.lightness * 2 : this.lightness * 2);
		} else {
			this.saturation = 0;
		}
	}

	@OriginalMember(owner = "client!vj", name = "b", descriptor = "(IIBI)V")
	private void hslToRgb(@OriginalArg(0) int outputBlue, @OriginalArg(1) int hue, @OriginalArg(3) int saturation) {
		@Pc(35) int max = outputBlue > 2048 ? saturation + outputBlue - (outputBlue * saturation >> 12) : outputBlue * (4096 - -saturation) >> 12;
		if (max <= 0) {
			this.outputRed = this.outputGreen = this.outputBlue = outputBlue;
			return;
		}
		@Pc(51) int scaledHue = hue * 6;
		@Pc(59) int min = outputBlue + outputBlue - max;
		@Pc(68) int delta = (max - min << 12) / max;
		@Pc(72) int sector = scaledHue >> 12;
		@Pc(79) int remainder = scaledHue - (sector << 12);
		@Pc(87) int span = max * delta >> 12;
		@Pc(93) int step = span * remainder >> 12;
		@Pc(97) int rising = step + min;
		@Pc(101) int falling = max - step;
		if (sector == 0) {
			this.outputGreen = rising;
			this.outputRed = max;
			this.outputBlue = min;
		} else if (sector == 1) {
			this.outputBlue = min;
			this.outputRed = falling;
			this.outputGreen = max;
		} else if (sector == 2) {
			this.outputBlue = rising;
			this.outputRed = min;
			this.outputGreen = max;
		} else if (sector == 3) {
			this.outputGreen = falling;
			this.outputRed = min;
			this.outputBlue = max;
		} else if (sector == 4) {
			this.outputGreen = min;
			this.outputRed = rising;
			this.outputBlue = max;
		} else if (sector == 5) {
			this.outputRed = max;
			this.outputBlue = falling;
			this.outputGreen = min;
		}
	}

	@OriginalMember(owner = "client!vj", name = "a", descriptor = "(Lclient!cea;II)V")
	@Override
	public void decode(@OriginalArg(0) Packet packet, @OriginalArg(1) int opcode) {
		if (opcode == 0) {
			this.hueAdjust = packet.readShort();
		} else if (opcode == 1) {
			this.saturationAdjust = (packet.readByte() << 12) / 100;
		} else if (opcode == 2) {
			this.lightnessAdjust = (packet.readByte() << 12) / 100;
		}
	}
}
