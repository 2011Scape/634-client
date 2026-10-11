package com.jagex.game.runetek5;

import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.config.particle.ParticleType;

public final class FriendChat {

	@OriginalMember(owner = "client!la", name = "d", descriptor = "(I)V")
	public static void clearParticleCache() {
		ParticleType.particleCache.reset();
	}

	@OriginalMember(owner = "client!la", name = "s", descriptor = "I")
	public static int otherLevelGroupId;
}
