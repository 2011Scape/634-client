package com.jagex.game.runetek5.graphics.texture.water;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!rga")
public class WaveNoiseGenerator extends WaterTextureGenerator {

	@OriginalMember(owner = "client!rga", name = "t", descriptor = "I")
	private int signal;

	@OriginalMember(owner = "client!rga", name = "A", descriptor = "I")
	private int texelIndex;

	@OriginalMember(owner = "client!rga", name = "C", descriptor = "I")
	private int weight;

	@OriginalMember(owner = "client!rga", name = "D", descriptor = "I")
	private int total;

	@OriginalMember(owner = "client!rga", name = "J", descriptor = "[B")
	private byte[] outputBuffer;

	@OriginalMember(owner = "client!rga", name = "L", descriptor = "I")
	private final int offset;

	@OriginalMember(owner = "client!rga", name = "r", descriptor = "I")
	private final int gain;

	@OriginalMember(owner = "client!rga", name = "s", descriptor = "I")
	private final int amplitudeStep;

	@OriginalMember(owner = "client!rga", name = "y", descriptor = "I")
	private int amplitude;

	@OriginalMember(owner = "client!rga", name = "<init>", descriptor = "(IIIIIFFF)V")
	public WaveNoiseGenerator(@OriginalArg(0) int seed, @OriginalArg(1) int factorsOfTwo, @OriginalArg(2) int xFactor, @OriginalArg(3) int yFactor, @OriginalArg(4) int zFactor, @OriginalArg(5) float amplitudeExponent, @OriginalArg(6) float gain, @OriginalArg(7) float offset) {
		super(seed, factorsOfTwo, xFactor, yFactor, zFactor);
		this.offset = (int) (offset * 4096.0F);
		this.gain = (int) (gain * 4096.0F);
		this.amplitude = this.amplitudeStep = (int) (Math.pow(0.5D, (double) -amplitudeExponent) * 4096.0D);
	}

	@OriginalMember(owner = "client!rga", name = "a", descriptor = "(IBB)V")
	public void writeTexel(@OriginalArg(0) int index, @OriginalArg(1) byte value) {
		this.outputBuffer[index] = value;
	}

	@OriginalMember(owner = "client!rga", name = "a", descriptor = "(Z)V")
	@Override
	public final void reset() {
		this.texelIndex = 0;
		this.total = 0;
	}

	@OriginalMember(owner = "client!rga", name = "a", descriptor = "(III)V")
	@Override
	public final void accumulateOctave(@OriginalArg(0) int noise, @OriginalArg(1) int octave) {
		if (octave == 0) {
			this.signal = this.gain - (noise >= 0 ? noise : -noise);
			this.weight = 4096;
			this.signal = this.signal * this.signal >> 12;
			this.total = this.signal;
			return;
		}
		this.weight = this.signal * this.offset >> 12;
		if (this.weight < 0) {
			this.weight = 0;
		} else if (this.weight > 4096) {
			this.weight = 4096;
		}
		this.signal = this.gain - (noise < 0 ? -noise : noise);
		this.signal = this.signal * this.signal >> 12;
		this.signal = this.weight * this.signal >> 12;
		this.total += this.signal * this.amplitude >> 12;
		this.amplitude = this.amplitude * this.amplitudeStep >> 12;
	}

	@OriginalMember(owner = "client!rga", name = "c", descriptor = "(I)V")
	@Override
	public final void emitTexel() {
		this.amplitude = this.amplitudeStep;
		this.total >>= 0x4;
		if (this.total < 0) {
			this.total = 0;
		} else if (this.total > 255) {
			this.total = 255;
		}
		this.writeTexel(this.texelIndex++, (byte) this.total);
		this.total = 0;
	}
}
