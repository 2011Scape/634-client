package com.jagex.game.runetek5.client.cycle;

import com.jagex.game.runetek5.client.cycle.AbstractCycleManager;
import jagex3.jagmisc.jagmisc;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!md")
public final class JaclibCycleManager extends AbstractCycleManager {

	@OriginalMember(owner = "client!md", name = "e", descriptor = "J")
	private long currentCycleAt = 0L;

	@OriginalMember(owner = "client!md", name = "d", descriptor = "J")
	private long nextCycleAt = 0L;

	@OriginalMember(owner = "client!md", name = "g", descriptor = "I")
	private int cachePos = 0;

	@OriginalMember(owner = "client!md", name = "f", descriptor = "J")
	private long lastNanotime = 0L;

	@OriginalMember(owner = "client!md", name = "h", descriptor = "[J")
	private final long[] timeCache = new long[10];

	@OriginalMember(owner = "client!md", name = "i", descriptor = "I")
	private int entryCount = 1;

	@OriginalMember(owner = "client!md", name = "<init>", descriptor = "()V")
	public JaclibCycleManager() {
		this.nextCycleAt = this.currentCycleAt = jagmisc.nanoTime();
		if (this.currentCycleAt == 0L) {
			throw new RuntimeException();
		}
	}

	@OriginalMember(owner = "client!md", name = "c", descriptor = "(I)J")
	private long averageExecutionTime() {
		@Pc(1) long currentNanotime = jagmisc.nanoTime();
		@Pc(7) long timeDelta = currentNanotime - this.lastNanotime;
		this.lastNanotime = currentNanotime;
		if (timeDelta > -5000000000L && timeDelta < 5000000000L) {
			this.timeCache[this.cachePos] = timeDelta;
			if (this.entryCount < 1) {
				this.entryCount++;
			}
			this.cachePos = (this.cachePos + 1) % 10;
		}
		@Pc(48) long total = 0L;
		for (@Pc(57) int i = 1; i <= this.entryCount; i++) {
			total += this.timeCache[(this.cachePos + 10 - i) % 10];
		}
		return total / (long) this.entryCount;
	}

	@OriginalMember(owner = "client!md", name = "a", descriptor = "(I)J")
	@Override
	public long getIdleDurationMs() {
		this.currentCycleAt += this.averageExecutionTime();
		return this.currentCycleAt < this.nextCycleAt ? (this.nextCycleAt - this.currentCycleAt) / 1000000L : 0L;
	}

	@OriginalMember(owner = "client!md", name = "a", descriptor = "(B)V")
	@Override
	public void reset() {
		this.lastNanotime = 0L;
		if (this.nextCycleAt > this.currentCycleAt) {
			this.currentCycleAt += this.nextCycleAt - this.currentCycleAt;
		}
	}

	@OriginalMember(owner = "client!md", name = "b", descriptor = "(I)J")
	@Override
	public long getCurrentCycle() {
		return this.currentCycleAt;
	}

	@OriginalMember(owner = "client!md", name = "a", descriptor = "(IJ)I")
	@Override
	public int loopsInCycle(@OriginalArg(1) long preferredDuration) {
		if (this.currentCycleAt < this.nextCycleAt) {
			this.lastNanotime += this.nextCycleAt - this.currentCycleAt;
			this.currentCycleAt += this.nextCycleAt - this.currentCycleAt;
			this.nextCycleAt += preferredDuration;
			return 1;
		}
		@Pc(45) int loops = 0;
		do {
			loops++;
			this.nextCycleAt += preferredDuration;
		} while (loops < 10 && this.currentCycleAt > this.nextCycleAt);
		if (this.currentCycleAt > this.nextCycleAt) {
			this.nextCycleAt = this.currentCycleAt;
		}
		return loops;
	}
}
