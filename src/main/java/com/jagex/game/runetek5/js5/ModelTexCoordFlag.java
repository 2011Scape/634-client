package com.jagex.game.runetek5.js5;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;

public final class ModelTexCoordFlag {

	@OriginalMember(owner = "client!nw", name = "a", descriptor = "(IBI)Z")
	public static boolean hasTextureCoordinates(@OriginalArg(0) int preferenceFlags, @OriginalArg(2) int functionMask) {
		return (functionMask & 0x800) != 0;
	}
}
