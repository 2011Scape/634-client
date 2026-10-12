package com.jagex.game.runetek5

import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField

@OriginalClass("client!f")
class PlayerUpdateEntry {

	@OriginalMember(owner = "client!f", name = "b", descriptor = "I")
	@JvmField
	var packedCoordinates: Int = 0

	@OriginalMember(owner = "client!f", name = "e", descriptor = "I")
	@JvmField
	var rotation: Int = 0

	@OriginalMember(owner = "client!f", name = "g", descriptor = "I")
	@JvmField
	var faceEntity: Int = 0

	@OriginalMember(owner = "client!f", name = "h", descriptor = "Z")
	@JvmField
	var pMapdot: Boolean = false
}
