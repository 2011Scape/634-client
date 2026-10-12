package com.jagex.game.runetek5

import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField

object Protocol {

	@OriginalMember(owner = "client!jc", name = "t", descriptor = "[I")
	@JvmField
	val removedIds: IntArray = intArrayOf(19, 55, 38, 155, 255, 110, 137, 205, 76)

	@OriginalMember(owner = "client!mba", name = "H", descriptor = "Z")
	@JvmField
	var originPending: Boolean = false
}
