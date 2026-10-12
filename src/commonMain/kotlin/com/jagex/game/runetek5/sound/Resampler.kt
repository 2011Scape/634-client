package com.jagex.game.runetek5.sound

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmStatic

@OriginalClass("client!pe")
class Resampler {

	@OriginalMember(owner = "client!pe", name = "e", descriptor = "[[I")
	private var resampleTable: Array<IntArray?>? = null

	@OriginalMember(owner = "client!pe", name = "a", descriptor = "I")
	private var baseRate: Int = 0

	@OriginalMember(owner = "client!pe", name = "f", descriptor = "I")
	private var desiredRate: Int = 0

	@OriginalMember(owner = "client!pe", name = "<init>", descriptor = "(II)V")
	constructor(@OriginalArg(0) inputSampleRate: Int, @OriginalArg(1) outputSampleRate: Int) {
		if (outputSampleRate != inputSampleRate) {
			@Pc(16) val gcd: Int = Resampler.gcd(inputSampleRate, outputSampleRate)
			@Pc(20) val inputRate: Int = inputSampleRate / gcd
			@Pc(24) val outputRate: Int = outputSampleRate / gcd
			this.resampleTable = Array<IntArray?>(inputRate) { IntArray(14) }
			this.baseRate = outputRate
			this.desiredRate = inputRate
			@Pc(37) var i: Int = 0
			while (i < inputRate) {
				@Pc(44) val coefficients: IntArray = this.resampleTable!![i]!!
				@Pc(52) val centerPosition: Double = i.toDouble() / inputRate.toDouble() + 6.0
				@Pc(60) var tapIndex: Int = kotlin.math.floor(centerPosition + 1.0 - 7.0).toInt()
				if (tapIndex < 0) {
					tapIndex = 0
				}
				@Pc(73) var windowEnd: Int = kotlin.math.ceil(centerPosition + 7.0).toInt()
				if (windowEnd > 14) {
					windowEnd = 14
				}
				@Pc(86) val gainFactor: Double = outputRate.toDouble() / inputRate.toDouble()
				while (windowEnd > tapIndex) {
					@Pc(96) val sincArg: Double = (tapIndex.toDouble() - centerPosition) * 3.141592653589793
					@Pc(98) var tapValue: Double = gainFactor
					if (sincArg < -1.0E-4 || sincArg > 1.0E-4) {
						tapValue = gainFactor * (kotlin.math.sin(sincArg) / sincArg)
					}
					tapValue *= kotlin.math.cos((tapIndex.toDouble() - centerPosition) * 0.2243994752564138) * 0.46 + 0.54
					coefficients[tapIndex] = kotlin.math.floor(tapValue * 65536.0 + 0.5).toInt()
					tapIndex++
				}
				i++
			}
		}
	}

	@OriginalMember(owner = "client!pe", name = "a", descriptor = "(I[S)[S")
	fun resample(@OriginalArg(1) samples: ShortArray?): ShortArray? {
		var samples: ShortArray? = samples
		if (this.resampleTable != null) {
			@Pc(31) val outputLength: Int = (this.baseRate.toLong() * samples!!.size.toLong() / this.desiredRate.toLong()).toInt() + 14
			@Pc(34) val resampledData: IntArray = IntArray(outputLength)
			@Pc(36) var outputIndex: Int = 0
			@Pc(38) var phaseAccumulator: Int = 0
			@Pc(40) var i: Int = 0
			while (i < samples.size) {
				@Pc(46) val sample: Short = samples[i]
				@Pc(51) val coefficients: IntArray = this.resampleTable!![phaseAccumulator]!!
				@Pc(53) var j: Int = 0
				while (j < 14) {
					resampledData[outputIndex + j] += (sample.toInt() * coefficients[j]) shr 2
					j++
				}
				phaseAccumulator += this.baseRate
				@Pc(86) val phaseAdvance: Int = phaseAccumulator / this.desiredRate
				phaseAccumulator -= this.desiredRate * phaseAdvance
				outputIndex += phaseAdvance
				i++
			}
			samples = ShortArray(outputLength)
			@Pc(111) var k: Int = 0
			while (k < outputLength) {
				@Pc(121) val resampledSample: Int = (resampledData[k] + 8192) shr 14
				if (resampledSample < -32768) {
					samples[k] = (-32768).toShort()
				} else if (resampledSample > 32767) {
					samples[k] = 32767.toShort()
				} else {
					samples[k] = resampledSample.toShort()
				}
				k++
			}
		}
		return samples
	}

	@OriginalMember(owner = "client!pe", name = "a", descriptor = "(IB)I")
	fun convertSampleRate(@OriginalArg(0) rate: Int): Int {
		var rate: Int = rate
		if (this.resampleTable != null) {
			rate = (rate.toLong() * this.baseRate.toLong() / this.desiredRate.toLong()).toInt()
		}
		return rate
	}

	@OriginalMember(owner = "client!pe", name = "a", descriptor = "([BI)[B")
	fun resample(@OriginalArg(0) samples: ByteArray?): ByteArray? {
		var samples: ByteArray? = samples
		if (this.resampleTable != null) {
			@Pc(28) val outputLength: Int = (this.baseRate.toLong() * samples!!.size.toLong() / this.desiredRate.toLong()).toInt() + 14
			@Pc(31) val resampledData: IntArray = IntArray(outputLength)
			@Pc(33) var outputIndex: Int = 0
			@Pc(35) var phaseAccumulator: Int = 0
			@Pc(37) var i: Int = 0
			while (i < samples.size) {
				@Pc(43) val sample: Byte = samples[i]
				@Pc(48) val coefficients: IntArray = this.resampleTable!![phaseAccumulator]!!
				@Pc(50) var j: Int = 0
				while (j < 14) {
					resampledData[outputIndex + j] += sample.toInt() * coefficients[j]
					j++
				}
				phaseAccumulator += this.baseRate
				@Pc(79) val phaseAdvance: Int = phaseAccumulator / this.desiredRate
				phaseAccumulator -= this.desiredRate * phaseAdvance
				outputIndex += phaseAdvance
				i++
			}
			samples = ByteArray(outputLength)
			@Pc(100) var k: Int = 0
			while (k < outputLength) {
				@Pc(110) val resampledSample: Int = (resampledData[k] + 32768) shr 16
				if (resampledSample < -128) {
					samples[k] = (-128).toByte()
				} else if (resampledSample > 127) {
					samples[k] = 127.toByte()
				} else {
					samples[k] = resampledSample.toByte()
				}
				k++
			}
		}
		return samples
	}

	@OriginalMember(owner = "client!pe", name = "a", descriptor = "(II)I")
	fun convertBitRate(@OriginalArg(1) rate: Int): Int {
		var rate: Int = rate
		if (this.resampleTable != null) {
			rate = (rate.toLong() * this.baseRate.toLong() / this.desiredRate.toLong()).toInt() + 6
		}
		return rate
	}

	companion object {
		@OriginalMember(owner = "client!bg", name = "b", descriptor = "(III)I")
		@JvmStatic
		fun gcd(@OriginalArg(1) a: Int, @OriginalArg(2) b: Int): Int {
			var a: Int = a
			var b: Int = b
			@Pc(10) var temp: Int
			if (a < b) {
				temp = a
				a = b
				b = temp
			}
			while (b != 0) {
				temp = a % b
				a = b
				b = temp
			}
			return a
		}
	}
}
