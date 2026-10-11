package com.jagex.game.runetek5.config.material.texture.operation;

import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.graphics.EnvironmentLight;
import com.jagex.game.runetek5.config.material.texture.TextureOperation;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!aia")
public final class TextureOpBrightness extends TextureOperation {

	@OriginalMember(owner = "client!aia", name = "I", descriptor = "I")
	private int blueBrightness = 4096;

	@OriginalMember(owner = "client!aia", name = "K", descriptor = "[I")
	private final int[] targetColor = new int[3];

	@OriginalMember(owner = "client!aia", name = "P", descriptor = "I")
	private int greenBrightness = 4096;

	@OriginalMember(owner = "client!aia", name = "Q", descriptor = "I")
	private int redBrightness = 4096;

	@OriginalMember(owner = "client!aia", name = "S", descriptor = "I")
	private int colorTolerance = 409;

	@OriginalMember(owner = "client!aia", name = "<init>", descriptor = "()V")
	public TextureOpBrightness() {
		super(1, false);
	}

	@OriginalMember(owner = "client!aia", name = "a", descriptor = "(Lclient!cea;II)V")
	@Override
	public void decode(@OriginalArg(0) Packet packet, @OriginalArg(1) int opcode) {
		if (opcode == 0) {
			this.colorTolerance = packet.readUShort();
		} else if (opcode == 1) {
			this.blueBrightness = packet.readUShort();
		} else if (opcode == 2) {
			this.greenBrightness = packet.readUShort();
		} else if (opcode == 3) {
			this.redBrightness = packet.readUShort();
		} else if (opcode == 4) {
			@Pc(65) int rgb = packet.readUMedium();
			this.targetColor[2] = rgb >> 12 & 0x0;
			this.targetColor[1] = rgb >> 4 & 0xFF0;
			this.targetColor[0] = (rgb & 0xFF0000) << 4;
		}
	}

	@OriginalMember(owner = "client!aia", name = "d", descriptor = "(II)[[I")
	@Override
	public int[][] getColorOutput(@OriginalArg(0) int row) {
		@Pc(17) int[][] output = super.colorImageCache.get(row);
		if (super.colorImageCache.dirty) {
			@Pc(27) int[][] input = this.getChildColorOutput(row, 0);
			@Pc(31) int[] inputRed = input[0];
			@Pc(35) int[] inputGreen = input[1];
			@Pc(39) int[] inputBlue = input[2];
			@Pc(43) int[] outputRed = output[0];
			@Pc(47) int[] outputGreen = output[1];
			@Pc(51) int[] outputBlue = output[2];
			for (@Pc(53) int i = 0; i < EnvironmentLight.textureOpWidth; i++) {
				@Pc(59) int red = inputRed[i];
				@Pc(66) int difference = red - this.targetColor[0];
				if (difference < 0) {
					difference = -difference;
				}
				if (difference > this.colorTolerance) {
					outputRed[i] = red;
					outputGreen[i] = inputGreen[i];
					outputBlue[i] = inputBlue[i];
				} else {
					@Pc(104) int green = inputGreen[i];
					difference = green - this.targetColor[1];
					if (difference < 0) {
						difference = -difference;
					}
					if (difference > this.colorTolerance) {
						outputRed[i] = red;
						outputGreen[i] = green;
						outputBlue[i] = inputBlue[i];
					} else {
						@Pc(145) int blue = inputBlue[i];
						difference = blue - this.targetColor[2];
						if (difference < 0) {
							difference = -difference;
						}
						if (difference > this.colorTolerance) {
							outputRed[i] = red;
							outputGreen[i] = green;
							outputBlue[i] = blue;
						} else {
							outputRed[i] = red * this.redBrightness >> 12;
							outputGreen[i] = green * this.greenBrightness >> 12;
							outputBlue[i] = blue * this.blueBrightness >> 12;
						}
					}
				}
			}
		}
		return output;
	}
}
