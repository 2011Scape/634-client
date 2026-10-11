package com.jagex.game.runetek5.toolkit.opengl.effect;

import com.jagex.game.runetek5.graphics.gl.GlTexture;
import com.jagex.game.runetek5.graphics.gl.GlToolkit;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!vk")
public abstract class MaterialEffectGL {

	@OriginalMember(owner = "client!vk", name = "d", descriptor = "Lclient!qo;")
	public final GlToolkit toolkit;

	@OriginalMember(owner = "client!vk", name = "<init>", descriptor = "(Lclient!qo;)V")
	public MaterialEffectGL(@OriginalArg(0) GlToolkit glToolkit) {
		this.toolkit = glToolkit;
	}

	@OriginalMember(owner = "client!vk", name = "a", descriptor = "(I)V")
	public abstract void disable();

	@OriginalMember(owner = "client!vk", name = "a", descriptor = "(ZZ)V")
	public abstract void enable(@OriginalArg(0) boolean lit);

	@OriginalMember(owner = "client!vk", name = "a", descriptor = "(IIB)V")
	public abstract void setEffectParams(@OriginalArg(0) int effectParam1, @OriginalArg(1) int effectParam2);

	@OriginalMember(owner = "client!vk", name = "c", descriptor = "(I)Z")
	public abstract boolean isSupported();

	@OriginalMember(owner = "client!vk", name = "a", descriptor = "(ZB)V")
	public abstract void applyTextureCombine(@OriginalArg(0) boolean lit);

	@OriginalMember(owner = "client!vk", name = "a", descriptor = "(Lclient!ud;BI)V")
	public abstract void bindTexture(@OriginalArg(0) GlTexture texture, @OriginalArg(2) int combineMode);
}
