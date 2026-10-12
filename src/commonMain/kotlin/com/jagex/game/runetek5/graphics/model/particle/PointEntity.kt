package com.jagex.game.runetek5.graphics.model.particle

import com.jagex.game.runetek5.graphics.model.particle.ParticleNode
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField

@OriginalClass("client!jc")
open class PointEntity @OriginalMember(owner = "client!jc", name = "<init>", descriptor = "()V") constructor() : ParticleNode() {

	@OriginalMember(owner = "client!jc", name = "l", descriptor = "I")
	@JvmField
	var z: Int = 0

	@OriginalMember(owner = "client!jc", name = "m", descriptor = "I")
	@JvmField
	var colorArgb: Int = 0

	@OriginalMember(owner = "client!jc", name = "n", descriptor = "I")
	@JvmField
	var x: Int = 0

	@OriginalMember(owner = "client!jc", name = "o", descriptor = "I")
	@JvmField
	var materialId: Int = 0

	@OriginalMember(owner = "client!jc", name = "p", descriptor = "I")
	@JvmField
	var size: Int = 0

	@OriginalMember(owner = "client!jc", name = "q", descriptor = "Z")
	@JvmField
	var lightIntensityAdjustment: Boolean = false

	@OriginalMember(owner = "client!jc", name = "s", descriptor = "I")
	@JvmField
	var y: Int = 0

	@OriginalMember(owner = "client!jc", name = "r", descriptor = "B")
	@JvmField
	var priority: Byte = 5
}
