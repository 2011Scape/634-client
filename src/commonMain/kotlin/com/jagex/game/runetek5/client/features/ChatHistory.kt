package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.ChatLineView
import com.jagex.game.runetek5.plugin.api.ChatType
import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.MenuAction
import com.jagex.game.runetek5.plugin.api.MenuTarget
import com.jagex.game.runetek5.plugin.api.Plugin
import com.jagex.game.runetek5.plugin.api.PluginApi
import com.jagex.game.runetek5.plugin.api.PluginConfig
import com.jagex.game.runetek5.plugin.api.PluginContext
import com.jagex.game.runetek5.plugin.api.PluginDescriptor
import com.jagex.game.runetek5.plugin.api.SettingsSchema
import com.jagex.game.runetek5.plugin.api.Widget
import com.jagex.game.runetek5.plugin.api.events.ChatMessage
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.GameStateChanged
import com.jagex.game.runetek5.plugin.api.events.MenuEntryAdded
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: RuneLite's Chat History. Your public, private and clan chat lines are
 * kept when you log out and put back when you log in again (the client empties the chat box at each login;
 * ClientApi.addChatLine, API 27), a chat line's right-click menu has "Copy to clipboard", and each chat
 * tab's has "Clear history", which takes that tab's lines out of the chat box.
 *
 * 634 sends a private message to the name its prompt's script was given, kept in no variable a plugin
 * could change, so RuneLite's PM target cycling (Tab through your recent conversations) cannot be done
 * here. A built-in plugin (BuiltinPlugins, docs/PLUGINS.md).
 */
object ChatHistory : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("chathistory", "Chat History", "1", PluginApi.VERSION, "Keeps your chat across logins, and adds Copy to clipboard to chat lines and Clear history to the chat tabs.", "Pixel")

	/** The plugin manager's switch. */
	@JvmField var enabled: Boolean = true

	/** The chat box (137) and its typing line, which is no chat line. */
	const val CHATBOX: Int = 137
	const val CHATBOX_INPUT: Int = 55

	/** The chat tabs' buttons in the game frame (751): All, Game, Public, Private, Clan, Trade, Assist. */
	const val CHAT_TABS: Int = 751

	@JvmField
	val TAB_NAMES: Array<String> = arrayOf("All", "Game", "Public", "Private", "Clan", "Trade", "Assist")

	/** RuneLite's kept types: public chat and its moderators', private from and to, clan chat, and their quick chats. */
	@JvmField
	val KEPT_TYPES: IntArray = intArrayOf(
		ChatType.PUBLIC_MOD, ChatType.PUBLIC, ChatType.PRIVATE_FROM, ChatType.PRIVATE_TO, ChatType.PRIVATE_FROM_MOD, ChatType.FRIENDS_CHAT,
		ChatType.QUICK_PUBLIC, ChatType.QUICK_PRIVATE_FROM, ChatType.QUICK_PRIVATE_TO, ChatType.QUICK_FRIENDS_CHAT,
	)

	/** The types each tab shows, in TAB_NAMES' order (Void's: game 0, filtered 109, examines 27-29, clan notices 11, trade 100 and 103, duel 101, assist 102); All is every type. */
	@JvmField
	val TAB_TYPES: Array<IntArray?> = arrayOf(
		null,
		intArrayOf(ChatType.GAME, ChatType.GAME_FILTERED, 109, 27, 28, 29, ChatType.FRIEND_STATUS),
		intArrayOf(ChatType.PUBLIC_MOD, ChatType.PUBLIC, ChatType.QUICK_PUBLIC),
		intArrayOf(ChatType.PRIVATE_FROM, ChatType.PRIVATE_TO, ChatType.PRIVATE_FROM_MOD, ChatType.QUICK_PRIVATE_FROM, ChatType.QUICK_PRIVATE_TO),
		intArrayOf(ChatType.FRIENDS_CHAT, ChatType.QUICK_FRIENDS_CHAT, 11),
		intArrayOf(100, 101, 103),
		intArrayOf(102),
	)

	@JvmField var retainChatHistory: Boolean = true
	@JvmField var copyToClipboard: Boolean = true
	@JvmField var clearHistory: Boolean = true

	/** A kept line, oldest first in [history]. */
	class Kept(@JvmField val type: Int, @JvmField val name: String?, @JvmField val message: String, @JvmField val clan: String?)

	@JvmField val history: ArrayDeque<Kept> = ArrayDeque()

	/** A login happened: the kept lines go back at its first chat line. */
	@JvmField var restorePending: Boolean = false

	private var context: PluginContext? = null

	private var copyAction: Int = -1
	private var clearAction: Int = -1

	private val chatListener: EventListener<ChatMessage> = EventListener { event -> if (enabled) context?.client()?.let { chat(it, event) } }

	private val stateListener: EventListener<GameStateChanged> = EventListener { event ->
		if (event.state === GameState.LOGGED_IN && event.previous !== GameState.LOGGED_IN) {
			restorePending = enabled && retainChatHistory && history.isNotEmpty()
		}
	}

	private val menuListener: EventListener<MenuEntryAdded> = EventListener { event -> if (enabled) context?.client()?.let { menuEntryAdded(it, event) } }

	private val settingListener: EventListener<SettingChanged> = EventListener { event ->
		if (event.pluginId == DESCRIPTOR.id) {
			context?.config()?.let { readSettings(it) }
			if (!enabled) {
				// RuneLite's shutDown: what was kept goes
				history.clear()
				restorePending = false
			}
		}
	}

	private val copy: MenuAction = MenuAction { _, slot, widgetId, _ -> context?.client()?.let { copyLine(it, widgetId, slot) } }

	private val clear: MenuAction = MenuAction { _, _, widgetId, _ -> context?.client()?.let { clearTab(it, widgetId and 0xFFFF) } }

	private val toggle: CommandHandler = CommandHandler { _, _ ->
		retainChatHistory = !retainChatHistory
		context?.config()?.setBoolean("retainChatHistory", retainChatHistory)
		context?.config()?.save()
		context?.client()?.consolePrint(if (retainChatHistory) "Chat history is kept across logins." else "Chat history is not kept.")
	}

	override fun start(context: PluginContext) {
		this.context = context
		val s: SettingsSchema = context.settings()
		// RuneLite's ChatHistoryConfig, in its order, but PM target cycling (no target to change on 634)
		s.toggle("enabled", "Chat History", true)
		s.toggle("retainChatHistory", "Retain chat history", true)
		s.toggle("copyToClipboard", "Copy to clipboard", true)
		s.toggle("clearHistory", "Clear history option for all tabs", true)
		readSettings(context.config())
		copyAction = context.registerMenuAction(copy)
		clearAction = context.registerMenuAction(clear)
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		context.events().subscribe(Events.CHAT_MESSAGE, chatListener)
		context.events().subscribe(Events.GAME_STATE_CHANGED, stateListener)
		context.events().subscribe(Events.MENU_ENTRY_ADDED, menuListener)
		context.registerCommand("chathistory", toggle)
	}

	override fun stop() {
		context = null
		history.clear()
		restorePending = false
	}

	@JvmStatic
	fun readSettings(config: PluginConfig) {
		enabled = config.getBoolean("enabled", true)
		retainChatHistory = config.getBoolean("retainChatHistory", true)
		copyToClipboard = config.getBoolean("copyToClipboard", true)
		clearHistory = config.getBoolean("clearHistory", true)
	}

	/** RuneLite's onChatMessage: the kept types remembered (the last 100); the first line after a login puts them back first. */
	@JvmStatic
	fun chat(client: ClientApi, event: ChatMessage) {
		if (restorePending) {
			restorePending = false
			for (kept in history) {
				client.addChatLine(kept.type, kept.name, kept.message, kept.clan)
			}
		}
		val message: String = event.message ?: return
		if (!retainChatHistory || event.isConsumed() || event.type !in KEPT_TYPES) {
			return
		}
		history.addLast(Kept(event.type, event.name, message, event.clan))
		while (history.size > MAX_KEPT) {
			history.removeFirst()
		}
	}

	/** RuneLite's onMenuEntryAdded: "Copy to clipboard" on a chat line, "Clear history" on a chat tab. */
	@JvmStatic
	fun menuEntryAdded(client: ClientApi, event: MenuEntryAdded) {
		if (event.target !== MenuTarget.WIDGET) {
			return
		}
		val group: Int = event.widgetId ushr 16
		val component: Int = event.widgetId and 0xFFFF
		if (group == CHATBOX && component != CHATBOX_INPUT && copyToClipboard && copyAction >= 0 && lineText(client, event.widgetId, event.slot) != null) {
			client.addMenuEntry("Copy to clipboard", copyAction)
		} else if (group == CHAT_TABS && clearHistory && clearAction >= 0 && tab(component) >= 0) {
			client.addMenuEntry("<col=ffff00>" + TAB_NAMES[tab(component)] + ":</col> Clear history", clearAction)
		}
	}

	/** The chat tab of game frame component [component], -1 for none: the tabs' layers 751:2 to 751:8, or their pictures and labels (751:12 to 751:31, three each from Assist up, All two). */
	@JvmStatic
	fun tab(component: Int): Int {
		if (component in FIRST_TAB until FIRST_TAB + TAB_NAMES.size) {
			return component - FIRST_TAB
		}
		if (component in FIRST_PART..LAST_PART) {
			// Assist 12-14, Trade 15-17, Clan 18-20, Private 21-23, Public 24-26, Game 27-29, All 30-31
			return (TAB_NAMES.size - 1 - (component - FIRST_PART) / PARTS_PER_TAB).coerceAtLeast(0)
		}
		return -1
	}

	/** A chat line's text without its tags, null for none. */
	private fun lineText(client: ClientApi, widgetId: Int, slot: Int): String? {
		val widget: Widget = client.widgetById(widgetId) ?: return null
		val line: Widget = if (slot >= 0) widget.child(slot) ?: return null else widget
		val text: String = ClientFeatures.plainText(line.text() ?: return null).trim()
		return text.ifEmpty { null }
	}

	private fun copyLine(client: ClientApi, widgetId: Int, slot: Int) {
		val text: String = lineText(client, widgetId, slot) ?: return
		client.setClipboard(text)
	}

	/** RuneLite's clearMessageQueue: every line of the tab's types out of the chat box. */
	@JvmStatic
	fun clearTab(client: ClientApi, component: Int) {
		val tab: Int = tab(component)
		if (tab < 0) {
			return
		}
		val types: IntArray? = TAB_TYPES[tab]
		val ids: ArrayList<Int> = ArrayList()
		for (i in 0 until client.chatLineCount()) {
			val line: ChatLineView = client.chatLine(i) ?: continue
			if (types == null || line.type() in types) {
				ids.add(line.id())
			}
		}
		for (id in ids) {
			client.removeChatLine(id)
		}
		if (types == null || types.any { it in KEPT_TYPES }) {
			history.removeAll { types == null || it.type in types }
		}
	}

	private const val FIRST_TAB: Int = 2
	private const val FIRST_PART: Int = 12
	private const val LAST_PART: Int = 31
	private const val PARTS_PER_TAB: Int = 3
	private const val MAX_KEPT: Int = 100
}
