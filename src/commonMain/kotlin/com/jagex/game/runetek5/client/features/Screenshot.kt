package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.ChatType
import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.Image
import com.jagex.game.runetek5.plugin.api.Player
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
import com.jagex.game.runetek5.plugin.api.events.FrameDraw
import com.jagex.game.runetek5.plugin.api.events.GameTick
import com.jagex.game.runetek5.plugin.api.events.InterfaceChanged
import com.jagex.game.runetek5.plugin.api.events.KeyPressed
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: RuneLite's Screenshot. A hotkey (or ::screenshot) takes a screenshot of the
 * game, and so do your level-ups, quest completions and Barrows chests, your deaths, the players you kill and a
 * kick from a clan chat, each by its setting. They are saved under the player's name and the kind (Levels,
 * Quests, Barrows Rewards, Deaths, PvP Kills, Clan Chat Kicks) as "Attack(99) 2011-06-01_12-00-00.png": on the
 * desktop in Pictures/634-client, in a browser as a download (ClientApi.captureFrame and saveImage, API 33). It
 * can say where it went, put the picture on the clipboard, and show the date on the report button in it.
 *
 * Void's lines: "You've just advanced a Attack level! You have reached level 99.", "Oh dear, you are dead!",
 * "The cave begins to collapse!" after a Barrows chest, "You have been kicked from the channel."; its quest
 * complete scroll (277) says "You have completed X!". Void tells a killer nothing, so a kill is the player you
 * are fighting starting to die. The client's window frame, image hosts and OSRS's pets, clues, collection log,
 * combat achievements, raids and the like are not here. A built-in plugin (BuiltinPlugins, docs/PLUGINS.md).
 */
object Screenshot : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("screenshot", "Screenshot", "1", PluginApi.VERSION, "Takes screenshots on a hotkey and of level-ups, quests, Barrows chests, deaths, kills and clan chat kicks.", "Pixel")

	@JvmField
	val LEVEL_UP: Regex = Regex("You've just advanced an? ([A-Za-z]+) level! You have reached level (\\d+)\\.")

	const val DEATH: String = "Oh dear, you are dead!"
	const val BARROWS_CHEST: String = "The cave begins to collapse!"
	const val KICKED: String = "You have been kicked from the channel."

	/** Void's quest complete scroll and its quest name ("You have completed X!"). */
	const val QUEST_COMPLETE: Int = 277
	const val QUEST_NAME: Int = 4

	/** Void's ChatType.ClanChat, where its kicks come. */
	const val CLAN_CHAT: Int = 11

	@JvmField var displayDate: Boolean = true
	@JvmField var notifyWhenTaken: Boolean = true
	@JvmField var copyToClipboard: Boolean = false
	@JvmField var hotkey: Int = 0
	@JvmField var screenshotRewards: Boolean = true
	@JvmField var screenshotLevels: Boolean = true
	@JvmField var screenshotPlayerDeath: Boolean = false
	@JvmField var screenshotPlayerKills: Boolean = false
	@JvmField var screenshotKicks: Boolean = false

	/** A screenshot asked for: its kind's folder and name (empty for the hotkey's), and the frames since. */
	@JvmField var pendingFolder: String? = null
	@JvmField var pendingName: String = ""
	@JvmField var pendingFrames: Int = 0

	/** Where the last one went, for the smoke test. */
	@JvmField var lastSaved: String? = null

	@JvmField var clock: () -> Long = { lang.currentTimeMillis() }

	/** The report button's own text while the date is on it. */
	private var reportText: String? = null

	/** The last player killed (their index), so one death is one screenshot. */
	private var killed: Int = -1

	private var context: PluginContext? = null

	private val chatListener: EventListener<ChatMessage> = EventListener { event -> chat(event) }

	private val interfaceListener: EventListener<InterfaceChanged> = EventListener { event ->
		if (event.interfaceId == QUEST_COMPLETE && screenshotRewards) {
			request("Quests", "Quest")
		}
	}

	private val tickListener: EventListener<GameTick> = EventListener { _ -> context?.client()?.let { tick(it) } }

	private val frameListener: EventListener<FrameDraw> = EventListener { _ -> context?.client()?.let { frame(it) } }

	private val keyListener: EventListener<KeyPressed> = EventListener { event ->
		if (hotkey != 0 && event.keyCode == hotkey) {
			request("", "")
		}
	}

	private val settingListener: EventListener<SettingChanged> = EventListener { event ->
		if (event.pluginId == DESCRIPTOR.id) {
			context?.config()?.let { readSettings(it) }
		}
	}

	private val command: CommandHandler = CommandHandler { _, _ -> request("", "") }

	override fun start(context: PluginContext) {
		this.context = context
		val s: SettingsSchema = context.settings()
		// RuneLite's ScreenshotConfig, in its order, but the window frame, image hosts and OSRS-only events
		s.toggle("displayDate", "Display date", true)
		s.toggle("notifyWhenTaken", "Notify when taken", true)
		s.toggle("copyToClipboard", "Copy to clipboard", false)
		s.keybind("hotkey", "Screenshot hotkey", 0)
		s.toggle("rewards", "Screenshot rewards", true)
		s.toggle("levels", "Screenshot levels", true)
		s.toggle("playerDeath", "Screenshot deaths", false)
		s.toggle("pvpKills", "Screenshot PvP kills", false)
		s.toggle("kicks", "Screenshot clan chat kicks", false)
		readSettings(context.config())
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		context.events().subscribe(Events.CHAT_MESSAGE, chatListener)
		context.events().subscribe(Events.INTERFACE_OPENED, interfaceListener)
		context.events().subscribe(Events.GAME_TICK, tickListener)
		context.events().subscribe(Events.FRAME_DRAW, frameListener)
		context.events().subscribe(Events.KEY_PRESSED, keyListener)
		context.registerCommand("screenshot", command)
	}

	override fun stop() {
		context?.client()?.let { restoreReportButton(it) }
		context = null
		pendingFolder = null
	}

	@JvmStatic
	fun readSettings(config: PluginConfig) {
		displayDate = config.getBoolean("displayDate", true)
		notifyWhenTaken = config.getBoolean("notifyWhenTaken", true)
		copyToClipboard = config.getBoolean("copyToClipboard", false)
		hotkey = config.getInt("hotkey", 0)
		screenshotRewards = config.getBoolean("rewards", true)
		screenshotLevels = config.getBoolean("levels", true)
		screenshotPlayerDeath = config.getBoolean("playerDeath", false)
		screenshotPlayerKills = config.getBoolean("pvpKills", false)
		screenshotKicks = config.getBoolean("kicks", false)
	}

	/** Asks for a screenshot in [folder] named [name]; one at a time. */
	@JvmStatic
	fun request(folder: String, name: String) {
		if (pendingFolder != null) {
			return
		}
		pendingFolder = folder
		pendingName = name
		pendingFrames = 0
	}

	/** RuneLite's onChatMessage: the level-ups, deaths, Barrows chests and kicks. */
	@JvmStatic
	fun chat(event: ChatMessage) {
		val message: String = ClientFeatures.plainText(event.message ?: return)
		if (event.type == CLAN_CHAT) {
			if (screenshotKicks && message == KICKED) {
				request("Clan Chat Kicks", "Kick")
			}
			return
		}
		if (event.type != ChatType.GAME && event.type != ChatType.GAME_FILTERED) {
			return
		}
		val level: MatchResult? = LEVEL_UP.find(message)
		when {
			level != null && screenshotLevels -> request("Levels", level.groupValues[1] + "(" + level.groupValues[2] + ")")
			message == DEATH && screenshotPlayerDeath -> request("Deaths", "Death")
			message == BARROWS_CHEST && screenshotRewards -> request("Barrows Rewards", "Barrows")
		}
	}

	/** RuneLite's onPlayerDeath for a kill: the player being fought starts to die. */
	private fun tick(client: ClientApi) {
		val target: Player = client.localPlayer()?.interacting() as? Player ?: return
		if (!target.isDying()) {
			if (target.index() == killed) {
				killed = -1
			}
			return
		}
		if (screenshotPlayerKills && target.index() != killed) {
			killed = target.index()
			request("PvP Kills", "Kill " + (target.name() ?: ""))
		}
	}

	/** Two frames: the date goes on the report button, then the frame with it is taken (the OpenGL toolkits give the frame shown last). */
	private fun frame(client: ClientApi) {
		val folder: String = pendingFolder ?: return
		if (client.gameState() !== GameState.LOGGED_IN) {
			pendingFolder = null
			restoreReportButton(client)
			return
		}
		if (pendingFrames == 0 && displayDate) {
			client.widget(ReportButton.INTERFACE, ReportButton.TEXT)?.let { button ->
				val date: String = ReportButton.date(clock())
				reportText = button.text()
				// the Report Button plugin writes the button each cycle: it shows this meanwhile
				ReportButton.screenshotText = date
				button.setText(date)
			}
		}
		if (pendingFrames++ < CAPTURE_FRAME) {
			return
		}
		pendingFolder = null
		val image: Image? = client.captureFrame()
		restoreReportButton(client)
		if (image == null) {
			return
		}
		save(client, image, folder, if (folder == "Quests") questName(client) else pendingName)
	}

	private fun save(client: ClientApi, image: Image, kind: String, name: String) {
		val player: String = client.localPlayer()?.name() ?: "Unknown"
		val folder: String = if (kind.isEmpty()) player else "$player/$kind"
		val file: String = (if (name.isEmpty()) "" else "$name ") + fileDate(clock())
		val where: String? = client.saveImage(image, folder, file)
		lastSaved = where
		if (copyToClipboard) {
			client.copyImage(image)
		}
		if (notifyWhenTaken && where != null) {
			client.notify("A screenshot was saved to " + where)
		}
	}

	/** RuneLite's Quest(name), from the scroll's "You have completed X!". */
	private fun questName(client: ClientApi): String {
		val text: String = client.widget(QUEST_COMPLETE, QUEST_NAME)?.text()?.let { ClientFeatures.plainText(it) } ?: return "Quest"
		val name: String = text.removePrefix("You have completed ").removeSuffix("!").trim()
		return if (name.isEmpty()) "Quest" else "Quest($name)"
	}

	private fun restoreReportButton(client: ClientApi) {
		ReportButton.screenshotText = null
		val text: String = reportText ?: return
		reportText = null
		val button: Widget? = client.widget(ReportButton.INTERFACE, ReportButton.TEXT)
		button?.setText(text)
	}

	/** RuneLite's "yyyy-MM-dd_HH-mm-ss", in the local zone. */
	@JvmStatic
	fun fileDate(millis: Long): String {
		val c: util.Calendar = util.calendarInstance()
		c.setTime(util.Date(millis))
		return c.get(YEAR).toString() + "-" + two(c.get(MONTH) + 1) + "-" + two(c.get(DAY_OF_MONTH)) + "_" +
			two(c.get(HOUR_OF_DAY)) + "-" + two(c.get(MINUTE)) + "-" + two(c.get(SECOND))
	}

	private fun two(value: Int): String = (if (value < TEN) "0" else "") + value

	private const val CAPTURE_FRAME: Int = 2
	private const val TEN: Int = 10
	private const val YEAR: Int = 1
	private const val MONTH: Int = 2
	private const val DAY_OF_MONTH: Int = 5
	private const val HOUR_OF_DAY: Int = 11
	private const val MINUTE: Int = 12
	private const val SECOND: Int = 13
}
