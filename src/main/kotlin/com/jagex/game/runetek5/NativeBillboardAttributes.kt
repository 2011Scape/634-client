package com.jagex.game.runetek5

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!dk")
class NativeBillboardAttributes {

	@OriginalMember(owner = "client!dk", name = "a", descriptor = "I")
	@JvmField
	var angle: Int = 0

	@OriginalMember(owner = "client!dk", name = "d", descriptor = "I")
	@JvmField
	var offsetY: Int = 0

	@OriginalMember(owner = "client!dk", name = "h", descriptor = "I")
	@JvmField
	var offsetX: Int = 0

	@OriginalMember(owner = "client!dk", name = "l", descriptor = "I")
	@JvmField
	var scaleY: Int = 128

	@OriginalMember(owner = "client!dk", name = "o", descriptor = "I")
	@JvmField
	var scaleX: Int = 128

	@OriginalMember(owner = "client!dk", name = "f", descriptor = "I")
	@JvmField
	var colour: Int = 0

	@OriginalMember(owner = "client!dk", name = "<init>", descriptor = "(I)V")
	constructor(@OriginalArg(0) colour: Int) {
		this.colour = colour
	}

	@OriginalMember(owner = "client!dk", name = "<init>", descriptor = "(IIIIII)V")
	private constructor(@OriginalArg(0) colour: Int, @OriginalArg(1) scaleX: Int, @OriginalArg(2) scaleY: Int, @OriginalArg(3) offsetX: Int, @OriginalArg(4) offsetY: Int, @OriginalArg(5) angle: Int) {
		this.colour = colour
		this.scaleX = scaleX
		this.offsetY = offsetY
		this.offsetX = offsetX
		this.scaleY = scaleY
		this.angle = angle
	}

	@OriginalMember(owner = "client!dk", name = "a", descriptor = "(Lclient!dk;B)V")
	fun copyFrom(@OriginalArg(0) nativeBillboardAttributes: NativeBillboardAttributes?) {
		this.offsetX = nativeBillboardAttributes!!.offsetX
		this.angle = nativeBillboardAttributes.angle
		this.colour = nativeBillboardAttributes.colour
		this.scaleX = nativeBillboardAttributes.scaleX
		this.offsetY = nativeBillboardAttributes.offsetY
		this.scaleY = nativeBillboardAttributes.scaleY
	}

	@OriginalMember(owner = "client!dk", name = "a", descriptor = "(I)Lclient!dk;")
	fun copy(): NativeBillboardAttributes {
		return NativeBillboardAttributes(this.colour, this.scaleX, this.scaleY, this.offsetX, this.offsetY, this.angle)
	}
}
