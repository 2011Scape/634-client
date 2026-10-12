import com.jagex.game.runetek5.core.constants.MainLogicStep
import com.jagex.game.runetek5.graphics.MiniMenuReset
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.client.LoadingStage
import com.jagex.game.runetek5.LoadingScreenManager
import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.utils.Utils
import kotlin.jvm.JvmStatic

object LoadingStageTick {

	@OriginalMember(owner = "client!jca", name = "a", descriptor = "(Z)V")
	@JvmStatic
	fun cycle() {
		if (LoadingStage.LOADING_STAGES == null) {
			LoadingStage.LOADING_STAGES = LoadingStage.getValues()
			LoadingStage.LOADING_STAGE = LoadingStage.LOADING_STAGES!![0]
			LoadingStage.loadingStageChangedTime = Utils.currentTimeMillis()
		}
		if (ActiveLoadingScreenRenderer.aClass8_1 == null) {
			LoadingScreenThreadStart.initialize()
		}
		@Pc(23) val loadState: LoadingStage? = LoadingStage.LOADING_STAGE
		@Pc(26) val loadingPercent: Int = LoadingStageWork.processLoadingStage()
		if (loadState === LoadingStage.LOADING_STAGE) {
			LoadingStage.LOADING_TEXT = LoadingStage.LOADING_STAGE!!.stalledText!!.get(ClientState.language)
			if (LoadingStage.LOADING_STAGE!!.hasProgressTracking) {
				LoadingStage.displayedLoadingPercentage = loadingPercent * (LoadingStage.LOADING_STAGE!!.secondaryCompletionDisplay - LoadingStage.LOADING_STAGE!!.primaryCompletionDisplay) / 100 + LoadingStage.LOADING_STAGE!!.primaryCompletionDisplay
			}
			if (LoadingStage.LOADING_STAGE!!.showPercentage) {
				LoadingStage.LOADING_TEXT = LoadingStage.LOADING_TEXT + LoadingStage.displayedLoadingPercentage + "%"
			}
		} else if (LoadingStage.LOADING_STAGE === LoadingStage.aClass167_23) {
			ActiveLoadingScreenRenderer.aClass8_1 = null
			MainLogicStepTransition.setMainLogicStep(MainLogicStep.STEP_LOGIN_SCREEN)
		} else {
			LoadingStage.LOADING_TEXT = loadState!!.localisedText2!!.get(ClientState.language)
			if (LoadingStage.LOADING_STAGE!!.showPercentage) {
				LoadingStage.LOADING_TEXT = LoadingStage.LOADING_TEXT + loadState.secondaryCompletionDisplay + "%"
			}
			LoadingStage.displayedLoadingPercentage = loadState.secondaryCompletionDisplay
			if (LoadingStage.LOADING_STAGE!!.hasProgressTracking || loadState.hasProgressTracking) {
				LoadingStage.loadingStageChangedTime = Utils.currentTimeMillis()
			}
		}
		if (ActiveLoadingScreenRenderer.aClass8_1 == null) {
			return
		}
		ActiveLoadingScreenRenderer.aClass8_1!!.setLoadingState(LoadingStage.LOADING_STAGE, LoadingStage.LOADING_TEXT, LoadingStage.displayedLoadingPercentage, LoadingStage.loadingStageChangedTime)
		if (LoadingStage.gametipsToDisplay == null) {
			return
		}
		@Pc(130) var i: Int = LoadingStage.lastDisplayedGametip + 1
		while (i < LoadingStage.gametipsToDisplay!!.size) {
			if (LoadingStage.gametipsToDisplay!![i]!!.percentage() >= 100 && LoadingStage.lastDisplayedGametip == i - 1 && ClientState.mainLogicStep != MainLogicStep.STEP_STARTUP && ActiveLoadingScreenRenderer.aClass8_1!!.isFrontComponentFinished()) {
				try {
					LoadingStage.gametipsToDisplay!![i]!!.init()
				} catch (@Pc(162) ex: Exception) {
					LoadingStage.gametipsToDisplay = null
					return
				}
				ActiveLoadingScreenRenderer.aClass8_1!!.setFrontComponent(LoadingStage.gametipsToDisplay!![i])
				LoadingStage.lastDisplayedGametip++
				if (LoadingStage.lastDisplayedGametip >= LoadingStage.gametipsToDisplay!!.size - 1 && LoadingStage.gametipsToDisplay!!.size > 1) {
					LoadingStage.lastDisplayedGametip = if (LoadingScreenManager.aClass214_1!!.hasDefault()) 0 else -1
				}
			}
			i++
		}
		return
	}
}
