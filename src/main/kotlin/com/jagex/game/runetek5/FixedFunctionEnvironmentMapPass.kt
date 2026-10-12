package com.jagex.game.runetek5

import com.jagex.game.runetek5.graphics.texture.sampler.TextureSampler
import com.jagex.game.runetek5.toolkit.AbstractNativeToolkit
import com.jagex.game.runetek5.toolkit.companion.TextureCombineMode
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!ld")
class FixedFunctionEnvironmentMapPass @OriginalMember(owner = "client!ld", name = "<init>", descriptor = "(Lclient!wga;)V") constructor(@OriginalArg(0) nativeToolkit: AbstractNativeToolkit?) : RenderPass(nativeToolkit) {

	@OriginalMember(owner = "client!ld", name = "m", descriptor = "Z")
	private var active: Boolean = false

	@OriginalMember(owner = "client!ld", name = "a", descriptor = "(ILclient!du;I)V")
	override fun setBaseTexture(@OriginalArg(0) colourType: Int, @OriginalArg(1) baseTexture: TextureSampler?) {
		super.nativeToolkit!!.setTextureSampler(baseTexture)
		super.nativeToolkit!!.setColourType(colourType)
	}

	@OriginalMember(owner = "client!ld", name = "a", descriptor = "(IZ)V")
	override fun enable(@OriginalArg(1) lit: Boolean) {
		@Pc(8) val envMap: CubeTextureSampler? = super.nativeToolkit!!.getEnvironmentMapSampler()
		if (envMap == null || !lit) {
			super.nativeToolkit!!.setTextureAlphaStageState(0, TextureCombineSource.PRIMARY_COLOR)
			return
		}
		super.nativeToolkit!!.setTextureSlot(1)
		super.nativeToolkit!!.setTextureSampler(envMap)
		super.nativeToolkit!!.setTexCoordGeneration(TexCoordGenMode.aClass31_4)
		super.nativeToolkit!!.setTextureSlot(1)
		super.nativeToolkit!!.setTextureCombineModes(TextureCombineFunction.aClass315_4, TextureCombineFunction.aClass315_5)
		super.nativeToolkit!!.setTextureColourSource(true, 2, TextureCombineSource.PREVIOUS, false)
		super.nativeToolkit!!.setTextureAlphaStageState(0, TextureCombineSource.PRIMARY_COLOR)
		@Pc(67) val textureMatrix: NativeMatrix? = super.nativeToolkit!!.getCurrentTextureMatrix()
		textureMatrix!!.copyRotation(super.nativeToolkit!!.getInverseSunMatrix44())
		super.nativeToolkit!!.setTextureState(TextureTransformMode.aClass332_2)
		super.nativeToolkit!!.setTextureSlot(0)
		this.active = true
	}

	@OriginalMember(owner = "client!ld", name = "a", descriptor = "(B)V")
	override fun disable() {
		if (this.active) {
			super.nativeToolkit!!.setTextureSlot(1)
			super.nativeToolkit!!.setTexCoordGeneration(TexCoordGenMode.aClass31_3)
			super.nativeToolkit!!.setTextureCombineModes(TextureCombineMode.MODULATE, TextureCombineMode.MODULATE)
			super.nativeToolkit!!.setTextureRgbStageState(2, TextureCombineSource.CONSTANT_COLOR)
			super.nativeToolkit!!.setTextureAlphaStageState(0, TextureCombineSource.TEXTURE)
			super.nativeToolkit!!.resetCurrentTextureMatrix()
			super.nativeToolkit!!.setTextureSampler(null)
			super.nativeToolkit!!.setTextureSlot(0)
			this.active = false
		} else {
			super.nativeToolkit!!.setTextureAlphaStageState(0, TextureCombineSource.TEXTURE)
		}
		super.nativeToolkit!!.setTextureCombineModes(TextureCombineMode.MODULATE, TextureCombineMode.MODULATE)
	}

	@OriginalMember(owner = "client!ld", name = "a", descriptor = "(III)V")
	override fun setEffectParams(@OriginalArg(1) effectParam1: Int, @OriginalArg(2) effectParam2: Int) {
	}

	@OriginalMember(owner = "client!ld", name = "e", descriptor = "(I)Z")
	override fun isSupported(): Boolean {
		return true
	}

	@OriginalMember(owner = "client!ld", name = "a", descriptor = "(ZB)V")
	override fun applyTextureCombine(@OriginalArg(0) lit: Boolean) {
		super.nativeToolkit!!.setTextureCombineModes(TextureCombineFunction.aClass315_4, TextureCombineMode.MODULATE)
	}
}
