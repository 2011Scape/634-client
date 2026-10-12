package com.jagex.game.runetek5.graphics

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!en")
class NativeBillboardFace {

	@OriginalMember(owner = "client!en", name = "b", descriptor = "I")
	@JvmField
	val vertexA: Int

	@OriginalMember(owner = "client!en", name = "l", descriptor = "I")
	@JvmField
	val vertexC: Int

	@OriginalMember(owner = "client!en", name = "c", descriptor = "S")
	@JvmField
	val width: Short

	@OriginalMember(owner = "client!en", name = "g", descriptor = "I")
	@JvmField
	val vertexB: Int

	@OriginalMember(owner = "client!en", name = "j", descriptor = "I")
	@JvmField
	val distance: Int

	@OriginalMember(owner = "client!en", name = "f", descriptor = "B")
	@JvmField
	val blendMode: Byte

	@OriginalMember(owner = "client!en", name = "e", descriptor = "I")
	@JvmField
	val face: Int

	@OriginalMember(owner = "client!en", name = "d", descriptor = "S")
	@JvmField
	val height: Short

	@OriginalMember(owner = "client!en", name = "m", descriptor = "Z")
	@JvmField
	val hideWithBloom: Boolean

	@OriginalMember(owner = "client!en", name = "k", descriptor = "S")
	@JvmField
	val texture: Short

	@OriginalMember(owner = "client!en", name = "<init>", descriptor = "(IIIIIIIIIZZI)V")
	constructor(@OriginalArg(0) face: Int, @OriginalArg(1) vertexC: Int, @OriginalArg(2) vertexB: Int, @OriginalArg(3) vertexA: Int, @OriginalArg(4) height: Int, @OriginalArg(5) width: Int, @OriginalArg(6) texture: Int, @OriginalArg(7) colourOp: Int, @OriginalArg(8) blendMode: Int, @OriginalArg(9) hideFace: Boolean, @OriginalArg(10) hideWithBloom: Boolean, @OriginalArg(11) distance: Int) {
		this.vertexA = vertexA
		this.vertexC = vertexC
		this.width = width.toShort()
		this.vertexB = vertexB
		this.distance = distance
		this.blendMode = blendMode.toByte()
		this.face = face
		this.height = height.toShort()
		this.hideWithBloom = hideWithBloom
		this.texture = texture.toShort()
	}
}
