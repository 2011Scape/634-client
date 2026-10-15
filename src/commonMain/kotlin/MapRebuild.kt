import com.jagex.game.runetek5.core.constants.MainLogicStep
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.MapRegion
import com.jagex.game.runetek5.core.constants.AreaMode
import kotlin.jvm.JvmStatic

object MapRebuild {

	@OriginalMember(owner = "client!jn", name = "a", descriptor = "(B)V")
	@JvmStatic
	fun rebuildMap() {
		// Not part of the original client: the scene's size for the draw distance, from the last region
		// packet (StaticRegionInit.hdApplySceneSize) - the panel, ::hd renderDistance and the smoke test
		// all rebuild through here
		if (MapRegion.hdServerKeys != null && MapRegion.areaMode == AreaMode.STATIC_AREA) {
			StaticRegionInit.hdApplySceneSize()
		}
		if (ClientState.mainLogicStep === MainLogicStep.STEP_LOGIN_SCREEN) {
			MainLogicStepTransition.setMainLogicStep(MainLogicStep.STEP_LOGIN_SCREEN_MAP_BUILD)
		} else if (ClientState.mainLogicStep === MainLogicStep.STEP_LOBBY_SCREEN) {
			MainLogicStepTransition.setMainLogicStep(MainLogicStep.STEP_LOBBY_SCREEN_MAP_BUILD)
		} else if (ClientState.mainLogicStep === MainLogicStep.STEP_GAME_SCREEN) {
			MainLogicStepTransition.setMainLogicStep(MainLogicStep.STEP_GAME_SCREEN_MAP_BUILD)
			return
		}
	}
}
