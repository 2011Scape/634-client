package com.jagex.game.runetek5.graphics.map.worldmap

import com.jagex.game.runetek5.cache.node.Node
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!lga")
class WorldMapCategory : Node {

	@OriginalMember(owner = "client!lga", name = "r", descriptor = "I")
	@JvmField
	var level: Int = 0

	@OriginalMember(owner = "client!lga", name = "s", descriptor = "I")
	@JvmField
	var z: Int = 0

	@OriginalMember(owner = "client!lga", name = "t", descriptor = "I")
	@JvmField
	var x: Int = 0

	@OriginalMember(owner = "client!lga", name = "u", descriptor = "I")
	@JvmField
	var screenX: Int = 0

	@OriginalMember(owner = "client!lga", name = "w", descriptor = "I")
	@JvmField
	var screenY: Int = 0

	@OriginalMember(owner = "client!lga", name = "p", descriptor = "Z")
	@JvmField
	var isMouseOver: Boolean = false

	@OriginalMember(owner = "client!lga", name = "o", descriptor = "I")
	@JvmField
	var id: Int = -1

	@OriginalMember(owner = "client!lga", name = "<init>", descriptor = "(I)V")
	constructor(@OriginalArg(0) mecId: Int) : super() {
		this.id = mecId
	}
}
