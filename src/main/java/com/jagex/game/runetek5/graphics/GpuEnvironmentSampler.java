package com.jagex.game.runetek5.graphics;

import com.jagex.game.runetek5.EnvironmentSampler;
import com.jagex.game.runetek5.CubeTextureSampler;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!vc")
public abstract class GpuEnvironmentSampler extends EnvironmentSampler {

	@OriginalMember(owner = "client!vc", name = "<init>", descriptor = "()V")
	public GpuEnvironmentSampler() {
	}

	@OriginalMember(owner = "client!vc", name = "a", descriptor = "(Z)Lclient!nj;")
	public abstract CubeTextureSampler getNativeCubeTexture();
}
