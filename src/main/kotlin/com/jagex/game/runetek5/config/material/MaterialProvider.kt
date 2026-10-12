package com.jagex.game.runetek5.config.material

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!d")
interface MaterialProvider {

	@OriginalMember(owner = "client!d", name = "a", descriptor = "(IFZIII)[F")
	fun floatArgbOutput(@OriginalArg(0) width: Int, @OriginalArg(1) brightness: Float, @OriginalArg(4) height: Int, @OriginalArg(5) materialId: Int): FloatArray?

	@OriginalMember(owner = "client!d", name = "a", descriptor = "(Z)I")
	fun textureCount(): Int

	@OriginalMember(owner = "client!d", name = "a", descriptor = "(II)Lclient!hd;")
	fun getMetrics(@OriginalArg(0) materialId: Int): MaterialType?

	@OriginalMember(owner = "client!d", name = "b", descriptor = "(II)Z")
	fun textureAvailable(@OriginalArg(1) materialId: Int): Boolean

	@OriginalMember(owner = "client!d", name = "a", descriptor = "(ZIFIII)[I")
	fun getPixels(@OriginalArg(0) readReversed: Boolean, @OriginalArg(1) materialId: Int, @OriginalArg(2) brightness: Float, @OriginalArg(3) width: Int, @OriginalArg(4) height: Int): IntArray?

	@OriginalMember(owner = "client!d", name = "a", descriptor = "(IIFIZI)[I")
	fun getPixelsArgb(@OriginalArg(1) height: Int, @OriginalArg(2) brightness: Float, @OriginalArg(3) materialId: Int, @OriginalArg(5) width: Int): IntArray?
}
