package com.jagex.game.runetek5.statics;

import org.openrs2.deob.annotation.OriginalMember;

public final class PulseTimestampRing {

	@OriginalMember(owner = "client!dr", name = "h", descriptor = "[J")
	public static final long[] PULSE_TIMES = new long[32];
	@OriginalMember(owner = "client!tda", name = "e", descriptor = "I")
	public static int pulseTimesIndex;
}
