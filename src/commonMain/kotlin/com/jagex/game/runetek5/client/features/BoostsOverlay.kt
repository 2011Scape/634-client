package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.InfoBoxes
import com.jagex.game.runetek5.plugin.PluginImage
import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.Graphics
import com.jagex.game.runetek5.plugin.api.OverlayPanel
import com.jagex.game.runetek5.plugin.api.PanelOverlay
import com.jagex.game.runetek5.plugin.api.Size
import com.jagex.game.runetek5.plugin.api.Skill
import com.jagex.game.runetek5.plugin.api.Task
import com.jagex.game.runetek5.plugin.api.Viewport
import kotlin.jvm.JvmField

/**
 * Not part of the original client: Boosts Information's two panels at the top left of the game view,
 * RuneLite's BoostsOverlay ("Display panel": the next changes, then each skill's boosted level over its
 * level) and CompactBoostsOverlay ("Compact display": each skill's small icon and level, then the times),
 * both after the higher-priority panels there (PRIORITY_MED), movable, and with a Configure entry.
 */
object BoostsOverlay {

	/** RuneLite's CompactBoostsOverlay padding and the width its text is right-justified to. */
	private const val H_PADDING: Int = 2
	private const val V_PADDING: Int = 1
	private const val TEXT_WIDTH: Int = 22

	/** The small skill icons' size, RuneLite's skill_icons_small: 634 has only the Stats tab's, drawn smaller. */
	private const val SMALL_ICON: Int = 16

	/** What the panel and the compact list drew last frame (rows, or null when not drawn), for the smoke test. */
	@JvmField var panelRows: List<String>? = null
	@JvmField var compactRows: List<String>? = null

	private fun configure(): Task = Task { PluginManagerScreen.openSettings(Boosts.DESCRIPTOR.id) }

	@JvmField
	val panel: PanelOverlay = object : OverlayPanel("BoostsOverlay") {
		init {
			priority = PRIORITY_MED
			addMenuEntry("Configure", "Boosts overlay", configure())
		}

		override fun render(g: Graphics, view: Viewport): Size? {
			panelRows = null
			val client: ClientApi = Boosts.context()?.client() ?: return null
			if (!Boosts.active(client) || !Boosts.displayPanel || !g.fontReady()) {
				return null
			}
			val rows: ArrayList<String> = ArrayList()
			var nextChange: Int = Boosts.changeDownTicks(client)
			if (nextChange != -1) {
				val text: String = Boosts.changeDownSeconds(client).toString()
				panelComponent.addLine("Next + restore in", Boosts.WHITE, text, Boosts.WHITE)
				rows.add("Next + restore in " + text)
			}
			nextChange = Boosts.changeUpTicks(client)
			if (nextChange != -1) {
				val text: String = Boosts.changeUpSeconds(client).toString()
				panelComponent.addLine("Next - restore in", Boosts.WHITE, text, Boosts.WHITE)
				rows.add("Next - restore in " + text)
			}
			for (skill in 0 until Skill.COUNT) {
				if (!Boosts.toDisplay[skill]) {
					continue
				}
				val boosted: Int = client.skillLevel(skill)
				val base: Int = client.skillBaseLevel(skill)
				val boost: Int = boosted - base
				val colour: Int = Boosts.colour(boost)
				val text: String = if (Boosts.relativeBoost) {
					Boosts.relative(boost)
				} else {
					// RuneLite's ColorUtil.prependColorTag: the level in its colour, "/base" in white
					colourTag(colour) + boosted + colourTag(Boosts.WHITE) + "/" + base
				}
				panelComponent.addLine(Skill.name(skill), Boosts.WHITE, text, colour)
				rows.add(Skill.name(skill) + " " + if (Boosts.relativeBoost) text else boosted.toString() + "/" + base)
			}
			val size: Size? = super.render(g, view)
			if (size != null) {
				panelRows = rows
			}
			return size
		}
	}

	@JvmField
	val compact: PanelOverlay = object : PanelOverlay("CompactBoostsOverlay") {
		private val size: Size = Size()

		private var curY: Int = 0

		private var maxX: Int = 0

		init {
			priority = PRIORITY_MED
			addMenuEntry("Configure", "Boosts overlay", configure())
		}

		override fun render(g: Graphics, view: Viewport): Size? {
			compactRows = null
			val client: ClientApi = Boosts.context()?.client() ?: return null
			if (!Boosts.active(client) || !Boosts.compactDisplay || !g.fontReady()) {
				return null
			}
			curY = 0
			maxX = 0
			val rows: ArrayList<String> = ArrayList()
			val fontHeight: Int = g.fontHeight()
			for (skill in 0 until Skill.COUNT) {
				val boosted: Int = client.skillLevel(skill)
				val boost: Int = boosted - client.skillBaseLevel(skill)
				if (!Boosts.toDisplay[skill] || boost == 0) {
					continue
				}
				val text: String = if (Boosts.relativeBoost) Boosts.relative(boost) else boosted.toString()
				drawBoost(g, fontHeight, SkillSprites.ICONS[skill], null, Boosts.colour(boost), text)
				rows.add(Skill.name(skill) + " " + text)
			}
			var time: Int = Boosts.changeUpTicks(client)
			if (time != -1) {
				val text: String = Boosts.changeUpSeconds(client).toString()
				drawBoost(g, fontHeight, -1, debuffed, Boosts.timeColour(time), text)
				rows.add("- " + text)
			}
			time = Boosts.changeDownTicks(client)
			if (time != -1) {
				val text: String = Boosts.changeDownSeconds(client).toString()
				drawBoost(g, fontHeight, -1, buffed, Boosts.timeColour(time), text)
				rows.add("+ " + text)
			}
			if (rows.isEmpty()) {
				return null
			}
			compactRows = rows
			return size.set(maxX, curY)
		}

		/** RuneLite's drawBoost: the picture at the left, the text outlined and right-justified beside it. */
		private fun drawBoost(g: Graphics, fontHeight: Int, spriteId: Int, image: PluginImage?, colour: Int, text: String) {
			val imageWidth: Int = SMALL_ICON
			val imageHeight: Int = SMALL_ICON
			if (image != null) {
				g.image(image, 0, curY)
			} else {
				g.spriteScaled(spriteId, 0, curY, imageWidth, imageHeight)
			}
			val x: Int = imageWidth + H_PADDING + (TEXT_WIDTH - g.textWidth(text))
			InfoBoxes.outlined(g, x, curY + fontHeight, text, colour)
			curY += maxOf(imageHeight, fontHeight) + V_PADDING
			maxX = maxOf(maxX, imageWidth + H_PADDING + TEXT_WIDTH)
		}
	}

	/** RuneLite's DEBUFFED and BUFFED, the compact list's small pictures. */
	private val debuffed: PluginImage = BoostImages.debuffedSmall()

	private val buffed: PluginImage = BoostImages.buffedSmall()

	/** A `<col=rrggbb>` tag for [argb]. */
	private fun colourTag(argb: Int): String {
		val hex: String = (argb and 0xFFFFFF).toString(16)
		return "<col=" + "000000".substring(hex.length) + hex + ">"
	}
}
