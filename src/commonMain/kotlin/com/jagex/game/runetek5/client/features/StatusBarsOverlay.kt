package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.Font
import com.jagex.game.runetek5.plugin.api.Graphics
import com.jagex.game.runetek5.plugin.api.Widget
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: Status Bars' drawing, RuneLite's StatusBarsOverlay and BarRenderer. The
 * bars stand beside the side panel's tab area (where every tab opens), 4 pixels down and RuneLite's 252
 * tall: on the fixed screen over the stone columns either side of it, as RuneLite's do; on the resizable
 * one the left bar against its left edge, and the right one too (left of the left one) when the panel
 * reaches the screen's edge. Drawn at FRAME_DRAW, over the interfaces, as RuneLite's ABOVE_WIDGETS overlay.
 */
object StatusBarsOverlay {

	/** The root interfaces, and the first tab slot of each one's side panel: the fixed screen's (548) and the resizable one's (746). */
	const val FIXED: Int = 548
	const val RESIZABLE: Int = 746
	private const val FIXED_FIRST_TAB: Int = 202
	private const val RESIZABLE_FIRST_TAB: Int = 86

	/** RuneLite's offsets and height: both bars 4 pixels below the tab area's top, 252 tall. */
	private const val OFFSET_Y: Int = 4
	private const val HEIGHT: Int = 252

	/** RuneLite's colours, ARGB. */
	const val BACKGROUND: Int = (150 shl 24)
	const val OVERHEAL: Int = (150 shl 24) or 0xD8FF8B
	const val HEALTH: Int = (125 shl 24) or 0xE12300
	const val POISONED: Int = (150 shl 24) or 0x009100
	const val DISEASE: Int = (181 shl 24) or 0xFFC14B
	const val HEAL: Int = (150 shl 24) or 0xFF7006
	const val PRAYER: Int = (175 shl 24) or 0x32C8C8
	const val ACTIVE_PRAYER: Int = (225 shl 24) or 0x39FFBA
	const val PRAYER_HEAL: Int = (75 shl 24) or 0x39FFBA
	const val ENERGY: Int = (220 shl 24) or 0xC7AE00
	const val RUN_RESTING: Int = (255 shl 24) or 0xA07C48
	const val ENERGY_HEAL: Int = (218 shl 24) or 0xC77600
	const val SPECIAL_ATTACK: Int = (195 shl 24) or 0x039900
	const val SUMMONING: Int = (200 shl 24) or 0x3C9999
	const val SUMMONING_HEAL: Int = (90 shl 24) or 0x3C9999

	private const val BORDER: Int = 1
	private const val SKILL_ICON_HEIGHT: Int = 35
	private const val COUNTER_ICON_HEIGHT: Int = 18
	private const val MIN_ICON_AND_COUNTER_WIDTH: Int = 16

	/** The orbs' icons, read from the orbs each frame so that they follow the game's own (a poisoned heart). */
	private const val HEALTH_ORB: Int = 748
	private const val HEALTH_ICON: Int = 7
	private const val PRAYER_ORB: Int = 749
	private const val PRAYER_ICON: Int = 5
	private const val ENERGY_ORB: Int = 750
	private const val ENERGY_ICON: Int = 6
	private const val SUMMONING_ORB: Int = 747
	private const val SUMMONING_ICON: Int = 6

	/** The icons when the orbs are not loaded: heart, prayer, run and summoning; Attack's for special attack, which has no orb in 634. */
	private const val HEART: Int = 1197
	private const val PRAYER_SPRITE: Int = 1198
	private const val RUN_SPRITE: Int = 1199
	private const val SUMMONING_SPRITE: Int = 1200
	private const val ATTACK_ICON: Int = 197
	private const val ATTACK_ICON_SIZE: Int = 18

	/** Where the bars were drawn last frame (left x, right x, y, height; 0s when not), for the smoke test. */
	@JvmField
	val drawn: IntArray = IntArray(4)

	/**
	 * The tab area the bars stand beside (RuneLite's side panels): x, y, width, height on the screen, false
	 * when it does not show. It is the outermost container of the tab slots that is the same size as they
	 * are (fixed 548:194, resizable 746:85), so it shows whichever tab is open, or none.
	 */
	@JvmStatic
	fun sidePanel(client: ClientApi, out: IntArray): Boolean {
		val top: Int = client.topInterface()
		val first: Int = when (top) {
			FIXED -> FIXED_FIRST_TAB
			RESIZABLE -> RESIZABLE_FIRST_TAB
			else -> return false
		}
		var panel: Widget = client.widget(top, first) ?: return false
		var parent: Widget? = container(client, panel)
		var depth: Int = 0
		while (parent != null && depth++ < 8) {
			panel = parent
			parent = container(client, panel)
		}
		if (!shown(client, panel) || panel.width() <= 0 || panel.height() <= 0) {
			return false
		}
		out[0] = panel.screenX()
		out[1] = panel.screenY()
		out[2] = panel.width()
		out[3] = panel.height()
		return true
	}

	/** [w]'s parent when it covers the same area, else null. */
	private fun container(client: ClientApi, w: Widget): Widget? {
		val parent: Widget = (if (w.parentId() == -1) null else client.widgetById(w.parentId())) ?: return null
		val same: Boolean = parent.screenX() == w.screenX() && parent.screenY() == w.screenY() && parent.width() == w.width() && parent.height() == w.height()
		return if (same) parent else null
	}

	/** Not hidden, nor any parent of it. */
	private fun shown(client: ClientApi, widget: Widget): Boolean {
		var w: Widget? = widget
		var depth: Int = 0
		while (w != null && depth++ < 32) {
			if (w.isHidden()) {
				return false
			}
			w = if (w.parentId() == -1) null else client.widgetById(w.parentId())
		}
		return true
	}

	private val panel: IntArray = IntArray(4)

	@JvmStatic
	fun draw(g: Graphics, client: ClientApi, screenWidth: Int) {
		drawn.fill(0)
		if (!sidePanel(client, panel)) {
			return
		}
		val width: Int = if (client.topInterface() == RESIZABLE) StatusBars.barWidth else StatusBars.DEFAULT_WIDTH
		val height: Int = minOf(HEIGHT, panel[3] - OFFSET_Y)
		val y: Int = panel[1] + OFFSET_Y
		// the resizable side panel reaches the screen's right edge: then both bars stand left of it, the left bar outermost
		val room: Boolean = panel[0] + panel[2] + width <= screenWidth
		val leftX: Int = if (room) panel[0] - width else panel[0] - 2 * width
		val rightX: Int = if (room) panel[0] + panel[2] else panel[0] - width
		if (StatusBars.leftBarMode != StatusBars.DISABLED) {
			renderBar(g, client, StatusBars.leftBarMode, leftX, y, width, height)
		}
		if (StatusBars.rightBarMode != StatusBars.DISABLED) {
			renderBar(g, client, StatusBars.rightBarMode, rightX, y, width, height)
		}
		drawn[0] = leftX
		drawn[1] = rightX
		drawn[2] = y
		drawn[3] = height
	}

	/** RuneLite's BarRenderer.renderBar. */
	private fun renderBar(g: Graphics, client: ClientApi, mode: Int, x: Int, y: Int, width: Int, height: Int) {
		val max: Int = maxOf(1, StatusBars.maximum(client, mode))
		val current: Int = StatusBars.current(client, mode)
		val filled: Int = barHeight(max, current, height)
		g.drawRect(x, y, width, height, BACKGROUND)
		g.fillRect(x, y, width, height, BACKGROUND)
		g.fillRect(x + BORDER, y + BORDER + (height - filled), width - BORDER * 2, filled - BORDER * 2, colour(client, mode))
		if (StatusBars.enableRestorationBars) {
			renderRestore(g, client, mode, x, y, width, height, max, filled)
		}
		if ((StatusBars.enableSkillIcon || StatusBars.enableCounter) && width >= MIN_ICON_AND_COUNTER_WIDTH) {
			renderIconAndCounter(g, client, mode, x, y, width, current)
		}
	}

	private fun renderRestore(g: Graphics, client: ClientApi, mode: Int, x: Int, y: Int, width: Int, height: Int, max: Int, filledCurrent: Int) {
		val heal: Int = StatusBars.restore(client, mode)
		if (heal <= 0) {
			return
		}
		val filledHeal: Int = barHeight(max, heal, height)
		if (filledHeal + filledCurrent > height) {
			g.fillRect(x + BORDER, y + BORDER, width - BORDER * 2, height - filledCurrent - BORDER, OVERHEAL)
		} else {
			g.fillRect(x + BORDER, y + BORDER + height - (filledCurrent + filledHeal), width - BORDER * 2, filledHeal, healColour(mode))
		}
	}

	private fun renderIconAndCounter(g: Graphics, client: ClientApi, mode: Int, x: Int, y: Int, width: Int, current: Int) {
		if (StatusBars.enableSkillIcon) {
			if (mode == StatusBars.SPECIAL_ATTACK) {
				g.spriteScaled(ATTACK_ICON, x + width / 2 - ATTACK_ICON_SIZE / 2, y + 4, ATTACK_ICON_SIZE, ATTACK_ICON_SIZE)
			} else {
				val icon: Int = icon(client, mode)
				g.sprite(icon, x + width / 2 - g.spriteWidth(icon) / 2, y + 4)
			}
		}
		if (StatusBars.enableCounter && g.fontReady()) {
			g.font(Font.SMALL)
			val text: String = current.toString()
			val yOffset: Int = if (StatusBars.enableSkillIcon) SKILL_ICON_HEIGHT else COUNTER_ICON_HEIGHT
			g.text(x + width / 2 - g.textWidth(text) / 2, y + yOffset, text, -0x1, -0x1000000)
		}
	}

	/** RuneLite's getBarHeight: the share of [size] that [current] of [base] fills. */
	@JvmStatic
	fun barHeight(base: Int, current: Int, size: Int): Int {
		val ratio: Double = current.toDouble() / base
		if (ratio >= 1) {
			return size
		}
		return kotlin.math.round(ratio * size).toInt()
	}

	@JvmStatic
	fun colour(client: ClientApi, mode: Int): Int = when (mode) {
		StatusBars.HITPOINTS -> when {
			client.varp(StatusBars.POISON_VARP) > 0 -> POISONED
			client.varp(StatusBars.DISEASE_VARP) > 0 -> DISEASE
			else -> HEALTH
		}
		StatusBars.PRAYER -> if (prayerActive(client)) ACTIVE_PRAYER else PRAYER
		StatusBars.RUN_ENERGY -> if (client.varp(StatusBars.MOVEMENT_VARP) == StatusBars.RESTING) RUN_RESTING else ENERGY
		StatusBars.SPECIAL_ATTACK -> SPECIAL_ATTACK
		else -> SUMMONING
	}

	private fun healColour(mode: Int): Int = when (mode) {
		StatusBars.HITPOINTS -> HEAL
		StatusBars.PRAYER -> PRAYER_HEAL
		StatusBars.RUN_ENERGY -> ENERGY_HEAL
		else -> SUMMONING_HEAL
	}

	/** A prayer or curse on, by the book in use. */
	@JvmStatic
	fun prayerActive(client: ClientApi): Boolean {
		val curses: Boolean = client.varp(Boosts.PRAYER_BOOK_VARP) == 1
		return client.varp(if (curses) Boosts.CURSES_VARP else Boosts.PRAYERS_VARP) != 0
	}

	private fun icon(client: ClientApi, mode: Int): Int {
		val (orb, child, fallback) = when (mode) {
			StatusBars.HITPOINTS -> Triple(HEALTH_ORB, HEALTH_ICON, HEART)
			StatusBars.PRAYER -> Triple(PRAYER_ORB, PRAYER_ICON, PRAYER_SPRITE)
			StatusBars.RUN_ENERGY -> Triple(ENERGY_ORB, ENERGY_ICON, RUN_SPRITE)
			else -> Triple(SUMMONING_ORB, SUMMONING_ICON, SUMMONING_SPRITE)
		}
		val sprite: Int = client.widget(orb, child)?.spriteId() ?: -1
		return if (sprite >= 0) sprite else fallback
	}
}
