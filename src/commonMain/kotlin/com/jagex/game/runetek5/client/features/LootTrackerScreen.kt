package com.jagex.game.runetek5.client.features

import SubInterfaceCloser
import SubInterfaceOpener
import com.jagex.game.runetek5.InterfaceManager
import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.config.iftype.SubInterfaceType
import com.jagex.game.runetek5.config.rsinterface.Component
import com.jagex.game.runetek5.config.rsinterface.HookRequest
import com.jagex.game.runetek5.config.rsinterface.Interface
import com.jagex.game.runetek5.config.rsinterface.SubInterface
import com.jagex.game.runetek5.core.constants.MainLogicStep
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: the Loot Tracker's window, RuneLite's loot tracker panel on the
 * client's page kit (ClientPage) in the Graphics Options window's shell (742), as the plugin manager
 * is. At the top the view controls: grouped by source or kill by kill, ignored items shown or hidden,
 * every box collapsed or expanded, and Back from a source's details. Under them the totals (kills and
 * value) with Reset all, and then a box a source (or a kill), newest first: its name, its level or
 * kills, its value, and its items with their quantities. A box's title has RuneLite's menu (Collapse,
 * Hide loot / Include loot, Reset, View details) and an item's has Toggle item; hovering either shows
 * RuneLite's tooltip (the average of a source, an item's prices) on the line under the totals. The
 * page scrolls with the wheel. `::loot`, or Open on the Loot Tracker's row of the plugin manager.
 */
object LootTrackerScreen {

	const val SENTINEL: Int = -636

	private const val WINDOW: Int = 742

	private const val ROOT: Int = (742 shl 16)

	private const val BODY: Int = (742 shl 16) or 2

	private const val CLOSE: Int = (742 shl 16) or 14

	private const val TITLE: Int = (742 shl 16) or 15

	private const val HEIGHT: Int = 334

	private const val WIDTH: Int = 501

	private const val BODY_HEIGHT: Int = HEIGHT - 29

	private const val SCROLLBAR_X: Int = WIDTH - 10

	private const val SCROLLBAR_WIDTH: Int = 6

	private const val THUMB_MIN: Int = 12

	private const val WHEEL_STEP: Int = 24

	private const val BOX_X: Int = 8

	private const val BOX_WIDTH: Int = WIDTH - 24

	private const val HEADER_HEIGHT: Int = 20

	private const val CELL_WIDTH: Int = 40

	private const val CELL_HEIGHT: Int = 36

	private const val CELLS_PER_ROW: Int = BOX_WIDTH / CELL_WIDTH

	/** RuneLite's MAX_LOOT_BOXES in the kill-by-kill view. */
	private const val MAX_BOXES: Int = 500

	private const val DIM: Int = 0x9f9684

	private const val IGNORED: Int = 0x6f685c

	private const val WHITE: Int = 0xffffff

	/** An ignored item's icon, faded as RuneLite fades it. */
	private const val IGNORED_TRANSPARENCY: Int = 160

	// actions
	private const val A_CLOSE: Int = 1
	private const val A_HOVER: Int = 2
	private const val A_LEAVE: Int = 3
	private const val A_GROUPED: Int = 4
	private const val A_SINGLE: Int = 5
	private const val A_HIDDEN: Int = 6
	private const val A_COLLAPSE_ALL: Int = 7
	private const val A_BACK: Int = 8
	private const val A_RESET_ALL: Int = 9
	private const val A_CONFIRM_YES: Int = 10
	private const val A_CONFIRM_NO: Int = 11
	private const val A_BOX: Int = 12
	private const val A_ITEM: Int = 13
	private const val A_WHEEL: Int = 14
	private const val A_TIP: Int = 15
	private const val A_TIP_LEAVE: Int = 16

	@JvmField
	var open: Boolean = false

	/** RuneLite's groupLoot, hideIgnoredItems, and the source the details view shows (null for all). */
	@JvmField var groupLoot: Boolean = true
	@JvmField var hideIgnoredItems: Boolean = true
	@JvmField var currentView: String? = null
	@JvmField var currentType: String? = null

	/** The boxes collapsed, by their key. */
	private val collapsed: HashSet<String> = HashSet()

	/** What Yes does while a reset waits for it, and the question. */
	private var confirm: (() -> Unit)? = null

	private var confirmText: String = ""

	private val buttons: ArrayList<ClientPage.Button> = ArrayList()

	/** The boxes shown, by their index in the hooks, and each item shown, by its index. */
	private val shownBoxes: ArrayList<LootTrackerData.Record> = ArrayList()

	private val shownItems: ArrayList<LootTrackerData.Item> = ArrayList()

	private var contentHeight: Int = 0

	private var scrollTrack: Int = -1

	private var scrollThumb: Int = -1

	private var tipSlot: Int = -1

	/** Records drawn last time, for the smoke test. */
	@JvmField
	var boxesShown: Int = 0

	// --- opening and closing ---

	private fun modalSlot(): Int {
		val slot: Int = if (ClientState.BASE_WINDOW_ID == 548) 18 else 8
		return (ClientState.BASE_WINDOW_ID shl 16) or slot
	}

	/** `::loot`: the window, or closed again; null, or why it did not open. */
	@JvmStatic
	fun toggle(): String? {
		if (open) {
			close()
			return null
		}
		return open()
	}

	/** Opens the window; null when it opened, else why not. */
	@JvmStatic
	fun open(): String? {
		if (ClientState.mainLogicStep !== MainLogicStep.STEP_GAME_SCREEN) {
			return "The loot tracker opens in the game"
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
			title.text = "Loot Tracker"
			Component.redrawComponent(title)
		}
		val closeButton: Component? = Interface.getComponent(CLOSE)
		if (closeButton != null) {
			closeButton.hasScript = true
			closeButton.addOptionName(0, "Close")
			closeButton.params = arrayOf<Any?>(SENTINEL, A_CLOSE, 0)
		}
		open = true
		confirm = null
		rebuild()
		return null
	}

	@JvmStatic
	fun close() {
		if (!open) {
			return
		}
		val parent: Int = modalSlot()
		val sub: SubInterface? = InterfaceManager.OPEN_INTERFACES.get(parent.toLong()) as SubInterface?
		open = false
		forget()
		if (sub != null && sub.interfaceId == WINDOW) {
			SubInterfaceCloser.closeSubInterface(true, true, sub)
		}
	}

	/** The window was closed or replaced by someone else, or the client logged out. */
	@JvmStatic
	fun reset() {
		open = false
		confirm = null
		forget()
	}

	private fun forget() {
		buttons.clear()
		shownBoxes.clear()
		shownItems.clear()
		scrollTrack = -1
		scrollThumb = -1
		tipSlot = -1
	}

	/** The loot changed: the window shows it, when open. */
	@JvmStatic
	fun refresh() {
		if (open) {
			rebuild()
		}
	}

	// --- the page ---

	/** The records the view shows, newest first (RuneLite's rebuild over aggregateRecords or sessionRecords). */
	@JvmStatic
	fun records(): List<LootTrackerData.Record> {
		val source: Collection<LootTrackerData.Record> = if (groupLoot) LootTracker.aggregateRecords.values else LootTracker.sessionRecords
		val out: ArrayList<LootTrackerData.Record> = ArrayList()
		for (record in source) {
			if (record.matches(currentView, currentType) && !(hideIgnoredItems && LootTracker.isEventIgnored(record.title))) {
				out.add(record)
			}
		}
		out.reverse()
		while (!groupLoot && out.size > MAX_BOXES) {
			out.removeAt(out.size - 1)
		}
		return out
	}

	private fun key(record: LootTrackerData.Record): String {
		return if (groupLoot) record.type + "\u0000" + record.title else record.hashCode().toString()
	}

	private fun rebuild() {
		val body: Component = Interface.getComponent(BODY) ?: return
		forget()
		val children: ArrayList<Component> = ArrayList()
		var y: Int = controls(children)
		y = overall(children, y)
		for (record in records()) {
			y = box(children, record, y)
		}
		if (shownBoxes.isEmpty()) {
			children.add(ClientPage.text(BODY, children.size, 0, y + 10, WIDTH, 16, ClientPage.FONT_LABEL, "No loot yet.", DIM, 1, 1, false))
			y += 40
		}
		boxesShown = shownBoxes.size
		contentHeight = y + 6
		if (contentHeight > BODY_HEIGHT) {
			scrollTrack = children.size
			children.add(ClientPage.rect(BODY, children.size, SCROLLBAR_X, 0, SCROLLBAR_WIDTH, BODY_HEIGHT, ClientPage.BOX_FILL, true))
			scrollThumb = children.size
			children.add(ClientPage.rect(BODY, children.size, SCROLLBAR_X, 0, SCROLLBAR_WIDTH, THUMB_MIN, ClientPage.TEXT_TOP, true))
		}
		body.slotChildren = children.toTypedArray<Component?>()
		val scrolls: Boolean = contentHeight > BODY_HEIGHT
		body.scrollHeight = if (scrolls) contentHeight else 0
		body.hasScript = scrolls
		body.onMouseMove = if (scrolls) arrayOf<Any?>(SENTINEL, A_WHEEL, 0) else null
		if (!scrolls) {
			body.scrollY = 0
		}
		Component.layoutChildren(false, body)
		scrollTo(body.scrollY)
	}

	private fun scrollTo(y: Int) {
		val body: Component = Interface.getComponent(BODY) ?: return
		val most: Int = maxOf(0, body.scrollHeight - BODY_HEIGHT)
		body.scrollY = y.coerceIn(0, most)
		if (scrollTrack >= 0 && scrollThumb >= 0 && most > 0) {
			val thumbHeight: Int = maxOf(THUMB_MIN, BODY_HEIGHT * BODY_HEIGHT / body.scrollHeight)
			ClientPage.childOf(BODY, scrollTrack)?.basePositionY = body.scrollY
			ClientPage.childOf(BODY, scrollThumb)?.let {
				it.basePositionY = body.scrollY + (BODY_HEIGHT - thumbHeight) * body.scrollY / most
				it.baseHeight = thumbHeight
			}
			Component.layoutChildren(false, body)
		}
		Component.redrawComponent(body)
	}

	private fun button(children: ArrayList<Component>, label: String, selected: Boolean, action: Int, index: Int, x: Int, y: Int): Int {
		val width: Int = ClientPage.textWidth(ClientPage.FONT_BUTTON, label) + 26
		buttons.add(ClientPage.button(BODY, children, label, selected, SENTINEL, action, index, A_HOVER, A_LEAVE, buttons.size, x, y, width))
		return width
	}

	private fun buttonWidth(label: String): Int = ClientPage.textWidth(ClientPage.FONT_BUTTON, label) + 26

	/** RuneLite's actions bar: Back and the source's name in the details view, and the view controls at the right. */
	private fun controls(children: ArrayList<Component>): Int {
		val y: Int = 4
		// Back: out of a source's details to every source, else back to the plugin manager
		val back: Int = button(children, "Back", false, A_BACK, 0, BOX_X, y)
		val labels: Array<String> = arrayOf(if (isAllCollapsed()) "Expand all" else "Collapse all", if (hideIgnoredItems) "Show ignored" else "Hide ignored", "Single", "Grouped")
		val actions: IntArray = intArrayOf(A_COLLAPSE_ALL, A_HIDDEN, A_SINGLE, A_GROUPED)
		val selected: BooleanArray = booleanArrayOf(false, false, !groupLoot, groupLoot)
		var x: Int = BOX_X + BOX_WIDTH
		for (i in labels.indices) {
			x -= buttonWidth(labels[i]) + 4
			button(children, labels[i], selected[i], actions[i], 0, x, y)
		}
		val view: String? = currentView
		if (view != null) {
			val titleX: Int = BOX_X + back + 8
			children.add(ClientPage.text(BODY, children.size, titleX, y, maxOf(0, x - 6 - titleX), 26, ClientPage.FONT_LABEL, view, WHITE, 0, 1, true))
		}
		return y + 32
	}

	private fun isAllCollapsed(): Boolean {
		val shown: List<LootTrackerData.Record> = records()
		return shown.isNotEmpty() && shown.all { collapsed.contains(key(it)) }
	}

	/** RuneLite's overall panel: the kills and the value of what is shown, Reset all, and the tooltip line; or a reset's question. */
	private fun overall(children: ArrayList<Component>, top: Int): Int {
		children.add(ClientPage.rect(BODY, children.size, BOX_X, top, BOX_WIDTH, 42, ClientPage.BOX_FILL, true))
		children.add(ClientPage.rect(BODY, children.size, BOX_X, top, BOX_WIDTH, 42, ClientPage.BOX_LINE, false))
		val question: (() -> Unit)? = confirm
		if (question != null) {
			children.add(ClientPage.text(BODY, children.size, BOX_X + 8, top + 2, BOX_WIDTH - 150, 38, ClientPage.FONT_LABEL, confirmText, WHITE, 0, 1, true))
			var x: Int = BOX_X + BOX_WIDTH - 6 - buttonWidth("No")
			button(children, "No", false, A_CONFIRM_NO, 0, x, top + 8)
			x -= buttonWidth("Yes") + 6
			button(children, "Yes", false, A_CONFIRM_YES, 0, x, top + 8)
			return top + 50
		}
		var kills: Long = 0L
		var ge: Long = 0L
		var ha: Long = 0L
		for (record in records()) {
			var present: Int = record.items.size
			for (item in record.items) {
				if (hideIgnoredItems && item.ignored) {
					present--
				} else {
					ge += item.totalGe()
					ha += item.totalHa()
				}
			}
			if (present > 0) {
				kills += record.kills
			}
		}
		val priceType: String = if (LootTracker.showPriceType) (if (LootTracker.priceType == LootTracker.PRICE_HA) "HA " else "GE ") else ""
		val value: Long = if (LootTracker.priceType == LootTracker.PRICE_HA) ha else ge
		children.add(ClientPage.text(BODY, children.size, BOX_X + 8, top + 3, 220, 14, ClientPage.FONT_SMALL, "<col=9f9684>Total count: </col>" + GroundItemRules.quantityToStackSize(kills), WHITE, 0, 1, false))
		children.add(ClientPage.text(BODY, children.size, BOX_X + 8, top + 16, 220, 14, ClientPage.FONT_SMALL, "<col=9f9684>Total " + priceType + "value: </col>" + GroundItemRules.quantityToStackSize(value), WHITE, 0, 1, false))
		// RuneLite's tooltip on the totals, and the line the boxes' and items' tooltips show on
		tipSlot = children.size
		children.add(ClientPage.text(BODY, children.size, BOX_X + 8, top + 28, BOX_WIDTH - 110, 14, ClientPage.FONT_SMALL, "Total GE price: " + GroundItemRules.withCommas(ge) + " · Total HA price: " + GroundItemRules.withCommas(ha), DIM, 0, 1, false))
		button(children, "Reset all", false, A_RESET_ALL, 0, BOX_X + BOX_WIDTH - 6 - buttonWidth("Reset all"), top + 8)
		return top + 50
	}

	/** One source's (or kill's) box: RuneLite's LootTrackerBox. */
	private fun box(children: ArrayList<Component>, record: LootTrackerData.Record, top: Int): Int {
		val index: Int = shownBoxes.size
		shownBoxes.add(record)
		val ignoredEvent: Boolean = LootTracker.isEventIgnored(record.title)
		val visible: ArrayList<LootTrackerData.Item> = ArrayList()
		var total: Long = 0L
		for (item in record.items) {
			if (!item.ignored) {
				total += if (LootTracker.priceType == LootTracker.PRICE_HA) item.totalHa() else item.totalGe()
			}
			if (!(hideIgnoredItems && item.ignored)) {
				visible.add(item)
			}
		}
		children.add(ClientPage.rect(BODY, children.size, BOX_X, top, BOX_WIDTH, HEADER_HEIGHT, ClientPage.BEVEL_FILL, true))
		children.add(ClientPage.rect(BODY, children.size, BOX_X, top, BOX_WIDTH, HEADER_HEIGHT, ClientPage.BOX_LINE, false))
		val titleColour: Int = if (ignoredEvent) IGNORED else WHITE
		val title: String = ClientFeatures.plainText(record.title)
		children.add(ClientPage.text(BODY, children.size, BOX_X + 6, top + 1, 220, HEADER_HEIGHT - 2, ClientPage.FONT_SMALL, title, titleColour, 0, 1, true))
		val subTitle: String = if (record.kills > 1) "x " + record.kills else record.subTitle
		val titleWidth: Int = ClientPage.textWidth(ClientPage.FONT_SMALL, title)
		children.add(ClientPage.text(BODY, children.size, BOX_X + 12 + titleWidth, top + 1, 120, HEADER_HEIGHT - 2, ClientPage.FONT_SMALL, subTitle, DIM, 0, 1, false))
		val priceType: String = if (LootTracker.showPriceType) (if (LootTracker.priceType == LootTracker.PRICE_HA) "HA: " else "GE: ") else ""
		children.add(ClientPage.text(BODY, children.size, BOX_X, top + 1, BOX_WIDTH - 8, HEADER_HEIGHT - 2, ClientPage.FONT_SMALL, priceType + GroundItemRules.quantityToStackSize(total) + " gp", titleColour, 2, 1, false))
		val hit: Component = ClientPage.text(BODY, children.size, BOX_X, top, BOX_WIDTH, HEADER_HEIGHT, ClientPage.FONT_SMALL, "", WHITE, 0, 0, false)
		hit.hasScript = true
		val isCollapsed: Boolean = collapsed.contains(key(record))
		hit.addOptionName(0, if (isCollapsed) "Expand" else "Collapse")
		hit.addOptionName(1, if (ignoredEvent) "Include loot" else "Hide loot")
		hit.addOptionName(2, "Reset")
		if (currentView == null) {
			hit.addOptionName(3, "View details")
		}
		hit.opBase = "<col=ff9040>" + title
		hit.params = arrayOf<Any?>(SENTINEL, A_BOX, index)
		hit.onMouseOver = arrayOf<Any?>(SENTINEL, A_TIP, index)
		hit.onMouseLeaveScript = arrayOf<Any?>(SENTINEL, A_TIP_LEAVE, index)
		children.add(hit)
		var y: Int = top + HEADER_HEIGHT
		if (!isCollapsed && visible.isNotEmpty()) {
			val rows: Int = (visible.size + CELLS_PER_ROW - 1) / CELLS_PER_ROW
			val height: Int = rows * CELL_HEIGHT + 4
			children.add(ClientPage.rect(BODY, children.size, BOX_X, y, BOX_WIDTH, height, ClientPage.BOX_FILL, true))
			for (i in visible.indices) {
				val x: Int = BOX_X + 4 + (i % CELLS_PER_ROW) * CELL_WIDTH
				val iy: Int = y + 2 + (i / CELLS_PER_ROW) * CELL_HEIGHT
				itemCell(children, visible[i], x, iy)
			}
			y += height
		}
		return y + 6
	}

	/** An item: its icon with its quantity (faded when ignored), RuneLite's Toggle item, and its tooltip on hover. */
	private fun itemCell(children: ArrayList<Component>, item: LootTrackerData.Item, x: Int, y: Int) {
		val index: Int = shownItems.size
		shownItems.add(item)
		val icon: Component = ClientPage.sprite(BODY, children.size, x, y, 36, 32, -1, false, false, false)
		icon.containerItemId = item.id
		icon.itemStackSize = item.quantity
		icon.renderStack = 2 // the quantity when it is not 1, as the backpack shows it
		icon.spriteShadow = 0x333333
		if (item.ignored) {
			icon.transparency = IGNORED_TRANSPARENCY
		}
		icon.hasScript = true
		icon.addOptionName(0, "Toggle item")
		icon.opBase = "<col=ff9040>" + item.name
		icon.params = arrayOf<Any?>(SENTINEL, A_ITEM, index)
		icon.onMouseOver = arrayOf<Any?>(SENTINEL, A_TIP, -1 - index)
		icon.onMouseLeaveScript = arrayOf<Any?>(SENTINEL, A_TIP_LEAVE, -1 - index)
		children.add(icon)
	}

	/** RuneLite's tooltips: a box's average per kill, or an item's name, quantity and prices. */
	@JvmStatic
	fun tooltip(index: Int): String {
		if (index >= 0) {
			val record: LootTrackerData.Record = shownBoxes.getOrNull(index) ?: return ""
			var total: Long = 0L
			for (item in record.items) {
				if (!item.ignored) {
					total += if (LootTracker.priceType == LootTracker.PRICE_HA) item.totalHa() else item.totalGe()
				}
			}
			return if (record.kills > 1) GroundItemRules.withCommas(total / record.kills) + " gp (average)" else GroundItemRules.withCommas(total) + " gp"
		}
		val item: LootTrackerData.Item = shownItems.getOrNull(-1 - index) ?: return ""
		val text: StringBuilder = StringBuilder(item.name).append(" x ").append(GroundItemRules.withCommas(item.quantity.toLong()))
		text.append(" · GE: ").append(GroundItemRules.quantityToStackSize(item.totalGe()))
		if (item.quantity > 1) {
			text.append(" (").append(GroundItemRules.quantityToStackSize(item.gePrice.toLong())).append(" ea)")
		}
		text.append(" · HA: ").append(GroundItemRules.quantityToStackSize(item.totalHa()))
		if (item.quantity > 1) {
			text.append(" (").append(GroundItemRules.quantityToStackSize(item.haPrice.toLong())).append(" ea)")
		}
		if (item.ignored) {
			text.append(" · Ignored")
		}
		return text.toString()
	}

	private fun showTip(text: String) {
		val c: Component = ClientPage.childOf(BODY, tipSlot) ?: return
		c.text = text
		c.color = if (text.isEmpty()) DIM else WHITE
		Component.redrawComponent(c)
	}

	// --- the hooks ---

	@JvmStatic
	fun hook(hook: HookRequest) {
		val params: Array<Any?> = hook.params ?: return
		if (params.size < 3 || !open) {
			return
		}
		val action: Int = params[1] as Int
		val index: Int = params[2] as Int
		when (action) {
			A_CLOSE -> close()
			A_HOVER -> if (index < buttons.size) ClientPage.paint(buttons[index], true)
			A_LEAVE -> if (index < buttons.size) ClientPage.paint(buttons[index], false)
			A_WHEEL -> {
				val body: Component = Interface.getComponent(BODY) ?: return
				scrollTo(body.scrollY + hook.mouseY * WHEEL_STEP)
			}
			A_TIP -> showTip(tooltip(index))
			A_TIP_LEAVE -> showTip("")
			A_GROUPED -> changeGrouping(true)
			A_SINGLE -> changeGrouping(false)
			A_HIDDEN -> {
				hideIgnoredItems = !hideIgnoredItems
				rebuild()
			}
			A_COLLAPSE_ALL -> collapseAll()
			A_BACK -> if (currentView != null) showDetails(null, null) else backToPluginManager()
			A_RESET_ALL -> ask(if (currentView == null) "This will permanently delete all loot." else "This will permanently delete \"" + currentView + "\" loot.") { LootTracker.reset(currentView, currentType) }
			A_CONFIRM_YES -> {
				val yes: (() -> Unit)? = confirm
				confirm = null
				yes?.invoke()
				rebuild()
			}
			A_CONFIRM_NO -> {
				confirm = null
				rebuild()
			}
			A_BOX -> boxOption(index, hook.clickedOption)
			A_ITEM -> shownItems.getOrNull(index)?.let {
				LootTracker.toggleItem(it.name)
				rebuild()
			}
		}
	}

	private fun ask(question: String, yes: () -> Unit) {
		confirmText = question
		confirm = yes
		rebuild()
		scrollTo(0)
	}

	/** A box's menu, by the option clicked (1 up): Collapse/Expand, Hide/Include loot, Reset, View details. */
	private fun boxOption(index: Int, option: Int) {
		val record: LootTrackerData.Record = shownBoxes.getOrNull(index) ?: return
		when (option) {
			1 -> {
				val k: String = key(record)
				if (!collapsed.remove(k)) {
					collapsed.add(k)
				}
				rebuild()
			}
			2 -> {
				LootTracker.toggleEvent(record.title, !LootTracker.isEventIgnored(record.title))
				rebuild()
			}
			3 -> if (groupLoot) {
				ask("This will permanently delete \"" + record.title + "\" loot.") { LootTracker.reset(record.title, record.type) }
			} else {
				ask("This will delete one kill.") { LootTracker.resetOne(record) }
			}
			4 -> showDetails(record.title, record.type)
		}
	}

	/** Back from the list: the window closes and the plugin manager, which shares its shell, opens in its place. */
	@JvmStatic
	fun backToPluginManager() {
		close()
		PluginManagerScreen.open()
	}

	/** The smoke test's wheel: [notches] turns down (up when negative). */
	@JvmStatic
	fun scrollBy(notches: Int) {
		val body: Component = Interface.getComponent(BODY) ?: return
		scrollTo(body.scrollY + notches * WHEEL_STEP)
	}

	/** The scroll position, the content height (0 when the page does not scroll) and the visible height. */
	@JvmStatic
	fun scrollPosition(): IntArray {
		val body: Component = Interface.getComponent(BODY) ?: return intArrayOf(0, 0, BODY_HEIGHT)
		return intArrayOf(body.scrollY, body.scrollHeight, BODY_HEIGHT)
	}

	@JvmStatic
	fun changeGrouping(group: Boolean) {
		groupLoot = group
		collapsed.clear()
		rebuild()
	}

	@JvmStatic
	fun showDetails(title: String?, type: String?) {
		currentView = title
		currentType = type
		rebuild()
		scrollTo(0)
	}

	private fun collapseAll() {
		val shown: List<LootTrackerData.Record> = records()
		if (isAllCollapsed()) {
			for (record in shown) {
				collapsed.remove(key(record))
			}
		} else {
			for (record in shown) {
				collapsed.add(key(record))
			}
		}
		rebuild()
	}
}
