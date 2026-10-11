package com.jagex.game.runetek5;

import com.jagex.game.runetek5.graphics.texture.sampler.TextureSampler;
import com.jagex.game.runetek5.toolkit.AbstractNativeToolkit;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!fj")
public abstract class RenderPass {

	@OriginalMember(owner = "client!fj", name = "b", descriptor = "Lclient!wga;")
	public final AbstractNativeToolkit nativeToolkit;

	@OriginalMember(owner = "client!fj", name = "<init>", descriptor = "(Lclient!wga;)V")
	public RenderPass(@OriginalArg(0) AbstractNativeToolkit nativeToolkit) {
		this.nativeToolkit = nativeToolkit;
	}

	@OriginalMember(owner = "client!fj", name = "a", descriptor = "(B)V")
	public abstract void disable();

	@OriginalMember(owner = "client!fj", name = "a", descriptor = "(ZB)V")
	public abstract void applyTextureCombine(@OriginalArg(0) boolean lit);

	@OriginalMember(owner = "client!fj", name = "a", descriptor = "(I)V")
	public void onFogChanged() {
	}

	@OriginalMember(owner = "client!fj", name = "b", descriptor = "(B)V")
	public void onModelMatrixChanged() {
	}

	@OriginalMember(owner = "client!fj", name = "b", descriptor = "(I)V")
	public void onProjectionChanged() {
	}

	@OriginalMember(owner = "client!fj", name = "a", descriptor = "(IZ)V")
	public abstract void enable(@OriginalArg(1) boolean lit);

	@OriginalMember(owner = "client!fj", name = "a", descriptor = "(III)V")
	public abstract void setEffectParams(@OriginalArg(1) int effectParam1, @OriginalArg(2) int effectParam2);

	@OriginalMember(owner = "client!fj", name = "a", descriptor = "(ILclient!du;I)V")
	public abstract void setBaseTexture(@OriginalArg(0) int combineMode, @OriginalArg(1) TextureSampler baseTexture);

	@OriginalMember(owner = "client!fj", name = "c", descriptor = "(I)V")
	public void onUnderwaterSettingsChanged() {
	}

	@OriginalMember(owner = "client!fj", name = "e", descriptor = "(I)Z")
	public abstract boolean isSupported();

	@OriginalMember(owner = "client!fj", name = "a", descriptor = "(Z)V")
	public void onCameraChanged() {
	}

	@OriginalMember(owner = "client!fj", name = "f", descriptor = "(I)V")
	public void onTextureMatrixChanged() {
	}
}
