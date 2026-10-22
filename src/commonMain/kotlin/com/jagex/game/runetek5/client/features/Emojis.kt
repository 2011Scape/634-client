package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.ChatType
import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.Image
import com.jagex.game.runetek5.plugin.api.Player
import com.jagex.game.runetek5.plugin.api.Plugin
import com.jagex.game.runetek5.plugin.api.PluginApi
import com.jagex.game.runetek5.plugin.api.PluginConfig
import com.jagex.game.runetek5.plugin.api.PluginContext
import com.jagex.game.runetek5.plugin.api.PluginDescriptor
import com.jagex.game.runetek5.plugin.api.events.ChatMessage
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.OverheadTextChanged
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: RuneLite's Emojis. A word of a chat line (public, private or friends
 * chat) or of a player's overhead text that is one of RuneLite's 67 triggers (:) <3 ^_^ ...) is shown as
 * its emoji, a picture in the chat icons (ClientApi.addChatIcon). The pictures are Twemoji's (CC-BY
 * 4.0), 13 pixels square, in the client's resources as raw ARGB (/emojis/emojis.rgba: count and size,
 * then each picture row by row in [TRIGGERS] order), which the browser build does not carry, so there the
 * plugin changes nothing. RuneLite's :name: shortcodes need its downloaded set of every emoji and are not
 * here. The client escapes < and > in players' text as <lt> and <gt>, as OSRS does, but not @, so
 * RuneLite's <at> triggers are written with @. A built-in plugin (BuiltinPlugins, docs/PLUGINS.md).
 */
object Emojis : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("emojis", "Emojis", "1", PluginApi.VERSION, "Shows emoticons such as :) and ^_^ in chat and over players' heads as emoji pictures.", "Pixel")

	/** RuneLite's Emoji triggers, in its order (the pictures' order in the resource). */
	@JvmField
	val TRIGGERS: Array<String> = arrayOf(
		":)", // slight_smile
		"=')", // joy
		"3:)", // cowboy
		"^_^", // blush
		":D", // smile
		"=D", // grinning
		";)", // wink
		"X-P", // stuck_out_tongue_closed_eyes
		":P", // stuck_out_tongue
		"=P~", // yum
		"<gt>:D<lt>", // hugging
		":<gt>", // triumph
		":-?", // thinking
		":/", // confused
		"=|", // neutral_face
		":|", // expressionless
		":-|", // unamused
		":(", // slight_frown
		"=(", // frowning2
		":'(", // cry
		":_(", // sob
		":$", // flushed
		":-#", // zipper_mouth
		"<gt>_<lt>", // persevere
		"8-)", // sunglasses
		"O:)", // innocent
		"<gt>:)", // smiling_imp
		"<gt>:(", // rage
		":-O", // hushed
		":O", // open_mouth
		":-@", // scream
		"X_X", // see_no_evil
		"\\:D/", // dancer
		"(Ok)", // ok_hand
		"(Y)", // thumbsup
		"(N)", // thumbsdown
		"<lt>3", // hearts
		"<lt>/3", // broken_heart
		"Zzz", // zzz
		"<lt><gt><lt>", // fish
		":3", // cat
		"=3", // dog
		"V(;,;)V", // crab
		"--E", // fork_and_knife
		"--(o)", // cooking
		"@@@", // party_popper
		"O.O", // eyes
		";;", // sweat
		"~@~", // pile_of_poo
		"(/\\)", // fire
		"(@.@)", // alien
		"8=D", // eggplant
		"(^_^)/", // wave
		"(*.*)", // heart_eyes
		"M-)", // facepalm
		"V_V", // pensive
		"<lt>D~", // acorn
		":G", // gorilla
		"(n_n)", // pleading
		"Xd", // xd
		"--o", // spoon
		"Dx", // weary_face
		"<gt>==<gt>", // rocketship
		":O)", // clown
		"3:O", // cow
		"(=)", // handshake
		"=:3", // rabbit
	)

	const val RESOURCE: String = "/emojis/emojis.rgba"

	@JvmField var enabled: Boolean = false

	/** Each trigger's `<img=N>` number, null until the pictures are added (or when they cannot be). */
	private var icons: IntArray? = null

	private var triedLoading: Boolean = false

	private var context: PluginContext? = null

	private val chatListener: EventListener<ChatMessage> = EventListener { event -> context?.client()?.let { chat(it, event) } }

	private val overheadListener: EventListener<OverheadTextChanged> = EventListener { event -> context?.client()?.let { overhead(it, event) } }

	private val settingListener: EventListener<SettingChanged> = EventListener { event ->
		if (event.pluginId == DESCRIPTOR.id) {
			context?.config()?.let { readSettings(it) }
		}
	}

	private val toggle: CommandHandler = CommandHandler { _, _ ->
		enabled = !enabled
		context?.config()?.setBoolean("enabled", enabled)
		context?.config()?.save()
		context?.client()?.consolePrint(if (enabled) "Emojis enabled." else "Emojis disabled.")
	}

	override fun start(context: PluginContext) {
		this.context = context
		context.settings().toggle("enabled", "Emojis", false)
		readSettings(context.config())
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		context.events().subscribe(Events.CHAT_MESSAGE, chatListener)
		context.events().subscribe(Events.OVERHEAD_TEXT_CHANGED, overheadListener)
		context.registerCommand("emojis", toggle)
	}

	override fun stop() {
		context = null
	}

	@JvmStatic
	fun readSettings(config: PluginConfig) {
		enabled = config.getBoolean("enabled", false)
	}

	/** RuneLite's onChatMessage: players' lines only. */
	@JvmStatic
	fun chat(client: ClientApi, event: ChatMessage) {
		if (!enabled || !isPlayerChat(event.type)) {
			return
		}
		val message: String = event.message ?: return
		val updated: String = updateMessage(message, iconNumbers(client) ?: return) ?: return
		event.message = updated
	}

	/** RuneLite's onOverheadTextChanged: a player's line only. */
	@JvmStatic
	fun overhead(client: ClientApi, event: OverheadTextChanged) {
		if (!enabled || event.actor !is Player) {
			return
		}
		val updated: String = updateMessage(event.text, iconNumbers(client) ?: return) ?: return
		event.actor.setOverheadText(updated)
	}

	/** Public chat (a moderator's too), private chat both ways, and the friends chat. */
	@JvmStatic
	fun isPlayerChat(type: Int): Boolean {
		return when (type) {
			ChatType.PUBLIC, ChatType.PUBLIC_MOD, ChatType.PRIVATE_FROM, ChatType.PRIVATE_TO, ChatType.PRIVATE_FROM_MOD, ChatType.FRIENDS_CHAT -> true
			else -> false
		}
	}

	/**
	 * RuneLite's updateMessage: each whitespace-separated word, its tags taken out (but <lt> and <gt>
	 * kept), that is a trigger has every place it is in the line made its `<img=N>`; null for no change.
	 */
	@JvmStatic
	fun updateMessage(message: String, iconNumbers: IntArray): String? {
		var edited: String = message
		var startWs: Int = -1
		for (i in message.indices) {
			val c: Char = message[i]
			if (!c.isWhitespace() && c != NBSP && i + 1 != message.length) {
				continue
			}
			val endWs: Int = if (i + 1 == message.length) message.length else i
			val shortname: String = removeFormattingTags(message.substring(startWs + 1, endWs))
			startWs = i
			val emoji: Int = TRIGGERS.indexOf(shortname)
			if (emoji >= 0 && shortname.isNotEmpty()) {
				edited = edited.replace(shortname, "<img=" + iconNumbers[emoji] + ">")
			}
		}
		return if (edited == message) null else edited
	}

	/** RuneLite's Text.removeFormattingTags: every tag out but the escapes <lt>, <gt>, <at> and <nbh>. */
	@JvmStatic
	fun removeFormattingTags(text: String): String {
		return TAG.replace(text) { match -> if (match.value in KEPT_TAGS) match.value else "" }
	}

	/** The pictures added as chat icons on first need: their numbers, or null when they cannot be (yet). */
	private fun iconNumbers(client: ClientApi): IntArray? {
		val known: IntArray? = icons
		if (known != null || triedLoading) {
			return known
		}
		val images: Array<Image>? = loadImages(client)
		if (images == null) {
			triedLoading = true
			return null
		}
		val numbers = IntArray(images.size)
		for (i in images.indices) {
			numbers[i] = client.addChatIcon(images[i])
			if (numbers[i] < 0) {
				// the game's own icons are not loaded yet: try again with the next line (a later add is fine; earlier ones stay)
				return null
			}
		}
		icons = numbers
		return numbers
	}

	/** The pictures from the resource, null when it is missing or broken. */
	private fun loadImages(client: ClientApi): Array<Image>? {
		val bytes: ByteArray = lang.readResource(RESOURCE) ?: return null
		if (bytes.size < 8) {
			return null
		}
		val count: Int = readInt(bytes, 0)
		val size: Int = readInt(bytes, 4)
		if (count != TRIGGERS.size || size <= 0 || bytes.size < 8 + count * size * size * 4) {
			return null
		}
		return Array(count) { e ->
			val argb = IntArray(size * size) { p -> readInt(bytes, 8 + (e * size * size + p) * 4) }
			client.createImage(size, size, argb) ?: return null
		}
	}

	private fun readInt(bytes: ByteArray, at: Int): Int {
		return ((bytes[at].toInt() and 0xFF) shl 24) or ((bytes[at + 1].toInt() and 0xFF) shl 16) or ((bytes[at + 2].toInt() and 0xFF) shl 8) or (bytes[at + 3].toInt() and 0xFF)
	}

	private const val NBSP: Char = '\u00A0'

	private val TAG: Regex = Regex("<[^>]*>")

	private val KEPT_TAGS: Array<String> = arrayOf("<lt>", "<gt>", "<at>", "<nbh>")
}
