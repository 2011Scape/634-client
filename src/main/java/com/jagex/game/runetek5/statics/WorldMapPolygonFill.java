package com.jagex.game.runetek5.statics;

import com.jagex.game.runetek5.PolygonFiller;
import com.jagex.game.runetek5.graphics.Toolkit;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

public final class WorldMapPolygonFill {

	@OriginalMember(owner = "client!df", name = "a", descriptor = "(Lclient!ha;[IIII[I[I)V")
	public static void fillPolygon(@OriginalArg(0) Toolkit toolkit, @OriginalArg(1) int[] vertices, @OriginalArg(3) int count, @OriginalArg(4) int colour, @OriginalArg(5) int[] lineOffsets, @OriginalArg(6) int[] lineWidths) {
		@Pc(2) int[] clip = new int[4];
		toolkit.K(clip);
		if (lineOffsets != null && clip[3] - clip[1] != lineOffsets.length) {
			throw new IllegalStateException();
		}
		PolygonFiller.resetEdgeBuffer();
		PolygonFiller.addEdges(vertices, count);
		PolygonFiller.initializeScanline(clip[1]);
		while (true) {
			@Pc(33) int startX;
			@Pc(35) int endX;
			@Pc(37) int y;
			do {
				if (!PolygonFiller.advanceScanline(clip[3])) {
					return;
				}
				startX = PolygonFiller.scanlineStartX;
				endX = PolygonFiller.scanlineEndX;
				y = PolygonFiller.scanlineY;
				if (lineOffsets == null) {
					break;
				}
				@Pc(45) int line = y - clip[1];
				if (startX < lineOffsets[line] + clip[0]) {
					startX = lineOffsets[line] + clip[0];
				}
				if (endX > lineOffsets[line] + lineWidths[line] + clip[0]) {
					endX = lineOffsets[line] + lineWidths[line] + clip[0];
				}
			} while (endX - startX <= 0);
			toolkit.U(startX, y, endX - startX, colour, 1);
		}
	}
}
