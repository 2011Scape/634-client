package com.jagex.game.runetek5;

import com.jagex.game.runetek5.graphics.texture.sampler.TextureSampler;
import com.jagex.game.runetek5.toolkit.AbstractNativeToolkit;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.graphics.texture.water.NativeWaterTexture;
import com.jagex.game.runetek5.toolkit.companion.TextureCombineMode;

@OriginalClass("client!kl")
public final class FixedFunctionWaterPass extends RenderPass {

	@OriginalMember(owner = "client!kl", name = "j", descriptor = "F")
	private float wavePhase = 0.0F;

	@OriginalMember(owner = "client!kl", name = "m", descriptor = "Lclient!ur;")
	private final NativeWaterTexture water;

	@OriginalMember(owner = "client!kl", name = "<init>", descriptor = "(Lclient!wga;Lclient!ur;)V")
	public FixedFunctionWaterPass(@OriginalArg(0) AbstractNativeToolkit nativeToolkit, @OriginalArg(1) NativeWaterTexture water) {
		super(nativeToolkit);
		this.water = water;
	}

	@OriginalMember(owner = "client!kl", name = "a", descriptor = "(B)V")
	@Override
	public void disable() {
		super.nativeToolkit.setTextureSlot(1);
		super.nativeToolkit.setTextureCombineModes(TextureCombineMode.MODULATE, TextureCombineMode.MODULATE);
		super.nativeToolkit.setTextureRgbStageState(0, TextureCombineSource.TEXTURE);
		super.nativeToolkit.setTextureAlphaStageState(0, TextureCombineSource.TEXTURE);
		super.nativeToolkit.setTexCoordIndex(1);
		super.nativeToolkit.setTextureSampler(null);
		super.nativeToolkit.setTextureSlot(0);
		super.nativeToolkit.setTextureAlphaStageState(0, TextureCombineSource.TEXTURE);
	}

	@OriginalMember(owner = "client!kl", name = "a", descriptor = "(III)V")
	@Override
	public void setEffectParams(@OriginalArg(1) int effectParam1, @OriginalArg(2) int effectParam2) {
		super.nativeToolkit.setTextureSlot(1);
		if ((effectParam1 & 0x80) != 0) {
			super.nativeToolkit.setTextureSampler(null);
		} else if ((effectParam2 & 0x1) == 1) {
			if (this.water.hasDepth) {
				this.wavePhase = (float) (super.nativeToolkit.animatingCycle % 4000) / 4000.0F;
				super.nativeToolkit.setTextureSampler(this.water.waterSampler3d);
			} else {
				@Pc(87) int frame = super.nativeToolkit.animatingCycle % 4000 * 16 / 4000;
				super.nativeToolkit.setTextureSampler(this.water.waterTextureSamplers[frame]);
			}
		} else if (this.water.hasDepth) {
			super.nativeToolkit.setTextureSampler(this.water.waterSampler3d);
		} else {
			super.nativeToolkit.setTextureSampler(this.water.waterTextureSamplers[0]);
		}
		super.nativeToolkit.setTextureSlot(0);
	}

	@OriginalMember(owner = "client!kl", name = "e", descriptor = "(I)Z")
	@Override
	public boolean isSupported() {
		return this.water.readyToSample();
	}

	@OriginalMember(owner = "client!kl", name = "f", descriptor = "(I)V")
	@Override
	public void onTextureMatrixChanged() {
		if (super.nativeToolkit.getCurrentTextureSlot() != 0) {
			return;
		}
		@Pc(12) NativeMatrix baseMatrix = super.nativeToolkit.getTextureMatrix();
		super.nativeToolkit.setTextureSlot(1);
		@Pc(24) NativeMatrix waveMatrix = super.nativeToolkit.getCurrentTextureMatrix();
		waveMatrix.apply(baseMatrix);
		waveMatrix.scale(0.125F, 1.0F, 0.125F);
		waveMatrix.translate(0.0F, this.wavePhase, 0.0F);
		super.nativeToolkit.setTextureState(TextureTransformMode.aClass332_2);
		super.nativeToolkit.setTextureSlot(0);
	}

	@OriginalMember(owner = "client!kl", name = "a", descriptor = "(ZB)V")
	@Override
	public void applyTextureCombine(@OriginalArg(0) boolean lit) {
		super.nativeToolkit.setTextureCombineModes(TextureCombineFunction.aClass315_4, TextureCombineMode.MODULATE);
	}

	@OriginalMember(owner = "client!kl", name = "a", descriptor = "(IZ)V")
	@Override
	public void enable(@OriginalArg(1) boolean lit) {
		super.nativeToolkit.setTextureSlot(1);
		super.nativeToolkit.setTextureCombineModes(TextureCombineFunction.aClass315_4, TextureCombineFunction.aClass315_2);
		super.nativeToolkit.setTextureColourSource(true, 0, TextureCombineSource.TEXTURE, false);
		super.nativeToolkit.setTextureAlphaStageState(0, TextureCombineSource.PREVIOUS);
		super.nativeToolkit.setTexCoordIndex(0);
		super.nativeToolkit.setTextureSlot(0);
		super.nativeToolkit.setColourValue(-16777216);
		super.nativeToolkit.setTextureAlphaStageState(0, TextureCombineSource.CONSTANT_COLOR);
		this.onTextureMatrixChanged();
	}

	@OriginalMember(owner = "client!kl", name = "a", descriptor = "(ILclient!du;I)V")
	@Override
	public void setBaseTexture(@OriginalArg(0) int combineMode, @OriginalArg(1) TextureSampler baseTexture) {
		super.nativeToolkit.setTextureSampler(baseTexture);
	}
}
