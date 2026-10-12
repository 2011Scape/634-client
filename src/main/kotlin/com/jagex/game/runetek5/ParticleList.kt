package com.jagex.game.runetek5

import com.jagex.game.runetek5.graphics.model.particle.ParticleBinaryTree
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!mda")
class ParticleList {

	@OriginalMember(owner = "client!mda", name = "f", descriptor = "Lclient!lh;")
	@JvmField
	val particles: ParticleBinaryTree = ParticleBinaryTree()
}
