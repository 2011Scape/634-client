package com.jagex.game.runetek5.cache.node.lru

import com.jagex.game.runetek5.config.particle.ParticleType
import com.jagex.game.runetek5.graphics.model.particle.EffectiveVertex
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.VarpDomain
import com.jagex.game.runetek5.entity.ParticleSystem
import kotlin.jvm.JvmField

@OriginalClass("client!jo")
class ParticleEffector @OriginalMember(owner = "client!jo", name = "<init>", descriptor = "(Lclient!eca;Lclient!uj;)V") constructor(@OriginalArg(0) modelParticleEffector: EffectiveVertex?, @OriginalArg(1) particleSystem: ParticleSystem?) : CacheableNode() {

	@OriginalMember(owner = "client!jo", name = "z", descriptor = "I")
	@JvmField
	var directionX: Int = 0

	@OriginalMember(owner = "client!jo", name = "A", descriptor = "I")
	@JvmField
	var verticeX: Int = 0

	@OriginalMember(owner = "client!jo", name = "B", descriptor = "I")
	@JvmField
	var directionZ: Int = 0

	@OriginalMember(owner = "client!jo", name = "E", descriptor = "I")
	@JvmField
	var verticeY: Int = 0

	@OriginalMember(owner = "client!jo", name = "G", descriptor = "I")
	@JvmField
	var verticeZ: Int = 0

	@OriginalMember(owner = "client!jo", name = "w", descriptor = "Lclient!eca;")
	@JvmField
	val model: EffectiveVertex?

	@OriginalMember(owner = "client!jo", name = "y", descriptor = "Lclient!po;")
	@JvmField
	val type: ParticleType?

	init {
		this.model = modelParticleEffector
		this.type = this.model!!.getParticleType()
		this.update()
	}

	@OriginalMember(owner = "client!jo", name = "a", descriptor = "(I)V")
	fun update() {
		this.verticeX = this.model!!.verticeX
		this.verticeY = this.model.verticeY
		this.verticeZ = this.model.verticeZ
		if (this.model.matrix != null) {
			this.model.matrix!!.projectDirection(this.type!!.offsetX, this.type.offsetY, this.type.offsetZ, VarpDomain.projectedDirection)
		}
		this.directionX = VarpDomain.projectedDirection[0]
		this.directionZ = VarpDomain.projectedDirection[2]
	}
}
