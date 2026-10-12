package com.jagex.game.runetek5.graphics.skybox

import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.config.sun.Sun
import kotlin.jvm.JvmStatic

object SunModelReset {

	@OriginalMember(owner = "client!ki", name = "c", descriptor = "()V")
	@JvmStatic
	fun resetMedia() {
		Sun.sunModel = null
		Sun.activeSun = null
	}
	@OriginalMember(owner = "client!vc", name = "b", descriptor = "(Z)V")
	@JvmStatic
	fun reset() {
		SunModelReset.resetMedia()
	}
}
