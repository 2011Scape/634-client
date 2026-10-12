package com.jagex.game.runetek5

import com.jagex.game.runetek5.graphics.model.particle.ParticleSystemManager
import org.openrs2.deob.annotation.OriginalMember

object ChatHistory {

	@OriginalMember(owner = "client!lj", name = "a", descriptor = "(Z)I")
	@JvmStatic
	fun getParticleDetailLevel(): Int {
		return ParticleSystemManager.detailsSetting
	}
}
