package com.jagex.game.runetek5;

import org.openrs2.deob.annotation.OriginalMember;

public final class EnvironmentSamplerCacheReset {

	@OriginalMember(owner = "client!fh", name = "a", descriptor = "(I)V")
	public static void reset() {
		EnvironmentSampler.aClass169_27.reset();
	}
}
