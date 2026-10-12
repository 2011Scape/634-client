package com.jagex.game.runetek5

import com.jagex.game.runetek5.graphics.texture.sampler.TextureSampler
import com.jagex.game.runetek5.toolkit.AbstractNativeToolkit
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!fj")
abstract class RenderPass @OriginalMember(owner = "client!fj", name = "<init>", descriptor = "(Lclient!wga;)V") constructor(@OriginalArg(0) nativeToolkit: AbstractNativeToolkit?) {

	@OriginalMember(owner = "client!fj", name = "b", descriptor = "Lclient!wga;")
	@JvmField
	val nativeToolkit: AbstractNativeToolkit?

	init {
		this.nativeToolkit = nativeToolkit
	}

	@OriginalMember(owner = "client!fj", name = "a", descriptor = "(B)V")
	abstract fun disable()

	@OriginalMember(owner = "client!fj", name = "a", descriptor = "(ZB)V")
	abstract fun applyTextureCombine(@OriginalArg(0) lit: Boolean)

	@OriginalMember(owner = "client!fj", name = "a", descriptor = "(I)V")
	open fun onFogChanged() {
	}

	@OriginalMember(owner = "client!fj", name = "b", descriptor = "(B)V")
	open fun onModelMatrixChanged() {
	}

	@OriginalMember(owner = "client!fj", name = "b", descriptor = "(I)V")
	open fun onProjectionChanged() {
	}

	@OriginalMember(owner = "client!fj", name = "a", descriptor = "(IZ)V")
	abstract fun enable(@OriginalArg(1) lit: Boolean)

	@OriginalMember(owner = "client!fj", name = "a", descriptor = "(III)V")
	abstract fun setEffectParams(@OriginalArg(1) effectParam1: Int, @OriginalArg(2) effectParam2: Int)

	@OriginalMember(owner = "client!fj", name = "a", descriptor = "(ILclient!du;I)V")
	abstract fun setBaseTexture(@OriginalArg(0) combineMode: Int, @OriginalArg(1) baseTexture: TextureSampler?)

	@OriginalMember(owner = "client!fj", name = "c", descriptor = "(I)V")
	open fun onUnderwaterSettingsChanged() {
	}

	@OriginalMember(owner = "client!fj", name = "e", descriptor = "(I)Z")
	abstract fun isSupported(): Boolean

	@OriginalMember(owner = "client!fj", name = "a", descriptor = "(Z)V")
	open fun onCameraChanged() {
	}

	@OriginalMember(owner = "client!fj", name = "f", descriptor = "(I)V")
	open fun onTextureMatrixChanged() {
	}
}
