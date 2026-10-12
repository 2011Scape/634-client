package com.jagex.game.runetek5.graphics.safe

import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField

object PureJavaModel {

	@OriginalMember(owner = "client!jga", name = "S", descriptor = "I")
	@JvmField
	var renderCycle: Int = 0
}
