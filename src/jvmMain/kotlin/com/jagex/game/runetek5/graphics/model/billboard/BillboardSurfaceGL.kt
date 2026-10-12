package com.jagex.game.runetek5.graphics.model.billboard

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!iq")
class BillboardSurfaceGL {

	@OriginalMember(owner = "client!iq", name = "a", descriptor = "I")
	@JvmField
	var offsetY: Int = 0

	@OriginalMember(owner = "client!iq", name = "g", descriptor = "I")
	@JvmField
	var angle: Int = 0

	@OriginalMember(owner = "client!iq", name = "h", descriptor = "I")
	@JvmField
	var offsetX: Int = 0

	@OriginalMember(owner = "client!iq", name = "k", descriptor = "I")
	@JvmField
	var scaleY: Int = 128

	@OriginalMember(owner = "client!iq", name = "j", descriptor = "I")
	@JvmField
	var scaleX: Int = 128

	@OriginalMember(owner = "client!iq", name = "l", descriptor = "I")
	@JvmField
	var diffuseColor: Int = 0

	@OriginalMember(owner = "client!iq", name = "<init>", descriptor = "(I)V")
	constructor(@OriginalArg(0) diffuseColor: Int) {
		this.diffuseColor = diffuseColor
	}

	@OriginalMember(owner = "client!iq", name = "<init>", descriptor = "(IIIIII)V")
	private constructor(@OriginalArg(0) diffuseColor: Int, @OriginalArg(1) scaleX: Int, @OriginalArg(2) scaleY: Int, @OriginalArg(3) offsetX: Int, @OriginalArg(4) offsetY: Int, @OriginalArg(5) rotation: Int) {
		this.offsetY = offsetY
		this.offsetX = offsetX
		this.angle = rotation
		this.diffuseColor = diffuseColor
		this.scaleX = scaleX
		this.scaleY = scaleY
	}

	@OriginalMember(owner = "client!iq", name = "a", descriptor = "(Lclient!iq;B)V")
	fun copyFrom(@OriginalArg(0) glBillboardAttributes: BillboardSurfaceGL?) {
		this.scaleY = glBillboardAttributes!!.scaleY
		this.diffuseColor = glBillboardAttributes.diffuseColor
		this.scaleX = glBillboardAttributes.scaleX
		this.offsetX = glBillboardAttributes.offsetX
		this.offsetY = glBillboardAttributes.offsetY
		this.angle = glBillboardAttributes.angle
	}

	@OriginalMember(owner = "client!iq", name = "a", descriptor = "(Z)Lclient!iq;")
	fun copy(): BillboardSurfaceGL {
		return BillboardSurfaceGL(this.diffuseColor, this.scaleX, this.scaleY, this.offsetX, this.offsetY, this.angle)
	}
}
