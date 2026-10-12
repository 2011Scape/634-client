package com.jagex.game.runetek5.graphics

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!is")
interface OffscreenSurface {

	@OriginalMember(owner = "client!is", name = "a", descriptor = "(IIIIIIZZ)V")
	fun copyToSurface(@OriginalArg(0) srcX: Int, @OriginalArg(1) srcY: Int, @OriginalArg(2) width: Int, @OriginalArg(3) height: Int, @OriginalArg(4) dstX: Int, @OriginalArg(5) dstY: Int)

	@OriginalMember(owner = "client!is", name = "b", descriptor = "(IIIIIIZZ)V")
	fun copyFromSurface(@OriginalArg(0) srcX: Int, @OriginalArg(1) srcY: Int, @OriginalArg(2) width: Int, @OriginalArg(3) height: Int, @OriginalArg(6) copyRaster: Boolean)
}
