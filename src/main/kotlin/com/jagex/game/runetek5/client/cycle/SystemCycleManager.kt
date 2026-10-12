package com.jagex.game.runetek5.client.cycle

import com.jagex.game.runetek5.client.cycle.AbstractCycleManager
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!oq")
class SystemCycleManager @OriginalMember(owner = "client!oq", name = "<init>", descriptor = "()V") constructor() : AbstractCycleManager() {

	@OriginalMember(owner = "client!oq", name = "d", descriptor = "J")
	private var currentCycleAt: Long = 0L

	@OriginalMember(owner = "client!oq", name = "e", descriptor = "J")
	private var nextCycleAt: Long = 0L

	@OriginalMember(owner = "client!oq", name = "g", descriptor = "J")
	private var lastNanotime: Long = 0L

	@OriginalMember(owner = "client!oq", name = "h", descriptor = "I")
	private var cachePos: Int = 0

	@OriginalMember(owner = "client!oq", name = "f", descriptor = "I")
	private var entryCount: Int = 1

	@OriginalMember(owner = "client!oq", name = "i", descriptor = "[J")
	private val timeCache: LongArray = LongArray(10)

	init {
		this.currentCycleAt = System.nanoTime()
		this.nextCycleAt = System.nanoTime()
	}

	@OriginalMember(owner = "client!oq", name = "a", descriptor = "(I)J")
	override fun getIdleDurationMs(): Long {
		this.currentCycleAt += this.averageExecutionTime()
		return if (this.nextCycleAt > this.currentCycleAt) (this.nextCycleAt - this.currentCycleAt) / 1000000L else 0L
	}

	@OriginalMember(owner = "client!oq", name = "a", descriptor = "(B)V")
	override fun reset() {
		this.lastNanotime = 0L
		if (this.currentCycleAt < this.nextCycleAt) {
			this.currentCycleAt += this.nextCycleAt - this.currentCycleAt
		}
	}

	@OriginalMember(owner = "client!oq", name = "c", descriptor = "(I)J")
	private fun averageExecutionTime(): Long {
		@Pc(1) val currentNanotime: Long = System.nanoTime()
		@Pc(7) val timeDelta: Long = currentNanotime - this.lastNanotime
		this.lastNanotime = currentNanotime
		if (timeDelta > -5000000000L && timeDelta < 5000000000L) {
			this.timeCache[this.cachePos] = timeDelta
			if (this.entryCount < 1) {
				this.entryCount++
			}
			this.cachePos = (this.cachePos + 1) % 10
		}
		@Pc(56) var total: Long = 0L
		@Pc(58) var i: Int = 1
		while (i <= this.entryCount) {
			total += this.timeCache[(this.cachePos + 10 - i) % 10]
			i++
		}
		return total / this.entryCount.toLong()
	}

	@OriginalMember(owner = "client!oq", name = "a", descriptor = "(IJ)I")
	override fun loopsInCycle(@OriginalArg(1) preferredDuration: Long): Int {
		if (this.nextCycleAt > this.currentCycleAt) {
			this.lastNanotime += this.nextCycleAt - this.currentCycleAt
			this.currentCycleAt += this.nextCycleAt - this.currentCycleAt
			this.nextCycleAt += preferredDuration
			return 1
		}
		@Pc(12) var loops: Int = 0
		do {
			loops++
			this.nextCycleAt += preferredDuration
		} while (loops < 10 && this.currentCycleAt > this.nextCycleAt)
		if (this.nextCycleAt < this.currentCycleAt) {
			this.nextCycleAt = this.currentCycleAt
		}
		return loops
	}

	@OriginalMember(owner = "client!oq", name = "b", descriptor = "(I)J")
	override fun getCurrentCycle(): Long {
		return this.currentCycleAt
	}
}
