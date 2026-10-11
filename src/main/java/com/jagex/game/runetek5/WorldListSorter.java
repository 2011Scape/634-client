package com.jagex.game.runetek5;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.network.world.WorldList;
import com.jagex.game.runetek5.network.WorldConnection;
import com.jagex.game.runetek5.cache.node.PingRequest;

public final class WorldListSorter {

	@OriginalMember(owner = "client!wo", name = "a", descriptor = "(ZIIIZ)V")
	public static void sortWorldList(@OriginalArg(0) boolean secondaryAscending, @OriginalArg(2) int secondarySortMode, @OriginalArg(3) int sortMode, @OriginalArg(4) boolean ascending) {
		WorldList.quicksort(secondarySortMode, 0, WorldConnection.WORLD_DESCRIPTORS_BYID.length - 1, secondaryAscending, sortMode, ascending);
		WorldListSorter.pingWorldIndex = 0;
		WorldListSorter.aClass2_Sub36_2 = null;
	}
	@OriginalMember(owner = "client!pt", name = "b", descriptor = "Lclient!mj;")
	public static PingRequest aClass2_Sub36_2 = null;

	@OriginalMember(owner = "client!rs", name = "a", descriptor = "I")
	public static int pingWorldIndex = 0;
}
