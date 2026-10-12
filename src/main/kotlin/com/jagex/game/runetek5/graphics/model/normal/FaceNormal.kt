package com.jagex.game.runetek5.graphics.model.normal

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!iga")
class FaceNormal {

	@OriginalMember(owner = "client!iga", name = "a", descriptor = "I")
	@JvmField
	var z: Int = 0

	@OriginalMember(owner = "client!iga", name = "b", descriptor = "I")
	@JvmField
	var y: Int = 0

	@OriginalMember(owner = "client!iga", name = "c", descriptor = "I")
	@JvmField
	var x: Int = 0

	@OriginalMember(owner = "client!iga", name = "<init>", descriptor = "()V")
	constructor() {
	}

	@OriginalMember(owner = "client!iga", name = "<init>", descriptor = "(Lclient!iga;)V")
	constructor(@OriginalArg(0) faceNormal: FaceNormal?) {
		this.x = faceNormal!!.x
		this.y = faceNormal.y
		this.z = faceNormal.z
	}
}
