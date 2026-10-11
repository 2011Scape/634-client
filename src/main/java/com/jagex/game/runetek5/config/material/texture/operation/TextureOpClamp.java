package com.jagex.game.runetek5.config.material.texture.operation;

import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.graphics.EnvironmentLight;
import com.jagex.game.runetek5.config.material.texture.TextureOperation;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!fi")
public final class TextureOpClamp extends TextureOperation {

	@OriginalMember(owner = "client!fi", name = "K", descriptor = "I")
	private int maxValue = 4096;

	@OriginalMember(owner = "client!fi", name = "O", descriptor = "I")
	private int minValue = 0;

	@OriginalMember(owner = "client!fi", name = "<init>", descriptor = "()V")
	public TextureOpClamp() {
		super(1, false);
	}

	@OriginalMember(owner = "client!fi", name = "a", descriptor = "(Lclient!cea;II)V")
	@Override
	public void decode(@OriginalArg(0) Packet packet, @OriginalArg(1) int opcode) {
		if (opcode == 0) {
			this.minValue = packet.readUShort();
		} else if (opcode == 1) {
			this.maxValue = packet.readUShort();
		} else if (opcode == 2) {
			super.monochrome = packet.readUByte() == 1;
		}
	}

	@OriginalMember(owner = "client!fi", name = "d", descriptor = "(II)[[I")
	@Override
	public int[][] getColorOutput(@OriginalArg(0) int row) {
		@Pc(16) int[][] output = super.colorImageCache.get(row);
		if (super.colorImageCache.dirty) {
			@Pc(26) int[][] input = this.getChildColorOutput(row, 0);
			@Pc(30) int[] inputRed = input[0];
			@Pc(34) int[] inputGreen = input[1];
			@Pc(38) int[] inputBlue = input[2];
			@Pc(42) int[] outputRed = output[0];
			@Pc(46) int[] outputGreen = output[1];
			@Pc(50) int[] outputBlue = output[2];
			for (@Pc(52) int i = 0; i < EnvironmentLight.textureOpWidth; i++) {
				@Pc(58) int red = inputRed[i];
				@Pc(62) int green = inputGreen[i];
				@Pc(66) int blue = inputBlue[i];
				if (red < this.minValue) {
					outputRed[i] = this.minValue;
				} else if (red <= this.maxValue) {
					outputRed[i] = red;
				} else {
					outputRed[i] = this.maxValue;
				}
				if (this.minValue > green) {
					outputGreen[i] = this.minValue;
				} else if (this.maxValue >= green) {
					outputGreen[i] = green;
				} else {
					outputGreen[i] = this.maxValue;
				}
				if (blue < this.minValue) {
					outputBlue[i] = this.minValue;
				} else if (this.maxValue >= blue) {
					outputBlue[i] = blue;
				} else {
					outputBlue[i] = this.maxValue;
				}
			}
		}
		return output;
	}

	@OriginalMember(owner = "client!fi", name = "c", descriptor = "(II)[I")
	@Override
	public int[] monochromeOutput(@OriginalArg(0) int row) {
		@Pc(13) int[] output = super.monochromeImageCache.get(row);
		if (super.monochromeImageCache.dirty) {
			@Pc(23) int[] input = this.getChildMonochromeOutput(row, 0);
			for (@Pc(25) int i = 0; i < EnvironmentLight.textureOpWidth; i++) {
				@Pc(31) int value = input[i];
				if (value < this.minValue) {
					output[i] = this.minValue;
				} else if (value > this.maxValue) {
					output[i] = this.maxValue;
				} else {
					output[i] = value;
				}
			}
		}
		return output;
	}
}
