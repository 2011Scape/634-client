import com.jagex.game.runetek5.core.constants.MainLogicStep
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.network.WorldConnection
import com.jagex.game.runetek5.network.Connection
import com.jagex.game.runetek5.client.ClientState
import kotlin.jvm.JvmStatic

object ConnectionLostHandler {

	@OriginalMember(owner = "client!uu", name = "b", descriptor = "(I)V")
	@JvmStatic
	fun handleConnectionLost() {
		if (ClientState.mainLogicStep == MainLogicStep.STEP_LOBBY_SCREEN) {
			ClientLogout.logout(false)
		} else {
			WorldConnection.aClass116_2 = Connection.connection
			Connection.connection = null
			MainLogicStepTransition.setMainLogicStep(MainLogicStep.STEP_RECONNECTING)
		}
	}
}
