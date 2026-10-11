package com.jagex.game.runetek5;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

public final class TerrainBlockRotationY {

	@OriginalMember(owner = "client!lfa", name = "a", descriptor = "(IZII)I")
	public static int rotateTerrainBlockY(@OriginalArg(0) int localX, @OriginalArg(2) int localY, @OriginalArg(3) int rotation) {
		@Pc(3) int rotationMasked = rotation & 0x3;
		if (rotationMasked == 0) {
			return localY;
		} else if (rotationMasked == 1) {
			return 7 - localX;
		} else if (rotationMasked == 2) {
			return 7 - localY;
		} else {
			return localX;
		}
	}
}
