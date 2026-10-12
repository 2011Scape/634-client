package com.jagex.game.runetek5

import org.openrs2.deob.annotation.OriginalMember

object CutsceneManager {

	@OriginalMember(owner = "client!un", name = "Q", descriptor = "I")
	@JvmField
	var runningParticleSystemCount: Int = 0
}
