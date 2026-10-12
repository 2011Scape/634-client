import com.jagex.game.runetek5.core.constants.MainLogicStep
import com.jagex.game.runetek5.ChatLineWriter
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate
import com.jagex.game.runetek5.scene.Scene
import com.jagex.game.runetek5.graphics.map.minimap.ComponentMinimap
import com.jagex.game.runetek5.preference.GamePreferences
import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.MapRegion

object LevelChangeRebuild {

	@OriginalMember(owner = "client!ri", name = "a", descriptor = "(B)V")
	@JvmStatic
	fun checkLevelChange() {
		if (GamePreferences.uid!!.animateBackgroundOverride!!.getValue() == 0 && PlayerUpdate.currentLevel != MapRegion.mapBuildLevel) {
			MapBuildStart.performUpdate(MapRegion.mapBaseChunkY, MapRegion.mapCenterChunkX, MainLogicStep.STEP_GAME_SCREEN_MAP_BUILD, false)
		} else {
			ComponentMinimap.updateMinimapLevel(ClientState.activeToolkit)
			if (PlayerUpdate.currentLevel != PlayerUpdate.MY_PLAYER_PLANE) {
				Scene.setRoofOverride()
			}
		}
	}
}
