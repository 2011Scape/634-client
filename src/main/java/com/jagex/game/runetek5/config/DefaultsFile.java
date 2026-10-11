package com.jagex.game.runetek5.config;

import org.openrs2.deob.annotation.OriginalMember;

public final class DefaultsFile {
	@OriginalMember(owner = "client!ef", name = "c", descriptor = "I")
	public static int fileId = -1;

	private DefaultsFile() {
		/* empty */
	}
}
