package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.InterfaceVisitor
import com.jagex.game.runetek5.plugin.api.Plugin
import com.jagex.game.runetek5.plugin.api.PluginApi
import com.jagex.game.runetek5.plugin.api.PluginConfig
import com.jagex.game.runetek5.plugin.api.PluginContext
import com.jagex.game.runetek5.plugin.api.PluginDescriptor
import com.jagex.game.runetek5.plugin.api.SettingsSchema
import com.jagex.game.runetek5.plugin.api.Widget
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.GameStateChanged
import com.jagex.game.runetek5.plugin.api.events.GameTick
import com.jagex.game.runetek5.plugin.api.events.KeyInput
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: RuneLite's Key Remapping. Until Enter (or / or :) is pressed the chat
 * box reads "Press Enter to Chat..." and takes no typing, so the keys are free for other things: W, A, S
 * and D (or keys of the player's choosing) turn the camera as the arrow keys do, 1 to 0, - and = open the
 * tabs as F1 to F12 do, and keys can stand for Escape, Space (in a dialogue, to continue) and Control.
 * Enter sends the line and locks the chat again, as do Escape (which also drops it) and Backspace on an
 * empty line. The keys are changed before the client reads them (KeyInput, API 20), so the game sees the
 * arrow or F key itself. The chat box's typed line is client variable 1 and is shown in 137:55. A
 * built-in plugin (BuiltinPlugins, docs/PLUGINS.md).
 *
 * 634 opens Quick Chat on Enter with an empty line; the plugin keeps the Enter that unlocks the chat, and
 * one on an empty line while typing, from the game, so with the plugin on Quick Chat is not opened by Enter.
 */
object KeyRemapping : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("keyremapping", "Key Remapping", "1", PluginApi.VERSION, "Locks the chat until you press Enter, so W, A, S, D can turn the camera and the number keys can open the tabs.", "Pixel")

	/** The client's codes (KeyCode) of the keys the plugin maps to and reads. */
	const val UP: Int = 98
	const val DOWN: Int = 99
	const val LEFT: Int = 96
	const val RIGHT: Int = 97
	const val ESCAPE: Int = 13
	const val SPACE: Int = 83
	const val ENTER: Int = 84
	const val BACKSPACE: Int = 85
	const val CONTROL: Int = 82

	/** RuneLite's defaults: W, S, A, D; 1 to 9, 0, - and = for F1 to F12. */
	const val W: Int = 33
	const val S: Int = 49
	const val A: Int = 48
	const val D: Int = 50

	@JvmField
	val DEFAULT_F_KEYS: IntArray = intArrayOf(16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27)

	/** The chat box and its typed line (137:55), the typed text's client variable, and where dialogues open (752:12 and 752:13). */
	const val CHATBOX: Int = 137
	const val CHATBOX_INPUT: Int = 55
	const val CHAT_INPUT_VARC: Int = 1
	@JvmField val DIALOGUE_PARENTS: IntArray = intArrayOf((752 shl 16) or 12, (752 shl 16) or 13)

	/** The option menus (Void's dialogue_multi*): Space and Control are left alone in them. */
	@JvmField val OPTION_DIALOGUES: IntArray = intArrayOf(228, 229, 230, 231, 232, 233, 234, 235, 236, 237, 238, 451, 458, 557)

	const val PRESS_ENTER_TO_CHAT: String = "Press Enter to Chat..."

	@JvmField var enabled: Boolean = false
	@JvmField var cameraRemap: Boolean = true
	@JvmField var up: Int = W
	@JvmField var down: Int = S
	@JvmField var left: Int = A
	@JvmField var right: Int = D
	@JvmField var fkeyRemap: Boolean = false
	@JvmField val fKeys: IntArray = DEFAULT_F_KEYS.copyOf()
	@JvmField var esc: Int = ESCAPE
	@JvmField var space: Int = SPACE
	@JvmField var control: Int = 0

	/** Whether the player is typing in the chat (it is unlocked). */
	@JvmField var typing: Boolean = false

	/** The chat is to be locked at the next tick (after Enter sent the line). */
	private var lockNext: Boolean = false

	/** What each key held down was remapped to, for its release; 0 for none. */
	private val pressedAs: IntArray = IntArray(MAX_KEY)

	/** What the open interfaces say, worked out each tick. */
	@JvmField var dialogOpen: Boolean = false
	@JvmField var optionsDialogOpen: Boolean = false

	private var context: PluginContext? = null

	private val inputListener: EventListener<KeyInput> = EventListener { event -> context?.client()?.let { keyInput(it, event) } }

	private val tickListener: EventListener<GameTick> = EventListener { _ -> context?.client()?.let { tick(it) } }

	private val stateListener: EventListener<GameStateChanged> = EventListener { event ->
		if (event.state === GameState.LOGGED_IN) {
			typing = false
		}
	}

	private val settingListener: EventListener<SettingChanged> = EventListener { event ->
		if (event.pluginId == DESCRIPTOR.id) {
			val wasEnabled: Boolean = enabled
			context?.config()?.let { readSettings(it) }
			if (wasEnabled && !enabled) {
				context?.client()?.let { unlockChat(it) }
			}
		}
	}

	private val toggle: CommandHandler = CommandHandler { _, _ ->
		enabled = !enabled
		context?.config()?.setBoolean("enabled", enabled)
		context?.config()?.save()
		if (!enabled) {
			context?.client()?.let { unlockChat(it) }
		}
		context?.client()?.consolePrint(if (enabled) "Key remapping enabled." else "Key remapping disabled.")
	}

	override fun start(context: PluginContext) {
		this.context = context
		val s: SettingsSchema = context.settings()
		s.toggle("enabled", "Key remapping", false)
		// RuneLite's KeyRemappingConfig, in its order: camera, then the F-keys, then Space and Control
		s.toggle("cameraRemap", "Remap camera", true)
		s.keybind("up", "Camera up key", W)
		s.keybind("down", "Camera down key", S)
		s.keybind("left", "Camera left key", A)
		s.keybind("right", "Camera right key", D)
		s.toggle("fkeyRemap", "Remap F-keys", false)
		for (i in 0 until 12) {
			s.keybind("f" + (i + 1), "F" + (i + 1), DEFAULT_F_KEYS[i])
		}
		s.keybind("esc", "ESC", ESCAPE)
		s.keybind("space", "Space", SPACE)
		s.keybind("control", "Control", 0)
		readSettings(context.config())
		typing = false
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		context.events().subscribe(Events.KEY_INPUT, inputListener)
		context.events().subscribe(Events.GAME_TICK, tickListener)
		context.events().subscribe(Events.GAME_STATE_CHANGED, stateListener)
		context.registerCommand("keyremapping", toggle)
	}

	override fun stop() {
		context?.client()?.let { unlockChat(it) }
		context = null
		pressedAs.fill(0)
	}

	@JvmStatic
	fun readSettings(config: PluginConfig) {
		enabled = config.getBoolean("enabled", false)
		cameraRemap = config.getBoolean("cameraRemap", true)
		up = config.getInt("up", W)
		down = config.getInt("down", S)
		left = config.getInt("left", A)
		right = config.getInt("right", D)
		fkeyRemap = config.getBoolean("fkeyRemap", false)
		for (i in 0 until 12) {
			fKeys[i] = config.getInt("f" + (i + 1), DEFAULT_F_KEYS[i])
		}
		esc = config.getInt("esc", ESCAPE)
		space = config.getInt("space", SPACE)
		control = config.getInt("control", 0)
	}

	/**
	 * RuneLite's KeyRemappingListener for a key: while the chat is locked, a bound key becomes the key it
	 * stands for and characters are dropped (Enter, / and : unlock it); while typing, Escape drops the line,
	 * and Enter or Backspace on an empty line lock it again. A remapped key's release is remapped too.
	 */
	@JvmStatic
	fun keyInput(client: ClientApi, event: KeyInput) {
		if (event.type == KeyInput.RELEASED) {
			val code: Int = event.keyCode
			if (code in 0 until MAX_KEY && pressedAs[code] != 0) {
				event.keyCode = pressedAs[code]
				pressedAs[code] = 0
			}
			return
		}
		if (!enabled || !chatboxFocused(client)) {
			return
		}
		if (typing) {
			typingKey(client, event)
		} else if (event.type == KeyInput.PRESSED) {
			lockedPress(client, event)
		} else if (event.keyChar == '/' || event.keyChar == ':') {
			// the start of a command or a clan line: the character goes into the chat
			typing = true
			unlockChat(client)
		} else {
			event.consume()
		}
	}

	private fun lockedPress(client: ClientApi, event: KeyInput) {
		val code: Int = event.keyCode
		val mapped: Int = remap(code)
		if (mapped != 0) {
			if (code in 0 until MAX_KEY) {
				pressedAs[code] = mapped
			}
			event.keyCode = mapped
			return
		}
		if (code == ENTER) {
			// the Enter is the plugin's: on an empty line the game would open Quick Chat
			event.consume()
			typing = true
			unlockChat(client)
		}
	}

	/** The key [code] stands for while the chat is locked, or 0 for itself. */
	@JvmStatic
	fun remap(code: Int): Int {
		if (code == 0) {
			return 0
		}
		if (cameraRemap) {
			when (code) {
				up -> return UP
				down -> return DOWN
				left -> return LEFT
				right -> return RIGHT
			}
		}
		if (fkeyRemap && !dialogOpen) {
			for (i in 0 until 12) {
				if (fKeys[i] == code) {
					return i + 1
				}
			}
			if (code == esc && esc != ESCAPE) {
				return ESCAPE
			}
		}
		if (dialogOpen && !optionsDialogOpen && code == space && space != SPACE) {
			return SPACE
		}
		if (control != 0 && code == control && !optionsDialogOpen) {
			return CONTROL
		}
		return 0
	}

	private fun typingKey(client: ClientApi, event: KeyInput) {
		if (event.type != KeyInput.PRESSED) {
			return
		}
		when (event.keyCode) {
			ESCAPE -> {
				event.consume()
				typing = false
				client.setVarcString(CHAT_INPUT_VARC, "")
				lockChat(client)
			}
			ENTER -> {
				if (client.varcString(CHAT_INPUT_VARC).isNullOrEmpty()) {
					// nothing to send: the chat locks (the game would open Quick Chat)
					event.consume()
					typing = false
					lockChat(client)
				} else {
					// the line is sent by this Enter; the chat locks once it has gone
					typing = false
					lockNext = true
				}
			}
			BACKSPACE -> {
				if ((client.varcString(CHAT_INPUT_VARC)?.length ?: 0) <= 1) {
					typing = false
					lockNext = true
				}
			}
		}
	}

	/** Each client cycle: the dialogues open, and the locked chat's line kept reading "Press Enter to Chat...". */
	@JvmStatic
	fun tick(client: ClientApi) {
		if (!enabled || client.gameState() !== GameState.LOGGED_IN) {
			return
		}
		readDialogues(client)
		if (lockNext) {
			lockNext = false
			typing = false
		}
		if (!typing && chatboxFocused(client)) {
			lockChat(client)
		}
	}

	private fun readDialogues(client: ClientApi) {
		var dialogue = false
		var options = false
		client.openInterfaces(
			InterfaceVisitor { interfaceId, parentWidgetId, _ ->
				if (parentWidgetId in DIALOGUE_PARENTS) {
					dialogue = true
					if (interfaceId in OPTION_DIALOGUES) {
						options = true
					}
				}
			},
		)
		dialogOpen = dialogue
		optionsDialogOpen = options
	}

	/**
	 * Whether the chat box's line is the player's chat (and not, say, an amount being asked for): its
	 * line shows the name and a colon, or the plugin's own text.
	 */
	@JvmStatic
	fun chatboxFocused(client: ClientApi): Boolean {
		val input: Widget = client.widget(CHATBOX, CHATBOX_INPUT) ?: return false
		if (input.isHidden()) {
			return false
		}
		return prefix(input.text() ?: return false) != null
	}

	/**
	 * The start of the chat line up to the typed text: the name, ": " and the typed text's colour tag;
	 * null for a line that is not chat.
	 */
	@JvmStatic
	fun prefix(text: String): String? {
		val colon: Int = text.indexOf(": ")
		if (colon <= 0) {
			return null
		}
		var end: Int = colon + 2
		if (text.startsWith("<col=", end)) {
			val close: Int = text.indexOf('>', end)
			if (close > 0) {
				end = close + 1
			}
		}
		return text.substring(0, end)
	}

	/** RuneLite's lockChat: the line reads "Press Enter to Chat...". */
	@JvmStatic
	fun lockChat(client: ClientApi) {
		val input: Widget = client.widget(CHATBOX, CHATBOX_INPUT) ?: return
		val start: String = prefix(input.text() ?: return) ?: return
		val locked: String = start + PRESS_ENTER_TO_CHAT
		if (input.text() != locked) {
			input.setText(locked)
		}
	}

	/** RuneLite's unlockChat: the line shows what is typed again. */
	@JvmStatic
	fun unlockChat(client: ClientApi) {
		val input: Widget = client.widget(CHATBOX, CHATBOX_INPUT) ?: return
		val text: String = input.text() ?: return
		if (!text.endsWith(PRESS_ENTER_TO_CHAT)) {
			return
		}
		val start: String = prefix(text) ?: return
		input.setText(start + (client.varcString(CHAT_INPUT_VARC) ?: ""))
	}

	private const val MAX_KEY: Int = 112
}
