package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.Image
import com.jagex.game.runetek5.plugin.api.Overlay
import com.jagex.game.runetek5.plugin.api.Plugin
import com.jagex.game.runetek5.plugin.api.PluginApi
import com.jagex.game.runetek5.plugin.api.PluginConfig
import com.jagex.game.runetek5.plugin.api.PluginContext
import com.jagex.game.runetek5.plugin.api.PluginDescriptor
import com.jagex.game.runetek5.plugin.api.SettingsSchema
import com.jagex.game.runetek5.plugin.api.Timer
import com.jagex.game.runetek5.plugin.api.Widget
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.GameStateChanged
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import com.jagex.game.runetek5.plugin.api.events.VarpChanged
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: RuneLite's Poison. While you are poisoned an infobox counts down to the
 * next hit (its tooltip, and the life points orb's, give the damage, the time to it and the time until the
 * poison wears off), and the orb's heart turns green, or yellow while diseased (RuneLite's own hearts,
 * ClientApi.overrideSprite).
 *
 * Void's poison (Poison): the poison varp (102) holds the next hit in life points; every 30 ticks (18
 * seconds) it hits that much and falls by 2, and once it would fall to 10 or less it wears off instead of
 * hitting. Disease is varp 456. Venom is OSRS-only. A built-in plugin (BuiltinPlugins, docs/PLUGINS.md).
 */
object Poison : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("poison", "Poison", "1", PluginApi.VERSION, "Times your poison's next hit and its end, and turns the life points orb's heart green while poisoned.", "Pixel")

	const val POISON_VARP: Int = 102
	const val DISEASE_VARP: Int = 456

	/** Void's poison timer: 30 ticks. */
	const val POISON_TICK_MILLIS: Long = 18_000L

	/** At or under this the poison wears off instead of hitting (Void's Poison.tick). */
	const val CURED_AT: Int = 10

	const val HEALTH_ORB: Int = 748
	const val HEALTH_ORB_CIRCLE: Int = 3

	/** The life points orb's heart (748:7). */
	const val HEART_SPRITE: Int = 1197

	const val RESOURCE: String = "/poison/hearts.rgba"

	const val HEART_POISON: Int = 0
	const val HEART_DISEASE: Int = 1

	@JvmField var showInfoboxes: Boolean = false
	@JvmField var changeHealthIcon: Boolean = true

	@JvmField var lastDamage: Int = 0

	/** When the poison varp last changed (a hit or a new poison), from [clock]. */
	@JvmField var lastChange: Long = 0L

	/** The poison varp's value, for the cure time. */
	@JvmField var poisonValue: Int = 0

	/** Which heart is on the orb: -1 the game's own. */
	@JvmField var heart: Int = -1

	@JvmField var clock: () -> Long = { lang.currentTimeMillis() }

	/** RuneLite's PoisonInfobox: a Timer to the next hit, with the plugin's tooltip. */
	class PoisonInfobox(image: Image?) : Timer("Poison", POISON_TICK_MILLIS) {
		init {
			this.image = image
		}

		override fun tooltip(): String = createTooltip()

		override fun textColour(): Int = RED_BRIGHTER
	}

	@JvmField var infobox: PoisonInfobox? = null

	private var hearts: Array<Image>? = null

	private var context: PluginContext? = null

	/** RuneLite's PoisonOverlay: the tooltip over the life points orb. */
	private val overlay: Overlay = Overlay { _, _ -> context?.client()?.let { orbTooltip(it) } }

	private val varpListener: EventListener<VarpChanged> = EventListener { event ->
		val client: ClientApi = context?.client() ?: return@EventListener
		if (event.id == POISON_VARP) {
			poisonChanged(client, event.value)
		} else if (event.id == DISEASE_VARP) {
			checkHealthIcon(client)
		}
	}

	private val stateListener: EventListener<GameStateChanged> = EventListener { event ->
		if (event.state === GameState.LOGIN_SCREEN) {
			lastDamage = 0
			removeInfobox()
		}
	}

	private val settingListener: EventListener<SettingChanged> = EventListener { event ->
		if (event.pluginId == DESCRIPTOR.id) {
			val client: ClientApi = context?.client() ?: return@EventListener
			context?.config()?.let { readSettings(it) }
			if (!showInfoboxes) {
				removeInfobox()
			}
			if (changeHealthIcon) checkHealthIcon(client) else resetHealthIcon(client)
		}
	}

	private val toggle: CommandHandler = CommandHandler { _, _ ->
		showInfoboxes = !showInfoboxes
		context?.config()?.setBoolean("showInfoboxes", showInfoboxes)
		context?.config()?.save()
		if (!showInfoboxes) {
			removeInfobox()
		}
		context?.client()?.consolePrint(if (showInfoboxes) "Poison infoboxes on." else "Poison infoboxes off.")
	}

	override fun start(context: PluginContext) {
		this.context = context
		val s: SettingsSchema = context.settings()
		// RuneLite's PoisonConfig, in its order
		s.toggle("showInfoboxes", "Show infoboxes", false)
		s.toggle("changeHealthIcon", "Change HP orb icon", true)
		readSettings(context.config())
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		context.events().subscribe(Events.VARP_CHANGED, varpListener)
		context.events().subscribe(Events.GAME_STATE_CHANGED, stateListener)
		context.addOverlay(overlay)
		context.registerCommand("poisoninfo", toggle)
		if (context.client().gameState() === GameState.LOGGED_IN) {
			checkHealthIcon(context.client())
		}
	}

	override fun stop() {
		context?.client()?.let { resetHealthIcon(it) }
		removeInfobox()
		lastDamage = 0
		poisonValue = 0
		context = null
	}

	@JvmStatic
	fun readSettings(config: PluginConfig) {
		showInfoboxes = config.getBoolean("showInfoboxes", false)
		changeHealthIcon = config.getBoolean("changeHealthIcon", true)
	}

	/** The next hit of a poison varp of [value]: the value while it is still over the cure, else none. */
	@JvmStatic
	fun nextDamage(value: Int): Int = if (value - 2 > CURED_AT) value else 0

	/** The hits a poison varp of [value] has left: each takes 2 off, the last leaving it over the cure. */
	@JvmStatic
	fun hitsLeft(value: Int): Int = if (value <= CURED_AT + 2) 0 else (value - CURED_AT - 1) / 2

	/** RuneLite's onVarbitChanged(POISON): the timers start again and the infobox shows the next hit. */
	@JvmStatic
	fun poisonChanged(client: ClientApi, value: Int) {
		poisonValue = value
		lastChange = clock()
		lastDamage = nextDamage(value)
		if (showInfoboxes) {
			removeInfobox()
			if (lastDamage > 0) {
				val box: PoisonInfobox = PoisonInfobox(heartImage(client, HEART_POISON))
				infobox = box
				context?.addInfoBox(box)
			}
		}
		checkHealthIcon(client)
	}

	private fun removeInfobox() {
		val box: PoisonInfobox = infobox ?: return
		context?.removeInfoBox(box)
		infobox = null
	}

	/** RuneLite's createTooltip: the next damage in red, the time to it and the time until the poison wears off. */
	@JvmStatic
	fun createTooltip(): String {
		val elapsed: Long = clock() - lastChange
		val untilHit: Long = POISON_TICK_MILLIS - elapsed
		val untilCure: Long = (hitsLeft(poisonValue) + 1) * POISON_TICK_MILLIS - elapsed
		return "Next poison damage: <col=ff0000>" + lastDamage + "</col></br>Time until damage: " + formatted(untilHit) + "</br>Time until cure: " + formatted(untilCure)
	}

	@JvmStatic
	fun formatted(millis: Long): String {
		val seconds: Int = (maxOf(0L, millis) / 1000L).toInt()
		val rest: Int = seconds % 60
		return (seconds / 60).toString() + ":" + (if (rest < 10) "0" else "") + rest
	}

	private fun orbTooltip(client: ClientApi) {
		if (lastDamage <= 0 || client.gameState() !== GameState.LOGGED_IN) {
			return
		}
		val orb: Widget = client.widget(HEALTH_ORB, HEALTH_ORB_CIRCLE) ?: return
		if (orb.isHidden() || orb.screenX() <= 0) {
			return
		}
		val x: Int = client.mouseX() - orb.screenX()
		val y: Int = client.mouseY() - orb.screenY()
		if (x >= 0 && y >= 0 && x < orb.width() && y < orb.height()) {
			client.addTooltip(createTooltip())
		}
	}

	/** RuneLite's checkHealthIcon: the green heart while poisoned, the yellow one while diseased, else the game's. */
	@JvmStatic
	fun checkHealthIcon(client: ClientApi) {
		if (!changeHealthIcon) {
			return
		}
		val wanted: Int = when {
			client.varp(POISON_VARP) > 0 -> HEART_POISON
			client.varp(DISEASE_VARP) > 0 -> HEART_DISEASE
			else -> -1
		}
		if (wanted < 0) {
			resetHealthIcon(client)
			return
		}
		if (wanted != heart) {
			val image: Image = heartImage(client, wanted) ?: return
			heart = wanted
			client.overrideSprite(HEART_SPRITE, image)
		}
	}

	@JvmStatic
	fun resetHealthIcon(client: ClientApi) {
		if (heart < 0) {
			return
		}
		heart = -1
		client.overrideSprite(HEART_SPRITE, null)
	}

	/** RuneLite's poison and disease hearts (AlternateSprites), on the canvas of the orb's own heart. */
	private fun heartImage(client: ClientApi, which: Int): Image? {
		val loaded: Array<Image> = hearts ?: loadHearts(client)?.also { hearts = it } ?: return null
		return loaded.getOrNull(which)
	}

	private fun loadHearts(client: ClientApi): Array<Image>? {
		val bytes: ByteArray = lang.readResource(RESOURCE) ?: return null
		if (bytes.size < 8) {
			return null
		}
		val count: Int = readInt(bytes, 0)
		val size: Int = readInt(bytes, 4)
		if (count < 2 || size <= 0 || bytes.size < 8 + count * size * size * 4) {
			return null
		}
		return Array(count) { h ->
			val argb = IntArray(size * size) { p -> readInt(bytes, 8 + (h * size * size + p) * 4) }
			client.createImage(size, size, argb) ?: return null
		}
	}

	private fun readInt(bytes: ByteArray, at: Int): Int =
		((bytes[at].toInt() and 0xFF) shl 24) or ((bytes[at + 1].toInt() and 0xFF) shl 16) or ((bytes[at + 2].toInt() and 0xFF) shl 8) or (bytes[at + 3].toInt() and 0xFF)
}
