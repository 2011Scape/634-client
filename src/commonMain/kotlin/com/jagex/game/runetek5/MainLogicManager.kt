package com.jagex.game.runetek5

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.config.TypeLists
import com.jagex.game.runetek5.core.constants.MainLogicStep
import com.jagex.game.runetek5.sound.player.area.AmbientSound
import com.jagex.game.runetek5.vardomain.PlayerVarDomain
import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.client.menu.MiniMenu
import kotlin.jvm.JvmStatic

object MainLogicManager {

	@OriginalMember(owner = "client!pb", name = "b", descriptor = "(ZI)V")
	@JvmStatic
	fun onVarChange(@OriginalArg(1) varId: Int) {
		AmbientSound.updateTransformingSounds()
		@Pc(11) val clientCode: Int = TypeLists.VAR!!.list(varId)!!.clientCode
		if (clientCode == 0) {
			return
		}
		@Pc(27) val value: Int = PlayerVarDomain.playerVarProvider!!.activeVars[varId]
		if (clientCode == 5) {
			MiniMenu.menuOpenMode = value
		}
		if (clientCode == 6) {
			OverlayManager.chatEffectsMode = value
		}
	}
	@OriginalMember(owner = "client!oba", name = "a", descriptor = "(IB)Z")
	@JvmStatic
	fun isLoadingStep(@OriginalArg(0) step: MainLogicStep?): Boolean {
		return step === MainLogicStep.STEP_STARTUP || step === MainLogicStep.STEP_LOADING_1 || step === MainLogicStep.STEP_LOADING_2
	}

	@OriginalMember(owner = "client!uo", name = "a", descriptor = "(IB)Z")
	@JvmStatic
	fun isMapBuildStep(@OriginalArg(0) step: MainLogicStep?): Boolean {
		return step === MainLogicStep.STEP_LOGIN_SCREEN_MAP_BUILD || step === MainLogicStep.STEP_LOBBY_SCREEN_MAP_BUILD || step === MainLogicStep.STEP_GAME_SCREEN_MAP_BUILD
	}

	@OriginalMember(owner = "client!qia", name = "a", descriptor = "(IB)Z")
	@JvmStatic
	fun isLobbyScreenShown(@OriginalArg(0) step: MainLogicStep?): Boolean {
		return step === MainLogicStep.STEP_LOBBY_SCREEN || step === MainLogicStep.STEP_LOGGING_IN_FROM_LOBBYSCREEN_TO_GAME
	}

	@OriginalMember(owner = "client!vh", name = "a", descriptor = "(IZ)Z")
	@JvmStatic
	fun isLoginScreenShown(@OriginalArg(0) step: MainLogicStep?): Boolean {
		return step === MainLogicStep.STEP_LOGIN_SCREEN || step === MainLogicStep.STEP_LOGGING_IN_FROM_LOGINSCREEN_TO_LOBBY || step === MainLogicStep.STEP_LOGGING_IN_FROM_LOGINSCREEN_TO_GAME
	}

	@OriginalMember(owner = "client!qia", name = "c", descriptor = "(II)Z")
	@JvmStatic
	fun isLoginScreenStep(@OriginalArg(0) step: MainLogicStep?): Boolean {
		return step === MainLogicStep.STEP_LOGIN_SCREEN || step === MainLogicStep.STEP_LOGIN_SCREEN_MAP_BUILD || step === MainLogicStep.STEP_LOGGING_IN_FROM_LOGINSCREEN_TO_LOBBY || step === MainLogicStep.STEP_LOGGING_IN_FROM_LOGINSCREEN_TO_GAME
	}
	@OriginalMember(owner = "client!dk", name = "a", descriptor = "(ZI)Z")
	@JvmStatic
	fun isInGameStep(@OriginalArg(1) step: MainLogicStep?): Boolean {
		return step === MainLogicStep.STEP_GAME_SCREEN || step === MainLogicStep.STEP_GAME_SCREEN_MAP_BUILD || step === MainLogicStep.STEP_LOGGING_IN_FROM_GAME_TO_LOBBY
	}
	@OriginalMember(owner = "client!eb", name = "a", descriptor = "(II)Z")
	@JvmStatic
	fun inLobby(@OriginalArg(0) step: MainLogicStep?): Boolean {
		return step === MainLogicStep.STEP_LOBBY_SCREEN || step === MainLogicStep.STEP_LOBBY_SCREEN_MAP_BUILD || step === MainLogicStep.STEP_LOGGING_IN_FROM_LOBBYSCREEN_TO_GAME
	}

	@OriginalMember(owner = "client!ufa", name = "a", descriptor = "(BI)Z")
	@JvmStatic
	fun isLoginLobbyOrGameScreen(@OriginalArg(1) step: MainLogicStep?): Boolean {
		return step === MainLogicStep.STEP_LOGIN_SCREEN || step === MainLogicStep.STEP_LOBBY_SCREEN || step === MainLogicStep.STEP_GAME_SCREEN
	}

	@OriginalMember(owner = "client!vb", name = "a", descriptor = "(B)Z")
	@JvmStatic
	fun isPastStartup(): Boolean {
		return ClientState.mainLogicStep !== MainLogicStep.STEP_STARTUP
	}
}
