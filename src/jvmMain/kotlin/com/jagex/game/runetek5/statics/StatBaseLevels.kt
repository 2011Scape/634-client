package com.jagex.game.runetek5.statics

import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

object StatBaseLevels {

	@OriginalMember(owner = "client!uca", name = "b", descriptor = "[I")
	@JvmField
	val statBaseLevels: IntArray = IntArray(25)

	@OriginalMember(owner = "client!sc", name = "i", descriptor = "[I")
	@JvmField
	val SKILL_MAX_LEVEL: IntArray = intArrayOf(99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 120)

	@OriginalMember(owner = "client!sv", name = "S", descriptor = "[I")
	@JvmField
	val SKILL_XP_FOR_LEVEL: IntArray = IntArray(120)

	init {
		@Pc(10) var sum: Int = 0
		@Pc(12) var level: Int = 0
		while (level < 120) {
			@Pc(17) val nextLevel: Int = level + 1
			@Pc(30) val offset: Int = (Math.pow(2.0, nextLevel.toDouble() / 7.0) * 300.0 + nextLevel.toDouble()).toInt()
			sum += offset
			StatBaseLevels.SKILL_XP_FOR_LEVEL[level] = sum / 4
			level++
		}
	}
}
