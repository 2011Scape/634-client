package com.jagex.game.runetek5.network.world;

import com.jagex.game.runetek5.network.world.WorldDescriptor;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.network.WorldConnection;
import com.jagex.game.runetek5.WorldComparator;

public final class WorldList {

	@OriginalMember(owner = "client!mh", name = "a", descriptor = "(IIIZBIZ)V")
	public static void quicksort(@OriginalArg(0) int secondarySortMode, @OriginalArg(1) int start, @OriginalArg(2) int end, @OriginalArg(3) boolean secondaryAscending, @OriginalArg(5) int sortMode, @OriginalArg(6) boolean ascending) {
		if (end <= start) {
			return;
		}
		@Pc(18) int center = (end + start) / 2;
		@Pc(20) int storeIndex = start;
		@Pc(24) WorldDescriptor gameWorld = WorldConnection.WORLD_DESCRIPTORS_BYID[center];
		WorldConnection.WORLD_DESCRIPTORS_BYID[center] = WorldConnection.WORLD_DESCRIPTORS_BYID[end];
		WorldConnection.WORLD_DESCRIPTORS_BYID[end] = gameWorld;
		for (@Pc(36) int i = start; i < end; i++) {
			if (WorldComparator.compareWorldsWithSecondary(sortMode, gameWorld, secondarySortMode, ascending, secondaryAscending, WorldConnection.WORLD_DESCRIPTORS_BYID[i]) <= 0) {
				@Pc(53) WorldDescriptor gameWorld2 = WorldConnection.WORLD_DESCRIPTORS_BYID[i];
				WorldConnection.WORLD_DESCRIPTORS_BYID[i] = WorldConnection.WORLD_DESCRIPTORS_BYID[storeIndex];
				WorldConnection.WORLD_DESCRIPTORS_BYID[storeIndex++] = gameWorld2;
			}
		}
		WorldConnection.WORLD_DESCRIPTORS_BYID[end] = WorldConnection.WORLD_DESCRIPTORS_BYID[storeIndex];
		WorldConnection.WORLD_DESCRIPTORS_BYID[storeIndex] = gameWorld;
		quicksort(secondarySortMode, start, storeIndex - 1, secondaryAscending, sortMode, ascending);
		quicksort(secondarySortMode, storeIndex + 1, end, secondaryAscending, sortMode, ascending);
	}
}
