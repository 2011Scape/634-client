package com.jagex.game.runetek5.client.input.mouse

import com.jagex.game.runetek5.cache.node.Node
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.AbstractMouseManager

@OriginalClass("client!bu")
abstract class MouseRecord : Node() {

	@OriginalMember(owner = "client!bu", name = "a", descriptor = "(B)I")
	abstract fun getMouseX(): Int

	@OriginalMember(owner = "client!bu", name = "b", descriptor = "(I)I")
	abstract fun getClickType(): Int

	@OriginalMember(owner = "client!bu", name = "d", descriptor = "(I)I")
	abstract fun getMouseY(): Int

	@OriginalMember(owner = "client!bu", name = "b", descriptor = "(B)J")
	abstract fun getTime(): Long

	@OriginalMember(owner = "client!bu", name = "f", descriptor = "(I)I")
	abstract fun getMeta(): Int

	companion object {
		@OriginalMember(owner = "client!wha", name = "r", descriptor = "Lclient!g;")
		@JvmField
		var mouseRecorder: AbstractMouseManager? = null
	}
}
