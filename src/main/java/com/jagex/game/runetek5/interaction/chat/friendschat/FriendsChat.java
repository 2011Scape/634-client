package com.jagex.game.runetek5.interaction.chat.friendschat;

import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.ClientPacket;
import com.jagex.game.runetek5.network.BufferedConnectionContext;
import com.jagex.game.runetek5.network.packet.TcpPacket;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.config.rsinterface.cs2.CS2Executor;
import com.jagex.game.runetek5.FriendsChatPlayer;
import com.jagex.game.runetek5.network.packet.ClientProt;
import com.jagex.game.runetek5.utils.Utils;

public final class FriendsChat {
	@OriginalMember(owner = "client!ig", name = "L", descriptor = "I")
	public static int dragParentY = -1;

	private FriendsChat() {
		/* empty */
	}
	@OriginalMember(owner = "client!lga", name = "v", descriptor = "Ljava/lang/String;")
	public static String clanChatOwnerName = null;

	@OriginalMember(owner = "client!ws", name = "a", descriptor = "(BLjava/lang/String;)V")
	public static void kickFriendsChatUser(@OriginalArg(1) String string) {
		if (FriendsChat.clanChatUsers != null) {
			@Pc(15) TcpPacket packet = TcpPacket.createPacket(ClientProt.FRIENDCHAT_KICKUSER, TcpPacket.isaac);
			packet.buffer.writeByte(Utils.getEncodedStringLength(string));
			packet.buffer.writeString(string);
			BufferedConnectionContext.queuePacket(packet);
		}
	}
	@OriginalMember(owner = "client!tba", name = "b", descriptor = "(I)V")
	public static void sendLeaveFriendChat() {
		@Pc(18) TcpPacket packet = TcpPacket.createPacket(CS2Executor.FRIENDCHAT_JOIN_LEAVE, TcpPacket.isaac);
		packet.buffer.writeByte(0);
		BufferedConnectionContext.queuePacket(packet);
	}

	@OriginalMember(owner = "client!cw", name = "h", descriptor = "B")
	public static byte myFriendsChatRank;

	@OriginalMember(owner = "client!saa", name = "a", descriptor = "(Ljava/lang/String;I)V")
	public static void sendJoinFriendChat(@OriginalArg(0) String string) {
		if (!string.equals("")) {
			@Pc(25) TcpPacket packet = TcpPacket.createPacket(CS2Executor.FRIENDCHAT_JOIN_LEAVE, TcpPacket.isaac);
			packet.buffer.writeByte(Utils.getEncodedStringLength(string));
			packet.buffer.writeString(string);
			BufferedConnectionContext.queuePacket(packet);
		}
	}

	@OriginalMember(owner = "client!sb", name = "j", descriptor = "B")
	public static byte friendChatMinKickRank;

	@OriginalMember(owner = "client!iu", name = "c", descriptor = "Ljava/lang/String;")
	public static String friendsChatName = null;
	@OriginalMember(owner = "client!pha", name = "e", descriptor = "[Lclient!hi;")
	public static FriendsChatPlayer[] clanChatUsers;
	@OriginalMember(owner = "client!ida", name = "g", descriptor = "I")
	public static int clanChatUserCount;
	@OriginalMember(owner = "client!sj", name = "e", descriptor = "I")
	public static int lastFriendsChatUpdateTick = 0;
	@OriginalMember(owner = "client!bj", name = "a", descriptor = "(Ljava/lang/String;Z)Ljava/lang/String;")
	public static String formatDisplayName(@OriginalArg(0) String string) {
		@Pc(15) String string2 = Utils.decodeUidName(Utils.encodeBase37(string));
		if (string2 == null) {
			string2 = "";
		}
		return string2;
	}
}
