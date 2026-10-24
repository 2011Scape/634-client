package com.jagex.game.runetek5.client.features

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
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.FrameDraw
import com.jagex.game.runetek5.plugin.api.events.GameStateChanged
import com.jagex.game.runetek5.plugin.api.events.KeyPressed
import com.jagex.game.runetek5.plugin.api.events.KeyTyped
import com.jagex.game.runetek5.plugin.api.events.MenuEntryAdded
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: RuneLite's Friend Notes. Right-clicking a name on the friends list
 * (interface 550) offers Add Note or Edit Note and Delete Note; the note is typed in (Enter keeps it,
 * Escape leaves it as it was) and shows as a tooltip with the mouse over the name. A row is a friend's
 * when its text is a name on the friends list, so the plugin needs no component ids. Notes are kept by
 * name (`::friendnote <name> [note]` sets or clears one). A built-in plugin (BuiltinPlugins,
 * docs/PLUGINS.md).
 */
object FriendNotes : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("friendnotes", "Friend Notes", "1", PluginApi.VERSION, "Keep a note on a friend: right-click their name on the friends list, and see it with the mouse over the name.", "Pixel")

	/** The friends list tab. */
	const val FRIENDS: Int = 550

	/** RuneLite's 128 characters. */
	const val MAX_NOTE: Int = 128

	private const val KEY_ENTER: Int = 84

	private const val KEY_BACKSPACE: Int = 85

	private const val KEY_ESCAPE: Int = 13

	private const val WHITE: Int = 0xFFFFFFFF.toInt()

	private const val TITLE_COLOUR: Int = 0xFFFF981F.toInt()

	private const val SHADOW: Int = 0xFF000000.toInt()

	@JvmField var enabled: Boolean = true

	/** The notes by lower-case name, as kept. */
	@JvmField
	val notes: HashMap<String, String> = HashMap()

	/** The friend whose note is being typed, and what is typed so far. */
	@JvmField var editing: String? = null
	@JvmField val typed: StringBuilder = StringBuilder()

	private var context: PluginContext? = null

	private var editAction: Int = -1
	private var deleteAction: Int = -1

	/** The friend the last menu was built on (the menu actions get the row, not the name). */
	private var menuFriend: String? = null

	private val size: Size = Size()

	private val menuListener: EventListener<MenuEntryAdded> = EventListener { event ->
		val client: ClientApi = context?.client() ?: return@EventListener
		if (!enabled || event.target !== MenuTarget.WIDGET || (event.widgetId ushr 16) != FRIENDS) {
			return@EventListener
		}
		val name: String = rowName(client, event.widgetId, event.slot) ?: return@EventListener
		menuFriend = name
		// the last added is the highest
		if (note(name) != null) {
			client.addMenuEntry("Delete Note", deleteAction)
		}
		client.addMenuEntry(if (note(name) == null) "Add Note" else "Edit Note", editAction)
	}

	/** The note of the friend under the mouse, as a tooltip. */
	private val frameListener: EventListener<FrameDraw> = EventListener { _ ->
		val client: ClientApi = context?.client() ?: return@EventListener
		if (!enabled || editing != null || client.gameState() !== GameState.LOGGED_IN) {
			return@EventListener
		}
		tooltipAt(client, client.mouseX(), client.mouseY())?.let { client.addTooltip(it) }
	}

	private val keyTypedListener: EventListener<KeyTyped> = EventListener { event ->
		if (editing != null) {
			event.consume()
			val c: Char = event.keyChar
			if (c >= ' ' && c < '\u007f' && typed.length < MAX_NOTE) {
				typed.append(c)
			}
		}
	}

	private val keyPressedListener: EventListener<KeyPressed> = EventListener { event ->
		val name: String = editing ?: return@EventListener
		when (event.keyCode) {
			KEY_ENTER -> {
				setNote(name, typed.toString())
				editing = null
				event.consume()
			}
			KEY_ESCAPE -> {
				editing = null
				event.consume()
			}
			KEY_BACKSPACE -> {
				if (typed.isNotEmpty()) {
					typed.setLength(typed.length - 1)
				}
				event.consume()
			}
		}
	}

	private val stateListener: EventListener<GameStateChanged> = EventListener { _ -> editing = null }

	private val settingListener: EventListener<SettingChanged> = EventListener { event ->
		if (event.pluginId == DESCRIPTOR.id) {
			context?.config()?.let { readSettings(it) }
		}
	}

	private val command: CommandHandler = CommandHandler { _, args ->
		val context: PluginContext = this.context ?: return@CommandHandler
		if (args.isEmpty()) {
			context.client().consolePrint("::friendnote <name> [note]: sets the friend's note, or clears it with none. Names with spaces: use _ for the spaces.")
			return@CommandHandler
		}
		val name: String = args[0].replace('_', ' ')
		setNote(name, args.drop(1).joinToString(" "))
		context.client().consolePrint(if (args.size > 1) "Note kept for " + name + "." else "Note cleared for " + name + ".")
	}

	/** The note being typed, at the top of the game view. */
	@JvmField
	val prompt: PanelOverlay = object : PanelOverlay("FriendNotesPrompt") {
		override fun render(g: Graphics, view: Viewport): Size? {
			val name: String = editing ?: return null
			if (!g.fontReady()) {
				return null
			}
			g.font(Font.BOLD)
			val title: String = "Note for " + name + " (Enter keeps it, Escape leaves it)"
			val text: String = typed.toString() + "_"
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
		context.settings().toggle("enabled", "Friend Notes", true)
		readSettings(context.config())
		notes.clear()
		for (key in context.config().keys()) {
			if (key.startsWith("note_")) {
				notes[key.substring(5)] = context.config().get(key, "")
			}
		}
		editAction = context.registerMenuAction(MenuAction { _, _, _, _ -> menuFriend?.let { startEditing(it) } })
		deleteAction = context.registerMenuAction(MenuAction { _, _, _, _ -> menuFriend?.let { setNote(it, "") } })
		prompt.priority = PanelOverlay.PRIORITY_HIGH
		prompt.position = OverlayPosition.TOP_CENTER
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		context.events().subscribe(Events.MENU_ENTRY_ADDED, menuListener)
		context.events().subscribe(Events.FRAME_DRAW, frameListener)
		context.events().subscribe(Events.KEY_TYPED, keyTypedListener)
		context.events().subscribe(Events.KEY_PRESSED, keyPressedListener)
		context.events().subscribe(Events.GAME_STATE_CHANGED, stateListener)
		context.addOverlay(prompt)
		context.registerCommand("friendnote", command)
	}

	override fun stop() {
		context = null
		editing = null
	}

	@JvmStatic
	fun readSettings(config: PluginConfig) {
		enabled = config.getBoolean("enabled", true)
	}

	/** A name as the notes are kept by: no-break spaces as spaces, lower case. */
	@JvmStatic
	fun key(name: String): String = name.replace(' ', ' ').trim().lowercase()

	/** A row's text without its colour and image tags: "<col=ffffff>Zezima" is Zezima. */
	@JvmStatic
	fun plain(text: String): String {
		val out: StringBuilder = StringBuilder(text.length)
		var inTag: Boolean = false
		for (c in text) {
			when {
				c == '<' -> inTag = true
				c == '>' -> inTag = false
				!inTag -> out.append(c)
			}
		}
		return out.toString().replace(' ', ' ').trim()
	}

	@JvmStatic
	fun note(name: String): String? = notes[key(name)]

	/** Keeps [note] for [name], or deletes it when blank. */
	@JvmStatic
	fun setNote(name: String, note: String) {
		val k: String = key(name)
		val text: String = note.trim().take(MAX_NOTE)
		if (text.isEmpty()) {
			notes.remove(k)
		} else {
			notes[k] = text
		}
		val config: PluginConfig = context?.config() ?: return
		if (text.isEmpty()) {
			config.remove("note_" + k)
		} else {
			config.set("note_" + k, text)
		}
		config.save()
	}

	private fun startEditing(name: String) {
		editing = name
		typed.setLength(0)
		typed.append(note(name) ?: "")
	}

	/** The friend a friends list row ([widgetId], its dynamic child [slot] or itself) names, or null for a row that is not a friend. */
	@JvmStatic
	fun rowName(client: ClientApi, widgetId: Int, slot: Int): String? {
		val parent: Widget = client.widget(widgetId ushr 16, widgetId and 0xFFFF) ?: return null
		val row: Widget = (if (slot >= 0 && slot < parent.childCount()) parent.child(slot) else parent) ?: return null
		return friendNamed(client, row)
	}

	private fun friendNamed(client: ClientApi, row: Widget): String? {
		val text: String = plain(row.text() ?: row.targetText() ?: return null)
		return if (text.isNotEmpty() && client.isFriend(text)) text else null
	}

	/** The friends list row drawn at ([x], [y]) that names a friend, or null. */
	@JvmStatic
	fun rowAt(client: ClientApi, x: Int, y: Int): Widget? = findRow(client) { row ->
		x >= row.screenX() && y >= row.screenY() && x < row.screenX() + row.width() && y < row.screenY() + row.height()
	}

	/** The friends list row drawn for friend [name], or null. */
	@JvmStatic
	fun rowOf(client: ClientApi, name: String): Widget? = findRow(client) { row -> key(friendNamed(client, row) ?: "") == key(name) }

	private inline fun findRow(client: ClientApi, test: (Widget) -> Boolean): Widget? {
		for (component in 0 until 40) {
			val parent: Widget = client.widget(FRIENDS, component) ?: continue
			if (parent.isHidden() || !parent.isDrawnThisFrame()) {
				continue
			}
			for (i in 0 until parent.childCount()) {
				val row: Widget = parent.child(i) ?: continue
				if (!row.isHidden() && friendNamed(client, row) != null && test(row)) {
					return row
				}
			}
		}
		return null
	}

	/** The note of the friend whose row is at ([x], [y]), or null. */
	@JvmStatic
	fun tooltipAt(client: ClientApi, x: Int, y: Int): String? {
		val row: Widget = rowAt(client, x, y) ?: return null
		val name: String = friendNamed(client, row) ?: return null
		return note(name)?.let { "Note: " + it }
	}
}
