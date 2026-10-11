package com.jagex.game.runetek5.config.material.texture.operation;

import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.graphics.EnvironmentLight;
import com.jagex.game.runetek5.config.material.texture.TextureOperation;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!uf")
public final class TextureOpColorize extends TextureOperation {

	@OriginalMember(owner = "client!uf", name = "I", descriptor = "I")
	private int redMultiplier = 4096;

	@OriginalMember(owner = "client!uf", name = "L", descriptor = "I")
	private int blueMultiplier = 4096;

	@OriginalMember(owner = "client!uf", name = "S", descriptor = "I")
	private int greenMultiplier = 4096;

	@OriginalMember(owner = "client!uf", name = "<init>", descriptor = "()V")
	public TextureOpColorize() {
		super(1, false);
	}

	@OriginalMember(owner = "client!uf", name = "d", descriptor = "(II)[[I")
	@Override
	public int[][] getColorOutput(@OriginalArg(0) int row) {
		@Pc(18) int[][] output = super.colorImageCache.get(row);
		if (super.colorImageCache.dirty) {
			@Pc(28) int[][] input = this.getChildColorOutput(row, 0);
			@Pc(32) int[] inputRed = input[0];
			@Pc(36) int[] inputGreen = input[1];
			@Pc(40) int[] inputBlue = input[2];
			@Pc(44) int[] outputRed = output[0];
			@Pc(48) int[] outputGreen = output[1];
			@Pc(52) int[] outputBlue = output[2];
			for (@Pc(54) int i = 0; i < EnvironmentLight.textureOpWidth; i++) {
				@Pc(60) int red = inputRed[i];
				@Pc(64) int blue = inputBlue[i];
				@Pc(68) int green = inputGreen[i];
				if (blue == red && green == blue) {
					outputRed[i] = red * this.redMultiplier >> 12;
					outputGreen[i] = this.greenMultiplier * blue >> 12;
					outputBlue[i] = this.blueMultiplier * green >> 12;
				} else {
					outputRed[i] = this.redMultiplier;
					outputGreen[i] = this.greenMultiplier;
					outputBlue[i] = this.blueMultiplier;
				}
			}
		}
		return output;
	}

	@OriginalMember(owner = "client!uf", name = "a", descriptor = "(Lclient!cea;II)V")
	@Override
	public void decode(@OriginalArg(0) Packet packet, @OriginalArg(1) int opcode) {
		if (opcode == 0) {
			this.redMultiplier = packet.readUShort();
		} else if (opcode == 1) {
			this.greenMultiplier = packet.readUShort();
		} else if (opcode == 2) {
			this.blueMultiplier = packet.readUShort();
		}
	}
}
