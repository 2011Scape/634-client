package com.jagex.game.runetek5.graphics.map.worldmap

import com.jagex.game.runetek5.graphics.Toolkit
import com.jagex.game.runetek5.statics.WorldMapPolygonFill
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember

object WorldMapRasterManager {
	@OriginalMember(owner = "client!df", name = "a", descriptor = "(Lclient!ha;[II)V")
	@JvmStatic
	fun fillLandmarkPolygon(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(1) vertices: IntArray?, @OriginalArg(2) colour: Int) {
		WorldMapPolygonFill.fillPolygon(toolkit, vertices, vertices!!.size, colour, null, null)
	}

	@OriginalMember(owner = "client!df", name = "a", descriptor = "(Lclient!ha;[II[I[I)V")
	@JvmStatic
	fun fillPolygonMasked(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(1) vertices: IntArray?, @OriginalArg(2) colour: Int, @OriginalArg(3) lineOffsets: IntArray?, @OriginalArg(4) lineWidths: IntArray?) {
		WorldMapPolygonFill.fillPolygon(toolkit, vertices, vertices!!.size, colour, lineOffsets, lineWidths)
	}
}
