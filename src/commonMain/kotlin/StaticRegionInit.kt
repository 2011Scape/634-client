import com.jagex.game.runetek5.core.constants.MainLogicStep
import com.jagex.game.runetek5.core.constants.AreaMode
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.config.Resource
import com.jagex.game.runetek5.MapRegion
import com.jagex.game.runetek5.network.BufferedConnectionContext
import com.jagex.game.runetek5.NPCList
import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.client.features.ClientFeatures
import kotlin.jvm.JvmStatic

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
		// Not part of the original client: the server's keys, kept by region for a scene of another size
		// (ClientFeatures.RENDER_DISTANCE builds a bigger one than the server's: hdApplySceneSize)
		@Pc(43) val regionCount: Int = (BufferedConnectionContext.currentPacketSize - BufferedConnectionContext.receivedBuffer!!.currentPosition) / 16
		val serverKeys: Array<IntArray?> = arrayOfNulls<IntArray>(regionCount)
		@Pc(49) var i: Int = 0
		while (i < regionCount) {
			val keys: IntArray = IntArray(4)
			var keyIndex: Int = 0
			while (keyIndex < 4) {
				keys[keyIndex] = BufferedConnectionContext.receivedBuffer!!.readInt()
				keyIndex++
			}
			serverKeys[i] = keys
			i++
		}
		MapRegion.hdServerMapSize = mapSize
		MapRegion.hdServerKeys = serverKeys
		MapRegion.hdBaseX = baseX
		MapRegion.hdBaseY = baseY
		hdApplySceneSize()
		MapBuildStart.performUpdate(baseY, baseX, MainLogicStep.STEP_GAME_SCREEN_MAP_BUILD, forcedPositionUpdate)
	}

	/**
	 * Not part of the original client: the scene's size and its region list (the original's, moved
	 * here) for the last region packet (MapRegion.hdServerMapSize, hdBaseX, hdBaseY, hdServerKeys),
	 * the size raised by the draw distance (ClientFeatures.sceneMapSize): the game sizes its fog and
	 * draw radius from the scene, so a bigger scene is a longer view with the edge still fogged. The
	 * server's keys are matched to the regions by position (the server's own list order over its
	 * size); a region the server did not send gets zero keys (Void's maps are not encrypted).
	 */
	@JvmStatic
	fun hdApplySceneSize() {
		val serverMapSize: Int = MapRegion.hdServerMapSize
		val baseX: Int = MapRegion.hdBaseX
		val baseY: Int = MapRegion.hdBaseY
		val serverKeys: Array<IntArray?> = MapRegion.hdServerKeys ?: arrayOfNulls<IntArray>(0)
		// the server's regions, in its order
		val serverSize: Int = MapRegion.MAP_SIZES[serverMapSize]
		val serverIds: IntArray = IntArray(serverKeys.size)
		var serverCount: Int = 0
		var sx: Int = (baseX - (serverSize shr 4)) / 8
		while (sx <= (baseX + (serverSize shr 4)) / 8) {
			var sy: Int = (baseY - (serverSize shr 4)) / 8
			while (sy <= ((serverSize shr 4) + baseY) / 8) {
				if (serverCount < serverIds.size) {
					serverIds[serverCount] = sy + (sx shl 8)
				}
				serverCount++
				sy++
			}
			sx++
		}
		MapRegion.setMapSize(ClientFeatures.sceneMapSize(serverMapSize))
		var regionCount: Int = 0
		var countX: Int = (baseX - (MapRegion.sceneSizeX shr 4)) / 8
		while (countX <= (baseX + (MapRegion.sceneSizeX shr 4)) / 8) {
			regionCount += ((MapRegion.sceneSizeY shr 4) + baseY) / 8 - (baseY - (MapRegion.sceneSizeY shr 4)) / 8 + 1
			countX++
		}
		MapRegion.xteas = Array<IntArray?>(regionCount) { IntArray(4) }
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
			var j: Int = (baseY - (MapRegion.sceneSizeY shr 4)) / 8
			while (j <= ((MapRegion.sceneSizeY shr 4) + baseY) / 8) {
				val id: Int = j + (regionX shl 8)
				MapRegion.regionIds!![regionCount] = id
				for (k in 0 until minOf(serverCount, serverIds.size)) {
					if (serverIds[k] == id) {
						val keys: IntArray? = serverKeys[k]
						if (keys != null) {
							for (key in 0 until 4) {
								MapRegion.xteas!![regionCount]!![key] = keys[key]
							}
						}
					}
				}
				MapRegion.surfaceMapArchives!![regionCount] = Resource.MAPS!!.getGroupId("m" + regionX + "_" + j)
				MapRegion.surfaceLocArchives!![regionCount] = Resource.MAPS!!.getGroupId("l" + regionX + "_" + j)
				MapRegion.underwaterMapArchives!![regionCount] = Resource.MAPS!!.getGroupId("um" + regionX + "_" + j)
				MapRegion.underwaterLocArchives!![regionCount] = Resource.MAPS!!.getGroupId("ul" + regionX + "_" + j)
				regionCount++
				j++
			}
			regionX++
		}
	}
}
