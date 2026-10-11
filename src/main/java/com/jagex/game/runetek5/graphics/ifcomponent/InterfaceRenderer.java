package com.jagex.game.runetek5.graphics.ifcomponent;

import com.jagex.game.runetek5.config.rsinterface.Component;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.InterfaceManager;
import com.jagex.game.runetek5.config.rsinterface.ComponentType;
import com.jagex.game.runetek5.client.ClientState;

public final class InterfaceRenderer {
	@OriginalMember(owner = "client!client", name = "c", descriptor = "(Lclient!at;)Z")
	public static boolean isComponentHidden(@OriginalArg(0) Component component) {
		if (InterfaceManager.qaOpTest) {
			if (InterfaceManager.getComponentSettings(component).settingsHash != 0) {
				return false;
			}
			if (component.type == ComponentType.CONTAINER) {
				return false;
			}
		}
		return component.hidden;
	}

	private InterfaceRenderer() {
		/* empty */
	}
	@OriginalMember(owner = "client!ga", name = "y", descriptor = "I")
	public static int dragTickCount;
	@OriginalMember(owner = "client!jl", name = "i", descriptor = "I")
	public static int lastRenderedCycle = -2;
}
