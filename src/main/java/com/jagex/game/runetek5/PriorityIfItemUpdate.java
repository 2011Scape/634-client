package com.jagex.game.runetek5;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.cache.node.lru.PulseEvent;
import com.jagex.game.runetek5.client.periodic.PulseEventHandler;

public final class PriorityIfItemUpdate {

	@OriginalMember(owner = "client!kf", name = "a", descriptor = "(BIII)V")
	public static void priorityIfItemUpdate(@OriginalArg(1) int amount, @OriginalArg(2) int itemId, @OriginalArg(3) int componentHash) {
		@Pc(8) PulseEvent event = PulseEventHandler.createPulseEvent(componentHash, 9);
		event.queueHighPriority();
		event.primary = itemId;
		event.secondary = amount;
	}
}
