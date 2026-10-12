package com.jagex.game.runetek5

import org.openrs2.deob.annotation.OriginalMember

object Chat {

	@OriginalMember(owner = "client!uf", name = "K", descriptor = "I")
	@JvmField
	var hsvToRgbUsers: Int = 0
	@OriginalMember(owner = "client!es", name = "s", descriptor = "[J")
	@JvmField
	val CHAT_MESSAGE_UNIQUE_HASHES: LongArray = LongArray(100)
}
