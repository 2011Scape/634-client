package com.jagex.game.runetek5.graphics.texture.water

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!rga")
open class WaveNoiseGenerator : WaterTextureGenerator {

	@OriginalMember(owner = "client!rga", name = "t", descriptor = "I")
	private var signal: Int = 0

	@OriginalMember(owner = "client!rga", name = "A", descriptor = "I")
	private var texelIndex: Int = 0

	@OriginalMember(owner = "client!rga", name = "C", descriptor = "I")
	private var weight: Int = 0

	@OriginalMember(owner = "client!rga", name = "D", descriptor = "I")
	private var total: Int = 0

	@OriginalMember(owner = "client!rga", name = "J", descriptor = "[B")
	private var outputBuffer: ByteArray? = null

	@OriginalMember(owner = "client!rga", name = "L", descriptor = "I")
	private val offset: Int

	@OriginalMember(owner = "client!rga", name = "r", descriptor = "I")
	private val gain: Int

	@OriginalMember(owner = "client!rga", name = "s", descriptor = "I")
	private val amplitudeStep: Int

	@OriginalMember(owner = "client!rga", name = "y", descriptor = "I")
	private var amplitude: Int = 0

	@OriginalMember(owner = "client!rga", name = "<init>", descriptor = "(IIIIIFFF)V")
	constructor(@OriginalArg(0) seed: Int, @OriginalArg(1) factorsOfTwo: Int, @OriginalArg(2) xFactor: Int, @OriginalArg(3) yFactor: Int, @OriginalArg(4) zFactor: Int, @OriginalArg(5) amplitudeExponent: Float, @OriginalArg(6) gain: Float, @OriginalArg(7) offset: Float) : super(seed, factorsOfTwo, xFactor, yFactor, zFactor) {
		this.offset = (offset * 4096.0f).toInt()
		this.gain = (gain * 4096.0f).toInt()
		this.amplitudeStep = (java.lang.Math.pow(0.5, (-amplitudeExponent).toDouble()) * 4096.0).toInt()
		this.amplitude = this.amplitudeStep
	}

	@OriginalMember(owner = "client!rga", name = "a", descriptor = "(IBB)V")
	open fun writeTexel(@OriginalArg(0) index: Int, @OriginalArg(1) value: Byte) {
		this.outputBuffer!![index] = value
	}

	@OriginalMember(owner = "client!rga", name = "a", descriptor = "(Z)V")
	final override fun reset() {
		this.texelIndex = 0
		this.total = 0
	}

	@OriginalMember(owner = "client!rga", name = "a", descriptor = "(III)V")
	final override fun accumulateOctave(@OriginalArg(0) noise: Int, @OriginalArg(1) octave: Int) {
		if (octave == 0) {
			this.signal = this.gain - (if (noise >= 0) noise else -noise)
			this.weight = 4096
			this.signal = (this.signal * this.signal) shr 12
			this.total = this.signal
			return
		}
		this.weight = (this.signal * this.offset) shr 12
		if (this.weight < 0) {
			this.weight = 0
		} else if (this.weight > 4096) {
			this.weight = 4096
		}
		this.signal = this.gain - (if (noise < 0) -noise else noise)
		this.signal = (this.signal * this.signal) shr 12
		this.signal = (this.weight * this.signal) shr 12
		this.total += (this.signal * this.amplitude) shr 12
		this.amplitude = (this.amplitude * this.amplitudeStep) shr 12
	}

	@OriginalMember(owner = "client!rga", name = "c", descriptor = "(I)V")
	final override fun emitTexel() {
		this.amplitude = this.amplitudeStep
		this.total = this.total shr 0x4
		if (this.total < 0) {
			this.total = 0
		} else if (this.total > 255) {
			this.total = 255
		}
		this.writeTexel(this.texelIndex++, this.total.toByte())
		this.total = 0
	}
}
