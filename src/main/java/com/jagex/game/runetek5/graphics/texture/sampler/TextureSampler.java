package com.jagex.game.runetek5.graphics.texture.sampler;

import com.jagex.game.runetek5.FilterMode;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!du")
public interface TextureSampler {

	@OriginalMember(owner = "client!du", name = "a", descriptor = "(B)V")
	public void bindTexture();

	@OriginalMember(owner = "client!du", name = "a", descriptor = "(Lclient!ria;I)V")
	public void setFilterMode(@OriginalArg(0) FilterMode mode);
}
