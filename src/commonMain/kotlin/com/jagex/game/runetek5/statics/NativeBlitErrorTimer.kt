package com.jagex.game.runetek5.statics

import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField

object NativeBlitErrorTimer {

	@OriginalMember(owner = "client!p", name = "q", descriptor = "J")
	@JvmField
	var firstErrorTime: Long = 0L

	@OriginalMember(owner = "client!p", name = "o", descriptor = "Z")
	@JvmField
	var blitFailed: Boolean = false
}
