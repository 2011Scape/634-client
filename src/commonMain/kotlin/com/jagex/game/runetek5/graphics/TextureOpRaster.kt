package com.jagex.game.runetek5.graphics

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.utils.ArrayUtils
import com.jagex.game.runetek5.config.material.texture.TextureOpRasterizer
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.config.material.texture.operation.BezierCurveRasterizer
import com.jagex.game.runetek5.config.material.texture.operation.RectangleRasterizer
import com.jagex.game.runetek5.config.material.texture.operation.EllipseRasterizer
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

object TextureOpRaster {

	@OriginalMember(owner = "client!hd", name = "a", descriptor = "(IIIIB)V")
	@JvmStatic
	fun drawHorizontalLine(@OriginalArg(0) x1: Int, @OriginalArg(1) colour: Int, @OriginalArg(2) y: Int, @OriginalArg(3) x2: Int) {
		if (x1 < x2) {
			ArrayUtils.setArrayValuesTo(x2, TextureOpRaster.textureOpPixelRows!![y], x1, colour)
		} else {
			ArrayUtils.setArrayValuesTo(x1, TextureOpRaster.textureOpPixelRows!![y], x2, colour)
		}
	}

	@OriginalMember(owner = "client!hd", name = "a", descriptor = "(B[[I)V")
	@JvmStatic
	fun setPixelBuffer(@OriginalArg(1) pixelBuffer: Array<IntArray?>?) {
		TextureOpRaster.textureOpPixelRows = pixelBuffer
	}
	@OriginalMember(owner = "client!lj", name = "a", descriptor = "(ZIIII)V")
	@JvmStatic
	fun drawHorizontalLineClipped(@OriginalArg(1) x1: Int, @OriginalArg(2) colour: Int, @OriginalArg(3) x2: Int, @OriginalArg(4) y: Int) {
		if (TextureOpRasterizer.clipMinY <= y && TextureOpRasterizer.clipMaxY >= y) {
			@Pc(19) val clampedX2: Int = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, x2, TextureOpRasterizer.textureOpClipMinX)
			@Pc(25) val clampedX1: Int = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, x1, TextureOpRasterizer.textureOpClipMinX)
			TextureOpRaster.drawHorizontalLine(clampedX1, colour, y, clampedX2)
		}
	}
	@OriginalMember(owner = "client!un", name = "a", descriptor = "(IIIIII)V")
	@JvmStatic
	fun fillRect(@OriginalArg(1) maxX: Int, @OriginalArg(2) color: Int, @OriginalArg(3) maxY: Int, @OriginalArg(4) minX: Int, @OriginalArg(5) minY: Int) {
		if (minX >= TextureOpRasterizer.textureOpClipMinX && TextureOpRasterizer.clipMaxX >= maxX && minY >= TextureOpRasterizer.clipMinY && TextureOpRasterizer.clipMaxY >= maxY) {
			TextureOpRaster.fillRectUnclipped(maxY, maxX, minX, minY, color)
		} else {
			TextureOpRaster.fillRectClipped(minY, minX, maxY, maxX, color)
		}
	}

	@OriginalMember(owner = "client!tw", name = "a", descriptor = "(IIIII)V")
	@JvmStatic
	fun fillCircleUnclipped(@OriginalArg(0) centerY: Int, @OriginalArg(1) centerX: Int, @OriginalArg(3) color: Int, @OriginalArg(4) radius: Int) {
		@Pc(12) var x: Int = 0
		@Pc(14) var y: Int = radius
		@Pc(17) var error: Int = -radius
		@Pc(19) var delta: Int = -1
		ArrayUtils.setArrayValuesTo(centerX + radius, TextureOpRaster.textureOpPixelRows!![centerY], centerX - radius, color)
		while (y > x) {
			delta += 2
			error += delta
			x++
			if (error >= 0) {
				y--
				error -= y shl 1
				@Pc(61) val rowBelow: IntArray? = TextureOpRaster.textureOpPixelRows!![centerY + y]
				@Pc(68) val rowAbove: IntArray? = TextureOpRaster.textureOpPixelRows!![centerY - y]
				@Pc(72) val spanRight: Int = x + centerX
				@Pc(77) val spanLeft: Int = centerX - x
				ArrayUtils.setArrayValuesTo(spanRight, rowBelow, spanLeft, color)
				ArrayUtils.setArrayValuesTo(spanRight, rowAbove, spanLeft, color)
			}
			@Pc(93) val right: Int = y + centerX
			@Pc(97) val left: Int = centerX - y
			@Pc(103) val lowerRow: IntArray? = TextureOpRaster.textureOpPixelRows!![x + centerY]
			@Pc(110) val upperRow: IntArray? = TextureOpRaster.textureOpPixelRows!![centerY - x]
			ArrayUtils.setArrayValuesTo(right, lowerRow, left, color)
			ArrayUtils.setArrayValuesTo(right, upperRow, left, color)
		}
	}

	@OriginalMember(owner = "client!wh", name = "a", descriptor = "(IIBIIII)V")
	@JvmStatic
	fun fillCircleWithStrokeUnclipped(@OriginalArg(0) outerRadius: Int, @OriginalArg(1) centerX: Int, @OriginalArg(3) strokeWidth: Int, @OriginalArg(4) centerY: Int, @OriginalArg(5) fillColor: Int, @OriginalArg(6) strokeColor: Int) {
		TextureOpRaster.ensureCircleBufferCapacity(outerRadius)
		@Pc(21) var x: Int = 0
		@Pc(26) var innerRadius: Int = outerRadius - strokeWidth
		if (innerRadius < 0) {
			innerRadius = 0
		}
		@Pc(35) var outerY: Int = outerRadius
		@Pc(38) var outerError: Int = -outerRadius
		@Pc(40) var innerY: Int = innerRadius
		@Pc(43) var innerError: Int = -innerRadius
		@Pc(45) var outerDelta: Int = -1
		@Pc(47) var innerDelta: Int = -1
		@Pc(51) val scanline: IntArray? = TextureOpRaster.textureOpPixelRows!![centerY]
		@Pc(56) val innerLeft: Int = centerX - innerRadius
		@Pc(60) val innerRight: Int = innerRadius + centerX
		ArrayUtils.setArrayValuesTo(innerLeft, scanline, centerX - outerRadius, strokeColor)
		ArrayUtils.setArrayValuesTo(innerRight, scanline, innerLeft, fillColor)
		ArrayUtils.setArrayValuesTo(outerRadius + centerX, scanline, innerRight, strokeColor)
		while (outerY > x) {
			innerDelta += 2
			outerDelta += 2
			innerError += innerDelta
			outerError += outerDelta
			if (innerError >= 0 && innerY >= 1) {
				TextureOpRaster.circleInnerRadiusBuffer!![innerY] = x
				innerY--
				innerError -= innerY shl 1
			}
			x++
			@Pc(132) var upperRow: IntArray?
			@Pc(139) var lowerRow: IntArray?
			@Pc(144) var local144: Int
			@Pc(149) var local149: Int
			@Pc(189) var local189: Int
			@Pc(194) var innerRight2: Int
			@Pc(199) var innerLeft2: Int
			if (outerError >= 0) {
				outerY--
				outerError -= outerY shl 1
				if (innerRadius <= outerY) {
					upperRow = TextureOpRaster.textureOpPixelRows!![outerY + centerY]
					lowerRow = TextureOpRaster.textureOpPixelRows!![centerY - outerY]
					val spanRight: Int = centerX + x
					val spanLeft: Int = centerX - x
					ArrayUtils.setArrayValuesTo(spanRight, upperRow, spanLeft, strokeColor)
					ArrayUtils.setArrayValuesTo(spanRight, lowerRow, spanLeft, strokeColor)
				} else {
					upperRow = TextureOpRaster.textureOpPixelRows!![centerY + outerY]
					lowerRow = TextureOpRaster.textureOpPixelRows!![centerY - outerY]
					val innerExtent: Int = TextureOpRaster.circleInnerRadiusBuffer!![outerY]
					val spanRight: Int = x + centerX
					val spanLeft: Int = centerX - x
					innerRight2 = centerX + innerExtent
					innerLeft2 = centerX - innerExtent
					ArrayUtils.setArrayValuesTo(innerLeft2, upperRow, spanLeft, strokeColor)
					ArrayUtils.setArrayValuesTo(innerRight2, upperRow, innerLeft2, fillColor)
					ArrayUtils.setArrayValuesTo(spanRight, upperRow, innerRight2, strokeColor)
					ArrayUtils.setArrayValuesTo(innerLeft2, lowerRow, spanLeft, strokeColor)
					ArrayUtils.setArrayValuesTo(innerRight2, lowerRow, innerLeft2, fillColor)
					ArrayUtils.setArrayValuesTo(spanRight, lowerRow, innerRight2, strokeColor)
				}
			}
			upperRow = TextureOpRaster.textureOpPixelRows!![centerY + x]
			lowerRow = TextureOpRaster.textureOpPixelRows!![centerY - x]
			val outerRight: Int = centerX + outerY
			val outerLeft: Int = centerX - outerY
			if (innerRadius > x) {
				val innerExtent: Int = if (innerY >= x) innerY else TextureOpRaster.circleInnerRadiusBuffer!![x]
				innerRight2 = innerExtent + centerX
				innerLeft2 = centerX - innerExtent
				ArrayUtils.setArrayValuesTo(innerLeft2, upperRow, outerLeft, strokeColor)
				ArrayUtils.setArrayValuesTo(innerRight2, upperRow, innerLeft2, fillColor)
				ArrayUtils.setArrayValuesTo(outerRight, upperRow, innerRight2, strokeColor)
				ArrayUtils.setArrayValuesTo(innerLeft2, lowerRow, outerLeft, strokeColor)
				ArrayUtils.setArrayValuesTo(innerRight2, lowerRow, innerLeft2, fillColor)
				ArrayUtils.setArrayValuesTo(outerRight, lowerRow, innerRight2, strokeColor)
			} else {
				ArrayUtils.setArrayValuesTo(outerRight, upperRow, outerLeft, strokeColor)
				ArrayUtils.setArrayValuesTo(outerRight, lowerRow, outerLeft, strokeColor)
			}
		}
	}

	@OriginalMember(owner = "client!tea", name = "a", descriptor = "(IIIIIIIIIZ)V")
	@JvmStatic
	fun drawBezierCurve(@OriginalArg(0) p0y: Int, @OriginalArg(1) p1y: Int, @OriginalArg(2) color: Int, @OriginalArg(3) p3y: Int, @OriginalArg(4) p1x: Int, @OriginalArg(5) p0x: Int, @OriginalArg(6) p2x: Int, @OriginalArg(7) p2y: Int, @OriginalArg(8) p3x: Int) {
		if (TextureOpRasterizer.textureOpClipMinX <= p0x && p0x <= TextureOpRasterizer.clipMaxX && p1x >= TextureOpRasterizer.textureOpClipMinX && TextureOpRasterizer.clipMaxX >= p1x && p2x >= TextureOpRasterizer.textureOpClipMinX && TextureOpRasterizer.clipMaxX >= p2x && p3x >= TextureOpRasterizer.textureOpClipMinX && p3x <= TextureOpRasterizer.clipMaxX && TextureOpRasterizer.clipMinY <= p0y && p0y <= TextureOpRasterizer.clipMaxY && TextureOpRasterizer.clipMinY <= p1y && p1y <= TextureOpRasterizer.clipMaxY && p2y >= TextureOpRasterizer.clipMinY && p2y <= TextureOpRasterizer.clipMaxY && p3y >= TextureOpRasterizer.clipMinY && TextureOpRasterizer.clipMaxY >= p3y) {
			BezierCurveRasterizer.drawUnclippedBezier(p1y, p2y, p1x, p3x, p3y, p2x, p0y, color, p0x)
		} else {
			BezierCurveRasterizer.drawClippedBezier(color, p2x, p0x, p1y, p1x, p2y, p3x, p0y, p3y)
		}
	}

	@OriginalMember(owner = "client!pia", name = "a", descriptor = "(IIIIBIII)V")
	@JvmStatic
	fun fillRectWithStroke(@OriginalArg(0) fillColour: Int, @OriginalArg(1) minY: Int, @OriginalArg(2) minX: Int, @OriginalArg(3) strokeColour: Int, @OriginalArg(5) strokeWidth: Int, @OriginalArg(6) maxX: Int, @OriginalArg(7) maxY: Int) {
		if (TextureOpRasterizer.textureOpClipMinX <= minX && maxX <= TextureOpRasterizer.clipMaxX && minY >= TextureOpRasterizer.clipMinY && TextureOpRasterizer.clipMaxY >= maxY) {
			TextureOpRaster.fillRectWithStrokeUnclipped(strokeColour, maxY, minY, maxX, fillColour, strokeWidth, minX)
		} else {
			RectangleRasterizer.drawClippedStrokedAndFilledRectangle(maxY, maxX, minY, fillColour, strokeWidth, minX, strokeColour)
		}
	}

	@OriginalMember(owner = "client!sba", name = "a", descriptor = "(IIIIBI)V")
	@JvmStatic
	fun fillRectClipped(@OriginalArg(0) top: Int, @OriginalArg(1) left: Int, @OriginalArg(2) bottom: Int, @OriginalArg(3) right: Int, @OriginalArg(5) color: Int) {
		@Pc(11) val clippedTop: Int = ArrayUtils.clamp(TextureOpRasterizer.clipMaxY, top, TextureOpRasterizer.clipMinY)
		@Pc(17) val clippedBottom: Int = ArrayUtils.clamp(TextureOpRasterizer.clipMaxY, bottom, TextureOpRasterizer.clipMinY)
		@Pc(23) val clippedLeft: Int = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, left, TextureOpRasterizer.textureOpClipMinX)
		@Pc(29) val clippedRight: Int = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, right, TextureOpRasterizer.textureOpClipMinX)
		@Pc(39) var i: Int = clippedTop
		while (i <= clippedBottom) {
			ArrayUtils.setArrayValuesTo(clippedRight, TextureOpRaster.textureOpPixelRows!![i], clippedLeft, color)
			i++
		}
	}
	@OriginalMember(owner = "client!fo", name = "a", descriptor = "(IIIIII)V")
	@JvmStatic
	fun fillEllipseClipped(@OriginalArg(0) centerX: Int, @OriginalArg(1) radiusY: Int, @OriginalArg(2) centerY: Int, @OriginalArg(4) color: Int, @OriginalArg(5) radiusX: Int) {
		@Pc(7) var x: Int = 0
		@Pc(9) var y: Int = radiusY
		@Pc(13) val rxSq: Int = radiusX * radiusX
		@Pc(17) val rySq: Int = radiusY * radiusY
		@Pc(21) val twoRySq: Int = rySq shl 1
		@Pc(25) val twoRxSq: Int = rxSq shl 1
		@Pc(29) val diameterY: Int = radiusY shl 1
		@Pc(38) var decisionParam1: Int = twoRySq + rxSq * (1 - diameterY)
		@Pc(47) var decisionParam2: Int = rySq - (diameterY - 1) * twoRxSq
		@Pc(51) val fourRxSq: Int = rxSq shl 2
		@Pc(55) val fourRySq: Int = rySq shl 2
		@Pc(63) var gradientX: Int = twoRySq * 3
		@Pc(71) var gradientY: Int = twoRxSq * ((radiusY shl 1) - 3)
		@Pc(77) var stepX: Int = fourRySq
		@Pc(99) var local99: Int
		@Pc(108) var local108: Int
		if (centerY >= TextureOpRasterizer.clipMinY && centerY <= TextureOpRasterizer.clipMaxY) {
			val rowRight: Int = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, centerX + radiusX, TextureOpRasterizer.textureOpClipMinX)
			val rowLeft: Int = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, centerX - radiusX, TextureOpRasterizer.textureOpClipMinX)
			ArrayUtils.setArrayValuesTo(rowRight, TextureOpRaster.textureOpPixelRows!![centerY], rowLeft, color)
		}
		@Pc(122) var stepY: Int = fourRxSq * (radiusY - 1)
		while (y > 0) {
			if (decisionParam1 < 0) {
				while (decisionParam1 < 0) {
					decisionParam2 += stepX
					decisionParam1 += gradientX
					x++
					gradientX += fourRySq
					stepX += fourRySq
				}
			}
			if (decisionParam2 < 0) {
				decisionParam1 += gradientX
				decisionParam2 += stepX
				gradientX += fourRySq
				stepX += fourRySq
				x++
			}
			decisionParam2 += -gradientY
			decisionParam1 += -stepY
			stepY -= fourRxSq
			gradientY -= fourRxSq
			y--
			val topRow: Int = centerY - y
			val bottomRow: Int = y + centerY
			if (TextureOpRasterizer.clipMinY <= bottomRow && TextureOpRasterizer.clipMaxY >= topRow) {
				@Pc(225) val clippedRight: Int = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, centerX + x, TextureOpRasterizer.textureOpClipMinX)
				@Pc(234) val clippedLeft: Int = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, centerX - x, TextureOpRasterizer.textureOpClipMinX)
				if (topRow >= TextureOpRasterizer.clipMinY) {
					ArrayUtils.setArrayValuesTo(clippedRight, TextureOpRaster.textureOpPixelRows!![topRow], clippedLeft, color)
				}
				if (bottomRow <= TextureOpRasterizer.clipMaxY) {
					ArrayUtils.setArrayValuesTo(clippedRight, TextureOpRaster.textureOpPixelRows!![bottomRow], clippedLeft, color)
				}
			}
		}
	}

	@OriginalMember(owner = "client!ff", name = "a", descriptor = "(IIIIIII)V")
	@JvmStatic
	fun fillCircleWithStroke(@OriginalArg(0) strokeColor: Int, @OriginalArg(1) radius: Int, @OriginalArg(2) thickness: Int, @OriginalArg(4) centerY: Int, @OriginalArg(5) fillColor: Int, @OriginalArg(6) centerX: Int) {
		if (TextureOpRasterizer.textureOpClipMinX <= centerX - radius && radius + centerX <= TextureOpRasterizer.clipMaxX && TextureOpRasterizer.clipMinY <= centerY - radius && centerY + radius <= TextureOpRasterizer.clipMaxY) {
			TextureOpRaster.fillCircleWithStrokeUnclipped(radius, centerX, thickness, centerY, fillColor, strokeColor)
		} else {
			TextureOpRaster.drawClippedFilledAndStrokedCircle(strokeColor, centerY, fillColor, radius, thickness, centerX)
		}
	}

	@OriginalMember(owner = "client!hg", name = "u", descriptor = "[[I")
	@JvmField
	var textureOpPixelRows: Array<IntArray?>? = null

	@OriginalMember(owner = "client!jaa", name = "a", descriptor = "(IIIIIIII)V")
	@JvmStatic
	fun fillRectWithStrokeUnclipped(@OriginalArg(0) borderColor: Int, @OriginalArg(1) bottom: Int, @OriginalArg(2) top: Int, @OriginalArg(3) right: Int, @OriginalArg(4) fillColor: Int, @OriginalArg(5) thickness: Int, @OriginalArg(7) left: Int) {
		@Pc(22) val innerBottom: Int = bottom - thickness
		@Pc(26) val innerTop: Int = thickness + top
		@Pc(28) var i: Int = top
		while (i < innerTop) {
			ArrayUtils.setArrayValuesTo(right, TextureOpRaster.textureOpPixelRows!![i], left, borderColor)
			i++
		}
		@Pc(47) val innerRight: Int = right - thickness
		@Pc(51) val innerLeft: Int = thickness + left
		@Pc(53) var j: Int = bottom
		while (j > innerBottom) {
			ArrayUtils.setArrayValuesTo(right, TextureOpRaster.textureOpPixelRows!![j], left, borderColor)
			j--
		}
		@Pc(71) var k: Int = innerTop
		while (k <= innerBottom) {
			@Pc(77) val row: IntArray? = TextureOpRaster.textureOpPixelRows!![k]
			ArrayUtils.setArrayValuesTo(innerLeft, row, left, borderColor)
			ArrayUtils.setArrayValuesTo(innerRight, row, innerLeft, fillColor)
			ArrayUtils.setArrayValuesTo(right, row, innerRight, borderColor)
			k++
		}
	}

	@OriginalMember(owner = "client!jfa", name = "a", descriptor = "(IIIIII)V")
	@JvmStatic
	fun fillEllipseUnclipped(@OriginalArg(0) color: Int, @OriginalArg(1) centerX: Int, @OriginalArg(2) centerY: Int, @OriginalArg(3) radiusY: Int, @OriginalArg(4) radiusX: Int) {
		@Pc(7) var x: Int = 0
		@Pc(9) var y: Int = radiusY
		@Pc(13) val rxSq: Int = radiusX * radiusX
		@Pc(17) val rySq: Int = radiusY * radiusY
		@Pc(21) val twoRySq: Int = rySq shl 1
		@Pc(25) val twoRxSq: Int = rxSq shl 1
		@Pc(37) val diameterY: Int = radiusY shl 1
		@Pc(46) var decisionParam1: Int = twoRySq + rxSq * (1 - diameterY)
		@Pc(55) var decisionParam2: Int = rySq - twoRxSq * (diameterY - 1)
		@Pc(59) val fourRxSq: Int = rxSq shl 2
		@Pc(63) val fourRySq: Int = rySq shl 2
		@Pc(71) var gradientX: Int = twoRySq * 3
		@Pc(79) var gradientY: Int = twoRxSq * ((radiusY shl 1) - 3)
		@Pc(85) var stepX: Int = fourRySq
		ArrayUtils.setArrayValuesTo(centerX + radiusX, TextureOpRaster.textureOpPixelRows!![centerY], centerX - radiusX, color)
		@Pc(104) var stepY: Int = fourRxSq * (radiusY - 1)
		while (y > 0) {
			if (decisionParam1 < 0) {
				while (decisionParam1 < 0) {
					decisionParam2 += stepX
					decisionParam1 += gradientX
					x++
					stepX += fourRySq
					gradientX += fourRySq
				}
			}
			if (decisionParam2 < 0) {
				decisionParam2 += stepX
				decisionParam1 += gradientX
				x++
				stepX += fourRySq
				gradientX += fourRySq
			}
			decisionParam2 += -gradientY
			decisionParam1 += -stepY
			stepY -= fourRxSq
			gradientY -= fourRxSq
			y--
			@Pc(178) val rowAbove: Int = centerY - y
			@Pc(183) val rowBelow: Int = centerY + y
			@Pc(188) val right: Int = centerX + x
			@Pc(193) val left: Int = centerX - x
			ArrayUtils.setArrayValuesTo(right, TextureOpRaster.textureOpPixelRows!![rowAbove], left, color)
			ArrayUtils.setArrayValuesTo(right, TextureOpRaster.textureOpPixelRows!![rowBelow], left, color)
		}
	}

	@OriginalMember(owner = "client!nha", name = "a", descriptor = "(IIIIII)V")
	@JvmStatic
	fun fillRectUnclipped(@OriginalArg(0) maxY: Int, @OriginalArg(2) maxX: Int, @OriginalArg(3) minX: Int, @OriginalArg(4) minY: Int, @OriginalArg(5) color: Int) {
		@Pc(3) var i: Int = minY
		while (i <= maxY) {
			ArrayUtils.setArrayValuesTo(maxX, TextureOpRaster.textureOpPixelRows!![i], minX, color)
			i++
		}
	}

	@OriginalMember(owner = "client!nd", name = "a", descriptor = "(BI)V")
	@JvmStatic
	fun ensureCircleBufferCapacity(@OriginalArg(1) radius: Int) {
		if (TextureOpRaster.circleInnerRadiusBuffer == null || TextureOpRaster.circleInnerRadiusBuffer!!.size < radius) {
			TextureOpRaster.circleInnerRadiusBuffer = IntArray(radius)
		}
	}

	@OriginalMember(owner = "client!ua", name = "Ob", descriptor = "[I")
	@JvmField
	var circleInnerRadiusBuffer: IntArray? = null

	@OriginalMember(owner = "client!rfa", name = "a", descriptor = "(IIIIIII)V")
	@JvmStatic
	fun drawClippedFilledAndStrokedCircle(@OriginalArg(0) colour: Int, @OriginalArg(1) y: Int, @OriginalArg(2) fillColour: Int, @OriginalArg(3) radius: Int, @OriginalArg(5) thickness: Int, @OriginalArg(6) x: Int) {
		TextureOpRaster.ensureCircleBufferCapacity(radius)
		@Pc(10) var offset: Int = 0
		@Pc(15) var innerRadius: Int = radius - thickness
		if (innerRadius < 0) {
			innerRadius = 0
		}
		@Pc(24) var outerExtent: Int = radius
		@Pc(27) var outerError: Int = -radius
		@Pc(29) var innerExtent: Int = innerRadius
		@Pc(32) var innerError: Int = -innerRadius
		@Pc(34) var outerStep: Int = -1
		@Pc(53) var local53: Int
		@Pc(64) var local64: Int
		@Pc(72) var local72: Int
		@Pc(81) var local81: Int
		if (y >= TextureOpRasterizer.clipMinY && y <= TextureOpRasterizer.clipMaxY) {
			@Pc(44) val row: IntArray? = TextureOpRaster.textureOpPixelRows!![y]
			val outerLeft: Int = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, x - radius, TextureOpRasterizer.textureOpClipMinX)
			val outerRight: Int = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, x + radius, TextureOpRasterizer.textureOpClipMinX)
			val fillLeft: Int = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, x - innerRadius, TextureOpRasterizer.textureOpClipMinX)
			val fillRight: Int = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, x + innerRadius, TextureOpRasterizer.textureOpClipMinX)
			ArrayUtils.setArrayValuesTo(fillLeft, row, outerLeft, colour)
			ArrayUtils.setArrayValuesTo(fillRight, row, fillLeft, fillColour)
			ArrayUtils.setArrayValuesTo(outerRight, row, fillRight, colour)
		}
		@Pc(101) var innerStep: Int = -1
		while (outerExtent > offset) {
			outerStep += 2
			innerStep += 2
			innerError += innerStep
			outerError += outerStep
			if (innerError >= 0 && innerExtent >= 1) {
				innerExtent--
				TextureOpRaster.circleInnerRadiusBuffer!![innerExtent] = offset
				innerError -= innerExtent shl 1
			}
			offset++
			@Pc(246) var innerRight: Int
			@Pc(254) var innerLeft: Int
			@Pc(265) var row: IntArray?
			@Pc(146) var topRow: Int
			if (outerError >= 0) {
				outerExtent--
				outerError -= outerExtent shl 1
				topRow = y - outerExtent
				val bottomRow: Int = y + outerExtent
				if (bottomRow >= TextureOpRasterizer.clipMinY && TextureOpRasterizer.clipMaxY >= topRow) {
					if (innerRadius <= outerExtent) {
						val spanRight: Int = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, offset + x, TextureOpRasterizer.textureOpClipMinX)
						val spanLeft: Int = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, x - offset, TextureOpRasterizer.textureOpClipMinX)
						if (TextureOpRasterizer.clipMaxY >= bottomRow) {
							ArrayUtils.setArrayValuesTo(spanRight, TextureOpRaster.textureOpPixelRows!![bottomRow], spanLeft, colour)
						}
						if (topRow >= TextureOpRasterizer.clipMinY) {
							ArrayUtils.setArrayValuesTo(spanRight, TextureOpRaster.textureOpPixelRows!![topRow], spanLeft, colour)
						}
					} else {
						val innerOffset: Int = TextureOpRaster.circleInnerRadiusBuffer!![outerExtent]
						val spanRight: Int = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, offset + x, TextureOpRasterizer.textureOpClipMinX)
						val spanLeft: Int = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, x - offset, TextureOpRasterizer.textureOpClipMinX)
						innerRight = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, innerOffset + x, TextureOpRasterizer.textureOpClipMinX)
						innerLeft = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, x - innerOffset, TextureOpRasterizer.textureOpClipMinX)
						if (bottomRow <= TextureOpRasterizer.clipMaxY) {
							row = TextureOpRaster.textureOpPixelRows!![bottomRow]
							ArrayUtils.setArrayValuesTo(innerLeft, row, spanLeft, colour)
							ArrayUtils.setArrayValuesTo(innerRight, row, innerLeft, fillColour)
							ArrayUtils.setArrayValuesTo(spanRight, row, innerRight, colour)
						}
						if (TextureOpRasterizer.clipMinY <= topRow) {
							row = TextureOpRaster.textureOpPixelRows!![topRow]
							ArrayUtils.setArrayValuesTo(innerLeft, row, spanLeft, colour)
							ArrayUtils.setArrayValuesTo(innerRight, row, innerLeft, fillColour)
							ArrayUtils.setArrayValuesTo(spanRight, row, innerRight, colour)
						}
					}
				}
			}
			topRow = y - offset
			val lowerRow: Int = y + offset
			if (lowerRow >= TextureOpRasterizer.clipMinY && topRow <= TextureOpRasterizer.clipMaxY) {
				var rowRight: Int = outerExtent + x
				var rowLeft: Int = x - outerExtent
				if (TextureOpRasterizer.textureOpClipMinX <= rowRight && rowLeft <= TextureOpRasterizer.clipMaxX) {
					rowRight = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, rowRight, TextureOpRasterizer.textureOpClipMinX)
					rowLeft = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, rowLeft, TextureOpRasterizer.textureOpClipMinX)
					if (innerRadius <= offset) {
						if (lowerRow <= TextureOpRasterizer.clipMaxY) {
							ArrayUtils.setArrayValuesTo(rowRight, TextureOpRaster.textureOpPixelRows!![lowerRow], rowLeft, colour)
						}
						if (TextureOpRasterizer.clipMinY <= topRow) {
							ArrayUtils.setArrayValuesTo(rowRight, TextureOpRaster.textureOpPixelRows!![topRow], rowLeft, colour)
						}
					} else {
						val innerOffset: Int = if (innerExtent < offset) TextureOpRaster.circleInnerRadiusBuffer!![offset] else innerExtent
						innerRight = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, innerOffset + x, TextureOpRasterizer.textureOpClipMinX)
						innerLeft = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, x - innerOffset, TextureOpRasterizer.textureOpClipMinX)
						if (lowerRow <= TextureOpRasterizer.clipMaxY) {
							row = TextureOpRaster.textureOpPixelRows!![lowerRow]
							ArrayUtils.setArrayValuesTo(innerLeft, row, rowLeft, colour)
							ArrayUtils.setArrayValuesTo(innerRight, row, innerLeft, fillColour)
							ArrayUtils.setArrayValuesTo(rowRight, row, innerRight, colour)
						}
						if (topRow >= TextureOpRasterizer.clipMinY) {
							row = TextureOpRaster.textureOpPixelRows!![topRow]
							ArrayUtils.setArrayValuesTo(innerLeft, row, rowLeft, colour)
							ArrayUtils.setArrayValuesTo(innerRight, row, innerLeft, fillColour)
							ArrayUtils.setArrayValuesTo(rowRight, row, innerRight, colour)
						}
					}
				}
			}
		}
	}

	@OriginalMember(owner = "client!nl", name = "a", descriptor = "(IIIIB)V")
	@JvmStatic
	fun drawFilledCircle(@OriginalArg(0) y: Int, @OriginalArg(1) colour: Int, @OriginalArg(2) x: Int, @OriginalArg(3) radius: Int) {
		if (TextureOpRasterizer.textureOpClipMinX <= x - radius && TextureOpRasterizer.clipMaxX >= radius + x && TextureOpRasterizer.clipMinY <= y - radius && TextureOpRasterizer.clipMaxY >= y + radius) {
			TextureOpRaster.fillCircleUnclipped(y, x, colour, radius)
		} else {
			EllipseRasterizer.fillClippedCircle(x, colour, y, radius)
		}
	}
}
