package com.jagex.game.runetek5;

import com.jagex.game.runetek5.js5.Js5WorkerThread;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.interaction.chat.ChatBox;

public final class ChatLineCount {

	@OriginalMember(owner = "client!fa", name = "d", descriptor = "(I)I")
	public static int getChatLineCount() {
		return ChatBox.NUM_CHAT_LINES;
	}
}
