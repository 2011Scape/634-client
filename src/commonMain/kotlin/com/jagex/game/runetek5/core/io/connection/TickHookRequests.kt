package com.jagex.game.runetek5.core.io.connection

import com.jagex.game.runetek5.core.datastruct.key.Deque
import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField

object TickHookRequests {

	@OriginalMember(owner = "client!rha", name = "d", descriptor = "Lclient!uh;")
	@JvmField
	val TICK_HOOK_REQUESTS: Deque = Deque()
	@OriginalMember(owner = "client!fia", name = "d", descriptor = "Lclient!uh;")
	@JvmField
	val MOUSE_HOOK_REQUESTS: Deque = Deque()
}
