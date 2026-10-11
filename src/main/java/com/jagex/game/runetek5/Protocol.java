package com.jagex.game.runetek5;

import org.openrs2.deob.annotation.OriginalMember;

public final class Protocol {

	@OriginalMember(owner = "client!jc", name = "t", descriptor = "[I")
	public static final int[] removedIds = new int[] { 19, 55, 38, 155, 255, 110, 137, 205, 76 };

	@OriginalMember(owner = "client!mba", name = "H", descriptor = "Z")
	public static boolean originPending;
}
