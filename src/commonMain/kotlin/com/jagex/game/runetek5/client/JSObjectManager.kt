package com.jagex.game.runetek5.client

import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField

object JSObjectManager {
	@OriginalMember(owner = "client!ko", name = "v", descriptor = "I")
	@JvmField
	var highlightFlashCycles: Int = 0
}
