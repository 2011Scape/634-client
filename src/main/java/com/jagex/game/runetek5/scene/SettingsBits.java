package com.jagex.game.runetek5.scene;

import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.core.constants.TileFlag;

public final class SettingsBits {
	@OriginalMember(owner = "client!raa", name = "w", descriptor = "[[[B")
	public static byte[][][] tileFunctionMasks;

	private SettingsBits() {
		/* empty */
	}
	@OriginalMember(owner = "client!su", name = "a", descriptor = "(IIII)V")
	public static void allocateTileFunctionMasks(@OriginalArg(1) int sceneSizeY, @OriginalArg(2) int sceneSizeX) {
		SettingsBits.tileFunctionMasks = new byte[4][sceneSizeX][sceneSizeY];
	}
	@OriginalMember(owner = "client!jca", name = "b", descriptor = "(B)V")
	public static void clearTileFunctionMasks() {
		for (@Pc(3) int i = 0; i < SettingsBits.tileFunctionMasks.length; i++) {
			for (@Pc(7) int j = 0; j < SettingsBits.tileFunctionMasks[0].length; j++) {
				for (@Pc(11) int k = 0; k < SettingsBits.tileFunctionMasks[0][0].length; k++) {
					SettingsBits.tileFunctionMasks[i][j][k] = 0;
				}
			}
		}
	}
	@OriginalMember(owner = "client!jl", name = "a", descriptor = "(IZIII)Z")
	public static boolean isVisibleFromLevel(@OriginalArg(0) int z, @OriginalArg(2) int x, @OriginalArg(3) int renderLevel, @OriginalArg(4) int level) {
		if ((SettingsBits.tileFunctionMasks[0][x][z] & TileFlag.BRIDGE) != 0) {
			return true;
		} else if ((SettingsBits.tileFunctionMasks[level][x][z] & TileFlag.INVISIBLE) == 0) {
			return renderLevel == SettingsBits.getRenderLevel(level, z, x);
		} else {
			return false;
		}
	}

	@OriginalMember(owner = "client!bb", name = "a", descriptor = "(IIZ)Z")
	public static boolean isBridge(@OriginalArg(0) int y, @OriginalArg(1) int x) {
		if (x >= 0 && y >= 0 && x < SettingsBits.tileFunctionMasks[1].length && SettingsBits.tileFunctionMasks[1][x].length > y) {
			return (SettingsBits.tileFunctionMasks[1][x][y] & TileFlag.BRIDGE) != 0;
		} else {
			return false;
		}
	}
	@OriginalMember(owner = "client!od", name = "b", descriptor = "(IIII)I")
	public static int getRenderLevel(@OriginalArg(1) int level, @OriginalArg(2) int z, @OriginalArg(3) int x) {
		if ((SettingsBits.tileFunctionMasks[level][x][z] & 0x8) == 0) {
			return level <= 0 || (SettingsBits.tileFunctionMasks[1][x][z] & 0x2) == 0 ? level : level - 1;
		} else {
			return 0;
		}
	}
}
