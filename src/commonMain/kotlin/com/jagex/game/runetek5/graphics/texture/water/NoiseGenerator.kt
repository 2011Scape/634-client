package com.jagex.game.runetek5.graphics.texture.water

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmStatic

@OriginalClass("client!qg")
abstract class NoiseGenerator @OriginalMember(owner = "client!qg", name = "<init>", descriptor = "()V") constructor() {

	@OriginalMember(owner = "client!qg", name = "a", descriptor = "(IIFFIFIFII[F)V")
	abstract fun generate(@OriginalArg(2) scaleX: Float, @OriginalArg(3) scaleZ: Float, @OriginalArg(4) offset: Int, @OriginalArg(5) amplitude: Float, @OriginalArg(7) scaleY: Float, @OriginalArg(9) layer: Int, @OriginalArg(10) output: FloatArray?)

	companion object {
		@OriginalMember(owner = "client!lj", name = "a", descriptor = "(IIIFIFFFFLclient!qg;I)[B")
		@JvmStatic
		fun generateHeightData(@OriginalArg(3) persistence: Float, @OriginalArg(5) frequencyX: Float, @OriginalArg(6) frequencyY: Float, @OriginalArg(7) amplitude: Float, @OriginalArg(8) frequencyZ: Float, @OriginalArg(9) noiseGenerator: NoiseGenerator?): ByteArray {
			@Pc(15) val heightData: ByteArray = ByteArray(262144)
			NoiseGenerator.generateLayers(amplitude, heightData, frequencyX, 0, frequencyZ, noiseGenerator, frequencyY, persistence)
			return heightData
		}

		@OriginalMember(owner = "client!u", name = "a", descriptor = "(IF[BFIFLclient!qg;FIBIFI)V")
		@JvmStatic
		fun generateLayers(@OriginalArg(1) amplitude: Float, @OriginalArg(2) output: ByteArray?, @OriginalArg(3) scaleX: Float, @OriginalArg(4) offset: Int, @OriginalArg(5) scaleZ: Float, @OriginalArg(6) noiseGenerator: NoiseGenerator?, @OriginalArg(7) scaleY: Float, @OriginalArg(11) persistence: Float) {
			var offset: Int = offset
			@Pc(3) var i: Int = 0
			while (i < 16) {
				NoiseGenerator.generateLayer(amplitude, offset, scaleX, persistence, noiseGenerator, i, scaleY, output, scaleZ)
				offset += 16384
				i++
			}
		}

		@OriginalMember(owner = "client!ke", name = "a", descriptor = "(FIFIIFLclient!qg;IBIF[BIF)V")
		@JvmStatic
		fun generateLayer(@OriginalArg(0) amplitude: Float, @OriginalArg(1) offset: Int, @OriginalArg(2) scaleX: Float, @OriginalArg(5) persistence: Float, @OriginalArg(6) noiseGenerator: NoiseGenerator?, @OriginalArg(7) layer: Int, @OriginalArg(10) scaleY: Float, @OriginalArg(11) output: ByteArray?, @OriginalArg(13) scaleZ: Float) {
			var amplitude: Float = amplitude
			var scaleX: Float = scaleX
			var scaleY: Float = scaleY
			var scaleZ: Float = scaleZ
			@Pc(12) val noise: FloatArray = FloatArray(16384)
			@Pc(18) var outputIndex: Int
			@Pc(52) var sampleIndex: Int
			@Pc(14) var i: Int = 0
			while (i < 8) {
				outputIndex = offset
				noiseGenerator!!.generate(scaleX / 128.toFloat(), scaleZ / 16.toFloat(), 0, amplitude * 127.0f, scaleY / 128.toFloat(), layer, noise)
				scaleY *= 2.0f
				scaleX *= 2.0f
				sampleIndex = 0
				while (sampleIndex < 16384) {
					output!![outputIndex] = (output[outputIndex].toFloat() + noise[sampleIndex]).toInt().toByte()
					outputIndex++
					sampleIndex++
				}
				scaleZ *= 2.0f
				amplitude *= persistence
				i++
			}
			outputIndex = offset
			sampleIndex = 0
			while (sampleIndex < 16384) {
				output!![outputIndex] = (output[outputIndex] + 127).toByte()
				outputIndex++
				sampleIndex++
			}
		}
	}
}
