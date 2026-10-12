package com.jagex.game.runetek5

import com.jagex.game.runetek5.cache.node.lru.PulseEvent
import com.jagex.game.runetek5.client.periodic.PulseEventHandler
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

object PriorityIfItemUpdate {

	@OriginalMember(owner = "client!kf", name = "a", descriptor = "(BIII)V")
	@JvmStatic
	fun priorityIfItemUpdate(@OriginalArg(1) amount: Int, @OriginalArg(2) itemId: Int, @OriginalArg(3) componentHash: Int) {
		@Pc(8) val event: PulseEvent? = PulseEventHandler.createPulseEvent(componentHash, 9)
		event!!.queueHighPriority()
		event!!.primary = itemId
		event!!.secondary = amount
	}
}
