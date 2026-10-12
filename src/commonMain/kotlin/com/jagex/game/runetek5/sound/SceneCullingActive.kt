package com.jagex.game.runetek5.sound

import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField

object SceneCullingActive {

	@OriginalMember(owner = "client!ho", name = "h", descriptor = "Z")
	@JvmField
	var cullingActive: Boolean = true
}
