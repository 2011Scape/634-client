package com.jagex.game.runetek5.graphics

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!go")
class JavaBillboardFace {

	@OriginalMember(owner = "client!go", name = "c", descriptor = "Z")
	@JvmField
	val hideFace: Boolean

	@OriginalMember(owner = "client!go", name = "n", descriptor = "B")
	@JvmField
	val blendMode: Byte

	@OriginalMember(owner = "client!go", name = "l", descriptor = "I")
	@JvmField
	val distance: Int

	@OriginalMember(owner = "client!go", name = "a", descriptor = "S")
	@JvmField
	val height: Short

	@OriginalMember(owner = "client!go", name = "f", descriptor = "B")
	@JvmField
	val colourOp: Byte

	@OriginalMember(owner = "client!go", name = "d", descriptor = "S")
	@JvmField
	val texture: Short

	@OriginalMember(owner = "client!go", name = "h", descriptor = "S")
	@JvmField
	val width: Short

	@OriginalMember(owner = "client!go", name = "b", descriptor = "I")
	@JvmField
	val face: Int

	@OriginalMember(owner = "client!go", name = "<init>", descriptor = "(IIIIIIIIIZI)V")
	constructor(@OriginalArg(0) face: Int, @OriginalArg(1) vertexA: Int, @OriginalArg(2) vertexB: Int, @OriginalArg(3) vertexC: Int, @OriginalArg(4) width: Int, @OriginalArg(5) height: Int, @OriginalArg(6) texture: Int, @OriginalArg(7) colourOp: Int, @OriginalArg(8) blendMode: Int, @OriginalArg(9) hideFace: Boolean, @OriginalArg(10) distance: Int) {
		this.hideFace = hideFace
		this.blendMode = blendMode.toByte()
		this.distance = distance
		this.height = height.toShort()
		this.colourOp = colourOp.toByte()
		this.texture = texture.toShort()
		this.width = width.toShort()
		this.face = face
	}
}
