package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.FontMetricsLoader
import com.jagex.game.runetek5.InterfaceManager
import com.jagex.game.runetek5.config.Resource
import com.jagex.game.runetek5.config.rsinterface.Component
import com.jagex.game.runetek5.config.rsinterface.ComponentType
import com.jagex.game.runetek5.config.rsinterface.Interface
import com.jagex.game.runetek5.graphics.sprite.StaticMedia
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: the pieces a client-built page is made of, as the cache scripts
 * make them (`cc_create`) and with the stock sprites, fonts and colours, shared by the 634HD page
 * (HdOptionsScreen) and the plugin manager (PluginManagerScreen). Every piece is a flat slot child
 * of a static component and none is a container (a created container recurses in
 * updateComponentAnimations); a button is three sprites and a label that carries the hooks, whose
 * script id is the page's sentinel (a negative number CS2Interpreter.executeHookInternal routes
 * back to the page).
 */
object ClientPage {

	const val EV_COM: Int = -2147483645

	const val EV_COMSUBID: Int = -2147483643

	// the stock look
	const val FONT_SMALL: Int = 494
	const val FONT_LABEL: Int = 495
	const val FONT_TITLE: Int = 4040
	const val FONT_BUTTON: Int = 3793
	const val TEXT: Int = 0xebe0bc
	const val BOX_FILL: Int = 0x2e2b26
	const val BOX_LINE: Int = 0x5f5b52
	const val BOX_LINE_HOVER: Int = 0x80786d
	const val BOX_LINE_OPEN: Int = 0xb2aa9f
	const val ITEM_HOVER: Int = 0x26527b

	/** A value at its highest (On, or the last choice), as the stock Custom page colours a maximum. */
	const val TEXT_TOP: Int = 0x00b1e1
	const val BEVEL_FILL: Int = 0x383530
	const val ARROW: Int = 2554
	const val ARROW_OPEN: Int = 2556
	const val ROW_PITCH: Int = 20

	/** A tickbox's square, and the mark inside it when it is on. */
	const val TICK_SIZE: Int = 16

	const val TICK_MARK: Int = 8
	const val ITEM_HEIGHT: Int = 15

	/** A dropdown list's own scrollbar, its smallest thumb, the window's title bar it stays under, the gap it keeps from the window's edge, its fewest rows, and the rows a turn of the wheel moves. */
	const val POPUP_SCROLLBAR: Int = 8
	const val THUMB_MIN: Int = 12
	const val POPUP_TITLE: Int = 29
	const val POPUP_MARGIN: Int = 4
	const val POPUP_MIN_ROWS: Int = 4
	const val POPUP_WHEEL_ROWS: Int = 2

	@JvmField
	val BUTTON_NORMAL: IntArray = intArrayOf(4037, 4031, 4034)

	@JvmField
	val BUTTON_HOVER: IntArray = intArrayOf(4038, 4032, 4035)

	@JvmField
	val BUTTON_SELECTED: IntArray = intArrayOf(4039, 4033, 4036)

	/** A preset-style button: its hooked label, the slots of its three sprites under [parentHash], and whether it draws selected. */
	class Button(@JvmField val label: Component, @JvmField val parentHash: Int, @JvmField val slots: IntArray, @JvmField val selected: Boolean)

	@JvmStatic
	fun child(parentHash: Int, slot: Int, type: ComponentType): Component {
		val c: Component = Component()
		c.type = type
		c.idHash = parentHash
		c.parentId = parentHash
		c.slotId = slot
		return c
	}

	@JvmStatic
	fun place(c: Component, x: Int, y: Int, width: Int, height: Int) {
		c.basePositionX = x
		c.basePositionY = y
		c.aspectXType = 0
		c.aspectYType = 0
		c.baseWidth = width
		c.baseHeight = height
		c.aspectWidthType = 0
		c.aspectHeightType = 0
	}

	@JvmStatic
	fun text(parentHash: Int, slot: Int, x: Int, y: Int, width: Int, height: Int, font: Int, text: String, colour: Int, halign: Int, valign: Int, shadow: Boolean): Component {
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

	@JvmStatic
	fun rect(parentHash: Int, slot: Int, x: Int, y: Int, width: Int, height: Int, colour: Int, filled: Boolean): Component {
		val c: Component = child(parentHash, slot, ComponentType.FIGURE)
		place(c, x, y, width, height)
		c.color = colour
		c.filled = filled
		return c
	}

	/** [hflip] mirrors left to right and [vflip] top to bottom (the fields are honest; only the cc_set*flip opcodes are named crosswise). */
	@JvmStatic
	fun sprite(parentHash: Int, slot: Int, x: Int, y: Int, width: Int, height: Int, spriteId: Int, tiling: Boolean, hflip: Boolean, vflip: Boolean): Component {
		val c: Component = child(parentHash, slot, ComponentType.SPRITE)
		place(c, x, y, width, height)
		c.spriteId = spriteId
		c.tiling = tiling
		c.flipHorizontal = hflip
		c.flipVertical = vflip
		return c
	}

	/** The stock bevelled box under [parentHash]: a fill and eight edge pieces, appended to [children]. */
	@JvmStatic
	fun bevelBox(parentHash: Int, children: ArrayList<Component>, x: Int, y: Int, width: Int, height: Int) {
		children.add(rect(parentHash, children.size, x + 8, y + 8, width - 16, height - 16, BEVEL_FILL, true))
		children.add(sprite(parentHash, children.size, x, y, 8, 8, 2642, false, false, false))
		children.add(sprite(parentHash, children.size, x + width - 8, y, 8, 8, 2642, false, true, false))
		children.add(sprite(parentHash, children.size, x, y + height - 8, 8, 8, 2642, false, false, true))
		children.add(sprite(parentHash, children.size, x + width - 8, y + height - 8, 8, 8, 2642, false, true, true))
		children.add(sprite(parentHash, children.size, x + 8, y, width - 16, 8, 2643, true, false, false))
		children.add(sprite(parentHash, children.size, x + 8, y + height - 8, width - 16, 8, 2643, true, false, true))
		children.add(sprite(parentHash, children.size, x, y + 8, 8, height - 16, 2644, true, false, false))
		children.add(sprite(parentHash, children.size, x + width - 8, y + 8, 8, height - 16, 2644, true, true, false))
	}

	/**
	 * A stock preset-style button at ([x], [y]), [width] by 27, appended to [children] (the next slots
	 * under [parentHash]): the tiled middle, the two caps, and the label over the whole button, which
	 * takes the click as `[sentinel, action, index]` and the hover and leave as `[sentinel, hoverAction |
	 * leaveAction, hoverIndex]` (the page paints the button on those with [paint]).
	 */
	@JvmStatic
	fun button(parentHash: Int, children: ArrayList<Component>, label: String, selected: Boolean, sentinel: Int, action: Int, index: Int, hoverAction: Int, leaveAction: Int, hoverIndex: Int, x: Int, y: Int, width: Int): Button {
		val sprites: IntArray = if (selected) BUTTON_SELECTED else BUTTON_NORMAL
		val slots: IntArray = IntArray(3)
		slots[0] = children.size
		children.add(sprite(parentHash, children.size, x + 3, y, width - 6, 26, sprites[0], true, false, false))
		slots[1] = children.size
		children.add(sprite(parentHash, children.size, x, y, 26, 26, sprites[1], false, false, false))
		slots[2] = children.size
		children.add(sprite(parentHash, children.size, x + width - 26, y, 26, 26, sprites[2], false, false, false))
		val t: Component = text(parentHash, children.size, x, y - 3, width, 27, FONT_BUTTON, label, TEXT, 1, 1, false)
		t.hasScript = true
		t.addOptionName(0, label)
		t.opBase = ""
		t.params = arrayOf<Any?>(sentinel, action, index)
		t.onMouseOver = arrayOf<Any?>(sentinel, hoverAction, hoverIndex)
		t.onMouseLeaveScript = arrayOf<Any?>(sentinel, leaveAction, hoverIndex)
		children.add(t)
		return Button(t, parentHash, slots, selected)
	}

	/**
	 * A tickbox: the dropdown box's fill and outline, [TICK_SIZE] square, with a [TICK_MARK] square
	 * of [TEXT_TOP] inside when [on]; a click runs [action] with [index] through [sentinel]. Returns
	 * the hit component, whose option is [optionName].
	 */
	@JvmStatic
	fun tickbox(parentHash: Int, children: ArrayList<Component>, on: Boolean, sentinel: Int, action: Int, index: Int, x: Int, y: Int, optionName: String): Component {
		children.add(rect(parentHash, children.size, x, y, TICK_SIZE, TICK_SIZE, BOX_FILL, true))
		children.add(rect(parentHash, children.size, x, y, TICK_SIZE, TICK_SIZE, BOX_LINE, false))
		if (on) {
			val inset: Int = (TICK_SIZE - TICK_MARK) / 2
			children.add(rect(parentHash, children.size, x + inset, y + inset, TICK_MARK, TICK_MARK, TEXT_TOP, true))
		}
		val hit: Component = text(parentHash, children.size, x, y, TICK_SIZE, TICK_SIZE, FONT_SMALL, "", TEXT, 1, 1, false)
		hit.hasScript = true
		hit.addOptionName(0, optionName)
		hit.opBase = ""
		hit.params = arrayOf<Any?>(sentinel, action, index)
		children.add(hit)
		return hit
	}

	/** Sets a button's three sprites: hover, else selected, else normal (what script 3929 does). */
	@JvmStatic
	fun paint(button: Button, hover: Boolean) {
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
	 * A stock dropdown's box with its right edge at [right]: the fill, the outline (returned), the value
	 * text, the arrow, and an unseen text over the box that takes the hover (cache script 2691) and the
	 * click as `[sentinel, action, index]`. The slots are under [parentHash]; the hit component is returned
	 * with the slots of the outline, the arrow and the value text in [slots] (three ints).
	 */
	@JvmStatic
	fun dropdown(parentHash: Int, children: ArrayList<Component>, label: String, value: String, valueColour: Int, sentinel: Int, action: Int, index: Int, right: Int, y: Int, boxWidth: Int, slots: IntArray): Component {
		val x: Int = right - boxWidth
		children.add(rect(parentHash, children.size, x, y, boxWidth, 16, BOX_FILL, true))
		slots[0] = children.size
		children.add(rect(parentHash, children.size, x, y, boxWidth, 16, BOX_LINE, false))
		slots[2] = children.size
		children.add(text(parentHash, children.size, x + 2, y, boxWidth - 21, 16, FONT_SMALL, value, valueColour, 1, 1, false))
		slots[1] = children.size
		children.add(sprite(parentHash, children.size, right - 17, y, 16, 16, ARROW, false, false, false))
		val hit: Component = text(parentHash, children.size, x, y, boxWidth, 16, FONT_SMALL, "", TEXT, 1, 1, false)
		hit.hasScript = true
		hit.addOptionName(0, "Select")
		hit.opBase = label
		hit.params = arrayOf<Any?>(sentinel, action, index)
		hit.onMouseOver = arrayOf<Any?>(2691, EV_COM, slots[1], 1, slots[0], BOX_LINE_HOVER, 1)
		hit.onMouseLeaveScript = arrayOf<Any?>(2691, EV_COM, slots[1], 0, slots[0], BOX_LINE, 1)
		children.add(hit)
		return hit
	}

	/** A slot child of [parentHash] by slot, or null. */
	@JvmStatic
	fun childOf(parentHash: Int, slot: Int): Component? {
		val parent: Component = Interface.getComponent(parentHash) ?: return null
		val children: Array<Component?> = parent.slotChildren ?: return null
		return if (slot < children.size) children[slot] else null
	}

	@JvmStatic
	fun setSprite(parentHash: Int, slot: Int, spriteId: Int) {
		val c: Component = childOf(parentHash, slot) ?: return
		c.spriteId = spriteId
		Component.redrawComponent(c)
	}

	@JvmStatic
	fun setColour(parentHash: Int, slot: Int, colour: Int) {
		val c: Component = childOf(parentHash, slot) ?: return
		c.color = colour
		Component.redrawComponent(c)
	}

	@JvmStatic
	fun capital(text: String): String = if (text.isEmpty()) text else text.substring(0, 1).uppercase() + text.substring(1)

	/** The width of [text] in the cache font [fontId] (what the scripts' STRINGWIDTH gives), or a guess before the font loads. */
	@JvmStatic
	fun textWidth(fontId: Int, text: String): Int {
		val metrics = FontMetricsLoader.getFontMetrics(fontId, Resource.FONT_METRICS)
		return metrics?.getTextWidth(text, StaticMedia.nameIconSprites) ?: (text.length * 7)
	}

	/**
	 * The hidden dropdown list of the Graphics Options window (742:16, items in 742:21, a scrollbar
	 * slot 742:22), opened under a box by a page: the items are texts with `[sentinel, itemAction, i]`
	 * as their click and the cache's hover script 1356; a click anywhere on the window root closes it
	 * (`[sentinel, closeAction, 0]`, script 2696's way). One list is open at a time.
	 */
	class Popup(private val root: Int, private val body: Int, private val popup: Int, private val items: Int, private val scroll: Int, private val sentinel: Int, private val itemAction: Int, private val closeAction: Int, private val wheelAction: Int = -1) {

		@JvmField
		var values: Array<String> = arrayOf()

		@JvmField
		var open: Boolean = false

		private var rootClickBefore: Array<Any?>? = null

		/** Opens the list under [hit] (above it when it would pass the window's bottom) with [shown] as the items' texts and [colours] their colours. */
		fun open(hit: Component, values: Array<String>, shown: Array<String>, colours: IntArray) {
			close()
			val rootComponent: Component = Interface.getComponent(root) ?: return
			val bodyComponent: Component = Interface.getComponent(body) ?: return
			val popupComponent: Component = Interface.getComponent(popup) ?: return
			val itemsComponent: Component = Interface.getComponent(items) ?: return
			this.values = values
			// under the box, or above it when there is more room there; a list longer than the room scrolls (with a wheel action to scroll by)
			val hitTop: Int = rootComponent.y + bodyComponent.y - bodyComponent.scrollY + hit.y
			val below: Int = rootComponent.y + rootComponent.height - (hitTop + hit.height - 1) - POPUP_MARGIN
			val above: Int = hitTop + 1 - (rootComponent.y + POPUP_TITLE) - POPUP_MARGIN
			val room: Int = if (wheelAction >= 0) maxOf(below, above) else Int.MAX_VALUE
			val rows: Int = minOf(values.size, maxOf(POPUP_MIN_ROWS, (room - 2) / ITEM_HEIGHT))
			scrolls = rows < values.size
			val height: Int = ITEM_HEIGHT * rows + 2
			var y: Int = hitTop + hit.height - 1
			if (y + height > rootComponent.y + rootComponent.height) {
				y = hitTop - height + 1
			}
			place(popupComponent, rootComponent.x + bodyComponent.x - bodyComponent.scrollX + hit.x, y, hit.width, height)
			popupComponent.hidden = false
			InterfaceManager.relayoutAgainstParent(popupComponent)
			itemsComponent.baseWidth = if (scrolls) 2 + POPUP_SCROLLBAR else 2
			itemsComponent.aspectWidthType = 1
			val children: Array<Component?> = arrayOfNulls(values.size)
			for (i in values.indices) {
				val t: Component = text(items, i, 0, i * ITEM_HEIGHT, 2, ITEM_HEIGHT, FONT_SMALL, shown[i], colours[i], 1, 1, false)
				t.aspectXType = 1
				t.aspectWidthType = 1
				t.hasScript = true
				t.addOptionName(0, "Select")
				t.opBase = shown[i]
				t.params = arrayOf<Any?>(sentinel, itemAction, i)
				t.onMouseOver = arrayOf<Any?>(1356, EV_COM, EV_COMSUBID, ITEM_HOVER)
				t.onMouseLeaveScript = arrayOf<Any?>(1356, EV_COM, EV_COMSUBID, colours[i])
				children[i] = t
			}
			itemsComponent.slotChildren = children
			itemsComponent.scrollWidth = 0
			itemsComponent.scrollHeight = if (scrolls) ITEM_HEIGHT * values.size else 0
			itemsComponent.scrollY = 0
			itemsComponent.hasScript = scrolls
			itemsComponent.onMouseMove = if (scrolls) arrayOf<Any?>(sentinel, wheelAction, 0) else null
			val scrollComponent: Component? = Interface.getComponent(scroll)
			if (scrollComponent != null) {
				scrollComponent.hidden = true
			}
			// our own scrollbar beside the items, as the settings page has one: a track and a thumb
			popupComponent.slotChildren = if (scrolls) {
				arrayOf<Component?>(
					rect(popup, 0, hit.width - POPUP_SCROLLBAR, 1, POPUP_SCROLLBAR - 1, height - 2, BOX_FILL, true),
					rect(popup, 1, hit.width - POPUP_SCROLLBAR, 1, POPUP_SCROLLBAR - 1, maxOf(THUMB_MIN, (height - 2) * rows / values.size), TEXT_TOP, true),
				)
			} else {
				null
			}
			Component.layoutChildren(false, popupComponent)
			Component.redrawComponent(popupComponent)
			rootClickBefore = rootComponent.onClick
			rootComponent.onClick = arrayOf<Any?>(sentinel, closeAction, 0)
			rootComponent.hasScript = true
			open = true
		}

		/** Whether the open list is longer than its room, and scrolls. */
		@JvmField
		var scrolls: Boolean = false

		/** Scrolls the open list by [turns] of the wheel (positive downwards), held to its ends; the thumb follows. */
		fun scrollBy(turns: Int) {
			if (!open || !scrolls) {
				return
			}
			val popupComponent: Component = Interface.getComponent(popup) ?: return
			val itemsComponent: Component = Interface.getComponent(items) ?: return
			val visible: Int = itemsComponent.height
			val most: Int = maxOf(0, itemsComponent.scrollHeight - visible)
			itemsComponent.scrollY = (itemsComponent.scrollY + turns * ITEM_HEIGHT * POPUP_WHEEL_ROWS).coerceIn(0, most)
			val thumb: Component? = popupComponent.slotChildren?.getOrNull(1)
			if (thumb != null && most > 0) {
				thumb.basePositionY = 1 + (popupComponent.height - 2 - thumb.baseHeight) * itemsComponent.scrollY / most
				Component.layoutChildren(false, popupComponent)
			}
			Component.redrawComponent(popupComponent)
		}

		/** The open list's scroll position and its most, for the smoke test. */
		fun scrollPosition(): IntArray {
			val itemsComponent: Component = Interface.getComponent(items) ?: return intArrayOf(0, 0)
			return intArrayOf(itemsComponent.scrollY, maxOf(0, itemsComponent.scrollHeight - itemsComponent.height))
		}

		fun close() {
			if (!open) {
				return
			}
			open = false
			scrolls = false
			val popupComponent: Component? = Interface.getComponent(popup)
			if (popupComponent != null) {
				popupComponent.hidden = true
				popupComponent.slotChildren = null
				Component.redrawComponent(popupComponent)
			}
			val itemsComponent: Component? = Interface.getComponent(items)
			if (itemsComponent != null) {
				itemsComponent.slotChildren = null
				itemsComponent.scrollY = 0
				itemsComponent.scrollHeight = 0
				itemsComponent.hasScript = false
				itemsComponent.onMouseMove = null
			}
			val rootComponent: Component? = Interface.getComponent(root)
			if (rootComponent != null) {
				rootComponent.onClick = rootClickBefore
			}
			rootClickBefore = null
		}
	}
}
