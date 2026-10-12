package com.jagex.game.runetek5.client.event.mouse

import com.jagex.game.runetek5.cache.node.lru.PulseEvent
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.client.periodic.PulseEventHandler

object DelayedIfTextUpdate {

	@OriginalMember(owner = "client!qd", name = "c", descriptor = "(II)V")
	@JvmStatic
	fun delayedIfTextUpdate(@OriginalArg(1) idHash: Int) {
		@Pc(8) val pulseEvent: PulseEvent? = PulseEventHandler.createPulseEvent(idHash, 3)
		pulseEvent!!.queueLowPriority()
	}
}
