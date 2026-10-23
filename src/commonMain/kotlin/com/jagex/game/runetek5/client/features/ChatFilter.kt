package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.Actor
import com.jagex.game.runetek5.plugin.api.ChatLineView
import com.jagex.game.runetek5.plugin.api.ChatType
import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.Player
import com.jagex.game.runetek5.plugin.api.Plugin
import com.jagex.game.runetek5.plugin.api.PluginApi
import com.jagex.game.runetek5.plugin.api.PluginConfig
import com.jagex.game.runetek5.plugin.api.PluginContext
import com.jagex.game.runetek5.plugin.api.PluginDescriptor
import com.jagex.game.runetek5.plugin.api.SettingsSchema
import com.jagex.game.runetek5.plugin.api.events.ChatMessage
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.GameStateChanged
import com.jagex.game.runetek5.plugin.api.events.OverheadTextChanged
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: RuneLite's Chat Filter. Messages with the words or patterns you list,
 * or from the names you list, have the words starred, are replaced with "Hey, everyone, I just tried to
 * say something very silly!", or are not shown at all (ChatMessage.consume, API 26), in other players'
 * public, private and clan chat lines and their overhead chat, and in the game's lines when asked; friends
 * and clan chat members can be left alone. Repeated lines can be collapsed into the latest with its count
 * (" (3)"; ClientApi.removeChatLine takes the earlier one out), and public chat repeated too often dropped.
 *
 * The filter works on lines as they come, so a list changed later does not touch the lines already shown.
 * 634's Clan Chat is RuneLite's friends chat; OSRS's 2021 clan chats do not exist here. A built-in plugin
 * (BuiltinPlugins, docs/PLUGINS.md).
 */
object ChatFilter : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("chatfilter", "Chat Filter", "1", PluginApi.VERSION, "Censors or hides chat with the words, patterns or names you list, and collapses repeated lines.", "Pixel")

	/** RuneLite's ChatFilterType. */
	@JvmField
	val FILTER_TYPES: Array<String> = arrayOf("Censor words", "Censor message", "Remove message")

	const val CENSOR_WORDS: Int = 0
	const val CENSOR_MESSAGE: Int = 1
	const val REMOVE_MESSAGE: Int = 2

	const val CENSOR_TEXT: String = "Hey, everyone, I just tried to say something very silly!"

	/** The players' chat types: public, its moderators', private from, clan chat and their quick chats. */
	@JvmField
	val PLAYER_TYPES: IntArray = intArrayOf(ChatType.PUBLIC, ChatType.PUBLIC_MOD, ChatType.PRIVATE_FROM, ChatType.PRIVATE_FROM_MOD, ChatType.FRIENDS_CHAT, ChatType.QUICK_PUBLIC, ChatType.QUICK_PRIVATE_FROM, ChatType.QUICK_FRIENDS_CHAT)

	/** The game's chat types: game messages, filtered ones (Void's 109), and the examines. */
	@JvmField
	val GAME_TYPES: IntArray = intArrayOf(ChatType.GAME, ChatType.GAME_FILTERED, FILTERED, ITEM_EXAMINE, NPC_EXAMINE, OBJECT_EXAMINE)

	/** RuneLite's COLLAPSIBLE_MESSAGETYPES: the game's lines and private messages. */
	@JvmField
	val COLLAPSIBLE_TYPES: IntArray = GAME_TYPES + intArrayOf(ChatType.PRIVATE_FROM, ChatType.PRIVATE_TO, ChatType.PRIVATE_FROM_MOD)

	@JvmField var filteredWords: String = ""
	@JvmField var filteredRegex: String = ""
	@JvmField var filteredNames: String = ""
	@JvmField var filterType: Int = CENSOR_WORDS
	@JvmField var filterFriends: Boolean = false
	@JvmField var filterClan: Boolean = false
	@JvmField var filterGameChat: Boolean = false
	@JvmField var collapseGameChat: Boolean = false
	@JvmField var collapsePlayerChat: Boolean = false
	@JvmField var maxRepeatedPublicChats: Int = 0
	@JvmField var stripAccents: Boolean = false

	private var patterns: List<Regex> = emptyList()
	private var namePatterns: List<Regex> = emptyList()

	/** RuneLite's duplicate cache: "name:message" to its count and the line showing it. */
	private class Duplicate(@JvmField var count: Int, @JvmField var lineId: Int)

	private val duplicates: LinkedHashMap<String, Duplicate> = LinkedHashMap()

	private var context: PluginContext? = null

	private val chatListener: EventListener<ChatMessage> = EventListener { event -> context?.client()?.let { chat(it, event) } }

	private val overheadListener: EventListener<OverheadTextChanged> = EventListener { event -> context?.client()?.let { overhead(it, event) } }

	private val stateListener: EventListener<GameStateChanged> = EventListener { event ->
		if (event.state === GameState.LOGIN_SCREEN || event.state === GameState.LOADING) {
			duplicates.clear()
		}
	}

	private val settingListener: EventListener<SettingChanged> = EventListener { event ->
		if (event.pluginId == DESCRIPTOR.id) {
			context?.config()?.let { readSettings(it) }
		}
	}

	private val toggle: CommandHandler = CommandHandler { _, _ ->
		filterGameChat = !filterGameChat
		context?.config()?.setBoolean("filterGameChat", filterGameChat)
		context?.config()?.save()
		context?.client()?.consolePrint(if (filterGameChat) "The chat filter also filters game messages." else "The chat filter leaves game messages alone.")
	}

	override fun start(context: PluginContext) {
		this.context = context
		val s: SettingsSchema = context.settings()
		// RuneLite's ChatFilterConfig, in its order: the Filter lists section first. 634's Clan Chat is RuneLite's
		// friends chat; OSRS's 2021 clan chats have no setting here
		s.text("filteredWords", "Filtered words (comma separated)", "", MAX_TEXT)
		s.text("filteredRegex", "Filtered regex (one per line or ;)", "", MAX_TEXT)
		s.text("filteredNames", "Filtered names (regex, one per line or ;)", "", MAX_TEXT)
		s.choice("filterType", "Filter type", FILTER_TYPES[CENSOR_WORDS], FILTER_TYPES)
		s.toggle("filterFriends", "Filter friends", false)
		s.toggle("filterClan", "Filter clan chat members", false)
		s.toggle("filterGameChat", "Filter game chat", false)
		s.toggle("collapseGameChat", "Collapse game chat", false)
		s.toggle("collapsePlayerChat", "Collapse player chat", false)
		s.number("maxRepeatedPublicChats", "Repeat filter", 0, 0, 100, 1)
		s.toggle("stripAccents", "Strip accents", false)
		readSettings(context.config())
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		context.events().subscribe(Events.CHAT_MESSAGE, chatListener)
		context.events().subscribe(Events.OVERHEAD_TEXT_CHANGED, overheadListener)
		context.events().subscribe(Events.GAME_STATE_CHANGED, stateListener)
		context.registerCommand("chatfilter", toggle)
	}

	override fun stop() {
		context = null
		duplicates.clear()
	}

	@JvmStatic
	fun readSettings(config: PluginConfig) {
		filteredWords = config.get("filteredWords", "")
		filteredRegex = config.get("filteredRegex", "")
		filteredNames = config.get("filteredNames", "")
		val type: Int = FILTER_TYPES.indexOf(config.get("filterType", FILTER_TYPES[CENSOR_WORDS]))
		filterType = if (type < 0) CENSOR_WORDS else type
		filterFriends = config.getBoolean("filterFriends", false)
		filterClan = config.getBoolean("filterClan", false)
		filterGameChat = config.getBoolean("filterGameChat", false)
		collapseGameChat = config.getBoolean("collapseGameChat", false)
		collapsePlayerChat = config.getBoolean("collapsePlayerChat", false)
		maxRepeatedPublicChats = config.getInt("maxRepeatedPublicChats", 0)
		stripAccents = config.getBoolean("stripAccents", false)
		compile()
	}

	/** RuneLite's updateFilteredPatterns: each word quoted, each pattern as written, case-blind. */
	@JvmStatic
	fun compile() {
		val words: List<Regex> = filteredWords.split(',', '\n').map { it.trim() }.filter { it.isNotEmpty() }
			.map { Regex(Regex.escape(if (stripAccents) strip(it) else it), RegexOption.IGNORE_CASE) }
		val regexes: List<Regex> = lines(filteredRegex).mapNotNull { runCatching { Regex(it, RegexOption.IGNORE_CASE) }.getOrNull() }
		patterns = words + regexes
		namePatterns = lines(filteredNames).mapNotNull { runCatching { Regex(it, RegexOption.IGNORE_CASE) }.getOrNull() }
	}

	private fun lines(text: String): List<String> = text.split('\n', ';').map { it.trim() }.filter { it.isNotEmpty() }

	/** RuneLite's canFilterPlayer: never yourself, friends and clan chat members only when asked. */
	@JvmStatic
	fun canFilterPlayer(client: ClientApi, name: String): Boolean {
		val own: String? = client.localPlayer()?.name()?.let { ClientFeatures.plainText(it) }
		return !name.equals(own, ignoreCase = true) &&
			(filterFriends || !client.isFriend(name)) &&
			(filterClan || !client.isInFriendsChat(name))
	}

	/** RuneLite's censorMessage: the filtered names whole, then each pattern; null to remove the line. */
	@JvmStatic
	fun censorMessage(name: String?, message: String): String? {
		if (name != null && namePatterns.any { it.containsMatchIn(name) }) {
			return when (filterType) {
				CENSOR_WORDS -> "*".repeat(ClientFeatures.plainText(message).length)
				CENSOR_MESSAGE -> CENSOR_TEXT
				else -> null
			}
		}
		var text: String = ClientFeatures.plainText(message).replace(' ', ' ')
		val search: String = if (stripAccents) strip(text) else text
		var filtered: Boolean = false
		var searched: String = search
		for (pattern in patterns) {
			if (!pattern.containsMatchIn(searched)) {
				continue
			}
			when (filterType) {
				CENSOR_MESSAGE -> return CENSOR_TEXT
				REMOVE_MESSAGE -> return null
			}
			val out: CharArray = text.toCharArray()
			for (match in pattern.findAll(searched)) {
				for (i in match.range) {
					out[i] = '*'
				}
			}
			text = out.concatToString()
			searched = if (stripAccents) strip(text) else text
			filtered = true
		}
		return if (filtered) text else message
	}

	/** RuneLite's chatFilterCheck, on the line as it comes: censored, removed or collapsed. */
	@JvmStatic
	fun chat(client: ClientApi, event: ChatMessage) {
		val message: String = event.message ?: return
		val name: String = event.name?.let { ClientFeatures.plainText(it) } ?: ""
		var text: String? = message
		if (event.type in PLAYER_TYPES && name.isNotEmpty() && canFilterPlayer(client, name)) {
			text = censorMessage(name, message)
		} else if (event.type in GAME_TYPES && filterGameChat) {
			text = censorMessage(null, message)
		}
		if (text == null) {
			event.consume()
			return
		}
		val public: Boolean = event.type == ChatType.PUBLIC || event.type == ChatType.PUBLIC_MOD
		val collapse: Boolean = if (public) collapsePlayerChat else event.type in COLLAPSIBLE_TYPES && collapseGameChat
		if (collapse) {
			text = collapse(client, name + ":" + text, text, public, event)
		}
		if (text != null && text != message) {
			event.message = text
		}
	}

	/** The repeat: the earlier line taken out and the count on this one; a public line repeated too often dropped. */
	private fun collapse(client: ClientApi, key: String, text: String, public: Boolean, event: ChatMessage): String? {
		val duplicate: Duplicate? = duplicates[key]
		if (duplicate == null) {
			remember(key, Duplicate(1, nextLineId(client)))
			return text
		}
		duplicate.count++
		if (public && maxRepeatedPublicChats > 0 && duplicate.count > maxRepeatedPublicChats) {
			event.consume()
			return null
		}
		client.removeChatLine(duplicate.lineId)
		duplicate.lineId = nextLineId(client)
		return text + " (" + duplicate.count + ")"
	}

	/** The id this line will have: the newest line's plus one, as the client numbers its lines. */
	private fun nextLineId(client: ClientApi): Int {
		val newest: ChatLineView? = client.chatLine(0)
		return (newest?.id() ?: -1) + 1
	}

	private fun remember(key: String, duplicate: Duplicate) {
		duplicates[key] = duplicate
		if (duplicates.size > MAX_ENTRIES) {
			duplicates.remove(duplicates.keys.first())
		}
	}

	/** RuneLite's onOverheadTextChanged: another player's overhead chat censored too, a blank for a removed one. */
	@JvmStatic
	fun overhead(client: ClientApi, event: OverheadTextChanged) {
		val actor: Actor = event.actor
		val player: Player = actor as? Player ?: return
		val name: String = player.name()?.let { ClientFeatures.plainText(it) } ?: return
		val text: String = event.text ?: return
		if (!canFilterPlayer(client, name)) {
			return
		}
		val censored: String = censorMessage(name, text) ?: " "
		if (censored != text) {
			player.setOverheadText(censored)
		}
	}

	/** RuneLite's stripAccents, for the letters chat can carry: the same length, each accented letter its base. */
	@JvmStatic
	fun strip(text: String): String {
		val out: StringBuilder = StringBuilder(text.length)
		for (c in text) {
			val i: Int = ACCENTED.indexOf(c)
			out.append(if (i < 0) c else PLAIN[i])
		}
		return out.toString()
	}

	private const val ACCENTED: String = "ÀÁÂÃÄÅàáâãäåÈÉÊËèéêëÌÍÎÏìíîïÒÓÔÕÖòóôõöÙÚÛÜùúûüÝýÿÑñÇç"
	private const val PLAIN: String = "AAAAAAaaaaaaEEEEeeeeIIIIiiiiOOOOOoooooUUUUuuuuYyyNnCc"

	private const val FILTERED: Int = 109
	private const val ITEM_EXAMINE: Int = 27
	private const val NPC_EXAMINE: Int = 28
	private const val OBJECT_EXAMINE: Int = 29
	private const val MAX_ENTRIES: Int = 100
	private const val MAX_TEXT: Int = 500
}
