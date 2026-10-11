import com.jagex.game.runetek5.network.packet.TcpPacket;
import com.jagex.game.runetek5.config.staticelements.StaticElements;
import com.jagex.game.runetek5.MapRegion;
import com.jagex.game.runetek5.rendering.MessageBox;
import com.jagex.game.runetek5.Minimap;
import com.jagex.game.runetek5.W2DebugMode;
import com.jagex.game.runetek5.core.constants.AreaMode;
import com.jagex.game.runetek5.core.constants.MainLogicStep;
import com.jagex.game.runetek5.UnloadedLocCount;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.features.ClientFeatures;
import com.jagex.game.runetek5.config.Resource;
import com.jagex.game.runetek5.graphics.font.FontManager;
import com.jagex.game.runetek5.scene.MapLoader;
import com.jagex.game.runetek5.rendering.Renderers;
import com.jagex.game.runetek5.ClientPacket;
import com.jagex.game.runetek5.client.GameShell;
import com.jagex.game.runetek5.network.BufferedConnectionContext;
import com.jagex.game.runetek5.scene.environment.EnvironmentManager;
import com.jagex.game.runetek5.tools.LocalizedText;
import com.jagex.game.runetek5.scene.Scene;
import com.jagex.game.runetek5.client.input.console.Console;
import com.jagex.game.runetek5.sound.player.area.SoundEffectPlayer;
import com.jagex.game.runetek5.network.packet.ClientProt;
import com.jagex.game.runetek5.NPCList;
import com.jagex.game.runetek5.ObjStackEntity;
import com.jagex.game.runetek5.scene.LocAction;
import com.jagex.game.runetek5.scene.SettingsBits;
import com.jagex.game.runetek5.Skybox;
import com.jagex.game.runetek5.Terrain;
import com.jagex.game.runetek5.client.login.LoginManager;
import com.jagex.game.runetek5.network.Connection;
import com.jagex.game.runetek5.preference.GamePreferences;
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMap;
import com.jagex.game.runetek5.sound.player.area.AmbientSound;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.config.TypeLists;
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate;
import com.jagex.game.runetek5.utils.Utils;

public final class MapBuildStep {

	@OriginalMember(owner = "client!vg", name = "a", descriptor = "(I)V")
	public static void loadRegion() {
		BufferedConnectionContext.sendKeepAlive(false);
		MapBuildStep.pendingMapFileCount = 0;
		@Pc(12) boolean loaded = true;
		for (@Pc(14) int i = 0; i < MapRegion.surfaceMapBuffer.length; i++) {
			if (MapRegion.surfaceMapArchives[i] != -1 && MapRegion.surfaceMapBuffer[i] == null) {
				MapRegion.surfaceMapBuffer[i] = Resource.MAPS.getFile(MapRegion.surfaceMapArchives[i], 0);
				if (MapRegion.surfaceMapBuffer[i] == null) {
					loaded = false;
					MapBuildStep.pendingMapFileCount++;
				}
			}
			if (MapRegion.surfaceLocArchives[i] != -1 && MapRegion.locMapBuffers[i] == null) {
				// Not part of the original client: a group whose key is missing or wrong fails to decrypt;
				// it is read as empty, so the region loads without it instead of the client crashing.
				try {
					MapRegion.locMapBuffers[i] = Resource.MAPS.getFile(MapRegion.surfaceLocArchives[i], 0, MapRegion.xteas[i]);
				} catch (RuntimeException ex) {
					MapRegion.locMapBuffers[i] = new byte[4];
				}
				if (MapRegion.locMapBuffers[i] == null) {
					MapBuildStep.pendingMapFileCount++;
					loaded = false;
				}
			}
			if (MapRegion.underwaterMapArchives[i] != -1 && MapRegion.underwaterMapBuffer[i] == null) {
				MapRegion.underwaterMapBuffer[i] = Resource.MAPS.getFile(MapRegion.underwaterMapArchives[i], 0);
				if (MapRegion.underwaterMapBuffer[i] == null) {
					MapBuildStep.pendingMapFileCount++;
					loaded = false;
				}
			}
			if (MapRegion.underwaterLocArchives[i] != -1 && MapRegion.underwaterLocBuffer[i] == null) {
				MapRegion.underwaterLocBuffer[i] = Resource.MAPS.getFile(MapRegion.underwaterLocArchives[i], 0);
				if (MapRegion.underwaterLocBuffer[i] == null) {
					loaded = false;
					MapBuildStep.pendingMapFileCount++;
				}
			}
			if (MapRegion.npcSpawnArchives != null && MapRegion.npcSpawnBuffers[i] == null && MapRegion.npcSpawnArchives[i] != -1) {
				// Not part of the original client: a group whose key is missing or wrong fails to decrypt;
				// it is read as empty, so the region loads without it instead of the client crashing.
				try {
					MapRegion.npcSpawnBuffers[i] = Resource.MAPS.getFile(MapRegion.npcSpawnArchives[i], 0, MapRegion.xteas[i]);
				} catch (RuntimeException ex) {
					MapRegion.npcSpawnBuffers[i] = new byte[4];
				}
				if (MapRegion.npcSpawnBuffers[i] == null) {
					loaded = false;
					MapBuildStep.pendingMapFileCount++;
				}
			}
		}
		if (StaticElements.aClass333_3 == null) {
			if (WorldMap.aClass2_Sub6_Sub4_3 == null || !Resource.WORLD_MAP.groupExists(WorldMap.aClass2_Sub6_Sub4_3.filenamePrefix + "_staticelements")) {
				StaticElements.aClass333_3 = new StaticElements(0);
			} else if (Resource.WORLD_MAP.isGroupReady(WorldMap.aClass2_Sub6_Sub4_3.filenamePrefix + "_staticelements")) {
				StaticElements.aClass333_3 = StaticElements.load(Resource.WORLD_MAP, WorldMap.aClass2_Sub6_Sub4_3.filenamePrefix + "_staticelements", LoginManager.memberWorld);
			} else {
				loaded = false;
				MapBuildStep.pendingMapFileCount++;
			}
		}
		if (!loaded) {
			MapBuildStep.mapLoadingStage = 1;
			return;
		}
		loaded = true;
		UnloadedLocCount.unloadedLocCount = 0;
		@Pc(268) int local268;
		@Pc(278) int local278;
		for (@Pc(250) int j = 0; j < MapRegion.surfaceMapBuffer.length; j++) {
			@Pc(256) byte[] mapData = MapRegion.locMapBuffers[j];
			if (mapData != null) {
				int baseX = (MapRegion.regionIds[j] >> 8) * 64 - MapRegion.sceneBaseX;
				int baseY = (MapRegion.regionIds[j] & 0xFF) * 64 - MapRegion.sceneBaseY;
				if (MapRegion.areaMode != AreaMode.STATIC_AREA) {
					baseY = 10;
					baseX = 10;
				}
				loaded &= MapRegion.areMapLocTypesLoaded(MapRegion.sceneSizeX, baseX, MapRegion.sceneSizeY, baseY, mapData);
			}
			mapData = MapRegion.underwaterLocBuffer[j];
			if (mapData != null) {
				int baseX = (MapRegion.regionIds[j] >> 8) * 64 - MapRegion.sceneBaseX;
				int baseY = (MapRegion.regionIds[j] & 0xFF) * 64 - MapRegion.sceneBaseY;
				if (MapRegion.areaMode != AreaMode.STATIC_AREA) {
					baseY = 10;
					baseX = 10;
				}
				loaded &= MapRegion.areMapLocTypesLoaded(MapRegion.sceneSizeX, baseX, MapRegion.sceneSizeY, baseY, mapData);
			}
		}
		if (!loaded) {
			MapBuildStep.mapLoadingStage = 2;
			return;
		}
		if (MapBuildStep.mapLoadingStage != 0) {
			MessageBox.draw(true, FontManager.aClass68_4, FontManager.messageboxFont, LocalizedText.aClass351_14.get(ClientState.language) + "<br>(100%)", ClientState.activeToolkit);
		}
		SoundEffectPlayer.updatePlayers();
		TypeLists.resetAllCaches();
		Static683.method656();
		@Pc(382) boolean hasUnderwaterMap = false;
		if (ClientState.activeToolkit.stillwaterEffectLoaded() && GamePreferences.uid.water.getValue() == 2) {
			for (int regionIndex = 0; regionIndex < MapRegion.surfaceMapBuffer.length; regionIndex++) {
				if (MapRegion.underwaterLocBuffer[regionIndex] != null || MapRegion.underwaterMapBuffer[regionIndex] != null) {
					hasUnderwaterMap = true;
					break;
				}
			}
		}
		int visibleTiles;
		if (GamePreferences.uid.fog.getValue() == 1) {
			visibleTiles = MapRegion.FOG_VISIBLE_TILES[MapRegion.currentMapSize];
		} else {
			visibleTiles = MapRegion.DRAW_DISTANCES_NO_FOG[MapRegion.currentMapSize];
		}
		if (ClientState.activeToolkit.extendBuildArea()) {
			visibleTiles++;
		}
		if (ClientFeatures.RENDER_DISTANCE != 1.0D) {
			// Not part of the original client: the scene is drawn further out.
			visibleTiles = (int) (ClientFeatures.RENDER_DISTANCE * (double) visibleTiles);
		}
		SceneInitializer.initializeScene(ClientState.activeToolkit, Scene.renderCores, MapRegion.sceneSizeX, MapRegion.sceneSizeY, visibleTiles, hasUnderwaterMap, ClientState.activeToolkit.getMaxLights() > 0);
		W2DebugMode.setW2DebugMode(W2DebugMode.w2DebugMode);
		if (W2DebugMode.w2DebugMode == 0) {
			W2DebugMode.setFont(null);
		} else {
			W2DebugMode.setFont(Renderers.FONT_RENDERER);
		}
		for (int level = 0; level < 4; level++) {
			MapRegion.aClass104Array3[level].reset();
		}
		SettingsBits.clearTileFunctionMasks();
		AmbientSound.unlinkSounds(false);
		EnvironmentManager.resetChunkEnvironments();
		Skybox.configNpcJs5 = null;
		Scene.gameObjectsUploaded = false;
		SoundEffectPlayer.updatePlayers();
		System.gc();
		BufferedConnectionContext.sendKeepAlive(true);
		MapRegion.enableLowDetailShadows();
		MapLoader.hdShadows = GamePreferences.uid.hardShadows.getValue();
		Terrain.highLightDetail = GameShell.maxMemory >= 96;
		Terrain.hdWater = GamePreferences.uid.water.getValue() == 2;
		Terrain.hdLight = GamePreferences.uid.lightDetail.getValue() == 1;
		Minimap.visibleFromLevel = GamePreferences.uid.animateBackgroundOverride.getValue() == 1 ? -1 : MapRegion.mapBuildLevel;
		Terrain.blendFloor = GamePreferences.uid.groundBlending.getValue() == 1;
		Terrain.enableTextures = GamePreferences.uid.textures.getValue() == 1;
		MapRegion.aClass322_Sub1_1 = new MapRegion(4, MapRegion.sceneSizeX, MapRegion.sceneSizeY, false);
		if (MapRegion.areaMode == AreaMode.STATIC_AREA) {
			MapRegion.decodeMapTilesClient(MapRegion.aClass322_Sub1_1, MapRegion.surfaceMapBuffer);
		} else {
			MapRegion.decodeMapTilesServer(MapRegion.aClass322_Sub1_1, MapRegion.surfaceMapBuffer);
		}
		EnvironmentManager.applyEnvironmentSunPosition(MapRegion.sceneSizeX >> 4, MapRegion.sceneSizeY >> 4);
		EnvironmentManager.updateAtmosphere();
		if (hasUnderwaterMap) {
			Scene.setUnderwater(true);
			MapRegion.aClass322_Sub1_2 = new MapRegion(1, MapRegion.sceneSizeX, MapRegion.sceneSizeY, true);
			if (MapRegion.areaMode == AreaMode.STATIC_AREA) {
				MapRegion.decodeMapTilesClient(MapRegion.aClass322_Sub1_2, MapRegion.underwaterMapBuffer);
				BufferedConnectionContext.sendKeepAlive(true);
			} else {
				MapRegion.decodeMapTilesServer(MapRegion.aClass322_Sub1_2, MapRegion.underwaterMapBuffer);
				BufferedConnectionContext.sendKeepAlive(true);
			}
			MapRegion.aClass322_Sub1_2.addHeightOffsets(MapRegion.aClass322_Sub1_1.tileHeights[0]);
			MapRegion.aClass322_Sub1_2.initializeFloor(ClientState.activeToolkit, null, null);
			Scene.setUnderwater(false);
		}
		MapRegion.aClass322_Sub1_1.initializeFloor(ClientState.activeToolkit, hasUnderwaterMap ? MapRegion.aClass322_Sub1_2.tileHeights : null, MapRegion.aClass104Array3);
		if (MapRegion.areaMode == AreaMode.STATIC_AREA) {
			BufferedConnectionContext.sendKeepAlive(true);
			MapRegion.decodeLocMapsClient(MapRegion.locMapBuffers, MapRegion.aClass322_Sub1_1);
			if (MapRegion.npcSpawnBuffers != null) {
				NPCList.decodeNpcSpawns();
			}
		} else {
			BufferedConnectionContext.sendKeepAlive(true);
			MapRegion.decodeLocMapsServer(MapRegion.locMapBuffers, MapRegion.aClass322_Sub1_1);
		}
		TypeLists.resetAllCaches();
		if (GameShell.maxMemory < 96) {
			Scene.uploadAllGameObjects();
		}
		BufferedConnectionContext.sendKeepAlive(true);
		MapRegion.aClass322_Sub1_1.calculateUnderlayPalette(null, hasUnderwaterMap ? Scene.aClass65Array3[0] : null, ClientState.activeToolkit);
		MapRegion.aClass322_Sub1_1.calculateVisibility(false, ClientState.activeToolkit);
		BufferedConnectionContext.sendKeepAlive(true);
		if (hasUnderwaterMap) {
			Scene.setUnderwater(true);
			BufferedConnectionContext.sendKeepAlive(true);
			if (MapRegion.areaMode == AreaMode.STATIC_AREA) {
				MapRegion.decodeLocMapsClient(MapRegion.underwaterLocBuffer, MapRegion.aClass322_Sub1_2);
			} else {
				MapRegion.decodeLocMapsServer(MapRegion.underwaterLocBuffer, MapRegion.aClass322_Sub1_2);
			}
			TypeLists.resetAllCaches();
			BufferedConnectionContext.sendKeepAlive(true);
			MapRegion.aClass322_Sub1_2.calculateUnderlayPalette(Scene.aClass65Array2[0], null, ClientState.activeToolkit);
			MapRegion.aClass322_Sub1_2.calculateVisibility(true, ClientState.activeToolkit);
			BufferedConnectionContext.sendKeepAlive(true);
			Scene.setUnderwater(false);
		}
		Terrain.releaseUnderlayData();
		@Pc(771) int lowestLevel = MapRegion.aClass322_Sub1_1.minLevel;
		if (PlayerUpdate.currentLevel < lowestLevel) {
			lowestLevel = PlayerUpdate.currentLevel;
		}
		if (PlayerUpdate.currentLevel - 1 > lowestLevel) {
			lowestLevel = PlayerUpdate.currentLevel - 1;
		}
		if (GamePreferences.uid.animateBackgroundOverride.getValue() == 0) {
			Scene.fillLevelTiles(lowestLevel);
		} else {
			Scene.fillLevelTiles(0);
		}
		@Pc(813) int local813;
		@Pc(817) int local817;
		for (@Pc(809) int k = 0; k < 4; k++) {
			for (int tileX = 0; tileX < MapRegion.sceneSizeX; tileX++) {
				for (int tileY = 0; tileY < MapRegion.sceneSizeY; tileY++) {
					ObjStackEntity.refreshObjStack(tileY, tileX, k);
				}
			}
		}
		Scene.setRoofOverride();
		SoundEffectPlayer.updatePlayers();
		LocAction.refreshPendingChanges();
		TypeLists.resetAllCaches();
		EnvironmentManager.applyFogSettings();
		@Pc(866) TcpPacket packet;
		if (GameShell.engineFrame != null && Connection.connection != null && ClientState.mainLogicStep == MainLogicStep.STEP_GAME_SCREEN_MAP_BUILD) {
			packet = TcpPacket.createPacket(ClientProt.aClass94_23, TcpPacket.isaac);
			packet.buffer.writeInt(1057001181);
			BufferedConnectionContext.queuePacket(packet);
		}
		if (MapRegion.areaMode == AreaMode.STATIC_AREA) {
			int minRegionX = (MapRegion.mapCenterChunkX - (MapRegion.sceneSizeX >> 4)) / 8;
			int maxRegionX = (MapRegion.mapCenterChunkX + (MapRegion.sceneSizeX >> 4)) / 8;
			@Pc(906) int minRegionY = (MapRegion.mapBaseChunkY - (MapRegion.sceneSizeY >> 4)) / 8;
			@Pc(914) int maxRegionY = ((MapRegion.sceneSizeY >> 4) + MapRegion.mapBaseChunkY) / 8;
			for (@Pc(918) int m = minRegionX - 1; m <= maxRegionX + 1; m++) {
				for (@Pc(924) int regionY = minRegionY - 1; regionY <= maxRegionY + 1; regionY++) {
					if (minRegionX > m || maxRegionX < m || regionY < minRegionY || maxRegionY < regionY) {
						Resource.MAPS.requestGroup("m" + m + "_" + regionY);
						Resource.MAPS.requestGroup("l" + m + "_" + regionY);
					}
				}
			}
		}
		if (ClientState.mainLogicStep == MainLogicStep.STEP_LOGIN_SCREEN_MAP_BUILD) {
			MainLogicStepTransition.setMainLogicStep(MainLogicStep.STEP_LOGIN_SCREEN);
		} else if (ClientState.mainLogicStep == MainLogicStep.STEP_LOBBY_SCREEN_MAP_BUILD) {
			MainLogicStepTransition.setMainLogicStep(MainLogicStep.STEP_LOBBY_SCREEN);
		} else {
			MainLogicStepTransition.setMainLogicStep(MainLogicStep.STEP_GAME_SCREEN);
			if (Connection.connection != null) {
				packet = TcpPacket.createPacket(ClientProt.MAP_BUILD_COMPLETE, TcpPacket.isaac);
				BufferedConnectionContext.queuePacket(packet);
			}
		}
		PendingWorldMapOpen.openPendingWorldMap();
		SoundEffectPlayer.updatePlayers();
		GameShell.resetDrawCache();
		Scene.staticSceneDirty = true;
		if (MapBuildStep.rebuildProfile) {
			Console.printToConsole("Took: " + (Utils.currentTimeMillis() - MapRegion.debugRegionLoadTimeStart) + "ms");
			MapBuildStep.rebuildProfile = false;
		}
	}
	@OriginalMember(owner = "client!wha", name = "x", descriptor = "Z")
	public static boolean rebuildProfile = false;

	@OriginalMember(owner = "client!wl", name = "u", descriptor = "I")
	public static int totalMapCount = 1;

	@OriginalMember(owner = "client!cb", name = "m", descriptor = "I")
	public static int pendingMapFileCount = 0;

	@OriginalMember(owner = "client!ia", name = "x", descriptor = "I")
	public static int maxUnloadedLocCount = 1;

	@OriginalMember(owner = "client!id", name = "d", descriptor = "I")
	public static int mapLoadingStage = 0;
}
