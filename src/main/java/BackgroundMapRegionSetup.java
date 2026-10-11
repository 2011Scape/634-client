import com.jagex.game.runetek5.core.constants.MainLogicStep;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.config.Resource;
import com.jagex.game.runetek5.client.input.camera.Camera;
import com.jagex.game.runetek5.MapRegion;
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate;
import com.jagex.game.runetek5.preference.GamePreferences;
import com.jagex.game.runetek5.client.ClientState;

public final class BackgroundMapRegionSetup {

	@OriginalMember(owner = "client!bca", name = "a", descriptor = "(B)V")
	public static void setupBackgroundMapRegions() {
		MapRegion.setMapSize(GamePreferences.uid.buildArea.getValue());
		@Pc(20) int centerChunkX = (Camera.cameraPositionAbsoluteX >> 12) + (MapRegion.sceneBaseX >> 3);
		@Pc(28) int centerChunkY = (MapRegion.sceneBaseY >> 3) + (Camera.cameraPositionAbsoluteY >> 12);
		PlayerUpdate.currentLevel = PlayerUpdate.localPlayer.level = 0;
		PlayerUpdate.localPlayer.move(8, 8);
		MapRegion.npcSpawnArchives = new int[18];
		MapRegion.npcSpawnBuffers = new byte[18][];
		MapRegion.xteas = new int[18][4];
		MapRegion.surfaceMapArchives = new int[18];
		MapRegion.locMapBuffers = new byte[18][];
		MapRegion.regionIds = new int[18];
		MapRegion.underwaterLocArchives = new int[18];
		MapRegion.underwaterMapArchives = new int[18];
		MapRegion.surfaceMapBuffer = new byte[18][];
		MapRegion.surfaceLocArchives = new int[18];
		MapRegion.underwaterLocBuffer = new byte[18][];
		MapRegion.underwaterMapBuffer = new byte[18][];
		@Pc(84) int regionCount = 0;
		@Pc(104) int local104;
		for (@Pc(93) int i = (centerChunkX - (MapRegion.sceneSizeX >> 4)) / 8; i <= (centerChunkX + (MapRegion.sceneSizeX >> 4)) / 8; i++) {
			for (int regionY = (centerChunkY - (MapRegion.sceneSizeY >> 4)) / 8; regionY <= (centerChunkY + (MapRegion.sceneSizeY >> 4)) / 8; regionY++) {
				@Pc(112) int regionId = regionY + (i << 8);
				MapRegion.regionIds[regionCount] = regionId;
				MapRegion.surfaceMapArchives[regionCount] = Resource.MAPS.getGroupId("m" + i + "_" + regionY);
				MapRegion.surfaceLocArchives[regionCount] = Resource.MAPS.getGroupId("l" + i + "_" + regionY);
				MapRegion.npcSpawnArchives[regionCount] = Resource.MAPS.getGroupId("n" + i + "_" + regionY);
				MapRegion.underwaterMapArchives[regionCount] = Resource.MAPS.getGroupId("um" + i + "_" + regionY);
				MapRegion.underwaterLocArchives[regionCount] = Resource.MAPS.getGroupId("ul" + i + "_" + regionY);
				if (MapRegion.npcSpawnArchives[regionCount] == -1) {
					MapRegion.surfaceMapArchives[regionCount] = -1;
					MapRegion.surfaceLocArchives[regionCount] = -1;
					MapRegion.underwaterMapArchives[regionCount] = -1;
					MapRegion.underwaterLocArchives[regionCount] = -1;
				}
				regionCount++;
			}
		}
		for (int regionIndex = regionCount; regionIndex < MapRegion.npcSpawnArchives.length; regionIndex++) {
			MapRegion.npcSpawnArchives[regionIndex] = -1;
			MapRegion.surfaceMapArchives[regionIndex] = -1;
			MapRegion.surfaceLocArchives[regionIndex] = -1;
			MapRegion.underwaterMapArchives[regionIndex] = -1;
			MapRegion.underwaterLocArchives[regionIndex] = -1;
		}
		@Pc(282) MainLogicStep mapBuildStep;
		if (ClientState.mainLogicStep == MainLogicStep.STEP_LOGIN_SCREEN) {
			mapBuildStep = MainLogicStep.STEP_LOGIN_SCREEN_MAP_BUILD;
		} else {
			mapBuildStep = MainLogicStep.STEP_LOBBY_SCREEN_MAP_BUILD;
		}
		MapBuildStart.performUpdate(centerChunkY, centerChunkX, mapBuildStep, false);
	}
}
