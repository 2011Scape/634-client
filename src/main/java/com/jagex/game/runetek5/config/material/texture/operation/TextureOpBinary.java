package com.jagex.game.runetek5.config.material.texture.operation;

import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.graphics.EnvironmentLight;
import com.jagex.game.runetek5.config.material.texture.TextureOperation;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!hga")
public final class TextureOpBinary extends TextureOperation {

	@OriginalMember(owner = "client!hga", name = "N", descriptor = "I")
	private int upperThreshold = 4096;

	@OriginalMember(owner = "client!hga", name = "K", descriptor = "I")
	private int lowerThreshold = 0;

	@OriginalMember(owner = "client!hga", name = "<init>", descriptor = "()V")
	public TextureOpBinary() {
		super(1, true);
	}

	@OriginalMember(owner = "client!hga", name = "a", descriptor = "(Lclient!cea;II)V")
	@Override
	public void decode(@OriginalArg(0) Packet packet, @OriginalArg(1) int opcode) {
		if (opcode == 0) {
			this.lowerThreshold = packet.readUShort();
		} else if (opcode == 1) {
			this.upperThreshold = packet.readUShort();
		}
	}

	@OriginalMember(owner = "client!hga", name = "c", descriptor = "(II)[I")
	@Override
	public int[] monochromeOutput(@OriginalArg(0) int row) {
		@Pc(19) int[] output = super.monochromeImageCache.get(row);
		if (super.monochromeImageCache.dirty) {
			@Pc(29) int[] input = this.getChildMonochromeOutput(row, 0);
			for (@Pc(31) int i = 0; i < EnvironmentLight.textureOpWidth; i++) {
				@Pc(37) int value = input[i];
				output[i] = value >= this.lowerThreshold && this.upperThreshold >= value ? 4096 : 0;
			}
		}
		return output;
	}
}
