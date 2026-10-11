package com.jagex.game.runetek5.graphics.model.particle;

import com.jagex.game.runetek5.config.particle.ParticleType;
import com.jagex.game.runetek5.config.effectortype.ParticleEffectorTypeList;
import com.jagex.game.runetek5.graphics.Matrix;
import com.jagex.game.runetek5.statics.StatBaseLevels;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!eca")
public final class EffectiveVertex {

	@OriginalMember(owner = "client!eca", name = "a", descriptor = "I")
	public int verticeZ;

	@OriginalMember(owner = "client!eca", name = "b", descriptor = "I")
	public int verticeX;

	@OriginalMember(owner = "client!eca", name = "c", descriptor = "I")
	public int verticeY;

	@OriginalMember(owner = "client!eca", name = "k", descriptor = "Lclient!eca;")
	public EffectiveVertex child;

	@OriginalMember(owner = "client!eca", name = "o", descriptor = "Lclient!bca;")
	public Matrix matrix;

	@OriginalMember(owner = "client!eca", name = "h", descriptor = "I")
	public final int effectId;

	@OriginalMember(owner = "client!eca", name = "g", descriptor = "I")
	public final int appliesOnVertex;

	@OriginalMember(owner = "client!eca", name = "<init>", descriptor = "(II)V")
	public EffectiveVertex(@OriginalArg(0) int effectId, @OriginalArg(1) int appliesOnVertex) {
		this.effectId = effectId;
		this.appliesOnVertex = appliesOnVertex;
	}

	@OriginalMember(owner = "client!eca", name = "a", descriptor = "(B)Lclient!po;")
	public ParticleType getParticleType() {
		return ParticleEffectorTypeList.list(this.effectId);
	}

	@OriginalMember(owner = "client!eca", name = "a", descriptor = "(BI)Lclient!eca;")
	public EffectiveVertex copyWithVertex(@OriginalArg(1) int appliesOnVertex) {
		return new EffectiveVertex(this.effectId, appliesOnVertex);
	}
}
