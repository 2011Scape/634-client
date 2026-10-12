package com.jagex.game.runetek5.config.material.texture.operation

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.graphics.EnvironmentLight
import com.jagex.game.runetek5.config.material.texture.TextureOperation
import com.jagex.game.runetek5.config.material.texture.TextureRaster

@OriginalClass("client!oj")
class TextureOpCurve @OriginalMember(owner = "client!oj", name = "<init>", descriptor = "()V") constructor() : TextureOperation(1, true) {

	@OriginalMember(owner = "client!oj", name = "L", descriptor = "[I")
	private var lastMarker: IntArray? = null

	@OriginalMember(owner = "client!oj", name = "N", descriptor = "[[I")
	private var controlPoints: Array<IntArray?>? = null

	@OriginalMember(owner = "client!oj", name = "R", descriptor = "[I")
	private var extrapolatedEnd: IntArray? = null

	@OriginalMember(owner = "client!oj", name = "O", descriptor = "I")
	private var interpolationMode: Int = 0

	@OriginalMember(owner = "client!oj", name = "S", descriptor = "[S")
	private val curveLookup: ShortArray = ShortArray(257)

	@OriginalMember(owner = "client!oj", name = "a", descriptor = "(Lclient!cea;II)V")
	override fun decode(@OriginalArg(0) packet: Packet?, @OriginalArg(1) opcode: Int) {
		if (opcode != 0) {
			return
		}
		this.interpolationMode = packet!!.readUByte()
		this.controlPoints = Array<IntArray?>(packet.readUByte()) { IntArray(2) }
		@Pc(19) var i: Int = 0
		while (i < this.controlPoints!!.size) {
			this.controlPoints!![i]!![0] = packet.readUShort()
			this.controlPoints!![i]!![1] = packet.readUShort()
			i++
		}
	}

	@OriginalMember(owner = "client!oj", name = "f", descriptor = "(I)V")
	private fun buildLookup() {
		@Pc(4) var interpolationMode: Int = this.interpolationMode
		@Pc(26) var index: Int
		@Pc(24) var input: Int
		@Pc(53) var start: IntArray
		@Pc(58) var end: IntArray
		@Pc(67) val local67: Int
		@Pc(71) val local71: Int
		@Pc(75) val local75: Int
		@Pc(84) val local84: Int
		if (interpolationMode == 2) {
			interpolationMode = 0
			while (interpolationMode < 257) {
				input = interpolationMode shl 4
				index = 1
				while (this.controlPoints!!.size - 1 > index && this.controlPoints!![index]!![0] <= input) {
					index++
				}
				start = this.controlPoints!![index - 1]!!
				end = this.controlPoints!![index]!!
				val prevValue: Int = this.markerAt(index - 2)!![1]
				val startValue: Int = start[1]
				val endValue: Int = end[1]
				val nextValue: Int = this.markerAt(index + 1)!![1]
				@Pc(102) val fraction: Int = ((input - start[0]) shl 12) / (end[0] - start[0])
				@Pc(108) val fractionSquared: Int = (fraction * fraction) shr 12
				@Pc(118) val cubic: Int = startValue + nextValue - endValue - prevValue
				@Pc(125) val quadratic: Int = prevValue - cubic - startValue
				@Pc(130) val linear: Int = endValue - prevValue
				@Pc(142) val cubicTerm: Int = (((fraction * cubic) shr 12) * fractionSquared) shr 12
				@Pc(148) val quadraticTerm: Int = (quadratic * fractionSquared) shr 12
				@Pc(154) val linearTerm: Int = (fraction * linear) shr 12
				@Pc(162) var value: Int = startValue + linearTerm + quadraticTerm + cubicTerm
				if (value <= -32768) {
					value = -32767
				}
				if (value >= 32768) {
					value = 32767
				}
				this.curveLookup[interpolationMode] = value.toShort()
				interpolationMode++
			}
		} else if (interpolationMode == 1) {
			interpolationMode = 0
			while (interpolationMode < 257) {
				input = interpolationMode shl 4
				index = 1
				while (this.controlPoints!!.size - 1 > index && this.controlPoints!![index]!![0] <= input) {
					index++
				}
				start = this.controlPoints!![index - 1]!!
				end = this.controlPoints!![index]!!
				val fraction: Int = ((input - start[0]) shl 12) / (end[0] - start[0])
				val endWeight: Int = (4096 - TextureRaster.PALETTE_COS!![(fraction shr 5) and 0xFF]) shr 1
				val startWeight: Int = 4096 - endWeight
				var value: Int = (start[1] * startWeight + end[1] * endWeight) shr 12
				if (value <= -32768) {
					value = -32767
				}
				if (value >= 32768) {
					value = 32767
				}
				this.curveLookup[interpolationMode] = value.toShort()
				interpolationMode++
			}
		} else {
			interpolationMode = 0
			while (interpolationMode < 257) {
				input = interpolationMode shl 4
				index = 1
				while (index < this.controlPoints!!.size - 1 && input >= this.controlPoints!![index]!![0]) {
					index++
				}
				start = this.controlPoints!![index - 1]!!
				end = this.controlPoints!![index]!!
				val fraction: Int = ((input - start[0]) shl 12) / (end[0] - start[0])
				val startWeight: Int = 4096 - fraction
				var value: Int = (end[1] * fraction + startWeight * start[1]) shr 12
				if (value <= -32768) {
					value = -32767
				}
				if (value >= 32768) {
					value = 32767
				}
				this.curveLookup[interpolationMode] = value.toShort()
				interpolationMode++
			}
		}
	}

	@OriginalMember(owner = "client!oj", name = "c", descriptor = "(II)[I")
	override fun monochromeOutput(@OriginalArg(0) row: Int): IntArray {
		@Pc(11) val output: IntArray = this.monochromeImageCache!!.get(row)!!
		if (this.monochromeImageCache!!.dirty) {
			@Pc(27) val source: IntArray = this.getChildMonochromeOutput(row, 0)!!
			@Pc(29) var x: Int = 0
			while (x < EnvironmentLight.textureOpWidth) {
				@Pc(37) var index: Int = source[x] shr 4
				if (index < 0) {
					index = 0
				}
				if (index > 256) {
					index = 256
				}
				output[x] = this.curveLookup[index].toInt()
				x++
			}
		}
		return output
	}

	@OriginalMember(owner = "client!oj", name = "b", descriptor = "(B)V")
	private fun computeExtrapolatedEndpoints() {
		@Pc(8) val first: IntArray = this.controlPoints!![0]!!
		@Pc(13) val second: IntArray = this.controlPoints!![1]!!
		@Pc(22) val penultimate: IntArray = this.controlPoints!![this.controlPoints!!.size - 2]!!
		@Pc(31) val last: IntArray = this.controlPoints!![this.controlPoints!!.size - 1]!!
		this.lastMarker = intArrayOf(penultimate[0] + penultimate[0] - last[0], penultimate[1] - (-penultimate[1] + last[1]))
		this.extrapolatedEnd = intArrayOf(first[0] + first[0] - second[0], first[1] - (-first[1] - -second[1]))
	}

	@OriginalMember(owner = "client!oj", name = "e", descriptor = "(I)V")
	override fun postDecode() {
		if (this.controlPoints == null) {
			this.controlPoints = arrayOf<IntArray?>(IntArray(2), intArrayOf(4096, 4096))
		}
		if (this.controlPoints!!.size < 2) {
			throw RuntimeException("Curve operation requires at least two markers")
		}
		if (this.interpolationMode == 2) {
			this.computeExtrapolatedEndpoints()
		}
		TextureRaster.calculateTrigTables()
		this.buildLookup()
	}

	@OriginalMember(owner = "client!oj", name = "a", descriptor = "(IB)[I")
	private fun markerAt(@OriginalArg(0) index: Int): IntArray? {
		if (index < 0) {
			return this.extrapolatedEnd
		} else if (index >= this.controlPoints!!.size) {
			return this.lastMarker
		} else {
			return this.controlPoints!![index]
		}
	}
}
