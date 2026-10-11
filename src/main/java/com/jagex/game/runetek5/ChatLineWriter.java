package com.jagex.game.runetek5;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.interaction.chat.ChatBox;
import com.jagex.game.runetek5.client.menu.MiniMenu;
import com.jagex.game.runetek5.client.ClientState;

public final class ChatLineWriter {

	@OriginalMember(owner = "client!to", name = "a", descriptor = "(Ljava/lang/String;Ljava/lang/String;BILjava/lang/String;Ljava/lang/String;IILjava/lang/String;)V")
	public static void writeMessage(@OriginalArg(0) String name, @OriginalArg(1) String message, @OriginalArg(3) int quickchatMessageId, @OriginalArg(4) String crownedName, @OriginalArg(5) String clan, @OriginalArg(6) int type, @OriginalArg(7) int effectFlags, @OriginalArg(8) String simpleName) {
		@Pc(9) ChatLine chatLine = ChatBox.CHAT_LINES[99];
		for (@Pc(11) int i = 99; i > 0; i--) {
			ChatBox.CHAT_LINES[i] = ChatBox.CHAT_LINES[i - 1];
		}
		if (chatLine == null) {
			chatLine = new ChatLine(type, effectFlags, crownedName, name, simpleName, clan, quickchatMessageId, message);
		} else {
			chatLine.set(type, effectFlags, simpleName, name, clan, quickchatMessageId, crownedName, message);
		}
		ChatBox.CHAT_LINES[0] = chatLine;
		MiniMenu.lastChatMessageTick = ClientState.serverTick;
		ChatBox.NUM_CHAT_LINES++;
	}
	@OriginalMember(owner = "client!di", name = "a", descriptor = "(Ljava/lang/String;IBILjava/lang/String;Ljava/lang/String;Ljava/lang/String;)V")
	public static void writeMessage(@OriginalArg(0) String string, @OriginalArg(1) int type, @OriginalArg(3) int effectFlags, @OriginalArg(4) String string2, @OriginalArg(5) String string3, @OriginalArg(6) String string4) {
		ChatLineWriter.writeMessage(string, string2, -1, string3, null, type, effectFlags, string4);
	}
	@OriginalMember(owner = "client!qk", name = "a", descriptor = "(Ljava/lang/String;I)V")
	public static void writeUnfilterableMessage(@OriginalArg(0) String string) {
		ChatLineWriter.writeMessage("", 0, 0, string, "", "");
	}
}
