package com.jagex.game.runetek5

import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField

object Preferences {

	@OriginalMember(owner = "client!vl", name = "q", descriptor = "I")
	@JvmField
	var favoriteWorlds: Int = 0
}
