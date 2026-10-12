package com.jagex.game.runetek5.client.menu

import com.jagex.game.runetek5.config.rsinterface.Component
import org.openrs2.deob.annotation.OriginalMember

object MenuInteraction {
	@OriginalMember(owner = "client!ae", name = "q", descriptor = "Lclient!at;")
	@JvmField
	var lastHoveredComponent: Component? = null

	@OriginalMember(owner = "client!mo", name = "a", descriptor = "I")
	@JvmField
	var menuItemHoverCycle: Int = 0
}
