package com.jagex.game.runetek5.config.material.texture.operation;

import com.jagex.game.runetek5.config.material.texture.operation.TextureShapeRasterizer;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.graphics.TextureOpRaster;
import com.jagex.game.runetek5.client.ClientState;

@OriginalClass("client!hc")
public final class BezierCurveRasterizer extends TextureShapeRasterizer {

	@OriginalMember(owner = "client!hc", name = "m", descriptor = "I")
	private final int controlX3;

	@OriginalMember(owner = "client!hc", name = "p", descriptor = "I")
	private final int controlX0;

	@OriginalMember(owner = "client!hc", name = "t", descriptor = "I")
	private final int controlY0;

	@OriginalMember(owner = "client!hc", name = "s", descriptor = "I")
	private final int controlY3;

	@OriginalMember(owner = "client!hc", name = "q", descriptor = "I")
	private final int controlY1;

	@OriginalMember(owner = "client!hc", name = "f", descriptor = "I")
	private final int controlX2;

	@OriginalMember(owner = "client!hc", name = "o", descriptor = "I")
	private final int controlX1;

	@OriginalMember(owner = "client!hc", name = "g", descriptor = "I")
	private final int controlY2;

	@OriginalMember(owner = "client!hc", name = "<init>", descriptor = "(IIIIIIIIII)V")
	public BezierCurveRasterizer(@OriginalArg(0) int controlX0, @OriginalArg(1) int controlY0, @OriginalArg(2) int controlX1, @OriginalArg(3) int controlY1, @OriginalArg(4) int controlX2, @OriginalArg(5) int controlY2, @OriginalArg(6) int controlX3, @OriginalArg(7) int controlY3, @OriginalArg(8) int strokeColor, @OriginalArg(9) int lineWidth) {
		super(-1, strokeColor, lineWidth);
		this.controlX3 = controlX3;
		this.controlX0 = controlX0;
		this.controlY0 = controlY0;
		this.controlY3 = controlY3;
		this.controlY1 = controlY1;
		this.controlX2 = controlX2;
		this.controlX1 = controlX1;
		this.controlY2 = controlY2;
	}

	@OriginalMember(owner = "client!hc", name = "c", descriptor = "(III)V")
	@Override
	public void drawStroked(@OriginalArg(0) int height, @OriginalArg(1) int width) {
		@Pc(10) int x0 = this.controlX0 * width >> 12;
		@Pc(17) int y0 = this.controlY0 * height >> 12;
		@Pc(24) int x1 = this.controlX1 * width >> 12;
		@Pc(31) int y1 = this.controlY1 * height >> 12;
		@Pc(38) int x2 = width * this.controlX2 >> 12;
		@Pc(45) int y2 = this.controlY2 * height >> 12;
		@Pc(52) int x3 = width * this.controlX3 >> 12;
		@Pc(64) int y3 = this.controlY3 * height >> 12;
		TextureOpRaster.drawBezierCurve(y0, y1, super.strokeColor, y3, x1, x0, x2, y2, x3);
	}

	@OriginalMember(owner = "client!hc", name = "b", descriptor = "(III)V")
	@Override
	public void drawFilled(@OriginalArg(1) int width, @OriginalArg(2) int height) {
	}

	@OriginalMember(owner = "client!hc", name = "a", descriptor = "(III)V")
	@Override
	public void drawFilledAndStroked(@OriginalArg(0) int width, @OriginalArg(1) int height) {
	}
	@OriginalMember(owner = "client!jm", name = "a", descriptor = "(ILclient!cea;)Lclient!hc;")
	public static BezierCurveRasterizer decode(@OriginalArg(1) Packet packet) {
		return new BezierCurveRasterizer(packet.readShort(), packet.readShort(), packet.readShort(), packet.readShort(), packet.readShort(), packet.readShort(), packet.readShort(), packet.readShort(), packet.readUMedium(), packet.readUByte());
	}
	@OriginalMember(owner = "client!cj", name = "a", descriptor = "(IIIIIIIIII)V")
	public static void drawClippedBezier(@OriginalArg(0) int color, @OriginalArg(1) int controlX2, @OriginalArg(2) int startX, @OriginalArg(3) int controlY1, @OriginalArg(4) int controlX1, @OriginalArg(5) int controlY2, @OriginalArg(6) int endX, @OriginalArg(7) int startY, @OriginalArg(9) int endY) {
		if (startX == controlX1 && controlY1 == startY && endX == controlX2 && controlY2 == endY) {
			TextureShapeRasterizer.drawLine(startX, color, startY, endY, endX);
			return;
		}
		@Pc(27) int prevX = startX;
		@Pc(29) int prevY = startY;
		@Pc(33) int startX3 = startX * 3;
		@Pc(37) int startY3 = startY * 3;
		@Pc(41) int control1X3 = controlX1 * 3;
		@Pc(45) int control1Y3 = controlY1 * 3;
		@Pc(49) int control2X3 = controlX2 * 3;
		@Pc(53) int control2Y3 = controlY2 * 3;
		@Pc(63) int cubicCoeffX = control1X3 + endX - startX - control2X3;
		@Pc(74) int cubicCoeffY = endY + control1Y3 - startY - control2Y3;
		@Pc(84) int quadCoeffX = control2X3 + startX3 - control1X3 - control1X3;
		@Pc(93) int quadCoeffY = startY3 + control2Y3 - control1Y3 - control1Y3;
		@Pc(98) int linearCoeffX = control1X3 - startX3;
		@Pc(103) int linearCoeffY = control1Y3 - startY3;
		for (@Pc(105) int i = 128; i <= 4096; i += 128) {
			@Pc(113) int tSquared = i * i >> 12;
			@Pc(119) int tCubed = tSquared * i >> 12;
			@Pc(123) int cubicTermX = cubicCoeffX * tCubed;
			@Pc(127) int cubicTermY = cubicCoeffY * tCubed;
			@Pc(131) int quadTermX = quadCoeffX * tSquared;
			@Pc(135) int quadTermY = tSquared * quadCoeffY;
			@Pc(139) int linearTermX = i * linearCoeffX;
			@Pc(143) int linearTermY = linearCoeffY * i;
			@Pc(156) int x = startX + (cubicTermX + quadTermX + linearTermX >> 12);
			@Pc(168) int y = startY + (quadTermY + cubicTermY + linearTermY >> 12);
			TextureShapeRasterizer.drawLine(prevX, color, prevY, y, x);
			prevX = x;
			prevY = y;
		}
	}
	@OriginalMember(owner = "client!pi", name = "a", descriptor = "(IIIIIBIIII)V")
	public static void drawUnclippedBezier(@OriginalArg(0) int p1y, @OriginalArg(1) int p2y, @OriginalArg(2) int p1x, @OriginalArg(3) int p3x, @OriginalArg(4) int p3y, @OriginalArg(6) int p2x, @OriginalArg(7) int p0y, @OriginalArg(8) int color, @OriginalArg(9) int p0x) {
		if (p1x == p0x && p0y == p1y && p2x == p3x && p2y == p3y) {
			BezierCurveRasterizer.drawBresenhamLine(p0x, p0y, p3y, p3x, color);
			return;
		}
		@Pc(23) int prevX = p0x;
		@Pc(25) int prevY = p0y;
		@Pc(29) int p0x3 = p0x * 3;
		@Pc(33) int p0y3 = p0y * 3;
		@Pc(37) int p1x3 = p1x * 3;
		@Pc(41) int p1y3 = p1y * 3;
		@Pc(45) int p2x3 = p2x * 3;
		@Pc(49) int p2y3 = p2y * 3;
		@Pc(59) int coeffAx = p1x3 + p3x - p0x - p2x3;
		@Pc(69) int coeffAy = p1y3 + p3y - p0y - p2y3;
		@Pc(79) int coeffBx = p0x3 + p2x3 - p1x3 - p1x3;
		@Pc(90) int coeffBy = p2y3 + p0y3 - p1y3 - p1y3;
		@Pc(95) int coeffCx = p1x3 - p0x3;
		@Pc(99) int coeffCy = p1y3 - p0y3;
		for (@Pc(101) int i = 128; i <= 4096; i += 128) {
			@Pc(109) int tSquared = i * i >> 12;
			@Pc(115) int tCubed = tSquared * i >> 12;
			@Pc(119) int axTerm = coeffAx * tCubed;
			@Pc(123) int ayTerm = tCubed * coeffAy;
			@Pc(127) int bxTerm = coeffBx * tSquared;
			@Pc(131) int byTerm = tSquared * coeffBy;
			@Pc(135) int cxTerm = coeffCx * i;
			@Pc(139) int cyTerm = coeffCy * i;
			@Pc(149) int curX = p0x + (axTerm + bxTerm + cxTerm >> 12);
			@Pc(160) int curY = p0y + (ayTerm + byTerm + cyTerm >> 12);
			BezierCurveRasterizer.drawBresenhamLine(prevX, prevY, curY, curX, color);
			prevY = curY;
			prevX = curX;
		}
	}

	@OriginalMember(owner = "client!pr", name = "a", descriptor = "(IIIIII)V")
	public static void drawBresenhamLine(@OriginalArg(0) int x1, @OriginalArg(2) int y1, @OriginalArg(3) int y2, @OriginalArg(4) int x2, @OriginalArg(5) int color) {
		@Pc(10) int dy = y2 - y1;
		@Pc(15) int dx = x2 - x1;
		if (dx == 0) {
			if (dy != 0) {
				TextureShapeRasterizer.fillVerticalLine(y2, y1, x1, color);
			}
		} else if (dy == 0) {
			TextureOpRaster.drawHorizontalLine(x2, color, y1, x1);
		} else {
			if (dy < 0) {
				dy = -dy;
			}
			if (dx < 0) {
				dx = -dx;
			}
			@Pc(61) boolean steep = dx < dy;
			@Pc(65) int local65;
			@Pc(67) int local67;
			if (steep) {
				int tempX1 = x1;
				int tempX2 = x2;
				x1 = y1;
				x2 = y2;
				y1 = tempX1;
				y2 = tempX2;
			}
			if (x1 > x2) {
				int tempX = x1;
				x1 = x2;
				int tempY = y1;
				x2 = tempX;
				y1 = y2;
				y2 = tempY;
			}
			int minorPos = y1;
			int majorDelta = x2 - x1;
			@Pc(106) int minorDelta = y2 - y1;
			@Pc(111) int error = -(majorDelta >> 1);
			if (minorDelta < 0) {
				minorDelta = -minorDelta;
			}
			@Pc(123) int step = y2 <= y1 ? -1 : 1;
			@Pc(127) int majorPos;
			if (steep) {
				for (majorPos = x1; majorPos <= x2; majorPos++) {
					TextureOpRaster.textureOpPixelRows[majorPos][minorPos] = color;
					error += minorDelta;
					if (error > 0) {
						minorPos += step;
						error -= majorDelta;
					}
				}
			} else {
				for (majorPos = x1; majorPos <= x2; majorPos++) {
					error += minorDelta;
					TextureOpRaster.textureOpPixelRows[minorPos][majorPos] = color;
					if (error > 0) {
						minorPos += step;
						error -= majorDelta;
					}
				}
			}
		}
	}
}
