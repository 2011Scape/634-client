package com.jagex.game.runetek5.config.material.texture.operation

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.graphics.EnvironmentLight
import com.jagex.game.runetek5.config.material.texture.TextureOperation

@OriginalClass("client!aw")
class TextureOpColorGradient @OriginalMember(owner = "client!aw", name = "<init>", descriptor = "()V") constructor() : TextureOperation(1, false) {

	@OriginalMember(owner = "client!aw", name = "N", descriptor = "[[I")
	private var colorStops: Array<IntArray?>? = null

	@OriginalMember(owner = "client!aw", name = "S", descriptor = "[I")
	private val gradientLookup: IntArray = IntArray(257)

	@OriginalMember(owner = "client!aw", name = "a", descriptor = "(Lclient!cea;II)V")
	override fun decode(@OriginalArg(0) packet: Packet?, @OriginalArg(1) opcode: Int) {
		if (opcode != 0) {
			return
		}
		@Pc(7) val preset: Int = packet!!.readUByte()
		if (preset != 0) {
			this.setPreset(preset)
			return
		}
		this.colorStops = Array<IntArray?>(packet.readUByte()) { IntArray(4) }
		@Pc(29) var i: Int = 0
		while (i < this.colorStops!!.size) {
			this.colorStops!![i]!![0] = packet.readUShort()
			this.colorStops!![i]!![1] = packet.readUByte() shl 4
			this.colorStops!![i]!![2] = packet.readUByte() shl 4
			this.colorStops!![i]!![3] = packet.readUByte() shl 4
			i++
		}
	}

	@OriginalMember(owner = "client!aw", name = "d", descriptor = "(II)[[I")
	override fun getColorOutput(@OriginalArg(0) row: Int): Array<IntArray?> {
		@Pc(13) val output: Array<IntArray?> = this.colorImageCache!!.get(row)!!
		if (this.colorImageCache!!.dirty) {
			@Pc(23) val input: IntArray = this.getChildMonochromeOutput(row, 0)!!
			@Pc(27) val outputRed: IntArray = output[0]!!
			@Pc(31) val outputGreen: IntArray = output[1]!!
			@Pc(35) val outputBlue: IntArray = output[2]!!
			@Pc(37) var i: Int = 0
			while (i < EnvironmentLight.textureOpWidth) {
				@Pc(45) var local45: Int = input[i] shr 4
				if (local45 < 0) {
					local45 = 0
				}
				if (local45 > 256) {
					local45 = 256
				}
				local45 = this.gradientLookup[local45]
				outputRed[i] = (local45 shr 12) and 0xFF0
				outputGreen[i] = (local45 shr 4) and 0xFF0
				outputBlue[i] = (local45 and 0xFF) shl 4
				i++
			}
		}
		return output
	}

	@OriginalMember(owner = "client!aw", name = "e", descriptor = "(I)V")
	override fun postDecode() {
		if (this.colorStops == null) {
			this.setPreset(1)
		}
		this.buildRamp()
	}

	@OriginalMember(owner = "client!aw", name = "e", descriptor = "(II)V")
	private fun setPreset(@OriginalArg(0) preset: Int) {
		if (preset == 0) {
			return
		}
		if (preset == 1) {
			this.colorStops = Array<IntArray?>(2) { IntArray(4) }
			this.colorStops!![0]!![1] = 0
			this.colorStops!![0]!![2] = 0
			this.colorStops!![0]!![0] = 0
			this.colorStops!![0]!![3] = 0
			this.colorStops!![1]!![3] = 4096
			this.colorStops!![1]!![2] = 4096
			this.colorStops!![1]!![1] = 4096
			this.colorStops!![1]!![0] = 4096
		} else if (preset == 2) {
			this.colorStops = Array<IntArray?>(8) { IntArray(4) }
			this.colorStops!![0]!![3] = 2361
			this.colorStops!![0]!![1] = 2650
			this.colorStops!![0]!![0] = 0
			this.colorStops!![0]!![2] = 2602
			this.colorStops!![1]!![2] = 1799
			this.colorStops!![1]!![1] = 2313
			this.colorStops!![1]!![3] = 1558
			this.colorStops!![1]!![0] = 2867
			this.colorStops!![2]!![2] = 1734
			this.colorStops!![2]!![1] = 2618
			this.colorStops!![2]!![3] = 1413
			this.colorStops!![2]!![0] = 3072
			this.colorStops!![3]!![2] = 1220
			this.colorStops!![3]!![0] = 3276
			this.colorStops!![3]!![3] = 947
			this.colorStops!![3]!![1] = 2296
			this.colorStops!![4]!![2] = 963
			this.colorStops!![4]!![3] = 722
			this.colorStops!![4]!![0] = 3481
			this.colorStops!![4]!![1] = 2072
			this.colorStops!![5]!![3] = 1766
			this.colorStops!![5]!![2] = 2152
			this.colorStops!![5]!![0] = 3686
			this.colorStops!![5]!![1] = 2730
			this.colorStops!![6]!![2] = 1060
			this.colorStops!![6]!![0] = 3891
			this.colorStops!![6]!![1] = 2232
			this.colorStops!![6]!![3] = 915
			this.colorStops!![7]!![2] = 1413
			this.colorStops!![7]!![3] = 1140
			this.colorStops!![7]!![1] = 1686
			this.colorStops!![7]!![0] = 4096
		} else if (preset == 3) {
			this.colorStops = Array<IntArray?>(7) { IntArray(4) }
			this.colorStops!![0]!![0] = 0
			this.colorStops!![0]!![1] = 0
			this.colorStops!![0]!![2] = 0
			this.colorStops!![0]!![3] = 4096
			this.colorStops!![1]!![3] = 4096
			this.colorStops!![1]!![1] = 0
			this.colorStops!![1]!![0] = 663
			this.colorStops!![1]!![2] = 4096
			this.colorStops!![2]!![0] = 1363
			this.colorStops!![2]!![2] = 4096
			this.colorStops!![2]!![1] = 0
			this.colorStops!![2]!![3] = 0
			this.colorStops!![3]!![0] = 2048
			this.colorStops!![3]!![2] = 4096
			this.colorStops!![3]!![3] = 0
			this.colorStops!![3]!![1] = 4096
			this.colorStops!![4]!![0] = 2727
			this.colorStops!![4]!![1] = 4096
			this.colorStops!![4]!![2] = 0
			this.colorStops!![4]!![3] = 0
			this.colorStops!![5]!![0] = 3411
			this.colorStops!![5]!![3] = 4096
			this.colorStops!![5]!![2] = 0
			this.colorStops!![5]!![1] = 4096
			this.colorStops!![6]!![2] = 0
			this.colorStops!![6]!![3] = 4096
			this.colorStops!![6]!![0] = 4096
			this.colorStops!![6]!![1] = 0
		} else if (preset == 4) {
			this.colorStops = Array<IntArray?>(6) { IntArray(4) }
			this.colorStops!![0]!![2] = 0
			this.colorStops!![0]!![1] = 0
			this.colorStops!![0]!![3] = 0
			this.colorStops!![0]!![0] = 0
			this.colorStops!![1]!![3] = 1493
			this.colorStops!![1]!![1] = 0
			this.colorStops!![1]!![2] = 0
			this.colorStops!![1]!![0] = 1843
			this.colorStops!![2]!![3] = 2939
			this.colorStops!![2]!![2] = 0
			this.colorStops!![2]!![1] = 0
			this.colorStops!![2]!![0] = 2457
			this.colorStops!![3]!![3] = 3565
			this.colorStops!![3]!![1] = 0
			this.colorStops!![3]!![2] = 1124
			this.colorStops!![3]!![0] = 2781
			this.colorStops!![4]!![2] = 3084
			this.colorStops!![4]!![0] = 3481
			this.colorStops!![4]!![1] = 546
			this.colorStops!![4]!![3] = 4031
			this.colorStops!![5]!![0] = 4096
			this.colorStops!![5]!![3] = 4096
			this.colorStops!![5]!![2] = 4096
			this.colorStops!![5]!![1] = 4096
		} else if (preset == 5) {
			this.colorStops = Array<IntArray?>(16) { IntArray(4) }
			this.colorStops!![0]!![0] = 0
			this.colorStops!![0]!![3] = 321
			this.colorStops!![0]!![2] = 192
			this.colorStops!![0]!![1] = 80
			this.colorStops!![1]!![0] = 155
			this.colorStops!![1]!![2] = 449
			this.colorStops!![1]!![3] = 562
			this.colorStops!![1]!![1] = 321
			this.colorStops!![2]!![0] = 389
			this.colorStops!![2]!![3] = 803
			this.colorStops!![2]!![1] = 578
			this.colorStops!![2]!![2] = 690
			this.colorStops!![3]!![1] = 947
			this.colorStops!![3]!![3] = 1140
			this.colorStops!![3]!![0] = 671
			this.colorStops!![3]!![2] = 995
			this.colorStops!![4]!![1] = 1285
			this.colorStops!![4]!![3] = 1509
			this.colorStops!![4]!![2] = 1397
			this.colorStops!![4]!![0] = 897
			this.colorStops!![5]!![2] = 1429
			this.colorStops!![5]!![3] = 1413
			this.colorStops!![5]!![0] = 1175
			this.colorStops!![5]!![1] = 1525
			this.colorStops!![6]!![0] = 1368
			this.colorStops!![6]!![2] = 1461
			this.colorStops!![6]!![1] = 1734
			this.colorStops!![6]!![3] = 1333
			this.colorStops!![7]!![2] = 1525
			this.colorStops!![7]!![1] = 1413
			this.colorStops!![7]!![0] = 1507
			this.colorStops!![7]!![3] = 1702
			this.colorStops!![8]!![3] = 2056
			this.colorStops!![8]!![1] = 1108
			this.colorStops!![8]!![2] = 1590
			this.colorStops!![8]!![0] = 1736
			this.colorStops!![9]!![2] = 2056
			this.colorStops!![9]!![3] = 2666
			this.colorStops!![9]!![0] = 2088
			this.colorStops!![9]!![1] = 1766
			this.colorStops!![10]!![0] = 2355
			this.colorStops!![10]!![2] = 2586
			this.colorStops!![10]!![1] = 2409
			this.colorStops!![10]!![3] = 3276
			this.colorStops!![11]!![2] = 3148
			this.colorStops!![11]!![3] = 3228
			this.colorStops!![11]!![1] = 3116
			this.colorStops!![11]!![0] = 2691
			this.colorStops!![12]!![3] = 3196
			this.colorStops!![12]!![0] = 3031
			this.colorStops!![12]!![1] = 3806
			this.colorStops!![12]!![2] = 3710
			this.colorStops!![13]!![3] = 3019
			this.colorStops!![13]!![1] = 3437
			this.colorStops!![13]!![2] = 3421
			this.colorStops!![13]!![0] = 3522
			this.colorStops!![14]!![1] = 3116
			this.colorStops!![14]!![2] = 3148
			this.colorStops!![14]!![3] = 3228
			this.colorStops!![14]!![0] = 3727
			this.colorStops!![15]!![2] = 2505
			this.colorStops!![15]!![3] = 2746
			this.colorStops!![15]!![1] = 2377
			this.colorStops!![15]!![0] = 4096
		} else if (preset == 6) {
			this.colorStops = Array<IntArray?>(4) { IntArray(4) }
			this.colorStops!![0]!![1] = 0
			this.colorStops!![0]!![2] = 4096
			this.colorStops!![0]!![3] = 0
			this.colorStops!![0]!![0] = 2048
			this.colorStops!![1]!![0] = 2867
			this.colorStops!![1]!![3] = 0
			this.colorStops!![1]!![1] = 4096
			this.colorStops!![1]!![2] = 4096
			this.colorStops!![2]!![0] = 3276
			this.colorStops!![2]!![1] = 4096
			this.colorStops!![2]!![3] = 0
			this.colorStops!![2]!![2] = 4096
			this.colorStops!![3]!![0] = 4096
			this.colorStops!![3]!![2] = 0
			this.colorStops!![3]!![1] = 4096
			this.colorStops!![3]!![3] = 0
		} else {
			throw RuntimeException("Invalid gradient preset")
		}
	}

	@OriginalMember(owner = "client!aw", name = "f", descriptor = "(I)V")
	private fun buildRamp() {
		@Pc(15) val stopCount: Int = this.colorStops!!.size
		if (stopCount <= 0) {
			return
		}
		@Pc(22) var index: Int = 0
		while (index < 257) {
			@Pc(26) var upperIndex: Int = 0
			@Pc(30) val position: Int = index shl 4
			@Pc(32) var i: Int = 0
			while (i < stopCount && this.colorStops!![i]!![0] <= position) {
				upperIndex++
				i++
			}
			@Pc(61) var red: Int
			@Pc(65) var green: Int
			@Pc(69) var blue: Int
			@Pc(57) val upper: IntArray
			if (upperIndex >= stopCount) {
				upper = this.colorStops!![stopCount - 1]!!
				red = upper[1]
				green = upper[2]
				blue = upper[3]
			} else {
				upper = this.colorStops!![upperIndex]!!
				if (upperIndex <= 0) {
					red = upper[1]
					blue = upper[3]
					green = upper[2]
				} else {
					@Pc(102) val lower: IntArray = this.colorStops!![upperIndex - 1]!!
					@Pc(119) val weight: Int = ((position - lower[0]) shl 12) / (upper[0] - lower[0])
					@Pc(124) val inverse: Int = 4096 - weight
					red = (lower[1] * inverse + upper[1] * weight) shr 12
					blue = (weight * upper[3] + inverse * lower[3]) shr 12
					green = (lower[2] * inverse + upper[2] * weight) shr 12
				}
			}
			green = green shr 0x4
			red = red shr 0x4
			blue = blue shr 0x4
			if (red < 0) {
				red = 0
			} else if (red > 255) {
				red = 255
			}
			if (blue < 0) {
				blue = 0
			} else if (blue > 255) {
				blue = 255
			}
			if (green < 0) {
				green = 0
			} else if (green > 255) {
				green = 255
			}
			this.gradientLookup[index] = blue or (red shl 16) or (green shl 8)
			index++
		}
	}
}
