package com.jagex.game.runetek5.graphics;

import org.openrs2.deob.annotation.OriginalMember;

public final class EnvironmentLight {

	@OriginalMember(owner = "client!hr", name = "Q", descriptor = "I")
	public static int textureOpWidth;

	@OriginalMember(owner = "client!ql", name = "q", descriptor = "[I")
	public static int[] horizontalTable;
}
