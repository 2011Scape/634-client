package com.jagex.game.runetek5.graphics.texture.water;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!tg")
public class BillowNoiseGenerator extends WaterTextureGenerator {

	@OriginalMember(owner = "client!tg", name = "r", descriptor = "I")
	private int accumulator;

	@OriginalMember(owner = "client!tg", name = "x", descriptor = "I")
	private int writePosition;

	@OriginalMember(owner = "client!tg", name = "y", descriptor = "[B")
	private byte[] outputBuffer;

	@OriginalMember(owner = "client!tg", name = "s", descriptor = "[I")
	private final int[] amplitudeWeights = new int[this.octaveCount];

	@OriginalMember(owner = "client!tg", name = "<init>", descriptor = "(IIIIIF)V")
	public BillowNoiseGenerator(@OriginalArg(0) int seed, @OriginalArg(1) int factorsOfTwo, @OriginalArg(2) int xFactor, @OriginalArg(3) int yFactor, @OriginalArg(4) int zFactor, @OriginalArg(5) float frequency) {
		super(seed, factorsOfTwo, xFactor, yFactor, zFactor);
		for (@Pc(15) int index = 0; index < super.octaveCount; index++) {
			this.amplitudeWeights[index] = (short) (Math.pow((double) frequency, (double) index) * 4096.0D);
		}
	}

	@OriginalMember(owner = "client!tg", name = "a", descriptor = "(III)V")
	@Override
	public final void accumulateOctave(@OriginalArg(0) int noise, @OriginalArg(1) int octave) {
		this.accumulator += this.amplitudeWeights[octave] * noise >> 12;
	}

	@OriginalMember(owner = "client!tg", name = "a", descriptor = "(Z)V")
	@Override
	public final void reset() {
		this.accumulator = 0;
		this.writePosition = 0;
	}

	@OriginalMember(owner = "client!tg", name = "c", descriptor = "(I)V")
	@Override
	public final void emitTexel() {
		this.accumulator = Math.abs(this.accumulator);
		if (this.accumulator >= 4096) {
			this.accumulator = 4095;
		}
		this.writeTexel(this.writePosition++, (byte) (this.accumulator >> 4));
		this.accumulator = 0;
	}

	@OriginalMember(owner = "client!tg", name = "a", descriptor = "(BIB)V")
	public void writeTexel(@OriginalArg(1) int index, @OriginalArg(2) byte value) {
		this.outputBuffer[this.writePosition++] = (byte) ((value >> 1 & 0x7F) + 127);
	}
}
