package com.jagex.game.runetek5.config.material.texture.operation

import com.jagex.game.runetek5.config.material.texture.operation.TextureShapeRasterizer
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.graphics.TextureOpRaster
import kotlin.jvm.JvmStatic

@OriginalClass("client!hc")
class BezierCurveRasterizer @OriginalMember(owner = "client!hc", name = "<init>", descriptor = "(IIIIIIIIII)V") constructor(@OriginalArg(0) controlX0: Int, @OriginalArg(1) controlY0: Int, @OriginalArg(2) controlX1: Int, @OriginalArg(3) controlY1: Int, @OriginalArg(4) controlX2: Int, @OriginalArg(5) controlY2: Int, @OriginalArg(6) controlX3: Int, @OriginalArg(7) controlY3: Int, @OriginalArg(8) strokeColor: Int, @OriginalArg(9) lineWidth: Int) : TextureShapeRasterizer(-1, strokeColor, lineWidth) {

	@OriginalMember(owner = "client!hc", name = "m", descriptor = "I")
	private val controlX3: Int

	@OriginalMember(owner = "client!hc", name = "p", descriptor = "I")
	private val controlX0: Int

	@OriginalMember(owner = "client!hc", name = "t", descriptor = "I")
	private val controlY0: Int

	@OriginalMember(owner = "client!hc", name = "s", descriptor = "I")
	private val controlY3: Int

	@OriginalMember(owner = "client!hc", name = "q", descriptor = "I")
	private val controlY1: Int

	@OriginalMember(owner = "client!hc", name = "f", descriptor = "I")
	private val controlX2: Int

	@OriginalMember(owner = "client!hc", name = "o", descriptor = "I")
	private val controlX1: Int

	@OriginalMember(owner = "client!hc", name = "g", descriptor = "I")
	private val controlY2: Int

	init {
		this.controlX3 = controlX3
		this.controlX0 = controlX0
		this.controlY0 = controlY0
		this.controlY3 = controlY3
		this.controlY1 = controlY1
		this.controlX2 = controlX2
		this.controlX1 = controlX1
		this.controlY2 = controlY2
	}

	@OriginalMember(owner = "client!hc", name = "c", descriptor = "(III)V")
	override fun drawStroked(@OriginalArg(0) height: Int, @OriginalArg(1) width: Int) {
		@Pc(10) val x0: Int = (this.controlX0 * width) shr 12
		@Pc(17) val y0: Int = (this.controlY0 * height) shr 12
		@Pc(24) val x1: Int = (this.controlX1 * width) shr 12
		@Pc(31) val y1: Int = (this.controlY1 * height) shr 12
		@Pc(38) val x2: Int = (width * this.controlX2) shr 12
		@Pc(45) val y2: Int = (this.controlY2 * height) shr 12
		@Pc(52) val x3: Int = (width * this.controlX3) shr 12
		@Pc(64) val y3: Int = (this.controlY3 * height) shr 12
		TextureOpRaster.drawBezierCurve(y0, y1, this.strokeColor, y3, x1, x0, x2, y2, x3)
	}

	@OriginalMember(owner = "client!hc", name = "b", descriptor = "(III)V")
	override fun drawFilled(@OriginalArg(1) width: Int, @OriginalArg(2) height: Int) {
	}

	@OriginalMember(owner = "client!hc", name = "a", descriptor = "(III)V")
	override fun drawFilledAndStroked(@OriginalArg(0) width: Int, @OriginalArg(1) height: Int) {
	}

	companion object {
		@OriginalMember(owner = "client!jm", name = "a", descriptor = "(ILclient!cea;)Lclient!hc;")
		@JvmStatic
		fun decode(@OriginalArg(1) packet: Packet?): BezierCurveRasterizer {
			return BezierCurveRasterizer(packet!!.readShort(), packet.readShort(), packet.readShort(), packet.readShort(), packet.readShort(), packet.readShort(), packet.readShort(), packet.readShort(), packet.readUMedium(), packet.readUByte())
		}

		@OriginalMember(owner = "client!cj", name = "a", descriptor = "(IIIIIIIIII)V")
		@JvmStatic
		fun drawClippedBezier(@OriginalArg(0) color: Int, @OriginalArg(1) controlX2: Int, @OriginalArg(2) startX: Int, @OriginalArg(3) controlY1: Int, @OriginalArg(4) controlX1: Int, @OriginalArg(5) controlY2: Int, @OriginalArg(6) endX: Int, @OriginalArg(7) startY: Int, @OriginalArg(9) endY: Int) {
			if (startX == controlX1 && controlY1 == startY && endX == controlX2 && controlY2 == endY) {
				TextureShapeRasterizer.drawLine(startX, color, startY, endY, endX)
				return
			}
			@Pc(27) var prevX: Int = startX
			@Pc(29) var prevY: Int = startY
			@Pc(33) val startX3: Int = startX * 3
			@Pc(37) val startY3: Int = startY * 3
			@Pc(41) val control1X3: Int = controlX1 * 3
			@Pc(45) val control1Y3: Int = controlY1 * 3
			@Pc(49) val control2X3: Int = controlX2 * 3
			@Pc(53) val control2Y3: Int = controlY2 * 3
			@Pc(63) val cubicCoeffX: Int = control1X3 + endX - startX - control2X3
			@Pc(74) val cubicCoeffY: Int = endY + control1Y3 - startY - control2Y3
			@Pc(84) val quadCoeffX: Int = control2X3 + startX3 - control1X3 - control1X3
			@Pc(93) val quadCoeffY: Int = startY3 + control2Y3 - control1Y3 - control1Y3
			@Pc(98) val linearCoeffX: Int = control1X3 - startX3
			@Pc(103) val linearCoeffY: Int = control1Y3 - startY3
			@Pc(105) var i: Int = 128
			while (i <= 4096) {
				@Pc(113) val tSquared: Int = (i * i) shr 12
				@Pc(119) val tCubed: Int = (tSquared * i) shr 12
				@Pc(123) val cubicTermX: Int = cubicCoeffX * tCubed
				@Pc(127) val cubicTermY: Int = cubicCoeffY * tCubed
				@Pc(131) val quadTermX: Int = quadCoeffX * tSquared
				@Pc(135) val quadTermY: Int = tSquared * quadCoeffY
				@Pc(139) val linearTermX: Int = i * linearCoeffX
				@Pc(143) val linearTermY: Int = linearCoeffY * i
				@Pc(156) val x: Int = startX + ((cubicTermX + quadTermX + linearTermX) shr 12)
				@Pc(168) val y: Int = startY + ((quadTermY + cubicTermY + linearTermY) shr 12)
				TextureShapeRasterizer.drawLine(prevX, color, prevY, y, x)
				prevX = x
				prevY = y
				i += 128
			}
		}

		@OriginalMember(owner = "client!pi", name = "a", descriptor = "(IIIIIBIIII)V")
		@JvmStatic
		fun drawUnclippedBezier(@OriginalArg(0) p1y: Int, @OriginalArg(1) p2y: Int, @OriginalArg(2) p1x: Int, @OriginalArg(3) p3x: Int, @OriginalArg(4) p3y: Int, @OriginalArg(6) p2x: Int, @OriginalArg(7) p0y: Int, @OriginalArg(8) color: Int, @OriginalArg(9) p0x: Int) {
			if (p1x == p0x && p0y == p1y && p2x == p3x && p2y == p3y) {
				BezierCurveRasterizer.drawBresenhamLine(p0x, p0y, p3y, p3x, color)
				return
			}
			@Pc(23) var prevX: Int = p0x
			@Pc(25) var prevY: Int = p0y
			@Pc(29) val p0x3: Int = p0x * 3
			@Pc(33) val p0y3: Int = p0y * 3
			@Pc(37) val p1x3: Int = p1x * 3
			@Pc(41) val p1y3: Int = p1y * 3
			@Pc(45) val p2x3: Int = p2x * 3
			@Pc(49) val p2y3: Int = p2y * 3
			@Pc(59) val coeffAx: Int = p1x3 + p3x - p0x - p2x3
			@Pc(69) val coeffAy: Int = p1y3 + p3y - p0y - p2y3
			@Pc(79) val coeffBx: Int = p0x3 + p2x3 - p1x3 - p1x3
			@Pc(90) val coeffBy: Int = p2y3 + p0y3 - p1y3 - p1y3
			@Pc(95) val coeffCx: Int = p1x3 - p0x3
			@Pc(99) val coeffCy: Int = p1y3 - p0y3
			@Pc(101) var i: Int = 128
			while (i <= 4096) {
				@Pc(109) val tSquared: Int = (i * i) shr 12
				@Pc(115) val tCubed: Int = (tSquared * i) shr 12
				@Pc(119) val axTerm: Int = coeffAx * tCubed
				@Pc(123) val ayTerm: Int = tCubed * coeffAy
				@Pc(127) val bxTerm: Int = coeffBx * tSquared
				@Pc(131) val byTerm: Int = tSquared * coeffBy
				@Pc(135) val cxTerm: Int = coeffCx * i
				@Pc(139) val cyTerm: Int = coeffCy * i
				@Pc(149) val curX: Int = p0x + ((axTerm + bxTerm + cxTerm) shr 12)
				@Pc(160) val curY: Int = p0y + ((ayTerm + byTerm + cyTerm) shr 12)
				BezierCurveRasterizer.drawBresenhamLine(prevX, prevY, curY, curX, color)
				prevY = curY
				prevX = curX
				i += 128
			}
		}

		@OriginalMember(owner = "client!pr", name = "a", descriptor = "(IIIIII)V")
		@JvmStatic
		fun drawBresenhamLine(@OriginalArg(0) x1: Int, @OriginalArg(2) y1: Int, @OriginalArg(3) y2: Int, @OriginalArg(4) x2: Int, @OriginalArg(5) color: Int) {
			var x1: Int = x1
			var y1: Int = y1
			var y2: Int = y2
			var x2: Int = x2
			@Pc(10) var dy: Int = y2 - y1
			@Pc(15) var dx: Int = x2 - x1
			if (dx == 0) {
				if (dy != 0) {
					TextureShapeRasterizer.fillVerticalLine(y2, y1, x1, color)
				}
			} else if (dy == 0) {
				TextureOpRaster.drawHorizontalLine(x2, color, y1, x1)
			} else {
				if (dy < 0) {
					dy = -dy
				}
				if (dx < 0) {
					dx = -dx
				}
				@Pc(61) val steep: Boolean = dx < dy
				@Pc(65) val local65: Int
				@Pc(67) val local67: Int
				if (steep) {
					val tempX1: Int = x1
					val tempX2: Int = x2
					x1 = y1
					x2 = y2
					y1 = tempX1
					y2 = tempX2
				}
				if (x1 > x2) {
					val tempX: Int = x1
					x1 = x2
					val tempY: Int = y1
					x2 = tempX
					y1 = y2
					y2 = tempY
				}
				var minorPos: Int = y1
				val majorDelta: Int = x2 - x1
				@Pc(106) var minorDelta: Int = y2 - y1
				@Pc(111) var error: Int = -(majorDelta shr 1)
				if (minorDelta < 0) {
					minorDelta = -minorDelta
				}
				@Pc(123) val step: Int = if (y2 <= y1) -1 else 1
				@Pc(127) var majorPos: Int
				if (steep) {
					majorPos = x1
					while (majorPos <= x2) {
						TextureOpRaster.textureOpPixelRows!![majorPos]!![minorPos] = color
						error += minorDelta
						if (error > 0) {
							minorPos += step
							error -= majorDelta
						}
						majorPos++
					}
				} else {
					majorPos = x1
					while (majorPos <= x2) {
						error += minorDelta
						TextureOpRaster.textureOpPixelRows!![minorPos]!![majorPos] = color
						if (error > 0) {
							minorPos += step
							error -= majorDelta
						}
						majorPos++
					}
				}
			}
		}
	}
}
