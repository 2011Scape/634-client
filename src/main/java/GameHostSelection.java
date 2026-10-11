import com.jagex.game.runetek5.WorldConnectionInfo;
import com.jagex.game.runetek5.core.constants.Ports;
import java.net.URL;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.ServerEnvironment;
import com.jagex.game.runetek5.network.packet.TcpPacket;
import com.jagex.game.runetek5.network.WorldConnection;
import com.jagex.game.runetek5.ModeWhere;
import com.jagex.game.runetek5.statics.BrowserJavaScript;
import com.jagex.game.runetek5.client.GameShell;
import com.jagex.game.runetek5.client.ClientState;

public final class GameHostSelection {

	@OriginalMember(owner = "client!jm", name = "a", descriptor = "(Ljava/lang/String;II)Z")
	public static boolean setGameHost(@OriginalArg(0) String host, @OriginalArg(1) int worldId) {
		if (GameShell.signLink.signed) {
			TcpPacket.CURRENT_REQUEST = new WorldConnectionInfo();
			TcpPacket.CURRENT_REQUEST.worldId = worldId;
			TcpPacket.CURRENT_REQUEST.host = host;
			if (ServerEnvironment.LIVE != ModeWhere.WTRC) {
				TcpPacket.CURRENT_REQUEST.primaryPort = TcpPacket.CURRENT_REQUEST.worldId + 50000;
				TcpPacket.CURRENT_REQUEST.secondaryPort = TcpPacket.CURRENT_REQUEST.worldId + 40000;
			}
			if (worldId < WorldConnection.WORLD_DESCRIPTORS_BYID.length && WorldConnection.WORLD_DESCRIPTORS_BYID[worldId] != null) {
				GameShell.worldFlags = WorldConnection.WORLD_DESCRIPTORS_BYID[worldId].flags;
			}
			return true;
		}
		@Pc(50) String string2 = "";
		if (ModeWhere.WTRC != ServerEnvironment.LIVE) {
			string2 = ":" + (worldId + Ports.HTTP_OFFSET);
		}
		@Pc(65) String string3 = "";
		if (GameShell.siteSettings != null) {
			string3 = "/p=" + GameShell.siteSettings;
		}
		@Pc(113) String string4 = "http://" + host + string2 + "/l=" + ClientState.language + "/a=" + GameShell.AFFILIATE + string3 + "/j" + (BrowserJavaScript.javaScriptEnabled ? "1" : "0") + ",o" + (GameShell.objectTag ? "1" : "0") + ",a2";
		try {
			ActiveClient.activeClient.getAppletContext().showDocument(new URL(string4), "_self");
			return true;
		} catch (@Pc(128) Exception ex) {
			return false;
		}
	}
}
