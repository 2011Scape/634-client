package com.jagex.game.runetek5.graphics.map.worldmap;

import com.jagex.game.runetek5.graphics.Toolkit;
import com.jagex.game.runetek5.statics.WorldMapPolygonFill;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;

public final class WorldMapRasterManager {
	@OriginalMember(owner = "client!df", name = "a", descriptor = "(Lclient!ha;[II)V")
	public static void fillLandmarkPolygon(@OriginalArg(0) Toolkit toolkit, @OriginalArg(1) int[] vertices, @OriginalArg(2) int colour) {
		WorldMapPolygonFill.fillPolygon(toolkit, vertices, vertices.length, colour, null, null);
	}

	private WorldMapRasterManager() {
		/* empty */
	}
	@OriginalMember(owner = "client!df", name = "a", descriptor = "(Lclient!ha;[II[I[I)V")
	public static void fillPolygonMasked(@OriginalArg(0) Toolkit toolkit, @OriginalArg(1) int[] vertices, @OriginalArg(2) int colour, @OriginalArg(3) int[] lineOffsets, @OriginalArg(4) int[] lineWidths) {
		WorldMapPolygonFill.fillPolygon(toolkit, vertices, vertices.length, colour, lineOffsets, lineWidths);
	}
}
