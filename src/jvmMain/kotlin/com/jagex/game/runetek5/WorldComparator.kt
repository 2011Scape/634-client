package com.jagex.game.runetek5

import com.jagex.game.runetek5.network.world.WorldDescriptor
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.utils.Utils
import com.jagex.game.runetek5.client.ClientState

object WorldComparator {

	@OriginalMember(owner = "client!mfa", name = "a", descriptor = "(Lclient!mf;Lclient!mf;ZIB)I")
	@JvmStatic
	fun compareWorlds(@OriginalArg(0) gameWorld: WorldDescriptor?, @OriginalArg(1) gameWorld2: WorldDescriptor?, @OriginalArg(2) ascending: Boolean, @OriginalArg(3) sortMode: Int): Int {
		@Pc(9) var value1: Int
		@Pc(12) var value2: Int
		if (sortMode == 1) {
			value1 = gameWorld!!.population
			value2 = gameWorld2!!.population
			if (!ascending) {
				if (value1 == -1) {
					value1 = 2001
				}
				if (value2 == -1) {
					value2 = 2001
				}
			}
			return value1 - value2
		} else if (sortMode == 2) {
			return Utils.compareLocaleAware(gameWorld2!!.getCountry()!!.name!!, ClientState.language, gameWorld!!.getCountry()!!.name!!)
		} else if (sortMode == 3) {
			if (gameWorld!!.address!!.equals("-")) {
				if (gameWorld2!!.address!!.equals("-")) {
					return 0
				} else if (ascending) {
					return -1
				} else {
					return 1
				}
			} else if (gameWorld2!!.address!!.equals("-")) {
				return if (ascending) 1 else -1
			} else {
				return Utils.compareLocaleAware(gameWorld2.address!!, ClientState.language, gameWorld.address!!)
			}
		} else if (sortMode == 4) {
			if (gameWorld!!.isLootshare()) {
				return if (gameWorld2!!.isLootshare()) 0 else 1
			} else if (gameWorld2!!.isLootshare()) {
				return -1
			} else {
				return 0
			}
		} else if (sortMode == 5) {
			if (gameWorld!!.isQuickChat()) {
				return if (gameWorld2!!.isQuickChat()) 0 else 1
			} else if (gameWorld2!!.isQuickChat()) {
				return -1
			} else {
				return 0
			}
		} else if (sortMode == 6) {
			if (gameWorld!!.isPvp()) {
				return if (gameWorld2!!.isPvp()) 0 else 1
			} else if (gameWorld2!!.isPvp()) {
				return -1
			} else {
				return 0
			}
		} else if (sortMode == 7) {
			if (gameWorld!!.isMembers()) {
				return if (gameWorld2!!.isMembers()) 0 else 1
			} else if (gameWorld2!!.isMembers()) {
				return -1
			} else {
				return 0
			}
		} else if (sortMode == 8) {
			value1 = gameWorld!!.ping
			value2 = gameWorld2!!.ping
			if (ascending) {
				if (value2 == 1000) {
					value2 = -1
				}
				if (value1 == 1000) {
					value1 = -1
				}
			} else {
				if (value2 == -1) {
					value2 = 1000
				}
				if (value1 == -1) {
					value1 = 1000
				}
			}
			return value1 - value2
		} else {
			return gameWorld!!.worldNumber - gameWorld2!!.worldNumber
		}
	}
	@OriginalMember(owner = "client!o", name = "a", descriptor = "(BILclient!mf;IZZLclient!mf;)I")
	@JvmStatic
	fun compareWorldsWithSecondary(@OriginalArg(1) sortMode: Int, @OriginalArg(2) gameWorld: WorldDescriptor?, @OriginalArg(3) secondarySortMode: Int, @OriginalArg(4) ascending: Boolean, @OriginalArg(5) secondaryAscending: Boolean, @OriginalArg(6) gameWorld2: WorldDescriptor?): Int {
		@Pc(10) val result: Int = WorldComparator.compareWorlds(gameWorld2, gameWorld, ascending, sortMode)
		if (result != 0) {
			return if (ascending) -result else result
		} else if (secondarySortMode == -1) {
			return 0
		} else {
			@Pc(37) val secondaryResult: Int = WorldComparator.compareWorlds(gameWorld2, gameWorld, secondaryAscending, secondarySortMode)
			return if (secondaryAscending) -secondaryResult else secondaryResult
		}
	}
}
