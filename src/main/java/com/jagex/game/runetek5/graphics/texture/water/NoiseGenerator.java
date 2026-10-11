package com.jagex.game.runetek5.graphics.texture.water;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!qg")
public abstract class NoiseGenerator {

	@OriginalMember(owner = "client!qg", name = "<init>", descriptor = "()V")
	public NoiseGenerator() {
	}

	@OriginalMember(owner = "client!qg", name = "a", descriptor = "(IIFFIFIFII[F)V")
	public abstract void generate(@OriginalArg(2) float scaleX, @OriginalArg(3) float scaleZ, @OriginalArg(4) int offset, @OriginalArg(5) float amplitude, @OriginalArg(7) float scaleY, @OriginalArg(9) int layer, @OriginalArg(10) float[] output);
	@OriginalMember(owner = "client!lj", name = "a", descriptor = "(IIIFIFFFFLclient!qg;I)[B")
	public static byte[] generateHeightData(@OriginalArg(3) float persistence, @OriginalArg(5) float frequencyX, @OriginalArg(6) float frequencyY, @OriginalArg(7) float amplitude, @OriginalArg(8) float frequencyZ, @OriginalArg(9) NoiseGenerator noiseGenerator) {
		@Pc(15) byte[] heightData = new byte[262144];
		NoiseGenerator.generateLayers(amplitude, heightData, frequencyX, 0, frequencyZ, noiseGenerator, frequencyY, persistence);
		return heightData;
	}
	@OriginalMember(owner = "client!u", name = "a", descriptor = "(IF[BFIFLclient!qg;FIBIFI)V")
	public static void generateLayers(@OriginalArg(1) float amplitude, @OriginalArg(2) byte[] output, @OriginalArg(3) float scaleX, @OriginalArg(4) int offset, @OriginalArg(5) float scaleZ, @OriginalArg(6) NoiseGenerator noiseGenerator, @OriginalArg(7) float scaleY, @OriginalArg(11) float persistence) {
		for (@Pc(3) int i = 0; i < 16; i++) {
			NoiseGenerator.generateLayer(amplitude, offset, scaleX, persistence, noiseGenerator, i, scaleY, output, scaleZ);
			offset += 16384;
		}
	}
	@OriginalMember(owner = "client!ke", name = "a", descriptor = "(FIFIIFLclient!qg;IBIF[BIF)V")
	public static void generateLayer(@OriginalArg(0) float amplitude, @OriginalArg(1) int offset, @OriginalArg(2) float scaleX, @OriginalArg(5) float persistence, @OriginalArg(6) NoiseGenerator noiseGenerator, @OriginalArg(7) int layer, @OriginalArg(10) float scaleY, @OriginalArg(11) byte[] output, @OriginalArg(13) float scaleZ) {
		@Pc(12) float[] noise = new float[16384];
		@Pc(18) int outputIndex;
		@Pc(52) int sampleIndex;
		for (@Pc(14) int i = 0; i < 8; i++) {
			outputIndex = offset;
			noiseGenerator.generate(scaleX / (float) 128, scaleZ / (float) 16, 0, amplitude * 127.0F, scaleY / (float) 128, layer, noise);
			scaleY *= 2.0F;
			scaleX *= 2.0F;
			for (sampleIndex = 0; sampleIndex < 16384; sampleIndex++) {
				output[outputIndex] = (byte) ((float) output[outputIndex] + noise[sampleIndex]);
				outputIndex++;
			}
			scaleZ *= 2.0F;
			amplitude *= persistence;
		}
		outputIndex = offset;
		for (sampleIndex = 0; sampleIndex < 16384; sampleIndex++) {
			output[outputIndex] = (byte) (output[outputIndex] + 127);
			outputIndex++;
		}
	}
}
