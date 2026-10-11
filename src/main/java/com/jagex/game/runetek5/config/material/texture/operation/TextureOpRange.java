package com.jagex.game.runetek5.config.material.texture.operation;

import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.graphics.EnvironmentLight;
import com.jagex.game.runetek5.config.material.texture.TextureOperation;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!kq")
public final class TextureOpRange extends TextureOperation {

	@OriginalMember(owner = "client!kq", name = "K", descriptor = "I")
	private int min = 1024;

	@OriginalMember(owner = "client!kq", name = "J", descriptor = "I")
	private int rangeMax = 3072;

	@OriginalMember(owner = "client!kq", name = "Q", descriptor = "I")
	private int range = 2048;

	@OriginalMember(owner = "client!kq", name = "<init>", descriptor = "()V")
	public TextureOpRange() {
		super(1, false);
	}

	@OriginalMember(owner = "client!kq", name = "a", descriptor = "(Lclient!cea;II)V")
	@Override
	public void decode(@OriginalArg(0) Packet packet, @OriginalArg(1) int opcode) {
		if (opcode == 0) {
			this.min = packet.readUShort();
		} else if (opcode == 1) {
			this.rangeMax = packet.readUShort();
		} else if (opcode == 2) {
			super.monochrome = packet.readUByte() == 1;
		}
	}

	@OriginalMember(owner = "client!kq", name = "e", descriptor = "(I)V")
	@Override
	public void postDecode() {
		this.range = this.rangeMax - this.min;
	}

	@OriginalMember(owner = "client!kq", name = "c", descriptor = "(II)[I")
	@Override
	public int[] monochromeOutput(@OriginalArg(0) int y) {
		@Pc(11) int[] output = super.monochromeImageCache.get(y);
		if (super.monochromeImageCache.dirty) {
			@Pc(21) int[] input = this.getChildMonochromeOutput(y, 0);
			for (@Pc(23) int i = 0; i < EnvironmentLight.textureOpWidth; i++) {
				output[i] = this.min + (this.range * input[i] >> 12);
			}
		}
		return output;
	}

	@OriginalMember(owner = "client!kq", name = "d", descriptor = "(II)[[I")
	@Override
	public int[][] getColorOutput(@OriginalArg(0) int y) {
		@Pc(11) int[][] output = super.colorImageCache.get(y);
		if (super.colorImageCache.dirty) {
			@Pc(29) int[][] input = this.getChildColorOutput(y, 0);
			@Pc(33) int[] inputRed = input[0];
			@Pc(37) int[] inputGreen = input[1];
			@Pc(41) int[] inputBlue = input[2];
			@Pc(45) int[] outputRed = output[0];
			@Pc(49) int[] outputGreen = output[1];
			@Pc(53) int[] outputBlue = output[2];
			for (@Pc(55) int i = 0; i < EnvironmentLight.textureOpWidth; i++) {
				outputRed[i] = (this.range * inputRed[i] >> 12) + this.min;
				outputGreen[i] = (this.range * inputGreen[i] >> 12) + this.min;
				outputBlue[i] = (inputBlue[i] * this.range >> 12) + this.min;
			}
		}
		return output;
	}
}
