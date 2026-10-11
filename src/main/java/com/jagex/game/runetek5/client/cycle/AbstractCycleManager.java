package com.jagex.game.runetek5.client.cycle;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.core.util.TimeUtils;

@OriginalClass("client!tb")
public abstract class AbstractCycleManager {

	@OriginalMember(owner = "client!tb", name = "a", descriptor = "(B)V")
	public abstract void reset();

	@OriginalMember(owner = "client!tb", name = "a", descriptor = "(I)J")
	public abstract long getIdleDurationMs();

	@OriginalMember(owner = "client!tb", name = "a", descriptor = "(IJ)I")
	public abstract int loopsInCycle(@OriginalArg(1) long preferredDuration);

	@OriginalMember(owner = "client!tb", name = "b", descriptor = "(IJ)I")
	public final int loopCycle(@OriginalArg(1) long preferredDuration) {
		@Pc(7) long sleepTime = this.getIdleDurationMs();
		if ((long) 0 < sleepTime) {
			TimeUtils.sleep(sleepTime);
		}
		return this.loopsInCycle(preferredDuration);
	}

	@OriginalMember(owner = "client!tb", name = "b", descriptor = "(I)J")
	public abstract long getCurrentCycle();
	@OriginalMember(owner = "client!sia", name = "a", descriptor = "(Z)Lclient!tb;")
	public static AbstractCycleManager create() {
		try {
			return new JaclibCycleManager();
		} catch (@Pc(13) Throwable ex) {
			try {
				return (AbstractCycleManager) Class.forName("com.jagex.game.runetek5.client.cycle.SystemCycleManager").getDeclaredConstructor().newInstance();
			} catch (@Pc(19) Throwable ex2) {
				return new DefaultTickScheduler();
			}
		}
	}
}
