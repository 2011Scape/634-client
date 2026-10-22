package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.Graphics
import com.jagex.game.runetek5.plugin.api.OverlayPanel
import com.jagex.game.runetek5.plugin.api.PanelChild
import com.jagex.game.runetek5.plugin.api.Size
import com.jagex.game.runetek5.plugin.api.Task
import com.jagex.game.runetek5.plugin.api.Viewport
import com.jagex.game.runetek5.plugin.api.Widget
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: Barrows Brothers' drawing. RuneLite's BarrowsBrotherSlainOverlay, the
 * panel at the top left while the game's crypt overlay is open (each brother with a tick or a cross, and
 * the reward potential), and its BarrowsOverlay, the brothers' letters on the minimap and the puzzle's
 * answer outlined, both drawn at the end of the frame, over the interfaces.
 */
object BarrowsOverlay {

	private const val WHITE: Int = -0x1
	private const val BLACK: Int = -0x1000000

	/** Java's Color.GREEN, Color.RED and Color.YELLOW, RuneLite's. */
	private const val GREEN: Int = 0xFF00FF00.toInt()
	private const val RED: Int = 0xFFFF0000.toInt()
	private const val YELLOW: Int = 0xFFFFFF00.toInt()

	/** The reward potential's thresholds: the blood runes' roll (to the bolt racks'), and the death runes'. */
	private const val BLOOD_RUNES: Int = 756
	private const val BOLT_RACKS: Int = 881
	private const val DEATH_RUNES: Int = 631

	/** The brothers' lines drawn last frame, the letters on the minimap and whether the answer was outlined, for the smoke test. */
	@JvmField var drawnLines: Int = 0
	@JvmField var drawnLetters: Int = 0
	@JvmField var drawnAnswer: Widget? = null
	@JvmField var drawnPotential: String? = null

	private val area: IntArray = IntArray(4)
	private val point: IntArray = IntArray(2)

	/** RuneLite's BarrowsBrotherSlainOverlay: an OverlayPanel at the top left, after the others there. */
	@JvmField
	val panel: OverlayPanel = object : OverlayPanel("BarrowsBrotherSlainOverlay") {
		init {
			priority = PRIORITY_LOW
			addMenuEntry("Configure", "Barrows overlay", Task { PluginManagerScreen.openSettings(BarrowsBrothers.DESCRIPTOR.id) })
		}

		override fun render(g: Graphics, view: Viewport): Size? {
			drawnLines = 0
			drawnPotential = null
			val client: ClientApi = BarrowsBrothers.context()?.client() ?: return null
			// only while the game's own list would show: the crypts' overlay is open
			if (!BarrowsBrothers.enabled || !BarrowsBrothers.overlayOpen || client.gameState() !== GameState.LOGGED_IN) {
				return null
			}
			for (brother in BarrowsBrothers.BROTHERS) {
				panelComponent.children.add(SlainLine(brother.name, BarrowsBrothers.isSlain(client, brother)))
				drawnLines++
			}
			val potential: Int = BarrowsBrothers.rewardPotential()
			val text: String = percent(potential)
			drawnPotential = text
			panelComponent.addLine("Potential", WHITE, text, potentialColour(potential))
			return super.render(g, view)
		}
	}

	/** RuneLite's colours: green from the blood runes to the bolt racks, white under the death runes, else yellow. */
	@JvmStatic
	fun potentialColour(potential: Int): Int {
		if (potential >= BLOOD_RUNES && potential < BOLT_RACKS) {
			return GREEN
		}
		return if (potential < DEATH_RUNES) WHITE else YELLOW
	}

	/** RuneLite's REWARD_POTENTIAL_FORMATTER, "##0.00%": [potential] out of the most, to two places. */
	@JvmStatic
	fun percent(potential: Int): String {
		val hundredths: Int = (potential * 10000 + BarrowsBrothers.MAX_POTENTIAL / 2) / BarrowsBrothers.MAX_POTENTIAL
		val part: Int = hundredths % 100
		return (hundredths / 100).toString() + "." + (if (part < 10) "0" else "") + part + "%"
	}

	/**
	 * A brother's line: his name, and RuneLite's ✓ in green or ✗ in red at the far side. The game's fonts have
	 * neither, so they are drawn as lines, a glyph's size.
	 */
	private class SlainLine(private val name: String, private val slain: Boolean) : PanelChild {
		override fun render(g: Graphics, x: Int, y: Int, preferredWidth: Int, preferredHeight: Int, out: Size) {
			val height: Int = g.fontHeight()
			g.text(x, y + height, name, WHITE, BLACK)
			val size: Int = maxOf(6, height - 6)
			val left: Int = x + preferredWidth - size - 1
			val top: Int = y + (height - size) / 2 + 1
			if (slain) {
				mark(g, left, top + size / 2, left + size / 3, top + size, left + size, top, GREEN)
			} else {
				cross(g, left, top, size, RED)
			}
			out.set(preferredWidth, height)
		}

		private fun mark(g: Graphics, x1: Int, y1: Int, x2: Int, y2: Int, x3: Int, y3: Int, colour: Int) {
			g.thickLine(x1 + 1, y1 + 1, x2 + 1, y2 + 1, 2, BLACK)
			g.thickLine(x2 + 1, y2 + 1, x3 + 1, y3 + 1, 2, BLACK)
			g.thickLine(x1, y1, x2, y2, 2, colour)
			g.thickLine(x2, y2, x3, y3, 2, colour)
		}

		private fun cross(g: Graphics, x: Int, y: Int, size: Int, colour: Int) {
			g.thickLine(x + 1, y + 1, x + size + 1, y + size + 1, 2, BLACK)
			g.thickLine(x + size + 1, y + 1, x + 1, y + size + 1, 2, BLACK)
			g.thickLine(x, y, x + size, y + size, 2, colour)
			g.thickLine(x + size, y, x, y + size, 2, colour)
		}
	}

	/** At the end of the frame: the brothers' letters on the minimap, and the puzzle's answer outlined. */
	@JvmStatic
	fun drawFrame(g: Graphics, client: ClientApi) {
		drawnLetters = 0
		drawnAnswer = null
		if (!BarrowsBrothers.enabled || client.gameState() !== GameState.LOGGED_IN) {
			return
		}
		if (BarrowsBrothers.showBrotherLoc && g.fontReady() && BarrowsBrothers.isBarrowsLoaded(client) && client.minimapArea(area)) {
			drawBrothers(g, client)
		}
		val answer: Widget? = if (BarrowsBrothers.showPuzzleAnswer) BarrowsBrothers.puzzleAnswer(client) else null
		if (answer != null && !answer.isHidden()) {
			g.drawRect(answer.screenX(), answer.screenY(), answer.width(), answer.height(), GREEN)
			drawnAnswer = answer
		}
	}

	/** RuneLite's renderBarrowsBrothers: each brother's first letter at his mound, red once he is slain. */
	private fun drawBrothers(g: Graphics, client: ClientApi) {
		val baseX: Int = client.sceneBaseX()
		val baseY: Int = client.sceneBaseY()
		val size: Int = client.sceneSize()
		g.clip(area[0], area[1], area[2], area[3])
		for (brother in BarrowsBrothers.BROTHERS) {
			val sceneX: Int = brother.x - baseX
			val sceneY: Int = brother.y - baseY
			if (sceneX < 0 || sceneY < 0 || sceneX >= size || sceneY >= size || !client.projectMinimap(sceneX, sceneY, point)) {
				continue
			}
			val colour: Int = if (BarrowsBrothers.isSlain(client, brother)) BarrowsBrothers.deadBrotherLocColour else BarrowsBrothers.brotherLocColour
			// Perspective.getCanvasTextMiniMapLocation: the text's middle on the point, a black copy a pixel down and right
			g.textCentered(point[0], point[1] + g.fontHeight() / 2, brother.name.substring(0, 1), colour or BLACK, BLACK)
			drawnLetters++
		}
		g.unclip()
	}
}
