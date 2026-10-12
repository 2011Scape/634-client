package com.jagex.game.runetek5.config.midi.instrument

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.cache.node.VorbisSound
import com.jagex.game.runetek5.utils.Utils
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic
import kotlin.math.pow

@OriginalClass("client!fq")
class MusicSampleCodebook {

	@OriginalMember(owner = "client!fq", name = "f", descriptor = "[I")
	private var huffmanTree: IntArray? = null

	@OriginalMember(owner = "client!fq", name = "a", descriptor = "I")
	@JvmField
	val dimensions: Int

	@OriginalMember(owner = "client!fq", name = "e", descriptor = "I")
	private val entries: Int

	@OriginalMember(owner = "client!fq", name = "c", descriptor = "[I")
	private val lengths: IntArray

	@OriginalMember(owner = "client!fq", name = "d", descriptor = "[I")
	private var mults: IntArray? = null

	@OriginalMember(owner = "client!fq", name = "b", descriptor = "[[F")
	private var multiplicands: Array<FloatArray>? = null

	@OriginalMember(owner = "client!fq", name = "<init>", descriptor = "()V")
	constructor() {
		MusicSample.getBits(24)
		this.dimensions = MusicSample.getBits(16)
		this.entries = MusicSample.getBits(24)
		this.lengths = IntArray(this.entries)
		@Pc(23) val ordered: Boolean = VorbisSound.getBit() != 0
		@Pc(27) val local27: Int
		@Pc(32) val local32: Int
		@Pc(43) val local43: Int
		if (ordered) {
			var currentEntry: Int = 0
			var currentLength: Int = MusicSample.getBits(5) + 1
			while (currentEntry < this.entries) {
				@Pc(41) val runLength: Int = MusicSample.getBits(Utils.bitLength(this.entries - currentEntry))
				for (runIndex in 0 until runLength) {
					this.lengths[currentEntry++] = currentLength
				}
				currentLength++
			}
		} else {
			@Pc(66) val sparse: Boolean = VorbisSound.getBit() != 0
			for (entryIndex in 0 until this.entries) {
				if (sparse && VorbisSound.getBit() == 0) {
					this.lengths[entryIndex] = 0
				} else {
					this.lengths[entryIndex] = MusicSample.getBits(5) + 1
				}
			}
		}
		this.computeAcceleratedHuffman()
		val lookupType: Int = MusicSample.getBits(4)
		if (lookupType > 0) {
			@Pc(103) val minimumValue: Float = VorbisSound.float32Unpack(MusicSample.getBits(32))
			@Pc(107) val deltaValue: Float = VorbisSound.float32Unpack(MusicSample.getBits(32))
			val valueBits: Int = MusicSample.getBits(4) + 1
			@Pc(118) val sequenceP: Boolean = VorbisSound.getBit() != 0
			@Pc(127) val lookupValues: Int
			if (lookupType == 1) {
				lookupValues = MusicSampleCodebook.lookup1Values(this.entries, this.dimensions)
			} else {
				lookupValues = this.entries * this.dimensions
			}
			this.mults = IntArray(lookupValues)
			for (@Pc(140) i in 0 until lookupValues) {
				this.mults!![i] = MusicSample.getBits(valueBits)
			}
			this.multiplicands = Array(this.entries) { FloatArray(this.dimensions) }
			@Pc(163) var entry: Int
			@Pc(166) var last: Float
			@Pc(168) val local168: Int
			@Pc(170) var dim: Int
			if (lookupType == 1) {
				entry = 0
				while (entry < this.entries) {
					last = 0.0f
					var divisor: Int = 1
					dim = 0
					while (dim < this.dimensions) {
						@Pc(177) val lookupOffset: Int = entry / divisor % lookupValues
						@Pc(189) val value: Float = this.mults!![lookupOffset].toFloat() * deltaValue + minimumValue + last
						this.multiplicands!![entry][dim] = value
						if (sequenceP) {
							last = value
						}
						divisor *= lookupValues
						dim++
					}
					entry++
				}
				return
			}
			entry = 0
			while (entry < this.entries) {
				last = 0.0f
				var multIndex: Int = entry * this.dimensions
				dim = 0
				while (dim < this.dimensions) {
					@Pc(240) val value: Float = this.mults!![multIndex].toFloat() * deltaValue + minimumValue + last
					this.multiplicands!![entry][dim] = value
					if (sequenceP) {
						last = value
					}
					multIndex++
					dim++
				}
				entry++
			}
		}
	}

	@OriginalMember(owner = "client!fq", name = "a", descriptor = "()V")
	private fun computeAcceleratedHuffman() {
		@Pc(3) val codewords: IntArray = IntArray(this.entries)
		@Pc(6) val available: IntArray = IntArray(33)
		@Pc(14) val local14: Int
		@Pc(22) val local22: Int
		@Pc(26) val local26: Int
		@Pc(40) val local40: Int
		@Pc(49) val local49: Int
		@Pc(54) val local54: Int
		@Pc(63) var mask: Int
		for (@Pc(8) i in 0 until this.entries) {
			val length: Int = this.lengths[i]
			if (length != 0) {
				val lengthBit: Int = 0x1 shl (32 - length)
				val code: Int = available[length]
				codewords[i] = code
				val nextCode: Int
				if ((code and lengthBit) == 0) {
					nextCode = code or lengthBit
					for (shorterLength in length - 1 downTo 1) {
						val availableCode: Int = available[shorterLength]
						if (availableCode != code) {
							break
						}
						mask = 0x1 shl (32 - shorterLength)
						if ((availableCode and mask) != 0) {
							available[shorterLength] = available[shorterLength - 1]
							break
						}
						available[shorterLength] = availableCode or mask
					}
				} else {
					nextCode = available[length - 1]
				}
				available[length] = nextCode
				for (longerLength in length + 1..32) {
					val availableCode: Int = available[longerLength]
					if (availableCode == code) {
						available[longerLength] = nextCode
					}
				}
			}
		}
		this.huffmanTree = IntArray(8)
		var nextFreeNode: Int = 0
		for (entryIndex in 0 until this.entries) {
			val length: Int = this.lengths[entryIndex]
			if (length != 0) {
				val codeword: Int = codewords[entryIndex]
				var node: Int = 0
				for (bitIndex in 0 until length) {
					mask = Int.MIN_VALUE ushr bitIndex
					if ((codeword and mask) == 0) {
						node++
					} else {
						if (this.huffmanTree!![node] == 0) {
							this.huffmanTree!![node] = nextFreeNode
						}
						node = this.huffmanTree!![node]
					}
					if (node >= this.huffmanTree!!.size) {
						@Pc(178) val grownTree: IntArray = IntArray(this.huffmanTree!!.size * 2)
						for (@Pc(180) j in 0 until this.huffmanTree!!.size) {
							grownTree[j] = this.huffmanTree!![j]
						}
						this.huffmanTree = grownTree
					}
				}
				this.huffmanTree!![node] = entryIndex.inv()
				if (node >= nextFreeNode) {
					nextFreeNode = node + 1
				}
			}
		}
	}

	@OriginalMember(owner = "client!fq", name = "b", descriptor = "()I")
	fun decode(): Int {
		@Pc(1) var i: Int = 0
		while (this.huffmanTree!![i] >= 0) {
			i = if (VorbisSound.getBit() == 0) i + 1 else this.huffmanTree!![i]
		}
		return this.huffmanTree!![i].inv()
	}

	@OriginalMember(owner = "client!fq", name = "c", descriptor = "()[F")
	fun decodeVector(): FloatArray {
		return this.multiplicands!![this.decode()]
	}

	companion object {
		@OriginalMember(owner = "client!fq", name = "a", descriptor = "(II)I")
		@JvmStatic
		fun lookup1Values(@OriginalArg(0) entries: Int, @OriginalArg(1) dimensions: Int): Int {
			@Pc(10) var values: Int = (entries.toDouble()).pow(1.0 / dimensions.toDouble()).toInt() + 1
			while (Utils.intPow(dimensions, values) > entries) {
				values--
			}
			return values
		}
	}
}
