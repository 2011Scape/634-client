package com.jagex.game.runetek5.graphics.texture.water;

import com.jagex.game.runetek5.graphics.texture.water.BillowNoiseGenerator;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!wd")
public final class NativeBillowTextureData extends BillowNoiseGenerator {

	@OriginalMember(owner = "client!wd", name = "H", descriptor = "[B")
	private byte[] data;

	@OriginalMember(owner = "client!wd", name = "<init>", descriptor = "()V")
	public NativeBillowTextureData() {
		super(12, 5, 16, 2, 2, 0.45F);
	}

	@OriginalMember(owner = "client!wd", name = "a", descriptor = "(IBII)[B")
	public byte[] getData() {
		this.data = new byte[524288];
		this.renderTexels();
		return this.data;
	}

	@OriginalMember(owner = "client!wd", name = "a", descriptor = "(BIB)V")
	@Override
	public void writeTexel(@OriginalArg(1) int index, @OriginalArg(2) byte value) {
		@Pc(12) byte sample = (byte) ((value >> 1 & 0x7F) + 127);
		@Pc(16) int offset = index * 2;
		@Pc(19) int firstIndex = offset;
		@Pc(20) int secondIndex = offset + 1;
		this.data[firstIndex] = sample;
		this.data[secondIndex] = sample;
	}
}
