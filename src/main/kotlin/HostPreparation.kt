import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.client.GameShell
import com.jagex.game.runetek5.network.WorldConnection

object HostPreparation {

	@OriginalMember(owner = "client!jo", name = "e", descriptor = "(I)V")
	@JvmStatic
	fun prepareHost() {
		if (GameShell.signLink!!.signed && WorldConnection.aClass255_4!!.worldId != -1) {
			GameHostSelection.setGameHost(WorldConnection.aClass255_4!!.host, WorldConnection.aClass255_4!!.worldId)
		}
	}
}
