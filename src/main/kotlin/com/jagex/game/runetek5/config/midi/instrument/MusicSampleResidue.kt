package com.jagex.game.runetek5.config.midi.instrument

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.cache.node.VorbisSound

@OriginalClass("client!eda")
class MusicSampleResidue {

	@OriginalMember(owner = "client!eda", name = "c", descriptor = "I")
	private val residueType: Int = MusicSample.getBits(16)

	@OriginalMember(owner = "client!eda", name = "d", descriptor = "I")
	private val residueBegin: Int = MusicSample.getBits(24)

	@OriginalMember(owner = "client!eda", name = "e", descriptor = "I")
	private val residueEnd: Int = MusicSample.getBits(24)

	@OriginalMember(owner = "client!eda", name = "b", descriptor = "I")
	private val partitionSize: Int = MusicSample.getBits(24) + 1

	@OriginalMember(owner = "client!eda", name = "a", descriptor = "I")
	private val classifications: Int = MusicSample.getBits(6) + 1

	@OriginalMember(owner = "client!eda", name = "g", descriptor = "I")
	private val classbook: Int = MusicSample.getBits(8)

	@OriginalMember(owner = "client!eda", name = "f", descriptor = "[I")
	private val residueBooks: IntArray

	@OriginalMember(owner = "client!eda", name = "<init>", descriptor = "()V")
	constructor() {
		@Pc(33) val residueCascade: IntArray = IntArray(this.classifications)
		@Pc(38) val local38: Int
		for (@Pc(35) i in 0 until this.classifications) {
			var highBits: Int = 0
			@Pc(41) val lowBits: Int = MusicSample.getBits(3)
			@Pc(47) val hasHighBits: Boolean = VorbisSound.getBit() != 0
			if (hasHighBits) {
				highBits = MusicSample.getBits(5)
			}
			residueCascade[i] = (highBits shl 3) or lowBits
		}
		this.residueBooks = IntArray(this.classifications * 8)
		for (bookIndex in 0 until this.classifications * 8) {
			this.residueBooks[bookIndex] = if ((residueCascade[bookIndex shr 3] and (0x1 shl (bookIndex and 0x7))) == 0) -1 else MusicSample.getBits(8)
		}
	}

	@OriginalMember(owner = "client!eda", name = "a", descriptor = "([FIZ)V")
	fun decode(@OriginalArg(0) v: FloatArray?, @OriginalArg(1) outputSize: Int, @OriginalArg(2) decode: Boolean) {
		for (@Pc(1) i in 0 until outputSize) {
			v!![i] = 0.0f
		}
		if (decode) {
			return
		}
		@Pc(19) val dimensions: Int = MusicSample.codebooks!![this.classbook]!!.dimensions
		@Pc(25) val windowSize: Int = this.residueEnd - this.residueBegin
		@Pc(30) val numWindows: Int = windowSize / this.partitionSize
		@Pc(33) val partitionClasses: IntArray = IntArray(numWindows)
		for (@Pc(35) j in 0 until 8) {
			@Pc(38) var partitionIndex: Int = 0
			while (partitionIndex < numWindows) {
				@Pc(47) val local47: Int
				@Pc(51) val local51: Int
				if (j == 0) {
					var classWord: Int = MusicSample.codebooks!![this.classbook]!!.decode()
					for (classSlot in dimensions - 1 downTo 0) {
						if (partitionIndex + classSlot < numWindows) {
							partitionClasses[partitionIndex + classSlot] = classWord % this.classifications
						}
						classWord /= this.classifications
					}
				}
				for (partitionStep in 0 until dimensions) {
					val partitionClass: Int = partitionClasses[partitionIndex]
					@Pc(90) val bookIndex: Int = this.residueBooks[partitionClass * 8 + j]
					if (bookIndex >= 0) {
						@Pc(100) val offset: Int = this.residueBegin + partitionIndex * this.partitionSize
						@Pc(104) val vorbisCodebook: MusicSampleCodebook? = MusicSample.codebooks!![bookIndex]
						@Pc(113) val local113: Int
						if (this.residueType == 0) {
							val stride: Int = this.partitionSize / vorbisCodebook!!.dimensions
							for (@Pc(115) k in 0 until stride) {
								@Pc(119) val values: FloatArray = vorbisCodebook.decodeVector()
								for (@Pc(121) m in 0 until vorbisCodebook.dimensions) {
									v!![offset + k + m * stride] += values[m]
								}
							}
						} else {
							var position: Int = 0
							while (position < this.partitionSize) {
								@Pc(153) val values: FloatArray = vorbisCodebook!!.decodeVector()
								for (@Pc(155) dim in 0 until vorbisCodebook.dimensions) {
									v!![offset + position] += values[dim]
									position++
								}
							}
						}
					}
					partitionIndex++
					if (partitionIndex >= numWindows) {
						break
					}
				}
			}
		}
	}
}
