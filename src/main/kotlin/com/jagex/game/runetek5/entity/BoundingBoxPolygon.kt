package com.jagex.game.runetek5.entity

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!iw")
class BoundingBoxPolygon : EntityNode() {

	@OriginalMember(owner = "client!iw", name = "j", descriptor = "I")
	@JvmField
	var bottom: Int = 0

	@OriginalMember(owner = "client!iw", name = "k", descriptor = "I")
	@JvmField
	var size: Int = 0

	@OriginalMember(owner = "client!iw", name = "l", descriptor = "I")
	@JvmField
	var top: Int = 0

	@OriginalMember(owner = "client!iw", name = "m", descriptor = "I")
	@JvmField
	var left: Int = 0

	@OriginalMember(owner = "client!iw", name = "n", descriptor = "I")
	@JvmField
	var right: Int = 0

	@OriginalMember(owner = "client!iw", name = "i", descriptor = "Z")
	@JvmField
	var positionCalculated: Boolean = false

	@OriginalMember(owner = "client!iw", name = "a", descriptor = "(II)Z")
	fun hovered(@OriginalArg(0) mouseX: Int, @OriginalArg(1) mouseY: Int): Boolean {
		if (!this.positionCalculated) {
			return false
		}
		@Pc(10) val deltaX: Int = this.right - this.left
		@Pc(16) val deltaY: Int = this.top - this.bottom
		@Pc(24) val lengthSquared: Int = deltaX * deltaX + deltaY * deltaY
		@Pc(42) var projection: Int = mouseX * deltaX + mouseY * deltaY - this.left * deltaX - this.bottom * deltaY
		@Pc(49) val distX: Int
		@Pc(54) val distY: Int
		if (projection <= 0) {
			distX = this.left - mouseX
			distY = this.bottom - mouseY
			return distX * distX + distY * distY < this.size * this.size
		} else if (projection > lengthSquared) {
			distX = this.right - mouseX
			distY = this.top - mouseY
			return distX * distX + distY * distY < this.size * this.size
		} else {
			projection = (projection shl 10) / lengthSquared
			distX = this.left + (deltaX * projection shr 10) - mouseX
			distY = this.bottom + (deltaY * projection shr 10) - mouseY
			return distX * distX + distY * distY < this.size * this.size
		}
	}
}
