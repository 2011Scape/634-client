package com.jagex.game.runetek5.client.periodic;

import com.jagex.game.runetek5.cache.node.Queue;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.cache.node.lru.PulseEvent;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.utils.Utils;

public final class PulseEventHandler {
	@OriginalMember(owner = "client!bk", name = "z", descriptor = "Lclient!mn;")
	public static final Queue HIGH_PRIORITY = new Queue();

	@OriginalMember(owner = "client!ed", name = "r", descriptor = "Lclient!mn;")
	public static final Queue LOW_PRIORITY = new Queue();

	private PulseEventHandler() {
		/* empty */
	}
	@OriginalMember(owner = "client!dia", name = "a", descriptor = "(I)V")
	public static void clearAllPulseEvents() {
		PulseEvent.aClass99_48.clear();
		PulseEventHandler.LOW_PRIORITY.clear();
		PulseEventHandler.HIGH_PRIORITY.clear();
	}
	@OriginalMember(owner = "client!vj", name = "f", descriptor = "(I)Lclient!et;")
	public static PulseEvent getNextExecutedEvent() {
		@Pc(13) PulseEvent event = (PulseEvent) PulseEventHandler.HIGH_PRIORITY.first();
		if (event != null) {
			event.unlink();
			event.unlinkSecondary();
			return event;
		}
		do {
			event = (PulseEvent) PulseEventHandler.LOW_PRIORITY.first();
			if (event == null) {
				return null;
			}
			if (event.executeAfter() > Utils.currentTimeMillis()) {
				return null;
			}
			event.unlink();
			event.unlinkSecondary();
		} while ((Long.MIN_VALUE & event.indexReference) == 0L);
		return event;
	}

	@OriginalMember(owner = "client!ev", name = "a", descriptor = "(IBI)Lclient!et;")
	public static PulseEvent createPulseEvent(@OriginalArg(0) int id, @OriginalArg(2) int type) {
		@Pc(17) PulseEvent event = (PulseEvent) PulseEvent.aClass99_48.get((long) type << 32 | (long) id);
		if (event == null) {
			event = new PulseEvent(type, id);
			PulseEvent.aClass99_48.put(event.key, event);
		}
		return event;
	}
}
