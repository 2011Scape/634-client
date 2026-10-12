package com.jagex.game.runetek5

import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.utils.Utils

@OriginalClass("client!lv")
class MillisNanoTimer : AbstractNanoTimer() {

	@OriginalMember(owner = "client!lv", name = "b", descriptor = "(I)J")
	override fun nanoTime(): Long {
		return Utils.currentTimeMillis() * 1000000L
	}
}
