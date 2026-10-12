package com.jagex.game.runetek5.entity.gameobject

import com.jagex.game.runetek5.graphics.Toolkit
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!se")
interface GameObject {

	@OriginalMember(owner = "client!se", name = "a", descriptor = "(I)Z")
	fun hardShadow(): Boolean

	@OriginalMember(owner = "client!se", name = "b", descriptor = "(I)I")
	fun getShape(): Int

	@OriginalMember(owner = "client!se", name = "c", descriptor = "(I)V")
	fun upload()

	@OriginalMember(owner = "client!se", name = "d", descriptor = "(I)I")
	fun getRotation(): Int

	@OriginalMember(owner = "client!se", name = "e", descriptor = "(I)I")
	fun getId(): Int

	@OriginalMember(owner = "client!se", name = "a", descriptor = "(Lclient!ha;I)V")
	fun removeShadow(@OriginalArg(0) toolkit: Toolkit?)

	@OriginalMember(owner = "client!se", name = "a", descriptor = "(ILclient!ha;)V")
	fun addShadow(@OriginalArg(1) toolkit: Toolkit?)
}
