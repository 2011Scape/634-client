package com.jagex.game.runetek5.client.event.mouse;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;

public final class MouseLog {

	public static final int TYPE_PRESS_LEFT = 0;

	public static final int TYPE_PRESS_RIGHT = 2;

	public static final int TYPE_SCROLL = 6;

	private MouseLog() {
		/* empty */
	}
	@OriginalMember(owner = "client!fr", name = "a", descriptor = "(II)Z")
	public static boolean isMousePressType(@OriginalArg(0) int clickType) {
		return clickType == 0 || clickType == 1 || clickType == 2;
	}
}
