package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.DiscordRpc
import com.jagex.game.runetek5.plugin.DiscordRpcListener
import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.Plugin
import com.jagex.game.runetek5.plugin.api.PluginApi
import com.jagex.game.runetek5.plugin.api.PluginConfig
import com.jagex.game.runetek5.plugin.api.PluginContext
import com.jagex.game.runetek5.plugin.api.PluginDescriptor
import com.jagex.game.runetek5.plugin.api.SettingsSchema
import com.jagex.game.runetek5.plugin.api.Skill
import com.jagex.game.runetek5.plugin.api.Task
import com.jagex.game.runetek5.plugin.api.events.ClientTick
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.GameStateChanged
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import com.jagex.game.runetek5.plugin.api.events.StatChanged
import com.jagex.game.runetek5.plugin.clientVersion
import kotlin.jvm.JvmField

/**
 * Not part of the original client: RuneLite's Discord plugin for the 634 client. While on, the
 * Discord desktop app shows the player's status under "Playing Void Client": the skill being
 * trained (the details line and a small icon), the place (the state line, from the map region:
 * a city, a dungeon, a minigame, a boss or a region, each kind behind a setting), "In Game" or
 * "In Menu" otherwise, and the time elapsed since login or since the activity began. A skill
 * drops off the status after the activity timeout (5 minutes by default) without an experience
 * drop. Off until turned on in the plugin manager or with `::discord`; `::discord status` says
 * how the connection is doing.
 *
 * A built-in plugin (BuiltinPlugins, docs/PLUGINS.md). The stack of what is shown is
 * [DiscordState], the events and the places [DiscordGameEvents], the connection
 * [DiscordRpc] (nothing on the web). The Discord application whose name and icons show is
 * [APPLICATION_ID]; the config key `applicationId` overrides it for someone with an application of
 * their own, whose icons must be uploaded under the same keys (`default`, `icon_<skill>`).
 */
object DiscordPresence : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("discord", "Discord", "1", PluginApi.VERSION, "Shows what you are doing on your Discord profile: the skill you are training, where you are and for how long (::discord status checks the connection).", "Pixel")

	/** The Discord application "Void Client", whose uploaded art the image keys name. */
	const val APPLICATION_ID: String = "1562513489396768839"

	private const val CYCLES_PER_SECOND: Int = 50

	private const val REGION_CHECK_CYCLES: Int = CYCLES_PER_SECOND

	private const val TIMEOUT_CHECK_CYCLES: Int = 60 * CYCLES_PER_SECOND

	private const val MIN_TIMEOUT_MINUTES: Int = 1

	private const val MAX_TIMEOUT_MINUTES: Int = 120

	private const val CLIENT_NAME: String = "Void"

	@JvmField
	val ELAPSED_OPTIONS: Array<String> = arrayOf("Total elapsed time", "Per activity", "Hide elapsed time")

	@JvmField
	var enabled: Boolean = false

	private var showMainMenu: Boolean = true

	private var showSkillActivity: Boolean = true

	private var showBossActivity: Boolean = true

	private var showCityActivity: Boolean = true

	private var showDungeonActivity: Boolean = true

	private var showMinigameActivity: Boolean = true

	private var showRegionsActivity: Boolean = true

	private var applicationId: String = APPLICATION_ID

	private var context: PluginContext? = null

	/** Experience last seen per skill, -1 before the first update since login: the first is the login's, not a drop. */
	private val skillXp: IntArray = IntArray(Skill.COUNT) { -1 }

	private var lastRegion: Int = -1

	private var cycles: Int = 0

	@JvmField
	val state: DiscordState = DiscordState({ lang.currentTimeMillis() }, { activity -> DiscordRpc.setActivity(activity) })

	private val rpcListener: DiscordRpcListener = object : DiscordRpcListener {
		override fun event(message: String) {
			val context: PluginContext = this@DiscordPresence.context ?: return
			context.invokeLater(Task { context.log(message) })
		}
	}

	private val settingListener: EventListener<SettingChanged> = EventListener { event ->
		val context: PluginContext? = this.context
		if (event.pluginId == DESCRIPTOR.id && context != null) {
			val wasEnabled: Boolean = enabled
			readSettings(context.config())
			if (enabled != wasEnabled) {
				if (enabled) connect() else disconnect()
			} else if (enabled) {
				refresh()
			}
		}
	}

	private val stateListener: EventListener<GameStateChanged> = EventListener { event ->
		if (enabled) {
			resetState()
			gameStateUpdate()
			if (event.state === GameState.LOGGED_IN) {
				areaUpdate()
			}
		}
	}

	private val statListener: EventListener<StatChanged> = EventListener { event -> statChanged(event.skill, event.newXp) }

	private val tickListener: EventListener<ClientTick> = EventListener { _ -> tick() }

	private val command: CommandHandler = CommandHandler { _, args -> command(args) }

	override fun start(context: PluginContext) {
		this.context = context
		val settings: SettingsSchema = context.settings()
		settings.toggle("enabled", "Discord presence", false)
		settings.choice("elapsedTime", "Elapsed time", ELAPSED_OPTIONS[0], ELAPSED_OPTIONS)
		settings.number("actionTimeout", "Activity timeout (minutes)", DiscordState.DEFAULT_TIMEOUT_MINUTES.toInt(), MIN_TIMEOUT_MINUTES, MAX_TIMEOUT_MINUTES, 1)
		settings.toggle("showMainMenu", "Main menu: show status when not logged in", true)
		settings.toggle("showSkillActivity", "Skilling: show the skill you train", true)
		settings.toggle("showBossActivity", "Bosses: show your location at bosses", true)
		settings.toggle("showCityActivity", "Cities: show your location in cities", true)
		settings.toggle("showDungeonActivity", "Dungeons: show your location in dungeons", true)
		settings.toggle("showMinigameActivity", "Minigames: show your location in minigames", true)
		settings.toggle("showRegionsActivity", "Regions: show your location in other regions", true)
		val version: String? = clientVersion()
		state.largeImageText = if (version == null) CLIENT_NAME else CLIENT_NAME + " v" + version
		readSettings(context.config())
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		context.events().subscribe(Events.GAME_STATE_CHANGED, stateListener)
		context.events().subscribe(Events.STAT_CHANGED, statListener)
		context.events().subscribe(Events.CLIENT_TICK, tickListener)
		context.registerCommand("discord", command)
		if (enabled) {
			connect()
		}
	}

	override fun stop() {
		disconnect()
		context = null
	}

	private fun readSettings(config: PluginConfig) {
		enabled = config.getBoolean("enabled", false)
		state.elapsedMode = elapsedMode(config.get("elapsedTime", ELAPSED_OPTIONS[0]))
		val minutes: Int = config.getInt("actionTimeout", DiscordState.DEFAULT_TIMEOUT_MINUTES.toInt())
		state.timeoutMillis = minutes.coerceIn(MIN_TIMEOUT_MINUTES, MAX_TIMEOUT_MINUTES) * DiscordState.MILLIS_PER_MINUTE
		showMainMenu = config.getBoolean("showMainMenu", true)
		showSkillActivity = config.getBoolean("showSkillActivity", true)
		showBossActivity = config.getBoolean("showBossActivity", true)
		showCityActivity = config.getBoolean("showCityActivity", true)
		showDungeonActivity = config.getBoolean("showDungeonActivity", true)
		showMinigameActivity = config.getBoolean("showMinigameActivity", true)
		showRegionsActivity = config.getBoolean("showRegionsActivity", true)
		val id: String = config.get("applicationId", "").trim()
		applicationId = if (id.isEmpty()) APPLICATION_ID else id
	}

	/** The elapsed-time mode the option text [option] names; total for anything else. */
	fun elapsedMode(option: String): Int {
		return when (option) {
			ELAPSED_OPTIONS[1] -> DiscordState.ELAPSED_ACTIVITY
			ELAPSED_OPTIONS[2] -> DiscordState.ELAPSED_HIDDEN
			else -> DiscordState.ELAPSED_TOTAL
		}
	}

	private fun connect() {
		DiscordRpc.start(applicationId, rpcListener)
		refresh()
	}

	private fun disconnect() {
		resetState()
		DiscordRpc.stop()
	}

	/** Starts the stack over from what the client shows now, as RuneLite does when a setting changes. */
	private fun refresh() {
		resetState()
		gameStateUpdate()
		areaUpdate()
	}

	private fun resetState() {
		state.reset()
		skillXp.fill(-1)
		lastRegion = -1
	}

	private fun gameStateUpdate() {
		val client: ClientApi = context?.client() ?: return
		val loggedIn: Boolean = client.gameState() === GameState.LOGGED_IN
		if (showMainMenu || loggedIn) {
			state.triggerEvent(if (loggedIn) DiscordGameEvents.IN_GAME else DiscordGameEvents.IN_MENU)
		}
	}

	/** The place the player stands in, when it is one the table knows and its kind is shown; "In Game" when it is unknown. */
	private fun areaUpdate() {
		val client: ClientApi = context?.client() ?: return
		if (client.gameState() !== GameState.LOGGED_IN || client.localPlayer() == null) {
			return
		}
		val regionId: Int = client.regionId()
		if (regionId <= 0) {
			return
		}
		lastRegion = regionId
		val area: DiscordGameEvent = DiscordGameEvents.fromRegion(regionId) ?: run {
			state.triggerEvent(DiscordGameEvents.IN_GAME)
			return
		}
		if (showArea(area)) {
			state.triggerEvent(area)
		}
	}

	private fun showArea(area: DiscordGameEvent): Boolean {
		return when (area.areaType) {
			DiscordAreaType.BOSSES -> showBossActivity
			DiscordAreaType.CITIES -> showCityActivity
			DiscordAreaType.DUNGEONS -> showDungeonActivity
			DiscordAreaType.MINIGAMES -> showMinigameActivity
			DiscordAreaType.REGIONS -> showRegionsActivity
			null -> false
		}
	}

	/** An experience drop (not the login's first value, not a loss) puts the skill on the stack. */
	private fun statChanged(skill: Int, xp: Int) {
		if (!enabled || skill < 0 || skill >= Skill.COUNT) {
			return
		}
		val previous: Int = skillXp[skill]
		skillXp[skill] = xp
		if (previous < 0 || previous >= xp || !showSkillActivity) {
			return
		}
		val event: DiscordGameEvent = DiscordGameEvents.fromSkill(skill) ?: return
		state.triggerEvent(event)
	}

	private fun tick() {
		if (!enabled) {
			return
		}
		cycles++
		if (cycles % REGION_CHECK_CYCLES == 0) {
			val client: ClientApi = context?.client() ?: return
			if (client.gameState() === GameState.LOGGED_IN && client.regionId() != lastRegion) {
				areaUpdate()
			}
		}
		if (cycles % TIMEOUT_CHECK_CYCLES == 0) {
			state.checkForTimeout()
		}
	}

	private fun command(args: Array<String>) {
		val context: PluginContext = this.context ?: return
		val client: ClientApi = context.client()
		if (args.isNotEmpty() && args[0].equals("status", ignoreCase = true)) {
			client.consolePrint("Discord: " + (if (enabled) DiscordRpc.status() else "off") + "; " + stackSummary())
			return
		}
		enabled = !enabled
		context.config().setBoolean("enabled", enabled)
		context.config().save()
		if (enabled) connect() else disconnect()
		client.consolePrint(if (enabled) "Discord presence on." else "Discord presence off.")
	}

	private fun stackSummary(): String {
		val activity: DiscordActivity? = state.lastActivity
		if (activity == null) {
			return "nothing shown"
		}
		val out: StringBuilder = StringBuilder("showing \"").append(activity.details).append("\" / \"").append(activity.state).append("\"")
		if (activity.smallImageKey != null) {
			out.append(" (").append(activity.smallImageKey).append(")")
		}
		out.append("; stack:")
		for (i in 0 until state.eventCount()) {
			out.append(' ').append(state.eventAt(i).name)
		}
		return out.toString()
	}
}
