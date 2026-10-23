package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.Graphics
import com.jagex.game.runetek5.plugin.api.LocVisitor
import com.jagex.game.runetek5.plugin.api.Overlay
import com.jagex.game.runetek5.plugin.api.OverlayPanel
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
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.GameStateChanged
import com.jagex.game.runetek5.plugin.api.events.GameTick
import com.jagex.game.runetek5.plugin.api.events.LocChanged
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import com.jagex.game.runetek5.plugin.api.events.StatChanged
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic
import kotlin.math.ceil

/**
 * Not part of the original client: RuneLite's Agility, for the courses of the 634 server (Void). The
 * obstacles of the Gnome Stronghold, Barbarian Outpost (both with their advanced laps), Wilderness and Ape
 * Atoll courses, and Void's agility shortcuts, show their click boxes; a panel counts your laps, the laps to
 * your goal (the XP Tracker's: the next level) and the laps an hour, and goes after "Hide lap count"
 * minutes without a lap.
 *
 * Void sends no lap message and no lap count, so a lap is the course's lap bonus coming in as Agility
 * experience (39 at the Gnome Stronghold, 605 and 615 on the advanced laps, 46.3 at the Barbarian Outpost,
 * 499 in the Wilderness, 200 on Ape Atoll), and a lap's experience is measured from one bonus to the next
 * (the courses' own sums before the first). Marks of grace, the rooftop courses, portals, the Brimhaven
 * Agility Arena, the Werewolf course's stick and traps and the Hallowed Sepulchre are not on Void. A
 * built-in plugin (BuiltinPlugins, docs/PLUGINS.md).
 */
object Agility : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("agility", "Agility", "1", PluginApi.VERSION, "Shows the agility obstacles and shortcuts, and counts your laps, the laps to your goal and the laps an hour.", "Pixel")

	/** Void's courses (RuneLite's Courses): the lap bonus as whole experience (both ways a tenth can round), and a lap's experience. */
	class Course(@JvmField val name: String, @JvmField val bonuses: IntArray, @JvmField val lapXp: Double)

	@JvmField
	val COURSES: Array<Course> = arrayOf(
		Course("Gnome Stronghold", intArrayOf(39), 86.5),
		Course("Gnome Stronghold advanced", intArrayOf(605), 705.0),
		Course("Barbarian Outpost", intArrayOf(46, 47), 153.3),
		Course("Barbarian Outpost advanced", intArrayOf(615), 705.0),
		Course("Wilderness", intArrayOf(499), 571.5),
		Course("Ape Atoll", intArrayOf(200), 580.0),
	)

	/** The courses' obstacles: the locations their scripts answer (Void's agility course scripts). */
	@JvmField
	val OBSTACLES: IntArray = intArrayOf(
		1948, 2283, 2285, 2286, 2288, 2295, 2297, 2302, 2307, 2308, 2309, 2312, 2314, 2328, 4059, 12568,
		12570, 12573, 12576, 12578, 12618, 20210, 20211, 35970, 37704, 43526, 43527, 43528, 43529, 43531, 43532, 43533,
		43539, 43543, 43544, 43581, 43587, 43595, 43597,
	)

	/** Void's agility shortcuts: the locations its shortcut scripts and objects answer. */
	@JvmField
	val SHORTCUTS: IntArray = intArrayOf(
		51, 993, 2296, 2333, 2334, 2335, 3730, 3931, 3932, 3933, 5099, 5100, 5948, 7527, 9301, 9302,
		9309, 9310, 9311, 9312, 9315, 10536, 11844, 17034, 17036, 17037, 17038, 17039, 17040, 17041, 17042, 17043,
		17047, 17048, 17049, 17050, 17051, 17052, 17062, 17063, 17065, 17067, 17068, 18411, 19222, 29370, 29460, 34776,
		35997, 35999, 45205, 48208,
	)

	@JvmField var showClickboxes: Boolean = true
	@JvmField var showLapCount: Boolean = true
	@JvmField var lapTimeout: Int = 5
	@JvmField var lapsToLevel: Boolean = true
	@JvmField var lapsPerHour: Boolean = true
	@JvmField var overlayColor: Int = GREEN
	@JvmField var highlightShortcuts: Boolean = true

	/** RuneLite's AgilitySession. */
	class Session(@JvmField val course: Course) {
		@JvmField var lastLapCompleted: Long = 0L
		@JvmField var totalLaps: Int = 0
		@JvmField var lapsTillGoal: Int = 0
		@JvmField var lapsPerHour: Int = 0
		@JvmField val lapTimes: ArrayList<Long> = ArrayList()

		/** The Agility experience gained since the last lap, and the last lap's (0 before one is measured). */
		@JvmField var xpSinceLap: Int = 0
		@JvmField var measuredLapXp: Int = 0
	}

	@JvmField var session: Session? = null

	/** The obstacles and shortcuts in the scene: tile x, tile y, level, id, and 1 for a shortcut. */
	@JvmField val found: ArrayList<IntArray> = ArrayList()

	/** For the smoke test: the click boxes the last frame drew. */
	@JvmField var drawnLastFrame: Int = 0

	@JvmField var clock: () -> Long = { lang.currentTimeMillis() }

	private var sceneBaseX: Int = -1
	private var sceneBaseY: Int = -1

	private val xs: IntArray = IntArray(64)
	private val ys: IntArray = IntArray(64)

	private var context: PluginContext? = null

	/** RuneLite's LapCounterOverlay. */
	@JvmField
	val panel: OverlayPanel = object : OverlayPanel("LapCounterOverlay") {
		init {
			addMenuEntry("Reset", "Agility overlay", Task { session = null })
		}

		override fun render(g: Graphics, view: Viewport): Size? {
			val current: Session = session ?: return null
			if (!showLapCount || current.lastLapCompleted == 0L) {
				return null
			}
			if (clock() - current.lastLapCompleted >= lapTimeout * MINUTE_MILLIS) {
				current.lastLapCompleted = 0L
				return null
			}
			panelComponent.addLine("Total Laps:", WHITE, current.totalLaps.toString(), WHITE)
			if (lapsToLevel && current.lapsTillGoal > 0) {
				panelComponent.addLine("Laps until goal:", WHITE, current.lapsTillGoal.toString(), WHITE)
			}
			if (lapsPerHour && current.lapsPerHour > 0) {
				panelComponent.addLine("Laps per hour:", WHITE, current.lapsPerHour.toString(), WHITE)
			}
			return super.render(g, view)
		}
	}

	/** RuneLite's AgilityOverlay: the click boxes, under the interfaces. */
	@JvmField
	val overlay: Overlay = Overlay { g, view -> context?.client()?.let { draw(g, view, it) } }

	private val statListener: EventListener<StatChanged> = EventListener { event -> statChanged(event) }

	private val tickListener: EventListener<GameTick> = EventListener { _ -> context?.client()?.let { tick(it) } }

	private val locListener: EventListener<LocChanged> = EventListener { event -> locChanged(event) }

	private val stateListener: EventListener<GameStateChanged> = EventListener { event ->
		if (event.state !== GameState.LOGGED_IN) {
			found.clear()
			sceneBaseX = -1
		}
	}

	private val settingListener: EventListener<SettingChanged> = EventListener { event ->
		if (event.pluginId == DESCRIPTOR.id) {
			context?.config()?.let { readSettings(it) }
		}
	}

	private val toggle: CommandHandler = CommandHandler { _, _ ->
		showClickboxes = !showClickboxes
		context?.config()?.setBoolean("showClickboxes", showClickboxes)
		context?.config()?.save()
		context?.client()?.consolePrint(if (showClickboxes) "Agility click boxes on." else "Agility click boxes off.")
	}

	override fun start(context: PluginContext) {
		this.context = context
		val s: SettingsSchema = context.settings()
		// RuneLite's AgilityConfig, in its order, but the settings for what is not on Void
		s.toggle("showClickboxes", "Show clickboxes", true)
		s.toggle("showLapCount", "Show lap count", true)
		s.number("lapTimeout", "Hide lap count (minutes)", 5, 1, 60, 1)
		s.toggle("lapsToLevel", "Show laps until goal", true)
		s.toggle("lapsPerHour", "Show laps per hour", true)
		s.colour("overlayColor", "Overlay color", GREEN, false)
		s.toggle("highlightShortcuts", "Highlight agility shortcuts", true)
		readSettings(context.config())
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		context.events().subscribe(Events.STAT_CHANGED, statListener)
		context.events().subscribe(Events.GAME_TICK, tickListener)
		context.events().subscribe(Events.LOC_CHANGED, locListener)
		context.events().subscribe(Events.GAME_STATE_CHANGED, stateListener)
		context.addOverlay(panel)
		context.addOverlay(overlay)
		context.registerCommand("agility", toggle)
	}

	override fun stop() {
		context = null
		session = null
		found.clear()
		sceneBaseX = -1
	}

	@JvmStatic
	fun readSettings(config: PluginConfig) {
		showClickboxes = config.getBoolean("showClickboxes", true)
		showLapCount = config.getBoolean("showLapCount", true)
		lapTimeout = config.getInt("lapTimeout", 5)
		lapsToLevel = config.getBoolean("lapsToLevel", true)
		lapsPerHour = config.getBoolean("lapsPerHour", true)
		overlayColor = config.getColour("overlayColor", GREEN)
		highlightShortcuts = config.getBoolean("highlightShortcuts", true)
	}

	/** The course whose lap bonus [gain] experience is, or null. */
	@JvmStatic
	fun courseForBonus(gain: Int): Course? {
		for (course in COURSES) {
			if (gain in course.bonuses) {
				return course
			}
		}
		return null
	}

	/** RuneLite's onStatChanged for Agility: experience counted towards the lap, and a lap bonus ends one. */
	@JvmStatic
	fun statChanged(event: StatChanged) {
		if (event.skill != Skill.AGILITY || event.oldXp <= 0 || event.newXp <= event.oldXp) {
			return
		}
		val gain: Int = event.newXp - event.oldXp
		val course: Course? = courseForBonus(gain)
		val current: Session? = session
		if (course == null) {
			if (current != null) {
				current.xpSinceLap += gain
			}
			return
		}
		val lap: Session = if (current != null && current.course === course) current else Session(course).also { session = it }
		lap.xpSinceLap += gain
		if (lap.totalLaps > 0) {
			lap.measuredLapXp = lap.xpSinceLap
		}
		lap.xpSinceLap = 0
		lapCompleted(lap, event.newXp)
	}

	/** RuneLite's AgilitySession.incrementLapCount: the laps an hour from the last 30 laps, and the laps to the goal. */
	@JvmStatic
	fun lapCompleted(lap: Session, xp: Int) {
		val now: Long = clock()
		if (lap.lastLapCompleted != 0L && now >= lap.lastLapCompleted) {
			lap.lapTimes.add(now - lap.lastLapCompleted)
			if (lap.lapTimes.size > MAX_LAP_TIMES) {
				lap.lapTimes.removeAt(0)
			}
			val average: Long = lap.lapTimes.sum() / lap.lapTimes.size
			lap.lapsPerHour = if (average > 0) (HOUR_MILLIS / average).toInt() else 0
		}
		lap.lastLapCompleted = now
		lap.totalLaps++
		val goal: Int = XpTrackerState.xpForLevel(XpTrackerState.levelForXp(xp.toLong()) + 1)
		val remaining: Int = goal - xp
		val lapXp: Double = if (lap.measuredLapXp > 0) lap.measuredLapXp.toDouble() else lap.course.lapXp
		lap.lapsTillGoal = if (remaining > 0) ceil(remaining / lapXp).toInt() else 0
	}

	/** The obstacles found again when the scene moves (a map load), as RuneLite's onGameObjectSpawned does tile by tile. */
	@JvmStatic
	fun tick(client: ClientApi) {
		if (client.gameState() !== GameState.LOGGED_IN) {
			return
		}
		if (client.sceneBaseX() != sceneBaseX || client.sceneBaseY() != sceneBaseY) {
			sceneBaseX = client.sceneBaseX()
			sceneBaseY = client.sceneBaseY()
			scan(client)
		}
	}

	@JvmStatic
	fun scan(client: ClientApi) {
		found.clear()
		val size: Int = client.sceneSize()
		for (level in 0 until LEVELS) {
			for (x in 0 until size) {
				for (y in 0 until size) {
					client.locsAt(x, y, level, LocVisitor { id, _, _, _ -> add(x, y, level, id) })
				}
			}
		}
	}

	private fun add(x: Int, y: Int, level: Int, id: Int) {
		val shortcut: Boolean = id in SHORTCUTS
		if (!shortcut && id !in OBSTACLES) {
			return
		}
		for (f in found) {
			// a large obstacle is on every tile it covers: once is enough
			if (f[3] == id && f[2] == level && kotlin.math.abs(f[0] - x) <= LARGE && kotlin.math.abs(f[1] - y) <= LARGE) {
				return
			}
		}
		found.add(intArrayOf(x, y, level, id, if (shortcut) 1 else 0))
	}

	/** A location changed: an obstacle that went is dropped and one that came is added. */
	@JvmStatic
	fun locChanged(event: LocChanged) {
		found.removeAll { it[0] == event.sceneX && it[1] == event.sceneY && it[2] == event.level && it[3] == event.oldId }
		if (event.newId >= 0) {
			add(event.sceneX, event.sceneY, event.level, event.newId)
		}
	}

	private fun draw(g: Graphics, view: Viewport, client: ClientApi) {
		drawnLastFrame = 0
		if (!showClickboxes || client.gameState() !== GameState.LOGGED_IN) {
			return
		}
		val mouseX: Int = client.mouseX() - view.x
		val mouseY: Int = client.mouseY() - view.y
		for (f in found) {
			if ((f[4] == 0 || highlightShortcuts) && drawClickbox(g, view, client, f, mouseX, mouseY)) {
				drawnLastFrame++
			}
		}
	}

	/** RuneLite's renderHoverableArea: the fill always, the border darker under the mouse; false when off the screen. */
	private fun drawClickbox(g: Graphics, view: Viewport, client: ClientApi, f: IntArray, mouseX: Int, mouseY: Int): Boolean {
		val count: Int = client.locClickbox(f[0], f[1], f[2], f[3], xs, ys)
		if (count < 3) {
			return false
		}
		val hovered: Boolean = inside(xs, ys, count, mouseX, mouseY)
		for (i in 0 until count) {
			xs[i] += view.x
			ys[i] += view.y
		}
		g.fillPolygon(xs, ys, count, (FILL_ALPHA shl 24) or (overlayColor and 0xFFFFFF))
		g.polygon(xs, ys, count, if (hovered) Fishing.darker(overlayColor) else overlayColor)
		return true
	}

	/** Whether ([px], [py]) is inside the polygon (even-odd). */
	@JvmStatic
	fun inside(xs: IntArray, ys: IntArray, count: Int, px: Int, py: Int): Boolean {
		var result: Boolean = false
		var j: Int = count - 1
		for (i in 0 until count) {
			if ((ys[i] > py) != (ys[j] > py) && px < (xs[j] - xs[i]) * (py - ys[i]) / (ys[j] - ys[i]) + xs[i]) {
				result = !result
			}
			j = i
		}
		return result
	}

	private const val LEVELS: Int = 4
	private const val LARGE: Int = 2
	private const val MAX_LAP_TIMES: Int = 30
	private const val FILL_ALPHA: Int = 50
	private const val HOUR_MILLIS: Long = 3_600_000L
	private const val MINUTE_MILLIS: Long = 60_000L

	private const val WHITE: Int = -0x1
	private const val GREEN: Int = 0xFF00FF00.toInt()
}
