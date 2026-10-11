package com.jagex.game.runetek5.config.effectortype;

import com.jagex.game.runetek5.Loading;
import com.jagex.game.runetek5.cache.node.IntNode;
import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.config.particle.ParticleType;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.config.particle.ParticleTypeList;

public final class ParticleEffectorTypeList {

	@OriginalMember(owner = "client!uca", name = "a", descriptor = "(ZI)Lclient!po;")
	public static ParticleType list(@OriginalArg(1) int partileFileId) {
		@Pc(10) ParticleType particleEffectorType = (ParticleType) ParticleType.particleCache.get((long) partileFileId);
		if (particleEffectorType != null) {
			return particleEffectorType;
		}
		@Pc(20) byte[] data = ParticleTypeList.PARTICLE_INDEX.getFile(1, partileFileId);
		particleEffectorType = new ParticleType();
		particleEffectorType.partileFileId = partileFileId;
		if (data != null) {
			particleEffectorType.decode(new Packet(data));
		}
		particleEffectorType.postDecode();
		if (particleEffectorType.particleHandlingType == 2 && ParticleType.particleTypeCache.get((long) partileFileId) == null) {
			ParticleType.particleTypeCache.put((long) partileFileId, new IntNode(Loading.particleType));
			ParticleType.particleTypes[Loading.particleType++] = particleEffectorType;
		}
		ParticleType.particleCache.put(particleEffectorType, (long) partileFileId);
		return particleEffectorType;
	}
}
