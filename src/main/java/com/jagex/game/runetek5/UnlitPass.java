package com.jagex.game.runetek5;

import com.jagex.game.runetek5.graphics.texture.sampler.TextureSampler;
import com.jagex.game.runetek5.toolkit.AbstractNativeToolkit;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!tea")
public final class UnlitPass extends RenderPass {

	@OriginalMember(owner = "client!tea", name = "<init>", descriptor = "(Lclient!wga;)V")
	public UnlitPass(@OriginalArg(0) AbstractNativeToolkit nativeToolkit) {
		super(nativeToolkit);
	}

	@OriginalMember(owner = "client!tea", name = "a", descriptor = "(IZ)V")
	@Override
	public void enable(@OriginalArg(1) boolean lit) {
		super.nativeToolkit.setForceLightsDisabled(true);
	}

	@OriginalMember(owner = "client!tea", name = "a", descriptor = "(ILclient!du;I)V")
	@Override
	public void setBaseTexture(@OriginalArg(0) int combineMode, @OriginalArg(1) TextureSampler baseTexture) {
		super.nativeToolkit.setTextureSampler(baseTexture);
		super.nativeToolkit.setColourType(combineMode);
	}

	@OriginalMember(owner = "client!tea", name = "a", descriptor = "(B)V")
	@Override
	public void disable() {
		super.nativeToolkit.setForceLightsDisabled(false);
	}

	@OriginalMember(owner = "client!tea", name = "a", descriptor = "(ZB)V")
	@Override
	public void applyTextureCombine(@OriginalArg(0) boolean lit) {
	}

	@OriginalMember(owner = "client!tea", name = "e", descriptor = "(I)Z")
	@Override
	public boolean isSupported() {
		return true;
	}

	@OriginalMember(owner = "client!tea", name = "a", descriptor = "(III)V")
	@Override
	public void setEffectParams(@OriginalArg(1) int effectParam1, @OriginalArg(2) int effectParam2) {
	}
}
