package com.jagex.game.runetek5.config

import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField

object DefaultsFile {
	@OriginalMember(owner = "client!ef", name = "c", descriptor = "I")
	@JvmField
	var fileId: Int = -1
}
