package com.jagex.game.runetek5.config.material.texture.operation

import com.jagex.game.runetek5.config.material.texture.operation.TextureShapeRasterizer
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.utils.ArrayUtils
import com.jagex.game.runetek5.config.material.texture.TextureOpRasterizer
import com.jagex.game.runetek5.graphics.TextureOpRaster
import kotlin.jvm.JvmStatic

@OriginalClass("client!gfa")
class RectangleRasterizer @OriginalMember(owner = "client!gfa", name = "<init>", descriptor = "(IIIIIII)V") constructor(@OriginalArg(0) left: Int, @OriginalArg(1) top: Int, @OriginalArg(2) right: Int, @OriginalArg(3) bottom: Int, @OriginalArg(4) fillColor: Int, @OriginalArg(5) strokeColor: Int, @OriginalArg(6) lineWidth: Int) : TextureShapeRasterizer(fillColor, strokeColor, lineWidth) {

	@OriginalMember(owner = "client!gfa", name = "j", descriptor = "I")
	private val left: Int

	@OriginalMember(owner = "client!gfa", name = "f", descriptor = "I")
	private val top: Int

	@OriginalMember(owner = "client!gfa", name = "i", descriptor = "I")
	private val bottom: Int

	@OriginalMember(owner = "client!gfa", name = "k", descriptor = "I")
	private val right: Int

	init {
		this.left = left
		this.top = top
		this.bottom = bottom
		this.right = right
	}

	@OriginalMember(owner = "client!gfa", name = "a", descriptor = "(III)V")
	override fun drawFilledAndStroked(@OriginalArg(0) width: Int, @OriginalArg(1) height: Int) {
		@Pc(10) val minX: Int = (width * this.left) shr 12
		@Pc(17) val maxX: Int = (this.right * width) shr 12
		@Pc(24) val minY: Int = (height * this.top) shr 12
		@Pc(36) val maxY: Int = (height * this.bottom) shr 12
		TextureOpRaster.fillRectWithStroke(this.fillColor, minY, minX, this.strokeColor, this.strokeWidth, maxX, maxY)
	}

	@OriginalMember(owner = "client!gfa", name = "c", descriptor = "(III)V")
	override fun drawStroked(@OriginalArg(0) height: Int, @OriginalArg(1) width: Int) {
		@Pc(10) val minX: Int = (width * this.left) shr 12
		@Pc(21) val maxX: Int = (width * this.right) shr 12
		@Pc(28) val minY: Int = (height * this.top) shr 12
		@Pc(35) val maxY: Int = (height * this.bottom) shr 12
		RectangleRasterizer.drawStrokedRectangle(this.strokeColor, this.strokeWidth, minY, maxY, maxX, minX)
	}

	@OriginalMember(owner = "client!gfa", name = "b", descriptor = "(III)V")
	override fun drawFilled(@OriginalArg(1) width: Int, @OriginalArg(2) height: Int) {
		@Pc(10) val minX: Int = (width * this.left) shr 12
		@Pc(17) val maxX: Int = (this.right * width) shr 12
		@Pc(30) val minY: Int = (height * this.top) shr 12
		@Pc(37) val maxY: Int = (height * this.bottom) shr 12
		TextureOpRaster.fillRect(maxX, this.fillColor, maxY, minX, minY)
	}

	companion object {
		@OriginalMember(owner = "client!fu", name = "a", descriptor = "(BIIIII)V")
		@JvmStatic
		fun drawClippedStrokedRectangle(@OriginalArg(1) right: Int, @OriginalArg(2) color: Int, @OriginalArg(3) bottom: Int, @OriginalArg(4) top: Int, @OriginalArg(5) left: Int) {
			var right: Int = right
			var bottom: Int = bottom
			var top: Int = top
			var left: Int = left
			if (TextureOpRasterizer.clipMaxY < top || TextureOpRasterizer.clipMinY > bottom) {
				return
			}
			@Pc(27) val leftVisible: Boolean
			if (left < TextureOpRasterizer.textureOpClipMinX) {
				left = TextureOpRasterizer.textureOpClipMinX
				leftVisible = false
			} else if (left <= TextureOpRasterizer.clipMaxX) {
				leftVisible = true
			} else {
				leftVisible = false
				left = TextureOpRasterizer.clipMaxX
			}
			@Pc(46) val rightVisible: Boolean
			if (TextureOpRasterizer.textureOpClipMinX > right) {
				right = TextureOpRasterizer.textureOpClipMinX
				rightVisible = false
			} else if (right > TextureOpRasterizer.clipMaxX) {
				right = TextureOpRasterizer.clipMaxX
				rightVisible = false
			} else {
				rightVisible = true
			}
			if (top < TextureOpRasterizer.clipMinY) {
				top = TextureOpRasterizer.clipMinY
			} else {
				ArrayUtils.setArrayValuesTo(right, TextureOpRaster.textureOpPixelRows!![top++], left, color)
			}
			if (bottom <= TextureOpRasterizer.clipMaxY) {
				ArrayUtils.setArrayValuesTo(right, TextureOpRaster.textureOpPixelRows!![bottom--], left, color)
			} else {
				bottom = TextureOpRasterizer.clipMaxY
			}
			@Pc(109) var y: Int
			if (leftVisible && rightVisible) {
				y = top
				while (y <= bottom) {
					@Pc(151) val row: IntArray? = TextureOpRaster.textureOpPixelRows!![y]
					row!![right] = color
					row[left] = color
					y++
				}
			} else if (leftVisible) {
				y = top
				while (y <= bottom) {
					TextureOpRaster.textureOpPixelRows!![y]!![left] = color
					y++
				}
			} else if (rightVisible) {
				y = top
				while (y <= bottom) {
					TextureOpRaster.textureOpPixelRows!![y]!![right] = color
					y++
				}
			}
		}

		@OriginalMember(owner = "client!mc", name = "a", descriptor = "(IIIIIII)V")
		@JvmStatic
		fun drawStrokedRectangle(@OriginalArg(0) color: Int, @OriginalArg(2) thickness: Int, @OriginalArg(3) top: Int, @OriginalArg(4) bottom: Int, @OriginalArg(5) right: Int, @OriginalArg(6) left: Int) {
			if (left >= TextureOpRasterizer.textureOpClipMinX && right <= TextureOpRasterizer.clipMaxX && TextureOpRasterizer.clipMinY <= top && bottom <= TextureOpRasterizer.clipMaxY) {
				if (thickness == 1) {
					RectangleRasterizer.drawStrokedRectangleToPixelBuffer(top, color, right, left, bottom)
				} else {
					RectangleRasterizer.drawUnclippedStrokedRectangle(thickness, top, left, right, bottom, color)
				}
			} else if (thickness == 1) {
				RectangleRasterizer.drawClippedStrokedRectangle(right, color, bottom, top, left)
			} else {
				RectangleRasterizer.drawClippedThickStrokedRectangle(left, bottom, thickness, right, color, top)
			}
		}

		@OriginalMember(owner = "client!jl", name = "a", descriptor = "(IIIIIIB)V")
		@JvmStatic
		fun drawClippedThickStrokedRectangle(@OriginalArg(0) left: Int, @OriginalArg(1) bottom: Int, @OriginalArg(2) thickness: Int, @OriginalArg(3) right: Int, @OriginalArg(4) color: Int, @OriginalArg(5) top: Int) {
			@Pc(11) val clippedTop: Int = ArrayUtils.clamp(TextureOpRasterizer.clipMaxY, top, TextureOpRasterizer.clipMinY)
			@Pc(17) val clippedBottom: Int = ArrayUtils.clamp(TextureOpRasterizer.clipMaxY, bottom, TextureOpRasterizer.clipMinY)
			@Pc(34) val clippedLeft: Int = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, left, TextureOpRasterizer.textureOpClipMinX)
			@Pc(40) val clippedRight: Int = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, right, TextureOpRasterizer.textureOpClipMinX)
			@Pc(48) val innerTop: Int = ArrayUtils.clamp(TextureOpRasterizer.clipMaxY, top + thickness, TextureOpRasterizer.clipMinY)
			@Pc(57) val innerBottom: Int = ArrayUtils.clamp(TextureOpRasterizer.clipMaxY, bottom - thickness, TextureOpRasterizer.clipMinY)
			@Pc(59) var i: Int = clippedTop
			while (i < innerTop) {
				ArrayUtils.setArrayValuesTo(clippedRight, TextureOpRaster.textureOpPixelRows!![i], clippedLeft, color)
				i++
			}
			@Pc(79) var j: Int = clippedBottom
			while (j > innerBottom) {
				ArrayUtils.setArrayValuesTo(clippedRight, TextureOpRaster.textureOpPixelRows!![j], clippedLeft, color)
				j--
			}
			@Pc(101) val innerLeft: Int = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, thickness + left, TextureOpRasterizer.textureOpClipMinX)
			@Pc(110) val innerRight: Int = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, right - thickness, TextureOpRasterizer.textureOpClipMinX)
			@Pc(112) var k: Int = innerTop
			while (k <= innerBottom) {
				@Pc(118) val row: IntArray? = TextureOpRaster.textureOpPixelRows!![k]
				ArrayUtils.setArrayValuesTo(innerLeft, row, clippedLeft, color)
				ArrayUtils.setArrayValuesTo(clippedRight, row, innerRight, color)
				k++
			}
		}

		@OriginalMember(owner = "client!qm", name = "a", descriptor = "(IIIIIIII)V")
		@JvmStatic
		fun drawClippedStrokedAndFilledRectangle(@OriginalArg(0) bottom: Int, @OriginalArg(1) right: Int, @OriginalArg(2) top: Int, @OriginalArg(3) fillColor: Int, @OriginalArg(4) thickness: Int, @OriginalArg(6) left: Int, @OriginalArg(7) borderColor: Int) {
			@Pc(11) val clippedTop: Int = ArrayUtils.clamp(TextureOpRasterizer.clipMaxY, top, TextureOpRasterizer.clipMinY)
			@Pc(17) val clippedBottom: Int = ArrayUtils.clamp(TextureOpRasterizer.clipMaxY, bottom, TextureOpRasterizer.clipMinY)
			@Pc(23) val clippedLeft: Int = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, left, TextureOpRasterizer.textureOpClipMinX)
			@Pc(29) val clippedRight: Int = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, right, TextureOpRasterizer.textureOpClipMinX)
			@Pc(37) val innerTop: Int = ArrayUtils.clamp(TextureOpRasterizer.clipMaxY, top + thickness, TextureOpRasterizer.clipMinY)
			@Pc(46) val innerBottom: Int = ArrayUtils.clamp(TextureOpRasterizer.clipMaxY, bottom - thickness, TextureOpRasterizer.clipMinY)
			@Pc(52) var i: Int = clippedTop
			while (i < innerTop) {
				ArrayUtils.setArrayValuesTo(clippedRight, TextureOpRaster.textureOpPixelRows!![i], clippedLeft, borderColor)
				i++
			}
			@Pc(72) var j: Int = clippedBottom
			while (j > innerBottom) {
				ArrayUtils.setArrayValuesTo(clippedRight, TextureOpRaster.textureOpPixelRows!![j], clippedLeft, borderColor)
				j--
			}
			@Pc(94) val innerLeft: Int = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, left + thickness, TextureOpRasterizer.textureOpClipMinX)
			@Pc(103) val innerRight: Int = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, right - thickness, TextureOpRasterizer.textureOpClipMinX)
			@Pc(105) var k: Int = innerTop
			while (k <= innerBottom) {
				@Pc(111) val row: IntArray? = TextureOpRaster.textureOpPixelRows!![k]
				ArrayUtils.setArrayValuesTo(innerLeft, row, clippedLeft, borderColor)
				ArrayUtils.setArrayValuesTo(innerRight, row, innerLeft, fillColor)
				ArrayUtils.setArrayValuesTo(clippedRight, row, innerRight, borderColor)
				k++
			}
		}

		@OriginalMember(owner = "client!lha", name = "a", descriptor = "(IIIIIII)V")
		@JvmStatic
		fun drawUnclippedStrokedRectangle(@OriginalArg(0) borderWidth: Int, @OriginalArg(1) top: Int, @OriginalArg(2) left: Int, @OriginalArg(3) right: Int, @OriginalArg(4) bottom: Int, @OriginalArg(6) color: Int) {
			@Pc(14) val topBorderEnd: Int = top + borderWidth
			@Pc(19) val bottomBorderStart: Int = bottom - borderWidth
			@Pc(21) var i: Int = top
			while (i < topBorderEnd) {
				ArrayUtils.setArrayValuesTo(right, TextureOpRaster.textureOpPixelRows!![i], left, color)
				i++
			}
			@Pc(40) val rightBorderStart: Int = right - borderWidth
			@Pc(42) var j: Int = bottom
			while (j > bottomBorderStart) {
				ArrayUtils.setArrayValuesTo(right, TextureOpRaster.textureOpPixelRows!![j], left, color)
				j--
			}
			@Pc(64) val leftBorderEnd: Int = borderWidth + left
			@Pc(66) var k: Int = topBorderEnd
			while (k <= bottomBorderStart) {
				@Pc(72) val scanline: IntArray? = TextureOpRaster.textureOpPixelRows!![k]
				ArrayUtils.setArrayValuesTo(leftBorderEnd, scanline, left, color)
				ArrayUtils.setArrayValuesTo(right, scanline, rightBorderStart, color)
				k++
			}
		}

		@OriginalMember(owner = "client!qea", name = "a", descriptor = "(IIIIII)V")
		@JvmStatic
		fun drawStrokedRectangleToPixelBuffer(@OriginalArg(0) top: Int, @OriginalArg(1) color: Int, @OriginalArg(3) right: Int, @OriginalArg(4) left: Int, @OriginalArg(5) bottom: Int) {
			@Pc(6) val innerTop: Int = top + 1
			ArrayUtils.setArrayValuesTo(right, TextureOpRaster.textureOpPixelRows!![top], left, color)
			@Pc(19) val innerBottom: Int = bottom - 1
			ArrayUtils.setArrayValuesTo(right, TextureOpRaster.textureOpPixelRows!![bottom], left, color)
			@Pc(33) var i: Int = innerTop
			while (i <= innerBottom) {
				@Pc(39) val scanline: IntArray? = TextureOpRaster.textureOpPixelRows!![i]
				scanline!![right] = color
				scanline[left] = color
				i++
			}
		}
	}
}
