package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.Actor
import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.Font
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.Graphics
import com.jagex.game.runetek5.plugin.api.Viewport
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic
import kotlin.math.floor
import kotlin.math.round

/**
 * Not part of the original client: Opponent Information's panel, RuneLite's OpponentInfoOverlay: the
 * opponent's name over its health bar, at the top left of the game view. The name, the bar and the most
 * lifepoints are taken while the opponent's bar shows and kept between bars, as RuneLite keeps them. The
 * lifepoints are worked back from the bar as RuneLite does; with the most unknown the bar shows a
 * percentage whatever the style.
 */
object OpponentInfoOverlay {

	/** RuneLite's ComponentConstants.STANDARD_WIDTH, STANDARD_BORDER and the panel's own border. */
	private const val STANDARD_WIDTH: Int = 129
	private const val STANDARD_BORDER: Int = 4
	private const val BORDER: Int = 2
	private const val GAP: Int = 2
	private const val BAR_HEIGHT: Int = 16

	/** Where the panel sits: the left edge and the top the other left-hand panels use. */
	private const val LEFT: Int = 4
	const val TOP: Int = 34

	/** RuneLite's STANDARD_BACKGROUND_COLOR (70, 61, 50 at 156), HP_GREEN and HP_RED. */
	private const val BACKGROUND: Int = 0x9C463D32.toInt()
	private const val HP_GREEN: Int = 0xE6009236.toInt()
	private const val HP_RED: Int = 0xE6660F10.toInt()

	private const val WHITE: Int = -0x1

	private const val BLACK: Int = -0x1000000

	/** RuneLite's lastMaxHealth, lastRatio, lastHealthScale and opponentName. */
	private var lastMaxHealth: Int? = null
	private var lastRatio: Int = 0
	private var lastHealthScale: Int = 0
	private var opponentName: String? = null

	/** Where the panel ends (from the view's top; 0 when not drawn), for the left-hand panels below it. */
	@JvmField
	var panelBottom: Int = 0

	/** The name and the bar's text drawn last frame (null when none), for the smoke test. */
	@JvmField var drawnName: String? = null
	@JvmField var drawnLabel: String? = null

	@JvmStatic
	fun reset() {
		lastMaxHealth = null
		lastRatio = 0
		lastHealthScale = 0
		opponentName = null
		panelBottom = 0
	}

	@JvmStatic
	fun draw(g: Graphics, view: Viewport) {
		panelBottom = 0
		drawnName = null
		drawnLabel = null
		val plugin: OpponentInfo = OpponentInfo
		val client: ClientApi = plugin.context()?.client() ?: return
		if (!plugin.enabled || !g.fontReady() || client.gameState() !== GameState.LOGGED_IN) {
			return
		}
		val opponent: Actor? = plugin.lastOpponent
		if (opponent == null) {
			opponentName = null
			return
		}
		refresh(opponent)
		val name: String = opponentName ?: return
		if (!plugin.showOpponentHealthOverlay) {
			return
		}
		g.font(Font.REGULAR)
		val lineHeight: Int = g.fontHeight()
		val width: Int = maxOf(STANDARD_WIDTH, g.textWidth(name) + STANDARD_BORDER + STANDARD_BORDER)
		val showBar: Boolean = lastRatio >= 0 && lastHealthScale > 0
		val height: Int = BORDER + lineHeight + (if (showBar) GAP + BAR_HEIGHT else 0) + BORDER
		val x: Int = view.x + LEFT
		val top: Int = view.y + TOP
		g.fillRect(x, top, width, height, BACKGROUND)
		g.textCentered(x + width / 2, top + BORDER + lineHeight - 2, name, WHITE, BLACK)
		drawnName = name
		if (showBar) {
			val barX: Int = x + BORDER
			val barY: Int = top + BORDER + lineHeight + GAP
			val barWidth: Int = width - BORDER - BORDER
			val label: String = bar(g, barX, barY, barWidth, plugin.hitpointsDisplayStyle)
			drawnLabel = label
		}
		panelBottom = top + height - view.y
	}

	/** RuneLite's refresh: while the bar shows, the opponent's name, bar and most lifepoints. */
	private fun refresh(opponent: Actor) {
		val name: String? = OpponentInfo.nameOf(opponent)
		if (name == null || opponent.healthScale() <= 0) {
			return
		}
		lastRatio = opponent.healthRatio()
		lastHealthScale = opponent.healthScale()
		opponentName = name
		lastMaxHealth = OpponentInfo.maxHealthOf(opponent)
	}

	/** RuneLite's ProgressBarComponent with the panel's values; the label it drew. */
	private fun bar(g: Graphics, x: Int, y: Int, width: Int, style: Int): String {
		val max: Int? = lastMaxHealth
		val value: Double
		val maximum: Double
		val label: String
		if (style != OpponentInfo.STYLE_PERCENTAGE && max != null) {
			value = healthFromRatio(lastRatio, lastHealthScale, max).toDouble()
			maximum = max.toDouble()
			label = if (style == OpponentInfo.STYLE_BOTH) bothLabel(value, max) else fullLabel(value, max)
		} else {
			value = lastRatio / lastHealthScale.toFloat() * 100.0
			maximum = 100.0
			label = percentLabel(value / maximum)
		}
		val fraction: Double = value / maximum
		val filled: Int = (width * minOf(1.0, fraction)).toInt()
		g.fillRect(x, y, filled, BAR_HEIGHT, HP_GREEN)
		g.fillRect(x + filled, y, width - filled, BAR_HEIGHT, HP_RED)
		g.textCentered(x + width / 2, y + (BAR_HEIGHT - g.fontHeight()) / 2 + g.fontHeight() - 1, label, WHITE, BLACK)
		return label
	}

	/**
	 * RuneLite's lifepoints from a bar: the server makes the bar `ratio = 1 + (scale - 1) * hp / max`
	 * (0 only when dead), so the lifepoints lie between two bounds; the middle of them, rounded up.
	 */
	@JvmStatic
	fun healthFromRatio(ratio: Int, scale: Int, max: Int): Int {
		if (ratio <= 0) {
			return 0
		}
		var minHealth: Int = 1
		var maxHealth: Int
		if (scale > 1) {
			if (ratio > 1) {
				// the lowest lifepoints at which the bar is this full
				minHealth = (max * (ratio - 1) + scale - 2) / (scale - 1)
			}
			// the highest
			maxHealth = (max * ratio - 1) / (scale - 1)
			if (maxHealth > max) {
				maxHealth = max
			}
		} else {
			maxHealth = max
		}
		return (minHealth + maxHealth + 1) / 2
	}

	/** RuneLite's ProgressBarComponent labels: FULL "50/99", PERCENTAGE "50.0%", BOTH "50/99 (50.5%)". */
	@JvmStatic
	fun fullLabel(value: Double, max: Int): String = floor(value).toLong().toString() + "/" + max

	@JvmStatic
	fun percentLabel(fraction: Double): String = oneDecimal(fraction * 100.0) + "%"

	@JvmStatic
	fun bothLabel(value: Double, max: Int): String = fullLabel(value, max) + " (" + percentLabel(value / max) + ")"

	/** DecimalFormat("0.0"): one decimal, a half to the even digit. */
	private fun oneDecimal(value: Double): String {
		val tenths: Long = round(value * 10.0).toLong()
		return (tenths / 10).toString() + "." + (tenths % 10)
	}
}
