package com.jagex.game.runetek5.client.cycle

import com.jagex.game.runetek5.client.cycle.AbstractCycleManager
import jagex3.jagmisc.jagmisc
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!md")
class JaclibCycleManager @OriginalMember(owner = "client!md", name = "<init>", descriptor = "()V") constructor() : AbstractCycleManager() {

	@OriginalMember(owner = "client!md", name = "e", descriptor = "J")
	private var currentCycleAt: Long = 0L

	@OriginalMember(owner = "client!md", name = "d", descriptor = "J")
	private var nextCycleAt: Long = 0L

	@OriginalMember(owner = "client!md", name = "g", descriptor = "I")
	private var cachePos: Int = 0

	@OriginalMember(owner = "client!md", name = "f", descriptor = "J")
	private var lastNanotime: Long = 0L

	@OriginalMember(owner = "client!md", name = "h", descriptor = "[J")
	private val timeCache: LongArray = LongArray(10)

	@OriginalMember(owner = "client!md", name = "i", descriptor = "I")
	private var entryCount: Int = 1

	init {
		this.currentCycleAt = jagmisc.nanoTime()
		this.nextCycleAt = this.currentCycleAt
		if (this.currentCycleAt == 0L) {
			throw RuntimeException()
		}
	}

	@OriginalMember(owner = "client!md", name = "c", descriptor = "(I)J")
	private fun averageExecutionTime(): Long {
		@Pc(1) val currentNanotime: Long = jagmisc.nanoTime()
		@Pc(7) val timeDelta: Long = currentNanotime - this.lastNanotime
		this.lastNanotime = currentNanotime
		if (timeDelta > -5000000000L && timeDelta < 5000000000L) {
			this.timeCache[this.cachePos] = timeDelta
			if (this.entryCount < 1) {
				this.entryCount++
			}
			this.cachePos = (this.cachePos + 1) % 10
		}
		@Pc(48) var total: Long = 0L
		@Pc(57) var i: Int = 1
		while (i <= this.entryCount) {
			total += this.timeCache[(this.cachePos + 10 - i) % 10]
			i++
		}
		return total / this.entryCount.toLong()
	}

	@OriginalMember(owner = "client!md", name = "a", descriptor = "(I)J")
	override fun getIdleDurationMs(): Long {
		this.currentCycleAt += this.averageExecutionTime()
		return if (this.currentCycleAt < this.nextCycleAt) (this.nextCycleAt - this.currentCycleAt) / 1000000L else 0L
	}

	@OriginalMember(owner = "client!md", name = "a", descriptor = "(B)V")
	override fun reset() {
		this.lastNanotime = 0L
		if (this.nextCycleAt > this.currentCycleAt) {
			this.currentCycleAt += this.nextCycleAt - this.currentCycleAt
		}
	}

	@OriginalMember(owner = "client!md", name = "b", descriptor = "(I)J")
	override fun getCurrentCycle(): Long {
		return this.currentCycleAt
	}

	@OriginalMember(owner = "client!md", name = "a", descriptor = "(IJ)I")
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
		if (this.currentCycleAt > this.nextCycleAt) {
			this.nextCycleAt = this.currentCycleAt
		}
		return loops
	}
}
