package com.jagex.game.runetek5.config.material.texture.operation

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.graphics.EnvironmentLight
import com.jagex.game.runetek5.config.material.texture.TextureOperation
import com.jagex.game.runetek5.config.material.texture.TextureRaster

@OriginalClass("client!fd")
class TextureOpBoxBlur @OriginalMember(owner = "client!fd", name = "<init>", descriptor = "()V") constructor() : TextureOperation(1, false) {

	@OriginalMember(owner = "client!fd", name = "O", descriptor = "I")
	private var radiusY: Int = 1

	@OriginalMember(owner = "client!fd", name = "R", descriptor = "I")
	private var radiusX: Int = 1

	@OriginalMember(owner = "client!fd", name = "a", descriptor = "(Lclient!cea;II)V")
	override fun decode(@OriginalArg(0) packet: Packet?, @OriginalArg(1) opcode: Int) {
		if (opcode == 0) {
			this.radiusX = packet!!.readUByte()
		} else if (opcode == 1) {
			this.radiusY = packet!!.readUByte()
		} else if (opcode == 2) {
			this.monochrome = packet!!.readUByte() == 1
		}
	}

	@OriginalMember(owner = "client!fd", name = "c", descriptor = "(II)[I")
	override fun monochromeOutput(@OriginalArg(0) row: Int): IntArray {
		@Pc(11) val output: IntArray = this.monochromeImageCache!!.get(row)!!
		if (this.monochromeImageCache!!.dirty) {
			@Pc(28) val kernelHeight: Int = this.radiusY + this.radiusY + 1
			@Pc(32) val verticalWeight: Int = 65536 / kernelHeight
			@Pc(40) val kernelWidth: Int = this.radiusX + this.radiusX + 1
			@Pc(44) val horizontalWeight: Int = 65536 / kernelWidth
			@Pc(47) val blurredRows: Array<IntArray?> = arrayOfNulls<IntArray>(kernelHeight)
			@Pc(68) val local68: Int
			@Pc(53) var i: Int = row - this.radiusY
			while (i <= row + this.radiusY) {
				@Pc(63) val input: IntArray = this.getChildMonochromeOutput(TextureOperation.columnEnd and i, 0)!!
				@Pc(66) val blurredRow: IntArray = IntArray(EnvironmentLight.textureOpWidth)
				var rowSum: Int = 0
				@Pc(72) var j: Int = -this.radiusX
				while (j <= this.radiusX) {
					rowSum += input[j and TextureRaster.widthMask]
					j++
				}
				@Pc(89) var x: Int = 0
				while (x < EnvironmentLight.textureOpWidth) {
					blurredRow[x] = (horizontalWeight * rowSum) shr 16
					rowSum -= input[(x - this.radiusX) and TextureRaster.widthMask]
					x++
					rowSum += input[(x + this.radiusX) and TextureRaster.widthMask]
				}
				blurredRows[i + this.radiusY - row] = blurredRow
				i++
			}
			@Pc(146) var k: Int = 0
			while (k < EnvironmentLight.textureOpWidth) {
				@Pc(150) var sum: Int = 0
				var kernelRow: Int = 0
				while (kernelRow < kernelHeight) {
					sum += blurredRows[kernelRow]!![k]
					kernelRow++
				}
				output[k] = (sum * verticalWeight) shr 16
				k++
			}
		}
		return output
	}

	@OriginalMember(owner = "client!fd", name = "d", descriptor = "(II)[[I")
	override fun getColorOutput(@OriginalArg(0) count: Int): Array<IntArray?> {
		@Pc(18) val output: Array<IntArray?> = this.colorImageCache!!.get(count)!!
		if (this.colorImageCache!!.dirty) {
			@Pc(30) val kernelHeight: Int = this.radiusY + this.radiusY + 1
			@Pc(34) val verticalWeight: Int = 65536 / kernelHeight
			@Pc(42) val kernelWidth: Int = this.radiusX + this.radiusX + 1
			@Pc(46) val horizontalWeight: Int = 65536 / kernelWidth
			@Pc(49) val blurredRows: Array<Array<IntArray?>?> = arrayOfNulls<Array<IntArray?>>(kernelHeight)
			@Pc(73) val local73: Int
			@Pc(75) val local75: Int
			@Pc(55) var i: Int = count - this.radiusY
			while (i <= this.radiusY + count) {
				@Pc(65) val input: Array<IntArray?> = this.getChildColorOutput(TextureOperation.columnEnd and i, 0)!!
				@Pc(69) val blurredRow: Array<IntArray?> = Array<IntArray?>(3) { IntArray(EnvironmentLight.textureOpWidth) }
				@Pc(71) var redSum: Int = 0
				var greenSum: Int = 0
				var blueSum: Int = 0
				@Pc(79) val inputRed: IntArray = input[0]!!
				@Pc(83) val inputGreen: IntArray = input[1]!!
				@Pc(87) val inputBlue: IntArray = input[2]!!
				@Pc(91) var j: Int = -this.radiusX
				while (j <= this.radiusX) {
					@Pc(97) val sampleX: Int = TextureRaster.widthMask and j
					blueSum += inputBlue[sampleX]
					redSum += inputRed[sampleX]
					greenSum += inputGreen[sampleX]
					j++
				}
				@Pc(128) val blurredRed: IntArray = blurredRow[0]!!
				@Pc(132) val blurredGreen: IntArray = blurredRow[1]!!
				@Pc(136) val blurredBlue: IntArray = blurredRow[2]!!
				@Pc(138) var x: Int = 0
				while (x < EnvironmentLight.textureOpWidth) {
					blurredRed[x] = (redSum * horizontalWeight) shr 16
					blurredGreen[x] = (greenSum * horizontalWeight) shr 16
					blurredBlue[x] = (blueSum * horizontalWeight) shr 16
					@Pc(172) val trailingX: Int = TextureRaster.widthMask and (x - this.radiusX)
					redSum -= inputRed[trailingX]
					x++
					greenSum -= inputGreen[trailingX]
					blueSum -= inputBlue[trailingX]
					@Pc(199) val leadingX: Int = (x + this.radiusX) and TextureRaster.widthMask
					blueSum += inputBlue[leadingX]
					greenSum += inputGreen[leadingX]
					redSum += inputRed[leadingX]
				}
				blurredRows[i + this.radiusY - count] = blurredRow
				i++
			}
			@Pc(245) val outputRed: IntArray = output[0]!!
			@Pc(249) val outputGreen: IntArray = output[1]!!
			@Pc(253) val outputBlue: IntArray = output[2]!!
			var column: Int = 0
			while (column < EnvironmentLight.textureOpWidth) {
				var redSum: Int = 0
				@Pc(261) var greenSum: Int = 0
				@Pc(263) var blueSum: Int = 0
				@Pc(265) var k: Int = 0
				while (k < kernelHeight) {
					@Pc(271) val blurredRow: Array<IntArray?> = blurredRows[k]!!
					redSum += blurredRow[0]!![column]
					greenSum += blurredRow[1]!![column]
					blueSum += blurredRow[2]!![column]
					k++
				}
				outputRed[column] = (redSum * verticalWeight) shr 16
				outputGreen[column] = (greenSum * verticalWeight) shr 16
				outputBlue[column] = (verticalWeight * blueSum) shr 16
				column++
			}
		}
		return output
	}
}
