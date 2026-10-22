package com.jagex.game.runetek5.plugin

import awt.CursorType
import awt.getDefaultCursor
import awt.getPredefinedCursor
import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.client.GameShell
import com.jagex.game.runetek5.client.input.keyboard.KeyRecorder
import com.jagex.game.runetek5.client.input.mouse.CursorManager
import com.jagex.game.runetek5.client.input.mouse.MouseRecord
import com.jagex.game.runetek5.client.menu.MiniMenu
import com.jagex.game.runetek5.client.menu.MiniMenuEntry
import com.jagex.game.runetek5.config.rsinterface.Component
import com.jagex.game.runetek5.config.rsinterface.Interface
import com.jagex.game.runetek5.core.constants.MiniMenuAction
import com.jagex.game.runetek5.client.event.mouse.MouseLog
import com.jagex.game.runetek5.plugin.api.Font
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.KeyCode
import com.jagex.game.runetek5.plugin.api.OverlayMenuEntry
import com.jagex.game.runetek5.plugin.api.OverlayOrigin
import com.jagex.game.runetek5.plugin.api.OverlayOriginX
import com.jagex.game.runetek5.plugin.api.OverlayOriginY
import com.jagex.game.runetek5.plugin.api.OverlayPosition
import com.jagex.game.runetek5.plugin.api.PanelOverlay
import com.jagex.game.runetek5.plugin.api.Size
import com.jagex.game.runetek5.plugin.api.Viewport
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: RuneLite's OverlayManager and OverlayRenderer for the plugins'
 * [PanelOverlay]s. Each frame the panels are laid out in the game view's corners, stacked in order of
 * priority, or drawn where the player put them; holding the drag key (Alt) shows their outlines and the
 * snap corners, and a press on a panel then drags it (or, on a resizable one's edge, resizes it) without
 * the game seeing the click, a right press puts it back, and letting go over a corner snaps it there. The
 * places are kept in `plugin-config/overlay-layout.properties`, under RuneLite's key names. Shift and a
 * right click on a panel adds its menu entries (and an Overlay Origin submenu) to the game's menu.
 *
 * Game thread only. The infobox panels (InfoBoxes) are panels of the client's own.
 */
@Suppress("TooGenericExceptionCaught") // a panel that throws disables its plugin and the game goes on
object OverlayLayout {

	/** One panel and whose it is: null for the client's own (the infobox panels, the snap corners). */
	class Entry(@JvmField val overlay: PanelOverlay, @JvmField val owner: PluginRegistration?, @JvmField val serial: Int) {
		/** What its place is kept under: the plugin's id and the panel's name, or the name alone for the client's. */
		@JvmField
		val key: String = if (owner == null) overlay.name else owner.descriptor.id + "." + overlay.name

		/** Where it was drawn this frame, for the hover and the drag; empty when nothing was. */
		@JvmField var x: Int = 0
		@JvmField var y: Int = 0
		@JvmField var width: Int = 0
		@JvmField var height: Int = 0

		fun contains(px: Int, py: Int): Boolean = width > 0 && height > 0 && px >= x && py >= y && px < x + width && py < y + height

		fun intersects(other: Entry): Boolean = width > 0 && height > 0 && other.width > 0 && other.height > 0 &&
			x < other.x + other.width && other.x < x + width && y < other.y + other.height && other.y < y + height
	}

	/** RuneLite's OverlayRenderer constants. */
	private const val PADDING: Int = 2
	private const val RESIZE_TOLERANCE: Int = 5
	private const val MOVING: Int = 0x64FFFF00
	private const val MOVING_ACTIVE: Int = 0xC8FFFF00.toInt()
	private const val MOVING_TARGET: Int = 0xFFFF0000.toInt()
	private const val MOVING_RESIZING: Int = 0xC8FF00FF.toInt()

	/** RuneLite's Rectangle.outcode bits. */
	private const val OUT_LEFT: Int = 1
	private const val OUT_TOP: Int = 2
	private const val OUT_RIGHT: Int = 4
	private const val OUT_BOTTOM: Int = 8

	/** The game screen's parts, from Void's interface_types.toml: the chat box and the tabs' content, fixed (548) and resizable (746). */
	private const val FIXED_ROOT: Int = 548
	private const val FIXED_CHAT: Int = 191
	private const val RESIZABLE_CHAT: Int = 68
	private const val FIXED_SIDE: Int = 206
	private const val RESIZABLE_SIDE: Int = 90

	/**
	 * The resizable screen's corners that cover the game view, from Void's gameframe.ifaces.toml: the
	 * minimap's frame (the XP orb, compass, world map, adviser and logout buttons, and the four orbs) at the
	 * top right, and the tab buttons (35 to 50) at the bottom right.
	 */
	private val RESIZABLE_MAP_PARTS: IntArray = intArrayOf(224, 170, 178, 171, 172, 173, 174, 175, 176)
	private const val RESIZABLE_FIRST_TAB: Int = 35
	private const val RESIZABLE_LAST_TAB: Int = 50

	/** RuneLite's keys, in the layout's own file. */
	private const val PREFERRED_LOCATION: String = "_preferredLocation"
	private const val PREFERRED_POSITION: String = "_preferredPosition"
	private const val PREFERRED_SIZE: String = "_preferredSize"
	private const val ORIGIN: String = "_origin"
	private const val ORIGIN_X: String = "_originX"
	private const val ORIGIN_Y: String = "_originY"

	/** The menu's target colour (RuneLite's JagexColors.MENU_TARGET). */
	private const val MENU_TARGET: String = "<col=ff9040>"

	/* The Overlays settings (OverlaySettings), with RuneLite's defaults. */
	@JvmField var dragKey: Int = KeyCode.ALT
	@JvmField var background: Int = PanelOverlay.STANDARD_BACKGROUND
	@JvmField var interfaceFont: Int = Font.REGULAR
	@JvmField var tooltipFont: Int = Font.SMALL
	@JvmField var infoboxFont: Int = Font.REGULAR
	@JvmField var tooltipAbove: Boolean = false
	@JvmField var infoBoxSize: Int = 35
	@JvmField var infoBoxTextOutline: Boolean = false

	@JvmField
	val entries: ArrayList<Entry> = ArrayList()

	private var nextSerial: Int = 1

	private val corners: Array<SnapCorner> = SnapCorner.all()

	private val cornerEntries: ArrayList<Entry> = ArrayList()

	/** The places, read on first use. */
	private var store: PluginConfigImpl? = null

	/* RuneLite's managing state. */
	@JvmField var editMode: Boolean = false
	@JvmField var dragging: Boolean = false
	@JvmField var resizing: Boolean = false
	@JvmField var current: Entry? = null
	private var dragTarget: Entry? = null
	private var hovered: Entry? = null
	private var lastHovered: Entry? = null
	private var offsetX: Int = 0
	private var offsetY: Int = 0
	private var resizeEdges: Int = 0
	private var startX: Int = 0
	private var startY: Int = 0
	private var startWidth: Int = 0
	private var startHeight: Int = 0
	private var shownCursor: Int = -1

	/** The smoke test's mouse and drag key, in place of the real ones while [testMouse] is set. */
	@JvmField var testMouse: Boolean = false
	@JvmField var testMouseX: Int = 0
	@JvmField var testMouseY: Int = 0
	@JvmField var testButtonDown: Boolean = false
	@JvmField var testKeyHeld: Boolean = false
	@JvmField var testShift: Boolean = false

	/** The game view this frame, and the parts of the game screen the origins measure from. */
	private val view: Viewport = Viewport()
	private val chat: IntArray = IntArray(4)
	private val side: IntArray = IntArray(4)
	private val part: IntArray = IntArray(4)

	/**
	 * RuneLite's HUD container, the part of the view the game's own frame leaves clear: on the resizable
	 * screen its right edge at the top is left of the minimap's frame, and its bottom is above the tabs (and
	 * the open tab's panel); on the fixed screen it is the view.
	 */
	private var hudTopRight: Int = 0
	private var hudBottom: Int = 0
	private val origins: Array<IntArray> = arrayOf(IntArray(2), IntArray(2), IntArray(2), IntArray(2), IntArray(2))
	private val point: IntArray = IntArray(2)

	/** The menu entries offered last time the menu was built: what a click on one of ours runs. */
	private val offeredEntries: ArrayList<OverlayMenuEntry> = ArrayList()
	private var offeredFor: Entry? = null

	private fun store(): PluginConfigImpl {
		var s: PluginConfigImpl? = store
		if (s == null) {
			s = PluginConfigImpl("overlay-layout")
			store = s
			for (corner in corners) {
				corner.load(s)
			}
		}
		return s
	}

	// --- the panels ---

	@JvmStatic
	fun add(owner: PluginRegistration?, overlay: PanelOverlay): Entry {
		for (entry in entries) {
			if (entry.overlay === overlay) {
				return entry
			}
		}
		val entry: Entry = Entry(overlay, owner, nextSerial++)
		load(entry)
		entries.add(entry)
		return entry
	}

	@JvmStatic
	fun remove(overlay: PanelOverlay) {
		for (i in entries.indices) {
			if (entries[i].overlay === overlay) {
				forget(entries.removeAt(i))
				return
			}
		}
	}

	/** Every panel of [owner]'s, when it stops. */
	@JvmStatic
	fun removeAll(owner: PluginRegistration) {
		var i: Int = 0
		while (i < entries.size) {
			if (entries[i].owner === owner) {
				forget(entries.removeAt(i))
			} else {
				i++
			}
		}
	}

	private fun forget(entry: Entry) {
		if (current === entry) {
			resetManaging()
		}
		if (hovered === entry) {
			hovered = null
		}
		if (lastHovered === entry) {
			lastHovered = null
		}
	}

	@JvmStatic
	fun find(overlay: PanelOverlay): Entry? {
		for (entry in entries) {
			if (entry.overlay === overlay) {
				return entry
			}
		}
		return null
	}

	// --- drawing ---

	/** After the plain overlays of the frame: every panel, in its place, with the outlines while managing. */
	@JvmStatic
	fun draw(viewport: Viewport) {
		view.x = viewport.x
		view.y = viewport.y
		view.width = viewport.width
		view.height = viewport.height
		hovered = null
		if (!loggedIn()) {
			return
		}
		measureScreen()
		positionCorners()
		val mouseX: Int = mouseX()
		val mouseY: Int = mouseY()
		if (editMode) {
			for (entry in cornerEntries) {
				drawEntry(entry, mouseX, mouseY)
			}
		}
		sortEntries()
		for (entry in entries) {
			drawEntry(entry, mouseX, mouseY)
		}
		hovered?.overlay?.onMouseOver()
	}

	/** RuneLite's OVERLAY_COMPARATOR for the corner panels: by corner, then the higher priority first; the order added for the rest. */
	private fun sortEntries() {
		// an insertion sort: stable, and the list is almost always in order already
		for (i in 1 until entries.size) {
			val entry: Entry = entries[i]
			var j: Int = i - 1
			while (j >= 0 && compare(entries[j], entry) > 0) {
				entries[j + 1] = entries[j]
				j--
			}
			entries[j + 1] = entry
		}
	}

	private fun compare(a: Entry, b: Entry): Int {
		val aPos: OverlayPosition = a.overlay.preferredPosition ?: a.overlay.position
		val bPos: OverlayPosition = b.overlay.preferredPosition ?: b.overlay.position
		if (aPos !== bPos) {
			return aPos.ordinal - bPos.ordinal
		}
		return b.overlay.priority.compareTo(a.overlay.priority)
	}

	private fun drawEntry(entry: Entry, mouseX: Int, mouseY: Int) {
		val overlay: PanelOverlay = entry.overlay
		var corner: SnapCorner? = null
		if (overlay.hasPreferredLocation) {
			absolute(overlay.origin, overlay.originX, overlay.originY, overlay.preferredX, overlay.preferredY, point)
		} else {
			corner = cornerFor(correctedPosition(overlay))
			corner.nextDrawPosition(entry.width, entry.height, point)
		}
		clamp(point[0], point[1], entry.width, entry.height, point)
		entry.x = point[0]
		entry.y = point[1]
		if (overlay.preferredWidth > 0 || overlay.preferredHeight > 0) {
			entry.width = overlay.preferredWidth
			entry.height = overlay.preferredHeight
		}
		overlay.boundsX = entry.x
		overlay.boundsY = entry.y
		overlay.boundsWidth = entry.width
		overlay.boundsHeight = entry.height
		overlay.backgroundColour = background
		ToolkitGraphics.translate(entry.x, entry.y)
		ToolkitGraphics.font(interfaceFont)
		var size: Size? = null
		try {
			size = overlay.render(ToolkitGraphics, view)
		} catch (error: Throwable) {
			PluginRuntime.fail(entry.owner, error)
		}
		ToolkitGraphics.reset()
		if (size != null && size.width > 0 && size.height > 0) {
			entry.width = size.width
			entry.height = size.height
		} else {
			entry.width = 0
			entry.height = 0
		}
		overlay.boundsWidth = entry.width
		overlay.boundsHeight = entry.height
		if (corner != null && entry.width + entry.height > 0) {
			corner.shift(entry.x, entry.y, entry.width, entry.height, PADDING)
		}
		if (entry.width == 0 || entry.height == 0) {
			return
		}
		if (editMode && overlay.movable) {
			val colour: Int = if (resizing && current === entry) {
				MOVING_RESIZING
			} else if (dragging && current === entry) {
				MOVING_ACTIVE
			} else if (dragging && overlay.dragTargetable && current?.overlay?.dragTargetable == true && current!!.intersects(entry)) {
				dragTarget = entry
				MOVING_TARGET
			} else {
				MOVING
			}
			ToolkitGraphics.drawRect(entry.x, entry.y, entry.width + 1, entry.height + 1, colour)
		}
		if (!MiniMenu.isOpen && !MiniMenu.interfaceSelectionActive && entry.contains(mouseX, mouseY)) {
			val h: Entry? = hovered
			if (h == null || entry.width * entry.height <= h.width * h.height) {
				hovered = entry
			}
		}
	}

	/** RuneLite's getCorrectedOverlayPosition: on the fixed screen the chat-box and canvas corners are the view's. */
	private fun correctedPosition(overlay: PanelOverlay): OverlayPosition {
		val position: OverlayPosition = overlay.preferredPosition ?: overlay.position
		if (!resizable()) {
			if (position === OverlayPosition.CANVAS_TOP_RIGHT) {
				return OverlayPosition.TOP_RIGHT
			}
			if (position === OverlayPosition.ABOVE_CHATBOX_RIGHT) {
				return OverlayPosition.BOTTOM_RIGHT
			}
		}
		return position
	}

	private fun cornerFor(position: OverlayPosition): SnapCorner {
		return corners[position.ordinal]
	}

	/** RuneLite's clampOverlayLocation, inside the game view: the panels are drawn into it. */
	private fun clamp(x: Int, y: Int, width: Int, height: Int, out: IntArray) {
		out[0] = x.coerceIn(view.x, maxOf(view.x, view.x + view.width - width))
		out[1] = y.coerceIn(view.y, maxOf(view.y, view.y + view.height - height))
	}

	// --- the screen's parts ---

	private fun resizable(): Boolean = ClientState.BASE_WINDOW_ID != FIXED_ROOT

	private fun measureScreen() {
		val fixed: Boolean = !resizable()
		if (!component(ClientState.BASE_WINDOW_ID, if (fixed) FIXED_CHAT else RESIZABLE_CHAT, chat)) {
			chat[0] = view.x
			chat[1] = view.y + view.height
			chat[2] = view.width
			chat[3] = 0
		}
		// the fixed screen's chat box is under the game view: its top, for the panels, is the view's bottom
		if (chat[1] > view.y + view.height) {
			chat[1] = view.y + view.height
		}
		val sideShown: Boolean = component(ClientState.BASE_WINDOW_ID, if (fixed) FIXED_SIDE else RESIZABLE_SIDE, side)
		if (!sideShown) {
			side[0] = view.x + view.width
			side[1] = view.y + view.height
			side[2] = 0
			side[3] = 0
		}
		hudTopRight = view.x + view.width
		hudBottom = view.y + view.height
		if (fixed) {
			return
		}
		for (index in RESIZABLE_MAP_PARTS) {
			if (component(ClientState.BASE_WINDOW_ID, index, part) && part[1] < view.y + view.height / 3) {
				hudTopRight = minOf(hudTopRight, part[0])
			}
		}
		if (PluginRuntime.minimapWidth > 0 && PluginRuntime.minimapY < view.y + view.height / 3) {
			hudTopRight = minOf(hudTopRight, PluginRuntime.minimapX)
		}
		for (index in RESIZABLE_FIRST_TAB..RESIZABLE_LAST_TAB) {
			if (component(ClientState.BASE_WINDOW_ID, index, part) && part[1] > view.y + view.height / 2) {
				hudBottom = minOf(hudBottom, part[1])
			}
		}
		if (sideShown && side[1] > view.y + view.height / 3) {
			hudBottom = minOf(hudBottom, side[1])
		}
	}

	private fun component(root: Int, index: Int, out: IntArray): Boolean {
		if (root < 0) {
			return false
		}
		val component: Component = Interface.getComponent((root shl 16) or index) ?: return false
		if ((component.width() <= 0 && component.height() <= 0) || component.isHidden()) {
			return false
		}
		out[0] = component.screenX()
		out[1] = component.screenY()
		out[2] = component.width()
		out[3] = component.height()
		return true
	}

	/** RuneLite's computeAbsolutePosition: the screen point of ([x], [y]) from an origin. */
	private fun absolute(origin: OverlayOrigin, originX: OverlayOriginX, originY: OverlayOriginY, x: Int, y: Int, out: IntArray) {
		if (origin !== OverlayOrigin.AUTO && origin !== OverlayOrigin.MANUAL) {
			val at: IntArray = originPoint(origin)
			out[0] = at[0] + x
			out[1] = at[1] + y
			return
		}
		// at the bottom the HUD reaches the view's right edge (the tabs are below it); at the top it stops at the minimap
		val bottom: Boolean = originY === OverlayOriginY.HUD_CONTAINER_BOTTOM
		val hudRight: Int = if (bottom) view.x + view.width else hudTopRight
		out[0] = when (originX) {
			OverlayOriginX.LEFT, OverlayOriginX.HUD_CONTAINER_LEFT -> view.x + x
			OverlayOriginX.RIGHT -> view.x + view.width + x
			OverlayOriginX.HUD_CONTAINER_RIGHT -> hudRight + x
			OverlayOriginX.CENTER -> view.x + view.width / 2 + x
			OverlayOriginX.HUD_CONTAINER_CENTER -> view.x + (hudRight - view.x) / 2 + x
			OverlayOriginX.CHATBOX_LEFT -> chat[0] + x
			OverlayOriginX.CHATBOX_RIGHT -> chat[0] + chat[2] + x
		}
		out[1] = when (originY) {
			OverlayOriginY.TOP, OverlayOriginY.HUD_CONTAINER_TOP -> view.y + y
			OverlayOriginY.BOTTOM -> view.y + view.height + y
			OverlayOriginY.HUD_CONTAINER_BOTTOM -> hudBottom + y
			OverlayOriginY.CENTER -> view.y + view.height / 2 + y
			OverlayOriginY.CHATBOX_TOP -> chat[1] + y
		}
	}

	/** RuneLite's computeOriginPosition: the screen point ([x], [y]) measured from an origin. */
	private fun relative(origin: OverlayOrigin, originX: OverlayOriginX, originY: OverlayOriginY, x: Int, y: Int, out: IntArray) {
		absolute(origin, originX, originY, 0, 0, out)
		out[0] = x - out[0]
		out[1] = y - out[1]
	}

	/** Where a part of the screen is (the last place it was seen, while it is not there). */
	private fun originPoint(origin: OverlayOrigin): IntArray {
		val at: IntArray = origins[origin.ordinal]
		when (origin) {
			OverlayOrigin.SIDEPANEL -> if (side[2] > 0) {
				at[0] = side[0]
				at[1] = side[1]
			}
			OverlayOrigin.CHATBOX -> if (chat[2] > 0) {
				at[0] = chat[0]
				at[1] = chat[1]
			}
			OverlayOrigin.MINIMAP -> if (PluginRuntime.minimapWidth > 0) {
				at[0] = PluginRuntime.minimapX
				at[1] = PluginRuntime.minimapY
			}
			else -> {
			}
		}
		return at
	}

	/** RuneLite's computeOverlayOrigins: the nearest sides of the view for a panel dropped at ([x], [y]). */
	private fun autoOrigins(overlay: PanelOverlay, x: Int, y: Int, width: Int, height: Int) {
		val cx: Int = x - view.x + width / 2
		val cy: Int = y - view.y + height / 2
		overlay.originX = if (cx > view.width * .55f) OverlayOriginX.RIGHT else if (cx >= view.width * .45f) OverlayOriginX.CENTER else OverlayOriginX.LEFT
		overlay.originY = if (cy > view.height * .55f) OverlayOriginY.BOTTOM else if (cy > view.height * .45f) OverlayOriginY.CENTER else OverlayOriginY.TOP
	}

	/** RuneLite's positionSnapcorners: each corner where its offset from its origin puts it, kept on the view. */
	private fun positionCorners() {
		for (corner in corners) {
			absolute(OverlayOrigin.MANUAL, corner.originX, corner.originY, corner.curX, corner.curY, point)
			val cx: Int = point[0]
			val cy: Int = point[1]
			corner.alignOffset(point)
			val ax: Int = point[0]
			val ay: Int = point[1]
			clamp(ax, ay, corner.lastWidth, corner.lastHeight, point)
			corner.setPosition(cx + point[0] - ax, cy + point[1] - ay)
		}
	}

	// --- the mouse ---

	private fun loggedIn(): Boolean = PluginRuntime.started && ClientApiImpl.gameState() === GameState.LOGGED_IN

	/** The mouse on the screen (the smoke test's while [testMouse]). */
	@JvmStatic
	fun mouseX(): Int = if (testMouse) testMouseX else MouseRecord.mouseRecorder?.getMouseX() ?: -1

	@JvmStatic
	fun mouseY(): Int = if (testMouse) testMouseY else MouseRecord.mouseRecorder?.getMouseY() ?: -1

	private fun leftDown(): Boolean = if (testMouse) testButtonDown else MouseRecord.mouseRecorder?.isLeftButtonDown() == true

	private fun dragKeyHeld(): Boolean {
		if (testMouse) {
			return testKeyHeld
		}
		val keys: KeyRecorder = KeyRecorder.keyRecorder ?: return false
		return dragKey > 0 && keys.isPressed(dragKey) && GameShell.appletHasFocus
	}

	/**
	 * Each client cycle, before the mouse's presses are handled: managing while the drag key is held (and
	 * the window has the focus), the drag followed, and a let-go button ending it.
	 */
	@JvmStatic
	fun cycle() {
		val managing: Boolean = loggedIn() && dragKeyHeld()
		if (managing != editMode) {
			if (managing) {
				enterEditMode()
			} else {
				leaveEditMode()
			}
		}
		if (!editMode) {
			setCursor(CursorType.DEFAULT_CURSOR)
			return
		}
		val mouseX: Int = mouseX()
		val mouseY: Int = mouseY()
		val entry: Entry? = current
		if (entry != null && (dragging || resizing)) {
			if (!leftDown()) {
				released(mouseX, mouseY)
			} else {
				dragged(mouseX, mouseY)
			}
		} else {
			moved(mouseX, mouseY)
		}
	}

	private fun enterEditMode() {
		editMode = true
		cornerEntries.clear()
		for (corner in corners) {
			val entry: Entry = Entry(corner.overlay(), null, nextSerial++)
			cornerEntries.add(entry)
		}
	}

	private fun leaveEditMode() {
		editMode = false
		cornerEntries.clear()
		val s: PluginConfigImpl = store()
		for (corner in corners) {
			corner.save(s)
		}
		s.save()
		resetManaging()
	}

	/** RuneLite's resetOverlayManagementMode. */
	private fun resetManaging() {
		dragging = false
		resizing = false
		current = null
		dragTarget = null
		resizeEdges = 0
		for (corner in corners) {
			corner.inDragMode = false
		}
		setCursor(CursorType.DEFAULT_CURSOR)
	}

	/** The panel under the mouse last frame, the snap corners included while managing. */
	private fun lastHoveredEntry(mouseX: Int, mouseY: Int): Entry? {
		var found: Entry? = lastHovered
		if (editMode) {
			for (entry in cornerEntries) {
				if (entry.contains(mouseX, mouseY) && (found == null || entry.width * entry.height <= found.width * found.height)) {
					found = entry
				}
			}
		}
		return found
	}

	/** RuneLite's mouseMoved while managing: a resizable panel's edges show the resize cursors. */
	private fun moved(mouseX: Int, mouseY: Int) {
		val entry: Entry? = lastHoveredEntry(mouseX, mouseY)
		if (entry == null || !entry.overlay.resizable) {
			resizeEdges = 0
			setCursor(CursorType.DEFAULT_CURSOR)
			return
		}
		resizeEdges = outcode(entry, mouseX, mouseY)
		setCursor(cursorFor(resizeEdges))
	}

	/** Which of the panel's edges (within the tolerance) the point is on: RuneLite's Rectangle.outcode of the shrunk bounds. */
	private fun outcode(entry: Entry, px: Int, py: Int): Int {
		val x: Int = entry.x + RESIZE_TOLERANCE
		val y: Int = entry.y + RESIZE_TOLERANCE
		val width: Int = entry.width - RESIZE_TOLERANCE * 2
		val height: Int = entry.height - RESIZE_TOLERANCE * 2
		var code: Int = 0
		if (width <= 0) {
			code = code or OUT_LEFT or OUT_RIGHT
		} else if (px < x) {
			code = code or OUT_LEFT
		} else if (px > x + width) {
			code = code or OUT_RIGHT
		}
		if (height <= 0) {
			code = code or OUT_TOP or OUT_BOTTOM
		} else if (py < y) {
			code = code or OUT_TOP
		} else if (py > y + height) {
			code = code or OUT_BOTTOM
		}
		return code
	}

	private fun cursorFor(edges: Int): Int {
		return when (edges) {
			OUT_TOP -> CursorType.N_RESIZE_CURSOR
			OUT_TOP or OUT_LEFT -> CursorType.NW_RESIZE_CURSOR
			OUT_LEFT -> CursorType.W_RESIZE_CURSOR
			OUT_LEFT or OUT_BOTTOM -> CursorType.SW_RESIZE_CURSOR
			OUT_BOTTOM -> CursorType.S_RESIZE_CURSOR
			OUT_BOTTOM or OUT_RIGHT -> CursorType.SE_RESIZE_CURSOR
			OUT_RIGHT -> CursorType.E_RESIZE_CURSOR
			OUT_RIGHT or OUT_TOP -> CursorType.NE_RESIZE_CURSOR
			else -> CursorType.DEFAULT_CURSOR
		}
	}

	/** The resize cursors on the canvas; back to the game's own cursor (which the game then sets again) after. */
	private fun setCursor(type: Int) {
		if (type == shownCursor || (shownCursor == -1 && type == CursorType.DEFAULT_CURSOR)) {
			return
		}
		val canvas = GameShell.gameCanvas ?: return
		shownCursor = type
		if (type == CursorType.DEFAULT_CURSOR) {
			canvas.setCursor(getDefaultCursor())
			shownCursor = -1
			// the game's own cursor goes back on at its next frame
			CursorManager.currentCursor = -2
		} else {
			canvas.setCursor(getPredefinedCursor(type))
		}
	}

	/**
	 * A mouse button went down; true when it was on a panel while managing, and the game must not have
	 * it: RuneLite's mousePressed. A right press puts the panel back; a left one starts dragging it, or
	 * resizing it from the edge it is on.
	 */
	@JvmStatic
	fun mousePressed(mouseX: Int, mouseY: Int, clickType: Int): Boolean {
		if (!editMode) {
			return false
		}
		val entry: Entry = lastHoveredEntry(mouseX, mouseY) ?: return false
		val overlay: PanelOverlay = entry.overlay
		if (!overlay.movable) {
			return false
		}
		if (clickType == MouseLog.TYPE_PRESS_RIGHT) {
			if (overlay.resettable) {
				reset(entry)
			}
			return true
		}
		if (clickType != MouseLog.TYPE_PRESS_LEFT) {
			return false
		}
		current = entry
		offsetX = mouseX - entry.x
		offsetY = mouseY - entry.y
		resizeEdges = if (overlay.resizable) outcode(entry, mouseX, mouseY) else 0
		resizing = resizeEdges != 0
		dragging = !resizing
		startX = entry.x
		startY = entry.y
		startWidth = entry.width
		startHeight = entry.height
		for (corner in corners) {
			corner.inDragMode = dragging
		}
		return true
	}

	/** RuneLite's mouseDragged: the panel follows the mouse, or its edges do. */
	private fun dragged(mouseX: Int, mouseY: Int) {
		val entry: Entry = current ?: return
		val overlay: PanelOverlay = entry.overlay
		val target: Entry? = dragTarget
		if (target != null && !entry.intersects(target)) {
			dragTarget = null
		}
		if (mouseX < view.x || mouseY < view.y || mouseX >= view.x + view.width || mouseY >= view.y + view.height) {
			return
		}
		if (resizing) {
			val left: Int = mouseX - startX
			val top: Int = mouseY - startY
			var x: Int = startX
			var y: Int = startY
			var width: Int = startWidth
			var height: Int = startHeight
			when (resizeEdges) {
				OUT_TOP -> {
					y += top
					height -= top
				}
				OUT_TOP or OUT_LEFT -> {
					x += left
					y += top
					width -= left
					height -= top
				}
				OUT_LEFT -> {
					x += left
					width -= left
				}
				OUT_LEFT or OUT_BOTTOM -> {
					x += left
					width -= left
					height = top
				}
				OUT_BOTTOM -> height = top
				OUT_BOTTOM or OUT_RIGHT -> {
					width = left
					height = top
				}
				OUT_RIGHT -> width = left
				OUT_RIGHT or OUT_TOP -> {
					y += top
					width = left
					height -= top
				}
			}
			val minimum: Int = overlay.minimumSize
			val widthOverflow: Int = maxOf(0, minimum - width)
			val heightOverflow: Int = maxOf(0, minimum - height)
			if (widthOverflow > 0) {
				width = minimum
				if (x > startX) {
					x -= widthOverflow
				}
			}
			if (heightOverflow > 0) {
				height = minimum
				if (y > startY) {
					y -= heightOverflow
				}
			}
			val dx: Int = x - startX
			val dy: Int = y - startY
			startX = x
			startY = y
			startWidth = width
			startHeight = height
			overlay.preferredWidth = width
			overlay.preferredHeight = height
			if (overlay.hasPreferredLocation) {
				overlay.preferredX += dx
				overlay.preferredY += dy
			}
			return
		}
		clamp(mouseX - offsetX, mouseY - offsetY, entry.width, entry.height, point)
		val x: Int = point[0]
		val y: Int = point[1]
		if (overlay.origin === OverlayOrigin.AUTO) {
			autoOrigins(overlay, x, y, entry.width, entry.height)
		}
		relative(overlay.origin, overlay.originX, overlay.originY, x, y, point)
		overlay.preferredPosition = null
		overlay.hasPreferredLocation = true
		overlay.preferredX = point[0]
		overlay.preferredY = point[1]
		try {
			overlay.onMoved()
		} catch (error: Throwable) {
			PluginRuntime.fail(entry.owner, error)
		}
	}

	/** RuneLite's mouseReleased: dropped on a drag target, taken; over a snap corner, snapped there; then kept. */
	private fun released(mouseX: Int, mouseY: Int) {
		val entry: Entry = current ?: return
		val overlay: PanelOverlay = entry.overlay
		val target: Entry? = dragTarget
		if (target != null) {
			var taken: Boolean = false
			try {
				taken = target.overlay.onDrag(overlay)
			} catch (error: Throwable) {
				PluginRuntime.fail(target.owner, error)
			}
			if (taken) {
				resetManaging()
				return
			}
		}
		if (overlay.snappable && dragging) {
			for (corner in corners) {
				if (corner.cornerContains(mouseX, mouseY)) {
					var position: OverlayPosition? = corner.position
					if (position === correctedPosition(overlay)) {
						position = null // back to its own corner
					}
					if (position === overlay.position) {
						position = null
					}
					overlay.preferredPosition = position
					overlay.hasPreferredLocation = false
					overlay.origin = OverlayOrigin.AUTO
					overlay.originX = OverlayOriginX.LEFT
					overlay.originY = OverlayOriginY.TOP
					break
				}
			}
		}
		save(entry)
		resetManaging()
	}

	/** RuneLite's resetOverlay: no place, corner or size of the player's. */
	private fun reset(entry: Entry) {
		val overlay: PanelOverlay = entry.overlay
		overlay.preferredPosition = null
		overlay.hasPreferredLocation = false
		overlay.preferredWidth = 0
		overlay.preferredHeight = 0
		overlay.origin = OverlayOrigin.AUTO
		overlay.originX = OverlayOriginX.LEFT
		overlay.originY = OverlayOriginY.TOP
		try {
			overlay.onReset()
		} catch (error: Throwable) {
			PluginRuntime.fail(entry.owner, error)
		}
		save(entry)
	}

	/** `::resetanchors`: every snap corner back where it was. */
	@JvmStatic
	fun resetAnchors() {
		val s: PluginConfigImpl = store()
		for (corner in corners) {
			corner.reset()
			corner.save(s)
		}
		s.save()
	}

	// --- keeping the places ---

	private fun load(entry: Entry) {
		if (entry.owner == null && entry.overlay is SnapCorner.CornerOverlay) {
			return
		}
		val s: PluginConfigImpl = store()
		val overlay: PanelOverlay = entry.overlay
		val location: IntArray? = pair(s.get(entry.key + PREFERRED_LOCATION, ""))
		if (overlay.movable && location != null) {
			overlay.hasPreferredLocation = true
			overlay.preferredX = location[0]
			overlay.preferredY = location[1]
			val origin: OverlayOrigin? = enumOf(OverlayOrigin.entries, s.get(entry.key + ORIGIN, ""))
			val originX: OverlayOriginX? = enumOf(OverlayOriginX.entries, s.get(entry.key + ORIGIN_X, ""))
			val originY: OverlayOriginY? = enumOf(OverlayOriginY.entries, s.get(entry.key + ORIGIN_Y, ""))
			if (origin != null && originX != null && originY != null) {
				overlay.origin = origin
				overlay.originX = originX
				overlay.originY = originY
			}
		}
		val size: IntArray? = pair(s.get(entry.key + PREFERRED_SIZE, ""))
		if (size != null) {
			overlay.preferredWidth = size[0]
			overlay.preferredHeight = size[1]
		}
		if (overlay.snappable) {
			overlay.preferredPosition = enumOf(OverlayPosition.entries, s.get(entry.key + PREFERRED_POSITION, ""))
		}
	}

	/** RuneLite's saveOverlay: the place, the corner and the size, each removed when there is none. */
	@JvmStatic
	fun save(entry: Entry) {
		if (entry.overlay is SnapCorner.CornerOverlay) {
			return // a corner keeps its own place (SnapCorner.save) when managing ends
		}
		val s: PluginConfigImpl = store()
		val overlay: PanelOverlay = entry.overlay
		if (overlay.hasPreferredLocation) {
			s.set(entry.key + PREFERRED_LOCATION, overlay.preferredX.toString() + ":" + overlay.preferredY)
			s.set(entry.key + ORIGIN, overlay.origin.name)
			s.set(entry.key + ORIGIN_X, overlay.originX.name)
			s.set(entry.key + ORIGIN_Y, overlay.originY.name)
		} else {
			s.remove(entry.key + PREFERRED_LOCATION)
			s.remove(entry.key + ORIGIN)
			s.remove(entry.key + ORIGIN_X)
			s.remove(entry.key + ORIGIN_Y)
		}
		if (overlay.preferredWidth > 0 || overlay.preferredHeight > 0) {
			s.set(entry.key + PREFERRED_SIZE, overlay.preferredWidth.toString() + ":" + overlay.preferredHeight)
		} else {
			s.remove(entry.key + PREFERRED_SIZE)
		}
		val position: OverlayPosition? = overlay.preferredPosition
		if (position != null) {
			s.set(entry.key + PREFERRED_POSITION, position.name)
		} else {
			s.remove(entry.key + PREFERRED_POSITION)
		}
		s.save()
	}

	/** The layout's file, for the infobox groups (InfoBoxes) and the snap corners. */
	@JvmStatic
	fun config(): PluginConfigImpl = store()

	/** A "x:y" pair (RuneLite's Point and Dimension), or null. */
	private fun pair(text: String): IntArray? {
		val at: Int = text.indexOf(':')
		if (at <= 0) {
			return null
		}
		val a: Int = text.substring(0, at).trim().toIntOrNull() ?: return null
		val b: Int = text.substring(at + 1).trim().toIntOrNull() ?: return null
		return intArrayOf(a, b)
	}

	private fun <E : Enum<E>> enumOf(values: List<E>, name: String): E? {
		for (value in values) {
			if (value.name == name) {
				return value
			}
		}
		return null
	}

	// --- shift and a right click ---

	/**
	 * The menu for what is under the mouse is built (MiniMenuInputTick): with Shift held over a panel, its
	 * entries just under the top one (RuneLite's onPostMenuSort) and, for a movable one, an Overlay Origin
	 * submenu just above Cancel (RuneLite's addOriginMenu).
	 */
	@JvmStatic
	fun menuBuilt() {
		lastHovered = hovered
		val entry: Entry = hovered ?: return
		if (MiniMenu.isOpen || !loggedIn()) {
			return
		}
		val keys: KeyRecorder = KeyRecorder.keyRecorder ?: return
		if (!(if (testMouse) testShift else keys.isPressed(KeyCode.SHIFT))) {
			return
		}
		offeredEntries.clear()
		offeredFor = entry
		var menu: List<OverlayMenuEntry> = emptyList()
		try {
			menu = entry.overlay.menuEntries()
		} catch (error: Throwable) {
			PluginRuntime.fail(entry.owner, error)
		}
		if (menu.isNotEmpty()) {
			val top: MiniMenuEntry? = MiniMenu.entryList.last() as MiniMenuEntry?
			for (i in menu.size - 1 downTo 0) {
				val item: OverlayMenuEntry = menu[i]
				offeredEntries.add(item)
				MiniMenu.addEntry(false, MENU_TARGET + item.target, offeredEntries.size - 1, true, entry.serial, -1, true, MiniMenuAction.OVERLAY_MENU, 0L, item.option, 0L, -1)
			}
			if (top != null) {
				// the top entry stays the left click
				top.unlink()
				MiniMenu.entryList.addLast(top)
			}
		}
		if (entry.overlay.movable && entry.overlay !is SnapCorner.CornerOverlay) {
			addOriginMenu(entry)
		}
	}

	/** RuneLite's addOriginMenu options, and the origins they choose. */
	private val ORIGIN_OPTIONS: Array<String> = arrayOf("Top left", "Top center", "Top right", "Bottom left", "Bottom center", "Bottom right", "Sidepanel", "Chatbox", "Minimap")

	/** param1 of an Overlay Origin entry: this plus the option's index. */
	private const val ORIGIN_CHOICE: Int = 1000

	private fun addOriginMenu(entry: Entry) {
		val first: MiniMenuEntry = MiniMenu.entryList.first() as MiniMenuEntry? ?: return
		val submenu: Int = PluginRuntime.clientSubmenu("Overlay Origin", "", entry.serial)
		if (submenu < 0) {
			return
		}
		for (i in ORIGIN_OPTIONS.indices) {
			PluginRuntime.clientSubmenuEntry(submenu, ORIGIN_OPTIONS[i], entry.serial, ORIGIN_CHOICE + i)
		}
		// just above the bottom entry (Cancel), as RuneLite's createMenuEntry(1)
		val parent: MiniMenuEntry = MiniMenu.entryList.last() as MiniMenuEntry? ?: return
		val after: com.jagex.game.runetek5.cache.node.Node? = first.next
		if (after != null && after !== parent) {
			com.jagex.game.runetek5.cache.node.Node.insertBefore(parent, after)
		}
	}

	/** One of our menu entries was clicked (MiniMenuActionExecutor). */
	@JvmStatic
	fun menuClicked(serial: Int, choice: Int) {
		val entry: Entry = offeredFor ?: return
		if (entry.serial != serial || !entries.contains(entry)) {
			return
		}
		if (choice >= ORIGIN_CHOICE) {
			chooseOrigin(entry, choice - ORIGIN_CHOICE)
			return
		}
		if (choice < 0 || choice >= offeredEntries.size) {
			return
		}
		try {
			offeredEntries[choice].action.run()
		} catch (error: Throwable) {
			PluginRuntime.fail(entry.owner, error)
		}
	}

	private fun chooseOrigin(entry: Entry, option: Int) {
		val overlay: PanelOverlay = entry.overlay
		if (option < 6) {
			val originX: OverlayOriginX = when (option % 3) {
				0 -> OverlayOriginX.LEFT
				1 -> OverlayOriginX.CENTER
				else -> OverlayOriginX.RIGHT
			}
			val originY: OverlayOriginY = if (option < 3) OverlayOriginY.TOP else OverlayOriginY.BOTTOM
			ClientApiImpl.chatMessage(
				"This overlay will now be automatically repositioned relative to the " + originY.name.lowercase() + " " + originX.name.lowercase() + " of the screen when the client is resized.",
			)
			relative(OverlayOrigin.MANUAL, originX, originY, entry.x, entry.y, point)
			overlay.preferredX = point[0]
			overlay.preferredY = point[1]
			overlay.hasPreferredLocation = true
			overlay.preferredPosition = null
			overlay.origin = OverlayOrigin.MANUAL
			overlay.originX = originX
			overlay.originY = originY
		} else {
			val origin: OverlayOrigin = when (option) {
				6 -> OverlayOrigin.SIDEPANEL
				7 -> OverlayOrigin.CHATBOX
				else -> OverlayOrigin.MINIMAP
			}
			ClientApiImpl.chatMessage("This overlay will now be automatically repositioned relative to the " + origin.name.lowercase() + " when the client is resized.")
			relative(origin, overlay.originX, overlay.originY, entry.x, entry.y, point)
			overlay.preferredX = point[0]
			overlay.preferredY = point[1]
			overlay.hasPreferredLocation = true
			overlay.preferredPosition = null
			overlay.origin = origin
		}
		save(entry)
	}

	/** For the smoke test: the panel's entry by name, or null. */
	@JvmStatic
	fun entryNamed(name: String): Entry? {
		for (entry in entries) {
			if (entry.overlay.name == name) {
				return entry
			}
		}
		return null
	}

	/** For the smoke test: a corner's box this frame (x, y, width, height). */
	@JvmStatic
	fun cornerBox(position: OverlayPosition, out: IntArray) {
		corners[position.ordinal].cornerBox(out)
	}
}
