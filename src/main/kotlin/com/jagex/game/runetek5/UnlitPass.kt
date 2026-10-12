package com.jagex.game.runetek5

import com.jagex.game.runetek5.graphics.texture.sampler.TextureSampler
import com.jagex.game.runetek5.toolkit.AbstractNativeToolkit
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!tea")
class UnlitPass @OriginalMember(owner = "client!tea", name = "<init>", descriptor = "(Lclient!wga;)V") constructor(@OriginalArg(0) nativeToolkit: AbstractNativeToolkit?) : RenderPass(nativeToolkit) {

	@OriginalMember(owner = "client!tea", name = "a", descriptor = "(IZ)V")
	override fun enable(@OriginalArg(1) lit: Boolean) {
		super.nativeToolkit!!.setForceLightsDisabled(true)
	}

	@OriginalMember(owner = "client!tea", name = "a", descriptor = "(ILclient!du;I)V")
	override fun setBaseTexture(@OriginalArg(0) combineMode: Int, @OriginalArg(1) baseTexture: TextureSampler?) {
		super.nativeToolkit!!.setTextureSampler(baseTexture)
		super.nativeToolkit!!.setColourType(combineMode)
	}

	@OriginalMember(owner = "client!tea", name = "a", descriptor = "(B)V")
	override fun disable() {
		super.nativeToolkit!!.setForceLightsDisabled(false)
	}

	@OriginalMember(owner = "client!tea", name = "a", descriptor = "(ZB)V")
	override fun applyTextureCombine(@OriginalArg(0) lit: Boolean) {
	}

	@OriginalMember(owner = "client!tea", name = "e", descriptor = "(I)Z")
	override fun isSupported(): Boolean {
		return true
	}

	@OriginalMember(owner = "client!tea", name = "a", descriptor = "(III)V")
	override fun setEffectParams(@OriginalArg(1) effectParam1: Int, @OriginalArg(2) effectParam2: Int) {
	}
}
