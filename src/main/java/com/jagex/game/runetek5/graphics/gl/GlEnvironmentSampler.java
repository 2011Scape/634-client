package com.jagex.game.runetek5.graphics.gl;

import com.jagex.game.runetek5.EnvironmentSampler;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!dp")
public abstract class GlEnvironmentSampler extends EnvironmentSampler {

	@OriginalMember(owner = "client!dp", name = "<init>", descriptor = "()V")
	public GlEnvironmentSampler() {
	}

	@OriginalMember(owner = "client!dp", name = "a", descriptor = "(B)Lclient!q;")
	public abstract GlCubeTexture getCubeTexture();
}
