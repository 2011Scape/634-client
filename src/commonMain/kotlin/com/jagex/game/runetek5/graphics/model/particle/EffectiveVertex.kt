package com.jagex.game.runetek5.graphics.model.particle

import com.jagex.game.runetek5.config.particle.ParticleType
import com.jagex.game.runetek5.config.effectortype.ParticleEffectorTypeList
import com.jagex.game.runetek5.graphics.Matrix
import com.jagex.game.runetek5.statics.StatBaseLevels
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField

@OriginalClass("client!eca")
class EffectiveVertex @OriginalMember(owner = "client!eca", name = "<init>", descriptor = "(II)V") constructor(@OriginalArg(0) effectId: Int, @OriginalArg(1) appliesOnVertex: Int) {

	@OriginalMember(owner = "client!eca", name = "a", descriptor = "I")
	@JvmField
	var verticeZ: Int = 0

	@OriginalMember(owner = "client!eca", name = "b", descriptor = "I")
	@JvmField
	var verticeX: Int = 0

	@OriginalMember(owner = "client!eca", name = "c", descriptor = "I")
	@JvmField
	var verticeY: Int = 0

	@OriginalMember(owner = "client!eca", name = "k", descriptor = "Lclient!eca;")
	@JvmField
	var child: EffectiveVertex? = null

	@OriginalMember(owner = "client!eca", name = "o", descriptor = "Lclient!bca;")
	@JvmField
	var matrix: Matrix? = null

	@OriginalMember(owner = "client!eca", name = "h", descriptor = "I")
	@JvmField
	val effectId: Int

	@OriginalMember(owner = "client!eca", name = "g", descriptor = "I")
	@JvmField
	val appliesOnVertex: Int

	init {
		this.effectId = effectId
		this.appliesOnVertex = appliesOnVertex
	}

	@OriginalMember(owner = "client!eca", name = "a", descriptor = "(B)Lclient!po;")
	fun getParticleType(): ParticleType? {
		return ParticleEffectorTypeList.list(this.effectId)
	}

	@OriginalMember(owner = "client!eca", name = "a", descriptor = "(BI)Lclient!eca;")
	fun copyWithVertex(@OriginalArg(1) appliesOnVertex: Int): EffectiveVertex {
		return EffectiveVertex(this.effectId, appliesOnVertex)
	}
}
