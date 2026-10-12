import com.jagex.game.runetek5.core.constants.MainLogicStep
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.client.ClientState
import kotlin.jvm.JvmStatic

object LoginStepRollback {

	@OriginalMember(owner = "client!ol", name = "a", descriptor = "(B)V")
	@JvmStatic
	fun revertLoginStep() {
		if (ClientState.mainLogicStep === MainLogicStep.STEP_LOGGING_IN_FROM_LOBBYSCREEN_TO_GAME) {
			MainLogicStepTransition.setMainLogicStep(MainLogicStep.STEP_LOGGING_IN_FROM_LOGINSCREEN_TO_LOBBY)
		} else if (ClientState.mainLogicStep === MainLogicStep.STEP_LOGGING_IN_FROM_LOGINSCREEN_TO_LOBBY || ClientState.mainLogicStep === MainLogicStep.STEP_LOGGING_IN_FROM_LOGINSCREEN_TO_GAME) {
			MainLogicStepTransition.setMainLogicStep(MainLogicStep.STEP_LOGIN_SCREEN)
			return
		} else if (ClientState.mainLogicStep === MainLogicStep.STEP_LOGGING_IN_FROM_GAME_TO_LOBBY) {
			MainLogicStepTransition.setMainLogicStep(MainLogicStep.STEP_LOGIN_SCREEN)
			return
		}
	}
}
