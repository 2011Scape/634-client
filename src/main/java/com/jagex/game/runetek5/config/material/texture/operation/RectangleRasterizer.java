package com.jagex.game.runetek5.config.material.texture.operation;

import com.jagex.game.runetek5.config.material.texture.operation.TextureShapeRasterizer;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.utils.ArrayUtils;
import com.jagex.game.runetek5.config.material.texture.TextureOpRasterizer;
import com.jagex.game.runetek5.graphics.TextureOpRaster;
import com.jagex.game.runetek5.client.ClientState;

@OriginalClass("client!gfa")
public final class RectangleRasterizer extends TextureShapeRasterizer {

	@OriginalMember(owner = "client!gfa", name = "j", descriptor = "I")
	private final int left;

	@OriginalMember(owner = "client!gfa", name = "f", descriptor = "I")
	private final int top;

	@OriginalMember(owner = "client!gfa", name = "i", descriptor = "I")
	private final int bottom;

	@OriginalMember(owner = "client!gfa", name = "k", descriptor = "I")
	private final int right;

	@OriginalMember(owner = "client!gfa", name = "<init>", descriptor = "(IIIIIII)V")
	public RectangleRasterizer(@OriginalArg(0) int left, @OriginalArg(1) int top, @OriginalArg(2) int right, @OriginalArg(3) int bottom, @OriginalArg(4) int fillColor, @OriginalArg(5) int strokeColor, @OriginalArg(6) int lineWidth) {
		super(fillColor, strokeColor, lineWidth);
		this.left = left;
		this.top = top;
		this.bottom = bottom;
		this.right = right;
	}

	@OriginalMember(owner = "client!gfa", name = "a", descriptor = "(III)V")
	@Override
	public void drawFilledAndStroked(@OriginalArg(0) int width, @OriginalArg(1) int height) {
		@Pc(10) int minX = width * this.left >> 12;
		@Pc(17) int maxX = this.right * width >> 12;
		@Pc(24) int minY = height * this.top >> 12;
		@Pc(36) int maxY = height * this.bottom >> 12;
		TextureOpRaster.fillRectWithStroke(super.fillColor, minY, minX, super.strokeColor, super.strokeWidth, maxX, maxY);
	}

	@OriginalMember(owner = "client!gfa", name = "c", descriptor = "(III)V")
	@Override
	public void drawStroked(@OriginalArg(0) int height, @OriginalArg(1) int width) {
		@Pc(10) int minX = width * this.left >> 12;
		@Pc(21) int maxX = width * this.right >> 12;
		@Pc(28) int minY = height * this.top >> 12;
		@Pc(35) int maxY = height * this.bottom >> 12;
		RectangleRasterizer.drawStrokedRectangle(super.strokeColor, super.strokeWidth, minY, maxY, maxX, minX);
	}

	@OriginalMember(owner = "client!gfa", name = "b", descriptor = "(III)V")
	@Override
	public void drawFilled(@OriginalArg(1) int width, @OriginalArg(2) int height) {
		@Pc(10) int minX = width * this.left >> 12;
		@Pc(17) int maxX = this.right * width >> 12;
		@Pc(30) int minY = height * this.top >> 12;
		@Pc(37) int maxY = height * this.bottom >> 12;
		TextureOpRaster.fillRect(maxX, super.fillColor, maxY, minX, minY);
	}
	@OriginalMember(owner = "client!fu", name = "a", descriptor = "(BIIIII)V")
	public static void drawClippedStrokedRectangle(@OriginalArg(1) int right, @OriginalArg(2) int color, @OriginalArg(3) int bottom, @OriginalArg(4) int top, @OriginalArg(5) int left) {
		if (TextureOpRasterizer.clipMaxY < top || TextureOpRasterizer.clipMinY > bottom) {
			return;
		}
		@Pc(27) boolean leftVisible;
		if (left < TextureOpRasterizer.textureOpClipMinX) {
			left = TextureOpRasterizer.textureOpClipMinX;
			leftVisible = false;
		} else if (left <= TextureOpRasterizer.clipMaxX) {
			leftVisible = true;
		} else {
			leftVisible = false;
			left = TextureOpRasterizer.clipMaxX;
		}
		@Pc(46) boolean rightVisible;
		if (TextureOpRasterizer.textureOpClipMinX > right) {
			right = TextureOpRasterizer.textureOpClipMinX;
			rightVisible = false;
		} else if (right > TextureOpRasterizer.clipMaxX) {
			right = TextureOpRasterizer.clipMaxX;
			rightVisible = false;
		} else {
			rightVisible = true;
		}
		if (top < TextureOpRasterizer.clipMinY) {
			top = TextureOpRasterizer.clipMinY;
		} else {
			ArrayUtils.setArrayValuesTo(right, TextureOpRaster.textureOpPixelRows[top++], left, color);
		}
		if (bottom <= TextureOpRasterizer.clipMaxY) {
			ArrayUtils.setArrayValuesTo(right, TextureOpRaster.textureOpPixelRows[bottom--], left, color);
		} else {
			bottom = TextureOpRasterizer.clipMaxY;
		}
		@Pc(109) int y;
		if (leftVisible && rightVisible) {
			for (y = top; y <= bottom; y++) {
				@Pc(151) int[] row = TextureOpRaster.textureOpPixelRows[y];
				row[left] = row[right] = color;
			}
		} else if (leftVisible) {
			for (y = top; y <= bottom; y++) {
				TextureOpRaster.textureOpPixelRows[y][left] = color;
			}
		} else if (rightVisible) {
			for (y = top; y <= bottom; y++) {
				TextureOpRaster.textureOpPixelRows[y][right] = color;
			}
		}
	}
	@OriginalMember(owner = "client!mc", name = "a", descriptor = "(IIIIIII)V")
	public static void drawStrokedRectangle(@OriginalArg(0) int color, @OriginalArg(2) int thickness, @OriginalArg(3) int top, @OriginalArg(4) int bottom, @OriginalArg(5) int right, @OriginalArg(6) int left) {
		if (left >= TextureOpRasterizer.textureOpClipMinX && right <= TextureOpRasterizer.clipMaxX && TextureOpRasterizer.clipMinY <= top && bottom <= TextureOpRasterizer.clipMaxY) {
			if (thickness == 1) {
				RectangleRasterizer.drawStrokedRectangleToPixelBuffer(top, color, right, left, bottom);
			} else {
				RectangleRasterizer.drawUnclippedStrokedRectangle(thickness, top, left, right, bottom, color);
			}
		} else if (thickness == 1) {
			RectangleRasterizer.drawClippedStrokedRectangle(right, color, bottom, top, left);
		} else {
			RectangleRasterizer.drawClippedThickStrokedRectangle(left, bottom, thickness, right, color, top);
		}
	}

	@OriginalMember(owner = "client!jl", name = "a", descriptor = "(IIIIIIB)V")
	public static void drawClippedThickStrokedRectangle(@OriginalArg(0) int left, @OriginalArg(1) int bottom, @OriginalArg(2) int thickness, @OriginalArg(3) int right, @OriginalArg(4) int color, @OriginalArg(5) int top) {
		@Pc(11) int clippedTop = ArrayUtils.clamp(TextureOpRasterizer.clipMaxY, top, TextureOpRasterizer.clipMinY);
		@Pc(17) int clippedBottom = ArrayUtils.clamp(TextureOpRasterizer.clipMaxY, bottom, TextureOpRasterizer.clipMinY);
		@Pc(34) int clippedLeft = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, left, TextureOpRasterizer.textureOpClipMinX);
		@Pc(40) int clippedRight = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, right, TextureOpRasterizer.textureOpClipMinX);
		@Pc(48) int innerTop = ArrayUtils.clamp(TextureOpRasterizer.clipMaxY, top + thickness, TextureOpRasterizer.clipMinY);
		@Pc(57) int innerBottom = ArrayUtils.clamp(TextureOpRasterizer.clipMaxY, bottom - thickness, TextureOpRasterizer.clipMinY);
		for (@Pc(59) int i = clippedTop; i < innerTop; i++) {
			ArrayUtils.setArrayValuesTo(clippedRight, TextureOpRaster.textureOpPixelRows[i], clippedLeft, color);
		}
		for (@Pc(79) int j = clippedBottom; j > innerBottom; j--) {
			ArrayUtils.setArrayValuesTo(clippedRight, TextureOpRaster.textureOpPixelRows[j], clippedLeft, color);
		}
		@Pc(101) int innerLeft = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, thickness + left, TextureOpRasterizer.textureOpClipMinX);
		@Pc(110) int innerRight = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, right - thickness, TextureOpRasterizer.textureOpClipMinX);
		for (@Pc(112) int k = innerTop; k <= innerBottom; k++) {
			@Pc(118) int[] row = TextureOpRaster.textureOpPixelRows[k];
			ArrayUtils.setArrayValuesTo(innerLeft, row, clippedLeft, color);
			ArrayUtils.setArrayValuesTo(clippedRight, row, innerRight, color);
		}
	}

	@OriginalMember(owner = "client!qm", name = "a", descriptor = "(IIIIIIII)V")
	public static void drawClippedStrokedAndFilledRectangle(@OriginalArg(0) int bottom, @OriginalArg(1) int right, @OriginalArg(2) int top, @OriginalArg(3) int fillColor, @OriginalArg(4) int thickness, @OriginalArg(6) int left, @OriginalArg(7) int borderColor) {
		@Pc(11) int clippedTop = ArrayUtils.clamp(TextureOpRasterizer.clipMaxY, top, TextureOpRasterizer.clipMinY);
		@Pc(17) int clippedBottom = ArrayUtils.clamp(TextureOpRasterizer.clipMaxY, bottom, TextureOpRasterizer.clipMinY);
		@Pc(23) int clippedLeft = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, left, TextureOpRasterizer.textureOpClipMinX);
		@Pc(29) int clippedRight = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, right, TextureOpRasterizer.textureOpClipMinX);
		@Pc(37) int innerTop = ArrayUtils.clamp(TextureOpRasterizer.clipMaxY, top + thickness, TextureOpRasterizer.clipMinY);
		@Pc(46) int innerBottom = ArrayUtils.clamp(TextureOpRasterizer.clipMaxY, bottom - thickness, TextureOpRasterizer.clipMinY);
		for (@Pc(52) int i = clippedTop; i < innerTop; i++) {
			ArrayUtils.setArrayValuesTo(clippedRight, TextureOpRaster.textureOpPixelRows[i], clippedLeft, borderColor);
		}
		for (@Pc(72) int j = clippedBottom; j > innerBottom; j--) {
			ArrayUtils.setArrayValuesTo(clippedRight, TextureOpRaster.textureOpPixelRows[j], clippedLeft, borderColor);
		}
		@Pc(94) int innerLeft = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, left + thickness, TextureOpRasterizer.textureOpClipMinX);
		@Pc(103) int innerRight = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, right - thickness, TextureOpRasterizer.textureOpClipMinX);
		for (@Pc(105) int k = innerTop; k <= innerBottom; k++) {
			@Pc(111) int[] row = TextureOpRaster.textureOpPixelRows[k];
			ArrayUtils.setArrayValuesTo(innerLeft, row, clippedLeft, borderColor);
			ArrayUtils.setArrayValuesTo(innerRight, row, innerLeft, fillColor);
			ArrayUtils.setArrayValuesTo(clippedRight, row, innerRight, borderColor);
		}
	}
	@OriginalMember(owner = "client!lha", name = "a", descriptor = "(IIIIIII)V")
	public static void drawUnclippedStrokedRectangle(@OriginalArg(0) int borderWidth, @OriginalArg(1) int top, @OriginalArg(2) int left, @OriginalArg(3) int right, @OriginalArg(4) int bottom, @OriginalArg(6) int color) {
		@Pc(14) int topBorderEnd = top + borderWidth;
		@Pc(19) int bottomBorderStart = bottom - borderWidth;
		for (@Pc(21) int i = top; i < topBorderEnd; i++) {
			ArrayUtils.setArrayValuesTo(right, TextureOpRaster.textureOpPixelRows[i], left, color);
		}
		@Pc(40) int rightBorderStart = right - borderWidth;
		for (@Pc(42) int j = bottom; j > bottomBorderStart; j--) {
			ArrayUtils.setArrayValuesTo(right, TextureOpRaster.textureOpPixelRows[j], left, color);
		}
		@Pc(64) int leftBorderEnd = borderWidth + left;
		for (@Pc(66) int k = topBorderEnd; k <= bottomBorderStart; k++) {
			@Pc(72) int[] scanline = TextureOpRaster.textureOpPixelRows[k];
			ArrayUtils.setArrayValuesTo(leftBorderEnd, scanline, left, color);
			ArrayUtils.setArrayValuesTo(right, scanline, rightBorderStart, color);
		}
	}

	@OriginalMember(owner = "client!qea", name = "a", descriptor = "(IIIIII)V")
	public static void drawStrokedRectangleToPixelBuffer(@OriginalArg(0) int top, @OriginalArg(1) int color, @OriginalArg(3) int right, @OriginalArg(4) int left, @OriginalArg(5) int bottom) {
		@Pc(6) int innerTop = top + 1;
		ArrayUtils.setArrayValuesTo(right, TextureOpRaster.textureOpPixelRows[top], left, color);
		@Pc(19) int innerBottom = bottom - 1;
		ArrayUtils.setArrayValuesTo(right, TextureOpRaster.textureOpPixelRows[bottom], left, color);
		for (@Pc(33) int i = innerTop; i <= innerBottom; i++) {
			@Pc(39) int[] scanline = TextureOpRaster.textureOpPixelRows[i];
			scanline[left] = scanline[right] = color;
		}
	}
}
