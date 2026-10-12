package com.jagex.game.runetek5.graphics.particles

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!vha")
class ParticleEmitterRelated {

	@OriginalMember(owner = "client!vha", name = "c", descriptor = "I")
	@JvmField
	var verticeZC: Int = 0

	@OriginalMember(owner = "client!vha", name = "d", descriptor = "I")
	@JvmField
	var centerY: Int = 0

	@OriginalMember(owner = "client!vha", name = "e", descriptor = "I")
	@JvmField
	var verticeXA: Int = 0

	@OriginalMember(owner = "client!vha", name = "f", descriptor = "I")
	@JvmField
	var verticeYB: Int = 0

	@OriginalMember(owner = "client!vha", name = "g", descriptor = "I")
	@JvmField
	var verticeYA: Int = 0

	@OriginalMember(owner = "client!vha", name = "h", descriptor = "I")
	@JvmField
	var centerZ: Int = 0

	@OriginalMember(owner = "client!vha", name = "i", descriptor = "I")
	@JvmField
	var verticeZB: Int = 0

	@OriginalMember(owner = "client!vha", name = "j", descriptor = "I")
	@JvmField
	var centerX: Int = 0

	@OriginalMember(owner = "client!vha", name = "l", descriptor = "I")
	@JvmField
	var verticeXC: Int = 0

	@OriginalMember(owner = "client!vha", name = "o", descriptor = "I")
	@JvmField
	var verticeYC: Int = 0

	@OriginalMember(owner = "client!vha", name = "p", descriptor = "I")
	@JvmField
	var verticeZA: Int = 0

	@OriginalMember(owner = "client!vha", name = "q", descriptor = "I")
	@JvmField
	var verticeXB: Int = 0

	@OriginalMember(owner = "client!vha", name = "a", descriptor = "(Lclient!vha;Z)Z")
	fun hasSameCenter(@OriginalArg(0) particleEmitterRelated: ParticleEmitterRelated?): Boolean {
		return particleEmitterRelated!!.centerX == this.centerX && particleEmitterRelated.centerY == this.centerY && particleEmitterRelated.centerZ == this.centerZ
	}
}
