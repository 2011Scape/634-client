package com.jagex.game.runetek5.graphics.map.worldmap

import com.jagex.game.runetek5.cache.node.Node
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField

@OriginalClass("client!ar")
class ClickzoneMEC : Node {

	@OriginalMember(owner = "client!ar", name = "s", descriptor = "I")
	@JvmField
	var rectY2: Int = Int.MIN_VALUE

	@OriginalMember(owner = "client!ar", name = "p", descriptor = "I")
	@JvmField
	var spriteTop: Int = Int.MIN_VALUE

	@OriginalMember(owner = "client!ar", name = "E", descriptor = "I")
	@JvmField
	var spriteRight: Int = Int.MIN_VALUE

	@OriginalMember(owner = "client!ar", name = "z", descriptor = "I")
	@JvmField
	var textLeft: Int = Int.MAX_VALUE

	@OriginalMember(owner = "client!ar", name = "A", descriptor = "I")
	@JvmField
	var spriteBottom: Int = Int.MAX_VALUE

	@OriginalMember(owner = "client!ar", name = "r", descriptor = "I")
	@JvmField
	var textRight: Int = Int.MIN_VALUE

	@OriginalMember(owner = "client!ar", name = "B", descriptor = "I")
	@JvmField
	var spriteLeft: Int = Int.MAX_VALUE

	@OriginalMember(owner = "client!ar", name = "H", descriptor = "I")
	@JvmField
	var textBottom: Int = Int.MAX_VALUE

	@OriginalMember(owner = "client!ar", name = "F", descriptor = "Lclient!lga;")
	@JvmField
	val category: WorldMapCategory?

	@OriginalMember(owner = "client!ar", name = "<init>", descriptor = "(Lclient!lga;)V")
	constructor(@OriginalArg(0) mapElementListEntry: WorldMapCategory?) : super() {
		this.category = mapElementListEntry
	}

	@OriginalMember(owner = "client!ar", name = "b", descriptor = "(III)Z")
	fun contains(@OriginalArg(0) x: Int, @OriginalArg(1) mouseX: Int): Boolean {
		if (this.spriteLeft <= mouseX && this.spriteRight >= mouseX && x >= this.spriteBottom && this.spriteTop >= x) {
			return true
		} else {
			return this.textLeft <= mouseX && this.textRight >= mouseX && this.textBottom <= x && this.rectY2 >= x
		}
	}
}
