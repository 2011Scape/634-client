package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.graphics.Toolkit
import com.jagex.game.runetek5.graphics.font.AbstractFontRenderer
import com.jagex.game.runetek5.plugin.api.Graphics
import com.jagex.game.runetek5.rendering.Renderers

/**
 * Not part of the original client: the plugin API's 2D drawing on the active toolkit and the
 * client's small font, as the client's own overlays draw (docs/FEATURES.md).
 */
object ToolkitGraphics : Graphics {

	override fun line(x1: Int, y1: Int, x2: Int, y2: Int, argb: Int) {
		val toolkit: Toolkit = ClientState.activeToolkit ?: return
		toolkit.line(x1, y1, x2, y2, argb, 1)
	}

	override fun fillRect(x: Int, y: Int, width: Int, height: Int, argb: Int) {
		val toolkit: Toolkit = ClientState.activeToolkit ?: return
		toolkit.aa(x, y, width, height, argb, 1)
	}

	override fun drawRect(x: Int, y: Int, width: Int, height: Int, argb: Int) {
		val toolkit: Toolkit = ClientState.activeToolkit ?: return
		toolkit.outlineRect(x, y, width, height, argb, 1)
	}

	override fun text(x: Int, y: Int, text: String, argb: Int, shadowArgb: Int) {
		val font: AbstractFontRenderer = Renderers.FONT_RENDERER ?: return
		font.drawString(text, argb, y, x, shadowArgb)
	}

	override fun textCentered(x: Int, y: Int, text: String, argb: Int, shadowArgb: Int) {
		val font: AbstractFontRenderer = Renderers.FONT_RENDERER ?: return
		font.renderCentered(x, argb, text, shadowArgb, y)
	}

	override fun textRight(x: Int, y: Int, text: String, argb: Int, shadowArgb: Int) {
		val font: AbstractFontRenderer = Renderers.FONT_RENDERER ?: return
		font.renderRightAligned(text, y, argb, x, shadowArgb)
	}

	override fun textWidth(text: String): Int {
		val font: AbstractFontRenderer = Renderers.FONT_RENDERER ?: return 0
		return font.textWidth(text)
	}

	override fun fontReady(): Boolean {
		return Renderers.FONT_RENDERER != null
	}
}
