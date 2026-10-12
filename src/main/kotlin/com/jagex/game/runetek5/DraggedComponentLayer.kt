package com.jagex.game.runetek5

import com.jagex.game.runetek5.config.rsinterface.Component
import org.openrs2.deob.annotation.OriginalMember

object DraggedComponentLayer {

	@OriginalMember(owner = "client!oaa", name = "c", descriptor = "[Lclient!at;")
	@JvmField
	var components: Array<Component?>? = null
}
