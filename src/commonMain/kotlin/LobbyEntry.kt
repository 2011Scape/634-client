import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable
import com.jagex.game.runetek5.config.rsinterface.SubInterface
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.client.login.LoginManager
import com.jagex.game.runetek5.config.defaults.EntityDefaults
import com.jagex.game.runetek5.config.rsinterface.Interface
import com.jagex.game.runetek5.client.window.WindowMode
import com.jagex.game.runetek5.InterfaceManager
import com.jagex.game.runetek5.InterfaceRectangles
import com.jagex.game.runetek5.client.ClientState
import kotlin.jvm.JvmStatic

object LobbyEntry {

	@OriginalMember(owner = "client!mn", name = "a", descriptor = "(ZB)V")
	@JvmStatic
	fun openLobby(@OriginalArg(0) prepareWindow: Boolean) {
		if (prepareWindow) {
			if (ClientState.BASE_WINDOW_ID != -1) {
				Interface.unloadInterface(ClientState.BASE_WINDOW_ID)
			}
			@Pc(18) var subInterface: SubInterface? = InterfaceManager.OPEN_INTERFACES.first() as SubInterface?
			while (subInterface != null) {
				if (!subInterface.isLinked()) {
					subInterface = InterfaceManager.OPEN_INTERFACES.first() as SubInterface?
					if (subInterface == null) {
						break
					}
				}
				SubInterfaceCloser.closeSubInterface(true, false, subInterface)
				subInterface = InterfaceManager.OPEN_INTERFACES.next() as SubInterface?
			}
			ClientState.BASE_WINDOW_ID = -1
			InterfaceManager.OPEN_INTERFACES = IterableHashTable(8)
			InterfaceManager.resetInterfaces()
			ClientState.BASE_WINDOW_ID = EntityDefaults.lobbyWindow
			WindowMode.interfaceResize(false)
			InterfaceRectangles.invalidateAllRectangles()
			CS2Interpreter.runInterfaceOnLoadScripts(ClientState.BASE_WINDOW_ID)
		}
		LoginManager.currentlyInLobby = true
	}
}
