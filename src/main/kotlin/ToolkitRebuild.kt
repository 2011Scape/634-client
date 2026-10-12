import com.jagex.game.runetek5.core.constants.MainLogicStep
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.graphics.sprite.StaticMedia
import com.jagex.game.runetek5.config.Resource
import com.jagex.game.runetek5.graphics.font.FontManager
import com.jagex.game.runetek5.client.LoadingStage
import com.jagex.game.runetek5.client.menu.MiniMenu
import com.jagex.game.runetek5.graphics.font.FontCombo
import com.jagex.game.runetek5.InterfaceRectangles
import com.jagex.game.runetek5.client.ClientState

object ToolkitRebuild {

	@OriginalMember(owner = "client!eba", name = "a", descriptor = "(ILjava/lang/String;ZI)V")
	@JvmStatic
	fun loadAssets(@OriginalArg(1) string: String?, @OriginalArg(2) skipSettingsVerification: Boolean, @OriginalArg(3) toolkitId: Int) {
		LoadingScreenThreadStop.killLoginscreenThread()
		Static657.method2905()
		CanvasInputReattach.buildCanvasComponents()
		ActiveToolkitInit.prepareForRendering(string, toolkitId, skipSettingsVerification)
		FontCombo.clearFontComboFonts()
		FontCombo.createFonts(ClientState.activeToolkit)
		FontManager.loadDefaultFonts(ClientState.activeToolkit)
		StaticMedia.loadSprites(Resource.SPRITES, ClientState.activeToolkit)
		MiniMenu.resetMiniMenuSprites()
		StaticMedia.initializeChatIcons(StaticMedia.nameIconSprites)
		InterfaceRectangles.invalidateAllRectangles()
		GraphicCacheReset.resetGraphicCaches()
		if (ClientState.mainLogicStep === MainLogicStep.STEP_LOGIN_SCREEN) {
			MainLogicStepTransition.setMainLogicStep(MainLogicStep.STEP_LOGIN_SCREEN_MAP_BUILD)
		} else if (ClientState.mainLogicStep === MainLogicStep.STEP_LOBBY_SCREEN) {
			MainLogicStepTransition.setMainLogicStep(MainLogicStep.STEP_LOBBY_SCREEN_MAP_BUILD)
		} else if (ClientState.mainLogicStep === MainLogicStep.STEP_GAME_SCREEN) {
			MainLogicStepTransition.setMainLogicStep(MainLogicStep.STEP_GAME_SCREEN_MAP_BUILD)
		} else if (ClientState.mainLogicStep === MainLogicStep.STEP_LOADING_1 || ClientState.mainLogicStep === MainLogicStep.STEP_LOADING_2) {
			LoadingStage.initializeTips()
		}
	}
}
