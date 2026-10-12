package com.jagex.game.runetek5.cache.node

import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.cache.node.impl.VariableRateSoundPacket
import com.jagex.game.runetek5.config.midi.instrument.MusicSamepleMapping
import com.jagex.game.runetek5.config.midi.instrument.MusicSample
import com.jagex.game.runetek5.config.midi.instrument.MusicSampleCodebook
import com.jagex.game.runetek5.config.midi.instrument.MusicSampleFloor
import com.jagex.game.runetek5.config.midi.instrument.MusicSampleResidue
import com.jagex.game.runetek5.utils.Utils
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!hp")
class VorbisSound : Node {

	@OriginalMember(owner = "client!hp", name = "p", descriptor = "[F")
	private var previousWindow: FloatArray? = null

	@OriginalMember(owner = "client!hp", name = "q", descriptor = "I")
	private var sampleRate: Int = 0

	@OriginalMember(owner = "client!hp", name = "r", descriptor = "[[B")
	private var packets: Array<ByteArray?>? = null

	@OriginalMember(owner = "client!hp", name = "u", descriptor = "Z")
	private var previousNoDecodeFloor: Boolean = false

	@OriginalMember(owner = "client!hp", name = "E", descriptor = "I")
	private var maxBitRate: Int = 0

	@OriginalMember(owner = "client!hp", name = "F", descriptor = "I")
	private var minBitRate: Int = 0

	@OriginalMember(owner = "client!hp", name = "L", descriptor = "I")
	private var nominalBitRate: Int = 0

	@OriginalMember(owner = "client!hp", name = "P", descriptor = "I")
	private var previousWindowEnd: Int = 0

	@OriginalMember(owner = "client!hp", name = "Q", descriptor = "Z")
	private var negativeBitRate: Boolean = false

	@OriginalMember(owner = "client!hp", name = "S", descriptor = "I")
	private var previousWindowSize: Int = 0

	@OriginalMember(owner = "client!hp", name = "T", descriptor = "[B")
	private var packetData: ByteArray? = null

	@OriginalMember(owner = "client!hp", name = "U", descriptor = "I")
	private var currentPacket: Int = 0

	@OriginalMember(owner = "client!hp", name = "V", descriptor = "I")
	private var currentOff: Int = 0

	@OriginalMember(owner = "client!hp", name = "<init>", descriptor = "([B)V")
	constructor(@OriginalArg(0) bytes: ByteArray?) {
		this.decode(bytes)
	}

	@OriginalMember(owner = "client!hp", name = "a", descriptor = "([B)V")
	private fun decode(@OriginalArg(0) data: ByteArray?) {
		@Pc(4) val buffer: Packet = Packet(data)
		this.sampleRate = buffer.readInt()
		this.maxBitRate = buffer.readInt()
		this.nominalBitRate = buffer.readInt()
		this.minBitRate = buffer.readInt()
		if (this.minBitRate < 0) {
			this.minBitRate = this.minBitRate.inv()
			this.negativeBitRate = true
		}
		@Pc(40) val count: Int = buffer.readInt()
		this.packets = arrayOfNulls<ByteArray>(count)
		@Pc(46) var i: Int = 0
		while (i < count) {
			@Pc(49) var totalSize: Int = 0
			@Pc(53) var size: Int
			do {
				size = buffer.readUByte()
				totalSize += size
			} while (size >= 255)
			@Pc(64) val packet: ByteArray = ByteArray(totalSize)
			buffer.readBytes(0, totalSize, packet)
			this.packets!![i] = packet
			i++
		}
	}

	@OriginalMember(owner = "client!hp", name = "a", descriptor = "([I)Lclient!ob;")
	fun toPcmSound(@OriginalArg(0) sampleBudget: IntArray?): VariableRateSoundPacket? {
		if (sampleBudget != null && sampleBudget[0] <= 0) {
			return null
		}
		if (this.packetData == null) {
			this.previousWindowSize = 0
			this.previousWindow = FloatArray(VorbisSound.blockSize1)
			this.packetData = ByteArray(this.maxBitRate)
			this.currentOff = 0
			this.currentPacket = 0
		}
		while (this.currentPacket < this.packets!!.size) {
			if (sampleBudget != null && sampleBudget[0] <= 0) {
				return null
			}
			@Pc(42) val samples: FloatArray? = this.decode(this.currentPacket)
			if (samples != null) {
				@Pc(47) var currentOff: Int = this.currentOff
				@Pc(50) var count: Int = samples.size
				if (count > this.maxBitRate - currentOff) {
					count = this.maxBitRate - currentOff
				}
				@Pc(63) var i: Int = 0
				while (i < count) {
					@Pc(73) var sample: Int = (samples[i] * 128.0f + 128.0f).toInt()
					if ((sample and 0xFFFFFF00.toInt()) != 0) {
						sample = sample.inv() shr 31
					}
					this.packetData!![currentOff++] = (sample - 128).toByte()
					i++
				}
				if (sampleBudget != null) {
					sampleBudget[0] -= currentOff - this.currentOff
				}
				this.currentOff = currentOff
			}
			this.currentPacket++
		}
		this.previousWindow = null
		@Pc(129) val packetData: ByteArray? = this.packetData
		this.packetData = null
		return VariableRateSoundPacket(this.sampleRate, packetData, this.nominalBitRate, this.minBitRate, this.negativeBitRate)
	}

	@OriginalMember(owner = "client!hp", name = "d", descriptor = "(I)[F")
	private fun decode(@OriginalArg(0) packet: Int): FloatArray? {
		VorbisSound.setData(this.packets!![packet])
		VorbisSound.getBit()
		@Pc(15) val mode: Int = MusicSample.getBits(Utils.bitLength(VorbisSound.modes!!.size - 1))
		@Pc(19) val blockFlag: Boolean = VorbisSound.noResidues!![mode]
		@Pc(25) val blockSize: Int = if (blockFlag) VorbisSound.blockSize1 else VorbisSound.blockSize0
		@Pc(27) var prevBlockFlag: Boolean = false
		@Pc(29) var nextBlockFlag: Boolean = false
		if (blockFlag) {
			prevBlockFlag = VorbisSound.getBit() != 0
			nextBlockFlag = VorbisSound.getBit() != 0
		}
		@Pc(47) val halfBlockSize: Int = blockSize shr 1
		@Pc(59) val leftWindowStart: Int
		@Pc(67) val leftWindowEnd: Int
		@Pc(71) val leftWindowSize: Int
		if (blockFlag && !prevBlockFlag) {
			leftWindowStart = (blockSize shr 2) - (VorbisSound.blockSize0 shr 2)
			leftWindowEnd = (blockSize shr 2) + (VorbisSound.blockSize0 shr 2)
			leftWindowSize = VorbisSound.blockSize0 shr 1
		} else {
			leftWindowStart = 0
			leftWindowEnd = halfBlockSize
			leftWindowSize = blockSize shr 1
		}
		@Pc(94) val rightWindowStart: Int
		@Pc(104) val rightWindowEnd: Int
		@Pc(108) val rightWindowSize: Int
		if (blockFlag && !nextBlockFlag) {
			rightWindowStart = blockSize - (blockSize shr 2) - (VorbisSound.blockSize0 shr 2)
			rightWindowEnd = blockSize + (VorbisSound.blockSize0 shr 2) - (blockSize shr 2)
			rightWindowSize = VorbisSound.blockSize0 shr 1
		} else {
			rightWindowStart = halfBlockSize
			rightWindowEnd = blockSize
			rightWindowSize = blockSize shr 1
		}
		@Pc(123) val vorbisMapping: MusicSamepleMapping? = MusicSample.mappings!![VorbisSound.modes!![mode]]
		@Pc(126) val mux: Int = vorbisMapping!!.mux
		@Pc(131) val floorIndex: Int = vorbisMapping.submapFloors[mux]
		@Pc(140) val floorUnused: Boolean = !MusicSample.floors!![floorIndex]!!.decode()
		@Pc(154) var local154: FloatArray?
		@Pc(144) var i: Int = 0
		while (i < vorbisMapping.submaps) {
			@Pc(152) val vorbisResidue: MusicSampleResidue? = MusicSample.residues!![vorbisMapping.submapResidues[i]]
			val residueWindow: FloatArray? = VorbisSound.window
			vorbisResidue!!.decode(residueWindow, blockSize shr 1, floorUnused)
			i++
		}
		@Pc(171) var local171: Int
		@Pc(176) var local176: Int
		if (!floorUnused) {
			val mappingMux: Int = vorbisMapping.mux
			val floorId: Int = vorbisMapping.submapFloors[mappingMux]
			MusicSample.floors!![floorId]!!.computeFloor(VorbisSound.window, blockSize shr 1)
		}
		@Pc(212) var local212: Int
		if (floorUnused) {
			var sampleIndex: Int = blockSize shr 1
			while (sampleIndex < blockSize) {
				VorbisSound.window!![sampleIndex] = 0.0f
				sampleIndex++
			}
		} else {
			val halfLength: Int = blockSize shr 1
			val quarterLength: Int = blockSize shr 2
			val eighthLength: Int = blockSize shr 3
			@Pc(214) val window: FloatArray = VorbisSound.window!!
			@Pc(216) var j: Int = 0
			while (j < halfLength) {
				window[j] *= 0.5f
				j++
			}
			@Pc(230) var k: Int = halfLength
			while (k < blockSize) {
				window[k] = -window[blockSize - k - 1]
				k++
			}
			@Pc(252) val tableA: FloatArray = (if (blockFlag) VorbisSound.A_1 else VorbisSound.A_0)!!
			@Pc(258) val tableB: FloatArray = (if (blockFlag) VorbisSound.B_1 else VorbisSound.B_0)!!
			@Pc(264) val tableC: FloatArray = (if (blockFlag) VorbisSound.C_1 else VorbisSound.C_0)!!
			@Pc(270) val bitReverse: IntArray = (if (blockFlag) VorbisSound.BITREVERSE_1 else VorbisSound.BITREVERSE_0)!!
			@Pc(307) var local307: Float
			@Pc(313) var local313: Float
			@Pc(321) var local321: Float
			@Pc(272) var m: Int = 0
			while (m < quarterLength) {
				@Pc(289) val diff: Float = window[m * 4] - window[blockSize - m * 4 - 1]
				val diff2: Float = window[m * 4 + 2] - window[blockSize - m * 4 - 3]
				val tableCos: Float = tableA[m * 2]
				val tableSin: Float = tableA[m * 2 + 1]
				window[blockSize - m * 4 - 1] = diff * tableCos - diff2 * tableSin
				window[blockSize - m * 4 - 3] = diff * tableSin + diff2 * tableCos
				m++
			}
			@Pc(359) var butterflyIndex: Int = 0
			while (butterflyIndex < eighthLength) {
				val high3: Float = window[halfLength + butterflyIndex * 4 + 3]
				val high1: Float = window[halfLength + butterflyIndex * 4 + 1]
				val low3: Float = window[butterflyIndex * 4 + 3]
				@Pc(396) val low1: Float = window[butterflyIndex * 4 + 1]
				window[halfLength + butterflyIndex * 4 + 3] = high3 + low3
				window[halfLength + butterflyIndex * 4 + 1] = high1 + low1
				@Pc(430) val cosA: Float = tableA[halfLength - butterflyIndex * 4 - 4]
				@Pc(440) val sinA: Float = tableA[halfLength - butterflyIndex * 4 - 3]
				window[butterflyIndex * 4 + 3] = (high3 - low3) * cosA - (high1 - low1) * sinA
				window[butterflyIndex * 4 + 1] = (high1 - low1) * cosA + (high3 - low3) * sinA
				butterflyIndex++
			}
			@Pc(486) val log2BlockSize: Int = Utils.bitLength(blockSize - 1)
			@Pc(520) var local520: Int
			@Pc(522) var local522: Int
			@Pc(527) var local527: Int
			@Pc(535) var local535: Float
			@Pc(543) var local543: Float
			@Pc(551) var local551: Float
			@Pc(559) var local559: Float
			@Pc(585) var local585: Float
			@Pc(495) var local495: Int
			@Pc(499) var local499: Int
			@Pc(501) var local501: Int
			@Pc(510) var local510: Int
			@Pc(488) var stage: Int = 0
			while (stage < log2BlockSize - 3) {
				val stageSpan: Int = blockSize shr (stage + 2)
				val twiddleStep: Int = 0x8 shl stage
				var groupIndex: Int = 0
				while (groupIndex < (0x2 shl stage)) {
					val upperBase: Int = blockSize - stageSpan * 2 * groupIndex
					val lowerBase: Int = blockSize - stageSpan * (groupIndex * 2 + 1)
					var pairIndex: Int = 0
					while (pairIndex < (blockSize shr (stage + 4))) {
						val pairOffset: Int = pairIndex * 4
						val upper1: Float = window[upperBase - pairOffset - 1]
						val upper3: Float = window[upperBase - pairOffset - 3]
						val lower1: Float = window[lowerBase - pairOffset - 1]
						val lower3: Float = window[lowerBase - pairOffset - 3]
						window[upperBase - pairOffset - 1] = upper1 + lower1
						window[upperBase - pairOffset - 3] = upper3 + lower3
						val twiddleCos: Float = tableA[pairIndex * twiddleStep]
						@Pc(593) val twiddleSin: Float = tableA[pairIndex * twiddleStep + 1]
						window[lowerBase - pairOffset - 1] = (upper1 - lower1) * twiddleCos - (upper3 - lower3) * twiddleSin
						window[lowerBase - pairOffset - 3] = (upper3 - lower3) * twiddleCos + (upper1 - lower1) * twiddleSin
						pairIndex++
					}
					groupIndex++
				}
				stage++
			}
			@Pc(673) var local673: Float
			var bitIndex: Int = 1
			while (bitIndex < eighthLength - 1) {
				val reversedIndex: Int = bitReverse[bitIndex]
				if (bitIndex < reversedIndex) {
					val offsetA: Int = bitIndex * 8
					val offsetB: Int = reversedIndex * 8
					val swap1: Float = window[offsetA + 1]
					window[offsetA + 1] = window[offsetB + 1]
					window[offsetB + 1] = swap1
					@Pc(695) val swap3: Float = window[offsetA + 3]
					window[offsetA + 3] = window[offsetB + 3]
					window[offsetB + 3] = swap3
					@Pc(717) val swap5: Float = window[offsetA + 5]
					window[offsetA + 5] = window[offsetB + 5]
					window[offsetB + 5] = swap5
					@Pc(739) val swap7: Float = window[offsetA + 7]
					window[offsetA + 7] = window[offsetB + 7]
					window[offsetB + 7] = swap7
				}
				bitIndex++
			}
			var compactIndex: Int = 0
			while (compactIndex < halfLength) {
				window[compactIndex] = window[compactIndex * 2 + 1]
				compactIndex++
			}
			var unpackIndex: Int = 0
			while (unpackIndex < eighthLength) {
				window[blockSize - unpackIndex * 2 - 1] = window[unpackIndex * 4]
				window[blockSize - unpackIndex * 2 - 2] = window[unpackIndex * 4 + 1]
				window[blockSize - quarterLength - unpackIndex * 2 - 1] = window[unpackIndex * 4 + 2]
				window[blockSize - quarterLength - unpackIndex * 2 - 2] = window[unpackIndex * 4 + 3]
				unpackIndex++
			}
			var twiddleIndex: Int = 0
			while (twiddleIndex < eighthLength) {
				val tableCCos: Float = tableC[twiddleIndex * 2]
				@Pc(868) val tableCSin: Float = tableC[twiddleIndex * 2 + 1]
				@Pc(876) val valueA: Float = window[halfLength + twiddleIndex * 2]
				val valueB: Float = window[halfLength + twiddleIndex * 2 + 1]
				val valueC: Float = window[blockSize - twiddleIndex * 2 - 2]
				val valueD: Float = window[blockSize - twiddleIndex * 2 - 1]
				var rotated: Float = tableCSin * (valueA - valueC) + tableCCos * (valueB + valueD)
				window[halfLength + twiddleIndex * 2] = (valueA + valueC + rotated) * 0.5f
				window[blockSize - twiddleIndex * 2 - 2] = (valueA + valueC - rotated) * 0.5f
				rotated = tableCSin * (valueB + valueD) - tableCCos * (valueA - valueC)
				window[halfLength + twiddleIndex * 2 + 1] = (valueB + rotated - valueD) * 0.5f
				window[blockSize - twiddleIndex * 2 - 1] = (valueD + rotated - valueB) * 0.5f
				twiddleIndex++
			}
			var rotateIndex: Int = 0
			while (rotateIndex < quarterLength) {
				window[rotateIndex] = window[rotateIndex * 2 + halfLength] * tableB[rotateIndex * 2] + window[rotateIndex * 2 + halfLength + 1] * tableB[rotateIndex * 2 + 1]
				window[halfLength - rotateIndex - 1] = window[rotateIndex * 2 + halfLength] * tableB[rotateIndex * 2 + 1] - window[rotateIndex * 2 + halfLength + 1] * tableB[rotateIndex * 2]
				rotateIndex++
			}
			var negateIndex: Int = 0
			while (negateIndex < quarterLength) {
				window[blockSize + negateIndex - quarterLength] = -window[negateIndex]
				negateIndex++
			}
			var shiftIndex: Int = 0
			while (shiftIndex < quarterLength) {
				window[shiftIndex] = window[quarterLength + shiftIndex]
				shiftIndex++
			}
			@Pc(1111) var reflectIndex: Int = 0
			while (reflectIndex < quarterLength) {
				window[quarterLength + reflectIndex] = -window[quarterLength - reflectIndex - 1]
				reflectIndex++
			}
			@Pc(1131) var copyIndex: Int = 0
			while (copyIndex < quarterLength) {
				window[halfLength + copyIndex] = window[blockSize - copyIndex - 1]
				copyIndex++
			}
			@Pc(1150) var leftIndex: Int = leftWindowStart
			while (leftIndex < leftWindowEnd) {
				val leftCurve: Float = java.lang.Math.sin(((leftIndex - leftWindowStart).toDouble() + 0.5) / leftWindowSize.toDouble() * 0.5 * 3.141592653589793).toFloat()
				VorbisSound.window!![leftIndex] *= java.lang.Math.sin(leftCurve.toDouble() * 1.5707963267948966 * leftCurve.toDouble()).toFloat()
				leftIndex++
			}
			@Pc(1188) var rightIndex: Int = rightWindowStart
			while (rightIndex < rightWindowEnd) {
				val rightCurve: Float = java.lang.Math.sin(((rightIndex - rightWindowStart).toDouble() + 0.5) / rightWindowSize.toDouble() * 0.5 * 3.141592653589793 + 1.5707963267948966).toFloat()
				VorbisSound.window!![rightIndex] *= java.lang.Math.sin(rightCurve.toDouble() * 1.5707963267948966 * rightCurve.toDouble()).toFloat()
				rightIndex++
			}
		}
		@Pc(1228) var output: FloatArray? = null
		if (this.previousWindowSize > 0) {
			val outputLength: Int = (this.previousWindowSize + blockSize) shr 2
			output = FloatArray(outputLength)
			@Pc(1254) var local1254: Int
			if (!this.previousNoDecodeFloor) {
				var prevIndex: Int = 0
				while (prevIndex < this.previousWindowEnd) {
					val prevWindowIndex: Int = (this.previousWindowSize shr 1) + prevIndex
					output[prevIndex] += this.previousWindow!![prevWindowIndex]
					prevIndex++
				}
			}
			if (!floorUnused) {
				var sampleIndex: Int = leftWindowStart
				while (sampleIndex < (blockSize shr 1)) {
					val outputIndex: Int = output.size + sampleIndex - (blockSize shr 1)
					output[outputIndex] += VorbisSound.window!![sampleIndex]
					sampleIndex++
				}
			}
		}
		val recycledWindow: FloatArray? = this.previousWindow
		this.previousWindow = VorbisSound.window
		VorbisSound.window = recycledWindow
		this.previousWindowSize = blockSize
		this.previousWindowEnd = rightWindowEnd - (blockSize shr 1)
		this.previousNoDecodeFloor = floorUnused
		return output
	}

	companion object {
		@OriginalMember(owner = "client!hp", name = "A", descriptor = "[F")
		@JvmField
		var A_1: FloatArray? = null

		@OriginalMember(owner = "client!hp", name = "B", descriptor = "[Z")
		@JvmField
		var noResidues: BooleanArray? = null

		@OriginalMember(owner = "client!hp", name = "C", descriptor = "[B")
		@JvmField
		var instrumentData: ByteArray? = null

		@OriginalMember(owner = "client!hp", name = "H", descriptor = "[I")
		@JvmField
		var BITREVERSE_0: IntArray? = null

		@OriginalMember(owner = "client!hp", name = "I", descriptor = "[F")
		@JvmField
		var B_0: FloatArray? = null

		@OriginalMember(owner = "client!hp", name = "J", descriptor = "I")
		@JvmField
		var blockSize1: Int = 0

		@OriginalMember(owner = "client!hp", name = "K", descriptor = "[F")
		@JvmField
		var A_0: FloatArray? = null

		@OriginalMember(owner = "client!hp", name = "M", descriptor = "Z")
		@JvmField
		var fullyCached: Boolean = false

		@OriginalMember(owner = "client!hp", name = "N", descriptor = "I")
		@JvmField
		var blockSize0: Int = 0

		@OriginalMember(owner = "client!hp", name = "O", descriptor = "[F")
		@JvmField
		var B_1: FloatArray? = null

		@OriginalMember(owner = "client!hp", name = "R", descriptor = "I")
		@JvmField
		var pos: Int = 0

		@OriginalMember(owner = "client!hp", name = "a", descriptor = "(I)F")
		@JvmStatic
		fun float32Unpack(@OriginalArg(0) v: Int): Float {
			@Pc(3) var mantissa: Int = v and 0x1FFFFF
			@Pc(7) val sign: Int = v and Int.MIN_VALUE
			@Pc(13) val exponent: Int = (v shr 21) and 0x3FF
			if (sign != 0) {
				mantissa = -mantissa
			}
			return (mantissa.toDouble() * java.lang.Math.pow(2.0, (exponent - 788).toDouble())).toFloat()
		}

		@OriginalMember(owner = "client!hp", name = "a", descriptor = "([BI)V")
		@JvmStatic
		fun setData(@OriginalArg(0) data: ByteArray?) {
			instrumentData = data
			pos = 0
			bitPos = 0
		}

		@OriginalMember(owner = "client!hp", name = "b", descriptor = "()I")
		@JvmStatic
		fun getBit(): Int {
			@Pc(7) val value: Int = (instrumentData!![pos].toInt() shr bitPos) and 0x1
			bitPos++
			pos += bitPos shr 3
			bitPos = bitPos and 0x7
			return value
		}

		@OriginalMember(owner = "client!hp", name = "b", descriptor = "([B)V")
		@JvmStatic
		fun decodeHeader(@OriginalArg(0) header: ByteArray?) {
			setData(header)
			blockSize0 = 0x1 shl MusicSample.getBits(4)
			blockSize1 = 0x1 shl MusicSample.getBits(4)
			window = FloatArray(blockSize1)
			@Pc(24) var blockSize: Int
			@Pc(28) var halfSize: Int
			@Pc(32) var quarterSize: Int
			@Pc(36) var eighthSize: Int
			@Pc(41) var indexA: Int
			@Pc(86) var indexB: Int
			@Pc(138) var indexC: Int
			@Pc(191) var bitCount: Int
			@Pc(17) var i: Int = 0
			while (i < 2) {
				blockSize = if (i == 0) blockSize0 else blockSize1
				halfSize = blockSize shr 1
				quarterSize = blockSize shr 2
				eighthSize = blockSize shr 3
				@Pc(39) val tableA: FloatArray = FloatArray(halfSize)
				indexA = 0
				while (indexA < quarterSize) {
					tableA[indexA * 2] = java.lang.Math.cos((indexA * 4).toDouble() * 3.141592653589793 / blockSize.toDouble()).toFloat()
					tableA[indexA * 2 + 1] = -(java.lang.Math.sin((indexA * 4).toDouble() * 3.141592653589793 / blockSize.toDouble()).toFloat())
					indexA++
				}
				@Pc(84) val tableB: FloatArray = FloatArray(halfSize)
				indexB = 0
				while (indexB < quarterSize) {
					tableB[indexB * 2] = java.lang.Math.cos((indexB * 2 + 1).toDouble() * 3.141592653589793 / (blockSize * 2).toDouble()).toFloat()
					tableB[indexB * 2 + 1] = java.lang.Math.sin((indexB * 2 + 1).toDouble() * 3.141592653589793 / (blockSize * 2).toDouble()).toFloat()
					indexB++
				}
				@Pc(136) val tableC: FloatArray = FloatArray(quarterSize)
				indexC = 0
				while (indexC < eighthSize) {
					tableC[indexC * 2] = java.lang.Math.cos((indexC * 4 + 2).toDouble() * 3.141592653589793 / blockSize.toDouble()).toFloat()
					tableC[indexC * 2 + 1] = -(java.lang.Math.sin((indexC * 4 + 2).toDouble() * 3.141592653589793 / blockSize.toDouble()).toFloat())
					indexC++
				}
				@Pc(185) val bitReverse: IntArray = IntArray(eighthSize)
				bitCount = Utils.bitLength(eighthSize - 1)
				@Pc(193) var j: Int = 0
				while (j < eighthSize) {
					bitReverse[j] = MusicSample.reverseBits(bitCount, j)
					j++
				}
				if (i == 0) {
					A_0 = tableA
					B_0 = tableB
					C_0 = tableC
					BITREVERSE_0 = bitReverse
				} else {
					A_1 = tableA
					B_1 = tableB
					C_1 = tableC
					BITREVERSE_1 = bitReverse
				}
				i++
			}
			blockSize = MusicSample.getBits(8) + 1
			MusicSample.codebooks = arrayOfNulls<MusicSampleCodebook>(blockSize)
			halfSize = 0
			while (halfSize < blockSize) {
				MusicSample.codebooks!![halfSize] = MusicSampleCodebook()
				halfSize++
			}
			quarterSize = MusicSample.getBits(6) + 1
			eighthSize = 0
			while (eighthSize < quarterSize) {
				MusicSample.getBits(16)
				eighthSize++
			}
			@Pc(269) val floorCount: Int = MusicSample.getBits(6) + 1
			MusicSample.floors = arrayOfNulls<MusicSampleFloor>(floorCount)
			indexA = 0
			while (indexA < floorCount) {
				MusicSample.floors!![indexA] = MusicSampleFloor()
				indexA++
			}
			@Pc(290) val residueCount: Int = MusicSample.getBits(6) + 1
			MusicSample.residues = arrayOfNulls<MusicSampleResidue>(residueCount)
			indexB = 0
			while (indexB < residueCount) {
				MusicSample.residues!![indexB] = MusicSampleResidue()
				indexB++
			}
			@Pc(311) val mappingCount: Int = MusicSample.getBits(6) + 1
			MusicSample.mappings = arrayOfNulls<MusicSamepleMapping>(mappingCount)
			indexC = 0
			while (indexC < mappingCount) {
				MusicSample.mappings!![indexC] = MusicSamepleMapping()
				indexC++
			}
			@Pc(332) val modeCount: Int = MusicSample.getBits(6) + 1
			noResidues = BooleanArray(modeCount)
			modes = IntArray(modeCount)
			bitCount = 0
			while (bitCount < modeCount) {
				noResidues!![bitCount] = getBit() != 0
				MusicSample.getBits(16)
				MusicSample.getBits(16)
				modes!![bitCount] = MusicSample.getBits(8)
				bitCount++
			}
			fullyCached = true
		}

		@OriginalMember(owner = "client!hp", name = "o", descriptor = "[F")
		@JvmField
		var C_1: FloatArray? = null

		@OriginalMember(owner = "client!hp", name = "t", descriptor = "[I")
		@JvmField
		var BITREVERSE_1: IntArray? = null

		@OriginalMember(owner = "client!hp", name = "v", descriptor = "[F")
		@JvmField
		var window: FloatArray? = null

		@OriginalMember(owner = "client!hp", name = "w", descriptor = "[F")
		@JvmField
		var C_0: FloatArray? = null

		@OriginalMember(owner = "client!hp", name = "x", descriptor = "[I")
		@JvmField
		var modes: IntArray? = null

		@OriginalMember(owner = "client!hp", name = "z", descriptor = "I")
		@JvmField
		var bitPos: Int = 0
	}
}
