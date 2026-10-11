package com.jagex.game.runetek5.graphics.texture.water;

import com.jagex.game.runetek5.graphics.texture.water.WaveNoiseGenerator;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.statics.StatBaseLevels;

@OriginalClass("client!sv")
public final class NativeWaveTextureData extends WaveNoiseGenerator {

	@OriginalMember(owner = "client!sv", name = "P", descriptor = "[B")
	private byte[] texels;

	@OriginalMember(owner = "client!sv", name = "<init>", descriptor = "()V")
	public NativeWaveTextureData() {
		super(8, 5, 8, 8, 2, 0.1F, 0.55F, 3.0F);
	}

	@OriginalMember(owner = "client!sv", name = "a", descriptor = "(IBB)V")
	@Override
	public void writeTexel(@OriginalArg(0) int index, @OriginalArg(1) byte value) {
		@Pc(7) int offset = index * 2;
		@Pc(11) int level = value & 0xFF;
		@Pc(14) int firstIndex = offset;
		@Pc(15) int secondIndex = offset + 1;
		this.texels[firstIndex] = (byte) (level * 3 >> 5);
		this.texels[secondIndex] = (byte) (level * 3 >> 5);
	}

	@OriginalMember(owner = "client!sv", name = "a", descriptor = "(IIBI)[B")
	public byte[] getData() {
		this.texels = new byte[524288];
		this.renderTexels();
		return this.texels;
	}
}
