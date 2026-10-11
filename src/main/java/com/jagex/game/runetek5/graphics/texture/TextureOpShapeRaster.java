package com.jagex.game.runetek5.graphics.texture;

import com.jagex.game.runetek5.config.material.texture.operation.RectangleRasterizer;
import com.jagex.game.runetek5.sound.player.PcmPlayer;
import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable;
import com.jagex.game.runetek5.buffer.Packet;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.utils.ArrayUtils;
import com.jagex.game.runetek5.config.material.texture.TextureOpRasterizer;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.graphics.TextureOpRaster;

public final class TextureOpShapeRaster {

	@OriginalMember(owner = "client!tf", name = "a", descriptor = "(Lclient!cea;I)Lclient!gfa;")
	public static RectangleRasterizer decodeRectangle(@OriginalArg(0) Packet packet) {
		return new RectangleRasterizer(packet.readShort(), packet.readShort(), packet.readShort(), packet.readShort(), packet.readUMedium(), packet.readUMedium(), packet.readUByte());
	}

	@OriginalMember(owner = "client!tf", name = "a", descriptor = "(IIIIIIII)V")
	public static void drawClippedEllipse(@OriginalArg(0) int outerRx, @OriginalArg(1) int cx, @OriginalArg(2) int cy, @OriginalArg(4) int outerRy, @OriginalArg(5) int fillColor, @OriginalArg(6) int strokeColor, @OriginalArg(7) int thickness) {
		@Pc(7) int outerX = 0;
		@Pc(9) int outerY = outerRy;
		@Pc(11) int innerX = 0;
		@Pc(16) int innerRx = outerRx - thickness;
		@Pc(20) int innerRy = outerRy - thickness;
		@Pc(24) int outerRxSq = outerRx * outerRx;
		@Pc(28) int outerRySq = outerRy * outerRy;
		@Pc(32) int innerRxSq = innerRx * innerRx;
		@Pc(36) int innerRySq = innerRy * innerRy;
		@Pc(40) int outerRySq2 = outerRySq << 1;
		@Pc(44) int outerRxSq2 = outerRxSq << 1;
		@Pc(48) int innerRySq2 = innerRySq << 1;
		@Pc(52) int innerRxSq2 = innerRxSq << 1;
		@Pc(56) int outerDiam = outerRy << 1;
		@Pc(60) int innerDiam = innerRy << 1;
		@Pc(69) int outerDecA = outerRySq2 + outerRxSq * (1 - outerDiam);
		@Pc(78) int outerDecB = outerRySq - outerRxSq2 * (outerDiam - 1);
		@Pc(86) int innerDecA = (1 - innerDiam) * innerRxSq + innerRySq2;
		@Pc(95) int innerDecB = innerRySq - innerRxSq2 * (innerDiam - 1);
		@Pc(99) int outerRxSq4 = outerRxSq << 2;
		@Pc(103) int outerRySq4 = outerRySq << 2;
		@Pc(107) int innerRxSq4 = innerRxSq << 2;
		@Pc(111) int innerRySq4 = innerRySq << 2;
		@Pc(115) int outerGradA = outerRySq2 * 3;
		@Pc(121) int outerGradB = outerRxSq2 * (outerDiam - 3);
		@Pc(125) int innerGradA = innerRySq2 * 3;
		@Pc(131) int innerGradB = (innerDiam - 3) * innerRxSq2;
		@Pc(133) int outerStepA = outerRySq4;
		@Pc(139) int outerStepB = outerRxSq4 * (outerRy - 1);
		@Pc(141) int innerStepA = innerRySq4;
		@Pc(147) int innerStepB = innerRxSq4 * (innerRy - 1);
		@Pc(170) int local170;
		@Pc(178) int local178;
		@Pc(189) int local189;
		@Pc(197) int local197;
		if (TextureOpRasterizer.clipMinY <= cy && TextureOpRasterizer.clipMaxY >= cy) {
			@Pc(161) int[] centerRow = TextureOpRaster.textureOpPixelRows[cy];
			int outerLeft = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, cx - outerRx, TextureOpRasterizer.textureOpClipMinX);
			int outerRight = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, outerRx + cx, TextureOpRasterizer.textureOpClipMinX);
			int innerLeft = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, cx - innerRx, TextureOpRasterizer.textureOpClipMinX);
			int innerRight = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, cx + innerRx, TextureOpRasterizer.textureOpClipMinX);
			ArrayUtils.setArrayValuesTo(innerLeft, centerRow, outerLeft, strokeColor);
			ArrayUtils.setArrayValuesTo(innerRight, centerRow, innerLeft, fillColor);
			ArrayUtils.setArrayValuesTo(outerRight, centerRow, innerRight, strokeColor);
		}
		while (outerY > 0) {
			@Pc(224) boolean withinInner = innerRy >= outerY;
			if (withinInner) {
				if (innerDecA < 0) {
					while (innerDecA < 0) {
						innerDecA += innerGradA;
						innerDecB += innerStepA;
						innerStepA += innerRySq4;
						innerX++;
						innerGradA += innerRySq4;
					}
				}
				if (innerDecB < 0) {
					innerDecA += innerGradA;
					innerDecB += innerStepA;
					innerStepA += innerRySq4;
					innerGradA += innerRySq4;
					innerX++;
				}
				innerDecA += -innerStepB;
				innerDecB += -innerGradB;
				innerStepB -= innerRxSq4;
				innerGradB -= innerRxSq4;
			}
			if (outerDecA < 0) {
				while (outerDecA < 0) {
					outerDecA += outerGradA;
					outerDecB += outerStepA;
					outerX++;
					outerStepA += outerRySq4;
					outerGradA += outerRySq4;
				}
			}
			if (outerDecB < 0) {
				outerDecB += outerStepA;
				outerDecA += outerGradA;
				outerGradA += outerRySq4;
				outerX++;
				outerStepA += outerRySq4;
			}
			outerDecA += -outerStepB;
			outerDecB += -outerGradB;
			outerY--;
			outerGradB -= outerRxSq4;
			outerStepB -= outerRxSq4;
			int topY = cy - outerY;
			int bottomY = outerY + cy;
			if (TextureOpRasterizer.clipMinY <= bottomY && topY <= TextureOpRasterizer.clipMaxY) {
				int outerRightEdge = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, outerX + cx, TextureOpRasterizer.textureOpClipMinX);
				int outerLeftEdge = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, cx - outerX, TextureOpRasterizer.textureOpClipMinX);
				if (withinInner) {
					@Pc(440) int innerRightEdge = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, cx + innerX, TextureOpRasterizer.textureOpClipMinX);
					@Pc(449) int innerLeftEdge = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, cx - innerX, TextureOpRasterizer.textureOpClipMinX);
					@Pc(460) int[] row;
					if (topY >= TextureOpRasterizer.clipMinY) {
						row = TextureOpRaster.textureOpPixelRows[topY];
						ArrayUtils.setArrayValuesTo(innerLeftEdge, row, outerLeftEdge, strokeColor);
						ArrayUtils.setArrayValuesTo(innerRightEdge, row, innerLeftEdge, fillColor);
						ArrayUtils.setArrayValuesTo(outerRightEdge, row, innerRightEdge, strokeColor);
					}
					if (TextureOpRasterizer.clipMaxY >= bottomY) {
						row = TextureOpRaster.textureOpPixelRows[bottomY];
						ArrayUtils.setArrayValuesTo(innerLeftEdge, row, outerLeftEdge, strokeColor);
						ArrayUtils.setArrayValuesTo(innerRightEdge, row, innerLeftEdge, fillColor);
						ArrayUtils.setArrayValuesTo(outerRightEdge, row, innerRightEdge, strokeColor);
					}
				} else {
					if (TextureOpRasterizer.clipMinY <= topY) {
						ArrayUtils.setArrayValuesTo(outerRightEdge, TextureOpRaster.textureOpPixelRows[topY], outerLeftEdge, strokeColor);
					}
					if (bottomY <= TextureOpRasterizer.clipMaxY) {
						ArrayUtils.setArrayValuesTo(outerRightEdge, TextureOpRaster.textureOpPixelRows[bottomY], outerLeftEdge, strokeColor);
					}
				}
			}
		}
	}
}
