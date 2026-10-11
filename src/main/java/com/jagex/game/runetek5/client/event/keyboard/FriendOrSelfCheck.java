package com.jagex.game.runetek5.client.event.keyboard;

import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.interaction.FriendList;
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate;

public final class FriendOrSelfCheck {

	@OriginalMember(owner = "client!ica", name = "a", descriptor = "(Ljava/lang/String;B)Z")
	public static boolean isFriendOrSelf(@OriginalArg(0) String string) {
		if (string == null) {
			return false;
		}
		for (@Pc(11) int i = 0; i < FriendList.FRIEND_COUNT; i++) {
			if (string.equalsIgnoreCase(FriendList.friendNames[i])) {
				return true;
			}
		}
		return string.equalsIgnoreCase(PlayerUpdate.localPlayer.displayName);
	}
}
