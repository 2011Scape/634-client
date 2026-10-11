package com.jagex.game.runetek5;

import com.jagex.game.runetek5.graphics.texture.sampler.TextureSampler;
import com.jagex.game.runetek5.toolkit.AbstractNativeToolkit;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.toolkit.companion.TextureCombineMode;

@OriginalClass("client!ld")
public final class FixedFunctionEnvironmentMapPass extends RenderPass {

	@OriginalMember(owner = "client!ld", name = "m", descriptor = "Z")
	private boolean active = false;

	@OriginalMember(owner = "client!ld", name = "<init>", descriptor = "(Lclient!wga;)V")
	public FixedFunctionEnvironmentMapPass(@OriginalArg(0) AbstractNativeToolkit nativeToolkit) {
		super(nativeToolkit);
	}

	@OriginalMember(owner = "client!ld", name = "a", descriptor = "(ILclient!du;I)V")
	@Override
	public void setBaseTexture(@OriginalArg(0) int colourType, @OriginalArg(1) TextureSampler baseTexture) {
		super.nativeToolkit.setTextureSampler(baseTexture);
		super.nativeToolkit.setColourType(colourType);
	}

	@OriginalMember(owner = "client!ld", name = "a", descriptor = "(IZ)V")
	@Override
	public void enable(@OriginalArg(1) boolean lit) {
		@Pc(8) CubeTextureSampler envMap = super.nativeToolkit.getEnvironmentMapSampler();
		if (envMap == null || !lit) {
			super.nativeToolkit.setTextureAlphaStageState(0, TextureCombineSource.PRIMARY_COLOR);
			return;
		}
		super.nativeToolkit.setTextureSlot(1);
		super.nativeToolkit.setTextureSampler(envMap);
		super.nativeToolkit.setTexCoordGeneration(TexCoordGenMode.aClass31_4);
		super.nativeToolkit.setTextureSlot(1);
		super.nativeToolkit.setTextureCombineModes(TextureCombineFunction.aClass315_4, TextureCombineFunction.aClass315_5);
		super.nativeToolkit.setTextureColourSource(true, 2, TextureCombineSource.PREVIOUS, false);
		super.nativeToolkit.setTextureAlphaStageState(0, TextureCombineSource.PRIMARY_COLOR);
		@Pc(67) NativeMatrix textureMatrix = super.nativeToolkit.getCurrentTextureMatrix();
		textureMatrix.copyRotation(super.nativeToolkit.getInverseSunMatrix44());
		super.nativeToolkit.setTextureState(TextureTransformMode.aClass332_2);
		super.nativeToolkit.setTextureSlot(0);
		this.active = true;
	}

	@OriginalMember(owner = "client!ld", name = "a", descriptor = "(B)V")
	@Override
	public void disable() {
		if (this.active) {
			super.nativeToolkit.setTextureSlot(1);
			super.nativeToolkit.setTexCoordGeneration(TexCoordGenMode.aClass31_3);
			super.nativeToolkit.setTextureCombineModes(TextureCombineMode.MODULATE, TextureCombineMode.MODULATE);
			super.nativeToolkit.setTextureRgbStageState(2, TextureCombineSource.CONSTANT_COLOR);
			super.nativeToolkit.setTextureAlphaStageState(0, TextureCombineSource.TEXTURE);
			super.nativeToolkit.resetCurrentTextureMatrix();
			super.nativeToolkit.setTextureSampler(null);
			super.nativeToolkit.setTextureSlot(0);
			this.active = false;
		} else {
			super.nativeToolkit.setTextureAlphaStageState(0, TextureCombineSource.TEXTURE);
		}
		super.nativeToolkit.setTextureCombineModes(TextureCombineMode.MODULATE, TextureCombineMode.MODULATE);
	}

	@OriginalMember(owner = "client!ld", name = "a", descriptor = "(III)V")
	@Override
	public void setEffectParams(@OriginalArg(1) int effectParam1, @OriginalArg(2) int effectParam2) {
	}

	@OriginalMember(owner = "client!ld", name = "e", descriptor = "(I)Z")
	@Override
	public boolean isSupported() {
		return true;
	}

	@OriginalMember(owner = "client!ld", name = "a", descriptor = "(ZB)V")
	@Override
	public void applyTextureCombine(@OriginalArg(0) boolean lit) {
		super.nativeToolkit.setTextureCombineModes(TextureCombineFunction.aClass315_4, TextureCombineMode.MODULATE);
	}
}
