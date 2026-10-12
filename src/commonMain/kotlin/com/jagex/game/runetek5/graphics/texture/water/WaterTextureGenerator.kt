package com.jagex.game.runetek5.graphics.texture.water

import com.jagex.game.runetek5.EnvironmentContext
import com.jagex.game.runetek5.utils.Utils
import util.Random
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic
import kotlin.math.pow

@OriginalClass("client!jp")
abstract class WaterTextureGenerator {

	@OriginalMember(owner = "client!jp", name = "b", descriptor = "[S")
	private var octaveWeights: ShortArray? = null

	@OriginalMember(owner = "client!jp", name = "g", descriptor = "I")
	private var scaleZ: Int = 4

	@OriginalMember(owner = "client!jp", name = "d", descriptor = "I")
	@JvmField
	var octaveCount: Int = 4

	@OriginalMember(owner = "client!jp", name = "n", descriptor = "I")
	private var seed: Int = 0

	@OriginalMember(owner = "client!jp", name = "o", descriptor = "[S")
	private val permutationTable: ShortArray = ShortArray(512)

	@OriginalMember(owner = "client!jp", name = "k", descriptor = "I")
	private var scaleY: Int = 4

	@OriginalMember(owner = "client!jp", name = "l", descriptor = "I")
	private var scaleX: Int = 4

	@OriginalMember(owner = "client!jp", name = "<init>", descriptor = "(IIIII)V")
	constructor(@OriginalArg(0) seed: Int, @OriginalArg(1) factorsOfTwo: Int, @OriginalArg(2) xFactor: Int, @OriginalArg(3) yFactor: Int, @OriginalArg(4) zFactor: Int) {
		this.octaveCount = factorsOfTwo
		this.scaleY = yFactor
		this.scaleX = xFactor
		this.seed = seed
		this.scaleZ = zFactor
		this.buildOctaveFrequencies()
		this.shufflePermutations()
	}

	@OriginalMember(owner = "client!jp", name = "a", descriptor = "(I)V")
	private fun shufflePermutations() {
		@Pc(12) val random: Random = Random(this.seed.toLong())
		@Pc(14) var index: Int = 0
		while (index < 255) {
			this.permutationTable[index] = index.toShort()
			index++
		}
		@Pc(28) var i: Int = 0
		while (i < 255) {
			@Pc(35) val last: Int = 255 - i
			@Pc(40) val pick: Int = Utils.randomInt(last, random)
			@Pc(45) val picked: Short = this.permutationTable[pick]
			this.permutationTable[pick] = this.permutationTable[last]
			this.permutationTable[last + 256] = picked
			this.permutationTable[last] = picked
			i++
		}
	}

	@OriginalMember(owner = "client!jp", name = "b", descriptor = "(I)V")
	private fun buildOctaveFrequencies() {
		this.octaveWeights = ShortArray(this.octaveCount)
		@Pc(12) var octave: Int = 0
		while (octave < this.octaveCount) {
			this.octaveWeights!![octave] = 2.0.pow(octave.toDouble()).toInt().toShort()
			octave++
		}
	}

	@OriginalMember(owner = "client!jp", name = "c", descriptor = "(I)V")
	abstract fun emitTexel()

	@OriginalMember(owner = "client!jp", name = "a", descriptor = "(IIII)V")
	fun renderTexels() {
		@Pc(8) val xCoords: IntArray = IntArray(128)
		@Pc(11) val yCoords: IntArray = IntArray(128)
		@Pc(13) var i: Int = 0
		while (i < 128) {
			xCoords[i] = (i shl 12) / 128
			i++
		}
		@Pc(30) val zCoords: IntArray = IntArray(16)
		@Pc(32) var j: Int = 0
		while (j < 128) {
			yCoords[j] = (j shl 12) / 128
			j++
		}
		@Pc(52) var k: Int = 0
		while (k < 16) {
			zCoords[k] = (k shl 12) / 16
			k++
		}
		this.reset()
		@Pc(71) var m: Int = 0
		while (m < 16) {
			@Pc(75) var y: Int = 0
			while (y < 128) {
				@Pc(79) var x: Int = 0
				while (x < 128) {
					@Pc(83) var octave: Int = 0
					while (octave < this.octaveCount) {
						@Pc(92) val frequency: Int = this.octaveWeights!![octave].toInt() shl 12
						@Pc(100) val sampleZ: Int = (frequency * zCoords[m]) shr 12
						@Pc(107) val wrapZ: Int = (this.scaleZ * frequency) shr 12
						@Pc(114) val wrapY: Int = (this.scaleY * frequency) shr 12
						@Pc(122) val sampleX: Int = (xCoords[x] * frequency) shr 12
						@Pc(129) val wrapX: Int = (frequency * this.scaleX) shr 12
						@Pc(137) val sampleY: Int = (frequency * yCoords[y]) shr 12
						@Pc(142) val scaledX: Int = sampleX * this.scaleX
						@Pc(147) val scaledY: Int = sampleY * this.scaleY
						@Pc(152) val scaledZ: Int = sampleZ * this.scaleZ
						@Pc(156) val gridX: Int = scaledX shr 12
						@Pc(160) var gridX1: Int = gridX + 1
						@Pc(164) val gridX0: Int = gridX and 0xFF
						@Pc(168) val gridY: Int = scaledY shr 12
						@Pc(172) var gridY1: Int = gridY + 1
						@Pc(176) val gridY0: Int = gridY and 0xFF
						@Pc(180) var gridZ0: Int = scaledZ shr 12
						@Pc(184) var gridZ1: Int = gridZ0 + 1
						@Pc(188) val fracX: Int = scaledX and 0xFFF
						@Pc(192) val fracY: Int = scaledY and 0xFFF
						if (wrapY <= gridY1) {
							gridY1 = 0
						} else {
							gridY1 = gridY1 and 0xFF
						}
						if (gridX1 >= wrapX) {
							gridX1 = 0
						} else {
							gridX1 = gridX1 and 0xFF
						}
						gridZ0 = gridZ0 and 0xFF
						if (wrapZ > gridZ1) {
							gridZ1 = gridZ1 and 0xFF
						} else {
							gridZ1 = 0
						}
						val fracZ: Int = scaledZ and 0xFFF
						@Pc(241) val farY: Int = fracY - 4096
						@Pc(246) val hashZ1: Short = this.permutationTable[gridZ1]
						@Pc(250) val farX: Int = fracX - 4096
						@Pc(254) val fadeZ: Int = EnvironmentContext.FADE_TABLE[fracZ]
						@Pc(258) val fadeX: Int = EnvironmentContext.FADE_TABLE[fracX]
						@Pc(262) val fadeY: Int = EnvironmentContext.FADE_TABLE[fracY]
						@Pc(266) val farZ: Int = fracZ - 4096
						@Pc(271) val hashZ0: Short = this.permutationTable[gridZ0]
						@Pc(278) val hashY1Z0: Short = this.permutationTable[hashZ0 + gridY1]
						@Pc(285) val hashY0Z1: Short = this.permutationTable[hashZ1 + gridY0]
						@Pc(293) val hashY1Z1: Short = this.permutationTable[gridY1 + hashZ1]
						@Pc(300) val hashY0Z0: Short = this.permutationTable[hashZ0 + gridY0]
						@Pc(313) val grad0: Int = WaterTextureGenerator.gradient(this.permutationTable[gridX0 + hashY0Z0].toInt(), fracZ, fracY, fracX)
						@Pc(325) val grad1: Int = WaterTextureGenerator.gradient(this.permutationTable[gridX1 + hashY0Z0].toInt(), fracZ, fracY, farX)
						@Pc(336) val edgeY0Z0: Int = grad0 + (((grad1 - grad0) * fadeX) shr 12)
						@Pc(348) val grad2: Int = WaterTextureGenerator.gradient(this.permutationTable[hashY1Z0 + gridX0].toInt(), fracZ, farY, fracX)
						@Pc(360) val grad3: Int = WaterTextureGenerator.gradient(this.permutationTable[gridX1 + hashY1Z0].toInt(), fracZ, farY, farX)
						@Pc(371) val edgeY1Z0: Int = grad2 + (((grad3 - grad2) * fadeX) shr 12)
						@Pc(383) val grad4: Int = WaterTextureGenerator.gradient(this.permutationTable[hashY0Z1 + gridX0].toInt(), farZ, fracY, fracX)
						@Pc(394) val faceZ0: Int = (((edgeY1Z0 - edgeY0Z0) * fadeY) shr 12) + edgeY0Z0
						@Pc(406) val grad5: Int = WaterTextureGenerator.gradient(this.permutationTable[hashY0Z1 + gridX1].toInt(), farZ, fracY, farX)
						@Pc(416) val edgeY0Z1: Int = grad4 + ((fadeX * (grad5 - grad4)) shr 12)
						@Pc(429) val grad6: Int = WaterTextureGenerator.gradient(this.permutationTable[gridX0 + hashY1Z1].toInt(), farZ, farY, fracX)
						@Pc(442) val grad7: Int = WaterTextureGenerator.gradient(this.permutationTable[gridX1 + hashY1Z1].toInt(), farZ, farY, farX)
						@Pc(453) val edgeY1Z1: Int = (((grad7 - grad6) * fadeX) shr 12) + grad6
						@Pc(463) val faceZ1: Int = (((edgeY1Z1 - edgeY0Z1) * fadeY) shr 12) + edgeY0Z1
						this.accumulateOctave(faceZ0 + (((faceZ1 - faceZ0) * fadeZ) shr 12), octave)
						octave++
					}
					this.emitTexel()
					x++
				}
				y++
			}
			m++
		}
	}

	@OriginalMember(owner = "client!jp", name = "a", descriptor = "(Z)V")
	abstract fun reset()

	@OriginalMember(owner = "client!jp", name = "a", descriptor = "(III)V")
	abstract fun accumulateOctave(@OriginalArg(0) noise: Int, @OriginalArg(1) octave: Int)

	companion object {
		@OriginalMember(owner = "client!cda", name = "a", descriptor = "(ZIIII)I")
		@JvmStatic
		fun gradient(@OriginalArg(1) hash: Int, @OriginalArg(2) z: Int, @OriginalArg(3) y: Int, @OriginalArg(4) x: Int): Int {
			@Pc(7) val h: Int = hash and 0xF
			@Pc(21) val u: Int = if (h < 8) x else y
			@Pc(40) val v: Int = if (h < 4) y else if (h == 12 || h == 14) x else z
			return (if ((h and 0x1) == 0) u else -u) + (if ((h and 0x2) == 0) v else -v)
		}

		@OriginalMember(owner = "client!tu", name = "a", descriptor = "(II)I")
		@JvmStatic
		fun fade(@OriginalArg(1) t: Int): Int {
			@Pc(18) val t3: Int = (t * ((t * t) shr 12)) shr 12
			@Pc(24) val sixTMinus15: Int = t * 6 - 61440
			@Pc(33) val poly: Int = ((sixTMinus15 * t) shr 12) + 40960
			return (poly * t3) shr 12
		}
	}
}
