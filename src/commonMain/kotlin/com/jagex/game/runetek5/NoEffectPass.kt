package com.jagex.game.runetek5

import com.jagex.game.runetek5.graphics.texture.sampler.TextureSampler
import com.jagex.game.runetek5.toolkit.AbstractNativeToolkit
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!al")
class NoEffectPass : RenderPass {

	@OriginalMember(owner = "client!al", name = "<init>", descriptor = "(Lclient!wga;)V")
	constructor(@OriginalArg(0) nativeToolkit: AbstractNativeToolkit?) : super(nativeToolkit)

	@OriginalMember(owner = "client!al", name = "a", descriptor = "(B)V")
	override fun disable() {
	}

	@OriginalMember(owner = "client!al", name = "a", descriptor = "(III)V")
	override fun setEffectParams(@OriginalArg(1) effectParam1: Int, @OriginalArg(2) effectParam2: Int) {
	}

	@OriginalMember(owner = "client!al", name = "a", descriptor = "(IZ)V")
	override fun enable(@OriginalArg(1) lit: Boolean) {
	}

	@OriginalMember(owner = "client!al", name = "a", descriptor = "(ILclient!du;I)V")
	override fun setBaseTexture(@OriginalArg(0) combineMode: Int, @OriginalArg(1) baseTexture: TextureSampler?) {
		super.nativeToolkit!!.setTextureSampler(baseTexture)
		super.nativeToolkit!!.setColourType(combineMode)
	}

	@OriginalMember(owner = "client!al", name = "e", descriptor = "(I)Z")
	override fun isSupported(): Boolean {
		return false
	}

	@OriginalMember(owner = "client!al", name = "a", descriptor = "(ZB)V")
	override fun applyTextureCombine(@OriginalArg(0) lit: Boolean) {
	}
}
