package com.jagex.game.runetek5.config.particle;

import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.OriginalArg;

public final class ParticleTypeList {
	@OriginalMember(owner = "client!dha", name = "D", descriptor = "Lclient!in;")
	public static Js5ResourceProvider PARTICLE_INDEX;

	private ParticleTypeList() {
		/* empty */
	}
	@OriginalMember(owner = "client!ak", name = "a", descriptor = "(ILclient!in;)V")
	public static void initialize(@OriginalArg(1) Js5ResourceProvider particleIndex) {
		ParticleTypeList.PARTICLE_INDEX = particleIndex;
	}
}
