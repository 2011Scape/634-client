package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.client.GameShell
import com.jagex.game.runetek5.graphics.Toolkit
import com.jagex.game.runetek5.graphics.font.AbstractFontRenderer
import com.jagex.game.runetek5.graphics.ToolkitType
import com.jagex.game.runetek5.graphics.font.FontManager
import com.jagex.game.runetek5.preference.GamePreferences
import hdAvailable
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: 634HD's settings panel, a window the client draws over the
 * interfaces, opened from the "Settings" button beside the 634HD toggle in Graphics Options
 * (HdOptionsButton) or with ::hdsettings. A row of presets (Off, Low, Medium, High, Ultra) and a row
 * per switch of HdSettings; a click on a row moves it to its next value. Every change applies at
 * once and is saved (HdSettings.save), so the next start begins with it. Desktop only.
 */
object HdSettingsPanel {

	@JvmField
	var open: Boolean = false

	/** Set when the draw distance changed: the root-package caller builds the map again (MapRebuild). */
	@JvmField
	var mapRebuildWanted: Boolean = false

	private const val WIDTH: Int = 300

	private const val ROW: Int = 15

	private const val PRESET_HEIGHT: Int = 18

	/** The rows: switch, label, and the values a click steps through. */
	private val ROWS: Array<String> = arrayOf(
		"tonemap", "shadows", "terrainShadows", "cutoutShadows", "lights", "pixelLighting", "ambientOcclusion", "materials", "worldTextures", "environmentLighting", "sky", "dayNight", "fog",
		"fade", "water", "caustics", "terrainBlend", "foliage", "grassDisplacement", "particles", "softParticles", "bloom", "fxaa",
		"colourGrading", "mipmaps", "anisotropy", "renderDistance", "scale",
	)

	private val LABELS: Array<String> = arrayOf(
		"HDR tonemapping", "Shadows", "Terrain shadows", "Cut-out shadows", "Point lights", "Per-pixel lighting", "Ambient occlusion",
		"Material maps", "World textures", "Area lighting", "Sky", "Day and night", "Fog", "Fade-in at distance", "Water", "Caustics", "Ground blending", "Foliage sway", "Grass bends",
		"Embers and sparks", "Soft particles", "Bloom", "FXAA", "Colour grading", "Mipmaps",
		"Anisotropic filtering", "Draw distance", "Interface scale (next start)",
	)

	private val CHOICES: Array<Array<String>> = arrayOf(
		arrayOf("off", "aces", "agx"), arrayOf("0", "1024", "2048", "4096"), ONOFF, ONOFF, ONOFF, ONOFF, ONOFF, ONOFF, ONOFF, ONOFF, ONOFF, arrayOf("off", "realtime", "10", "30", "60"), ONOFF, ONOFF, arrayOf("0", "1"), ONOFF, ONOFF, ONOFF, ONOFF, ONOFF, ONOFF, ONOFF, ONOFF, ONOFF, ONOFF, arrayOf("0", "2", "4", "8", "16"), arrayOf("1", "1.5", "2"), arrayOf("auto", "1", "1.5", "2", "2.5", "3"),
	)

	private val ONOFF: Array<String>
		get() = arrayOf("off", "on")

	// where the panel was last drawn
	private var left: Int = 0

	private var top: Int = 0

	private var height: Int = 0

	@JvmStatic
	fun toggleOpen() {
		open = hdAvailable && !open
	}

	/**
	 * Called at the end of the frame, over the interfaces (beside the console's call in client): the
	 * panel, if open. [mouseX] and [mouseY] light the row under the mouse.
	 */
	@JvmStatic
	fun draw(toolkit: Toolkit?, mouseX: Int, mouseY: Int) {
		if (!open || toolkit == null) {
			return
		}
		val font: AbstractFontRenderer = FontManager.aClass68_4 ?: return
		height = 44 + PRESET_HEIGHT + ROWS.size * ROW + 22
		left = (GameShell.screenCanvasWidth - WIDTH) / 2
		top = maxOf(4, (GameShell.screenCanvasHeight - height) / 2)
		toolkit.KA(0, 0, GameShell.screenCanvasWidth, GameShell.screenCanvasHeight)
		toolkit.aa(left, top, WIDTH, height, -0x1f000000 or 0x2b241c, 1)
		toolkit.outlineRect(left, top, WIDTH, height, -0x8a7b62, 1)
		font.renderCentered(left + WIDTH / 2, -0x3300, "634HD settings", -0x1000000, top + 16)
		font.renderCentered(left + WIDTH / 2, -0x5f5f60, if (GamePreferences.uid!!.toolkit!!.getValue() == ToolkitType.HD) "Changes apply at once and are saved" else "Applies while 634HD is on", -0x1000000, top + 30)
		// presets
		val current: String? = HdSettings.currentPreset()
		val presetWidth: Int = (WIDTH - 12) / HdSettings.PRESETS.size
		val presetY: Int = top + 38
		for (i in HdSettings.PRESETS.indices) {
			val x: Int = left + 6 + i * presetWidth
			val chosen: Boolean = HdSettings.PRESETS[i] == current
			val hover: Boolean = inside(mouseX, mouseY, x, presetY, presetWidth - 4, PRESET_HEIGHT)
			toolkit.aa(x, presetY, presetWidth - 4, PRESET_HEIGHT, if (chosen) -0xff9a00 else if (hover) -0xc5cfd8 else -0xd8dce2, 1)
			toolkit.outlineRect(x, presetY, presetWidth - 4, PRESET_HEIGHT, -0x8a7b62, 1)
			font.renderCentered(x + (presetWidth - 4) / 2, if (chosen) -1 else -0x3300, capital(HdSettings.PRESETS[i]), -0x1000000, presetY + 13)
		}
		// one row per switch
		var y: Int = presetY + PRESET_HEIGHT + 6
		for (i in ROWS.indices) {
			if (inside(mouseX, mouseY, left + 4, y, WIDTH - 8, ROW)) {
				toolkit.aa(left + 4, y, WIDTH - 8, ROW, -0xc5cfd8, 1)
			}
			val value: String = current(ROWS[i])
			val on: Boolean = value != "off" && value != "0"
			font.drawString(LABELS[i], -0x3300, y + 11, left + 10, -0x1000000)
			font.renderRightAligned(shown(ROWS[i], value), y + 11, if (on) -0xff0100 else -0x7f7f80, left + WIDTH - 10, -0x1000000)
			y += ROW
		}
		// close
		val closeY: Int = top + height - 20
		val hover: Boolean = inside(mouseX, mouseY, left + WIDTH / 2 - 40, closeY, 80, 16)
		toolkit.aa(left + WIDTH / 2 - 40, closeY, 80, 16, if (hover) -0xc5cfd8 else -0xd8dce2, 1)
		toolkit.outlineRect(left + WIDTH / 2 - 40, closeY, 80, 16, -0x8a7b62, 1)
		font.renderCentered(left + WIDTH / 2, -0x3300, "Close", -0x1000000, closeY + 12)
	}

	/** A left press: true when it was on the panel (and is not passed on to the game). */
	@JvmStatic
	fun pressed(mouseX: Int, mouseY: Int): Boolean {
		if (!open) {
			return false
		}
		if (!inside(mouseX, mouseY, left, top, WIDTH, height)) {
			return false
		}
		val presetWidth: Int = (WIDTH - 12) / HdSettings.PRESETS.size
		val presetY: Int = top + 38
		for (i in HdSettings.PRESETS.indices) {
			if (inside(mouseX, mouseY, left + 6 + i * presetWidth, presetY, presetWidth - 4, PRESET_HEIGHT)) {
				val textures: Boolean = HdSettings.worldTextures
				HdSettings.applyPreset(HdSettings.PRESETS[i])
				// the ground takes its tile overrides when the map is built
				if (HdSettings.worldTextures != textures) {
					mapRebuildWanted = true
				}
				HdSettings.save()
				return true
			}
		}
		var y: Int = presetY + PRESET_HEIGHT + 6
		for (i in ROWS.indices) {
			if (inside(mouseX, mouseY, left + 4, y, WIDTH - 8, ROW)) {
				val choices: Array<String> = CHOICES[i]
				val value: String = current(ROWS[i])
				var next: Int = 0
				for (c in choices.indices) {
					if (choices[c] == value) {
						next = (c + 1) % choices.size
					}
				}
				HdSettings.set(ROWS[i], choices[next])
				if (ROWS[i] == "worldTextures") {
					// the ground takes its tile overrides when the map is built
					mapRebuildWanted = true
				}
				if (ROWS[i] == "renderDistance") {
					// live: the scene is built again at the new distance
					ClientFeatures.RENDER_DISTANCE = HdSettings.renderDistance
					mapRebuildWanted = true
				}
				HdSettings.save()
				return true
			}
			y += ROW
		}
		if (inside(mouseX, mouseY, left + WIDTH / 2 - 40, top + height - 20, 80, 16)) {
			open = false
		}
		return true
	}

	// for the smoke test: presses at the places the panel was last drawn

	@JvmStatic
	fun clickPreset(name: String): Boolean {
		val presetWidth: Int = (WIDTH - 12) / HdSettings.PRESETS.size
		for (i in HdSettings.PRESETS.indices) {
			if (HdSettings.PRESETS[i] == name) {
				return pressed(left + 6 + i * presetWidth + 4, top + 38 + 4)
			}
		}
		return false
	}

	@JvmStatic
	fun clickRow(name: String): Boolean {
		for (i in ROWS.indices) {
			if (ROWS[i] == name) {
				return pressed(left + 20, top + 38 + PRESET_HEIGHT + 6 + i * ROW + 4)
			}
		}
		return false
	}

	@JvmStatic
	fun clickClose(): Boolean = pressed(left + WIDTH / 2, top + height - 12)

	/** A row's value now: the draw distance in use (not only the saved one), else the setting. */
	private fun current(name: String): String = if (name == "renderDistance") HdSettings.number(ClientFeatures.RENDER_DISTANCE) else HdSettings.valueOf(name)

	private fun shown(name: String, value: String): String = when (name) {
		"renderDistance" -> value + "x"
		"dayNight" -> if (value == "off") "Off" else if (value == "realtime") "Clock" else value + " min"
		"scale" -> if (value == "auto") "Auto (now " + HdSettings.number(ClientFeatures.SCALE) + "x)" else value + "x"
		"tonemap" -> if (value == "aces") "ACES" else if (value == "agx") "AgX" else "Off"
		"shadows" -> if (value == "0") "Off" else value
		"anisotropy" -> if (value == "0") "Off" else value + "x"
		"water" -> if (value == "1") "On" else "Off"
		else -> capital(value)
	}

	private fun capital(text: String): String = if (text.isEmpty()) text else text.substring(0, 1).uppercase() + text.substring(1)

	private fun inside(px: Int, py: Int, x: Int, y: Int, width: Int, height: Int): Boolean = px >= x && py >= y && px < x + width && py < y + height
}
