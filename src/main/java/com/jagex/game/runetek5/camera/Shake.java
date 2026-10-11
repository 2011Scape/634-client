package com.jagex.game.runetek5.camera;
import org.openrs2.deob.annotation.OriginalMember;

public final class Shake {

	public static final int DIRECTIONS = 5;

	private Shake() {
		/* empty */
	}
	@OriginalMember(owner = "client!uo", name = "T", descriptor = "[I")
	public static final int[] CAM_SHAKE_PHASE = new int[5];

	@OriginalMember(owner = "client!ub", name = "i", descriptor = "[I")
	public static final int[] CAM_SHAKE_AMPLITUDE = new int[5];
	@OriginalMember(owner = "client!ea", name = "p", descriptor = "[I")
	public static final int[] CAM_SHAKE_FREQUENCY = new int[5];
}
