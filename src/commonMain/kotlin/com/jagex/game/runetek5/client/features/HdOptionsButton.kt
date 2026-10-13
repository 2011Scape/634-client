package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.client.input.console.Console
import com.jagex.game.runetek5.graphics.ToolkitType
import com.jagex.game.runetek5.graphics.font.FontManager
import com.jagex.game.runetek5.preference.GamePreferences
import hdAvailable
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic
import ToolkitSwitch

/**
 * Not part of the original client: 634HD in the Graphics Options screen. The screen (interface 742)
 * is built by cache scripts that only know toolkits 0 to 5, and the cache is not edited, so the
 * client draws a toggle of its own in the screen's title bar, "634HD (requires OpenGL 3.3)", ticked
 * while toolkit 6 draws. A left click on it switches to 634HD, or back to the toolkit before it
 * (OpenGL if none), and saves the choice as ::tk6 does. A failed switch falls back and says why
 * (HdFallback). Desktop only (hdAvailable).
 */
object HdOptionsButton {

	/** The title of the Graphics Options screen (742:15), beside which the toggle sits. */
	private const val TITLE: Int = (742 shl 16) or 15

	private const val LABEL: String = "634HD (requires OpenGL 3.3)"

	private const val WIDTH: Int = 172

	private const val HEIGHT: Int = 16

	/** Where the toggle was last drawn, in screen pixels, and the client cycle it was drawn in. */
	@JvmField
	var x: Int = 0

	@JvmField
	var y: Int = 0

	private var drawnCycle: Int = -100

	/** The toolkit to go back to when 634HD is turned off. */
	private var previous: Int = ToolkitType.GL

	/**
	 * Called by InterfaceComponentDraw for every component it draws, at its screen position: draws the
	 * toggle at the left end of the Graphics Options title bar.
	 */
	@JvmStatic
	fun componentDrawn(idHash: Int, startX: Int, startY: Int, height: Int) {
		if (idHash != TITLE || !hdAvailable) {
			return
		}
		x = startX + 6
		y = startY + (height - HEIGHT) / 2
		drawnCycle = ClientState.clientCycle
		val on: Boolean = GamePreferences.uid!!.toolkit!!.getValue() == ToolkitType.HD
		val toolkit = ClientState.activeToolkit!!
		toolkit.aa(x, y, HEIGHT, HEIGHT, -16777216, 1)
		toolkit.outlineRect(x, y, HEIGHT, HEIGHT, -7566196, 1)
		if (on) {
			toolkit.aa(x + 4, y + 4, HEIGHT - 8, HEIGHT - 8, -16711936, 1)
		}
		FontManager.aClass68_4!!.renderCentered(x + HEIGHT + 4 + (WIDTH - HEIGHT - 4) / 2, if (on) -16711936 else -256, LABEL, -16777216, y + 12)
	}

	/** Called on a left press before the mini-menu acts; true when it hit the toggle and was handled. */
	@JvmStatic
	fun pressed(mouseX: Int, mouseY: Int): Boolean {
		if (!hdAvailable || ClientState.clientCycle - drawnCycle > 2) {
			return false
		}
		if (mouseX < x || mouseY < y || mouseX >= x + WIDTH || mouseY >= y + HEIGHT) {
			return false
		}
		toggle()
		return true
	}

	@JvmStatic
	fun toggle() {
		val current: Int = GamePreferences.uid!!.toolkit!!.getValue()
		val target: Int
		if (current == ToolkitType.HD) {
			target = previous
		} else {
			previous = current
			target = ToolkitType.HD
		}
		ToolkitSwitch.loadAssets(false, target)
		val now: Int = GamePreferences.uid!!.toolkit!!.getValue()
		if (now == target) {
			GamePreferences.uid!!.setValue(GamePreferences.uid!!.toolkitDefault, target)
			GamePreferences.savePreferences()
			GamePreferences.preferencesSavedOnServer = false
			Console.printToConsole(if (target == ToolkitType.HD) "634HD on" else "634HD off")
		}
	}
}
