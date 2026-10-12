package com.jagex.game.runetek5

import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!gr")
class VertexNormalGL {

	@OriginalMember(owner = "client!gr", name = "a", descriptor = "I")
	@JvmField
	var z: Int = 0

	@OriginalMember(owner = "client!gr", name = "b", descriptor = "I")
	@JvmField
	var magnitude: Int = 0

	@OriginalMember(owner = "client!gr", name = "c", descriptor = "I")
	@JvmField
	var x: Int = 0

	@OriginalMember(owner = "client!gr", name = "d", descriptor = "I")
	@JvmField
	var y: Int = 0
}
