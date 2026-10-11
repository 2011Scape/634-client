import com.jagex.game.runetek5.core.constants.MainLogicStep;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.config.Resource;
import com.jagex.game.runetek5.MapRegion;
import com.jagex.game.runetek5.network.BufferedConnectionContext;
import com.jagex.game.runetek5.NPCList;
import com.jagex.game.runetek5.client.ClientState;

public final class ConstructedMapInit {

	@OriginalMember(owner = "client!bc", name = "a", descriptor = "(B)V")
	public static void initializeConstructedMap() {
		@Pc(9) int mapSizeType = BufferedConnectionContext.receivedBuffer.readUByte();
		@Pc(13) int mapBaseY = BufferedConnectionContext.receivedBuffer.readUShortAddLittle();
		@Pc(24) boolean forcedPositionUpdate = BufferedConnectionContext.receivedBuffer.readUByte() == 1;
		@Pc(30) int mapBaseX = BufferedConnectionContext.receivedBuffer.readUShortAdd();
		MapRegion.areaMode = BufferedConnectionContext.receivedBuffer.readUByteAdd();
		NPCList.resetNpcsOnAreaChange();
		MapRegion.setMapSize(mapSizeType);
		BufferedConnectionContext.receivedBuffer.enterBitMode();
		@Pc(48) int local48;
		@Pc(52) int local52;
		@Pc(59) int local59;
		for (@Pc(44) int i = 0; i < 4; i++) {
			for (int zoneX = 0; zoneX < MapRegion.sceneSizeX >> 3; zoneX++) {
				for (int zoneY = 0; zoneY < MapRegion.sceneSizeY >> 3; zoneY++) {
					int chunkFlag = BufferedConnectionContext.receivedBuffer.gbit(1);
					if (chunkFlag == 1) {
						MapRegion.chunkBitpackedPosition[i][zoneX][zoneY] = BufferedConnectionContext.receivedBuffer.gbit(26);
					} else {
						MapRegion.chunkBitpackedPosition[i][zoneX][zoneY] = -1;
					}
				}
			}
		}
		BufferedConnectionContext.receivedBuffer.exitBitMode();
		int regionCount = (BufferedConnectionContext.currentPacketSize - BufferedConnectionContext.receivedBuffer.currentPosition) / 16;
		MapRegion.xteas = new int[regionCount][4];
		for (int regionIndex = 0; regionIndex < regionCount; regionIndex++) {
			for (int keyIndex = 0; keyIndex < 4; keyIndex++) {
				MapRegion.xteas[regionIndex][keyIndex] = BufferedConnectionContext.receivedBuffer.readInt();
			}
		}
		MapRegion.npcSpawnArchives = null;
		MapRegion.surfaceLocArchives = new int[regionCount];
		MapRegion.underwaterLocArchives = new int[regionCount];
		MapRegion.surfaceMapArchives = new int[regionCount];
		MapRegion.regionIds = new int[regionCount];
		MapRegion.underwaterMapArchives = new int[regionCount];
		MapRegion.npcSpawnBuffers = null;
		MapRegion.underwaterLocBuffer = new byte[regionCount][];
		MapRegion.underwaterMapBuffer = new byte[regionCount][];
		MapRegion.locMapBuffers = new byte[regionCount][];
		MapRegion.surfaceMapBuffer = new byte[regionCount][];
		regionCount = 0;
		for (int level = 0; level < 4; level++) {
			for (@Pc(198) int j = 0; j < MapRegion.sceneSizeX >> 3; j++) {
				for (@Pc(202) int k = 0; k < MapRegion.sceneSizeY >> 3; k++) {
					@Pc(212) int bitpackedPosition = MapRegion.chunkBitpackedPosition[level][j][k];
					if (bitpackedPosition != -1) {
						@Pc(221) int chunkX = bitpackedPosition >> 14 & 0x3FF;
						@Pc(227) int chunkY = bitpackedPosition >> 3 & 0x7FF;
						@Pc(237) int regionId = chunkY / 8 + (chunkX / 8 << 8);
						for (@Pc(239) int m = 0; m < regionCount; m++) {
							if (MapRegion.regionIds[m] == regionId) {
								regionId = -1;
								break;
							}
						}
						if (regionId != -1) {
							MapRegion.regionIds[regionCount] = regionId;
							@Pc(276) int regionX = regionId >> 8 & 0xFF;
							@Pc(280) int regionY = regionId & 0xFF;
							MapRegion.surfaceMapArchives[regionCount] = Resource.MAPS.getGroupId("m" + regionX + "_" + regionY);
							MapRegion.surfaceLocArchives[regionCount] = Resource.MAPS.getGroupId("l" + regionX + "_" + regionY);
							MapRegion.underwaterMapArchives[regionCount] = Resource.MAPS.getGroupId("um" + regionX + "_" + regionY);
							MapRegion.underwaterLocArchives[regionCount] = Resource.MAPS.getGroupId("ul" + regionX + "_" + regionY);
							regionCount++;
						}
					}
				}
			}
		}
		MapBuildStart.performUpdate(mapBaseY, mapBaseX, MainLogicStep.STEP_GAME_SCREEN_MAP_BUILD, forcedPositionUpdate);
	}
}
