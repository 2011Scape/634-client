package com.jagex.game.runetek5.graphics.model.normal

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!pd")
class MeshBillboard @OriginalMember(owner = "client!pd", name = "<init>", descriptor = "(IIII)V") constructor(@OriginalArg(0) billboardId: Int, @OriginalArg(1) appliesOnFace: Int, @OriginalArg(2) depth: Int, @OriginalArg(3) distance: Int) {

	@OriginalMember(owner = "client!pd", name = "e", descriptor = "I")
	@JvmField
	val depth: Int

	@OriginalMember(owner = "client!pd", name = "d", descriptor = "I")
	@JvmField
	val appliesOnFace: Int

	@OriginalMember(owner = "client!pd", name = "g", descriptor = "I")
	@JvmField
	val distance: Int

	@OriginalMember(owner = "client!pd", name = "b", descriptor = "I")
	@JvmField
	val billboardId: Int

	init {
		this.depth = depth
		this.appliesOnFace = appliesOnFace
		this.distance = distance
		this.billboardId = billboardId
	}

	@OriginalMember(owner = "client!pd", name = "a", descriptor = "(II)Lclient!pd;")
	fun copyWithFace(@OriginalArg(1) appliesOnFace: Int): MeshBillboard {
		return MeshBillboard(this.billboardId, appliesOnFace, this.depth, this.distance)
	}
}
