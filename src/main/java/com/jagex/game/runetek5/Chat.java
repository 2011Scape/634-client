package com.jagex.game.runetek5;

import org.openrs2.deob.annotation.OriginalMember;

public final class Chat {

	@OriginalMember(owner = "client!uf", name = "K", descriptor = "I")
	public static int hsvToRgbUsers = 0;
	@OriginalMember(owner = "client!es", name = "s", descriptor = "[J")
	public static final long[] CHAT_MESSAGE_UNIQUE_HASHES = new long[100];
}
