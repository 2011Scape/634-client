import com.jagex.game.runetek5.network.packet.TcpPacket
import com.jagex.game.runetek5.config.staticelements.StaticElements
import com.jagex.game.runetek5.MapRegion
import com.jagex.game.runetek5.rendering.MessageBox
import com.jagex.game.runetek5.Minimap
import com.jagex.game.runetek5.W2DebugMode
import com.jagex.game.runetek5.core.constants.AreaMode
import com.jagex.game.runetek5.core.constants.MainLogicStep
import com.jagex.game.runetek5.UnloadedLocCount
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.client.features.ClientFeatures
import com.jagex.game.runetek5.config.Resource
import com.jagex.game.runetek5.graphics.font.FontManager
import com.jagex.game.runetek5.scene.MapLoader
import com.jagex.game.runetek5.rendering.Renderers
import com.jagex.game.runetek5.ClientPacket
import com.jagex.game.runetek5.client.GameShell
import com.jagex.game.runetek5.network.BufferedConnectionContext
import com.jagex.game.runetek5.scene.environment.EnvironmentManager
import com.jagex.game.runetek5.tools.LocalizedText
import com.jagex.game.runetek5.scene.Scene
import com.jagex.game.runetek5.client.input.console.Console
import com.jagex.game.runetek5.sound.player.area.SoundEffectPlayer
import com.jagex.game.runetek5.network.packet.ClientProt
import com.jagex.game.runetek5.NPCList
import com.jagex.game.runetek5.ObjStackEntity
import com.jagex.game.runetek5.scene.LocAction
import com.jagex.game.runetek5.scene.SettingsBits
import com.jagex.game.runetek5.Skybox
import com.jagex.game.runetek5.Terrain
import com.jagex.game.runetek5.client.login.LoginManager
import com.jagex.game.runetek5.network.Connection
import com.jagex.game.runetek5.preference.GamePreferences
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMap
import com.jagex.game.runetek5.sound.player.area.AmbientSound
import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.config.TypeLists
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate
import com.jagex.game.runetek5.utils.Utils

object MapBuildStep {

	@OriginalMember(owner = "client!vg", name = "a", descriptor = "(I)V")
	@JvmStatic
	fun loadRegion() {
		BufferedConnectionContext.sendKeepAlive(false)
		MapBuildStep.pendingMapFileCount = 0
		@Pc(12) var loaded: Boolean = true
		@Pc(14) var i: Int = 0
		while (i < MapRegion.surfaceMapBuffer!!.size) {
			if (MapRegion.surfaceMapArchives!![i] != -1 && MapRegion.surfaceMapBuffer!![i] == null) {
				MapRegion.surfaceMapBuffer!![i] = Resource.MAPS!!.getFile(MapRegion.surfaceMapArchives!![i], 0)
				if (MapRegion.surfaceMapBuffer!![i] == null) {
					loaded = false
					MapBuildStep.pendingMapFileCount++
				}
			}
			if (MapRegion.surfaceLocArchives!![i] != -1 && MapRegion.locMapBuffers!![i] == null) {
				// Not part of the original client: a group whose key is missing or wrong fails to decrypt;
				// it is read as empty, so the region loads without it instead of the client crashing.
				try {
					MapRegion.locMapBuffers!![i] = Resource.MAPS!!.getFile(MapRegion.surfaceLocArchives!![i], 0, MapRegion.xteas!![i])
				} catch (ex: RuntimeException) {
					MapRegion.locMapBuffers!![i] = ByteArray(4)
				}
				if (MapRegion.locMapBuffers!![i] == null) {
					MapBuildStep.pendingMapFileCount++
					loaded = false
				}
			}
			if (MapRegion.underwaterMapArchives!![i] != -1 && MapRegion.underwaterMapBuffer!![i] == null) {
				MapRegion.underwaterMapBuffer!![i] = Resource.MAPS!!.getFile(MapRegion.underwaterMapArchives!![i], 0)
				if (MapRegion.underwaterMapBuffer!![i] == null) {
					MapBuildStep.pendingMapFileCount++
					loaded = false
				}
			}
			if (MapRegion.underwaterLocArchives!![i] != -1 && MapRegion.underwaterLocBuffer!![i] == null) {
				MapRegion.underwaterLocBuffer!![i] = Resource.MAPS!!.getFile(MapRegion.underwaterLocArchives!![i], 0)
				if (MapRegion.underwaterLocBuffer!![i] == null) {
					loaded = false
					MapBuildStep.pendingMapFileCount++
				}
			}
			if (MapRegion.npcSpawnArchives != null && MapRegion.npcSpawnBuffers!![i] == null && MapRegion.npcSpawnArchives!![i] != -1) {
				// Not part of the original client: a group whose key is missing or wrong fails to decrypt;
				// it is read as empty, so the region loads without it instead of the client crashing.
				try {
					MapRegion.npcSpawnBuffers!![i] = Resource.MAPS!!.getFile(MapRegion.npcSpawnArchives!![i], 0, MapRegion.xteas!![i])
				} catch (ex: RuntimeException) {
					MapRegion.npcSpawnBuffers!![i] = ByteArray(4)
				}
				if (MapRegion.npcSpawnBuffers!![i] == null) {
					loaded = false
					MapBuildStep.pendingMapFileCount++
				}
			}
			i++
		}
		if (StaticElements.aClass333_3 == null) {
			if (WorldMap.aClass2_Sub6_Sub4_3 == null || !Resource.WORLD_MAP!!.groupExists(WorldMap.aClass2_Sub6_Sub4_3!!.filenamePrefix + "_staticelements")) {
				StaticElements.aClass333_3 = StaticElements(0)
			} else if (Resource.WORLD_MAP!!.isGroupReady(WorldMap.aClass2_Sub6_Sub4_3!!.filenamePrefix + "_staticelements")) {
				StaticElements.aClass333_3 = StaticElements.load(Resource.WORLD_MAP, WorldMap.aClass2_Sub6_Sub4_3!!.filenamePrefix + "_staticelements", LoginManager.memberWorld)
			} else {
				loaded = false
				MapBuildStep.pendingMapFileCount++
			}
		}
		if (!loaded) {
			MapBuildStep.mapLoadingStage = 1
			return
		}
		loaded = true
		UnloadedLocCount.unloadedLocCount = 0
		@Pc(268) var local268: Int
		@Pc(278) var local278: Int
		@Pc(250) var j: Int = 0
		while (j < MapRegion.surfaceMapBuffer!!.size) {
			@Pc(256) var mapData: ByteArray? = MapRegion.locMapBuffers!![j]
			if (mapData != null) {
				var baseX: Int = (MapRegion.regionIds!![j] shr 8) * 64 - MapRegion.sceneBaseX
				var baseY: Int = (MapRegion.regionIds!![j] and 0xFF) * 64 - MapRegion.sceneBaseY
				if (MapRegion.areaMode != AreaMode.STATIC_AREA) {
					baseY = 10
					baseX = 10
				}
				loaded = loaded and MapRegion.areMapLocTypesLoaded(MapRegion.sceneSizeX, baseX, MapRegion.sceneSizeY, baseY, mapData)
			}
			mapData = MapRegion.underwaterLocBuffer!![j]
			if (mapData != null) {
				var baseX: Int = (MapRegion.regionIds!![j] shr 8) * 64 - MapRegion.sceneBaseX
				var baseY: Int = (MapRegion.regionIds!![j] and 0xFF) * 64 - MapRegion.sceneBaseY
				if (MapRegion.areaMode != AreaMode.STATIC_AREA) {
					baseY = 10
					baseX = 10
				}
				loaded = loaded and MapRegion.areMapLocTypesLoaded(MapRegion.sceneSizeX, baseX, MapRegion.sceneSizeY, baseY, mapData)
			}
			j++
		}
		if (!loaded) {
			MapBuildStep.mapLoadingStage = 2
			return
		}
		if (MapBuildStep.mapLoadingStage != 0) {
			MessageBox.draw(true, FontManager.aClass68_4, FontManager.messageboxFont, LocalizedText.aClass351_14.get(ClientState.language) + "<br>(100%)", ClientState.activeToolkit)
		}
		SoundEffectPlayer.updatePlayers()
		TypeLists.resetAllCaches()
		Static683.method656()
		@Pc(382) var hasUnderwaterMap: Boolean = false
		if (ClientState.activeToolkit!!.stillwaterEffectLoaded() && GamePreferences.uid!!.water!!.getValue() == 2) {
			var regionIndex: Int = 0
			while (regionIndex < MapRegion.surfaceMapBuffer!!.size) {
				if (MapRegion.underwaterLocBuffer!![regionIndex] != null || MapRegion.underwaterMapBuffer!![regionIndex] != null) {
					hasUnderwaterMap = true
					break
				}
				regionIndex++
			}
		}
		var visibleTiles: Int
		if (GamePreferences.uid!!.fog!!.getValue() == 1) {
			visibleTiles = MapRegion.FOG_VISIBLE_TILES[MapRegion.currentMapSize]
		} else {
			visibleTiles = MapRegion.DRAW_DISTANCES_NO_FOG[MapRegion.currentMapSize]
		}
		if (ClientState.activeToolkit!!.extendBuildArea()) {
			visibleTiles++
		}
		if (ClientFeatures.RENDER_DISTANCE != 1.0) {
			// Not part of the original client: the scene is drawn further out.
			visibleTiles = (ClientFeatures.RENDER_DISTANCE * visibleTiles.toDouble()).toInt()
		}
		SceneInitializer.initializeScene(ClientState.activeToolkit, Scene.renderCores, MapRegion.sceneSizeX, MapRegion.sceneSizeY, visibleTiles, hasUnderwaterMap, ClientState.activeToolkit!!.getMaxLights() > 0)
		W2DebugMode.setW2DebugMode(W2DebugMode.w2DebugMode)
		if (W2DebugMode.w2DebugMode == 0) {
			W2DebugMode.setFont(null)
		} else {
			W2DebugMode.setFont(Renderers.FONT_RENDERER)
		}
		var level: Int = 0
		while (level < 4) {
			MapRegion.aClass104Array3[level]!!.reset()
			level++
		}
		SettingsBits.clearTileFunctionMasks()
		AmbientSound.unlinkSounds(false)
		EnvironmentManager.resetChunkEnvironments()
		Skybox.configNpcJs5 = null
		Scene.gameObjectsUploaded = false
		SoundEffectPlayer.updatePlayers()
		System.gc()
		BufferedConnectionContext.sendKeepAlive(true)
		MapRegion.enableLowDetailShadows()
		MapLoader.hdShadows = GamePreferences.uid!!.hardShadows!!.getValue()
		Terrain.highLightDetail = GameShell.maxMemory >= 96
		Terrain.hdWater = GamePreferences.uid!!.water!!.getValue() == 2
		Terrain.hdLight = GamePreferences.uid!!.lightDetail!!.getValue() == 1
		Minimap.visibleFromLevel = if (GamePreferences.uid!!.animateBackgroundOverride!!.getValue() == 1) -1 else MapRegion.mapBuildLevel
		Terrain.blendFloor = GamePreferences.uid!!.groundBlending!!.getValue() == 1
		Terrain.enableTextures = GamePreferences.uid!!.textures!!.getValue() == 1
		MapRegion.aClass322_Sub1_1 = MapRegion(4, MapRegion.sceneSizeX, MapRegion.sceneSizeY, false)
		if (MapRegion.areaMode == AreaMode.STATIC_AREA) {
			MapRegion.decodeMapTilesClient(MapRegion.aClass322_Sub1_1, MapRegion.surfaceMapBuffer)
		} else {
			MapRegion.decodeMapTilesServer(MapRegion.aClass322_Sub1_1, MapRegion.surfaceMapBuffer)
		}
		EnvironmentManager.applyEnvironmentSunPosition(MapRegion.sceneSizeX shr 4, MapRegion.sceneSizeY shr 4)
		EnvironmentManager.updateAtmosphere()
		if (hasUnderwaterMap) {
			Scene.setUnderwater(true)
			MapRegion.aClass322_Sub1_2 = MapRegion(1, MapRegion.sceneSizeX, MapRegion.sceneSizeY, true)
			if (MapRegion.areaMode == AreaMode.STATIC_AREA) {
				MapRegion.decodeMapTilesClient(MapRegion.aClass322_Sub1_2, MapRegion.underwaterMapBuffer)
				BufferedConnectionContext.sendKeepAlive(true)
			} else {
				MapRegion.decodeMapTilesServer(MapRegion.aClass322_Sub1_2, MapRegion.underwaterMapBuffer)
				BufferedConnectionContext.sendKeepAlive(true)
			}
			MapRegion.aClass322_Sub1_2!!.addHeightOffsets(MapRegion.aClass322_Sub1_1!!.tileHeights!![0])
			MapRegion.aClass322_Sub1_2!!.initializeFloor(ClientState.activeToolkit, null, null)
			Scene.setUnderwater(false)
		}
		MapRegion.aClass322_Sub1_1!!.initializeFloor(ClientState.activeToolkit, if (hasUnderwaterMap) MapRegion.aClass322_Sub1_2!!.tileHeights else null, MapRegion.aClass104Array3)
		if (MapRegion.areaMode == AreaMode.STATIC_AREA) {
			BufferedConnectionContext.sendKeepAlive(true)
			MapRegion.decodeLocMapsClient(MapRegion.locMapBuffers, MapRegion.aClass322_Sub1_1)
			if (MapRegion.npcSpawnBuffers != null) {
				NPCList.decodeNpcSpawns()
			}
		} else {
			BufferedConnectionContext.sendKeepAlive(true)
			MapRegion.decodeLocMapsServer(MapRegion.locMapBuffers, MapRegion.aClass322_Sub1_1)
		}
		TypeLists.resetAllCaches()
		if (GameShell.maxMemory < 96) {
			Scene.uploadAllGameObjects()
		}
		BufferedConnectionContext.sendKeepAlive(true)
		MapRegion.aClass322_Sub1_1!!.calculateUnderlayPalette(null, if (hasUnderwaterMap) Scene.aClass65Array3!![0] else null, ClientState.activeToolkit)
		MapRegion.aClass322_Sub1_1!!.calculateVisibility(false, ClientState.activeToolkit)
		BufferedConnectionContext.sendKeepAlive(true)
		if (hasUnderwaterMap) {
			Scene.setUnderwater(true)
			BufferedConnectionContext.sendKeepAlive(true)
			if (MapRegion.areaMode == AreaMode.STATIC_AREA) {
				MapRegion.decodeLocMapsClient(MapRegion.underwaterLocBuffer, MapRegion.aClass322_Sub1_2)
			} else {
				MapRegion.decodeLocMapsServer(MapRegion.underwaterLocBuffer, MapRegion.aClass322_Sub1_2)
			}
			TypeLists.resetAllCaches()
			BufferedConnectionContext.sendKeepAlive(true)
			MapRegion.aClass322_Sub1_2!!.calculateUnderlayPalette(Scene.aClass65Array2!![0], null, ClientState.activeToolkit)
			MapRegion.aClass322_Sub1_2!!.calculateVisibility(true, ClientState.activeToolkit)
			BufferedConnectionContext.sendKeepAlive(true)
			Scene.setUnderwater(false)
		}
		Terrain.releaseUnderlayData()
		@Pc(771) var lowestLevel: Int = MapRegion.aClass322_Sub1_1!!.minLevel
		if (PlayerUpdate.currentLevel < lowestLevel) {
			lowestLevel = PlayerUpdate.currentLevel
		}
		if (PlayerUpdate.currentLevel - 1 > lowestLevel) {
			lowestLevel = PlayerUpdate.currentLevel - 1
		}
		if (GamePreferences.uid!!.animateBackgroundOverride!!.getValue() == 0) {
			Scene.fillLevelTiles(lowestLevel)
		} else {
			Scene.fillLevelTiles(0)
		}
		@Pc(813) var local813: Int
		@Pc(817) var local817: Int
		@Pc(809) var k: Int = 0
		while (k < 4) {
			var tileX: Int = 0
			while (tileX < MapRegion.sceneSizeX) {
				var tileY: Int = 0
				while (tileY < MapRegion.sceneSizeY) {
					ObjStackEntity.refreshObjStack(tileY, tileX, k)
					tileY++
				}
				tileX++
			}
			k++
		}
		Scene.setRoofOverride()
		SoundEffectPlayer.updatePlayers()
		LocAction.refreshPendingChanges()
		TypeLists.resetAllCaches()
		EnvironmentManager.applyFogSettings()
		@Pc(866) var packet: TcpPacket?
		if (GameShell.engineFrame != null && Connection.connection != null && ClientState.mainLogicStep === MainLogicStep.STEP_GAME_SCREEN_MAP_BUILD) {
			packet = TcpPacket.createPacket(ClientProt.aClass94_23, TcpPacket.isaac)
			packet!!.buffer!!.writeInt(1057001181)
			BufferedConnectionContext.queuePacket(packet)
		}
		if (MapRegion.areaMode == AreaMode.STATIC_AREA) {
			val minRegionX: Int = (MapRegion.mapCenterChunkX - (MapRegion.sceneSizeX shr 4)) / 8
			val maxRegionX: Int = (MapRegion.mapCenterChunkX + (MapRegion.sceneSizeX shr 4)) / 8
			@Pc(906) val minRegionY: Int = (MapRegion.mapBaseChunkY - (MapRegion.sceneSizeY shr 4)) / 8
			@Pc(914) val maxRegionY: Int = ((MapRegion.sceneSizeY shr 4) + MapRegion.mapBaseChunkY) / 8
			@Pc(918) var m: Int = minRegionX - 1
			while (m <= maxRegionX + 1) {
				@Pc(924) var regionY: Int = minRegionY - 1
				while (regionY <= maxRegionY + 1) {
					if (minRegionX > m || maxRegionX < m || regionY < minRegionY || maxRegionY < regionY) {
						Resource.MAPS!!.requestGroup("m" + m + "_" + regionY)
						Resource.MAPS!!.requestGroup("l" + m + "_" + regionY)
					}
					regionY++
				}
				m++
			}
		}
		if (ClientState.mainLogicStep === MainLogicStep.STEP_LOGIN_SCREEN_MAP_BUILD) {
			MainLogicStepTransition.setMainLogicStep(MainLogicStep.STEP_LOGIN_SCREEN)
		} else if (ClientState.mainLogicStep === MainLogicStep.STEP_LOBBY_SCREEN_MAP_BUILD) {
			MainLogicStepTransition.setMainLogicStep(MainLogicStep.STEP_LOBBY_SCREEN)
		} else {
			MainLogicStepTransition.setMainLogicStep(MainLogicStep.STEP_GAME_SCREEN)
			if (Connection.connection != null) {
				packet = TcpPacket.createPacket(ClientProt.MAP_BUILD_COMPLETE, TcpPacket.isaac)
				BufferedConnectionContext.queuePacket(packet)
			}
		}
		PendingWorldMapOpen.openPendingWorldMap()
		SoundEffectPlayer.updatePlayers()
		GameShell.resetDrawCache()
		Scene.staticSceneDirty = true
		if (MapBuildStep.rebuildProfile) {
			Console.printToConsole("Took: " + (Utils.currentTimeMillis() - MapRegion.debugRegionLoadTimeStart) + "ms")
			MapBuildStep.rebuildProfile = false
		}
	}

	@OriginalMember(owner = "client!wha", name = "x", descriptor = "Z")
	@JvmField
	var rebuildProfile: Boolean = false

	@OriginalMember(owner = "client!wl", name = "u", descriptor = "I")
	@JvmField
	var totalMapCount: Int = 1

	@OriginalMember(owner = "client!cb", name = "m", descriptor = "I")
	@JvmField
	var pendingMapFileCount: Int = 0

	@OriginalMember(owner = "client!ia", name = "x", descriptor = "I")
	@JvmField
	var maxUnloadedLocCount: Int = 1

	@OriginalMember(owner = "client!id", name = "d", descriptor = "I")
	@JvmField
	var mapLoadingStage: Int = 0
}
