package com.jagex.game.runetek5

import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField

@OriginalClass("client!eg")
class FaceNormalGL {

	@OriginalMember(owner = "client!eg", name = "b", descriptor = "I")
	@JvmField
	var y: Int = 0

	@OriginalMember(owner = "client!eg", name = "e", descriptor = "I")
	@JvmField
	var x: Int = 0

	@OriginalMember(owner = "client!eg", name = "f", descriptor = "I")
	@JvmField
	var z: Int = 0
}
