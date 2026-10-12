package com.jagex.game.runetek5.config.effectortype

import com.jagex.game.runetek5.Loading
import com.jagex.game.runetek5.cache.node.IntNode
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.config.particle.ParticleType
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.config.particle.ParticleTypeList
import kotlin.jvm.JvmStatic

object ParticleEffectorTypeList {

	@OriginalMember(owner = "client!uca", name = "a", descriptor = "(ZI)Lclient!po;")
	@JvmStatic
	fun list(@OriginalArg(1) partileFileId: Int): ParticleType? {
		@Pc(10) var particleEffectorType: ParticleType? = ParticleType.particleCache!!.get(partileFileId.toLong()) as ParticleType?
		if (particleEffectorType != null) {
			return particleEffectorType
		}
		@Pc(20) val data: ByteArray? = ParticleTypeList.PARTICLE_INDEX!!.getFile(1, partileFileId)
		particleEffectorType = ParticleType()
		particleEffectorType.partileFileId = partileFileId
		if (data != null) {
			particleEffectorType.decode(Packet(data))
		}
		particleEffectorType.postDecode()
		if (particleEffectorType.particleHandlingType == 2 && ParticleType.particleTypeCache!!.get(partileFileId.toLong()) == null) {
			ParticleType.particleTypeCache!!.put(partileFileId.toLong(), IntNode(Loading.particleType))
			ParticleType.particleTypes!![Loading.particleType++] = particleEffectorType
		}
		ParticleType.particleCache!!.put(particleEffectorType, partileFileId.toLong())
		return particleEffectorType
	}
}
