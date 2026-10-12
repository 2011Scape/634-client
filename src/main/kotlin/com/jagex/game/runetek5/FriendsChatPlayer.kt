package com.jagex.game.runetek5

import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!hi")
class FriendsChatPlayer {

	@OriginalMember(owner = "client!hi", name = "c", descriptor = "Ljava/lang/String;")
	@JvmField
	var username: String? = null

	@OriginalMember(owner = "client!hi", name = "e", descriptor = "I")
	@JvmField
	var worldId: Int = 0

	@OriginalMember(owner = "client!hi", name = "g", descriptor = "B")
	@JvmField
	var rank: Byte = 0

	@OriginalMember(owner = "client!hi", name = "i", descriptor = "Ljava/lang/String;")
	@JvmField
	var textName: String? = null

	@OriginalMember(owner = "client!hi", name = "j", descriptor = "Ljava/lang/String;")
	@JvmField
	var worldName: String? = null

	@OriginalMember(owner = "client!hi", name = "k", descriptor = "Ljava/lang/String;")
	@JvmField
	var displayName: String? = null
}
