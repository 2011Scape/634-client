package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.Graphics
import com.jagex.game.runetek5.plugin.api.Viewport
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: the XP Tracker's canvas boxes, RuneLite's XpInfoBoxOverlay: for each
 * skill added to the canvas, a panel with the skill's icon, two lines (the on-screen display modes, by
 * default XP gained and XP an hour) and a progress bar in the skill's colour from its level to the next.
 * They stack down the game view's left side, under the inventory value's panel when it is shown.
 */
object XpTrackerOverlay {

	private const val WIDTH: Int = 150

	private const val ICON: Int = 25

	private const val BAR_HEIGHT: Int = 12

	private const val GAP: Int = 4

	/** RuneLite's ComponentConstants.STANDARD_BACKGROUND_COLOR (70, 61, 50 at 156) and the bar's background (61, 56, 49). */
	private const val BACKGROUND: Int = 0x9C463D32.toInt()

	private const val BAR_BACKGROUND: Int = 0xFF3D3831.toInt()

	private const val BORDER: Int = 0xFF1E1E1E.toInt()

	private const val WHITE: Int = -0x1

	private const val BLACK: Int = -0x1000000

	/** Boxes drawn last frame, for the smoke test. */
	@JvmField
	var drawnLastFrame: Int = 0

	@JvmStatic
	fun draw(g: Graphics, view: Viewport) {
		drawnLastFrame = 0
		val plugin: XpTracker = XpTracker
		val client: ClientApi = plugin.context()?.client() ?: return
		if (!plugin.enabled || !g.fontReady() || client.gameState() !== GameState.LOGGED_IN) {
			return
		}
		val x: Int = view.x + 4
		var y: Int = view.y + maxOf(34, InventoryValue.panelBottom + 6)
		for (skill in plugin.canvasShown()) {
			val state: XpTrackerState = plugin.states[skill] ?: continue
			y += box(g, skill, state, x, y) + GAP
			drawnLastFrame++
		}
	}

	/** One box; its height. */
	private fun box(g: Graphics, skill: Int, state: XpTrackerState, x: Int, y: Int): Int {
		val plugin: XpTracker = XpTracker
		val lineHeight: Int = g.fontHeight()
		val height: Int = 2 + maxOf(ICON, lineHeight * 2) + 2 + BAR_HEIGHT + 2
		g.fillRect(x, y, WIDTH, height, BACKGROUND)
		g.drawRect(x, y, WIDTH, height, BORDER)
		g.sprite(XpTracker.ICONS[skill], x + 3, y + 2)
		val textX: Int = x + 3 + ICON + 4
		val right: Int = x + WIDTH - 4
		var baseline: Int = y + 2 + lineHeight
		for (label in intArrayOf(plugin.onScreenDisplayMode, plugin.onScreenDisplayModeBottom)) {
			g.text(textX, baseline, XpTracker.LABELS[label] + ":", WHITE, BLACK)
			g.textRight(right, baseline, XpTracker.labelValue(label, state), WHITE, BLACK)
			baseline += lineHeight
		}
		val barY: Int = y + 2 + maxOf(ICON, lineHeight * 2) + 2
		bar(g, skill, state, x + 3, barY, WIDTH - 6)
		return height
	}

	/** RuneLite's ProgressBarComponent: the start level at the left, the end level (or 200M) at the right, the progress in the middle. */
	private fun bar(g: Graphics, skill: Int, state: XpTrackerState, x: Int, y: Int, width: Int) {
		g.fillRect(x, y, width, BAR_HEIGHT, BAR_BACKGROUND)
		val filled: Int = (width * (state.progress() / 100.0)).toInt().coerceIn(0, width)
		g.fillRect(x, y, filled, BAR_HEIGHT, -0x1000000 or XpTracker.COLOURS[skill])
		val baseline: Int = y + BAR_HEIGHT - 2
		val start: Int = XpTrackerState.levelForXp(state.startLevelExp.toLong())
		val end: String = if (state.endLevelExp == XpTrackerState.MAX_SKILL_XP) "200M" else XpTrackerState.levelForXp(state.endLevelExp.toLong()).toString()
		g.text(x + 2, baseline, start.toString(), WHITE, BLACK)
		g.textRight(x + width - 2, baseline, end, WHITE, BLACK)
		g.textCentered(x + width / 2, baseline, XpTracker.twoDecimals(state.progress()) + "%", WHITE, BLACK)
	}
}
