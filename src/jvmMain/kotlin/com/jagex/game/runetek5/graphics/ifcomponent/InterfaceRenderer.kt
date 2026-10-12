package com.jagex.game.runetek5.graphics.ifcomponent

import com.jagex.game.runetek5.config.rsinterface.Component
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.InterfaceManager
import com.jagex.game.runetek5.config.rsinterface.ComponentType

object InterfaceRenderer {
	@OriginalMember(owner = "client!client", name = "c", descriptor = "(Lclient!at;)Z")
	@JvmStatic
	fun isComponentHidden(@OriginalArg(0) component: Component?): Boolean {
		if (InterfaceManager.qaOpTest) {
			if (InterfaceManager.getComponentSettings(component)!!.settingsHash != 0) {
				return false
			}
			if (component!!.type === ComponentType.CONTAINER) {
				return false
			}
		}
		return component!!.hidden
	}

	@OriginalMember(owner = "client!ga", name = "y", descriptor = "I")
	@JvmField
	var dragTickCount: Int = 0
	@OriginalMember(owner = "client!jl", name = "i", descriptor = "I")
	@JvmField
	var lastRenderedCycle: Int = -2
}
