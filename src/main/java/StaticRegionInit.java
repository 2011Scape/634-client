import com.jagex.game.runetek5.core.constants.MainLogicStep;
import com.jagex.game.runetek5.core.constants.AreaMode;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.config.Resource;
import com.jagex.game.runetek5.MapRegion;
import com.jagex.game.runetek5.network.BufferedConnectionContext;
import com.jagex.game.runetek5.NPCList;
import com.jagex.game.runetek5.client.ClientState;

public final class StaticRegionInit {

	@OriginalMember(owner = "client!uf", name = "f", descriptor = "(I)V")
	public static void initializeRegion() {
		MapRegion.areaMode = AreaMode.STATIC_AREA;
		@Pc(11) int mapSize = BufferedConnectionContext.receivedBuffer.readUByteSubtract();
		@Pc(15) int baseY = BufferedConnectionContext.receivedBuffer.readUShortAddLittle();
		@Pc(19) int baseX = BufferedConnectionContext.receivedBuffer.readUShortLittle();
		@Pc(30) boolean forcedPositionUpdate = BufferedConnectionContext.receivedBuffer.readUByteInverse() == 1;
		NPCList.resetNpcsOnAreaChange();
		MapRegion.setMapSize(mapSize);
		@Pc(43) int regionCount = (BufferedConnectionContext.currentPacketSize - BufferedConnectionContext.receivedBuffer.currentPosition) / 16;
		MapRegion.xteas = new int[regionCount][4];
		@Pc(53) int local53;
		for (@Pc(49) int i = 0; i < regionCount; i++) {
			for (int keyIndex = 0; keyIndex < 4; keyIndex++) {
				MapRegion.xteas[i][keyIndex] = BufferedConnectionContext.receivedBuffer.readInt();
			}
		}
		MapRegion.underwaterLocBuffer = new byte[regionCount][];
		MapRegion.underwaterMapBuffer = new byte[regionCount][];
		MapRegion.surfaceMapArchives = new int[regionCount];
		MapRegion.locMapBuffers = new byte[regionCount][];
		MapRegion.surfaceLocArchives = new int[regionCount];
		MapRegion.npcSpawnBuffers = null;
		MapRegion.underwaterLocArchives = new int[regionCount];
		MapRegion.npcSpawnArchives = null;
		MapRegion.surfaceMapBuffer = new byte[regionCount][];
		MapRegion.underwaterMapArchives = new int[regionCount];
		MapRegion.regionIds = new int[regionCount];
		regionCount = 0;
		for (int regionX = (baseX - (MapRegion.sceneSizeX >> 4)) / 8; regionX <= (baseX + (MapRegion.sceneSizeX >> 4)) / 8; regionX++) {
			for (@Pc(128) int j = (baseY - (MapRegion.sceneSizeY >> 4)) / 8; j <= ((MapRegion.sceneSizeY >> 4) + baseY) / 8; j++) {
				MapRegion.regionIds[regionCount] = j + (regionX << 8);
				MapRegion.surfaceMapArchives[regionCount] = Resource.MAPS.getGroupId("m" + regionX + "_" + j);
				MapRegion.surfaceLocArchives[regionCount] = Resource.MAPS.getGroupId("l" + regionX + "_" + j);
				MapRegion.underwaterMapArchives[regionCount] = Resource.MAPS.getGroupId("um" + regionX + "_" + j);
				MapRegion.underwaterLocArchives[regionCount] = Resource.MAPS.getGroupId("ul" + regionX + "_" + j);
				regionCount++;
			}
		}
		MapBuildStart.performUpdate(baseY, baseX, MainLogicStep.STEP_GAME_SCREEN_MAP_BUILD, forcedPositionUpdate);
	}
}
