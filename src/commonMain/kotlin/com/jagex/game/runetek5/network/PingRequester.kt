package com.jagex.game.runetek5.network

import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.core.datastruct.key.Deque
import kotlin.jvm.JvmField

object PingRequester {
	@OriginalMember(owner = "client!aha", name = "f", descriptor = "J")
	@JvmField
	var NEXT_PING_UPDATE_TO_SERVER: Long = -1L

	@OriginalMember(owner = "client!fc", name = "T", descriptor = "Lclient!uh;")
	@JvmField
	val CURRENT_REQUEST: Deque = Deque()
}
