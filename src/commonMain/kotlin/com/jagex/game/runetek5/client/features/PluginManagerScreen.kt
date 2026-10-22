package com.jagex.game.runetek5.client.features

import SubInterfaceCloser
import SubInterfaceOpener
import com.jagex.game.runetek5.InterfaceManager
import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.client.input.keyboard.KeyRecord
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
import com.jagex.game.runetek5.plugin.PluginIcon
import com.jagex.game.runetek5.plugin.PluginRegistration
import com.jagex.game.runetek5.plugin.PluginRuntime
import com.jagex.game.runetek5.plugin.PluginSchemaImpl
import com.jagex.game.runetek5.plugin.PluginConfigImpl
import com.jagex.game.runetek5.plugin.api.PluginConfig
import com.jagex.game.runetek5.plugin.api.KeyCode
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

	/** The list rows' icons' sprite ids: this plus the registration's index, after [ICON_SPRITE]. */
	private const val ROW_ICON_SPRITE: Int = ICON_SPRITE + 1

	/** The rows' icon slot, which every row keeps so that the names line up, and the gap after it. */
	private const val ICON_SLOT: Int = PluginIcon.SIZE

	private const val ICON_GAP: Int = 8

	/** The window's height while ours (the Custom page's), and its content layer's width. */
	private const val HEIGHT: Int = 334

	private const val WIDTH: Int = 501

	private const val ROW_PITCH: Int = 28

	private const val ROWS_PER_PAGE: Int = 9

	private const val SETTING_PITCH: Int = 24

	private const val DIM: Int = 0x9f9684

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
	private const val A_SET_CHOICE: Int = 13
	private const val A_ITEM: Int = 14
	private const val A_POPUP_CLOSE: Int = 15
	private const val A_EDIT: Int = 16
	private const val A_ROW_HOVER: Int = 17
	private const val A_ROW_LEAVE: Int = 18
	private const val A_WHEEL: Int = 19
	private const val A_COLOUR: Int = 20
	private const val A_KEYBIND: Int = 21
	private const val A_PICK_CELL: Int = 22
	private const val A_PICK_OK: Int = 23
	private const val A_PICK_CANCEL: Int = 24
	private const val A_PICK_HEX: Int = 25
	private const val A_PICK_ALPHA: Int = 26
	private const val A_PICK_RECENT: Int = 27
	private const val A_OPEN_PANEL: Int = 28

	/** Built-ins with a window of their own (the Loot Tracker's), by plugin id: what their row's Open button does; null, or why it did not open. */
	private val panels: HashMap<String, () -> String?> = HashMap()

	/** A built-in's window, opened from an Open button on its row. */
	@JvmStatic
	fun registerPanel(id: String, opener: () -> String?) {
		panels[id] = opener
	}

	@JvmStatic
	fun unregisterPanel(id: String) {
		panels.remove(id)
	}

	/** A colour setting's swatch, and the picker's cells. */
	private const val SWATCH_WIDTH: Int = 40

	private const val KEY_BOX_WIDTH: Int = 110

	private const val CELL_WIDTH: Int = 18

	private const val CELL_HEIGHT: Int = 14

	private const val HUES: Int = 24

	/** The palette's rows: four of full saturation getting brighter, four of full brightness getting paler, one of greys. */
	private const val PALETTE_ROWS: Int = 9

	private const val MAX_RECENT: Int = HUES

	private const val CHECK_LIGHT: Int = 0xc8c8c8

	private const val CHECK_DARK: Int = 0x7a7a7a

	private const val ALPHA_MASK: Int = -0x1000000

	private const val RGB_MASK: Int = 0xFFFFFF

	/** The content layer's visible height (the window's less its title bar), and the scrollbar's place in it. */
	private const val BODY_HEIGHT: Int = HEIGHT - 29

	private const val SCROLLBAR_X: Int = WIDTH - 10

	private const val SCROLLBAR_WIDTH: Int = 6

	private const val THUMB_MIN: Int = 12

	/** The tooltip: the stock pale box with black text, lines wrapped to this width. */
	private const val TIP_FILL: Int = 0xffffa0
	private const val TIP_LINE: Int = 0x000000
	private const val TIP_WIDTH: Int = 300
	private const val TIP_LINE_HEIGHT: Int = 13

	/** The client's codes for Enter, Backspace and Escape (CanvasKeyRecorder.REMAPPED_KEYS). */
	private const val KEY_ENTER: Int = 84
	private const val KEY_BACKSPACE: Int = 85
	private const val KEY_ESCAPE: Int = 13

	private const val NUMBER_BOX_WIDTH: Int = 70

	private const val TEXT_BOX_WIDTH: Int = 240

	private const val MAX_DIGITS: Int = 9

	/** Whether the window is open with our page in it. */
	@JvmField
	var open: Boolean = false

	/** A colour being picked: what it started as, whether its alpha is the player's, what it is now, and what gets it. */
	private class Pick(@JvmField val alpha: Boolean, @JvmField var argb: Int, @JvmField val done: (Int) -> Unit, @JvmField val openedForIt: Boolean)

	private var picking: Pick? = null

	/** The picker's box being typed into: 0 none, 1 the hex, 2 the alpha; its text so far; the slots of the two boxes' outlines and values. */
	private var pickEdit: Int = 0

	private var pickText: String = ""

	private var pickHexOutline: Int = -1

	private var pickHexValue: Int = -1

	private var pickAlphaOutline: Int = -1

	private var pickAlphaValue: Int = -1

	/** The colours picked lately, newest first, offered on the picker's last row. */
	private val recentColours: ArrayList<Int> = ArrayList()

	/** A keybind setting waiting for its key, and the slots of its box. */
	private var capturing: PluginSchemaImpl.Entry? = null

	private val keyOutline: HashMap<Int, Int> = HashMap()

	private val keyValue: HashMap<Int, Int> = HashMap()

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

	/** The number setting being typed into, its text so far, and the slots of its box outline and value text. */
	private var editing: PluginSchemaImpl.Entry? = null

	private var editText: String = ""

	private var editOutline: Int = -1

	private var editValue: Int = -1

	/** The list rows' hit components, by registration index, for the tooltip and the smoke test. */
	private val rowHits: HashMap<Int, Component> = HashMap()

	/** The registration index whose tooltip is up, -1 for none. */
	private var tipFor: Int = -1

	/** Each number row's outline and value slots, by entry index, for [startEdit]. */
	private val numberOutline: HashMap<Int, Int> = HashMap()

	private val numberValue: HashMap<Int, Int> = HashMap()

	/** The Plugins button's hit component in the Settings tab. */
	private var tabButton: Component? = null

	/** The settings page's content height, its scrollbar's track and thumb slots (-1 without one), and the plugin the page was last built for. */
	private var settingsContent: Int = 0

	private var scrollTrack: Int = -1

	private var scrollThumb: Int = -1

	private var scrolledFor: PluginRegistration? = null

	private var icon: NativeSprite? = null

	private var iconToolkit: Toolkit? = null

	/** False once the icon resource was looked for and not found (the browser): the button says "Plugins" instead. */
	private var iconAvailable: Boolean = true

	/** The list rows' icons as sprites, by registration index, and the toolkit they were made for. */
	private val rowIcons: HashMap<Int, NativeSprite> = HashMap()

	private var rowIconToolkit: Toolkit? = null

	// --- the Settings tab ---

	/**
	 * Each game tick while the Settings tab is loaded: the icon in the sprite cache (the caches are
	 * emptied at logout and map builds), Graphics and Audio Settings at the three-button pitch, and the
	 * Plugins button after them, appended again whenever the tab's own scripts have rebuilt its children.
	 */
	@JvmStatic
	fun tick() {
		if (open) {
			ensureRowIcons()
		}
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

	/**
	 * Each plugin's icon in the component sprite cache under [ROW_ICON_SPRITE] plus its index, made
	 * for the active toolkit (again after a toolkit change) and put back whenever the cache was emptied.
	 */
	private fun ensureRowIcons() {
		val toolkit: Toolkit = ClientState.activeToolkit ?: return
		if (rowIconToolkit !== toolkit) {
			rowIcons.clear()
			rowIconToolkit = toolkit
		}
		for (i in 0 until PluginRuntime.count) {
			ensureRowIcon(toolkit, i)
		}
	}

	private fun ensureRowIcon(toolkit: Toolkit, i: Int) {
		val icon: PluginIcon = PluginRuntime.registrations[i]?.icon ?: return
		val id: Long = (ROW_ICON_SPRITE + i).toLong()
		if (Component.NATIVE_SPRITE_CACHE.get(id) != null) {
			return
		}
		var sprite: NativeSprite? = rowIcons[i]
		if (sprite == null) {
			sprite = toolkit.createNativeSprite(icon.pixels, icon.width, icon.width, icon.height, false) ?: return
			rowIcons[i] = sprite
		}
		Component.NATIVE_SPRITE_CACHE.put(sprite, id, icon.width * icon.height * 4)
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
		picking = null // a colour being picked when the window closes is left as it was
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
		picking = null
		tipFor = -1
		forget()
	}

	private fun forget() {
		buttons.clear()
		choices.clear()
		numberOutline.clear()
		numberValue.clear()
		editing = null
		capturing = null
		keyOutline.clear()
		keyValue.clear()
		pickEdit = 0
		rowHits.clear()
		scrollTrack = -1
		scrollThumb = -1
		settingsContent = 0
	}

	// --- the pages ---

	private fun rebuild() {
		closePopup()
		hideTip()
		val body: Component = Interface.getComponent(BODY) ?: return
		forget()
		ensureRowIcons()
		val children: ArrayList<Component> = ArrayList()
		val plugin: PluginRegistration? = selected
		if (plugin !== scrolledFor) {
			body.scrollY = 0 // another page: from its top
			scrolledFor = plugin
		}
		val pick: Pick? = picking
		if (pick != null) {
			pickerPage(children, pick)
		} else if (plugin == null) {
			listPage(children)
		} else {
			settingsPage(children, plugin)
		}
		body.slotChildren = children.toTypedArray<Component?>()
		// a settings page taller than the layer scrolls: the layer draws and clips its children by scrollY, and the wheel comes to it as a hook
		val scrolls: Boolean = plugin != null && pick == null && settingsContent > BODY_HEIGHT
		body.scrollHeight = if (scrolls) settingsContent else 0
		body.hasScript = scrolls
		body.onMouseMove = if (scrolls) arrayOf<Any?>(SENTINEL, A_WHEEL, 0) else null
		if (!scrolls) {
			body.scrollY = 0
		}
		Component.layoutChildren(false, body)
		scrollTo(body.scrollY)
	}

	/** Scrolls the settings page to [y], held to its range, and puts the scrollbar where it now shows. */
	private fun scrollTo(y: Int) {
		val body: Component = Interface.getComponent(BODY) ?: return
		val most: Int = maxOf(0, body.scrollHeight - BODY_HEIGHT)
		body.scrollY = y.coerceIn(0, most)
		if (scrollTrack >= 0 && scrollThumb >= 0 && most > 0) {
			val thumbHeight: Int = maxOf(THUMB_MIN, BODY_HEIGHT * BODY_HEIGHT / body.scrollHeight)
			val thumbY: Int = body.scrollY + (BODY_HEIGHT - thumbHeight) * body.scrollY / most
			ClientPage.childOf(BODY, scrollTrack)?.basePositionY = body.scrollY
			ClientPage.childOf(BODY, scrollThumb)?.let {
				it.basePositionY = thumbY
				it.baseHeight = thumbHeight
			}
			Component.layoutChildren(false, body)
		}
		Component.redrawComponent(body)
	}

	/** The smoke test's wheel: [notches] turns down (up when negative). */
	@JvmStatic
	fun scrollBy(notches: Int) {
		val body: Component = Interface.getComponent(BODY) ?: return
		scrollTo(body.scrollY + notches * SETTING_PITCH)
	}

	/** The smoke test's read: the scroll position, the content height (0 when the page does not scroll) and the visible height. */
	@JvmStatic
	fun scrollPosition(): IntArray {
		val body: Component = Interface.getComponent(BODY) ?: return intArrayOf(0, 0, BODY_HEIGHT)
		return intArrayOf(body.scrollY, body.scrollHeight, BODY_HEIGHT)
	}

	private fun button(children: ArrayList<Component>, label: String, selected: Boolean, action: Int, index: Int, x: Int, y: Int, width: Int) {
		buttons.add(ClientPage.button(BODY, children, label, selected, SENTINEL, action, index, A_HOVER, A_LEAVE, buttons.size, x, y, width))
	}

	private fun pageCount(): Int {
		val n: Int = PluginRuntime.count
		return if (n == 0) 1 else (n + ROWS_PER_PAGE - 1) / ROWS_PER_PAGE
	}

	/**
	 * Every plugin, [ROWS_PER_PAGE] a page: its icon, name, version, author and where it came from (or
	 * why it failed), a Settings button for one with settings, and its tickbox.
	 */
	private fun listPage(children: ArrayList<Component>) {
		val pages: Int = pageCount()
		if (page >= pages) {
			page = pages - 1
		}
		val settingsWidth: Int = ClientPage.textWidth(ClientPage.FONT_BUTTON, "Settings") + 30
		val x0: Int = 12 + ICON_SLOT + ICON_GAP
		var y: Int = 10
		val first: Int = page * ROWS_PER_PAGE
		val last: Int = minOf(PluginRuntime.count, first + ROWS_PER_PAGE)
		for (i in first until last) {
			val registration: PluginRegistration = PluginRuntime.registrations[i] ?: continue
			// the row's hit, under everything else in it: hovering it shows the plugin's description
			val hit: Component = ClientPage.text(BODY, children.size, 6, y - 4, WIDTH - 12, ROW_PITCH - 3, ClientPage.FONT_SMALL, "", ClientPage.TEXT, 0, 0, false)
			hit.hasScript = true // the input pass visits only components flagged so; no option, so no menu entry
			hit.onMouseOver = arrayOf<Any?>(SENTINEL, A_ROW_HOVER, i)
			hit.onMouseLeaveScript = arrayOf<Any?>(SENTINEL, A_ROW_LEAVE, i)
			children.add(hit)
			rowHits[i] = hit
			if (registration.icon != null) {
				children.add(ClientPage.sprite(BODY, children.size, 12, y - 1, ICON_SLOT, ICON_SLOT, ROW_ICON_SPRITE + i, false, false, false))
			}
			children.add(ClientPage.text(BODY, children.size, x0, y, 190, 16, ClientPage.FONT_LABEL, registration.descriptor.name, ClientPage.TEXT, 0, 1, true))
			val detail: String = detailLine(registration, true)
			val detailColour: Int = if (registration.state == PluginRegistration.FAILED) FAILED else DIM
			children.add(ClientPage.text(BODY, children.size, x0, y + 13, WIDTH - 12 - x0, 12, ClientPage.FONT_SMALL, detail, detailColour, 0, 1, false))
			var bx: Int = WIDTH - 12 - ClientPage.TICK_SIZE
			val on: Boolean = isOn(registration)
			if (switchable(registration)) {
				ClientPage.tickbox(BODY, children, on, SENTINEL, A_TOGGLE, i, bx, y + 4, if (on) "Turn off" else "Turn on")
			}
			if (hasSettings(registration)) {
				bx -= settingsWidth + 10
				button(children, "Settings", false, A_SETTINGS, i, bx, y - 2, settingsWidth)
			}
			if (registration.context != null && panels.containsKey(registration.descriptor.id)) {
				val openWidth: Int = ClientPage.textWidth(ClientPage.FONT_BUTTON, "Open") + 30
				bx -= openWidth + 10
				button(children, "Open", false, A_OPEN_PANEL, i, bx, y - 2, openWidth)
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

	/**
	 * "Version 1.2 · by Someone · built in" ("· always on" after it for a built-in without a switch,
	 * since no button says so), without the source when [withSource] is false, or "failed: why".
	 */
	private fun detailLine(registration: PluginRegistration, withSource: Boolean): String {
		val d = registration.descriptor
		if (registration.state == PluginRegistration.FAILED) {
			return "failed: " + (registration.error ?: "?")
		}
		val line: StringBuilder = StringBuilder("Version ").append(d.version)
		if (d.author.isNotEmpty()) {
			line.append(" · by ").append(d.author)
		}
		if (withSource) {
			line.append(" · ").append(if (registration.builtIn) "built in" else registration.source)
		}
		if (registration.builtIn && !switchable(registration)) {
			line.append(" · always on")
		}
		return line.toString()
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
		children.add(ClientPage.text(BODY, children.size, 0, 20, WIDTH, 12, ClientPage.FONT_SMALL, detailLine(registration, false), DIM, 1, 1, false))
		val backWidth: Int = ClientPage.textWidth(ClientPage.FONT_BUTTON, "Back") + 30
		if (context == null) {
			children.add(ClientPage.text(BODY, children.size, 0, 48, WIDTH, 16, ClientPage.FONT_LABEL, "Turn the plugin on to change its settings.", DIM, 1, 1, false))
			button(children, "Back", false, A_BACK, 0, (WIDTH - backWidth) / 2, HEIGHT - 29 - 30, backWidth)
			return
		}
		val config: PluginConfig = context.config()
		val entries: ArrayList<PluginSchemaImpl.Entry> = context.schema.entries
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
		var y: Int = 40
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
					ClientPage.tickbox(BODY, children, on, SENTINEL, A_SET_TOGGLE, i, right - ClientPage.TICK_SIZE, y, if (on) "Turn off" else "Turn on")
				}
				PluginSchemaImpl.NUMBER -> {
					// a box to type into: click, digits, Enter; the range beside it
					val value: Int = config.getInt(entry.key, entry.defaultInt)
					val bx: Int = right - NUMBER_BOX_WIDTH
					children.add(ClientPage.text(BODY, children.size, 12, y + 1, bx - 18, 16, ClientPage.FONT_SMALL, entry.min.toString() + " to " + entry.max, DIM, 2, 1, false))
					children.add(ClientPage.rect(BODY, children.size, bx, y - 1, NUMBER_BOX_WIDTH, ClientPage.ITEM_HEIGHT + 4, ClientPage.BOX_FILL, true))
					numberOutline[i] = children.size
					children.add(ClientPage.rect(BODY, children.size, bx, y - 1, NUMBER_BOX_WIDTH, ClientPage.ITEM_HEIGHT + 4, ClientPage.BOX_LINE, false))
					numberValue[i] = children.size
					val text: Component = ClientPage.text(BODY, children.size, bx, y - 1, NUMBER_BOX_WIDTH, ClientPage.ITEM_HEIGHT + 4, ClientPage.FONT_LABEL, value.toString(), ClientPage.TEXT, 1, 1, false)
					text.hasScript = true
					text.addOptionName(0, "Edit")
					text.opBase = ""
					text.params = arrayOf<Any?>(SENTINEL, A_EDIT, i)
					children.add(text)
				}
				PluginSchemaImpl.TEXT -> {
					// a wider box for a line of text, typed the same way
					val value: String = config.get(entry.key, entry.defaultText)
					val bx: Int = right - TEXT_BOX_WIDTH
					children.add(ClientPage.rect(BODY, children.size, bx, y - 1, TEXT_BOX_WIDTH, ClientPage.ITEM_HEIGHT + 4, ClientPage.BOX_FILL, true))
					numberOutline[i] = children.size
					children.add(ClientPage.rect(BODY, children.size, bx, y - 1, TEXT_BOX_WIDTH, ClientPage.ITEM_HEIGHT + 4, ClientPage.BOX_LINE, false))
					numberValue[i] = children.size
					val text: Component = ClientPage.text(BODY, children.size, bx + 4, y - 1, TEXT_BOX_WIDTH - 8, ClientPage.ITEM_HEIGHT + 4, ClientPage.FONT_LABEL, fitText(value, TEXT_BOX_WIDTH - 8), ClientPage.TEXT, 0, 1, false)
					text.hasScript = true
					text.addOptionName(0, "Edit")
					text.opBase = ""
					text.params = arrayOf<Any?>(SENTINEL, A_EDIT, i)
					children.add(text)
				}
				PluginSchemaImpl.COLOUR -> {
					// a swatch to click for the picker, its hex beside it
					val argb: Int = config.getColour(entry.key, entry.defaultInt)
					val bx: Int = right - SWATCH_WIDTH
					children.add(ClientPage.text(BODY, children.size, 12, y + 1, bx - 20, 16, ClientPage.FONT_SMALL, hexOf(argb, entry.max == 1), DIM, 2, 1, false))
					swatch(children, bx, y - 1, SWATCH_WIDTH, ClientPage.ITEM_HEIGHT + 4, argb)
					val hit: Component = ClientPage.text(BODY, children.size, bx, y - 1, SWATCH_WIDTH, ClientPage.ITEM_HEIGHT + 4, ClientPage.FONT_SMALL, "", ClientPage.TEXT, 1, 1, false)
					hit.hasScript = true
					hit.addOptionName(0, "Pick")
					hit.opBase = entry.label
					hit.params = arrayOf<Any?>(SENTINEL, A_COLOUR, i)
					children.add(hit)
				}
				PluginSchemaImpl.KEYBIND -> {
					// a box showing the key: click, then press the key
					val code: Int = config.getInt(entry.key, entry.defaultInt)
					val bx: Int = right - KEY_BOX_WIDTH
					children.add(ClientPage.rect(BODY, children.size, bx, y - 1, KEY_BOX_WIDTH, ClientPage.ITEM_HEIGHT + 4, ClientPage.BOX_FILL, true))
					keyOutline[i] = children.size
					children.add(ClientPage.rect(BODY, children.size, bx, y - 1, KEY_BOX_WIDTH, ClientPage.ITEM_HEIGHT + 4, ClientPage.BOX_LINE, false))
					keyValue[i] = children.size
					val text: Component = ClientPage.text(BODY, children.size, bx, y - 1, KEY_BOX_WIDTH, ClientPage.ITEM_HEIGHT + 4, ClientPage.FONT_LABEL, KeyCode.name(code), ClientPage.TEXT, 1, 1, false)
					text.hasScript = true
					text.addOptionName(0, "Set")
					text.opBase = ""
					text.params = arrayOf<Any?>(SENTINEL, A_KEYBIND, i)
					children.add(text)
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
		if (y == 40) {
			children.add(ClientPage.text(BODY, children.size, 0, 40, WIDTH, 16, ClientPage.FONT_LABEL, "This plugin has no settings beyond its switch.", DIM, 1, 1, false))
		}
		// Back after the last row, at the window's bottom when the rows leave room; a taller page scrolls, with a bar at its right edge
		val backY: Int = maxOf(HEIGHT - 29 - 30, y + 8)
		button(children, "Back", false, A_BACK, 0, (WIDTH - backWidth) / 2, backY, backWidth)
		settingsContent = backY + 30 // the button's 26 and a little under it: a page with room does not scroll
		if (settingsContent > BODY_HEIGHT) {
			scrollTrack = children.size
			children.add(ClientPage.rect(BODY, children.size, SCROLLBAR_X, 0, SCROLLBAR_WIDTH, BODY_HEIGHT, ClientPage.BOX_FILL, true))
			scrollThumb = children.size
			children.add(ClientPage.rect(BODY, children.size, SCROLLBAR_X, 0, SCROLLBAR_WIDTH, THUMB_MIN, ClientPage.TEXT_TOP, true))
		}
	}

	private fun writeSetting(registration: PluginRegistration, entry: PluginSchemaImpl.Entry, text: String?, number: Int, toggle: Boolean) {
		val context: PluginContextImpl = registration.context ?: return
		val config: PluginConfig = context.config()
		when (entry.kind) {
			PluginSchemaImpl.TOGGLE -> config.setBoolean(entry.key, toggle)
			PluginSchemaImpl.NUMBER, PluginSchemaImpl.KEYBIND -> config.setInt(entry.key, number)
			PluginSchemaImpl.COLOUR -> config.setColour(entry.key, number)
			else -> config.set(entry.key, text ?: entry.defaultText)
		}
		config.save()
		PluginRuntime.settingChanged(registration, entry.key)
	}

	// --- colours ---

	/** `#RRGGBB`, or `#AARRGGBB` when the alpha is the player's. */
	private fun hexOf(argb: Int, alpha: Boolean): String {
		val full: String = PluginConfigImpl.formatColour(argb)
		return if (alpha) full else "#" + full.substring(3)
	}

	/** A swatch of [argb]: a checkerboard under the colour, so that how see-through it is shows, and an outline. */
	private fun swatch(children: ArrayList<Component>, x: Int, y: Int, width: Int, height: Int, argb: Int) {
		val halfW: Int = width / 2
		val halfH: Int = height / 2
		children.add(ClientPage.rect(BODY, children.size, x, y, width, height, CHECK_LIGHT, true))
		children.add(ClientPage.rect(BODY, children.size, x, y, halfW, halfH, CHECK_DARK, true))
		children.add(ClientPage.rect(BODY, children.size, x + halfW, y + halfH, width - halfW, height - halfH, CHECK_DARK, true))
		val colour: Component = ClientPage.rect(BODY, children.size, x, y, width, height, argb and RGB_MASK, true)
		colour.transparency = 255 - (argb ushr 24)
		children.add(colour)
		children.add(ClientPage.rect(BODY, children.size, x, y, width, height, ClientPage.BOX_LINE, false))
	}

	/** The palette's cell [index], row by row: a hue across, and down it full colours getting brighter, then paler, then greys. */
	@JvmStatic
	fun paletteColour(index: Int): Int {
		val row: Int = index / HUES
		val column: Int = index % HUES
		if (row >= PALETTE_ROWS - 1) {
			val grey: Int = column * 255 / (HUES - 1)
			return ALPHA_MASK or (grey shl 16) or (grey shl 8) or grey
		}
		val hue: Float = column * 360f / HUES
		val saturation: Float = if (row < 4) 1f else 1f - (row - 3) * 0.2f
		val value: Float = if (row < 4) (row + 1) * 0.25f else 1f
		return hsv(hue, saturation, value)
	}

	private fun hsv(hue: Float, saturation: Float, value: Float): Int {
		val c: Float = value * saturation
		val h: Float = hue / 60f
		val x: Float = c * (1f - kotlin.math.abs(h % 2f - 1f))
		val m: Float = value - c
		var r: Float = 0f
		var g: Float = 0f
		var b: Float = 0f
		when (h.toInt()) {
			0 -> { r = c; g = x }
			1 -> { r = x; g = c }
			2 -> { g = c; b = x }
			3 -> { g = x; b = c }
			4 -> { r = x; b = c }
			else -> { r = c; b = x }
		}
		val ri: Int = ((r + m) * 255f + 0.5f).toInt().coerceIn(0, 255)
		val gi: Int = ((g + m) * 255f + 0.5f).toInt().coerceIn(0, 255)
		val bi: Int = ((b + m) * 255f + 0.5f).toInt().coerceIn(0, 255)
		return ALPHA_MASK or (ri shl 16) or (gi shl 8) or bi
	}

	/**
	 * A plugin's colour picker (PluginContext.pickColour): opens the window on the picker, or puts the
	 * picker in it when it is already open; [done] gets the colour on OK. Null when it opened, else why not.
	 */
	@JvmStatic
	fun pickColour(initial: Int, alpha: Boolean, done: (Int) -> Unit): String? {
		var openedForIt: Boolean = false
		if (!open) {
			val why: String? = open()
			if (why != null) {
				return why
			}
			openedForIt = true
		}
		startPick(initial, alpha, openedForIt, done)
		return null
	}

	private fun startPick(initial: Int, alpha: Boolean, openedForIt: Boolean, done: (Int) -> Unit) {
		closeEdit()
		closePopup()
		picking = Pick(alpha, if (alpha) initial else initial or ALPHA_MASK, done, openedForIt)
		rebuild()
	}

	/** OK hands the colour on and remembers it; either way the page under the picker comes back, or the window closes when it was opened for the picker. */
	private fun finishPick(ok: Boolean) {
		val pick: Pick = picking ?: return
		picking = null
		if (ok) {
			recentColours.remove(pick.argb)
			recentColours.add(0, pick.argb)
			while (recentColours.size > MAX_RECENT) {
				recentColours.removeAt(recentColours.size - 1)
			}
			pick.done(pick.argb) // before the page comes back, so that it shows the new colour
		}
		if (pick.openedForIt) {
			close()
		} else if (open) {
			rebuild()
		}
	}

	/**
	 * The picker: the palette, the colours picked lately, a large swatch of the colour with its hex
	 * (typed: click the box) and, when the alpha is the player's, how opaque it is from 0 to 255 (typed too), OK and Cancel.
	 */
	private fun pickerPage(children: ArrayList<Component>, pick: Pick) {
		children.add(ClientPage.text(BODY, children.size, 0, 2, WIDTH, 18, ClientPage.FONT_TITLE, "Choose a colour", ClientPage.TEXT, 1, 1, true))
		val x0: Int = (WIDTH - HUES * CELL_WIDTH) / 2
		var y: Int = 26
		for (i in 0 until HUES * PALETTE_ROWS) {
			val cx: Int = x0 + (i % HUES) * CELL_WIDTH
			val cy: Int = y + (i / HUES) * CELL_HEIGHT
			cell(children, cx, cy, paletteColour(i), A_PICK_CELL, i)
		}
		y += PALETTE_ROWS * CELL_HEIGHT + 8
		children.add(ClientPage.text(BODY, children.size, x0, y, 60, CELL_HEIGHT, ClientPage.FONT_SMALL, "Recent", DIM, 0, 1, false))
		for (i in 0 until minOf(recentColours.size, HUES - 4)) {
			cell(children, x0 + (i + 4) * CELL_WIDTH, y, recentColours[i], A_PICK_RECENT, i)
		}
		y += CELL_HEIGHT + 12
		swatch(children, x0, y, 60, 30, pick.argb)
		var bx: Int = x0 + 76
		children.add(ClientPage.text(BODY, children.size, bx, y + 7, 30, 16, ClientPage.FONT_LABEL, "Hex", ClientPage.TEXT, 0, 1, true))
		bx += 34
		val hex: String = if (pickEdit == 1) pickText + "|" else hexOf(pick.argb, pick.alpha)
		val boxes: IntArray = IntArray(2)
		editBox(children, bx, y + 6, 100, hex, A_PICK_HEX, boxes)
		pickHexOutline = boxes[0]
		pickHexValue = boxes[1]
		pickAlphaOutline = -1
		pickAlphaValue = -1
		if (pick.alpha) {
			bx += 120
			children.add(ClientPage.text(BODY, children.size, bx, y + 7, 60, 16, ClientPage.FONT_LABEL, "Opacity", ClientPage.TEXT, 0, 1, true))
			bx += 60
			val alphaText: String = if (pickEdit == 2) pickText + "|" else (pick.argb ushr 24).toString()
			editBox(children, bx, y + 6, NUMBER_BOX_WIDTH, alphaText, A_PICK_ALPHA, boxes)
			pickAlphaOutline = boxes[0]
			pickAlphaValue = boxes[1]
			children.add(ClientPage.text(BODY, children.size, bx + NUMBER_BOX_WIDTH + 8, y + 7, 60, 16, ClientPage.FONT_SMALL, "0 to 255", DIM, 0, 1, false))
		}
		if (pickEdit == 1 && pickHexOutline >= 0) {
			children[pickHexOutline].color = ClientPage.BOX_LINE_OPEN
		} else if (pickEdit == 2 && pickAlphaOutline >= 0) {
			children[pickAlphaOutline].color = ClientPage.BOX_LINE_OPEN
		}
		val by: Int = HEIGHT - 29 - 30
		val okWidth: Int = ClientPage.textWidth(ClientPage.FONT_BUTTON, "Cancel") + 30
		button(children, "OK", false, A_PICK_OK, 0, WIDTH / 2 - okWidth - 6, by, okWidth)
		button(children, "Cancel", false, A_PICK_CANCEL, 0, WIDTH / 2 + 6, by, okWidth)
	}

	/** A palette or recent cell: the colour, an outline, and the click. */
	private fun cell(children: ArrayList<Component>, x: Int, y: Int, argb: Int, action: Int, index: Int) {
		children.add(ClientPage.rect(BODY, children.size, x, y, CELL_WIDTH, CELL_HEIGHT, argb and RGB_MASK, true))
		children.add(ClientPage.rect(BODY, children.size, x, y, CELL_WIDTH, CELL_HEIGHT, ClientPage.BOX_FILL, false))
		val hit: Component = ClientPage.text(BODY, children.size, x, y, CELL_WIDTH, CELL_HEIGHT, ClientPage.FONT_SMALL, "", ClientPage.TEXT, 1, 1, false)
		hit.hasScript = true
		hit.addOptionName(0, "Pick")
		hit.opBase = hexOf(argb, false)
		hit.params = arrayOf<Any?>(SENTINEL, action, index)
		children.add(hit)
	}

	/** A box to type into, its click [action]; the slots of its outline and its value text in [slots]. */
	private fun editBox(children: ArrayList<Component>, x: Int, y: Int, width: Int, value: String, action: Int, slots: IntArray) {
		children.add(ClientPage.rect(BODY, children.size, x, y, width, ClientPage.ITEM_HEIGHT + 4, ClientPage.BOX_FILL, true))
		slots[0] = children.size
		children.add(ClientPage.rect(BODY, children.size, x, y, width, ClientPage.ITEM_HEIGHT + 4, ClientPage.BOX_LINE, false))
		slots[1] = children.size
		val text: Component = ClientPage.text(BODY, children.size, x, y, width, ClientPage.ITEM_HEIGHT + 4, ClientPage.FONT_LABEL, value, ClientPage.TEXT, 1, 1, false)
		text.hasScript = true
		text.addOptionName(0, "Edit")
		text.opBase = ""
		text.params = arrayOf<Any?>(SENTINEL, action, 0)
		children.add(text)
	}

	private fun startPickEdit(which: Int) {
		val pick: Pick = picking ?: return
		if (pickEdit == which) {
			endPickEdit(false)
			return
		}
		pickEdit = which
		pickText = if (which == 1) hexOf(pick.argb, pick.alpha) else (pick.argb ushr 24).toString()
		rebuild()
	}

	/** Ends the typing: with [keep], what was typed becomes the colour (a hex that is no colour, or a number out of range, is dropped). */
	private fun endPickEdit(keep: Boolean) {
		val which: Int = pickEdit
		pickEdit = 0
		val pick: Pick? = picking
		if (keep && pick != null) {
			if (which == 1) {
				val parsed: Int? = PluginConfigImpl.parseColour(pickText)
				if (parsed != null) {
					val sixDigits: Boolean = pickText.trim().removePrefix("#").length == 6
					pick.argb = if (!pick.alpha) parsed or ALPHA_MASK else if (sixDigits) (pick.argb and ALPHA_MASK) or (parsed and RGB_MASK) else parsed
				}
			} else if (which == 2) {
				val alpha: Int? = pickText.toIntOrNull()
				if (alpha != null) {
					pick.argb = (alpha.coerceIn(0, 255) shl 24) or (pick.argb and RGB_MASK)
				}
			}
		}
		if (open) {
			rebuild()
		}
	}

	private fun pickKeyTyped(record: KeyRecord) {
		val code: Int = record.getKeyCode()
		val ch: Char = record.getKeyChar()
		if (code == KEY_ENTER) {
			endPickEdit(true)
			return
		}
		if (code == KEY_ESCAPE) {
			endPickEdit(false)
			return
		}
		if (code == KEY_BACKSPACE) {
			if (pickText.isNotEmpty()) {
				pickText = pickText.substring(0, pickText.length - 1)
			}
		} else if (pickEdit == 1 && pickText.length < 9 && (ch.isDigit() || ch.uppercaseChar() in 'A'..'F' || (ch == '#' && pickText.isEmpty()))) {
			pickText += ch
		} else if (pickEdit == 2 && pickText.length < 3 && ch.isDigit()) {
			pickText += ch
		} else {
			return
		}
		val slot: Int = if (pickEdit == 1) pickHexValue else pickAlphaValue
		val c: Component = ClientPage.childOf(BODY, slot) ?: return
		c.text = pickText + "|"
		Component.redrawComponent(c)
	}

	/** The colour the picker shows now, for the smoke test; null when it is not open. */
	@JvmStatic
	fun pickedColour(): Int? {
		return picking?.argb
	}

	/** The smoke test's clicks on the picker: a palette cell, then OK. */
	@JvmStatic
	fun clickPaletteCell(index: Int): Boolean {
		val pick: Pick = picking ?: return false
		pick.argb = (pick.argb and ALPHA_MASK) or (paletteColour(index) and RGB_MASK)
		rebuild()
		return true
	}

	@JvmStatic
	fun clickPickOk(): Boolean {
		if (picking == null) {
			return false
		}
		finishPick(true)
		return true
	}

	// --- typing a number or a line of text ---

	/** The text, or as much of its end as fits in [width] pixels of the label font. */
	private fun fitText(text: String, width: Int): String {
		var shown: String = text
		while (shown.isNotEmpty() && ClientPage.textWidth(ClientPage.FONT_LABEL, shown) > width) {
			shown = shown.substring(1)
		}
		return shown
	}

	/** The value a box shows when it is not being typed into: the number, or the text. */
	private fun settingText(config: PluginConfig, entry: PluginSchemaImpl.Entry): String {
		return if (entry.kind == PluginSchemaImpl.TEXT) config.get(entry.key, entry.defaultText) else config.getInt(entry.key, entry.defaultInt).toString()
	}

	/** The number or text row [index] takes the keyboard: its box lights and shows the value with a cursor. */
	private fun startEdit(index: Int) {
		val registration: PluginRegistration = selected ?: return
		val context: PluginContextImpl = registration.context ?: return
		val entry: PluginSchemaImpl.Entry = entryAt(registration, index) ?: return
		val outline: Int = numberOutline[index] ?: return
		val value: Int = numberValue[index] ?: return
		closeEdit()
		closePopup()
		editing = entry
		editText = settingText(context.config(), entry)
		editOutline = outline
		editValue = value
		ClientPage.setColour(BODY, outline, ClientPage.BOX_LINE_OPEN)
		showEdit()
	}

	private fun showEdit() {
		val c: Component = ClientPage.childOf(BODY, editValue) ?: return
		val entry: PluginSchemaImpl.Entry? = editing
		c.text = if (entry != null && entry.kind == PluginSchemaImpl.TEXT) fitText(editText + "|", TEXT_BOX_WIDTH - 8) else editText + "|"
		Component.redrawComponent(c)
	}

	/** Leaves the box as it was, the typed text dropped. */
	private fun closeEdit() {
		val entry: PluginSchemaImpl.Entry = editing ?: return
		editing = null
		if (!open) {
			return
		}
		ClientPage.setColour(BODY, editOutline, ClientPage.BOX_LINE)
		val registration: PluginRegistration = selected ?: return
		val context: PluginContextImpl = registration.context ?: return
		val c: Component = ClientPage.childOf(BODY, editValue) ?: return
		val text: String = settingText(context.config(), entry)
		c.text = if (entry.kind == PluginSchemaImpl.TEXT) fitText(text, TEXT_BOX_WIDTH - 8) else text
		Component.redrawComponent(c)
	}

	/** Enter: the typed number, held to the entry's range, or the typed text, saved; a number box with nothing typed keeps its value. */
	private fun commitEdit() {
		val entry: PluginSchemaImpl.Entry = editing ?: return
		val registration: PluginRegistration? = selected
		if (entry.kind == PluginSchemaImpl.TEXT) {
			val text: String = editText.trim()
			closeEdit()
			if (registration != null) {
				writeSetting(registration, entry, text, 0, false)
				rebuild()
			}
			return
		}
		val typed: Int? = editText.toIntOrNull()
		closeEdit()
		if (typed == null || registration == null) {
			return
		}
		var value: Int = typed
		if (value < entry.min) {
			value = entry.min
		}
		if (value > entry.max) {
			value = entry.max
		}
		writeSetting(registration, entry, null, value, false)
		rebuild()
	}

	/**
	 * A typed key while a number or text box has the keyboard (from the client's key loop, before
	 * the interfaces and the plugins see it): digits and a leading minus go into a number box, any
	 * printable character into a text box up to its length, Backspace takes one off, Enter keeps
	 * what was typed, Escape drops it. True when the key was ours.
	 */
	@JvmStatic
	fun keyTyped(record: KeyRecord): Boolean {
		if (open && capturing != null) {
			return true // the key itself comes as a press (keyPressed)
		}
		if (open && pickEdit != 0) {
			pickKeyTyped(record)
			return true
		}
		val entry: PluginSchemaImpl.Entry = editing ?: return false
		if (!open) {
			editing = null
			return false
		}
		val code: Int = record.getKeyCode()
		val ch: Char = record.getKeyChar()
		if (code == KEY_ENTER) {
			commitEdit()
		} else if (code == KEY_ESCAPE) {
			closeEdit()
		} else if (code == KEY_BACKSPACE) {
			if (editText.isNotEmpty()) {
				editText = editText.substring(0, editText.length - 1)
				showEdit()
			}
		} else if (entry.kind == PluginSchemaImpl.TEXT) {
			if (ch >= ' ' && ch < '\u007f' && editText.length < entry.max) {
				editText += ch
				showEdit()
			}
		} else if (ch in '0'..'9' && editText.length < MAX_DIGITS) {
			editText += ch
			showEdit()
		} else if (ch == '-' && entry.min < 0 && editText.isEmpty()) {
			editText = "-"
			showEdit()
		}
		return true
	}

	/**
	 * A key press (from the client's key loop, before the server and the plugins hear of it): a keybind
	 * box waiting for its key takes it (Escape leaves the setting, Backspace clears it). While any box
	 * has the keyboard, no key press reaches the server or the interfaces. True when the key was ours.
	 */
	@JvmStatic
	fun keyPressed(record: KeyRecord): Boolean {
		if (!open) {
			return false
		}
		val entry: PluginSchemaImpl.Entry? = capturing
		if (entry != null) {
			val code: Int = record.getKeyCode()
			capturing = null
			val registration: PluginRegistration? = selected
			if (registration != null && code != KEY_ESCAPE) {
				writeSetting(registration, entry, null, if (code == KEY_BACKSPACE) 0 else code, false)
			}
			rebuild()
			return true
		}
		return editing != null || pickEdit != 0
	}

	// --- the tooltip ---

	/** The plugin's description in the stock pale box under its row (above it near the window's bottom). */
	private fun showTip(index: Int) {
		val hit: Component = rowHits[index] ?: return
		val registration: PluginRegistration = PluginRuntime.registrations[index] ?: return
		val rootComponent: Component = Interface.getComponent(ROOT) ?: return
		val bodyComponent: Component = Interface.getComponent(BODY) ?: return
		val popupComponent: Component = Interface.getComponent(POPUP) ?: return
		hideTip()
		var description: String = registration.descriptor.description.trim()
		if (description.isEmpty()) {
			description = "No description."
		}
		val lines: ArrayList<String> = wrap(description, TIP_WIDTH - 10)
		var width: Int = 0
		for (line in lines) {
			width = maxOf(width, ClientPage.textWidth(ClientPage.FONT_SMALL, line))
		}
		width += 10
		val height: Int = lines.size * TIP_LINE_HEIGHT + 6
		val x: Int = rootComponent.x + bodyComponent.x - bodyComponent.scrollX + hit.x + 10
		var y: Int = rootComponent.y + bodyComponent.y - bodyComponent.scrollY + hit.y + hit.height
		if (y + height > rootComponent.y + rootComponent.height - 4) {
			y = rootComponent.y + bodyComponent.y - bodyComponent.scrollY + hit.y - height
		}
		ClientPage.place(popupComponent, x, y, width, height)
		popupComponent.hidden = false
		InterfaceManager.relayoutAgainstParent(popupComponent)
		val children: ArrayList<Component> = ArrayList()
		children.add(ClientPage.rect(POPUP, children.size, 0, 0, width, height, TIP_FILL, true))
		children.add(ClientPage.rect(POPUP, children.size, 0, 0, width, height, TIP_LINE, false))
		for (i in 0 until lines.size) {
			children.add(ClientPage.text(POPUP, children.size, 5, 3 + i * TIP_LINE_HEIGHT, width - 10, TIP_LINE_HEIGHT, ClientPage.FONT_SMALL, lines[i], TIP_LINE, 0, 1, false))
		}
		popupComponent.slotChildren = children.toTypedArray<Component?>()
		Component.layoutChildren(false, popupComponent)
		Component.redrawComponent(popupComponent)
		tipFor = index
	}

	private fun hideTip() {
		if (tipFor < 0) {
			return
		}
		tipFor = -1
		val popupComponent: Component = Interface.getComponent(POPUP) ?: return
		popupComponent.slotChildren = null
		popupComponent.hidden = true
		Component.redrawComponent(popupComponent)
	}

	/** The text in lines no wider than [width] pixels in the small font, broken at spaces. */
	private fun wrap(text: String, width: Int): ArrayList<String> {
		val lines: ArrayList<String> = ArrayList()
		var line: String = ""
		for (word in text.split(' ')) {
			if (word.isEmpty()) {
				continue
			}
			val next: String = if (line.isEmpty()) word else line + " " + word
			if (line.isNotEmpty() && ClientPage.textWidth(ClientPage.FONT_SMALL, next) > width) {
				lines.add(line)
				line = word
			} else {
				line = next
			}
		}
		if (line.isNotEmpty()) {
			lines.add(line)
		}
		return lines
	}

	// --- the choice list ---

	private fun openPopup(choice: Choice) {
		closePopup()
		hideTip()
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
		if (editing != null && action != A_EDIT && action != A_HOVER && action != A_LEAVE && action != A_ROW_HOVER && action != A_ROW_LEAVE && action != A_WHEEL) {
			closeEdit() // a click elsewhere on the page ends the typing
		}
		if (pickEdit != 0 && action != A_PICK_HEX && action != A_PICK_ALPHA && action != A_HOVER && action != A_LEAVE) {
			endPickEdit(false)
		}
		if (capturing != null && action != A_HOVER && action != A_LEAVE && action != A_ROW_HOVER && action != A_ROW_LEAVE && action != A_WHEEL) {
			capturing = null // a click elsewhere leaves the key as it was
			rebuild()
		}
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
			A_WHEEL -> if (plugin != null) {
				val body: Component = Interface.getComponent(BODY) ?: return
				scrollTo(body.scrollY + hook.mouseY * SETTING_PITCH) // the wheel's turns, positive downwards
			}
			A_HOVER -> if (index < buttons.size) ClientPage.paint(buttons[index], true)
			A_LEAVE -> if (index < buttons.size) ClientPage.paint(buttons[index], false)
			A_SET_TOGGLE -> if (plugin != null) {
				val entry: PluginSchemaImpl.Entry = entryAt(plugin, index) ?: return
				val context: PluginContextImpl = plugin.context ?: return
				writeSetting(plugin, entry, null, 0, !context.config().getBoolean(entry.key, entry.defaultInt == 1))
				rebuild()
			}
			A_EDIT -> if (plugin != null) {
				if (editing != null && editing === entryAt(plugin, index)) {
					closeEdit()
				} else {
					startEdit(index)
				}
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
			A_OPEN_PANEL -> if (index < PluginRuntime.count) {
				val opener: (() -> String?)? = PluginRuntime.registrations[index]?.descriptor?.id?.let { panels[it] }
				if (opener != null) {
					close() // the two share the window's shell: one at a time
					val why: String? = opener()
					if (why != null) {
						Console.printToConsole(why)
					}
				}
			}
			A_COLOUR -> if (plugin != null) {
				val entry: PluginSchemaImpl.Entry = entryAt(plugin, index) ?: return
				val context: PluginContextImpl = plugin.context ?: return
				startPick(context.config().getColour(entry.key, entry.defaultInt), entry.max == 1, false) { argb -> writeSetting(plugin, entry, null, argb, false) }
			}
			A_KEYBIND -> if (plugin != null) {
				val entry: PluginSchemaImpl.Entry = entryAt(plugin, index) ?: return
				capturing = entry
				ClientPage.setColour(BODY, keyOutline[index] ?: -1, ClientPage.BOX_LINE_OPEN)
				val c: Component? = ClientPage.childOf(BODY, keyValue[index] ?: -1)
				if (c != null) {
					c.text = "Press a key"
					Component.redrawComponent(c)
				}
			}
			A_PICK_CELL -> picking?.let {
				it.argb = (it.argb and ALPHA_MASK) or (paletteColour(index) and RGB_MASK)
				rebuild()
			}
			A_PICK_RECENT -> picking?.let {
				if (index < recentColours.size) {
					it.argb = if (it.alpha) recentColours[index] else recentColours[index] or ALPHA_MASK
					rebuild()
				}
			}
			A_PICK_HEX -> startPickEdit(1)
			A_PICK_ALPHA -> startPickEdit(2)
			A_PICK_OK -> finishPick(true)
			A_PICK_CANCEL -> finishPick(false)
			A_ROW_HOVER -> if (selected == null) showTip(index)
			A_ROW_LEAVE -> if (tipFor == index) hideTip()
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

	/** Opens the window on plugin [id]'s settings page (a plugin's own Setup, as XP Drops' on the XP button); null when it opened, else why not. */
	@JvmStatic
	fun openSettings(id: String): String? {
		val registration: PluginRegistration = PluginRuntime.find(id) ?: return "No plugin " + id
		val why: String? = open()
		if (why != null) {
			return why
		}
		if (!hasSettings(registration)) {
			return "The plugin is off: turn it on to change its settings"
		}
		selected = registration
		rebuild()
		return null
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

	/** The canvas position of the middle of plugin [id]'s row, for the smoke test's pointer; null when not listed. */
	@JvmStatic
	fun rowCentre(id: String): IntArray? {
		for (i in 0 until PluginRuntime.count) {
			val registration: PluginRegistration = PluginRuntime.registrations[i] ?: continue
			if (registration.descriptor.id == id) {
				val hit: Component = rowHits[i] ?: return null
				return intArrayOf(hit.screenX() + 40, hit.screenY() + hit.height / 2)
			}
		}
		return null
	}

	/** The lines of the tooltip up now, 0 when none. */
	@JvmStatic
	fun tipLines(): Int {
		if (tipFor < 0) {
			return 0
		}
		val popupComponent: Component = Interface.getComponent(POPUP) ?: return 0
		return (popupComponent.slotChildren?.size ?: 2) - 2
	}

	/** Types [text] into the selected plugin's number setting [key] and presses Enter, as a player does. */
	@JvmStatic
	fun typeNumber(key: String, text: String): Boolean {
		val registration: PluginRegistration = selected ?: return false
		val context: PluginContextImpl = registration.context ?: return false
		val entries: ArrayList<PluginSchemaImpl.Entry> = context.schema.entries
		for (i in 0 until entries.size) {
			if (entries[i].key == key && (entries[i].kind == PluginSchemaImpl.NUMBER || entries[i].kind == PluginSchemaImpl.TEXT)) {
				startEdit(i)
				if (editing == null) {
					return false
				}
				editText = text
				commitEdit()
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
