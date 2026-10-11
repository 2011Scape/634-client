package com.jagex.game.runetek5;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

public final class LastRedrawTime {

	@OriginalMember(owner = "client!kw", name = "I", descriptor = "J")
	public static long lastRedrawTimeMillis = 0L;
}
