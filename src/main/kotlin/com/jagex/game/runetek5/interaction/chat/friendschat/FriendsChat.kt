package com.jagex.game.runetek5.interaction.chat.friendschat

import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.network.BufferedConnectionContext
import com.jagex.game.runetek5.network.packet.TcpPacket
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.config.rsinterface.cs2.CS2Executor
import com.jagex.game.runetek5.FriendsChatPlayer
import com.jagex.game.runetek5.network.packet.ClientProt
import com.jagex.game.runetek5.utils.Utils

object FriendsChat {
	@OriginalMember(owner = "client!ig", name = "L", descriptor = "I")
	@JvmField
	var dragParentY: Int = -1

	@OriginalMember(owner = "client!lga", name = "v", descriptor = "Ljava/lang/String;")
	@JvmField
	var clanChatOwnerName: String? = null

	@OriginalMember(owner = "client!ws", name = "a", descriptor = "(BLjava/lang/String;)V")
	@JvmStatic
	fun kickFriendsChatUser(@OriginalArg(1) string: String?) {
		if (FriendsChat.clanChatUsers != null) {
			@Pc(15) val packet: TcpPacket = TcpPacket.createPacket(ClientProt.FRIENDCHAT_KICKUSER, TcpPacket.isaac)
			packet.buffer!!.writeByte(Utils.getEncodedStringLength(string!!))
			packet.buffer!!.writeString(string)
			BufferedConnectionContext.queuePacket(packet)
		}
	}
	@OriginalMember(owner = "client!tba", name = "b", descriptor = "(I)V")
	@JvmStatic
	fun sendLeaveFriendChat() {
		@Pc(18) val packet: TcpPacket = TcpPacket.createPacket(CS2Executor.FRIENDCHAT_JOIN_LEAVE, TcpPacket.isaac)
		packet.buffer!!.writeByte(0)
		BufferedConnectionContext.queuePacket(packet)
	}

	@OriginalMember(owner = "client!cw", name = "h", descriptor = "B")
	@JvmField
	var myFriendsChatRank: Byte = 0

	@OriginalMember(owner = "client!saa", name = "a", descriptor = "(Ljava/lang/String;I)V")
	@JvmStatic
	fun sendJoinFriendChat(@OriginalArg(0) string: String?) {
		if (!string!!.equals("")) {
			@Pc(25) val packet: TcpPacket = TcpPacket.createPacket(CS2Executor.FRIENDCHAT_JOIN_LEAVE, TcpPacket.isaac)
			packet.buffer!!.writeByte(Utils.getEncodedStringLength(string))
			packet.buffer!!.writeString(string)
			BufferedConnectionContext.queuePacket(packet)
		}
	}

	@OriginalMember(owner = "client!sb", name = "j", descriptor = "B")
	@JvmField
	var friendChatMinKickRank: Byte = 0

	@OriginalMember(owner = "client!iu", name = "c", descriptor = "Ljava/lang/String;")
	@JvmField
	var friendsChatName: String? = null
	@OriginalMember(owner = "client!pha", name = "e", descriptor = "[Lclient!hi;")
	@JvmField
	var clanChatUsers: Array<FriendsChatPlayer?>? = null
	@OriginalMember(owner = "client!ida", name = "g", descriptor = "I")
	@JvmField
	var clanChatUserCount: Int = 0
	@OriginalMember(owner = "client!sj", name = "e", descriptor = "I")
	@JvmField
	var lastFriendsChatUpdateTick: Int = 0
	@OriginalMember(owner = "client!bj", name = "a", descriptor = "(Ljava/lang/String;Z)Ljava/lang/String;")
	@JvmStatic
	fun formatDisplayName(@OriginalArg(0) string: String?): String? {
		@Pc(15) var string2: String? = Utils.decodeUidName(Utils.encodeBase37(string!!))
		if (string2 == null) {
			string2 = ""
		}
		return string2
	}
}
