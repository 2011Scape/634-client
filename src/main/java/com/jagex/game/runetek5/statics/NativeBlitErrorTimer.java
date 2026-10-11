package com.jagex.game.runetek5.statics;

import org.openrs2.deob.annotation.OriginalMember;

public final class NativeBlitErrorTimer {

	@OriginalMember(owner = "client!p", name = "q", descriptor = "J")
	public static long firstErrorTime;

	@OriginalMember(owner = "client!p", name = "o", descriptor = "Z")
	public static boolean blitFailed = false;
}
