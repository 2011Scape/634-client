package com.jagex.game.runetek5

import com.jagex.game.runetek5.plugin.PluginRuntime
import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.client.menu.MiniMenu
import com.jagex.game.runetek5.interaction.chat.ChatBox
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmStatic

object ChatLineWriter {

	@OriginalMember(owner = "client!to", name = "a", descriptor = "(Ljava/lang/String;Ljava/lang/String;BILjava/lang/String;Ljava/lang/String;IILjava/lang/String;)V")
	@JvmStatic
	fun writeMessage(@OriginalArg(0) name: String?, @OriginalArg(1) message: String?, @OriginalArg(3) quickchatMessageId: Int, @OriginalArg(4) crownedName: String?, @OriginalArg(5) clan: String?, @OriginalArg(6) type: Int, @OriginalArg(7) effectFlags: Int, @OriginalArg(8) simpleName: String?) {
		@Pc(9) var chatLine: ChatLine? = ChatBox.CHAT_LINES[99]
		@Pc(11) var i: Int = 99
		while (i > 0) {
			ChatBox.CHAT_LINES[i] = ChatBox.CHAT_LINES[i - 1]
			i--
		}
		if (chatLine == null) {
			chatLine = ChatLine(type, effectFlags, crownedName, name, simpleName, clan, quickchatMessageId, message)
		} else {
			chatLine.set(type, effectFlags, simpleName, name, clan, quickchatMessageId, crownedName, message)
		}
		ChatBox.CHAT_LINES[0] = chatLine
		MiniMenu.lastChatMessageTick = ClientState.serverTick
		ChatBox.NUM_CHAT_LINES++
		chatLine.message = PluginRuntime.chatMessage(type, name, message, clan) // Not part of the original client: the plugins' ChatMessage, which may rewrite the line.
	}

	@OriginalMember(owner = "client!di", name = "a", descriptor = "(Ljava/lang/String;IBILjava/lang/String;Ljava/lang/String;Ljava/lang/String;)V")
	@JvmStatic
	fun writeMessage(@OriginalArg(0) string: String?, @OriginalArg(1) type: Int, @OriginalArg(3) effectFlags: Int, @OriginalArg(4) string2: String?, @OriginalArg(5) string3: String?, @OriginalArg(6) string4: String?) {
		ChatLineWriter.writeMessage(string, string2, -1, string3, null, type, effectFlags, string4)
	}

	@OriginalMember(owner = "client!qk", name = "a", descriptor = "(Ljava/lang/String;I)V")
	@JvmStatic
	fun writeUnfilterableMessage(@OriginalArg(0) string: String?) {
		ChatLineWriter.writeMessage("", 0, 0, string, "", "")
	}
}
