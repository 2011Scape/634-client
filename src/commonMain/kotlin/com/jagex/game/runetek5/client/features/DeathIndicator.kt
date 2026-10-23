package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.ChatType
import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.Graphics
import com.jagex.game.runetek5.plugin.api.Player
import com.jagex.game.runetek5.plugin.api.Plugin
import com.jagex.game.runetek5.plugin.api.PluginApi
import com.jagex.game.runetek5.plugin.api.PluginConfig
import com.jagex.game.runetek5.plugin.api.PluginContext
import com.jagex.game.runetek5.plugin.api.PluginDescriptor
import com.jagex.game.runetek5.plugin.api.SettingsSchema
import com.jagex.game.runetek5.plugin.api.Task
import com.jagex.game.runetek5.plugin.api.Timer
import com.jagex.game.runetek5.plugin.api.events.ChatMessage
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.FrameDraw
import com.jagex.game.runetek5.plugin.api.events.GameTick
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: RuneLite's Death Indicator (before OSRS added its own grave marker).
 * When you die and your gravestone appears, the game's hint arrow points at the tile you died on
 * (ClientApi.setHintArrow, API 29), an infobox counts down the gravestone's time with the world you died
 * on, and the world map marks the place (projectWorldMap, API 29); all go once you stand on the tile or
 * the gravestone crumbles, and they are kept over a logout.
 *
 * Void's death: "Oh dear, you are dead!", then (outside the Wilderness, where your items go to your killer)
 * "Your gravestone has appeared and will last N minutes…", the minutes by the gravestone you chose; you
 * come back in Lumbridge. A built-in plugin (BuiltinPlugins, docs/PLUGINS.md).
 */
object DeathIndicator : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("deathIndicator", "Death Indicator", "1", PluginApi.VERSION, "Points you back to your gravestone after a death, with its time left and a mark on the world map.", "Pixel")

	/** The plugin manager's switch. */
	@JvmField var enabled: Boolean = true

	const val DEATH_MESSAGE: String = "Oh dear, you are dead!"

	/** Void's Gravestone: "Your gravestone has appeared and will last N minute(s) before crumbling to dust. You". */
	@JvmField
	val GRAVESTONE: Regex = Regex("Your gravestone has appeared and will last (\\d+) minutes?")

	/** RuneLite's infobox picture: bones. */
	const val BONES: Int = 526

	@JvmField var deathHintArrow: Boolean = true
	@JvmField var deathInfoBox: Boolean = true
	@JvmField var deathOnWorldMap: Boolean = true

	/** The death being shown: world tile, level, world and when the gravestone crumbles; -1 for none. */
	@JvmField var deathX: Int = -1
	@JvmField var deathY: Int = -1
	@JvmField var deathPlane: Int = -1
	@JvmField var deathWorld: Int = -1
	@JvmField var expires: Long = 0L

	/** Whether the player has been off the tile since dying: the gravestone's line comes while still on it. */
	@JvmField var leftTile: Boolean = false

	/** Where the player died, waiting for the gravestone's message. */
	private var pendingX: Int = -1
	private var pendingY: Int = -1
	private var pendingPlane: Int = -1

	@JvmField var clock: () -> Long = { lang.currentTimeMillis() }

	/** RuneLite's death timer infobox, with its Clear entry. */
	@JvmField var infobox: Timer? = null

	/** For the smoke test: whether the last frame marked the world map. */
	@JvmField var mapMarked: Boolean = false

	private var arrowSet: Boolean = false
	private val point: IntArray = IntArray(2)
	private val area: IntArray = IntArray(4)
	private var context: PluginContext? = null

	private val chatListener: EventListener<ChatMessage> = EventListener { event -> if (enabled) context?.client()?.let { chat(it, event) } }

	private val tickListener: EventListener<GameTick> = EventListener { _ -> if (enabled) context?.client()?.let { tick(it) } }

	private val frameListener: EventListener<FrameDraw> = EventListener { event -> if (enabled) context?.client()?.let { drawWorldMap(event.graphics, it) } }

	private val settingListener: EventListener<SettingChanged> = EventListener { event ->
		if (event.pluginId == DESCRIPTOR.id) {
			context?.config()?.let { readSettings(it) }
			// switched off, the marks go; the death stays kept for when it is on again
			context?.client()?.let { if (enabled) refresh(it) else removeMarks(it) }
		}
	}

	private val toggle: CommandHandler = CommandHandler { _, _ -> context?.client()?.let { clear(it) } }

	override fun start(context: PluginContext) {
		this.context = context
		val s: SettingsSchema = context.settings()
		// RuneLite's DeathIndicatorConfig, in its order
		s.toggle("enabled", "Death Indicator", true)
		s.toggle("deathHintArrow", "Death Hint Arrow", true)
		s.toggle("deathInfoBox", "Death InfoBox", true)
		s.toggle("deathOnWorldMap", "Mark on World Map", true)
		readSettings(context.config())
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		context.events().subscribe(Events.CHAT_MESSAGE, chatListener)
		context.events().subscribe(Events.GAME_TICK, tickListener)
		context.events().subscribe(Events.FRAME_DRAW, frameListener)
		context.registerCommand("deathindicator", toggle)
	}

	override fun stop() {
		context?.client()?.let { removeMarks(it) }
		context = null
	}

	@JvmStatic
	fun readSettings(config: PluginConfig) {
		enabled = config.getBoolean("enabled", true)
		deathHintArrow = config.getBoolean("deathHintArrow", true)
		deathInfoBox = config.getBoolean("deathInfoBox", true)
		deathOnWorldMap = config.getBoolean("deathOnWorldMap", true)
		deathX = config.getInt("deathLocationX", -1)
		deathY = config.getInt("deathLocationY", -1)
		deathPlane = config.getInt("deathLocationPlane", -1)
		deathWorld = config.getInt("deathWorld", -1)
		expires = config.get("timeOfDeath", "0").toLongOrNull() ?: 0L
	}

	@JvmStatic
	fun hasDeath(): Boolean = deathX >= 0 && clock() < expires

	/** RuneLite's onPlayerDeath, from Void's lines: the tile when "Oh dear, you are dead!", the grave's time from its own line. */
	@JvmStatic
	fun chat(client: ClientApi, event: ChatMessage) {
		if (event.type != ChatType.GAME && event.type != ChatType.GAME_FILTERED) {
			return
		}
		val message: String = ClientFeatures.plainText(event.message ?: return)
		if (message == DEATH_MESSAGE) {
			val player: Player = client.localPlayer() ?: return
			pendingX = player.tileX() + client.sceneBaseX()
			pendingY = player.tileY() + client.sceneBaseY()
			pendingPlane = player.level()
			return
		}
		val grave: MatchResult = GRAVESTONE.find(message) ?: return
		if (pendingX < 0) {
			return
		}
		died(client, pendingX, pendingY, pendingPlane, grave.groupValues[1].toInt())
		pendingX = -1
	}

	/** A death to show: kept in the settings (over a logout), the marks put up. */
	@JvmStatic
	fun died(client: ClientApi, x: Int, y: Int, plane: Int, minutes: Int) {
		deathX = x
		deathY = y
		deathPlane = plane
		deathWorld = client.worldId()
		expires = clock() + minutes * MINUTE_MILLIS
		leftTile = false
		context?.config()?.let { config ->
			config.setInt("deathLocationX", x)
			config.setInt("deathLocationY", y)
			config.setInt("deathLocationPlane", plane)
			config.setInt("deathWorld", deathWorld)
			config.set("timeOfDeath", expires.toString())
			config.save()
		}
		refresh(client)
	}

	/** RuneLite's onGameTick: the marks kept up (the arrow follows the scene), gone on the tile or when the grave crumbles. */
	@JvmStatic
	fun tick(client: ClientApi) {
		if (deathX < 0 || client.gameState() !== GameState.LOGGED_IN) {
			return
		}
		val player: Player? = client.localPlayer()
		val onTile: Boolean = player != null && player.tileX() + client.sceneBaseX() == deathX && player.tileY() + client.sceneBaseY() == deathY && player.level() == deathPlane
		if (!onTile) {
			leftTile = true
		}
		if (!hasDeath() || (onTile && leftTile)) {
			clear(client)
			return
		}
		if (deathHintArrow && client.worldId() == deathWorld) {
			client.setHintArrow(deathX, deathY, deathPlane)
			arrowSet = true
		}
		if (deathInfoBox && infobox == null) {
			addInfobox(client)
		}
	}

	private fun refresh(client: ClientApi) {
		removeMarks(client)
		tick(client)
	}

	private fun addInfobox(client: ClientApi) {
		val box: Timer = object : Timer("Death", maxOf(0L, expires - clock())) {
			override fun tooltip(): String = "Died on world: " + deathWorld
		}
		box.itemId = BONES
		box.addMenuEntry("Clear", "Death Indicator", Task { clear(client) })
		infobox = box
		context?.addInfoBox(box)
	}

	/** The death forgotten: the marks down and the settings cleared. */
	@JvmStatic
	fun clear(client: ClientApi) {
		removeMarks(client)
		deathX = -1
		deathY = -1
		deathPlane = -1
		deathWorld = -1
		expires = 0L
		context?.config()?.let { config ->
			for (key in arrayOf("deathLocationX", "deathLocationY", "deathLocationPlane", "deathWorld", "timeOfDeath")) {
				config.remove(key)
			}
			config.save()
		}
	}

	private fun removeMarks(client: ClientApi) {
		if (arrowSet) {
			client.clearHintArrow()
			arrowSet = false
		}
		infobox?.let { context?.removeInfoBox(it) }
		infobox = null
	}

	/** RuneLite's WorldMapPoint: a red arrow on the world map pointing down at the tile. */
	private fun drawWorldMap(g: Graphics, client: ClientApi) {
		mapMarked = false
		if (!deathOnWorldMap || !hasDeath() || !client.worldMapArea(area) || !client.projectWorldMap(deathX, deathY, point)) {
			return
		}
		g.clip(area[0], area[1], area[2], area[3])
		val x: Int = point[0]
		val y: Int = point[1]
		val xs: IntArray = intArrayOf(x - ARROW, x + ARROW, x)
		val ys: IntArray = intArrayOf(y - ARROW * 2, y - ARROW * 2, y)
		g.fillPolygon(xs, ys, 3, RED)
		g.polygon(xs, ys, 3, BLACK)
		if (g.fontReady()) {
			g.textCentered(x, y - ARROW * 2 - 3, "Grave", WHITE, BLACK)
		}
		g.unclip()
		mapMarked = true
	}

	private const val MINUTE_MILLIS: Long = 60_000L
	private const val ARROW: Int = 6
	private const val RED: Int = 0xFFE01010.toInt()
	private const val BLACK: Int = 0xFF000000.toInt()
	private const val WHITE: Int = -0x1
}
