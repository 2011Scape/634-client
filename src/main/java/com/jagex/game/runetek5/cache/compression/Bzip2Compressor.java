package com.jagex.game.runetek5.cache.compression;

import org.openrs2.deob.annotation.OriginalMember;

public final class Bzip2Compressor {
	@OriginalMember(owner = "client!em", name = "s", descriptor = "[I")
	public static int[] transformTable;

	private Bzip2Compressor() {
		/* empty */
	}
}
