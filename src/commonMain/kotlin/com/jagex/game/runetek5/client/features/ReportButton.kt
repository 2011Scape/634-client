package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.Plugin
import com.jagex.game.runetek5.plugin.api.PluginApi
import com.jagex.game.runetek5.plugin.api.PluginConfig
import com.jagex.game.runetek5.plugin.api.PluginContext
import com.jagex.game.runetek5.plugin.api.PluginDescriptor
import com.jagex.game.runetek5.plugin.api.SettingsSchema
import com.jagex.game.runetek5.plugin.api.Widget
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.GameStateChanged
import com.jagex.game.runetek5.plugin.api.events.GameTick
import com.jagex.game.runetek5.plugin.api.events.KeyPressed
import com.jagex.game.runetek5.plugin.api.events.MouseClicked
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: RuneLite's Report Button. The chat box's Report Abuse button (751:11)
 * shows the date, the time logged in, the time in UTC, at Jagex's office (London), or here, the game ticks
 * since login, or how long the mouse and keyboard have been idle, instead of its words; it reads "Report
 * Abuse" again when the plugin is switched off. The zones are the platform's (util.Calendar); the browser
 * has only its own zone and UTC, so there London is the local time. A built-in plugin (BuiltinPlugins,
 * docs/PLUGINS.md).
 */
object ReportButton : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("reportbutton", "Report Button", "1", PluginApi.VERSION, "Shows the time, the date, your time logged in, game ticks or idle time on the Report Abuse button.", "Pixel")

	/** RuneLite's TimeStyle, in its order. */
	@JvmField
	val STYLES: Array<String> = arrayOf("Date", "Login Timer", "UTC Time", "Jagex HQ Time", "Local Time", "Game Ticks", "Idle Time")

	const val DATE: Int = 0
	const val LOGIN_TIME: Int = 1
	const val UTC: Int = 2
	const val JAGEX: Int = 3
	const val LOCAL_TIME: Int = 4
	const val GAME_TICKS: Int = 5
	const val IDLE_TIME: Int = 6

	/** RuneLite's TimeFormat. */
	@JvmField
	val FORMATS: Array<String> = arrayOf("12-hour", "24-hour")

	/** The button's text (751:11) and its own words. */
	const val INTERFACE: Int = 751
	const val TEXT: Int = 11
	const val REPORT_ABUSE: String = "Report Abuse"

	/** Client cycles (20 ms) to a game tick. */
	const val CYCLES_PER_TICK: Int = 30

	@JvmField val MONTHS: Array<String> = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

	@JvmField var enabled: Boolean = true
	@JvmField var time: Int = LOGIN_TIME
	@JvmField var twentyFourHour: Boolean = false

	/** When the player logged in (wall clock, client cycle), and the last input (client cycle). */
	private var loginMillis: Long = -1L
	private var loginCycle: Int = 0
	private var lastInputCycle: Int = 0
	private var lastMouseX: Int = -1
	private var lastMouseY: Int = -1

	/** Whether the plugin's text is on the button, to put its words back. */
	private var changed: Boolean = false

	@JvmField var clock: () -> Long = { lang.currentTimeMillis() }

	private var context: PluginContext? = null

	private val tickListener: EventListener<GameTick> = EventListener { _ -> context?.client()?.let { tick(it) } }

	private val stateListener: EventListener<GameStateChanged> = EventListener { event ->
		val client: ClientApi = context?.client() ?: return@EventListener
		if (event.state === GameState.LOGGED_IN && event.previous !== GameState.LOGGED_IN) {
			// RuneLite's onGameStateChanged: the login timer starts as the game is entered
			loginMillis = clock()
			loginCycle = client.clientCycle()
			lastInputCycle = client.clientCycle()
		}
	}

	private val keyListener: EventListener<KeyPressed> = EventListener { _ -> context?.client()?.let { lastInputCycle = it.clientCycle() } }

	private val clickListener: EventListener<MouseClicked> = EventListener { _ -> context?.client()?.let { lastInputCycle = it.clientCycle() } }

	private val settingListener: EventListener<SettingChanged> = EventListener { event ->
		if (event.pluginId == DESCRIPTOR.id) {
			context?.config()?.let { readSettings(it) }
			if (!enabled) {
				context?.client()?.let { restore(it) }
			}
		}
	}

	private val toggle: CommandHandler = CommandHandler { _, _ ->
		enabled = !enabled
		context?.config()?.setBoolean("enabled", enabled)
		context?.config()?.save()
		if (!enabled) {
			context?.client()?.let { restore(it) }
		}
		context?.client()?.consolePrint(if (enabled) "Report button enabled." else "Report button disabled.")
	}

	override fun start(context: PluginContext) {
		this.context = context
		val s: SettingsSchema = context.settings()
		s.toggle("enabled", "Report button", true)
		// RuneLite's ReportButtonConfig
		s.choice("time", "Display options", STYLES[LOGIN_TIME], STYLES)
		s.choice("switchTimeFormat", "Time format", FORMATS[0], FORMATS)
		readSettings(context.config())
		if (context.client().gameState() === GameState.LOGGED_IN) {
			loginMillis = clock()
			loginCycle = context.client().clientCycle()
		}
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		context.events().subscribe(Events.GAME_TICK, tickListener)
		context.events().subscribe(Events.GAME_STATE_CHANGED, stateListener)
		context.events().subscribe(Events.KEY_PRESSED, keyListener)
		context.events().subscribe(Events.MOUSE_CLICKED, clickListener)
		context.registerCommand("reportbutton", toggle)
	}

	override fun stop() {
		context?.client()?.let { restore(it) }
		context = null
	}

	@JvmStatic
	fun readSettings(config: PluginConfig) {
		enabled = config.getBoolean("enabled", true)
		val style: Int = STYLES.indexOf(config.get("time", STYLES[LOGIN_TIME]))
		time = if (style < 0) LOGIN_TIME else style
		twentyFourHour = config.get("switchTimeFormat", FORMATS[0]) == FORMATS[1]
	}

	/** Each client cycle: the mouse's moves noted for the idle time, and the button's text when it changes. */
	@JvmStatic
	fun tick(client: ClientApi) {
		val x: Int = client.mouseX()
		val y: Int = client.mouseY()
		if (x != lastMouseX || y != lastMouseY) {
			lastMouseX = x
			lastMouseY = y
			lastInputCycle = client.clientCycle()
		}
		if (!enabled || client.gameState() !== GameState.LOGGED_IN) {
			return
		}
		val button: Widget = client.widget(INTERFACE, TEXT) ?: return
		val text: String = text(client)
		if (button.text() != text) {
			button.setText(text)
		}
		changed = true
	}

	/** The button's text for the chosen style now. */
	@JvmStatic
	fun text(client: ClientApi): String {
		val now: Long = clock()
		return when (time) {
			DATE -> date(now)
			LOGIN_TIME -> if (loginMillis < 0L) "00:00:00" else duration((now - loginMillis) / 1000L)
			UTC -> clockTime(util.timeZone("UTC"), now)
			JAGEX -> clockTime(util.timeZone("Europe/London"), now)
			LOCAL_TIME -> clockTime(null, now)
			GAME_TICKS -> ((client.clientCycle() - loginCycle) / CYCLES_PER_TICK).toString()
			else -> idle((client.clientCycle() - lastInputCycle) * CYCLE_MILLIS / 1000L)
		}
	}

	/** RuneLite's DATE_FORMAT, "MMM. dd, yyyy" (in the local zone). */
	@JvmStatic
	fun date(millis: Long): String {
		val c: util.Calendar = util.calendarInstance()
		c.setTime(util.Date(millis))
		return MONTHS[c.get(MONTH)] + ". " + two(c.get(DAY_OF_MONTH)) + ", " + c.get(YEAR)
	}

	/** The time in [zone] (null for the local one): "HH:mm:ss", or "h:mm:ss AM" for 12-hour (RuneLite's MEDIUM style, in English). */
	@JvmStatic
	fun clockTime(zone: util.TimeZone?, millis: Long): String {
		val c: util.Calendar = if (zone == null) util.calendarInstance() else util.calendarInstance(zone)
		c.setTime(util.Date(millis))
		return format(c.get(HOUR_OF_DAY), c.get(MINUTE), c.get(SECOND))
	}

	@JvmStatic
	fun format(hour: Int, minute: Int, second: Int): String {
		if (twentyFourHour) {
			return two(hour) + ":" + two(minute) + ":" + two(second)
		}
		val twelve: Int = if (hour % 12 == 0) 12 else hour % 12
		return twelve.toString() + ":" + two(minute) + ":" + two(second) + (if (hour < 12) " AM" else " PM")
	}

	/** RuneLite's login timer: hours, minutes and seconds. */
	@JvmStatic
	fun duration(seconds: Long): String = two((seconds / 3600L).toInt()) + ":" + two(((seconds / 60L) % 60L).toInt()) + ":" + two((seconds % 60L).toInt())

	/** RuneLite's idle time: minutes and seconds. */
	@JvmStatic
	fun idle(seconds: Long): String = two((seconds / 60L).toInt()) + ":" + two((seconds % 60L).toInt())

	private fun two(value: Int): String = (if (value < 10) "0" else "") + value

	/** RuneLite's shutDown: the button's own words back. */
	@JvmStatic
	fun restore(client: ClientApi) {
		if (!changed) {
			return
		}
		client.widget(INTERFACE, TEXT)?.setText(REPORT_ABUSE)
		changed = false
	}

	/** java.util.Calendar's fields. */
	private const val YEAR: Int = 1
	private const val MONTH: Int = 2
	private const val DAY_OF_MONTH: Int = 5
	private const val HOUR_OF_DAY: Int = 11
	private const val MINUTE: Int = 12
	private const val SECOND: Int = 13

	private const val CYCLE_MILLIS: Long = 20L
}
