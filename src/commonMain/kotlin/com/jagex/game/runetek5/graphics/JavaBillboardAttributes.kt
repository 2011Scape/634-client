package com.jagex.game.runetek5.graphics

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField

@OriginalClass("client!ei")
class JavaBillboardAttributes {

	@OriginalMember(owner = "client!ei", name = "a", descriptor = "I")
	@JvmField
	var projectedHeight: Int = 0

	@OriginalMember(owner = "client!ei", name = "b", descriptor = "I")
	@JvmField
	var rotation: Int = 0

	@OriginalMember(owner = "client!ei", name = "c", descriptor = "I")
	@JvmField
	var projectedWidth: Int = 0

	@OriginalMember(owner = "client!ei", name = "d", descriptor = "I")
	@JvmField
	var y: Int = 0

	@OriginalMember(owner = "client!ei", name = "f", descriptor = "I")
	@JvmField
	var x: Int = 0

	@OriginalMember(owner = "client!ei", name = "j", descriptor = "I")
	@JvmField
	var offsetX: Int = 0

	@OriginalMember(owner = "client!ei", name = "k", descriptor = "I")
	@JvmField
	var offsetY: Int = 0

	@OriginalMember(owner = "client!ei", name = "n", descriptor = "I")
	@JvmField
	var z: Int = 0

	@OriginalMember(owner = "client!ei", name = "e", descriptor = "I")
	@JvmField
	var scaleY: Int = 128

	@OriginalMember(owner = "client!ei", name = "h", descriptor = "I")
	@JvmField
	var scaleX: Int = 128

	@OriginalMember(owner = "client!ei", name = "g", descriptor = "I")
	@JvmField
	var diffuseColor: Int = 0

	@OriginalMember(owner = "client!ei", name = "<init>", descriptor = "(I)V")
	constructor(@OriginalArg(0) diffuseColor: Int) {
		this.diffuseColor = diffuseColor
	}

	@OriginalMember(owner = "client!ei", name = "<init>", descriptor = "(IIIIII)V")
	private constructor(@OriginalArg(0) diffuseColor: Int, @OriginalArg(1) scaleX: Int, @OriginalArg(2) scaleY: Int, @OriginalArg(3) offsetX: Int, @OriginalArg(4) offsetY: Int, @OriginalArg(5) rotation: Int) {
		this.offsetY = offsetY
		this.diffuseColor = diffuseColor
		this.offsetX = offsetX
		this.scaleY = scaleY
		this.rotation = rotation
		this.scaleX = scaleX
	}

	@OriginalMember(owner = "client!ei", name = "a", descriptor = "(Z)Lclient!ei;")
	fun copy(): JavaBillboardAttributes? {
		return JavaBillboardAttributes(this.diffuseColor, this.scaleX, this.scaleY, this.offsetX, this.offsetY, this.rotation)
	}

	@OriginalMember(owner = "client!ei", name = "a", descriptor = "(Lclient!ei;Z)V")
	fun copyFrom(@OriginalArg(0) javaBillboardAttributes: JavaBillboardAttributes?) {
		this.rotation = javaBillboardAttributes!!.rotation
		this.scaleX = javaBillboardAttributes.scaleX
		this.offsetX = javaBillboardAttributes.offsetX
		this.offsetY = javaBillboardAttributes.offsetY
		this.diffuseColor = javaBillboardAttributes.diffuseColor
		this.scaleY = javaBillboardAttributes.scaleY
	}
}
