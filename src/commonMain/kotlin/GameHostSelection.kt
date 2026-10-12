import com.jagex.game.runetek5.WorldConnectionInfo
import com.jagex.game.runetek5.core.constants.Ports
import net.URL
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.client.ServerEnvironment
import com.jagex.game.runetek5.network.packet.TcpPacket
import com.jagex.game.runetek5.network.WorldConnection
import com.jagex.game.runetek5.ModeWhere
import com.jagex.game.runetek5.statics.BrowserJavaScript
import com.jagex.game.runetek5.client.GameShell
import com.jagex.game.runetek5.client.ClientState
import kotlin.jvm.JvmStatic

object GameHostSelection {

	@OriginalMember(owner = "client!jm", name = "a", descriptor = "(Ljava/lang/String;II)Z")
	@JvmStatic
	fun setGameHost(@OriginalArg(0) host: String?, @OriginalArg(1) worldId: Int): Boolean {
		if (GameShell.signLink!!.signed) {
			TcpPacket.CURRENT_REQUEST = WorldConnectionInfo()
			TcpPacket.CURRENT_REQUEST!!.worldId = worldId
			TcpPacket.CURRENT_REQUEST!!.host = host
			if (ServerEnvironment.LIVE !== ModeWhere.WTRC) {
				TcpPacket.CURRENT_REQUEST!!.primaryPort = TcpPacket.CURRENT_REQUEST!!.worldId + 50000
				TcpPacket.CURRENT_REQUEST!!.secondaryPort = TcpPacket.CURRENT_REQUEST!!.worldId + 40000
			}
			if (worldId < WorldConnection.WORLD_DESCRIPTORS_BYID!!.size && WorldConnection.WORLD_DESCRIPTORS_BYID!![worldId] != null) {
				GameShell.worldFlags = WorldConnection.WORLD_DESCRIPTORS_BYID!![worldId]!!.flags
			}
			return true
		}
		@Pc(50) var string2: String = ""
		if (ModeWhere.WTRC !== ServerEnvironment.LIVE) {
			string2 = ":" + (worldId + Ports.HTTP_OFFSET)
		}
		@Pc(65) var string3: String = ""
		if (GameShell.siteSettings != null) {
			string3 = "/p=" + GameShell.siteSettings
		}
		@Pc(113) val string4: String = "http://" + host + string2 + "/l=" + ClientState.language + "/a=" + GameShell.AFFILIATE + string3 + "/j" + (if (BrowserJavaScript.javaScriptEnabled) "1" else "0") + ",o" + (if (GameShell.objectTag) "1" else "0") + ",a2"
		try {
			ActiveClient.activeClient!!.getAppletContext()!!.showDocument(URL(string4), "_self")
			return true
		} catch (@Pc(128) ex: Exception) {
			return false
		}
	}
}
