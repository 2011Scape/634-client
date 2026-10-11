package com.jagex.game.runetek5.sound;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!pe")
public final class Resampler {

	@OriginalMember(owner = "client!pe", name = "e", descriptor = "[[I")
	private int[][] resampleTable;

	@OriginalMember(owner = "client!pe", name = "a", descriptor = "I")
	private int baseRate;

	@OriginalMember(owner = "client!pe", name = "f", descriptor = "I")
	private int desiredRate;

	@OriginalMember(owner = "client!pe", name = "<init>", descriptor = "(II)V")
	public Resampler(@OriginalArg(0) int inputSampleRate, @OriginalArg(1) int outputSampleRate) {
		if (outputSampleRate != inputSampleRate) {
			@Pc(16) int gcd = Resampler.gcd(inputSampleRate, outputSampleRate);
			@Pc(20) int inputRate = inputSampleRate / gcd;
			@Pc(24) int outputRate = outputSampleRate / gcd;
			this.resampleTable = new int[inputRate][14];
			this.baseRate = outputRate;
			this.desiredRate = inputRate;
			for (@Pc(37) int i = 0; i < inputRate; i++) {
				@Pc(44) int[] coefficients = this.resampleTable[i];
				@Pc(52) double centerPosition = (double) i / (double) inputRate + 6.0D;
				@Pc(60) int tapIndex = (int) Math.floor(centerPosition + 1.0D - 7.0D);
				if (tapIndex < 0) {
					tapIndex = 0;
				}
				@Pc(73) int windowEnd = (int) Math.ceil(centerPosition + 7.0D);
				if (windowEnd > 14) {
					windowEnd = 14;
				}
				@Pc(86) double gainFactor = (double) outputRate / (double) inputRate;
				while (windowEnd > tapIndex) {
					@Pc(96) double sincArg = ((double) tapIndex - centerPosition) * 3.141592653589793D;
					@Pc(98) double tapValue = gainFactor;
					if (sincArg < -1.0E-4D || sincArg > 1.0E-4D) {
						tapValue = gainFactor * (Math.sin(sincArg) / sincArg);
					}
					tapValue *= Math.cos(((double) tapIndex - centerPosition) * 0.2243994752564138D) * 0.46D + 0.54D;
					coefficients[tapIndex] = (int) Math.floor(tapValue * 65536.0D + 0.5D);
					tapIndex++;
				}
			}
		}
	}

	@OriginalMember(owner = "client!pe", name = "a", descriptor = "(I[S)[S")
	public short[] resample(@OriginalArg(1) short[] samples) {
		if (this.resampleTable != null) {
			@Pc(31) int outputLength = (int) ((long) this.baseRate * (long) samples.length / (long) this.desiredRate) + 14;
			@Pc(34) int[] resampledData = new int[outputLength];
			@Pc(36) int outputIndex = 0;
			@Pc(38) int phaseAccumulator = 0;
			for (@Pc(40) int i = 0; i < samples.length; i++) {
				@Pc(46) short sample = samples[i];
				@Pc(51) int[] coefficients = this.resampleTable[phaseAccumulator];
				for (@Pc(53) int j = 0; j < 14; j++) {
					resampledData[outputIndex + j] += sample * coefficients[j] >> 2;
				}
				phaseAccumulator += this.baseRate;
				@Pc(86) int phaseAdvance = phaseAccumulator / this.desiredRate;
				phaseAccumulator -= this.desiredRate * phaseAdvance;
				outputIndex += phaseAdvance;
			}
			samples = new short[outputLength];
			for (@Pc(111) int k = 0; k < outputLength; k++) {
				@Pc(121) int resampledSample = resampledData[k] + 8192 >> 14;
				if (resampledSample < -32768) {
					samples[k] = -32768;
				} else if (resampledSample > 32767) {
					samples[k] = 32767;
				} else {
					samples[k] = (short) resampledSample;
				}
			}
		}
		return samples;
	}

	@OriginalMember(owner = "client!pe", name = "a", descriptor = "(IB)I")
	public int convertSampleRate(@OriginalArg(0) int rate) {
		if (this.resampleTable != null) {
			rate = (int) ((long) rate * (long) this.baseRate / (long) this.desiredRate);
		}
		return rate;
	}

	@OriginalMember(owner = "client!pe", name = "a", descriptor = "([BI)[B")
	public byte[] resample(@OriginalArg(0) byte[] samples) {
		if (this.resampleTable != null) {
			@Pc(28) int outputLength = (int) ((long) this.baseRate * (long) samples.length / (long) this.desiredRate) + 14;
			@Pc(31) int[] resampledData = new int[outputLength];
			@Pc(33) int outputIndex = 0;
			@Pc(35) int phaseAccumulator = 0;
			for (@Pc(37) int i = 0; i < samples.length; i++) {
				@Pc(43) byte sample = samples[i];
				@Pc(48) int[] coefficients = this.resampleTable[phaseAccumulator];
				for (@Pc(50) int j = 0; j < 14; j++) {
					resampledData[outputIndex + j] += sample * coefficients[j];
				}
				phaseAccumulator += this.baseRate;
				@Pc(79) int phaseAdvance = phaseAccumulator / this.desiredRate;
				phaseAccumulator -= this.desiredRate * phaseAdvance;
				outputIndex += phaseAdvance;
			}
			samples = new byte[outputLength];
			for (@Pc(100) int k = 0; k < outputLength; k++) {
				@Pc(110) int resampledSample = resampledData[k] + 32768 >> 16;
				if (resampledSample < -128) {
					samples[k] = -128;
				} else if (resampledSample > 127) {
					samples[k] = 127;
				} else {
					samples[k] = (byte) resampledSample;
				}
			}
		}
		return samples;
	}

	@OriginalMember(owner = "client!pe", name = "a", descriptor = "(II)I")
	public int convertBitRate(@OriginalArg(1) int rate) {
		if (this.resampleTable != null) {
			rate = (int) ((long) rate * (long) this.baseRate / (long) this.desiredRate) + 6;
		}
		return rate;
	}
	@OriginalMember(owner = "client!bg", name = "b", descriptor = "(III)I")
	public static int gcd(@OriginalArg(1) int a, @OriginalArg(2) int b) {
		@Pc(10) int temp;
		if (a < b) {
			temp = a;
			a = b;
			b = temp;
		}
		while (b != 0) {
			temp = a % b;
			a = b;
			b = temp;
		}
		return a;
	}
}
