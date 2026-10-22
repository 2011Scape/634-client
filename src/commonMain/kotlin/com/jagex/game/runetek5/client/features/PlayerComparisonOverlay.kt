package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.Actor
import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.Graphics
import com.jagex.game.runetek5.plugin.api.OverlayPosition
import com.jagex.game.runetek5.plugin.api.PanelComponent
import com.jagex.game.runetek5.plugin.api.PanelOverlay
import com.jagex.game.runetek5.plugin.api.Player
import com.jagex.game.runetek5.plugin.api.Size
import com.jagex.game.runetek5.plugin.api.Skill
import com.jagex.game.runetek5.plugin.api.Task
import com.jagex.game.runetek5.plugin.api.Viewport
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: Opponent Information's player comparison, RuneLite's
 * PlayerComparisonOverlay: with "Lookup players on interaction" on and a player your opponent, once their
 * hiscores are in, their combat levels against yours (green where yours is higher, red where lower) in a
 * panel at the bottom left of the game view, above the chat box (or where the player moved it).
 */
object PlayerComparisonOverlay {

	/** RuneLite's combat skills, in its order: Attack, Strength, Defence, Hitpoints (634's Constitution), Ranged, Magic, Prayer. */
	@JvmField
	val SKILLS: IntArray = intArrayOf(Skill.ATTACK, Skill.STRENGTH, Skill.DEFENCE, Skill.CONSTITUTION, Skill.RANGED, Skill.MAGIC, Skill.PRAYER)

	/** RuneLite's ComponentConstants: the panel's width and border. */
	private const val WIDTH: Int = 129
	private const val BORDER: Int = 4

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

	/** The panel: RuneLite's OverlayPanel at the bottom left, above the chat box, resizable, with a Configure entry. */
	@JvmField
	val panel: PanelOverlay = object : PanelOverlay("PlayerComparisonOverlay") {
		init {
			position = OverlayPosition.BOTTOM_LEFT
			resizable = true
			addMenuEntry("Configure", "Opponent info overlay", Task { PluginManagerScreen.openSettings(OpponentInfo.DESCRIPTOR.id) })
		}

		override fun render(g: Graphics, view: Viewport): Size? = draw(g, this)
	}

	private val size: Size = Size()

	/** The panel at (0, 0); its size, or null when it is not shown. */
	@JvmStatic
	fun draw(g: Graphics, overlay: PanelOverlay): Size? {
		drawnRows = 0
		val plugin: OpponentInfo = OpponentInfo
		val client: ClientApi = plugin.context()?.client() ?: return null
		if (!plugin.enabled || !plugin.lookupOnInteraction || !g.fontReady() || client.gameState() !== GameState.LOGGED_IN) {
			return null
		}
		val opponent: Actor? = plugin.lastOpponent
		if (opponent !is Player) {
			return null
		}
		val name: String = OpponentInfo.nameOf(opponent) ?: return null
		val result: HiscoreLookup.Result = HiscoreLookup.lookup(name) ?: return null
		val width: Int = if (overlay.preferredWidth > 0) overlay.preferredWidth else WIDTH
		val line: Int = g.fontHeight() + 2
		var rows: Int = 2
		for (skill in SKILLS) {
			if (result.levels[skill] >= 0) {
				rows++
			}
		}
		val height: Int = BORDER + rows * line + BORDER
		val x: Int = 0
		val top: Int = 0
		PanelComponent.background(g, x, top, width, height, overlay.backgroundColour)
		val left: Int = x + BORDER
		val right: Int = x + width - BORDER
		var baseline: Int = top + BORDER + line - 3
		g.textCentered(x + width / 2, baseline, result.player, HIGHLIGHT, BLACK)
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
		return size.set(width, height)
	}
}
