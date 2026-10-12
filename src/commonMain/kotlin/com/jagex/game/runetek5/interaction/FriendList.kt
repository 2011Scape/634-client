package com.jagex.game.runetek5.interaction

import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate
import com.jagex.game.runetek5.network.BufferedConnectionContext
import com.jagex.game.runetek5.network.packet.TcpPacket
import com.jagex.game.runetek5.tools.LocalizedText
import com.jagex.game.runetek5.interaction.chat.ChatBox
import com.jagex.game.runetek5.network.packet.ClientProt
import com.jagex.game.runetek5.client.login.LoginManager
import com.jagex.game.runetek5.utils.Utils
import com.jagex.game.runetek5.client.ClientState
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

object FriendList {
	@OriginalMember(owner = "client!tg", name = "u", descriptor = "[Ljava/lang/String;")
	@JvmField
	val LANGUAGE_CODES: Array<String?> = arrayOf<String?>("en", "de", "fr", "pt", "nl")

	@OriginalMember(owner = "client!vj", name = "I", descriptor = "I")
	@JvmField
	var FRIEND_COUNT: Int = 0
	@OriginalMember(owner = "client!ht", name = "a", descriptor = "(BLjava/lang/String;)I")
	@JvmStatic
	fun getLanguageId(@OriginalArg(1) string: String?): Int {
		@Pc(5) var i: Int = 0
		while (i < FriendList.LANGUAGE_CODES.size) {
			if (FriendList.LANGUAGE_CODES[i]!!.equals(string, ignoreCase = true)) {
				return i
			}
			i++
		}
		return -1
	}
	@OriginalMember(owner = "client!lc", name = "b", descriptor = "[Ljava/lang/String;")
	@JvmField
	val friendNames: Array<String?> = arrayOfNulls<String>(200)

	@OriginalMember(owner = "client!ha", name = "r", descriptor = "[I")
	@JvmField
	val friendWorldIds: IntArray = IntArray(200)

	@OriginalMember(owner = "client!vi", name = "j", descriptor = "[Ljava/lang/String;")
	@JvmField
	val friendWorldNames: Array<String?> = arrayOfNulls<String>(200)

	@OriginalMember(owner = "client!tn", name = "a", descriptor = "(ZLjava/lang/String;)V")
	@JvmStatic
	fun removeFriend(@OriginalArg(1) string: String?) {
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
		@Pc(25) var i: Int = 0
		while (i < FriendList.FRIEND_COUNT) {
			@Pc(31) var string3: String? = FriendList.friendNames[i]
			if (string3!!.startsWith("*")) {
				string3 = string3.substring(1)
			}
			string3 = Utils.formatDisplayName(string3)
			if (string3 != null && string3 == string2) {
				FriendList.FRIEND_COUNT--
				@Pc(55) var j: Int = i
				while (j < FriendList.FRIEND_COUNT) {
					FriendList.friendNames[j] = FriendList.friendNames[j + 1]
					FriendList.friendPreviousNames[j] = FriendList.friendPreviousNames[j + 1]
					FriendList.friendWorldIds[j] = FriendList.friendWorldIds[j + 1]
					FriendList.friendWorldNames[j] = FriendList.friendWorldNames[j + 1]
					FriendList.friendRanks[j] = FriendList.friendRanks[j + 1]
					FriendList.friendSameGame[j] = FriendList.friendSameGame[j + 1]
					j++
				}
				FriendList.lastFriendIgnoreUpdateTick = ClientState.serverTick
				@Pc(120) val packet: TcpPacket = TcpPacket.createPacket(ClientProt.FRIENDLIST_DEL, TcpPacket.isaac)
				packet.buffer!!.writeByte(Utils.getEncodedStringLength(string))
				packet.buffer!!.writeString(string)
				BufferedConnectionContext.queuePacket(packet)
				break
			}
			i++
		}
	}

	@OriginalMember(owner = "client!vj", name = "a", descriptor = "(ILjava/lang/String;)V")
	@JvmStatic
	fun addFriend(@OriginalArg(1) string: String?) {
		if (string == null) {
			return
		}
		@Pc(30) var string2: String?
		if (FriendList.FRIEND_COUNT >= 200 && !LoginManager.isMember || FriendList.FRIEND_COUNT >= 200) {
			ChatBox.writeGameMessage(LocalizedText.aClass351_12.get(ClientState.language))
			string2 = LocalizedText.aClass351_13.get(ClientState.language)
			if (string2 != null) {
				ChatBox.writeGameMessage(string2)
			}
			return
		}
		string2 = Utils.formatDisplayName(string)
		if (string2 == null) {
			return
		}
		@Pc(85) var string3: String?
		@Pc(46) var i: Int = 0
		while (i < FriendList.FRIEND_COUNT) {
			@Pc(54) val string4: String? = Utils.formatDisplayName(FriendList.friendNames[i])
			if (string4 != null && string4 == string2) {
				ChatBox.writeGameMessage(string + LocalizedText.aClass351_40.get(ClientState.language))
				return
			}
			if (FriendList.friendPreviousNames[i] != null) {
				string3 = Utils.formatDisplayName(FriendList.friendPreviousNames[i])
				if (string3 != null && string3 == string2) {
					ChatBox.writeGameMessage(string + LocalizedText.aClass351_40.get(ClientState.language))
					return
				}
			}
			i++
		}
		@Pc(116) var j: Int = 0
		while (j < IgnoreList.ignoreCount) {
			string3 = Utils.formatDisplayName(IgnoreList.ignoreNames[j])
			if (string3 != null && string3 == string2) {
				ChatBox.writeGameMessage(LocalizedText.aClass351_45.get(ClientState.language) + string + LocalizedText.aClass351_46.get(ClientState.language))
				return
			}
			if (IgnoreList.ignorePreviousNames[j] != null) {
				@Pc(160) val string5: String? = Utils.formatDisplayName(IgnoreList.ignorePreviousNames[j])
				if (string5 != null && string5 == string2) {
					ChatBox.writeGameMessage(LocalizedText.aClass351_45.get(ClientState.language) + string + LocalizedText.aClass351_46.get(ClientState.language))
					return
				}
			}
			j++
		}
		if (Utils.formatDisplayName(PlayerUpdate.localPlayer!!.displayName)!!.equals(string2)) {
			ChatBox.writeGameMessage(LocalizedText.aClass351_43.get(ClientState.language))
		} else {
			@Pc(214) val packet: TcpPacket = TcpPacket.createPacket(ClientProt.FRIENDLIST_ADD, TcpPacket.isaac)
			packet.buffer!!.writeByte(Utils.getEncodedStringLength(string))
			packet.buffer!!.writeString(string)
			BufferedConnectionContext.queuePacket(packet)
		}
	}

	@OriginalMember(owner = "client!di", name = "g", descriptor = "[Ljava/lang/String;")
	@JvmField
	val friendPreviousNames: Array<String?> = arrayOfNulls<String>(200)
	@OriginalMember(owner = "client!wia", name = "a", descriptor = "(Ljava/lang/String;I)I")
	@JvmStatic
	fun getFriendIndex(@OriginalArg(0) string: String?): Int {
		if (string == null) {
			return -1
		}
		@Pc(16) var i: Int = 0
		while (i < FriendList.FRIEND_COUNT) {
			if (string.equals(FriendList.friendNames[i], ignoreCase = true)) {
				return i
			}
			i++
		}
		return -1
	}

	@OriginalMember(owner = "client!nha", name = "r", descriptor = "I")
	@JvmField
	var friendListStatus: Int = 0
	@OriginalMember(owner = "client!nga", name = "f", descriptor = "[Z")
	@JvmField
	val friendSameGame: BooleanArray = BooleanArray(200)

	@OriginalMember(owner = "client!pm", name = "d", descriptor = "[I")
	@JvmField
	val friendRanks: IntArray = IntArray(200)
	@OriginalMember(owner = "client!uga", name = "a", descriptor = "(IILjava/lang/String;)V")
	@JvmStatic
	fun setFriendRank(@OriginalArg(1) rank: Int, @OriginalArg(2) string: String?) {
		@Pc(14) val packet: TcpPacket = TcpPacket.createPacket(ClientProt.FRIEND_SETRANK, TcpPacket.isaac)
		packet.buffer!!.writeByte(Utils.getEncodedStringLength(string!!) + 1)
		packet.buffer!!.writeByteSubtract(rank)
		packet.buffer!!.writeString(string)
		BufferedConnectionContext.queuePacket(packet)
	}
	@OriginalMember(owner = "client!nia", name = "e", descriptor = "I")
	@JvmField
	var lastFriendIgnoreUpdateTick: Int = 0
}
