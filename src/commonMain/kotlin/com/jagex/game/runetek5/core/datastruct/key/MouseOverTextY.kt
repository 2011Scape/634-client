package com.jagex.game.runetek5.core.datastruct.key

import com.jagex.game.runetek5.config.rsinterface.Component
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

object MouseOverTextY {

	@OriginalMember(owner = "client!ib", name = "e", descriptor = "I")
	@JvmField
	var mouseOverTextY: Int = -1
	@OriginalMember(owner = "client!rk", name = "w", descriptor = "Lclient!at;")
	@JvmField
	var component: Component? = null

	@OriginalMember(owner = "client!dj", name = "d", descriptor = "I")
	@JvmField
	var mouseOverTextX: Int = -1
	@OriginalMember(owner = "client!pq", name = "a", descriptor = "(ILclient!at;II)V")
	@JvmStatic
	fun setMouseOverText(@OriginalArg(0) y: Int, @OriginalArg(1) component: Component?, @OriginalArg(3) x: Int) {
		MouseOverTextY.mouseOverTextY = y
		MouseOverTextY.component = component
		MouseOverTextY.mouseOverTextX = x
	}
}
