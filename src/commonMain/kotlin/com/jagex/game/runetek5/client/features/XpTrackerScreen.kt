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
import com.jagex.game.runetek5.plugin.api.Skill
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: the XP Tracker's window, RuneLite's XP Tracker side panel on the
 * client's page kit (ClientPage) in the Graphics Options window's shell (742), as the Loot Tracker's
 * window is. At the top the total ("Gained", "Per hour") with RuneLite's panel menu (Reset All, Reset
 * All/hr, Pause All, Unpause All); under it a box a skill trained this session: its icon, the four
 * labels the settings choose, and a progress bar from its level to the next in the skill's colour, with
 * its label (dimmed and "Paused" while paused); its menu is RuneLite's (Collapse for the compact view,
 * Reset, Reset others, Reset/hr, Pause or Unpause, Add to canvas or Remove from canvas). Hovering a bar
 * shows RuneLite's tooltip on the total's line. Updated every second while open; scrolls with the wheel.
 */
object XpTrackerScreen {

	const val SENTINEL: Int = -637

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

	private const val ICON: Int = 25

	private const val BAR_HEIGHT: Int = 14

	private const val DIM: Int = 0x9f9684

	private const val WHITE: Int = 0xffffff

	private const val BAR_BACKGROUND: Int = 0x3d3831

	/** A paused skill's bar, dimmed as RuneLite dims it. */
	private const val PAUSED_TRANSPARENCY: Int = 140

	private const val A_CLOSE: Int = 1
	private const val A_OVERALL: Int = 2
	private const val A_BOX: Int = 3
	private const val A_WHEEL: Int = 4
	private const val A_TIP: Int = 5
	private const val A_TIP_LEAVE: Int = 6
	private const val A_BACK: Int = 7
	private const val A_HOVER: Int = 8
	private const val A_LEAVE: Int = 9

	private val buttons: ArrayList<ClientPage.Button> = ArrayList()

	@JvmField
	var open: Boolean = false

	/** The skills of the boxes shown, by their index in the hooks. */
	private val shown: ArrayList<Int> = ArrayList()

	private var scrollTrack: Int = -1

	private var scrollThumb: Int = -1

	private var tipSlot: Int = -1

	/** The skill whose tooltip is up, -1 for none, so that the second's refresh keeps it. */
	private var tipFor: Int = -1

	/** Boxes drawn last time, for the smoke test. */
	@JvmField
	var boxesShown: Int = 0

	private fun modalSlot(): Int {
		val slot: Int = if (ClientState.BASE_WINDOW_ID == 548) 18 else 8
		return (ClientState.BASE_WINDOW_ID shl 16) or slot
	}

	/** `::xp`: the window, or closed again; null, or why it did not open. */
	@JvmStatic
	fun toggle(): String? {
		if (open) {
			close()
			return null
		}
		return open()
	}

	@JvmStatic
	fun open(): String? {
		if (ClientState.mainLogicStep !== MainLogicStep.STEP_GAME_SCREEN) {
			return "The XP tracker opens in the game"
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
			title.text = "XP Tracker"
			Component.redrawComponent(title)
		}
		val closeButton: Component? = Interface.getComponent(CLOSE)
		if (closeButton != null) {
			closeButton.hasScript = true
			closeButton.addOptionName(0, "Close")
			closeButton.params = arrayOf<Any?>(SENTINEL, A_CLOSE, 0)
		}
		open = true
		tipFor = -1
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

	@JvmStatic
	fun reset() {
		open = false
		forget()
	}

	private fun forget() {
		buttons.clear()
		shown.clear()
		scrollTrack = -1
		scrollThumb = -1
		tipSlot = -1
	}

	@JvmStatic
	fun refresh() {
		if (open) {
			rebuild()
		}
	}

	private fun rebuild() {
		val body: Component = Interface.getComponent(BODY) ?: return
		forget()
		val children: ArrayList<Component> = ArrayList()
		// Back to the plugin manager, which shares the window's shell
		val backWidth: Int = ClientPage.textWidth(ClientPage.FONT_BUTTON, "Back") + 26
		buttons.add(ClientPage.button(BODY, children, "Back", false, SENTINEL, A_BACK, 0, A_HOVER, A_LEAVE, buttons.size, BOX_X, 4, backWidth))
		var y: Int = overall(children, 36)
		for (skill in XpTracker.order) {
			val state: XpTrackerState = XpTracker.states[skill] ?: continue
			y = box(children, skill, state, y)
		}
		if (shown.isEmpty()) {
			children.add(ClientPage.text(BODY, children.size, 0, y + 10, WIDTH, 16, ClientPage.FONT_LABEL, "No experience gained yet.", DIM, 1, 1, false))
			y += 40
		}
		boxesShown = shown.size
		val content: Int = y + 6
		if (content > BODY_HEIGHT) {
			scrollTrack = children.size
			children.add(ClientPage.rect(BODY, children.size, SCROLLBAR_X, 0, SCROLLBAR_WIDTH, BODY_HEIGHT, ClientPage.BOX_FILL, true))
			scrollThumb = children.size
			children.add(ClientPage.rect(BODY, children.size, SCROLLBAR_X, 0, SCROLLBAR_WIDTH, THUMB_MIN, ClientPage.TEXT_TOP, true))
		}
		body.slotChildren = children.toTypedArray<Component?>()
		val scrolls: Boolean = content > BODY_HEIGHT
		body.scrollHeight = if (scrolls) content else 0
		body.hasScript = scrolls
		body.onMouseMove = if (scrolls) arrayOf<Any?>(SENTINEL, A_WHEEL, 0) else null
		if (!scrolls) {
			body.scrollY = 0
		}
		Component.layoutChildren(false, body)
		scrollTo(body.scrollY)
		if (tipFor >= 0) {
			showTip(tooltip(tipFor))
		}
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

	private fun label(key: String, value: String): String = "<col=9f9684>$key </col>$value"

	/** RuneLite's overall panel: the total gained and per hour, the tooltip line, and its menu on a right-click. */
	private fun overall(children: ArrayList<Component>, top: Int): Int {
		val height: Int = 44
		children.add(ClientPage.rect(BODY, children.size, BOX_X, top, BOX_WIDTH, height, ClientPage.BOX_FILL, true))
		children.add(ClientPage.rect(BODY, children.size, BOX_X, top, BOX_WIDTH, height, ClientPage.BOX_LINE, false))
		val total: XpTrackerState? = XpTracker.states[XpTracker.OVERALL]
		val gained: String = if (total == null) "0" else GroundItemRules.quantityToRSDecimalStack(total.totalXpGained(), true)
		val perHour: String = if (total == null) "0" else GroundItemRules.quantityToRSDecimalStack(total.xpPerHour(), true)
		children.add(ClientPage.text(BODY, children.size, BOX_X + 8, top + 3, 200, 14, ClientPage.FONT_SMALL, label("Gained:", gained), WHITE, 0, 1, false))
		children.add(ClientPage.text(BODY, children.size, BOX_X + 8, top + 16, 200, 14, ClientPage.FONT_SMALL, label("Per hour:", perHour), WHITE, 0, 1, false))
		tipSlot = children.size
		children.add(ClientPage.text(BODY, children.size, BOX_X + 8, top + 29, BOX_WIDTH - 16, 14, ClientPage.FONT_SMALL, "", DIM, 0, 1, false))
		val hit: Component = ClientPage.text(BODY, children.size, BOX_X, top, BOX_WIDTH, height, ClientPage.FONT_SMALL, "", WHITE, 0, 0, false)
		hit.hasScript = true
		hit.addOptionName(0, "Reset All")
		hit.addOptionName(1, "Reset All/hr")
		hit.addOptionName(2, "Pause All")
		hit.addOptionName(3, "Unpause All")
		hit.opBase = "<col=ff9040>XP Tracker"
		hit.params = arrayOf<Any?>(SENTINEL, A_OVERALL, 0)
		children.add(hit)
		return top + height + 6
	}

	/** A skill's box: RuneLite's XpInfoBox, compact (icon and bar) when collapsed. */
	private fun box(children: ArrayList<Component>, skill: Int, state: XpTrackerState, top: Int): Int {
		val index: Int = shown.size
		shown.add(skill)
		val height: Int = if (state.compact) ICON + 8 else 64
		children.add(ClientPage.rect(BODY, children.size, BOX_X, top, BOX_WIDTH, height, ClientPage.BEVEL_FILL, true))
		children.add(ClientPage.rect(BODY, children.size, BOX_X, top, BOX_WIDTH, height, ClientPage.BOX_LINE, false))
		children.add(ClientPage.sprite(BODY, children.size, BOX_X + 6, top + 4, ICON, ICON, XpTracker.ICONS[skill], false, false, false))
		val textX: Int = BOX_X + 6 + ICON + 10
		val barY: Int
		if (state.compact) {
			barY = top + 4 + (ICON - BAR_HEIGHT) / 2
		} else {
			val columnWidth: Int = (BOX_WIDTH - (textX - BOX_X) - 8) / 2
			for (i in 0 until 4) {
				val label: Int = XpTracker.panelLabels[i]
				val x: Int = textX + (i % 2) * columnWidth
				val y: Int = top + 4 + (i / 2) * 14
				children.add(ClientPage.text(BODY, children.size, x, y, columnWidth, 14, ClientPage.FONT_SMALL, label(XpTracker.LABELS[label] + ":", XpTracker.labelValue(label, state)), WHITE, 0, 1, false))
			}
			barY = top + 40
		}
		val barX: Int = if (state.compact) textX else BOX_X + 6
		bar(children, skill, state, barX, barY, BOX_X + BOX_WIDTH - 6 - barX)
		val hit: Component = ClientPage.text(BODY, children.size, BOX_X, top, BOX_WIDTH, height, ClientPage.FONT_SMALL, "", WHITE, 0, 0, false)
		hit.hasScript = true
		hit.addOptionName(0, if (state.compact) "Expand" else "Collapse")
		hit.addOptionName(1, "Reset")
		hit.addOptionName(2, "Reset others")
		hit.addOptionName(3, "Reset/hr")
		hit.addOptionName(4, if (state.isPaused()) "Unpause" else "Pause")
		hit.addOptionName(5, if (XpTracker.canvas.contains(skill)) "Remove from canvas" else "Add to canvas")
		hit.opBase = "<col=ff9040>" + Skill.name(skill)
		hit.params = arrayOf<Any?>(SENTINEL, A_BOX, index)
		hit.onMouseOver = arrayOf<Any?>(SENTINEL, A_TIP, skill)
		hit.onMouseLeaveScript = arrayOf<Any?>(SENTINEL, A_TIP_LEAVE, skill)
		children.add(hit)
		return top + height + 6
	}

	/** RuneLite's progress bar: "Lvl. N" at the left and the next at the right, its label in the middle, "Paused" and dimmed while paused. */
	private fun bar(children: ArrayList<Component>, skill: Int, state: XpTrackerState, x: Int, y: Int, width: Int) {
		children.add(ClientPage.rect(BODY, children.size, x, y, width, BAR_HEIGHT, BAR_BACKGROUND, true))
		val filled: Int = (width * (state.progress() / 100.0)).toInt().coerceIn(0, width)
		val fill: Component = ClientPage.rect(BODY, children.size, x, y, filled, BAR_HEIGHT, XpTracker.COLOURS[skill], true)
		if (state.isPaused()) {
			fill.transparency = PAUSED_TRANSPARENCY
		}
		children.add(fill)
		val start: Int = XpTrackerState.levelForXp(state.startLevelExp.toLong())
		val end: String = if (state.endLevelExp == XpTrackerState.MAX_SKILL_XP) "200M" else "Lvl. " + XpTrackerState.levelForXp(state.endLevelExp.toLong())
		children.add(ClientPage.text(BODY, children.size, x + 3, y, 60, BAR_HEIGHT, ClientPage.FONT_SMALL, "Lvl. $start", WHITE, 0, 1, true))
		children.add(ClientPage.text(BODY, children.size, x + width - 63, y, 60, BAR_HEIGHT, ClientPage.FONT_SMALL, end, WHITE, 2, 1, true))
		val middle: String = if (state.isPaused()) "Paused" else XpTracker.barValue(XpTracker.progressBarLabel, state)
		children.add(ClientPage.text(BODY, children.size, x, y, width, BAR_HEIGHT, ClientPage.FONT_SMALL, middle, if (state.isPaused()) DIM else WHITE, 1, 1, true))
	}

	/** RuneLite's progress bar tooltip: the skill, its experience against the goal's, and the tooltip label. */
	@JvmStatic
	fun tooltip(skill: Int): String {
		val state: XpTrackerState = XpTracker.states[skill] ?: return ""
		return Skill.name(skill) + ": " + GroundItemRules.withCommas(state.currentXp()) + " / " + GroundItemRules.withCommas(state.endLevelExp.toLong()) + " exp · " + XpTracker.barValue(XpTracker.progressBarTooltipLabel, state)
	}

	private fun showTip(text: String) {
		val c: Component = ClientPage.childOf(BODY, tipSlot) ?: return
		c.text = text
		c.color = if (text.isEmpty()) DIM else WHITE
		Component.redrawComponent(c)
	}

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
			A_BACK -> hookBack()
			A_HOVER -> if (index < buttons.size) ClientPage.paint(buttons[index], true)
			A_LEAVE -> if (index < buttons.size) ClientPage.paint(buttons[index], false)
			A_WHEEL -> {
				val body: Component = Interface.getComponent(BODY) ?: return
				scrollTo(body.scrollY + hook.mouseY * WHEEL_STEP)
			}
			A_TIP -> {
				tipFor = index
				showTip(tooltip(index))
			}
			A_TIP_LEAVE -> if (tipFor == index) {
				tipFor = -1
				showTip("")
			}
			A_OVERALL -> overallOption(hook.clickedOption)
			A_BOX -> shown.getOrNull(index)?.let { boxOption(it, hook.clickedOption) }
		}
	}

	/** The smoke test's Back. */
	@JvmStatic
	fun hookBack() {
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

	private fun overallOption(option: Int) {
		when (option) {
			1 -> XpTracker.resetAll()
			2 -> XpTracker.resetAllPerHour()
			3 -> XpTracker.pauseAll(true)
			4 -> XpTracker.pauseAll(false)
		}
		rebuild()
	}

	/** A box's menu, by the option clicked (1 up). */
	private fun boxOption(skill: Int, option: Int) {
		val state: XpTrackerState? = XpTracker.states[skill]
		when (option) {
			1 -> state?.let { it.compact = !it.compact }
			2 -> XpTracker.resetSkill(skill)
			3 -> XpTracker.resetOthers(skill)
			4 -> XpTracker.resetPerHour(skill)
			5 -> XpTracker.pause(skill, state?.isPaused() != true)
			6 -> XpTracker.toggleCanvas(skill)
		}
		rebuild()
	}
}
