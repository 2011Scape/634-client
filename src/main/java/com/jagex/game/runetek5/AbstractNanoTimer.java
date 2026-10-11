package com.jagex.game.runetek5;

import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!io")
public abstract class AbstractNanoTimer {

	@OriginalMember(owner = "client!io", name = "b", descriptor = "(I)J")
	public abstract long nanoTime();
	@OriginalMember(owner = "client!bea", name = "d", descriptor = "(I)Lclient!io;")
	public static AbstractNanoTimer create() {
		try {
			return (AbstractNanoTimer) Class.forName("com.jagex.game.runetek5.NanoTimer").getDeclaredConstructor().newInstance();
		} catch (@Pc(9) Throwable ex) {
			return new MillisNanoTimer();
		}
	}
}
