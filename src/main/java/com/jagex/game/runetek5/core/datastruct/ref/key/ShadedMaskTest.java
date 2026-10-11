package com.jagex.game.runetek5.core.datastruct.ref.key;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;

public final class ShadedMaskTest {

	@OriginalMember(owner = "client!rd", name = "a", descriptor = "(IBI)Z")
	public static boolean usesShading(@OriginalArg(0) int flags, @OriginalArg(2) int functionMask) {
		return (functionMask & 0x10000) != 0;
	}
}
