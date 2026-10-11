package com.jagex.game.runetek5;

import com.jagex.game.runetek5.network.world.WorldDescriptor;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.utils.Utils;
import com.jagex.game.runetek5.client.ClientState;

public final class WorldComparator {

	@OriginalMember(owner = "client!mfa", name = "a", descriptor = "(Lclient!mf;Lclient!mf;ZIB)I")
	public static int compareWorlds(@OriginalArg(0) WorldDescriptor gameWorld, @OriginalArg(1) WorldDescriptor gameWorld2, @OriginalArg(2) boolean ascending, @OriginalArg(3) int sortMode) {
		@Pc(9) int value1;
		@Pc(12) int value2;
		if (sortMode == 1) {
			value1 = gameWorld.population;
			value2 = gameWorld2.population;
			if (!ascending) {
				if (value1 == -1) {
					value1 = 2001;
				}
				if (value2 == -1) {
					value2 = 2001;
				}
			}
			return value1 - value2;
		} else if (sortMode == 2) {
			return Utils.compareLocaleAware(gameWorld2.getCountry().name, ClientState.language, gameWorld.getCountry().name);
		} else if (sortMode == 3) {
			if (gameWorld.address.equals("-")) {
				if (gameWorld2.address.equals("-")) {
					return 0;
				} else if (ascending) {
					return -1;
				} else {
					return 1;
				}
			} else if (gameWorld2.address.equals("-")) {
				return ascending ? 1 : -1;
			} else {
				return Utils.compareLocaleAware(gameWorld2.address, ClientState.language, gameWorld.address);
			}
		} else if (sortMode == 4) {
			if (gameWorld.isLootshare()) {
				return gameWorld2.isLootshare() ? 0 : 1;
			} else if (gameWorld2.isLootshare()) {
				return -1;
			} else {
				return 0;
			}
		} else if (sortMode == 5) {
			if (gameWorld.isQuickChat()) {
				return gameWorld2.isQuickChat() ? 0 : 1;
			} else if (gameWorld2.isQuickChat()) {
				return -1;
			} else {
				return 0;
			}
		} else if (sortMode == 6) {
			if (gameWorld.isPvp()) {
				return gameWorld2.isPvp() ? 0 : 1;
			} else if (gameWorld2.isPvp()) {
				return -1;
			} else {
				return 0;
			}
		} else if (sortMode == 7) {
			if (gameWorld.isMembers()) {
				return gameWorld2.isMembers() ? 0 : 1;
			} else if (gameWorld2.isMembers()) {
				return -1;
			} else {
				return 0;
			}
		} else if (sortMode == 8) {
			value1 = gameWorld.ping;
			value2 = gameWorld2.ping;
			if (ascending) {
				if (value2 == 1000) {
					value2 = -1;
				}
				if (value1 == 1000) {
					value1 = -1;
				}
			} else {
				if (value2 == -1) {
					value2 = 1000;
				}
				if (value1 == -1) {
					value1 = 1000;
				}
			}
			return value1 - value2;
		} else {
			return gameWorld.worldNumber - gameWorld2.worldNumber;
		}
	}
	@OriginalMember(owner = "client!o", name = "a", descriptor = "(BILclient!mf;IZZLclient!mf;)I")
	public static int compareWorldsWithSecondary(@OriginalArg(1) int sortMode, @OriginalArg(2) WorldDescriptor gameWorld, @OriginalArg(3) int secondarySortMode, @OriginalArg(4) boolean ascending, @OriginalArg(5) boolean secondaryAscending, @OriginalArg(6) WorldDescriptor gameWorld2) {
		@Pc(10) int result = WorldComparator.compareWorlds(gameWorld2, gameWorld, ascending, sortMode);
		if (result != 0) {
			return ascending ? -result : result;
		} else if (secondarySortMode == -1) {
			return 0;
		} else {
			@Pc(37) int secondaryResult = WorldComparator.compareWorlds(gameWorld2, gameWorld, secondaryAscending, secondarySortMode);
			return secondaryAscending ? -secondaryResult : secondaryResult;
		}
	}
}
