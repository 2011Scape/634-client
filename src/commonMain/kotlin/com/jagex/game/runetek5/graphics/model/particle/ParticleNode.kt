package com.jagex.game.runetek5.graphics.model.particle

import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.entity.EntityNode
import kotlin.jvm.JvmField

@OriginalClass("client!taa")
open class ParticleNode : EntityNode() {

	@OriginalMember(owner = "client!taa", name = "j", descriptor = "Lclient!taa;")
	@JvmField
	var prev2: ParticleNode? = null

	@OriginalMember(owner = "client!taa", name = "k", descriptor = "Lclient!taa;")
	@JvmField
	var next2: ParticleNode? = null

	@OriginalMember(owner = "client!taa", name = "a", descriptor = "(I)V")
	fun unlink2() {
		if (this.prev2 != null) {
			this.prev2!!.next2 = this.next2
			this.next2!!.prev2 = this.prev2
			this.next2 = null
			this.prev2 = null
		}
	}
}
