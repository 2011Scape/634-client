import com.jagex.game.runetek5.ThirdLastPacket
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.interaction.FriendList
import com.jagex.game.runetek5.interaction.chat.friendschat.FriendsChat
import com.jagex.game.runetek5.network.BufferedConnectionContext
import com.jagex.game.runetek5.interaction.chat.ChatBox
import kotlin.jvm.JvmStatic

object SessionStateReset {

	@OriginalMember(owner = "client!td", name = "e", descriptor = "(B)V")
	@JvmStatic
	fun prepareForIngame() {
		ActiveClient.activeClient!!.loadJagmisc()
		BufferedConnectionContext.resetQueue()
		client.rebootTimer = 0
		ThirdLastPacket.thirdLastPacket = null
		BufferedConnectionContext.idleReadPulses = 0
		BufferedConnectionContext.secondLastPacket = null
		BufferedConnectionContext.receivedBuffer!!.currentPosition = 0
		BufferedConnectionContext.lastPacket = null
		ChatBox.clearChatLines()
		FriendList.FRIEND_COUNT = 0
		FriendsChat.friendsChatName = null
		FriendsChat.clanChatUserCount = 0
		FriendsChat.clanChatUsers = null
		FriendList.friendListStatus = 0
	}
}
