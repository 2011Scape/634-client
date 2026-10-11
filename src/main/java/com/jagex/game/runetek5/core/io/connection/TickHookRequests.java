package com.jagex.game.runetek5.core.io.connection;

import com.jagex.game.runetek5.core.datastruct.key.Deque;
import org.openrs2.deob.annotation.OriginalMember;

public final class TickHookRequests {

	@OriginalMember(owner = "client!rha", name = "d", descriptor = "Lclient!uh;")
	public static final Deque TICK_HOOK_REQUESTS = new Deque();
	@OriginalMember(owner = "client!fia", name = "d", descriptor = "Lclient!uh;")
	public static final Deque MOUSE_HOOK_REQUESTS = new Deque();
}
