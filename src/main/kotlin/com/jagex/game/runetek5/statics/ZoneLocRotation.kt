package com.jagex.game.runetek5.statics

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

object ZoneLocRotation {

	@OriginalMember(owner = "client!tj", name = "a", descriptor = "(IIIZIII)I")
	@JvmStatic
	fun localOffsetX(@OriginalArg(0) localY: Int, @OriginalArg(1) localX: Int, @OriginalArg(2) rotation: Int, @OriginalArg(4) sizeY: Int, @OriginalArg(5) sizeX: Int, @OriginalArg(6) locRotation: Int): Int {
		var sizeY: Int = sizeY
		var sizeX: Int = sizeX
		@Pc(3) val rotationMasked: Int = rotation and 0x3
		if ((locRotation and 0x1) == 1) {
			@Pc(21) val temp: Int = sizeX
			sizeX = sizeY
			sizeY = temp
		}
		if (rotationMasked == 0) {
			return localX
		} else if (rotationMasked == 1) {
			return localY
		} else if (rotationMasked == 2) {
			return 7 + 1 - localX - sizeX
		} else {
			return 1 + 7 - sizeY - localY
		}
	}

	@OriginalMember(owner = "client!mt", name = "a", descriptor = "(IIIIIIB)I")
	@JvmStatic
	fun localOffsetZ(@OriginalArg(0) x: Int, @OriginalArg(1) sizeY: Int, @OriginalArg(2) locRotation: Int, @OriginalArg(3) sizeX: Int, @OriginalArg(4) z: Int, @OriginalArg(5) chunkRotation: Int): Int {
		var sizeY: Int = sizeY
		var sizeX: Int = sizeX
		@Pc(3) val rotation: Int = chunkRotation and 0x3
		if ((locRotation and 0x1) == 1) {
			@Pc(16) val swap: Int = sizeX
			sizeX = sizeY
			sizeY = swap
		}
		if (rotation == 0) {
			return z
		} else if (rotation == 1) {
			return 7 + 1 - x - sizeX
		} else if (rotation == 2) {
			return 7 + 1 - z - sizeY
		} else {
			return x
		}
	}
}
