package com.jagex.game.runetek5.graphics.model;

import org.openrs2.deob.annotation.OriginalMember;

public final class ModelSM {
	@OriginalMember(owner = "client!jga", name = "bb", descriptor = "I")
	public static int maxModelVertices = 4096;

	@OriginalMember(owner = "client!jga", name = "X", descriptor = "I")
	public static int maxModelTriangles = 4096;

	private ModelSM() {
		/* empty */
	}
}
