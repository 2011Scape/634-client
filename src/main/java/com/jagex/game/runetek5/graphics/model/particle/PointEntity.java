package com.jagex.game.runetek5.graphics.model.particle;

import com.jagex.game.runetek5.graphics.model.particle.ParticleNode;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!jc")
public class PointEntity extends ParticleNode {

	@OriginalMember(owner = "client!jc", name = "l", descriptor = "I")
	public int z;

	@OriginalMember(owner = "client!jc", name = "m", descriptor = "I")
	public int colorArgb;

	@OriginalMember(owner = "client!jc", name = "n", descriptor = "I")
	public int x;

	@OriginalMember(owner = "client!jc", name = "o", descriptor = "I")
	public int materialId;

	@OriginalMember(owner = "client!jc", name = "p", descriptor = "I")
	public int size;

	@OriginalMember(owner = "client!jc", name = "q", descriptor = "Z")
	public boolean lightIntensityAdjustment;

	@OriginalMember(owner = "client!jc", name = "s", descriptor = "I")
	public int y;

	@OriginalMember(owner = "client!jc", name = "r", descriptor = "B")
	public byte priority = 5;

	@OriginalMember(owner = "client!jc", name = "<init>", descriptor = "()V")
	public PointEntity() {
	}
}
