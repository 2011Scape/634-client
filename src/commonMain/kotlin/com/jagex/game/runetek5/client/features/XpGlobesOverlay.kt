package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.OverlayLayout
import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.Font
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.Graphics
import com.jagex.game.runetek5.plugin.api.OverlayPosition
import com.jagex.game.runetek5.plugin.api.PanelComponent
import com.jagex.game.runetek5.plugin.api.PanelOverlay
import com.jagex.game.runetek5.plugin.api.Size
import com.jagex.game.runetek5.plugin.api.Skill
import com.jagex.game.runetek5.plugin.api.Task
import com.jagex.game.runetek5.plugin.api.Viewport
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic
import kotlin.math.PI
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.sin

/**
 * Not part of the original client: XP Globes' drawing, RuneLite's XpGlobesOverlay, at the top centre of the
 * game view: each globe a disc in the background colour with the skill's icon, a 5 pixel ring in the
 * outline colour and the progress arc over it from the top, clockwise, as far as the skill has come
 * towards its next level. A hovered globe darkens, shows its percentage, keeps itself up and shows the
 * tooltip. Shift and right-click: Configure, and Flip for the globes down instead of across.
 *
 * The discs and rings are drawn as filled polygons (a ring: the outer edge then the inner one back), which
 * keep the colours' alpha; the client has no arc of its own.
 */
object XpGlobesOverlay : PanelOverlay("XpGlobesOverlay") {

	/** RuneLite's constants. */
	private const val MINIMUM_STEP: Int = 10
	private const val PROGRESS_RADIUS_START: Double = 90.0
	private const val PROGRESS_BACKGROUND_SIZE: Int = 5
	private const val TOOLTIP_RECT_SIZE_X: Int = 150
	private const val DARK_OVERLAY_COLOR: Int = (180 shl 24)
	private const val GLOBE_ICON_RATIO: Double = 0.65

	private const val WHITE: Int = -0x1
	private const val SHADOW: Int = -0x1000000

	/** A ring's corners: one each few degrees, outer then inner. */
	private const val ARC_STEP_DEGREES: Double = 4.0
	private const val MAX_POINTS: Int = 2 * (360 / 4 + 2)

	/** The globes drawn last frame, and the skill hovered (-1 for none), for the smoke test. */
	@JvmField
	var drawnGlobes: Int = 0

	@JvmField
	var hoveredSkill: Int = -1

	private val size: Size = Size()

	private val xs: IntArray = IntArray(MAX_POINTS)

	private val ys: IntArray = IntArray(MAX_POINTS)

	private val tooltip: PanelComponent = PanelComponent().also { it.preferredWidth = TOOLTIP_RECT_SIZE_X }

	init {
		position = OverlayPosition.TOP_CENTER
		addMenuEntry("Configure", "XP Globes overlay", Task { PluginManagerScreen.openSettings(XpGlobes.DESCRIPTOR.id) })
		addMenuEntry("Flip", "XP Globes overlay", Task { XpGlobes.flip() })
	}

	override fun render(g: Graphics, view: Viewport): Size? {
		drawnGlobes = 0
		hoveredSkill = -1
		val client: ClientApi = XpGlobes.context()?.client() ?: return null
		val globes: List<XpGlobes.XpGlobe> = XpGlobes.globes
		if (!XpGlobes.enabled || globes.isEmpty() || client.gameState() !== GameState.LOGGED_IN) {
			return null
		}
		val orb: Int = XpGlobes.xpOrbSize
		// the arcs are drawn either side of the disc's edge: this keeps them inside the overlay
		val arcOffset: Int = ceil(maxOf(PROGRESS_BACKGROUND_SIZE, XpGlobes.progressArcStrokeWidth) / 2.0).toInt()
		var position: Int = arcOffset
		val mouseX: Int = OverlayLayout.mouseX() - boundsX
		val mouseY: Int = OverlayLayout.mouseY() - boundsY
		for (globe in globes) {
			val state: XpTrackerState? = XpGlobes.state(globe.skill)
			val startXp: Int = state?.startLevelExp ?: XpTrackerState.xpForLevel(globe.currentLevel)
			val goalXp: Int = state?.endLevelExp ?: XpTrackerState.xpForLevel(globe.currentLevel + 1)
			if (XpGlobes.alignOrbsVertically) {
				renderProgressCircle(g, globe, state, startXp, goalXp, arcOffset, position, mouseX, mouseY)
			} else {
				renderProgressCircle(g, globe, state, startXp, goalXp, position, arcOffset, mouseX, mouseY)
			}
			position += MINIMUM_STEP + orb
			drawnGlobes++
		}
		val length: Int = globes.size * (orb + arcOffset) + MINIMUM_STEP * (globes.size - 1)
		return if (XpGlobes.alignOrbsVertically) size.set(orb + arcOffset * 2, length) else size.set(length, orb + arcOffset * 2)
	}

	/** RuneLite's getSkillProgress: per cent of the way from [startXp] to [goalXp]. */
	@JvmStatic
	fun skillProgress(startXp: Int, currentXp: Long, goalXp: Int): Double {
		val goal: Double = (goalXp - startXp).toDouble()
		return if (goal <= 0.0) 100.0 else (currentXp - startXp) / goal * 100.0
	}

	private fun renderProgressCircle(g: Graphics, globe: XpGlobes.XpGlobe, state: XpTrackerState?, startXp: Int, goalXp: Int, x: Int, y: Int, mouseX: Int, mouseY: Int) {
		val orb: Int = XpGlobes.xpOrbSize
		val radius: Double = orb / 2.0
		val cx: Double = x + radius
		val cy: Double = y + radius
		// RuneLite's arc goes backwards (a negative extent): clockwise from the top
		val progressSweep: Double = -(3.6 * skillProgress(startXp, globe.currentXp, goalXp))
		disc(g, cx, cy, radius, XpGlobes.progressOrbBackgroundColor)
		drawSkillImage(g, globe, x, y)
		val dx: Double = mouseX - cx
		val dy: Double = mouseY - cy
		if (dx * dx + dy * dy <= radius * radius) {
			hoveredSkill = globe.skill
			disc(g, cx, cy, radius, DARK_OVERLAY_COLOR)
			drawProgressLabel(g, globe, startXp, goalXp, cx.toInt(), cy.toInt())
			if (XpGlobes.enableTooltips) {
				drawTooltip(globe, state, goalXp)
			}
		}
		ring(g, cx, cy, radius, 0.0, 360.0, PROGRESS_BACKGROUND_SIZE, XpGlobes.progressOrbOutLineColor)
		val colour: Int = if (XpGlobes.enableCustomArcColor) XpGlobes.progressArcColor else SkillSprites.COLOURS[globe.skill] or 0xFF000000.toInt()
		ring(g, cx, cy, radius, PROGRESS_RADIUS_START, progressSweep, XpGlobes.progressArcStrokeWidth, colour)
	}

	/** A filled disc as a polygon: Graphics.fillCircle draws nothing with a translucent colour on the HD toolkit. */
	private fun disc(g: Graphics, cx: Double, cy: Double, radius: Double, argb: Int) {
		if (argb ushr 24 == 0) {
			return
		}
		val steps: Int = (360.0 / ARC_STEP_DEGREES).toInt()
		for (i in 0 until steps) {
			val angle: Double = 2.0 * PI * i / steps
			xs[i] = kotlin.math.round(cx + radius * cos(angle)).toInt()
			ys[i] = kotlin.math.round(cy - radius * sin(angle)).toInt()
		}
		g.fillPolygon(xs, ys, steps, argb)
	}

	/** An arc [width] pixels wide about the circle of [radius], from [startDegrees] (0 at three o'clock, anticlockwise) through [sweepDegrees]. */
	private fun ring(g: Graphics, cx: Double, cy: Double, radius: Double, startDegrees: Double, sweepDegrees: Double, width: Int, argb: Int) {
		val count: Int = arcPoints(cx, cy, radius + width / 2.0, radius - width / 2.0, startDegrees, sweepDegrees, xs, ys)
		if (count >= 3 && argb ushr 24 != 0) {
			g.fillPolygon(xs, ys, count, argb)
		}
	}

	/**
	 * The corners of an arc band between radii [outer] and [inner] about ([cx], [cy]), from [startDegrees]
	 * through [sweepDegrees] (Java's Arc2D: 0 at three o'clock, positive anticlockwise on the screen): the
	 * outer edge forwards, then the inner one back. The number of points written to [xs] and [ys].
	 */
	@JvmStatic
	fun arcPoints(cx: Double, cy: Double, outer: Double, inner: Double, startDegrees: Double, sweepDegrees: Double, xs: IntArray, ys: IntArray): Int {
		if (sweepDegrees == 0.0) {
			return 0
		}
		val sweep: Double = sweepDegrees.coerceIn(-360.0, 360.0)
		val steps: Int = maxOf(1, ceil(kotlin.math.abs(sweep) / ARC_STEP_DEGREES).toInt())
		var n: Int = 0
		for (i in 0..steps) {
			val angle: Double = (startDegrees + sweep * i / steps) * PI / 180.0
			xs[n] = kotlin.math.round(cx + outer * cos(angle)).toInt()
			ys[n] = kotlin.math.round(cy - outer * sin(angle)).toInt()
			n++
		}
		for (i in steps downTo 0) {
			val angle: Double = (startDegrees + sweep * i / steps) * PI / 180.0
			xs[n] = kotlin.math.round(cx + inner * cos(angle)).toInt()
			ys[n] = kotlin.math.round(cy - inner * sin(angle)).toInt()
			n++
		}
		return n
	}

	private fun drawProgressLabel(g: Graphics, globe: XpGlobes.XpGlobe, startXp: Int, goalXp: Int, cx: Int, cy: Int) {
		if (goalXp <= globe.currentXp || !g.fontReady()) {
			return
		}
		g.font(Font.REGULAR)
		val text: String = skillProgress(startXp, globe.currentXp, goalXp).toInt().toString() + "%"
		g.text(cx - g.textWidth(text) / 2, cy + g.fontHeight() / 2, text, WHITE, SHADOW)
	}

	private fun drawSkillImage(g: Graphics, globe: XpGlobes.XpGlobe, x: Int, y: Int) {
		val orb: Int = XpGlobes.xpOrbSize
		val iconSize: Int = ((orb - XpGlobes.progressArcStrokeWidth) * GLOBE_ICON_RATIO).toInt()
		if (iconSize > 0) {
			g.spriteScaled(SkillSprites.ICONS[globe.skill], x + orb / 2 - iconSize / 2, y + orb / 2 - iconSize / 2, iconSize, iconSize)
		}
	}

	/** RuneLite's drawTooltip: the skill and level, then the lines the settings ask for while the level is not reached. */
	private fun drawTooltip(globe: XpGlobes.XpGlobe, state: XpTrackerState?, goalXp: Int) {
		// hovering keeps the globe up
		globe.time = XpGlobes.clock()
		val client: ClientApi = XpGlobes.context()?.client() ?: return
		tooltip.children.clear()
		tooltipLines(tooltip, globe, state, goalXp)
		client.addTooltip(tooltip)
	}

	/** The tooltip's lines, RuneLite's: labels in orange, values in white. */
	@JvmStatic
	fun tooltipLines(panel: PanelComponent, globe: XpGlobes.XpGlobe, state: XpTrackerState?, goalXp: Int) {
		panel.addLine(Skill.name(globe.skill), WHITE, globe.currentLevel.toString(), WHITE)
		panel.addLine("Current XP:", XpGlobes.ORANGE, InventoryValue.withCommas(globe.currentXp), WHITE)
		if (goalXp <= globe.currentXp) {
			return
		}
		if (XpGlobes.showActionsLeft && state != null) {
			val actionsLeft: Int = state.actionsRemaining()
			if (actionsLeft != Int.MAX_VALUE) {
				panel.addLine("Actions left:", XpGlobes.ORANGE, InventoryValue.withCommas(actionsLeft.toLong()), WHITE)
			}
		}
		if (XpGlobes.showXpLeft) {
			panel.addLine("XP left:", XpGlobes.ORANGE, InventoryValue.withCommas(goalXp - globe.currentXp), WHITE)
		}
		if (XpGlobes.showXpHour && state != null) {
			val xpHr: Int = state.xpPerHour()
			if (xpHr != 0) {
				panel.addLine("XP per hour:", XpGlobes.ORANGE, InventoryValue.withCommas(xpHr.toLong()), WHITE)
			}
		}
		if (XpGlobes.showTimeTilGoal && state != null) {
			panel.addLine("Time left:", XpGlobes.ORANGE, state.timeTillLevel(XpTrackerState.SHORT), WHITE)
		}
	}
}
