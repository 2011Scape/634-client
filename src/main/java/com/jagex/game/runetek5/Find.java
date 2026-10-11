package com.jagex.game.runetek5;

import org.openrs2.deob.annotation.OriginalMember;

public final class Find {

	@OriginalMember(owner = "client!kda", name = "Wb", descriptor = "I")
	public static int menuBottomBorderSpriteId;
	@OriginalMember(owner = "client!ks", name = "h", descriptor = "I")
	public static int findResultCount;

	@OriginalMember(owner = "client!qq", name = "h", descriptor = "[S")
	public static short[] queryResults;
}
