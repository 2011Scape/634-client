package com.jagex.game.runetek5.config.paramtype;

import org.openrs2.deob.annotation.OriginalMember;

public final class SkillUpdateCycle {

	@OriginalMember(owner = "client!dc", name = "d", descriptor = "I")
	public static int skillUpdateCycle = 0;
	@OriginalMember(owner = "client!rc", name = "i", descriptor = "[I")
	public static final int[] recentSkillUpdates = new int[32];
}
