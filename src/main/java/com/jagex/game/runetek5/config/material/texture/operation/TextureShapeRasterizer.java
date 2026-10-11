package com.jagex.game.runetek5.config.material.texture.operation;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.utils.ArrayUtils;
import com.jagex.game.runetek5.config.material.texture.TextureOpRasterizer;
import com.jagex.game.runetek5.graphics.TextureOpRaster;
import com.jagex.game.runetek5.client.ClientState;

@OriginalClass("client!iu")
public abstract class TextureShapeRasterizer {

	@OriginalMember(owner = "client!iu", name = "b", descriptor = "I")
	public final int strokeWidth;

	@OriginalMember(owner = "client!iu", name = "d", descriptor = "I")
	public final int fillColor;

	@OriginalMember(owner = "client!iu", name = "e", descriptor = "I")
	public final int strokeColor;

	@OriginalMember(owner = "client!iu", name = "<init>", descriptor = "(III)V")
	public TextureShapeRasterizer(@OriginalArg(0) int lineColour, @OriginalArg(1) int fillColour, @OriginalArg(2) int lineWidth) {
		this.strokeWidth = lineWidth;
		this.fillColor = lineColour;
		this.strokeColor = fillColour;
	}

	@OriginalMember(owner = "client!iu", name = "a", descriptor = "(III)V")
	public abstract void drawFilledAndStroked(@OriginalArg(0) int width, @OriginalArg(1) int height);

	@OriginalMember(owner = "client!iu", name = "b", descriptor = "(III)V")
	public abstract void drawFilled(@OriginalArg(1) int width, @OriginalArg(2) int height);

	@OriginalMember(owner = "client!iu", name = "c", descriptor = "(III)V")
	public abstract void drawStroked(@OriginalArg(0) int height, @OriginalArg(1) int width);
	@OriginalMember(owner = "client!dj", name = "a", descriptor = "(IIIII)V")
	public static void fillVerticalLine(@OriginalArg(0) int y1, @OriginalArg(1) int y2, @OriginalArg(3) int x, @OriginalArg(4) int colour) {
		@Pc(15) int y;
		if (y1 < y2) {
			for (y = y1; y < y2; y++) {
				TextureOpRaster.textureOpPixelRows[y][x] = colour;
			}
		} else {
			for (y = y2; y < y1; y++) {
				TextureOpRaster.textureOpPixelRows[y][x] = colour;
			}
		}
	}

	@OriginalMember(owner = "client!dt", name = "a", descriptor = "(IIBII)V")
	public static void drawVerticalLine(@OriginalArg(0) int y1, @OriginalArg(1) int x, @OriginalArg(3) int colour, @OriginalArg(4) int y2) {
		if (x >= TextureOpRasterizer.textureOpClipMinX && TextureOpRasterizer.clipMaxX >= x) {
			@Pc(24) int clampedY1 = ArrayUtils.clamp(TextureOpRasterizer.clipMaxY, y1, TextureOpRasterizer.clipMinY);
			@Pc(30) int clampedY2 = ArrayUtils.clamp(TextureOpRasterizer.clipMaxY, y2, TextureOpRasterizer.clipMinY);
			TextureShapeRasterizer.fillVerticalLine(clampedY2, clampedY1, x, colour);
		}
	}

	@OriginalMember(owner = "client!eaa", name = "a", descriptor = "(IBIIII)V")
	public static void drawLine(@OriginalArg(0) int x0, @OriginalArg(2) int color, @OriginalArg(3) int y0, @OriginalArg(4) int y1, @OriginalArg(5) int x1) {
		@Pc(9) int dx = x1 - x0;
		@Pc(14) int dy = y1 - y0;
		if (dx == 0) {
			if (dy != 0) {
				TextureShapeRasterizer.drawVerticalLine(y0, x0, color, y1);
			}
		} else if (dy == 0) {
			TextureOpRaster.drawHorizontalLineClipped(x1, color, x0, y0);
		} else {
			@Pc(46) int slope = (dy << 12) / dx;
			@Pc(55) int intercept = y0 - (slope * x0 >> 12);
			@Pc(68) int clippedX0;
			@Pc(66) int clippedY0;
			if (x0 < TextureOpRasterizer.textureOpClipMinX) {
				clippedY0 = (slope * TextureOpRasterizer.textureOpClipMinX >> 12) + intercept;
				clippedX0 = TextureOpRasterizer.textureOpClipMinX;
			} else if (TextureOpRasterizer.clipMaxX < x0) {
				clippedY0 = intercept + (TextureOpRasterizer.clipMaxX * slope >> 12);
				clippedX0 = TextureOpRasterizer.clipMaxX;
			} else {
				clippedX0 = x0;
				clippedY0 = y0;
			}
			@Pc(111) int clippedX1;
			@Pc(113) int clippedY1;
			if (TextureOpRasterizer.textureOpClipMinX > x1) {
				clippedX1 = TextureOpRasterizer.textureOpClipMinX;
				clippedY1 = (slope * TextureOpRasterizer.textureOpClipMinX >> 12) + intercept;
			} else if (x1 <= TextureOpRasterizer.clipMaxX) {
				clippedX1 = x1;
				clippedY1 = y1;
			} else {
				clippedX1 = TextureOpRasterizer.clipMaxX;
				clippedY1 = intercept + (TextureOpRasterizer.clipMaxX * slope >> 12);
			}
			if (TextureOpRasterizer.clipMinY > clippedY1) {
				clippedX1 = (TextureOpRasterizer.clipMinY - intercept << 12) / slope;
				clippedY1 = TextureOpRasterizer.clipMinY;
			} else if (clippedY1 > TextureOpRasterizer.clipMaxY) {
				clippedX1 = (TextureOpRasterizer.clipMaxY - intercept << 12) / slope;
				clippedY1 = TextureOpRasterizer.clipMaxY;
			}
			if (TextureOpRasterizer.clipMinY > clippedY0) {
				clippedX0 = (TextureOpRasterizer.clipMinY - intercept << 12) / slope;
				clippedY0 = TextureOpRasterizer.clipMinY;
			} else if (clippedY0 > TextureOpRasterizer.clipMaxY) {
				clippedX0 = (TextureOpRasterizer.clipMaxY - intercept << 12) / slope;
				clippedY0 = TextureOpRasterizer.clipMaxY;
			}
			BezierCurveRasterizer.drawBresenhamLine(clippedX0, clippedY0, clippedY1, clippedX1, color);
		}
	}
}
