package com.jagex.game.runetek5.graphics.texture.water;

import com.jagex.game.runetek5.graphics.texture.water.BillowNoiseGenerator;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!dha")
public final class GLBillowTextureData extends BillowNoiseGenerator {

	@OriginalMember(owner = "client!dha", name = "H", descriptor = "[B")
	private byte[] data;

	@OriginalMember(owner = "client!dha", name = "<init>", descriptor = "()V")
	public GLBillowTextureData() {
		super(12, 5, 16, 2, 2, 0.45F);
	}

	@OriginalMember(owner = "client!dha", name = "a", descriptor = "(BIB)V")
	@Override
	public void writeTexel(@OriginalArg(1) int index, @OriginalArg(2) byte value) {
		@Pc(16) byte sample = (byte) ((value >> 1 & 0x7F) + 127);
		@Pc(20) int offset = index * 2;
		@Pc(23) int firstIndex = offset;
		@Pc(24) int secondIndex = offset + 1;
		this.data[firstIndex] = sample;
		this.data[secondIndex] = sample;
	}

	@OriginalMember(owner = "client!dha", name = "a", descriptor = "(IBII)[B")
	public byte[] getData() {
		this.data = new byte[524288];
		this.renderTexels();
		return this.data;
	}
}
