package com.jagex.game.runetek5

import com.jagex.game.runetek5.graphics.texture.sampler.TextureSampler
import com.jagex.game.runetek5.graphics.texture.water.NativeWaterTexture
import com.jagex.game.runetek5.toolkit.AbstractNativeToolkit
import com.jagex.game.runetek5.toolkit.companion.TextureCombineMode
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!kl")
class FixedFunctionWaterPass @OriginalMember(owner = "client!kl", name = "<init>", descriptor = "(Lclient!wga;Lclient!ur;)V") constructor(@OriginalArg(0) nativeToolkit: AbstractNativeToolkit?, @OriginalArg(1) water: NativeWaterTexture?) : RenderPass(nativeToolkit) {

	@OriginalMember(owner = "client!kl", name = "j", descriptor = "F")
	private var wavePhase: Float = 0.0f

	@OriginalMember(owner = "client!kl", name = "m", descriptor = "Lclient!ur;")
	private val water: NativeWaterTexture? = water

	@OriginalMember(owner = "client!kl", name = "a", descriptor = "(B)V")
	override fun disable() {
		super.nativeToolkit!!.setTextureSlot(1)
		super.nativeToolkit!!.setTextureCombineModes(TextureCombineMode.MODULATE, TextureCombineMode.MODULATE)
		super.nativeToolkit!!.setTextureRgbStageState(0, TextureCombineSource.TEXTURE)
		super.nativeToolkit!!.setTextureAlphaStageState(0, TextureCombineSource.TEXTURE)
		super.nativeToolkit!!.setTexCoordIndex(1)
		super.nativeToolkit!!.setTextureSampler(null)
		super.nativeToolkit!!.setTextureSlot(0)
		super.nativeToolkit!!.setTextureAlphaStageState(0, TextureCombineSource.TEXTURE)
	}

	@OriginalMember(owner = "client!kl", name = "a", descriptor = "(III)V")
	override fun setEffectParams(@OriginalArg(1) effectParam1: Int, @OriginalArg(2) effectParam2: Int) {
		super.nativeToolkit!!.setTextureSlot(1)
		if ((effectParam1 and 0x80) != 0) {
			super.nativeToolkit!!.setTextureSampler(null)
		} else if ((effectParam2 and 0x1) == 1) {
			if (this.water!!.hasDepth) {
				this.wavePhase = (super.nativeToolkit!!.animatingCycle % 4000).toFloat() / 4000.0f
				super.nativeToolkit!!.setTextureSampler(this.water.waterSampler3d)
			} else {
				@Pc(87) val frame: Int = super.nativeToolkit!!.animatingCycle % 4000 * 16 / 4000
				super.nativeToolkit!!.setTextureSampler(this.water.waterTextureSamplers!![frame])
			}
		} else if (this.water!!.hasDepth) {
			super.nativeToolkit!!.setTextureSampler(this.water.waterSampler3d)
		} else {
			super.nativeToolkit!!.setTextureSampler(this.water.waterTextureSamplers!![0])
		}
		super.nativeToolkit!!.setTextureSlot(0)
	}

	@OriginalMember(owner = "client!kl", name = "e", descriptor = "(I)Z")
	override fun isSupported(): Boolean {
		return this.water!!.readyToSample()
	}

	@OriginalMember(owner = "client!kl", name = "f", descriptor = "(I)V")
	override fun onTextureMatrixChanged() {
		if (super.nativeToolkit!!.getCurrentTextureSlot() != 0) {
			return
		}
		@Pc(12) val baseMatrix: NativeMatrix? = super.nativeToolkit!!.getTextureMatrix()
		super.nativeToolkit!!.setTextureSlot(1)
		@Pc(24) val waveMatrix: NativeMatrix? = super.nativeToolkit!!.getCurrentTextureMatrix()
		waveMatrix!!.apply(baseMatrix)
		waveMatrix.scale(0.125f, 1.0f, 0.125f)
		waveMatrix.translate(0.0f, this.wavePhase, 0.0f)
		super.nativeToolkit!!.setTextureState(TextureTransformMode.aClass332_2)
		super.nativeToolkit!!.setTextureSlot(0)
	}

	@OriginalMember(owner = "client!kl", name = "a", descriptor = "(ZB)V")
	override fun applyTextureCombine(@OriginalArg(0) lit: Boolean) {
		super.nativeToolkit!!.setTextureCombineModes(TextureCombineFunction.aClass315_4, TextureCombineMode.MODULATE)
	}

	@OriginalMember(owner = "client!kl", name = "a", descriptor = "(IZ)V")
	override fun enable(@OriginalArg(1) lit: Boolean) {
		super.nativeToolkit!!.setTextureSlot(1)
		super.nativeToolkit!!.setTextureCombineModes(TextureCombineFunction.aClass315_4, TextureCombineFunction.aClass315_2)
		super.nativeToolkit!!.setTextureColourSource(true, 0, TextureCombineSource.TEXTURE, false)
		super.nativeToolkit!!.setTextureAlphaStageState(0, TextureCombineSource.PREVIOUS)
		super.nativeToolkit!!.setTexCoordIndex(0)
		super.nativeToolkit!!.setTextureSlot(0)
		super.nativeToolkit!!.setColourValue(-16777216)
		super.nativeToolkit!!.setTextureAlphaStageState(0, TextureCombineSource.CONSTANT_COLOR)
		this.onTextureMatrixChanged()
	}

	@OriginalMember(owner = "client!kl", name = "a", descriptor = "(ILclient!du;I)V")
	override fun setBaseTexture(@OriginalArg(0) combineMode: Int, @OriginalArg(1) baseTexture: TextureSampler?) {
		super.nativeToolkit!!.setTextureSampler(baseTexture)
	}
}
