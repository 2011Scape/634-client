package com.jagex.game.runetek5.config.material.texture.operation

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.utils.ArrayUtils
import com.jagex.game.runetek5.config.material.texture.TextureOpRasterizer
import com.jagex.game.runetek5.graphics.TextureOpRaster

@OriginalClass("client!iu")
abstract class TextureShapeRasterizer {

	@OriginalMember(owner = "client!iu", name = "b", descriptor = "I")
	@JvmField
	val strokeWidth: Int

	@OriginalMember(owner = "client!iu", name = "d", descriptor = "I")
	@JvmField
	val fillColor: Int

	@OriginalMember(owner = "client!iu", name = "e", descriptor = "I")
	@JvmField
	val strokeColor: Int

	@OriginalMember(owner = "client!iu", name = "<init>", descriptor = "(III)V")
	constructor(@OriginalArg(0) lineColour: Int, @OriginalArg(1) fillColour: Int, @OriginalArg(2) lineWidth: Int) {
		this.strokeWidth = lineWidth
		this.fillColor = lineColour
		this.strokeColor = fillColour
	}

	@OriginalMember(owner = "client!iu", name = "a", descriptor = "(III)V")
	abstract fun drawFilledAndStroked(@OriginalArg(0) width: Int, @OriginalArg(1) height: Int)

	@OriginalMember(owner = "client!iu", name = "b", descriptor = "(III)V")
	abstract fun drawFilled(@OriginalArg(1) width: Int, @OriginalArg(2) height: Int)

	@OriginalMember(owner = "client!iu", name = "c", descriptor = "(III)V")
	abstract fun drawStroked(@OriginalArg(0) height: Int, @OriginalArg(1) width: Int)

	companion object {
		@OriginalMember(owner = "client!dj", name = "a", descriptor = "(IIIII)V")
		@JvmStatic
		fun fillVerticalLine(@OriginalArg(0) y1: Int, @OriginalArg(1) y2: Int, @OriginalArg(3) x: Int, @OriginalArg(4) colour: Int) {
			@Pc(15) var y: Int
			if (y1 < y2) {
				y = y1
				while (y < y2) {
					TextureOpRaster.textureOpPixelRows!![y]!![x] = colour
					y++
				}
			} else {
				y = y2
				while (y < y1) {
					TextureOpRaster.textureOpPixelRows!![y]!![x] = colour
					y++
				}
			}
		}

		@OriginalMember(owner = "client!dt", name = "a", descriptor = "(IIBII)V")
		@JvmStatic
		fun drawVerticalLine(@OriginalArg(0) y1: Int, @OriginalArg(1) x: Int, @OriginalArg(3) colour: Int, @OriginalArg(4) y2: Int) {
			if (x >= TextureOpRasterizer.textureOpClipMinX && TextureOpRasterizer.clipMaxX >= x) {
				@Pc(24) val clampedY1: Int = ArrayUtils.clamp(TextureOpRasterizer.clipMaxY, y1, TextureOpRasterizer.clipMinY)
				@Pc(30) val clampedY2: Int = ArrayUtils.clamp(TextureOpRasterizer.clipMaxY, y2, TextureOpRasterizer.clipMinY)
				TextureShapeRasterizer.fillVerticalLine(clampedY2, clampedY1, x, colour)
			}
		}

		@OriginalMember(owner = "client!eaa", name = "a", descriptor = "(IBIIII)V")
		@JvmStatic
		fun drawLine(@OriginalArg(0) x0: Int, @OriginalArg(2) color: Int, @OriginalArg(3) y0: Int, @OriginalArg(4) y1: Int, @OriginalArg(5) x1: Int) {
			@Pc(9) val dx: Int = x1 - x0
			@Pc(14) val dy: Int = y1 - y0
			if (dx == 0) {
				if (dy != 0) {
					TextureShapeRasterizer.drawVerticalLine(y0, x0, color, y1)
				}
			} else if (dy == 0) {
				TextureOpRaster.drawHorizontalLineClipped(x1, color, x0, y0)
			} else {
				@Pc(46) val slope: Int = (dy shl 12) / dx
				@Pc(55) val intercept: Int = y0 - ((slope * x0) shr 12)
				@Pc(68) var clippedX0: Int
				@Pc(66) var clippedY0: Int
				if (x0 < TextureOpRasterizer.textureOpClipMinX) {
					clippedY0 = ((slope * TextureOpRasterizer.textureOpClipMinX) shr 12) + intercept
					clippedX0 = TextureOpRasterizer.textureOpClipMinX
				} else if (TextureOpRasterizer.clipMaxX < x0) {
					clippedY0 = intercept + ((TextureOpRasterizer.clipMaxX * slope) shr 12)
					clippedX0 = TextureOpRasterizer.clipMaxX
				} else {
					clippedX0 = x0
					clippedY0 = y0
				}
				@Pc(111) var clippedX1: Int
				@Pc(113) var clippedY1: Int
				if (TextureOpRasterizer.textureOpClipMinX > x1) {
					clippedX1 = TextureOpRasterizer.textureOpClipMinX
					clippedY1 = ((slope * TextureOpRasterizer.textureOpClipMinX) shr 12) + intercept
				} else if (x1 <= TextureOpRasterizer.clipMaxX) {
					clippedX1 = x1
					clippedY1 = y1
				} else {
					clippedX1 = TextureOpRasterizer.clipMaxX
					clippedY1 = intercept + ((TextureOpRasterizer.clipMaxX * slope) shr 12)
				}
				if (TextureOpRasterizer.clipMinY > clippedY1) {
					clippedX1 = ((TextureOpRasterizer.clipMinY - intercept) shl 12) / slope
					clippedY1 = TextureOpRasterizer.clipMinY
				} else if (clippedY1 > TextureOpRasterizer.clipMaxY) {
					clippedX1 = ((TextureOpRasterizer.clipMaxY - intercept) shl 12) / slope
					clippedY1 = TextureOpRasterizer.clipMaxY
				}
				if (TextureOpRasterizer.clipMinY > clippedY0) {
					clippedX0 = ((TextureOpRasterizer.clipMinY - intercept) shl 12) / slope
					clippedY0 = TextureOpRasterizer.clipMinY
				} else if (clippedY0 > TextureOpRasterizer.clipMaxY) {
					clippedX0 = ((TextureOpRasterizer.clipMaxY - intercept) shl 12) / slope
					clippedY0 = TextureOpRasterizer.clipMaxY
				}
				BezierCurveRasterizer.drawBresenhamLine(clippedX0, clippedY0, clippedY1, clippedX1, color)
			}
		}
	}
}
