package com.jagex.game.runetek5.cache.node

import java.awt.Canvas
import java.awt.Graphics
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!pi")
abstract class JavaSurface : Node() {

	@OriginalMember(owner = "client!pi", name = "r", descriptor = "[I")
	@JvmField
	var raster: IntArray? = null

	@OriginalMember(owner = "client!pi", name = "s", descriptor = "I")
	@JvmField
	var width: Int = 0

	@OriginalMember(owner = "client!pi", name = "v", descriptor = "I")
	@JvmField
	var height: Int = 0

	@OriginalMember(owner = "client!pi", name = "a", descriptor = "(Ljava/awt/Canvas;III)V")
	abstract fun initialize(@OriginalArg(0) canvas: Canvas?, @OriginalArg(1) width: Int, @OriginalArg(3) height: Int)

	@OriginalMember(owner = "client!pi", name = "a", descriptor = "(IIILjava/awt/Graphics;IIII)V")
	abstract fun clip(@OriginalArg(0) dstY: Int, @OriginalArg(1) srcX: Int, @OriginalArg(2) height: Int, @OriginalArg(3) graphics: Graphics?, @OriginalArg(5) dstX: Int, @OriginalArg(6) width: Int, @OriginalArg(7) srcY: Int)
}
