package com.jagex.game.runetek5.interaction;

import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.ClientPacket;
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate;
import com.jagex.game.runetek5.network.BufferedConnectionContext;
import com.jagex.game.runetek5.network.packet.TcpPacket;
import com.jagex.game.runetek5.tools.LocalizedText;
import com.jagex.game.runetek5.interaction.chat.ChatBox;
import com.jagex.game.runetek5.network.packet.ClientProt;
import com.jagex.game.runetek5.client.login.LoginManager;
import com.jagex.game.runetek5.utils.Utils;
import com.jagex.game.runetek5.client.ClientState;

public final class FriendList {
	@OriginalMember(owner = "client!tg", name = "u", descriptor = "[Ljava/lang/String;")
	public static final String[] LANGUAGE_CODES = new String[] { "en", "de", "fr", "pt", "nl" };

	private FriendList() {
		/* empty */
	}
	@OriginalMember(owner = "client!vj", name = "I", descriptor = "I")
	public static int FRIEND_COUNT = 0;
	@OriginalMember(owner = "client!ht", name = "a", descriptor = "(BLjava/lang/String;)I")
	public static int getLanguageId(@OriginalArg(1) String string) {
		for (@Pc(5) int i = 0; i < FriendList.LANGUAGE_CODES.length; i++) {
			if (FriendList.LANGUAGE_CODES[i].equalsIgnoreCase(string)) {
				return i;
			}
		}
		return -1;
	}
	@OriginalMember(owner = "client!lc", name = "b", descriptor = "[Ljava/lang/String;")
	public static final String[] friendNames = new String[200];

	@OriginalMember(owner = "client!ha", name = "r", descriptor = "[I")
	public static final int[] friendWorldIds = new int[200];

	@OriginalMember(owner = "client!vi", name = "j", descriptor = "[Ljava/lang/String;")
	public static final String[] friendWorldNames = new String[200];

	@OriginalMember(owner = "client!tn", name = "a", descriptor = "(ZLjava/lang/String;)V")
	public static void removeFriend(@OriginalArg(1) String string) {
		if (string == null) {
			return;
		}
		if (string.startsWith("*")) {
			string = string.substring(1);
		}
		@Pc(20) String string2 = Utils.formatDisplayName(string);
		if (string2 == null) {
			return;
		}
		for (@Pc(25) int i = 0; i < FriendList.FRIEND_COUNT; i++) {
			@Pc(31) String string3 = FriendList.friendNames[i];
			if (string3.startsWith("*")) {
				string3 = string3.substring(1);
			}
			string3 = Utils.formatDisplayName(string3);
			if (string3 != null && string3.equals(string2)) {
				FriendList.FRIEND_COUNT--;
				for (@Pc(55) int j = i; j < FriendList.FRIEND_COUNT; j++) {
					FriendList.friendNames[j] = FriendList.friendNames[j + 1];
					FriendList.friendPreviousNames[j] = FriendList.friendPreviousNames[j + 1];
					FriendList.friendWorldIds[j] = FriendList.friendWorldIds[j + 1];
					FriendList.friendWorldNames[j] = FriendList.friendWorldNames[j + 1];
					FriendList.friendRanks[j] = FriendList.friendRanks[j + 1];
					FriendList.friendSameGame[j] = FriendList.friendSameGame[j + 1];
				}
				FriendList.lastFriendIgnoreUpdateTick = ClientState.serverTick;
				@Pc(120) TcpPacket packet = TcpPacket.createPacket(ClientProt.FRIENDLIST_DEL, TcpPacket.isaac);
				packet.buffer.writeByte(Utils.getEncodedStringLength(string));
				packet.buffer.writeString(string);
				BufferedConnectionContext.queuePacket(packet);
				break;
			}
		}
	}

	@OriginalMember(owner = "client!vj", name = "a", descriptor = "(ILjava/lang/String;)V")
	public static void addFriend(@OriginalArg(1) String string) {
		if (string == null) {
			return;
		}
		@Pc(30) String string2;
		if (FriendList.FRIEND_COUNT >= 200 && !LoginManager.isMember || FriendList.FRIEND_COUNT >= 200) {
			ChatBox.writeGameMessage(LocalizedText.aClass351_12.get(ClientState.language));
			string2 = LocalizedText.aClass351_13.get(ClientState.language);
			if (string2 != null) {
				ChatBox.writeGameMessage(string2);
			}
			return;
		}
		string2 = Utils.formatDisplayName(string);
		if (string2 == null) {
			return;
		}
		@Pc(85) String string3;
		for (@Pc(46) int i = 0; i < FriendList.FRIEND_COUNT; i++) {
			@Pc(54) String string4 = Utils.formatDisplayName(FriendList.friendNames[i]);
			if (string4 != null && string4.equals(string2)) {
				ChatBox.writeGameMessage(string + LocalizedText.aClass351_40.get(ClientState.language));
				return;
			}
			if (FriendList.friendPreviousNames[i] != null) {
				string3 = Utils.formatDisplayName(FriendList.friendPreviousNames[i]);
				if (string3 != null && string3.equals(string2)) {
					ChatBox.writeGameMessage(string + LocalizedText.aClass351_40.get(ClientState.language));
					return;
				}
			}
		}
		for (@Pc(116) int j = 0; j < IgnoreList.ignoreCount; j++) {
			string3 = Utils.formatDisplayName(IgnoreList.ignoreNames[j]);
			if (string3 != null && string3.equals(string2)) {
				ChatBox.writeGameMessage(LocalizedText.aClass351_45.get(ClientState.language) + string + LocalizedText.aClass351_46.get(ClientState.language));
				return;
			}
			if (IgnoreList.ignorePreviousNames[j] != null) {
				@Pc(160) String string5 = Utils.formatDisplayName(IgnoreList.ignorePreviousNames[j]);
				if (string5 != null && string5.equals(string2)) {
					ChatBox.writeGameMessage(LocalizedText.aClass351_45.get(ClientState.language) + string + LocalizedText.aClass351_46.get(ClientState.language));
					return;
				}
			}
		}
		if (Utils.formatDisplayName(PlayerUpdate.localPlayer.displayName).equals(string2)) {
			ChatBox.writeGameMessage(LocalizedText.aClass351_43.get(ClientState.language));
		} else {
			@Pc(214) TcpPacket packet = TcpPacket.createPacket(ClientProt.FRIENDLIST_ADD, TcpPacket.isaac);
			packet.buffer.writeByte(Utils.getEncodedStringLength(string));
			packet.buffer.writeString(string);
			BufferedConnectionContext.queuePacket(packet);
		}
	}

	@OriginalMember(owner = "client!di", name = "g", descriptor = "[Ljava/lang/String;")
	public static final String[] friendPreviousNames = new String[200];
	@OriginalMember(owner = "client!wia", name = "a", descriptor = "(Ljava/lang/String;I)I")
	public static int getFriendIndex(@OriginalArg(0) String string) {
		if (string == null) {
			return -1;
		}
		for (@Pc(16) int i = 0; i < FriendList.FRIEND_COUNT; i++) {
			if (string.equalsIgnoreCase(FriendList.friendNames[i])) {
				return i;
			}
		}
		return -1;
	}

	@OriginalMember(owner = "client!nha", name = "r", descriptor = "I")
	public static int friendListStatus = 0;
	@OriginalMember(owner = "client!nga", name = "f", descriptor = "[Z")
	public static final boolean[] friendSameGame = new boolean[200];

	@OriginalMember(owner = "client!pm", name = "d", descriptor = "[I")
	public static final int[] friendRanks = new int[200];
	@OriginalMember(owner = "client!uga", name = "a", descriptor = "(IILjava/lang/String;)V")
	public static void setFriendRank(@OriginalArg(1) int rank, @OriginalArg(2) String string) {
		@Pc(14) TcpPacket packet = TcpPacket.createPacket(ClientProt.FRIEND_SETRANK, TcpPacket.isaac);
		packet.buffer.writeByte(Utils.getEncodedStringLength(string) + 1);
		packet.buffer.writeByteSubtract(rank);
		packet.buffer.writeString(string);
		BufferedConnectionContext.queuePacket(packet);
	}
	@OriginalMember(owner = "client!nia", name = "e", descriptor = "I")
	public static int lastFriendIgnoreUpdateTick = 0;
}
