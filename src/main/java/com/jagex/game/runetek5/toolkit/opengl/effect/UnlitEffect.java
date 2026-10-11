package com.jagex.game.runetek5.toolkit.opengl.effect;

import com.jagex.game.runetek5.graphics.gl.GlTexture;
import com.jagex.game.runetek5.graphics.gl.GlToolkit;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!di")
public final class UnlitEffect extends MaterialEffectGL {

	@OriginalMember(owner = "client!di", name = "<init>", descriptor = "(Lclient!qo;)V")
	public UnlitEffect(@OriginalArg(0) GlToolkit glToolkit) {
		super(glToolkit);
	}

	@OriginalMember(owner = "client!di", name = "a", descriptor = "(I)V")
	@Override
	public void disable() {
		super.toolkit.setHideLights(false);
	}

	@OriginalMember(owner = "client!di", name = "a", descriptor = "(Lclient!ud;BI)V")
	@Override
	public void bindTexture(@OriginalArg(0) GlTexture texture, @OriginalArg(2) int combineMode) {
		super.toolkit.setActiveTexture(texture);
		super.toolkit.setColourOp(combineMode);
	}

	@OriginalMember(owner = "client!di", name = "a", descriptor = "(IIB)V")
	@Override
	public void setEffectParams(@OriginalArg(0) int effectParam1, @OriginalArg(1) int effectParam2) {
	}

	@OriginalMember(owner = "client!di", name = "c", descriptor = "(I)Z")
	@Override
	public boolean isSupported() {
		return true;
	}

	@OriginalMember(owner = "client!di", name = "a", descriptor = "(ZZ)V")
	@Override
	public void enable(@OriginalArg(0) boolean lit) {
		super.toolkit.setHideLights(true);
	}

	@OriginalMember(owner = "client!di", name = "a", descriptor = "(ZB)V")
	@Override
	public void applyTextureCombine(@OriginalArg(0) boolean lit) {
	}
}
