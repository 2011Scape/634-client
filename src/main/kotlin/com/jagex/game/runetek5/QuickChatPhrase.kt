package com.jagex.game.runetek5

import com.jagex.game.runetek5.config.quickchat.QuickchatMessageType
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!iia")
class QuickChatPhrase {

	@OriginalMember(owner = "client!iia", name = "d", descriptor = "Lclient!td;")
	@JvmField
	var type: QuickchatMessageType? = null

	@OriginalMember(owner = "client!iia", name = "e", descriptor = "I")
	@JvmField
	var id: Int = 0

	@OriginalMember(owner = "client!iia", name = "g", descriptor = "[I")
	@JvmField
	var values: IntArray? = null
}
