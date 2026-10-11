package com.jagex.game.runetek5.network;

import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.core.datastruct.key.Deque;

public final class PingRequester {
	@OriginalMember(owner = "client!aha", name = "f", descriptor = "J")
	public static long NEXT_PING_UPDATE_TO_SERVER = -1L;

	private PingRequester() {
		/* empty */
	}
	@OriginalMember(owner = "client!fc", name = "T", descriptor = "Lclient!uh;")
	public static final Deque CURRENT_REQUEST = new Deque();
}
