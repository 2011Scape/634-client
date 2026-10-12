package com.jagex.game.runetek5

import com.jagex.game.runetek5.interaction.chat.ChatBox
import org.openrs2.deob.annotation.OriginalMember

object ChatLineCount {

	@OriginalMember(owner = "client!fa", name = "d", descriptor = "(I)I")
	@JvmStatic
	fun getChatLineCount(): Int {
		return ChatBox.NUM_CHAT_LINES
	}
}
