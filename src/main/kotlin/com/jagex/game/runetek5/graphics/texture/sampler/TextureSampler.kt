package com.jagex.game.runetek5.graphics.texture.sampler

import com.jagex.game.runetek5.FilterMode
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!du")
interface TextureSampler {

	@OriginalMember(owner = "client!du", name = "a", descriptor = "(B)V")
	fun bindTexture()

	@OriginalMember(owner = "client!du", name = "a", descriptor = "(Lclient!ria;I)V")
	fun setFilterMode(@OriginalArg(0) mode: FilterMode?)
}
