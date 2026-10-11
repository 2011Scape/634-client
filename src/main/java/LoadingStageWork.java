import com.jagex.game.runetek5.config.enumtype.EnumTypeList;
import com.jagex.game.runetek5.client.window.WindowModeType;
import com.jagex.game.runetek5.Js5TextureSource;
import com.jagex.game.runetek5.LoadingScreenManager;
import com.jagex.game.runetek5.MapList;
import com.jagex.game.runetek5.config.sequence.SeqTypeList;
import com.jagex.game.runetek5.ChatLineCount;
import com.jagex.game.runetek5.LoadingStageAdvancer;
import com.jagex.game.runetek5.TextureTypeListInit;
import com.jagex.game.runetek5.PlayerInfoBitDecoder;
import com.jagex.game.runetek5.EmailStatus;
import com.jagex.game.runetek5.MidiChannelVolumeReset;
import com.jagex.game.runetek5.systeminfo.SystemInfo;
import com.jagex.game.runetek5.config.varp.VarbitTypeList;
import com.jagex.game.runetek5.core.constants.MainLogicStep;
import com.jagex.game.runetek5.core.crypto.GpuModelRetainMasks;
import com.jagex.game.runetek5.SimpleFillerDecoder;
import com.jagex.game.runetek5.preference.GamePreferences;
import com.jagex.game.runetek5.encoding.Huffman;
import com.jagex.game.runetek5.config.rsinterface.Component;
import com.jagex.game.runetek5.config.quickchat.QuickchatMessageTypeList;
import com.jagex.game.runetek5.config.bas.BasTypeList;
import com.jagex.game.runetek5.config.cursor.CursorTypeList;
import com.jagex.game.runetek5.config.defaults.DefaultsGroup;
import com.jagex.game.runetek5.config.flo.FloTypeList;
import com.jagex.game.runetek5.config.flu.FluTypeList;
import com.jagex.game.runetek5.config.hitsplat.HitSplatTypeList;
import com.jagex.game.runetek5.config.idktype.IDKTypeList;
import com.jagex.game.runetek5.config.invtype.InvTypeList;
import com.jagex.game.runetek5.config.light.LightTypeList;
import com.jagex.game.runetek5.config.loctype.LocTypeList;
import com.jagex.game.runetek5.config.mec.MECTypeList;
import com.jagex.game.runetek5.config.mapsprite.MapSpriteTypeList;
import com.jagex.game.runetek5.config.npctype.NPCTypeList;
import com.jagex.game.runetek5.config.objtype.ObjTypeList;
import com.jagex.game.runetek5.config.param.ParamTypeList;
import com.jagex.game.runetek5.config.quest.QuestTypeList;
import com.jagex.game.runetek5.config.quickchat.QuickchatCategoryTypeList;
import com.jagex.game.runetek5.config.sun.SunTypeList;
import com.jagex.game.runetek5.config.skyboxtype.SkyBoxTypeList;
import com.jagex.game.runetek5.config.sequence.spot.SpotAnimationTypeList;
import com.jagex.game.runetek5.config.struct.StructTypeList;
import com.jagex.game.runetek5.vardomain.PlayerVarDomain;
import com.jagex.game.runetek5.config.varc.VarcTypeList;
import com.jagex.game.runetek5.config.vartype.VarcstrTypeList;
import com.jagex.game.runetek5.config.vartype.player.VarPlayerTypeListClient;
import com.jagex.game.runetek5.js5.Js5Archive;
import com.jagex.game.runetek5.js5.request.RequestManager;
import com.jagex.game.runetek5.statics.WorldMapRenderer;
import jagex3.jagmisc.jagmisc;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.event.keyboard.FriendOrSelfCheck;
import com.jagex.game.runetek5.client.features.ClientFeatures;
import com.jagex.game.runetek5.graphics.loginscreen.LoginScreenComponent;
import com.jagex.game.runetek5.client.input.camera.Camera;
import com.jagex.game.runetek5.client.LoadingStage;
import com.jagex.game.runetek5.config.DefaultsFile;
import com.jagex.game.runetek5.config.Resource;
import com.jagex.game.runetek5.config.TypeLists;
import com.jagex.game.runetek5.config.defaults.EntityDefaults;
import com.jagex.game.runetek5.entity.PathingEntity;
import com.jagex.game.runetek5.graphics.sprite.StaticMedia;
import com.jagex.game.runetek5.sound.player.area.AreaSoundPlayer;
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMap;
import com.jagex.game.runetek5.client.GameShell;
import com.jagex.game.runetek5.MapRegion;
import com.jagex.game.runetek5.scene.CollisionMap;
import com.jagex.game.runetek5.graphics.font.FontManager;
import com.jagex.game.runetek5.encoding.HuffmanList;
import com.jagex.game.runetek5.graphics.font.FontCombo;
import com.jagex.game.runetek5.scene.environment.EnvironmentManager;
import com.jagex.game.runetek5.config.rsinterface.Interface;
import com.jagex.game.runetek5.graphics.model.billboard.BillboardTypeList;
import com.jagex.game.runetek5.InterfaceManager;
import com.jagex.game.runetek5.NativeLibraryOwner;
import com.jagex.game.runetek5.client.Game;
import com.jagex.game.runetek5.client.cycle.AbstractCycleManager;
import com.jagex.game.runetek5.config.defaults.EquipmentDefaultsType;
import com.jagex.game.runetek5.graphics.model.particle.ParticleSystemManager;
import com.jagex.game.runetek5.SoundManager;
import com.jagex.game.runetek5.js5.request.local.LocalRequester;
import com.jagex.game.runetek5.config.rsinterface.ComponentType;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.utils.Utils;

public final class LoadingStageWork {

	@OriginalMember(owner = "client!pea", name = "a", descriptor = "(I)I")
	public static int processLoadingStage() {
		@Pc(13) int local13;
		if (GamePreferences.uid.startupSafeMode.getValue() == 0) {
			for (int keyIndex = 0; keyIndex < client.maximumHeldKeys; keyIndex++) {
				if (client.KEY_RECORDS[keyIndex].getKeyChar() == 's' || client.KEY_RECORDS[keyIndex].getKeyChar() == 'S') {
					GamePreferences.uid.setValue(GamePreferences.uid.startupSafeMode, 1);
					LoadingStage.safeModeChosenAtStartup = true;
					break;
				}
			}
		}
		@Pc(70) int local70;
		if (LoadingStage.LOADING_STAGE == LoadingStage.aClass167_3) {
			@Pc(60) Runtime runtime = Runtime.getRuntime();
			int usedMemoryKb = (int) ((runtime.totalMemory() - runtime.freeMemory()) / 1024L);
			@Pc(75) long now = Utils.currentTimeMillis();
			if (LoadingStage.gcWaitStartTime == 0L) {
				LoadingStage.gcWaitStartTime = now;
			}
			if (usedMemoryKb > 16384 && now - LoadingStage.gcWaitStartTime < 5000L) {
				if (now - LoadingStage.lastGcTime > 1000L) {
					System.gc();
					LoadingStage.lastGcTime = now;
				}
				return 0;
			}
		}
		if (LoadingStage.aClass167_4 == LoadingStage.LOADING_STAGE) {
			if (RequestManager.requestManager == null) {
				RequestManager.requestManager = new RequestManager(Resource.remoteFileRequesterJs5, LocalRequester.aClass5_3, RequestManager.js5RsaExponent, RequestManager.js5Modulus);
			}
			if (!RequestManager.requestManager.initialize()) {
				return 0;
			}
			ActiveToolkitInit.prepareForRendering(null, 0, true);
			LoadingScreenSpriteCache.useRawLoadingSprites = !JpegDecoderProbe.supportsJpegDecoding();
			Resource.LOADING_SPRITES = Resource.createProvider(false, LoadingScreenSpriteCache.useRawLoadingSprites ? Js5Archive.LOADING_SPRITES_RAW : Js5Archive.LOADING_SPRITES, 1);
			Resource.GAME_TIPS = Resource.createProvider(false, Js5Archive.LOADING_SCREENS, 1);
			Resource.FONT_METRICS = Resource.createProvider(false, Js5Archive.FONTMETRICS, 1);
		}
		@Pc(167) boolean loaded;
		if (LoadingStage.aClass167_5 == LoadingStage.LOADING_STAGE) {
			loaded = Resource.GAME_TIPS.fullyLoaded();
			int totalPercent = Resource.remoteFileWorkersByIndex[33].getIndexPercentageComplete();
			totalPercent += Resource.remoteFileWorkersByIndex[LoadingScreenSpriteCache.useRawLoadingSprites ? Js5Archive.LOADING_SPRITES_RAW : Js5Archive.LOADING_SPRITES].getIndexPercentageComplete();
			totalPercent += Resource.remoteFileWorkersByIndex[Js5Archive.FONTMETRICS].getIndexPercentageComplete();
			totalPercent += loaded ? 100 : Resource.GAME_TIPS.getCompletePercentage();
			if (totalPercent != 400) {
				return totalPercent / 4;
			}
			AreaSoundPlayer.loadingSpritesCrc = Resource.LOADING_SPRITES.getIndexCrc();
			Resource.gameTipsCrc = Resource.GAME_TIPS.getIndexCrc();
			FontManager.loadFontIds(Resource.LOADING_SPRITES);
			@Pc(232) int tipsStartStagePreference = GamePreferences.uid.gametipStartStagePreference.getValue();
			LoadingScreenManager.aClass214_1 = new LoadingScreenManager(Game.CURRENT_GAME, ClientState.language, Resource.GAME_TIPS);
			@Pc(244) int[] screenSequence = LoadingScreenManager.aClass214_1.getSequence(tipsStartStagePreference);
			if (screenSequence.length == 0) {
				screenSequence = LoadingScreenManager.aClass214_1.getSequence(0);
			}
			@Pc(261) LoadingScreenOpFactory loadingScreenOpFactory = new LoadingScreenOpFactory(Resource.LOADING_SPRITES, Resource.FONT_METRICS);
			if (screenSequence.length > 0) {
				LoadingStage.gametipsToDisplay = new LoginScreenComponent[screenSequence.length];
				for (@Pc(273) int i = 0; i < LoadingStage.gametipsToDisplay.length; i++) {
					LoadingStage.gametipsToDisplay[i] = new ProceduralLoadingScreen(LoadingScreenManager.aClass214_1.loadScreenType(screenSequence[i]), loadingScreenOpFactory);
				}
			}
		}
		if (LoadingStage.LOADING_STAGE == LoadingStage.aClass167_6) {
			FontCombo.init(Resource.FONT_METRICS, FontManager.getDefaultFontIds(), Resource.LOADING_SPRITES);
		}
		if (LoadingStage.aClass167_7 == LoadingStage.LOADING_STAGE) {
			int readyCount = FontCombo.getFontComboReadyFileCount();
			int totalCount = FontCombo.getTotalFileCount();
			if (readyCount < totalCount) {
				return readyCount * 100 / totalCount;
			}
		}
		if (LoadingStage.LOADING_STAGE == LoadingStage.aClass167_8) {
			if (LoadingStage.gametipsToDisplay != null && LoadingStage.gametipsToDisplay.length > 0) {
				if (LoadingStage.gametipsToDisplay[0].percentage() < 100) {
					return 0;
				}
				if (LoadingStage.gametipsToDisplay.length > 1 && LoadingScreenManager.aClass214_1.hasDefault() && LoadingStage.gametipsToDisplay[1].percentage() < 100) {
					return 0;
				}
			}
			FontCombo.createFonts(ClientState.activeToolkit);
			FontManager.loadDefaultFonts(ClientState.activeToolkit);
			MainLogicStepTransition.setMainLogicStep(MainLogicStep.STEP_LOADING_1);
		}
		if (LoadingStage.LOADING_STAGE == LoadingStage.aClass167_9) {
			for (int level = 0; level < 4; level++) {
				MapRegion.aClass104Array3[level] = CollisionMap.create(MapRegion.sceneSizeY, MapRegion.sceneSizeX);
			}
		}
		if (LoadingStage.LOADING_STAGE == LoadingStage.aClass167_10) {
			Resource.SPRITES = Resource.createProvider(false, Js5Archive.SPRITES, 1);
			Resource.FRAME_SET = Resource.createProvider(false, Js5Archive.ANIMS, 1);
			Resource.FRAME_BASE = Resource.createProvider(false, Js5Archive.BASES, 1);
			Resource.CONFIG = Resource.createProvider(false, Js5Archive.CONFIG, 1);
			Resource.INTERFACE = Resource.createProvider(false, Js5Archive.INTERFACES, 1);
			Resource.SOUND_EFFECT = Resource.createProvider(false, Js5Archive.SYNTH_SOUNDS, 1);
			Resource.MAPS = Resource.createProvider(true, Js5Archive.MAPS, 1);
			Resource.MUSIC = Resource.createProvider(true, Js5Archive.MIDI_SONGS, 1);
			Resource.MODEL = Resource.createProvider(false, Js5Archive.MODELS, 1);
			Resource.TEXTURE = Resource.createProvider(false, Js5Archive.TEXTURES, 1);
			Resource.HUFFMAN = Resource.createProvider(false, Js5Archive.BINARY, 1);
			Resource.JINGLE = Resource.createProvider(false, Js5Archive.MIDI_JINGLES, 1);
			Resource.CS2 = Resource.createProvider(false, Js5Archive.CLIENTSCRIPTS, 1);
			Resource.MIDI_INSTRUMENT = Resource.createProvider(false, Js5Archive.VORBIS, 1);
			Resource.SOUND_MIDI = Resource.createProvider(false, 15, 1);
			Resource.LOC = Resource.createProvider(false, Js5Archive.CONFIG_LOC, 1);
			Resource.ENUM = Resource.createProvider(false, Js5Archive.CONFIG_ENUM, 1);
			Resource.NPC = Resource.createProvider(false, Js5Archive.CONFIG_NPC, 1);
			Resource.OBJ = Resource.createProvider(false, Js5Archive.CONFIG_OBJ, 1);
			Resource.SEQ = Resource.createProvider(false, Js5Archive.CONFIG_SEQ, 1);
			Resource.GFX = Resource.createProvider(false, Js5Archive.CONFIG_SPOT, 1);
			Resource.VARBIT = Resource.createProvider(false, Js5Archive.CONFIG_STRUCT, 1);
			Resource.WORLD_MAP = Resource.createProvider(true, Js5Archive.WORLDMAPDATA, 1);
			Resource.QC_MESSAGES = Resource.createProvider(false, Js5Archive.QUICKCHAT, 1);
			Resource.QC_MENU = Resource.createProvider(false, Js5Archive.QUICKCHAT_GLOBAL, 1);
			Resource.MATERIAL = Resource.createProvider(true, Js5Archive.MATERIALS, 1);
			Resource.PARTICLE = Resource.createProvider(false, Js5Archive.CONFIG_PARTICLE, 1);
			Resource.DEFAULTS = Resource.createProvider(true, Js5Archive.DEFAULTS, 1);
			Resource.BILLBOARD = Resource.createProvider(false, Js5Archive.CONFIG_BILLBOARD, 1);
			Resource.NATIVE_LIBRARY = Resource.createProvider(true, Js5Archive.DLLS, 1);
			Resource.SHADER = Resource.createProvider(true, Js5Archive.SHADERS, 1);
			Resource.VORBIS = Resource.createProvider(true, 36, 2);
		}
		if (LoadingStage.aClass167_11 == LoadingStage.LOADING_STAGE) {
			int loadPercent = 0;
			for (int archiveId = 0; archiveId < Js5Archive.ID_LIMIT; archiveId++) {
				if (Resource.remoteFileWorkersByIndex[archiveId] != null) {
					loadPercent += Resource.remoteFileWorkersByIndex[archiveId].getIndexPercentageComplete() * LoadingStage.JS5_ARCHIVE_LOAD_WEIGHTS[archiveId] / 100;
				}
			}
			if (loadPercent != 100) {
				if (LoadingStage.js5IndexPercentBaseline < 0) {
					LoadingStage.js5IndexPercentBaseline = loadPercent;
				}
				return (loadPercent - LoadingStage.js5IndexPercentBaseline) * 100 / (100 - LoadingStage.js5IndexPercentBaseline);
			}
			StaticMedia.loadGroupIds(Resource.SPRITES);
			FontCombo.init(Resource.FONT_METRICS, FontManager.getDefaultFontIds(), Resource.SPRITES);
		}
		if (LoadingStage.aClass167_12 == LoadingStage.LOADING_STAGE) {
			if (DefaultsFile.fileId == -1) {
				DefaultsFile.fileId = Resource.MUSIC.getGroupId("scape main");
			}
			SoundManager.initializeAudio();
			MainLogicStepTransition.setMainLogicStep(MainLogicStep.STEP_LOADING_2);
		}
		if (LoadingStage.aClass167_13 == LoadingStage.LOADING_STAGE) {
			NativeLibraryOwner.init(Resource.NATIVE_LIBRARY, GameShell.signLink);
		}
		if (LoadingStage.aClass167_14 == LoadingStage.LOADING_STAGE) {
			int dependencyPercent = StartupDependencyLoading.loadDependencies();
			if (dependencyPercent < 100) {
				return dependencyPercent;
			}
			EnvironmentManager.decodeMapDefaults(Resource.DEFAULTS.getFile(DefaultsGroup.MAP));
			EntityDefaults.decode(Resource.DEFAULTS.getFile(3));
		}
		if (LoadingStage.LOADING_STAGE == LoadingStage.aClass167_15) {
			if (EntityDefaults.profilingMesh != -1 && !Resource.MODEL.isFileReady(EntityDefaults.profilingMesh, 0)) {
				return 99;
			}
			Js5TextureSource.anInterface4_10 = new Js5TextureSource(Resource.MATERIAL, Resource.TEXTURE, Resource.SPRITES);
			TypeLists.PARAMS = new ParamTypeList(Game.CURRENT_GAME, ClientState.language, Resource.CONFIG);
			TypeLists.BAS = new BasTypeList(Game.CURRENT_GAME, ClientState.language, Resource.CONFIG);
			TypeLists.CURSOR = new CursorTypeList(Game.CURRENT_GAME, ClientState.language, Resource.CONFIG, Resource.SPRITES);
			TypeLists.ENUM = new EnumTypeList(Game.CURRENT_GAME, ClientState.language, Resource.ENUM);
			TypeLists.aClass49_3 = new FloTypeList(Game.CURRENT_GAME, ClientState.language, Resource.CONFIG);
			TypeLists.FLU = new FluTypeList(Game.CURRENT_GAME, ClientState.language, Resource.CONFIG);
			TypeLists.aClass307_2 = new HitSplatTypeList(Game.CURRENT_GAME, ClientState.language, Resource.CONFIG, Resource.SPRITES);
			TypeLists.IDK = new IDKTypeList(Game.CURRENT_GAME, ClientState.language, Resource.CONFIG, Resource.MODEL);
			TypeLists.OP_MAPELEMENT2 = new InvTypeList(Game.CURRENT_GAME, ClientState.language, Resource.CONFIG);
			TypeLists.LIGHT = new LightTypeList(Game.CURRENT_GAME, ClientState.language, Resource.CONFIG);
			TypeLists.LOC = new LocTypeList(Game.CURRENT_GAME, ClientState.language, true, Resource.LOC, Resource.MODEL);
			TypeLists.MEC = new MECTypeList(Game.CURRENT_GAME, ClientState.language, Resource.CONFIG, Resource.SPRITES);
			TypeLists.renderer = new MapSpriteTypeList(Game.CURRENT_GAME, ClientState.language, Resource.CONFIG, Resource.SPRITES);
			TypeLists.NPC = new NPCTypeList(Game.CURRENT_GAME, ClientState.language, true, Resource.NPC, Resource.MODEL);
			TypeLists.OBJ = new ObjTypeList(Game.CURRENT_GAME, ClientState.language, true, TypeLists.PARAMS, Resource.OBJ, Resource.MODEL);
			TypeLists.QUEST = new QuestTypeList(Game.CURRENT_GAME, ClientState.language, Resource.CONFIG);
			TypeLists.SEQ = new SeqTypeList(Game.CURRENT_GAME, ClientState.language, Resource.SEQ, Resource.FRAME_SET, Resource.FRAME_BASE);
			TypeLists.SKYBOX = new SkyBoxTypeList(Game.CURRENT_GAME, ClientState.language, Resource.CONFIG);
			TypeLists.SUN = new SunTypeList(Game.CURRENT_GAME, ClientState.language, Resource.CONFIG);
			TypeLists.SPOT_ANIM = new SpotAnimationTypeList(Game.CURRENT_GAME, ClientState.language, Resource.GFX, Resource.MODEL);
			TypeLists.STRUCT = new StructTypeList(Game.CURRENT_GAME, ClientState.language, Resource.CONFIG);
			TypeLists.aClass188_1 = new VarcstrTypeList(Game.CURRENT_GAME, ClientState.language, Resource.CONFIG);
			TypeLists.aClass338_1 = new VarcTypeList(Game.CURRENT_GAME, ClientState.language, Resource.CONFIG);
			TypeLists.VARBIT = new VarbitTypeList(Game.CURRENT_GAME, ClientState.language, Resource.VARBIT);
			TypeLists.VAR = new VarPlayerTypeListClient(Game.CURRENT_GAME, ClientState.language, Resource.CONFIG);
			Interface.init(Resource.FONT_METRICS, Resource.INTERFACE, Resource.SPRITES, Resource.MODEL);
			BillboardTypeList.initialize(Resource.BILLBOARD);
			TypeLists.QUICK_CHAT_CATEGORY = new QuickchatCategoryTypeList(ClientState.language, Resource.QC_MESSAGES, Resource.QC_MENU);
			TypeLists.QUICK_CHAT_MESSAGE = new QuickchatMessageTypeList(ClientState.language, Resource.QC_MESSAGES, Resource.QC_MENU, new SimpleFillerDecoder());
			EquipmentDefaultsType.initCustomizableObjSlots();
			TypeLists.LOC.setLowDetail(GamePreferences.uid.animateBackgroundOverride.getValue() == 0);
			PlayerVarDomain.playerVarProvider = new PlayerVarDomain();
			GraphicCacheReset.resetGraphicCaches();
			ParticleSystemManager.initializeIndex(Resource.PARTICLE);
			TextureTypeListInit.initialize(Resource.MODEL, Js5TextureSource.anInterface4_10);
			@Pc(995) Huffman huffman = new Huffman(Resource.HUFFMAN.getFile("huffman", ""));
			HuffmanList.setHuffman(huffman);
			try {
				jagmisc.init();
			} catch (@Pc(1002) Throwable ex) {
			}
			GameShell.cycleManager = AbstractCycleManager.create();
			SystemInfo.SYSTEM_INFO = new SystemInfo(true, GameShell.signLink);
		}
		if (LoadingStage.LOADING_STAGE == LoadingStage.aClass167_17) {
			int readyCount = StaticMedia.countReadyGroups(Resource.SPRITES) + FontCombo.getReadyFileCount(true);
			int totalCount = StaticMedia.getStaticMediaGroupCount() + FontCombo.getTotalFileCount();
			if (readyCount < totalCount) {
				return readyCount * 100 / totalCount;
			}
		}
		if (LoadingStage.aClass167_18 == LoadingStage.LOADING_STAGE) {
			WorldMap.initialize(Resource.WORLD_MAP, TypeLists.aClass49_3, TypeLists.FLU, TypeLists.LOC, TypeLists.MEC, TypeLists.renderer, PlayerVarDomain.playerVarProvider);
		}
		if (LoadingStage.aClass167_19 == LoadingStage.LOADING_STAGE) {
			// Not part of the original client, which sized the tables by the cache's varc count. Void's scripts read client variables past it.
			VarcTypeList.VARC_INT = new int[TypeLists.aClass338_1.num + (ClientFeatures.VOID ? 1000 : 0)];
			VarcTypeList.VARC_PERSISTENT = new boolean[TypeLists.aClass338_1.num + (ClientFeatures.VOID ? 1000 : 0)];
			VarcTypeList.VARC_STRING = new String[TypeLists.aClass188_1.num];
			for (int varcId = 0; varcId < TypeLists.aClass338_1.num + 1000; varcId++) {
				if (TypeLists.aClass338_1.list(varcId).persistenceType == 0) {
					VarcTypeList.VARC_PERSISTENT[varcId] = true;
					VarcTypeList.persistentVarcCount++;
				}
				VarcTypeList.VARC_INT[varcId] = -1;
			}
			VarcTypeList.loadPersistentVarcs();
			EntityDefaults.loginInterfaceId = Resource.INTERFACE.getGroupId("loginscreen");
			EntityDefaults.lobbyWindow = Resource.INTERFACE.getGroupId("lobbyscreen");
			Resource.MAPS.discardNames(false);
			Resource.MUSIC.discardNames(true);
			Resource.SPRITES.discardNames(true);
			Resource.FONT_METRICS.discardNames(true);
			Resource.HUFFMAN.discardNames(true);
			Resource.INTERFACE.discardNames(true);
			Resource.CONFIG.loadType = 2;
			TypeLists.cacheCleaningEnabled = true;
			Resource.ENUM.loadType = 2;
			Resource.LOC.loadType = 2;
			Resource.NPC.loadType = 2;
			Resource.OBJ.loadType = 2;
			Resource.SEQ.loadType = 2;
			Resource.GFX.loadType = 2;
		}
		if (LoadingStage.LOADING_STAGE == LoadingStage.aClass167_20) {
			if (!InterfaceManager.loadInterface(EntityDefaults.loginInterfaceId)) {
				return 0;
			}
			loaded = true;
			for (int componentIndex = 0; componentIndex < Interface.interfaces[EntityDefaults.loginInterfaceId].length; componentIndex++) {
				@Pc(1186) Component component = Interface.interfaces[EntityDefaults.loginInterfaceId][componentIndex];
				if (component.type == ComponentType.SPRITE && component.spriteId != -1 && !Resource.SPRITES.isFileReady(component.spriteId, 0)) {
					loaded = false;
				}
			}
			if (!loaded) {
				return 0;
			}
		}
		if (LoadingStage.aClass167_21 == LoadingStage.LOADING_STAGE) {
			LoginScreenEntry.openLoginScreen(true);
		}
		if (LoadingStage.LOADING_STAGE == LoadingStage.aClass167_22) {
			ActiveLoadingScreenRenderer.aClass8_1.stop();
			try {
				ActiveLoadingScreenRenderer.loadingScreenThread.join();
			} catch (@Pc(1232) InterruptedException ex2) {
				return 0;
			}
			LoadingScreenManager.aClass214_1 = null;
			Resource.GAME_TIPS = null;
			ActiveLoadingScreenRenderer.aClass8_1 = null;
			LoadingStage.gametipsToDisplay = null;
			ActiveLoadingScreenRenderer.loadingScreenThread = null;
			Resource.LOADING_SPRITES = null;
			LoadingScreenManager.clearLoadingScreenSpriteCache();
			client.isForcedSafemode = GamePreferences.uid.startupSafeMode.getValue() == 1;
			GamePreferences.uid.setValue(GamePreferences.uid.startupSafeMode, 1);
			if (client.isForcedSafemode) {
				GamePreferences.uid.setValue(GamePreferences.uid.toolkitDefault, 0);
			} else if (GamePreferences.uid.toolkitDefault.isDefault && SystemInfo.SYSTEM_INFO.ram < 512 && SystemInfo.SYSTEM_INFO.ram != 0) {
				GamePreferences.uid.setValue(GamePreferences.uid.toolkitDefault, 0);
			}
			GamePreferences.savePreferences();
			if (client.isForcedSafemode) {
				ToolkitSwitch.loadAssets(false, 0);
			} else {
				ToolkitSwitch.loadAssets(false, GamePreferences.uid.toolkitDefault.getValue());
			}
			WindowModeRequest.requestWindowMode(WindowModeType.fromId(GamePreferences.uid.fixedScreen.getValue()), -1, false, -1);
			FontCombo.createFonts(ClientState.activeToolkit);
			FontManager.loadDefaultFonts(ClientState.activeToolkit);
			StaticMedia.loadSprites(Resource.SPRITES, ClientState.activeToolkit);
			StaticMedia.initializeChatIcons(StaticMedia.nameIconSprites);
		}
		return LoadingStageAdvancer.finalizeStartup();
	}
}
