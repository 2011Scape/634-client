import com.jagex.game.runetek5.plugin.PluginRuntime
import com.jagex.game.runetek5.config.enumtype.EnumTypeList
import com.jagex.game.runetek5.client.window.WindowModeType
import com.jagex.game.runetek5.Js5TextureSource
import com.jagex.game.runetek5.LoadingScreenManager
import com.jagex.game.runetek5.MapList
import com.jagex.game.runetek5.config.sequence.SeqTypeList
import com.jagex.game.runetek5.ChatLineCount
import com.jagex.game.runetek5.LoadingStageAdvancer
import com.jagex.game.runetek5.TextureTypeListInit
import com.jagex.game.runetek5.PlayerInfoBitDecoder
import com.jagex.game.runetek5.EmailStatus
import com.jagex.game.runetek5.MidiChannelVolumeReset
import com.jagex.game.runetek5.systeminfo.SystemInfo
import com.jagex.game.runetek5.config.varp.VarbitTypeList
import com.jagex.game.runetek5.core.constants.MainLogicStep
import com.jagex.game.runetek5.core.crypto.GpuModelRetainMasks
import com.jagex.game.runetek5.SimpleFillerDecoder
import com.jagex.game.runetek5.preference.GamePreferences
import com.jagex.game.runetek5.encoding.Huffman
import com.jagex.game.runetek5.config.rsinterface.Component
import com.jagex.game.runetek5.config.quickchat.QuickchatMessageTypeList
import com.jagex.game.runetek5.config.bas.BasTypeList
import com.jagex.game.runetek5.config.cursor.CursorTypeList
import com.jagex.game.runetek5.config.defaults.DefaultsGroup
import com.jagex.game.runetek5.config.flo.FloTypeList
import com.jagex.game.runetek5.config.flu.FluTypeList
import com.jagex.game.runetek5.config.hitsplat.HitSplatTypeList
import com.jagex.game.runetek5.config.idktype.IDKTypeList
import com.jagex.game.runetek5.config.invtype.InvTypeList
import com.jagex.game.runetek5.config.light.LightTypeList
import com.jagex.game.runetek5.config.loctype.LocTypeList
import com.jagex.game.runetek5.config.mec.MECTypeList
import com.jagex.game.runetek5.config.mapsprite.MapSpriteTypeList
import com.jagex.game.runetek5.config.npctype.NPCTypeList
import com.jagex.game.runetek5.config.objtype.ObjTypeList
import com.jagex.game.runetek5.config.param.ParamTypeList
import com.jagex.game.runetek5.config.quest.QuestTypeList
import com.jagex.game.runetek5.config.quickchat.QuickchatCategoryTypeList
import com.jagex.game.runetek5.config.sun.SunTypeList
import com.jagex.game.runetek5.config.skyboxtype.SkyBoxTypeList
import com.jagex.game.runetek5.config.sequence.spot.SpotAnimationTypeList
import com.jagex.game.runetek5.config.struct.StructTypeList
import com.jagex.game.runetek5.vardomain.PlayerVarDomain
import com.jagex.game.runetek5.config.varc.VarcTypeList
import com.jagex.game.runetek5.config.vartype.VarcstrTypeList
import com.jagex.game.runetek5.config.vartype.player.VarPlayerTypeListClient
import com.jagex.game.runetek5.js5.Js5Archive
import com.jagex.game.runetek5.js5.request.RequestManager
import com.jagex.game.runetek5.statics.WorldMapRenderer
import jagex3.jagmisc.jagmisc
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.client.event.keyboard.FriendOrSelfCheck
import com.jagex.game.runetek5.client.features.ClientFeatures
import com.jagex.game.runetek5.graphics.loginscreen.LoginScreenComponent
import com.jagex.game.runetek5.client.input.camera.Camera
import com.jagex.game.runetek5.client.LoadingStage
import com.jagex.game.runetek5.config.DefaultsFile
import com.jagex.game.runetek5.config.Resource
import com.jagex.game.runetek5.config.TypeLists
import com.jagex.game.runetek5.config.defaults.EntityDefaults
import com.jagex.game.runetek5.entity.PathingEntity
import com.jagex.game.runetek5.graphics.sprite.StaticMedia
import com.jagex.game.runetek5.sound.player.area.AreaSoundPlayer
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMap
import com.jagex.game.runetek5.client.GameShell
import com.jagex.game.runetek5.MapRegion
import com.jagex.game.runetek5.scene.CollisionMap
import com.jagex.game.runetek5.graphics.font.FontManager
import com.jagex.game.runetek5.encoding.HuffmanList
import com.jagex.game.runetek5.graphics.font.FontCombo
import com.jagex.game.runetek5.scene.environment.EnvironmentManager
import com.jagex.game.runetek5.config.rsinterface.Interface
import com.jagex.game.runetek5.graphics.model.billboard.BillboardTypeList
import com.jagex.game.runetek5.InterfaceManager
import com.jagex.game.runetek5.NativeLibraryOwner
import com.jagex.game.runetek5.client.Game
import com.jagex.game.runetek5.client.cycle.AbstractCycleManager
import com.jagex.game.runetek5.config.defaults.EquipmentDefaultsType
import com.jagex.game.runetek5.graphics.model.particle.ParticleSystemManager
import com.jagex.game.runetek5.SoundManager
import com.jagex.game.runetek5.js5.request.local.LocalRequester
import com.jagex.game.runetek5.config.rsinterface.ComponentType
import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.utils.Utils
import kotlin.jvm.JvmStatic
import lang.InterruptedException

object LoadingStageWork {

	@OriginalMember(owner = "client!pea", name = "a", descriptor = "(I)I")
	@JvmStatic
	fun processLoadingStage(): Int {
		@Pc(13) var local13: Int
		if (GamePreferences.uid!!.startupSafeMode!!.getValue() == 0) {
			var keyIndex: Int = 0
			while (keyIndex < client.maximumHeldKeys) {
				if (client.KEY_RECORDS[keyIndex]!!.getKeyChar() == 's' || client.KEY_RECORDS[keyIndex]!!.getKeyChar() == 'S') {
					GamePreferences.uid!!.setValue(GamePreferences.uid!!.startupSafeMode, 1)
					LoadingStage.safeModeChosenAtStartup = true
					break
				}
				keyIndex++
			}
		}
		@Pc(70) var local70: Int
		if (LoadingStage.LOADING_STAGE === LoadingStage.aClass167_3) {
			@Pc(60) val runtime: lang.PlatformRuntime = lang.PlatformRuntime
			val usedMemoryKb: Int = ((runtime.totalMemory() - runtime.freeMemory()) / 1024L).toInt()
			@Pc(75) val now: Long = Utils.currentTimeMillis()
			if (LoadingStage.gcWaitStartTime == 0L) {
				LoadingStage.gcWaitStartTime = now
			}
			if (usedMemoryKb > 16384 && now - LoadingStage.gcWaitStartTime < 5000L) {
				if (now - LoadingStage.lastGcTime > 1000L) {
					lang.gc()
					LoadingStage.lastGcTime = now
				}
				return 0
			}
		}
		if (LoadingStage.aClass167_4 === LoadingStage.LOADING_STAGE) {
			if (RequestManager.requestManager == null) {
				RequestManager.requestManager = RequestManager(Resource.remoteFileRequesterJs5, LocalRequester.aClass5_3, RequestManager.js5RsaExponent, RequestManager.js5Modulus)
			}
			if (!RequestManager.requestManager!!.initialize()) {
				return 0
			}
			ActiveToolkitInit.prepareForRendering(null, 0, true)
			LoadingScreenSpriteCache.useRawLoadingSprites = !JpegDecoderProbe.supportsJpegDecoding()
			Resource.LOADING_SPRITES = Resource.createProvider(false, if (LoadingScreenSpriteCache.useRawLoadingSprites) Js5Archive.LOADING_SPRITES_RAW else Js5Archive.LOADING_SPRITES, 1)
			Resource.GAME_TIPS = Resource.createProvider(false, Js5Archive.LOADING_SCREENS, 1)
			Resource.FONT_METRICS = Resource.createProvider(false, Js5Archive.FONTMETRICS, 1)
		}
		@Pc(167) var loaded: Boolean
		if (LoadingStage.aClass167_5 === LoadingStage.LOADING_STAGE) {
			loaded = Resource.GAME_TIPS!!.fullyLoaded()
			var totalPercent: Int = Resource.remoteFileWorkersByIndex[33]!!.getIndexPercentageComplete()
			totalPercent += Resource.remoteFileWorkersByIndex[if (LoadingScreenSpriteCache.useRawLoadingSprites) Js5Archive.LOADING_SPRITES_RAW else Js5Archive.LOADING_SPRITES]!!.getIndexPercentageComplete()
			totalPercent += Resource.remoteFileWorkersByIndex[Js5Archive.FONTMETRICS]!!.getIndexPercentageComplete()
			totalPercent += if (loaded) 100 else Resource.GAME_TIPS!!.getCompletePercentage()
			if (totalPercent != 400) {
				return totalPercent / 4
			}
			AreaSoundPlayer.loadingSpritesCrc = Resource.LOADING_SPRITES!!.getIndexCrc()
			Resource.gameTipsCrc = Resource.GAME_TIPS!!.getIndexCrc()
			FontManager.loadFontIds(Resource.LOADING_SPRITES)
			@Pc(232) val tipsStartStagePreference: Int = GamePreferences.uid!!.gametipStartStagePreference!!.getValue()
			LoadingScreenManager.aClass214_1 = LoadingScreenManager(Game.CURRENT_GAME, ClientState.language, Resource.GAME_TIPS)
			@Pc(244) var screenSequence: IntArray? = LoadingScreenManager.aClass214_1!!.getSequence(tipsStartStagePreference)
			if (screenSequence!!.size == 0) {
				screenSequence = LoadingScreenManager.aClass214_1!!.getSequence(0)
			}
			@Pc(261) val loadingScreenOpFactory: LoadingScreenOpFactory = LoadingScreenOpFactory(Resource.LOADING_SPRITES, Resource.FONT_METRICS)
			if (screenSequence!!.size > 0) {
				LoadingStage.gametipsToDisplay = arrayOfNulls<LoginScreenComponent>(screenSequence.size)
				@Pc(273) var i: Int = 0
				while (i < LoadingStage.gametipsToDisplay!!.size) {
					LoadingStage.gametipsToDisplay!![i] = ProceduralLoadingScreen(LoadingScreenManager.aClass214_1!!.loadScreenType(screenSequence[i]), loadingScreenOpFactory)
					i++
				}
			}
		}
		if (LoadingStage.LOADING_STAGE === LoadingStage.aClass167_6) {
			FontCombo.init(Resource.FONT_METRICS, FontManager.getDefaultFontIds(), Resource.LOADING_SPRITES)
		}
		if (LoadingStage.aClass167_7 === LoadingStage.LOADING_STAGE) {
			val readyCount: Int = FontCombo.getFontComboReadyFileCount()
			val totalCount: Int = FontCombo.getTotalFileCount()
			if (readyCount < totalCount) {
				return readyCount * 100 / totalCount
			}
		}
		if (LoadingStage.LOADING_STAGE === LoadingStage.aClass167_8) {
			if (LoadingStage.gametipsToDisplay != null && LoadingStage.gametipsToDisplay!!.size > 0) {
				if (LoadingStage.gametipsToDisplay!![0]!!.percentage() < 100) {
					return 0
				}
				if (LoadingStage.gametipsToDisplay!!.size > 1 && LoadingScreenManager.aClass214_1!!.hasDefault() && LoadingStage.gametipsToDisplay!![1]!!.percentage() < 100) {
					return 0
				}
			}
			FontCombo.createFonts(ClientState.activeToolkit)
			FontManager.loadDefaultFonts(ClientState.activeToolkit)
			MainLogicStepTransition.setMainLogicStep(MainLogicStep.STEP_LOADING_1)
		}
		if (LoadingStage.LOADING_STAGE === LoadingStage.aClass167_9) {
			for (level in 0 until 4) {
				MapRegion.aClass104Array3[level] = CollisionMap.create(MapRegion.sceneSizeY, MapRegion.sceneSizeX)
			}
		}
		if (LoadingStage.LOADING_STAGE === LoadingStage.aClass167_10) {
			Resource.SPRITES = Resource.createProvider(false, Js5Archive.SPRITES, 1)
			Resource.FRAME_SET = Resource.createProvider(false, Js5Archive.ANIMS, 1)
			Resource.FRAME_BASE = Resource.createProvider(false, Js5Archive.BASES, 1)
			Resource.CONFIG = Resource.createProvider(false, Js5Archive.CONFIG, 1)
			Resource.INTERFACE = Resource.createProvider(false, Js5Archive.INTERFACES, 1)
			Resource.SOUND_EFFECT = Resource.createProvider(false, Js5Archive.SYNTH_SOUNDS, 1)
			Resource.MAPS = Resource.createProvider(true, Js5Archive.MAPS, 1)
			Resource.MUSIC = Resource.createProvider(true, Js5Archive.MIDI_SONGS, 1)
			Resource.MODEL = Resource.createProvider(false, Js5Archive.MODELS, 1)
			Resource.TEXTURE = Resource.createProvider(false, Js5Archive.TEXTURES, 1)
			Resource.HUFFMAN = Resource.createProvider(false, Js5Archive.BINARY, 1)
			Resource.JINGLE = Resource.createProvider(false, Js5Archive.MIDI_JINGLES, 1)
			Resource.CS2 = Resource.createProvider(false, Js5Archive.CLIENTSCRIPTS, 1)
			Resource.MIDI_INSTRUMENT = Resource.createProvider(false, Js5Archive.VORBIS, 1)
			Resource.SOUND_MIDI = Resource.createProvider(false, 15, 1)
			Resource.LOC = Resource.createProvider(false, Js5Archive.CONFIG_LOC, 1)
			Resource.ENUM = Resource.createProvider(false, Js5Archive.CONFIG_ENUM, 1)
			Resource.NPC = Resource.createProvider(false, Js5Archive.CONFIG_NPC, 1)
			Resource.OBJ = Resource.createProvider(false, Js5Archive.CONFIG_OBJ, 1)
			Resource.SEQ = Resource.createProvider(false, Js5Archive.CONFIG_SEQ, 1)
			Resource.GFX = Resource.createProvider(false, Js5Archive.CONFIG_SPOT, 1)
			Resource.VARBIT = Resource.createProvider(false, Js5Archive.CONFIG_STRUCT, 1)
			Resource.WORLD_MAP = Resource.createProvider(true, Js5Archive.WORLDMAPDATA, 1)
			Resource.QC_MESSAGES = Resource.createProvider(false, Js5Archive.QUICKCHAT, 1)
			Resource.QC_MENU = Resource.createProvider(false, Js5Archive.QUICKCHAT_GLOBAL, 1)
			Resource.MATERIAL = Resource.createProvider(true, Js5Archive.MATERIALS, 1)
			Resource.PARTICLE = Resource.createProvider(false, Js5Archive.CONFIG_PARTICLE, 1)
			Resource.DEFAULTS = Resource.createProvider(true, Js5Archive.DEFAULTS, 1)
			Resource.BILLBOARD = Resource.createProvider(false, Js5Archive.CONFIG_BILLBOARD, 1)
			Resource.NATIVE_LIBRARY = Resource.createProvider(true, Js5Archive.DLLS, 1)
			Resource.SHADER = Resource.createProvider(true, Js5Archive.SHADERS, 1)
			Resource.VORBIS = Resource.createProvider(true, 36, 2)
		}
		if (LoadingStage.aClass167_11 === LoadingStage.LOADING_STAGE) {
			var loadPercent: Int = 0
			for (archiveId in 0 until Js5Archive.ID_LIMIT) {
				if (Resource.remoteFileWorkersByIndex[archiveId] != null) {
					loadPercent += Resource.remoteFileWorkersByIndex[archiveId]!!.getIndexPercentageComplete() * LoadingStage.JS5_ARCHIVE_LOAD_WEIGHTS[archiveId] / 100
				}
			}
			if (loadPercent != 100) {
				if (LoadingStage.js5IndexPercentBaseline < 0) {
					LoadingStage.js5IndexPercentBaseline = loadPercent
				}
				return (loadPercent - LoadingStage.js5IndexPercentBaseline) * 100 / (100 - LoadingStage.js5IndexPercentBaseline)
			}
			StaticMedia.loadGroupIds(Resource.SPRITES)
			FontCombo.init(Resource.FONT_METRICS, FontManager.getDefaultFontIds(), Resource.SPRITES)
		}
		if (LoadingStage.aClass167_12 === LoadingStage.LOADING_STAGE) {
			if (DefaultsFile.fileId == -1) {
				DefaultsFile.fileId = Resource.MUSIC!!.getGroupId("scape main")
			}
			SoundManager.initializeAudio()
			MainLogicStepTransition.setMainLogicStep(MainLogicStep.STEP_LOADING_2)
		}
		if (LoadingStage.aClass167_13 === LoadingStage.LOADING_STAGE) {
			NativeLibraryOwner.init(Resource.NATIVE_LIBRARY, GameShell.signLink)
		}
		if (LoadingStage.aClass167_14 === LoadingStage.LOADING_STAGE) {
			val dependencyPercent: Int = StartupDependencyLoading.loadDependencies()
			if (dependencyPercent < 100) {
				return dependencyPercent
			}
			EnvironmentManager.decodeMapDefaults(Resource.DEFAULTS!!.getFile(DefaultsGroup.MAP))
			EntityDefaults.decode(Resource.DEFAULTS!!.getFile(3))
		}
		if (LoadingStage.LOADING_STAGE === LoadingStage.aClass167_15) {
			if (EntityDefaults.profilingMesh != -1 && !Resource.MODEL!!.isFileReady(EntityDefaults.profilingMesh, 0)) {
				return 99
			}
			Js5TextureSource.anInterface4_10 = Js5TextureSource(Resource.MATERIAL, Resource.TEXTURE, Resource.SPRITES)
			TypeLists.PARAMS = ParamTypeList(Game.CURRENT_GAME, ClientState.language, Resource.CONFIG)
			TypeLists.BAS = BasTypeList(Game.CURRENT_GAME, ClientState.language, Resource.CONFIG)
			TypeLists.CURSOR = CursorTypeList(Game.CURRENT_GAME, ClientState.language, Resource.CONFIG, Resource.SPRITES)
			TypeLists.ENUM = EnumTypeList(Game.CURRENT_GAME, ClientState.language, Resource.ENUM)
			TypeLists.aClass49_3 = FloTypeList(Game.CURRENT_GAME, ClientState.language, Resource.CONFIG)
			TypeLists.FLU = FluTypeList(Game.CURRENT_GAME, ClientState.language, Resource.CONFIG)
			TypeLists.aClass307_2 = HitSplatTypeList(Game.CURRENT_GAME, ClientState.language, Resource.CONFIG!!, Resource.SPRITES)
			TypeLists.IDK = IDKTypeList(Game.CURRENT_GAME, ClientState.language, Resource.CONFIG, Resource.MODEL)
			TypeLists.OP_MAPELEMENT2 = InvTypeList(Game.CURRENT_GAME, ClientState.language, Resource.CONFIG)
			TypeLists.LIGHT = LightTypeList(Game.CURRENT_GAME, ClientState.language, Resource.CONFIG)
			TypeLists.LOC = LocTypeList(Game.CURRENT_GAME, ClientState.language, true, Resource.LOC, Resource.MODEL)
			TypeLists.MEC = MECTypeList(Game.CURRENT_GAME, ClientState.language, Resource.CONFIG!!, Resource.SPRITES)
			TypeLists.renderer = MapSpriteTypeList(Game.CURRENT_GAME, ClientState.language, Resource.CONFIG, Resource.SPRITES)
			TypeLists.NPC = NPCTypeList(Game.CURRENT_GAME, ClientState.language, true, Resource.NPC, Resource.MODEL)
			TypeLists.OBJ = ObjTypeList(Game.CURRENT_GAME, ClientState.language, true, TypeLists.PARAMS, Resource.OBJ, Resource.MODEL)
			TypeLists.QUEST = QuestTypeList(Game.CURRENT_GAME, ClientState.language, Resource.CONFIG)
			TypeLists.SEQ = SeqTypeList(Game.CURRENT_GAME, ClientState.language, Resource.SEQ, Resource.FRAME_SET, Resource.FRAME_BASE)
			TypeLists.SKYBOX = SkyBoxTypeList(Game.CURRENT_GAME, ClientState.language, Resource.CONFIG)
			TypeLists.SUN = SunTypeList(Game.CURRENT_GAME, ClientState.language, Resource.CONFIG!!)
			TypeLists.SPOT_ANIM = SpotAnimationTypeList(Game.CURRENT_GAME, ClientState.language, Resource.GFX, Resource.MODEL)
			TypeLists.STRUCT = StructTypeList(Game.CURRENT_GAME, ClientState.language, Resource.CONFIG)
			TypeLists.aClass188_1 = VarcstrTypeList(Game.CURRENT_GAME, ClientState.language, Resource.CONFIG)
			TypeLists.aClass338_1 = VarcTypeList(Game.CURRENT_GAME, ClientState.language, Resource.CONFIG)
			TypeLists.VARBIT = VarbitTypeList(Game.CURRENT_GAME, ClientState.language, Resource.VARBIT)
			TypeLists.VAR = VarPlayerTypeListClient(Game.CURRENT_GAME, ClientState.language, Resource.CONFIG)
			Interface.init(Resource.FONT_METRICS, Resource.INTERFACE, Resource.SPRITES, Resource.MODEL)
			BillboardTypeList.initialize(Resource.BILLBOARD)
			TypeLists.QUICK_CHAT_CATEGORY = QuickchatCategoryTypeList(ClientState.language, Resource.QC_MESSAGES, Resource.QC_MENU)
			TypeLists.QUICK_CHAT_MESSAGE = QuickchatMessageTypeList(ClientState.language, Resource.QC_MESSAGES, Resource.QC_MENU, SimpleFillerDecoder())
			EquipmentDefaultsType.initCustomizableObjSlots()
			TypeLists.LOC!!.setLowDetail(GamePreferences.uid!!.animateBackgroundOverride!!.getValue() == 0)
			PlayerVarDomain.playerVarProvider = PlayerVarDomain()
			PluginRuntime.start() // Not part of the original client: the plugins start once the types, interfaces and vars exist.
			GraphicCacheReset.resetGraphicCaches()
			ParticleSystemManager.initializeIndex(Resource.PARTICLE)
			TextureTypeListInit.initialize(Resource.MODEL, Js5TextureSource.anInterface4_10)
			@Pc(995) val huffman: Huffman = Huffman(Resource.HUFFMAN!!.getFile("huffman", ""))
			HuffmanList.setHuffman(huffman)
			try {
				jagmisc.init()
			} catch (@Pc(1002) ex: Throwable) {
			}
			GameShell.cycleManager = AbstractCycleManager.create()
			SystemInfo.SYSTEM_INFO = SystemInfo(true, GameShell.signLink)
		}
		if (LoadingStage.LOADING_STAGE === LoadingStage.aClass167_17) {
			val readyCount: Int = StaticMedia.countReadyGroups(Resource.SPRITES) + FontCombo.getReadyFileCount(true)
			val totalCount: Int = StaticMedia.getStaticMediaGroupCount() + FontCombo.getTotalFileCount()
			if (readyCount < totalCount) {
				return readyCount * 100 / totalCount
			}
		}
		if (LoadingStage.aClass167_18 === LoadingStage.LOADING_STAGE) {
			WorldMap.initialize(Resource.WORLD_MAP, TypeLists.aClass49_3, TypeLists.FLU, TypeLists.LOC, TypeLists.MEC, TypeLists.renderer, PlayerVarDomain.playerVarProvider)
		}
		if (LoadingStage.aClass167_19 === LoadingStage.LOADING_STAGE) {
			// Not part of the original client, which sized the tables by the cache's varc count. Void's scripts read client variables past it.
			VarcTypeList.VARC_INT = IntArray(TypeLists.aClass338_1!!.num + (if (ClientFeatures.VOID) 1000 else 0))
			VarcTypeList.VARC_PERSISTENT = BooleanArray(TypeLists.aClass338_1!!.num + (if (ClientFeatures.VOID) 1000 else 0))
			VarcTypeList.VARC_STRING = arrayOfNulls<String>(TypeLists.aClass188_1!!.num)
			var varcId: Int = 0
			while (varcId < TypeLists.aClass338_1!!.num + 1000) {
				if (TypeLists.aClass338_1!!.list(varcId)!!.persistenceType == 0) {
					VarcTypeList.VARC_PERSISTENT!![varcId] = true
					VarcTypeList.persistentVarcCount++
				}
				VarcTypeList.VARC_INT!![varcId] = -1
				varcId++
			}
			VarcTypeList.loadPersistentVarcs()
			EntityDefaults.loginInterfaceId = Resource.INTERFACE!!.getGroupId("loginscreen")
			EntityDefaults.lobbyWindow = Resource.INTERFACE!!.getGroupId("lobbyscreen")
			Resource.MAPS!!.discardNames(false)
			Resource.MUSIC!!.discardNames(true)
			Resource.SPRITES!!.discardNames(true)
			Resource.FONT_METRICS!!.discardNames(true)
			Resource.HUFFMAN!!.discardNames(true)
			Resource.INTERFACE!!.discardNames(true)
			Resource.CONFIG!!.loadType = 2
			TypeLists.cacheCleaningEnabled = true
			Resource.ENUM!!.loadType = 2
			Resource.LOC!!.loadType = 2
			Resource.NPC!!.loadType = 2
			Resource.OBJ!!.loadType = 2
			Resource.SEQ!!.loadType = 2
			Resource.GFX!!.loadType = 2
		}
		if (LoadingStage.LOADING_STAGE === LoadingStage.aClass167_20) {
			if (!InterfaceManager.loadInterface(EntityDefaults.loginInterfaceId)) {
				return 0
			}
			loaded = true
			var componentIndex: Int = 0
			while (componentIndex < Interface.interfaces!![EntityDefaults.loginInterfaceId]!!.size) {
				@Pc(1186) val component: Component? = Interface.interfaces!![EntityDefaults.loginInterfaceId]!![componentIndex]
				if (component!!.type == ComponentType.SPRITE && component.spriteId != -1 && !Resource.SPRITES!!.isFileReady(component.spriteId, 0)) {
					loaded = false
				}
				componentIndex++
			}
			if (!loaded) {
				return 0
			}
		}
		if (LoadingStage.aClass167_21 === LoadingStage.LOADING_STAGE) {
			LoginScreenEntry.openLoginScreen(true)
		}
		if (LoadingStage.LOADING_STAGE === LoadingStage.aClass167_22) {
			ActiveLoadingScreenRenderer.aClass8_1!!.stop()
			try {
				ActiveLoadingScreenRenderer.loadingScreenThread!!.join()
			} catch (@Pc(1232) ex2: InterruptedException) {
				return 0
			}
			LoadingScreenManager.aClass214_1 = null
			Resource.GAME_TIPS = null
			ActiveLoadingScreenRenderer.aClass8_1 = null
			LoadingStage.gametipsToDisplay = null
			ActiveLoadingScreenRenderer.loadingScreenThread = null
			Resource.LOADING_SPRITES = null
			LoadingScreenManager.clearLoadingScreenSpriteCache()
			client.isForcedSafemode = GamePreferences.uid!!.startupSafeMode!!.getValue() == 1
			GamePreferences.uid!!.setValue(GamePreferences.uid!!.startupSafeMode, 1)
			if (client.isForcedSafemode) {
				GamePreferences.uid!!.setValue(GamePreferences.uid!!.toolkitDefault, 0)
			} else if (GamePreferences.uid!!.toolkitDefault!!.isDefault && SystemInfo.SYSTEM_INFO!!.ram < 512 && SystemInfo.SYSTEM_INFO!!.ram != 0) {
				GamePreferences.uid!!.setValue(GamePreferences.uid!!.toolkitDefault, 0)
			}
			GamePreferences.savePreferences()
			if (client.isForcedSafemode) {
				ToolkitSwitch.loadAssets(false, 0)
			} else {
				// Not part of the original client: client.toolkit picks the toolkit to start on.
				ToolkitSwitch.loadAssets(false, if (ClientFeatures.TOOLKIT >= 0) ClientFeatures.TOOLKIT else GamePreferences.uid!!.toolkitDefault!!.getValue())
			}
			WindowModeRequest.requestWindowMode(WindowModeType.fromId(GamePreferences.uid!!.fixedScreen!!.getValue()), -1, false, -1)
			FontCombo.createFonts(ClientState.activeToolkit)
			FontManager.loadDefaultFonts(ClientState.activeToolkit)
			StaticMedia.loadSprites(Resource.SPRITES, ClientState.activeToolkit)
			StaticMedia.initializeChatIcons(StaticMedia.nameIconSprites)
		}
		return LoadingStageAdvancer.finalizeStartup()
	}
}
