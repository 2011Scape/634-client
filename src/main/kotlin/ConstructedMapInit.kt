import com.jagex.game.runetek5.core.constants.MainLogicStep
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.config.Resource
import com.jagex.game.runetek5.MapRegion
import com.jagex.game.runetek5.network.BufferedConnectionContext
import com.jagex.game.runetek5.NPCList
import com.jagex.game.runetek5.client.ClientState

object ConstructedMapInit {

	@OriginalMember(owner = "client!bc", name = "a", descriptor = "(B)V")
	@JvmStatic
	fun initializeConstructedMap() {
		@Pc(9) val mapSizeType: Int = BufferedConnectionContext.receivedBuffer.readUByte()
		@Pc(13) val mapBaseY: Int = BufferedConnectionContext.receivedBuffer.readUShortAddLittle()
		@Pc(24) val forcedPositionUpdate: Boolean = BufferedConnectionContext.receivedBuffer.readUByte() == 1
		@Pc(30) val mapBaseX: Int = BufferedConnectionContext.receivedBuffer.readUShortAdd()
		MapRegion.areaMode = BufferedConnectionContext.receivedBuffer.readUByteAdd()
		NPCList.resetNpcsOnAreaChange()
		MapRegion.setMapSize(mapSizeType)
		BufferedConnectionContext.receivedBuffer.enterBitMode()
		@Pc(48) var local48: Int
		@Pc(52) var local52: Int
		@Pc(59) var local59: Int
		for (@Pc(44) i in 0 until 4) {
			var zoneX: Int = 0
			while (zoneX < MapRegion.sceneSizeX shr 3) {
				var zoneY: Int = 0
				while (zoneY < MapRegion.sceneSizeY shr 3) {
					val chunkFlag: Int = BufferedConnectionContext.receivedBuffer.gbit(1)
					if (chunkFlag == 1) {
						MapRegion.chunkBitpackedPosition!![i]!![zoneX]!![zoneY] = BufferedConnectionContext.receivedBuffer.gbit(26)
					} else {
						MapRegion.chunkBitpackedPosition!![i]!![zoneX]!![zoneY] = -1
					}
					zoneY++
				}
				zoneX++
			}
		}
		BufferedConnectionContext.receivedBuffer.exitBitMode()
		var regionCount: Int = (BufferedConnectionContext.currentPacketSize - BufferedConnectionContext.receivedBuffer.currentPosition) / 16
		MapRegion.xteas = Array<IntArray?>(regionCount) { IntArray(4) }
		for (regionIndex in 0 until regionCount) {
			for (keyIndex in 0 until 4) {
				MapRegion.xteas!![regionIndex]!![keyIndex] = BufferedConnectionContext.receivedBuffer.readInt()
			}
		}
		MapRegion.npcSpawnArchives = null
		MapRegion.surfaceLocArchives = IntArray(regionCount)
		MapRegion.underwaterLocArchives = IntArray(regionCount)
		MapRegion.surfaceMapArchives = IntArray(regionCount)
		MapRegion.regionIds = IntArray(regionCount)
		MapRegion.underwaterMapArchives = IntArray(regionCount)
		MapRegion.npcSpawnBuffers = null
		MapRegion.underwaterLocBuffer = arrayOfNulls<ByteArray>(regionCount)
		MapRegion.underwaterMapBuffer = arrayOfNulls<ByteArray>(regionCount)
		MapRegion.locMapBuffers = arrayOfNulls<ByteArray>(regionCount)
		MapRegion.surfaceMapBuffer = arrayOfNulls<ByteArray>(regionCount)
		regionCount = 0
		for (level in 0 until 4) {
			@Pc(198) var j: Int = 0
			while (j < MapRegion.sceneSizeX shr 3) {
				@Pc(202) var k: Int = 0
				while (k < MapRegion.sceneSizeY shr 3) {
					@Pc(212) val bitpackedPosition: Int = MapRegion.chunkBitpackedPosition!![level]!![j]!![k]
					if (bitpackedPosition != -1) {
						@Pc(221) val chunkX: Int = (bitpackedPosition shr 14) and 0x3FF
						@Pc(227) val chunkY: Int = (bitpackedPosition shr 3) and 0x7FF
						@Pc(237) var regionId: Int = chunkY / 8 + ((chunkX / 8) shl 8)
						for (@Pc(239) m in 0 until regionCount) {
							if (MapRegion.regionIds!![m] == regionId) {
								regionId = -1
								break
							}
						}
						if (regionId != -1) {
							MapRegion.regionIds!![regionCount] = regionId
							@Pc(276) val regionX: Int = (regionId shr 8) and 0xFF
							@Pc(280) val regionY: Int = regionId and 0xFF
							MapRegion.surfaceMapArchives!![regionCount] = Resource.MAPS!!.getGroupId("m" + regionX + "_" + regionY)
							MapRegion.surfaceLocArchives!![regionCount] = Resource.MAPS!!.getGroupId("l" + regionX + "_" + regionY)
							MapRegion.underwaterMapArchives!![regionCount] = Resource.MAPS!!.getGroupId("um" + regionX + "_" + regionY)
							MapRegion.underwaterLocArchives!![regionCount] = Resource.MAPS!!.getGroupId("ul" + regionX + "_" + regionY)
							regionCount++
						}
					}
					k++
				}
				j++
			}
		}
		MapBuildStart.performUpdate(mapBaseY, mapBaseX, MainLogicStep.STEP_GAME_SCREEN_MAP_BUILD, forcedPositionUpdate)
	}
}
