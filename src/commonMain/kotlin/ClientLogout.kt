import com.jagex.game.runetek5.client.features.HdOptionsScreen
import com.jagex.game.runetek5.client.features.LootTrackerScreen
import com.jagex.game.runetek5.client.features.PluginManagerScreen
import com.jagex.game.runetek5.client.input.camera.Camera
import com.jagex.game.runetek5.core.constants.MainLogicStep
import com.jagex.game.runetek5.client.login.LoginStep
import com.jagex.game.runetek5.scene.Scene
import com.jagex.game.runetek5.MidiChannelVolumeReset
import com.jagex.game.runetek5.statics.BrowserJavaScript
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.sound.player.area.AreaSoundPlayer
import com.jagex.game.runetek5.sound.player.area.SoundEffectPlayer
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMapDisplay
import com.jagex.game.runetek5.client.PingWorker
import com.jagex.game.runetek5.MapRegion
import com.jagex.game.runetek5.client.login.LoginManager
import com.jagex.game.runetek5.NPCList
import com.jagex.game.runetek5.client.GameShell
import com.jagex.game.runetek5.cache.node.ObjStack
import com.jagex.game.runetek5.network.Connection
import com.jagex.game.runetek5.sound.player.area.AmbientSound
import com.jagex.game.runetek5.sound.player.song.SongPlayer
import com.jagex.game.runetek5.vardomain.PlayerVarDomain
import com.jagex.game.runetek5.config.TypeLists
import com.jagex.game.runetek5.network.BufferedConnectionContext
import kotlin.jvm.JvmStatic

object ClientLogout {

	@OriginalMember(owner = "client!cfa", name = "a", descriptor = "(ZB)V")
	@JvmStatic
	fun logout(@OriginalArg(0) toLobby: Boolean) {
		if (Connection.connection != null) {
			Connection.connection!!.close()
			Connection.connection = null
		}
		LoginManager.loginStep = LoginStep.IDLE
		HdOptionsScreen.reset() // Not part of the original client: the 634HD page of Graphics Options
		PluginManagerScreen.reset() // Not part of the original client: the plugin manager's window
		LootTrackerScreen.reset() // Not part of the original client: the loot tracker's window
		TypeLists.resetAllCaches()
		Scene.destroy()
		for (@Pc(24) i in 0 until 4) {
			MapRegion.aClass104Array3[i]!!.reset()
		}
		WorldMapRelease.resetWorldmapCaches(false)
		lang.gc()
		SongPlayer.fadeOutMusic()
		SoundEffectPlayer.soundJinglePlaying = false
		AreaSoundPlayer.currentMusicArchive = -1
		MidiChannelVolumeReset.resetChannelVolumes()
		AmbientSound.unlinkSounds(true)
		MapRegion.sceneBaseY = 0
		MapRegion.sceneBaseX = 0
		MapRegion.mapCenterChunkX = 0
		MapRegion.mapBaseChunkY = 0
		MapRegion.areaMode = 0
		MapRegion.constructed = false // Not part of the original client: the plugins' isInInstance
		// Not part of the original client: the last region packet's keys go with the game area, so a map
		// rebuild on the login screen keeps its own region list (StaticRegionInit.hdApplySceneSize)
		MapRegion.hdServerKeys = null
		NPCList.previousAreaMode = 0
		for (@Pc(69) j in 0 until PlayerUpdate.HINT_ARROWS.size) {
			PlayerUpdate.HINT_ARROWS[j] = null
		}
		PlayerUpdate.resetPlayerUpdateState()
		for (@Pc(88) k in 0 until 2048) {
			PlayerUpdate.players[k] = null
		}
		NPCList.NPC_UPDATE_INDEX = 0
		NPCList.NPC_MAP.clear()
		NPCList.npcCount = 0
		ObjStack.GROUND_ITEMS.clear()
		Camera.reset()
		PlayerVarDomain.varVerifyId = 0
		PlayerVarDomain.playerVarProvider!!.reset()
		WorldMapExitToolkitRestore.releaseWorldMapRestoreToolkit()
		WorldMapDisplay.clearHiddenElements()
		BufferedConnectionContext.NEXT_PING_UPDATE_TO_SERVER = 0L
		PingWorker.aClass2_Sub36_1 = null
		if (toLobby) {
			MainLogicStepTransition.setMainLogicStep(MainLogicStep.STEP_LOGGING_IN_FROM_GAME_TO_LOBBY)
			return
		}
		MainLogicStepTransition.setMainLogicStep(MainLogicStep.STEP_LOGIN_SCREEN)
		try {
			BrowserJavaScript.callJavaScript(GameShell.applet, "loggedout")
		} catch (@Pc(147) ex: Throwable) {
		}
	}
}
