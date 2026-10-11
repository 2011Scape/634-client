package com.jagex.game.runetek5.config.material.texture.operation;

import com.jagex.game.runetek5.config.material.texture.operation.TextureShapeRasterizer;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.graphics.texture.TextureOpShapeRaster;
import com.jagex.game.runetek5.config.material.texture.TextureOpRasterizer;
import com.jagex.game.runetek5.utils.ArrayUtils;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.graphics.TextureOpRaster;

@OriginalClass("client!gb")
public final class EllipseRasterizer extends TextureShapeRasterizer {

	@OriginalMember(owner = "client!gb", name = "k", descriptor = "I")
	private final int centerX;

	@OriginalMember(owner = "client!gb", name = "s", descriptor = "I")
	private final int radiusY;

	@OriginalMember(owner = "client!gb", name = "j", descriptor = "I")
	private final int radiusX;

	@OriginalMember(owner = "client!gb", name = "i", descriptor = "I")
	private final int centerY;

	@OriginalMember(owner = "client!gb", name = "<init>", descriptor = "(IIIIIII)V")
	public EllipseRasterizer(@OriginalArg(0) int centerX, @OriginalArg(1) int centerY, @OriginalArg(2) int radiusX, @OriginalArg(3) int radiusY, @OriginalArg(4) int fillColor, @OriginalArg(5) int strokeColor, @OriginalArg(6) int lineWidth) {
		super(fillColor, strokeColor, lineWidth);
		this.centerX = centerX;
		this.radiusY = radiusY;
		this.radiusX = radiusX;
		this.centerY = centerY;
	}

	@OriginalMember(owner = "client!gb", name = "a", descriptor = "(III)V")
	@Override
	public void drawFilledAndStroked(@OriginalArg(0) int width, @OriginalArg(1) int height) {
		@Pc(15) int scaledCenterX = this.centerX * width >> 12;
		@Pc(22) int scaledRadiusX = width * this.radiusX >> 12;
		@Pc(29) int scaledCenterY = this.centerY * height >> 12;
		@Pc(36) int scaledRadiusY = height * this.radiusY >> 12;
		EllipseRasterizer.drawEllipse(scaledRadiusY, scaledCenterX, super.strokeColor, super.fillColor, super.strokeWidth, scaledCenterY, scaledRadiusX);
	}

	@OriginalMember(owner = "client!gb", name = "c", descriptor = "(III)V")
	@Override
	public void drawStroked(@OriginalArg(0) int height, @OriginalArg(1) int width) {
	}

	@OriginalMember(owner = "client!gb", name = "b", descriptor = "(III)V")
	@Override
	public void drawFilled(@OriginalArg(1) int width, @OriginalArg(2) int height) {
		@Pc(16) int scaledCenterX = width * this.centerX >> 12;
		@Pc(23) int scaledRadiusX = this.radiusX * width >> 12;
		@Pc(30) int scaledCenterY = this.centerY * height >> 12;
		@Pc(37) int scaledRadiusY = height * this.radiusY >> 12;
		EllipseRasterizer.drawFilledEllipse(scaledCenterY, scaledRadiusY, super.fillColor, scaledRadiusX, scaledCenterX);
	}
	@OriginalMember(owner = "client!uk", name = "a", descriptor = "(Lclient!cea;I)Lclient!gb;")
	public static EllipseRasterizer decode(@OriginalArg(0) Packet packet) {
		return new EllipseRasterizer(packet.readShort(), packet.readShort(), packet.readShort(), packet.readShort(), packet.readUMedium(), packet.readUMedium(), packet.readUByte());
	}
	@OriginalMember(owner = "client!vc", name = "a", descriptor = "(IIIIIIII)V")
	public static void drawEllipse(@OriginalArg(1) int radiusY, @OriginalArg(2) int centerX, @OriginalArg(3) int strokeColor, @OriginalArg(4) int fillColor, @OriginalArg(5) int thickness, @OriginalArg(6) int centerY, @OriginalArg(7) int radiusX) {
		if (radiusY == radiusX) {
			TextureOpRaster.fillCircleWithStroke(strokeColor, radiusX, thickness, centerY, fillColor, centerX);
		} else if (TextureOpRasterizer.textureOpClipMinX <= centerX - radiusX && radiusX + centerX <= TextureOpRasterizer.clipMaxX && centerY - radiusY >= TextureOpRasterizer.clipMinY && radiusY + centerY <= TextureOpRasterizer.clipMaxY) {
			EllipseRasterizer.drawUnclippedEllipse(radiusY, fillColor, radiusX, strokeColor, centerX, centerY, thickness);
		} else {
			TextureOpShapeRaster.drawClippedEllipse(radiusX, centerX, centerY, radiusY, fillColor, strokeColor, thickness);
		}
	}

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "(IIBIII)V")
	public static void drawFilledEllipse(@OriginalArg(0) int centerY, @OriginalArg(1) int radiusY, @OriginalArg(3) int color, @OriginalArg(4) int radiusX, @OriginalArg(5) int centerX) {
		if (radiusY == radiusX) {
			TextureOpRaster.drawFilledCircle(centerY, color, centerX, radiusX);
		} else if (TextureOpRasterizer.textureOpClipMinX <= centerX - radiusX && centerX + radiusX <= TextureOpRasterizer.clipMaxX && TextureOpRasterizer.clipMinY <= centerY - radiusY && TextureOpRasterizer.clipMaxY >= centerY + radiusY) {
			TextureOpRaster.fillEllipseUnclipped(color, centerX, centerY, radiusY, radiusX);
		} else {
			TextureOpRaster.fillEllipseClipped(centerX, radiusY, centerY, color, radiusX);
		}
	}
	@OriginalMember(owner = "client!pi", name = "a", descriptor = "(IIIIIBII)V")
	public static void drawUnclippedEllipse(@OriginalArg(0) int outerRy, @OriginalArg(1) int fillColor, @OriginalArg(2) int outerRx, @OriginalArg(3) int strokeColor, @OriginalArg(4) int cx, @OriginalArg(6) int cy, @OriginalArg(7) int thickness) {
		@Pc(7) int outerX = 0;
		@Pc(9) int outerY = outerRy;
		@Pc(11) int innerX = 0;
		@Pc(16) int innerRx = outerRx - thickness;
		@Pc(21) int innerRy = outerRy - thickness;
		@Pc(25) int outerRxSq = outerRx * outerRx;
		@Pc(29) int outerRySq = outerRy * outerRy;
		@Pc(33) int innerRxSq = innerRx * innerRx;
		@Pc(37) int innerRySq = innerRy * innerRy;
		@Pc(41) int outerRySq2 = outerRySq << 1;
		@Pc(45) int outerRxSq2 = outerRxSq << 1;
		@Pc(49) int innerRySq2 = innerRySq << 1;
		@Pc(53) int innerRxSq2 = innerRxSq << 1;
		@Pc(57) int outerDiam = outerRy << 1;
		@Pc(61) int innerDiam = innerRy << 1;
		@Pc(70) int outerDecA = outerRySq2 + (1 - outerDiam) * outerRxSq;
		@Pc(79) int outerDecB = outerRySq - outerRxSq2 * (outerDiam - 1);
		@Pc(88) int innerDecA = innerRySq2 + (1 - innerDiam) * innerRxSq;
		@Pc(97) int innerDecB = innerRySq - (innerDiam - 1) * innerRxSq2;
		@Pc(101) int outerRxSq4 = outerRxSq << 2;
		@Pc(105) int outerRySq4 = outerRySq << 2;
		@Pc(109) int innerRxSq4 = innerRxSq << 2;
		@Pc(113) int innerRySq4 = innerRySq << 2;
		@Pc(117) int outerGradA = outerRySq2 * 3;
		@Pc(123) int outerGradB = (outerDiam - 3) * outerRxSq2;
		@Pc(127) int innerGradA = innerRySq2 * 3;
		@Pc(133) int innerGradB = innerRxSq2 * (innerDiam - 3);
		@Pc(135) int outerStepA = outerRySq4;
		@Pc(141) int outerStepB = outerRxSq4 * (outerRy - 1);
		@Pc(151) int innerStepA = innerRySq4;
		@Pc(157) int innerStepB = (innerRy - 1) * innerRxSq4;
		@Pc(161) int[] centerRow = TextureOpRaster.textureOpPixelRows[cy];
		ArrayUtils.setArrayValuesTo(cx - innerRx, centerRow, cx - outerRx, strokeColor);
		ArrayUtils.setArrayValuesTo(cx + innerRx, centerRow, cx - innerRx, fillColor);
		ArrayUtils.setArrayValuesTo(outerRx + cx, centerRow, cx + innerRx, strokeColor);
		while (outerY > 0) {
			@Pc(202) boolean withinInner = innerRy >= outerY;
			if (outerDecA < 0) {
				while (outerDecA < 0) {
					outerDecB += outerStepA;
					outerDecA += outerGradA;
					outerStepA += outerRySq4;
					outerX++;
					outerGradA += outerRySq4;
				}
			}
			if (withinInner) {
				if (innerDecA < 0) {
					while (innerDecA < 0) {
						innerDecB += innerStepA;
						innerDecA += innerGradA;
						innerX++;
						innerStepA += innerRySq4;
						innerGradA += innerRySq4;
					}
				}
				if (innerDecB < 0) {
					innerDecB += innerStepA;
					innerDecA += innerGradA;
					innerGradA += innerRySq4;
					innerX++;
					innerStepA += innerRySq4;
				}
				innerDecB += -innerGradB;
				innerDecA += -innerStepB;
				innerGradB -= innerRxSq4;
				innerStepB -= innerRxSq4;
			}
			if (outerDecB < 0) {
				outerDecB += outerStepA;
				outerDecA += outerGradA;
				outerStepA += outerRySq4;
				outerX++;
				outerGradA += outerRySq4;
			}
			outerDecB += -outerGradB;
			outerDecA += -outerStepB;
			outerGradB -= outerRxSq4;
			outerY--;
			outerStepB -= outerRxSq4;
			@Pc(342) int topRow = cy - outerY;
			@Pc(347) int bottomRow = cy + outerY;
			@Pc(351) int rightEdge = outerX + cx;
			@Pc(356) int leftEdge = cx - outerX;
			if (withinInner) {
				@Pc(362) int innerRightEdge = cx + innerX;
				@Pc(367) int innerLeftEdge = cx - innerX;
				ArrayUtils.setArrayValuesTo(innerLeftEdge, TextureOpRaster.textureOpPixelRows[topRow], leftEdge, strokeColor);
				ArrayUtils.setArrayValuesTo(innerRightEdge, TextureOpRaster.textureOpPixelRows[topRow], innerLeftEdge, fillColor);
				ArrayUtils.setArrayValuesTo(rightEdge, TextureOpRaster.textureOpPixelRows[topRow], innerRightEdge, strokeColor);
				ArrayUtils.setArrayValuesTo(innerLeftEdge, TextureOpRaster.textureOpPixelRows[bottomRow], leftEdge, strokeColor);
				ArrayUtils.setArrayValuesTo(innerRightEdge, TextureOpRaster.textureOpPixelRows[bottomRow], innerLeftEdge, fillColor);
				ArrayUtils.setArrayValuesTo(rightEdge, TextureOpRaster.textureOpPixelRows[bottomRow], innerRightEdge, strokeColor);
			} else {
				ArrayUtils.setArrayValuesTo(rightEdge, TextureOpRaster.textureOpPixelRows[topRow], leftEdge, strokeColor);
				ArrayUtils.setArrayValuesTo(rightEdge, TextureOpRaster.textureOpPixelRows[bottomRow], leftEdge, strokeColor);
			}
		}
	}
	@OriginalMember(owner = "client!go", name = "a", descriptor = "(IIIII)V")
	public static void fillClippedCircle(@OriginalArg(1) int centerX, @OriginalArg(2) int color, @OriginalArg(3) int centerY, @OriginalArg(4) int radius) {
		@Pc(7) int dy = 0;
		@Pc(14) int dx = radius;
		@Pc(17) int error = -radius;
		@Pc(19) int errorStep = -1;
		@Pc(27) int right = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, radius + centerX, TextureOpRasterizer.textureOpClipMinX);
		@Pc(36) int left = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, centerX - radius, TextureOpRasterizer.textureOpClipMinX);
		ArrayUtils.setArrayValuesTo(right, TextureOpRaster.textureOpPixelRows[centerY], left, color);
		while (dx > dy) {
			errorStep += 2;
			error += errorStep;
			@Pc(68) int topRow;
			@Pc(72) int bottomRow;
			@Pc(94) int spanRight;
			@Pc(103) int spanLeft;
			if (error > 0) {
				dx--;
				error -= dx << 1;
				topRow = centerY - dx;
				bottomRow = dx + centerY;
				if (TextureOpRasterizer.clipMinY <= bottomRow && topRow <= TextureOpRasterizer.clipMaxY) {
					spanRight = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, dy + centerX, TextureOpRasterizer.textureOpClipMinX);
					spanLeft = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, centerX - dy, TextureOpRasterizer.textureOpClipMinX);
					if (bottomRow <= TextureOpRasterizer.clipMaxY) {
						ArrayUtils.setArrayValuesTo(spanRight, TextureOpRaster.textureOpPixelRows[bottomRow], spanLeft, color);
					}
					if (topRow >= TextureOpRasterizer.clipMinY) {
						ArrayUtils.setArrayValuesTo(spanRight, TextureOpRaster.textureOpPixelRows[topRow], spanLeft, color);
					}
				}
			}
			dy++;
			topRow = centerY - dy;
			bottomRow = centerY + dy;
			if (bottomRow >= TextureOpRasterizer.clipMinY && TextureOpRasterizer.clipMaxY >= topRow) {
				spanRight = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, centerX + dx, TextureOpRasterizer.textureOpClipMinX);
				spanLeft = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, centerX - dx, TextureOpRasterizer.textureOpClipMinX);
				if (TextureOpRasterizer.clipMaxY >= bottomRow) {
					ArrayUtils.setArrayValuesTo(spanRight, TextureOpRaster.textureOpPixelRows[bottomRow], spanLeft, color);
				}
				if (TextureOpRasterizer.clipMinY <= topRow) {
					ArrayUtils.setArrayValuesTo(spanRight, TextureOpRaster.textureOpPixelRows[topRow], spanLeft, color);
				}
			}
		}
	}
}
