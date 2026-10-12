package com.jagex.game.runetek5.client.cycle

import com.jagex.game.runetek5.client.cycle.AbstractCycleManager
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.utils.Utils

@OriginalClass("client!cca")
class DefaultTickScheduler : AbstractCycleManager() {

	@OriginalMember(owner = "client!cca", name = "f", descriptor = "I")
	private var entryCount: Int = 1

	@OriginalMember(owner = "client!cca", name = "e", descriptor = "J")
	private var lastNanotime: Long = 0L

	@OriginalMember(owner = "client!cca", name = "g", descriptor = "[J")
	private val timeCache: LongArray = LongArray(10)

	@OriginalMember(owner = "client!cca", name = "d", descriptor = "J")
	private var nextCycleAt: Long = 0L

	@OriginalMember(owner = "client!cca", name = "i", descriptor = "I")
	private var cachePos: Int = 0

	@OriginalMember(owner = "client!cca", name = "h", descriptor = "J")
	private var currentCycleAt: Long = 0L

	@OriginalMember(owner = "client!cca", name = "b", descriptor = "(I)J")
	override fun getCurrentCycle(): Long {
		return this.currentCycleAt
	}

	@OriginalMember(owner = "client!cca", name = "a", descriptor = "(IJ)I")
	override fun loopsInCycle(@OriginalArg(1) preferredDuration: Long): Int {
		if (this.currentCycleAt < this.nextCycleAt) {
			this.lastNanotime += this.nextCycleAt - this.currentCycleAt
			this.currentCycleAt += this.nextCycleAt - this.currentCycleAt
			this.nextCycleAt += preferredDuration
			return 1
		}
		@Pc(45) var loops: Int = 0
		do {
			loops++
			this.nextCycleAt += preferredDuration
		} while (loops < 10 && this.currentCycleAt > this.nextCycleAt)
		if (this.nextCycleAt < this.currentCycleAt) {
			this.nextCycleAt = this.currentCycleAt
		}
		return loops
	}

	@OriginalMember(owner = "client!cca", name = "a", descriptor = "(I)J")
	override fun getIdleDurationMs(): Long {
		this.currentCycleAt += this.averageExecutionTime()
		return if (this.nextCycleAt > this.currentCycleAt) (this.nextCycleAt - this.currentCycleAt) / 1000000L else 0L
	}

	@OriginalMember(owner = "client!cca", name = "a", descriptor = "(B)V")
	override fun reset() {
		this.lastNanotime = 0L
		if (this.nextCycleAt > this.currentCycleAt) {
			this.currentCycleAt += this.nextCycleAt - this.currentCycleAt
		}
	}

	@OriginalMember(owner = "client!cca", name = "c", descriptor = "(I)J")
	private fun averageExecutionTime(): Long {
		@Pc(4) val currentNanotime: Long = Utils.currentTimeMillis() * 1000000L
		@Pc(10) val timeDelta: Long = currentNanotime - this.lastNanotime
		this.lastNanotime = currentNanotime
		if (timeDelta > -5000000000L && timeDelta < 5000000000L) {
			this.timeCache[this.cachePos] = timeDelta
			if (this.entryCount < 10) {
				this.entryCount++
			}
			this.cachePos = (this.cachePos + 1) % 10
		}
		@Pc(49) var total: Long = 0L
		@Pc(51) var i: Int = 1
		while (i <= this.entryCount) {
			total += this.timeCache[(this.cachePos + 10 - i) % 10]
			i++
		}
		return total / this.entryCount.toLong()
	}
}
