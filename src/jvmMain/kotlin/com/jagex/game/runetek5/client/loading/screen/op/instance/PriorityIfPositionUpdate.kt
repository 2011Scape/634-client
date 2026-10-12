package com.jagex.game.runetek5.client.loading.screen.op.instance

import com.jagex.game.runetek5.cache.node.lru.PulseEvent
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.client.periodic.PulseEventHandler

object PriorityIfPositionUpdate {

	@OriginalMember(owner = "client!nfa", name = "a", descriptor = "(IIII)V")
	@JvmStatic
	fun priorityIfPositionUpdate(@OriginalArg(0) x: Int, @OriginalArg(2) y: Int, @OriginalArg(3) componentHash: Int) {
		@Pc(12) val pulseEvent: PulseEvent? = PulseEventHandler.createPulseEvent(componentHash, 11)
		pulseEvent!!.queueHighPriority()
		pulseEvent.primary = x
		pulseEvent.secondary = y
	}
}
