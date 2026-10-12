package com.jagex.game.runetek5.graphics.sprite

import com.jagex.game.runetek5.core.util.Arrays
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import com.jagex.game.runetek5.graphics.SpriteDataProvider

@OriginalClass("client!rh")
class SpriteType {

	@OriginalMember(owner = "client!rh", name = "a", descriptor = "[B")
	@JvmField
	var alpha: ByteArray? = null

	@OriginalMember(owner = "client!rh", name = "b", descriptor = "I")
	@JvmField
	var height: Int = 0

	@OriginalMember(owner = "client!rh", name = "c", descriptor = "[I")
	@JvmField
	var pallete: IntArray? = null

	@OriginalMember(owner = "client!rh", name = "d", descriptor = "I")
	@JvmField
	var borderX: Int = 0

	@OriginalMember(owner = "client!rh", name = "e", descriptor = "[B")
	@JvmField
	var pixels: ByteArray? = null

	@OriginalMember(owner = "client!rh", name = "f", descriptor = "I")
	@JvmField
	var minY: Int = 0

	@OriginalMember(owner = "client!rh", name = "g", descriptor = "I")
	@JvmField
	var borderY: Int = 0

	@OriginalMember(owner = "client!rh", name = "h", descriptor = "I")
	@JvmField
	var width: Int = 0

	@OriginalMember(owner = "client!rh", name = "i", descriptor = "I")
	@JvmField
	var minX: Int = 0

	@OriginalMember(owner = "client!rh", name = "a", descriptor = "()I")
	fun widthSum(): Int {
		return this.width + this.minX + this.borderX
	}

	@OriginalMember(owner = "client!rh", name = "a", descriptor = "(I)V")
	fun outline(@OriginalArg(0) outlineColor: Int) {
		@Pc(1) var outlineIndex: Int = -1
		@Pc(8) val local8: Int
		@Pc(69) val local69: Int
		@Pc(63) val local63: Int
		if (this.pallete!!.size < 255) {
			for (paletteIndex in 0 until this.pallete!!.size) {
				if (this.pallete!![paletteIndex] == outlineColor) {
					outlineIndex = paletteIndex
					break
				}
			}
			if (outlineIndex == -1) {
				outlineIndex = this.pallete!!.size
				@Pc(38) val newPalette: IntArray = IntArray(this.pallete!!.size + 1)
				Arrays.copy(this.pallete, 0, newPalette, 0, this.pallete!!.size)
				this.pallete = newPalette
				newPalette[outlineIndex] = outlineColor
			}
		} else {
			var bestDistance: Int = Int.MAX_VALUE
			val targetRed: Int = (outlineColor shr 16) and 0xFF
			val targetGreen: Int = (outlineColor shr 8) and 0xFF
			@Pc(73) val targetBlue: Int = outlineColor and 0xFF
			for (@Pc(75) i in 0 until this.pallete!!.size) {
				@Pc(81) val paletteColor: Int = this.pallete!![i]
				@Pc(87) val red: Int = (paletteColor shr 16) and 0xFF
				@Pc(93) val green: Int = (paletteColor shr 8) and 0xFF
				@Pc(97) val blue: Int = paletteColor and 0xFF
				@Pc(101) var redDiff: Int = targetRed - red
				if (redDiff < 0) {
					redDiff = -redDiff
				}
				@Pc(110) var greenDiff: Int = targetGreen - green
				if (greenDiff < 0) {
					greenDiff = -greenDiff
				}
				@Pc(119) var blueDiff: Int = targetBlue - blue
				if (blueDiff < 0) {
					blueDiff = -blueDiff
				}
				@Pc(130) val distance: Int = redDiff + greenDiff + blueDiff
				if (distance < bestDistance) {
					bestDistance = distance
					outlineIndex = i
				}
			}
		}
		var row: Int = this.height - 1
		while (row > 0) {
			val rowOffset: Int = row * this.width
			var column: Int = this.width - 1
			while (column > 0) {
				if (this.pallete!![this.pixels!![column + rowOffset].toInt() and 0xFF] == 0 && this.pallete!![this.pixels!![column + rowOffset - this.width - 1].toInt() and 0xFF] != 0) {
					this.pixels!![column + rowOffset] = outlineIndex.toByte()
				}
				column--
			}
			row--
		}
	}

	@OriginalMember(owner = "client!rh", name = "b", descriptor = "(I)V")
	fun drawSmallBorder(@OriginalArg(0) borderThickness: Int) {
		@Pc(2) val fullWidth: Int = this.widthSum()
		@Pc(5) val fullHeight: Int = this.heightSum()
		if (this.width == fullWidth && this.height == fullHeight) {
			return
		}
		@Pc(16) var padLeft: Int = borderThickness
		if (borderThickness > this.minX) {
			padLeft = this.minX
		}
		@Pc(25) var padRight: Int = borderThickness
		if (borderThickness + this.minX + this.width > fullWidth) {
			padRight = fullWidth - this.minX - this.width
		}
		@Pc(44) var padTop: Int = borderThickness
		if (borderThickness > this.minY) {
			padTop = this.minY
		}
		@Pc(53) var padBottom: Int = borderThickness
		if (borderThickness + this.minY + this.height > fullHeight) {
			padBottom = fullHeight - this.minY - this.height
		}
		@Pc(77) val newWidth: Int = this.width + padLeft + padRight
		@Pc(84) val newHeight: Int = this.height + padTop + padBottom
		@Pc(89) val newPixels: ByteArray = ByteArray(newWidth * newHeight)
		@Pc(100) val local100: Int
		@Pc(108) val local108: Int
		@Pc(110) val local110: Int
		if (this.alpha == null) {
			for (@Pc(94) i in 0 until this.height) {
				var srcIndex: Int = i * this.width
				var dstIndex: Int = (i + padTop) * newWidth + padLeft
				for (column in 0 until this.width) {
					newPixels[dstIndex++] = this.pixels!![srcIndex++]
				}
			}
		} else {
			@Pc(136) val newAlpha: ByteArray = ByteArray(newWidth * newHeight)
			for (row in 0 until this.height) {
				var srcIndex: Int = row * this.width
				var dstIndex: Int = (row + padTop) * newWidth + padLeft
				for (@Pc(154) j in 0 until this.width) {
					newAlpha[dstIndex] = this.alpha!![srcIndex]
					newPixels[dstIndex++] = this.pixels!![srcIndex++]
				}
			}
			this.alpha = newAlpha
		}
		this.minX -= padLeft
		this.minY -= padTop
		this.borderX -= padRight
		this.borderY -= padBottom
		this.width = newWidth
		this.height = newHeight
		this.pixels = newPixels
	}

	@OriginalMember(owner = "client!rh", name = "b", descriptor = "()V")
	fun flipVertical() {
		@Pc(2) val pixels: ByteArray? = this.pixels
		@Pc(37) var tmp: Byte
		@Pc(12) val local12: Int
		@Pc(18) val local18: Int
		@Pc(28) val local28: Int
		@Pc(32) val local32: Int
		if (this.alpha == null) {
			var row: Int = (this.height shr 1) - 1
			while (row >= 0) {
				var topIndex: Int = row * this.width
				var bottomIndex: Int = (this.height - row - 1) * this.width
				var column: Int = -this.width
				while (column < 0) {
					tmp = pixels!![topIndex]
					pixels[topIndex] = pixels[bottomIndex]
					pixels[bottomIndex] = tmp
					topIndex++
					bottomIndex++
					column++
				}
				row--
			}
		} else {
			@Pc(59) val alpha: ByteArray? = this.alpha
			var row: Int = (this.height shr 1) - 1
			while (row >= 0) {
				var topIndex: Int = row * this.width
				var bottomIndex: Int = (this.height - row - 1) * this.width
				@Pc(86) var i: Int = -this.width
				while (i < 0) {
					tmp = pixels!![topIndex]
					pixels[topIndex] = pixels[bottomIndex]
					pixels[bottomIndex] = tmp
					tmp = alpha!![topIndex]
					alpha[topIndex] = alpha[bottomIndex]
					alpha[bottomIndex] = tmp
					topIndex++
					bottomIndex++
					i++
				}
				row--
			}
		}
		val oldMinY: Int = this.minY
		this.minY = this.borderY
		this.borderY = oldMinY
	}

	@OriginalMember(owner = "client!rh", name = "c", descriptor = "(I)V")
	fun drawThickBorder(@OriginalArg(0) color: Int) {
		@Pc(1) var borderIndex: Int = -1
		@Pc(8) val local8: Int
		@Pc(69) val local69: Int
		@Pc(73) val local73: Int
		@Pc(75) val local75: Int
		if (this.pallete!!.size < 255) {
			for (paletteIndex in 0 until this.pallete!!.size) {
				if (this.pallete!![paletteIndex] == color) {
					borderIndex = paletteIndex
					break
				}
			}
			if (borderIndex == -1) {
				borderIndex = this.pallete!!.size
				@Pc(38) val newPalette: IntArray = IntArray(this.pallete!!.size + 1)
				Arrays.copy(this.pallete, 0, newPalette, 0, this.pallete!!.size)
				this.pallete = newPalette
				newPalette[borderIndex] = color
			}
		} else {
			var bestDistance: Int = Int.MAX_VALUE
			@Pc(63) val targetRed: Int = (color shr 16) and 0xFF
			val targetGreen: Int = (color shr 8) and 0xFF
			val targetBlue: Int = color and 0xFF
			for (paletteIndex in 0 until this.pallete!!.size) {
				@Pc(81) val paletteColor: Int = this.pallete!![paletteIndex]
				@Pc(87) val red: Int = (paletteColor shr 16) and 0xFF
				@Pc(93) val green: Int = (paletteColor shr 8) and 0xFF
				@Pc(97) val blue: Int = paletteColor and 0xFF
				@Pc(101) var redDiff: Int = targetRed - red
				if (redDiff < 0) {
					redDiff = -redDiff
				}
				@Pc(110) var greenDiff: Int = targetGreen - green
				if (greenDiff < 0) {
					greenDiff = -greenDiff
				}
				@Pc(119) var blueDiff: Int = targetBlue - blue
				if (blueDiff < 0) {
					blueDiff = -blueDiff
				}
				@Pc(130) val distance: Int = redDiff + greenDiff + blueDiff
				if (distance < bestDistance) {
					bestDistance = distance
					borderIndex = paletteIndex
				}
			}
		}
		var pixelIndex: Int = 0
		@Pc(152) val newPixels: ByteArray = ByteArray(this.width * this.height)
		for (row in 0 until this.height) {
			for (column in 0 until this.width) {
				var paletteIndex: Int = this.pixels!![pixelIndex].toInt() and 0xFF
				if (this.pallete!![paletteIndex] == 0) {
					if (column > 0 && this.pallete!![this.pixels!![pixelIndex - 1].toInt() and 0xFF] != 0) {
						paletteIndex = borderIndex
					} else if (row > 0 && this.pallete!![this.pixels!![pixelIndex - this.width].toInt() and 0xFF] != 0) {
						paletteIndex = borderIndex
					} else if (column < this.width - 1 && this.pallete!![this.pixels!![pixelIndex + 1].toInt() and 0xFF] != 0) {
						paletteIndex = borderIndex
					} else if (row < this.height - 1 && this.pallete!![this.pixels!![pixelIndex + this.width].toInt() and 0xFF] != 0) {
						paletteIndex = borderIndex
					}
				}
				newPixels[pixelIndex++] = paletteIndex.toByte()
			}
		}
		this.pixels = newPixels
	}

	@OriginalMember(owner = "client!rh", name = "c", descriptor = "()[I")
	fun getPixels(): IntArray {
		@Pc(2) val fullWidth: Int = this.widthSum()
		@Pc(8) val argb: IntArray = IntArray(fullWidth * this.heightSum())
		@Pc(13) var y: Int
		@Pc(19) var srcIndex: Int
		@Pc(29) var dstIndex: Int
		@Pc(31) var x: Int
		if (this.alpha == null) {
			y = 0
			while (y < this.height) {
				srcIndex = y * this.width
				dstIndex = this.minX + (y + this.minY) * fullWidth
				x = 0
				while (x < this.width) {
					@Pc(96) val colour: Int = this.pallete!![this.pixels!![srcIndex++].toInt() and 0xFF]
					if (colour == 0) {
						argb[dstIndex++] = 0
					} else {
						argb[dstIndex++] = colour or 0xFF000000.toInt()
					}
					x++
				}
				y++
			}
		} else {
			y = 0
			while (y < this.height) {
				srcIndex = y * this.width
				dstIndex = this.minX + (y + this.minY) * fullWidth
				x = 0
				while (x < this.width) {
					argb[dstIndex++] = (this.alpha!![srcIndex].toInt() shl 24) or this.pallete!![this.pixels!![srcIndex].toInt() and 0xFF]
					srcIndex++
					x++
				}
				y++
			}
		}
		return argb
	}

	@OriginalMember(owner = "client!rh", name = "d", descriptor = "()V")
	fun flipHorizontal() {
		@Pc(2) val pixels: ByteArray? = this.pixels
		@Pc(29) var tmp: Byte
		@Pc(10) val local10: Int
		@Pc(16) val local16: Int
		@Pc(23) val local23: Int
		if (this.alpha == null) {
			var row: Int = this.height - 1
			while (row >= 0) {
				var leftIndex: Int = row * this.width
				var rightIndex: Int = (row + 1) * this.width
				while (leftIndex < rightIndex) {
					rightIndex--
					tmp = pixels!![leftIndex]
					pixels[leftIndex] = pixels[rightIndex]
					pixels[rightIndex] = tmp
					leftIndex++
				}
				row--
			}
		} else {
			@Pc(50) val alpha: ByteArray? = this.alpha
			var row: Int = this.height - 1
			while (row >= 0) {
				var leftIndex: Int = row * this.width
				@Pc(68) var rowEnd: Int = (row + 1) * this.width
				while (leftIndex < rowEnd) {
					rowEnd--
					tmp = pixels!![leftIndex]
					pixels[leftIndex] = pixels[rowEnd]
					pixels[rowEnd] = tmp
					tmp = alpha!![leftIndex]
					alpha[leftIndex] = alpha[rowEnd]
					alpha[rowEnd] = tmp
					leftIndex++
				}
				row--
			}
		}
		val oldMinX: Int = this.minX
		this.minX = this.borderX
		this.borderX = oldMinX
	}

	@OriginalMember(owner = "client!rh", name = "e", descriptor = "()V")
	fun rotate90DegLeft() {
		@Pc(6) val newPixels: ByteArray = ByteArray(this.width * this.height)
		@Pc(8) var dstIndex: Int = 0
		@Pc(13) val local13: Int
		@Pc(19) val local19: Int
		if (this.alpha == null) {
			for (column in 0 until this.width) {
				var row: Int = this.height - 1
				while (row >= 0) {
					newPixels[dstIndex++] = this.pixels!![column + row * this.width]
					row--
				}
			}
			this.pixels = newPixels
		} else {
			@Pc(52) val newAlpha: ByteArray = ByteArray(this.width * this.height)
			for (column in 0 until this.width) {
				@Pc(60) var i: Int = this.height - 1
				while (i >= 0) {
					newPixels[dstIndex] = this.pixels!![column + i * this.width]
					newAlpha[dstIndex++] = this.alpha!![column + i * this.width]
					i--
				}
			}
			this.pixels = newPixels
			this.alpha = newAlpha
		}
		var swap: Int = this.minY
		this.minY = this.minX
		this.minX = this.borderY
		this.borderY = this.borderX
		this.borderX = this.minY
		swap = this.height
		this.height = this.width
		this.width = swap
	}

	@OriginalMember(owner = "client!rh", name = "f", descriptor = "()I")
	fun heightSum(): Int {
		return this.height + this.minY + this.borderY
	}

	@OriginalMember(owner = "client!rh", name = "g", descriptor = "()V")
	fun expandToFullSize() {
		@Pc(2) val fullWidth: Int = this.widthSum()
		@Pc(5) val fullHeight: Int = this.heightSum()
		if (this.width == fullWidth && this.height == fullHeight) {
			return
		}
		@Pc(19) val newPixels: ByteArray = ByteArray(fullWidth * fullHeight)
		@Pc(29) val local29: Int
		@Pc(35) val local35: Int
		@Pc(45) val local45: Int
		if (this.alpha == null) {
			for (@Pc(80) i in 0 until this.height) {
				var srcIndex: Int = i * this.width
				var dstIndex: Int = (i + this.minY) * fullWidth + this.minX
				for (column in 0 until this.width) {
					newPixels[dstIndex++] = this.pixels!![srcIndex++]
				}
			}
		} else {
			@Pc(27) val newAlpha: ByteArray = ByteArray(fullWidth * fullHeight)
			for (row in 0 until this.height) {
				var srcIndex: Int = row * this.width
				var dstIndex: Int = (row + this.minY) * fullWidth + this.minX
				for (@Pc(47) j in 0 until this.width) {
					newPixels[dstIndex] = this.pixels!![srcIndex]
					newAlpha[dstIndex++] = this.alpha!![srcIndex++]
				}
			}
			this.alpha = newAlpha
		}
		this.borderY = 0
		this.minY = 0
		this.borderX = 0
		this.minX = 0
		this.width = fullWidth
		this.height = fullHeight
		this.pixels = newPixels
	}

	companion object {
		@OriginalMember(owner = "client!rh", name = "a", descriptor = "(Lclient!in;II)[Lclient!rh;")
		@JvmStatic
		fun decodeGroup(@OriginalArg(0) resource: Js5ResourceProvider?, @OriginalArg(1) archive: Int): Array<SpriteType?>? {
			@Pc(5) val data: ByteArray? = resource!!.getFile(archive, 0)
			return if (data == null) null else SpriteDataProvider.load(data)
		}

		@OriginalMember(owner = "client!rh", name = "b", descriptor = "(Lclient!in;I)[Lclient!rh;")
		@JvmStatic
		fun listGroup(@OriginalArg(0) resource: Js5ResourceProvider?, @OriginalArg(1) fileId: Int): Array<SpriteType?>? {
			@Pc(4) val data: ByteArray? = resource!!.getFile(fileId)
			return if (data == null) null else SpriteDataProvider.load(data)
		}
	}
}
