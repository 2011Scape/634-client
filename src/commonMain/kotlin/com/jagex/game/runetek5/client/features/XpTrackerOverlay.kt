package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.Font
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.Graphics
import com.jagex.game.runetek5.plugin.api.PanelComponent
import com.jagex.game.runetek5.plugin.api.PanelOverlay
import com.jagex.game.runetek5.plugin.api.PluginContext
import com.jagex.game.runetek5.plugin.api.Size
import com.jagex.game.runetek5.plugin.api.Skill
import com.jagex.game.runetek5.plugin.api.Task
import com.jagex.game.runetek5.plugin.api.Viewport
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: the XP Tracker's canvas boxes, RuneLite's XpInfoBoxOverlay: for each
 * skill added to the canvas, a panel of its own with the skill's icon, two lines (the on-screen display
 * modes, by default XP gained and XP an hour) and a progress bar in the skill's colour from its level to
 * the next. They stack at the game view's top left in the order added, each movable (holding Alt) and
 * resizable as RuneLite's are.
 */
object XpTrackerOverlay {

	private const val WIDTH: Int = 150

	private const val ICON: Int = 25

	private const val BAR_HEIGHT: Int = 12

	/** The bar's background (61, 56, 49). */
	private const val BAR_BACKGROUND: Int = 0xFF3D3831.toInt()

	private const val WHITE: Int = -0x1

	private const val BLACK: Int = -0x1000000

	/** One skill's box, RuneLite's XpInfoBoxOverlay; kept under the skill's name, so each keeps its own place. */
	class SkillPanel(@JvmField val skill: Int) : PanelOverlay("XpInfoBoxOverlay_" + Skill.name(skill)) {
		/** Whether it drew last frame, for the smoke test. */
		@JvmField
		var drawn: Boolean = false

		private val size: Size = Size()

		init {
			resizable = true
			addMenuEntry("Configure", "XP Tracker overlay", Task { PluginManagerScreen.openSettings(XpTracker.DESCRIPTOR.id) })
		}

		override fun render(g: Graphics, view: Viewport): Size? {
			drawn = false
			val plugin: XpTracker = XpTracker
			val client: ClientApi = plugin.context()?.client() ?: return null
			if (!plugin.enabled || !g.fontReady() || client.gameState() !== GameState.LOGGED_IN || !plugin.canvasShown().contains(skill)) {
				return null
			}
			val state: XpTrackerState = plugin.states[skill] ?: return null
			// RuneLite sets the small font so that the box is not huge
			g.font(Font.SMALL)
			val width: Int = if (preferredWidth > 0) preferredWidth else WIDTH
			val height: Int = box(g, skill, state, 0, 0, width, backgroundColour)
			drawn = true
			return size.set(width, height)
		}
	}

	/** The skills' panels, made as needed, and the ones the plugin has given the client, in canvas order. */
	private val panels: Array<SkillPanel?> = arrayOfNulls<SkillPanel?>(Skill.COUNT)

	private val added: ArrayList<Int> = ArrayList()

	/** Boxes drawn last frame, for the smoke test. */
	@JvmStatic
	fun drawnLastFrame(): Int {
		var n: Int = 0
		for (skill in added) {
			if (panels[skill]?.drawn == true) {
				n++
			}
		}
		return n
	}

	/**
	 * Each client cycle: one panel per canvas skill, given to the client in the canvas's order (taken back
	 * and given again when the order changed), as RuneLite adds an overlay when a skill is added.
	 */
	@JvmStatic
	fun sync(context: PluginContext) {
		val canvas: List<Int> = XpTracker.canvas
		if (canvas == added) {
			return
		}
		for (skill in added) {
			panels[skill]?.let { context.removeOverlay(it) }
		}
		added.clear()
		for (skill in canvas) {
			var panel: SkillPanel? = panels[skill]
			if (panel == null) {
				panel = SkillPanel(skill)
				panels[skill] = panel
			}
			context.addOverlay(panel)
			added.add(skill)
		}
	}

	/** The plugin stopped: its panels went with it. */
	@JvmStatic
	fun forget() {
		added.clear()
	}

	/** One box [width] wide on [background]; its height. */
	private fun box(g: Graphics, skill: Int, state: XpTrackerState, x: Int, y: Int, width: Int, background: Int): Int {
		val plugin: XpTracker = XpTracker
		val lineHeight: Int = g.fontHeight()
		val height: Int = 2 + maxOf(ICON, lineHeight * 2) + 2 + BAR_HEIGHT + 2
		PanelComponent.background(g, x, y, width, height, background)
		g.sprite(SkillSprites.ICONS[skill], x + 3, y + 2)
		val textX: Int = x + 3 + ICON + 4
		val right: Int = x + width - 4
		var baseline: Int = y + 2 + lineHeight
		for (label in intArrayOf(plugin.onScreenDisplayMode, plugin.onScreenDisplayModeBottom)) {
			g.text(textX, baseline, XpTracker.LABELS[label] + ":", WHITE, BLACK)
			g.textRight(right, baseline, XpTracker.labelValue(label, state), WHITE, BLACK)
			baseline += lineHeight
		}
		val barY: Int = y + 2 + maxOf(ICON, lineHeight * 2) + 2
		bar(g, skill, state, x + 3, barY, width - 6)
		return height
	}

	/** RuneLite's ProgressBarComponent: the start level at the left, the end level (or 200M) at the right, the progress in the middle. */
	private fun bar(g: Graphics, skill: Int, state: XpTrackerState, x: Int, y: Int, width: Int) {
		g.fillRect(x, y, width, BAR_HEIGHT, BAR_BACKGROUND)
		val filled: Int = (width * (state.progress() / 100.0)).toInt().coerceIn(0, width)
		g.fillRect(x, y, filled, BAR_HEIGHT, -0x1000000 or SkillSprites.COLOURS[skill])
		val baseline: Int = y + BAR_HEIGHT - 2
		val start: Int = XpTrackerState.levelForXp(state.startLevelExp.toLong())
		val end: String = if (state.endLevelExp == XpTrackerState.MAX_SKILL_XP) "200M" else XpTrackerState.levelForXp(state.endLevelExp.toLong()).toString()
		g.text(x + 2, baseline, start.toString(), WHITE, BLACK)
		g.textRight(x + width - 2, baseline, end, WHITE, BLACK)
		g.textCentered(x + width / 2, baseline, XpTracker.twoDecimals(state.progress()) + "%", WHITE, BLACK)
	}
}
