package com.jagex.game.runetek5

import com.jagex.game.runetek5.config.particle.ParticleType
import org.openrs2.deob.annotation.OriginalMember

object FriendChat {

	@OriginalMember(owner = "client!la", name = "d", descriptor = "(I)V")
	@JvmStatic
	fun clearParticleCache() {
		ParticleType.particleCache.reset()
	}

	@OriginalMember(owner = "client!la", name = "s", descriptor = "I")
	@JvmField
	var otherLevelGroupId: Int = 0
}
