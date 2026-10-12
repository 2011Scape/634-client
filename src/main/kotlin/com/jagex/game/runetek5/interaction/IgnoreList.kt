package com.jagex.game.runetek5.interaction

import com.jagex.game.runetek5.network.packet.TcpPacket
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate
import com.jagex.game.runetek5.network.BufferedConnectionContext
import com.jagex.game.runetek5.tools.LocalizedText
import com.jagex.game.runetek5.network.packet.ClientProt
import com.jagex.game.runetek5.interaction.chat.ChatBox
import com.jagex.game.runetek5.utils.Utils
import com.jagex.game.runetek5.client.ClientState

object IgnoreList {

	@OriginalMember(owner = "client!ht", name = "a", descriptor = "(Ljava/lang/String;IZ)V")
	@JvmStatic
	fun add(@OriginalArg(0) displayName: String?, @OriginalArg(2) temporary: Boolean) {
		if (displayName == null) {
			return
		}
		if (IgnoreList.ignoreCount >= 100) {
			ChatBox.writeGameMessage(LocalizedText.aClass351_41.get(ClientState.language))
			return
		}
		@Pc(25) val string2: String? = Utils.formatDisplayName(displayName)
		if (string2 == null) {
			return
		}
		@Pc(69) var string3: String?
		@Pc(30) var i: Int = 0
		while (i < IgnoreList.ignoreCount) {
			@Pc(38) val string4: String? = Utils.formatDisplayName(IgnoreList.ignoreNames[i])
			if (string4 != null && string4 == string2) {
				ChatBox.writeGameMessage(displayName + LocalizedText.aClass351_42.get(ClientState.language))
				return
			}
			if (IgnoreList.ignorePreviousNames[i] != null) {
				string3 = Utils.formatDisplayName(IgnoreList.ignorePreviousNames[i])
				if (string3 != null && string3 == string2) {
					ChatBox.writeGameMessage(displayName + LocalizedText.aClass351_42.get(ClientState.language))
					return
				}
			}
			i++
		}
		@Pc(104) var j: Int = 0
		while (j < FriendList.FRIEND_COUNT) {
			string3 = Utils.formatDisplayName(FriendList.friendNames[j])
			if (string3 != null && string3 == string2) {
				ChatBox.writeGameMessage(LocalizedText.aClass351_47.get(ClientState.language) + displayName + LocalizedText.aClass351_48.get(ClientState.language))
				return
			}
			if (FriendList.friendPreviousNames[j] != null) {
				@Pc(148) val string5: String? = Utils.formatDisplayName(FriendList.friendPreviousNames[j])
				if (string5 != null && string5 == string2) {
					ChatBox.writeGameMessage(LocalizedText.aClass351_47.get(ClientState.language) + displayName + LocalizedText.aClass351_48.get(ClientState.language))
					return
				}
			}
			j++
		}
		if (Utils.formatDisplayName(PlayerUpdate.localPlayer.displayName)!!.equals(string2)) {
			ChatBox.writeGameMessage(LocalizedText.aClass351_44.get(ClientState.language))
			return
		}
		@Pc(202) val packet: TcpPacket = TcpPacket.createPacket(ClientProt.IGNORELIST_ADD, TcpPacket.isaac)
		packet.buffer!!.writeByte(Utils.getEncodedStringLength(displayName) + 1)
		packet.buffer!!.writeString(displayName)
		packet.buffer!!.writeByte(if (temporary) 1 else 0)
		BufferedConnectionContext.queuePacket(packet)
	}
	@OriginalMember(owner = "client!iv", name = "pb", descriptor = "[Ljava/lang/String;")
	@JvmField
	val ignoreNames: Array<String?> = arrayOfNulls<String>(100)

	@OriginalMember(owner = "client!ej", name = "a", descriptor = "(Ljava/lang/String;I)Z")
	@JvmStatic
	fun isIgnored(@OriginalArg(0) string: String?): Boolean {
		if (string == null) {
			return false
		}
		@Pc(11) var i: Int = 0
		while (i < IgnoreList.ignoreCount) {
			if (string.equals(IgnoreList.ignoreNamesUnfiltered[i], ignoreCase = true)) {
				return true
			}
			if (string.equals(IgnoreList.ignorePreviousNamesUnfiltered[i], ignoreCase = true)) {
				return true
			}
			i++
		}
		return false
	}
	@OriginalMember(owner = "client!tea", name = "o", descriptor = "[Z")
	@JvmField
	val ignoreIsTemporary: BooleanArray = BooleanArray(100)

	@OriginalMember(owner = "client!rp", name = "R", descriptor = "[Ljava/lang/String;")
	@JvmField
	val ignorePreviousNames: Array<String?> = arrayOfNulls<String>(100)
	@OriginalMember(owner = "client!ov", name = "a", descriptor = "(ZLjava/lang/String;)V")
	@JvmStatic
	fun removeIgnore(@OriginalArg(1) string: String?) {
		var string: String? = string
		if (string == null) {
			return
		}
		if (string.startsWith("*")) {
			string = string.substring(1)
		}
		@Pc(20) val string2: String? = Utils.formatDisplayName(string)
		if (string2 == null) {
			return
		}
		@Pc(34) var i: Int = 0
		while (i < IgnoreList.ignoreCount) {
			@Pc(40) var string3: String? = IgnoreList.ignoreNames[i]
			if (string3!!.startsWith("*")) {
				string3 = string3.substring(1)
			}
			string3 = Utils.formatDisplayName(string3)
			if (string3 != null && string3 == string2) {
				IgnoreList.ignoreCount--
				@Pc(64) var j: Int = i
				while (j < IgnoreList.ignoreCount) {
					IgnoreList.ignoreNames[j] = IgnoreList.ignoreNames[j + 1]
					IgnoreList.ignoreNamesUnfiltered[j] = IgnoreList.ignoreNamesUnfiltered[j + 1]
					IgnoreList.ignorePreviousNames[j] = IgnoreList.ignorePreviousNames[j + 1]
					IgnoreList.ignorePreviousNamesUnfiltered[j] = IgnoreList.ignorePreviousNamesUnfiltered[j + 1]
					IgnoreList.ignoreIsTemporary[j] = IgnoreList.ignoreIsTemporary[j + 1]
					j++
				}
				FriendList.lastFriendIgnoreUpdateTick = ClientState.serverTick
				@Pc(121) val packet: TcpPacket = TcpPacket.createPacket(ClientProt.IGNORELIST_DEL, TcpPacket.isaac)
				packet.buffer!!.writeByte(Utils.getEncodedStringLength(string))
				packet.buffer!!.writeString(string)
				BufferedConnectionContext.queuePacket(packet)
				return
			}
			i++
		}
	}
	@OriginalMember(owner = "client!db", name = "I", descriptor = "I")
	@JvmField
	var ignoreCount: Int = 0
	@OriginalMember(owner = "client!nga", name = "h", descriptor = "[Ljava/lang/String;")
	@JvmField
	val ignoreNamesUnfiltered: Array<String?> = arrayOfNulls<String>(100)
	@OriginalMember(owner = "client!lp", name = "g", descriptor = "[Ljava/lang/String;")
	@JvmField
	val ignorePreviousNamesUnfiltered: Array<String?> = arrayOfNulls<String>(100)
}
