package com.jagex.game.runetek5.client.menu;

import com.jagex.game.runetek5.config.rsinterface.Component;
import org.openrs2.deob.annotation.OriginalMember;

public final class MenuInteraction {
	@OriginalMember(owner = "client!ae", name = "q", descriptor = "Lclient!at;")
	public static Component lastHoveredComponent;

	private MenuInteraction() {
		/* empty */
	}
	@OriginalMember(owner = "client!mo", name = "a", descriptor = "I")
	public static int menuItemHoverCycle = 0;
}
