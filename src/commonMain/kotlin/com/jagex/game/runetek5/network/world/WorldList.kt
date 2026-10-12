package com.jagex.game.runetek5.network.world

import com.jagex.game.runetek5.WorldComparator
import com.jagex.game.runetek5.network.WorldConnection
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmStatic

object WorldList {

	@OriginalMember(owner = "client!mh", name = "a", descriptor = "(IIIZBIZ)V")
	@JvmStatic
	fun quicksort(@OriginalArg(0) secondarySortMode: Int, @OriginalArg(1) start: Int, @OriginalArg(2) end: Int, @OriginalArg(3) secondaryAscending: Boolean, @OriginalArg(5) sortMode: Int, @OriginalArg(6) ascending: Boolean) {
		if (end <= start) {
			return
		}
		@Pc(18) val center: Int = (end + start) / 2
		@Pc(20) var storeIndex: Int = start
		@Pc(24) val gameWorld: WorldDescriptor? = WorldConnection.WORLD_DESCRIPTORS_BYID[center]
		WorldConnection.WORLD_DESCRIPTORS_BYID[center] = WorldConnection.WORLD_DESCRIPTORS_BYID[end]
		WorldConnection.WORLD_DESCRIPTORS_BYID[end] = gameWorld
		@Pc(36) var i: Int = start
		while (i < end) {
			if (WorldComparator.compareWorldsWithSecondary(sortMode, gameWorld, secondarySortMode, ascending, secondaryAscending, WorldConnection.WORLD_DESCRIPTORS_BYID[i]) <= 0) {
				@Pc(53) val gameWorld2: WorldDescriptor? = WorldConnection.WORLD_DESCRIPTORS_BYID[i]
				WorldConnection.WORLD_DESCRIPTORS_BYID[i] = WorldConnection.WORLD_DESCRIPTORS_BYID[storeIndex]
				WorldConnection.WORLD_DESCRIPTORS_BYID[storeIndex++] = gameWorld2
			}
			i++
		}
		WorldConnection.WORLD_DESCRIPTORS_BYID[end] = WorldConnection.WORLD_DESCRIPTORS_BYID[storeIndex]
		WorldConnection.WORLD_DESCRIPTORS_BYID[storeIndex] = gameWorld
		quicksort(secondarySortMode, start, storeIndex - 1, secondaryAscending, sortMode, ascending)
		quicksort(secondarySortMode, storeIndex + 1, end, secondaryAscending, sortMode, ascending)
	}
}
