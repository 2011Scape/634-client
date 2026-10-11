package com.jagex.game.runetek5.core.datastruct.key;

import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.config.rsinterface.Component;
import org.openrs2.deob.annotation.OriginalArg;

public final class MouseOverTextY {

	@OriginalMember(owner = "client!ib", name = "e", descriptor = "I")
	public static int mouseOverTextY = -1;
	@OriginalMember(owner = "client!rk", name = "w", descriptor = "Lclient!at;")
	public static Component component = null;

	@OriginalMember(owner = "client!dj", name = "d", descriptor = "I")
	public static int mouseOverTextX = -1;
	@OriginalMember(owner = "client!pq", name = "a", descriptor = "(ILclient!at;II)V")
	public static void setMouseOverText(@OriginalArg(0) int y, @OriginalArg(1) Component component, @OriginalArg(3) int x) {
		MouseOverTextY.mouseOverTextY = y;
		MouseOverTextY.component = component;
		MouseOverTextY.mouseOverTextX = x;
	}
}
