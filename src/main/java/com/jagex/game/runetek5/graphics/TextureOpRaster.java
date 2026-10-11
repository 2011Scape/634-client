package com.jagex.game.runetek5.graphics;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.utils.ArrayUtils;
import com.jagex.game.runetek5.config.material.texture.TextureOpRasterizer;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.config.material.texture.operation.BezierCurveRasterizer;
import com.jagex.game.runetek5.config.material.texture.operation.RectangleRasterizer;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.config.material.texture.operation.EllipseRasterizer;

public final class TextureOpRaster {

	@OriginalMember(owner = "client!hd", name = "a", descriptor = "(IIIIB)V")
	public static void drawHorizontalLine(@OriginalArg(0) int x1, @OriginalArg(1) int colour, @OriginalArg(2) int y, @OriginalArg(3) int x2) {
		if (x1 < x2) {
			ArrayUtils.setArrayValuesTo(x2, TextureOpRaster.textureOpPixelRows[y], x1, colour);
		} else {
			ArrayUtils.setArrayValuesTo(x1, TextureOpRaster.textureOpPixelRows[y], x2, colour);
		}
	}

	@OriginalMember(owner = "client!hd", name = "a", descriptor = "(B[[I)V")
	public static void setPixelBuffer(@OriginalArg(1) int[][] pixelBuffer) {
		TextureOpRaster.textureOpPixelRows = pixelBuffer;
	}
	@OriginalMember(owner = "client!lj", name = "a", descriptor = "(ZIIII)V")
	public static void drawHorizontalLineClipped(@OriginalArg(1) int x1, @OriginalArg(2) int colour, @OriginalArg(3) int x2, @OriginalArg(4) int y) {
		if (TextureOpRasterizer.clipMinY <= y && TextureOpRasterizer.clipMaxY >= y) {
			@Pc(19) int clampedX2 = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, x2, TextureOpRasterizer.textureOpClipMinX);
			@Pc(25) int clampedX1 = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, x1, TextureOpRasterizer.textureOpClipMinX);
			TextureOpRaster.drawHorizontalLine(clampedX1, colour, y, clampedX2);
		}
	}
	@OriginalMember(owner = "client!un", name = "a", descriptor = "(IIIIII)V")
	public static void fillRect(@OriginalArg(1) int maxX, @OriginalArg(2) int color, @OriginalArg(3) int maxY, @OriginalArg(4) int minX, @OriginalArg(5) int minY) {
		if (minX >= TextureOpRasterizer.textureOpClipMinX && TextureOpRasterizer.clipMaxX >= maxX && minY >= TextureOpRasterizer.clipMinY && TextureOpRasterizer.clipMaxY >= maxY) {
			TextureOpRaster.fillRectUnclipped(maxY, maxX, minX, minY, color);
		} else {
			TextureOpRaster.fillRectClipped(minY, minX, maxY, maxX, color);
		}
	}

	@OriginalMember(owner = "client!tw", name = "a", descriptor = "(IIIII)V")
	public static void fillCircleUnclipped(@OriginalArg(0) int centerY, @OriginalArg(1) int centerX, @OriginalArg(3) int color, @OriginalArg(4) int radius) {
		@Pc(12) int x = 0;
		@Pc(14) int y = radius;
		@Pc(17) int error = -radius;
		@Pc(19) int delta = -1;
		ArrayUtils.setArrayValuesTo(centerX + radius, TextureOpRaster.textureOpPixelRows[centerY], centerX - radius, color);
		while (y > x) {
			delta += 2;
			error += delta;
			x++;
			if (error >= 0) {
				y--;
				error -= y << 1;
				@Pc(61) int[] rowBelow = TextureOpRaster.textureOpPixelRows[centerY + y];
				@Pc(68) int[] rowAbove = TextureOpRaster.textureOpPixelRows[centerY - y];
				@Pc(72) int spanRight = x + centerX;
				@Pc(77) int spanLeft = centerX - x;
				ArrayUtils.setArrayValuesTo(spanRight, rowBelow, spanLeft, color);
				ArrayUtils.setArrayValuesTo(spanRight, rowAbove, spanLeft, color);
			}
			@Pc(93) int right = y + centerX;
			@Pc(97) int left = centerX - y;
			@Pc(103) int[] lowerRow = TextureOpRaster.textureOpPixelRows[x + centerY];
			@Pc(110) int[] upperRow = TextureOpRaster.textureOpPixelRows[centerY - x];
			ArrayUtils.setArrayValuesTo(right, lowerRow, left, color);
			ArrayUtils.setArrayValuesTo(right, upperRow, left, color);
		}
	}

	@OriginalMember(owner = "client!wh", name = "a", descriptor = "(IIBIIII)V")
	public static void fillCircleWithStrokeUnclipped(@OriginalArg(0) int outerRadius, @OriginalArg(1) int centerX, @OriginalArg(3) int strokeWidth, @OriginalArg(4) int centerY, @OriginalArg(5) int fillColor, @OriginalArg(6) int strokeColor) {
		TextureOpRaster.ensureCircleBufferCapacity(outerRadius);
		@Pc(21) int x = 0;
		@Pc(26) int innerRadius = outerRadius - strokeWidth;
		if (innerRadius < 0) {
			innerRadius = 0;
		}
		@Pc(35) int outerY = outerRadius;
		@Pc(38) int outerError = -outerRadius;
		@Pc(40) int innerY = innerRadius;
		@Pc(43) int innerError = -innerRadius;
		@Pc(45) int outerDelta = -1;
		@Pc(47) int innerDelta = -1;
		@Pc(51) int[] scanline = TextureOpRaster.textureOpPixelRows[centerY];
		@Pc(56) int innerLeft = centerX - innerRadius;
		@Pc(60) int innerRight = innerRadius + centerX;
		ArrayUtils.setArrayValuesTo(innerLeft, scanline, centerX - outerRadius, strokeColor);
		ArrayUtils.setArrayValuesTo(innerRight, scanline, innerLeft, fillColor);
		ArrayUtils.setArrayValuesTo(outerRadius + centerX, scanline, innerRight, strokeColor);
		while (outerY > x) {
			innerDelta += 2;
			outerDelta += 2;
			innerError += innerDelta;
			outerError += outerDelta;
			if (innerError >= 0 && innerY >= 1) {
				TextureOpRaster.circleInnerRadiusBuffer[innerY] = x;
				innerY--;
				innerError -= innerY << 1;
			}
			x++;
			@Pc(132) int[] upperRow;
			@Pc(139) int[] lowerRow;
			@Pc(144) int local144;
			@Pc(149) int local149;
			@Pc(189) int local189;
			@Pc(194) int innerRight2;
			@Pc(199) int innerLeft2;
			if (outerError >= 0) {
				outerY--;
				outerError -= outerY << 1;
				if (innerRadius <= outerY) {
					upperRow = TextureOpRaster.textureOpPixelRows[outerY + centerY];
					lowerRow = TextureOpRaster.textureOpPixelRows[centerY - outerY];
					int spanRight = centerX + x;
					int spanLeft = centerX - x;
					ArrayUtils.setArrayValuesTo(spanRight, upperRow, spanLeft, strokeColor);
					ArrayUtils.setArrayValuesTo(spanRight, lowerRow, spanLeft, strokeColor);
				} else {
					upperRow = TextureOpRaster.textureOpPixelRows[centerY + outerY];
					lowerRow = TextureOpRaster.textureOpPixelRows[centerY - outerY];
					int innerExtent = TextureOpRaster.circleInnerRadiusBuffer[outerY];
					int spanRight = x + centerX;
					int spanLeft = centerX - x;
					innerRight2 = centerX + innerExtent;
					innerLeft2 = centerX - innerExtent;
					ArrayUtils.setArrayValuesTo(innerLeft2, upperRow, spanLeft, strokeColor);
					ArrayUtils.setArrayValuesTo(innerRight2, upperRow, innerLeft2, fillColor);
					ArrayUtils.setArrayValuesTo(spanRight, upperRow, innerRight2, strokeColor);
					ArrayUtils.setArrayValuesTo(innerLeft2, lowerRow, spanLeft, strokeColor);
					ArrayUtils.setArrayValuesTo(innerRight2, lowerRow, innerLeft2, fillColor);
					ArrayUtils.setArrayValuesTo(spanRight, lowerRow, innerRight2, strokeColor);
				}
			}
			upperRow = TextureOpRaster.textureOpPixelRows[centerY + x];
			lowerRow = TextureOpRaster.textureOpPixelRows[centerY - x];
			int outerRight = centerX + outerY;
			int outerLeft = centerX - outerY;
			if (innerRadius > x) {
				int innerExtent = innerY >= x ? innerY : TextureOpRaster.circleInnerRadiusBuffer[x];
				innerRight2 = innerExtent + centerX;
				innerLeft2 = centerX - innerExtent;
				ArrayUtils.setArrayValuesTo(innerLeft2, upperRow, outerLeft, strokeColor);
				ArrayUtils.setArrayValuesTo(innerRight2, upperRow, innerLeft2, fillColor);
				ArrayUtils.setArrayValuesTo(outerRight, upperRow, innerRight2, strokeColor);
				ArrayUtils.setArrayValuesTo(innerLeft2, lowerRow, outerLeft, strokeColor);
				ArrayUtils.setArrayValuesTo(innerRight2, lowerRow, innerLeft2, fillColor);
				ArrayUtils.setArrayValuesTo(outerRight, lowerRow, innerRight2, strokeColor);
			} else {
				ArrayUtils.setArrayValuesTo(outerRight, upperRow, outerLeft, strokeColor);
				ArrayUtils.setArrayValuesTo(outerRight, lowerRow, outerLeft, strokeColor);
			}
		}
	}

	@OriginalMember(owner = "client!tea", name = "a", descriptor = "(IIIIIIIIIZ)V")
	public static void drawBezierCurve(@OriginalArg(0) int p0y, @OriginalArg(1) int p1y, @OriginalArg(2) int color, @OriginalArg(3) int p3y, @OriginalArg(4) int p1x, @OriginalArg(5) int p0x, @OriginalArg(6) int p2x, @OriginalArg(7) int p2y, @OriginalArg(8) int p3x) {
		if (TextureOpRasterizer.textureOpClipMinX <= p0x && p0x <= TextureOpRasterizer.clipMaxX && p1x >= TextureOpRasterizer.textureOpClipMinX && TextureOpRasterizer.clipMaxX >= p1x && p2x >= TextureOpRasterizer.textureOpClipMinX && TextureOpRasterizer.clipMaxX >= p2x && p3x >= TextureOpRasterizer.textureOpClipMinX && p3x <= TextureOpRasterizer.clipMaxX && TextureOpRasterizer.clipMinY <= p0y && p0y <= TextureOpRasterizer.clipMaxY && TextureOpRasterizer.clipMinY <= p1y && p1y <= TextureOpRasterizer.clipMaxY && p2y >= TextureOpRasterizer.clipMinY && p2y <= TextureOpRasterizer.clipMaxY && p3y >= TextureOpRasterizer.clipMinY && TextureOpRasterizer.clipMaxY >= p3y) {
			BezierCurveRasterizer.drawUnclippedBezier(p1y, p2y, p1x, p3x, p3y, p2x, p0y, color, p0x);
		} else {
			BezierCurveRasterizer.drawClippedBezier(color, p2x, p0x, p1y, p1x, p2y, p3x, p0y, p3y);
		}
	}

	@OriginalMember(owner = "client!pia", name = "a", descriptor = "(IIIIBIII)V")
	public static void fillRectWithStroke(@OriginalArg(0) int fillColour, @OriginalArg(1) int minY, @OriginalArg(2) int minX, @OriginalArg(3) int strokeColour, @OriginalArg(5) int strokeWidth, @OriginalArg(6) int maxX, @OriginalArg(7) int maxY) {
		if (TextureOpRasterizer.textureOpClipMinX <= minX && maxX <= TextureOpRasterizer.clipMaxX && minY >= TextureOpRasterizer.clipMinY && TextureOpRasterizer.clipMaxY >= maxY) {
			TextureOpRaster.fillRectWithStrokeUnclipped(strokeColour, maxY, minY, maxX, fillColour, strokeWidth, minX);
		} else {
			RectangleRasterizer.drawClippedStrokedAndFilledRectangle(maxY, maxX, minY, fillColour, strokeWidth, minX, strokeColour);
		}
	}

	@OriginalMember(owner = "client!sba", name = "a", descriptor = "(IIIIBI)V")
	public static void fillRectClipped(@OriginalArg(0) int top, @OriginalArg(1) int left, @OriginalArg(2) int bottom, @OriginalArg(3) int right, @OriginalArg(5) int color) {
		@Pc(11) int clippedTop = ArrayUtils.clamp(TextureOpRasterizer.clipMaxY, top, TextureOpRasterizer.clipMinY);
		@Pc(17) int clippedBottom = ArrayUtils.clamp(TextureOpRasterizer.clipMaxY, bottom, TextureOpRasterizer.clipMinY);
		@Pc(23) int clippedLeft = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, left, TextureOpRasterizer.textureOpClipMinX);
		@Pc(29) int clippedRight = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, right, TextureOpRasterizer.textureOpClipMinX);
		for (@Pc(39) int i = clippedTop; i <= clippedBottom; i++) {
			ArrayUtils.setArrayValuesTo(clippedRight, TextureOpRaster.textureOpPixelRows[i], clippedLeft, color);
		}
	}
	@OriginalMember(owner = "client!fo", name = "a", descriptor = "(IIIIII)V")
	public static void fillEllipseClipped(@OriginalArg(0) int centerX, @OriginalArg(1) int radiusY, @OriginalArg(2) int centerY, @OriginalArg(4) int color, @OriginalArg(5) int radiusX) {
		@Pc(7) int x = 0;
		@Pc(9) int y = radiusY;
		@Pc(13) int rxSq = radiusX * radiusX;
		@Pc(17) int rySq = radiusY * radiusY;
		@Pc(21) int twoRySq = rySq << 1;
		@Pc(25) int twoRxSq = rxSq << 1;
		@Pc(29) int diameterY = radiusY << 1;
		@Pc(38) int decisionParam1 = twoRySq + rxSq * (1 - diameterY);
		@Pc(47) int decisionParam2 = rySq - (diameterY - 1) * twoRxSq;
		@Pc(51) int fourRxSq = rxSq << 2;
		@Pc(55) int fourRySq = rySq << 2;
		@Pc(63) int gradientX = twoRySq * 3;
		@Pc(71) int gradientY = twoRxSq * ((radiusY << 1) - 3);
		@Pc(77) int stepX = fourRySq;
		@Pc(99) int local99;
		@Pc(108) int local108;
		if (centerY >= TextureOpRasterizer.clipMinY && centerY <= TextureOpRasterizer.clipMaxY) {
			int rowRight = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, centerX + radiusX, TextureOpRasterizer.textureOpClipMinX);
			int rowLeft = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, centerX - radiusX, TextureOpRasterizer.textureOpClipMinX);
			ArrayUtils.setArrayValuesTo(rowRight, TextureOpRaster.textureOpPixelRows[centerY], rowLeft, color);
		}
		@Pc(122) int stepY = fourRxSq * (radiusY - 1);
		while (y > 0) {
			if (decisionParam1 < 0) {
				while (decisionParam1 < 0) {
					decisionParam2 += stepX;
					decisionParam1 += gradientX;
					x++;
					gradientX += fourRySq;
					stepX += fourRySq;
				}
			}
			if (decisionParam2 < 0) {
				decisionParam1 += gradientX;
				decisionParam2 += stepX;
				gradientX += fourRySq;
				stepX += fourRySq;
				x++;
			}
			decisionParam2 += -gradientY;
			decisionParam1 += -stepY;
			stepY -= fourRxSq;
			gradientY -= fourRxSq;
			y--;
			int topRow = centerY - y;
			int bottomRow = y + centerY;
			if (TextureOpRasterizer.clipMinY <= bottomRow && TextureOpRasterizer.clipMaxY >= topRow) {
				@Pc(225) int clippedRight = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, centerX + x, TextureOpRasterizer.textureOpClipMinX);
				@Pc(234) int clippedLeft = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, centerX - x, TextureOpRasterizer.textureOpClipMinX);
				if (topRow >= TextureOpRasterizer.clipMinY) {
					ArrayUtils.setArrayValuesTo(clippedRight, TextureOpRaster.textureOpPixelRows[topRow], clippedLeft, color);
				}
				if (bottomRow <= TextureOpRasterizer.clipMaxY) {
					ArrayUtils.setArrayValuesTo(clippedRight, TextureOpRaster.textureOpPixelRows[bottomRow], clippedLeft, color);
				}
			}
		}
	}

	@OriginalMember(owner = "client!ff", name = "a", descriptor = "(IIIIIII)V")
	public static void fillCircleWithStroke(@OriginalArg(0) int strokeColor, @OriginalArg(1) int radius, @OriginalArg(2) int thickness, @OriginalArg(4) int centerY, @OriginalArg(5) int fillColor, @OriginalArg(6) int centerX) {
		if (TextureOpRasterizer.textureOpClipMinX <= centerX - radius && radius + centerX <= TextureOpRasterizer.clipMaxX && TextureOpRasterizer.clipMinY <= centerY - radius && centerY + radius <= TextureOpRasterizer.clipMaxY) {
			TextureOpRaster.fillCircleWithStrokeUnclipped(radius, centerX, thickness, centerY, fillColor, strokeColor);
		} else {
			TextureOpRaster.drawClippedFilledAndStrokedCircle(strokeColor, centerY, fillColor, radius, thickness, centerX);
		}
	}

	@OriginalMember(owner = "client!hg", name = "u", descriptor = "[[I")
	public static int[][] textureOpPixelRows;

	@OriginalMember(owner = "client!jaa", name = "a", descriptor = "(IIIIIIII)V")
	public static void fillRectWithStrokeUnclipped(@OriginalArg(0) int borderColor, @OriginalArg(1) int bottom, @OriginalArg(2) int top, @OriginalArg(3) int right, @OriginalArg(4) int fillColor, @OriginalArg(5) int thickness, @OriginalArg(7) int left) {
		@Pc(22) int innerBottom = bottom - thickness;
		@Pc(26) int innerTop = thickness + top;
		for (@Pc(28) int i = top; i < innerTop; i++) {
			ArrayUtils.setArrayValuesTo(right, TextureOpRaster.textureOpPixelRows[i], left, borderColor);
		}
		@Pc(47) int innerRight = right - thickness;
		@Pc(51) int innerLeft = thickness + left;
		for (@Pc(53) int j = bottom; j > innerBottom; j--) {
			ArrayUtils.setArrayValuesTo(right, TextureOpRaster.textureOpPixelRows[j], left, borderColor);
		}
		for (@Pc(71) int k = innerTop; k <= innerBottom; k++) {
			@Pc(77) int[] row = TextureOpRaster.textureOpPixelRows[k];
			ArrayUtils.setArrayValuesTo(innerLeft, row, left, borderColor);
			ArrayUtils.setArrayValuesTo(innerRight, row, innerLeft, fillColor);
			ArrayUtils.setArrayValuesTo(right, row, innerRight, borderColor);
		}
	}

	@OriginalMember(owner = "client!jfa", name = "a", descriptor = "(IIIIII)V")
	public static void fillEllipseUnclipped(@OriginalArg(0) int color, @OriginalArg(1) int centerX, @OriginalArg(2) int centerY, @OriginalArg(3) int radiusY, @OriginalArg(4) int radiusX) {
		@Pc(7) int x = 0;
		@Pc(9) int y = radiusY;
		@Pc(13) int rxSq = radiusX * radiusX;
		@Pc(17) int rySq = radiusY * radiusY;
		@Pc(21) int twoRySq = rySq << 1;
		@Pc(25) int twoRxSq = rxSq << 1;
		@Pc(37) int diameterY = radiusY << 1;
		@Pc(46) int decisionParam1 = twoRySq + rxSq * (1 - diameterY);
		@Pc(55) int decisionParam2 = rySq - twoRxSq * (diameterY - 1);
		@Pc(59) int fourRxSq = rxSq << 2;
		@Pc(63) int fourRySq = rySq << 2;
		@Pc(71) int gradientX = twoRySq * 3;
		@Pc(79) int gradientY = twoRxSq * ((radiusY << 1) - 3);
		@Pc(85) int stepX = fourRySq;
		ArrayUtils.setArrayValuesTo(centerX + radiusX, TextureOpRaster.textureOpPixelRows[centerY], centerX - radiusX, color);
		@Pc(104) int stepY = fourRxSq * (radiusY - 1);
		while (y > 0) {
			if (decisionParam1 < 0) {
				while (decisionParam1 < 0) {
					decisionParam2 += stepX;
					decisionParam1 += gradientX;
					x++;
					stepX += fourRySq;
					gradientX += fourRySq;
				}
			}
			if (decisionParam2 < 0) {
				decisionParam2 += stepX;
				decisionParam1 += gradientX;
				x++;
				stepX += fourRySq;
				gradientX += fourRySq;
			}
			decisionParam2 += -gradientY;
			decisionParam1 += -stepY;
			stepY -= fourRxSq;
			gradientY -= fourRxSq;
			y--;
			@Pc(178) int rowAbove = centerY - y;
			@Pc(183) int rowBelow = centerY + y;
			@Pc(188) int right = centerX + x;
			@Pc(193) int left = centerX - x;
			ArrayUtils.setArrayValuesTo(right, TextureOpRaster.textureOpPixelRows[rowAbove], left, color);
			ArrayUtils.setArrayValuesTo(right, TextureOpRaster.textureOpPixelRows[rowBelow], left, color);
		}
	}

	@OriginalMember(owner = "client!nha", name = "a", descriptor = "(IIIIII)V")
	public static void fillRectUnclipped(@OriginalArg(0) int maxY, @OriginalArg(2) int maxX, @OriginalArg(3) int minX, @OriginalArg(4) int minY, @OriginalArg(5) int color) {
		for (@Pc(3) int i = minY; i <= maxY; i++) {
			ArrayUtils.setArrayValuesTo(maxX, TextureOpRaster.textureOpPixelRows[i], minX, color);
		}
	}

	@OriginalMember(owner = "client!nd", name = "a", descriptor = "(BI)V")
	public static void ensureCircleBufferCapacity(@OriginalArg(1) int radius) {
		if (TextureOpRaster.circleInnerRadiusBuffer == null || TextureOpRaster.circleInnerRadiusBuffer.length < radius) {
			TextureOpRaster.circleInnerRadiusBuffer = new int[radius];
		}
	}

	@OriginalMember(owner = "client!ua", name = "Ob", descriptor = "[I")
	public static int[] circleInnerRadiusBuffer;

	@OriginalMember(owner = "client!rfa", name = "a", descriptor = "(IIIIIII)V")
	public static void drawClippedFilledAndStrokedCircle(@OriginalArg(0) int colour, @OriginalArg(1) int y, @OriginalArg(2) int fillColour, @OriginalArg(3) int radius, @OriginalArg(5) int thickness, @OriginalArg(6) int x) {
		TextureOpRaster.ensureCircleBufferCapacity(radius);
		@Pc(10) int offset = 0;
		@Pc(15) int innerRadius = radius - thickness;
		if (innerRadius < 0) {
			innerRadius = 0;
		}
		@Pc(24) int outerExtent = radius;
		@Pc(27) int outerError = -radius;
		@Pc(29) int innerExtent = innerRadius;
		@Pc(32) int innerError = -innerRadius;
		@Pc(34) int outerStep = -1;
		@Pc(53) int local53;
		@Pc(64) int local64;
		@Pc(72) int local72;
		@Pc(81) int local81;
		if (y >= TextureOpRasterizer.clipMinY && y <= TextureOpRasterizer.clipMaxY) {
			@Pc(44) int[] row = TextureOpRaster.textureOpPixelRows[y];
			int outerLeft = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, x - radius, TextureOpRasterizer.textureOpClipMinX);
			int outerRight = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, x + radius, TextureOpRasterizer.textureOpClipMinX);
			int fillLeft = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, x - innerRadius, TextureOpRasterizer.textureOpClipMinX);
			int fillRight = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, x + innerRadius, TextureOpRasterizer.textureOpClipMinX);
			ArrayUtils.setArrayValuesTo(fillLeft, row, outerLeft, colour);
			ArrayUtils.setArrayValuesTo(fillRight, row, fillLeft, fillColour);
			ArrayUtils.setArrayValuesTo(outerRight, row, fillRight, colour);
		}
		@Pc(101) int innerStep = -1;
		while (outerExtent > offset) {
			outerStep += 2;
			innerStep += 2;
			innerError += innerStep;
			outerError += outerStep;
			if (innerError >= 0 && innerExtent >= 1) {
				innerExtent--;
				TextureOpRaster.circleInnerRadiusBuffer[innerExtent] = offset;
				innerError -= innerExtent << 1;
			}
			offset++;
			@Pc(246) int innerRight;
			@Pc(254) int innerLeft;
			@Pc(265) int[] row;
			@Pc(146) int topRow;
			if (outerError >= 0) {
				outerExtent--;
				outerError -= outerExtent << 1;
				topRow = y - outerExtent;
				int bottomRow = y + outerExtent;
				if (bottomRow >= TextureOpRasterizer.clipMinY && TextureOpRasterizer.clipMaxY >= topRow) {
					if (innerRadius <= outerExtent) {
						int spanRight = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, offset + x, TextureOpRasterizer.textureOpClipMinX);
						int spanLeft = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, x - offset, TextureOpRasterizer.textureOpClipMinX);
						if (TextureOpRasterizer.clipMaxY >= bottomRow) {
							ArrayUtils.setArrayValuesTo(spanRight, TextureOpRaster.textureOpPixelRows[bottomRow], spanLeft, colour);
						}
						if (topRow >= TextureOpRasterizer.clipMinY) {
							ArrayUtils.setArrayValuesTo(spanRight, TextureOpRaster.textureOpPixelRows[topRow], spanLeft, colour);
						}
					} else {
						int innerOffset = TextureOpRaster.circleInnerRadiusBuffer[outerExtent];
						int spanRight = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, offset + x, TextureOpRasterizer.textureOpClipMinX);
						int spanLeft = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, x - offset, TextureOpRasterizer.textureOpClipMinX);
						innerRight = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, innerOffset + x, TextureOpRasterizer.textureOpClipMinX);
						innerLeft = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, x - innerOffset, TextureOpRasterizer.textureOpClipMinX);
						if (bottomRow <= TextureOpRasterizer.clipMaxY) {
							row = TextureOpRaster.textureOpPixelRows[bottomRow];
							ArrayUtils.setArrayValuesTo(innerLeft, row, spanLeft, colour);
							ArrayUtils.setArrayValuesTo(innerRight, row, innerLeft, fillColour);
							ArrayUtils.setArrayValuesTo(spanRight, row, innerRight, colour);
						}
						if (TextureOpRasterizer.clipMinY <= topRow) {
							row = TextureOpRaster.textureOpPixelRows[topRow];
							ArrayUtils.setArrayValuesTo(innerLeft, row, spanLeft, colour);
							ArrayUtils.setArrayValuesTo(innerRight, row, innerLeft, fillColour);
							ArrayUtils.setArrayValuesTo(spanRight, row, innerRight, colour);
						}
					}
				}
			}
			topRow = y - offset;
			int lowerRow = y + offset;
			if (lowerRow >= TextureOpRasterizer.clipMinY && topRow <= TextureOpRasterizer.clipMaxY) {
				int rowRight = outerExtent + x;
				int rowLeft = x - outerExtent;
				if (TextureOpRasterizer.textureOpClipMinX <= rowRight && rowLeft <= TextureOpRasterizer.clipMaxX) {
					rowRight = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, rowRight, TextureOpRasterizer.textureOpClipMinX);
					rowLeft = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, rowLeft, TextureOpRasterizer.textureOpClipMinX);
					if (innerRadius <= offset) {
						if (lowerRow <= TextureOpRasterizer.clipMaxY) {
							ArrayUtils.setArrayValuesTo(rowRight, TextureOpRaster.textureOpPixelRows[lowerRow], rowLeft, colour);
						}
						if (TextureOpRasterizer.clipMinY <= topRow) {
							ArrayUtils.setArrayValuesTo(rowRight, TextureOpRaster.textureOpPixelRows[topRow], rowLeft, colour);
						}
					} else {
						int innerOffset = innerExtent < offset ? TextureOpRaster.circleInnerRadiusBuffer[offset] : innerExtent;
						innerRight = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, innerOffset + x, TextureOpRasterizer.textureOpClipMinX);
						innerLeft = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, x - innerOffset, TextureOpRasterizer.textureOpClipMinX);
						if (lowerRow <= TextureOpRasterizer.clipMaxY) {
							row = TextureOpRaster.textureOpPixelRows[lowerRow];
							ArrayUtils.setArrayValuesTo(innerLeft, row, rowLeft, colour);
							ArrayUtils.setArrayValuesTo(innerRight, row, innerLeft, fillColour);
							ArrayUtils.setArrayValuesTo(rowRight, row, innerRight, colour);
						}
						if (topRow >= TextureOpRasterizer.clipMinY) {
							row = TextureOpRaster.textureOpPixelRows[topRow];
							ArrayUtils.setArrayValuesTo(innerLeft, row, rowLeft, colour);
							ArrayUtils.setArrayValuesTo(innerRight, row, innerLeft, fillColour);
							ArrayUtils.setArrayValuesTo(rowRight, row, innerRight, colour);
						}
					}
				}
			}
		}
	}

	@OriginalMember(owner = "client!nl", name = "a", descriptor = "(IIIIB)V")
	public static void drawFilledCircle(@OriginalArg(0) int y, @OriginalArg(1) int colour, @OriginalArg(2) int x, @OriginalArg(3) int radius) {
		if (TextureOpRasterizer.textureOpClipMinX <= x - radius && TextureOpRasterizer.clipMaxX >= radius + x && TextureOpRasterizer.clipMinY <= y - radius && TextureOpRasterizer.clipMaxY >= y + radius) {
			TextureOpRaster.fillCircleUnclipped(y, x, colour, radius);
		} else {
			EllipseRasterizer.fillClippedCircle(x, colour, y, radius);
		}
	}
}
