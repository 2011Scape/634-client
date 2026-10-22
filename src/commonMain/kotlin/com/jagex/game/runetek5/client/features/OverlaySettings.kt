package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.OverlayLayout
import com.jagex.game.runetek5.plugin.ToolkitGraphics
import com.jagex.game.runetek5.plugin.api.Font
import com.jagex.game.runetek5.plugin.api.KeyCode
import com.jagex.game.runetek5.plugin.api.PanelOverlay
import com.jagex.game.runetek5.plugin.api.Plugin
import com.jagex.game.runetek5.plugin.api.PluginApi
import com.jagex.game.runetek5.plugin.api.PluginConfig
import com.jagex.game.runetek5.plugin.api.PluginContext
import com.jagex.game.runetek5.plugin.api.PluginDescriptor
import com.jagex.game.runetek5.plugin.api.SettingsSchema
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: RuneLite's overlay settings (its RuneLiteConfig's Overlay section and
 * drag hotkey) as a built-in without a switch: the fonts of the overlays, panels, infoboxes and
 * tooltips, where tooltips go, the infoboxes' size and text outline, the panels' background, and the key
 * held to move the panels (OverlayLayout).
 */
object OverlaySettings : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("overlays", "Overlays", "1", PluginApi.VERSION, "Sets the fonts, colour and infobox size of plugin overlays, and the key that moves them: hold Alt to drag a panel, Alt and right-click to put it back.", "Pixel")

	/** RuneLite's FontType, in the client's three fonts. */
	@JvmField
	val FONTS: Array<String> = arrayOf("Regular", "Bold", "Small")

	private val FONT_IDS: IntArray = intArrayOf(Font.REGULAR, Font.BOLD, Font.SMALL)

	/** RuneLite's TooltipPositionType. */
	@JvmField
	val TOOLTIP_POSITIONS: Array<String> = arrayOf("Above cursor", "Under cursor")

	private var context: PluginContext? = null

	private val settingListener: EventListener<SettingChanged> = EventListener { event ->
		if (event.pluginId == DESCRIPTOR.id) {
			context?.config()?.let { apply(it) }
		}
	}

	override fun start(context: PluginContext) {
		this.context = context
		val s: SettingsSchema = context.settings()
		// RuneLite's RuneLiteConfig, in its order
		s.choice("fontType", "Dynamic overlay font", FONTS[2], FONTS)
		s.choice("tooltipFontType", "Tooltip font", FONTS[2], FONTS)
		s.choice("interfaceFontType", "Interface font", FONTS[0], FONTS)
		s.choice("infoboxFontType", "Infobox font", FONTS[0], FONTS)
		s.choice("tooltipPosition", "Tooltip position", TOOLTIP_POSITIONS[1], TOOLTIP_POSITIONS)
		s.number("infoBoxSize", "Infobox size", 35, 2, 100, 1)
		s.toggle("infoBoxTextOutline", "Outline infobox text", false)
		s.colour("overlayBackgroundColor", "Overlay color", PanelOverlay.STANDARD_BACKGROUND, true)
		s.keybind("dragHotkey", "Drag hotkey", KeyCode.ALT)
		apply(context.config())
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
	}

	override fun stop() {
		context = null
	}

	@JvmStatic
	fun apply(config: PluginConfig) {
		ToolkitGraphics.defaultFont = font(config, "fontType", 2)
		OverlayLayout.tooltipFont = font(config, "tooltipFontType", 2)
		OverlayLayout.interfaceFont = font(config, "interfaceFontType", 0)
		OverlayLayout.infoboxFont = font(config, "infoboxFontType", 0)
		OverlayLayout.tooltipAbove = config.get("tooltipPosition", TOOLTIP_POSITIONS[1]) == TOOLTIP_POSITIONS[0]
		OverlayLayout.infoBoxSize = config.getInt("infoBoxSize", 35).coerceIn(2, 100)
		OverlayLayout.infoBoxTextOutline = config.getBoolean("infoBoxTextOutline", false)
		OverlayLayout.background = config.getColour("overlayBackgroundColor", PanelOverlay.STANDARD_BACKGROUND)
		OverlayLayout.dragKey = config.getInt("dragHotkey", KeyCode.ALT)
	}

	private fun font(config: PluginConfig, key: String, default: Int): Int {
		val i: Int = FONTS.indexOf(config.get(key, FONTS[default]))
		return FONT_IDS[if (i < 0) default else i]
	}
}
