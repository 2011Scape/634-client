package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.Actor
import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.Font
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.Graphics
import com.jagex.game.runetek5.plugin.api.Player
import com.jagex.game.runetek5.plugin.api.Skill
import com.jagex.game.runetek5.plugin.api.Viewport
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: Opponent Information's player comparison, RuneLite's
 * PlayerComparisonOverlay: with "Lookup players on interaction" on and a player your opponent, once their
 * hiscores are in, their combat levels against yours (green where yours is higher, red where lower) in a
 * panel at the bottom left of the game view, above the chat box.
 */
object PlayerComparisonOverlay {

	/** RuneLite's combat skills, in its order: Attack, Strength, Defence, Hitpoints (634's Constitution), Ranged, Magic, Prayer. */
	@JvmField
	val SKILLS: IntArray = intArrayOf(Skill.ATTACK, Skill.STRENGTH, Skill.DEFENCE, Skill.CONSTITUTION, Skill.RANGED, Skill.MAGIC, Skill.PRAYER)

	/** RuneLite's ComponentConstants: the panel's width and border. */
	private const val WIDTH: Int = 129
	private const val BORDER: Int = 4

	/** The chat box over the game view's bottom left in the resizable modes, and the gap kept. */
	private const val CHAT_HEIGHT: Int = 180
	private const val MARGIN: Int = 4

	private const val BACKGROUND: Int = 0x9C463D32.toInt()

	/** RuneLite's HIGHLIGHT_COLOR (255, 200, 0), Color.GREEN and Color.RED. */
	private const val HIGHLIGHT: Int = 0xFFFFC800.toInt()
	const val HIGHER: Int = 0xFF00FF00.toInt()
	const val LOWER: Int = 0xFFFF0000.toInt()
	const val EQUAL: Int = -0x1

	private const val BLACK: Int = -0x1000000

	/** Rows drawn last frame (0 when none), for the smoke test. */
	@JvmField
	var drawnRows: Int = 0

	/** RuneLite's colour for your level against theirs. */
	@JvmStatic
	fun colour(yours: Int, theirs: Int): Int = if (yours > theirs) HIGHER else if (yours < theirs) LOWER else EQUAL

	@JvmStatic
	fun draw(g: Graphics, view: Viewport) {
		drawnRows = 0
		val plugin: OpponentInfo = OpponentInfo
		val client: ClientApi = plugin.context()?.client() ?: return
		if (!plugin.enabled || !plugin.lookupOnInteraction || !g.fontReady() || client.gameState() !== GameState.LOGGED_IN) {
			return
		}
		val opponent: Actor? = plugin.lastOpponent
		if (opponent !is Player) {
			return
		}
		val name: String = OpponentInfo.nameOf(opponent) ?: return
		val result: HiscoreLookup.Result = HiscoreLookup.lookup(name) ?: return
		g.font(Font.REGULAR)
		val line: Int = g.fontHeight() + 2
		var rows: Int = 2
		for (skill in SKILLS) {
			if (result.levels[skill] >= 0) {
				rows++
			}
		}
		val height: Int = BORDER + rows * line + BORDER
		val resizable: Boolean = view.x == 0 && view.y == 0
		val x: Int = view.x + MARGIN
		val top: Int = view.y + view.height - (if (resizable) CHAT_HEIGHT else MARGIN) - height
		g.fillRect(x, top, WIDTH, height, BACKGROUND)
		val left: Int = x + BORDER
		val right: Int = x + WIDTH - BORDER
		var baseline: Int = top + BORDER + line - 3
		g.textCentered(x + WIDTH / 2, baseline, result.player, HIGHLIGHT, BLACK)
		baseline += line
		g.text(left, baseline, "Skill", HIGHLIGHT, BLACK)
		g.textRight(right, baseline, "You/Them", HIGHLIGHT, BLACK)
		for (skill in SKILLS) {
			val theirs: Int = result.levels[skill]
			if (theirs < 0) {
				continue
			}
			baseline += line
			val yours: Int = client.skillBaseLevel(skill)
			g.text(left, baseline, Skill.NAMES[skill], EQUAL, BLACK)
			g.textRight(right, baseline, yours.toString() + "/" + theirs, colour(yours, theirs), BLACK)
		}
		drawnRows = rows
	}
}
