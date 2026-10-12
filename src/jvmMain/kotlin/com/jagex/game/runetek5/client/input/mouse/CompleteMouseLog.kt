package com.jagex.game.runetek5.client.input.mouse

import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!no")
class CompleteMouseLog : MouseRecord() {

	@OriginalMember(owner = "client!no", name = "v", descriptor = "I")
	@JvmField
	var mouseY: Int = 0

	@OriginalMember(owner = "client!no", name = "w", descriptor = "J")
	@JvmField
	var time: Long = 0L

	@OriginalMember(owner = "client!no", name = "x", descriptor = "I")
	@JvmField
	var meta: Int = 0

	@OriginalMember(owner = "client!no", name = "y", descriptor = "I")
	@JvmField
	var clickType: Int = 0

	@OriginalMember(owner = "client!no", name = "z", descriptor = "I")
	@JvmField
	var extra: Int = 0

	@OriginalMember(owner = "client!no", name = "b", descriptor = "(I)I")
	override fun getClickType(): Int {
		return this.clickType
	}

	@OriginalMember(owner = "client!no", name = "b", descriptor = "(B)J")
	override fun getTime(): Long {
		return this.time
	}

	@OriginalMember(owner = "client!no", name = "d", descriptor = "(I)I")
	override fun getMouseY(): Int {
		return this.mouseY
	}

	@OriginalMember(owner = "client!no", name = "f", descriptor = "(I)I")
	override fun getMeta(): Int {
		return this.meta
	}

	@OriginalMember(owner = "client!no", name = "a", descriptor = "(B)I")
	override fun getMouseX(): Int {
		return this.extra
	}
}
