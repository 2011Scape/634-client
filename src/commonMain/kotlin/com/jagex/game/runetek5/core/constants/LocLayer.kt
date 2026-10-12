package com.jagex.game.runetek5.core.constants

import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField

object LocLayer {

	const val WALL: Int = 0

	const val WALLDECOR: Int = 1

	const val GROUND: Int = 2

	const val GROUNDDECOR: Int = 3

	@OriginalMember(owner = "client!id", name = "b", descriptor = "[I")
	@JvmField
	val OBJECT_TYPE_SLOTS: IntArray = intArrayOf(0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3)
}
