package com.jagex.game.runetek5.graphics

import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.EnvironmentSampler
import com.jagex.game.runetek5.CubeTextureSampler

@OriginalClass("client!vc")
abstract class GpuEnvironmentSampler @OriginalMember(owner = "client!vc", name = "<init>", descriptor = "()V") constructor() : EnvironmentSampler() {

	@OriginalMember(owner = "client!vc", name = "a", descriptor = "(Z)Lclient!nj;")
	abstract fun getNativeCubeTexture(): CubeTextureSampler?
}
