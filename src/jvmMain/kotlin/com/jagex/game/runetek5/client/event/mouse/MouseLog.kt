package com.jagex.game.runetek5.client.event.mouse
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember

object MouseLog {

	const val TYPE_PRESS_LEFT: Int = 0

	const val TYPE_PRESS_RIGHT: Int = 2

	const val TYPE_SCROLL: Int = 6

	@OriginalMember(owner = "client!fr", name = "a", descriptor = "(II)Z")
	@JvmStatic
	fun isMousePressType(@OriginalArg(0) clickType: Int): Boolean {
		return clickType == 0 || clickType == 1 || clickType == 2
	}
}
