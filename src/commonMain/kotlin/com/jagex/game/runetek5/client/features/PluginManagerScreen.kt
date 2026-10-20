package com.jagex.game.runetek5.client.features

import SubInterfaceCloser
import SubInterfaceOpener
import com.jagex.game.runetek5.InterfaceManager
import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.client.input.console.Console
import com.jagex.game.runetek5.config.iftype.SubInterfaceType
import com.jagex.game.runetek5.config.rsinterface.Component
import com.jagex.game.runetek5.config.rsinterface.HookRequest
import com.jagex.game.runetek5.config.rsinterface.Interface
import com.jagex.game.runetek5.config.rsinterface.SubInterface
import com.jagex.game.runetek5.graphics.Toolkit
import com.jagex.game.runetek5.graphics.sprite.NativeSprite
import com.jagex.game.runetek5.core.constants.MainLogicStep
import com.jagex.game.runetek5.plugin.PluginContextImpl
import com.jagex.game.runetek5.plugin.PluginRegistration
import com.jagex.game.runetek5.plugin.PluginRuntime
import com.jagex.game.runetek5.plugin.PluginSchemaImpl
import com.jagex.game.runetek5.plugin.api.PluginConfig
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: the plugin manager, a window of the client's own built in the
 * Graphics Options window's shell (interface 742, opened by the client with its scripts suppressed,
 * its title set to "Plugins" and its close button hooked), listing every plugin with an on/off
 * switch and, for one that declared settings (plugin.api.SettingsSchema), a page of them. Opened
 * by the Plugins button in the Settings tab (interface 261, where [tick] keeps it beside Graphics
 * and Audio Settings, those two moved to the three-button pitch of the rows below; its icon is a
 * sprite of the client's own, put into the component sprite cache under an id no cache sprite has)
 * or `::pluginmanager`. Built from [ClientPage]'s pieces
 * as flat children of the window's content layer (742:2); hooks carry [SENTINEL], which
 * CS2Interpreter.executeHookInternal routes to [hook]. The server knows nothing of the window: one
 * it opens in the same slot replaces it, and the game goes on.
 */
object PluginManagerScreen {

	/** The script id of our hooks: never a cache script. The second element is the action, the third its index. */
	const val SENTINEL: Int = -635

	private const val WINDOW: Int = 742

	private const val ROOT: Int = (742 shl 16)

	private const val BODY: Int = (742 shl 16) or 2

	private const val CLOSE: Int = (742 shl 16) or 14

	private const val TITLE: Int = (742 shl 16) or 15

	private const val POPUP: Int = (742 shl 16) or 16

	private const val POPUP_ITEMS: Int = (742 shl 16) or 21

	private const val POPUP_SCROLL: Int = (742 shl 16) or 22

	/** The Settings tab and its two stock buttons with their icons. */
	private const val TAB_ROOT: Int = 261 shl 16

	private const val TAB_GRAPHICS: Int = (261 shl 16) or 16

	private const val TAB_GRAPHICS_ICON: Int = (261 shl 16) or 17

	private const val TAB_AUDIO: Int = (261 shl 16) or 18

	private const val TAB_AUDIO_ICON: Int = (261 shl 16) or 19

	/** The three-button pitch of the tab's other rows, and the row the buttons sit on. */
	private const val TAB_X_GRAPHICS: Int = 19

	private const val TAB_X_AUDIO: Int = 75

	private const val TAB_X_PLUGINS: Int = 131

	private const val TAB_Y: Int = 41

	/** The stock button background. */
	private const val TAB_BUTTON: Int = 761

	/** The icon's sprite id: none the cache has, so the component sprite cache is the only place it is found. */
	private const val ICON_SPRITE: Int = 0x7FFF027B

	private const val ICON_RESOURCE: String = "/plugins/icon.rgba"

	/** The window's height while ours (the Custom page's), and its content layer's width. */
	private const val HEIGHT: Int = 334

	private const val WIDTH: Int = 501

	private const val ROW_PITCH: Int = 28

	private const val ROWS_PER_PAGE: Int = 9

	private const val SETTING_PITCH: Int = 24

	private const val DIM: Int = 0x9f9684

	private const val ON: Int = ClientPage.TEXT_TOP

	private const val OFF: Int = 0xc06040

	private const val FAILED: Int = 0xe05050

	// actions
	private const val A_OPEN: Int = 1
	private const val A_CLOSE: Int = 2
	private const val A_TOGGLE: Int = 3
	private const val A_SETTINGS: Int = 4
	private const val A_BACK: Int = 5
	private const val A_PREV: Int = 6
	private const val A_NEXT: Int = 7
	private const val A_HOVER: Int = 8
	private const val A_LEAVE: Int = 9
	private const val A_SET_TOGGLE: Int = 10
	private const val A_SET_MINUS: Int = 11
	private const val A_SET_PLUS: Int = 12
	private const val A_SET_CHOICE: Int = 13
	private const val A_ITEM: Int = 14
	private const val A_POPUP_CLOSE: Int = 15

	/** Whether the window is open with our page in it. */
	@JvmField
	var open: Boolean = false

	/** The plugin whose settings page is shown, or null for the list. */
	private var selected: PluginRegistration? = null

	/** The list's page, 0 up. */
	private var page: Int = 0

	private val buttons: ArrayList<ClientPage.Button> = ArrayList()

	/** A choice setting's dropdown on the settings page: its entry, the hit component and the slots of its outline, arrow and value. */
	private class Choice(@JvmField val entry: PluginSchemaImpl.Entry, @JvmField val hit: Component, @JvmField val outline: Int, @JvmField val arrow: Int)

	private val choices: ArrayList<Choice> = ArrayList()

	private val popup: ClientPage.Popup = ClientPage.Popup(ROOT, BODY, POPUP, POPUP_ITEMS, POPUP_SCROLL, SENTINEL, A_ITEM, A_POPUP_CLOSE)

	private var popupFor: Choice? = null

	/** The Plugins button's hit component in the Settings tab. */
	private var tabButton: Component? = null

	private var icon: NativeSprite? = null

	private var iconToolkit: Toolkit? = null

	/** False once the icon resource was looked for and not found (the browser): the button says "Plugins" instead. */
	private var iconAvailable: Boolean = true

	// --- the Settings tab ---

	/**
	 * Each game tick while the Settings tab is loaded: the icon in the sprite cache (the caches are
	 * emptied at logout and map builds), Graphics and Audio Settings at the three-button pitch, and the
	 * Plugins button after them, appended again whenever the tab's own scripts have rebuilt its children.
	 */
	@JvmStatic
	fun tick() {
		val root: Component? = Interface.peekComponent(TAB_ROOT)
		if (root == null) {
			tabButton = null
			return
		}
		ensureIcon()
		var moved: Boolean = moveTo(TAB_GRAPHICS, TAB_X_GRAPHICS)
		moved = moveTo(TAB_GRAPHICS_ICON, TAB_X_GRAPHICS) || moved
		moved = moveTo(TAB_AUDIO, TAB_X_AUDIO) || moved
		moved = moveTo(TAB_AUDIO_ICON, TAB_X_AUDIO + 4) || moved
		val present: Component? = tabButton
		val children: Array<Component?>? = root.slotChildren
		if (present != null && children != null && present.slotId < children.size && children[present.slotId] === present) {
			if (moved) {
				Component.layoutChildren(false, root)
				Component.redrawComponent(root)
			}
			return
		}
		val list: ArrayList<Component> = ArrayList()
		if (children != null) {
			for (c in children) {
				if (c != null) {
					list.add(c)
				}
			}
		}
		list.add(ClientPage.sprite(TAB_ROOT, list.size, TAB_X_PLUGINS, TAB_Y, 40, 40, TAB_BUTTON, false, false, false))
		if (iconAvailable) {
			list.add(ClientPage.sprite(TAB_ROOT, list.size, TAB_X_PLUGINS + 4, TAB_Y + 4, 32, 32, ICON_SPRITE, false, false, false))
		}
		val hit: Component = ClientPage.text(TAB_ROOT, list.size, TAB_X_PLUGINS, TAB_Y, 40, 40, ClientPage.FONT_SMALL, if (iconAvailable) "" else "Plugins", ClientPage.TEXT, 1, 1, true)
		hit.hasScript = true
		hit.addOptionName(0, "Plugins")
		hit.opBase = ""
		hit.params = arrayOf<Any?>(SENTINEL, A_OPEN, 0)
		list.add(hit)
		root.slotChildren = list.toTypedArray<Component?>()
		tabButton = hit
		Component.layoutChildren(false, root)
		Component.redrawComponent(root)
	}

	/** Puts the stock component at [x] in its parent; true when it was elsewhere. */
	private fun moveTo(hash: Int, x: Int): Boolean {
		val c: Component = Interface.peekComponent(hash) ?: return false
		if (c.basePositionX == x && c.aspectXType.toInt() == 0) {
			return false
		}
		c.basePositionX = x
		c.aspectXType = 0
		return true
	}

	/** The icon, made from the client's own resource for the active toolkit, in the component sprite cache under [ICON_SPRITE]. */
	private fun ensureIcon() {
		if (!iconAvailable) {
			return
		}
		val toolkit: Toolkit = ClientState.activeToolkit ?: return
		if (Component.NATIVE_SPRITE_CACHE.get(ICON_SPRITE.toLong()) != null) {
			return
		}
		if (icon == null || iconToolkit !== toolkit) {
			icon = loadIcon(toolkit)
			iconToolkit = toolkit
			if (icon == null) {
				iconAvailable = false
				tabButton = null // built again with the text instead
				return
			}
		}
		val sprite: NativeSprite = icon ?: return
		Component.NATIVE_SPRITE_CACHE.put(sprite, ICON_SPRITE.toLong(), sprite.getWidth() * sprite.getHeight() * 4)
	}

	/** The resource is two big-endian ints, width and height, then width * height ARGB pixels. */
	private fun loadIcon(toolkit: Toolkit): NativeSprite? {
		val bytes: ByteArray = lang.readResource(ICON_RESOURCE) ?: return null
		if (bytes.size < 8) {
			return null
		}
		val width: Int = int(bytes, 0)
		val height: Int = int(bytes, 4)
		if (width <= 0 || height <= 0 || width > 64 || height > 64 || bytes.size < 8 + width * height * 4) {
			return null
		}
		val pixels: IntArray = IntArray(width * height)
		for (i in 0 until width * height) {
			pixels[i] = int(bytes, 8 + i * 4)
		}
		return toolkit.createNativeSprite(pixels, width, width, height, false)
	}

	private fun int(bytes: ByteArray, at: Int): Int {
		return ((bytes[at].toInt() and 0xFF) shl 24) or ((bytes[at + 1].toInt() and 0xFF) shl 16) or ((bytes[at + 2].toInt() and 0xFF) shl 8) or (bytes[at + 3].toInt() and 0xFF)
	}

	/** The Plugins button's hit component in the Settings tab, for the smoke test; null when the tab is not loaded. */
	@JvmStatic
	fun tabButton(): Component? {
		return tabButton
	}

	// --- opening and closing ---

	/** `::pluginmanager`: the window, or closed again. */
	@JvmStatic
	fun openFromCommand() {
		if (open) {
			close()
			Console.printToConsole("Plugin manager closed")
		} else {
			val why: String? = open()
			Console.printToConsole(why ?: "Plugin manager open")
		}
	}

	/** The main-screen slot the server opens such windows in: 548:18 fixed, 746:8 resizable. */
	private fun modalSlot(): Int {
		val slot: Int = if (ClientState.BASE_WINDOW_ID == 548) 18 else 8
		return (ClientState.BASE_WINDOW_ID shl 16) or slot
	}

	/** Opens the window with the list; null when it opened, else why not. */
	@JvmStatic
	fun open(): String? {
		if (ClientState.mainLogicStep !== MainLogicStep.STEP_GAME_SCREEN) {
			return "The plugin manager opens in the game"
		}
		if (open) {
			return null
		}
		val parent: Int = modalSlot()
		if (InterfaceManager.OPEN_INTERFACES.get(parent.toLong()) != null) {
			return "Close the open window first"
		}
		SubInterfaceOpener.openSubInterface(WINDOW, parent, SubInterfaceType.MODAL, true) ?: return "The window cannot be opened"
		val root: Component = Interface.getComponent(ROOT) ?: return "The window has no root"
		root.baseHeight = HEIGHT
		root.aspectHeightType = 0
		Component.redrawComponent(root)
		InterfaceManager.relayoutAgainstParent(root)
		Component.layoutChildren(false, root)
		val title: Component? = Interface.getComponent(TITLE)
		if (title != null) {
			title.text = "Plugins"
			Component.redrawComponent(title)
		}
		val closeButton: Component? = Interface.getComponent(CLOSE)
		if (closeButton != null) {
			// the load script would have given it its click; ours closes the window on the spot
			closeButton.hasScript = true
			closeButton.addOptionName(0, "Close")
			closeButton.params = arrayOf<Any?>(SENTINEL, A_CLOSE, 0)
		}
		open = true
		selected = null
		page = 0
		rebuild()
		return null
	}

	/** Closes the window if it is ours. */
	@JvmStatic
	fun close() {
		if (!open) {
			return
		}
		closePopup()
		val parent: Int = modalSlot()
		val sub: SubInterface? = InterfaceManager.OPEN_INTERFACES.get(parent.toLong()) as SubInterface?
		open = false
		forget()
		if (sub != null && sub.interfaceId == WINDOW) {
			SubInterfaceCloser.closeSubInterface(true, true, sub)
		}
	}

	/** The window was closed or opened by someone else, or the client logged out: nothing of ours is left. */
	@JvmStatic
	fun reset() {
		if (popupFor != null) {
			closePopup()
		}
		open = false
		selected = null
		forget()
	}

	private fun forget() {
		buttons.clear()
		choices.clear()
	}

	// --- the pages ---

	private fun rebuild() {
		closePopup()
		val body: Component = Interface.getComponent(BODY) ?: return
		forget()
		val children: ArrayList<Component> = ArrayList()
		val plugin: PluginRegistration? = selected
		if (plugin == null) {
			listPage(children)
		} else {
			settingsPage(children, plugin)
		}
		body.slotChildren = children.toTypedArray<Component?>()
		Component.layoutChildren(false, body)
		Component.redrawComponent(body)
	}

	private fun button(children: ArrayList<Component>, label: String, selected: Boolean, action: Int, index: Int, x: Int, y: Int, width: Int) {
		buttons.add(ClientPage.button(BODY, children, label, selected, SENTINEL, action, index, A_HOVER, A_LEAVE, buttons.size, x, y, width))
	}

	private fun pageCount(): Int {
		val n: Int = PluginRuntime.count
		return if (n == 0) 1 else (n + ROWS_PER_PAGE - 1) / ROWS_PER_PAGE
	}

	/** Every plugin, [ROWS_PER_PAGE] a page: name, what it is and its state, a Settings button for one with settings, and On or Off. */
	private fun listPage(children: ArrayList<Component>) {
		val pages: Int = pageCount()
		if (page >= pages) {
			page = pages - 1
		}
		val onWidth: Int = maxOf(ClientPage.textWidth(ClientPage.FONT_BUTTON, "On"), ClientPage.textWidth(ClientPage.FONT_BUTTON, "Off")) + 30
		val settingsWidth: Int = ClientPage.textWidth(ClientPage.FONT_BUTTON, "Settings") + 30
		val x0: Int = 12
		var y: Int = 10
		val first: Int = page * ROWS_PER_PAGE
		val last: Int = minOf(PluginRuntime.count, first + ROWS_PER_PAGE)
		for (i in first until last) {
			val registration: PluginRegistration = PluginRuntime.registrations[i] ?: continue
			children.add(ClientPage.text(BODY, children.size, x0, y, 190, 16, ClientPage.FONT_LABEL, registration.descriptor.name, ClientPage.TEXT, 0, 1, true))
			val state: String = stateLine(registration)
			val stateColour: Int = if (registration.state == PluginRegistration.FAILED) FAILED else DIM
			children.add(ClientPage.text(BODY, children.size, x0, y + 13, WIDTH - 24, 12, ClientPage.FONT_SMALL, state, stateColour, 0, 1, false))
			var bx: Int = WIDTH - 12 - onWidth
			val on: Boolean = isOn(registration)
			if (switchable(registration)) {
				button(children, if (on) "On" else "Off", on, A_TOGGLE, i, bx, y - 2, onWidth)
			}
			if (hasSettings(registration)) {
				bx -= settingsWidth + 6
				button(children, "Settings", false, A_SETTINGS, i, bx, y - 2, settingsWidth)
			}
			if (i < last - 1) {
				children.add(ClientPage.rect(BODY, children.size, 6, y + ROW_PITCH - 3, WIDTH - 12, 1, ClientPage.BOX_LINE, true))
			}
			y += ROW_PITCH
		}
		if (pages > 1) {
			val fy: Int = HEIGHT - 29 - 30
			val w: Int = ClientPage.textWidth(ClientPage.FONT_BUTTON, "Next") + 30
			children.add(ClientPage.text(BODY, children.size, 0, fy + 5, WIDTH, 16, ClientPage.FONT_SMALL, "Page " + (page + 1) + " of " + pages, ClientPage.TEXT, 1, 1, false))
			if (page > 0) {
				button(children, "Prev", false, A_PREV, 0, 12, fy, w)
			}
			if (page < pages - 1) {
				button(children, "Next", false, A_NEXT, 0, WIDTH - 12 - w, fy, w)
			}
		}
	}

	/** "built in · on", "jar name · 1.2 · off", "failed: why". */
	private fun stateLine(registration: PluginRegistration): String {
		val d = registration.descriptor
		if (registration.state == PluginRegistration.FAILED) {
			return "failed: " + (registration.error ?: "?")
		}
		val where: String = if (registration.builtIn) "built in" else registration.source
		val state: String = if (registration.builtIn) (if (switchable(registration)) (if (isOn(registration)) "on" else "off") else "always on") else registration.stateName()
		return d.id + " " + d.version + " · " + where + " · " + state
	}

	/** A built-in is switched by its "enabled" setting; a jar by starting and stopping it. */
	private fun switchable(registration: PluginRegistration): Boolean {
		if (!registration.builtIn) {
			return registration.state != PluginRegistration.FAILED || registration.loadable()
		}
		return enabledEntry(registration) != null
	}

	private fun enabledEntry(registration: PluginRegistration): PluginSchemaImpl.Entry? {
		val context: PluginContextImpl = registration.context ?: return null
		for (entry in context.schema.entries) {
			if (entry.key == "enabled" && entry.kind == PluginSchemaImpl.TOGGLE) {
				return entry
			}
		}
		return null
	}

	private fun isOn(registration: PluginRegistration): Boolean {
		if (!registration.builtIn) {
			return registration.context != null
		}
		val entry: PluginSchemaImpl.Entry = enabledEntry(registration) ?: return true
		val context: PluginContextImpl = registration.context ?: return true
		return context.config().getBoolean("enabled", entry.defaultInt == 1)
	}

	/** Whether the plugin declared settings the page can show: more than its own switch, and it is running (its context holds them). */
	private fun hasSettings(registration: PluginRegistration): Boolean {
		val context: PluginContextImpl = registration.context ?: return false
		for (entry in context.schema.entries) {
			if (entry.key != "enabled") {
				return true
			}
		}
		return false
	}

	private fun toggle(registration: PluginRegistration) {
		if (registration.builtIn) {
			val entry: PluginSchemaImpl.Entry = enabledEntry(registration) ?: return
			val context: PluginContextImpl = registration.context ?: return
			val now: Boolean = !context.config().getBoolean("enabled", entry.defaultInt == 1)
			context.config().setBoolean("enabled", now)
			context.config().save()
			PluginRuntime.settingChanged(registration, "enabled")
			return
		}
		Console.printToConsole(PluginRuntime.setPluginEnabled(registration, registration.context == null))
	}

	/** One plugin's settings: a row per declared entry, and Back. */
	private fun settingsPage(children: ArrayList<Component>, registration: PluginRegistration) {
		val context: PluginContextImpl? = registration.context
		children.add(ClientPage.text(BODY, children.size, 0, 2, WIDTH, 18, ClientPage.FONT_TITLE, registration.descriptor.name, ClientPage.TEXT, 1, 1, true))
		val backWidth: Int = ClientPage.textWidth(ClientPage.FONT_BUTTON, "Back") + 30
		button(children, "Back", false, A_BACK, 0, (WIDTH - backWidth) / 2, HEIGHT - 29 - 30, backWidth)
		if (context == null) {
			children.add(ClientPage.text(BODY, children.size, 0, 40, WIDTH, 16, ClientPage.FONT_LABEL, "Turn the plugin on to change its settings.", DIM, 1, 1, false))
			return
		}
		val config: PluginConfig = context.config()
		val entries: ArrayList<PluginSchemaImpl.Entry> = context.schema.entries
		val onWidth: Int = maxOf(ClientPage.textWidth(ClientPage.FONT_BUTTON, "On"), ClientPage.textWidth(ClientPage.FONT_BUTTON, "Off")) + 30
		val stepWidth: Int = ClientPage.textWidth(ClientPage.FONT_BUTTON, "+") + 30
		var boxWidth: Int = 0
		for (entry in entries) {
			val options: Array<String>? = entry.options
			if (options != null) {
				for (option in options) {
					boxWidth = maxOf(boxWidth, ClientPage.textWidth(ClientPage.FONT_SMALL, option))
				}
			}
		}
		boxWidth += 21
		var y: Int = 30
		for (i in 0 until entries.size) {
			val entry: PluginSchemaImpl.Entry = entries[i]
			if (entry.key == "enabled" && entry.kind == PluginSchemaImpl.TOGGLE) {
				continue // the list's switch
			}
			children.add(ClientPage.text(BODY, children.size, 12, y, WIDTH - 24, 16, ClientPage.FONT_LABEL, entry.label, ClientPage.TEXT, 0, 1, true))
			val right: Int = WIDTH - 12
			when (entry.kind) {
				PluginSchemaImpl.TOGGLE -> {
					val on: Boolean = config.getBoolean(entry.key, entry.defaultInt == 1)
					button(children, if (on) "On" else "Off", on, A_SET_TOGGLE, i, right - onWidth, y - 2, onWidth)
				}
				PluginSchemaImpl.NUMBER -> {
					val value: Int = config.getInt(entry.key, entry.defaultInt)
					button(children, "+", false, A_SET_PLUS, i, right - stepWidth, y - 2, stepWidth)
					children.add(ClientPage.text(BODY, children.size, right - stepWidth - 6 - 60, y, 60, 16, ClientPage.FONT_LABEL, value.toString(), if (value >= entry.max) ON else ClientPage.TEXT, 1, 1, false))
					button(children, "-", false, A_SET_MINUS, i, right - stepWidth - 6 - 60 - 6 - stepWidth, y - 2, stepWidth)
				}
				PluginSchemaImpl.CHOICE -> {
					val value: String = config.get(entry.key, entry.defaultText)
					val slots: IntArray = IntArray(3)
					val hit: Component = ClientPage.dropdown(BODY, children, entry.label, value, ClientPage.TEXT, SENTINEL, A_SET_CHOICE, choices.size, right, y, boxWidth, slots)
					choices.add(Choice(entry, hit, slots[0], slots[1]))
				}
			}
			y += SETTING_PITCH
		}
		if (y == 30) {
			children.add(ClientPage.text(BODY, children.size, 0, 40, WIDTH, 16, ClientPage.FONT_LABEL, "This plugin has no settings beyond its switch.", DIM, 1, 1, false))
		}
	}

	private fun writeSetting(registration: PluginRegistration, entry: PluginSchemaImpl.Entry, text: String?, number: Int, toggle: Boolean) {
		val context: PluginContextImpl = registration.context ?: return
		val config: PluginConfig = context.config()
		when (entry.kind) {
			PluginSchemaImpl.TOGGLE -> config.setBoolean(entry.key, toggle)
			PluginSchemaImpl.NUMBER -> config.setInt(entry.key, number)
			else -> config.set(entry.key, text ?: entry.defaultText)
		}
		config.save()
		PluginRuntime.settingChanged(registration, entry.key)
	}

	private fun step(registration: PluginRegistration, entry: PluginSchemaImpl.Entry, direction: Int) {
		val context: PluginContextImpl = registration.context ?: return
		var value: Int = context.config().getInt(entry.key, entry.defaultInt) + direction * entry.step
		if (value < entry.min) {
			value = entry.min
		}
		if (value > entry.max) {
			value = entry.max
		}
		writeSetting(registration, entry, null, value, false)
	}

	// --- the choice list ---

	private fun openPopup(choice: Choice) {
		closePopup()
		val registration: PluginRegistration = selected ?: return
		val context: PluginContextImpl = registration.context ?: return
		val options: Array<String> = choice.entry.options ?: return
		val current: String = context.config().get(choice.entry.key, choice.entry.defaultText)
		val values: ArrayList<String> = ArrayList()
		for (option in options) {
			if (option != current) {
				values.add(option)
			}
		}
		val shown: Array<String> = values.toTypedArray()
		val colours: IntArray = IntArray(shown.size) { ClientPage.TEXT }
		popup.open(choice.hit, shown, shown, colours)
		if (!popup.open) {
			return
		}
		ClientPage.setSprite(BODY, choice.arrow, ClientPage.ARROW_OPEN)
		ClientPage.setColour(BODY, choice.outline, ClientPage.BOX_LINE_OPEN)
		popupFor = choice
	}

	private fun closePopup() {
		val choice: Choice = popupFor ?: return
		popupFor = null
		popup.close()
		if (open) {
			ClientPage.setSprite(BODY, choice.arrow, ClientPage.ARROW)
			ClientPage.setColour(BODY, choice.outline, ClientPage.BOX_LINE)
		}
	}

	// --- our hooks ---

	/** A hook whose script id is [SENTINEL]: from CS2Interpreter.executeHookInternal. */
	@JvmStatic
	fun hook(hook: HookRequest) {
		val params: Array<Any?> = hook.params ?: return
		if (params.size < 3) {
			return
		}
		val action: Int = params[1] as Int
		val index: Int = params[2] as Int
		if (action == A_OPEN) {
			val why: String? = open()
			if (why != null) {
				Console.printToConsole(why)
			}
			return
		}
		if (!open) {
			return
		}
		val plugin: PluginRegistration? = selected
		when (action) {
			A_CLOSE -> close()
			A_TOGGLE -> if (index < PluginRuntime.count) {
				val registration: PluginRegistration? = PluginRuntime.registrations[index]
				if (registration != null) {
					toggle(registration)
					rebuild()
				}
			}
			A_SETTINGS -> if (index < PluginRuntime.count) {
				selected = PluginRuntime.registrations[index]
				rebuild()
			}
			A_BACK -> {
				selected = null
				rebuild()
			}
			A_PREV -> {
				page--
				rebuild()
			}
			A_NEXT -> {
				page++
				rebuild()
			}
			A_HOVER -> if (index < buttons.size) ClientPage.paint(buttons[index], true)
			A_LEAVE -> if (index < buttons.size) ClientPage.paint(buttons[index], false)
			A_SET_TOGGLE -> if (plugin != null) {
				val entry: PluginSchemaImpl.Entry = entryAt(plugin, index) ?: return
				val context: PluginContextImpl = plugin.context ?: return
				writeSetting(plugin, entry, null, 0, !context.config().getBoolean(entry.key, entry.defaultInt == 1))
				rebuild()
			}
			A_SET_MINUS -> if (plugin != null) {
				val entry: PluginSchemaImpl.Entry = entryAt(plugin, index) ?: return
				step(plugin, entry, -1)
				rebuild()
			}
			A_SET_PLUS -> if (plugin != null) {
				val entry: PluginSchemaImpl.Entry = entryAt(plugin, index) ?: return
				step(plugin, entry, 1)
				rebuild()
			}
			A_SET_CHOICE -> if (index < choices.size) {
				val choice: Choice = choices[index]
				if (popupFor === choice) {
					closePopup()
				} else {
					openPopup(choice)
				}
			}
			A_ITEM -> {
				val choice: Choice? = popupFor
				if (plugin != null && choice != null && index < popup.values.size) {
					val value: String = popup.values[index]
					closePopup()
					writeSetting(plugin, choice.entry, value, 0, false)
					rebuild()
				}
			}
			A_POPUP_CLOSE -> closePopup()
		}
	}

	private fun entryAt(registration: PluginRegistration, index: Int): PluginSchemaImpl.Entry? {
		val context: PluginContextImpl = registration.context ?: return null
		return if (index >= 0 && index < context.schema.entries.size) context.schema.entries[index] else null
	}

	// --- for the smoke test: the same actions the clicks take ---

	/** How many plugin rows the list page shows now, -1 when the window is not ours. */
	@JvmStatic
	fun rowsShown(): Int {
		if (!open || selected != null) {
			return -1
		}
		return minOf(PluginRuntime.count - page * ROWS_PER_PAGE, ROWS_PER_PAGE)
	}

	@JvmStatic
	fun clickToggle(id: String): Boolean {
		val registration: PluginRegistration = PluginRuntime.find(id) ?: return false
		if (!open || !switchable(registration)) {
			return false
		}
		toggle(registration)
		rebuild()
		return true
	}

	@JvmStatic
	fun clickSettings(id: String): Boolean {
		val registration: PluginRegistration = PluginRuntime.find(id) ?: return false
		if (!open || !hasSettings(registration)) {
			return false
		}
		selected = registration
		rebuild()
		return true
	}

	/** Steps the selected plugin's number setting [key] once, as the "+" or "-" button does. */
	@JvmStatic
	fun clickStep(key: String, up: Boolean): Boolean {
		val registration: PluginRegistration = selected ?: return false
		val context: PluginContextImpl = registration.context ?: return false
		for (entry in context.schema.entries) {
			if (entry.key == key && entry.kind == PluginSchemaImpl.NUMBER) {
				step(registration, entry, if (up) 1 else -1)
				rebuild()
				return true
			}
		}
		return false
	}

	@JvmStatic
	fun clickBack(): Boolean {
		if (!open || selected == null) {
			return false
		}
		selected = null
		rebuild()
		return true
	}
}
