import com.jagex.game.runetek5.core.constants.MainLogicStep
import com.jagex.game.runetek5.core.constants.AreaMode
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.config.Resource
import com.jagex.game.runetek5.MapRegion
import com.jagex.game.runetek5.network.BufferedConnectionContext
import com.jagex.game.runetek5.NPCList
import com.jagex.game.runetek5.client.ClientState

object StaticRegionInit {

	@OriginalMember(owner = "client!uf", name = "f", descriptor = "(I)V")
	@JvmStatic
	fun initializeRegion() {
		MapRegion.areaMode = AreaMode.STATIC_AREA
		@Pc(11) val mapSize: Int = BufferedConnectionContext.receivedBuffer!!.readUByteSubtract()
		@Pc(15) val baseY: Int = BufferedConnectionContext.receivedBuffer!!.readUShortAddLittle()
		@Pc(19) val baseX: Int = BufferedConnectionContext.receivedBuffer!!.readUShortLittle()
		@Pc(30) val forcedPositionUpdate: Boolean = BufferedConnectionContext.receivedBuffer!!.readUByteInverse() == 1
		NPCList.resetNpcsOnAreaChange()
		MapRegion.setMapSize(mapSize)
		@Pc(43) var regionCount: Int = (BufferedConnectionContext.currentPacketSize - BufferedConnectionContext.receivedBuffer!!.currentPosition) / 16
		MapRegion.xteas = Array<IntArray?>(regionCount) { IntArray(4) }
		@Pc(53) var local53: Int
		@Pc(49) var i: Int = 0
		while (i < regionCount) {
			var keyIndex: Int = 0
			while (keyIndex < 4) {
				MapRegion.xteas!![i]!![keyIndex] = BufferedConnectionContext.receivedBuffer!!.readInt()
				keyIndex++
			}
			i++
		}
		MapRegion.underwaterLocBuffer = arrayOfNulls<ByteArray>(regionCount)
		MapRegion.underwaterMapBuffer = arrayOfNulls<ByteArray>(regionCount)
		MapRegion.surfaceMapArchives = IntArray(regionCount)
		MapRegion.locMapBuffers = arrayOfNulls<ByteArray>(regionCount)
		MapRegion.surfaceLocArchives = IntArray(regionCount)
		MapRegion.npcSpawnBuffers = null
		MapRegion.underwaterLocArchives = IntArray(regionCount)
		MapRegion.npcSpawnArchives = null
		MapRegion.surfaceMapBuffer = arrayOfNulls<ByteArray>(regionCount)
		MapRegion.underwaterMapArchives = IntArray(regionCount)
		MapRegion.regionIds = IntArray(regionCount)
		regionCount = 0
		var regionX: Int = (baseX - (MapRegion.sceneSizeX shr 4)) / 8
		while (regionX <= (baseX + (MapRegion.sceneSizeX shr 4)) / 8) {
			@Pc(128) var j: Int = (baseY - (MapRegion.sceneSizeY shr 4)) / 8
			while (j <= ((MapRegion.sceneSizeY shr 4) + baseY) / 8) {
				MapRegion.regionIds!![regionCount] = j + (regionX shl 8)
				MapRegion.surfaceMapArchives!![regionCount] = Resource.MAPS!!.getGroupId("m" + regionX + "_" + j)
				MapRegion.surfaceLocArchives!![regionCount] = Resource.MAPS!!.getGroupId("l" + regionX + "_" + j)
				MapRegion.underwaterMapArchives!![regionCount] = Resource.MAPS!!.getGroupId("um" + regionX + "_" + j)
				MapRegion.underwaterLocArchives!![regionCount] = Resource.MAPS!!.getGroupId("ul" + regionX + "_" + j)
				regionCount++
				j++
			}
			regionX++
		}
		MapBuildStart.performUpdate(baseY, baseX, MainLogicStep.STEP_GAME_SCREEN_MAP_BUILD, forcedPositionUpdate)
	}
}
