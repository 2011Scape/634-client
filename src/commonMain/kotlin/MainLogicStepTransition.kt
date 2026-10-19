import com.jagex.game.runetek5.plugin.PluginRuntime
import com.jagex.game.runetek5.ChatLineCount
import com.jagex.game.runetek5.EmailStatus
import com.jagex.game.runetek5.MidiChannelVolumeReset
import com.jagex.game.runetek5.core.constants.MainLogicStep
import com.jagex.game.runetek5.UnloadedLocCount
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.config.DefaultsFile
import com.jagex.game.runetek5.config.Resource
import com.jagex.game.runetek5.config.defaults.EntityDefaults
import com.jagex.game.runetek5.sound.player.area.AreaSoundPlayer
import com.jagex.game.runetek5.network.WorldConnection
import com.jagex.game.runetek5.client.login.LoginManager
import com.jagex.game.runetek5.MainLogicManager
import com.jagex.game.runetek5.preference.GamePreferences
import com.jagex.game.runetek5.sound.player.song.SongPlayer
import com.jagex.game.runetek5.client.ClientState
import kotlin.jvm.JvmStatic

object MainLogicStepTransition {

	@OriginalMember(owner = "client!cea", name = "k", descriptor = "(II)V")
	@JvmStatic
	fun setMainLogicStep(@OriginalArg(1) step: MainLogicStep?) {
		if (step === ClientState.mainLogicStep) {
			return
		}
		if (step === MainLogicStep.STEP_RECONNECTING) {
			if (LoginManager.ssKey == null) {
				LoginManager.worldLogin(LoginManager.loginPassword, LoginManager.anInt6172, LoginManager.loginUsername)
			} else {
				LoginManager.worldLoginWithStoredCredentials(LoginManager.anInt6172)
			}
		}
		if (step !== MainLogicStep.STEP_RECONNECTING && WorldConnection.aClass116_2 != null) {
			WorldConnection.aClass116_2!!.close()
			WorldConnection.aClass116_2 = null
		}
		if (step === MainLogicStep.STEP_LOGIN_SCREEN) {
			LoginScreenEntry.openLoginScreen(ClientState.BASE_WINDOW_ID != EntityDefaults.loginInterfaceId)
		}
		if (step === MainLogicStep.STEP_LOBBY_SCREEN) {
			LobbyEntry.openLobby(EntityDefaults.lobbyWindow != ClientState.BASE_WINDOW_ID)
		}
		if (step === MainLogicStep.STEP_LOGGING_IN_FROM_LOGINSCREEN_TO_LOBBY) {
			if (LoginManager.ssKey == null) {
				LoginManager.lobbyLogin(LoginManager.loginUsername, LoginManager.loginPassword)
			} else {
				LoginManager.lobbyLoginWithStoredCredentials()
			}
		} else if (step === MainLogicStep.STEP_LOGGING_IN_FROM_LOGINSCREEN_TO_GAME) {
			if (LoginManager.ssKey == null) {
				LoginManager.worldLogin(LoginManager.loginPassword, LoginManager.anInt6172, LoginManager.loginUsername)
			} else {
				LoginManager.worldLoginWithStoredCredentials(LoginManager.anInt6172)
			}
		} else if (step === MainLogicStep.STEP_LOGGING_IN_FROM_LOBBYSCREEN_TO_GAME) {
			if (LoginManager.ssKey == null) {
				LoginManager.worldLogin(LoginManager.loginPassword, LoginManager.anInt6172, LoginManager.loginUsername)
			} else {
				LoginManager.worldLoginWithStoredCredentials(LoginManager.anInt6172)
			}
		} else if (step === MainLogicStep.STEP_LOGGING_IN_FROM_GAME_TO_LOBBY) {
			if (LoginManager.ssKey == null) {
				LoginManager.lobbyLogin(LoginManager.loginUsername, LoginManager.loginPassword)
			} else {
				LoginManager.lobbyLoginWithStoredCredentials()
			}
		}
		if (MainLogicManager.isMapBuildStep(ClientState.mainLogicStep)) {
			Resource.CONFIG!!.loadType = 2
			Resource.ENUM!!.loadType = 2
			Resource.LOC!!.loadType = 2
			Resource.NPC!!.loadType = 2
			Resource.OBJ!!.loadType = 2
			Resource.SEQ!!.loadType = 2
			Resource.GFX!!.loadType = 2
		}
		if (MainLogicManager.isMapBuildStep(step)) {
			MapBuildStep.maxUnloadedLocCount = 1
			MapBuildStep.mapLoadingStage = 0
			MapBuildStep.totalMapCount = 1
			UnloadedLocCount.unloadedLocCount = 0
			MapBuildStep.pendingMapFileCount = 0
			WorldMapRelease.resetWorldmapCaches(true)
			Resource.CONFIG!!.loadType = 1
			Resource.ENUM!!.loadType = 1
			Resource.LOC!!.loadType = 1
			Resource.NPC!!.loadType = 1
			Resource.OBJ!!.loadType = 1
			Resource.SEQ!!.loadType = 1
			Resource.GFX!!.loadType = 1
		}
		if (step === MainLogicStep.STEP_GAME_SCREEN_MAP_BUILD || step === MainLogicStep.STEP_LOGIN_SCREEN) {
			ToolbarRefresh.refreshToolbar()
		}
		@Pc(226) val isOutsideGame: Boolean = step === MainLogicStep.STEP_LOADING_2 || MainLogicManager.isLoginScreenStep(step) || MainLogicManager.inLobby(step)
		@Pc(241) val wasOutsideGame: Boolean = ClientState.mainLogicStep === MainLogicStep.STEP_LOADING_2 || MainLogicManager.isLoginScreenStep(ClientState.mainLogicStep) || MainLogicManager.inLobby(ClientState.mainLogicStep)
		if (isOutsideGame != wasOutsideGame) {
			if (isOutsideGame) {
				AreaSoundPlayer.currentMusicArchive = DefaultsFile.fileId
				if (GamePreferences.uid!!.musicVolume!!.getValue() == 0) {
					SongPlayer.fadeOutMusic()
				} else {
					AreaSoundPlayer.musicSourceUpdate(GamePreferences.uid!!.musicVolume!!.getValue(), Resource.MUSIC, DefaultsFile.fileId)
					MidiChannelVolumeReset.resetChannelVolumes()
				}
				Resource.remoteFileRequesterJs5!!.writeLoggedIn(false)
			} else {
				SongPlayer.fadeOutMusic()
				Resource.remoteFileRequesterJs5!!.writeLoggedIn(true)
			}
		}
		if (MainLogicManager.isMapBuildStep(step) || step === MainLogicStep.STEP_RECONNECTING) {
			ClientState.activeToolkit!!.renderLastFrame()
		}
		ClientState.mainLogicStep = step
		PluginRuntime.stepChanged() // Not part of the original client: the plugins' GameStateChanged.
	}
}
