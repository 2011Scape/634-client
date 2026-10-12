import com.jagex.game.runetek5.core.constants.MainLogicStep
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.config.Resource
import com.jagex.game.runetek5.client.input.camera.Camera
import com.jagex.game.runetek5.MapRegion
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate
import com.jagex.game.runetek5.preference.GamePreferences
import com.jagex.game.runetek5.client.ClientState
import kotlin.jvm.JvmStatic

object BackgroundMapRegionSetup {

	@OriginalMember(owner = "client!bca", name = "a", descriptor = "(B)V")
	@JvmStatic
	fun setupBackgroundMapRegions() {
		MapRegion.setMapSize(GamePreferences.uid!!.buildArea!!.getValue())
		@Pc(20) val centerChunkX: Int = (Camera.cameraPositionAbsoluteX shr 12) + (MapRegion.sceneBaseX shr 3)
		@Pc(28) val centerChunkY: Int = (MapRegion.sceneBaseY shr 3) + (Camera.cameraPositionAbsoluteY shr 12)
		PlayerUpdate.localPlayer!!.level = 0
		PlayerUpdate.currentLevel = 0
		PlayerUpdate.localPlayer!!.move(8, 8)
		MapRegion.npcSpawnArchives = IntArray(18)
		MapRegion.npcSpawnBuffers = arrayOfNulls<ByteArray>(18)
		MapRegion.xteas = Array<IntArray?>(18) { IntArray(4) }
		MapRegion.surfaceMapArchives = IntArray(18)
		MapRegion.locMapBuffers = arrayOfNulls<ByteArray>(18)
		MapRegion.regionIds = IntArray(18)
		MapRegion.underwaterLocArchives = IntArray(18)
		MapRegion.underwaterMapArchives = IntArray(18)
		MapRegion.surfaceMapBuffer = arrayOfNulls<ByteArray>(18)
		MapRegion.surfaceLocArchives = IntArray(18)
		MapRegion.underwaterLocBuffer = arrayOfNulls<ByteArray>(18)
		MapRegion.underwaterMapBuffer = arrayOfNulls<ByteArray>(18)
		@Pc(84) var regionCount: Int = 0
		@Pc(104) var local104: Int
		@Pc(93) var i: Int = (centerChunkX - (MapRegion.sceneSizeX shr 4)) / 8
		while (i <= (centerChunkX + (MapRegion.sceneSizeX shr 4)) / 8) {
			var regionY: Int = (centerChunkY - (MapRegion.sceneSizeY shr 4)) / 8
			while (regionY <= (centerChunkY + (MapRegion.sceneSizeY shr 4)) / 8) {
				@Pc(112) val regionId: Int = regionY + (i shl 8)
				MapRegion.regionIds!![regionCount] = regionId
				MapRegion.surfaceMapArchives!![regionCount] = Resource.MAPS!!.getGroupId("m" + i + "_" + regionY)
				MapRegion.surfaceLocArchives!![regionCount] = Resource.MAPS!!.getGroupId("l" + i + "_" + regionY)
				MapRegion.npcSpawnArchives!![regionCount] = Resource.MAPS!!.getGroupId("n" + i + "_" + regionY)
				MapRegion.underwaterMapArchives!![regionCount] = Resource.MAPS!!.getGroupId("um" + i + "_" + regionY)
				MapRegion.underwaterLocArchives!![regionCount] = Resource.MAPS!!.getGroupId("ul" + i + "_" + regionY)
				if (MapRegion.npcSpawnArchives!![regionCount] == -1) {
					MapRegion.surfaceMapArchives!![regionCount] = -1
					MapRegion.surfaceLocArchives!![regionCount] = -1
					MapRegion.underwaterMapArchives!![regionCount] = -1
					MapRegion.underwaterLocArchives!![regionCount] = -1
				}
				regionCount++
				regionY++
			}
			i++
		}
		var regionIndex: Int = regionCount
		while (regionIndex < MapRegion.npcSpawnArchives!!.size) {
			MapRegion.npcSpawnArchives!![regionIndex] = -1
			MapRegion.surfaceMapArchives!![regionIndex] = -1
			MapRegion.surfaceLocArchives!![regionIndex] = -1
			MapRegion.underwaterMapArchives!![regionIndex] = -1
			MapRegion.underwaterLocArchives!![regionIndex] = -1
			regionIndex++
		}
		@Pc(282) val mapBuildStep: MainLogicStep?
		if (ClientState.mainLogicStep == MainLogicStep.STEP_LOGIN_SCREEN) {
			mapBuildStep = MainLogicStep.STEP_LOGIN_SCREEN_MAP_BUILD
		} else {
			mapBuildStep = MainLogicStep.STEP_LOBBY_SCREEN_MAP_BUILD
		}
		MapBuildStart.performUpdate(centerChunkY, centerChunkX, mapBuildStep, false)
	}
}
