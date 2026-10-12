package com.jagex.game.runetek5

import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField

@OriginalClass("client!k")
class GpuFaceNormal {

	@OriginalMember(owner = "client!k", name = "b", descriptor = "I")
	@JvmField
	var x: Int = 0

	@OriginalMember(owner = "client!k", name = "c", descriptor = "I")
	@JvmField
	var y: Int = 0

	@OriginalMember(owner = "client!k", name = "f", descriptor = "I")
	@JvmField
	var z: Int = 0
}
