package com.jagex.game.runetek5.client.features

import client
import InterfaceOptionInteract
import com.jagex.game.runetek5.SceneDrawer
import com.jagex.game.runetek5.core.constants.TileFlag
import com.jagex.game.runetek5.scene.SettingsBits
import com.jagex.game.runetek5.SceneVisibleTiles
import memoryReport
import com.jagex.game.runetek5.LocType
import turnElapsedMs
import com.jagex.game.runetek5.MapRegion
import com.jagex.game.runetek5.statics.StatBaseLevels
import com.jagex.game.runetek5.config.rsinterface.MediaType
import com.jagex.game.runetek5.entity.NPCEntityNode
import com.jagex.game.runetek5.NPCList
import com.jagex.game.runetek5.vardomain.PlayerVarDomain
import com.jagex.game.runetek5.config.TypeLists
import com.jagex.game.runetek5.config.rsinterface.itemcontainer.ItemContainer
import com.jagex.game.runetek5.graphics.model.Model
import com.jagex.game.runetek5.core.constants.MiniMenuAction
import com.jagex.game.runetek5.client.menu.MiniMenuEntry
import com.jagex.game.runetek5.client.menu.MiniMenu
import com.jagex.game.runetek5.client.menu.MiniMenuBuilder
import com.jagex.game.runetek5.graphics.MiniMenuReset
import com.jagex.game.runetek5.config.iftype.SubInterfaceType
import com.jagex.game.runetek5.InterfaceManager
import com.jagex.game.runetek5.config.rsinterface.ComponentType
import com.jagex.game.runetek5.config.rsinterface.Component
import com.jagex.game.runetek5.config.rsinterface.Interface
import com.jagex.game.runetek5.config.rsinterface.SubInterface
import com.jagex.game.runetek5.client.window.WindowModeType
import com.jagex.game.runetek5.graphics.Toolkit
import com.jagex.game.runetek5.core.constants.MainLogicStep
import com.jagex.game.runetek5.entity.PathingEntitySpotAnimation
import com.jagex.game.runetek5.entity.PlayerEntity
import awt.getInsets
import awt.image.BufferedImage
import io.File
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate
import com.jagex.game.runetek5.client.window.WindowMode
import com.jagex.game.runetek5.OverlayManager
import com.jagex.game.runetek5.client.login.LoginManager
import com.jagex.game.runetek5.client.login.LoginStep
import com.jagex.game.runetek5.preference.GamePreferences
import com.jagex.game.runetek5.client.ClientState
import kotlin.jvm.JvmField
import com.jagex.game.runetek5.client.input.mouse.MouseManager
import com.jagex.game.runetek5.client.input.mouse.MouseRecord
import com.jagex.game.runetek5.plugin.ItemPrices
import com.jagex.game.runetek5.plugin.ClientApiImpl
import com.jagex.game.runetek5.plugin.api.LocKind
import com.jagex.game.runetek5.plugin.PluginConfigImpl
import com.jagex.game.runetek5.plugin.PluginRegistration
import com.jagex.game.runetek5.plugin.api.MenuTarget
import com.jagex.game.runetek5.entity.NPCEntity
import com.jagex.game.runetek5.plugin.PluginRuntime
import com.jagex.game.runetek5.client.input.mouse.CursorManager
import com.jagex.game.runetek5.plugin.PluginCursor
import com.jagex.game.runetek5.client.input.keyboard.CanvasKeyRecorder
import com.jagex.game.runetek5.client.input.keyboard.KeyRecorder
import com.jagex.game.runetek5.ChatLineWriter
import com.jagex.game.runetek5.interaction.chat.ChatBox
import com.jagex.game.runetek5.plugin.api.ChatType
import com.jagex.game.runetek5.cache.node.Node
import com.jagex.game.runetek5.client.event.mouse.MouseLog
import com.jagex.game.runetek5.plugin.api.OverlayPosition
import com.jagex.game.runetek5.plugin.Tooltips
import com.jagex.game.runetek5.plugin.InfoBoxes
import com.jagex.game.runetek5.plugin.OverlayLayout
import com.jagex.game.runetek5.plugin.api.QuestState
import com.jagex.game.runetek5.plugin.api.Skill
import kotlin.jvm.JvmStatic
import lang.jClass
import lang.javaSplit
import lang.javaTrim
import ClientCommandProcessor
import ClientLogout
import com.jagex.game.runetek5.client.features.RenderStats
import com.jagex.game.runetek5.graphics.ToolkitType
import com.jagex.game.runetek5.Minimap
import com.jagex.game.runetek5.scene.culling.Culling
import com.jagex.game.runetek5.client.input.camera.Camera
import MiniMenuActionExecutor
import MiniMenuInputTick
import ModalInterfaceClose
import SubInterfaceOpener
import ToolkitSwitch

/**
 * Not part of the original client. With client.test=smoke the client logs itself in and exits
 * with a verdict, so a change can be checked without a person at the keyboard:
 *
 * - on the login screen it logs in as client.test.user (default smoketest) with
 *   client.test.password (default smoketest1), once;
 * - once in game, with a local player, it waits for client.test.frames frames (default 250) to be
 *   drawn, then reads the last one through the toolkit, saves it to client.test.shot, and checks
 *   it is not blank and, when client.test.reference names a directory holding a reference for this
 *   toolkit (smoke-toolkit<id>.png), that it is close to it. The frame is cut into 32 by 24 blocks.
 *   The mean difference of the blocks' average colours, out of 255, may be at most
 *   client.test.tolerance (default 12); it catches a missing scene or a moved camera. The mean
 *   difference of the blocks' contrast (the spread of brightness inside each) may be at most
 *   client.test.detailTolerance (default 6); it catches lost textures and detail. Two runs of an
 *   unchanged client differ by about 7 and 3;
 *   then it prints "SMOKE PASS ..." and exits 0;
 * - plugins read and write no settings under the test, unless client.test.pluginHome names a folder
 *   whose `.634-client/plugin-config/<id>.properties` seed them (PluginPaths), as a run that
 *   watches a plugin's own behaviour needs (the idle notifier's, with ::anim 879 as the command);
 * - with client.test.command (for example "gfx level_up", which needs an admin account), it sends
 *   that command once the first frame passes, waits for a gfx command's graphic to arrive (up to
 *   150 frames; the server answers on its next tick), then client.test.commandFrames more frames
 *   (default 60), and checks the frame again, against smoke-toolkit<id>-<first word of the command>.png. For
 *   a gfx command, one of the local player's spot animation slots must also have taken the graphic
 *   and advanced its frames;
 * - if that has not happened within client.test.timeout seconds (default 180), or the client
 *   throws, it prints "SMOKE FAIL ..." with the step it reached and exits 1.
 *
 * - with client.test.toolkit (a ToolkitType id: 0 Java, 1 Java OpenGL, 2 native software,
 *   5 native OpenGL), it first switches to that toolkit once in game, through the client's own
 *   switch, and fails if the client does not end up on it; every check then runs on that toolkit,
 *   against its own references. At the end it switches back to the toolkit it started on, a saved
 *   preference, and waits for that to settle before exiting.
 *
 * It runs when the client is started with client.test=smoke against a local Void server.
 */
object SmokeTest {

	@JvmField
	val ENABLED: Boolean = "smoke" == lang.getProperty("client.test")

	private val USER: String = lang.getProperty("client.test.user", "smoketest")

	private val PASSWORD: String = lang.getProperty("client.test.password", "smoketest1")

	private val FRAMES: Int = lang.getInteger("client.test.frames", 250)

	private val TIMEOUT: Long = lang.getInteger("client.test.timeout", 180).toLong() * 1000L

	private val SHOT: String = lang.getProperty("client.test.shot", "build/smoke/frame.png")

	private val REFERENCE: String = lang.getProperty("client.test.reference", "")

	private val TOLERANCE: Int = lang.getInteger("client.test.tolerance", 12)

	private val DETAIL_TOLERANCE: Int = lang.getInteger("client.test.detailTolerance", 6)

	private const val BLOCKS_X: Int = 32

	private const val BLOCKS_Y: Int = 24

	private val COMMAND: String = lang.getProperty("client.test.command", "")

	private val COMMAND_FRAMES: Int = lang.getInteger("client.test.commandFrames", 60)

	/** client.test.sceneCommand: a :: command sent once, at the first scene reached (e.g. "occlude", "w2debug 2"). */
	private val SCENE_COMMAND: String = lang.getProperty("client.test.sceneCommand", "")
	/** client.test.npcDump=id[,id...]: at the first scene, each NPC type's meshes: faces, translucent faces (alpha, height, flat or not). */
	private val NPC_DUMP: String = lang.getProperty("client.test.npcDump", "")
	private var sceneCommandSent: Boolean = false

	private val CHAT: String = lang.getProperty("client.test.chat", "smoke test chat")

	private val CHAT_FRAMES: Int = lang.getInteger("client.test.chatFrames", 150)

	private var firstImage: String? = null

	private var spotModelBuilt: Boolean = false

	private var chatFrame: Int = -1

	private var chatSeen: String? = null

	private var chatColour: Int = -1

	private var chatTicksFirst: Int = -1

	private var chatTicksLast: Int = -1

	private var chatQueued: Boolean = false

	private var lastText: String? = null

	private val WINDOW_MODES: Boolean = "false" != lang.getProperty("client.test.windowModes")

	private const val WINDOW_FRAMES: Int = 30

	private var startMode: WindowModeType? = null

	private var windowFrame: Int = -1

	private var windowStep: Int = 0

	private val SUB_INTERFACES: Boolean = "false" != lang.getProperty("client.test.subInterfaces")

	private const val SUB_INTERFACE_FRAMES: Int = 20

	private var subInterfaceStep: Int = 0

	private var subInterfaceFrame: Int = -1

	private var modalParent: Long = -1L

	private var overlaysAtStart: Int = 0

	private val XP_DROPS: Boolean = "false" != lang.getProperty("client.test.xpDrops")

	/** client.test.respawn=false skips the npc indicators' respawn timer check. */
	private val RESPAWN: Boolean = "false" != lang.getProperty("client.test.respawn")

	/** The Tutorial Island guide, who stands beside the test account. */
	private const val GUIDE_NPC: Int = 945

	private const val RESPAWN_FRAMES: Int = 10

	private var respawnStep: Int = 0

	private var respawnFrame: Int = -1

	private var respawnGuide: NPCEntity? = null

	private var respawnTimerWasOn: Boolean = false

	private const val XP_WAIT: Int = 300

	private var xpStep: Int = 0

	private var xpRightFrame: Int = -1

	private var xpFrame: Int = -1

	private var xpWasEnabled: Boolean = false

	private var xpDrawn: Int = 0

	private var xpDrawnFrame: Int = -1

	private val MARKERS: Boolean = "false" != lang.getProperty("client.test.markers")

	/** client.test.lootTracker: a made-up kill of the guide, its loot in the Loot Tracker, and the loot window. */
	private val LOOT_TRACKER: Boolean = "true" == lang.getProperty("client.test.lootTracker")

	private var lootStep: Int = 0

	private var lootScrollSummary: String = ""

	private var lootFrame: Int = -1

	/** client.test.xpTracker: experience fed through the stat hook, a canvas box for it, and the XP tracker's window. */
	private val XP_TRACKER: Boolean = "true" == lang.getProperty("client.test.xpTracker")

	/** client.test.opponentInfo: a made-up fight with the guide in Opponent Information's panel, its Attack entry marked, a player's comparison and the opponent forgotten. */
	private val OPPONENT_INFO: Boolean = "true" == lang.getProperty("client.test.opponentInfo")

	private var opponentStep: Int = 0

	/** client.test.bankOrganizer: the bank opened with the admin ::bank, Bank Organizer's two-colour box over its coins. */
	private val BANK_ORGANIZER: Boolean = "true" == lang.getProperty("client.test.bankOrganizer")

	private var bankStep: Int = 0

	/** client.test.statusBars: Status Bars beside the side panel, with counters and a shark's restore preview. */
	private val STATUS_BARS: Boolean = "true" == lang.getProperty("client.test.statusBars")

	private var statusStep: Int = 0

	private var statusFrame: Int = -1

	private var statusStartMode: WindowModeType? = null

	/** client.test.timers: Timers and Buffs from the server's lines (through the plugin runtime), its poison varp and a cast animation. */
	private val TIMERS: Boolean = "true" == lang.getProperty("client.test.timers")

	private var timersStep: Int = 0

	private var timersFrame: Int = -1

	/** client.test.tileIndicators: Tile Indicators' destination (the minimap flag two tiles east) and true tile drawn. */
	private val TILE_INDICATORS: Boolean = "true" == lang.getProperty("client.test.tileIndicators")

	private var tileStep: Int = 0

	private var tileFrame: Int = -1

	/** client.test.xpGlobes: Attack and Woodcutting gain experience through the stat hook; their globes, then one hovered for its tooltip. */
	private val XP_GLOBES: Boolean = "true" == lang.getProperty("client.test.xpGlobes")

	private var globesStep: Int = 0

	private var globesFrame: Int = -1

	/**
	 * client.test.barrows: Barrows Brothers on the Void server: teleported to the hills (the brothers'
	 * letters on the minimap), into Dharok's crypt (the server's overlay, the panel in its place, the prayer
	 * drain timer), the puzzle door's window opened here with a puzzle's models (the answer outlined), and home.
	 */
	private val BARROWS: Boolean = "true" == lang.getProperty("client.test.barrows")

	private var barrowsStep: Int = 0

	private var barrowsFrame: Int = -1

	/** client.test.attackStyles: Attack Styles warning for Attack: the unarmed style named and its combat tab button hidden. */
	private val ATTACK_STYLES: Boolean = "true" == lang.getProperty("client.test.attackStyles")

	private var attackStylesFrame: Int = -1

	/** client.test.cannon: Cannon with a cannon taken to stand beside the player, loaded with 20: its count and double-hit tiles drawn. */
	private val CANNON: Boolean = "true" == lang.getProperty("client.test.cannon")

	private var cannonFrame: Int = -1

	/** client.test.antiDrag: Anti Drag with Shift held (by the test): the backpack's slots take the delay, and their own once let go. */
	private val ANTI_DRAG: Boolean = "true" == lang.getProperty("client.test.antiDrag")

	private var antiDragStep: Int = 0

	private var antiDragFrame: Int = -1

	/** client.test.emojis: Emojis on a public chat line and the player's overhead line, both written here as the packets would. */
	private val EMOJIS: Boolean = "true" == lang.getProperty("client.test.emojis")

	private var emojisFrame: Int = -1

	/**
	 * client.test.keyRemapping: Key Remapping with keys given as the keyboard would: the chat locked, D held turns
	 * into Right (the camera turns) and its character is dropped, Enter unlocks, typed letters go into the line, Escape drops it and locks.
	 */
	private val KEY_REMAPPING: Boolean = "true" == lang.getProperty("client.test.keyRemapping")

	private var keyRemapStep: Int = 0

	private var keyRemapFrame: Int = -1

	private var keyRemapYaw: Int = 0

	/** client.test.customCursor: Custom Cursor's first picture, then the worn weapon's icon, put on the canvas, and the game's back. */
	private val CUSTOM_CURSOR: Boolean = "true" == lang.getProperty("client.test.customCursor")

	private var cursorStep: Int = 0

	private var cursorFrame: Int = -1

	private var cursorIconPixels: Int = 0

	/** client.test.itemStats: Item Stats' tooltips for a shark, a super strength and a rune scimitar, each shown in turn. */
	private val ITEM_STATS: Boolean = "true" == lang.getProperty("client.test.itemStats")

	private var itemStatsStep: Int = 0

	private var itemStatsFrame: Int = -1

	/**
	 * client.test.woodcutting: Woodcutting with a tree beside the player felled as the server would tell it (a
	 * normal tree's LocChanged to its stump) and three logs said in the chat: the pie and the session's panel.
	 */
	private val WOODCUTTING: Boolean = "true" == lang.getProperty("client.test.woodcutting")

	private var woodcuttingFrame: Int = -1

	/** client.test.mining: Mining with a copper rock beside the player mined out as the server would tell it, and an ore said in the chat. */
	private val MINING: Boolean = "true" == lang.getProperty("client.test.mining")

	private var miningFrame: Int = -1

	/** client.test.reportButton: Report Button's login timer, then the date, on 751:11, and its own words back once off. */
	private val REPORT_BUTTON: Boolean = "true" == lang.getProperty("client.test.reportButton")

	private var reportStep: Int = 0

	private var reportFrame: Int = -1

	private var reportTexts: String = ""

	/** client.test.playerIndicators: Player Indicators on the player's own name, tile and minimap name, and the friends chat rank icons made from the cache's sprites (API 23). */
	private val PLAYER_INDICATORS: Boolean = "true" == lang.getProperty("client.test.playerIndicators")

	private var playerIndicatorsFrame: Int = -1

	/** client.test.specialCounter: a Bandos godsword special's hit counted by Special Attack Counter, its infobox and the drop over the player's head. */
	private val SPECIAL_COUNTER: Boolean = "true" == lang.getProperty("client.test.specialCounter")

	private var specialCounterFrame: Int = -1

	/** client.test.regenMeter: Regeneration Meter's ring round the life points orb, shown at full life points. */
	private val REGEN_METER: Boolean = "true" == lang.getProperty("client.test.regenMeter")

	private var regenMeterFrame: Int = -1

	private var regenMeterShown: Int = -1

	/** client.test.runEnergy: Run Energy's run time left on the run orb (750:5), then the energy back once off, and its tooltip's text. */
	private val RUN_ENERGY: Boolean = "true" == lang.getProperty("client.test.runEnergy")

	private var runEnergyStep: Int = 0

	private var runEnergyFrame: Int = -1

	private var runEnergyText: String = ""

	/** client.test.poison: the player poisoned by Void's ::poison 14, Poison's infobox and the orb's green heart (API 24's sprite override). */
	private val POISON: Boolean = "true" == lang.getProperty("client.test.poison")

	private var poisonFrame: Int = -1

	private var poisonSeen: Int = -1

	/** client.test.fishing: Fishing at Catherby's shore (a teleport there and back): the spots' tiles, icons and names, and the session panel after a catch said in the chat. */
	private val FISHING: Boolean = "true" == lang.getProperty("client.test.fishing")

	private var fishingStep: Int = 0

	private var fishingFrame: Int = -1

	/** client.test.agility: Agility at the Gnome Stronghold course (a teleport there and back): the obstacles' click boxes, and the lap panel after a lap bonus. */
	private val AGILITY: Boolean = "true" == lang.getProperty("client.test.agility")

	private var agilityStep: Int = 0

	private var agilityFrame: Int = -1

	/** client.test.slayer: a cows task set with Void's ::var (slayer_target and slayer_count, put back after), Slayer's infobox and the cows of Lumbridge highlighted (a teleport there and back). */
	private val SLAYER: Boolean = "true" == lang.getProperty("client.test.slayer")

	private var slayerStep: Int = 0

	private var slayerFrame: Int = -1

	private var slayerFailure: String? = null

	/** client.test.itemCharges: an amulet of glory(4) put in an empty backpack slot on this client only (as UPDATE_INV_PARTIAL would), its charges drawn by Item Charges, then the slot emptied again. */
	private val ITEM_CHARGES: Boolean = "true" == lang.getProperty("client.test.itemCharges")

	private var itemChargesStep: Int = 0

	private var itemChargesFrame: Int = -1

	private var itemChargesSlot: Int = -1

	/** client.test.itemIdentification: a clean guam put in an empty backpack slot on this client only, labelled "G" by Item Identification with herbs on, then the slot emptied again. */
	private val ITEM_IDENTIFICATION: Boolean = "true" == lang.getProperty("client.test.itemIdentification")

	private var itemIdentificationStep: Int = 0

	private var itemIdentificationFrame: Int = -1

	private var itemIdentificationSlot: Int = -1

	/** client.test.chatNotifications: a public chat line naming the player, written through the client's own chat path, highlighted by Chat Notifications. */
	private val CHAT_NOTIFICATIONS: Boolean = "true" == lang.getProperty("client.test.chatNotifications")

	private var chatNotificationsFrame: Int = -1

	/** client.test.chatFilter: through the client's own chat path, a public line with a filtered word not stored (API 26's consume), and a repeated game line collapsed into one with its count (removeChatLine). */
	private val CHAT_FILTER: Boolean = "true" == lang.getProperty("client.test.chatFilter")

	private var chatFilterFrame: Int = -1

	/** client.test.chatHistory: Chat History putting a kept public line back before the next chat line (as after a login; API 27's addChatLine), the clipboard round trip, and Clear history on the Public tab. */
	private val CHAT_HISTORY: Boolean = "true" == lang.getProperty("client.test.chatHistory")

	private var chatHistoryFrame: Int = -1

	/** client.test.combatLevel: Combat Level's precise level on the combat tab (884:1), then in the Wilderness (a teleport there and back) the attack range by its skull (381). */
	private val COMBAT_LEVEL: Boolean = "true" == lang.getProperty("client.test.combatLevel")

	private var combatLevelStep: Int = 0

	private var combatLevelFrame: Int = -1

	private var combatLevelText: String = ""

	private var combatLevelFailure: String? = null

	private var combatLevelShown: Int = -1

	/** client.test.virtualLevels: the Stats tab open and Attack given 14.4 million experience on this client only, Virtual Levels writing its level 100, then the experience given back. */
	private val VIRTUAL_LEVELS: Boolean = "true" == lang.getProperty("client.test.virtualLevels")

	private var virtualLevelsStep: Int = 0

	private var virtualLevelsFrame: Int = -1

	private var virtualLevelsXp: Int = 0

	private var virtualLevelsShown: Int = -1

	/** client.test.metronome: the server's ticks (API 28's ServerTick) counted over three seconds, and Metronome playing on them. */
	private val METRONOME: Boolean = "true" == lang.getProperty("client.test.metronome")

	private var metronomeStart: Long = -1L

	private var metronomeTicks: Int = 0

	/** client.test.deathIndicator: Void's death and gravestone lines (written through the client's chat path, nothing really happening), the hint arrow (API 29) and infobox, then the world map opened and the grave marked on it. */
	private val DEATH_INDICATOR: Boolean = "true" == lang.getProperty("client.test.deathIndicator")

	private var deathIndicatorStep: Int = 0

	private var deathIndicatorFrame: Int = -1

	private var deathIndicatorFailure: String? = null

	/** client.test.npcAggression: the NPC Aggression Timer always active with its area lines: the timer infobox and the 21 by 21 square drawn round the player. */
	private val NPC_AGGRESSION: Boolean = "true" == lang.getProperty("client.test.npcAggression")

	private var npcAggressionFrame: Int = -1

	/** client.test.hunter: a box trap set beside the player as the server would tell it (LOC_CHANGED through the runtime), Hunter's pie drawn on it. */
	private val HUNTER: Boolean = "true" == lang.getProperty("client.test.hunter")

	private var hunterFrame: Int = -1

	/** client.test.implings: a ::tele into Puro-Puro with every impling kind highlighted and the spawn tiles shown, both drawn, then back home. */
	private val IMPLINGS: Boolean = "true" == lang.getProperty("client.test.implings")

	private var implingsStep: Int = 0

	private var implingsFrame: Int = -1

	private var implingsFailure: String? = null

	/** client.test.cooking: Void's cooked and burnt lines (written through the client's chat path, nothing cooked), Cooking's panel counting them. */
	private val COOKING: Boolean = "true" == lang.getProperty("client.test.cooking")

	private var cookingFrame: Int = -1

	private var bankFrame: Int = -1

	/** client.test.boosts: Attack boosted and Defence drained by hand, in Boosts Information's infoboxes, panel and compact list, with its timers and its notification. */
	private val BOOSTS: Boolean = "true" == lang.getProperty("client.test.boosts")

	private var boostsStep: Int = 0

	private var boostsFrame: Int = -1

	private var boostsSummary: String = ""

	/** client.test.overlayDrag: the plugins' panels managed with the drag key held: outlines, a drag, a snap, a resize, a detach and merge, the shift menu and a reset. */
	private val OVERLAY_DRAG: Boolean = "true" == lang.getProperty("client.test.overlayDrag")

	private var dragStep: Int = 0

	private var dragFrame: Int = -1

	private var dragSummary: String = ""

	private var dragStartX: Int = 0

	private var dragStartY: Int = 0

	private var opponentFrame: Int = -1

	private var xpTrackerStep: Int = 0

	private var xpScrollSummary: String = ""

	private var xpTrackerFrame: Int = -1

	/** client.test.objectMarkers: an object beside the player marked in all four styles. */
	private val OBJECT_MARKERS: Boolean = "true" == lang.getProperty("client.test.objectMarkers")

	private var objectStep: Int = 0

	private var objectFrame: Int = -1

	private var objectMarked: IntArray? = null

	/** client.test.groundItems: Ground Items' labels, lootbeam and hotkey boxes on items made up beside the player. */
	private val GROUND_ITEMS: Boolean = "true" == lang.getProperty("client.test.groundItems")

	private var groundItemsStep: Int = 0

	private var groundItemsFrame: Int = -1

	private var groundItemsSummary: String = ""

	private const val MARKER_FRAMES: Int = 40

	/** The backpack's item container, and an amulet of glory(4) for Item Charges. */
	private const val BACKPACK: Int = 93

	private const val GLORY_4: Int = 1712

	/** A clean guam, for Item Identification. */
	private const val CLEAN_GUAM: Int = 249

	private const val COINS: Int = 995

	private const val COOKS_ASSISTANT: Int = 6

	private const val SHARK: Int = 385

	private const val FIXED_TAB_AREA_X: Int = 547

	private const val RESIZABLE_SIDE_BOX: Int = 75
	private const val FIXED_TAB_AREA_WIDTH: Int = 190

	private const val BANK_WAIT_FRAMES: Int = 500

	/** How long a teleport (and the crypt overlay the server opens with it) may take. */
	private const val BARROWS_WAIT_FRAMES: Int = 500

	private var markerStep: Int = 0

	private var markerFrame: Int = -1

	private var markersWereEnabled: Boolean = false

	private var markersBefore: Int = 0

	private val MEDIA: Boolean = "false" != lang.getProperty("client.test.media")

	private const val MEDIA_FRAMES: Int = 300

	private var mediaStep: Int = 0

	private var mediaFrame: Int = -1

	private var mediaBuilt: String = ""

	private val MENU_WALK: Boolean = "false" != lang.getProperty("client.test.menuWalk")

	/** client.test.menuCheck: run a finished scene menu and a component menu through the runtime, for the plugins that edit menus. */
	private val MENU_CHECK: Boolean = "true" == lang.getProperty("client.test.menuCheck")

	/** client.test.npcMenu=<verb>: the plugins' entries for the nearest npc are built as its right-click menu builds them; fail unless [verb] is among them. */
	private val NPC_MENU: String = lang.getProperty("client.test.npcMenu", "")

	private var npcMenuChecked: Boolean = false

	/** client.test.pluginManager: open the plugin manager's window, switch a built-in, open a settings page, step a number, close; frames in build/smoke/plugins-*.png. */
	private val PLUGIN_MANAGER: Boolean = "true" == lang.getProperty("client.test.pluginManager")

	private var pluginManagerStep: Int = 0

	private var pluginManagerFrame: Int = -1

	private var pluginManagerTipLines: Int = -1

	/** The plugin manager's filtered list, the gear lit, is on screen for its shot. */
	private var pluginManagerSearched: Boolean = false

	private const val MENU_WALK_FRAMES: Int = 400

	private var menuWalkFrame: Int = -1

	private var menuWalkStartX: Int = -1

	private var menuWalkStartY: Int = -1

	private var menuWalkStep: Int = 0

	private var menuWalkHomeX: Int = -1

	private var menuWalkHomeY: Int = -1

	private var menuWalkAwayX: Int = -1

	private var menuWalkAwayY: Int = -1

	private val TOOLKIT: Int = lang.getInteger("client.test.toolkit", -1)

	/**
	 * client.test.bench=N: after the settle frames, time N frames and print one SMOKE BENCH line
	 * (frame-time average, median and 95th percentile, fps, game ticks per frame), then stop, instead
	 * of running the checks. client.test.benchMode=resizable or fixed switches the window mode first;
	 * resizable also lifts the maximum screen size preference, so the view fills the window.
	 */
	private val BENCH: Int = lang.getInteger("client.test.bench", 0)

	private val BENCH_MODE: String = lang.getProperty("client.test.benchMode", "")

	/**
	 * client.test.scenePosition=x,y[,level[,yaw,pitch,zoom]]: the bench runs there (teleported, needs
	 * an admin account), with the camera held as for a scene, then the player is sent home.
	 */
	private val BENCH_POSITION: String = lang.getProperty("client.test.scenePosition", "")

	private var benchArrived: Boolean = false

	private var benchDrawCalls: Long = 0L

	private val benchCategories: LongArray = LongArray(3)

	private var benchTriangles: Long = 0L

	private var benchHomeFrame: Int = -1

	private var benchCamera: IntArray? = null

	/** client.test.benchWindow=WxH: the game area size to bench at (the window is grown to hold it), e.g. 1920x1080. */
	private val BENCH_WINDOW: String = lang.getProperty("client.test.benchWindow", "")

	private const val BENCH_SETTLE: Int = 60

	private var benchModeFrame: Int = -1

	private var benchTimes: DoubleArray? = null

	private var benchCount: Int = -1

	private var benchLast: Long = 0L

	private var benchTicks: Int = 0

	private var benchWork: DoubleArray? = null

	/* The maximum screen size preference before benchMode=resizable lifted it, or -1. */
	private var benchMaxScreenSize: Int = -1

	/**
	 * client.test.scenes: the screenshot harness, run instead of the checks. "default" takes
	 * DEFAULT_SCENES; otherwise a list of name:x,y,level,yaw,pitch,zoom[,limit[,outfit]] separated by ';'
	 * (outfit: slot:item pairs separated by '|', the player dressed in them on arrival). For each
	 * scene the player is teleported there (::tele, which needs an admin account), the map is built,
	 * the camera is held at the given yaw (0-16383), pitch (1024-3072) and zoom offset, and after
	 * client.test.sceneFrames frames (default 150) the frame is saved to build/smoke/scenes/ and
	 * compared pixel by pixel with scene-<name>-toolkit<id>.png in client.test.reference.
	 * client.test.sceneReferenceToolkit compares against another toolkit's references (634HD against
	 * toolkit 1). A pixel differs when a channel is off by more than client.test.scenePixelTolerance
	 * (default 24); a scene fails when more than client.test.sceneMaxDifferent pixels per thousand
	 * (default 40) differ - or the scene's own limit, its seventh value - and a diff image (differing
	 * pixels red over a dimmed frame) is saved. Two runs of toolkit 1 differ by 3 to 29: moving NPCs
	 * and the player's idle animation; at the cow field, where the cows wander, by 33 to 57 (limit 65).
	 * After the list, the first scene is captured again after a map reload (teleport away and back)
	 * and after a relog. The player is sent home (SCENE_HOME) at the end.
	 */
	private val SCENES: String = lang.getProperty("client.test.scenes", "")

	/**
	 * client.test.soak=M: a memory soak run instead of the checks, for M minutes. The player visits
	 * DEFAULT_SCENES' positions in turn (::tele, an admin account), walks a few tiles there while the
	 * camera turns, for client.test.soakFrames frames (default 600); after every visit a line
	 * "SMOKE SOAK" gives the time and memoryReport() (heap, direct buffers, resident size, free video
	 * memory). Every client.test.soakToggle visits (default 3, 0 never) the client switches to
	 * toolkit 1 and back to client.test.toolkit, as a player toggling 634HD would. It ends at home.
	 */
	private val SOAK_MINUTES: Int = lang.getInteger("client.test.soak", 0)

	private val SOAK_FRAMES: Int = lang.getInteger("client.test.soakFrames", 600)

	private val SOAK_TOGGLE: Int = lang.getInteger("client.test.soakToggle", 3)

	private var soakPositions: Array<IntArray>? = null

	private var soakIndex: Int = 0

	private var soakStep: Int = 0

	private var soakFrame: Int = 0

	private var soakVisits: Int = 0

	private val SCENE_FRAMES: Int = lang.getInteger("client.test.sceneFrames", 150)

	/** client.test.sceneBurst: consecutive frames saved per scene before its comparison (flicker checks). */
	private val SCENE_BURST: Int = lang.getInteger("client.test.sceneBurst", 0)

	private var burstIndex: Int = 0

	private val SCENE_WALK_TILES: Int = lang.getInteger("client.test.sceneWalkTiles", 6)

	private val SCENE_WALK_EVERY: Int = lang.getInteger("client.test.sceneWalkEvery", 1000000)

	private val SCENE_BURST_LOC: Int = lang.getInteger("client.test.sceneBurstLoc", 0)

	/** client.test.sceneBurstSave=false: a burst only measured, its frames not written (long walks). */
	private val SCENE_BURST_SAVE: Boolean = lang.getProperty("client.test.sceneBurstSave", "true") != "false"

	/** client.test.sceneLumaJump: the largest frame-to-frame change in mean brightness (0-255) a burst may have. */
	private val SCENE_LUMA_JUMP: Double = (lang.getProperty("client.test.sceneLumaJump", "3.0").toDoubleOrNull() ?: 3.0)

	private var burstLuma: Double = 0.0

	private val SCENE_LUMA_LOG: Boolean = lang.getBoolean("client.test.sceneLumaLog")

	private var burstPixels: IntArray? = null

	private var burstPrevious: IntArray? = null

	private var burstWidth: Int = 0

	private var burstHeight: Int = 0

	private var burstSaved: Int = 0

	private var burstRoofs: Long = 0L

	private var burstRoofFrame: Int = -1

	/**
	 * What the game hides or rebuilds, packed: the area whose roofs are hidden, the player's remove-roof
	 * tile and plane, the scene's base (a map rebuild). A change is a real jump, not a flicker.
	 */
	private fun roofSignature(): Long {
		// the roofs over the player's tile go when it stands on a remove-roof tile (the default roof setting)
		val player: PlayerEntity? = PlayerUpdate.localPlayer
		var underRoof: Long = 0L
		if (player != null && SettingsBits.tileFunctionMasks != null) {
			val level: Int = player.level.toInt()
			val tileX: Int = player.x shr 9
			val tileZ: Int = player.z shr 9
			if (tileX >= 0 && tileZ >= 0 && tileX < MapRegion.sceneSizeX && tileZ < MapRegion.sceneSizeY) {
				underRoof = ((SettingsBits.tileFunctionMasks!![level]!![tileX]!![tileZ].toInt() and TileFlag.REMOVE_ROOF) + level * 1024).toLong()
			}
		}
		// the roof stamps near the player: whatever hides or shows a roof (the player's tile, the camera,
		// the roof setting) changes them, and the scene draws roofs by them
		var stamps: Long = 0L
		val roofStamps = MapRegion.roofStamps
		if (player != null && roofStamps != null) {
			val px: Int = player.x shr 9
			val pz: Int = player.z shr 9
			for (level in 0 until roofStamps.size) {
				val plane = roofStamps[level] ?: continue
				for (x in kotlin.math.max(0, px - 12)..kotlin.math.min(plane.size - 1, px + 12)) {
					val row = plane[x] ?: continue
					for (z in kotlin.math.max(0, pz - 12)..kotlin.math.min(row.size - 1, pz + 12)) {
						// hidden or not is all that matters: the stamp equal to this frame's roof value
						stamps = stamps * 31L + (if (row[z].toInt() == 1 || row[z].toInt() == ((SceneDrawer.timesSceneRendered) and 0xFF).toByte().toInt()) 1L else 0L)
					}
				}
			}
		}
		// a map rebuild re-centres the scene (and works out what is hidden anew)
		val base: Long = (MapRegion.sceneBaseX.toLong() shl 40) xor (MapRegion.sceneBaseY.toLong() shl 24)
		return stamps xor base xor (underRoof shl 56) xor (MapRegion.roofMinX[0].toLong() shl 48) xor (MapRegion.roofMaxX[0].toLong() shl 32) xor (MapRegion.roofMinZ[0].toLong() shl 16) xor MapRegion.roofMaxZ[0].toLong() xor (MapRegion.visibleBelowDrawheight[0].toLong() shl 20)
	}

	private var burstMaxJump: Double = 0.0

	private var burstMaxJumpAt: Int = 0

	private val SCENE_SPIN: Int = lang.getInteger("client.test.sceneSpin", 0)

	private val SCENE_WALK: Boolean = lang.getBoolean("client.test.sceneWalk")

	/**
	 * client.test.hdCycle=true (toolkit 6, scenes mode): at the first scene, the frame with every
	 * 634HD switch off is taken, then each switch is set to its highest value while playing, held,
	 * saved (build/smoke/scenes/cycle-<name>-on.png) and set off again; the frame after must match the
	 * first one within the scene's limit. Catches a switch that only works from the start, or that
	 * leaves something behind. The "all" line does every switch at once.
	 */
	private val HD_CYCLE: Boolean = lang.getBoolean("client.test.hdCycle")

	private val CYCLE_SWITCHES: Array<String> = arrayOf(
		"tonemap=aces", "dither=on", "anisotropy=16", "mipmaps=on", "fog=on", "fade=on", "ambientOcclusion=on",
		"pixelLighting=on", "shadows=4096", "lights=on", "lightGrid=on", "terrainBlend=on", "water=2", "foliage=on", "sky=on",
		"softParticles=on", "fxaa=on", "bloom=on", "colourGrading=on", "materials=on", "textureReplacements=on", "particles=on", "preset=ultra",
	)

	private var cycleDone: Boolean = false

	private var cycleIndex: Int = -1

	private var cycleFrame: Int = 0

	private var cycleBaseline: IntArray? = null

	private var cycleWorst: Int = 0

	private const val CYCLE_FRAMES: Int = 40

	/** One frame of the switch cycle (client.test.hdCycle). */
	private fun cycleStep() {
		if (gameFrames - cycleFrame < CYCLE_FRAMES) {
			return
		}
		cycleFrame = gameFrames
		val toolkit: Toolkit = ClientState.activeToolkit!!
		val size: IntArray = IntArray(2)
		toolkit.accumulateViewportDimensions(size)
		if (cycleIndex < 0) {
			cycleBaseline = toolkit.na(0, 0, size[0], size[1])
			println("SMOKE cycle: baseline taken, " + CYCLE_SWITCHES.size + " switches to cycle")
			cycleIndex = 0
			cycleState = 0
			return
		}
		val entry: String = CYCLE_SWITCHES[cycleIndex]
		val at: Int = entry.indexOf('=')
		val switch: String = entry.substring(0, at)
		if (cycleState == 0) {
			// on
			val error: String? = if (switch == "preset") HdSettings.applyPreset(entry.substring(at + 1)) else HdSettings.set(switch, entry.substring(at + 1))
			if (error != null) {
				fail("cycle: " + entry + ": " + error)
			}
			cycleState = 1
			return
		}
		if (cycleState == 1) {
			// held: saved, then off again (dither's default is on)
			saveFrame("build/smoke/scenes/cycle-" + switch + "-on.png")
			if (switch == "preset") {
				HdSettings.applyPreset("off")
			} else if (switch == "dither") {
				HdSettings.set(switch, "on")
			} else {
				HdSettings.set(switch, "off")
			}
			cycleState = 2
			return
		}
		// off again: the frame must be back to the baseline
		val pixels: IntArray = toolkit.na(0, 0, size[0], size[1])!!
		val base: IntArray = cycleBaseline!!
		var different: Int = 0
		for (i in pixels.indices) {
			val a: Int = pixels[i]
			val b: Int = base[i]
			if (kotlin.math.abs((a shr 16 and 0xFF) - (b shr 16 and 0xFF)) > SCENE_PIXEL_TOLERANCE || kotlin.math.abs((a shr 8 and 0xFF) - (b shr 8 and 0xFF)) > SCENE_PIXEL_TOLERANCE || kotlin.math.abs((a and 0xFF) - (b and 0xFF)) > SCENE_PIXEL_TOLERANCE) {
				different++
			}
		}
		val perMille: Int = (different.toLong() * 1000L / pixels.size.toLong()).toInt()
		if (perMille > cycleWorst) cycleWorst = perMille
		val ok: Boolean = perMille <= sceneLimit
		println("SMOKE cycle " + entry + ": on and off again, " + perMille + "/1000 from the baseline" + (if (ok) "" else " LEFT SOMETHING BEHIND"))
		if (!ok) {
			sceneFailures++
			saveFrame("build/smoke/scenes/cycle-" + switch + "-after.png")
		}
		cycleState = 0
		cycleIndex++
		if (cycleIndex >= CYCLE_SWITCHES.size) {
			cycleDone = true
			println("SMOKE cycle done: worst " + cycleWorst + "/1000")
		}
	}

	private var cycleState: Int = 0

	private var burstFrame: Int = -1

	private val SCENE_REFERENCE_TOOLKIT: Int = lang.getInteger("client.test.sceneReferenceToolkit", -1)

	private val SCENE_PIXEL_TOLERANCE: Int = lang.getInteger("client.test.scenePixelTolerance", 24)

	private val SCENE_MAX_DIFFERENT: Int = lang.getInteger("client.test.sceneMaxDifferent", 40)

	/** The limit for the scene being compared: its seventh value, else client.test.sceneMaxDifferent. */
	private var sceneLimit: Int = 40

	private const val SCENE_WAIT: Int = 600

	/** Where smoketest starts, and must be left: the other references are taken there. */
	private val SCENE_HOME: IntArray = intArrayOf(3094, 3107, 0)

	/** Lumbridge courtyard, the camera zoomed onto the player (held in one pose: holdPose). */
	private const val OUTFIT_VIEW: String = "3222,3218,0,0,1200,450,35"

	// not a const: the compiler's constant folding runs out of memory on a chain this long
	@Suppress("MayBeConst")
	private val DEFAULT_SCENES: String = "lumbridge:3222,3218,0,0,1400,0;" +
		"varrock:3212,3428,0,2048,1400,0;" +
		"grand_exchange:3164,3478,0,0,1600,0;" +
		"falador_park:3007,3373,0,4096,1400,0;" +
		"draynor_swamp:3108,3290,0,8192,1400,0;" +
		"karamja_volcano:2852,3168,0,12288,1600,0;" +
		"varrock_sewers:3237,9866,0,0,1400,0;" +
		// the cows wander between runs: toolkit 1 against its own reference differs by 33 to 57
		"cows:3256,3276,0,4096,1400,0,65;" +
		// the wizards wander: toolkit 1 against its own reference reached 47
		"wizards_tower:3109,3162,0,0,1400,0,65;" +
		// many NPCs: guards in shiny armour, and barbarians around their fires (they wander too; the
		// palace reached 69 on toolkit 6)
		"varrock_palace:3212,3468,0,0,1400,0,80;" +
		"barbarian_village:3082,3420,0,0,1400,0,65;" +
		// the environment fog (environments.json): Al Kharid's town in the desert haze, the open
		// desert south of the Shantay pass under the generated KHARIDIAN_DESERT entry
		"al_kharid:3293,3180,0,0,1400,0;" +
		"kharid_desert:3304,3060,0,0,1400,0;" +
		// the Fishing Guild dock from the north at the lowest pitch: the tile under the player was culled
		// behind a railing's occluder sized as a full wall (MapRegion.occluderHeight)
		"fishing_guild_dock:2593,3415,0,8192,1024,0;" +
		// the player close up in outfits that between them use every way an item's material draws
		// (client.test.materialScan): shiny metal and its three highlights, reflection, unlit, HDR,
		// scrolling, cut-out and blended transparency
		"outfit_metal:" + OUTFIT_VIEW + ",0:4732|1:6570|2:87|3:35|4:1127|5:1201|7:4730;" +
		"outfit_robes:" + OUTFIT_VIEW + ",0:12866|1:-1|2:15511|3:3053|4:19317|5:-1|7:1079;" +
		"outfit_khazard:" + OUTFIT_VIEW + ",0:74|1:-1|2:86|3:1285|4:75|5:1201|7:1079;" +
		"outfit_torch:" + OUTFIT_VIEW + ",0:4732|1:10637|2:15511|3:8987|4:1127|5:-1|7:4730;" +
		// the fire cape from behind (its lava, texture 40: the flowing, unlit material with materials on;
		// the game's scrolling texture without, which this compares to toolkit 1)
		"outfit_firecape:3222,3218,0,8192,1200,450,25,0:4732|1:6570|2:87|3:35|4:1127|5:1201|7:4730"

	/**
	 * client.test.findText=word: once in game, prints every interface component whose text holds the
	 * word (ignoring case), as SMOKE TEXT interface:component "text", then stops. Finds the components
	 * a client-side hook has to reach.
	 */
	private val FIND_TEXT: String = lang.getProperty("client.test.findText", "")

	private var sceneList: Array<String?>? = null

	private var sceneIndex: Int = 0

	/* 0 teleport, 1 wait to arrive, 2 settle and capture; 3 logged out for the relog. */
	private var sceneStep: Int = 0

	private var sceneFrame: Int = 0

	private var sceneFailures: Int = 0

	private var sceneSummary: String = ""

	/** The checks done so far, one bit each in the order frame() runs them. */
	private var checksDone: Long = 0L

	private var startToolkit: Int = -1

	private var toolkitSwitched: Boolean = false

	private var passLine: String? = null

	private var restoreFrame: Int = -1

	private var commandFrame: Int = -1

	private var spotSeen: Int = -1

	private var spotSeenFrame: Int = -1

	private const val COMMAND_WAIT: Int = 150

	private var spotFrameMax: Int = 0

	private val START: Long = lang.currentTimeMillis()

	private var loginSent: Boolean = false

	private var loginTime: Long = 0L

	private const val LOGIN_RETRY: Long = 8000L

	private var highestStep: Int = 0

	private var lastStep: Int = -1

	private var gameFrames: Int = 0

	private var frames: Int = 0

	/**
	 * Called at the start of every logic tick.
	 */
	@JvmStatic
	fun tick() {
		if (!ENABLED) {
			return
		}
		if (benchCount >= 0) {
			benchTicks++
		}
		val step: Int = ClientState.mainLogicStep!!.id()
		if (step != lastStep) {
			lastStep = step
			highestStep = kotlin.math.max(highestStep, step)
			println("SMOKE step " + step + " at " + elapsed() + "s")
		}
		// after a relog the server may still hold the old session for a while and refuse ("already
		// logged in"): try again every LOGIN_RETRY ms while the login screen is idle
		if (ClientState.mainLogicStep === MainLogicStep.STEP_LOGIN_SCREEN && (!loginSent || sceneStep == 3 && lang.currentTimeMillis() - loginTime > LOGIN_RETRY && LoginManager.loginStep == LoginStep.IDLE)) {
			loginSent = true
			loginTime = lang.currentTimeMillis()
			println("SMOKE logging in as " + USER)
			LoginManager.lobbyLogin(USER, PASSWORD)
		}
		if (lang.currentTimeMillis() - START > TIMEOUT) {
			fail("timed out after " + elapsed() + "s at step " + step + " (highest " + highestStep + "), " + frames + " frames drawn")
		}
	}

	/**
	 * Called after every frame is drawn.
	 */
	@JvmStatic
	fun frame() {
		if (!ENABLED) {
			return
		}
		frames++
		if (restoreFrame >= 0) {
			// switched back to the starting toolkit: let it settle, then report
			if (frames - restoreFrame >= 50) {
				println(passLine)
				lang.flushOut()
				lang.halt(0)
			}
			return
		}
		val player: PlayerEntity? = PlayerUpdate.localPlayer
		if (ClientState.mainLogicStep !== MainLogicStep.STEP_GAME_SCREEN || player == null) {
			return
		}
		// client.test.wear=slot:item,...: the local player shown wearing those items, on this client only
		// (Void has no command to equip them); e.g. 0:1163,4:1127,5:1201,7:1079 is rune armour
		if (WEAR.length > 0 && !worn && player.playerModel != null) {
			worn = true
			val pairs: Array<String> = WEAR.javaSplit(",")
			for (pair in pairs) {
				val colon: Int = pair.indexOf(':')
				player.playerModel!!.addAppearanceItem(pair.substring(0, colon).javaTrim().toInt(), pair.substring(colon + 1).javaTrim().toInt(), TypeLists.OBJ)
			}
			println("SMOKE wearing " + WEAR)
		}
		// ... and in the worn container (94) too, on this client only, once it exists: 634HD's carried
		// light looks there (a worn fire cape or firemaking cape lights the player)
		if (WEAR.length > 0 && !wornContainer && ItemContainer.getContainer(94, false) != null) {
			wornContainer = true
			val pairs: Array<String> = WEAR.javaSplit(",")
			for (pair in pairs) {
				val colon: Int = pair.indexOf(':')
				val item: Int = pair.substring(colon + 1).javaTrim().toInt()
				if (item >= 0) ItemContainer.setItemInContainer(94, false, pair.substring(0, colon).javaTrim().toInt(), 1, item)
			}
		}
		// client.test.carry=item,...: those items in the backpack's last slots, on this client only (the
		// server's backpack is untouched): 634HD's carried light, e.g. 594 a lit torch, 4550 a bullseye lantern
		if (CARRY.length > 0 && !carried && ItemContainer.getContainer(93, false) != null) {
			carried = true
			val ids: Array<String> = CARRY.javaSplit(",")
			for (i in ids.indices) {
				ItemContainer.setItemInContainer(93, false, 27 - i, 1, ids[i].javaTrim().toInt())
			}
			println("SMOKE carrying " + CARRY)
		}
		// client.test.characterShadows=0|1: the Character shadows graphics option for this run (not saved)
		if (CHARACTER_SHADOWS >= 0 && GamePreferences.uid!!.characterShadows!!.getValue() != CHARACTER_SHADOWS) {
			GamePreferences.uid!!.setValue(GamePreferences.uid!!.characterShadows, CHARACTER_SHADOWS)
			println("SMOKE character shadows " + CHARACTER_SHADOWS)
		}
		if (TOOLKIT >= 0 && !toolkitSwitched) {
			toolkitSwitched = true
			startToolkit = GamePreferences.uid!!.toolkit!!.getValue()
			if (startToolkit != TOOLKIT) {
				println("SMOKE switching from toolkit " + startToolkit + " to " + TOOLKIT)
				switchToolkit(TOOLKIT)
				if (GamePreferences.uid!!.toolkit!!.getValue() != TOOLKIT) {
					fail("toolkit " + TOOLKIT + " could not start; the client is on toolkit " + GamePreferences.uid!!.toolkit!!.getValue())
				}
				println("SMOKE now on toolkit " + TOOLKIT + " (" + ClientState.activeToolkit!!.jClass.getSimpleName() + ")")
				gameFrames = 0
				return
			}
		}
		if (++gameFrames < FRAMES) {
			return
		}
		if (MATERIAL_SCAN) {
			materialScan()
			return
		}
		if (BENCH > 0) {
			bench()
			return
		}
		if (SOAK_MINUTES > 0) {
			soak(player)
			return
		}
		if (SCENES.length > 0) {
			scenes(player)
			return
		}
		if (FIND_TEXT.startsWith("open:")) {
			openAndCapture()
			return
		}
		if (FIND_TEXT.length > 0) {
			findText()
			return
		}
		if (firstImage == null) {
			firstImage = checkImage("", SHOT)
			if (COMMAND.length > 0) {
				println("SMOKE sending ::" + COMMAND)
				sendCommand(COMMAND)
				commandFrame = gameFrames
			}
			if (MENU_CHECK) {
				menuCheck()
			}
			if (NPC_MENU.length > 0 && !npcMenuChecked) {
				npcMenuChecked = true
				npcMenuCheck()
			}
		}
		if (PLUGIN_MANAGER && pluginManagerStep < 7) {
			pluginManagerCheck()
			return
		}
		if (commandFrame >= 0) {
			val slots: Array<PathingEntitySpotAnimation> = arrayOf(player.primarySpotAnimation, player.secondarySpotAnimation)
			for (i in 0 until slots.size) {
				if (slots[i].spotAnimationId != -1) {
					if (spotSeen == -1) {
						println("SMOKE " + (if (i == 0) "primary" else "secondary") + " spot animation slot took graphic " + slots[i].spotAnimationId)
					}
					spotSeen = slots[i].spotAnimationId
					spotFrameMax = kotlin.math.max(spotFrameMax, slots[i].currentFrameId)
				}
			}
			// the server answers on its next tick, up to 600 ms away: count from when the graphic arrives
			if (spotSeen != -1 && spotSeenFrame < 0) {
				spotSeenFrame = gameFrames
			}
			val gfx: Boolean = COMMAND.javaSplit(" ")[0] == "gfx"
			if (gfx && spotSeen == -1 && gameFrames - commandFrame < COMMAND_WAIT) {
				return
			}
			if (gameFrames - (if (spotSeenFrame >= 0) spotSeenFrame else commandFrame) < COMMAND_FRAMES) {
				return
			}
			val word: String = COMMAND.javaSplit(" ")[0]
			if (word == "gfx" && (spotSeen == -1 || spotFrameMax == 0)) {
				fail("::" + COMMAND + " did not play: neither spot animation slot " + (if (spotSeen == -1) "took a graphic" else "took graphic " + spotSeen + " but never advanced its frame"))
			}
			if (word == "gfx" && !spotModelBuilt) {
				fail("::" + COMMAND + " played in the spot animation slot, but its model was never drawn")
			}
			if (chatFrame < 0) {
				val second: String? = checkImage("-" + word, SHOT.replace(".png", "-" + word + ".png"))
				firstImage += "; after ::" + COMMAND + (if (spotSeen == -1) "" else " (spot animation " + spotSeen + ", frame up to " + spotFrameMax + ", model built)") + ": " + second
			}
		}
		if (CHAT.length > 0) {
			if (chatFrame < 0) {
				println("SMOKE forcing overhead chat \"" + CHAT + "\"")
				sendCommand("chat \"" + CHAT + "\"")
				chatFrame = gameFrames
				commandFrame = -1
				return
			}
			val expected: String = CHAT
			if (player.hintDetails.text != null && player.hintDetails.text != lastText) {
				lastText = player.hintDetails.text
				println("SMOKE overhead chat now \"" + lastText + "\"")
			}
			if (expected.equals(player.hintDetails.text, ignoreCase = true)) {
				if (chatSeen == null) {
					println("SMOKE overhead chat took \"" + player.hintDetails.text + "\", colour " + player.hintDetails.colorType + ", " + player.hintDetails.ticksLeft + " ticks")
					chatTicksFirst = player.hintDetails.ticksLeft
				}
				chatSeen = player.hintDetails.text
				chatColour = player.hintDetails.colorType
				chatTicksLast = player.hintDetails.ticksLeft
				for (i in 0 until OverlayManager.overheadTextCount) {
					if (expected.equals(OverlayManager.OVERHEAD_TEXTS[i], ignoreCase = true)) {
						chatQueued = true
					}
				}
			}
			if (gameFrames - chatFrame < CHAT_FRAMES) {
				return
			}
			if (chatSeen == null) {
				fail("the overhead chat never took \"" + expected + "\"")
			}
			if (chatTicksLast >= chatTicksFirst) {
				fail("the overhead chat's ticks did not count down (" + chatTicksFirst + " to " + chatTicksLast + ")")
			}
			if (!chatQueued) {
				fail("the overhead chat never reached the overlay's text queue")
			}
			if (!firstImage!!.contains("; overhead chat")) {
				firstImage += "; overhead chat \"" + chatSeen + "\" colour " + chatColour + ", ticks " + chatTicksFirst + " to " + chatTicksLast + ", drawn"
			}
		}
		// each check runs until it is done, once: a done check is not asked again (and so does not add its
		// summary to the pass line on every later frame)
		if (WINDOW_MODES && (checksDone and 1L) == 0L) {
			if (!windowModesDone()) {
				return
			}
			checksDone = checksDone or 1L
		}
		if (SUB_INTERFACES && (checksDone and 2L) == 0L) {
			if (!subInterfacesDone()) {
				return
			}
			checksDone = checksDone or 2L
		}
		if (MENU_WALK && (checksDone and 4L) == 0L) {
			if (!menuWalkDone(player)) {
				return
			}
			checksDone = checksDone or 4L
		}
		if (MEDIA && (checksDone and 8L) == 0L) {
			if (!mediaDone(player)) {
				return
			}
			checksDone = checksDone or 8L
		}
		if (MARKERS && (checksDone and 16L) == 0L) {
			if (!markersDone(player)) {
				return
			}
			checksDone = checksDone or 16L
		}
		if (XP_DROPS && (checksDone and 32L) == 0L) {
			if (!xpDropsDone()) {
				return
			}
			checksDone = checksDone or 32L
		}
		if (RESPAWN && (checksDone and 64L) == 0L) {
			if (!respawnDone()) {
				return
			}
			checksDone = checksDone or 64L
		}
		if (GROUND_ITEMS && (checksDone and 128L) == 0L) {
			if (!groundItemsDone(player)) {
				return
			}
			checksDone = checksDone or 128L
		}
		if (LOOT_TRACKER && (checksDone and 256L) == 0L) {
			if (!lootTrackerDone()) {
				return
			}
			checksDone = checksDone or 256L
		}
		if (OBJECT_MARKERS && (checksDone and 512L) == 0L) {
			if (!objectMarkersDone(player)) {
				return
			}
			checksDone = checksDone or 512L
		}
		if (XP_TRACKER && (checksDone and 1024L) == 0L) {
			if (!xpTrackerDone()) {
				return
			}
			checksDone = checksDone or 1024L
		}
		if (OPPONENT_INFO && (checksDone and 2048L) == 0L) {
			if (!opponentInfoDone(player)) {
				return
			}
			checksDone = checksDone or 2048L
		}
		if ((BOOSTS || OVERLAY_DRAG) && (checksDone and 4096L) == 0L) {
			if (!boostsDone()) {
				return
			}
			checksDone = checksDone or 4096L
		}
		if (OVERLAY_DRAG && (checksDone and 8192L) == 0L) {
			if (!overlayDragDone()) {
				return
			}
			checksDone = checksDone or 8192L
		}
		if (BANK_ORGANIZER && (checksDone and 16384L) == 0L) {
			if (!bankOrganizerDone()) {
				return
			}
			checksDone = checksDone or 16384L
		}
		if (STATUS_BARS && (checksDone and 32768L) == 0L) {
			if (!statusBarsDone()) {
				return
			}
			checksDone = checksDone or 32768L
		}
		if (TIMERS && (checksDone and 65536L) == 0L) {
			if (!timersDone()) {
				return
			}
			checksDone = checksDone or 65536L
		}
		if (TILE_INDICATORS && (checksDone and 131072L) == 0L) {
			if (!tileIndicatorsDone(player)) {
				return
			}
			checksDone = checksDone or 131072L
		}
		if (XP_GLOBES && (checksDone and 262144L) == 0L) {
			if (!xpGlobesDone()) {
				return
			}
			checksDone = checksDone or 262144L
		}
		if (BARROWS && (checksDone and 524288L) == 0L) {
			if (!barrowsDone()) {
				return
			}
			checksDone = checksDone or 524288L
		}
		if (ATTACK_STYLES && (checksDone and 1048576L) == 0L) {
			if (!attackStylesDone()) {
				return
			}
			checksDone = checksDone or 1048576L
		}
		if (CANNON && (checksDone and 2097152L) == 0L) {
			if (!cannonDone(player)) {
				return
			}
			checksDone = checksDone or 2097152L
		}
		if (ANTI_DRAG && (checksDone and 4194304L) == 0L) {
			if (!antiDragDone()) {
				return
			}
			checksDone = checksDone or 4194304L
		}
		if (EMOJIS && (checksDone and 8388608L) == 0L) {
			if (!emojisDone(player)) {
				return
			}
			checksDone = checksDone or 8388608L
		}
		if (KEY_REMAPPING && (checksDone and 16777216L) == 0L) {
			if (!keyRemappingDone()) {
				return
			}
			checksDone = checksDone or 16777216L
		}
		if (CUSTOM_CURSOR && (checksDone and 33554432L) == 0L) {
			if (!customCursorDone()) {
				return
			}
			checksDone = checksDone or 33554432L
		}
		if (ITEM_STATS && (checksDone and 67108864L) == 0L) {
			if (!itemStatsDone()) {
				return
			}
			checksDone = checksDone or 67108864L
		}
		if (WOODCUTTING && (checksDone and 134217728L) == 0L) {
			if (!woodcuttingDone(player)) {
				return
			}
			checksDone = checksDone or 134217728L
		}
		if (MINING && (checksDone and 268435456L) == 0L) {
			if (!miningDone(player)) {
				return
			}
			checksDone = checksDone or 268435456L
		}
		if (REPORT_BUTTON && (checksDone and 536870912L) == 0L) {
			if (!reportButtonDone()) {
				return
			}
			checksDone = checksDone or 536870912L
		}
		if (PLAYER_INDICATORS && (checksDone and 1073741824L) == 0L) {
			if (!playerIndicatorsDone()) {
				return
			}
			checksDone = checksDone or 1073741824L
		}
		if (SPECIAL_COUNTER && (checksDone and 2147483648L) == 0L) {
			if (!specialCounterDone()) {
				return
			}
			checksDone = checksDone or 2147483648L
		}
		if (REGEN_METER && (checksDone and 4294967296L) == 0L) {
			if (!regenMeterDone()) {
				return
			}
			checksDone = checksDone or 4294967296L
		}
		if (RUN_ENERGY && (checksDone and 8589934592L) == 0L) {
			if (!runEnergyDone()) {
				return
			}
			checksDone = checksDone or 8589934592L
		}
		if (POISON && (checksDone and 17179869184L) == 0L) {
			if (!poisonDone()) {
				return
			}
			checksDone = checksDone or 17179869184L
		}
		if (FISHING && (checksDone and 34359738368L) == 0L) {
			if (!fishingDone()) {
				return
			}
			checksDone = checksDone or 34359738368L
		}
		if (AGILITY && (checksDone and 68719476736L) == 0L) {
			if (!agilityDone()) {
				return
			}
			checksDone = checksDone or 68719476736L
		}
		if (SLAYER && (checksDone and 137438953472L) == 0L) {
			if (!slayerDone()) {
				return
			}
			checksDone = checksDone or 137438953472L
		}
		if (ITEM_CHARGES && (checksDone and 274877906944L) == 0L) {
			if (!itemChargesDone()) {
				return
			}
			checksDone = checksDone or 274877906944L
		}
		if (ITEM_IDENTIFICATION && (checksDone and 549755813888L) == 0L) {
			if (!itemIdentificationDone()) {
				return
			}
			checksDone = checksDone or 549755813888L
		}
		if (CHAT_NOTIFICATIONS && (checksDone and 1099511627776L) == 0L) {
			if (!chatNotificationsDone()) {
				return
			}
			checksDone = checksDone or 1099511627776L
		}
		if (CHAT_FILTER && (checksDone and 2199023255552L) == 0L) {
			if (!chatFilterDone()) {
				return
			}
			checksDone = checksDone or 2199023255552L
		}
		if (CHAT_HISTORY && (checksDone and 4398046511104L) == 0L) {
			if (!chatHistoryDone()) {
				return
			}
			checksDone = checksDone or 4398046511104L
		}
		if (COMBAT_LEVEL && (checksDone and 8796093022208L) == 0L) {
			if (!combatLevelDone()) {
				return
			}
			checksDone = checksDone or 8796093022208L
		}
		if (VIRTUAL_LEVELS && (checksDone and 17592186044416L) == 0L) {
			if (!virtualLevelsDone()) {
				return
			}
			checksDone = checksDone or 17592186044416L
		}
		if (METRONOME && (checksDone and 35184372088832L) == 0L) {
			if (!metronomeDone()) {
				return
			}
			checksDone = checksDone or 35184372088832L
		}
		if (DEATH_INDICATOR && (checksDone and 70368744177664L) == 0L) {
			if (!deathIndicatorDone()) {
				return
			}
			checksDone = checksDone or 70368744177664L
		}
		if (NPC_AGGRESSION && (checksDone and 140737488355328L) == 0L) {
			if (!npcAggressionDone()) {
				return
			}
			checksDone = checksDone or 140737488355328L
		}
		if (HUNTER && (checksDone and 281474976710656L) == 0L) {
			if (!hunterDone(player)) {
				return
			}
			checksDone = checksDone or 281474976710656L
		}
		if (IMPLINGS && (checksDone and 562949953421312L) == 0L) {
			if (!implingsDone(player)) {
				return
			}
			checksDone = checksDone or 562949953421312L
		}
		if (COOKING && (checksDone and 1125899906842624L) == 0L) {
			if (!cookingDone()) {
				return
			}
			checksDone = checksDone or 1125899906842624L
		}
		passLine = "SMOKE PASS in game as " + USER + " at scene tile " + player.movementBaseX[0] + "," + player.movementBaseY[0] + " after " + elapsed() + "s, " + gameFrames + " frames in game, " + frames + " in all; " + firstImage
		if (toolkitSwitched && startToolkit != TOOLKIT) {
			println("SMOKE switching back to toolkit " + startToolkit)
			switchToolkit(startToolkit)
			restoreFrame = frames
			return
		}
		println(passLine)
		lang.flushOut()
		lang.halt(0)
	}

	/**
	 * The benchmark (client.test.bench): switch the window mode if asked and let it settle, then time
	 * BENCH frames from one frame() to the next, print the SMOKE BENCH line and stop.
	 */
	private fun bench() {
		if (benchHomeFrame >= 0) {
			val player: PlayerEntity = PlayerUpdate.localPlayer!!
			if (sceneArrived(player, SCENE_HOME[0], SCENE_HOME[1], SCENE_HOME[2]) || gameFrames - benchHomeFrame > SCENE_WAIT) {
				lang.flushOut()
				lang.halt(0)
			}
			return
		}
		if (BENCH_POSITION.length > 0 && !benchArrived) {
			val values: Array<String> = BENCH_POSITION.javaSplit(",")
			val x: Int = values[0].javaTrim().toInt()
			val y: Int = values[1].javaTrim().toInt()
			val level: Int = if (values.size > 2) values[2].javaTrim().toInt() else 0
			benchCamera = intArrayOf(if (values.size > 3) values[3].javaTrim().toInt() else 0, if (values.size > 4) values[4].javaTrim().toInt() else 1400, if (values.size > 5) values[5].javaTrim().toInt() else 0)
			val player: PlayerEntity = PlayerUpdate.localPlayer!!
			if (sceneStep == 0) {
				sendCommand("tele " + x + " " + y + " " + level)
				sceneStep = 1
				sceneFrame = gameFrames
				return
			}
			if (!sceneArrived(player, x, y, level)) {
				if (gameFrames - sceneFrame > SCENE_WAIT) {
					fail("bench: never arrived at " + BENCH_POSITION)
				}
				return
			}
			benchArrived = true
			sceneStep = 0
		}
		val camera: IntArray? = benchCamera
		if (camera != null) {
			Camera.camAngleY = camera[0].toFloat()
			Camera.camAngleX = camera[1].toFloat()
			Camera.cameraYawVelocity = 0.0f
			Camera.cameraPitchVelocity = 0.0f
			ClientFeatures.zoomOffset = camera[2]
		}
		if (benchModeFrame < 0) {
			benchModeFrame = frames
			if (BENCH_MODE == "resizable") {
				// the whole window: a detail preset (as on a fresh profile) can cap the resizable view at 800x600
				// or 1024x768; put back before stopping, since the smoke references are taken with the cap
				benchMaxScreenSize = GamePreferences.uid!!.maxScreenSize!!.getValue()
				GamePreferences.uid!!.setValue(GamePreferences.uid!!.maxScreenSize, 0)
			}
			if (BENCH_WINDOW.length > 0) {
				val size: Array<String> = BENCH_WINDOW.javaSplit("x")
				val frame: awt.Frame = com.jagex.game.runetek5.client.GameShell.engineFrame!!
				val insets: awt.Insets = frame.getInsets()
				frame.setSize(size[0].toInt() + insets.left + insets.right, size[1].toInt() + insets.top + insets.bottom)
			}
			if (BENCH_MODE == "resizable" && WindowMode.getWindowMode() !== WindowModeType.RESIZABLE) {
				sendCommand("wm2")
			} else if (BENCH_MODE == "fixed" && WindowMode.getWindowMode() !== WindowModeType.FIXED) {
				sendCommand("wm1")
			}
			return
		}
		if (frames - benchModeFrame < BENCH_SETTLE) {
			return
		}
		val now: Long = lang.nanoTime()
		val work: Double = turnElapsedMs()
		if (benchCount < 0) {
			benchTimes = DoubleArray(BENCH)
			benchWork = DoubleArray(BENCH)
			benchCount = 0
			benchTicks = 0
			benchLast = now
			return
		}
		val times: DoubleArray = benchTimes!!
		benchDrawCalls += RenderStats.lastDrawCalls.toLong()
		for (i in 0 until 3) {
			benchCategories[i] += RenderStats.lastCategoryDrawCalls[i].toLong()
		}
		benchTriangles += RenderStats.lastTriangles.toLong()
		benchWork!![benchCount] = work
		times[benchCount++] = (now - benchLast).toDouble() / 1000000.0
		benchLast = now
		if (benchCount < BENCH) {
			return
		}
		var total: Double = 0.0
		for (i in 0 until BENCH) {
			total += times[i]
		}
		times.sort()
		val average: Double = total / BENCH.toDouble()
		// the work of each frame apart from waiting, where the platform measures it (turnElapsedMs)
		val workTimes: DoubleArray = benchWork!!
		var workLine: String = ""
		if (workTimes[0] >= 0.0) {
			var workTotal: Double = 0.0
			for (i in 0 until BENCH) {
				workTotal += workTimes[i]
			}
			workTimes.sort()
			workLine = " work=" + format(workTotal / BENCH.toDouble()) + "ms work95=" + format(workTimes[kotlin.math.min(BENCH - 1, BENCH * 95 / 100)]) + "ms"
		}
		val dimensions: IntArray = IntArray(2)
		ClientState.activeToolkit!!.accumulateViewportDimensions(dimensions)
		val size: String = dimensions[0].toString() + "x" + dimensions[1]
		println("SMOKE BENCH toolkit=" + GamePreferences.uid!!.toolkit!!.getValue() + " size=" + size + " mode=" + WindowMode.getWindowMode() + " frames=" + BENCH +
			" avg=" + format(average) + "ms p50=" + format(times[BENCH / 2]) + "ms p95=" + format(times[kotlin.math.min(BENCH - 1, BENCH * 95 / 100)]) + "ms" +
			" fps=" + format(1000.0 / average) + workLine + " ticks/frame=" + format(benchTicks.toDouble() / BENCH.toDouble()) +
			(if (benchDrawCalls > 0L) " drawCalls=" + (benchDrawCalls / BENCH.toLong()) + " (models " + (benchCategories[1] / BENCH.toLong()) + ", ground " + (benchCategories[2] / BENCH.toLong()) + ", other " + (benchCategories[0] / BENCH.toLong()) + ") triangles=" + (benchTriangles / BENCH.toLong()) + " regionHitch=" + RenderStats.lastRegionUploadMs + "ms regionBuild=" + RenderStats.lastRegionBuildMs + "ms chunkRebuilds=" + RenderStats.chunkRebuilds else ""))
		if (benchMaxScreenSize >= 0) {
			GamePreferences.uid!!.setValue(GamePreferences.uid!!.maxScreenSize, benchMaxScreenSize)
			GamePreferences.savePreferences()
		}
		if (benchArrived) {
			// home again before stopping, or the other checks' references stop matching
			sendCommand("tele " + SCENE_HOME[0] + " " + SCENE_HOME[1] + " " + SCENE_HOME[2])
			benchHomeFrame = gameFrames
			return
		}
		lang.flushOut()
		lang.halt(0)
	}

	/** The material survey (client.test.materialScan), a slice of the items a frame. */
	private fun materialScan() {
		val provider = ClientState.activeToolkit!!.materialProvider!!
		if (scanItem == 0 && scanPasses == 0) {
			val classes: HashMap<String, Int> = HashMap()
			for (m in 0 until provider.textureCount()) {
				val key: String = materialClass(m) ?: continue
				classes[key] = (classes[key] ?: 0) + 1
			}
			println("SMOKE SCAN " + provider.textureCount() + " materials, " + classes.size + " classes:")
			for (key in classes.keys.sorted()) {
				println("SMOKE SCAN   " + key + ": " + classes[key] + " materials")
			}
		}
		val objs = TypeLists.OBJ!!
		val start: Long = lang.currentTimeMillis()
		if (scanPasses == 0) {
			while (scanItem < objs.num && lang.currentTimeMillis() - start < 20L) {
				scanOne(scanItem)
				scanItem++
			}
			if (scanItem < objs.num) {
				return
			}
			scanPasses = 1
			sceneFrame = gameFrames
			return
		}
		// the models that were not loaded yet, again, until they are (or a while has passed)
		if (scanPending.size > 0 && gameFrames - sceneFrame < 3000) {
			val retry: ArrayList<Int> = ArrayList(scanPending)
			scanPending.clear()
			for (id in retry) {
				scanOne(id)
			}
			return
		}
		println("SMOKE SCAN items not loaded: " + scanPending.size)
		for (key in scanExamples.keys.sorted()) {
			println("SMOKE SCAN " + key + " -> " + scanExamples[key]!!.joinToString(", "))
		}
		println("SMOKE PASS material scan")
		lang.flushOut()
		lang.halt(0)
	}

	private fun scanOne(id: Int) {
		val type = TypeLists.OBJ!!.list(id) ?: return
		if (type.maleEquip1 == -1 || type.certTemplateId != -1) {
			return
		}
		if (!type.isBodyCached(false)) {
			scanPending.add(id)
			return
		}
		val mesh = type.getBodyMesh(false) ?: return
		val materials = mesh.facesMaterial ?: return
		val seen: HashSet<String> = HashSet()
		for (f in 0 until mesh.numFaces) {
			val m: Int = materials[f].toInt()
			if (m < 0) continue
			val key: String = materialClass(m) ?: continue
			if (!seen.add(key)) continue
			val list: ArrayList<String> = scanExamples.getOrPut(key) { ArrayList() }
			if (list.size < 6) list.add(id.toString() + " " + type.name)
		}
	}

	/** A material's drawing class, e.g. "effect1/1 comb0 mode0"; null for a missing one. */
	private fun materialClass(m: Int): String? {
		val t = ClientState.activeToolkit!!.materialProvider!!.getMetrics(m) ?: return null
		return "effect" + t.effectType + "/" + t.effectParam1 + (if (t.effectParam2 != 0) "/" + t.effectParam2 else "") + " combiner" + t.effectCombiner + " mode" + t.combineMode + (if (t.speedU.toInt() != 0 || t.speedV.toInt() != 0) " scroll" else "") + (if (t.hdr) " hdr" else "") + (if (t.alpha.toInt() != 0) " alpha" + t.alpha else "")
	}

	/** The soak run (client.test.soak), a frame at a time. */
	private fun soak(player: PlayerEntity) {
		if (soakPositions == null) {
			val parsed: Array<String> = DEFAULT_SCENES.javaSplit(";")
			val positions: Array<IntArray> = Array(parsed.size) { IntArray(3) }
			for (i in 0 until parsed.size) {
				val values: Array<String> = parsed[i].substring(parsed[i].indexOf(':') + 1).javaSplit(",")
				positions[i][0] = values[0].javaTrim().toInt()
				positions[i][1] = values[1].javaTrim().toInt()
				positions[i][2] = values[2].javaTrim().toInt()
			}
			soakPositions = positions
			println("SMOKE SOAK start, " + SOAK_MINUTES + " minutes over " + positions.size + " positions: " + memoryReport())
		}
		val positions: Array<IntArray> = soakPositions!!
		if (soakStep == 5) {
			// done: home, so the other checks' references still match
			if (sceneArrived(player, SCENE_HOME[0], SCENE_HOME[1], SCENE_HOME[2])) {
				println("SMOKE PASS soak on toolkit " + GamePreferences.uid!!.toolkit!!.getValue() + " after " + elapsed() + "s, " + soakVisits + " visits: " + memoryReport())
				lang.flushOut()
				lang.halt(0)
			} else if (gameFrames - soakFrame > SCENE_WAIT) {
				fail("soak: could not return home")
			}
			return
		}
		if (soakStep == 0) {
			if (elapsed() >= SOAK_MINUTES * 60L) {
				sendCommand("tele " + SCENE_HOME[0] + " " + SCENE_HOME[1] + " " + SCENE_HOME[2])
				soakStep = 5
				soakFrame = gameFrames
				return
			}
			val at: IntArray = positions[soakIndex]
			sendCommand("tele " + at[0] + " " + at[1] + " " + at[2])
			soakStep = 1
			soakFrame = gameFrames
			return
		}
		val at: IntArray = positions[soakIndex]
		if (soakStep == 1) {
			if (sceneArrived(player, at[0], at[1], at[2])) {
				soakStep = 2
				soakFrame = gameFrames
			} else if (gameFrames - soakFrame > SCENE_WAIT) {
				fail("soak: never arrived at " + at[0] + "," + at[1])
			}
			return
		}
		if (soakStep == 2) {
			val held: Int = gameFrames - soakFrame
			Camera.camAngleY = ((Camera.camAngleY.toInt() + 24) and 0x3fff).toFloat()
			if (held == 60) {
				walkFromMenu(player.movementBaseX[0] + 5, player.movementBaseY[0])
			} else if (held == SOAK_FRAMES / 2) {
				walkFromMenu(player.movementBaseX[0] - 5, player.movementBaseY[0])
			}
			if (held < SOAK_FRAMES) {
				return
			}
			soakVisits++
			soakIndex = (soakIndex + 1) % positions.size
			println("SMOKE SOAK t=" + elapsed() + "s visits=" + soakVisits + " " + memoryReport())
			if (SOAK_TOGGLE > 0 && TOOLKIT >= 0 && soakVisits % SOAK_TOGGLE == 0) {
				switchToolkit(1)
				soakStep = 3
				soakFrame = gameFrames
				return
			}
			soakStep = 0
			return
		}
		if (soakStep == 3 && gameFrames - soakFrame >= 50) {
			switchToolkit(TOOLKIT)
			if (GamePreferences.uid!!.toolkit!!.getValue() != TOOLKIT) {
				fail("soak: toolkit " + TOOLKIT + " did not start again after " + soakVisits + " visits")
			}
			soakStep = 4
			soakFrame = gameFrames
			return
		}
		if (soakStep == 4 && gameFrames - soakFrame >= 50) {
			soakStep = 0
		}
	}

	/**
	 * The screenshot harness (client.test.scenes), a frame at a time: teleport, wait to arrive, hold
	 * the camera, capture and compare; then the map reload and relog captures, then home and report.
	 */
	private fun scenes(player: PlayerEntity) {
		if (sceneList == null) {
			val parsed: Array<String> = (if (SCENES == "default") DEFAULT_SCENES else SCENES).javaSplit(";")
			val list: Array<String?> = arrayOfNulls(parsed.size + 3)
			for (i in 0 until parsed.size) {
				list[i] = parsed[i].javaTrim()
			}
			// the first scene again, after a map reload (away and back) and after a relog
			list[parsed.size] = "reload" + list[0]
			list[parsed.size + 1] = "relog" + list[0]
			// then a loc taken out of the scene, as a server's loc packet would (LocAction.replaceLoc)
			list[parsed.size + 2] = "locchange" + list[0]
			sceneList = list
			// the zoom extra on: a scene's zoom value (the outfits' close-ups) needs it, and at 0 it
			// leaves the view as it was
			ClientFeatures.ZOOM = true
			println("SMOKE scenes: " + parsed.size + " positions, then a map reload and a relog")
		}
		val list: Array<String?> = sceneList!!
		if (sceneIndex >= list.size) {
			if (!sceneArrived(player, SCENE_HOME[0], SCENE_HOME[1], SCENE_HOME[2])) {
				if (sceneStep == 0) {
					sendCommand("tele " + SCENE_HOME[0] + " " + SCENE_HOME[1] + " " + SCENE_HOME[2])
					sceneStep = 1
					sceneFrame = gameFrames
				} else if (gameFrames - sceneFrame > SCENE_WAIT) {
					fail("could not return home to " + SCENE_HOME[0] + "," + SCENE_HOME[1])
				}
				return
			}
			if (sceneFailures > 0) {
				fail(sceneFailures.toString() + " of " + list.size + " scenes differ:" + sceneSummary)
			}
			passLine = "SMOKE PASS scenes on toolkit " + GamePreferences.uid!!.toolkit!!.getValue() + " after " + elapsed() + "s:" + sceneSummary
			if (toolkitSwitched && startToolkit != TOOLKIT) {
				println("SMOKE switching back to toolkit " + startToolkit)
				switchToolkit(startToolkit)
				restoreFrame = frames
				return
			}
			println(passLine)
			lang.flushOut()
			lang.halt(0)
			return
		}
		var entry: String = list[sceneIndex]!!
		val reload: Boolean = entry.startsWith("reload")
		val relog: Boolean = entry.startsWith("relog")
		val locChange: Boolean = entry.startsWith("locchange")
		if (reload) {
			entry = entry.substring(6)
		} else if (relog) {
			entry = entry.substring(5)
		} else if (locChange) {
			entry = entry.substring(9)
		}
		val colon: Int = entry.indexOf(':')
		val name: String = entry.substring(0, colon) + (if (reload) "-reload" else if (relog) "-relog" else if (locChange) "-locchange" else "")
		val reference: String = if (locChange) name else entry.substring(0, colon)
		val values: Array<String> = entry.substring(colon + 1).javaSplit(",")
		val x: Int = values[0].javaTrim().toInt()
		val y: Int = values[1].javaTrim().toInt()
		val level: Int = if (values.size > 2) values[2].javaTrim().toInt() else 0
		val yaw: Int = if (values.size > 3) values[3].javaTrim().toInt() else 0
		val pitch: Int = if (values.size > 4) values[4].javaTrim().toInt() else 1400
		val zoom: Int = if (values.size > 5) values[5].javaTrim().toInt() else 0
		// a scene's own limit, for one whose NPCs wander between runs (toolkit 1 against itself is that noisy)
		sceneLimit = if (values.size > 6) values[6].javaTrim().toInt() else SCENE_MAX_DIFFERENT
		if (sceneStep == 0) {
			if (reload && sceneArrived(player, x, y, level)) {
				// already there: go away first, so coming back builds the map again
				sendCommand("tele " + SCENE_HOME[0] + " " + SCENE_HOME[1] + " " + SCENE_HOME[2])
				sceneFrame = gameFrames
				sceneStep = 4
				return
			}
			if (locChange) {
				if (!sceneArrived(player, x, y, level)) {
					sendCommand("tele " + x + " " + y + " " + level)
					locPending = true
					sceneStep = 1
					sceneFrame = gameFrames
					return
				}
				locRebuildsBefore = RenderStats.chunkRebuilds
				locRemoved = removeNearbyLoc(player)
				println("SMOKE scene " + name + ": removed " + locRemoved)
				sceneStep = 2
				sceneFrame = gameFrames
				return
			}
			if (relog) {
				println("SMOKE scene " + name + ": logging out")
				loginSent = false
				ClientLogout.logout(false)
				sceneStep = 3
				return
			}
			println("SMOKE scene " + name + ": teleporting to " + x + "," + y + "," + level)
			sendCommand("tele " + x + " " + y + " " + level)
			sceneStep = 1
			sceneFrame = gameFrames
			return
		}
		if (sceneStep == 4) {
			if (sceneArrived(player, SCENE_HOME[0], SCENE_HOME[1], SCENE_HOME[2])) {
				sendCommand("tele " + x + " " + y + " " + level)
				println("SMOKE scene " + name + ": back to " + x + "," + y + "," + level)
				sceneStep = 1
				sceneFrame = gameFrames
			} else if (gameFrames - sceneFrame > SCENE_WAIT) {
				fail("scene " + name + ": could not teleport away")
			}
			return
		}
		if (sceneStep == 3) {
			// back in game after the relog (frame() only runs in game); a walk may have left the player
			// elsewhere, so it is sent to the scene again
			println("SMOKE scene " + name + ": logged back in")
			if (!sceneArrived(player, x, y, level)) {
				sendCommand("tele " + x + " " + y + " " + level)
			}
			sceneStep = 1
			sceneFrame = gameFrames
		}
		if (sceneStep == 1) {
			if (!sceneArrived(player, x, y, level)) {
				if (gameFrames - sceneFrame > SCENE_WAIT) {
					fail("scene " + name + ": never arrived at " + x + "," + y + "," + level + " (at " + (player.movementBaseX[0] + MapRegion.sceneBaseX) + "," + (player.movementBaseY[0] + MapRegion.sceneBaseY) + ")")
				}
				return
			}
			sceneStep = 2
			sceneFrame = gameFrames
			// client.test.sceneCommand: sent once the first scene is reached (a map build resets what
			// some commands toggle, e.g. ::occlude, so it cannot go before the teleport)
			if (NPC_DUMP.length > 0 && !sceneCommandSent) {
				npcDump()
			}
			if (SCENE_COMMAND.length > 0 && !sceneCommandSent) {
				sceneCommandSent = true
				println("SMOKE sending ::" + SCENE_COMMAND + " at the first scene")
				sendCommand(SCENE_COMMAND)
			}
			if (locChange && locPending) {
				// arrived after a teleport (a walk had taken the player away): the loc goes now
				locPending = false
				locRebuildsBefore = RenderStats.chunkRebuilds
				locRemoved = removeNearbyLoc(player)
				println("SMOKE scene " + name + ": removed " + locRemoved)
			}
			// an eighth value, slot:item pairs separated by '|': the player dressed in them, on this
			// client only (as client.test.wear); -1 empties a slot
			if (values.size <= 7 && savedParts != null && player.playerModel != null) {
				// a scene without an outfit after one: what the player wore before
				player.playerModel!!.restoreParts(savedParts!!)
				savedParts = null
			}
			if (values.size > 7 && player.playerModel != null) {
				if (savedParts == null) {
					savedParts = player.playerModel!!.copyParts()
				}
				val pairs: Array<String> = values[7].javaTrim().javaSplit("\\|")
				for (pair in pairs) {
					val colon: Int = pair.indexOf(':')
					player.playerModel!!.addAppearanceItem(pair.substring(0, colon).javaTrim().toInt(), pair.substring(colon + 1).javaTrim().toInt(), TypeLists.OBJ)
				}
			}
		}
		// hold the camera where the scene asks, whatever the client's own camera logic does; during a
		// burst it turns client.test.sceneSpin yaw units a frame (moving-camera flicker checks)
		// an outfit's close-up: the toolkit's clock held at 0, so the game's scrolling textures (the fire
		// cape's lava) sit at the same phase on both toolkits (client.kt passes it to the toolkit's tick)
		heldTick = if (values.size > 7) 0 else -1
		if (values.size > 7) {
			// an outfit's close-up: the player held in the first frame of its animation, so both toolkits
			// capture the same pose (the idle sway alone differs by more than a material's change)
			player.movingSequence.currentFrameId = 0
			player.movingSequence.nextFrameId = 0
			player.movingSequence.currentFrameDuration = 0
			holdPose(player.currentSequence)
		}
		Camera.camAngleY = (yaw + burstIndex * SCENE_SPIN).toFloat()
		Camera.camAngleX = pitch.toFloat()
		Camera.cameraYawVelocity = 0.0f
		Camera.cameraPitchVelocity = 0.0f
		Camera.cameraRandomisationA = 0
		Camera.cameraRandomisationV = 0
		Camera.cameraRandomisationX = 0
		Minimap.minimapRotation = 0
		Minimap.minimapZoom = 0
		ClientFeatures.zoomOffset = zoom
		// the fps and memory text changes from run to run
		Culling.wallCullingEnabled = false
		if (gameFrames - sceneFrame < SCENE_FRAMES) {
			return
		}
		// client.test.inspect=true: on the first scene, 634HD's Inspect text (the authoring tool) for the
		// player's tile and a loc next to it, printed
		if (INSPECT && !inspected) {
			inspected = true
			val px: Int = player.movementBaseX[0]
			val pz: Int = player.movementBaseY[0]
			// every loc within INSPECT_RADIUS tiles: walls, decorations, scenery
			val uids: StringBuilder = StringBuilder()
			val seen: HashSet<Long> = HashSet()
			var count: Int = 0
			for (dz in -INSPECT_RADIUS..INSPECT_RADIUS) {
				for (dx in -INSPECT_RADIUS..INSPECT_RADIUS) {
					for (lvl in 0 until com.jagex.game.runetek5.scene.Scene.length) {
						val tiles = com.jagex.game.runetek5.scene.Scene.aClass100ArrayArrayArray3 ?: break
						val tile: com.jagex.game.runetek5.scene.tile.Tile = tiles[lvl]?.get(px + dx)?.get(pz + dz) ?: continue
						val locs: ArrayList<Any?> = arrayListOf(tile.primaryWall, tile.secondaryWall, tile.primaryWallDecoration, tile.secondaryWallDecoration, tile.groundDecoration)
						var node = tile.interactives
						while (node != null) {
							locs.add(node.interactiveObject)
							node = node.child
						}
						for (loc in locs) {
							if (loc is com.jagex.game.runetek5.entity.gameobject.GameObject && count < 120) {
								val uid: Long = (loc.getId().toLong() shl 32) or ((loc.getRotation() and 0x3).toLong() shl 20) or ((loc.getShape() and 0x1F).toLong() shl 14) or ((pz + dz).toLong() shl 7) or (px + dx).toLong()
								// each id, shape and tile once (a big loc sits on several tiles)
								if (seen.add(uid)) {
									uids.append(',').append(uid)
									count++
								}
							}
						}
					}
				}
			}
			println("SMOKE inspect: player tile " + (px + MapRegion.sceneBaseX) + "," + (pz + MapRegion.sceneBaseY) + ", " + count + " locs within " + INSPECT_RADIUS + " tiles")
			// the occluders active this frame (Culling.members) within the radius: type (1/2 walls along
			// x/z, 8/16 diagonals, 4 floors), plane, tile span, scene-unit corners and heights
			val members = Culling.members
			if (members != null) {
				for (m in 0 until Culling.activeOccluderCount) {
					val o = members[m] ?: continue
					if (o.endX < px - INSPECT_RADIUS || o.startX > px + INSPECT_RADIUS || o.endY < pz - INSPECT_RADIUS || o.startY > pz + INSPECT_RADIUS) continue
					println("SMOKE occluder type " + o.type + " plane " + o.plane + " tiles " + (o.startX + MapRegion.sceneBaseX) + ".." + (o.endX + MapRegion.sceneBaseX) + "," + (o.startY + MapRegion.sceneBaseY) + ".." + (o.endY + MapRegion.sceneBaseY) + " x " + o.tileCullingsX.joinToString(",") + " z " + o.tileCullingsZ.joinToString(",") + " y " + o.drawHeights.joinToString(","))
				}
			}
			lang.setProperty("client.test.inspectNow", px.toString() + "," + pz + uids)
			return
		}
		// client.test.sceneBurst=N: N consecutive frames saved first, to measure flicker between frames
		if (burstIndex < SCENE_BURST) {
			if (burstFrame != gameFrames) {
				if (SCENE_WALK && burstIndex % SCENE_WALK_EVERY == 0) {
					// client.test.sceneWalk: the player walks east during the burst, client.test.sceneWalkTiles
					// (default 6) at a time, again every client.test.sceneWalkEvery frames: far enough, it
					// crosses into a map region the client builds anew
					walkFromMenu(player.movementBaseX[0] + SCENE_WALK_TILES, player.movementBaseY[0])
				}
				if (SCENE_BURST_LOC > 0 && burstIndex == SCENE_BURST_LOC) {
					// client.test.sceneBurstLoc=N: a nearby loc taken out at the burst's Nth frame (the scene
					// changes, and 634HD bakes its occlusion again while the player walks)
					println("SMOKE scene " + name + ": removed " + removeNearbyLoc(player) + " at burst frame " + burstIndex)
				}
				burstFrame = gameFrames
				val luma: Double = burstFrame(if (SCENE_BURST_SAVE) "build/smoke/scenes/burst-" + name + "-" + burstIndex + ".png" else null)
				// the game's own roof hiding (MapRegion.handleRoofDisplay): the player stepping under or out of a
				// roof changes what is drawn - a real jump, not a flicker, so that frame is not measured
				val roofs: Long = roofSignature()
				if (burstIndex > 0 && roofs != burstRoofs) {
					burstRoofFrame = burstIndex
					println("SMOKE scene " + name + ": roofs hidden or shown, or the map rebuilt, at burst frame " + burstIndex + " (the next 3 frames not measured)")
				}
				burstRoofs = roofs
				// the scene shows the change a frame or two after the tick that made it
				val roofChanged: Boolean = burstRoofFrame >= 0 && burstIndex - burstRoofFrame <= 3
				if (burstIndex > 0 && !roofChanged) {
					val jump: Double = kotlin.math.abs(luma - burstLuma)
					if (jump > burstMaxJump) {
						burstMaxJump = jump
						burstMaxJumpAt = burstIndex
					}
					if (jump > SCENE_LUMA_JUMP && burstSaved < 4) {
						// the frames either side of the jump, to see what it was
						burstSaved++
						savePixels(burstPrevious!!, burstWidth, burstHeight, "build/smoke/scenes/jump-" + name + "-" + (burstIndex - 1) + ".png")
						savePixels(burstPixels!!, burstWidth, burstHeight, "build/smoke/scenes/jump-" + name + "-" + burstIndex + ".png")
					}
				}
				if (SCENE_LUMA_LOG) {
					println("SMOKE LUMA " + name + " " + burstIndex + " " + format(luma) + " step " + ClientState.mainLogicStep)
				}
				burstLuma = luma
				burstIndex++
			}
			return
		}
		if (SCENE_BURST > 0) {
			// the largest change in the 3D view's brightness from one frame to the next: a darkness pop or
			// a flash shows as a jump well above the few tenths walking and turning make
			println("SMOKE scene " + name + ": burst of " + SCENE_BURST + ", largest brightness jump " + format(burstMaxJump) + " at frame " + burstMaxJumpAt)
			if (burstMaxJump > SCENE_LUMA_JUMP) {
				fail("scene " + name + ": the brightness jumped by " + format(burstMaxJump) + " at burst frame " + burstMaxJumpAt + " (limit " + SCENE_LUMA_JUMP + ")")
			}
			burstMaxJump = 0.0
			burstRoofFrame = -1
		}
		burstIndex = 0
		// client.test.hdCycle: on the first scene, every 634HD switch turned on and off again live
		if (HD_CYCLE && sceneIndex == 0 && !cycleDone) {
			cycleStep()
			return
		}
		// an outfit's close-up compares only the middle third of the view, where the player stands:
		// passers-by at the edges are not the outfit
		compareCentre = values.size > 7
		var result: String = compareScene(name, reference)
		compareCentre = false
		if (locChange) {
			val rebuilt: Int = RenderStats.chunkRebuilds - locRebuildsBefore
			result += ", chunks rebuilt " + rebuilt
			// on 634HD exactly the changed loc's chunk is built again (Toolkit.sceneChanged)
			// (a walk's own loc changes, client.test.sceneBurstLoc, rebuild more: then it is not counted)
			if (GamePreferences.uid!!.toolkit!!.getValue() == ToolkitType.HD && SCENE_BURST_LOC == 0 && (rebuilt < 1 || rebuilt > 2)) {
				sceneFailures++
				result += " (expected 1)"
			}
		}
		sceneSummary += " " + result + ";"
		println("SMOKE scene " + result)
		sceneIndex++
		sceneStep = 0
	}

	private var openFrame: Int = -1

	/** client.test.openAt: where the open:/toggle test teleports to first ("x,y[,level]"). */
	private val OPEN_AT: String = lang.getProperty("client.test.openAt", "")

	private var openAtFrame: Int = -1

	/**
	 * client.test.openMode=fixed|resizable: the window mode the open:/toggle test switches to first
	 * (resizable by default: the server remembers the last mode, and the references are resizable).
	 */
	private val OPEN_MODE: String = lang.getProperty("client.test.openMode", "resizable")

	private var openModeFrame: Int = -1

	/**
	 * client.test.findText=open:N: opens interface N as a modal in the main-screen slot, and after 60 frames saves the frame to build/smoke/open-N.png and stops.
	 */
	private fun openAndCapture() {
		val id: Int = FIND_TEXT.substring(5).toInt()
		// client.test.openAt=x,y,level: the toggle test somewhere else first (an admin account's ::tele)
		if (OPEN_AT.length > 0 && openAtFrame < 0) {
			val at: Array<String> = OPEN_AT.split(",").toTypedArray()
			sendCommand("tele " + at[0] + " " + at[1] + " " + (if (at.size > 2) at[2] else "0"))
			openAtFrame = gameFrames
			return
		}
		if (OPEN_AT.length > 0 && gameFrames - openAtFrame < 200) {
			return
		}
		if (openFrame < 0) {
			// the main-screen slot, where the server opens such interfaces: 548:18 fixed, 746:8 resizable
			val slot: Int = if (ClientState.BASE_WINDOW_ID == 548) 18 else 8
			val parent: Int = (ClientState.BASE_WINDOW_ID shl 16) or slot
			if (id == 742) {
				// Graphics Options as a player opens it, "Graphics Settings" on the Options tab (261:16): the
				// server opens the window (so it also takes a window-mode change) and its load scripts
				// build the stock page the 634HD button goes on
				InterfaceOptionInteract.ifOptionInteract(-1, "Graphics Settings", 1, (261 shl 16) or 16)
			} else {
				openSubInterface(id, parent, SubInterfaceType.MODAL)
			}
			openFrame = gameFrames
			return
		}
		if (gameFrames - openFrame < 60) {
			return
		}
		// client.test.openMode=fixed|resizable: the window mode, which the server changes only while
		// Graphics Options is open, and then opens the window again in the new layout
		if (OPEN_MODE.length > 0 && openModeFrame < 0) {
			val wanted: WindowModeType = if (OPEN_MODE == "fixed") WindowModeType.FIXED else WindowModeType.RESIZABLE
			val graphics: Component? = Interface.getComponent((261 shl 16) or 16)
			println("SMOKE window mode " + WindowMode.getWindowMode() + ", base window " + ClientState.BASE_WINDOW_ID + ", Graphics Settings op enabled " + (graphics != null && InterfaceManager.getComponentSettings(graphics)!!.isOpEnabled(0)))
			if (WindowMode.getWindowMode() !== wanted) {
				sendCommand(if (OPEN_MODE == "fixed") "wm1" else "wm2")
			}
			openModeFrame = gameFrames
			return
		}
		if (OPEN_MODE.length > 0 && gameFrames - openModeFrame < 200) {
			return
		}
		if (openStep == 0) {
			if (OPEN_MODE.length > 0) {
				println("SMOKE window mode now " + WindowMode.getWindowMode() + ", base window " + ClientState.BASE_WINDOW_ID)
				val wantedBase: Int = if (OPEN_MODE == "fixed") 548 else 746
				if (ClientState.BASE_WINDOW_ID != wantedBase) {
					fail("the window mode did not change to " + OPEN_MODE + ": base window " + ClientState.BASE_WINDOW_ID)
				}
			}
			checkImage("-open", "build/smoke/open-" + id + ".png")
			if (id != 742) {
				println("SMOKE PASS opened " + id)
				lang.flushOut()
				lang.halt(0)
			}
			// the stock page, with the 634HD button the client adds to its preset row
			if (!HdOptionsScreen.stockButtonPresent()) {
				fail("no 634HD button in the Graphics Options preset row")
			}
			println("SMOKE 634HD settings before the toggle: " + HdSettings.describe().joinToString("; ") + ", configured " + HdSettings.configured)
			if (!HdOptionsScreen.clickStockButton() || !HdOptionsScreen.open) {
				fail("the 634HD button did not open the 634HD page")
			}
			if (GamePreferences.uid!!.toolkit!!.getValue() == ToolkitType.HD) {
				// a saved default from an earlier run: off first, so the flow below turns it on
				println("SMOKE started on 634HD: turned off first")
				if (!HdOptionsScreen.clickToggle() || GamePreferences.uid!!.toolkit!!.getValue() == ToolkitType.HD) {
					fail("the 634HD row did not turn 634HD off")
				}
			}
			openToolkit = GamePreferences.uid!!.toolkit!!.getValue()
			openStep = 1
			openFrame = gameFrames
			return
		}
		if (openStep == 1) {
			// the page on the toolkit the test started on, then 634HD turned on from its row
			checkImage("-open-page", "build/smoke/open-" + id + "-page.png")
			if (!HdOptionsScreen.clickToggle() || GamePreferences.uid!!.toolkit!!.getValue() != ToolkitType.HD) {
				fail("the 634HD row left the client on toolkit " + GamePreferences.uid!!.toolkit!!.getValue())
			}
			println("SMOKE 634HD settings after the toggle: " + HdSettings.describe().joinToString("; "))
			// turned on with nothing saved: the Medium preset (with switches given, those)
			if (!HdSettings.configured && HdSettings.currentPreset() != "medium") {
				fail("turning 634HD on gave the settings " + HdSettings.currentPreset() + ", not the medium preset")
			}
			openStep = 3
			openFrame = gameFrames
			return
		}
		if (openStep == 5) {
			if (GamePreferences.uid!!.toolkit!!.getValue() != ToolkitType.HD) {
				fail("round " + toggleRound + ": the 634HD row left the client on toolkit " + GamePreferences.uid!!.toolkit!!.getValue())
			}
			checkImage("-open-hd-round", "build/smoke/open-" + id + "-hd-round" + toggleRound + ".png")
			if (!HdOptionsScreen.clickToggle() || !HdOptionsScreen.clickBack()) {
				fail("the 634HD row did not take the second press in round " + toggleRound)
			}
			openStep = 6
			openFrame = gameFrames
			return
		}
		if (openStep == 3) {
			// the page on 634HD: a preset, a row in each of two tabs, the draw distance (live), the interface scale
			checkImage("-open-hd", "build/smoke/open-" + id + "-hd.png")
			if (!HdOptionsScreen.clickPreset("high") || HdSettings.currentPreset() != "high") {
				fail("the High preset did not apply: " + HdSettings.currentPreset())
			}
			// the bloom row's list open (its frame is taken in the next step)
			bloomBefore = HdSettings.valueOf("bloom")
			if (!HdOptionsScreen.openRow("bloom") || !HdOptionsScreen.popupOpen()) {
				fail("a click on the bloom row did not open its list")
			}
			openStep = 8
			openFrame = gameFrames
			return
		}
		if (openStep == 8) {
			checkImage("-open-hd-list", "build/smoke/open-" + id + "-hd-list.png")
			if (!HdOptionsScreen.choosePopupItem() || HdOptionsScreen.popupOpen() || HdSettings.valueOf("bloom") == bloomBefore) {
				fail("a choice in the bloom row's list did not change it")
			}
			// the draw distance row: live, the map built again further out (checked in the next step)
			visibleTilesBefore = SceneVisibleTiles.visibleTiles
			val distance: Double = ClientFeatures.RENDER_DISTANCE
			distanceBefore = distance
			if (!HdOptionsScreen.cycleRow("renderDistance") || ClientFeatures.RENDER_DISTANCE == distance || !HdOptionsScreen.mapRebuildWanted) {
				fail("a choice in the draw distance row did not change it (" + distance + " to " + ClientFeatures.RENDER_DISTANCE + ")")
			}
			val scale: String = HdSettings.valueOf("scale")
			if (!HdOptionsScreen.cycleRow("scale") || HdSettings.valueOf("scale") == scale) {
				fail("a choice in the interface scale row did not change it")
			}
			openStep = 4
			openFrame = gameFrames
			return
		}
		if (openStep == 4) {
			// further out, or nearer when the choice wrapped round from the largest distance to 1
			val further: Boolean = ClientFeatures.RENDER_DISTANCE > distanceBefore
			if (if (further) SceneVisibleTiles.visibleTiles <= visibleTilesBefore else SceneVisibleTiles.visibleTiles >= visibleTilesBefore) {
				fail("the map was not built again at draw distance " + ClientFeatures.RENDER_DISTANCE + " (from " + distanceBefore + "): " + visibleTilesBefore + " visible tiles, now " + SceneVisibleTiles.visibleTiles)
			}
			if (MapRegion.sceneBaseX != (MapRegion.mapCenterChunkX - (MapRegion.sceneSizeX shr 4)) * 8 || MapRegion.sceneBaseY != (MapRegion.mapBaseChunkY - (MapRegion.sceneSizeY shr 4)) * 8) {
				fail("the scene's base " + MapRegion.sceneBaseX + "," + MapRegion.sceneBaseY + " was not moved for its size " + MapRegion.sceneSizeX)
			}
			println("SMOKE draw distance " + ClientFeatures.RENDER_DISTANCE + ": " + visibleTilesBefore + " to " + SceneVisibleTiles.visibleTiles + " visible tiles, base " + MapRegion.sceneBaseX + "," + MapRegion.sceneBaseY)
			checkImage("-open-hd-settings-high", "build/smoke/open-" + id + "-hd-settings-high.png")
			// Back: the stock page again, its 634HD button back on the next tick
			if (!HdOptionsScreen.clickBack() || HdOptionsScreen.open) {
				fail("Back did not leave the 634HD page")
			}
			openStep = 7
			openFrame = gameFrames
			return
		}
		if (openStep == 7) {
			if (!HdOptionsScreen.stockButtonPresent()) {
				fail("after Back, no 634HD button in the Graphics Options preset row")
			}
			checkImage("-open-hd-back", "build/smoke/open-" + id + "-hd-back.png")
			// 634HD off again, from the page
			if (!HdOptionsScreen.clickStockButton() || !HdOptionsScreen.clickToggle() || !HdOptionsScreen.clickBack()) {
				fail("the 634HD page did not take the press that turns 634HD off")
			}
			openStep = 2
			openFrame = gameFrames
			return
		}
		if (GamePreferences.uid!!.toolkit!!.getValue() != openToolkit) {
			fail("turning 634HD off left the client on toolkit " + GamePreferences.uid!!.toolkit!!.getValue() + ", not " + openToolkit)
		}
		// client.test.toggleRounds: on and off again, more times (a second 634HD in one session)
		if (toggleRound < TOGGLE_ROUNDS) {
			toggleRound++
			println("SMOKE 634HD toggle round " + toggleRound + " of " + TOGGLE_ROUNDS)
			if (!HdOptionsScreen.clickStockButton() || !HdOptionsScreen.clickToggle()) {
				fail("the 634HD page did not take the press in round " + toggleRound)
			}
			openStep = 5
			openFrame = gameFrames
			return
		}
		println("SMOKE PASS 634HD toggle in the Graphics Options: " + openToolkit + " to 6 (medium preset) and back" + (if (TOGGLE_ROUNDS > 1) ", " + TOGGLE_ROUNDS + " rounds" else "") + "; 634HD page: presets, rows, Back")
		lang.flushOut()
		lang.halt(0)
	}

	private var openStep: Int = 0

	private var bloomBefore: String = ""

	private val TOGGLE_ROUNDS: Int = lang.getInteger("client.test.toggleRounds", 1)

	private var visibleTilesBefore: Int = 0

	private val WEAR: String = lang.getProperty("client.test.wear", "")

	private val CARRY: String = lang.getProperty("client.test.carry", "")

	private var carried: Boolean = false

	/** Whether client.test.wear's items are in the worn container yet. */
	private var wornContainer: Boolean = false

	/**
	 * client.test.materialScan=true: instead of the checks, lists the material classes in the cache
	 * (effect, its parameter, combiner, combine mode, scrolling, HDR, alpha) and, for each, a few
	 * wearable items whose body models use it: what client.test.wear should dress the player in to
	 * cover every way a material draws.
	 */
	private val MATERIAL_SCAN: Boolean = lang.getBoolean("client.test.materialScan")

	private var scanItem: Int = 0

	private var scanPasses: Int = 0

	private val scanExamples: HashMap<String, ArrayList<String>> = HashMap()

	private val scanPending: ArrayList<Int> = ArrayList()

	private var worn: Boolean = false

	private var compareCentre: Boolean = false

	/** The loc-change scene teleported first: its loc is taken out on arrival. */
	private var locPending: Boolean = false

	private val INSPECT: Boolean = lang.getBoolean("client.test.inspect")

	/** client.test.inspectRadius: how far around the player the inspect check lists locs (default 6 tiles). */
	private val INSPECT_RADIUS: Int = lang.getInteger("client.test.inspectRadius", 6)

	private var inspected: Boolean = false

	/** What the player wore before the first scene with an outfit. */
	private var savedParts: IntArray? = null

	private val CHARACTER_SHADOWS: Int = lang.getInteger("client.test.characterShadows", -1)

	private var distanceBefore: Double = 0.0

	/** Set by a check that wants the map built again (MapRebuild, a root-package class: the hook does it). */
	@JvmField
	var rebuildMap: Boolean = false

	/**
	 * The toolkit's clock (its tick) held at this value during an outfit's close-up, -1 to let it run:
	 * the game's scrolling textures then sit at the same phase on every toolkit (client.kt).
	 */
	@JvmField
	var heldTick: Int = -1

	private var toggleRound: Int = 1

	private var openToolkit: Int = -1

	private fun findText() {
		if (FIND_TEXT.startsWith("textures:")) {
			// writes every cache texture (or those listed, comma-separated) as build/textures/<id>.png at
			// 128x128 as the toolkits make them, with its metrics on a line each: 634HD's material maps
			// are derived from these (tools/make_material_maps.py)
			val provider = ClientState.activeToolkit!!.materialProvider!!
			val wanted: String = FIND_TEXT.substring(9)
			var written: Int = 0
			io.File("build/textures").mkdirs()
			for (id in 0 until provider.textureCount()) {
				if (wanted.isNotEmpty() && ("," + wanted + ",").indexOf("," + id + ",") < 0) continue
				val metrics = try { provider.getMetrics(id) } catch (ex: Exception) { null } ?: continue
				if (!provider.textureAvailable(id)) continue
				val size: Int = if (metrics.isSmall) 64 else 128
				val pixels: IntArray = provider.getPixels(false, id, 0.7f, size, size) ?: continue
				val image: BufferedImage = BufferedImage(size, size, awt.image.BUFFERED_IMAGE_TYPE_INT_RGB)
				for (y in 0 until size) {
					for (x in 0 until size) {
						image.setRGB(x, y, pixels[y * size + x])
					}
				}
				writePng(image, io.File("build/textures/" + id + ".png"))
				println("SMOKE TEXTURE " + id + " size " + size + " effect " + metrics.effectType + " param " + metrics.effectParam1 + " combiner " + metrics.effectCombiner + " op " + metrics.combineMode + " scroll " + metrics.speedU + "," + metrics.speedV + " hdr " + metrics.hdr)
				written++
			}
			println("SMOKE PASS " + written + " textures")
			lang.flushOut()
			lang.halt(0)
		}
		if (FIND_TEXT.startsWith("locmodels:")) {
			// prints every loc type, named or not, that shares a model with a loc whose name matches a
			// regular expression: the unnamed decorations (wall torches) that look like a named loc
			val pattern: Regex = Regex(FIND_TEXT.substring(10), RegexOption.IGNORE_CASE)
			val models: HashMap<Int, String> = HashMap()
			val types: Array<LocType?> = arrayOfNulls(80000)
			for (id in 0 until 80000) {
				try {
					types[id] = TypeLists.LOC!!.list(id)
				} catch (ex: Exception) {
					continue
				}
				val name: String? = types[id]!!.name
				if (name != null && name != "null" && pattern.containsMatchIn(name)) {
					// animations too, as -1 - id: an unnamed torch has its own model but the same flames
					val animations: IntArray? = types[id]!!.animations
					if (animations != null) {
						for (animation in animations) {
							if (animation >= 0) models[-1 - animation] = name
						}
					}
					val ids: Array<IntArray?> = types[id]!!.hdModelIds() ?: continue
					for (shape in ids) {
						if (shape == null) continue
						for (model in shape) models[model] = name
					}
				}
			}
			var found: Int = 0
			for (id in 0 until 80000) {
				val ids: Array<IntArray?> = types[id]?.hdModelIds() ?: continue
				var match: String? = null
				val animations: IntArray? = types[id]!!.animations
				if (animations != null) {
					for (animation in animations) {
						if (animation >= 0 && models[-1 - animation] != null) match = models[-1 - animation]
					}
				}
				for (shape in ids) {
					if (shape == null) continue
					for (model in shape) {
						if (models[model] != null) match = models[model]
					}
				}
				if (match != null) {
					println("SMOKE LOC " + id + " " + types[id]!!.name + " like " + match)
					found++
				}
			}
			println("SMOKE PASS " + found + " types")
			lang.flushOut()
			lang.halt(0)
		}
		if (FIND_TEXT.startsWith("locs:") || FIND_TEXT.startsWith("npcs:")) {
			// prints the loc (or NPC) types whose name matches a regular expression, for 634HD's data files
			val npcs: Boolean = FIND_TEXT.startsWith("npcs:")
			val pattern: Regex = Regex(FIND_TEXT.substring(5), RegexOption.IGNORE_CASE)
			var found: Int = 0
			for (id in 0 until (if (npcs) 20000 else 80000)) {
				val name: String?
				try {
					name = if (npcs) TypeLists.NPC!!.list(id).name else TypeLists.LOC!!.list(id).name
				} catch (ex: Exception) {
					continue
				}
				if (name != null && name != "null" && pattern.containsMatchIn(name)) {
					println("SMOKE " + (if (npcs) "NPC " else "LOC ") + id + " " + name)
					found++
				}
			}
			println("SMOKE PASS " + found + " types")
			lang.flushOut()
			lang.halt(0)
		}
		if (FIND_TEXT.startsWith("struct:")) {
			// prints every struct string param holding the word (keys 0..4095)
			val word: String = FIND_TEXT.substring(7).lowercase()
			for (id in 0 until 8000) {
				val type: com.jagex.game.runetek5.cache.node.lru.StructType = TypeLists.STRUCT!!.list(id) ?: continue
				for (key in 0 until 4096) {
					val value: String?
					try {
						value = type.getStringParam("\u0000", key)
					} catch (ex: ClassCastException) {
						continue
					}
					if (value != null && value != "\u0000" && value.lowercase().contains(word)) {
						println("SMOKE STRUCT " + id + " param " + key + " = \"" + value + "\"")
					}
				}
			}
			println("SMOKE PASS searched structs")
			lang.flushOut()
			lang.halt(0)
		}
		if (FIND_TEXT.startsWith("enum:")) {
			// prints every enum with a string value holding the word, with all its entries
			val word: String = FIND_TEXT.substring(5).lowercase()
			for (id in 0 until 20000) {
				val type: com.jagex.game.runetek5.config.enumtype.EnumType = TypeLists.ENUM!!.list(id) ?: continue
				val table: com.jagex.game.runetek5.core.datastruct.key.IterableHashTable = type.parameters ?: continue
				var match: Boolean = false
				var node: com.jagex.game.runetek5.cache.node.Node? = table.first()
				while (node != null) {
					if (node is com.jagex.game.runetek5.cache.node.StringNode && node.value != null && node.value!!.lowercase().contains(word)) {
						match = true
					}
					node = table.next()
				}
				if (!match) {
					continue
				}
				node = table.first()
				while (node != null) {
					println("SMOKE ENUM " + id + " key " + node.key + " = " + (if (node is com.jagex.game.runetek5.cache.node.StringNode) "\"" + node.value + "\"" else type.getInt(node.key.toInt()).toString()))
					node = table.next()
				}
			}
			println("SMOKE PASS searched enums")
			lang.flushOut()
			lang.halt(0)
		}
		if (FIND_TEXT.startsWith("cs2op:")) {
			// prints every script that uses one of the given opcodes (comma separated)
			val wanted: Array<String> = FIND_TEXT.substring(6).javaSplit(",")
			val groups: Int = com.jagex.game.runetek5.config.Resource.CS2!!.getCapacity()
			for (id in 0 until groups) {
				val script: com.jagex.game.runetek5.cache.node.lru.ClientScript = com.jagex.game.runetek5.cache.node.lru.ClientScript.getClientScript(id) ?: continue
				val ops: IntArray = script.operations ?: continue
				for (i in 0 until ops.size) {
					for (w in 0 until wanted.size) {
						if (ops[i] == wanted[w].toInt()) {
							println("SMOKE CS2OP script " + id + " uses " + ops[i] + " at " + i)
						}
					}
				}
			}
			println("SMOKE PASS searched " + groups + " scripts")
			lang.flushOut()
			lang.halt(0)
		}
		if (FIND_TEXT.startsWith("cs2:")) {
			// prints a script's instructions (opcode, int operand, string operand) and every script it calls
			val pending: IntArray = IntArray(256)
			val done: BooleanArray = BooleanArray(65536)
			var count: Int = 1
			pending[0] = FIND_TEXT.substring(4).toInt()
			while (count > 0) {
				val id: Int = pending[--count]
				if (done[id]) {
					continue
				}
				done[id] = true
				val script: com.jagex.game.runetek5.cache.node.lru.ClientScript = com.jagex.game.runetek5.cache.node.lru.ClientScript.getClientScript(id) ?: continue
				val ops: IntArray = script.operations!!
				println("SMOKE CS2 script " + id)
				for (i in 0 until ops.size) {
					println("SMOKE CS2 " + id + "." + i + " op " + ops[i] + " int " + script.intOpValues!![i] + (if (script.stringOpValues != null && script.stringOpValues!![i] != null) " str \"" + script.stringOpValues!![i] + "\"" else ""))
					if (ops[i] == 40 && count < pending.size) {
						pending[count++] = script.intOpValues!![i]
					}
				}
			}
			println("SMOKE PASS dumped scripts")
			lang.flushOut()
			lang.halt(0)
		}
		if (FIND_TEXT.startsWith("if:")) {
			dumpInterface(FIND_TEXT.substring(3).toInt())
			return
		}
		val word: String = FIND_TEXT.lowercase()
		var found: Int = 0
		var missing: Int = 0
		val count: Int = Interface.interfaces!!.size
		for (id in 0 until count) {
			if (!InterfaceManager.loadInterface(id)) {
				missing++
				continue
			}
			val components: Array<Component?> = Interface.interfaces!![id] ?: continue
			for (i in 0 until components.size) {
				val component: Component = components[i] ?: continue
				val text: String? = component.text
				if (text != null && text.lowercase().contains(word)) {
					println("SMOKE TEXT " + id + ":" + i + " \"" + text + "\" parent " + (component.parentId shr 16) + ":" + (component.parentId and 0xFFFF))
					found++
				}
			}
		}
		println("SMOKE PASS findText \"" + FIND_TEXT + "\": " + found + " components, " + missing + " interfaces not loaded")
		lang.flushOut()
		lang.halt(0)
	}

	/** client.test.findText=if:N: prints every component of interface N with what a hook needs. */
	private fun dumpInterface(id: Int) {
		if (!InterfaceManager.loadInterface(id)) {
			fail("interface " + id + " is not loaded")
		}
		val components: Array<Component?> = Interface.interfaces!![id]!!
		for (i in 0 until components.size) {
			val c: Component = components[i] ?: continue
			println("SMOKE IF " + id + ":" + i + " type " + c.type + " parent " + (c.parentId and 0xFFFF) + " text \"" + c.text + "\" sprite " + c.spriteId + " name " + c.name +
				" onClick " + script(c.onClick) + " onLoad " + script(c.onLoadScript) + " onVarp " + script(c.onVarpTransmit) + " onMouseOver " + script(c.onMouseOver))
		}
		println("SMOKE PASS dumped interface " + id + ", " + components.size + " components")
		lang.flushOut()
		lang.halt(0)
	}

	private fun script(args: Array<Any?>?): String {
		if (args == null) {
			return "-"
		}
		var out: String = "["
		for (i in 0 until args.size) {
			out += (if (i > 0) "," else "") + args[i]
		}
		return out + "]"
	}

	private var locRebuildsBefore: Int = 0

	private var locRemoved: String = ""

	/** Takes the first static scenery loc within 5 tiles of the player out of the scene; what it took. */
	private fun removeNearbyLoc(player: PlayerEntity): String {
		val px: Int = player.movementBaseX[0]
		val pz: Int = player.movementBaseY[0]
		for (dz in -5..5) {
			for (dx in -5..5) {
				val tile: com.jagex.game.runetek5.scene.tile.Tile = com.jagex.game.runetek5.scene.Scene.getTile(0, px + dx, pz + dz) ?: continue
				val container: com.jagex.game.runetek5.entity.InteractiveEntityContainer = tile.interactives ?: continue
				if (container.interactiveObject is com.jagex.game.runetek5.entity.StaticLocation) {
					com.jagex.game.runetek5.scene.LocAction.replaceLoc(px + dx, 2, -1, -1, 10, pz + dz, 0, 0)
					return "the scenery at " + (px + dx) + "," + (pz + dz)
				}
			}
		}
		return "nothing (no scenery nearby)"
	}

	private fun holdPose(sequence: com.jagex.game.runetek5.config.sequence.PathingEntitySequence) {
		sequence.currentFrameId = 0
		sequence.nextFrameId = 0
		sequence.currentFrameDuration = 0
	}

	private fun sceneArrived(player: PlayerEntity, x: Int, y: Int, level: Int): Boolean {
		return player.movementBaseX[0] + MapRegion.sceneBaseX == x && player.movementBaseY[0] + MapRegion.sceneBaseY == y && PlayerUpdate.currentLevel == level
	}

	/**
	 * Saves the frame as scene-<name>-toolkit<id>.png and compares it with the reference pixel by
	 * pixel, saving a diff image beside it when any pixel differs. Returns a summary.
	 */
	/** One burst frame: its 3D view's mean brightness (interface edges left out), saved to [path] if given. */
	private fun burstFrame(path: String?): Double {
		val toolkit: Toolkit = ClientState.activeToolkit!!
		val size: IntArray = IntArray(2)
		toolkit.accumulateViewportDimensions(size)
		val pixels: IntArray = toolkit.na(0, 0, size[0], size[1])!!
		burstPrevious = burstPixels
		burstPixels = pixels
		burstWidth = size[0]
		burstHeight = size[1]
		if (path != null) {
			val shot: BufferedImage = BufferedImage(size[0], size[1], awt.image.BUFFERED_IMAGE_TYPE_INT_RGB)
			shot.setRGB(0, 0, size[0], size[1], pixels, 0, size[0])
			try {
				val file: File = File(path)
				file.getParentFile()!!.mkdirs()
				writePng(shot, file)
			} catch (ex: Exception) {
				fail("cannot save " + path + ": " + ex)
			}
		}
		// the 3D view without the minimap and the side panel (right), the chat box (bottom) and the top line
		val right: Int = size[0] - 260
		val bottom: Int = size[1] - 170
		var sum: Long = 0L
		var count: Int = 0
		var y: Int = 40
		while (y < bottom) {
			var x: Int = 0
			while (x < right) {
				val rgb: Int = pixels[y * size[0] + x]
				sum += (((rgb shr 16) and 0xFF) * 299 + ((rgb shr 8) and 0xFF) * 587 + (rgb and 0xFF) * 114).toLong()
				count++
				x += 2
			}
			y += 2
		}
		return if (count == 0) 0.0 else sum.toDouble() / count.toDouble() / 1000.0
	}

	private fun savePixels(pixels: IntArray, width: Int, height: Int, path: String) {
		val shot: BufferedImage = BufferedImage(width, height, awt.image.BUFFERED_IMAGE_TYPE_INT_RGB)
		shot.setRGB(0, 0, width, height, pixels, 0, width)
		try {
			val file: File = File(path)
			file.getParentFile()!!.mkdirs()
			writePng(shot, file)
		} catch (ex: Exception) {
			fail("cannot save " + path + ": " + ex)
		}
	}

	private fun saveFrame(path: String) {
		val toolkit: Toolkit = ClientState.activeToolkit!!
		val size: IntArray = IntArray(2)
		toolkit.accumulateViewportDimensions(size)
		val pixels: IntArray = toolkit.na(0, 0, size[0], size[1])!!
		val shot: BufferedImage = BufferedImage(size[0], size[1], awt.image.BUFFERED_IMAGE_TYPE_INT_RGB)
		shot.setRGB(0, 0, size[0], size[1], pixels, 0, size[0])
		val file: File = File(path)
		try {
			file.getParentFile()!!.mkdirs()
			writePng(shot, file)
		} catch (ex: Exception) {
			fail("cannot save " + file + ": " + ex)
		}
	}

	private fun compareScene(name: String, referenceName: String): String {
		val toolkit: Toolkit = ClientState.activeToolkit!!
		val size: IntArray = IntArray(2)
		toolkit.accumulateViewportDimensions(size)
		val width: Int = size[0]
		val height: Int = size[1]
		val pixels: IntArray = toolkit.na(0, 0, width, height)!!
		val toolkitId: Int = GamePreferences.uid!!.toolkit!!.getValue()
		val shot: BufferedImage = BufferedImage(width, height, awt.image.BUFFERED_IMAGE_TYPE_INT_RGB)
		shot.setRGB(0, 0, width, height, pixels, 0, width)
		val shotFile: File = File("build/smoke/scenes/scene-" + name + "-toolkit" + toolkitId + ".png")
		try {
			shotFile.getParentFile()!!.mkdirs()
			writePng(shot, shotFile)
		} catch (ex: Exception) {
			fail("cannot save " + shotFile + ": " + ex)
		}
		if (spread(blocks(shot)) < 8.0) {
			sceneFailures++
			return name + " blank"
		}
		if (REFERENCE.length == 0) {
			return name + " saved"
		}
		val referenceToolkit: Int = if (SCENE_REFERENCE_TOOLKIT >= 0) SCENE_REFERENCE_TOOLKIT else toolkitId
		val referenceFile: File = File(REFERENCE, "scene-" + referenceName + "-toolkit" + referenceToolkit + ".png")
		if (!referenceFile.isFile()) {
			return name + " saved, no reference " + referenceFile.getName()
		}
		val expected: BufferedImage?
		try {
			expected = readImage(referenceFile)
		} catch (ex: Exception) {
			fail("cannot read " + referenceFile + ": " + ex)
			return ""
		}
		if (expected!!.getWidth() != width || expected.getHeight() != height) {
			sceneFailures++
			return name + " size " + width + "x" + height + " but the reference is " + expected.getWidth() + "x" + expected.getHeight()
		}
		val diff: IntArray = IntArray(width * height)
		var different: Int = 0
		var compared: Int = 0
		for (y in 0 until height) {
			for (x in 0 until width) {
				val a: Int = pixels[y * width + x]
				if (compareCentre && (x < width / 3 || x >= width * 2 / 3 || y >= height * 4 / 5)) {
					diff[y * width + x] = 0
					continue
				}
				compared++
				val b: Int = expected.getRGB(x, y)
				val dr: Int = kotlin.math.abs((a shr 16 and 0xFF) - (b shr 16 and 0xFF))
				val dg: Int = kotlin.math.abs((a shr 8 and 0xFF) - (b shr 8 and 0xFF))
				val db: Int = kotlin.math.abs((a and 0xFF) - (b and 0xFF))
				if (dr > SCENE_PIXEL_TOLERANCE || dg > SCENE_PIXEL_TOLERANCE || db > SCENE_PIXEL_TOLERANCE) {
					different++
					diff[y * width + x] = 0xFF0000
				} else {
					diff[y * width + x] = (a shr 2) and 0x3F3F3F
				}
			}
		}
		val perMille: Int = (different.toLong() * 1000L / kotlin.math.max(compared, 1).toLong()).toInt()
		if (different > 0) {
			val diffImage: BufferedImage = BufferedImage(width, height, awt.image.BUFFERED_IMAGE_TYPE_INT_RGB)
			diffImage.setRGB(0, 0, width, height, diff, 0, width)
			try {
				writePng(diffImage, File("build/smoke/scenes/scene-" + name + "-toolkit" + toolkitId + "-diff.png"))
			} catch (ex: Exception) {
				fail("cannot save the diff image: " + ex)
			}
		}
		val ok: Boolean = perMille <= sceneLimit
		if (!ok) {
			sceneFailures++
		}
		return name + (if (ok) " ok " else " DIFFERS ") + perMille + "/1000 pixels against " + referenceFile.getName()
	}

	/**
	 * Reads the frame just drawn, saves it, and fails the test if it is blank or too far from the
	 * reference. Returns a summary for the pass line.
	 */
	private fun checkImage(suffix: String, shotPath: String): String? {
		val toolkit: Toolkit? = ClientState.activeToolkit
		val size: IntArray = IntArray(2)
		toolkit!!.accumulateViewportDimensions(size)
		val width: Int = size[0]
		val height: Int = size[1]
		val pixels: IntArray? = toolkit.na(0, 0, width, height)
		val shot: BufferedImage = BufferedImage(width, height, awt.image.BUFFERED_IMAGE_TYPE_INT_RGB)
		shot.setRGB(0, 0, width, height, pixels, 0, width)
		val toolkitId: Int = GamePreferences.uid!!.toolkit!!.getValue()
		try {
			val file: File = File(shotPath)
			if (file.getParentFile() != null) {
				file.getParentFile()!!.mkdirs()
			}
			writePng(shot, file)
		} catch (ex: Exception) {
			fail("cannot save " + SHOT + ": " + ex)
		}
		val blocks: DoubleArray = blocks(shot)
		val detail: Double = spread(blocks)
		if (detail < 8.0) {
			fail("the frame is blank: block brightness varies by " + format(detail) + " (toolkit " + toolkitId + ", " + width + "x" + height + ", saved to " + shotPath + ")")
		}
		val summary: String = "toolkit " + toolkitId + ", " + width + "x" + height + ", detail " + format(detail)
		if (REFERENCE.length == 0) {
			return summary + ", no reference given"
		}
		// a check may keep several references taken in different runs (smoke-toolkit<id><suffix>.png,
		// then .2.png, .3.png, ...): the guide wanders, and a frame need only be close to one of them
		var reference: File? = null
		var difference: Double = Double.MAX_VALUE
		var detailDifference: Double = Double.MAX_VALUE
		val shotContrast: DoubleArray = contrast(shot)
		var n: Int = 1
		while (true) {
			// client.test.sceneReferenceToolkit: another toolkit's references (634HD against OpenGL's)
			val referenceToolkit: Int = if (SCENE_REFERENCE_TOOLKIT >= 0) SCENE_REFERENCE_TOOLKIT else toolkitId
			val candidate: File = File(REFERENCE, "smoke-toolkit" + referenceToolkit + suffix + (if (n == 1) "" else "." + n) + ".png")
			if (!candidate.isFile()) {
				break
			}
			val expected: BufferedImage?
			try {
				expected = readImage(candidate)
			} catch (ex: Exception) {
				fail("cannot read " + candidate + ": " + ex)
				return null
			}
			val colour: Double = difference(blocks, blocks(expected))
			val contrastDifference: Double = difference(shotContrast, contrast(expected))
			if (kotlin.math.max(colour / TOLERANCE, contrastDifference / DETAIL_TOLERANCE) < kotlin.math.max(difference / TOLERANCE, detailDifference / DETAIL_TOLERANCE)) {
				reference = candidate
				difference = colour
				detailDifference = contrastDifference
			}
			n++
		}
		if (reference == null) {
			return summary + ", no reference at " + File(REFERENCE, "smoke-toolkit" + (if (SCENE_REFERENCE_TOOLKIT >= 0) SCENE_REFERENCE_TOOLKIT else toolkitId) + suffix + ".png")
		}
		if (difference > TOLERANCE || detailDifference > DETAIL_TOLERANCE) {
			fail("the frame differs from its closest reference, " + reference + ": colour by " + format(difference) + " (at most " + TOLERANCE + "), detail by " + format(detailDifference) + " (at most " + DETAIL_TOLERANCE + "); " + summary + ", saved to " + shotPath)
		}
		return summary + ", from the reference " + reference.getName() + ": colour " + format(difference) + ", detail " + format(detailDifference)
	}

	/**
	 * The mean red, green and blue of each of BLOCKS_X by BLOCKS_Y blocks, row by row.
	 */
	private fun blocks(image: BufferedImage?): DoubleArray {
		val out: DoubleArray = DoubleArray(BLOCKS_X * BLOCKS_Y * 3)
		val width: Int = image!!.getWidth()
		val height: Int = image.getHeight()
		for (by in 0 until BLOCKS_Y) {
			for (bx in 0 until BLOCKS_X) {
				val x0: Int = bx * width / BLOCKS_X
				val x1: Int = (bx + 1) * width / BLOCKS_X
				val y0: Int = by * height / BLOCKS_Y
				val y1: Int = (by + 1) * height / BLOCKS_Y
				var red: Long = 0L
				var green: Long = 0L
				var blue: Long = 0L
				var count: Int = 0
				for (y in y0 until y1) {
					for (x in x0 until x1) {
						val rgb: Int = image.getRGB(x, y)
						red += (rgb shr 16 and 0xFF).toLong()
						green += (rgb shr 8 and 0xFF).toLong()
						blue += (rgb and 0xFF).toLong()
						count++
					}
				}
				val at: Int = (by * BLOCKS_X + bx) * 3
				if (count > 0) {
					out[at] = red.toDouble() / count
					out[at + 1] = green.toDouble() / count
					out[at + 2] = blue.toDouble() / count
				}
			}
		}
		return out
	}

	/**
	 * The spread of brightness inside each of BLOCKS_X by BLOCKS_Y blocks, row by row: textures and
	 * edges raise it, blur and blank areas lower it.
	 */
	private fun contrast(image: BufferedImage?): DoubleArray {
		val out: DoubleArray = DoubleArray(BLOCKS_X * BLOCKS_Y)
		val width: Int = image!!.getWidth()
		val height: Int = image.getHeight()
		for (by in 0 until BLOCKS_Y) {
			for (bx in 0 until BLOCKS_X) {
				var sum: Double = 0.0
				var squares: Double = 0.0
				var count: Int = 0
				for (y in by * height / BLOCKS_Y until (by + 1) * height / BLOCKS_Y) {
					for (x in bx * width / BLOCKS_X until (bx + 1) * width / BLOCKS_X) {
						val rgb: Int = image.getRGB(x, y)
						val brightness: Double = ((rgb shr 16 and 0xFF) * 299 + (rgb shr 8 and 0xFF) * 587 + (rgb and 0xFF) * 114) / 1000.0
						sum += brightness
						squares += brightness * brightness
						count++
					}
				}
				if (count > 0) {
					val mean: Double = sum / count
					out[by * BLOCKS_X + bx] = kotlin.math.sqrt(kotlin.math.max(0.0, squares / count - mean * mean))
				}
			}
		}
		return out
	}

	/**
	 * The standard deviation of the blocks' brightness: near 0 for a blank frame.
	 */
	private fun spread(blocks: DoubleArray): Double {
		val n: Int = blocks.size / 3
		var sum: Double = 0.0
		var squares: Double = 0.0
		for (i in 0 until n) {
			val brightness: Double = (blocks[i * 3] + blocks[i * 3 + 1] + blocks[i * 3 + 2]) / 3.0
			sum += brightness
			squares += brightness * brightness
		}
		val mean: Double = sum / n
		return kotlin.math.sqrt(kotlin.math.max(0.0, squares / n - mean * mean))
	}

	private fun difference(a: DoubleArray, b: DoubleArray): Double {
		var total: Double = 0.0
		for (i in 0 until a.size) {
			total += kotlin.math.abs(a[i] - b[i])
		}
		return total / a.size
	}

	/**
	 * Runs a console command as typed, through ClientCommandProcessor.executeCommand.
	 */
	/** client.test.npcDump: an NPC type's meshes, face by face where a face is translucent. */
	private fun npcDump() {
		for (idText in NPC_DUMP.javaSplit(",")) {
			val id: Int = idText.javaTrim().toIntOrNull() ?: continue
			val type = TypeLists.NPC!!.list(id)
			val models: IntArray? = type.hdModelIds()
			val out: StringBuilder = StringBuilder("SMOKE NPC " + id + " \"" + type.name + "\" size " + type.size + " models " + (models?.joinToString(" ") ?: "none") + "\n")
			if (models != null) {
				for (m in models) {
					if (m == -1) continue
					val mesh = try { com.jagex.game.runetek5.graphics.model.Mesh.fromResource(TypeLists.NPC!!.meshes, m) } catch (ex: Exception) { null }
					if (mesh == null) {
						out.append("  model " + m + ": not loaded\n")
						continue
					}
					val ys: IntArray = mesh.verticesY!!
					var minY: Int = Int.MAX_VALUE
					var maxY: Int = Int.MIN_VALUE
					for (v in 0 until mesh.numVertices) {
						if (ys[v] < minY) minY = ys[v]
						if (ys[v] > maxY) maxY = ys[v]
					}
					out.append("  model " + m + ": " + mesh.numFaces + " faces, y " + minY + ".." + maxY + "\n")
					// the faces by material: how many, their height range, and the material's effect (type,
					// combiner, alpha, hdr, scroll), from the toolkit's material provider
					val byMaterial = HashMap<Int, IntArray>()
					for (f in 0 until mesh.numFaces) {
						val material: Int = if (mesh.facesMaterial == null) -1 else mesh.facesMaterial!![f].toInt()
						val entry: IntArray = byMaterial.getOrPut(material) { intArrayOf(0, Int.MAX_VALUE, Int.MIN_VALUE) }
						entry[0]++
						for (v in intArrayOf(mesh.facesA!![f].toInt(), mesh.facesB!![f].toInt(), mesh.facesC!![f].toInt())) {
							if (ys[v] < entry[1]) entry[1] = ys[v]
							if (ys[v] > entry[2]) entry[2] = ys[v]
						}
					}
					for (material in byMaterial.keys.sorted()) {
						val entry: IntArray = byMaterial[material]!!
						out.append("    material " + material + ": " + entry[0] + " faces, y " + entry[1] + ".." + entry[2])
						if (material >= 0) {
							val metrics = ClientState.activeToolkit?.materialProvider?.getMetrics(material)
							if (metrics != null) out.append(" effectType " + metrics.effectType + " combiner " + metrics.effectCombiner + " alpha " + (metrics.alpha.toInt() and 0xFF) + " hdr " + metrics.hdr + " scroll " + metrics.speedU + "," + metrics.speedV)
						}
						out.append('\n')
					}
					val alphas: ByteArray? = mesh.facesAlpha
					if (alphas != null || mesh.numFaces <= 64) {
						for (f in 0 until mesh.numFaces) {
							val a: Int = if (alphas == null) 0 else alphas[f].toInt() and 0xFF
							// every face of a small model, else the translucent ones
							if (a == 0 && mesh.numFaces > 64) continue
							val ya: Int = ys[mesh.facesA!![f].toInt()]
							val yb: Int = ys[mesh.facesB!![f].toInt()]
							val yc: Int = ys[mesh.facesC!![f].toInt()]
							val flat: Boolean = ya == yb && yb == yc
							val material: Int = if (mesh.facesMaterial == null) -1 else mesh.facesMaterial!![f].toInt()
							val layer: Int = if (mesh.facesLayer == null) -1 else mesh.facesLayer!![f].toInt()
							val texture: Int = if (mesh.facesTexture == null) -1 else mesh.facesTexture!![f].toInt()
							out.append("    face " + f + ": transparency " + a + " (opacity " + (255 - a) + ") y " + ya + "," + yb + "," + yc + (if (flat) " flat" else "") + " colour " + (mesh.facesColor!![f].toInt() and 0xFFFF) + " material " + material + " texture " + texture + " layer " + layer + "\n")
						}
					}
				}
			}
			print(out)
		}
	}

	private fun sendCommand(command: String) {
		try {
			ClientCommandProcessor.executeCommand(command, false, false)
		} catch (ex: Exception) {
			fail("cannot send ::" + command + ": " + ex)
		}
	}

	/**
	 * Switches toolkit through ToolkitSwitch.loadAssets, the client's own switch.
	 */
	private fun switchToolkit(toolkit: Int) {
		try {
			ToolkitSwitch.loadAssets(false, toolkit)
		} catch (ex: Exception) {
			fail("cannot switch to toolkit " + toolkit + ": " + ex)
		}
	}

	private fun format(value: Double): String {
		return (lang.round(value * 10.0) / 10.0).toString()
	}

	/**
	 * Steps through the sub-interface check, a frame at a time: the overlays the server opened at login
	 * are there, a modal opened into a free container of the root interface draws, and closing modals
	 * removes it and leaves the overlays. True once it is done.
	 */
	private fun subInterfacesDone(): Boolean {
		if (subInterfaceStep == 0) {
			var overlay: Int = -1
			overlaysAtStart = 0
			var sub: SubInterface? = InterfaceManager.OPEN_INTERFACES.first() as SubInterface?
			while (sub != null) {
				if (sub.openType === SubInterfaceType.OVERLAY) {
					overlaysAtStart++
					overlay = sub.interfaceId
				}
				sub = InterfaceManager.OPEN_INTERFACES.next() as SubInterface?
			}
			if (overlaysAtStart == 0) {
				fail("no overlay sub-interface is open after login")
			}
			val root: Array<Component?>? = Interface.interfaces!![ClientState.BASE_WINDOW_ID]
			var i: Int = 0
			while (root != null && i < root.size && modalParent == -1L) {
				val c: Component? = root[i]
				if (c != null && c.type === ComponentType.CONTAINER && c.parentId != -1 && InterfaceManager.OPEN_INTERFACES.get(c.idHash.toLong()) == null) {
					modalParent = c.idHash.toLong()
				}
				i++
			}
			if (modalParent == -1L) {
				fail("the root interface " + ClientState.BASE_WINDOW_ID + " has no free container to open a modal into")
			}
			openSubInterface(overlay, modalParent.toInt(), SubInterfaceType.MODAL)
			subInterfaceFrame = frames
			subInterfaceStep = 1
			return false
		}
		if (frames - subInterfaceFrame < SUB_INTERFACE_FRAMES) {
			return false
		}
		if (subInterfaceStep == 1) {
			val modal: SubInterface? = InterfaceManager.OPEN_INTERFACES.get(modalParent) as SubInterface?
			if (modal == null || modal.openType !== SubInterfaceType.MODAL) {
				fail("the modal opened into " + modalParent + " is " + (if (modal == null) "not open" else "open as " + modal.openType))
			}
			closeModals()
			if (InterfaceManager.OPEN_INTERFACES.get(modalParent) != null) {
				fail("closing modals left the modal in " + modalParent + " open")
			}
			subInterfaceFrame = frames
			subInterfaceStep = 2
			return false
		}
		var overlays: Int = 0
		var sub: SubInterface? = InterfaceManager.OPEN_INTERFACES.first() as SubInterface?
		while (sub != null) {
			if (sub.openType === SubInterfaceType.OVERLAY) {
				overlays++
			}
			sub = InterfaceManager.OPEN_INTERFACES.next() as SubInterface?
		}
		if (overlays != overlaysAtStart) {
			fail("closing modals changed the overlays open from " + overlaysAtStart + " to " + overlays)
		}
		firstImage += "; modal opened into " + (modalParent shr 16) + ":" + (modalParent and 0xFFFFL) + " and closed, " + overlays + " overlays kept"
		return true
	}

	/**
	 * Steps through the mini-menu check: adds a "Walk here" entry for the tile two squares east of the
	 * player and runs it the way a click does (MiniMenuActionExecutor.processMenuClick), requires the
	 * player to leave its tile, then walks it back the same way and requires it home again. The
	 * account's position is saved by the server, so the next run starts where this one did. Home is
	 * the starting tile, or the scene tile client.test.menuWalkHome ("x,y") to put a moved account
	 * back. True once it is done.
	 */
	private fun menuWalkDone(player: PlayerEntity): Boolean {
		val x: Int = player.movementBaseX[0]
		val y: Int = player.movementBaseY[0]
		if (menuWalkStep == 0) {
			menuWalkStartX = x
			menuWalkStartY = y
			menuWalkHomeX = x
			menuWalkHomeY = y
			val home: String = lang.getProperty("client.test.menuWalkHome", "")
			if (home.length > 0) {
				menuWalkHomeX = lang.parseInt((home.javaSplit(",")[0]).javaTrim())
				menuWalkHomeY = lang.parseInt((home.javaSplit(",")[1]).javaTrim())
			}
			walkFromMenu(x + 2, y)
			menuWalkStep = 1
			return false
		}
		if (menuWalkStep == 1) {
			if (x == menuWalkStartX && y == menuWalkStartY) {
				if (frames - menuWalkFrame >= MENU_WALK_FRAMES) {
					fail("the player never left scene tile " + menuWalkStartX + "," + menuWalkStartY + " after a walk from the mini menu")
				}
				return false
			}
			menuWalkAwayX = x
			menuWalkAwayY = y
			walkFromMenu(menuWalkHomeX, menuWalkHomeY)
			menuWalkStep = 2
			return false
		}
		if (x != menuWalkHomeX || y != menuWalkHomeY) {
			if (frames - menuWalkFrame >= MENU_WALK_FRAMES) {
				fail("the player never got back to scene tile " + menuWalkHomeX + "," + menuWalkHomeY + " (it is at " + x + "," + y + ")")
			}
			return false
		}
		firstImage += "; walked from the mini menu to " + menuWalkAwayX + "," + menuWalkAwayY + " and back to " + x + "," + y
		return true
	}

	/**
	 * Steps through the npc indicators' respawn timer check without a kill: tags the guide, posts
	 * his despawn to the plugins as a death does (PluginRuntime.npcDespawned), requires a timer on
	 * his tile in a later frame, posts his spawn again and requires the respawn time learnt. The
	 * guide is never removed from the scene. True once it is done.
	 */
	private fun respawnDone(): Boolean {
		if (respawnStep == 0) {
			var guide: NPCEntity? = null
			for (i in 0 until NPCList.npcCount) {
				val npc: NPCEntity? = NPCList.NPC_ARRAY[i]?.npc
				if (npc != null && npc.npcType?.id == GUIDE_NPC) {
					guide = npc
				}
			}
			if (guide == null) {
				fail("no guide (npc " + GUIDE_NPC + ") in the scene for the respawn check")
				return true
			}
			respawnGuide = guide
			respawnTimerWasOn = NpcIndicators.showRespawnTimer
			NpcIndicators.enabled = true
			NpcIndicators.showRespawnTimer = true
			NpcIndicators.tag(guide.playerIndex)
			PluginRuntime.npcDespawned(guide)
			if (NpcIndicators.pendingRespawns() != 1) {
				fail("the guide's despawn left " + NpcIndicators.pendingRespawns() + " dead npcs remembered, not 1")
			}
			respawnFrame = frames
			respawnStep = 1
			return false
		}
		if (frames - respawnFrame < RESPAWN_FRAMES) {
			return false
		}
		val guide: NPCEntity = respawnGuide ?: return true
		if (NpcIndicators.respawnsDrawnLastFrame != 1) {
			fail("no respawn timer drawn on the guide's tile (" + NpcIndicators.respawnsDrawnLastFrame + " drawn in the last frame)")
		}
		checkImage("-respawn", SHOT.replace(".png", "-respawn.png"))
		PluginRuntime.npcSpawned(guide)
		if (NpcIndicators.learnedRespawns() != 1 || NpcIndicators.pendingRespawns() != 0) {
			fail("the guide's return did not teach a respawn time (" + NpcIndicators.learnedRespawns() + " learnt, " + NpcIndicators.pendingRespawns() + " still dead)")
		}
		NpcIndicators.untag(guide.playerIndex)
		NpcIndicators.showRespawnTimer = respawnTimerWasOn
		firstImage += "; the guide's respawn timer drawn and its time learnt"
		respawnStep = 2
		return true
	}

	/**
	 * The XP button by the minimap, found by its own option in the game frame shown, and XP Drops' Setup
	 * from it: the plugin manager opens on XP Drops' settings page. A summary for the pass line.
	 */
	private fun xpButtonSetup(): String {
		val frame: Int = ClientState.BASE_WINDOW_ID
		var found: Int = -1
		for (component in 0 until 400) {
			val widget = ClientApiImpl.widgetById((frame shl 16) or component) ?: continue
			for (i in 0 until widget.actionCount()) {
				if (widget.action(i) == XpDrops.XP_BUTTON_OPTION) {
					found = component
				}
			}
		}
		if (found < 0) {
			fail("no component of interface " + frame + " has the XP button's \"" + XpDrops.XP_BUTTON_OPTION + "\"")
			return "no XP button"
		}
		val why: String? = PluginManagerScreen.openSettings(XpDrops.DESCRIPTOR.id)
		if (why != null || !PluginManagerScreen.open) {
			fail("Setup did not open XP Drops' settings: " + why)
		}
		// the progress bar's list (27 choices) is longer than the window: it scrolls to its end with the wheel
		val list: IntArray = PluginManagerScreen.openChoiceList("progressBar")
		if (list[0] != 1 || list[1] <= 0) {
			fail("the progress bar's list does not scroll (" + list[0] + ", most " + list[1] + ")")
		}
		val end: IntArray = PluginManagerScreen.scrollChoiceList(100)
		if (end[0] != end[1]) {
			fail("the wheel did not take the progress bar's list to its end (" + end[0] + " of " + end[1] + ")")
		}
		PluginManagerScreen.close()
		println("SMOKE XP button: " + frame + ":" + found)
		return "the XP button is " + frame + ":" + found + ", its Setup opens the settings, the progress bar's list scrolls " + end[1] + " pixels"
	}

	/**
	 * Steps through the experience drop check: logging in must have made no drops. Void sends no
	 * stats at login, so it first sets the test account's Attack to 1 with its admin command
	 * ::level (the update only primes the skill), then to 2, which must make a "+83" drop with the Attack icon
	 * that frames draw, and then back to 1. True once it is done.
	 */
	private fun xpDropsDone(): Boolean {
		val level: Int = StatBaseLevels.statBaseLevels[0]
		if (xpStep == 0) {
			if (XpDrops.dropCount() != 0 || XpDrops.lastDropText != null) {
				fail("logging in made experience drops (" + XpDrops.lastDropText + ")")
			}
			xpWasEnabled = XpDrops.enabled
			XpDrops.enabled = true
			// Old School's setup at its fullest: in the middle, the total counter and the progress bar under it
			XpDrops.position = XpDrops.POSITION_MIDDLE
			XpDrops.counter = XpDrops.COUNTER_TOTAL
			XpDrops.progressBar = 1
			sendCommand("level attack 1")
			xpFrame = frames
			xpStep = 1
			return false
		}
		if (xpStep == 1) {
			if (level != 1) {
				if (frames - xpFrame >= XP_WAIT) {
					fail("::level attack 1 did not reach the client (Attack is " + level + ")")
				}
				return false
			}
			if (XpDrops.lastDropText != null) {
				fail("setting Attack to 1 made the drop \"" + XpDrops.lastDropText + "\"")
			}
			println("SMOKE raising Attack from 1 to 2")
			sendCommand("level attack 2")
			xpFrame = frames
			xpStep = 2
			return false
		}
		if (xpStep == 2) {
			if (XpDrops.drawnLastFrame > 0) {
				xpDrawn = kotlin.math.max(xpDrawn, XpDrops.drawnLastFrame)
			}
			if (XpDrops.lastDropText == null || xpDrawn == 0) {
				if (frames - xpFrame >= XP_WAIT) {
					fail("raising Attack made " + (if (XpDrops.lastDropText == null) "no experience drop" else "the drop \"" + XpDrops.lastDropText + "\" but no frame drew it"))
				}
				return false
			}
			// the screen shows a frame a little after it is drawn: let the drop be on it before the shot
			if (xpDrawnFrame < 0) {
				xpDrawnFrame = frames
			}
			if (frames - xpDrawnFrame < 10) {
				return false
			}
			if ("+83" != XpDrops.lastDropText || XpDrops.drops.lastOrNull()?.skills != listOf(0)) {
				fail("the XP drop reads \"" + XpDrops.lastDropText + "\" for skills " + XpDrops.drops.lastOrNull()?.skills + ", not \"+83\" with the Attack icon")
			}
			// saved for a look, never compared: the drop rises and fades, so no two runs catch the same frame
			checkImage("-xpdrop", SHOT.replace(".png", "-xpdrop.png"))
			// the same drop on the right at the largest size with the most recent counter, for the next frame
			XpDrops.position = XpDrops.POSITION_RIGHT
			XpDrops.size = 4
			XpDrops.counter = XpDrops.COUNTER_RECENT
			XpDrops.progressBar = 2 + 3 // Constitution's bar, under Attack's counter
			xpRightFrame = frames
			println("SMOKE experience drop \"" + XpDrops.lastDropText + "\" drawn; setting Attack back to 1")
			sendCommand("level attack 1")
			xpFrame = frames
			xpStep = 3
			return false
		}
		if (xpStep == 3) {
			if (xpRightFrame >= 0 && frames - xpRightFrame >= 10) {
				saveFrame("build/smoke/frame-xpdrop-right.png")
				xpRightFrame = -1
				XpDrops.size = 2
			}
			if (level != 1) {
				if (frames - xpFrame >= XP_WAIT) {
					fail("Attack did not go back to 1 (it is " + level + ")")
				}
				return false
			}
			XpDrops.enabled = xpWasEnabled
			XpDrops.position = XpDrops.POSITION_RIGHT
			XpDrops.counter = XpDrops.COUNTER_NONE
			XpDrops.progressBar = 0
			firstImage += "; XP drop \"" + XpDrops.lastDropText + "\" drawn with the Attack icon, Attack back to 1; " + xpButtonSetup()
			xpStep = 4
		}
		return true
	}

	/**
	 * Steps through the ground tiles check: turns the plugin on, marks the tile east of the player
	 * (as its Mark tile entry does), and after MARKER_FRAMES frames requires one tile that the last
	 * frame drew, with the tile's yellow on the frame at the middle of each of its edges. Then it
	 * unmarks the tile, requires it gone, and puts the switch back. True once it is done.
	 */
	private fun markersDone(player: PlayerEntity): Boolean {
		val x: Int = player.movementBaseX[0] + 1
		val y: Int = player.movementBaseY[0]
		if (markerStep == 0) {
			markersWereEnabled = GroundTiles.enabled
			GroundTiles.enabled = true
			markersBefore = GroundTiles.markers.size
			GroundTiles.mark(x, y)
			if (GroundTiles.markers.size != markersBefore + 1 || !GroundTiles.isMarked(x, y)) {
				fail("marking scene tile " + x + "," + y + " through the ground tiles plugin left " + GroundTiles.markers.size + " tiles (" + markersBefore + " before)")
			}
			markerFrame = frames
			markerStep = 1
			return false
		}
		if (frames - markerFrame < MARKER_FRAMES) {
			return false
		}
		if (markerStep == 1) {
			if (GroundTiles.drawnLastFrame != 1) {
				fail("the marked tile " + x + "," + y + " was not drawn (" + GroundTiles.drawnLastFrame + " tiles drawn in the last frame)")
			}
			// saved for a look; the check is the outline's own pixels, not the whole frame, which the
			// walking guide changes from run to run
			checkImage("-tilemarker", SHOT.replace(".png", "-tilemarker.png"))
			val image: String = markerPixels()
			GroundTiles.unmark(x, y)
			if (GroundTiles.markers.size != markersBefore) {
				fail("unmarking the tile left " + GroundTiles.markers.size + " tiles (" + markersBefore + " before)")
			}
			GroundTiles.enabled = markersWereEnabled
			firstImage += "; tile " + x + "," + y + " marked, drawn and unmarked: " + image
			markerStep = 2
		}
		return true
	}

	/**
	 * A window taller than its shell: it scrolls, and the wheel takes it to its end. The frame of the
	 * end waits for the next step ([scrolledBack]): the screen shows a frame a little after it is drawn.
	 * A summary for the pass line.
	 */
	private fun scrollToEnd(name: String, before: IntArray, scroll: (Int) -> Unit, position: () -> IntArray): String {
		if (before[1] <= before[2]) {
			fail("the " + name + " window does not scroll (content " + before[1] + ", visible " + before[2] + ")")
			return "no scrolling"
		}
		scroll(100)
		val end: IntArray = position()
		if (end[0] != end[1] - end[2]) {
			fail("the wheel did not take the " + name + " window to its end (at " + end[0] + " of " + (end[1] - end[2]) + ")")
		}
		return "scrolled " + end[1] + " tall to its end and back"
	}

	/** The frame of the window at its end (frame-<name>-scrolled.png), and the wheel back to its top. */
	private fun scrolledBack(name: String, scroll: (Int) -> Unit, position: () -> IntArray) {
		saveFrame("build/smoke/frame-" + name.lowercase() + "-scrolled.png")
		scroll(-100)
		if (position()[0] != 0) {
			fail("the wheel did not take the " + name + " window back to its top")
		}
	}

	/**
	 * The XP Tracker: once it has taken the login's experience, two Attack gains go through the client's
	 * stat hook (PluginRuntime.statChanged, as the server's stat packet does), Attack goes on the canvas,
	 * a frame of the box (-xpcanvas); then the window opens on its box, a frame of it (-xptracker), and
	 * it closes, Attack off the canvas and reset.
	 */
	private fun xpTrackerDone(): Boolean {
		val attack: Int = 0
		if (xpTrackerStep == 0) {
			val state: XpTrackerState = XpTracker.states[attack] ?: return false // the tracker waits out the login's update
			val xp: Int = state.currentXp().toInt()
			XpTracker.enabled = true
			PluginRuntime.statChanged(attack, xp, xp + 40, 1)
			PluginRuntime.statChanged(attack, xp + 40, xp + 80, 1)
			if (XpTracker.states[attack]?.totalXpGained() != 80 || !XpTracker.order.contains(attack)) {
				fail("the XP tracker counted " + XpTracker.states[attack]?.totalXpGained() + " Attack experience of 80")
				return true
			}
			if (!XpTracker.canvas.contains(attack)) {
				XpTracker.toggleCanvas(attack)
			}
			// gains in nine skills more, so that the window has to scroll
			for (skill in 1..9) {
				val start: Int = XpTracker.states[skill]?.currentXp()?.toInt() ?: continue
				PluginRuntime.statChanged(skill, start, start + 25, 1)
			}
			xpTrackerFrame = frames
			xpTrackerStep = 1
			return false
		}
		if (frames - xpTrackerFrame < MARKER_FRAMES) {
			return false
		}
		if (xpTrackerStep == 1) {
			checkImage("-xpcanvas", SHOT.replace(".png", "-xpcanvas.png"))
			if (XpTrackerOverlay.drawnLastFrame() != 1) {
				fail("the XP tracker drew " + XpTrackerOverlay.drawnLastFrame() + " canvas boxes, not 1")
			}
			val why: String? = XpTrackerScreen.open()
			if (why != null) {
				fail("the XP tracker's window did not open: " + why)
				return true
			}
			xpTrackerFrame = frames
			xpTrackerStep = 2
			return false
		}
		if (xpTrackerStep == 2) {
			checkImage("-xptracker", SHOT.replace(".png", "-xptracker.png"))
			if (XpTrackerScreen.boxesShown != 10) {
				fail("the XP tracker's window shows " + XpTrackerScreen.boxesShown + " skill boxes, not 10")
			}
		}
		if (xpTrackerStep == 2) {
			xpScrollSummary = scrollToEnd("XP", XpTrackerScreen.scrollPosition(), { XpTrackerScreen.scrollBy(it) }, { XpTrackerScreen.scrollPosition() })
			xpTrackerFrame = frames
			xpTrackerStep = 3
			return false
		}
		scrolledBack("XP", { XpTrackerScreen.scrollBy(it) }, { XpTrackerScreen.scrollPosition() })
		val xpScroll: String = xpScrollSummary
		XpTrackerScreen.hookBack()
		if (XpTrackerScreen.open || !PluginManagerScreen.open) {
			fail("Back in the XP tracker's window did not bring the plugin manager back")
		}
		PluginManagerScreen.close()
		XpTracker.toggleCanvas(attack)
		XpTracker.resetAll()
		firstImage += "; XP tracker: 80 Attack experience counted, its canvas box and its window drawn with 10 skills, " + xpScroll + ", Back to the plugin manager"
		return true
	}

	/**
	 * Object Markers on the first named location within six tiles of the player: marked as its Mark
	 * object entry does, given all four styles, a frame of it (-objectmarkers), and unmarked. The hull's
	 * box is printed beside the tile's projection, both view-relative, to show they share a space.
	 */
	private fun objectMarkersDone(player: PlayerEntity): Boolean {
		val client = ClientApiImpl
		if (objectStep == 0) {
			val px: Int = player.movementBaseX[0]
			val py: Int = player.movementBaseY[0]
			val level: Int = player.level.toInt()
			var chosen: IntArray? = null
			var best: Int = Int.MAX_VALUE
			val centre: IntArray = IntArray(2)
			for (dx in -6..6) {
				for (dy in -6..6) {
					client.locsAt(px + dx, py + dy, level) { id, kind, _, _ ->
						val name: String? = client.locType(client.resolveLoc(id))?.name()
						val distance: Int = dx * dx + dy * dy + (if (kind == LocKind.INTERACTIVE) 0 else 100)
						if (distance < best && name != null && name != "null" && name.isNotEmpty() && client.projectTile(px + dx, py + dy, level, 0, centre)) {
							best = distance
							chosen = intArrayOf(px + dx, py + dy, id)
						}
					}
				}
			}
			val loc: IntArray = chosen ?: run {
				fail("no named location within six tiles of the player for the object markers")
				return true
			}
			ObjectIndicators.enabled = true
			ObjectIndicators.toggleMark(loc[0], loc[1], loc[2])
			val marked: ObjectIndicators.ObjectPoint? = ObjectIndicators.points.values.flatten().firstOrNull { it.id == loc[2] }
			if (marked == null) {
				fail("marking location " + loc[2] + " at " + loc[0] + "," + loc[1] + " left no marker")
				return true
			}
			marked.hull = true
			marked.outline = true
			marked.clickbox = true
			marked.tile = true
			objectMarked = intArrayOf(marked.worldX() - MapRegion.sceneBaseX, marked.worldY() - MapRegion.sceneBaseY, loc[2])
			objectFrame = frames
			objectStep = 1
			return false
		}
		if (frames - objectFrame < MARKER_FRAMES) {
			return false
		}
		val loc: IntArray = objectMarked ?: return true
		val level: Int = player.level.toInt()
		checkImage("-objectmarkers", SHOT.replace(".png", "-objectmarkers.png"))
		val xs: IntArray = IntArray(64)
		val ys: IntArray = IntArray(64)
		val hull: Int = client.locHull(loc[0], loc[1], level, loc[2], xs, ys)
		val box: Int = client.locClickbox(loc[0], loc[1], level, loc[2], IntArray(64), IntArray(64))
		val centre: IntArray = IntArray(2)
		client.projectTile(loc[0], loc[1], level, 0, centre)
		val name: String? = client.locType(client.resolveLoc(loc[2]))?.name()
		println("SMOKE object markers: " + name + " (" + loc[2] + ") hull of " + hull + " points, x " + (xs.take(hull).minOrNull()) + ".." + (xs.take(hull).maxOrNull()) + ", y " + (ys.take(hull).minOrNull()) + ".." + (ys.take(hull).maxOrNull()) + "; its tile's middle at " + centre[0] + "," + centre[1] + "; click box of " + box + " points; " + ObjectIndicators.drawnLastFrame + " drawn")
		if (hull < 3 || box < 3 || ObjectIndicators.drawnLastFrame != 1) {
			fail("the marked " + name + " was not drawn whole: hull " + hull + ", click box " + box + ", " + ObjectIndicators.drawnLastFrame + " markers drawn")
		}
		ObjectIndicators.toggleMark(loc[0], loc[1], loc[2])
		firstImage += "; object marker on " + name + ": hull, outline, click box and tile drawn, unmarked"
		return true
	}

	/**
	 * The Loot Tracker on a kill that never happened: the guide's health bar is emptied and it leaves
	 * the scene, and bones and coins appear on its tile in the same cycle, as the server drops loot.
	 * The kill is the Loot Tracker's, under the guide's name; the loot window opens on it (grouped), a
	 * frame of it, then kill by kill, and it closes. Frame in build/smoke as -loottracker.
	 */
	/**
	 * XP Globes: two skills primed and then gaining experience through PluginRuntime.statChanged (as the
	 * stat packet does); two globes must be drawn. Then the test mouse over the first: its tooltip panel
	 * with the skill's line. Frame: frame-xpglobes.png.
	 */
	private fun xpGlobesDone(): Boolean {
		if (globesStep == 0) {
			XpGlobes.enabled = true
			for (skill in intArrayOf(Skill.ATTACK, Skill.WOODCUTTING)) {
				val xp: Int = client.statXp[skill]
				PluginRuntime.statChanged(skill, xp, xp, 1)
				PluginRuntime.statChanged(skill, xp, xp + 100, 1)
			}
			globesFrame = frames
			globesStep = 1
			return false
		}
		if (frames - globesFrame < MARKER_FRAMES) {
			return false
		}
		if (globesStep == 1) {
			if (XpGlobesOverlay.drawnGlobes != 2) {
				fail("XP Globes drew " + XpGlobesOverlay.drawnGlobes + " globes, not 2")
				return true
			}
			// the first globe's middle: the overlay's corner, the arcs' margin and half a globe
			val margin: Int = 3
			OverlayLayout.testMouse = true
			OverlayLayout.testMouseX = XpGlobesOverlay.boundsX + margin + XpGlobes.xpOrbSize / 2
			OverlayLayout.testMouseY = XpGlobesOverlay.boundsY + margin + XpGlobes.xpOrbSize / 2
			globesFrame = frames
			globesStep = 2
			return false
		}
		val tooltip: String = Tooltips.drawnLastFrame.firstOrNull { it.startsWith("Attack") } ?: ""
		if (XpGlobesOverlay.hoveredSkill != Skill.ATTACK || !tooltip.contains("Current XP:")) {
			fail("hovering the Attack globe hovered skill " + XpGlobesOverlay.hoveredSkill + " with the tooltips " + Tooltips.drawnLastFrame)
			OverlayLayout.testMouse = false
			return true
		}
		saveFrame("build/smoke/frame-xpglobes.png")
		println("SMOKE xp globes: 2 globes, Attack hovered: " + tooltip.replace("</br>", " | "))
		OverlayLayout.testMouse = false
		XpGlobes.globes.clear()
		return true
	}

	private fun barrowsDone(): Boolean {
		val api = ClientApiImpl
		if (barrowsStep == 0) {
			BarrowsBrothers.enabled = true
			sendCommand("tele 3565 3290 0")
			barrowsFrame = frames
			barrowsStep = 1
			return false
		}
		if (barrowsStep == 1) {
			if (api.regionId() != BarrowsBrothers.BARROWS_REGION || frames - barrowsFrame < MARKER_FRAMES) {
				if (frames - barrowsFrame > BARROWS_WAIT_FRAMES) {
					fail("the teleport to the Barrows hills left the player in region " + api.regionId())
					return true
				}
				return false
			}
			if (BarrowsOverlay.drawnLetters != BarrowsBrothers.BROTHERS.size) {
				fail("Barrows Brothers drew " + BarrowsOverlay.drawnLetters + " letters on the minimap, not " + BarrowsBrothers.BROTHERS.size)
				return true
			}
			saveFrame("build/smoke/frame-barrows-minimap.png")
			println("SMOKE barrows: " + BarrowsOverlay.drawnLetters + " letters on the minimap")
			sendCommand("tele 3556 9716 3")
			barrowsFrame = frames
			barrowsStep = 2
			return false
		}
		if (barrowsStep == 2) {
			if (!BarrowsBrothers.overlayOpen || frames - barrowsFrame < MARKER_FRAMES) {
				if (frames - barrowsFrame > BARROWS_WAIT_FRAMES) {
					fail("the crypt's overlay (" + BarrowsBrothers.OVERLAY_INTERFACE + ") did not open; region " + api.regionId())
					return true
				}
				return false
			}
			if (BarrowsOverlay.drawnLines != BarrowsBrothers.BROTHERS.size || BarrowsBrothers.prayerDrainTimer == null) {
				fail("Barrows panel drew " + BarrowsOverlay.drawnLines + " brothers; prayer timer " + BarrowsBrothers.prayerDrainTimer)
				return true
			}
			for (component in BarrowsBrothers.OVERLAY_HIDDEN) {
				if (api.widget(BarrowsBrothers.OVERLAY_INTERFACE, component)?.isHidden() != true) {
					fail("the crypt overlay's component " + component + " is not hidden behind the panel")
					return true
				}
			}
			saveFrame("build/smoke/frame-barrows.png")
			println("SMOKE barrows: panel of " + BarrowsOverlay.drawnLines + " brothers, potential " + BarrowsOverlay.drawnPotential + ", prayer drain timer " + BarrowsBrothers.prayerDrainTimer?.text())
			val parent: Int = freeModalParent()
			if (parent == -1) {
				fail("no free container to open the Barrows puzzle into")
				return true
			}
			openSubInterface(BarrowsBrothers.PUZZLE_INTERFACE, parent, SubInterfaceType.MODAL)
			barrowsFrame = frames
			barrowsStep = 3
			return false
		}
		if (barrowsStep == 3) {
			// Void's "squares" puzzle: the sequence 6722-6724, the answer 6719 (the first less 3) put in the middle choice
			val models: IntArray = intArrayOf(6, 6722, 7, 6723, 8, 6724, 2, 6721, 3, 6719, 5, 6720)
			for (i in 0 until models.size / 2) {
				val component: Component? = Interface.interfaces!![BarrowsBrothers.PUZZLE_INTERFACE]?.getOrNull(models[i * 2])
				if (component == null) {
					fail("the Barrows puzzle has no component " + models[i * 2])
					return true
				}
				component.mediaType = MediaType.RAW_MODEL
				component.primaryMediaId = models[i * 2 + 1]
			}
			barrowsFrame = frames
			barrowsStep = 4
			return false
		}
		if (barrowsStep == 4) {
			if (frames - barrowsFrame < MARKER_FRAMES) {
				return false
			}
			if (BarrowsOverlay.drawnAnswer?.componentId() != 3) {
				fail("the Barrows puzzle outlined " + BarrowsOverlay.drawnAnswer?.componentId() + ", not the answer 3 (puzzle open " + BarrowsBrothers.puzzleOpen + ")")
				closeModals()
				return true
			}
			saveFrame("build/smoke/frame-barrows-puzzle.png")
			println("SMOKE barrows: puzzle answer outlined at 25:3")
			closeModals()
			sendCommand("tele 3094 3107 0")
			barrowsFrame = frames
			barrowsStep = 5
			return false
		}
		if (api.regionId() == BarrowsBrothers.CRYPT_REGION || frames - barrowsFrame < MARKER_FRAMES) {
			return frames - barrowsFrame > BARROWS_WAIT_FRAMES
		}
		if (BarrowsBrothers.prayerDrainTimer != null) {
			fail("the prayer drain timer outlived the crypt")
		}
		return true
	}

	private fun attackStylesDone(): Boolean {
		val api = ClientApiImpl
		if (attackStylesFrame == -1) {
			AttackStyles.enabled = true
			AttackStyles.warnForAttack = true
			AttackStyles.removeWarnedStyles = true
			attackStylesFrame = frames
			return false
		}
		if (frames - attackStylesFrame < MARKER_FRAMES) {
			return false
		}
		val style: AttackStyles.AttackStyle? = AttackStyles.attackStyle
		val button: Boolean? = api.widget(AttackStyles.COMBAT_INTERFACE, AttackStyles.FIRST_STYLE)?.isHidden()
		val first: AttackStyles.AttackStyle? = WeaponStyles.styles(AttackStyles.weaponType(api)).firstOrNull()
		val expectHidden: Boolean = first != null && AttackStyles.isWarned(first)
		saveFrame("build/smoke/frame-attackstyles.png")
		AttackStyles.warnForAttack = false
		AttackStyles.removeWarnedStyles = false
		AttackStyles.tick(api)
		AttackStyles.enabled = false
		if (style == null || style == AttackStyles.AttackStyle.OTHER) {
			fail("Attack Styles found the style " + style + " for weapon type " + AttackStyles.weaponType(api) + ", varp 43 = " + api.varp(AttackStyles.STYLE_VARP))
			return true
		}
		if (button != expectHidden) {
			fail("Attack Styles left the warned style's button 884:" + AttackStyles.FIRST_STYLE + (if (button == true) " hidden" else " shown") + ", expected " + (if (expectHidden) "hidden" else "shown"))
			return true
		}
		if (api.widget(AttackStyles.COMBAT_INTERFACE, AttackStyles.FIRST_STYLE)?.isHidden() == true) {
			fail("Attack Styles did not show 884:" + AttackStyles.FIRST_STYLE + " again once the warning was off")
			return true
		}
		println("SMOKE attack styles: " + style.displayName + " for weapon type " + AttackStyles.weaponType(api) + ", button 884:11 " + (if (expectHidden) "hidden" else "shown") + " while Attack is warned, shown after")
		return true
	}

	private fun cannonDone(player: PlayerEntity): Boolean {
		val api = ClientApiImpl
		if (cannonFrame == -1) {
			// no cannon can be set up by the test account: one is taken to stand two tiles east of the player
			Cannon.enabled = true
			Cannon.showDoubleHitSpot = true
			Cannon.cannonPlaced = true
			Cannon.cannonX = api.sceneBaseX() + player.movementBaseX[0] + 2
			Cannon.cannonY = api.sceneBaseY() + player.movementBaseY[0] - 1
			Cannon.cannonLevel = api.localPlayer()?.level() ?: 0
			Cannon.cannonWorld = api.worldId()
			Cannon.chat(api, com.jagex.game.runetek5.plugin.api.events.ChatMessage(com.jagex.game.runetek5.plugin.api.ChatType.GAME, "", "You load the cannon with 20 cannon balls.", null))
			cannonFrame = frames
			return false
		}
		if (frames - cannonFrame < MARKER_FRAMES) {
			return false
		}
		val drawn: String? = Cannon.drawnCount
		saveFrame("build/smoke/frame-cannon.png")
		Cannon.showDoubleHitSpot = false
		Cannon.forget()
		if (drawn != "20") {
			fail("Cannon drew the count " + drawn + ", not 20")
			return true
		}
		println("SMOKE cannon: count " + drawn + " drawn over the cannon, double-hit tiles on")
		return true
	}

	private fun antiDragDone(): Boolean {
		val api = ClientApiImpl
		if (antiDragStep == 0) {
			AntiDrag.enabled = true
			AntiDrag.onShiftOnly = true
			AntiDrag.testShift = true
			antiDragFrame = frames
			antiDragStep = 1
			return false
		}
		if (frames - antiDragFrame < MARKER_FRAMES) {
			return false
		}
		val slot: com.jagex.game.runetek5.plugin.api.Widget? = api.widget(149, 0)?.child(0)
		if (slot == null) {
			fail("Anti Drag: the backpack (149:0) has no slots")
			return true
		}
		if (antiDragStep == 1) {
			if (slot.dragDeadTime() != AntiDrag.dragDelay) {
				fail("Anti Drag: a backpack slot's drag dead time is " + slot.dragDeadTime() + " with Shift held, not " + AntiDrag.dragDelay)
				AntiDrag.testShift = null
				AntiDrag.enabled = false
				return true
			}
			AntiDrag.testShift = false
			antiDragFrame = frames
			antiDragStep = 2
			return false
		}
		val restored: Int = slot.dragDeadTime()
		AntiDrag.testShift = null
		AntiDrag.enabled = false
		if (restored == AntiDrag.dragDelay) {
			fail("Anti Drag: the backpack kept the delay after Shift was let go")
			return true
		}
		println("SMOKE anti drag: backpack slots " + AntiDrag.dragDelay + " cycles with Shift held, " + restored + " after")
		return true
	}

	private fun emojisDone(player: PlayerEntity): Boolean {
		if (emojisFrame == -1) {
			Emojis.enabled = true
			// as PacketDecoder writes public chat: the player's text with < and > escaped
			ChatLineWriter.writeMessage(USER, "hello :) and ^_^ <lt>3", -1, USER, null, ChatType.PUBLIC, 0, USER)
			player.hintDetails.text = "hi :D"
			PluginRuntime.overheadTextChanged(player, player.hintDetails.text)
			emojisFrame = frames
			return false
		}
		if (frames - emojisFrame < MARKER_FRAMES) {
			return false
		}
		val line: String? = ChatBox.CHAT_LINES[0]?.message
		val overhead: String? = player.hintDetails.text
		saveFrame("build/smoke/frame-emojis.png")
		Emojis.enabled = false
		if (line == null || !line.contains("<img=") || line.contains(":)") || overhead == null || !overhead.contains("<img=")) {
			fail("Emojis left the chat line as \"" + line + "\" and the overhead as \"" + overhead + "\"")
			return true
		}
		println("SMOKE emojis: chat \"" + line + "\", overhead \"" + overhead + "\"")
		return true
	}

	private fun keyRemappingDone(): Boolean {
		val api = ClientApiImpl
		val keys = KeyRecorder.keyRecorder as CanvasKeyRecorder
		if (frames - keyRemapFrame < MARKER_FRAMES && keyRemapFrame != -1) {
			return false
		}
		val line: String = api.widget(KeyRemapping.CHATBOX, KeyRemapping.CHATBOX_INPUT)?.text() ?: ""
		keyRemapFrame = frames
		when (keyRemapStep) {
			0 -> KeyRemapping.enabled = true
			1 -> {
				if (!line.endsWith(KeyRemapping.PRESS_ENTER_TO_CHAT)) {
					return keyRemapFailed("the chat line reads \"" + line + "\", not locked")
				}
				keyRemapYaw = api.cameraYaw()
				keys.inject(KeyRemapping.D, '\u0000', 0)
				keys.inject(-1, 'd', 3)
			}
			2 -> {
				val held: Boolean = api.isKeyHeld(KeyRemapping.RIGHT)
				val typed: String? = api.varcString(KeyRemapping.CHAT_INPUT_VARC)
				keys.inject(KeyRemapping.D, '\u0000', 1)
				if (!held || api.isKeyHeld(KeyRemapping.D) || !typed.isNullOrEmpty() || api.cameraYaw() == keyRemapYaw) {
					return keyRemapFailed("D held: Right held " + held + ", D held " + api.isKeyHeld(KeyRemapping.D) + ", chat \"" + typed + "\", yaw " + keyRemapYaw + " to " + api.cameraYaw())
				}
				saveFrame("build/smoke/frame-keyremapping.png")
			}
			3 -> {
				if (api.isKeyHeld(KeyRemapping.RIGHT)) {
					return keyRemapFailed("Right still held after D was let go")
				}
				keys.inject(KeyRemapping.ENTER, '\u0000', 0)
				keys.inject(KeyRemapping.ENTER, '\u0000', 1)
			}
			4 -> {
				keys.inject(-1, 'h', 3)
				keys.inject(-1, 'i', 3)
			}
			5 -> {
				val typed: String? = api.varcString(KeyRemapping.CHAT_INPUT_VARC)
				if (!KeyRemapping.typing || typed != "hi") {
					return keyRemapFailed("after Enter, typing " + KeyRemapping.typing + " and the line \"" + typed + "\", not \"hi\"; shown \"" + line + "\", dialogue " + KeyRemapping.dialogOpen)
				}
				keys.inject(KeyRemapping.ESCAPE, '\u0000', 0)
				keys.inject(KeyRemapping.ESCAPE, '\u0000', 1)
			}
			else -> {
				val typed: String? = api.varcString(KeyRemapping.CHAT_INPUT_VARC)
				KeyRemapping.enabled = false
				KeyRemapping.unlockChat(api)
				if (KeyRemapping.typing || !typed.isNullOrEmpty() || !line.endsWith(KeyRemapping.PRESS_ENTER_TO_CHAT)) {
					return keyRemapFailed("after Escape, typing " + KeyRemapping.typing + ", the line \"" + typed + "\", shown \"" + line + "\"")
				}
				println("SMOKE key remapping: chat locked, D held as Right (the camera turned from yaw " + keyRemapYaw + ") with its character dropped, Enter unlocked, \"hi\" typed, Escape dropped it and locked")
				return true
			}
		}
		keyRemapStep++
		return false
	}

	private fun customCursorDone(): Boolean {
		if (cursorFrame != -1 && frames - cursorFrame < MARKER_FRAMES) {
			return false
		}
		cursorFrame = frames
		when (cursorStep++) {
			0 -> {
				CustomCursor.enabled = true
				CustomCursor.cursorStyle = 0
			}
			1 -> {
				if (CustomCursor.shownStyle != 0 || !PluginCursor.isSet()) {
					fail("Custom Cursor: style " + CustomCursor.shownStyle + " shown, a plugin cursor " + PluginCursor.isSet())
					CustomCursor.enabled = false
					return true
				}
				CustomCursor.cursorStyle = CustomCursor.EQUIPPED_WEAPON
			}
			2 -> {
				val weapon: Boolean = CustomCursor.shownStyle == CustomCursor.EQUIPPED_WEAPON && PluginCursor.isSet()
				CustomCursor.enabled = false
				val worn: Int = ClientApiImpl.itemContainer(CustomCursor.WORN)?.itemId(CustomCursor.WEAPON_SLOT) ?: -1
				val icon: com.jagex.game.runetek5.plugin.PluginImage? = ClientApiImpl.itemImage(worn, 1) as com.jagex.game.runetek5.plugin.PluginImage?
				cursorIconPixels = icon?.pixels?.count { (it ushr 24) != 0 } ?: 0
				if (!weapon || cursorIconPixels < 50) {
					fail("Custom Cursor: the worn weapon's icon is not the cursor (style " + CustomCursor.shownStyle + ", " + cursorIconPixels + " pixels drawn)")
					return true
				}
			}
			else -> {
				if (PluginCursor.isSet() || CursorManager.currentCursor == -3) {
					fail("Custom Cursor: the plugin's cursor stayed after it was switched off")
					return true
				}
				println("SMOKE custom cursor: RS3 Gold, then the worn weapon's icon (" + cursorIconPixels + " pixels drawn) on the canvas; the game's cursor back once off")
				return true
			}
		}
		return false
	}

	private fun itemStatsDone(): Boolean {
		val items: IntArray = intArrayOf(385, 157, 1333)
		val names: Array<String> = arrayOf("shark", "superstrength", "runescimitar")
		if (itemStatsFrame != -1 && frames - itemStatsFrame < MARKER_FRAMES) {
			return false
		}
		if (itemStatsStep > 0) {
			val shown: List<String> = ArrayList(ItemStats.shown)
			saveFrame("build/smoke/frame-itemstats-" + names[itemStatsStep - 1] + ".png")
			if (shown.isEmpty()) {
				ItemStats.testItem = -1
				fail("Item Stats showed nothing for item " + items[itemStatsStep - 1])
				return true
			}
			println("SMOKE item stats " + items[itemStatsStep - 1] + ": " + shown.joinToString(" | ").replace("</br>", " / "))
		}
		if (itemStatsStep == items.size) {
			ItemStats.testItem = -1
			return true
		}
		ItemStats.enabled = true
		ItemStats.testItem = items[itemStatsStep++]
		itemStatsFrame = frames
		return false
	}

	private fun woodcuttingDone(player: PlayerEntity): Boolean {
		val api = ClientApiImpl
		if (woodcuttingFrame == -1) {
			Woodcutting.enabled = true
			// a normal tree (1276) two tiles east of the player becomes its stump (1342)
			PluginRuntime.locChanged(player.movementBaseX[0] + 2, player.movementBaseY[0], api.localPlayer()?.level() ?: 0, 2, 1276, 1342, 10, 0)
			for (i in 0 until 3) {
				Woodcutting.chat(api, com.jagex.game.runetek5.plugin.api.events.ChatMessage(ChatType.GAME_FILTERED, "", "You get some logs.", null))
			}
			woodcuttingFrame = frames
			return false
		}
		if (frames - woodcuttingFrame < MARKER_FRAMES) {
			return false
		}
		saveFrame("build/smoke/frame-woodcutting.png")
		val respawns: Int = Woodcutting.respawns.size
		val logs: Int = Woodcutting.session?.logsCut ?: 0
		Woodcutting.enabled = false
		Woodcutting.session = null
		Woodcutting.respawns.clear()
		if (respawns != 1 || logs != 3) {
			fail("Woodcutting: " + respawns + " respawn pies (not 1) and " + logs + " logs (not 3)")
			return true
		}
		println("SMOKE woodcutting: a felled tree's pie and 3 logs in the session panel")
		return true
	}

	private fun miningDone(player: PlayerEntity): Boolean {
		val api = ClientApiImpl
		if (miningFrame == -1) {
			Mining.enabled = true
			// a Falador copper rock (5779) two tiles south of the player is mined out (5763)
			PluginRuntime.locChanged(player.movementBaseX[0], player.movementBaseY[0] - 2, api.localPlayer()?.level() ?: 0, 2, 5779, 5763, 10, 0)
			Mining.chat(com.jagex.game.runetek5.plugin.api.events.ChatMessage(ChatType.GAME_FILTERED, "", "You swing your pickaxe at the rock.", null))
			Mining.chat(com.jagex.game.runetek5.plugin.api.events.ChatMessage(ChatType.GAME_FILTERED, "", "You manage to mine some copper ore.", null))
			miningFrame = frames
			return false
		}
		if (frames - miningFrame < MARKER_FRAMES) {
			return false
		}
		saveFrame("build/smoke/frame-mining.png")
		val respawns: Int = Mining.respawns.size
		val session: Boolean = Mining.session != null
		Mining.enabled = false
		Mining.session = null
		Mining.respawns.clear()
		if (respawns != 1 || !session) {
			fail("Mining: " + respawns + " respawn pies (not 1), a session " + session)
			return true
		}
		println("SMOKE mining: a mined-out rock's pie and the session panel")
		return true
	}

	private fun reportButtonDone(): Boolean {
		val api = ClientApiImpl
		if (reportFrame != -1 && frames - reportFrame < MARKER_FRAMES) {
			return false
		}
		reportFrame = frames
		val shown: String = api.widget(ReportButton.INTERFACE, ReportButton.TEXT)?.text() ?: ""
		when (reportStep++) {
			0 -> {
				ReportButton.enabled = true
				ReportButton.time = ReportButton.LOGIN_TIME
			}
			1 -> {
				saveFrame("build/smoke/frame-reportbutton.png")
				reportTexts = shown
				if (!Regex("[0-9]{2}:[0-9]{2}:[0-9]{2}").matches(shown)) {
					fail("Report Button: the login timer reads \"" + shown + "\"")
					return true
				}
				ReportButton.time = ReportButton.DATE
			}
			2 -> {
				reportTexts += ", " + shown
				if (!Regex("[A-Z][a-z]{2}\\. [0-9]{2}, [0-9]{4}").matches(shown)) {
					fail("Report Button: the date reads \"" + shown + "\"")
					return true
				}
				ReportButton.enabled = false
				ReportButton.restore(api)
			}
			else -> {
				if (shown != ReportButton.REPORT_ABUSE) {
					fail("Report Button: the button reads \"" + shown + "\" once off")
					return true
				}
				println("SMOKE report button: " + reportTexts + ", then \"" + shown + "\" once off")
				return true
			}
		}
		return false
	}

	/** Player Indicators with the own player highlighted: their name drawn, the rank icons added as chat icons. Frame: frame-playerindicators.png. */
	private fun playerIndicatorsDone(): Boolean {
		if (playerIndicatorsFrame == -1) {
			PlayerIndicators.enabled = true
			PlayerIndicators.highlight[PlayerIndicators.SELF] = PlayerIndicators.ENABLED
			PlayerIndicators.drawPlayerTiles = true
			PlayerIndicators.drawMinimapNames = true
			playerIndicatorsFrame = frames
			return false
		}
		if (frames - playerIndicatorsFrame < MARKER_FRAMES) {
			return false
		}
		saveFrame("build/smoke/frame-playerindicators.png")
		val drawn: Int = PlayerIndicators.drawnLastFrame
		val icons: StringBuilder = StringBuilder()
		for (rank in PlayerIndicators.RANK_SPRITES.indices) {
			icons.append(PlayerIndicators.rankIcon(ClientApiImpl, rank))
		}
		PlayerIndicators.enabled = false
		PlayerIndicators.highlight[PlayerIndicators.SELF] = PlayerIndicators.DISABLED
		PlayerIndicators.drawPlayerTiles = false
		PlayerIndicators.drawMinimapNames = false
		if (drawn < 1 || icons.count { it == '<' } != PlayerIndicators.RANK_SPRITES.size) {
			fail("Player Indicators: " + drawn + " names drawn, rank icons \"" + icons + "\"")
			return true
		}
		println("SMOKE player indicators: own name drawn, rank icons " + icons)
		return true
	}

	/** Special Attack Counter given a godsword special's hit of 450: the infobox counts it, the drop rises. Frame: frame-specialcounter.png. */
	private fun specialCounterDone(): Boolean {
		if (specialCounterFrame == -1) {
			SpecialCounter.enabled = true
			SpecialCounter.specialAttackHit(ClientApiImpl, 1, 450)
			specialCounterFrame = frames
			return false
		}
		if (frames - specialCounterFrame < MARKER_FRAMES / 2) {
			return false
		}
		saveFrame("build/smoke/frame-specialcounter.png")
		val count: Int = SpecialCounter.counters[1]?.count ?: -1
		val energy: Int = ClientApiImpl.varp(SpecialCounter.SPECIAL_VARP)
		SpecialCounter.removeCounters()
		SpecialCounter.drops.clear()
		if (count != 450 || energy < 0 || energy > 1000) {
			fail("Special Attack Counter: counted " + count + " (not 450), energy " + energy)
			return true
		}
		println("SMOKE special counter: Bandos Godsword 450 counted and dropped, special energy " + energy)
		return true
	}

	/** Regeneration Meter with "Show at full hitpoints" on: the ring fills round the orb. Frame: frame-regenmeter.png. */
	private fun regenMeterDone(): Boolean {
		if (regenMeterFrame == -1) {
			RegenMeter.showWhenNoChange = true
			regenMeterFrame = frames
			return false
		}
		val waited: Int = frames - regenMeterFrame
		if (RegenMeter.hitpointsPercentage < 0.3 && waited < 600) {
			regenMeterShown = -1
			return false
		}
		// a few frames with the ring up: the OpenGL toolkits' frame is read from the last one shown
		if (regenMeterShown == -1) {
			regenMeterShown = frames
		}
		if (frames - regenMeterShown < 5) {
			return false
		}
		saveFrame("build/smoke/frame-regenmeter.png")
		val drawn: Int = RegenMeter.drawnLastFrame
		val percent: Double = RegenMeter.hitpointsPercentage
		RegenMeter.showWhenNoChange = false
		if (drawn < 1 || percent <= 0.0) {
			fail("Regeneration Meter: " + drawn + " rings drawn at " + percent)
			return true
		}
		val orb = ClientApiImpl.widget(RegenMeter.HEALTH_ORB, RegenMeter.HEALTH_ORB_CIRCLE)
		println("SMOKE regen meter: the life points ring drawn at " + percent + " round the orb at " + orb?.screenX() + "," + orb?.screenY() + " " + orb?.width() + "x" + orb?.height())
		return true
	}

	/** Run Energy with "Replace orb text" on: the orb reads the seconds left, then its energy again once off. Frame: frame-runenergy.png. */
	private fun runEnergyDone(): Boolean {
		val api = ClientApiImpl
		if (runEnergyFrame != -1 && frames - runEnergyFrame < MARKER_FRAMES) {
			return false
		}
		runEnergyFrame = frames
		val shown: String = api.widget(RunEnergy.RUN_ORB, RunEnergy.RUN_ORB_TEXT)?.text() ?: ""
		when (runEnergyStep++) {
			0 -> RunEnergy.replaceOrbText = true
			1 -> {
				saveFrame("build/smoke/frame-runenergy.png")
				runEnergyText = shown
				RunEnergy.replaceOrbText = false
				RunEnergy.resetOrbText(api)
				if (!Regex("[0-9]+s").matches(shown)) {
					fail("Run Energy: the orb reads \"" + shown + "\" with the run time on")
					return true
				}
			}
			else -> {
				if (shown != api.runEnergy().toString()) {
					fail("Run Energy: the orb reads \"" + shown + "\" once off, not " + api.runEnergy())
					return true
				}
				println("SMOKE run energy: the orb read \"" + runEnergyText + "\", then \"" + shown + "\"; tooltip \"" + RunEnergy.tooltipText(api) + "\"")
				return true
			}
		}
		return false
	}

	/** Poisoned for one hit of 14: the infobox, and the orb's heart drawn green. Frame: frame-poison.png. */
	private fun poisonDone(): Boolean {
		if (poisonFrame == -1) {
			Poison.showInfoboxes = true
			sendCommand("poison 14")
			poisonFrame = frames
			return false
		}
		val poisoned: Boolean = ClientApiImpl.varp(Poison.POISON_VARP) > 0
		if (!poisoned && frames - poisonFrame < BANK_WAIT_FRAMES) {
			return false
		}
		if (poisonSeen == -1) {
			poisonSeen = frames
		}
		if (frames - poisonSeen < MARKER_FRAMES) {
			return false
		}
		saveFrame("build/smoke/frame-poison.png")
		val heart: Int = Poison.heart
		val box: Boolean = Poison.infobox != null
		val damage: Int = Poison.lastDamage
		Poison.showInfoboxes = false
		if (!poisoned || heart != Poison.HEART_POISON || !box || damage != 14) {
			fail("Poison: poisoned " + poisoned + ", heart " + heart + ", infobox " + box + ", next damage " + damage)
			return true
		}
		println("SMOKE poison: poisoned for " + damage + ", the green heart on the orb and the infobox; " + Poison.createTooltip())
		return true
	}

	/** Fishing by Catherby's spots: they are drawn, and a catch starts the session panel. Frame: frame-fishing.png. */
	private fun fishingDone(): Boolean {
		when (fishingStep) {
			0 -> {
				Fishing.showNames = true
				sendCommand("tele 2846 3429")
				fishingFrame = frames
				fishingStep = 1
			}
			1 -> {
				if (Fishing.drawnLastFrame == 0 && frames - fishingFrame < BANK_WAIT_FRAMES) {
					return false
				}
				Fishing.chat(com.jagex.game.runetek5.plugin.api.events.ChatMessage(com.jagex.game.runetek5.plugin.api.ChatType.GAME_FILTERED, "", "You catch some raw shrimps.", null))
				fishingFrame = frames
				fishingStep = 2
			}
			2 -> {
				if (frames - fishingFrame < MARKER_FRAMES) {
					return false
				}
				saveFrame("build/smoke/frame-fishing.png")
				val drawn: Int = Fishing.drawnLastFrame
				val caught: Int = Fishing.session?.caught ?: 0
				Fishing.showNames = false
				Fishing.session = null
				sendCommand("tele 3094 3107")
				fishingFrame = frames
				fishingStep = 3
				if (drawn == 0 || caught != 1) {
					fail("Fishing: " + drawn + " spots drawn at Catherby, " + caught + " fish caught")
					return true
				}
				println("SMOKE fishing: " + drawn + " spots drawn at Catherby, the session panel after a catch")
			}
			else -> return frames - fishingFrame >= MARKER_FRAMES
		}
		return false
	}

	/** Agility at the Gnome Stronghold: its obstacles found and drawn, a lap counted from the bonus. Frame: frame-agility.png. */
	private fun agilityDone(): Boolean {
		when (agilityStep) {
			0 -> {
				sendCommand("tele 2474 3436")
				agilityFrame = frames
				agilityStep = 1
			}
			1 -> {
				if (Agility.drawnLastFrame == 0 && frames - agilityFrame < BANK_WAIT_FRAMES) {
					return false
				}
				val xp: Int = ClientApiImpl.skillXp(com.jagex.game.runetek5.plugin.api.Skill.AGILITY)
				Agility.statChanged(com.jagex.game.runetek5.plugin.api.events.StatChanged(com.jagex.game.runetek5.plugin.api.Skill.AGILITY, xp + 1, xp + 40, 1))
				agilityFrame = frames
				agilityStep = 2
			}
			2 -> {
				if (frames - agilityFrame < MARKER_FRAMES) {
					return false
				}
				saveFrame("build/smoke/frame-agility.png")
				val found: Int = Agility.found.size
				val drawn: Int = Agility.drawnLastFrame
				val laps: Int = Agility.session?.totalLaps ?: 0
				Agility.session = null
				sendCommand("tele 3094 3107")
				agilityFrame = frames
				agilityStep = 3
				if (found == 0 || drawn == 0 || laps != 1) {
					fail("Agility: " + found + " obstacles found, " + drawn + " drawn, " + laps + " laps")
					return true
				}
				println("SMOKE agility: " + found + " obstacles and shortcuts found at the Gnome Stronghold, " + drawn + " click boxes drawn, a lap counted")
			}
			else -> return frames - agilityFrame >= MARKER_FRAMES
		}
		return false
	}

	/** Slayer with a cows task of 25: the infobox, and the cows of Lumbridge highlighted. Frame: frame-slayer.png. */
	private fun slayerDone(): Boolean {
		when (slayerStep) {
			0 -> {
				Slayer.highlightHull = true
				Slayer.highlightTile = true
				sendCommand("var slayer_target cows")
				sendCommand("var slayer_count 25")
				sendCommand("tele 3256 3266")
				slayerFrame = frames
				slayerStep = 1
			}
			1 -> {
				if ((Slayer.task == null || Slayer.targetsDrawn == 0) && frames - slayerFrame < BANK_WAIT_FRAMES) {
					return false
				}
				slayerFrame = frames
				slayerStep = 2
			}
			2 -> {
				if (frames - slayerFrame < MARKER_FRAMES / 4) {
					return false
				}
				saveFrame("build/smoke/frame-slayer.png")
				val name: String? = Slayer.task?.name
				val amount: Int = Slayer.amount
				val drawn: Int = Slayer.targetsDrawn
				Slayer.highlightHull = false
				Slayer.highlightTile = false
				sendCommand("var slayer_count 0")
				sendCommand("var slayer_target nothing")
				sendCommand("tele 3094 3107")
				slayerFrame = frames
				slayerStep = 3
				// the account's task and place are put back before a failure ends the run
				slayerFailure = if (name != "Cows" || amount != 25 || drawn == 0 || Slayer.counter == null) "Slayer: task " + name + " x" + amount + ", " + drawn + " cows highlighted, infobox " + (Slayer.counter != null) else null
				if (slayerFailure == null) {
					println("SMOKE slayer: task " + name + " x" + amount + " from the varps, " + drawn + " cows highlighted, the infobox up")
				}
			}
			else -> {
				if (frames - slayerFrame < MARKER_FRAMES || (Slayer.task != null && frames - slayerFrame < BANK_WAIT_FRAMES)) {
					return false
				}
				slayerFailure?.let { fail(it) }
				return true
			}
		}
		return false
	}

	/** Puts [itemId] in backpack [slot] on this client only, as the server's partial inventory update does; -1 empties it. */
	private fun fakeBackpackItem(slot: Int, itemId: Int) {
		ItemContainer.setItemInContainer(BACKPACK, false, slot, if (itemId < 0) 0 else 1, itemId)
		ItemContainer.RECENTLY_USED_ITEM_CONTAINERS[client.recentlyUsedItemContainersCursorPos++ and 0x1F] = BACKPACK
	}

	/** Clicks the backpack's tab stone (the base window's component whose first option is Inventory). */
	private fun openInventoryTab() {
		openTab("Inventory")
	}

	/** Clicks the tab stone whose first option is one of [options]; false when the base window has none. */
	private fun openTab(vararg options: String): Boolean {
		val root: Array<Component?> = Interface.interfaces!![ClientState.BASE_WINDOW_ID] ?: return false
		for (c in root) {
			val option: String? = c?.options?.getOrNull(0)
			if (c != null && option != null && option in options) {
				InterfaceOptionInteract.ifOptionInteract(-1, option, 1, c.idHash)
				return true
			}
		}
		return false
	}

	/** Item Charges on a glory(4) in the backpack: the "4" drawn on it. Frame: frame-itemcharges.png. */
	private fun itemChargesDone(): Boolean {
		when (itemChargesStep) {
			0 -> {
				val backpack = ClientApiImpl.itemContainer(BACKPACK) ?: return false
				for (slot in 0 until backpack.size()) {
					if (itemChargesSlot < 0 && backpack.itemId(slot) < 0) {
						itemChargesSlot = slot
					}
				}
				if (itemChargesSlot < 0) {
					fail("Item Charges: no empty backpack slot")
					return true
				}
				openInventoryTab()
				itemChargesFrame = frames
				itemChargesStep = 1
			}
			1 -> {
				// after the tab's own redraw and any inventory update the login sent
				if (frames - itemChargesFrame < MARKER_FRAMES) {
					return false
				}
				fakeBackpackItem(itemChargesSlot, GLORY_4)
				itemChargesFrame = frames
				itemChargesStep = 2
			}
			2 -> {
				if (ItemCharges.drawnLastFrame == 0 && frames - itemChargesFrame < BANK_WAIT_FRAMES) {
					return false
				}
				itemChargesFrame = frames
				itemChargesStep = 3
			}
			else -> {
				if (frames - itemChargesFrame < 5) {
					return false
				}
				saveFrame("build/smoke/frame-itemcharges.png")
				val drawn: Int = ItemCharges.drawnLastFrame
				fakeBackpackItem(itemChargesSlot, -1)
				if (drawn == 0) {
					val grid = ClientApiImpl.widget(149, 0)
					val slot = grid?.child(itemChargesSlot)
					fail("Item Charges: no charges drawn on the glory in backpack slot " + itemChargesSlot + " (grid drawn " + grid?.isDrawnThisFrame() + ", " + grid?.childCount() + " slots; the slot holds " + slot?.itemId() + ", drawn " + slot?.isDrawnThisFrame() + "; the container " + ClientApiImpl.itemContainer(BACKPACK)?.itemId(itemChargesSlot) + ")")
					return true
				}
				println("SMOKE item charges: the glory(4) in backpack slot " + itemChargesSlot + " shows its charges (" + drawn + " drawn)")
				return true
			}
		}
		return false
	}

	/** Item Identification on a clean guam in the backpack: its "G" drawn. Frame: frame-itemidentification.png. */
	private fun itemIdentificationDone(): Boolean {
		val herbs: Int = ItemIdentification.TYPES.indexOf("HERB")
		when (itemIdentificationStep) {
			0 -> {
				val backpack = ClientApiImpl.itemContainer(BACKPACK) ?: return false
				for (slot in 0 until backpack.size()) {
					if (itemIdentificationSlot < 0 && backpack.itemId(slot) < 0) {
						itemIdentificationSlot = slot
					}
				}
				if (itemIdentificationSlot < 0) {
					fail("Item Identification: no empty backpack slot")
					return true
				}
				ItemIdentification.shown[herbs] = true
				openInventoryTab()
				itemIdentificationFrame = frames
				itemIdentificationStep = 1
			}
			1 -> {
				if (frames - itemIdentificationFrame < MARKER_FRAMES) {
					return false
				}
				fakeBackpackItem(itemIdentificationSlot, CLEAN_GUAM)
				itemIdentificationFrame = frames
				itemIdentificationStep = 2
			}
			2 -> {
				if (ItemIdentification.drawnLastFrame == 0 && frames - itemIdentificationFrame < BANK_WAIT_FRAMES) {
					return false
				}
				itemIdentificationFrame = frames
				itemIdentificationStep = 3
			}
			else -> {
				if (frames - itemIdentificationFrame < 5) {
					return false
				}
				saveFrame("build/smoke/frame-itemidentification.png")
				val drawn: Int = ItemIdentification.drawnLastFrame
				val label: String? = ItemIdentification.label(ClientApiImpl, CLEAN_GUAM)
				fakeBackpackItem(itemIdentificationSlot, -1)
				ItemIdentification.shown[herbs] = ItemIdentification.CATEGORY_DEFAULTS[herbs]
				if (drawn == 0) {
					fail("Item Identification: no label drawn on the guam in backpack slot " + itemIdentificationSlot)
					return true
				}
				println("SMOKE item identification: the clean guam in backpack slot " + itemIdentificationSlot + " labelled \"" + label + "\" (" + drawn + " drawn)")
				return true
			}
		}
		return false
	}

	/** Chat Notifications on "hello <name>" from Zezima: the name highlighted and underlined in the stored line. Frame: frame-chatnotifications.png. */
	private fun chatNotificationsDone(): Boolean {
		if (chatNotificationsFrame == -1) {
			ChatNotifications.highlightOwnName = true
			ChatLineWriter.writeMessage("Zezima", "hello " + USER + "!", -1, "Zezima", null, ChatType.PUBLIC, 0, "Zezima")
			chatNotificationsFrame = frames
			return false
		}
		if (frames - chatNotificationsFrame < MARKER_FRAMES) {
			return false
		}
		saveFrame("build/smoke/frame-chatnotifications.png")
		val line: String = com.jagex.game.runetek5.interaction.chat.ChatBox.CHAT_LINES[0]?.message ?: ""
		if (!line.contains("<u>")) {
			fail("Chat Notifications: the line reads \"" + line + "\"")
			return true
		}
		println("SMOKE chat notifications: \"" + line + "\"")
		return true
	}

	/** Chat Filter removing "buy gold" and collapsing a game line said twice. Frame: frame-chatfilter.png. */
	private fun chatFilterDone(): Boolean {
		val api = ClientApiImpl
		if (chatFilterFrame == -1) {
			ChatFilter.filteredWords = "gold"
			ChatFilter.filterType = ChatFilter.REMOVE_MESSAGE
			ChatFilter.collapseGameChat = true
			ChatFilter.compile()
			val before: Int = api.chatLineCount()
			ChatLineWriter.writeMessage("Spammer", "buy gold here", -1, "Spammer", null, ChatType.PUBLIC, 0, "Spammer")
			if (api.chatLineCount() != before) {
				fail("Chat Filter: the filtered line was stored")
				return true
			}
			ChatLineWriter.writeMessage(null, "Smoke test repeat.", -1, null, null, ChatType.GAME, 0, null)
			ChatLineWriter.writeMessage(null, "Smoke test repeat.", -1, null, null, ChatType.GAME, 0, null)
			chatFilterFrame = frames
			return false
		}
		if (frames - chatFilterFrame < MARKER_FRAMES) {
			return false
		}
		saveFrame("build/smoke/frame-chatfilter.png")
		val newest: String? = api.chatLine(0)?.message()
		val previous: String? = api.chatLine(1)?.message()
		ChatFilter.filteredWords = ""
		ChatFilter.filterType = ChatFilter.CENSOR_WORDS
		ChatFilter.collapseGameChat = false
		ChatFilter.compile()
		if (newest != "Smoke test repeat. (2)" || previous == "Smoke test repeat.") {
			fail("Chat Filter: the newest lines read \"" + newest + "\", \"" + previous + "\"")
			return true
		}
		println("SMOKE chat filter: the filtered line never stored, the repeat collapsed into \"" + newest + "\"")
		return true
	}

	/** Chat History's restore, clipboard and tab clearing. Frame: frame-chathistory.png. */
	private fun chatHistoryDone(): Boolean {
		val api = ClientApiImpl
		if (chatHistoryFrame == -1) {
			ChatHistory.history.clear()
			ChatHistory.history.addLast(ChatHistory.Kept(ChatType.PUBLIC, "Zezima", "kept from before", null))
			ChatHistory.restorePending = true
			ChatLineWriter.writeMessage(null, "Smoke test after login.", -1, null, null, ChatType.GAME, 0, null)
			chatHistoryFrame = frames
			return false
		}
		if (frames - chatHistoryFrame < MARKER_FRAMES) {
			return false
		}
		saveFrame("build/smoke/frame-chathistory.png")
		val restored: String? = api.chatLine(1)?.message()
		// the clipboard set and read, then given back what it held
		val held: String? = api.clipboard()
		val copied: Boolean = api.setClipboard("smoke clipboard") && api.clipboard() == "smoke clipboard"
		if (held != null) {
			api.setClipboard(held)
		}
		ChatHistory.clearTab(api, 4)
		var publicLeft: Int = 0
		for (i in 0 until api.chatLineCount()) {
			if (api.chatLine(i)?.type() == ChatType.PUBLIC) {
				publicLeft++
			}
		}
		if (restored != "kept from before" || publicLeft != 0) {
			fail("Chat History: restored \"" + restored + "\", " + publicLeft + " public lines after Clear history")
			return true
		}
		println("SMOKE chat history: \"" + restored + "\" put back before the next line, clipboard " + (if (copied) "set and read" else "not available") + ", the Public tab cleared")
		return true
	}

	/** Combat Level's combat tab text, and the Wilderness range at level 11. Frame: frame-combatlevel.png. */
	private fun combatLevelDone(): Boolean {
		val api = ClientApiImpl
		when (combatLevelStep) {
			0 -> {
				combatLevelFrame = frames
				combatLevelStep = 1
			}
			1 -> {
				if (frames - combatLevelFrame < MARKER_FRAMES) {
					return false
				}
				val l = CombatLevel.levels(api)
				combatLevelText = api.widget(CombatLevel.COMBAT_TAB, CombatLevel.COMBAT_LEVEL_TEXT)?.text() ?: ""
				if (combatLevelText != "Combat Lvl: " + CombatLevel.format(CombatLevel.precise(l.values, l.members))) {
					combatLevelFailure = "Combat Level: the combat tab reads \"" + combatLevelText + "\""
				}
				sendCommand("tele 3100 3600")
				combatLevelFrame = frames
				combatLevelStep = 2
			}
			2 -> {
				// the skull's text once the Wilderness overlay is open and drawn, a few frames on
				val skull = api.widget(CombatLevel.WILDERNESS, CombatLevel.WILDERNESS_RESIZABLE_TEXT)
				val shown: String = skull?.text() ?: ""
				val drawn: Boolean = skull != null && skull.isDrawnThisFrame() && shown.startsWith("Level: 11")
				if (!drawn && frames - combatLevelFrame < BANK_WAIT_FRAMES) {
					combatLevelShown = -1
					return false
				}
				if (combatLevelShown == -1) {
					combatLevelShown = frames
				}
				if (drawn && frames - combatLevelShown < MARKER_FRAMES / 4) {
					return false
				}
				saveFrame("build/smoke/frame-combatlevel.png")
				if (!drawn && combatLevelFailure == null) {
					combatLevelFailure = "Combat Level: the Wilderness text reads \"" + shown + "\""
				}
				combatLevelText += "; " + shown.replace("<br>", " ")
				sendCommand("tele 3094 3107")
				combatLevelFrame = frames
				combatLevelStep = 3
			}
			else -> {
				if (frames - combatLevelFrame < MARKER_FRAMES) {
					return false
				}
				combatLevelFailure?.let {
					fail(it)
					return true
				}
				println("SMOKE combat level: " + combatLevelText)
				return true
			}
		}
		return false
	}

	/** Virtual Levels' Attack at 100 in the Stats tab. Frame: frame-virtuallevels.png. */
	private fun virtualLevelsDone(): Boolean {
		val api = ClientApiImpl
		when (virtualLevelsStep) {
			0 -> {
				if (!openTab("Stats", "Skills")) {
					fail("Virtual Levels: no Stats tab stone")
					return true
				}
				virtualLevelsFrame = frames
				virtualLevelsStep = 1
			}
			1 -> {
				if (frames - virtualLevelsFrame < MARKER_FRAMES) {
					return false
				}
				virtualLevelsXp = client.statXp[0]
				client.statXp[0] = 14_391_160
				virtualLevelsFrame = frames
				virtualLevelsStep = 2
			}
			else -> {
				val base: String? = api.widget(VirtualLevels.STATS_TAB, XpTracker.STATS_COMPONENTS[0] - VirtualLevels.BASE_OFFSET)?.text()
				if (base != "100" && frames - virtualLevelsFrame < MARKER_FRAMES * 2) {
					virtualLevelsShown = -1
					return false
				}
				// a few frames with the level up: the OpenGL toolkits' frame is read from the last one shown
				if (virtualLevelsShown == -1) {
					virtualLevelsShown = frames
				}
				if (base == "100" && frames - virtualLevelsShown < 5) {
					return false
				}
				saveFrame("build/smoke/frame-virtuallevels.png")
				val total: String? = api.widget(VirtualLevels.STATS_TAB, VirtualLevels.TOTAL)?.text()
				client.statXp[0] = virtualLevelsXp
				VirtualLevels.enabled = false
				VirtualLevels.restore(api)
				VirtualLevels.enabled = true
				if (base != "100") {
					fail("Virtual Levels: Attack's level reads \"" + base + "\"")
					return true
				}
				println("SMOKE virtual levels: Attack at 14.4m experience reads " + base + ", \"" + total + "\"")
				return true
			}
		}
		return false
	}

	/** Metronome on for three seconds: about five server ticks, a sound played. */
	private fun metronomeDone(): Boolean {
		if (metronomeStart == -1L) {
			Metronome.enabled = true
			Metronome.lastSound = -1
			metronomeTicks = PluginRuntime.serverTicks()
			metronomeStart = lang.currentTimeMillis()
			return false
		}
		val elapsed: Long = lang.currentTimeMillis() - metronomeStart
		if (elapsed < 3000L) {
			return false
		}
		val ticks: Int = PluginRuntime.serverTicks() - metronomeTicks
		val sound: Int = Metronome.lastSound
		Metronome.enabled = false
		if (ticks < 4 || ticks > 6 || sound < 0) {
			fail("Metronome: " + ticks + " server ticks in " + elapsed + " ms, last sound " + sound)
			return true
		}
		println("SMOKE metronome: " + ticks + " server ticks in " + elapsed + " ms, sound " + sound + " played")
		return true
	}

	/** Death Indicator after a pretend death here: the arrow, the infobox and the world map's mark. Frame: frame-deathindicator.png. */
	private fun deathIndicatorDone(): Boolean {
		val api = ClientApiImpl
		when (deathIndicatorStep) {
			0 -> {
				ChatLineWriter.writeMessage(null, DeathIndicator.DEATH_MESSAGE, -1, null, null, ChatType.GAME, 0, null)
				ChatLineWriter.writeMessage(null, "Your gravestone has appeared and will last 5 minutes before crumbling to dust. You", -1, null, null, ChatType.GAME, 0, null)
				deathIndicatorFrame = frames
				deathIndicatorStep = 1
			}
			1 -> {
				if (frames - deathIndicatorFrame < MARKER_FRAMES) {
					return false
				}
				val arrow: Boolean = com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate.HINT_ARROWS[7] != null
				if (!DeathIndicator.hasDeath() || !arrow || DeathIndicator.infobox == null) {
					deathIndicatorFailure = "Death Indicator: death " + DeathIndicator.hasDeath() + ", arrow " + arrow + ", infobox " + (DeathIndicator.infobox != null)
				}
				if (!openTab("World Map", "View World Map")) {
					println("SMOKE death indicator: no world map button found; the map mark is not checked")
					deathIndicatorStep = 3
					return false
				}
				deathIndicatorFrame = frames
				deathIndicatorStep = 2
			}
			2 -> {
				if (!DeathIndicator.mapMarked && frames - deathIndicatorFrame < BANK_WAIT_FRAMES) {
					return false
				}
				saveFrame("build/smoke/frame-deathindicator.png")
				if (!DeathIndicator.mapMarked && deathIndicatorFailure == null) {
					deathIndicatorFailure = "Death Indicator: the world map was not marked"
				}
				closeModals()
				deathIndicatorStep = 3
			}
			else -> {
				DeathIndicator.clear(api)
				deathIndicatorFailure?.let {
					fail(it)
					return true
				}
				println("SMOKE death indicator: the grave's arrow, infobox and world map mark after the death and gravestone lines")
				return true
			}
		}
		return false
	}

	/** NPC Aggression Timer's timer and square. Frame: frame-npcaggression.png. */
	private fun npcAggressionDone(): Boolean {
		if (npcAggressionFrame == -1) {
			NpcAggression.npcUnaggroAlwaysActive = true
			NpcAggression.npcUnaggroShowAreaLines = true
			npcAggressionFrame = frames
			return false
		}
		if (frames - npcAggressionFrame < MARKER_FRAMES) {
			return false
		}
		saveFrame("build/smoke/frame-npcaggression.png")
		val timer: Boolean = NpcAggression.timer != null
		val lines: Int = NpcAggression.linesDrawn
		val left: Long = NpcAggression.remainingMillis()
		NpcAggression.npcUnaggroAlwaysActive = false
		NpcAggression.npcUnaggroShowAreaLines = false
		if (!timer || lines == 0) {
			fail("NPC Aggression Timer: timer " + timer + ", " + lines + " edge pieces drawn")
			return true
		}
		println("SMOKE npc aggression: the timer at " + left / 1000 + "s, " + lines + " edge pieces of the square drawn")
		return true
	}

	/** Hunter on a box trap laid east of the player: its open pie. Frame: frame-hunter.png. */
	private fun hunterDone(player: PlayerEntity): Boolean {
		if (hunterFrame == -1) {
			PluginRuntime.locChanged(player.movementBaseX[0] + 1, player.movementBaseY[0], player.level.toInt(), 2, -1, 19187, 10, 0)
			hunterFrame = frames
			return false
		}
		if (frames - hunterFrame < MARKER_FRAMES) {
			return false
		}
		saveFrame("build/smoke/frame-hunter.png")
		val traps: Int = Hunter.traps.size
		val drawn: Int = Hunter.drawnLastFrame
		Hunter.traps.clear()
		if (traps != 1 || drawn != 1) {
			fail("Hunter: " + traps + " traps, " + drawn + " drawn")
			return true
		}
		println("SMOKE hunter: the box trap laid beside the player marked with its pie")
		return true
	}

	/** Implings in Puro-Puro: the highlights and spawn tiles, then home. Frame: frame-implings.png. */
	private fun implingsDone(player: PlayerEntity): Boolean {
		when (implingsStep) {
			0 -> {
				Implings.modes.fill(Implings.HIGHLIGHT)
				Implings.showspawn = true
				sendCommand("tele 2592 4318 0")
				implingsFrame = frames
				implingsStep = 1
			}
			1 -> {
				// the implings come a little after the area: wait for both, or give up
				val inside: Boolean = player.movementBaseX[0] + MapRegion.sceneBaseX in 2560..2623 && player.movementBaseY[0] + MapRegion.sceneBaseY in 4288..4351
				if (!(inside && Implings.spawnsDrawn > 0 && Implings.drawnLastFrame > 0) && frames - implingsFrame < BANK_WAIT_FRAMES * 2) {
					return false
				}
				implingsFrame = frames
				implingsStep = 2
			}
			2 -> {
				if (frames - implingsFrame < MARKER_FRAMES) {
					return false
				}
				saveFrame("build/smoke/frame-implings.png")
				val spawns: Int = Implings.spawnsDrawn
				val implings: Int = Implings.drawnLastFrame
				if (spawns == 0 || implings == 0) {
					implingsFailure = "Implings: " + spawns + " spawn tiles and " + implings + " implings drawn in Puro-Puro"
				} else {
					println("SMOKE implings: in Puro-Puro, " + spawns + " spawn tiles and " + implings + " implings drawn")
				}
				for (k in Implings.KINDS.indices) {
					Implings.modes[k] = Implings.KINDS[k].defaultMode
				}
				Implings.showspawn = false
				sendCommand("tele " + SCENE_HOME[0] + " " + SCENE_HOME[1] + " " + SCENE_HOME[2])
				implingsFrame = frames
				implingsStep = 3
			}
			else -> {
				if (frames - implingsFrame < BANK_WAIT_FRAMES / 2) {
					return false
				}
				implingsFailure?.let {
					fail(it)
					return true
				}
				return true
			}
		}
		return false
	}

	/** Cooking's panel after two cooked lines and one burnt. Frame: frame-cooking.png. */
	private fun cookingDone(): Boolean {
		if (cookingFrame == -1) {
			ChatLineWriter.writeMessage(null, "You successfully cook a trout.", -1, null, null, ChatType.GAME_FILTERED, 0, null)
			ChatLineWriter.writeMessage(null, "You roast a lobster.", -1, null, null, ChatType.GAME_FILTERED, 0, null)
			ChatLineWriter.writeMessage(null, "You accidentally burn the shark.", -1, null, null, ChatType.GAME_FILTERED, 0, null)
			cookingFrame = frames
			return false
		}
		if (frames - cookingFrame < MARKER_FRAMES) {
			return false
		}
		saveFrame("build/smoke/frame-cooking.png")
		val cooked: Int = Cooking.session?.cookAmount ?: 0
		val burnt: Int = Cooking.session?.burnAmount ?: 0
		val drawn: Boolean = Cooking.panelDrawn
		Cooking.session = null
		if (cooked != 2 || burnt != 1 || !drawn) {
			fail("Cooking: " + cooked + " cooked, " + burnt + " burnt, panel drawn " + drawn)
			return true
		}
		println("SMOKE cooking: 2 cooked and 1 burnt counted from Void's lines, the panel drawn")
		return true
	}

	private fun keyRemapFailed(why: String): Boolean {
		fail("Key Remapping: " + why)
		KeyRemapping.enabled = false
		KeyRemapping.unlockChat(ClientApiImpl)
		return true
	}

	/** The first container of the root interface with nothing open in it, as subInterfacesDone finds one; -1 for none. */
	private fun freeModalParent(): Int {
		val root: Array<Component?> = Interface.interfaces!![ClientState.BASE_WINDOW_ID] ?: return -1
		for (c in root) {
			if (c != null && c.type === ComponentType.CONTAINER && c.parentId != -1 && InterfaceManager.OPEN_INTERFACES.get(c.idHash.toLong()) == null) {
				return c.idHash
			}
		}
		return -1
	}

	/**
	 * Tile Indicators with all three tiles on: the minimap's flag set two tiles east of the player stands for a
	 * walk's destination; the destination and the true tile must be drawn (the hovered one needs the mouse
	 * over the game). Frame: frame-tileindicators.png.
	 */
	private fun tileIndicatorsDone(player: PlayerEntity): Boolean {
		if (tileStep == 0) {
			TileIndicators.enabled = true
			TileIndicators.highlightDestinationTile = true
			TileIndicators.highlightHoveredTile = true
			TileIndicators.highlightCurrentTile = true
			Minimap.minimapFlagX = player.movementBaseX[0] + 2
			Minimap.minimapFlagY = player.movementBaseY[0]
			tileFrame = frames
			tileStep = 1
			return false
		}
		if (frames - tileFrame < MARKER_FRAMES) {
			return false
		}
		val drawn: BooleanArray = TileIndicators.drawn
		if (!drawn[0] || !drawn[2]) {
			fail("Tile Indicators drew the destination " + drawn[0] + " and the true tile " + drawn[2] + " (flag " + Minimap.minimapFlagX + "," + Minimap.minimapFlagY + ", API " + ClientApiImpl.destinationTileX() + ")")
			return true
		}
		saveFrame("build/smoke/frame-tileindicators.png")
		println("SMOKE tile indicators: destination " + ClientApiImpl.destinationTileX() + "," + ClientApiImpl.destinationTileY() + " and true tile drawn, hovered " + drawn[1])
		Minimap.resetMinimapFlag()
		TileIndicators.enabled = false
		return true
	}

	/**
	 * Timers and Buffs: an antifire and a teleblock line through the runtime's chat hook (the plugin's own
	 * listener), an antipoison's protection on the poison varp and a vengeance cast; their infoboxes (item
	 * icons and spell sprites) must be drawn. Then the antifire runs out. Frame: frame-timers.png.
	 */
	private fun timersDone(): Boolean {
		if (timersStep == 0) {
			TimersAndBuffs.enabled = true
			PluginRuntime.chatMessage(0, null, "You drink the antifire (4).", null)
			PluginRuntime.chatMessage(0, null, "You have been teleblocked.", null)
			TimersAndBuffs.poison(-200)
			ClientApiImpl.localPlayer()?.let { TimersAndBuffs.animation(it, TimersAndBuffs.VENGEANCE_ANIMATION) }
			timersFrame = frames
			timersStep = 1
			return false
		}
		if (frames - timersFrame < MARKER_FRAMES) {
			return false
		}
		val layer: InfoBoxes.Layer? = InfoBoxes.layers[InfoBoxes.DEFAULT_LAYER]
		val names: List<String> = layer?.drawn?.map { it.box.box.name } ?: emptyList()
		if (timersStep == 1) {
			val expected: List<String> = listOf("Antifire", "Teleblock", "Antipoison", "Vengeance", "Vengeance active")
			if (!names.containsAll(expected)) {
				fail("Timers and Buffs drew the infoboxes " + names + ", not " + expected)
				return true
			}
			saveFrame("build/smoke/frame-timers.png")
			println("SMOKE timers: " + names)
			PluginRuntime.chatMessage(0, null, "<col=480000>Your resistance to dragonfire has run out.", null)
			timersFrame = frames
			timersStep = 2
			return false
		}
		if (names.contains("Antifire")) {
			fail("the antifire infobox stayed after its run-out line")
			return true
		}
		TimersAndBuffs.clear()
		return true
	}

	/**
	 * Status Bars on with counters and icons (lifepoints left, prayer right), a shark as the hovered backpack
	 * item for the restore preview; the bars must stand beside the side panel. Frame: frame-statusbars.png.
	 */
	private fun statusBarsDone(): Boolean {
		if (statusStep == 0) {
			StatusBars.enabled = true
			StatusBars.enableCounter = true
			StatusBars.enableSkillIcon = true
			StatusBars.enableRestorationBars = true
			StatusBars.barsDisplayed = true
			statusFrame = frames
			statusStep = 1
			return false
		}
		// the menu is rebuilt every cycle: put the shark back each frame
		StatusBars.hoveredItem = SHARK
		if (frames - statusFrame < MARKER_FRAMES) {
			return false
		}
		// after a switch, wait for the other screen's root interface (a switch to resizable resizes the window first)
		val wantedRoot: Int = if (WindowMode.getWindowMode() === WindowModeType.FIXED) StatusBarsOverlay.FIXED else StatusBarsOverlay.RESIZABLE
		if (ClientApiImpl.topInterface() != wantedRoot && frames - statusFrame < BANK_WAIT_FRAMES) {
			return false
		}
		val d: IntArray = StatusBarsOverlay.drawn
		val mode: String = if (ClientApiImpl.topInterface() == StatusBarsOverlay.FIXED) "fixed" else "resizable"
		if (statusStep == 2 && ClientApiImpl.topInterface() != wantedRoot) {
			println("SMOKE status bars: the switch to the other screen did not happen; only " + mode + " checked")
			StatusBars.hoveredItem = -1
			StatusBars.enabled = false
			return true
		}
		if (d[3] <= 0) {
			fail("Status Bars drew no bars beside the side panel (" + mode + ")")
			return true
		}
		// on the resizable screen both stand left of the side panel's box (746:75), 4 pixels apart and from it, as tall as it
		val box = ClientApiImpl.widget(StatusBarsOverlay.RESIZABLE, RESIZABLE_SIDE_BOX)
		if (mode == "resizable" && box != null && (d[1] != box.screenX() - 4 - StatusBars.barWidth || d[0] != d[1] - 4 - StatusBars.barWidth || d[2] != box.screenY() || d[3] != box.height())) {
			fail("on the resizable screen the bars stand at x " + d[0] + " and " + d[1] + ", y " + d[2] + ", " + d[3] + " tall, not left of the box at " + box.screenX() + "," + box.screenY() + " (" + box.height() + " tall)")
			return true
		}
		// on the fixed screen they stand on the stone columns either side of the tab area (548:194, x 547 to 737)
		if (mode == "fixed" && (d[0] != FIXED_TAB_AREA_X - StatusBars.DEFAULT_WIDTH || d[1] != FIXED_TAB_AREA_X + FIXED_TAB_AREA_WIDTH)) {
			fail("on the fixed screen the bars stand at x " + d[0] + " and " + d[1] + ", not on the columns at " + (FIXED_TAB_AREA_X - StatusBars.DEFAULT_WIDTH) + " and " + (FIXED_TAB_AREA_X + FIXED_TAB_AREA_WIDTH))
			return true
		}
		saveFrame("build/smoke/frame-statusbars-" + mode + ".png")
		println("SMOKE status bars (" + mode + "): left at x " + d[0] + ", right at x " + d[1] + ", y " + d[2] + ", " + d[3] + " tall; lifepoints " + StatusBars.current(ClientApiImpl, StatusBars.HITPOINTS) + "/" + StatusBars.maximum(ClientApiImpl, StatusBars.HITPOINTS) + ", a shark restores " + StatusBars.restore(ClientApiImpl, StatusBars.HITPOINTS))
		if (statusStep == 1) {
			// the other screen layout, then back
			statusStartMode = WindowMode.getWindowMode()
			sendCommand(if (statusStartMode === WindowModeType.FIXED) "wm2" else "wm1")
			statusFrame = frames
			statusStep = 2
			return false
		}
		sendCommand(if (statusStartMode === WindowModeType.FIXED) "wm1" else "wm2")
		StatusBars.hoveredItem = -1
		StatusBars.enabled = false
		return true
	}

	/**
	 * Opens the bank and waits for Bank Organizer's box over the coins, the smoke account's only bank item. Coins
	 * are in no category, so the test gives them two colours itself: the matching is the unit tests' job
	 * (BankOrganizerTest), this checks the bank's grid, its open state and the drawing.
	 */
	private fun bankOrganizerDone(): Boolean {
		if (bankStep == 0) {
			BankOrganizer.enabled = true
			BankOrganizer.showBoxes = true
			sendCommand("bank")
			bankFrame = frames
			bankStep = 1
			return false
		}
		if (bankStep == 1) {
			// the bank's arrival works the marks out again: put the test's back each frame until the box shows
			if (!BankOrganizer.marks.containsKey(COINS)) {
				BankOrganizer.marks[COINS] = intArrayOf(BankCategories.DEFAULT_COLOURS[1], BankCategories.DEFAULT_COLOURS[2])
			}
			if (BankOrganizer.drawnBoxes == 0) {
				if (frames - bankFrame > BANK_WAIT_FRAMES) {
					fail("Bank Organizer drew no box over the bank (" + BankOrganizer.BANK_INTERFACE + ":" + BankOrganizer.BANK_ITEMS + ") in " + BANK_WAIT_FRAMES + " frames")
					return true
				}
				return false
			}
			bankFrame = frames
			bankStep = 2
			return false
		}
		if (frames - bankFrame < MARKER_FRAMES) {
			return false
		}
		saveFrame("build/smoke/frame-bank.png")
		println("SMOKE bank organizer: " + BankOrganizer.drawnBoxes + " box(es) over the bank grid " + BankOrganizer.BANK_INTERFACE + ":" + BankOrganizer.BANK_ITEMS)
		// the quest state behind Bank Cleanup, on the account's own variables: Cook's Assistant is quest 6, varp 29 (1 started, 2 finished)
		val api = ClientApiImpl
		val cooks: Int = api.questState(COOKS_ASSISTANT)
		val expected: Int = when {
			api.varp(29) >= 2 -> QuestState.FINISHED
			api.varp(29) >= 1 -> QuestState.IN_PROGRESS
			else -> QuestState.NOT_STARTED
		}
		if (api.questName(COOKS_ASSISTANT) != "Cook's Assistant" || cooks != expected) {
			fail("quest " + COOKS_ASSISTANT + " is \"" + api.questName(COOKS_ASSISTANT) + "\" in state " + cooks + " with varp 29 at " + api.varp(29) + ", not Cook's Assistant in state " + expected)
		}
		println("SMOKE bank cleanup: " + api.questCount() + " quests, Cook's Assistant state " + cooks + " (varp 29 = " + api.varp(29) + "), " + BankCleanupRules.size() + " rules")
		closeModals()
		BankOrganizer.enabled = false
		BankOrganizer.marks.clear()
		return true
	}

	/** A level as the stat packet sets it: the plugins hear of it first, then the client stores it. */
	private fun setLevel(skill: Int, level: Int) {
		val xp: Int = client.statXp[skill]
		PluginRuntime.statChanged(skill, xp, xp, level)
		client.statLevels[skill] = level
	}

	/** Attack's and Defence's levels and Defence's base before the boosts check, to put back. */
	private var boostsAttack: Int = -1
	private var boostsDefence: Int = -1
	private var boostsDefenceBase: Int = -1

	/** Attack, Defence and Boosts Information's settings back as they were. */
	private fun restoreBoosts() {
		if (boostsAttack < 0) {
			return
		}
		StatBaseLevels.statBaseLevels[Skill.DEFENCE] = boostsDefenceBase
		setLevel(Skill.ATTACK, boostsAttack)
		setLevel(Skill.DEFENCE, boostsDefence)
		boostsAttack = -1
		Boosts.quiet = false
		Boosts.context()?.config()?.let { Boosts.readSettings(it) }
		Boosts.configChanged()
		Boosts.reset()
	}

	/**
	 * Boosts Information: Attack boosted by five and then down one (a boost wearing off: the buff timer
	 * starts, and the threshold of four notifies), Defence's base made 10 and it drained to 6 and back up
	 * one (a drain coming back: the debuff timer). Then the infoboxes (-boosts-infobox: two skills and two
	 * timers), the panel (-boosts-panel) and the compact list with relative boosts (-boosts-compact).
	 * Left boosted for the overlay check when that runs too.
	 */
	private fun boostsDone(): Boolean {
		val api = ClientApiImpl
		if (boostsStep == 0) {
			Boosts.context() ?: return false
			boostsAttack = client.statLevels[Skill.ATTACK]
			boostsDefence = client.statLevels[Skill.DEFENCE]
			boostsDefenceBase = StatBaseLevels.statBaseLevels[Skill.DEFENCE]
			Boosts.enabled = true
			Boosts.quiet = true
			Boosts.displayBoosts = Boosts.BOOSTS_COMBAT
			Boosts.relativeBoost = false
			Boosts.displayIndicators = true
			Boosts.displayPanel = false
			Boosts.compactDisplay = false
			Boosts.displayNextBuffChange = Boosts.CHANGE_BOOSTED
			Boosts.displayNextDebuffChange = Boosts.CHANGE_BOOSTED
			Boosts.boostThreshold = 4
			Boosts.notifyOnBoost = true
			Boosts.configChanged()
			Boosts.lastNotification = null
			val base: Int = api.skillBaseLevel(Skill.ATTACK)
			setLevel(Skill.ATTACK, base + 5)
			setLevel(Skill.ATTACK, base + 4)
			StatBaseLevels.statBaseLevels[Skill.DEFENCE] = 10
			setLevel(Skill.DEFENCE, 6)
			setLevel(Skill.DEFENCE, 7)
			boostsFrame = frames
			boostsStep = 1
			return false
		}
		if (frames - boostsFrame < MARKER_FRAMES) {
			return false
		}
		if (boostsStep == 1) {
			checkImage("-boosts-infobox", SHOT.replace(".png", "-boosts-infobox.png"))
			val base: Int = api.skillBaseLevel(Skill.ATTACK)
			if (Boosts.lastNotification != "Attack level is getting low!") {
				fail("the boost falling to the threshold notified " + Boosts.lastNotification + ", not \"Attack level is getting low!\"")
			}
			if (!Boosts.toDisplay[Skill.ATTACK] || !Boosts.toDisplay[Skill.DEFENCE] || !Boosts.changedUp || !Boosts.changedDown) {
				fail("Boosts shows Attack " + Boosts.toDisplay[Skill.ATTACK] + " and Defence " + Boosts.toDisplay[Skill.DEFENCE] + ", up " + Boosts.changedUp + ", down " + Boosts.changedDown)
			}
			val down: Int = Boosts.changeDownSeconds(api)
			val up: Int = Boosts.changeUpSeconds(api)
			if (down !in 50..60 || up !in 50..60) {
				fail("the boost timers read " + down + " s (buff) and " + up + " s (debuff), not about a minute")
			}
			val layer: InfoBoxes.Layer? = InfoBoxes.layers[InfoBoxes.DEFAULT_LAYER]
			val texts: List<String> = layer?.drawn?.map { it.box.box.name + "=" + it.text } ?: emptyList()
			val expected: List<String> = listOf("Boost Attack=" + (base + 4), "Boost Defence=7")
			if (texts.size != 4 || texts.subList(0, 2) != expected || !texts[2].startsWith("BoostsPlugin_StatChangeIndicator=") || !texts[3].startsWith("BoostsPlugin_StatChangeIndicator=")) {
				fail("the infoboxes drawn are " + texts + ", not " + expected + " and the two timers")
			}
			boostsSummary = "Boosts: infoboxes " + texts.size + " (Attack " + (base + 4) + ", Defence 7, timers " + down + " s and " + up + " s), notified at the threshold"
			Boosts.displayIndicators = false
			Boosts.displayPanel = true
			boostsFrame = frames
			boostsStep = 2
			return false
		}
		if (boostsStep == 2) {
			checkImage("-boosts-panel", SHOT.replace(".png", "-boosts-panel.png"))
			val base: Int = api.skillBaseLevel(Skill.ATTACK)
			val rows: List<String>? = BoostsOverlay.panelRows
			if (rows == null || rows.size != 4 || !rows[0].startsWith("Next + restore in ") || !rows[1].startsWith("Next - restore in ") || rows[2] != "Attack " + (base + 4) + "/" + base || rows[3] != "Defence 7/10") {
				fail("the boosts panel drew " + rows)
			}
			boostsSummary += ", panel " + rows
			Boosts.displayPanel = false
			Boosts.compactDisplay = true
			Boosts.relativeBoost = true
			boostsFrame = frames
			boostsStep = 3
			return false
		}
		checkImage("-boosts-compact", SHOT.replace(".png", "-boosts-compact.png"))
		val rows: List<String>? = BoostsOverlay.compactRows
		if (rows == null || rows.size != 4 || rows[0] != "Attack +4" || rows[1] != "Defence -3" || !rows[2].startsWith("- ") || !rows[3].startsWith("+ ")) {
			fail("the compact boosts drew " + rows)
		}
		boostsSummary += ", compact " + rows
		firstImage += "; " + boostsSummary
		if (!OVERLAY_DRAG) {
			restoreBoosts()
		} else {
			// the overlay check moves the panel and the infoboxes: both shown
			Boosts.compactDisplay = false
			Boosts.relativeBoost = false
			Boosts.displayPanel = true
			Boosts.displayIndicators = true
		}
		return true
	}

	/** The middle of [entry] this frame. */
	private fun middle(entry: OverlayLayout.Entry, out: IntArray) {
		out[0] = entry.x + entry.width / 2
		out[1] = entry.y + entry.height / 2
	}

	private val dragPoint: IntArray = IntArray(4)

	/**
	 * The plugins' panels managed as a player would with Alt held (OverlayLayout's test mouse and key):
	 * the outlines and corners (-overlay-edit); Boosts' panel pressed (the press is the layout's, never the
	 * game's), dragged 150 right and 60 down and let go, kept as a place of its own; dragged into the top
	 * right corner, snapped there; the infobox panel narrowed from its right edge to two boxes a row; one
	 * box detached into a panel of its own and dropped back on the first; Shift's menu on the panel
	 * (Configure and Overlay Origin); and Alt and a right click putting the panel back.
	 */
	private fun overlayDragDone(): Boolean {
		val panelKey: String = "boosts.BoostsOverlay"
		if (dragStep == 0) {
			OverlayLayout.testMouse = true
			OverlayLayout.testKeyHeld = true
			OverlayLayout.testButtonDown = false
			OverlayLayout.testMouseX = 600
			OverlayLayout.testMouseY = 300
			dragFrame = frames
			dragStep = 1
			return false
		}
		if (frames - dragFrame < 20) {
			return false
		}
		val panel: OverlayLayout.Entry? = OverlayLayout.entryNamed("BoostsOverlay")
		if (panel == null || panel.width == 0) {
			fail("the boosts panel is not drawn for the overlay check")
			return true
		}
		if (dragStep == 1) {
			if (!OverlayLayout.editMode) {
				fail("holding the drag key did not start managing the panels")
				return true
			}
			checkImage("-overlay-edit", SHOT.replace(".png", "-overlay-edit.png"))
			middle(panel, dragPoint)
			OverlayLayout.testMouseX = dragPoint[0]
			OverlayLayout.testMouseY = dragPoint[1]
			dragFrame = frames
			dragStep = 2
			return false
		}
		if (dragStep == 2) {
			// the panel is under the mouse now: pressing it is the layout's
			dragStartX = panel.x
			dragStartY = panel.y
			if (!OverlayLayout.mousePressed(OverlayLayout.testMouseX, OverlayLayout.testMouseY, MouseLog.TYPE_PRESS_LEFT) || !OverlayLayout.dragging) {
				fail("a press on the boosts panel with the drag key held did not start dragging it")
				return true
			}
			OverlayLayout.testButtonDown = true
			OverlayLayout.testMouseX += 150
			OverlayLayout.testMouseY += 60
			dragFrame = frames
			dragStep = 3
			return false
		}
		if (dragStep == 3) {
			OverlayLayout.testButtonDown = false
			dragFrame = frames
			dragStep = 4
			return false
		}
		if (dragStep == 4) {
			if (!panel.overlay.hasPreferredLocation || panel.x != dragStartX + 150 || panel.y != dragStartY + 60 || OverlayLayout.dragging) {
				fail("the boosts panel went from " + dragStartX + "," + dragStartY + " to " + panel.x + "," + panel.y + ", not 150 right and 60 down")
				return true
			}
			if (OverlayLayout.config().get(panelKey + "_preferredLocation", "").isEmpty()) {
				fail("the boosts panel's place was not kept")
			}
			dragSummary = "dragged from " + dragStartX + "," + dragStartY + " to " + panel.x + "," + panel.y + " (kept as " + OverlayLayout.config().get(panelKey + "_preferredLocation", "") + " from " + panel.overlay.originX + " " + panel.overlay.originY + ")"
			// into the top right corner
			middle(panel, dragPoint)
			OverlayLayout.testMouseX = dragPoint[0]
			OverlayLayout.testMouseY = dragPoint[1]
			OverlayLayout.mousePressed(dragPoint[0], dragPoint[1], MouseLog.TYPE_PRESS_LEFT)
			OverlayLayout.testButtonDown = true
			OverlayLayout.cornerBox(OverlayPosition.TOP_RIGHT, dragPoint)
			OverlayLayout.testMouseX = dragPoint[0] + dragPoint[2] / 2
			OverlayLayout.testMouseY = dragPoint[1] + dragPoint[3] / 2
			dragFrame = frames
			dragStep = 5
			return false
		}
		if (dragStep == 5) {
			OverlayLayout.testButtonDown = false
			dragFrame = frames
			dragStep = 6
			return false
		}
		if (dragStep == 6) {
			// the top right corner is left of the minimap's frame on the resizable screen
			val viewMiddle: Int = PluginRuntime.viewX() + PluginRuntime.viewWidth() / 2
			if (panel.overlay.preferredPosition !== OverlayPosition.TOP_RIGHT || panel.overlay.hasPreferredLocation || panel.x < viewMiddle) {
				fail("dropped on the top right corner, the boosts panel is " + panel.overlay.preferredPosition + " at " + panel.x + "," + panel.y)
				return true
			}
			checkImage("-overlay-snapped", SHOT.replace(".png", "-overlay-snapped.png"))
			dragSummary += ", snapped to " + panel.overlay.preferredPosition + " at " + panel.x + "," + panel.y
			// the infobox panel narrowed from its right edge
			val boxes: OverlayLayout.Entry? = OverlayLayout.entryNamed(InfoBoxes.DEFAULT_LAYER)
			if (boxes == null || boxes.width == 0) {
				fail("the infobox panel is not drawn for the overlay check")
				return true
			}
			dragStartX = boxes.width
			dragStartY = boxes.height
			OverlayLayout.testMouseX = boxes.x + boxes.width - 2
			OverlayLayout.testMouseY = boxes.y + boxes.height / 2
			dragFrame = frames
			dragStep = 7
			return false
		}
		val boxes: OverlayLayout.Entry? = OverlayLayout.entryNamed(InfoBoxes.DEFAULT_LAYER)
		if (boxes == null) {
			fail("the infobox panel went")
			return true
		}
		if (dragStep == 7) {
			if (!OverlayLayout.mousePressed(OverlayLayout.testMouseX, OverlayLayout.testMouseY, MouseLog.TYPE_PRESS_LEFT) || !OverlayLayout.resizing) {
				fail("a press on the infobox panel's right edge did not start resizing it")
				return true
			}
			OverlayLayout.testButtonDown = true
			val size: Int = OverlayLayout.infoBoxSize
			OverlayLayout.testMouseX = boxes.x + 2 * (size + 1)
			dragFrame = frames
			dragStep = 8
			return false
		}
		if (dragStep == 8) {
			OverlayLayout.testButtonDown = false
			dragFrame = frames
			dragStep = 9
			return false
		}
		if (dragStep == 9) {
			val size: Int = OverlayLayout.infoBoxSize
			if (boxes.overlay.preferredWidth <= 0 || boxes.width > 2 * (size + 1) || boxes.height < 2 * size) {
				fail("narrowed from " + dragStartX + "x" + dragStartY + ", the infobox panel is " + boxes.width + "x" + boxes.height + " (preferred width " + boxes.overlay.preferredWidth + "), not two boxes a row")
				return true
			}
			checkImage("-overlay-resized", SHOT.replace(".png", "-overlay-resized.png"))
			dragSummary += ", infoboxes narrowed from " + dragStartX + "x" + dragStartY + " to " + boxes.width + "x" + boxes.height
			// hover the first box (Attack's): its tooltip, and its menu for the detach
			OverlayLayout.testMouseX = boxes.x + 5
			OverlayLayout.testMouseY = boxes.y + 5
			dragFrame = frames
			dragStep = 10
			return false
		}
		if (dragStep == 10) {
			if (!Tooltips.drawnLastFrame.contains("Attack boost")) {
				fail("hovering Attack's infobox showed the tooltips " + Tooltips.drawnLastFrame + ", not \"Attack boost\"")
			}
			val detach = boxes.overlay.menuEntries().firstOrNull { it.option == "Detach" }
			if (detach == null) {
				fail("the hovered infobox offers " + boxes.overlay.menuEntries().map { it.option } + ", no Detach")
				return true
			}
			detach.action.run()
			if (InfoBoxes.layers.size != 2) {
				fail("Detach left " + InfoBoxes.layers.size + " infobox panels, not 2")
				return true
			}
			dragFrame = frames
			dragStep = 11
			return false
		}
		if (dragStep == 11) {
			val detached: OverlayLayout.Entry? = OverlayLayout.entries.firstOrNull { it.overlay is InfoBoxes.Layer && it.overlay.name != InfoBoxes.DEFAULT_LAYER }
			if (detached == null || detached.width == 0) {
				fail("the detached infobox panel is not drawn")
				return true
			}
			dragSummary += ", Attack's box detached into " + detached.overlay.name
			// drag it by its middle onto the first panel: dropped there, the two merge
			middle(detached, dragPoint)
			OverlayLayout.testMouseX = dragPoint[0]
			OverlayLayout.testMouseY = dragPoint[1]
			dragFrame = frames
			dragStep = 12
			return false
		}
		if (dragStep == 12) {
			val detached: OverlayLayout.Entry = OverlayLayout.entries.first { it.overlay is InfoBoxes.Layer && it.overlay.name != InfoBoxes.DEFAULT_LAYER }
			OverlayLayout.mousePressed(OverlayLayout.testMouseX, OverlayLayout.testMouseY, MouseLog.TYPE_PRESS_LEFT)
			OverlayLayout.testButtonDown = true
			OverlayLayout.testMouseX += boxes.x + boxes.width / 2 - (detached.x + detached.width / 2)
			OverlayLayout.testMouseY += boxes.y + boxes.height / 2 - (detached.y + detached.height / 2)
			dragFrame = frames
			dragStep = 13
			return false
		}
		if (dragStep == 13) {
			OverlayLayout.testButtonDown = false
			dragFrame = frames
			dragStep = 14
			return false
		}
		if (dragStep == 14) {
			if (InfoBoxes.layers.size != 1 || (InfoBoxes.layers[InfoBoxes.DEFAULT_LAYER]?.boxes?.size ?: 0) != Skill.COUNT + 2) {
				fail("dropped on the first infobox panel, the detached one did not merge back: " + InfoBoxes.layers.keys)
				return true
			}
			dragSummary += " and dropped back on the first"
			// Shift's menu on the panel
			OverlayLayout.testKeyHeld = false
			OverlayLayout.testShift = true
			middle(panel, dragPoint)
			OverlayLayout.testMouseX = dragPoint[0]
			OverlayLayout.testMouseY = dragPoint[1]
			dragFrame = frames
			dragStep = 15
			return false
		}
		if (dragStep == 15) {
			if (OverlayLayout.editMode) {
				fail("letting go of the drag key did not stop managing the panels")
			}
			val options: ArrayList<String> = ArrayList()
			var node: Node? = MiniMenu.entryList.sentinel.previous
			while (node != null && node !== MiniMenu.entryList.sentinel) {
				if (node is MiniMenuEntry) {
					options.add((node.opBase ?: "") + " " + OpponentInfo.removeTags(node.op ?: ""))
				}
				node = node.previous
			}
			if (!options.contains("Configure Boosts overlay") || !options.contains("Overlay Origin ")) {
				fail("Shift over the boosts panel made the menu " + options + ", without Configure and Overlay Origin")
			}
			dragSummary += "; Shift's menu " + options.filter { it.startsWith("Configure") || it.startsWith("Overlay Origin") }
			OverlayLayout.testShift = false
			OverlayLayout.testKeyHeld = true
			dragFrame = frames
			dragStep = 16
			return false
		}
		if (dragStep == 16) {
			middle(panel, dragPoint)
			if (!OverlayLayout.mousePressed(dragPoint[0], dragPoint[1], MouseLog.TYPE_PRESS_RIGHT)) {
				fail("a right press on the boosts panel with the drag key held was not the layout's")
			}
			if (panel.overlay.preferredPosition != null || panel.overlay.hasPreferredLocation || OverlayLayout.config().get(panelKey + "_preferredPosition", "").isNotEmpty()) {
				fail("the right press did not put the boosts panel back: " + panel.overlay.preferredPosition)
			}
			OverlayLayout.entryNamed(InfoBoxes.DEFAULT_LAYER)?.let { e ->
				middle(e, dragPoint)
				OverlayLayout.testMouseX = dragPoint[0]
				OverlayLayout.testMouseY = dragPoint[1]
			}
			dragFrame = frames
			dragStep = 17
			return false
		}
		// the infobox panel put back too, and the mouse and key the player's again
		OverlayLayout.entryNamed(InfoBoxes.DEFAULT_LAYER)?.let { e -> OverlayLayout.mousePressed(e.x + 2, e.y + 2, MouseLog.TYPE_PRESS_RIGHT) }
		OverlayLayout.testKeyHeld = false
		OverlayLayout.testMouse = false
		dragSummary += "; Alt and a right click put it back"
		firstImage += "; overlays: " + dragSummary
		restoreBoosts()
		return true
	}

	/**
	 * Opponent Information: the player turns to the guide (the opponent) whose bar shows half, the panel
	 * names it with a percentage (the guide's lifepoints are not known); an Attack entry on it is marked;
	 * the player itself made the opponent with hiscores put in shows lifepoints and the comparison; and
	 * dealing with nothing for more than five seconds forgets the opponent.
	 */
	private fun opponentInfoDone(player: PlayerEntity): Boolean {
		if (opponentStep == 0) {
			var guide: NPCEntity? = null
			for (i in 0 until NPCList.npcCount) {
				val npc: NPCEntity? = NPCList.NPC_ARRAY[i]?.npc
				if (npc != null && npc.npcType?.id == GUIDE_NPC) {
					guide = npc
				}
			}
			if (guide == null) {
				fail("no guide (npc " + GUIDE_NPC + ") in the scene for the opponent check")
				return true
			}
			OpponentInfo.enabled = true
			OpponentInfo.showOpponentHealthOverlay = true
			OpponentInfo.showOpponentsInMenu = true
			OpponentInfo.lookupOnInteraction = true
			OpponentInfo.hitpointsDisplayStyle = OpponentInfo.STYLE_HITPOINTS
			player.faceEntity = guide.index()
			PluginRuntime.interactingChanged(player)
			if (OpponentInfo.lastOpponent !== guide) {
				fail("turning to the guide did not make it the opponent")
				return true
			}
			guide.healthRatio = 128
			guide.healthBarEndCycle = ClientState.clientCycle + 3000
			// its Attack entry, marked as the opponent's
			MiniMenuReset.resetEntries()
			MiniMenu.addEntry(false, "<col=ffff00>Guide", 1, true, 1, -1, true, MiniMenuAction.OPNPC2, 0L, "Attack", guide.index().toLong(), -1)
			MiniMenu.addEntry(false, "", 1, true, 1, -1, true, MiniMenuAction.WALK, 0L, "Walk here", 0L, -1)
			MiniMenuInputTick.processMenuInput()
			val marked: String? = findEntry("Attack")?.op
			MiniMenuReset.resetEntries()
			if (marked == null || !marked.startsWith("*")) {
				fail("the opponent's Attack entry was not marked: \"" + marked + "\"")
				return true
			}
			opponentFrame = frames
			opponentStep = 1
			return false
		}
		if (frames - opponentFrame < MARKER_FRAMES) {
			return false
		}
		if (opponentStep == 1) {
			// 128 of 255 with the most unknown: a percentage, whatever the style
			if (OpponentInfoOverlay.drawnName == null || OpponentInfoOverlay.drawnLabel != "50.2%") {
				fail("the opponent panel drew \"" + OpponentInfoOverlay.drawnName + "\" with \"" + OpponentInfoOverlay.drawnLabel + "\", not the guide at 50.2%")
				return true
			}
			saveFrame("build/smoke/frame-opponent.png")
			// the player as its own opponent, a player with hiscores: lifepoints from Constitution, and the comparison
			val name: String = OpponentInfo.nameOf(player) ?: "smoketest"
			val levels: IntArray = IntArray(Skill.COUNT) { 1 }
			levels[Skill.ATTACK] = 99
			levels[Skill.CONSTITUTION] = 10
			HiscoreLookup.put(name, HiscoreLookup.Result(name, levels))
			OpponentInfo.hitpointsDisplayStyle = OpponentInfo.STYLE_BOTH
			OpponentInfo.lastOpponent = player
			player.healthRatio = 255
			player.healthBarEndCycle = ClientState.clientCycle + 3000
			opponentFrame = frames
			opponentStep = 2
			return false
		}
		if (opponentStep == 2) {
			if (OpponentInfoOverlay.drawnLabel != "100/100 (100.0%)" || PlayerComparisonOverlay.drawnRows != 9) {
				fail("the player opponent drew \"" + OpponentInfoOverlay.drawnLabel + "\" (not 100/100 (100.0%)) and " + PlayerComparisonOverlay.drawnRows + " comparison rows of 9")
				return true
			}
			saveFrame("build/smoke/frame-opponent-player.png")
			// dealing with nothing: kept for five seconds, then forgotten
			player.faceEntity = -1
			PluginRuntime.interactingChanged(player)
			val stopped: Long = OpponentInfo.lastTime
			OpponentInfo.clock = { stopped + OpponentInfo.WAIT_MILLIS + 1 }
			opponentFrame = frames
			opponentStep = 3
			return false
		}
		OpponentInfo.clock = { lang.currentTimeMillis() }
		val gone: Boolean = OpponentInfo.lastOpponent == null && OpponentInfoOverlay.drawnName == null
		OpponentInfo.showOpponentsInMenu = false
		OpponentInfo.lookupOnInteraction = false
		OpponentInfo.hitpointsDisplayStyle = OpponentInfo.STYLE_HITPOINTS
		player.healthBarEndCycle = -1000
		HiscoreLookup.clear()
		if (!gone) {
			fail("the opponent was not forgotten after five seconds of dealing with nothing")
			return true
		}
		println("SMOKE opponent info: the guide at 50.2%, its Attack marked, a player at 100/100 with 9 comparison rows, forgotten after 5 s")
		return true
	}

	private fun lootTrackerDone(): Boolean {
		if (lootStep == 0) {
			var guide: NPCEntity? = null
			for (i in 0 until NPCList.npcCount) {
				val npc: NPCEntity? = NPCList.NPC_ARRAY[i]?.npc
				if (npc != null && npc.npcType?.id == GUIDE_NPC) {
					guide = npc
				}
			}
			if (guide == null) {
				fail("no guide (npc " + GUIDE_NPC + ") in the scene for the loot check")
				return true
			}
			ItemPrices.setForTest(mapOf(526 to 150))
			LootTracker.enabled = true
			guide.healthRatio = 0
			guide.healthBarEndCycle = ClientState.clientCycle + 100
			val x: Int = guide.tileX()
			val y: Int = guide.tileY()
			val level: Int = guide.level.toInt()
			PluginRuntime.npcDespawned(guide)
			PluginRuntime.groundItemSpawned(526, 1, x, y, level)
			PluginRuntime.groundItemSpawned(995, 25, x, y, level)
			PluginRuntime.npcSpawned(guide)
			guide.healthBarEndCycle = -1000
			lootFrame = frames
			lootStep = 1
			return false
		}
		if (frames - lootFrame < MARKER_FRAMES) {
			return false
		}
		if (lootStep == 1) {
			val record: LootTrackerData.Record? = LootTracker.sessionRecords.lastOrNull()
			if (record == null || record.items.size != 2) {
				fail("the guide's made-up kill left no loot record with its two items (" + LootTracker.sessionRecords.size + " records)")
				return true
			}
			// enough sources more that the window has to scroll
			for (i in 1..12) {
				LootTracker.addLoot(ClientApiImpl, "Smoke source " + i, i, LootTrackerData.NPC, listOf(intArrayOf(995, i * 10), intArrayOf(526, 1)))
			}
			val why: String? = LootTrackerScreen.open()
			if (why != null) {
				fail("the loot window did not open: " + why)
				return true
			}
			firstImage += "; loot tracker: " + record.title + " " + record.subTitle + " with " + record.items.size + " items"
			lootFrame = frames
			lootStep = 2
			return false
		}
		if (lootStep == 2) {
			checkImage("-loottracker", SHOT.replace(".png", "-loottracker.png"))
			if (LootTrackerScreen.boxesShown < 13) {
				fail("the loot window shows " + LootTrackerScreen.boxesShown + " boxes, not the guide's and twelve more")
			}
		}
		if (lootStep == 2) {
			lootScrollSummary = scrollToEnd("loot", LootTrackerScreen.scrollPosition(), { LootTrackerScreen.scrollBy(it) }, { LootTrackerScreen.scrollPosition() })
			lootFrame = frames
			lootStep = 3
			return false
		}
		scrolledBack("loot", { LootTrackerScreen.scrollBy(it) }, { LootTrackerScreen.scrollPosition() })
		val lootScroll: String = lootScrollSummary
		LootTrackerScreen.changeGrouping(false)
		val single: Int = LootTrackerScreen.boxesShown
		LootTrackerScreen.changeGrouping(true)
		LootTrackerScreen.backToPluginManager()
		if (LootTrackerScreen.open || !PluginManagerScreen.open) {
			fail("Back in the loot window did not bring the plugin manager back")
		}
		PluginManagerScreen.close()
		for (i in 1..12) {
			LootTracker.reset("Smoke source " + i, LootTrackerData.NPC)
		}
		firstImage += ", window drawn with " + LootTrackerScreen.boxesShown + " boxes, " + single + " kill by kill, " + lootScroll + ", Back to the plugin manager"
		return true
	}

	/**
	 * Ground Items on three items made up on the tile east of the player (the server sends nothing):
	 * a stack of rune scimitars priced above the High tier, and coins and bones, both on the default
	 * hidden list. One label and a lootbeam are drawn; with the hotkey held, the hidden two too, with
	 * the boxes. Frames in build/smoke as -grounditems and -grounditems-hotkey.
	 */
	private fun groundItemsDone(player: PlayerEntity): Boolean {
		val x: Int = player.movementBaseX[0] + 1
		val y: Int = player.movementBaseY[0]
		val level: Int = player.level.toInt()
		if (groundItemsStep == 0) {
			ItemPrices.setForTest(mapOf(1333 to 15_000, 526 to 100))
			GroundItems.enabled = true
			GroundItems.groundItemTimers = GroundItems.TIMER_PIE
			PluginRuntime.groundItemSpawned(1333, 150, x, y, level)
			PluginRuntime.groundItemSpawned(995, 50_000, x, y, level)
			PluginRuntime.groundItemSpawned(526, 1, x, y, level)
			val scimitars: GroundItems.Item? = GroundItems.itemAt(MapRegion.sceneBaseX + x, MapRegion.sceneBaseY + y, level, 1333)
			if (scimitars == null) {
				val registration: PluginRegistration? = PluginRuntime.find("grounditems")
				fail("Ground Items did not take the made-up rune scimitars: " + GroundItems.items.size + " items, plugin state " + registration?.state + " " + registration?.error + ", context " + (registration?.context != null))
				return true
			}
			scimitars.lootType = GroundItems.LOOT_DROPPED // a timer pie beside its label
			groundItemsFrame = frames
			groundItemsStep = 1
			return false
		}
		if (frames - groundItemsFrame < MARKER_FRAMES) {
			return false
		}
		if (groundItemsStep == 1) {
			checkImage("-grounditems", SHOT.replace(".png", "-grounditems.png"))
			if (GroundItemsOverlay.drawnLastFrame != 1) {
				fail("Ground Items drew " + GroundItemsOverlay.drawnLastFrame + " labels, not 1 (the coins and the bones are on the default hidden list)")
			}
			if (GroundItemsOverlay.beamsLastFrame != 1) {
				fail("Ground Items drew " + GroundItemsOverlay.beamsLastFrame + " lootbeams, not 1")
			}
			groundItemsSummary = "ground items: " + GroundItemsOverlay.drawnLastFrame + " labels (\"" + GroundItemsOverlay.lastLabel + "\" last), " + GroundItemsOverlay.beamsLastFrame + " lootbeam"
			GroundItems.hotKeyPressed = true
			groundItemsFrame = frames
			groundItemsStep = 2
			return false
		}
		checkImage("-grounditems-hotkey", SHOT.replace(".png", "-grounditems-hotkey.png"))
		if (GroundItemsOverlay.drawnLastFrame != 3) {
			fail("with the hotkey held Ground Items drew " + GroundItemsOverlay.drawnLastFrame + " labels, not 3")
		}
		GroundItems.hotKeyPressed = false
		PluginRuntime.groundItemDespawned(1333, 150, x, y, level)
		PluginRuntime.groundItemDespawned(995, 50_000, x, y, level)
		PluginRuntime.groundItemDespawned(526, 1, x, y, level)
		GroundItems.groundItemTimers = GroundItems.TIMER_OFF
		if (GroundItems.items.isNotEmpty()) {
			fail("Ground Items kept " + GroundItems.items.size + " items after they left")
		}
		firstImage += "; " + groundItemsSummary + "; 3 with the hotkey held"
		return true
	}

	/**
	 * Reads the frame around the middle of each edge of the last marker drawn and requires the
	 * marker's yellow within a few pixels of it. Returns a summary for the pass line.
	 */
	private fun markerPixels(): String {
		val size: IntArray = IntArray(2)
		ClientState.activeToolkit!!.accumulateViewportDimensions(size)
		val pixels: IntArray? = ClientState.activeToolkit!!.na(0, 0, size[0], size[1])
		var found: Int = 0
		for (edge in 0 until 4) {
			val next: Int = (edge + 1) % 4
			val midX: Int = (GroundTiles.lastCorners[edge * 2] + GroundTiles.lastCorners[next * 2]) / 2
			val midY: Int = (GroundTiles.lastCorners[edge * 2 + 1] + GroundTiles.lastCorners[next * 2 + 1]) / 2
			var yellow: Boolean = false
			var dy: Int = -3
			while (dy <= 3 && !yellow) {
				var dx: Int = -3
				while (dx <= 3 && !yellow) {
					val x: Int = midX + dx
					val y: Int = midY + dy
					if (x >= 0 && y >= 0 && x < size[0] && y < size[1]) {
						val rgb: Int = pixels!![y * size[0] + x]
						yellow = (rgb shr 16 and 0xFF) > 180 && (rgb shr 8 and 0xFF) > 180 && (rgb and 0xFF) < 100
					}
					dx++
				}
				dy++
			}
			if (!yellow) {
				fail("no marker yellow in the frame near " + midX + "," + midY + ", the middle of the marked tile's edge " + edge)
			}
			found++
		}
		return "yellow on all " + found + " edges"
	}

	/**
	 * Steps through the component media check: a scratch component takes each kind of media
	 * getAnimatedMediaModel builds (a raw model, the local player's head, an item, an npc's body and,
	 * if a nearby npc has one, its head) and must get a model for it within MEDIA_FRAMES frames,
	 * while the cache loads what it needs. True once it is done.
	 */
	private fun mediaDone(player: PlayerEntity): Boolean {
		val cases: Array<IntArray?> = mediaCases()
		if (mediaStep >= cases.size) {
			firstImage += "; component media built: " + mediaBuilt.substring(2)
			return true
		}
		if (mediaFrame < 0) {
			mediaFrame = frames
		}
		val media: IntArray? = cases[mediaStep]
		if (media == null) {
			mediaStep++
			mediaFrame = -1
			return false
		}
		val component: Component = Component()
		component.mediaType = MediaType.fromId(media[0])
		component.primaryMediaId = media[1]
		val model: Model? = component.getAnimatedMediaModel(TypeLists.IDK, TypeLists.SEQ, 2048, ClientState.activeToolkit, -1, 0, TypeLists.OBJ, PlayerVarDomain.playerVarProvider, player.playerModel, null, TypeLists.BAS, TypeLists.NPC, -1)
		if (model != null) {
			mediaBuilt += ", type " + media[0] + " (" + media[1] + ")"
			mediaStep++
			mediaFrame = -1
			return false
		}
		if (frames - mediaFrame >= MEDIA_FRAMES) {
			fail("a component with media type " + media[0] + " and id " + media[1] + " never got a model" + (if (Component.missingMedia) " (still loading)" else ""))
		}
		return false
	}

	/**
	 * The media the check builds, as { type, id }: a null entry is skipped (no nearby npc).
	 */
	private fun mediaCases(): Array<IntArray?> {
		var npc: Int = -1
		var i: Int = 0
		while (i < NPCList.npcCount && npc == -1) {
			val node: NPCEntityNode? = NPCList.NPC_ARRAY[i]
			if (node != null && node.npc != null && node.npc.npcType != null) {
				npc = node.npc.npcType!!.id
			}
			i++
		}
		return arrayOf<IntArray?>(intArrayOf(1, 0), intArrayOf(3, -1), intArrayOf(4, 995), if (npc == -1) null else intArrayOf(6, npc))
	}

	/**
	 * Adds a "Walk here" entry for a scene tile to the mini menu and runs it through
	 * MiniMenuActionExecutor.processMenuClick, in the root package.
	 */
	private fun walkFromMenu(tileX: Int, tileY: Int) {
		runMenuEntry(MiniMenuAction.WALK, tileX, tileY, MiniMenu.walkHereText)
		println("SMOKE walking from the mini menu to scene tile " + tileX + "," + tileY)
		menuWalkFrame = frames
	}

	/** The backpack's first slot with an item if its slots are laid out, else the first loaded component anywhere that shows an item. */
	private fun componentWithItem(): Component? {
		val interfaces: Array<Array<Component?>?> = Interface.interfaces ?: return null
		var fallback: Component? = null
		for (interfaceId in 0 until interfaces.size) {
			val components: Array<Component?> = interfaces[interfaceId] ?: continue
			for (i in 0 until components.size) {
				val component: Component = components[i] ?: continue
				val children: Array<Component?>? = component.slotChildren
				if (children != null) {
					for (j in 0 until children.size) {
						val child: Component? = children[j]
						if (child != null && child.containerItemId != -1) {
							if (interfaceId == 149) {
								return child
							}
							if (fallback == null) {
								fallback = child
							}
						}
					}
				}
				if (fallback == null && component.containerItemId != -1) {
					fallback = component
				}
			}
		}
		return fallback
	}

	/**
	 * The plugin manager's window, as its clicks take it: open, a frame of the list; a built-in switched off
	 * and on again (its own flag follows), the idle notifier's settings page, a frame of it; its wait stepped
	 * up once (its config follows), Back, close.
	 */
	private fun pluginManagerCheck() {
		if (pluginManagerStep == 0) {
			// the Settings tab's stone (548:102 fixed, 746:47 resizable), so the frame shows the tab
			val stone: Component? = Interface.getComponent(if (ClientState.BASE_WINDOW_ID == 548) (548 shl 16) or 102 else (746 shl 16) or 47)
			if (stone != null) {
				InterfaceOptionInteract.ifOptionInteract(-1, stone.options?.getOrNull(0) ?: "", 1, stone.idHash)
			}
			pluginManagerFrame = gameFrames
			pluginManagerStep = 1
			return
		}
		if (gameFrames - pluginManagerFrame < 30) {
			return
		}
		if (pluginManagerStep == 4) {
			pluginManagerScrolledCheck()
			return
		}
		if (pluginManagerStep >= 5) {
			pluginManagerPickerCheck()
			return
		}
		if (pluginManagerStep == 1) {
			saveFrame("build/smoke/plugins-tab.png")
			// the Plugins button the client keeps in the Settings tab, clicked as a player clicks it
			val button: Component? = PluginManagerScreen.tabButton()
			val graphics: Component? = Interface.getComponent((261 shl 16) or 16)
			if (button == null || graphics == null) {
				fail("no Plugins button in the Settings tab")
				return
			}
			println("SMOKE plugin manager: Plugins button at " + button.basePositionX + "," + button.basePositionY + " in the Settings tab, Graphics Settings moved to x " + graphics.basePositionX)
			InterfaceOptionInteract.ifOptionInteract(button.slotId, "Plugins", 1, 261 shl 16)
			if (!PluginManagerScreen.open) {
				fail("the Plugins button did not open the plugin manager")
			}
			// the pointer over the idle notifier's row: its tooltip, through the hover hook, in the frame the next step saves
			val centre: IntArray? = PluginManagerScreen.rowCentre("idle")
			if (centre == null) {
				fail("no row for the idle notifier")
				return
			}
			println("SMOKE plugin manager: pointer to " + centre[0] + "," + centre[1] + " over the idle notifier's row")
			(MouseRecord.mouseRecorder as MouseManager).smokeMoveTo(centre[0], centre[1])
			pluginManagerFrame = gameFrames
			pluginManagerStep = 2
			return
		}
		if (pluginManagerStep == 2 && pluginManagerSearched) {
			pluginManagerSearchedCheck()
			return
		}
		if (pluginManagerStep == 2) {
			val rows: Int = PluginManagerScreen.rowsShown()
			pluginManagerTipLines = PluginManagerScreen.tipLines()
			saveFrame("build/smoke/plugins-list.png")
			if (pluginManagerTipLines < 1) {
				fail("no tooltip over the idle notifier's row after the pointer moved there")
			}
			(MouseRecord.mouseRecorder as MouseManager).smokeMoveTo(-1, -1)
			if (rows < 5) {
				fail("the plugin manager lists " + rows + " plugins, fewer than the built-ins")
			}
			val before: Boolean = XpDrops.enabled
			if (!PluginManagerScreen.clickToggle("xpdrops") || XpDrops.enabled == before) {
				fail("the experience drops' switch in the plugin manager did not take: " + before + " still")
			}
			PluginManagerScreen.clickToggle("xpdrops")
			if (XpDrops.enabled != before) {
				fail("the experience drops' switch did not go back")
			}
			pluginManagerSortCheck(rows)
			// the list filtered and the experience drops' gear lit, for the shot the next pass saves
			PluginManagerScreen.search("xp")
			val gear: Int = PluginManagerScreen.hoverGear("xpdrops", true)
			if (gear < 0 || gear == PluginManagerScreen.hoverGear("xpdrops", false) || PluginManagerScreen.hoverGear("xpdrops", true) != gear) {
				fail("the experience drops' gear did not light under the pointer")
			}
			println("SMOKE plugin manager: " + rows + " rows; the idle notifier's tooltip of " + pluginManagerTipLines + " lines; experience drops switched off and on; \"xp\" lists " + PluginManagerScreen.listedIds())
			pluginManagerSearched = true
			pluginManagerFrame = gameFrames
			return
		}
		saveFrame("build/smoke/plugins-settings.png")
		val idle: PluginRegistration? = PluginRuntime.find("idle")
		if (idle == null || idle.context == null) {
			fail("no running idle notifier")
			return
		}
		val secondsBefore: Int = idle.context!!.config().getInt("seconds", 5)
		if (!PluginManagerScreen.typeNumber("seconds", "45")) {
			fail("the idle notifier's seconds box took no typing")
		}
		val secondsAfter: Int = idle.context!!.config().getInt("seconds", 5)
		if (secondsAfter != 45) {
			fail("the idle notifier's seconds went from " + secondsBefore + " to " + secondsAfter + ", not 45")
		}
		// out of range is held to the range
		PluginManagerScreen.typeNumber("seconds", "99999")
		if (idle.context!!.config().getInt("seconds", 5) != 600) {
			fail("the idle notifier's seconds were not held to 600")
		}
		PluginManagerScreen.typeNumber("seconds", secondsBefore.toString())
		if (!PluginManagerScreen.clickBack()) {
			fail("Back did not leave the settings page")
		}
		// the npc indicators' text setting: a comma-separated list typed into the box reaches the plugin's list
		val namesBefore: String = NpcIndicators.namesCsv()
		if (!PluginManagerScreen.clickSettings("npcindicators")) {
			fail("the npc indicators have no Settings button")
		}
		if (!PluginManagerScreen.typeNumber("names", "Pig, Cow*")) {
			fail("no names box on the npc indicators' settings page")
		}
		if (NpcIndicators.names != listOf("Pig", "Cow*") || !NpcIndicators.matches("Cow calf") || !NpcIndicators.matches("pig") || NpcIndicators.matches("Goblin")) {
			fail("typing \"Pig, Cow*\" into the names box left the list " + NpcIndicators.names)
		}
		PluginManagerScreen.typeNumber("names", namesBefore)
		// the npc indicators' page is taller than the window: it scrolls to its end and back
		val scroll: IntArray = PluginManagerScreen.scrollPosition()
		if (scroll[1] <= scroll[2]) {
			fail("the npc indicators' settings page does not scroll (content " + scroll[1] + ", visible " + scroll[2] + ")")
		}
		PluginManagerScreen.scrollBy(100)
		val down: IntArray = PluginManagerScreen.scrollPosition()
		if (down[0] != down[1] - down[2]) {
			fail("the wheel did not scroll the settings page to its end (at " + down[0] + " of " + (down[1] - down[2]) + ")")
		}
		println("SMOKE plugin manager: seconds typed " + secondsBefore + " to " + secondsAfter + ", 99999 held to 600, back to " + secondsBefore + "; names typed \"Pig, Cow*\" and back; the npc indicators' page, " + down[1] + " tall, scrolled to its end")
		// the screen shows a frame a little after it is drawn: the shot of the scrolled page waits for the next step
		pluginManagerFrame = gameFrames
		pluginManagerStep = 4
	}

	/** The list's orders, the search and the scrolling, on the list of [rows] plugins. */
	private fun pluginManagerSortCheck(rows: Int) {
		val az: List<String> = PluginManagerScreen.listedIds()
		PluginManagerScreen.sort(1)
		if (PluginManagerScreen.listedIds() != az.reversed()) {
			fail("Name Z-A is not A-Z reversed: " + PluginManagerScreen.listedIds())
		}
		PluginManagerScreen.sort(2)
		val enabledFirst: List<Boolean> = PluginManagerScreen.listedIds().map { PluginManagerScreen.listedEnabled(it) }
		if (enabledFirst.indexOf(false) in 0 until enabledFirst.lastIndexOf(true)) {
			fail("Enabled first lists an off plugin before an on one")
		}
		PluginManagerScreen.sort(3)
		val disabledFirst: List<Boolean> = PluginManagerScreen.listedIds().map { PluginManagerScreen.listedEnabled(it) }
		if (disabledFirst.indexOf(true) in 0 until disabledFirst.lastIndexOf(false)) {
			fail("Disabled first lists an on plugin before an off one")
		}
		PluginManagerScreen.sort(0)
		PluginManagerScreen.search("IDLE")
		val idle: List<String> = PluginManagerScreen.listedIds()
		if (!idle.contains("idle") || idle.size >= rows) {
			fail("searching \"IDLE\" lists " + idle)
		}
		PluginManagerScreen.search("zzzz")
		if (PluginManagerScreen.rowsShown() != 0) {
			fail("searching \"zzzz\" still lists " + PluginManagerScreen.rowsShown())
		}
		PluginManagerScreen.search("")
		val most: Int = maxOf(0, rows - PluginManagerScreen.visibleRows())
		val end: Int = PluginManagerScreen.scrollList(1000)
		val top: Int = PluginManagerScreen.scrollList(-1000)
		if (end != most || top != 0) {
			fail("the list scrolled to " + end + " of " + most + " and back to " + top)
		}
		println("SMOKE plugin manager: sorted A-Z, Z-A, enabled first, disabled first; \"IDLE\" lists " + idle + "; scrolled " + end + " rows to the end and back")
	}

	/** The filtered list's shot, then the search emptied, the gear put out, and the idle notifier's settings page opened by its gear's action. */
	private fun pluginManagerSearchedCheck() {
		saveFrame("build/smoke/plugins-search.png")
		PluginManagerScreen.hoverGear("xpdrops", false)
		PluginManagerScreen.search("")
		if (!PluginManagerScreen.clickSettings("idle")) {
			fail("the idle notifier's Settings did not open its page")
		}
		println("SMOKE plugin manager: the filtered list shot; the idle notifier's settings page open")
		pluginManagerSearched = false
		pluginManagerFrame = gameFrames
		pluginManagerStep = 3
	}

	/** The scrolled page's shot, then back to its top, Back, and the window closed. */
	private fun pluginManagerScrolledCheck() {
		checkImage("-settings-scrolled", SHOT.replace(".png", "-settings-scrolled.png"))
		PluginManagerScreen.scrollBy(-100)
		if (PluginManagerScreen.scrollPosition()[0] != 0) {
			fail("the wheel did not scroll the settings page back to its top")
		}
		if (!PluginManagerScreen.clickBack()) {
			fail("Back did not leave the npc indicators' settings page")
		}
		PluginManagerScreen.close()
		if (PluginManagerScreen.open) {
			fail("the plugin manager did not close")
		}
		println("SMOKE plugin manager: the scrolled page shot; back to its top; Back; closed")
		pluginManagerStep = 5
	}

	/** The colour a plugin's picker handed back, -1 before OK. */
	private var pickedColour: Int = -1

	/**
	 * The colour picker a plugin opens (PluginContext.pickColour): the window opens on it, a palette
	 * cell is picked, a frame of the page; OK hands the colour back, keeping the alpha it started with,
	 * and closes the window it opened.
	 */
	private fun pluginManagerPickerCheck() {
		val cell: Int = 2 * 24 + 8 // a full green, third row
		if (pluginManagerStep == 5) {
			val why: String? = PluginManagerScreen.pickColour(0x80FF0000.toInt(), true) { argb -> pickedColour = argb }
			if (why != null || !PluginManagerScreen.open) {
				fail("the colour picker did not open: " + why)
				pluginManagerStep = 7
				return
			}
			PluginManagerScreen.clickPaletteCell(cell)
			pluginManagerFrame = gameFrames
			pluginManagerStep = 6
			return
		}
		saveFrame("build/smoke/plugins-picker.png")
		val expected: Int = (0x80 shl 24) or (PluginManagerScreen.paletteColour(cell) and 0xFFFFFF)
		val shown: Int? = PluginManagerScreen.pickedColour()
		if (shown != expected) {
			fail("the picker shows " + shown + " after the cell, not " + expected)
		}
		PluginManagerScreen.clickPickOk()
		if (pickedColour != expected) {
			fail("OK handed back " + PluginConfigImpl.formatColour(pickedColour) + ", not " + PluginConfigImpl.formatColour(expected))
		}
		if (PluginManagerScreen.open) {
			fail("the window the picker opened did not close on OK")
		}
		println("SMOKE plugin manager: the colour picker opened, cell " + cell + " picked, OK gave " + PluginConfigImpl.formatColour(pickedColour) + " and closed the window")
		pluginManagerStep = 7
	}

	/** The verb of the menu's top row, which is the last entry of the list. */
	/** The plugins' entries for the first npc in the scene, as MiniMenuBuilder adds them after the npc's own. */
	private fun npcMenuCheck() {
		if (NPCList.NPC_UPDATE_INDEX == 0) {
			fail("npc menu check: no npc in the scene")
			return
		}
		val node: NPCEntityNode? = NPCList.NPC_MAP.get(NPCList.NPC_UPDATE_INDICES[0].toLong()) as NPCEntityNode?
		val npc: NPCEntity? = node?.npc
		if (npc == null) {
			fail("npc menu check: the first npc has no entity")
			return
		}
		MiniMenuReset.resetEntries()
		PluginRuntime.menuTarget(MenuTarget.NPC, (npc.x - (npc.getSize() - 1) * 256) shr 9, (npc.z - (npc.getSize() - 1) * 256) shr 9, npc.level.toInt(), npc.playerIndex, npc.playerIndex.toLong())
		val verbs: ArrayList<String> = ArrayList()
		var entry: MiniMenuEntry? = MiniMenu.entryList.first() as MiniMenuEntry?
		while (entry != null) {
			verbs.add(entry.opBase ?: "")
			entry = MiniMenu.entryList.next() as MiniMenuEntry?
		}
		val found: Boolean = findEntry(NPC_MENU) != null
		MiniMenuReset.resetEntries()
		println("SMOKE npc menu check: npc " + npc.npcType?.name + " index " + npc.playerIndex + ", plugin entries " + verbs + ", \"" + NPC_MENU + "\" " + (if (found) "present" else "MISSING"))
		if (!found) {
			fail("npc menu check: no \"" + NPC_MENU + "\" entry for " + npc.npcType?.name)
		}
	}

	private fun topEntryText(): String? {
		val top: MiniMenuEntry? = MiniMenu.entryList.sentinel.previous as MiniMenuEntry?
		return top?.opBase
	}

	private fun findEntry(verb: String): MiniMenuEntry? {
		var entry: MiniMenuEntry? = MiniMenu.entryList.first() as MiniMenuEntry?
		while (entry != null) {
			if (verb == entry.opBase) {
				return entry
			}
			entry = MiniMenu.entryList.next() as MiniMenuEntry?
		}
		return null
	}

	/**
	 * Builds a banker's menu as the client would (Talk-to, Examine, Bank, Walk here, with Cancel) and
	 * runs the menu tick on it, so that the plugins' MenuBuilt edits show in the top row; then builds the
	 * menu of the backpack's first item as the client would, clicks the first plugin entry on it, and
	 * builds it again, so that a plugin's entry on an item and its click can be checked without a mouse.
	 */
	private fun menuCheck() {
		MiniMenuReset.resetEntries()
		MiniMenu.addEntry(false, "<col=ffff00>Banker", 1, true, 1, -1, true, MiniMenuAction.OPNPC1, 0L, "Talk-to", 7L, -1)
		MiniMenu.addEntry(false, "<col=ffff00>Banker", 1, true, 1, -1, true, MiniMenuAction.OPNPC6, 0L, "Examine", 7L, -1)
		MiniMenu.addEntry(false, "<col=ffff00>Banker", 1, true, 1, -1, true, MiniMenuAction.OPNPC3, 0L, "Bank", 7L, -1)
		MiniMenu.addEntry(false, "", 1, true, 1, -1, true, MiniMenuAction.WALK, 0L, "Walk here", 0L, -1)
		val before: String? = topEntryText()
		MiniMenuInputTick.processMenuInput()
		println("SMOKE menu check: top entry \"" + before + "\" became \"" + topEntryText() + "\", " + MiniMenu.entryCount + " entries, Examine " + (if (findEntry("Examine") == null) "gone" else "kept"))
		MiniMenuReset.resetEntries()
		var slot: Component? = componentWithItem()
		var borrowed: Boolean = false
		var borrowedItem: Int = -1
		if (slot == null) {
			// nothing on screen shows an item yet: the root interface's first component is lent coins for the two hovers
			val root: Array<Component?>? = Interface.peekComponents(ClientState.BASE_WINDOW_ID)
			slot = if (root == null || root.isEmpty()) null else root[0]
			if (slot == null) {
				println("SMOKE menu check: no loaded component to hover; the component step is skipped")
				return
			}
			borrowed = true
			borrowedItem = slot.containerItemId
			slot.containerItemId = 995
		}
		MiniMenuBuilder.interfaceComponentHovered(0, 0, slot)
		var pluginEntry: MiniMenuEntry? = null
		var entry: MiniMenuEntry? = MiniMenu.entryList.first() as MiniMenuEntry?
		while (entry != null) {
			if (entry.clickType >= MiniMenuAction.PLUGIN_FIRST && entry.clickType <= MiniMenuAction.PLUGIN_LAST && pluginEntry == null) {
				pluginEntry = entry
			}
			entry = MiniMenu.entryList.next() as MiniMenuEntry?
		}
		val first: String? = pluginEntry?.opBase
		if (pluginEntry != null) {
			MiniMenuActionExecutor.processMenuClick(pluginEntry, 0, 0)
		}
		MiniMenuReset.resetEntries()
		MiniMenuBuilder.interfaceComponentHovered(0, 0, slot)
		var second: String? = null
		entry = MiniMenu.entryList.first() as MiniMenuEntry?
		while (entry != null) {
			if (entry.clickType >= MiniMenuAction.PLUGIN_FIRST && entry.clickType <= MiniMenuAction.PLUGIN_LAST && second == null) {
				second = entry.opBase
			}
			entry = MiniMenu.entryList.next() as MiniMenuEntry?
		}
		println("SMOKE menu check: item " + slot.containerItemId + " on component " + (slot.idHash shr 16) + ":" + (slot.idHash and 0xFFFF) + " got the plugin entry \"" + first + "\"; after its click, \"" + second + "\"")
		if (borrowed) {
			slot.containerItemId = borrowedItem
		}
		MiniMenuReset.resetEntries()
	}

	/**
	 * Adds a tile entry of the given click type to the mini menu and runs it through
	 * MiniMenuActionExecutor.processMenuClick, in the root package, as a click does.
	 */
	private fun runMenuEntry(clickType: Int, tileX: Int, tileY: Int, op: String?) {
		MiniMenu.addEntry(false, "", tileY, true, tileX, -1, true, clickType, (tileY or (tileX shl 0)).toLong(), op, 0L, -1)
		var found: MiniMenuEntry? = null
		var entry: MiniMenuEntry? = MiniMenu.entryList.first() as MiniMenuEntry?
		while (entry != null) {
			if (entry.clickType == clickType && entry.param0 == tileX && entry.param1 == tileY) {
				found = entry
			}
			entry = MiniMenu.entryList.next() as MiniMenuEntry?
		}
		if (found == null) {
			fail("the \"" + op + "\" entry added to the mini menu is not in its entry list")
		}
		try {
			MiniMenuActionExecutor.processMenuClick(found, 0, 0)
		} catch (ex: Exception) {
			fail("cannot run the \"" + op + "\" entry: " + ex)
		}
	}

	/**
	 * Opens a sub-interface through SubInterfaceOpener.openSubInterface, without its load scripts.
	 */
	private fun openSubInterface(interfaceId: Int, parentHash: Int, openType: SubInterfaceType, suppressScripts: Boolean = true) {
		try {
			SubInterfaceOpener.openSubInterface(interfaceId, parentHash, openType, suppressScripts)
		} catch (ex: Exception) {
			fail("cannot open interface " + interfaceId + ": " + ex)
		}
	}

	/**
	 * Closes the open modals through ModalInterfaceClose.closeInterface.
	 */
	private fun closeModals() {
		try {
			ModalInterfaceClose.closeInterface()
		} catch (ex: Exception) {
			fail("cannot close the modals: " + ex)
		}
	}

	/**
	 * Steps through the window mode check, a frame at a time. True once it is done.
	 */
	private fun windowModesDone(): Boolean {
		if (windowStep == 0) {
			startMode = WindowMode.getWindowMode()
			if (startMode === WindowModeType.FULLSCREEN) {
				firstImage += "; window modes skipped (full screen)"
				return true
			}
			val other: WindowModeType = if (startMode === WindowModeType.FIXED) WindowModeType.RESIZABLE else WindowModeType.FIXED
			println("SMOKE window mode " + startMode + ", switching to " + other)
			sendCommand(if (other === WindowModeType.FIXED) "wm1" else "wm2")
			if (WindowMode.getWindowMode() !== other) {
				fail("the window mode is " + WindowMode.getWindowMode() + " after switching to " + other)
			}
			windowFrame = frames
			windowStep = 1
			return false
		}
		if (frames - windowFrame < WINDOW_FRAMES) {
			return false
		}
		if (windowStep == 1) {
			println("SMOKE drew " + (frames - windowFrame) + " frames in " + WindowMode.getWindowMode() + ", switching back to " + startMode)
			sendCommand(if (startMode === WindowModeType.FIXED) "wm1" else "wm2")
			if (WindowMode.getWindowMode() !== startMode) {
				fail("the window mode is " + WindowMode.getWindowMode() + " after switching back to " + startMode)
			}
			windowFrame = frames
			windowStep = 2
			return false
		}
		firstImage += "; window modes " + startMode + " to " + (if (startMode === WindowModeType.FIXED) WindowModeType.RESIZABLE else WindowModeType.FIXED) + " and back, drawn in both"
		return true
	}

	/**
	 * Called by PlayerEntity as it draws, with the models it built: the body, then the primary and
	 * secondary spot animation. Records that the local player drew a spot animation model.
	 */
	@JvmStatic
	fun drawn(player: PlayerEntity?, models: Array<com.jagex.game.runetek5.graphics.model.Model?>?) {
		if (ENABLED && commandFrame >= 0 && player === PlayerUpdate.localPlayer) {
			if (player!!.primarySpotAnimation.spotAnimationId != -1 && models!![1] != null ||
				player.secondarySpotAnimation.spotAnimationId != -1 && models!![2] != null) {
				spotModelBuilt = true
			}
		}
	}

	/**
	 * Called when the client reports an error it would otherwise only log.
	 */
	@JvmStatic
	fun error(message: String?, ex: Throwable?) {
		if (ENABLED) {
			if (ex != null) {
				// the first frames, so a failure in the client's own code can be placed
				println(ex.stackTraceToString().lineSequence().take(30).joinToString("\n"))
			}
			fail("client error: " + message + (if (ex == null) "" else " " + ex))
		}
	}

	private fun fail(why: String) {
		println("SMOKE FAIL " + why)
		lang.flushOut()
		lang.halt(1)
	}

	private fun elapsed(): Long {
		return (lang.currentTimeMillis() - START) / 1000L
	}
}
