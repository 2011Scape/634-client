package com.jagex.game.runetek5.client.cycle

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.core.util.TimeUtils
import kotlin.jvm.JvmStatic
import lang.Class
import lang.getDeclaredConstructor

@OriginalClass("client!tb")
abstract class AbstractCycleManager {

	@OriginalMember(owner = "client!tb", name = "a", descriptor = "(B)V")
	abstract fun reset()

	@OriginalMember(owner = "client!tb", name = "a", descriptor = "(I)J")
	abstract fun getIdleDurationMs(): Long

	@OriginalMember(owner = "client!tb", name = "a", descriptor = "(IJ)I")
	abstract fun loopsInCycle(@OriginalArg(1) preferredDuration: Long): Int

	@OriginalMember(owner = "client!tb", name = "b", descriptor = "(IJ)I")
	fun loopCycle(@OriginalArg(1) preferredDuration: Long): Int {
		@Pc(7) val sleepTime: Long = this.getIdleDurationMs()
		if (0.toLong() < sleepTime) {
			TimeUtils.sleep(sleepTime)
		}
		return this.loopsInCycle(preferredDuration)
	}

	@OriginalMember(owner = "client!tb", name = "b", descriptor = "(I)J")
	abstract fun getCurrentCycle(): Long

	companion object {
		@OriginalMember(owner = "client!sia", name = "a", descriptor = "(Z)Lclient!tb;")
		@JvmStatic
		fun create(): AbstractCycleManager? {
			try {
				return JaclibCycleManager()
			} catch (@Pc(13) ex: Throwable) {
				try {
					return lang.forName("com.jagex.game.runetek5.client.cycle.SystemCycleManager").getDeclaredConstructor().newInstance() as AbstractCycleManager?
				} catch (@Pc(19) ex2: Throwable) {
					return DefaultTickScheduler()
				}
			}
		}
	}
}
