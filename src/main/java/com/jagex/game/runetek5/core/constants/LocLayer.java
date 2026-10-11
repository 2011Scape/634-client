package com.jagex.game.runetek5.core.constants;
import org.openrs2.deob.annotation.OriginalMember;

public final class LocLayer {

	public static final int WALL = 0;

	public static final int WALLDECOR = 1;

	public static final int GROUND = 2;

	public static final int GROUNDDECOR = 3;

	private LocLayer() {
		/* empty */
	}
	@OriginalMember(owner = "client!id", name = "b", descriptor = "[I")
	public static final int[] OBJECT_TYPE_SLOTS = new int[] { 0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3 };
}
