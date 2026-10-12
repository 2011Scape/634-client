package com.jagex.game.runetek5.statics

import com.jagex.game.runetek5.PolygonFiller
import com.jagex.game.runetek5.graphics.Toolkit
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmStatic

object WorldMapPolygonFill {

	@OriginalMember(owner = "client!df", name = "a", descriptor = "(Lclient!ha;[IIII[I[I)V")
	@JvmStatic
	fun fillPolygon(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(1) vertices: IntArray?, @OriginalArg(3) count: Int, @OriginalArg(4) colour: Int, @OriginalArg(5) lineOffsets: IntArray?, @OriginalArg(6) lineWidths: IntArray?) {
		@Pc(2) val clip: IntArray = IntArray(4)
		toolkit!!.K(clip)
		if (lineOffsets != null && clip[3] - clip[1] != lineOffsets.size) {
			throw IllegalStateException()
		}
		PolygonFiller.resetEdgeBuffer()
		PolygonFiller.addEdges(vertices, count)
		PolygonFiller.initializeScanline(clip[1])
		while (true) {
			@Pc(33) var startX: Int
			@Pc(35) var endX: Int
			@Pc(37) var y: Int
			do {
				if (!PolygonFiller.advanceScanline(clip[3])) {
					return
				}
				startX = PolygonFiller.scanlineStartX
				endX = PolygonFiller.scanlineEndX
				y = PolygonFiller.scanlineY
				if (lineOffsets == null) {
					break
				}
				@Pc(45) val line: Int = y - clip[1]
				if (startX < lineOffsets[line] + clip[0]) {
					startX = lineOffsets[line] + clip[0]
				}
				if (endX > lineOffsets[line] + lineWidths!![line] + clip[0]) {
					endX = lineOffsets[line] + lineWidths[line] + clip[0]
				}
			} while (endX - startX <= 0)
			toolkit.U(startX, y, endX - startX, colour, 1)
		}
	}
}
