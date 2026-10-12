package com.jagex.game.runetek5.entity

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField

@OriginalClass("client!vo")
class GroundRenderTask : EntityNode {

	@OriginalMember(owner = "client!vo", name = "j", descriptor = "I")
	@JvmField
	val level: Int

	@OriginalMember(owner = "client!vo", name = "<init>", descriptor = "(I)V")
	constructor(@OriginalArg(0) level: Int) : super() {
		this.level = level
	}
}
