package com.jagex.game.runetek5;

import com.jagex.game.runetek5.buffer.Packet;
import org.openrs2.deob.annotation.OriginalMember;

public final class UnloadedLocCount {

	@OriginalMember(owner = "client!oo", name = "l", descriptor = "I")
	public static int unloadedLocCount = 0;
}
