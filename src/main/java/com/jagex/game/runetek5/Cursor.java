package com.jagex.game.runetek5;

import org.openrs2.deob.annotation.OriginalMember;

public final class Cursor {

	@OriginalMember(owner = "client!ep", name = "v", descriptor = "I")
	public static int interaction = 0;
	@OriginalMember(owner = "client!fg", name = "c", descriptor = "I")
	public static int interfaceCursorId = -1;
}
