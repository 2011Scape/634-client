package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.LocType
import turnElapsedMs
import com.jagex.game.runetek5.ObjStackEntity
import com.jagex.game.runetek5.MapRegion
import com.jagex.game.runetek5.cache.node.ObjStackEntry
import com.jagex.game.runetek5.cache.node.ObjStack
import com.jagex.game.runetek5.statics.StatBaseLevels
import com.jagex.game.runetek5.config.rsinterface.MediaType
import com.jagex.game.runetek5.entity.NPCEntityNode
import com.jagex.game.runetek5.NPCList
import com.jagex.game.runetek5.vardomain.PlayerVarDomain
import com.jagex.game.runetek5.config.TypeLists
import com.jagex.game.runetek5.graphics.model.Model
import com.jagex.game.runetek5.core.constants.MiniMenuAction
import com.jagex.game.runetek5.client.menu.MiniMenuEntry
import com.jagex.game.runetek5.client.menu.MiniMenu
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
import kotlin.jvm.JvmStatic
import lang.Class
import lang.getMethod
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

	private val GROUND_ITEMS: Boolean = "false" != lang.getProperty("client.test.groundItems")

	private const val COINS: Int = 995

	private const val BRONZE_AXE: Int = 1351

	private var groundStep: Int = 0

	private var groundFrame: Int = -1

	private var groundWasEnabled: Boolean = false

	private var groundX: Int = -1

	private var groundY: Int = -1

	private val NPC_LABELS: Boolean = "false" != lang.getProperty("client.test.npcLabels")

	/** The Tutorial Island guide, who stands beside the test account. */
	private const val GUIDE_NPC: Int = 945

	private const val NPC_LABEL_WAIT: Int = 200

	private var npcLabelStep: Int = 0

	private var npcLabelFrame: Int = -1

	private var npcLabelsWereEnabled: Boolean = false

	private var npcLabelStart: Int = -1

	private val XP_DROPS: Boolean = "false" != lang.getProperty("client.test.xpDrops")

	private const val XP_WAIT: Int = 300

	private var xpStep: Int = 0

	private var xpFrame: Int = -1

	private var xpWasEnabled: Boolean = false

	private var xpDrawn: Int = 0

	private var xpDrawnFrame: Int = -1

	private val MARKERS: Boolean = "false" != lang.getProperty("client.test.markers")

	private const val MARKER_FRAMES: Int = 40

	private var markerStep: Int = 0

	private var markerFrame: Int = -1

	private var markersWereEnabled: Boolean = false

	private val MEDIA: Boolean = "false" != lang.getProperty("client.test.media")

	private const val MEDIA_FRAMES: Int = 300

	private var mediaStep: Int = 0

	private var mediaFrame: Int = -1

	private var mediaBuilt: String = ""

	private val MENU_WALK: Boolean = "false" != lang.getProperty("client.test.menuWalk")

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
	 * DEFAULT_SCENES; otherwise a list of name:x,y,level,yaw,pitch,zoom[,limit] separated by ';'. For each
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

	private val SCENE_FRAMES: Int = lang.getInteger("client.test.sceneFrames", 150)

	/** client.test.sceneBurst: consecutive frames saved per scene before its comparison (flicker checks). */
	private val SCENE_BURST: Int = lang.getInteger("client.test.sceneBurst", 0)

	private var burstIndex: Int = 0

	private val SCENE_SPIN: Int = lang.getInteger("client.test.sceneSpin", 0)

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
		"pixelLighting=on", "shadows=4096", "lights=on", "terrainBlend=on", "water=1", "foliage=on", "sky=on",
		"softParticles=on", "fxaa=on", "bloom=on", "colourGrading=on", "materials=on", "particles=on", "preset=ultra",
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

	private val DEFAULT_SCENES: String = "lumbridge:3222,3218,0,0,1400,0;" +
		"varrock:3212,3428,0,2048,1400,0;" +
		"grand_exchange:3164,3478,0,0,1600,0;" +
		"falador_park:3007,3373,0,4096,1400,0;" +
		"draynor_swamp:3108,3290,0,8192,1400,0;" +
		"karamja_volcano:2852,3168,0,12288,1600,0;" +
		"varrock_sewers:3237,9866,0,0,1400,0;" +
		// the cows wander between runs: toolkit 1 against its own reference differs by 33 to 57
		"cows:3256,3276,0,4096,1400,0,65;" +
		"wizards_tower:3109,3162,0,0,1400,0"

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
	private var checksDone: Int = 0

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
		if (BENCH > 0) {
			bench()
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
		if (WINDOW_MODES && (checksDone and 1) == 0) {
			if (!windowModesDone()) {
				return
			}
			checksDone = checksDone or 1
		}
		if (SUB_INTERFACES && (checksDone and 2) == 0) {
			if (!subInterfacesDone()) {
				return
			}
			checksDone = checksDone or 2
		}
		if (MENU_WALK && (checksDone and 4) == 0) {
			if (!menuWalkDone(player)) {
				return
			}
			checksDone = checksDone or 4
		}
		if (MEDIA && (checksDone and 8) == 0) {
			if (!mediaDone(player)) {
				return
			}
			checksDone = checksDone or 8
		}
		if (MARKERS && (checksDone and 16) == 0) {
			if (!markersDone(player)) {
				return
			}
			checksDone = checksDone or 16
		}
		if (XP_DROPS && (checksDone and 32) == 0) {
			if (!xpDropsDone()) {
				return
			}
			checksDone = checksDone or 32
		}
		if (NPC_LABELS && (checksDone and 64) == 0) {
			if (!npcLabelsDone()) {
				return
			}
			checksDone = checksDone or 64
		}
		if (GROUND_ITEMS && (checksDone and 128) == 0) {
			if (!groundItemsDone(player)) {
				return
			}
			checksDone = checksDone or 128
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
		println("SMOKE BENCH toolkit=" + GamePreferences.uid!!.toolkit!!.getValue() + " size=" + size + " mode=" + WindowMode.getWindowMode() + " frames=" + BENCH
			+ " avg=" + format(average) + "ms p50=" + format(times[BENCH / 2]) + "ms p95=" + format(times[kotlin.math.min(BENCH - 1, BENCH * 95 / 100)]) + "ms"
			+ " fps=" + format(1000.0 / average) + workLine + " ticks/frame=" + format(benchTicks.toDouble() / BENCH.toDouble())
			+ (if (benchDrawCalls > 0L) " drawCalls=" + (benchDrawCalls / BENCH.toLong()) + " (models " + (benchCategories[1] / BENCH.toLong()) + ", ground " + (benchCategories[2] / BENCH.toLong()) + ", other " + (benchCategories[0] / BENCH.toLong()) + ") triangles=" + (benchTriangles / BENCH.toLong()) + " regionHitch=" + RenderStats.lastRegionUploadMs + "ms regionBuild=" + RenderStats.lastRegionBuildMs + "ms chunkRebuilds=" + RenderStats.chunkRebuilds else ""))
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
			// back in game after the relog (frame() only runs in game)
			println("SMOKE scene " + name + ": logged back in")
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
		}
		// hold the camera where the scene asks, whatever the client's own camera logic does; during a
		// burst it turns client.test.sceneSpin yaw units a frame (moving-camera flicker checks)
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
		// client.test.sceneBurst=N: N consecutive frames saved first, to measure flicker between frames
		if (burstIndex < SCENE_BURST) {
			if (burstFrame != gameFrames) {
				burstFrame = gameFrames
				saveFrame("build/smoke/scenes/burst-" + name + "-" + burstIndex + ".png")
				burstIndex++
			}
			return
		}
		burstIndex = 0
		// client.test.hdCycle: on the first scene, every 634HD switch turned on and off again live
		if (HD_CYCLE && sceneIndex == 0 && !cycleDone) {
			cycleStep()
			return
		}
		var result: String = compareScene(name, reference)
		if (locChange) {
			val rebuilt: Int = RenderStats.chunkRebuilds - locRebuildsBefore
			result += ", chunks rebuilt " + rebuilt
			// on 634HD exactly the changed loc's chunk is built again (Toolkit.sceneChanged)
			if (GamePreferences.uid!!.toolkit!!.getValue() == ToolkitType.HD && (rebuilt < 1 || rebuilt > 2)) {
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

	/**
	 * client.test.findText=open:N: opens interface N as a modal in the main-screen slot, and after 60 frames saves the frame to build/smoke/open-N.png and stops.
	 */
	private fun openAndCapture() {
		val id: Int = FIND_TEXT.substring(5).toInt()
		if (openFrame < 0) {
			// the main-screen slot, where the server opens such interfaces: 548:18 fixed, 746:8 resizable
			val slot: Int = if (ClientState.BASE_WINDOW_ID == 548) 18 else 8
			val parent: Int = (ClientState.BASE_WINDOW_ID shl 16) or slot
			openSubInterface(id, parent, SubInterfaceType.MODAL)
			openFrame = gameFrames
			return
		}
		if (gameFrames - openFrame < 60) {
			return
		}
		if (openStep == 0) {
			checkImage("-open", "build/smoke/open-" + id + ".png")
			if (id != 742) {
				println("SMOKE PASS opened " + id)
				lang.flushOut()
				lang.halt(0)
			}
			// the 634HD toggle in the title bar: on, then off again
			openToolkit = GamePreferences.uid!!.toolkit!!.getValue()
			if (!HdOptionsButton.pressed(HdOptionsButton.x + 8, HdOptionsButton.y + 8)) {
				fail("the 634HD toggle did not take the press at " + HdOptionsButton.x + "," + HdOptionsButton.y)
			}
			openStep = 1
			openFrame = gameFrames
			return
		}
		if (openStep == 1) {
			if (GamePreferences.uid!!.toolkit!!.getValue() != ToolkitType.HD) {
				fail("the 634HD toggle left the client on toolkit " + GamePreferences.uid!!.toolkit!!.getValue())
			}
			// turned on with nothing saved: the Medium preset
			if (HdSettings.currentPreset() != "medium") {
				fail("turning 634HD on gave the settings " + HdSettings.currentPreset() + ", not the medium preset")
			}
			checkImage("-open-hd", "build/smoke/open-" + id + "-hd.png")
			// the settings panel: opened from its button, a preset and a row clicked, then closed
			if (!HdOptionsButton.pressed(HdOptionsButton.buttonX + 8, HdOptionsButton.y + 8) || !HdSettingsPanel.open) {
				fail("the 634HD settings button did not open the panel")
			}
			openStep = 3
			openFrame = gameFrames
			return
		}
		if (openStep == 3) {
			checkImage("-open-hd-settings", "build/smoke/open-" + id + "-hd-settings.png")
			if (!HdSettingsPanel.clickPreset("high") || HdSettings.currentPreset() != "high") {
				fail("the High preset did not apply: " + HdSettings.currentPreset())
			}
			val before: String = HdSettings.valueOf("bloom")
			if (!HdSettingsPanel.clickRow("bloom") || HdSettings.valueOf("bloom") == before) {
				fail("a click on the bloom row did not change it")
			}
			openStep = 4
			openFrame = gameFrames
			return
		}
		if (openStep == 4) {
			checkImage("-open-hd-settings-high", "build/smoke/open-" + id + "-hd-settings-high.png")
			if (!HdSettingsPanel.clickClose() || HdSettingsPanel.open) {
				fail("Close did not close the 634HD settings panel")
			}
			if (!HdOptionsButton.pressed(HdOptionsButton.x + 8, HdOptionsButton.y + 8)) {
				fail("the 634HD toggle did not take the second press")
			}
			openStep = 2
			openFrame = gameFrames
			return
		}
		if (GamePreferences.uid!!.toolkit!!.getValue() != openToolkit) {
			fail("turning 634HD off left the client on toolkit " + GamePreferences.uid!!.toolkit!!.getValue() + ", not " + openToolkit)
		}
		println("SMOKE PASS 634HD toggle in the Graphics Options: " + openToolkit + " to 6 (medium preset) and back; settings panel: presets, rows, close")
		lang.flushOut()
		lang.halt(0)
	}

	private var openStep: Int = 0

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
			println("SMOKE IF " + id + ":" + i + " type " + c.type + " parent " + (c.parentId and 0xFFFF) + " text \"" + c.text + "\" sprite " + c.spriteId + " name " + c.name
				+ " onClick " + script(c.onClick) + " onLoad " + script(c.onLoadScript) + " onVarp " + script(c.onVarpTransmit) + " onMouseOver " + script(c.onMouseOver))
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

	private fun sceneArrived(player: PlayerEntity, x: Int, y: Int, level: Int): Boolean {
		return player.movementBaseX[0] + MapRegion.sceneBaseX == x && player.movementBaseY[0] + MapRegion.sceneBaseY == y && PlayerUpdate.currentLevel == level
	}

	/**
	 * Saves the frame as scene-<name>-toolkit<id>.png and compares it with the reference pixel by
	 * pixel, saving a diff image beside it when any pixel differs. Returns a summary.
	 */
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
		for (y in 0 until height) {
			for (x in 0 until width) {
				val a: Int = pixels[y * width + x]
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
		val perMille: Int = (different.toLong() * 1000L / (width * height).toLong()).toInt()
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
	 * Steps through the ground item label check: turns the labels on and puts a pile of 1000 coins
	 * and a bronze axe on the tile east of the player the way the server's ground item packet does
	 * (ObjStack.addGroundItem, then ObjStackEntity.refreshObjStack), client side only. It requires a
	 * label whose bottom line is "Coins (1000)" (the pile lists its most valuable item first) inside
	 * the frame, checks that frame against its own reference, and takes the pile away the way the
	 * server's removal does. True once it is done.
	 */
	private fun groundItemsDone(player: PlayerEntity): Boolean {
		if (groundStep == 3) {
			return true
		}
		val level: Int = player.level.toInt()
		if (groundStep == 0) {
			groundWasEnabled = GroundItemLabels.enabled
			GroundItemLabels.enabled = true
			groundX = player.movementBaseX[0] + 1
			groundY = player.movementBaseY[0]
			ObjStack.addGroundItem(ObjStackEntry(COINS, 1000), level, MapRegion.sceneBaseY + groundY, MapRegion.sceneBaseX + groundX)
			ObjStack.addGroundItem(ObjStackEntry(BRONZE_AXE, 1), level, MapRegion.sceneBaseY + groundY, MapRegion.sceneBaseX + groundX)
			ObjStackEntity.refreshObjStack(groundY, groundX, level)
			groundFrame = frames
			groundStep = 1
			return false
		}
		var label: Int = -1
		for (i in 0 until GroundItemLabels.drawnLastFrame) {
			if ("Coins (1000)" == GroundItemLabels.lastTexts[i]) {
				label = i
			}
		}
		if (groundStep == 1) {
			if (label == -1) {
				if (frames - groundFrame >= 200) {
					fail("no frame labelled the pile on scene tile " + groundX + "," + groundY + " \"Coins (1000)\" (" + GroundItemLabels.drawnLastFrame + " piles labelled" + (if (GroundItemLabels.drawnLastFrame > 0) ", the first \"" + GroundItemLabels.lastTexts[0] + "\"" else "") + ")")
				}
				return false
			}
			// the screen shows a frame a little after it is drawn: let the label be on it before the shot
			groundFrame = frames
			groundStep = 2
			return false
		}
		if (frames - groundFrame < 10) {
			return false
		}
		if (label == -1) {
			groundStep = 1
			return false
		}
		val size: IntArray = IntArray(2)
		ClientState.activeToolkit!!.accumulateViewportDimensions(size)
		val x: Int = GroundItemLabels.lastX[label]
		val y: Int = GroundItemLabels.lastY[label]
		if (x < 0 || y < 0 || x >= size[0] || y >= size[1]) {
			fail("the pile's label is at " + x + "," + y + ", outside the " + size[0] + "x" + size[1] + " frame")
		}
		val image: String? = checkImage("-grounditems", SHOT.replace(".png", "-grounditems.png"))
		val pile: ObjStack? = ObjStack.GROUND_ITEMS.get(((MapRegion.sceneBaseY + groundY shl 14) or (level shl 28) or (MapRegion.sceneBaseX + groundX)).toLong()) as ObjStack?
		if (pile != null) {
			pile.unlink()
		}
		ObjStackEntity.refreshObjStack(groundY, groundX, level)
		GroundItemLabels.enabled = groundWasEnabled
		firstImage += "; ground items labelled \"Coins (1000)\" at " + x + "," + y + ": " + image
		groundStep = 3
		return true
	}

	/**
	 * Steps through the npc label check: turns the labels on and requires, within NPC_LABEL_WAIT
	 * frames, a label for the Tutorial Island guide at a point inside the frame. The frame with the
	 * labels is saved for a look but never compared: the guide walks. True once it is done.
	 */
	private fun npcLabelsDone(): Boolean {
		if (npcLabelStep == 3) {
			return true
		}
		if (npcLabelStep == 0) {
			npcLabelStart = frames
			npcLabelsWereEnabled = NpcLabels.enabled
			NpcLabels.enabled = true
			npcLabelFrame = frames
			npcLabelStep = 1
			return false
		}
		var guide: Int = -1
		for (i in 0 until NpcLabels.drawnLastFrame) {
			if (NpcLabels.lastTypes[i] == GUIDE_NPC) {
				guide = i
			}
		}
		if (npcLabelStep == 1) {
			if (guide == -1) {
				if (frames - npcLabelFrame >= NPC_LABEL_WAIT) {
					fail("no frame labelled the guide (npc " + GUIDE_NPC + "); the last frame labelled " + NpcLabels.drawnLastFrame + " npcs")
				}
				return false
			}
			// the screen shows a frame a little after it is drawn: let the labels be on it before the shot
			npcLabelFrame = frames
			npcLabelStep = 2
			return false
		}
		if (frames - npcLabelFrame < 10) {
			return false
		}
		if (guide == -1) {
			// the guide walked out of view while the screen caught up: wait for him again
			if (frames - npcLabelStart >= NPC_LABEL_WAIT * 3) {
				fail("the guide's label kept going away")
			}
			npcLabelStep = 1
			return false
		}
		val x: Int = NpcLabels.lastX[guide]
		val y: Int = NpcLabels.lastY[guide]
		val size: IntArray = IntArray(2)
		ClientState.activeToolkit!!.accumulateViewportDimensions(size)
		if (x < 0 || y < 0 || x >= size[0] || y >= size[1]) {
			fail("the guide's label is at " + x + "," + y + ", outside the " + size[0] + "x" + size[1] + " frame")
		}
		checkImage("-npclabels", SHOT.replace(".png", "-npclabels.png"))
		NpcLabels.enabled = npcLabelsWereEnabled
		firstImage += "; " + NpcLabels.drawnLastFrame + " npc labels, the guide's \"" + NpcLabels.label(TypeLists.NPC!!.list(GUIDE_NPC)) + "\" at " + x + "," + y
		npcLabelStep = 3
		return true
	}

	/**
	 * Steps through the experience drop check: logging in must have made no drops. Void sends no
	 * stats at login, so it first sets the test account's Attack to 1 with its admin command
	 * ::level (the update only primes the skill), then to 2, which must make a "+83 Attack" drop
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
			if ("+83 Attack" != XpDrops.lastDropText) {
				fail("the experience drop reads \"" + XpDrops.lastDropText + "\", not \"+83 Attack\"")
			}
			// saved for a look, never compared: the drop rises and fades, so no two runs catch the same frame
			checkImage("-xpdrop", SHOT.replace(".png", "-xpdrop.png"))
			println("SMOKE experience drop \"" + XpDrops.lastDropText + "\" drawn; setting Attack back to 1")
			sendCommand("level attack 1")
			xpFrame = frames
			xpStep = 3
			return false
		}
		if (xpStep == 3) {
			if (level != 1) {
				if (frames - xpFrame >= XP_WAIT) {
					fail("Attack did not go back to 1 (it is " + level + ")")
				}
				return false
			}
			XpDrops.enabled = xpWasEnabled
			firstImage += "; experience drop \"" + XpDrops.lastDropText + "\" drawn, Attack back to 1"
			xpStep = 4
		}
		return true
	}

	/**
	 * Steps through the tile marker check: turns the markers on, marks the tile east of the player
	 * through its mini menu entry, and after MARKER_FRAMES frames requires one marker that the last
	 * frame drew, with the marker's yellow on the frame at the middle of each of its edges. Then it unmarks the tile the same
	 * way, requires no markers, and puts the switch back. True once it is done.
	 */
	private fun markersDone(player: PlayerEntity): Boolean {
		val x: Int = player.movementBaseX[0] + 1
		val y: Int = player.movementBaseY[0]
		if (markerStep == 0) {
			markersWereEnabled = TileMarkers.enabled
			TileMarkers.enabled = true
			runMenuEntry(MiniMenuAction.TILE_MARKER, x, y, "Mark tile")
			if (TileMarkers.markerCount() != 1 || !TileMarkers.isMarked(x, y)) {
				fail("the Mark tile entry did not mark scene tile " + x + "," + y + " (" + TileMarkers.markerCount() + " markers)")
			}
			markerFrame = frames
			markerStep = 1
			return false
		}
		if (frames - markerFrame < MARKER_FRAMES) {
			return false
		}
		if (markerStep == 1) {
			if (TileMarkers.drawnLastFrame != 1) {
				fail("the marked tile " + x + "," + y + " was not drawn (" + TileMarkers.drawnLastFrame + " markers drawn in the last frame)")
			}
			// saved for a look; the check is the outline's own pixels, not the whole frame, which the
			// walking guide changes from run to run
			checkImage("-tilemarker", SHOT.replace(".png", "-tilemarker.png"))
			val image: String = markerPixels()
			runMenuEntry(MiniMenuAction.TILE_MARKER, x, y, "Unmark tile")
			if (TileMarkers.markerCount() != 0) {
				fail("the Unmark tile entry left " + TileMarkers.markerCount() + " markers")
			}
			TileMarkers.enabled = markersWereEnabled
			firstImage += "; tile " + x + "," + y + " marked, drawn and unmarked: " + image
			markerStep = 2
		}
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
			val midX: Int = (TileMarkers.lastCorners[edge * 2] + TileMarkers.lastCorners[next * 2]) / 2
			val midY: Int = (TileMarkers.lastCorners[edge * 2 + 1] + TileMarkers.lastCorners[next * 2 + 1]) / 2
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
	private fun openSubInterface(interfaceId: Int, parentHash: Int, openType: SubInterfaceType) {
		try {
			SubInterfaceOpener.openSubInterface(interfaceId, parentHash, openType, true)
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
