package com.jagex.game.runetek5.statics;

import com.jagex.game.runetek5.PrimitiveType;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

public final class ZoneLocRotation {

	@OriginalMember(owner = "client!tj", name = "a", descriptor = "(IIIZIII)I")
	public static int localOffsetX(@OriginalArg(0) int localY, @OriginalArg(1) int localX, @OriginalArg(2) int rotation, @OriginalArg(4) int sizeY, @OriginalArg(5) int sizeX, @OriginalArg(6) int locRotation) {
		@Pc(3) int rotationMasked = rotation & 0x3;
		if ((locRotation & 0x1) == 1) {
			@Pc(21) int temp = sizeX;
			sizeX = sizeY;
			sizeY = temp;
		}
		if (rotationMasked == 0) {
			return localX;
		} else if (rotationMasked == 1) {
			return localY;
		} else if (rotationMasked == 2) {
			return 7 + 1 - localX - sizeX;
		} else {
			return 1 + 7 - sizeY - localY;
		}
	}
	@OriginalMember(owner = "client!mt", name = "a", descriptor = "(IIIIIIB)I")
	public static int localOffsetZ(@OriginalArg(0) int x, @OriginalArg(1) int sizeY, @OriginalArg(2) int locRotation, @OriginalArg(3) int sizeX, @OriginalArg(4) int z, @OriginalArg(5) int chunkRotation) {
		@Pc(3) int rotation = chunkRotation & 0x3;
		if ((locRotation & 0x1) == 1) {
			@Pc(16) int swap = sizeX;
			sizeX = sizeY;
			sizeY = swap;
		}
		if (rotation == 0) {
			return z;
		} else if (rotation == 1) {
			return 7 + 1 - x - sizeX;
		} else if (rotation == 2) {
			return 7 + 1 - z - sizeY;
		} else {
			return x;
		}
	}
}
