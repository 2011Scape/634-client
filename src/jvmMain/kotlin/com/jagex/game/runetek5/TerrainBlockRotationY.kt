package com.jagex.game.runetek5

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

object TerrainBlockRotationY {

	@OriginalMember(owner = "client!lfa", name = "a", descriptor = "(IZII)I")
	@JvmStatic
	fun rotateTerrainBlockY(@OriginalArg(0) localX: Int, @OriginalArg(2) localY: Int, @OriginalArg(3) rotation: Int): Int {
		@Pc(3) val rotationMasked: Int = rotation and 0x3
		if (rotationMasked == 0) {
			return localY
		} else if (rotationMasked == 1) {
			return 7 - localX
		} else if (rotationMasked == 2) {
			return 7 - localY
		} else {
			return localX
		}
	}
}
