package com.jagex.game.runetek5;

import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!ne")
public final class NanoTimer extends AbstractNanoTimer {

	@OriginalMember(owner = "client!ne", name = "<init>", descriptor = "()V")
	public NanoTimer() {
		System.nanoTime();
	}

	@OriginalMember(owner = "client!ne", name = "b", descriptor = "(I)J")
	@Override
	public long nanoTime() {
		return System.nanoTime();
	}
}
