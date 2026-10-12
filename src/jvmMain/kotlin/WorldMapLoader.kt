import com.jagex.game.runetek5.config.staticelements.StaticElements
import com.jagex.game.runetek5.MapList
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMap
import com.jagex.game.runetek5.core.datastruct.key.Deque
import com.jagex.game.runetek5.config.material.MaterialProvider
import com.jagex.game.runetek5.graphics.Toolkit
import com.jagex.game.runetek5.statics.WorldMapRenderer
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.client.login.LoginManager
import com.jagex.game.runetek5.config.Resource
import com.jagex.game.runetek5.config.TypeLists
import com.jagex.game.runetek5.graphics.ifcomponent.InterfaceComponentRenderer
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMapDisplay
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMapInteraction
import com.jagex.game.runetek5.client.GameShell
import com.jagex.game.runetek5.graphics.map.minimap.ComponentMinimap
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate
import com.jagex.game.runetek5.network.BufferedConnectionContext
import com.jagex.game.runetek5.MapRegion

object WorldMapLoader {

	@OriginalMember(owner = "client!rba", name = "a", descriptor = "(Lclient!d;Lclient!ha;I)V")
	@JvmStatic
	fun loadAssets(@OriginalArg(0) textureSource: MaterialProvider?, @OriginalArg(1) toolkit: Toolkit?) {
		if (WorldMap.area == null) {
			return
		}
		if (ComponentMinimap.mapAssetLoadingPercentage < 10) {
			if (!MapList.MAP_AREA_RESOURCE!!.isGroupReady(WorldMap.area!!.filenamePrefix)) {
				ComponentMinimap.mapAssetLoadingPercentage = Resource.WORLD_MAP!!.getGroupCompletePercentage(WorldMap.area!!.filenamePrefix) / 10
				return
			}
			WorldMapMemoryRelease.resetSceneAndCaches()
			ComponentMinimap.mapAssetLoadingPercentage = 10
		}
		if (ComponentMinimap.mapAssetLoadingPercentage == 10) {
			WorldMap.mapSquareAbsX = WorldMap.area!!.displayMinZ shr 6 shl 6
			WorldMap.mapSquareAbsY = WorldMap.area!!.boundsMinX shr 6 shl 6
			WorldMapRenderer.mapSizeX = (WorldMap.area!!.displayMaxX shr 6 shl 6) + 64 - WorldMap.mapSquareAbsX
			WorldMapRenderer.mapSizeY = (WorldMap.area!!.displayZ2 shr 6 shl 6) + 64 - WorldMap.mapSquareAbsY
			@Pc(74) val placement: IntArray = IntArray(3)
			@Pc(76) var playerMapX: Int = -1
			@Pc(78) var playerMapY: Int = -1
			if (WorldMap.area!!.convertSourceToDisplay(placement, MapRegion.sceneBaseX + (PlayerUpdate.localPlayer!!.x shr 9), (PlayerUpdate.localPlayer!!.z shr 9) + MapRegion.sceneBaseY, PlayerUpdate.localPlayer!!.level.toInt())) {
				playerMapX = placement[1] - WorldMap.mapSquareAbsX
				playerMapY = placement[2] - WorldMap.mapSquareAbsY
			}
			if (!LoginManager.worldMapUseTargetPosition && playerMapX >= 0 && WorldMapRenderer.mapSizeX > playerMapX && playerMapY >= 0 && WorldMapRenderer.mapSizeY > playerMapY) {
				playerMapX += (java.lang.Math.random() * 10.0).toInt() - 5
				playerMapY += (java.lang.Math.random() * 10.0).toInt() - 5
				WorldMapDisplay.mapCenterX = playerMapX
				WorldMapDisplay.mapCenterY = playerMapY
			} else if (InterfaceComponentRenderer.worldMapRequestedScrollX == -1 || WorldMapDisplay.worldMapRequestedScrollY == -1) {
				WorldMap.area!!.convertDisplayToSource(placement, WorldMap.area!!.bitpackedPlacement and 0x3FFF, WorldMap.area!!.bitpackedPlacement shr 14 and 0x3FFF)
				WorldMapDisplay.mapCenterY = placement[2] - WorldMap.mapSquareAbsY
				WorldMapDisplay.mapCenterX = placement[1] - WorldMap.mapSquareAbsX
			} else {
				WorldMap.area!!.convertDisplayToSource(placement, WorldMapDisplay.worldMapRequestedScrollY, InterfaceComponentRenderer.worldMapRequestedScrollX)
				WorldMapDisplay.worldMapRequestedScrollY = -1
				InterfaceComponentRenderer.worldMapRequestedScrollX = -1
				if (placement != null) {
					WorldMapDisplay.mapCenterX = placement[1] - WorldMap.mapSquareAbsX
					WorldMapDisplay.mapCenterY = placement[2] - WorldMap.mapSquareAbsY
				}
				LoginManager.worldMapUseTargetPosition = false
			}
			if (WorldMap.area!!.defaultZoom == 37) {
				WorldMap.mapZoom = 3.0f
				WorldMapRenderer.requestedMapZoom = 3.0f
			} else if (WorldMap.area!!.defaultZoom == 50) {
				WorldMap.mapZoom = 4.0f
				WorldMapRenderer.requestedMapZoom = 4.0f
			} else if (WorldMap.area!!.defaultZoom == 75) {
				WorldMap.mapZoom = 6.0f
				WorldMapRenderer.requestedMapZoom = 6.0f
			} else if (WorldMap.area!!.defaultZoom == 100) {
				WorldMap.mapZoom = 8.0f
				WorldMapRenderer.requestedMapZoom = 8.0f
			} else if (WorldMap.area!!.defaultZoom == 200) {
				WorldMap.mapZoom = 16.0f
				WorldMapRenderer.requestedMapZoom = 16.0f
			} else {
				WorldMap.mapZoom = 8.0f
				WorldMapRenderer.requestedMapZoom = 8.0f
			}
			WorldMap.tileSize = WorldMap.mapZoom.toInt() shr 1
			WorldMap.tileShapePatterns = WorldMap.createTileShapePatterns(WorldMap.tileSize)
			WorldMapDisplay.clampMapCenter()
			WorldMap.allocateBuffers()
			WorldMapInteraction.MEC_CLICKZONES = Deque()
			WorldMapRenderer.hueRandomisation += (java.lang.Math.random() * 5.0).toInt() - 2
			if (WorldMapRenderer.hueRandomisation < -8) {
				WorldMapRenderer.hueRandomisation = -8
			}
			if (WorldMapRenderer.hueRandomisation > 8) {
				WorldMapRenderer.hueRandomisation = 8
			}
			WorldMapRenderer.lightnessRandomisation += (java.lang.Math.random() * 5.0).toInt() - 2
			if (WorldMapRenderer.lightnessRandomisation < -16) {
				WorldMapRenderer.lightnessRandomisation = -16
			}
			if (WorldMapRenderer.lightnessRandomisation > 16) {
				WorldMapRenderer.lightnessRandomisation = 16
			}
			WorldMap.initOverlayColours(textureSource, WorldMapRenderer.hueRandomisation shr 2 shl 10, WorldMapRenderer.lightnessRandomisation shr 1)
			WorldMap.MECTYPE_LIST!!.setCacheSize(256, 1024)
			MapList.MAP_SPRITE_TYPE_LIST!!.setCacheSize(256, 256)
			MapList.LOCTYPE_LIST!!.setCacheSize(4096)
			TypeLists.VARBIT!!.setCacheSize(256)
			ComponentMinimap.mapAssetLoadingPercentage = 20
		} else if (ComponentMinimap.mapAssetLoadingPercentage == 20) {
			BufferedConnectionContext.sendKeepAlive(true)
			MapList.renderMap(toolkit, WorldMapRenderer.hueRandomisation, WorldMapRenderer.lightnessRandomisation)
			ComponentMinimap.mapAssetLoadingPercentage = 60
			BufferedConnectionContext.sendKeepAlive(true)
			GameShell.resetDrawCache()
		} else if (ComponentMinimap.mapAssetLoadingPercentage == 60) {
			if (MapList.MAP_AREA_RESOURCE!!.groupExists(WorldMap.area!!.filenamePrefix + "_staticelements")) {
				if (!MapList.MAP_AREA_RESOURCE!!.isGroupReady(WorldMap.area!!.filenamePrefix + "_staticelements")) {
					return
				}
				MapList.staticElements = StaticElements.load(MapList.MAP_AREA_RESOURCE, WorldMap.area!!.filenamePrefix + "_staticelements", LoginManager.memberWorld)
			} else {
				MapList.staticElements = StaticElements(0)
			}
			MapList.loadWorldMapCategories()
			ComponentMinimap.mapAssetLoadingPercentage = 70
			BufferedConnectionContext.sendKeepAlive(true)
			GameShell.resetDrawCache()
		} else if (ComponentMinimap.mapAssetLoadingPercentage == 70) {
			WorldMapHelvetica11.helvetica11Font = HelveticaFont(toolkit, 11, true, GameShell.gameCanvas)
			ComponentMinimap.mapAssetLoadingPercentage = 73
			BufferedConnectionContext.sendKeepAlive(true)
			GameShell.resetDrawCache()
		} else if (ComponentMinimap.mapAssetLoadingPercentage == 73) {
			WorldMapHelvetica12.helvetica12Font = HelveticaFont(toolkit, 12, true, GameShell.gameCanvas)
			ComponentMinimap.mapAssetLoadingPercentage = 76
			BufferedConnectionContext.sendKeepAlive(true)
			GameShell.resetDrawCache()
		} else if (ComponentMinimap.mapAssetLoadingPercentage == 76) {
			WorldMapHelvetica14.helvetica14Font = HelveticaFont(toolkit, 14, true, GameShell.gameCanvas)
			ComponentMinimap.mapAssetLoadingPercentage = 79
			BufferedConnectionContext.sendKeepAlive(true)
			GameShell.resetDrawCache()
		} else if (ComponentMinimap.mapAssetLoadingPercentage == 79) {
			WorldMapHelvetica17.helvetica17Font = HelveticaFont(toolkit, 17, true, GameShell.gameCanvas)
			ComponentMinimap.mapAssetLoadingPercentage = 82
			BufferedConnectionContext.sendKeepAlive(true)
			GameShell.resetDrawCache()
		} else if (ComponentMinimap.mapAssetLoadingPercentage == 82) {
			WorldMapHelvetica19.helvetica19Font = HelveticaFont(toolkit, 19, true, GameShell.gameCanvas)
			ComponentMinimap.mapAssetLoadingPercentage = 85
			BufferedConnectionContext.sendKeepAlive(true)
			GameShell.resetDrawCache()
		} else if (ComponentMinimap.mapAssetLoadingPercentage == 85) {
			WorldMapHelvetica22.helvetica22Font = HelveticaFont(toolkit, 22, true, GameShell.gameCanvas)
			ComponentMinimap.mapAssetLoadingPercentage = 88
			BufferedConnectionContext.sendKeepAlive(true)
			GameShell.resetDrawCache()
		} else if (ComponentMinimap.mapAssetLoadingPercentage == 88) {
			WorldMapHelvetica26.helvetica26Font = HelveticaFont(toolkit, 26, true, GameShell.gameCanvas)
			ComponentMinimap.mapAssetLoadingPercentage = 91
			BufferedConnectionContext.sendKeepAlive(true)
			GameShell.resetDrawCache()
		} else {
			WorldMapHelvetica30.helvetica30Font = HelveticaFont(toolkit, 30, true, GameShell.gameCanvas)
			ComponentMinimap.mapAssetLoadingPercentage = 100
			BufferedConnectionContext.sendKeepAlive(true)
			GameShell.resetDrawCache()
			System.gc()
		}
	}
}
