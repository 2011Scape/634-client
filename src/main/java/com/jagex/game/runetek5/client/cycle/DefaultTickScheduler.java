package com.jagex.game.runetek5.client.cycle;

import com.jagex.game.runetek5.client.cycle.AbstractCycleManager;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.utils.Utils;

@OriginalClass("client!cca")
public final class DefaultTickScheduler extends AbstractCycleManager {

	@OriginalMember(owner = "client!cca", name = "f", descriptor = "I")
	private int entryCount = 1;

	@OriginalMember(owner = "client!cca", name = "e", descriptor = "J")
	private long lastNanotime = 0L;

	@OriginalMember(owner = "client!cca", name = "g", descriptor = "[J")
	private final long[] timeCache = new long[10];

	@OriginalMember(owner = "client!cca", name = "d", descriptor = "J")
	private long nextCycleAt = 0L;

	@OriginalMember(owner = "client!cca", name = "i", descriptor = "I")
	private int cachePos = 0;

	@OriginalMember(owner = "client!cca", name = "h", descriptor = "J")
	private long currentCycleAt = 0L;

	@OriginalMember(owner = "client!cca", name = "b", descriptor = "(I)J")
	@Override
	public long getCurrentCycle() {
		return this.currentCycleAt;
	}

	@OriginalMember(owner = "client!cca", name = "a", descriptor = "(IJ)I")
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
		if (this.nextCycleAt < this.currentCycleAt) {
			this.nextCycleAt = this.currentCycleAt;
		}
		return loops;
	}

	@OriginalMember(owner = "client!cca", name = "a", descriptor = "(I)J")
	@Override
	public long getIdleDurationMs() {
		this.currentCycleAt += this.averageExecutionTime();
		return this.nextCycleAt > this.currentCycleAt ? (this.nextCycleAt - this.currentCycleAt) / 1000000L : 0L;
	}

	@OriginalMember(owner = "client!cca", name = "a", descriptor = "(B)V")
	@Override
	public void reset() {
		this.lastNanotime = 0L;
		if (this.nextCycleAt > this.currentCycleAt) {
			this.currentCycleAt += this.nextCycleAt - this.currentCycleAt;
		}
	}

	@OriginalMember(owner = "client!cca", name = "c", descriptor = "(I)J")
	private long averageExecutionTime() {
		@Pc(4) long currentNanotime = Utils.currentTimeMillis() * 1000000L;
		@Pc(10) long timeDelta = currentNanotime - this.lastNanotime;
		this.lastNanotime = currentNanotime;
		if (timeDelta > -5000000000L && timeDelta < 5000000000L) {
			this.timeCache[this.cachePos] = timeDelta;
			if (this.entryCount < 10) {
				this.entryCount++;
			}
			this.cachePos = (this.cachePos + 1) % 10;
		}
		@Pc(49) long total = 0L;
		for (@Pc(51) int i = 1; i <= this.entryCount; i++) {
			total += this.timeCache[(this.cachePos + 10 - i) % 10];
		}
		return total / (long) this.entryCount;
	}
}
