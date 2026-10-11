package com.jagex.game.runetek5.client.event.mouse;

import com.jagex.game.runetek5.cache.node.lru.PulseEvent;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.periodic.PulseEventHandler;

public final class DelayedIfTextUpdate {

	@OriginalMember(owner = "client!qd", name = "c", descriptor = "(II)V")
	public static void delayedIfTextUpdate(@OriginalArg(1) int idHash) {
		@Pc(8) PulseEvent pulseEvent = PulseEventHandler.createPulseEvent(idHash, 3);
		pulseEvent.queueLowPriority();
	}
}
