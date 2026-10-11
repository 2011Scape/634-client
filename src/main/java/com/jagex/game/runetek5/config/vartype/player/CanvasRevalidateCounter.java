package com.jagex.game.runetek5.config.vartype.player;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

public final class CanvasRevalidateCounter {

	@OriginalMember(owner = "client!ow", name = "d", descriptor = "I")
	public static int revalidateCounter = 500;
}
