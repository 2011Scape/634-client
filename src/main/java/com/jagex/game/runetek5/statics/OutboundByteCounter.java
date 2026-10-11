package com.jagex.game.runetek5.statics;

import org.openrs2.deob.annotation.OriginalMember;

public final class OutboundByteCounter {

	@OriginalMember(owner = "client!lb", name = "b", descriptor = "I")
	public static int bytesWritten;
	@OriginalMember(owner = "client!bw", name = "w", descriptor = "I")
	public static int outboundBytesPerSecond;
	@OriginalMember(owner = "client!ui", name = "i", descriptor = "I")
	public static int inboundBytesPerSecond;

	@OriginalMember(owner = "client!am", name = "c", descriptor = "I")
	public static int bytesRead;
}
