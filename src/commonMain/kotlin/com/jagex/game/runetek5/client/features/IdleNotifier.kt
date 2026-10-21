package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.Actor
import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.Npc
import com.jagex.game.runetek5.plugin.api.NpcDefinition
import com.jagex.game.runetek5.plugin.api.Overlay
import com.jagex.game.runetek5.plugin.api.Plugin
import com.jagex.game.runetek5.plugin.api.PluginApi
import com.jagex.game.runetek5.plugin.api.PluginConfig
import com.jagex.game.runetek5.plugin.api.PluginContext
import com.jagex.game.runetek5.plugin.api.PluginDescriptor
import com.jagex.game.runetek5.plugin.api.SettingsSchema
import com.jagex.game.runetek5.plugin.api.Skill
import com.jagex.game.runetek5.plugin.api.events.AnimationChanged
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.GameStateChanged
import com.jagex.game.runetek5.plugin.api.events.GameTick
import com.jagex.game.runetek5.plugin.api.events.HitsplatApplied
import com.jagex.game.runetek5.plugin.api.events.KeyPressed
import com.jagex.game.runetek5.plugin.api.events.MouseClicked
import com.jagex.game.runetek5.plugin.api.events.MouseWheel
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import kotlin.jvm.JvmField

/**
 * Not part of the original client: RuneLite's Idle Notifier for the 634 client. It says when a
 * skilling animation has stopped, a fight or a fishing spot is over, the player has stood still,
 * or the mouse and keyboard have been untouched for a while; and when life points, prayer points
 * or run energy are low, or run energy or special attack energy are back. Each is a setting of
 * its own in the plugin manager, with a threshold where there is one, and how it is said is shared:
 * a line in the chat, a red frame around the game view, a sound effect, a notification on the
 * desktop, the window brought to the front, and whether only while the window is unfocused. Off until turned on in the plugin
 * manager or with `::idle`; `::idle <seconds>` sets the wait (5 by default).
 *
 * A built-in plugin (BuiltinPlugins, docs/PLUGINS.md) on API 8, for [ClientApi.isFocused],
 * [ClientApi.notify] and [ClientApi.requestFocus]. The rules are in [IdleDetector], the animations watched in [IdleAnimations].
 */
object IdleNotifier : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("idle", "Idle notifier", "2", PluginApi.VERSION, "Says when you stop skilling, fighting or moving, when you have been away from the keyboard, and when life points, prayer or energy cross a threshold: in the chat, a red frame, a sound or on the desktop (::idle turns it on and off, ::idle <seconds> sets the wait).", "Pixel")

	private const val MIN_SECONDS: Int = 2

	private const val MAX_SECONDS: Int = 600

	private const val MAX_INPUT_IDLE_MINUTES: Int = 60

	private const val DEFAULT_INPUT_IDLE_MINUTES: Int = 5

	private const val MAX_LIFE_POINTS: Int = 990

	private const val DEFAULT_LIFE_POINTS: Int = 100

	private const val MAX_PRAYER_POINTS: Int = 99

	private const val MAX_PERCENT: Int = 100

	/** The sound the AttackTimer recipe warns with. */
	private const val DEFAULT_SOUND: Int = 2266

	private const val MAX_SOUND: Int = 9999

	/** The exact life points, where the stat's level is only a tenth of them (the Void server's varbit). */
	private const val LIFE_POINTS_VARBIT: Int = 7198

	/** Special attack energy in tenths of a percent (the Void server's varp). */
	private const val SPECIAL_ATTACK_VARP: Int = 300

	/** A level in the stat is ten life points. */
	private const val LIFE_POINTS_PER_LEVEL: Int = 10

	/** How many client cycles the frame shows (50 a second). */
	private const val FLASH_CYCLES: Int = 100

	private const val FRAME_COLOUR: Int = 0xFFE03030.toInt()

	private const val FRAME_WIDTH: Int = 3

	private const val NPC_ACTIONS: Int = 5

	private const val CYCLES_PER_MINUTE: Int = 60 * IdleDetector.CYCLES_PER_SECOND

	@JvmField
	var enabled: Boolean = false

	private var waitSeconds: Int = IdleDetector.DEFAULT_DELAY_SECONDS

	private var inputIdleMinutes: Int = DEFAULT_INPUT_IDLE_MINUTES

	private var styleChat: Boolean = true

	private var styleFrame: Boolean = true

	private var styleSound: Boolean = false

	private var soundId: Int = DEFAULT_SOUND

	private var styleDesktop: Boolean = true

	private var styleFocus: Boolean = false

	private var onlyUnfocused: Boolean = true

	private val detector: IdleDetector = IdleDetector()

	private var lastMouseX: Int = -1

	private var lastMouseY: Int = -1

	/** The client cycle the frame shows until. */
	private var flashUntil: Int = 0

	/** How many times the player was told, for the smoke test. */
	@JvmField
	var notifications: Int = 0

	private var context: PluginContext? = null

	private val tickListener: EventListener<GameTick> = EventListener { _ -> tick() }

	private val stateListener: EventListener<GameStateChanged> = EventListener { event ->
		if (event.previous === GameState.LOGGED_IN) {
			reset()
		}
	}

	private val animationListener: EventListener<AnimationChanged> = EventListener { event ->
		if (isLocalPlayer(event.actor)) {
			detector.animationChanged(event.animationId)
		}
	}

	private val interactingListener: EventListener<Actor> = EventListener { actor ->
		if (isLocalPlayer(actor)) {
			detector.interactingChanged(interactKind(actor.interacting()))
		}
	}

	private val hitsplatListener: EventListener<HitsplatApplied> = EventListener { event ->
		if (isLocalPlayer(event.actor)) {
			detector.hitsplatOnPlayer()
		}
	}

	private val keyListener: EventListener<KeyPressed> = EventListener { _ -> detector.keyPressed() }

	private val clickListener: EventListener<MouseClicked> = EventListener { _ -> detector.mousePressed() }

	private val wheelListener: EventListener<MouseWheel> = EventListener { _ -> detector.mousePressed() }

	private val overlay: Overlay = Overlay { g, view ->
		val client: ClientApi = context?.client() ?: return@Overlay
		if (client.clientCycle() < flashUntil) {
			for (i in 0 until FRAME_WIDTH) {
				g.drawRect(view.x + i, view.y + i, view.width - 2 * i, view.height - 2 * i, FRAME_COLOUR)
			}
		}
	}

	private val command: CommandHandler = CommandHandler { _, args -> command(args) }

	/** The plugin manager changed a setting: read them back and start the timers again. */
	private val settingListener: EventListener<SettingChanged> = EventListener { event ->
		val context: PluginContext? = this.context
		if (event.pluginId == DESCRIPTOR.id && context != null) {
			readSettings(context.config())
			detector.resetTimers(-1, false)
		}
	}

	override fun start(context: PluginContext) {
		this.context = context
		val settings: SettingsSchema = context.settings()
		settings.toggle("enabled", "Idle notifier", false)
		settings.number("seconds", "Idle notification delay (seconds)", IdleDetector.DEFAULT_DELAY_SECONDS, MIN_SECONDS, MAX_SECONDS, 1)
		settings.toggle("animationIdle", "Idle animation notifications", true)
		settings.toggle("interactionIdle", "Idle interaction notifications (combat, fishing)", true)
		settings.toggle("movementIdle", "Idle movement notifications", false)
		settings.toggle("inputIdle", "Idle input notification (no mouse or keyboard)", true)
		settings.number("inputIdleMinutes", "Minutes without input before the warning", DEFAULT_INPUT_IDLE_MINUTES, 1, MAX_INPUT_IDLE_MINUTES, 1)
		settings.toggle("hitpointsNotification", "Life points notification", false)
		settings.number("hitpoints", "Life points threshold", DEFAULT_LIFE_POINTS, LIFE_POINTS_PER_LEVEL, MAX_LIFE_POINTS, LIFE_POINTS_PER_LEVEL)
		settings.toggle("prayerNotification", "Prayer notification", false)
		settings.number("prayer", "Prayer threshold", 1, 1, MAX_PRAYER_POINTS, 1)
		settings.toggle("lowEnergyNotification", "Low energy notification", false)
		settings.number("lowEnergy", "Low energy threshold (%)", 0, 0, MAX_PERCENT - 1, 1)
		settings.toggle("highEnergyNotification", "High energy notification", false)
		settings.number("highEnergy", "High energy threshold (%)", MAX_PERCENT, 1, MAX_PERCENT, 1)
		settings.toggle("specNotification", "Special attack notification", false)
		settings.number("spec", "Special attack threshold (%)", MAX_PERCENT, 1, MAX_PERCENT, 1)
		settings.toggle("styleChat", "Notify: chat line", true)
		settings.toggle("styleFrame", "Notify: red frame", true)
		settings.toggle("styleSound", "Notify: sound effect", false)
		settings.number("soundId", "Sound effect id", DEFAULT_SOUND, 0, MAX_SOUND, 1)
		settings.toggle("styleDesktop", "Notify: desktop notification", true)
		settings.toggle("styleFocus", "Notify: bring the window to the front", false)
		settings.toggle("onlyUnfocused", "Only when the window is unfocused", true)
		readSettings(context.config())
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		context.events().subscribe(Events.GAME_TICK, tickListener)
		context.events().subscribe(Events.GAME_STATE_CHANGED, stateListener)
		context.events().subscribe(Events.ANIMATION_CHANGED, animationListener)
		context.events().subscribe(Events.INTERACTING_CHANGED, interactingListener)
		context.events().subscribe(Events.HITSPLAT_APPLIED, hitsplatListener)
		context.events().subscribe(Events.KEY_PRESSED, keyListener)
		context.events().subscribe(Events.MOUSE_CLICKED, clickListener)
		context.events().subscribe(Events.MOUSE_WHEEL, wheelListener)
		context.addOverlay(overlay)
		context.registerCommand("idle", command)
	}

	override fun stop() {
		context = null
		reset()
	}

	private fun readSettings(config: PluginConfig) {
		enabled = config.getBoolean("enabled", false)
		waitSeconds = clampSeconds(config.getInt("seconds", IdleDetector.DEFAULT_DELAY_SECONDS))
		detector.delayCycles = waitSeconds * IdleDetector.CYCLES_PER_SECOND
		detector.animationIdle = config.getBoolean("animationIdle", true)
		detector.interactionIdle = config.getBoolean("interactionIdle", true)
		detector.movementIdle = config.getBoolean("movementIdle", false)
		inputIdleMinutes = clamp(config.getInt("inputIdleMinutes", DEFAULT_INPUT_IDLE_MINUTES), 1, MAX_INPUT_IDLE_MINUTES)
		detector.inputIdleCycles = if (config.getBoolean("inputIdle", true)) inputIdleMinutes * CYCLES_PER_MINUTE else 0
		detector.hitpointsThreshold = if (config.getBoolean("hitpointsNotification", false)) clamp(config.getInt("hitpoints", DEFAULT_LIFE_POINTS), 1, MAX_LIFE_POINTS) else 0
		detector.prayerThreshold = if (config.getBoolean("prayerNotification", false)) clamp(config.getInt("prayer", 1), 1, MAX_PRAYER_POINTS) else 0
		detector.lowEnergyThreshold = if (config.getBoolean("lowEnergyNotification", false)) clamp(config.getInt("lowEnergy", 0), 0, MAX_PERCENT - 1) else 0
		detector.highEnergyThreshold = if (config.getBoolean("highEnergyNotification", false)) clamp(config.getInt("highEnergy", MAX_PERCENT), 1, MAX_PERCENT) else 0
		detector.specThreshold = if (config.getBoolean("specNotification", false)) clamp(config.getInt("spec", MAX_PERCENT), 1, MAX_PERCENT) * LIFE_POINTS_PER_LEVEL else 0
		styleChat = config.getBoolean("styleChat", true)
		styleFrame = config.getBoolean("styleFrame", true)
		styleSound = config.getBoolean("styleSound", false)
		soundId = clamp(config.getInt("soundId", DEFAULT_SOUND), 0, MAX_SOUND)
		styleDesktop = config.getBoolean("styleDesktop", true)
		styleFocus = config.getBoolean("styleFocus", false)
		onlyUnfocused = config.getBoolean("onlyUnfocused", true)
	}

	private fun clamp(value: Int, min: Int, max: Int): Int {
		return if (value < min) min else if (value > max) max else value
	}

	private fun clampSeconds(seconds: Int): Int {
		return clamp(seconds, MIN_SECONDS, MAX_SECONDS)
	}

	private fun command(args: Array<String>) {
		val context: PluginContext = this.context ?: return
		detector.resetTimers(-1, false) // whatever was said before, the next idle spell is announced
		if (args.isEmpty()) {
			enabled = !enabled
			context.config().setBoolean("enabled", enabled)
			context.config().save()
			context.client().consolePrint(if (enabled) "Idle notifier on: " + waitSeconds + " seconds." else "Idle notifier off.")
			return
		}
		val seconds: Int? = args[0].toIntOrNull()
		if (seconds == null) {
			context.client().consolePrint("Usage: idle (on or off), or idle <seconds>")
			return
		}
		waitSeconds = clampSeconds(seconds)
		detector.delayCycles = waitSeconds * IdleDetector.CYCLES_PER_SECOND
		enabled = true
		context.config().setInt("seconds", waitSeconds)
		context.config().setBoolean("enabled", true)
		context.config().save()
		context.client().consolePrint("Idle notifier on: " + waitSeconds + " seconds.")
	}

	private fun isLocalPlayer(actor: Actor?): Boolean {
		val client: ClientApi = context?.client() ?: return false
		return actor != null && actor === client.localPlayer()
	}

	/** What kind of target [actor] is to the idle rules: an npc that can be attacked, a fishing spot, or anything else. */
	private fun interactKind(actor: Actor?): Int {
		if (actor == null) {
			return IdleDetector.INTERACT_NONE
		}
		val npc: Npc = actor as? Npc ?: return IdleDetector.INTERACT_OTHER
		val type: NpcDefinition = npc.type() ?: return IdleDetector.INTERACT_OTHER
		for (i in 0 until NPC_ACTIONS) {
			if (type.action(i) == "Attack") {
				return IdleDetector.INTERACT_COMBAT
			}
		}
		val name: String? = type.name()
		if (name != null && name.contains("Fishing spot")) {
			return IdleDetector.INTERACT_FISHING
		}
		return IdleDetector.INTERACT_OTHER
	}

	/** Each client cycle: what the detector makes of the player, said the ways the settings say. */
	private fun tick() {
		val client: ClientApi = context?.client() ?: return
		if (client.gameState() !== GameState.LOGGED_IN) {
			return
		}
		val player: Actor = client.localPlayer() ?: return
		val mouseX: Int = client.mouseX()
		val mouseY: Int = client.mouseY()
		if (mouseX != lastMouseX || mouseY != lastMouseY) {
			if (lastMouseX != -1 || lastMouseY != -1) {
				detector.mouseMoved()
			}
			lastMouseX = mouseX
			lastMouseY = mouseY
		}
		val mouseHeld: Boolean = client.isMouseButtonDown(0) || client.isMouseButtonDown(1) || client.isMouseButtonDown(2)
		var hitpoints: Int = client.varbit(LIFE_POINTS_VARBIT)
		if (hitpoints <= 0) {
			hitpoints = client.skillLevel(Skill.CONSTITUTION) * LIFE_POINTS_PER_LEVEL
		}
		val baseHitpoints: Int = client.skillBaseLevel(Skill.CONSTITUTION) * LIFE_POINTS_PER_LEVEL
		val bits: Int = detector.cycle(
			player.animationId(),
			player.interacting() != null,
			player.tileX(),
			player.tileY(),
			mouseHeld,
			hitpoints,
			baseHitpoints,
			client.skillLevel(Skill.PRAYER),
			client.skillBaseLevel(Skill.PRAYER),
			client.runEnergy(),
			client.varp(SPECIAL_ATTACK_VARP),
		)
		if (bits == 0 || !enabled) {
			return
		}
		if ((bits and IdleDetector.INPUT_IDLE) != 0) {
			announce(client, "You have not touched the mouse or keyboard for " + inputIdleMinutes + (if (inputIdleMinutes == 1) " minute!" else " minutes!"))
		}
		if ((bits and IdleDetector.ANIMATION) != 0) {
			announce(client, "You are now idle!")
		}
		if ((bits and IdleDetector.MOVEMENT) != 0) {
			announce(client, "You have stopped moving!")
		}
		if ((bits and IdleDetector.OUT_OF_COMBAT) != 0) {
			announce(client, "You are now out of combat!")
		}
		if ((bits and IdleDetector.INTERACTION) != 0) {
			announce(client, "You are now idle!")
		}
		if ((bits and IdleDetector.HITPOINTS) != 0) {
			announce(client, "You have low life points!")
		}
		if ((bits and IdleDetector.PRAYER) != 0) {
			announce(client, "You have low prayer points!")
		}
		if ((bits and IdleDetector.LOW_ENERGY) != 0) {
			announce(client, "You have low run energy!")
		}
		if ((bits and IdleDetector.HIGH_ENERGY) != 0) {
			announce(client, "You have reached your run energy threshold!")
		}
		if ((bits and IdleDetector.SPEC) != 0) {
			announce(client, "You have restored special attack energy!")
		}
	}

	/** Says [message] every way the settings ask, unless the window is focused and that is to keep it quiet. */
	private fun announce(client: ClientApi, message: String) {
		if (onlyUnfocused && client.isFocused()) {
			context?.log(message + " (not said: the window is focused; turn off \"Only when the window is unfocused\" to hear it anyway)")
			return
		}
		notifications++
		if (styleChat) {
			client.chatMessage(message)
		}
		if (styleFrame) {
			flashUntil = client.clientCycle() + FLASH_CYCLES
		}
		if (styleSound && soundId > 0) {
			client.playSound(soundId, 255)
		}
		val shown: Boolean = styleDesktop && client.notify(message)
		if (styleFocus) {
			client.requestFocus()
		}
		context?.log(message + " (cycle " + client.clientCycle() + (if (styleDesktop) (if (shown) ", on the desktop" else ", the desktop could not show it") else "") + ")")
	}

	private fun reset() {
		detector.reset()
		lastMouseX = -1
		lastMouseY = -1
		flashUntil = 0
	}
}
