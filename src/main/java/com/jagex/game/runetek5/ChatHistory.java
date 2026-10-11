package com.jagex.game.runetek5;

import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.graphics.model.particle.ParticleSystemManager;

public final class ChatHistory {

	@OriginalMember(owner = "client!lj", name = "a", descriptor = "(Z)I")
	public static int getParticleDetailLevel() {
		return ParticleSystemManager.detailsSetting;
	}
}
