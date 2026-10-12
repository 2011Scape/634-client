package com.jagex.game.runetek5.statics

import org.openrs2.deob.annotation.OriginalMember

object OutboundByteCounter {

	@OriginalMember(owner = "client!lb", name = "b", descriptor = "I")
	@JvmField
	var bytesWritten: Int = 0

	@OriginalMember(owner = "client!bw", name = "w", descriptor = "I")
	@JvmField
	var outboundBytesPerSecond: Int = 0

	@OriginalMember(owner = "client!ui", name = "i", descriptor = "I")
	@JvmField
	var inboundBytesPerSecond: Int = 0

	@OriginalMember(owner = "client!am", name = "c", descriptor = "I")
	@JvmField
	var bytesRead: Int = 0
}
