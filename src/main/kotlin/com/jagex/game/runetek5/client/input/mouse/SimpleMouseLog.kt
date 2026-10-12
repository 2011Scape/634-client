package com.jagex.game.runetek5.client.input.mouse

import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!qd")
class SimpleMouseLog : MouseRecord() {

	@OriginalMember(owner = "client!qd", name = "v", descriptor = "I")
	@JvmField
	var meta: Int = 0

	@OriginalMember(owner = "client!qd", name = "y", descriptor = "I")
	@JvmField
	var mouseY: Int = 0

	@OriginalMember(owner = "client!qd", name = "z", descriptor = "J")
	@JvmField
	var time: Long = 0L

	@OriginalMember(owner = "client!qd", name = "B", descriptor = "I")
	@JvmField
	var clickType: Int = 0

	@OriginalMember(owner = "client!qd", name = "E", descriptor = "I")
	@JvmField
	var mouseX: Int = 0

	@OriginalMember(owner = "client!qd", name = "b", descriptor = "(I)I")
	override fun getClickType(): Int {
		return this.clickType
	}

	@OriginalMember(owner = "client!qd", name = "d", descriptor = "(I)I")
	override fun getMouseY(): Int {
		return this.mouseY
	}

	@OriginalMember(owner = "client!qd", name = "a", descriptor = "(B)I")
	override fun getMouseX(): Int {
		return this.mouseX
	}

	@OriginalMember(owner = "client!qd", name = "b", descriptor = "(B)J")
	override fun getTime(): Long {
		return this.time
	}

	@OriginalMember(owner = "client!qd", name = "f", descriptor = "(I)I")
	override fun getMeta(): Int {
		return this.meta
	}
}
