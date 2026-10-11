package com.jagex.game.runetek5.interaction;

import com.jagex.game.runetek5.network.packet.TcpPacket;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.interaction.FriendList;
import com.jagex.game.runetek5.ClientPacket;
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate;
import com.jagex.game.runetek5.network.BufferedConnectionContext;
import com.jagex.game.runetek5.tools.LocalizedText;
import com.jagex.game.runetek5.network.packet.ClientProt;
import com.jagex.game.runetek5.interaction.chat.ChatBox;
import com.jagex.game.runetek5.utils.Utils;
import com.jagex.game.runetek5.client.ClientState;

public final class IgnoreList {

	@OriginalMember(owner = "client!ht", name = "a", descriptor = "(Ljava/lang/String;IZ)V")
	public static void add(@OriginalArg(0) String displayName, @OriginalArg(2) boolean temporary) {
		if (displayName == null) {
			return;
		}
		if (IgnoreList.ignoreCount >= 100) {
			ChatBox.writeGameMessage(LocalizedText.aClass351_41.get(ClientState.language));
			return;
		}
		@Pc(25) String string2 = Utils.formatDisplayName(displayName);
		if (string2 == null) {
			return;
		}
		@Pc(69) String string3;
		for (@Pc(30) int i = 0; i < IgnoreList.ignoreCount; i++) {
			@Pc(38) String string4 = Utils.formatDisplayName(IgnoreList.ignoreNames[i]);
			if (string4 != null && string4.equals(string2)) {
				ChatBox.writeGameMessage(displayName + LocalizedText.aClass351_42.get(ClientState.language));
				return;
			}
			if (IgnoreList.ignorePreviousNames[i] != null) {
				string3 = Utils.formatDisplayName(IgnoreList.ignorePreviousNames[i]);
				if (string3 != null && string3.equals(string2)) {
					ChatBox.writeGameMessage(displayName + LocalizedText.aClass351_42.get(ClientState.language));
					return;
				}
			}
		}
		for (@Pc(104) int j = 0; j < FriendList.FRIEND_COUNT; j++) {
			string3 = Utils.formatDisplayName(FriendList.friendNames[j]);
			if (string3 != null && string3.equals(string2)) {
				ChatBox.writeGameMessage(LocalizedText.aClass351_47.get(ClientState.language) + displayName + LocalizedText.aClass351_48.get(ClientState.language));
				return;
			}
			if (FriendList.friendPreviousNames[j] != null) {
				@Pc(148) String string5 = Utils.formatDisplayName(FriendList.friendPreviousNames[j]);
				if (string5 != null && string5.equals(string2)) {
					ChatBox.writeGameMessage(LocalizedText.aClass351_47.get(ClientState.language) + displayName + LocalizedText.aClass351_48.get(ClientState.language));
					return;
				}
			}
		}
		if (Utils.formatDisplayName(PlayerUpdate.localPlayer.displayName).equals(string2)) {
			ChatBox.writeGameMessage(LocalizedText.aClass351_44.get(ClientState.language));
			return;
		}
		@Pc(202) TcpPacket packet = TcpPacket.createPacket(ClientProt.IGNORELIST_ADD, TcpPacket.isaac);
		packet.buffer.writeByte(Utils.getEncodedStringLength(displayName) + 1);
		packet.buffer.writeString(displayName);
		packet.buffer.writeByte(temporary ? 1 : 0);
		BufferedConnectionContext.queuePacket(packet);
	}
	@OriginalMember(owner = "client!iv", name = "pb", descriptor = "[Ljava/lang/String;")
	public static final String[] ignoreNames = new String[100];

	@OriginalMember(owner = "client!ej", name = "a", descriptor = "(Ljava/lang/String;I)Z")
	public static boolean isIgnored(@OriginalArg(0) String string) {
		if (string == null) {
			return false;
		}
		for (@Pc(11) int i = 0; i < IgnoreList.ignoreCount; i++) {
			if (string.equalsIgnoreCase(IgnoreList.ignoreNamesUnfiltered[i])) {
				return true;
			}
			if (string.equalsIgnoreCase(IgnoreList.ignorePreviousNamesUnfiltered[i])) {
				return true;
			}
		}
		return false;
	}
	@OriginalMember(owner = "client!tea", name = "o", descriptor = "[Z")
	public static final boolean[] ignoreIsTemporary = new boolean[100];

	@OriginalMember(owner = "client!rp", name = "R", descriptor = "[Ljava/lang/String;")
	public static final String[] ignorePreviousNames = new String[100];
	@OriginalMember(owner = "client!ov", name = "a", descriptor = "(ZLjava/lang/String;)V")
	public static void removeIgnore(@OriginalArg(1) String string) {
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
		for (@Pc(34) int i = 0; i < IgnoreList.ignoreCount; i++) {
			@Pc(40) String string3 = IgnoreList.ignoreNames[i];
			if (string3.startsWith("*")) {
				string3 = string3.substring(1);
			}
			string3 = Utils.formatDisplayName(string3);
			if (string3 != null && string3.equals(string2)) {
				IgnoreList.ignoreCount--;
				for (@Pc(64) int j = i; j < IgnoreList.ignoreCount; j++) {
					IgnoreList.ignoreNames[j] = IgnoreList.ignoreNames[j + 1];
					IgnoreList.ignoreNamesUnfiltered[j] = IgnoreList.ignoreNamesUnfiltered[j + 1];
					IgnoreList.ignorePreviousNames[j] = IgnoreList.ignorePreviousNames[j + 1];
					IgnoreList.ignorePreviousNamesUnfiltered[j] = IgnoreList.ignorePreviousNamesUnfiltered[j + 1];
					IgnoreList.ignoreIsTemporary[j] = IgnoreList.ignoreIsTemporary[j + 1];
				}
				FriendList.lastFriendIgnoreUpdateTick = ClientState.serverTick;
				@Pc(121) TcpPacket packet = TcpPacket.createPacket(ClientProt.IGNORELIST_DEL, TcpPacket.isaac);
				packet.buffer.writeByte(Utils.getEncodedStringLength(string));
				packet.buffer.writeString(string);
				BufferedConnectionContext.queuePacket(packet);
				return;
			}
		}
	}
	@OriginalMember(owner = "client!db", name = "I", descriptor = "I")
	public static int ignoreCount = 0;
	@OriginalMember(owner = "client!nga", name = "h", descriptor = "[Ljava/lang/String;")
	public static final String[] ignoreNamesUnfiltered = new String[100];
	@OriginalMember(owner = "client!lp", name = "g", descriptor = "[Ljava/lang/String;")
	public static final String[] ignorePreviousNamesUnfiltered = new String[100];
}
