package com.jagex.game.runetek5.config.midi

import com.jagex.game.runetek5.buffer.Packet
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!eha")
class Filter {

	@OriginalMember(owner = "client!eha", name = "a", descriptor = "[I")
	@JvmField
	val numPairs: IntArray = IntArray(2)

	@OriginalMember(owner = "client!eha", name = "f", descriptor = "[I")
	private val unity: IntArray = IntArray(2)

	@OriginalMember(owner = "client!eha", name = "d", descriptor = "[[[I")
	private val pairPhase: Array<Array<IntArray>> = Array(2) { Array(2) { IntArray(4) } }

	@OriginalMember(owner = "client!eha", name = "b", descriptor = "[[[I")
	private val pairMagnitude: Array<Array<IntArray>> = Array(2) { Array(2) { IntArray(4) } }

	@OriginalMember(owner = "client!eha", name = "a", descriptor = "(Lclient!cea;Lclient!ra;)V")
	fun decode(@OriginalArg(0) packet: Packet?, @OriginalArg(1) envelope: Envelope?) {
		@Pc(3) val count: Int = packet!!.readUByte()
		this.numPairs[0] = count shr 4
		this.numPairs[1] = count and 0xF
		if (count == 0) {
			this.unity[1] = 0
			this.unity[0] = 0
			return
		}
		this.unity[0] = packet.readUShort()
		this.unity[1] = packet.readUShort()
		@Pc(37) val varyingMask: Int = packet.readUByte()
		@Pc(42) val local42: Int
		for (@Pc(39) i in 0 until 2) {
			for (pairIndex in 0 until this.numPairs[i]) {
				this.pairPhase[i][0][pairIndex] = packet.readUShort()
				this.pairMagnitude[i][0][pairIndex] = packet.readUShort()
			}
		}
		for (direction in 0 until 2) {
			for (@Pc(81) j in 0 until this.numPairs[direction]) {
				if ((varyingMask and ((0x1 shl (direction * 4)) shl j)) == 0) {
					this.pairPhase[direction][1][j] = this.pairPhase[direction][0][j]
					this.pairMagnitude[direction][1][j] = this.pairMagnitude[direction][0][j]
				} else {
					this.pairPhase[direction][1][j] = packet.readUShort()
					this.pairMagnitude[direction][1][j] = packet.readUShort()
				}
			}
		}
		if (varyingMask != 0 || this.unity[1] != this.unity[0]) {
			envelope!!.decodeShape(packet)
		}
	}

	@OriginalMember(owner = "client!eha", name = "a", descriptor = "(IIF)F")
	private fun adaptPhase(@OriginalArg(0) direction: Int, @OriginalArg(1) phase: Int, @OriginalArg(2) off: Float): Float {
		@Pc(30) val f1: Float = this.pairPhase[direction][0][phase].toFloat() + off * (this.pairPhase[direction][1][phase] - this.pairPhase[direction][0][phase]).toFloat()
		@Pc(34) val f2: Float = f1 * 1.2207031E-4f
		return Filter.normalize(f2)
	}

	@OriginalMember(owner = "client!eha", name = "b", descriptor = "(IIF)F")
	private fun adaptMagnitude(@OriginalArg(0) direction: Int, @OriginalArg(1) phase: Int, @OriginalArg(2) off: Float): Float {
		@Pc(30) val apha1: Float = this.pairMagnitude[direction][0][phase].toFloat() + off * (this.pairMagnitude[direction][1][phase] - this.pairMagnitude[direction][0][phase]).toFloat()
		@Pc(34) val alpha2: Float = apha1 * 0.0015258789f
		return 1.0f - java.lang.Math.pow(10.0, (-alpha2 / 20.0f).toDouble()).toFloat()
	}

	@OriginalMember(owner = "client!eha", name = "a", descriptor = "(IF)I")
	fun compute(@OriginalArg(0) direction: Int, @OriginalArg(1) off: Float): Int {
		@Pc(20) val local20: Float
		if (direction == 0) {
			val unityValue: Float = this.unity[0].toFloat() + (this.unity[1] - this.unity[0]).toFloat() * off
			@Pc(24) val unityDb: Float = unityValue * 0.0030517578f
			Filter.invUnityFloat = java.lang.Math.pow(0.1, (unityDb / 20.0f).toDouble()).toFloat()
			Filter.invUnityInt = (Filter.invUnityFloat * 65536.0f).toInt()
		}
		if (this.numPairs[direction] == 0) {
			return 0
		}
		var magnitude: Float = this.adaptMagnitude(direction, 0, off)
		Filter.COEFFICIENT_FLOAT[direction][0] = -2.0f * magnitude * java.lang.Math.cos(this.adaptPhase(direction, 0, off).toDouble()).toFloat()
		Filter.COEFFICIENT_FLOAT[direction][1] = magnitude * magnitude
		for (@Pc(77) phase in 1 until this.numPairs[direction]) {
			magnitude = this.adaptMagnitude(direction, phase, off)
			@Pc(97) val coeff1: Float = -2.0f * magnitude * java.lang.Math.cos(this.adaptPhase(direction, phase, off).toDouble()).toFloat()
			@Pc(101) val coeff2: Float = magnitude * magnitude
			Filter.COEFFICIENT_FLOAT[direction][phase * 2 + 1] = Filter.COEFFICIENT_FLOAT[direction][phase * 2 - 1] * coeff2
			Filter.COEFFICIENT_FLOAT[direction][phase * 2] = Filter.COEFFICIENT_FLOAT[direction][phase * 2 - 1] * coeff1 + Filter.COEFFICIENT_FLOAT[direction][phase * 2 - 2] * coeff2
			for (@Pc(157) i in phase * 2 - 1 downTo 2) {
				Filter.COEFFICIENT_FLOAT[direction][i] += Filter.COEFFICIENT_FLOAT[direction][i - 1] * coeff1 + Filter.COEFFICIENT_FLOAT[direction][i - 2] * coeff2
			}
			Filter.COEFFICIENT_FLOAT[direction][1] += Filter.COEFFICIENT_FLOAT[direction][0] * coeff1 + coeff2
			Filter.COEFFICIENT_FLOAT[direction][0] += coeff1
		}
		@Pc(226) var coefIndex: Int
		if (direction == 0) {
			coefIndex = 0
			while (coefIndex < this.numPairs[0] * 2) {
				Filter.COEFFICIENT_FLOAT[0][coefIndex] *= Filter.invUnityFloat
				coefIndex++
			}
		}
		coefIndex = 0
		while (coefIndex < this.numPairs[direction] * 2) {
			Filter.COEFFICIENT_INT[direction][coefIndex] = (Filter.COEFFICIENT_FLOAT[direction][coefIndex] * 65536.0f).toInt()
			coefIndex++
		}
		return this.numPairs[direction] * 2
	}

	companion object {

		@OriginalMember(owner = "client!eha", name = "a", descriptor = "(F)F")
		@JvmStatic
		fun normalize(@OriginalArg(0) octaves: Float): Float {
			@Pc(7) val _f: Float = java.lang.Math.pow(2.0, octaves.toDouble()).toFloat() * 32.703197f
			return _f * 3.1415927f / 11025.0f
		}

		@OriginalMember(owner = "client!eha", name = "c", descriptor = "[[I")
		@JvmField
		val COEFFICIENT_INT: Array<IntArray> = Array(2) { IntArray(8) }

		@OriginalMember(owner = "client!eha", name = "e", descriptor = "I")
		@JvmField
		var invUnityInt: Int = 0

		@OriginalMember(owner = "client!eha", name = "g", descriptor = "F")
		@JvmField
		var invUnityFloat: Float = 0.0f

		@OriginalMember(owner = "client!eha", name = "h", descriptor = "[[F")
		@JvmField
		val COEFFICIENT_FLOAT: Array<FloatArray> = Array(2) { FloatArray(8) }
	}
}
