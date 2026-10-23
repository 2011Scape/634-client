package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.ChatType
import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.Graphics
import com.jagex.game.runetek5.plugin.api.LocDefinition
import com.jagex.game.runetek5.plugin.api.Overlay
import com.jagex.game.runetek5.plugin.api.OverlayPanel
import com.jagex.game.runetek5.plugin.api.Player
import com.jagex.game.runetek5.plugin.api.Plugin
import com.jagex.game.runetek5.plugin.api.PluginApi
import com.jagex.game.runetek5.plugin.api.PluginConfig
import com.jagex.game.runetek5.plugin.api.PluginContext
import com.jagex.game.runetek5.plugin.api.PluginDescriptor
import com.jagex.game.runetek5.plugin.api.SettingsSchema
import com.jagex.game.runetek5.plugin.api.Size
import com.jagex.game.runetek5.plugin.api.Task
import com.jagex.game.runetek5.plugin.api.Viewport
import com.jagex.game.runetek5.plugin.api.events.ChatMessage
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.GameStateChanged
import com.jagex.game.runetek5.plugin.api.events.GameTick
import com.jagex.game.runetek5.plugin.api.events.LocChanged
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: RuneLite's Woodcutting, for the trees of the 634 server (Void). A panel
 * at the top left counts the logs cut this session and the logs an hour, and says whether the player is
 * chopping; it goes after Reset stats minutes without chopping. A bird's nest falling gives a notification,
 * and a felled tree's stump shows a pie of the time until the tree grows back.
 *
 * RuneLite reads the respawn time from an OSRS script; Void's is its ResourceRespawn: the logs' base time
 * plus seven times its square root over 0.6, in ticks, at the player counts this server has (fewer at a
 * full world), and normal trees 50 to 100 ticks at random, shown as 100. So the pies are close, not exact;
 * a stump that turns back into a tree takes its pie away. The trees come from Void's trees and logs tables
 * ([TREES], tree, stump and ticks, negative for the random ones). Forestry, redwoods, bark and clue nests
 * are OSRS-only. A built-in plugin (BuiltinPlugins, docs/PLUGINS.md).
 */
object Woodcutting : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("woodcutting", "Woodcutting", "1", PluginApi.VERSION, "Counts the logs you cut and logs an hour, tells you when a bird's nest falls, and times felled trees growing back.", "Pixel")

	/** Void's chopping animations: the hatchets', their ivy and tired ones, the adze's and the Dungeoneering hatchets'. */
	@JvmField
	val WOODCUTTING_ANIMS: IntArray = intArrayOf(
		879, 877, 875, 873, 871, 869, 867, 2846, 1905, 1907, 10251, 10249, 5383,
		12282, 2847, 880, 878, 876, 874, 872, 870, 12323, 12324, 12325,
		12329, 12330, 12331, 12332, 12333, 12334, 12335, 12336, 12337, 12338, 12339,
		12340, 12341, 12342, 12343, 12344, 12345, 12346, 12347, 12348, 12349,
		13118, 13119, 13120, 13121, 13122, 13123, 13124, 13125, 13126, 13127, 13128,
	)

	/** RuneLite's WOOD_CUT_PATTERN, as Void says it: "You get some oak logs." */
	@JvmField
	val WOOD_CUT_PATTERN: Regex = Regex("You get (?:some|an)[\\w ]+(?:logs?|mushrooms)\\.")

	const val NEST_MESSAGE: String = "A bird's nest falls out of the tree"

	/** Void's trees: tree, stump, respawn ticks (negative: a random time up to that). */
	const val TREES: String = "1276,1342,-100;1277,1343,-100;1278,7400,-100;1280,7400,-100;1281,1356,58;1282,1347,-100;1283,1347,-100;1284,6212,-100;1285,1347,-100;1286,1351,-100;1289,1353,-100;1291,23054,-100;1307,7400,147;1309,7402,217;1315,1342,-100;1316,1355,-100;1318,1355,-100;1319,1355,-100;1330,1355,-100;1331,1355,-100;1332,1355,-100;1365,1352,-100;1383,1358,-100;1384,1359,-100;2023,3371,132;2289,2310,176;2410,12004,-100;2411,7400,-100;2887,17493,-100;2889,4819,-100;2890,4821,-100;3033,1342,-100;3034,7400,-100;3037,1355,58;3879,3880,-100;3881,3880,-100;3882,3880,-100;3883,3880,-100;4060,4061,176;4674,7400,147;4818,4819,-100;4820,4821,-100;5551,5554,58;5552,5554,58;5553,5554,58;9034,9035,217;9036,9037,217;11999,12007,58;12000,12001,217;15062,9037,217;15948,15950,-100;15951,15953,-100;15954,15956,-100;16265,9035,-100;16604,16605,94;21273,21274,184;24168,24169,-100;28951,28954,201;28952,28955,201;28953,28956,201;29088,3371,132;29089,3371,132;37477,1342,-100;37478,1342,-100;37479,1356,58;37480,1350,58;37481,1347,-100;37482,1351,-100;37483,1358,-100;37652,7400,-100;37821,37822,293;37823,37824,248;42893,42860,-100;46274,9035,217;46275,9037,217;46277,7400,147;46318,46319,150;46320,46321,150;46322,46323,150;46324,46325,150;47594,47595,-100;47596,47597,-100;47598,47599,-100;47600,47601,-100"

	/** A client cycle is 20 ms, a game tick 600. */
	const val TICK_MILLIS: Long = 600L

	@JvmField var enabled: Boolean = false
	@JvmField var statTimeout: Int = 5
	@JvmField var showNestNotification: Boolean = true
	@JvmField var showWoodcuttingStats: Boolean = true
	@JvmField var showRespawnTimers: Boolean = true

	/** RuneLite's WoodcuttingSession: when it began, the last chop, the logs and the rate. */
	class Session(@JvmField val start: Long) {
		@JvmField var lastChopping: Long = 0L
		@JvmField var logsCut: Int = 0
		@JvmField var logsPerHr: Int = 0
		@JvmField var active: Boolean = true

		fun setLastChopping(now: Long) {
			lastChopping = now
			active = true
		}

		/** RuneLite's incrementLogsCut: the rate once three are cut. */
		fun incrementLogsCut(now: Long) {
			logsCut++
			val elapsed: Long = now - start
			if (logsCut >= 3 && elapsed > 0L) {
				logsPerHr = (logsCut.toDouble() * HOUR_MILLIS / elapsed).toInt()
			}
		}
	}

	/** RuneLite's TreeRespawn: a felled tree's south-west tile (world), its size, and its time. */
	class TreeRespawn(@JvmField val x: Int, @JvmField val y: Int, @JvmField val level: Int, @JvmField val sizeX: Int, @JvmField val sizeY: Int, @JvmField val start: Long, @JvmField val millis: Long)

	@JvmField var session: Session? = null
	@JvmField val respawns: ArrayList<TreeRespawn> = ArrayList()

	/** By tree: its stump and ticks. */
	private val trees: HashMap<Int, IntArray> = HashMap()

	/** The clock, the wall's; a test sets its own. */
	@JvmField var clock: () -> Long = { lang.currentTimeMillis() }

	private var context: PluginContext? = null

	private val point: IntArray = IntArray(2)

	/** RuneLite's WoodcuttingOverlay. */
	@JvmField
	val panel: OverlayPanel = object : OverlayPanel("WoodcuttingOverlay") {
		init {
			addMenuEntry("Reset", "Woodcutting overlay", Task { session = null })
		}

		override fun render(g: Graphics, view: Viewport): Size? {
			val client: ClientApi = context?.client() ?: return null
			val current: Session = session ?: return null
			if (!enabled || !current.active || !showWoodcuttingStats || client.gameState() !== GameState.LOGGED_IN) {
				return null
			}
			if (isWoodcutting(client)) {
				panelComponent.addTitle("Woodcutting", GREEN)
			} else {
				panelComponent.addTitle("NOT woodcutting", RED)
			}
			if (current.logsCut > 0) {
				panelComponent.addLine("Logs cut:", WHITE, current.logsCut.toString(), WHITE)
				if (current.logsPerHr > 0) {
					panelComponent.addLine("Logs/hr:", WHITE, current.logsPerHr.toString(), WHITE)
				}
			}
			return super.render(g, view)
		}
	}

	/** RuneLite's WoodcuttingSceneOverlay: the respawn pies, under the interfaces. */
	@JvmField
	val sceneOverlay: Overlay = Overlay { g, view -> context?.client()?.let { drawTimers(g, view, it) } }

	private val tickListener: EventListener<GameTick> = EventListener { _ -> context?.client()?.let { tick(it) } }

	private val chatListener: EventListener<ChatMessage> = EventListener { event -> context?.client()?.let { chat(it, event) } }

	private val locListener: EventListener<LocChanged> = EventListener { event -> context?.client()?.let { locChanged(it, event) } }

	private val stateListener: EventListener<GameStateChanged> = EventListener { event ->
		if (event.state === GameState.LOGIN_SCREEN || event.state === GameState.LOADING) {
			respawns.clear()
		}
	}

	private val settingListener: EventListener<SettingChanged> = EventListener { event ->
		if (event.pluginId == DESCRIPTOR.id) {
			context?.config()?.let { readSettings(it) }
		}
	}

	private val toggle: CommandHandler = CommandHandler { _, _ ->
		enabled = !enabled
		context?.config()?.setBoolean("enabled", enabled)
		context?.config()?.save()
		context?.client()?.consolePrint(if (enabled) "Woodcutting enabled." else "Woodcutting disabled.")
	}

	init {
		for (row in TREES.split(';')) {
			val f: List<String> = row.split(',')
			trees[f[0].toInt()] = intArrayOf(f[1].toInt(), f[2].toInt())
		}
	}

	override fun start(context: PluginContext) {
		this.context = context
		val s: SettingsSchema = context.settings()
		s.toggle("enabled", "Woodcutting", false)
		// RuneLite's WoodcuttingConfig, in its order (less its OSRS-only Forestry, redwood and clue nest settings)
		s.number("statTimeout", "Reset stats", 5, 1, 60, 1)
		s.toggle("showNestNotification", "Bird nest notification", true)
		s.toggle("showWoodcuttingStats", "Show session stats", true)
		s.toggle("showRespawnTimers", "Show respawn timers", true)
		readSettings(context.config())
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		context.events().subscribe(Events.GAME_TICK, tickListener)
		context.events().subscribe(Events.CHAT_MESSAGE, chatListener)
		context.events().subscribe(Events.LOC_CHANGED, locListener)
		context.events().subscribe(Events.GAME_STATE_CHANGED, stateListener)
		context.addOverlay(panel)
		context.addOverlay(sceneOverlay)
		context.registerCommand("woodcutting", toggle)
	}

	override fun stop() {
		context = null
		session = null
		respawns.clear()
	}

	@JvmStatic
	fun readSettings(config: PluginConfig) {
		enabled = config.getBoolean("enabled", false)
		statTimeout = config.getInt("statTimeout", 5)
		showNestNotification = config.getBoolean("showNestNotification", true)
		showWoodcuttingStats = config.getBoolean("showWoodcuttingStats", true)
		showRespawnTimers = config.getBoolean("showRespawnTimers", true)
	}

	@JvmStatic
	fun isWoodcutting(client: ClientApi): Boolean {
		val player: Player = client.localPlayer() ?: return false
		return player.animationId() in WOODCUTTING_ANIMS
	}

	/** RuneLite's onGameTick: the session kept up while chopping, put away after the timeout, and the old pies dropped. */
	@JvmStatic
	fun tick(client: ClientApi) {
		val now: Long = clock()
		respawns.removeAll { now - it.start > it.millis }
		if (!enabled || client.gameState() !== GameState.LOGGED_IN) {
			return
		}
		if (isWoodcutting(client)) {
			val current: Session = session ?: Session(now).also { session = it }
			current.setLastChopping(now)
		}
		val current: Session = session ?: return
		if (current.lastChopping != 0L && now - current.lastChopping >= statTimeout * MINUTE_MILLIS) {
			current.active = false
		}
	}

	/** RuneLite's onChatMessage: a log cut, and a nest falling. */
	@JvmStatic
	fun chat(client: ClientApi, event: ChatMessage) {
		if (!enabled || (event.type != ChatType.GAME && event.type != ChatType.GAME_FILTERED)) {
			return
		}
		val message: String = ClientFeatures.plainText(event.message ?: return)
		if (WOOD_CUT_PATTERN.matches(message)) {
			val now: Long = clock()
			val current: Session = session ?: Session(now).also { session = it }
			current.setLastChopping(now)
			current.incrementLogsCut(now)
		}
		if (message.contains(NEST_MESSAGE) && showNestNotification) {
			client.notify("A bird nest has spawned!")
		}
	}

	/** A tree felled to its stump starts its pie; the stump growing back (or any change on the tile) ends it. */
	@JvmStatic
	fun locChanged(client: ClientApi, event: LocChanged) {
		val x: Int = event.sceneX + client.sceneBaseX()
		val y: Int = event.sceneY + client.sceneBaseY()
		respawns.removeAll { it.x == x && it.y == y && it.level == event.level }
		val tree: IntArray = trees[event.oldId] ?: return
		if (event.newId != tree[0]) {
			return
		}
		val type: LocDefinition? = client.locType(event.oldId)
		val ticks: Int = kotlin.math.abs(tree[1])
		respawns.add(TreeRespawn(x, y, event.level, type?.sizeX() ?: 1, type?.sizeY() ?: 1, clock(), ticks * TICK_MILLIS))
	}

	/** RuneLite's renderTimers: a pie in the middle of each felled tree's tiles. */
	private fun drawTimers(g: Graphics, view: Viewport, client: ClientApi) {
		if (!enabled || !showRespawnTimers || respawns.isEmpty() || client.gameState() !== GameState.LOGGED_IN) {
			return
		}
		val now: Long = clock()
		for (respawn in respawns) {
			val progress: Float = (now - respawn.start).toFloat() / respawn.millis
			val sceneX: Int = respawn.x - client.sceneBaseX()
			val sceneY: Int = respawn.y - client.sceneBaseY()
			if (progress > 1f || sceneX < 0 || sceneY < 0 || sceneX >= client.sceneSize() || sceneY >= client.sceneSize()) {
				continue
			}
			// the footprint's middle, in scene units (512 a tile)
			if (client.projectPoint(sceneX * 512 + respawn.sizeX * 256, sceneY * 512 + respawn.sizeY * 256, respawn.level, 0, point)) {
				ProgressPie.draw(g, view.x + point[0], view.y + point[1], progress, YELLOW, ORANGE)
			}
		}
	}

	private const val HOUR_MILLIS: Long = 3_600_000L
	private const val MINUTE_MILLIS: Long = 60_000L

	private const val WHITE: Int = -0x1
	private const val GREEN: Int = 0xFF00FF00.toInt()
	private const val RED: Int = 0xFFFF0000.toInt()
	private const val YELLOW: Int = 0xFFFFFF00.toInt()
	private const val ORANGE: Int = 0xFFFFC800.toInt()
}
