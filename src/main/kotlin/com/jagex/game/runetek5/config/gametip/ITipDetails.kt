package com.jagex.game.runetek5.config.gametip

import com.jagex.game.runetek5.TipRenderDetails
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!af")
interface ITipDetails {

	@OriginalMember(owner = "client!af", name = "a", descriptor = "(B)Lclient!ad;")
	fun getRenderDetails(): TipRenderDetails?
}
