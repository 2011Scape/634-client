package com.jagex.game.runetek5.graphics.texture.water;

import com.jagex.game.runetek5.graphics.texture.water.WaveNoiseGenerator;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!hha")
public final class NativeWaveNoiseData extends WaveNoiseGenerator {

	@OriginalMember(owner = "client!hha", name = "T", descriptor = "[B")
	private byte[] data;

	@OriginalMember(owner = "client!hha", name = "<init>", descriptor = "()V")
	public NativeWaveNoiseData() {
		super(8, 5, 8, 8, 2, 0.1F, 0.55F, 3.0F);
	}

	@OriginalMember(owner = "client!hha", name = "a", descriptor = "(IBB)V")
	@Override
	public void writeTexel(@OriginalArg(0) int index, @OriginalArg(1) byte value) {
		@Pc(11) int offset = index * 2;
		@Pc(14) int firstIndex = offset;
		@Pc(15) int secondIndex = offset + 1;
		this.data[firstIndex] = -1;
		@Pc(21) int level = value & 0xFF;
		this.data[secondIndex] = (byte) (level * 3 >> 5);
	}

	@OriginalMember(owner = "client!hha", name = "a", descriptor = "(IIBI)[B")
	public byte[] getData() {
		this.data = new byte[524288];
		this.renderTexels();
		return this.data;
	}
}
