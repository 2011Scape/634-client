package com.jagex.game.runetek5.graphics.particles;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!vha")
public final class ParticleEmitterRelated {

	@OriginalMember(owner = "client!vha", name = "c", descriptor = "I")
	public int verticeZC;

	@OriginalMember(owner = "client!vha", name = "d", descriptor = "I")
	public int centerY;

	@OriginalMember(owner = "client!vha", name = "e", descriptor = "I")
	public int verticeXA;

	@OriginalMember(owner = "client!vha", name = "f", descriptor = "I")
	public int verticeYB;

	@OriginalMember(owner = "client!vha", name = "g", descriptor = "I")
	public int verticeYA;

	@OriginalMember(owner = "client!vha", name = "h", descriptor = "I")
	public int centerZ;

	@OriginalMember(owner = "client!vha", name = "i", descriptor = "I")
	public int verticeZB;

	@OriginalMember(owner = "client!vha", name = "j", descriptor = "I")
	public int centerX;

	@OriginalMember(owner = "client!vha", name = "l", descriptor = "I")
	public int verticeXC;

	@OriginalMember(owner = "client!vha", name = "o", descriptor = "I")
	public int verticeYC;

	@OriginalMember(owner = "client!vha", name = "p", descriptor = "I")
	public int verticeZA;

	@OriginalMember(owner = "client!vha", name = "q", descriptor = "I")
	public int verticeXB;

	@OriginalMember(owner = "client!vha", name = "a", descriptor = "(Lclient!vha;Z)Z")
	public boolean hasSameCenter(@OriginalArg(0) ParticleEmitterRelated particleEmitterRelated) {
		return particleEmitterRelated.centerX == this.centerX && particleEmitterRelated.centerY == this.centerY && particleEmitterRelated.centerZ == this.centerZ;
	}
}
