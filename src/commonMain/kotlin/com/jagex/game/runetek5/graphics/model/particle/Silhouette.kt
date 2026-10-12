package com.jagex.game.runetek5.graphics.model.particle

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.graphics.model.Model
import kotlin.jvm.JvmStatic

@OriginalClass("client!hw")
class Silhouette @OriginalMember(owner = "client!hw", name = "<init>", descriptor = "(IIIIIIIIII)V") constructor(@OriginalArg(0) absX: Int, @OriginalArg(1) absY: Int, @OriginalArg(2) absZ: Int, @OriginalArg(3) size2d: Int, @OriginalArg(4) minX: Int, @OriginalArg(5) maxX: Int, @OriginalArg(6) minY: Int, @OriginalArg(7) maxY: Int, @OriginalArg(8) minZ: Int, @OriginalArg(9) maxZ: Int) {

	@OriginalMember(owner = "client!hw", name = "e", descriptor = "I")
	private var absX: Int = 0

	@OriginalMember(owner = "client!hw", name = "n", descriptor = "I")
	private var absY: Int = 0

	@OriginalMember(owner = "client!hw", name = "b", descriptor = "I")
	private var squareRadius: Int = 0

	@OriginalMember(owner = "client!hw", name = "f", descriptor = "I")
	private var y: Int = 0

	@OriginalMember(owner = "client!hw", name = "i", descriptor = "I")
	private var z1: Int = 0

	@OriginalMember(owner = "client!hw", name = "c", descriptor = "I")
	private var y2: Int = 0

	@OriginalMember(owner = "client!hw", name = "m", descriptor = "I")
	private var y1: Int = 0

	@OriginalMember(owner = "client!hw", name = "a", descriptor = "I")
	private var z2: Int = 0

	@OriginalMember(owner = "client!hw", name = "p", descriptor = "I")
	private var x1: Int = 0

	@OriginalMember(owner = "client!hw", name = "o", descriptor = "I")
	private var x2: Int = 0

	init {
		this.absX = absZ
		this.absY = absY
		this.squareRadius = size2d * size2d
		this.y = absX
		this.z1 = this.absX + minZ
		this.y2 = maxY + this.absY
		this.y1 = minY + this.absY
		this.z2 = maxZ + this.absX
		this.x1 = maxX + this.y
		this.x2 = this.y + minX
	}

	@OriginalMember(owner = "client!hw", name = "a", descriptor = "(IIBIIIIIIII)V")
	fun set(@OriginalArg(0) maxY: Int, @OriginalArg(1) minY: Int, @OriginalArg(3) centerX: Int, @OriginalArg(4) size2d: Int, @OriginalArg(5) absX: Int, @OriginalArg(6) minX: Int, @OriginalArg(7) maxZ: Int, @OriginalArg(8) minZ: Int, @OriginalArg(9) absY: Int, @OriginalArg(10) maxX: Int) {
		this.y = centerX
		this.absY = absY
		this.squareRadius = size2d * size2d
		this.absX = absX
		this.z2 = maxZ + this.absX
		this.z1 = this.absX + minZ
		this.x1 = this.y + maxX
		this.x2 = minX + this.y
		this.y1 = minY + this.absY
		this.y2 = this.absY + maxY
	}

	@OriginalMember(owner = "client!hw", name = "a", descriptor = "(IIII)Z")
	fun contains(@OriginalArg(1) posZ: Int, @OriginalArg(2) posY: Int, @OriginalArg(3) posX: Int): Boolean {
		if (this.x2 > posX || this.x1 < posX) {
			return false
		} else if (posY < this.y1 || posY > this.y2) {
			return false
		} else if (posZ >= this.z1 && posZ <= this.z2) {
			@Pc(58) val deltaX: Int = posX - this.y
			@Pc(64) val deltaY: Int = posZ - this.absX
			return this.squareRadius > deltaX * deltaX + deltaY * deltaY
		} else {
			return false
		}
	}

	companion object {
		@OriginalMember(owner = "client!jm", name = "a", descriptor = "(ILclient!ka;III)Lclient!hw;")
		@JvmStatic
		fun fromModel(@OriginalArg(0) absX: Int, @OriginalArg(1) model: Model?, @OriginalArg(2) absZ: Int, @OriginalArg(3) absY: Int): Silhouette? {
			return if (model == null) null else Silhouette(absX, absY, absZ, model.na(), model.V(), model.RA(), model.fa(), model.EA(), model.HA(), model.G())
		}

		@OriginalMember(owner = "client!ub", name = "a", descriptor = "(IILclient!hw;Lclient!ka;ZI)V")
		@JvmStatic
		fun updateFromModel(@OriginalArg(0) absX: Int, @OriginalArg(1) absY: Int, @OriginalArg(2) boundingCylinder: Silhouette?, @OriginalArg(3) model: Model?, @OriginalArg(5) centerX: Int) {
			if (model != null) {
				boundingCylinder!!.set(model.EA(), model.fa(), centerX, model.na(), absX, model.V(), model.G(), model.HA(), absY, model.RA())
			}
		}
	}
}
