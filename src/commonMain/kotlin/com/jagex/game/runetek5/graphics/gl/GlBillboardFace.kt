package com.jagex.game.runetek5.graphics.gl

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField

@OriginalClass("client!nea")
class GlBillboardFace {

	@OriginalMember(owner = "client!nea", name = "a", descriptor = "I")
	@JvmField
	val vertexB: Int

	@OriginalMember(owner = "client!nea", name = "b", descriptor = "I")
	@JvmField
	val vertexC: Int

	@OriginalMember(owner = "client!nea", name = "h", descriptor = "I")
	@JvmField
	val vertexA: Int

	@OriginalMember(owner = "client!nea", name = "d", descriptor = "I")
	@JvmField
	val distance: Int

	@OriginalMember(owner = "client!nea", name = "i", descriptor = "B")
	@JvmField
	val blendMode: Byte

	@OriginalMember(owner = "client!nea", name = "k", descriptor = "Z")
	@JvmField
	val hideWithBloom: Boolean

	@OriginalMember(owner = "client!nea", name = "e", descriptor = "S")
	@JvmField
	val width: Short

	@OriginalMember(owner = "client!nea", name = "c", descriptor = "I")
	@JvmField
	val face: Int

	@OriginalMember(owner = "client!nea", name = "g", descriptor = "S")
	@JvmField
	val texture: Short

	@OriginalMember(owner = "client!nea", name = "j", descriptor = "S")
	@JvmField
	val height: Short

	@OriginalMember(owner = "client!nea", name = "<init>", descriptor = "(IIIIIIIIIZZI)V")
	constructor(@OriginalArg(0) face: Int, @OriginalArg(1) vertexB: Int, @OriginalArg(2) vertexA: Int, @OriginalArg(3) vertexC: Int, @OriginalArg(4) width: Int, @OriginalArg(5) height: Int, @OriginalArg(6) texture: Int, @OriginalArg(7) blendType: Int, @OriginalArg(8) blendMode: Int, @OriginalArg(9) hasUid: Boolean, @OriginalArg(10) hideWithBloom: Boolean, @OriginalArg(11) distance: Int) {
		this.vertexB = vertexB
		this.vertexC = vertexC
		this.vertexA = vertexA
		this.distance = distance
		this.blendMode = blendMode.toByte()
		this.hideWithBloom = hideWithBloom
		this.width = width.toShort()
		this.face = face
		this.texture = texture.toShort()
		this.height = height.toShort()
	}
}
