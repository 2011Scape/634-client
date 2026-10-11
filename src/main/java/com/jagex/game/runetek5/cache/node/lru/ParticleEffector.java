package com.jagex.game.runetek5.cache.node.lru;

import com.jagex.game.runetek5.cache.node.lru.CacheableNode;
import com.jagex.game.runetek5.config.particle.ParticleType;
import com.jagex.game.runetek5.graphics.model.particle.EffectiveVertex;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.VarpDomain;
import com.jagex.game.runetek5.entity.ParticleSystem;

@OriginalClass("client!jo")
public final class ParticleEffector extends CacheableNode {

	@OriginalMember(owner = "client!jo", name = "z", descriptor = "I")
	public int directionX;

	@OriginalMember(owner = "client!jo", name = "A", descriptor = "I")
	public int verticeX;

	@OriginalMember(owner = "client!jo", name = "B", descriptor = "I")
	public int directionZ;

	@OriginalMember(owner = "client!jo", name = "E", descriptor = "I")
	public int verticeY;

	@OriginalMember(owner = "client!jo", name = "G", descriptor = "I")
	public int verticeZ;

	@OriginalMember(owner = "client!jo", name = "w", descriptor = "Lclient!eca;")
	public final EffectiveVertex model;

	@OriginalMember(owner = "client!jo", name = "y", descriptor = "Lclient!po;")
	public final ParticleType type;

	@OriginalMember(owner = "client!jo", name = "<init>", descriptor = "(Lclient!eca;Lclient!uj;)V")
	public ParticleEffector(@OriginalArg(0) EffectiveVertex modelParticleEffector, @OriginalArg(1) ParticleSystem particleSystem) {
		this.model = modelParticleEffector;
		this.type = this.model.getParticleType();
		this.update();
	}

	@OriginalMember(owner = "client!jo", name = "a", descriptor = "(I)V")
	public void update() {
		this.verticeX = this.model.verticeX;
		this.verticeY = this.model.verticeY;
		this.verticeZ = this.model.verticeZ;
		if (this.model.matrix != null) {
			this.model.matrix.projectDirection(this.type.offsetX, this.type.offsetY, this.type.offsetZ, VarpDomain.projectedDirection);
		}
		this.directionX = VarpDomain.projectedDirection[0];
		this.directionZ = VarpDomain.projectedDirection[2];
	}
}
