package com.jagex.game.runetek5;

import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.utils.Utils;

@OriginalClass("client!lv")
public final class MillisNanoTimer extends AbstractNanoTimer {

	@OriginalMember(owner = "client!lv", name = "b", descriptor = "(I)J")
	@Override
	public long nanoTime() {
		return Utils.currentTimeMillis() * 1000000L;
	}
}
