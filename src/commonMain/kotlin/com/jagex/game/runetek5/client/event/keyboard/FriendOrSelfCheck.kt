package com.jagex.game.runetek5.client.event.keyboard

import com.jagex.game.runetek5.js5.Js5ResourceProvider
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.interaction.FriendList
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate
import kotlin.jvm.JvmStatic

object FriendOrSelfCheck {

	@OriginalMember(owner = "client!ica", name = "a", descriptor = "(Ljava/lang/String;B)Z")
	@JvmStatic
	fun isFriendOrSelf(@OriginalArg(0) string: String?): Boolean {
		if (string == null) {
			return false
		}
		@Pc(11) var i: Int = 0
		while (i < FriendList.FRIEND_COUNT) {
			if (string.equals(FriendList.friendNames[i], ignoreCase = true)) {
				return true
			}
			i++
		}
		return string.equals(PlayerUpdate.localPlayer!!.displayName, ignoreCase = true)
	}
}
