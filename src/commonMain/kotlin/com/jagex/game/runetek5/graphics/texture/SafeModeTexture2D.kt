package com.jagex.game.runetek5.graphics.texture

import com.jagex.game.runetek5.cache.node.Node
import com.jagex.game.runetek5.graphics.safe.PureJavaTexture
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmField

@OriginalClass("client!mg")
class SafeModeTexture2D : Node {

	@OriginalMember(owner = "client!mg", name = "o", descriptor = "I")
	@JvmField
	var cycle: Int = 0

	@OriginalMember(owner = "client!mg", name = "r", descriptor = "Z")
	@JvmField
	var isAnimated: Boolean = false

	@OriginalMember(owner = "client!mg", name = "s", descriptor = "I")
	@JvmField
	val materialId: Int

	@OriginalMember(owner = "client!mg", name = "p", descriptor = "I")
	private val totalColorCount: Int

	@OriginalMember(owner = "client!mg", name = "t", descriptor = "[I")
	private var colorArgb: IntArray?

	@OriginalMember(owner = "client!mg", name = "<init>", descriptor = "(II[IZ)V")
	constructor(@OriginalArg(0) materialId: Int, @OriginalArg(1) totalColorCount: Int, @OriginalArg(2) argbColor: IntArray?, @OriginalArg(3) shouldBlur: Boolean) : super() {
		this.materialId = materialId
		this.totalColorCount = totalColorCount
		this.colorArgb = argbColor
		if (shouldBlur) {
			@Pc(16) val alphaSums: IntArray = IntArray(this.totalColorCount)
			@Pc(20) val redSums: IntArray = IntArray(this.totalColorCount)
			@Pc(24) val greenSums: IntArray = IntArray(this.totalColorCount)
			@Pc(28) val blueSums: IntArray = IntArray(this.totalColorCount)
			if (PureJavaTexture.colorsArgb == null || PureJavaTexture.colorsArgb!!.size != this.colorArgb!!.size) {
				PureJavaTexture.colorsArgb = IntArray(this.colorArgb!!.size)
			}
			@Pc(44) val width: Int = this.totalColorCount
			@Pc(47) val height: Int = this.totalColorCount
			@Pc(51) val maxX: Int = width - 1
			@Pc(55) val maxY: Int = height - 1
			@Pc(59) val pixelCount: Int = width * height
			@Pc(62) var leadingIndex: Int = width
			@Pc(63) var trailingIndex: Int = width
			@Pc(68) var local68: Int
			@Pc(65) var i: Int = 2
			while (i >= 0) {
				var x: Int = maxX
				while (x >= 0) {
					leadingIndex--
					@Pc(75) val colour: Int = this.colorArgb!![leadingIndex]
					alphaSums[x] += (colour shr 24) and 0xFF
					redSums[x] += (colour shr 16) and 0xFF
					greenSums[x] += (colour shr 8) and 0xFF
					blueSums[x] += colour and 0xFF
					x--
				}
				if (leadingIndex == 0) {
					leadingIndex = pixelCount
				}
				i--
			}
			@Pc(129) var outIndex: Int = pixelCount
			@Pc(131) var j: Int = maxY
			while (j >= 0) {
				@Pc(134) var trailingX: Int = 1
				@Pc(136) var leadingX: Int = 1
				@Pc(139) var greenSum: Int = 0
				@Pc(141) var blueSum: Int = 0
				@Pc(143) var redSum: Int = 0
				@Pc(144) var alphaSum: Int = 0
				var tap: Int = 2
				while (tap >= 0) {
					leadingX--
					alphaSum += alphaSums[leadingX]
					redSum += redSums[leadingX]
					greenSum += greenSums[leadingX]
					blueSum += blueSums[leadingX]
					if (leadingX == 0) {
						leadingX = width
					}
					tap--
				}
				var x: Int = maxX
				while (x >= 0) {
					leadingX--
					trailingX--
					@Pc(188) val alpha: Int = alphaSum / 9
					@Pc(192) val red: Int = redSum / 9
					@Pc(196) val green: Int = greenSum / 9
					@Pc(200) val blue: Int = blueSum / 9
					outIndex--
					PureJavaTexture.colorsArgb!![outIndex] = (alpha shl 24) or (red shl 16) or (green shl 8) or blue
					alphaSum += alphaSums[leadingX] - alphaSums[trailingX]
					redSum += redSums[leadingX] - redSums[trailingX]
					blueSum += blueSums[leadingX] - blueSums[trailingX]
					greenSum += greenSums[leadingX] - greenSums[trailingX]
					if (leadingX == 0) {
						leadingX = width
					}
					if (trailingX == 0) {
						trailingX = width
					}
					x--
				}
				x = maxX
				while (x >= 0) {
					leadingIndex--
					@Pc(277) val enteringColour: Int = this.colorArgb!![leadingIndex]
					trailingIndex--
					@Pc(283) val leavingColour: Int = this.colorArgb!![trailingIndex]
					alphaSums[x] += ((enteringColour shr 24) and 0xFF) - ((leavingColour shr 24) and 0xFF)
					redSums[x] += ((enteringColour shr 16) and 0xFF) - ((leavingColour shr 16) and 0xFF)
					greenSums[x] += ((enteringColour shr 8) and 0xFF) - ((leavingColour shr 8) and 0xFF)
					blueSums[x] += (enteringColour and 0xFF) - (leavingColour and 0xFF)
					x--
				}
				if (leadingIndex == 0) {
					leadingIndex = pixelCount
				}
				if (trailingIndex == 0) {
					trailingIndex = pixelCount
				}
				j--
			}
			@Pc(364) val previousColours: IntArray? = this.colorArgb
			this.colorArgb = PureJavaTexture.colorsArgb
			PureJavaTexture.colorsArgb = previousColours
		}
	}

	@OriginalMember(owner = "client!mg", name = "c", descriptor = "(II)V")
	fun animate(@OriginalArg(0) offsetX: Int, @OriginalArg(1) offsetY: Int) {
		if (offsetX == 0 && offsetY == 0) {
			return
		}
		if (PureJavaTexture.colorsArgb == null || PureJavaTexture.colorsArgb!!.size != this.colorArgb!!.size) {
			PureJavaTexture.colorsArgb = IntArray(this.colorArgb!!.size)
		}
		@Pc(21) val length: Int = this.colorArgb!!.size
		@Pc(28) val maskX: Int = this.totalColorCount - 1
		@Pc(33) val rowOffset: Int = this.totalColorCount * offsetY
		@Pc(37) val mask: Int = length - 1
		@Pc(39) var i: Int = 0
		while (i < length) {
			@Pc(46) val srcRowStart: Int = (i + rowOffset) and mask
			@Pc(48) var j: Int = 0
			while (j < this.totalColorCount) {
				@Pc(53) val dstIndex: Int = i + j
				@Pc(61) val srcIndex: Int = srcRowStart + ((j + offsetX) and maskX)
				PureJavaTexture.colorsArgb!![dstIndex] = this.colorArgb!![srcIndex]
				j++
			}
			i += this.totalColorCount
		}
		@Pc(84) val previousColours: IntArray? = this.colorArgb
		this.colorArgb = PureJavaTexture.colorsArgb
		PureJavaTexture.colorsArgb = previousColours
	}

	@OriginalMember(owner = "client!mg", name = "b", descriptor = "()[I")
	fun getArgbColor(): IntArray? {
		return this.colorArgb
	}
}
