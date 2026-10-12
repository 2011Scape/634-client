package com.jagex.game.runetek5.graphics.texture.water

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.math.pow

@OriginalClass("client!tg")
open class BillowNoiseGenerator : WaterTextureGenerator {

	@OriginalMember(owner = "client!tg", name = "r", descriptor = "I")
	private var accumulator: Int = 0

	@OriginalMember(owner = "client!tg", name = "x", descriptor = "I")
	private var writePosition: Int = 0

	@OriginalMember(owner = "client!tg", name = "y", descriptor = "[B")
	private var outputBuffer: ByteArray? = null

	@OriginalMember(owner = "client!tg", name = "s", descriptor = "[I")
	private val amplitudeWeights: IntArray = IntArray(this.octaveCount)

	@OriginalMember(owner = "client!tg", name = "<init>", descriptor = "(IIIIIF)V")
	constructor(@OriginalArg(0) seed: Int, @OriginalArg(1) factorsOfTwo: Int, @OriginalArg(2) xFactor: Int, @OriginalArg(3) yFactor: Int, @OriginalArg(4) zFactor: Int, @OriginalArg(5) frequency: Float) : super(seed, factorsOfTwo, xFactor, yFactor, zFactor) {
		@Pc(15) var index: Int = 0
		while (index < super.octaveCount) {
			this.amplitudeWeights[index] = ((frequency.toDouble()).pow(index.toDouble()) * 4096.0).toInt().toShort().toInt()
			index++
		}
	}

	@OriginalMember(owner = "client!tg", name = "a", descriptor = "(III)V")
	final override fun accumulateOctave(@OriginalArg(0) noise: Int, @OriginalArg(1) octave: Int) {
		this.accumulator += (this.amplitudeWeights[octave] * noise) shr 12
	}

	@OriginalMember(owner = "client!tg", name = "a", descriptor = "(Z)V")
	final override fun reset() {
		this.accumulator = 0
		this.writePosition = 0
	}

	@OriginalMember(owner = "client!tg", name = "c", descriptor = "(I)V")
	final override fun emitTexel() {
		this.accumulator = kotlin.math.abs(this.accumulator)
		if (this.accumulator >= 4096) {
			this.accumulator = 4095
		}
		this.writeTexel(this.writePosition++, (this.accumulator shr 4).toByte())
		this.accumulator = 0
	}

	@OriginalMember(owner = "client!tg", name = "a", descriptor = "(BIB)V")
	open fun writeTexel(@OriginalArg(1) index: Int, @OriginalArg(2) value: Byte) {
		val buffer: ByteArray? = this.outputBuffer
		val position: Int = this.writePosition++
		buffer!![position] = (((value.toInt() shr 1) and 0x7F) + 127).toByte()
	}
}
