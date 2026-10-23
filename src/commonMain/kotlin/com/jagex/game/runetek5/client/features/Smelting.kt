package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.ChatType
import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.Graphics
import com.jagex.game.runetek5.plugin.api.OverlayPanel
import com.jagex.game.runetek5.plugin.api.Plugin
import com.jagex.game.runetek5.plugin.api.PluginApi
import com.jagex.game.runetek5.plugin.api.PluginConfig
import com.jagex.game.runetek5.plugin.api.PluginContext
import com.jagex.game.runetek5.plugin.api.PluginDescriptor
import com.jagex.game.runetek5.plugin.api.SettingsSchema
import com.jagex.game.runetek5.plugin.api.Size
import com.jagex.game.runetek5.plugin.api.Task
import com.jagex.game.runetek5.plugin.api.Viewport
import com.jagex.game.runetek5.plugin.api.events.ChatMessage
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.GameTick
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: RuneLite's Smelting. A panel at the top left says whether you are smelting
 * and how many bars and cannonballs you made this session and an hour, and goes after Reset stats minutes
 * without making any.
 *
 * Void's lines: "You retrieve a bar of …" for each bar (and "The magic of the Varrock armour enables you to
 * smelt 2 bars at the same time." for the second), "You remove the cannonballs from the mould." for four
 * cannonballs (Void has no double mould); its furnace animation. A built-in plugin (BuiltinPlugins,
 * docs/PLUGINS.md).
 */
object Smelting : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("smelting", "Smelting", "1", PluginApi.VERSION, "Counts the bars and cannonballs you smelt this session, and an hour.", "Pixel")

	/** The plugin manager's switch. */
	@JvmField var enabled: Boolean = true

	/** Void's furnace animation, for bars and cannonballs. */
	const val SMELTING_ANIMATION: Int = 899

	const val BAR: String = "You retrieve a bar of "
	const val VARROCK_ARMOUR: String = "The magic of the Varrock armour enables you to smelt 2 bars"
	const val CANNONBALLS: String = "You remove the cannonballs from the mould"

	/** Void's mould makes four. */
	const val CANNONBALLS_PER_BAR: Int = 4

	/** RuneLite's SMELT_TIMEOUT: still smelting this long after the last bar, between the furnace animations. */
	const val SMELT_TIMEOUT_MILLIS: Long = 7_000L

	/** RuneLite's SmeltingSession. */
	class Session(@JvmField val start: Long) {
		@JvmField var lastItemSmelted: Long = 0L
		@JvmField var barsSmelted: Int = 0
		@JvmField var cannonBallsSmelted: Int = 0
	}

	@JvmField var statTimeout: Int = 5

	@JvmField var session: Session? = null

	@JvmField var clock: () -> Long = { lang.currentTimeMillis() }

	/** For the smoke test: whether the last frame drew the panel. */
	@JvmField var panelDrawn: Boolean = false

	private var context: PluginContext? = null

	/** RuneLite's SmeltingOverlay. */
	@JvmField
	val panel: OverlayPanel = object : OverlayPanel("SmeltingOverlay") {
		init {
			addMenuEntry("Reset", "Smelting overlay", Task { session = null })
		}

		override fun render(g: Graphics, view: Viewport): Size? {
			if (!enabled) {
				return null
			}
			panelDrawn = false
			val client: ClientApi = context?.client() ?: return null
			val current: Session = session ?: return null
			if (client.gameState() !== GameState.LOGGED_IN) {
				return null
			}
			if (isSmelting(client, current)) {
				panelComponent.addTitle("Smelting", GREEN)
			} else {
				panelComponent.addTitle("NOT smelting", RED)
			}
			if (current.barsSmelted > 0) {
				panelComponent.addLine("Bars:", WHITE, current.barsSmelted.toString(), WHITE)
				panelComponent.addLine("Bars/hr:", WHITE, perHour(current, current.barsSmelted).toString(), WHITE)
			}
			if (current.cannonBallsSmelted > 0) {
				panelComponent.addLine("Cannonballs:", WHITE, current.cannonBallsSmelted.toString(), WHITE)
				panelComponent.addLine("Cannonballs/hr:", WHITE, perHour(current, current.cannonBallsSmelted).toString(), WHITE)
			}
			panelDrawn = true
			return super.render(g, view)
		}
	}

	private val tickListener: EventListener<GameTick> = EventListener { _ -> if (enabled) tick() }

	private val chatListener: EventListener<ChatMessage> = EventListener { event -> if (enabled) chat(event) }

	private val settingListener: EventListener<SettingChanged> = EventListener { event ->
		if (event.pluginId == DESCRIPTOR.id) {
			context?.config()?.let { readSettings(it) }
		}
	}

	private val toggle: CommandHandler = CommandHandler { _, _ ->
		session = null
		context?.client()?.consolePrint("Smelting stats reset.")
	}

	override fun start(context: PluginContext) {
		this.context = context
		val s: SettingsSchema = context.settings()
		// RuneLite's SmeltingConfig
		s.toggle("enabled", "Smelting", true)
		s.number("statTimeout", "Reset stats", 5, 1, 60, 1)
		readSettings(context.config())
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		context.events().subscribe(Events.GAME_TICK, tickListener)
		context.events().subscribe(Events.CHAT_MESSAGE, chatListener)
		context.addOverlay(panel)
		context.registerCommand("smelting", toggle)
	}

	override fun stop() {
		context = null
		session = null
	}

	@JvmStatic
	fun readSettings(config: PluginConfig) {
		enabled = config.getBoolean("enabled", true)
		statTimeout = config.getInt("statTimeout", 5)
	}

	/** RuneLite's: at the furnace, or a bar within the last seven seconds. */
	@JvmStatic
	fun isSmelting(client: ClientApi, current: Session): Boolean =
		client.localPlayer()?.animationId() == SMELTING_ANIMATION || clock() - current.lastItemSmelted < SMELT_TIMEOUT_MILLIS

	@JvmStatic
	fun perHour(current: Session, amount: Int): Int {
		val elapsed: Long = maxOf(MINUTE_MILLIS, clock() - current.start)
		return (amount.toDouble() * HOUR_MILLIS / elapsed).toInt()
	}

	/** RuneLite's onGameTick: the session dropped after the timeout. */
	@JvmStatic
	fun tick() {
		val current: Session = session ?: return
		if (clock() - current.lastItemSmelted >= statTimeout * MINUTE_MILLIS) {
			session = null
		}
	}

	/** RuneLite's onChatMessage: a bar or a mould of cannonballs starts or keeps the session and is counted. */
	@JvmStatic
	fun chat(event: ChatMessage) {
		if (event.type != ChatType.GAME && event.type != ChatType.GAME_FILTERED) {
			return
		}
		val message: String = ClientFeatures.plainText(event.message ?: return)
		val bars: Int = if (message.startsWith(BAR) || message.startsWith(VARROCK_ARMOUR)) 1 else 0
		val balls: Int = if (message.startsWith(CANNONBALLS)) CANNONBALLS_PER_BAR else 0
		if (bars == 0 && balls == 0) {
			return
		}
		val now: Long = clock()
		val current: Session = session ?: Session(now).also { session = it }
		current.lastItemSmelted = now
		current.barsSmelted += bars
		current.cannonBallsSmelted += balls
	}

	private const val HOUR_MILLIS: Long = 3_600_000L
	private const val MINUTE_MILLIS: Long = 60_000L

	private const val WHITE: Int = -0x1
	private const val GREEN: Int = 0xFF00FF00.toInt()
	private const val RED: Int = 0xFFFF0000.toInt()
}
