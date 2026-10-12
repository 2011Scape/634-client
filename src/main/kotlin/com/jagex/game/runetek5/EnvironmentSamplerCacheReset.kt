package com.jagex.game.runetek5

import org.openrs2.deob.annotation.OriginalMember

object EnvironmentSamplerCacheReset {

	@OriginalMember(owner = "client!fh", name = "a", descriptor = "(I)V")
	@JvmStatic
	fun reset() {
		EnvironmentSampler.aClass169_27.reset()
	}
}
