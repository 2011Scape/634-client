package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.input.mouse.MouseRecord
import com.jagex.game.runetek5.plugin.api.PanelComponent
import com.jagex.game.runetek5.plugin.api.Size
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: RuneLite's TooltipManager and TooltipOverlay. The tooltips asked for
 * while a frame is drawn ([add], ClientApi.addTooltip) are drawn over everything at its end, one under
 * another by the mouse (under it, or above it with the Overlays settings' tooltip position), kept on the
 * screen by last frame's size, and forgotten for the next frame. A tooltip is a text or a PanelComponent
 * (RuneLite's Tooltip with a panel), drawn as the panel draws itself.
 */
object Tooltips {

	/** RuneLite's TooltipOverlay.UNDER_OFFSET and PADDING, and TooltipComponent.OFFSET. */
	private const val UNDER_OFFSET: Int = 24
	private const val PADDING: Int = 2
	private const val OFFSET: Int = 4

	private const val WHITE: Int = -0x1

	/** RuneLite's TooltipComponent.BR: `<br>` and `</br>`. */
	private val BR: Regex = Regex("</?br>")

	/** This frame's tooltips: texts and panels. */
	private val texts: ArrayList<Any> = ArrayList()

	private var lastWidth: Int = 0

	private var lastHeight: Int = 0

	/** For the smoke test: the tooltips drawn last frame. */
	@JvmStatic
	var drawnLastFrame: List<String> = emptyList()
		private set

	@JvmStatic
	fun add(text: String) {
		if (text.isNotEmpty() && texts.size < 32) {
			texts.add(text)
		}
	}

	@JvmStatic
	fun add(panel: PanelComponent) {
		if (texts.size < 32) {
			texts.add(panel)
		}
	}

	/** A panel's lines as text, label and value, a line each: what the smoke test reads. */
	private fun panelText(panel: PanelComponent): String {
		return panel.children.joinToString("</br>") { child ->
			when (child) {
				is PanelComponent.LineComponent -> (child.left ?: "") + " " + (child.right ?: "")
				is PanelComponent.TitleComponent -> child.text
				else -> ""
			}
		}.trim()
	}

	@JvmStatic
	fun draw(screenWidth: Int, screenHeight: Int) {
		if (texts.isEmpty()) {
			drawnLastFrame = emptyList()
			return
		}
		val mouseX: Int = MouseRecord.mouseRecorder?.getMouseX() ?: 0
		val mouseY: Int = MouseRecord.mouseRecorder?.getMouseY() ?: 0
		val g: ToolkitGraphics = ToolkitGraphics
		g.font(OverlayLayout.tooltipFont)
		val x: Int = minOf(screenWidth - lastWidth, mouseX)
		val y: Int = if (OverlayLayout.tooltipAbove) maxOf(0, mouseY - lastHeight) else minOf(screenHeight - lastHeight, mouseY + UNDER_OFFSET)
		var width: Int = 0
		var height: Int = 0
		val lineHeight: Int = g.fontHeight()
		for (entry in texts) {
			if (entry is PanelComponent) {
				val size: Size? = entry.render(g, x, y + height)
				if (size != null) {
					height += size.height + PADDING
					width = maxOf(width, size.width)
				}
				continue
			}
			val text: String = entry as String
			val lines: List<String> = text.split(BR)
			var textWidth: Int = 0
			for (line in lines) {
				textWidth = maxOf(textWidth, g.textWidth(line))
			}
			val boxWidth: Int = textWidth + OFFSET * 2
			val boxHeight: Int = lineHeight * lines.size + OFFSET * 2
			val top: Int = y + height
			PanelComponent.background(g, x, top, boxWidth, boxHeight, OverlayLayout.background)
			for (i in lines.indices) {
				// RuneLite's baseline: the line's bottom less the font's descent, which the client's line height leaves under it
				g.text(x + OFFSET, top + OFFSET + (i + 1) * lineHeight - 3, lines[i], WHITE, PanelComponent.SHADOW)
			}
			height += boxHeight + PADDING
			width = maxOf(width, boxWidth)
		}
		g.reset()
		lastWidth = width
		lastHeight = height
		drawnLastFrame = texts.map { if (it is PanelComponent) panelText(it) else it as String }
		texts.clear()
	}
}
