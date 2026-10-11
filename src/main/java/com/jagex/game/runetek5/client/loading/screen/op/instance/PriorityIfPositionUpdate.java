package com.jagex.game.runetek5.client.loading.screen.op.instance;

import com.jagex.game.runetek5.cache.node.lru.PulseEvent;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.periodic.PulseEventHandler;

public final class PriorityIfPositionUpdate {

	@OriginalMember(owner = "client!nfa", name = "a", descriptor = "(IIII)V")
	public static void priorityIfPositionUpdate(@OriginalArg(0) int x, @OriginalArg(2) int y, @OriginalArg(3) int componentHash) {
		@Pc(12) PulseEvent pulseEvent = PulseEventHandler.createPulseEvent(componentHash, 11);
		pulseEvent.queueHighPriority();
		pulseEvent.primary = x;
		pulseEvent.secondary = y;
	}
}
