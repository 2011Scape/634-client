package com.jagex.game.runetek5.client.features

import CS2Interpreter
import MapRebuild
import SubInterfaceCloser
import ToolkitSwitch
import com.jagex.game.runetek5.FontMetricsLoader
import com.jagex.game.runetek5.InterfaceManager
import com.jagex.game.runetek5.client.input.console.Console
import com.jagex.game.runetek5.config.Resource
import com.jagex.game.runetek5.config.rsinterface.Component
import com.jagex.game.runetek5.config.rsinterface.ComponentType
import com.jagex.game.runetek5.config.rsinterface.HookRequest
import com.jagex.game.runetek5.config.rsinterface.Interface
import com.jagex.game.runetek5.config.rsinterface.SubInterface
import com.jagex.game.runetek5.graphics.ToolkitType
import com.jagex.game.runetek5.graphics.sprite.StaticMedia
import com.jagex.game.runetek5.preference.GamePreferences
import hdAvailable
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: 634HD's settings as a page of the Graphics Options window
 * (interface 742), built from interface components the way the cache scripts build the stock pages
 * (`cc_create`), with the stock sprites, fonts and colours, so the interface renderer draws it and it
 * fits the window in every display mode. The cache is not edited.
 *
 * The way in is a "634HD" button the client appends to the stock preset row (MIN LOW MID HIGH CUSTOM)
 * of the default page (978) and the Custom page (977); [tick] keeps it there, since the scripts build
 * those rows again after every preset click. The button, or `::hdsettings`, closes the stock page in
 * the window's content layer (742:2) and fills that layer with the 634HD page: a row of presets, a
 * "634HD" on/off dropdown (the toolkit switch), group tabs, the group's rows as stock dropdowns, and
 * Back, which runs the window's load script again (the stock page comes back).
 *
 * Everything is a direct child of a static component (742:2, 742:21 for the dropdown list, the
 * stock preset row), and none of it is a container: the client's `cc_find`, hook drain and click path
 * look one slot level below a static component only, and a created container would be taken for its
 * own siblings' parent (updateComponentAnimations recurses on parentId, without end). A button is
 * its three sprites and its label; the label, sized to the whole button, carries the hooks. A
 * dropdown's hooks sit on an empty text over its box. Hooks on our components carry [SENTINEL] as the script id,
 * and CS2Interpreter.executeHookInternal hands them to [hook]; the row arrows and the list items use
 * the cache's own hover scripts (2691, 1356), which take their targets as (hash, slot) arguments.
 * Desktop only (hdAvailable).
 */
object HdOptionsScreen {

	/** The script id of our hooks: never a cache script. The second element is the action, the third its index. */
	const val SENTINEL: Int = -634

	private const val ROOT: Int = 742 shl 16

	/** The window's content layer: 501 wide, the window's height less 29. */
	private const val BODY: Int = (742 shl 16) or 2

	/** The hidden dropdown list: its box, edge lines, the items' layer (21) and a scrollbar slot (22). */
	private const val POPUP: Int = (742 shl 16) or 16

	private const val POPUP_ITEMS: Int = (742 shl 16) or 21

	private const val POPUP_SCROLL: Int = (742 shl 16) or 22

	/** The stock preset rows, and the CUSTOM button anchored to their right end. */
	private const val ROW_DEFAULT: Int = (978 shl 16) or 35

	private const val CUSTOM_DEFAULT: Int = (978 shl 16) or 40

	private const val ROW_CUSTOM: Int = (977 shl 16) or 9

	private const val CUSTOM_CUSTOM: Int = (977 shl 16) or 14

	private const val EV_COM: Int = -2147483645

	private const val EV_COMSUBID: Int = -2147483643

	// the stock look
	private const val FONT_SMALL: Int = 494
	private const val FONT_LABEL: Int = 495
	private const val FONT_TITLE: Int = 4040
	private const val FONT_BUTTON: Int = 3793
	private const val TEXT: Int = 0xebe0bc
	private const val BOX_FILL: Int = 0x2e2b26
	private const val BOX_LINE: Int = 0x5f5b52
	private const val BOX_LINE_HOVER: Int = 0x80786d
	private const val BOX_LINE_OPEN: Int = 0xb2aa9f
	private const val ITEM_HOVER: Int = 0x26527b

	/** A value at its highest (On, or the last choice), as the stock Custom page colours a maximum. */
	private const val TEXT_TOP: Int = 0x00b1e1
	private const val BEVEL_FILL: Int = 0x383530
	private const val ARROW: Int = 2554
	private const val ARROW_OPEN: Int = 2556
	private const val ROW_PITCH: Int = 20
	private const val ITEM_HEIGHT: Int = 15
	private val BUTTON_NORMAL: IntArray = intArrayOf(4037, 4031, 4034)
	private val BUTTON_HOVER: IntArray = intArrayOf(4038, 4032, 4035)
	private val BUTTON_SELECTED: IntArray = intArrayOf(4039, 4033, 4036)

	// actions
	private const val A_OPEN: Int = 1
	private const val A_BACK: Int = 2
	private const val A_PRESET: Int = 3
	private const val A_TAB: Int = 4
	private const val A_ROW: Int = 5
	private const val A_ITEM: Int = 6
	private const val A_CLOSE: Int = 7
	private const val A_HOVER: Int = 8
	private const val A_LEAVE: Int = 9

	private class Row(val name: String, val label: String, val choices: Array<String>)

	private val ONOFF: Array<String> = arrayOf("off", "on")

	private val TABS: Array<String> = arrayOf("Lighting", "World", "Effects", "Display")

	/** Each tab's rows, top to bottom down the left column, then the right. */
	private val ROWS: Array<Array<Row>> = arrayOf(
		arrayOf(
			Row("lights", "Point lights", ONOFF), Row("lightGrid", "Room light", ONOFF), Row("playerLight", "Carried light", ONOFF),
			Row("pixelLighting", "Per-pixel lighting", ONOFF), Row("lightingModel", "Lighting model", ONOFF), Row("environmentLighting", "Area lighting", ONOFF),
			Row("ambientOcclusion", "Ambient occlusion", ONOFF), Row("shadows", "Shadows", arrayOf("0", "1024", "2048", "4096")), Row("cutoutShadows", "Cut-out shadows", ONOFF),
			Row("sky", "Sky", ONOFF), Row("dayNight", "Day and night", arrayOf("off", "realtime", "10", "30", "60")), Row("fog", "Fog", ONOFF), Row("fade", "Fade-in at distance", ONOFF),
		),
		arrayOf(
			Row("terrainBlend", "Ground blending", ONOFF), Row("worldTextures", "World textures", ONOFF), Row("textureReplacements", "Fresh textures", ONOFF),
			Row("materials", "Material maps", ONOFF), Row("water", "Water", arrayOf("0", "1", "2")), Row("reflections", "Reflections", ONOFF),
			Row("caustics", "Caustics", ONOFF), Row("foliage", "Foliage sway", ONOFF), Row("grassDisplacement", "Grass bends", ONOFF),
		),
		arrayOf(
			Row("particles", "Embers and sparks", ONOFF), Row("softParticles", "Soft particles", ONOFF), Row("bloom", "Bloom", ONOFF),
			Row("tonemap", "HDR tonemapping", arrayOf("off", "aces", "agx")), Row("colourGrading", "Colour grading", ONOFF), Row("fxaa", "FXAA", ONOFF),
			Row("mipmaps", "Mipmaps", ONOFF), Row("anisotropy", "Anisotropic filtering", arrayOf("0", "2", "4", "8", "16")), Row("dither", "Dither", ONOFF),
		),
		arrayOf(
			Row("renderDistance", "Draw distance", arrayOf("1", "1.5", "2")), Row("scale", "Interface scale (next start)", arrayOf("auto", "1", "1.5", "2", "2.5", "3")),
		),
	)

	/** The 634HD on/off row, on the presets line. */
	private val TOGGLE_ROW: Row = Row("634hd", "634HD", ONOFF)

	/** Whether the page is in the window (the stock page closed). */
	@JvmField
	var open: Boolean = false

	/** Set by a choice that changes the ground or the draw distance: the next game tick builds the map again. */
	@JvmField
	var mapRebuildWanted: Boolean = false

	/** The tab shown, kept for the session. */
	private var tab: Int = 0

	/** The window's height before the page set it (the Custom page uses 334 too), restored by Back. */
	private var savedHeight: Int = -1

	private var savedHeightType: Byte = 0

	/** The toolkit to go back to when 634HD is turned off. */
	private var previous: Int = ToolkitType.GL

	/** A row with a dropdown list: its hit component and the slots of its parts under BODY. */
	private class Dropdown(val row: Row, val hit: Component, val outline: Int, val arrow: Int, val valueText: Int)

	/** A preset-style button: its hooked label, the slots of its three sprites under [parentHash], and whether it draws selected. */
	private class Button(val label: Component, val parentHash: Int, val slots: IntArray, val selected: Boolean)

	private val buttons: ArrayList<Button> = ArrayList()

	private val dropdowns: ArrayList<Dropdown> = ArrayList()

	/** The dropdown whose list is open, its values, and what 742:0's click hook was before. */
	private var popupFor: Dropdown? = null

	private var popupValues: Array<String> = arrayOf()

	private var rootClickBefore: Array<Any?>? = null

	/** The "634HD" button in the stock preset row, or null. */
	private var stockButton: Button? = null

	// --- the game tick ---

	/**
	 * Called each game tick after the hook queues drained (before the mini menu's input): keeps the
	 * "634HD" button in the stock preset row, and notices a stock script taking the window back.
	 */
	@JvmStatic
	fun tick() {
		if (!hdAvailable) {
			return
		}
		if (mapRebuildWanted) {
			// a choice changed what the ground or the scene's size is: the map is built again, from the game tick
			mapRebuildWanted = false
			MapRebuild.rebuildMap()
		}
		val sub: SubInterface? = InterfaceManager.OPEN_INTERFACES.get(BODY.toLong()) as SubInterface?
		if (open && sub != null) {
			// a cache script put the stock page back (the window was opened again)
			reset()
		}
		if (sub == null) {
			stockButton = null
			return
		}
		val row: Int
		val custom: Int
		if (sub.interfaceId == 978) {
			row = ROW_DEFAULT
			custom = CUSTOM_DEFAULT
		} else if (sub.interfaceId == 977) {
			row = ROW_CUSTOM
			custom = CUSTOM_CUSTOM
		} else {
			stockButton = null
			return
		}
		val rowComponent: Component = Interface.getComponent(row) ?: return
		val present: Button? = stockButton
		if (present != null && rowComponent.slotChildren != null && present.label.slotId < rowComponent.slotChildren!!.size && rowComponent.slotChildren!![present.label.slotId] === present.label) {
			val selected: Boolean = GamePreferences.uid!!.toolkit!!.getValue() == ToolkitType.HD
			if (selected != present.selected) {
				stockButton = Button(present.label, present.parentHash, present.slots, selected)
				paint(stockButton!!, false)
			}
			return
		}
		// the row is 376 wide with CUSTOM at its right end: wider, CUSTOM where it was, 634HD after it
		val width: Int = textWidth(FONT_BUTTON, "634HD") + 30
		val customComponent: Component? = Interface.getComponent(custom)
		if (rowComponent.baseWidth < 376 + width + 10) {
			rowComponent.baseWidth = 376 + width + 10
			rowComponent.aspectWidthType = 0
			if (customComponent != null) {
				customComponent.basePositionX = width + 10
				customComponent.aspectXType = 2
			}
			InterfaceManager.relayoutAgainstParent(rowComponent)
		}
		val children: ArrayList<Component> = ArrayList()
		val old: Array<Component?>? = rowComponent.slotChildren
		if (old != null) {
			for (c in old) {
				if (c != null) {
					children.add(c)
				}
			}
		}
		val hd: Boolean = GamePreferences.uid!!.toolkit!!.getValue() == ToolkitType.HD
		button(row, children, "634HD", hd, A_OPEN, 0, 376 + 10, 0, width)
		rowComponent.slotChildren = children.toTypedArray<Component?>()
		stockButton = buttons[buttons.size - 1]
		Component.layoutChildren(false, rowComponent)
		Component.redrawComponent(rowComponent)
	}

	// --- opening, building, closing ---

	/** `::hdsettings`: the page, if the Graphics Options window is open. */
	@JvmStatic
	fun openFromCommand() {
		if (!hdAvailable) {
			Console.printToConsole("634HD is not available here")
			return
		}
		if (!windowOpen()) {
			Console.printToConsole("Open Graphics Options first (Options tab, Graphics Settings)")
			return
		}
		if (open) {
			back()
			Console.printToConsole("634HD settings closed")
		} else {
			open()
			Console.printToConsole("634HD settings open")
		}
	}

	/** Whether the Graphics Options window is open somewhere. */
	private fun windowOpen(): Boolean {
		val root: Component = Interface.getComponent(ROOT) ?: return false
		return Component.getParentLayer(root) != null
	}

	/** Takes the window's content layer: the stock page closed, the 634HD page built. */
	@JvmStatic
	fun open() {
		if (!hdAvailable || !windowOpen()) {
			return
		}
		val sub: SubInterface? = InterfaceManager.OPEN_INTERFACES.get(BODY.toLong()) as SubInterface?
		if (sub != null) {
			SubInterfaceCloser.closeSubInterface(true, true, sub)
		}
		stockButton = null
		val root: Component = Interface.getComponent(ROOT) ?: return
		if (savedHeight < 0) {
			savedHeight = root.baseHeight
			savedHeightType = root.aspectHeightType
		}
		root.baseHeight = 334
		root.aspectHeightType = 0
		Component.redrawComponent(root)
		InterfaceManager.relayoutAgainstParent(root)
		Component.layoutChildren(false, root)
		open = true
		rebuild()
	}

	/** Back: the page gone, the window's load script run again (the stock page, with its height). */
	@JvmStatic
	fun back() {
		if (!open) {
			return
		}
		closePopup()
		val body: Component? = Interface.getComponent(BODY)
		if (body != null) {
			body.slotChildren = null
			Component.redrawComponent(body)
		}
		val root: Component? = Interface.getComponent(ROOT)
		if (root != null && savedHeight >= 0) {
			root.baseHeight = savedHeight
			root.aspectHeightType = savedHeightType
			InterfaceManager.relayoutAgainstParent(root)
			Component.layoutChildren(false, root)
		}
		savedHeight = -1
		open = false
		forget()
		CS2Interpreter.runInterfaceOnLoadScripts(742)
	}

	/** The window closed, opened again by the server, or the client logged out: nothing of ours is left. */
	@JvmStatic
	fun reset() {
		if (popupFor != null) {
			closePopup()
		}
		open = false
		savedHeight = -1
		stockButton = null
		forget()
	}

	private fun forget() {
		buttons.clear()
		dropdowns.clear()
	}

	/** Builds the page again from the settings as they are now. */
	private fun rebuild() {
		closePopup()
		val body: Component = Interface.getComponent(BODY) ?: return
		forget()
		val children: ArrayList<Component> = ArrayList()
		val hd: Boolean = GamePreferences.uid!!.toolkit!!.getValue() == ToolkitType.HD
		val width: Int = 501
		// the hint line
		val hint: Component = text(BODY, children.size, 0, 0, width, 20, FONT_SMALL, if (hd) "634HD settings. Changes apply at once and are kept." else "634HD settings. Applies while 634HD is on; kept.", TEXT, 1, 1, false)
		children.add(hint)
		// presets: a titled bevel box, the 634HD on/off dropdown on its title line
		val sx: Int = (width - 475) / 2
		var y: Int = 20
		children.add(text(BODY, children.size, sx + 5, y + 3, 200, 16, FONT_TITLE, "Presets", TEXT, 0, 1, true))
		val toggleWidth: Int = maxOf(textWidth(FONT_SMALL, "Off"), textWidth(FONT_SMALL, "On")) + 21
		children.add(text(BODY, children.size, sx + 475 - 4 - toggleWidth - 6 - 180, y + 3, 180, 16, FONT_LABEL, "634HD (needs OpenGL 3.3)", TEXT, 2, 1, true))
		dropdownRow(children, TOGGLE_ROW, if (hd) "on" else "off", sx + 475 - 4, y + 3, toggleWidth)
		bevelBox(children, sx, y + 22, 475, 50)
		val current: String? = HdSettings.currentPreset()
		var bx: Int = 0
		val widths: IntArray = IntArray(HdSettings.PRESETS.size)
		for (i in HdSettings.PRESETS.indices) {
			widths[i] = textWidth(FONT_BUTTON, capital(HdSettings.PRESETS[i])) + 30
			bx += widths[i] + (if (i > 0) 10 else 0)
		}
		bx = (width - bx) / 2
		for (i in HdSettings.PRESETS.indices) {
			button(BODY, children, capital(HdSettings.PRESETS[i]), HdSettings.PRESETS[i] == current, A_PRESET, i, bx, y + 22 + (50 - 27) / 2, widths[i])
			bx += widths[i] + 10
		}
		// settings: the title line holds the tabs; the box holds the tab's rows in two columns
		y = 94
		children.add(text(BODY, children.size, sx + 5, y + 6, 200, 16, FONT_TITLE, "Settings", TEXT, 0, 1, true))
		var tx: Int = sx + 475
		for (i in TABS.indices.reversed()) {
			val w: Int = textWidth(FONT_BUTTON, TABS[i]) + 30
			tx -= w
			button(BODY, children, TABS[i], i == tab, A_TAB, i, tx, y, w)
			tx -= 6
		}
		bevelBox(children, sx, y + 29, 475, 148)
		val rows: Array<Row> = ROWS[tab]
		var boxWidth: Int = 0
		for (row in rows) {
			for (choice in row.choices) {
				boxWidth = maxOf(boxWidth, textWidth(FONT_SMALL, shown(row.name, choice)))
			}
		}
		boxWidth += 21
		val x0: Int = sx + 6
		var ry: Int = y + 29 + 5
		for (i in rows.indices) {
			val left: Boolean = i % 2 == 0
			val labelX: Int = if (left) x0 else x0 + 465 - 6 - 211
			val boxRight: Int = if (left) x0 + 233 else x0 + 465 - 4
			children.add(text(BODY, children.size, labelX, ry, 211, 16, FONT_LABEL, rows[i].label, TEXT, 0, 1, true))
			dropdownRow(children, rows[i], current(rows[i].name), boxRight, ry, boxWidth)
			if (!left || i == rows.size - 1) {
				if (i < rows.size - 1) {
					children.add(rect(BODY, children.size, sx + 1, ry + 18, 473, 1, BOX_LINE_HOVER, true))
				}
				ry += ROW_PITCH
			}
		}
		// back
		val backWidth: Int = textWidth(FONT_BUTTON, "Back") + 30
		button(BODY, children, "Back", false, A_BACK, 0, (width - backWidth) / 2, 276, backWidth)
		body.slotChildren = children.toTypedArray<Component?>()
		Component.layoutChildren(false, body)
		Component.redrawComponent(body)
	}

	// --- the widgets, as the cache scripts make them ---

	private fun child(parentHash: Int, slot: Int, type: ComponentType): Component {
		val c: Component = Component()
		c.type = type
		c.idHash = parentHash
		c.parentId = parentHash
		c.slotId = slot
		return c
	}

	private fun place(c: Component, x: Int, y: Int, width: Int, height: Int) {
		c.basePositionX = x
		c.basePositionY = y
		c.aspectXType = 0
		c.aspectYType = 0
		c.baseWidth = width
		c.baseHeight = height
		c.aspectWidthType = 0
		c.aspectHeightType = 0
	}

	private fun text(parentHash: Int, slot: Int, x: Int, y: Int, width: Int, height: Int, font: Int, text: String, colour: Int, halign: Int, valign: Int, shadow: Boolean): Component {
		val c: Component = child(parentHash, slot, ComponentType.TEXT)
		place(c, x, y, width, height)
		c.fontId = font
		c.text = text
		c.color = colour
		c.textHorizontalAli = halign
		c.textVerticalAli = valign
		c.shadow = shadow
		return c
	}

	private fun rect(parentHash: Int, slot: Int, x: Int, y: Int, width: Int, height: Int, colour: Int, filled: Boolean): Component {
		val c: Component = child(parentHash, slot, ComponentType.FIGURE)
		place(c, x, y, width, height)
		c.color = colour
		c.filled = filled
		return c
	}

	/** [hflip] mirrors left to right and [vflip] top to bottom (the fields are honest; only the cc_set*flip opcodes are named crosswise). */
	private fun sprite(parentHash: Int, slot: Int, x: Int, y: Int, width: Int, height: Int, spriteId: Int, tiling: Boolean, hflip: Boolean, vflip: Boolean): Component {
		val c: Component = child(parentHash, slot, ComponentType.SPRITE)
		place(c, x, y, width, height)
		c.spriteId = spriteId
		c.tiling = tiling
		c.flipHorizontal = hflip
		c.flipVertical = vflip
		return c
	}

	/** The stock bevelled box: a fill and eight edge pieces. */
	private fun bevelBox(children: ArrayList<Component>, x: Int, y: Int, width: Int, height: Int) {
		children.add(rect(BODY, children.size, x + 8, y + 8, width - 16, height - 16, BEVEL_FILL, true))
		children.add(sprite(BODY, children.size, x, y, 8, 8, 2642, false, false, false))
		children.add(sprite(BODY, children.size, x + width - 8, y, 8, 8, 2642, false, true, false))
		children.add(sprite(BODY, children.size, x, y + height - 8, 8, 8, 2642, false, false, true))
		children.add(sprite(BODY, children.size, x + width - 8, y + height - 8, 8, 8, 2642, false, true, true))
		children.add(sprite(BODY, children.size, x + 8, y, width - 16, 8, 2643, true, false, false))
		children.add(sprite(BODY, children.size, x + 8, y + height - 8, width - 16, 8, 2643, true, false, true))
		children.add(sprite(BODY, children.size, x, y + 8, 8, height - 16, 2644, true, false, false))
		children.add(sprite(BODY, children.size, x + width - 8, y + 8, 8, height - 16, 2644, true, true, false))
	}

	/**
	 * A stock preset-style button at ([x], [y]), [width] by 27, appended to [children] (the next slots
	 * under [parentHash]): the tiled middle, the two caps, and the label over the whole button, which
	 * takes the hover and the click ([action] with [index]).
	 */
	private fun button(parentHash: Int, children: ArrayList<Component>, label: String, selected: Boolean, action: Int, index: Int, x: Int, y: Int, width: Int) {
		val sprites: IntArray = if (selected) BUTTON_SELECTED else BUTTON_NORMAL
		val slots: IntArray = IntArray(3)
		slots[0] = children.size
		children.add(sprite(parentHash, children.size, x + 3, y, width - 6, 26, sprites[0], true, false, false))
		slots[1] = children.size
		children.add(sprite(parentHash, children.size, x, y, 26, 26, sprites[1], false, false, false))
		slots[2] = children.size
		children.add(sprite(parentHash, children.size, x + width - 26, y, 26, 26, sprites[2], false, false, false))
		val t: Component = text(parentHash, children.size, x, y - 3, width, 27, FONT_BUTTON, label, TEXT, 1, 1, false)
		val b: Int = buttons.size
		buttons.add(Button(t, parentHash, slots, selected))
		t.hasScript = true
		t.addOptionName(0, label)
		t.opBase = ""
		t.params = arrayOf<Any?>(SENTINEL, action, index)
		t.onMouseOver = arrayOf<Any?>(SENTINEL, A_HOVER, b)
		t.onMouseLeaveScript = arrayOf<Any?>(SENTINEL, A_LEAVE, b)
		children.add(t)
	}

	/** Sets a button's three sprites: hover, else selected, else normal (what script 3929 does). */
	private fun paint(button: Button, hover: Boolean) {
		val sprites: IntArray = if (hover) BUTTON_HOVER else if (button.selected) BUTTON_SELECTED else BUTTON_NORMAL
		val parent: Component = Interface.getComponent(button.parentHash) ?: return
		val parts: Array<Component?> = parent.slotChildren ?: return
		for (i in 0 until 3) {
			val part: Component = (if (button.slots[i] < parts.size) parts[button.slots[i]] else null) ?: return
			part.spriteId = sprites[i]
			Component.redrawComponent(part)
		}
	}

	/**
	 * A stock dropdown: the value box (fill and outline) with its right edge at [right], the value,
	 * the arrow, and an unseen container over the box that takes the hover (script 2691) and the click.
	 */
	private fun dropdownRow(children: ArrayList<Component>, row: Row, value: String, right: Int, y: Int, boxWidth: Int) {
		val x: Int = right - boxWidth
		children.add(rect(BODY, children.size, x, y, boxWidth, 16, BOX_FILL, true))
		val outline: Int = children.size
		children.add(rect(BODY, children.size, x, y, boxWidth, 16, BOX_LINE, false))
		val valueText: Int = children.size
		children.add(text(BODY, children.size, x + 2, y, boxWidth - 21, 16, FONT_SMALL, shown(row.name, value), valueColour(row, value), 1, 1, false))
		val arrow: Int = children.size
		children.add(sprite(BODY, children.size, right - 17, y, 16, 16, ARROW, false, false, false))
		val hit: Component = text(BODY, children.size, x, y, boxWidth, 16, FONT_SMALL, "", TEXT, 1, 1, false)
		hit.hasScript = true
		hit.addOptionName(0, "Select")
		hit.opBase = row.label
		hit.params = arrayOf<Any?>(SENTINEL, A_ROW, dropdowns.size)
		hit.onMouseOver = arrayOf<Any?>(2691, EV_COM, arrow, 1, outline, BOX_LINE_HOVER, 1)
		hit.onMouseLeaveScript = arrayOf<Any?>(2691, EV_COM, arrow, 0, outline, BOX_LINE, 1)
		children.add(hit)
		dropdowns.add(Dropdown(row, hit, outline, arrow, valueText))
	}

	// --- the dropdown list ---

	/** Opens the list under [d]'s box (above it when it would pass the window's bottom), as script 2695 does. */
	private fun openPopup(d: Dropdown) {
		closePopup()
		val root: Component = Interface.getComponent(ROOT) ?: return
		val body: Component = Interface.getComponent(BODY) ?: return
		val popup: Component = Interface.getComponent(POPUP) ?: return
		val items: Component = Interface.getComponent(POPUP_ITEMS) ?: return
		val current: String = current(d.row.name)
		val values: ArrayList<String> = ArrayList()
		for (choice in d.row.choices) {
			if (choice != current) {
				values.add(choice)
			}
		}
		popupValues = values.toTypedArray()
		val height: Int = ITEM_HEIGHT * values.size + 2
		var y: Int = root.y + body.y - body.scrollY + d.hit.y + d.hit.height - 1
		if (y + height > root.y + root.height) {
			y = root.y + body.y - body.scrollY + d.hit.y - height + 1
		}
		place(popup, root.x + body.x - body.scrollX + d.hit.x, y, d.hit.width, height)
		popup.hidden = false
		InterfaceManager.relayoutAgainstParent(popup)
		items.baseWidth = 2
		items.aspectWidthType = 1
		val children: Array<Component?> = arrayOfNulls(values.size)
		for (i in values.indices) {
			val colour: Int = valueColour(d.row, values[i])
			val t: Component = text(POPUP_ITEMS, i, 0, i * ITEM_HEIGHT, 2, ITEM_HEIGHT, FONT_SMALL, shown(d.row.name, values[i]), colour, 1, 1, false)
			t.aspectXType = 1
			t.aspectWidthType = 1
			t.hasScript = true
			t.addOptionName(0, "Select")
			t.opBase = shown(d.row.name, values[i])
			t.params = arrayOf<Any?>(SENTINEL, A_ITEM, i)
			t.onMouseOver = arrayOf<Any?>(1356, EV_COM, EV_COMSUBID, ITEM_HOVER)
			t.onMouseLeaveScript = arrayOf<Any?>(1356, EV_COM, EV_COMSUBID, colour)
			children[i] = t
		}
		items.slotChildren = children
		items.scrollWidth = 0
		items.scrollHeight = 0
		val scroll: Component? = Interface.getComponent(POPUP_SCROLL)
		if (scroll != null) {
			scroll.hidden = true
		}
		Component.layoutChildren(false, popup)
		Component.redrawComponent(popup)
		// the arrow open, the outline lit, the hover kept off the outline while the list is open
		setSprite(d.arrow, ARROW_OPEN)
		setColour(d.outline, BOX_LINE_OPEN)
		d.hit.onMouseOver = arrayOf<Any?>(2691, EV_COM, d.arrow, 1, d.outline, BOX_LINE_HOVER, 0)
		d.hit.onMouseLeaveScript = arrayOf<Any?>(2691, EV_COM, d.arrow, 0, d.outline, BOX_LINE, 0)
		// a click anywhere on the window closes it (script 2696's way)
		rootClickBefore = root.onClick
		root.onClick = arrayOf<Any?>(SENTINEL, A_CLOSE, 0)
		root.hasScript = true
		popupFor = d
	}

	private fun closePopup() {
		val d: Dropdown = popupFor ?: return
		popupFor = null
		val popup: Component? = Interface.getComponent(POPUP)
		if (popup != null) {
			popup.hidden = true
			Component.redrawComponent(popup)
		}
		val items: Component? = Interface.getComponent(POPUP_ITEMS)
		if (items != null) {
			items.slotChildren = null
		}
		val root: Component? = Interface.getComponent(ROOT)
		if (root != null) {
			root.onClick = rootClickBefore
		}
		rootClickBefore = null
		if (open) {
			setSprite(d.arrow, ARROW)
			setColour(d.outline, BOX_LINE)
			d.hit.onMouseOver = arrayOf<Any?>(2691, EV_COM, d.arrow, 1, d.outline, BOX_LINE_HOVER, 1)
			d.hit.onMouseLeaveScript = arrayOf<Any?>(2691, EV_COM, d.arrow, 0, d.outline, BOX_LINE, 1)
		}
	}

	private fun bodyChild(slot: Int): Component? {
		val body: Component = Interface.getComponent(BODY) ?: return null
		val children: Array<Component?> = body.slotChildren ?: return null
		return if (slot < children.size) children[slot] else null
	}

	private fun setSprite(slot: Int, spriteId: Int) {
		val c: Component = bodyChild(slot) ?: return
		c.spriteId = spriteId
		Component.redrawComponent(c)
	}

	private fun setColour(slot: Int, colour: Int) {
		val c: Component = bodyChild(slot) ?: return
		c.color = colour
		Component.redrawComponent(c)
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
		when (action) {
			A_OPEN -> open()
			A_BACK -> back()
			A_PRESET -> if (open && index < HdSettings.PRESETS.size) preset(HdSettings.PRESETS[index])
			A_TAB -> if (open && index < TABS.size) {
				tab = index
				rebuild()
			}
			A_ROW -> if (open && index < dropdowns.size) {
				val d: Dropdown = dropdowns[index]
				if (popupFor === d) {
					closePopup()
				} else {
					openPopup(d)
				}
			}
			A_ITEM -> if (open && popupFor != null && index < popupValues.size) {
				choose(popupFor!!.row, popupValues[index])
			}
			A_CLOSE -> closePopup()
			A_HOVER -> if (index < buttons.size) paint(buttons[index], true)
			A_LEAVE -> if (index < buttons.size) paint(buttons[index], false)
		}
	}

	private fun preset(name: String) {
		val textures: Boolean = HdSettings.worldTextures
		HdSettings.applyPreset(name)
		HdSettings.save()
		if (HdSettings.worldTextures != textures) {
			// the ground takes its tile overrides when the map is built
			mapRebuildWanted = true
		}
		rebuild()
	}

	/** A value chosen in a row's list: set, saved, applied; the page built again. */
	private fun choose(row: Row, value: String) {
		closePopup()
		if (row === TOGGLE_ROW) {
			toggle()
			rebuild()
			return
		}
		HdSettings.set(row.name, value)
		HdSettings.save()
		if (row.name == "worldTextures") {
			mapRebuildWanted = true
		}
		if (row.name == "renderDistance") {
			// live: the scene is built again at the new distance
			ClientFeatures.RENDER_DISTANCE = HdSettings.renderDistance
			mapRebuildWanted = true
		}
		rebuild()
	}

	/**
	 * 634HD on or off: to toolkit 6, or back to the toolkit before it (OpenGL if none), saved as the
	 * default. The first time it is turned on with nothing saved, the Medium preset is applied, so the
	 * switch changes something visible. A failed switch falls back and says why (HdFallback).
	 */
	@JvmStatic
	fun toggle() {
		if (!hdAvailable) {
			return
		}
		val current: Int = GamePreferences.uid!!.toolkit!!.getValue()
		val target: Int
		if (current == ToolkitType.HD) {
			target = previous
		} else {
			previous = current
			target = ToolkitType.HD
		}
		ToolkitSwitch.loadAssets(false, target)
		val now: Int = GamePreferences.uid!!.toolkit!!.getValue()
		if (now == target && target == ToolkitType.HD && !HdSettings.configured) {
			HdSettings.applyPreset("medium")
			HdSettings.save()
		}
		if (now == target) {
			GamePreferences.uid!!.setValue(GamePreferences.uid!!.toolkitDefault, target)
			GamePreferences.savePreferences()
			GamePreferences.preferencesSavedOnServer = false
			Console.printToConsole(if (target == ToolkitType.HD) "634HD on" else "634HD off")
		}
	}

	// --- values ---

	/** A row's value now: the draw distance in use (not only the saved one), else the setting. */
	private fun current(name: String): String = when (name) {
		"634hd" -> if (GamePreferences.uid!!.toolkit!!.getValue() == ToolkitType.HD) "on" else "off"
		"renderDistance" -> HdSettings.number(ClientFeatures.RENDER_DISTANCE)
		else -> HdSettings.valueOf(name)
	}

	private fun shown(name: String, value: String): String = when (name) {
		"renderDistance" -> value + "x"
		"dayNight" -> if (value == "off") "Off" else if (value == "realtime") "Clock" else value + " min"
		"scale" -> if (value == "auto") "Auto" else value + "x"
		"tonemap" -> if (value == "aces") "ACES" else if (value == "agx") "AgX" else "Off"
		"shadows" -> if (value == "0") "Off" else value
		"anisotropy" -> if (value == "0") "Off" else value + "x"
		"water" -> if (value == "2") "634HD" else if (value == "1") "Game" else "Off"
		else -> capital(value)
	}

	/** The value text's colour: blue at the row's highest choice (On, 4096, 16x, ...), else the stock text colour. */
	private fun valueColour(row: Row, value: String): Int = if (value == row.choices[row.choices.size - 1]) TEXT_TOP else TEXT

	private fun capital(text: String): String = if (text.isEmpty()) text else text.substring(0, 1).uppercase() + text.substring(1)

	/** The width of [text] in the cache font [fontId] (what the scripts' STRINGWIDTH gives), or a guess before the font loads. */
	private fun textWidth(fontId: Int, text: String): Int {
		val metrics = FontMetricsLoader.getFontMetrics(fontId, Resource.FONT_METRICS)
		return metrics?.getTextWidth(text, StaticMedia.nameIconSprites) ?: (text.length * 7)
	}

	// --- for the smoke test: the same actions the clicks take ---

	@JvmStatic
	fun stockButtonPresent(): Boolean = stockButton != null

	@JvmStatic
	fun clickStockButton(): Boolean {
		if (stockButton == null) {
			return false
		}
		open()
		return open
	}

	@JvmStatic
	fun clickPreset(name: String): Boolean {
		if (!open) {
			return false
		}
		for (i in HdSettings.PRESETS.indices) {
			if (HdSettings.PRESETS[i] == name) {
				preset(name)
				return true
			}
		}
		return false
	}

	@JvmStatic
	fun clickTab(name: String): Boolean {
		if (!open) {
			return false
		}
		for (i in TABS.indices) {
			if (TABS[i] == name) {
				tab = i
				rebuild()
				return true
			}
		}
		return false
	}

	/** Opens the row's list (switching to its tab first), as a click on its box does. */
	@JvmStatic
	fun openRow(name: String): Boolean {
		if (!open) {
			return false
		}
		for (t in TABS.indices) {
			for (row in ROWS[t]) {
				if (row.name == name) {
					if (t != tab) {
						tab = t
						rebuild()
					}
					for (d in dropdowns) {
						if (d.row === row) {
							openPopup(d)
							return popupFor === d && popupValues.isNotEmpty()
						}
					}
				}
			}
		}
		return false
	}

	/** Whether the list is open, and its items. */
	@JvmStatic
	fun popupOpen(): Boolean = popupFor != null

	/** Chooses the list's first item (the value after the current one), as a click on it does. */
	@JvmStatic
	fun choosePopupItem(): Boolean {
		val d: Dropdown = popupFor ?: return false
		if (popupValues.isEmpty()) {
			return false
		}
		choose(d.row, popupValues[0])
		return true
	}

	/** Opens the row's list and chooses the value after the current one. */
	@JvmStatic
	fun cycleRow(name: String): Boolean = openRow(name) && choosePopupItem()

	@JvmStatic
	fun clickToggle(): Boolean {
		if (!open) {
			return false
		}
		choose(TOGGLE_ROW, "")
		return true
	}

	@JvmStatic
	fun clickBack(): Boolean {
		if (!open) {
			return false
		}
		back()
		return true
	}
}
