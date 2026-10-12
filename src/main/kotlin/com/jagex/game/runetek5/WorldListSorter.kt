package com.jagex.game.runetek5

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.network.world.WorldList
import com.jagex.game.runetek5.network.WorldConnection
import com.jagex.game.runetek5.cache.node.PingRequest

object WorldListSorter {

	@OriginalMember(owner = "client!wo", name = "a", descriptor = "(ZIIIZ)V")
	@JvmStatic
	fun sortWorldList(@OriginalArg(0) secondaryAscending: Boolean, @OriginalArg(2) secondarySortMode: Int, @OriginalArg(3) sortMode: Int, @OriginalArg(4) ascending: Boolean) {
		WorldList.quicksort(secondarySortMode, 0, WorldConnection.WORLD_DESCRIPTORS_BYID.size - 1, secondaryAscending, sortMode, ascending)
		WorldListSorter.pingWorldIndex = 0
		WorldListSorter.aClass2_Sub36_2 = null
	}
	@OriginalMember(owner = "client!pt", name = "b", descriptor = "Lclient!mj;")
	@JvmField
	var aClass2_Sub36_2: PingRequest? = null

	@OriginalMember(owner = "client!rs", name = "a", descriptor = "I")
	@JvmField
	var pingWorldIndex: Int = 0
}
