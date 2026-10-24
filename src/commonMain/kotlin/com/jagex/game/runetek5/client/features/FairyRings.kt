package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.config.rsinterface.Component
import com.jagex.game.runetek5.config.rsinterface.HookRequest
import com.jagex.game.runetek5.config.rsinterface.Interface
import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.Font
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.Graphics
import com.jagex.game.runetek5.plugin.api.MenuAction
import com.jagex.game.runetek5.plugin.api.MenuTarget
import com.jagex.game.runetek5.plugin.api.OverlayPosition
import com.jagex.game.runetek5.plugin.api.PanelOverlay
import com.jagex.game.runetek5.plugin.api.Plugin
import com.jagex.game.runetek5.plugin.api.PluginApi
import com.jagex.game.runetek5.plugin.api.PluginConfig
import com.jagex.game.runetek5.plugin.api.PluginContext
import com.jagex.game.runetek5.plugin.api.PluginDescriptor
import com.jagex.game.runetek5.plugin.api.Size
import com.jagex.game.runetek5.plugin.api.Viewport
import com.jagex.game.runetek5.plugin.api.Widget
import com.jagex.game.runetek5.plugin.api.events.ClientTick
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.GameStateChanged
import com.jagex.game.runetek5.plugin.api.events.KeyPressed
import com.jagex.game.runetek5.plugin.api.events.KeyTyped
import com.jagex.game.runetek5.plugin.api.events.MenuEntryAdded
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import CS2Interpreter
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: RuneLite's Fairy Rings. With a fairy ring's dial open (734), the teleport
 * button says where the dialled code goes; the travel log beside it (735), which in 634 lists only the codes
 * the player has been to, lists every code with its destination instead, and can be searched: by typing at
 * the top of the game view (by **Search** on a code's right-click, or with the log when **Open search
 * automatically** is on), the codes whose letters or destination hold what is typed stay. RuneLite opens its
 * search by default, in the chat box where the player types; here the prompt is away from the chat box and
 * takes every key while it is up, so it opens by default only when asked for. Clicking a code dials
 * it, as the log's own entries do (the server sets the dials).
 *
 * The codes and destinations are the Void server's (fairy_ring_codes.toml); the dials are varbits 2341-2343,
 * whose values order the letters a d c b, i l k j, p s r q. The log's rows are 735:14-77, one a code in
 * alphabetical order; its own cache script (957, the list's load and varp hook) places the rows that have
 * text and sizes its scrollbar, so the plugin sets the rows' text and runs it again. A built-in plugin
 * (BuiltinPlugins, docs/PLUGINS.md).
 */
object FairyRings : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("fairyrings", "Fairy Rings", "1", PluginApi.VERSION, "Shows where a fairy ring code goes, and lists and searches every code in the travel log.", "Pixel")

	/** The dial, its teleport buttons (the one shown changes) and the travel log, its list and first row. */
	const val DIAL: Int = 734
	const val TELEPORT_BUTTON: Int = 21
	const val TELEPORT_BUTTON_2: Int = 22
	const val TRAVEL_LOG: Int = 735
	const val LIST: Int = 13
	const val FIRST_ROW: Int = 14
	const val ROWS: Int = 64

	/** The dials' varbits, and the letters their values stand for (Void's fairy_ring.varbits.toml). */
	@JvmField val DIAL_VARBITS: IntArray = intArrayOf(2341, 2342, 2343)
	@JvmField val DIAL_LETTERS: Array<String> = arrayOf("adcb", "ilkj", "psrq")

	/** The letters of each dial in the log's (alphabetical) order. */
	@JvmField val LOG_LETTERS: Array<String> = arrayOf("abcd", "ijkl", "pqrs")

	const val TELEPORT_TEXT: String = "Teleport to this location"
	const val INVALID: String = "Invalid location"

	/** RuneLite's 30 characters of search. */
	const val MAX_SEARCH: Int = 30

	private const val KEY_ENTER: Int = 84
	private const val KEY_BACKSPACE: Int = 85
	private const val KEY_ESCAPE: Int = 13
	private const val WHITE: Int = 0xFFFFFFFF.toInt()
	private const val TITLE_COLOUR: Int = 0xFFFF981F.toInt()
	private const val SHADOW: Int = 0xFF000000.toInt()

	/** Void's codes and where they go, upper case. */
	@JvmField
	val DESTINATIONS: Map<String, String> = linkedMapOf(
		"AIQ" to "Asgarnia: Mudskipper Point",
		"AIR" to "Islands: South of Witchaven",
		"AJQ" to "Dungeons: Cave south of Dorgesh-Kaan",
		"AJR" to "Kandarin: Slayer cave south-east of Rellekka",
		"AJS" to "Islands: Penguins near Miscellania",
		"AKQ" to "Kandarin: Piscatoris Hunter area",
		"AKS" to "Feldip Hills: Feldip Hunter area",
		"ALP" to "Kandarin: Feldip Hills",
		"ALQ" to "Morytania: Haunted Woods east of Canifis",
		"ALR" to "Other Realms: Abyssal Area",
		"ALS" to "Kandarin: McGrubor's Wood",
		"BIP" to "Islands: South-west of Mort Myre",
		"BIQ" to "Kharidian Desert: Near Kalphite Hive",
		"BIR" to "Other Realms: Sparse Plane",
		"BIS" to "Kandarin: Ardougne Zoo - Unicorns",
		"BJR" to "Other Realms: Realm of the Fisher King",
		"BKP" to "Feldip Hills: South of Castle Wars",
		"BKQ" to "Other Realms: Enchanted Valley",
		"BKR" to "Morytania: Mort Myre Swamp, south of Canifis",
		"BKS" to "Other Realms: Zanaris",
		"BLP" to "Dungeons: TzHaar area",
		"BLQ" to "Other Realms: Yu'biusk",
		"BLR" to "Kandarin: Legends' Guild",
		"CIP" to "Islands: Miscellania",
		"CIQ" to "Kandarin: North-west of Yanille",
		"CJR" to "Kandarin: Sinclair Mansion (east)",
		"CKP" to "Other Realms: Cosmic entity's plane",
		"CKR" to "Karamja: South of Tai Bwo Wannai Village",
		"CKS" to "Morytania: Canifis",
		"CLP" to "Islands: South of Draynor Village",
		"CLR" to "Islands: Ape Atoll",
		"CLS" to "Islands: Hazelmere's home",
		"DIP" to "Mos Le'Harmless: Isle on the coast of Mos Le'Harmless",
		"DIR" to "Other Realms: Gorak's Plane",
		"DIS" to "Misthalin: Wizards' Tower",
		"DJP" to "Kandarin: Tower of Life",
		"DJR" to "Kandarin: Sinclair Mansion (west)",
		"DKP" to "Karamja: South of Musa Point",
		"DKR" to "Misthalin: Edgeville",
		"DKS" to "Kandarin: Polar Hunter area",
		"DLQ" to "Kharidian Desert: North of Nardah",
		"DLR" to "Islands: Poison Waste south of Isafdar",
		"DLS" to "Dungeons: Myreque hideout under The Hollows",
	)

	@JvmField var enabled: Boolean = true
	@JvmField var autoOpen: Boolean = false

	/** What the log is searched by, and whether it is being typed. */
	@JvmField val search: StringBuilder = StringBuilder()
	@JvmField var typing: Boolean = false

	/** The rows' text as the server sent it (the codes the player has been to), by row, to put back. */
	private val serverText: Array<String?> = arrayOfNulls(ROWS)

	/** The rows' text the plugin set last, by row; a row whose text is not this was set by the server since. */
	private val ourText: Array<String?> = arrayOfNulls(ROWS)

	/** The search the log was last laid out for, null when it shows the server's rows. */
	private var laidOutFor: String? = null

	private var logWasOpen: Boolean = false

	private var context: PluginContext? = null

	private var searchAction: Int = -1

	private val size: Size = Size()

	private val tickListener: EventListener<ClientTick> = EventListener { _ -> context?.client()?.let { tick(it) } }

	private val menuListener: EventListener<MenuEntryAdded> = EventListener { event ->
		val client: ClientApi = context?.client() ?: return@EventListener
		if (enabled && event.target === MenuTarget.WIDGET && event.widgetId ushr 16 == TRAVEL_LOG && rowCode(event.widgetId and 0xFFFF) != null) {
			client.addMenuEntry("Search", searchAction)
		}
	}

	private val keyTypedListener: EventListener<KeyTyped> = EventListener { event ->
		if (typing) {
			event.consume()
			val c: Char = event.keyChar
			if (c >= ' ' && c < '\u007f' && search.length < MAX_SEARCH) {
				search.append(c)
			}
		}
	}

	private val keyPressedListener: EventListener<KeyPressed> = EventListener { event ->
		if (!typing) {
			return@EventListener
		}
		when (event.keyCode) {
			KEY_ENTER -> {
				typing = false
				event.consume()
			}
			KEY_ESCAPE -> {
				typing = false
				search.setLength(0)
				event.consume()
			}
			KEY_BACKSPACE -> {
				if (search.isNotEmpty()) {
					search.setLength(search.length - 1)
				}
				event.consume()
			}
		}
	}

	private val stateListener: EventListener<GameStateChanged> = EventListener { _ ->
		typing = false
		search.setLength(0)
	}

	private val settingListener: EventListener<SettingChanged> = EventListener { event ->
		if (event.pluginId == DESCRIPTOR.id) {
			context?.config()?.let { readSettings(it) }
		}
	}

	private val command: CommandHandler = CommandHandler { _, args ->
		val client: ClientApi = context?.client() ?: return@CommandHandler
		if (args.isEmpty()) {
			client.consolePrint("::fairyring <text>: the codes whose letters or destination hold it (the travel log, when open, is searched by it too).")
			return@CommandHandler
		}
		val text: String = args.joinToString(" ")
		search.setLength(0)
		search.append(text.take(MAX_SEARCH))
		val found: List<String> = matching(text)
		client.consolePrint(if (found.isEmpty()) "No fairy ring code matches \"$text\"." else found.joinToString("; ") { it + ": " + DESTINATIONS[it] })
	}

	/** The search being typed, at the top of the game view. */
	@JvmField
	val prompt: PanelOverlay = object : PanelOverlay("FairyRingsSearch") {
		override fun render(g: Graphics, view: Viewport): Size? {
			if (!typing || !g.fontReady()) {
				return null
			}
			g.font(Font.BOLD)
			val title: String = "Search the travel log (Enter keeps it, Escape clears it)"
			val text: String = search.toString() + "_"
			val width: Int = maxOf(g.textWidth(title), g.textWidth(text)) + 8
			val line: Int = g.fontHeight()
			g.fillRect(0, 0, width, line * 2 + 10, backgroundColour)
			g.text(4, line + 2, title, TITLE_COLOUR, SHADOW)
			g.text(4, line * 2 + 6, text, WHITE, SHADOW)
			return size.set(width, line * 2 + 10)
		}
	}

	override fun start(context: PluginContext) {
		this.context = context
		val s = context.settings()
		s.toggle("enabled", "Fairy Rings", true)
		s.toggle("autoOpen", "Open search automatically", false)
		readSettings(context.config())
		searchAction = context.registerMenuAction(MenuAction { _, _, _, _ -> startSearch() })
		prompt.priority = PanelOverlay.PRIORITY_HIGH
		prompt.position = OverlayPosition.TOP_CENTER
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		context.events().subscribe(Events.CLIENT_TICK, tickListener)
		context.events().subscribe(Events.MENU_ENTRY_ADDED, menuListener)
		context.events().subscribe(Events.KEY_TYPED, keyTypedListener)
		context.events().subscribe(Events.KEY_PRESSED, keyPressedListener)
		context.events().subscribe(Events.GAME_STATE_CHANGED, stateListener)
		context.addOverlay(prompt)
		context.registerCommand("fairyring", command)
	}

	override fun stop() {
		context?.client()?.let { restore(it) }
		context = null
		typing = false
	}

	@JvmStatic
	fun readSettings(config: PluginConfig) {
		enabled = config.getBoolean("enabled", true)
		autoOpen = config.getBoolean("autoOpen", false)
		if (!enabled) {
			typing = false
			context?.client()?.let { restore(it) }
		}
	}

	/** Search, on a code's right-click: types the search afresh. */
	@JvmStatic
	fun startSearch() {
		search.setLength(0)
		typing = true
	}

	/** The code dialled now, upper case ("AKQ"), from the dials' varbits. */
	@JvmStatic
	fun dialledCode(client: ClientApi): String {
		val out: StringBuilder = StringBuilder()
		for (i in 0 until 3) {
			out.append(DIAL_LETTERS[i][client.varbit(DIAL_VARBITS[i]) and 3].uppercaseChar())
		}
		return out.toString()
	}

	/** The code of travel log row component [component] (14 is AIP, 77 DLS), or null for another component. */
	@JvmStatic
	fun rowCode(component: Int): String? {
		val row: Int = component - FIRST_ROW
		if (row < 0 || row >= ROWS) {
			return null
		}
		return ("" + LOG_LETTERS[0][row / 16] + LOG_LETTERS[1][row / 4 % 4] + LOG_LETTERS[2][row % 4]).uppercase()
	}

	/** The travel log row component of code [code] ("AKQ" is 23). */
	@JvmStatic
	fun rowOf(code: String): Int {
		val c: String = code.lowercase()
		return FIRST_ROW + LOG_LETTERS[0].indexOf(c[0]) * 16 + LOG_LETTERS[1].indexOf(c[1]) * 4 + LOG_LETTERS[2].indexOf(c[2])
	}

	/** RuneLite's filter: the codes (in order) whose letters, with or without spaces, or destination hold [text]. */
	@JvmStatic
	fun matching(text: String): List<String> {
		val needle: String = text.trim().lowercase()
		return DESTINATIONS.keys.filter { code ->
			needle.isEmpty() || code.lowercase().contains(needle.replace(" ", "")) || DESTINATIONS[code]!!.lowercase().contains(needle)
		}
	}

	/** A row's text for code [code]: as the server writes the codes the player has been to, a line break then the place. */
	@JvmStatic
	fun rowText(code: String): String = "<br>" + DESTINATIONS[code]

	private fun tick(client: ClientApi) {
		if (!enabled || client.gameState() !== GameState.LOGGED_IN) {
			return
		}
		labelDial(client)
		val log: Widget? = client.widget(TRAVEL_LOG, LIST)
		val open: Boolean = log != null && log.isDrawnThisFrame()
		if (open && !logWasOpen) {
			serverText.fill(null)
			ourText.fill(null)
			laidOutFor = null
			search.setLength(0)
			typing = autoOpen
		} else if (!open && logWasOpen) {
			typing = false
			search.setLength(0)
		}
		logWasOpen = open
		if (open) {
			fillLog(client)
		}
	}

	/** RuneLite's setWidgetTextToDestination: the dial's teleport button says where the code goes. */
	private fun labelDial(client: ClientApi) {
		// only while the dial is up: one button or the other shows
		if (client.widget(DIAL, TELEPORT_BUTTON)?.isDrawnThisFrame() != true && client.widget(DIAL, TELEPORT_BUTTON_2)?.isDrawnThisFrame() != true) {
			return
		}
		val text: String = DESTINATIONS[dialledCode(client)] ?: INVALID
		for (button in intArrayOf(TELEPORT_BUTTON, TELEPORT_BUTTON_2)) {
			val w: Widget = client.widget(DIAL, button) ?: continue
			if (w.text() != text) {
				w.setText(text)
			}
		}
	}

	/** Every code the search keeps, with its destination, in the log: the rows' text set, and the log's script run. */
	private fun fillLog(client: ClientApi) {
		val filter: String = search.toString()
		var changed: Boolean = filter != laidOutFor
		val keep: Set<String> = matching(filter).toHashSet()
		for (row in 0 until ROWS) {
			val w: Widget = client.widget(TRAVEL_LOG, FIRST_ROW + row) ?: continue
			val now: String = w.text() ?: ""
			if (ourText[row] == null || now != ourText[row]) {
				// the server's text (on opening, or after a teleport): kept to put back
				serverText[row] = now
			}
			val code: String = rowCode(FIRST_ROW + row)!!
			val want: String = if (code in keep) rowText(code) else ""
			if (now != want) {
				w.setText(want)
				changed = true
			}
			ourText[row] = want
		}
		if (changed) {
			laidOutFor = filter
			relayout()
		}
	}

	/** The rows as the server left them, laid out again; the dial's buttons as the cache has them. */
	@JvmStatic
	fun restore(client: ClientApi) {
		for (button in intArrayOf(TELEPORT_BUTTON, TELEPORT_BUTTON_2)) {
			client.widget(DIAL, button)?.setText(TELEPORT_TEXT)
		}
		if (laidOutFor == null) {
			return
		}
		for (row in 0 until ROWS) {
			val text: String = serverText[row] ?: continue
			client.widget(TRAVEL_LOG, FIRST_ROW + row)?.setText(text)
		}
		laidOutFor = null
		logWasOpen = false
		relayout()
	}

	/** Runs the list's load hook (cache script 957) again: it places the rows with text and sizes the scrollbar. */
	private fun relayout() {
		val list: Component = Interface.getComponent((TRAVEL_LOG shl 16) or LIST) ?: return
		val hook: HookRequest = HookRequest()
		hook.source = list
		hook.params = list.onLoadScript ?: return
		CS2Interpreter.executeHookInner(hook)
	}
}
