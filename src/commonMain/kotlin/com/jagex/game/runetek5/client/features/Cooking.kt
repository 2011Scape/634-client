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
 * Not part of the original client: RuneLite's Cooking. A panel at the top left says whether you are cooking
 * and how much you cooked this session (and an hour) and burnt (and what share), and goes after Reset stats
 * minutes without cooking anything.
 *
 * Void's lines: "You accidentally burn the …" for a burn, and for a success one of its recipes' own lines
 * ("You successfully cook a trout.", "You cook a piece of meat.", "You roast a lobster.", "You manage to
 * cook a tuna."); its cooking animations on a fire and a range. Void does not ferment wine, so RuneLite's
 * fermenting timer is left out. A built-in plugin (BuiltinPlugins, docs/PLUGINS.md).
 */
object Cooking : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("cooking", "Cooking", "1", PluginApi.VERSION, "Counts what you cook and burn this session, and an hour.", "Pixel")

	/** The plugin manager's switch. */
	@JvmField var enabled: Boolean = true

	/** Void's cooking animations: on a range and on a fire. */
	@JvmField
	val COOKING_ANIMATIONS: IntArray = intArrayOf(896, 897)

	/** Void's recipes' cooked lines (cooking.recipes.toml). */
	@JvmField
	val COOKED: Regex = Regex("^You (?:successfully |manage to )?(?:cook|bake|roast|fry|scramble) ")

	/** Void's recipes' burnt lines. */
	@JvmField
	val BURNT: Regex = Regex("^You accidentally burn ")

	/** RuneLite's CookingSession. */
	class Session(@JvmField val start: Long) {
		@JvmField var lastCookingAction: Long = 0L
		@JvmField var cookAmount: Int = 0
		@JvmField var burnAmount: Int = 0

		/** The share burnt, in percent. */
		fun burntPercentage(): Double = if (cookAmount + burnAmount == 0) 0.0 else burnAmount * PERCENT / (cookAmount + burnAmount)
	}

	@JvmField var statTimeout: Int = 5

	@JvmField var session: Session? = null

	@JvmField var clock: () -> Long = { lang.currentTimeMillis() }

	/** For the smoke test: whether the last frame drew the panel. */
	@JvmField var panelDrawn: Boolean = false

	private var context: PluginContext? = null

	/** RuneLite's CookingOverlay. */
	@JvmField
	val panel: OverlayPanel = object : OverlayPanel("CookingOverlay") {
		init {
			addMenuEntry("Reset", "Cooking overlay", Task { session = null })
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
			if (isCooking(client)) {
				panelComponent.addTitle("Cooking", GREEN)
			} else {
				panelComponent.addTitle("NOT cooking", RED)
			}
			panelComponent.addLine("Cooked:", WHITE, current.cookAmount.toString() + " (" + perHour(current) + "/hr)", WHITE)
			panelDrawn = true
			panelComponent.addLine("Burnt:", WHITE, current.burnAmount.toString() + " (" + oneDecimal(current.burntPercentage()) + "%)", WHITE)
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
		context?.client()?.consolePrint("Cooking stats reset.")
	}

	override fun start(context: PluginContext) {
		this.context = context
		val s: SettingsSchema = context.settings()
		// RuneLite's CookingConfig, but the wine fermenting timer (Void does not ferment wine)
		s.toggle("enabled", "Cooking", true)
		s.number("statTimeout", "Reset stats (minutes)", 5, 1, 60, 1)
		readSettings(context.config())
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		context.events().subscribe(Events.GAME_TICK, tickListener)
		context.events().subscribe(Events.CHAT_MESSAGE, chatListener)
		context.addOverlay(panel)
		context.registerCommand("cooking", toggle)
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

	/** RuneLite's "%.1f": one decimal, rounded half up. */
	@JvmStatic
	fun oneDecimal(value: Double): String {
		val tenths: Long = kotlin.math.round(value * TENTHS).toLong()
		return (tenths / TENTHS.toLong()).toString() + "." + (tenths % TENTHS.toLong())
	}

	@JvmStatic
	fun isCooking(client: ClientApi): Boolean = (client.localPlayer()?.animationId() ?: -1) in COOKING_ANIMATIONS

	@JvmStatic
	fun perHour(current: Session): Int {
		val elapsed: Long = maxOf(MINUTE_MILLIS, clock() - current.start)
		return (current.cookAmount.toDouble() * HOUR_MILLIS / elapsed).toInt()
	}

	/** RuneLite's onGameTick: the session dropped after the timeout. */
	@JvmStatic
	fun tick() {
		val current: Session = session ?: return
		if (clock() - current.lastCookingAction >= statTimeout * MINUTE_MILLIS) {
			session = null
		}
	}

	/** RuneLite's onChatMessage: a cooked or burnt line starts or keeps the session and is counted. */
	@JvmStatic
	fun chat(event: ChatMessage) {
		if (event.type != ChatType.GAME && event.type != ChatType.GAME_FILTERED) {
			return
		}
		val message: String = ClientFeatures.plainText(event.message ?: return)
		val burnt: Boolean = BURNT.containsMatchIn(message)
		if (!burnt && !COOKED.containsMatchIn(message)) {
			return
		}
		val now: Long = clock()
		val current: Session = session ?: Session(now).also { session = it }
		current.lastCookingAction = now
		if (burnt) {
			current.burnAmount++
		} else {
			current.cookAmount++
		}
	}

	private const val PERCENT: Double = 100.0
	private const val TENTHS: Double = 10.0
	private const val HOUR_MILLIS: Long = 3_600_000L
	private const val MINUTE_MILLIS: Long = 60_000L

	private const val WHITE: Int = -0x1
	private const val GREEN: Int = 0xFF00FF00.toInt()
	private const val RED: Int = 0xFFFF0000.toInt()
}
