package com.jagex.game.runetek5.graphics.gl

import com.jagex.game.runetek5.EnvironmentSampler
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!dp")
abstract class GlEnvironmentSampler : EnvironmentSampler {

	@OriginalMember(owner = "client!dp", name = "<init>", descriptor = "()V")
	constructor() : super()

	@OriginalMember(owner = "client!dp", name = "a", descriptor = "(B)Lclient!q;")
	abstract fun getCubeTexture(): GlCubeTexture?
}
