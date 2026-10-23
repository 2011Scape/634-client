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
import com.jagex.game.runetek5.plugin.api.Skill
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
 * Not part of the original client: RuneLite's Mining, for the rocks of the 634 server (Void). A panel at
 * the top left says whether the player is mining and how much was mined this session and an hour (the XP
 * Tracker's Mining actions, as RuneLite takes them from its XP tracker; the ore lines counted when the XP
 * Tracker is off); it goes after Reset stats minutes without mining. A mined-out rock shows a pie of the
 * time until its ore is back.
 *
 * RuneLite reads the respawn time from an OSRS script; Void's is its ResourceRespawn: the ore's base time
 * plus seven times its square root over 0.6, in ticks, at this server's player counts (clay always 2), so
 * the pies are close, not exact; a rock that is back takes its pie away. The rocks come from Void's rocks and
 * ores tables ([ROCKS], rock, depleted rock and ticks). Motherlode, Daeyalt, amethyst and Volcanic Mine are
 * OSRS-only. A built-in plugin (BuiltinPlugins, docs/PLUGINS.md).
 */
object Mining : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("mining", "Mining", "1", PluginApi.VERSION, "Shows how much you have mined this session and an hour, and times mined-out rocks coming back.", "Pixel")

	/** Void's mining animations: each pickaxe's low, medium and high swings, the adze's, and the Dungeoneering pickaxes'. */
	@JvmField
	val MINING_ANIMATIONS: IntArray = intArrayOf(
		624, 625, 626, 627, 628, 629, 12189,
		10335, 10336, 10337, 10338, 10339, 10340, 10341, 12190,
		10342, 10343, 10344, 10345, 10346, 10347, 10348, 12191,
		13074, 13075, 13076, 13077, 13078, 13079, 13080, 13081, 13082, 13083, 13084,
		13107, 13108, 13109, 13110, 13111, 13112, 13113, 13114, 13115, 13116, 13117,
	)

	/** RuneLite's MINING_PATTERN, as Void says it: "You swing your pickaxe at the rock." */
	@JvmField
	val MINING_PATTERN: Regex = Regex("You swing your pick(?:axe)? at the (?:rock|star|deposit)(?:\\.|!)")

	/** An ore mined (one or more, with Varrock armour): what is counted while the XP Tracker is off. */
	@JvmField
	val MINED_PATTERN: Regex = Regex("You manage to mine (?:some|an?|two|three|four) .+")

	/** Void's rocks: rock, depleted rock, respawn ticks. */
	const val ROCKS: String = "2090,450,23;2091,451,23;2092,450,41;2093,451,41;2094,450,23;2095,451,23;2096,450,127;2097,451,127;2098,450,217;2099,451,217;2100,450,217;2101,451,217;2102,450,369;2103,451,369;2104,450,655;2105,451,655;2106,450,1369;2107,451,1369;2108,450,2;2109,451,2;2111,451,215;2609,21296,217;2610,21297,217;2611,21298,217;5766,5763,2;5767,5764,2;5768,5763,217;5769,5764,217;5770,5763,127;5771,5764,127;5772,5765,127;5773,5763,41;5774,5764,41;5775,5765,41;5776,5763,23;5777,5764,23;5778,5765,23;5779,5763,23;5780,5764,23;5781,5765,23;5782,5763,655;5783,5765,655;5784,5763,369;5785,5764,369;5786,5765,369;6669,6947,201;6670,6948,201;6671,11193,201;6943,6947,41;6944,6948,41;6945,6947,217;6946,6948,217;9708,9723,23;9709,9724,23;9710,9725,23;9711,9723,2;9712,9724,2;9713,9725,2;9714,9723,217;9715,9724,217;9716,9725,217;9717,9723,41;9718,9724,41;9719,9725,41;9720,9723,217;9721,9724,217;9722,9725,217;9737,37700,23;10574,19003,217;10575,19004,217;10576,19005,217;10577,10580,2;10578,10581,2;10579,10582,2;10583,10585,118;10584,10586,118;10948,10944,127;10949,10945,2;11165,37701,23;11179,6947,215;11180,6948,215;11181,11193,215;11183,37700,217;11184,37701,217;11185,37702,217;11186,37700,217;11187,37701,217;11188,37702,217;11189,37700,2;11190,37701,2;11191,37702,2;11194,11365,215;11195,11366,215;11364,11367,215;11930,10580,127;11931,10581,127;11932,10582,127;11933,10580,23;11934,10581,23;11935,10582,23;11936,10580,23;11937,10581,23;11938,10582,23;11939,10580,655;11940,10581,655;11941,10582,655;11942,10580,369;11943,10581,369;11944,10582,369;11945,11555,369;11946,11556,369;11947,11557,369;11948,11555,217;11949,11556,217;11950,11557,217;11951,11555,217;11952,11556,217;11953,11557,217;11954,11555,41;11955,11556,41;11956,11557,41;11957,11555,23;11958,11556,23;11959,11557,23;11960,11555,23;11961,11556,23;11962,11557,23;11963,11555,127;11964,11556,127;11965,11557,127;14850,14832,127;14851,14833,127;14852,14834,127;14853,14832,369;14854,14833,369;14855,14834,369;14856,14832,41;14857,14833,41;14858,14834,41;14859,14832,1369;14860,14833,1369;14861,14834,1369;14862,14832,655;14863,14833,655;14864,14834,655;14900,14894,41;14901,14895,41;14902,14892,23;14903,14893,23;14904,14892,2;14905,14893,2;14906,14892,23;14907,14893,23;14913,14892,41;14914,14893,41;15246,15249,127;15247,15250,127;15248,15251,127;15503,11555,2;15504,11556,2;15505,11557,2;16998,17007,217;16999,17008,217;17000,17009,217;17001,17007,217;17002,17008,217;17003,17009,217;17004,17007,215;17005,17008,215;17006,17009,215;18991,19003,23;18992,19004,23;18993,19005,23;18994,19003,23;18995,19004,23;18996,19005,23;18997,19003,127;18998,19004,127;18999,19005,127;19000,19003,41;19001,19004,41;19002,19005,41;19006,18952,23;19007,18953,23;19008,18954,23;19012,18952,369;19013,18953,369;19014,18954,369;19018,18952,655;19019,18953,655;19020,18954,655;19024,18952,23;19025,18953,23;19026,18954,23;21275,21296,655;21276,21297,655;21277,21298,655;21281,21296,41;21282,21297,41;21283,21298,41;21284,21296,23;21285,21297,23;21286,21298,23;21287,21296,127;21288,21297,127;21289,21298,127;21293,21296,23;21294,21297,23;21295,21298,23;29215,29218,127;29216,29219,127;29217,29220,127;29221,29218,41;29222,29219,41;29223,29220,41;29224,29218,217;29225,29219,217;29226,29220,217;29227,29218,23;29228,29219,23;29229,29220,23;29230,29218,23;29231,29219,23;29232,29220,23;29233,29218,655;29234,29219,655;29235,29220,655;29236,29218,369;29237,29219,369;29238,29220,369;31062,31059,2;31063,31060,2;31064,31061,2;31065,31059,217;31066,31060,217;31067,31061,217;31068,31059,127;31069,31060,127;31070,31061,127;31071,31059,41;31072,31060,41;31073,31061,41;31074,31059,217;31075,31060,217;31076,31061,217;31077,37639,23;31080,37639,23;31083,31059,655;31084,31060,655;31085,31061,655;31086,31059,369;31087,31060,369;31088,31061,369;32426,33400,127;32427,33401,127;32428,33402,127;32429,33400,2;32430,33401,2;32431,33402,2;32432,33400,217;32433,33401,217;32434,33402,217;32435,33400,655;32436,33401,655;32437,33402,655;32438,33400,369;32439,33401,369;32440,33402,369;32441,33400,41;32442,33401,41;32443,33402,41;32444,33400,217;32445,33401,217;32446,33402,217;32449,32447,127;32450,32448,127;32451,32447,41;32452,32448,41;33078,33400,1369;33079,33401,1369;33220,33222,118;33221,33223,118;37208,37700,1369;37304,10580,217;37305,10581,217;37306,10582,217;37307,10580,41;37308,10581,41;37309,10582,41;37310,10580,217;37312,10582,217;37637,37648,23;37638,37648,23;37642,31059,23;37643,31060,23;37644,31061,23;37645,31059,23;37646,31060,23;37647,31061,23;37670,11552,217;37685,37702,23;37686,37700,23;37687,37701,23;37688,37702,23;37689,37700,655;37690,37701,655;37691,37702,655;37692,37700,369;37693,37701,369;37694,37702,369;37695,37701,1369;37696,37702,1369;37697,37700,127;37698,37701,127;37699,37702,127;45067,29218,217;45068,29220,217;45069,29218,1369;45070,29220,1369"

	const val TICK_MILLIS: Long = 600L

	@JvmField var enabled: Boolean = false
	@JvmField var statTimeout: Int = 5
	@JvmField var showMiningStats: Boolean = true

	/** RuneLite's MiningSession, with the ores counted for when the XP Tracker is off. */
	class Session(@JvmField val start: Long) {
		@JvmField var lastMined: Long = 0L
		@JvmField var mined: Int = 0
	}

	/** RuneLite's RockRespawn: a mined-out rock's south-west tile (world), its size and its time. */
	class RockRespawn(@JvmField val x: Int, @JvmField val y: Int, @JvmField val level: Int, @JvmField val sizeX: Int, @JvmField val sizeY: Int, @JvmField val start: Long, @JvmField val millis: Long)

	@JvmField var session: Session? = null
	@JvmField val respawns: ArrayList<RockRespawn> = ArrayList()

	private val rocks: HashMap<Int, IntArray> = HashMap()

	@JvmField var clock: () -> Long = { lang.currentTimeMillis() }

	private var context: PluginContext? = null

	private val point: IntArray = IntArray(2)

	/** RuneLite's MiningOverlay. */
	@JvmField
	val panel: OverlayPanel = object : OverlayPanel("MiningOverlay") {
		init {
			addMenuEntry("Reset", "Mining overlay", Task { session = null })
		}

		override fun render(g: Graphics, view: Viewport): Size? {
			val client: ClientApi = context?.client() ?: return null
			val current: Session = session ?: return null
			if (!enabled || !showMiningStats || client.gameState() !== GameState.LOGGED_IN) {
				return null
			}
			if (isMining(client)) {
				panelComponent.addTitle("Mining", GREEN)
			} else {
				panelComponent.addTitle("NOT mining", RED)
			}
			val actions: Int = totalMined(current)
			if (actions > 0) {
				panelComponent.addLine("Total mined:", WHITE, actions.toString(), WHITE)
				if (actions > 2) {
					panelComponent.addLine("Mined/hr:", WHITE, minedPerHour(current).toString(), WHITE)
				}
			}
			return super.render(g, view)
		}
	}

	/** RuneLite's MiningRocksOverlay: the respawn pies, under the interfaces. */
	@JvmField
	val rocksOverlay: Overlay = Overlay { g, view -> context?.client()?.let { drawTimers(g, view, it) } }

	private val tickListener: EventListener<GameTick> = EventListener { _ -> context?.client()?.let { tick(it) } }

	private val chatListener: EventListener<ChatMessage> = EventListener { event -> chat(event) }

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
		context?.client()?.consolePrint(if (enabled) "Mining enabled." else "Mining disabled.")
	}

	init {
		for (row in ROCKS.split(';')) {
			val f: List<String> = row.split(',')
			rocks[f[0].toInt()] = intArrayOf(f[1].toInt(), f[2].toInt())
		}
	}

	override fun start(context: PluginContext) {
		this.context = context
		val s: SettingsSchema = context.settings()
		s.toggle("enabled", "Mining", false)
		// RuneLite's MiningConfig, in its order
		s.number("statTimeout", "Reset stats", 5, 1, 60, 1)
		s.toggle("showMiningStats", "Show session stats", true)
		readSettings(context.config())
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		context.events().subscribe(Events.GAME_TICK, tickListener)
		context.events().subscribe(Events.CHAT_MESSAGE, chatListener)
		context.events().subscribe(Events.LOC_CHANGED, locListener)
		context.events().subscribe(Events.GAME_STATE_CHANGED, stateListener)
		context.addOverlay(panel)
		context.addOverlay(rocksOverlay)
		context.registerCommand("mining", toggle)
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
		showMiningStats = config.getBoolean("showMiningStats", true)
	}

	@JvmStatic
	fun isMining(client: ClientApi): Boolean {
		val player: Player = client.localPlayer() ?: return false
		return player.animationId() in MINING_ANIMATIONS
	}

	/** The XP Tracker's Mining actions (RuneLite's), or the ores the plugin counted when it has more (the tracker off or just begun). */
	@JvmStatic
	fun totalMined(current: Session): Int {
		val state: XpTrackerState? = trackerState()
		return maxOf(state?.actions ?: 0, current.mined)
	}

	@JvmStatic
	fun minedPerHour(current: Session): Int {
		val state: XpTrackerState? = trackerState()
		if (state != null && state.actions >= current.mined) {
			return state.actionsPerHour()
		}
		val elapsed: Long = maxOf(MINUTE_MILLIS, clock() - current.start)
		return (current.mined.toDouble() * HOUR_MILLIS / elapsed).toInt()
	}

	private fun trackerState(): XpTrackerState? = if (XpTracker.enabled) XpTracker.states[Skill.MINING] else null

	/** RuneLite's onGameTick: the session kept up while mining and dropped after the timeout, and the old pies dropped. */
	@JvmStatic
	fun tick(client: ClientApi) {
		val now: Long = clock()
		respawns.removeAll { now - it.start > it.millis }
		if (!enabled || client.gameState() !== GameState.LOGGED_IN) {
			return
		}
		if (isMining(client)) {
			val current: Session = session ?: Session(now).also { session = it }
			current.lastMined = now
		}
		val current: Session = session ?: return
		if (current.lastMined != 0L && now - current.lastMined >= statTimeout * MINUTE_MILLIS) {
			session = null
		}
	}

	/** RuneLite's onChatMessage: a swing at a rock starts the session; an ore mined is counted. */
	@JvmStatic
	fun chat(event: ChatMessage) {
		if (!enabled || (event.type != ChatType.GAME && event.type != ChatType.GAME_FILTERED)) {
			return
		}
		val message: String = ClientFeatures.plainText(event.message ?: return)
		val now: Long = clock()
		if (MINING_PATTERN.matches(message)) {
			val current: Session = session ?: Session(now).also { session = it }
			current.lastMined = now
		} else if (MINED_PATTERN.matches(message)) {
			val current: Session = session ?: Session(now).also { session = it }
			current.lastMined = now
			current.mined++
		}
	}

	/** A rock mined out starts its pie; the rock back (or any change on the tile) ends it. */
	@JvmStatic
	fun locChanged(client: ClientApi, event: LocChanged) {
		val x: Int = event.sceneX + client.sceneBaseX()
		val y: Int = event.sceneY + client.sceneBaseY()
		respawns.removeAll { it.x == x && it.y == y && it.level == event.level }
		val rock: IntArray = rocks[event.oldId] ?: return
		if (event.newId != rock[0]) {
			return
		}
		val type: LocDefinition? = client.locType(event.oldId)
		respawns.add(RockRespawn(x, y, event.level, type?.sizeX() ?: 1, type?.sizeY() ?: 1, clock(), rock[1] * TICK_MILLIS))
	}

	/** RuneLite's MiningRocksOverlay: a pie on each mined-out rock. */
	private fun drawTimers(g: Graphics, view: Viewport, client: ClientApi) {
		if (!enabled || respawns.isEmpty() || client.gameState() !== GameState.LOGGED_IN) {
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
