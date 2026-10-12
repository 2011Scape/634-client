package com.jagex.game.runetek5.graphics.model.normal

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!fb")
class VertexNormal {

	@OriginalMember(owner = "client!fb", name = "a", descriptor = "I")
	@JvmField
	var z: Int = 0

	@OriginalMember(owner = "client!fb", name = "b", descriptor = "I")
	@JvmField
	var y: Int = 0

	@OriginalMember(owner = "client!fb", name = "c", descriptor = "I")
	@JvmField
	var magnitude: Int = 0

	@OriginalMember(owner = "client!fb", name = "d", descriptor = "I")
	@JvmField
	var x: Int = 0

	@OriginalMember(owner = "client!fb", name = "<init>", descriptor = "()V")
	constructor() {
	}

	@OriginalMember(owner = "client!fb", name = "<init>", descriptor = "(Lclient!fb;)V")
	constructor(@OriginalArg(0) vertexNormal: VertexNormal?) {
		this.x = vertexNormal!!.x
		this.y = vertexNormal.y
		this.z = vertexNormal.z
		this.magnitude = vertexNormal.magnitude
	}
}
