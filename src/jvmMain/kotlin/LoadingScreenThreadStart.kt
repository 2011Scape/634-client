import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.client.LoadingStage
import com.jagex.game.runetek5.client.ClientState

object LoadingScreenThreadStart {

	@OriginalMember(owner = "client!kf", name = "b", descriptor = "(I)V")
	@JvmStatic
	fun initialize() {
		if (LoadingStage.LOADING_STAGE != null) {
			ActiveLoadingScreenRenderer.aClass8_1 = AssetLoadingScreenRenderer()
			ActiveLoadingScreenRenderer.aClass8_1!!.setLoadingState(LoadingStage.LOADING_STAGE, LoadingStage.LOADING_STAGE!!.stalledText!!.get(ClientState.language), LoadingStage.LOADING_STAGE!!.primaryCompletionDisplay, LoadingStage.loadingStageChangedTime)
			ActiveLoadingScreenRenderer.loadingScreenThread = Thread(ActiveLoadingScreenRenderer.aClass8_1, "")
			ActiveLoadingScreenRenderer.loadingScreenThread!!.start()
		}
	}
}
