package com.jagex.game.runetek5

import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField

@OriginalClass("client!fr")
class GpuVertexNormal {

	@OriginalMember(owner = "client!fr", name = "a", descriptor = "I")
	@JvmField
	var x: Int = 0

	@OriginalMember(owner = "client!fr", name = "c", descriptor = "I")
	@JvmField
	var magnitude: Int = 0

	@OriginalMember(owner = "client!fr", name = "e", descriptor = "I")
	@JvmField
	var z: Int = 0

	@OriginalMember(owner = "client!fr", name = "f", descriptor = "I")
	@JvmField
	var y: Int = 0
}
