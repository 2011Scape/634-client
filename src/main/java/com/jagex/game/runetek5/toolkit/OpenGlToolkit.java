package com.jagex.game.runetek5.toolkit;

import org.openrs2.deob.annotation.OriginalMember;

public final class OpenGlToolkit {
	@OriginalMember(owner = "client!cha", name = "e", descriptor = "[F")
	public static final float[] LIGHT_MATRIX = new float[4];

	@OriginalMember(owner = "client!sfa", name = "e", descriptor = "[F")
	public static final float[] BUFFER_4F = new float[4];

	private OpenGlToolkit() {
		/* empty */
	}
}
