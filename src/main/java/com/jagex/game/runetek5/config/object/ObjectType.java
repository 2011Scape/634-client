package com.jagex.game.runetek5.config.object;

import org.openrs2.deob.annotation.OriginalMember;

public final class ObjectType {
	@OriginalMember(owner = "client!mj", name = "s", descriptor = "[S")
	public static short[] locRecolorPalette = new short[256];

	private ObjectType() {
		/* empty */
	}
}
