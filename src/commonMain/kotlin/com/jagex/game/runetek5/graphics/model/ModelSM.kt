package com.jagex.game.runetek5.graphics.model

import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField

object ModelSM {
	@OriginalMember(owner = "client!jga", name = "bb", descriptor = "I")
	@JvmField
	var maxModelVertices: Int = 4096

	@OriginalMember(owner = "client!jga", name = "X", descriptor = "I")
	@JvmField
	var maxModelTriangles: Int = 4096
}
