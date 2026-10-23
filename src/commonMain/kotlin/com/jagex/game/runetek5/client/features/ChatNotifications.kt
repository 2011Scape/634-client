package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.ChatType
import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.Plugin
import com.jagex.game.runetek5.plugin.api.PluginApi
import com.jagex.game.runetek5.plugin.api.PluginConfig
import com.jagex.game.runetek5.plugin.api.PluginContext
import com.jagex.game.runetek5.plugin.api.PluginDescriptor
import com.jagex.game.runetek5.plugin.api.SettingsSchema
import com.jagex.game.runetek5.plugin.api.events.ChatMessage
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: RuneLite's Chat Notifications. Words and patterns you list, and your own
 * name (underlined too), are highlighted in the chat lines of others and of the game; your name, a
 * highlight, a trade or duel request and a private message can each notify you.
 *
 * Void's types: trade requests 100 ("… wishes to trade with you."), duel challenges 101 ("… wishes to duel
 * with you."), private messages 3 and 7. Void sends no broadcasts (115), so that notification finds nothing;
 * OSRS's 2021 clan chats do not exist here. A built-in plugin (BuiltinPlugins, docs/PLUGINS.md).
 */
object ChatNotifications : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("chatnotification", "Chat Notifications", "1", PluginApi.VERSION, "Highlights your name and chosen words in the chat, and notifies you of them, trades, duels and private messages.", "Pixel")

	/** The plugin manager's switch. */
	@JvmField var enabled: Boolean = true

	const val TRADE_REQUEST: Int = 100
	const val DUEL_REQUEST: Int = 101
	const val BROADCAST: Int = 115

	/** RuneLite's highlight colour (its chat colour config's default): the matches in red. */
	const val HIGHLIGHT: String = "ff0000"

	@JvmField var highlightWordsString: String = ""
	@JvmField var highlightRegexString: String = ""
	@JvmField var highlightOwnName: Boolean = true
	@JvmField var notifyOnOwnName: Boolean = false
	@JvmField var notifyOnHighlight: Boolean = false
	@JvmField var notifyOnTrade: Boolean = false
	@JvmField var notifyOnDuel: Boolean = false
	@JvmField var notifyOnBroadcast: Boolean = false
	@JvmField var notifyOnPM: Boolean = false

	/** For the tests: the notification last sent. */
	@JvmField var lastNotification: String? = null

	private var wordsPattern: Regex? = null
	private var regexPatterns: List<Regex> = emptyList()
	private var namePattern: Regex? = null
	private var patternName: String? = null

	private var context: PluginContext? = null

	private val chatListener: EventListener<ChatMessage> = EventListener { event -> if (enabled) context?.client()?.let { chat(it, event) } }

	private val settingListener: EventListener<SettingChanged> = EventListener { event ->
		if (event.pluginId == DESCRIPTOR.id) {
			context?.config()?.let { readSettings(it) }
		}
	}

	private val toggle: CommandHandler = CommandHandler { _, _ ->
		highlightOwnName = !highlightOwnName
		context?.config()?.setBoolean("highlightOwnName", highlightOwnName)
		context?.config()?.save()
		context?.client()?.consolePrint(if (highlightOwnName) "Your name is highlighted in the chat." else "Your name is not highlighted in the chat.")
	}

	override fun start(context: PluginContext) {
		this.context = context
		val s: SettingsSchema = context.settings()
		// RuneLite's ChatNotificationsConfig, in its order: the Highlight lists section first
		s.toggle("enabled", "Chat Notifications", true)
		s.text("highlightWordsString", "Highlight words (comma separated)", "", MAX_TEXT)
		s.text("highlightRegexString", "Highlight regex (one per line or ;)", "", MAX_TEXT)
		s.toggle("highlightOwnName", "Highlight own name", true)
		s.toggle("notifyOnOwnName", "Notify on own name", false)
		s.toggle("notifyOnHighlight", "Notify on highlight", false)
		s.toggle("notifyOnTrade", "Notify on trade", false)
		s.toggle("notifyOnDuel", "Notify on duel", false)
		s.toggle("notifyOnBroadcast", "Notify on broadcast", false)
		s.toggle("notifyOnPM", "Notify on private message", false)
		readSettings(context.config())
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		context.events().subscribe(Events.CHAT_MESSAGE, chatListener)
		context.registerCommand("chatnotifications", toggle)
	}

	override fun stop() {
		context = null
	}

	@JvmStatic
	fun readSettings(config: PluginConfig) {
		enabled = config.getBoolean("enabled", true)
		highlightWordsString = config.get("highlightWordsString", "")
		highlightRegexString = config.get("highlightRegexString", "")
		highlightOwnName = config.getBoolean("highlightOwnName", true)
		notifyOnOwnName = config.getBoolean("notifyOnOwnName", false)
		notifyOnHighlight = config.getBoolean("notifyOnHighlight", false)
		notifyOnTrade = config.getBoolean("notifyOnTrade", false)
		notifyOnDuel = config.getBoolean("notifyOnDuel", false)
		notifyOnBroadcast = config.getBoolean("notifyOnBroadcast", false)
		notifyOnPM = config.getBoolean("notifyOnPM", false)
		compile()
	}

	/** RuneLite's onConfigChanged: the word list as one case-blind pattern on word bounds, the patterns one by one. */
	@JvmStatic
	fun compile() {
		val words: List<String> = highlightWordsString.split(',').map { it.trim() }.filter { it.isNotEmpty() }
		wordsPattern = if (words.isEmpty()) null else Regex("(?:\\b|(?<=\\s)|\\A)(?:" + words.joinToString("|") { Regex.escape(it) } + ")(?:\\b|(?=\\s)|\\z)", RegexOption.IGNORE_CASE)
		regexPatterns = highlightRegexString.split('\n', ';').map { it.trim() }.filter { it.isNotEmpty() }.mapNotNull { runCatching { Regex(it, RegexOption.IGNORE_CASE) }.getOrNull() }
	}

	/** RuneLite's username pattern: the name as a word, its spaces either kind. */
	private fun namePattern(name: String): Regex {
		if (name != patternName || namePattern == null) {
			patternName = name
			val words: List<String> = name.split(' ', ' ').filter { it.isNotEmpty() }
			namePattern = Regex("\\b" + words.joinToString("[  ]") { Regex.escape(it) } + "\\b", RegexOption.IGNORE_CASE)
		}
		return namePattern!!
	}

	/** RuneLite's onChatMessage: the notifications by type, then the highlights. */
	@JvmStatic
	fun chat(client: ClientApi, event: ChatMessage) {
		val message: String = event.message ?: return
		val sender: String? = event.name?.let { ClientFeatures.plainText(it) }?.takeIf { it.isNotEmpty() }
		when (event.type) {
			TRADE_REQUEST -> if (notifyOnTrade && message.contains("wishes to trade with you.")) notify(client, sender, message)
			DUEL_REQUEST -> if (notifyOnDuel && message.contains("wishes to duel with you.")) notify(client, sender, message)
			BROADCAST -> if (notifyOnBroadcast) notify(client, null, message)
			ChatType.PRIVATE_FROM, ChatType.PRIVATE_FROM_MOD, ChatType.QUICK_PRIVATE_FROM -> if (notifyOnPM) notify(client, sender, message)
		}
		if (isOwn(client, event, sender)) {
			return
		}
		var text: String = message
		var highlighted: Boolean = false
		val own: String? = client.localPlayer()?.name()?.let { ClientFeatures.plainText(it) }
		if (highlightOwnName && own != null && own.isNotEmpty()) {
			val pattern: Regex = namePattern(own)
			if (pattern.containsMatchIn(text)) {
				text = pattern.replace(text) { "<col=" + HIGHLIGHT + "><u>" + it.value + "</u></col>" }
				if (notifyOnOwnName) {
					notify(client, sender, message)
				}
			}
		}
		val words: Regex? = wordsPattern
		if (words != null && words.containsMatchIn(text)) {
			text = words.replace(text) { "<col=" + HIGHLIGHT + ">" + it.value + "</col>" }
			highlighted = true
		}
		for (pattern in regexPatterns) {
			if (pattern.containsMatchIn(text)) {
				text = pattern.replace(text) { "<col=" + HIGHLIGHT + ">" + it.value + "</col>" }
				highlighted = true
			}
		}
		if (highlighted && notifyOnHighlight) {
			notify(client, sender, message)
		}
		if (text != message) {
			event.message = text
		}
	}

	/** RuneLite skips your own public, chat channel and sent private messages. */
	private fun isOwn(client: ClientApi, event: ChatMessage, sender: String?): Boolean {
		if (event.type == ChatType.PRIVATE_TO || event.type == ChatType.QUICK_PRIVATE_TO) {
			return true
		}
		val own: String = client.localPlayer()?.name()?.let { ClientFeatures.plainText(it) } ?: return false
		return sender != null && sender.equals(own, ignoreCase = true)
	}

	/** RuneLite's sendNotification: "name: message" without its tags. */
	private fun notify(client: ClientApi, sender: String?, message: String) {
		val text: String = ClientFeatures.plainText(message)
		val full: String = if (sender == null) text else sender + ": " + text
		lastNotification = full
		client.notify(full)
	}

	private const val MAX_TEXT: Int = 500
}
