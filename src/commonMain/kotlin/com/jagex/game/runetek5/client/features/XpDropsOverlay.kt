package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.Font
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.Graphics
import com.jagex.game.runetek5.plugin.api.Skill
import com.jagex.game.runetek5.plugin.api.Viewport
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: XP Drops' drawing, as Old School draws its XP drops: the counter at
 * the top (when chosen) with the progress bar under it, and the drops rising toward it and fading, each
 * its skill icons (unless hidden) and "+N" in its colour, newer ones below older. On the right they sit
 * left of the minimap (which covers the game view's corner in the resizable modes), in the middle at the
 * view's centre, on the left at its edge.
 */
object XpDropsOverlay {

	/** The skills' icons, the Stats tab's sprites, by skill. */
	@JvmField
	val ICONS: IntArray = intArrayOf(197, 199, 198, 203, 200, 201, 202, 212, 214, 208, 211, 213, 207, 210, 209, 205, 204, 206, 216, 217, 215, 220, 221, 222, 3032)

	/** The font by size (Smallest to Largest); the skill icons are drawn at their own size, as Old School draws them. */
	private val FONTS: IntArray = intArrayOf(Font.SMALL, Font.SMALL, Font.REGULAR, Font.BOLD, Font.BOLD)

	/** The Stats tab icons' size before the sprite has loaded. */
	private const val ICON_FALLBACK: Int = 25

	/** The minimap's width over the game view's top-right corner in the resizable modes. */
	private const val MINIMAP_WIDTH: Int = 215

	private const val MARGIN: Int = 8

	private const val COUNTER_WIDTH: Int = 130

	private const val BAR_HEIGHT: Int = 10

	private const val BACKGROUND: Int = 0x9C463D32.toInt()

	private const val BORDER: Int = 0xFF1E1E1E.toInt()

	private const val BAR_BACKGROUND: Int = 0xFF3D3831.toInt()

	private const val BAR_FILL: Int = 0xFF2FA82F.toInt()

	private const val WHITE: Int = -0x1

	private const val BLACK: Int = -0x1000000

	@JvmStatic
	fun draw(g: Graphics, view: Viewport) {
		val plugin: XpDrops = XpDrops
		plugin.drawnLastFrame = 0
		val client: ClientApi = plugin.context()?.client() ?: return
		if (!plugin.enabled || !g.fontReady() || client.gameState() !== GameState.LOGGED_IN) {
			return
		}
		g.font(FONTS[plugin.size])
		val lineHeight: Int = maxOf(g.fontHeight(), if (plugin.hideSkillIcons) 0 else iconHeight(g, ICONS[0]))
		// the anchor: the right edge, the centre or the left edge of what is drawn
		val resizable: Boolean = view.x == 0 && view.y == 0
		val anchorX: Int = when (plugin.position) {
			XpDrops.POSITION_MIDDLE -> view.x + view.width / 2
			XpDrops.POSITION_LEFT -> view.x + MARGIN
			else -> view.x + view.width - MARGIN - (if (resizable) MINIMAP_WIDTH else 0)
		}
		var top: Int = view.y + MARGIN
		top = counter(g, client, plugin, anchorX, top)
		top = progressBar(g, client, plugin, anchorX, top)
		drops(g, client, plugin, anchorX, top, lineHeight)
	}

	/** The x where a box of [width] starts for the position's alignment. */
	private fun boxX(anchorX: Int, width: Int): Int {
		return when (XpDrops.position) {
			XpDrops.POSITION_MIDDLE -> anchorX - width / 2
			XpDrops.POSITION_LEFT -> anchorX
			else -> anchorX - width
		}
	}

	/** Old School's XP counter: the total experience, or the most recent skill's with its icon. Returns where the next piece goes. */
	private fun counter(g: Graphics, client: ClientApi, plugin: XpDrops, anchorX: Int, top: Int): Int {
		if (plugin.counter == XpDrops.COUNTER_NONE) {
			return top
		}
		val recent: Boolean = plugin.counter == XpDrops.COUNTER_RECENT
		if (recent && plugin.lastSkill < 0) {
			return top
		}
		var xp: Long = 0L
		if (recent) {
			xp = client.skillXp(plugin.lastSkill).toLong()
		} else {
			for (skill in 0 until Skill.COUNT) {
				xp += client.skillXp(skill)
			}
		}
		val height: Int = maxOf(g.fontHeight(), if (recent) iconHeight(g, ICONS[plugin.lastSkill]) else 0) + 6
		val x: Int = boxX(anchorX, COUNTER_WIDTH)
		g.fillRect(x, top, COUNTER_WIDTH, height, BACKGROUND)
		g.drawRect(x, top, COUNTER_WIDTH, height, BORDER)
		if (recent) {
			val icon: Int = ICONS[plugin.lastSkill]
			g.sprite(icon, x + 3, top + (height - iconHeight(g, icon)) / 2)
		} else {
			g.text(x + 5, top + height / 2 + g.fontHeight() / 2 - 2, "XP:", WHITE, BLACK)
		}
		g.textRight(x + COUNTER_WIDTH - 5, top + height / 2 + g.fontHeight() / 2 - 2, GroundItemRules.withCommas(xp), WHITE, BLACK)
		return top + height + 2
	}

	/** Old School's progress bar under the counter: the chosen skill (or the most recent) from its level to the next. */
	private fun progressBar(g: Graphics, client: ClientApi, plugin: XpDrops, anchorX: Int, top: Int): Int {
		if (plugin.progressBar == 0) {
			return top
		}
		val skill: Int = if (plugin.progressBar == 1) plugin.lastSkill else plugin.progressBar - 2
		if (skill < 0) {
			return top
		}
		val xp: Int = client.skillXp(skill)
		val level: Int = XpDrops.levelForXp(xp)
		val start: Int = XpDrops.xpForLevel(level)
		val end: Int = if (level >= 126) 200_000_000 else XpDrops.xpForLevel(level + 1)
		val fraction: Double = if (end <= start) 1.0 else (xp - start).toDouble() / (end - start)
		val x: Int = boxX(anchorX, COUNTER_WIDTH)
		g.fillRect(x, top, COUNTER_WIDTH, BAR_HEIGHT, BAR_BACKGROUND)
		g.fillRect(x, top, (COUNTER_WIDTH * fraction).toInt().coerceIn(0, COUNTER_WIDTH), BAR_HEIGHT, BAR_FILL)
		g.drawRect(x, top, COUNTER_WIDTH, BAR_HEIGHT, BORDER)
		return top + BAR_HEIGHT + 2
	}

	/** The drops rising from below [top] toward it, fading over the last quarter of their lives. */
	private fun drops(g: Graphics, client: ClientApi, plugin: XpDrops, anchorX: Int, top: Int, lineHeight: Int) {
		val now: Int = client.clientCycle()
		val life: Int = XpDrops.LIFETIMES[plugin.duration]
		val rise: Int = XpDrops.RISES[plugin.speed]
		val count: Int = plugin.drops.size
		for (i in 0 until count) {
			val drop: XpDrops.Drop = plugin.drops[i]
			val age: Int = now - drop.cycle
			if (age < 0 || age >= life) {
				continue
			}
			// newer drops below older ones, each line lower by its rank from the newest
			val y: Int = top + rise + (count - 1 - i) * (lineHeight + 2) - age * rise / life
			val fade: Int = if (age * 4 < life * 3) 255 else 255 * (life - age) * 4 / life
			drawDrop(g, plugin, drop, anchorX, y, lineHeight, fade.coerceIn(0, 255))
			plugin.drawnLastFrame++
		}
	}

	private fun iconWidth(g: Graphics, id: Int): Int = g.spriteWidth(id).takeIf { it > 0 } ?: ICON_FALLBACK

	private fun iconHeight(g: Graphics, id: Int): Int = g.spriteHeight(id).takeIf { it > 0 } ?: ICON_FALLBACK

	private fun drawDrop(g: Graphics, plugin: XpDrops, drop: XpDrops.Drop, anchorX: Int, top: Int, lineHeight: Int, alpha: Int) {
		val text: String = "+" + GroundItemRules.withCommas(drop.amount.toLong())
		var icons: Int = 0
		if (!plugin.hideSkillIcons) {
			for (skill in drop.skills) {
				icons += iconWidth(g, ICONS[skill]) + 2
			}
		}
		val width: Int = icons + g.textWidth(text)
		var x: Int = boxX(anchorX, width)
		if (!plugin.hideSkillIcons) {
			for (skill in drop.skills) {
				val icon: Int = ICONS[skill]
				g.sprite(icon, x, top + (lineHeight - iconHeight(g, icon)) / 2)
				x += iconWidth(g, icon) + 2
			}
		}
		val baseline: Int = top + lineHeight / 2 + g.fontHeight() / 2 - 2
		val colour: Int = (alpha shl 24) or (drop.colour and 0xFFFFFF)
		g.text(x, baseline, text, colour, (alpha shl 24) or (BLACK and 0xFFFFFF))
	}
}
