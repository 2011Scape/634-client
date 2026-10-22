package com.jagex.game.runetek5.plugin.api

import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * One thing in a [PanelComponent], RuneLite's LayoutableRenderableEntity: drawn with its top-left corner
 * at ([x], [y]) in the room the panel gives it ([preferredWidth] across, [preferredHeight] down, 0 for no
 * limit), the size it took in [out]. Since API 12.
 */
fun interface PanelChild {
	fun render(g: Graphics, x: Int, y: Int, preferredWidth: Int, preferredHeight: Int, out: Size)
}

/**
 * RuneLite's PanelComponent: a background box with [children] in it, one under another ([VERTICAL]) or
 * side by side ([HORIZONTAL]), wrapping to a new column or row at the panel's preferred size when [wrap].
 * Its background is the size its children took last frame, as RuneLite's is. [OverlayPanel] holds one;
 * a plugin can also draw one itself. Since API 12.
 */
class PanelComponent {
	@JvmField
	val children: ArrayList<PanelChild> = ArrayList()

	/** The box's colour, ARGB; 0 for none. */
	@JvmField
	var background: Int = PanelOverlay.STANDARD_BACKGROUND

	@JvmField
	var preferredWidth: Int = STANDARD_WIDTH

	@JvmField
	var preferredHeight: Int = 0

	@JvmField
	var orientation: Int = VERTICAL

	@JvmField
	var wrap: Boolean = false

	/** The space inside the box's edges: left, top, right, bottom. */
	@JvmField
	var borderLeft: Int = STANDARD_BORDER

	@JvmField
	var borderTop: Int = STANDARD_BORDER

	@JvmField
	var borderRight: Int = STANDARD_BORDER

	@JvmField
	var borderBottom: Int = STANDARD_BORDER

	/** The space between children, across and down. */
	@JvmField
	var gapX: Int = 0

	@JvmField
	var gapY: Int = 0

	private var childWidth: Int = 0

	private var childHeight: Int = 0

	private val childSize: Size = Size()

	private val size: Size = Size()

	/** A line of [left] text and [right] text at the far side, as RuneLite's LineComponent. */
	fun addLine(left: String?, leftColour: Int, right: String?, rightColour: Int) {
		children.add(LineComponent(left, leftColour, right, rightColour))
	}

	/** A line of [text] centred, as RuneLite's TitleComponent. */
	fun addTitle(text: String, colour: Int) {
		children.add(TitleComponent(text, colour))
	}

	/** Draws the panel at ([x], [y]): the size it is, or null when it has no children. */
	fun render(g: Graphics, x: Int, y: Int): Size? {
		if (children.isEmpty()) {
			return null
		}
		val width: Int = borderLeft + childWidth + borderRight
		val height: Int = borderTop + childHeight + borderBottom
		if (background != 0) {
			background(g, x, y, width, height, background)
		}
		val baseX: Int = x + borderLeft
		val baseY: Int = y + borderTop
		var across: Int = 0
		var down: Int = 0
		var cx: Int = baseX
		var cy: Int = baseY
		val innerWidth: Int = preferredWidth - borderLeft - borderRight
		val innerHeight: Int = preferredHeight - borderTop - borderBottom
		var totalWidth: Int = 0
		var totalHeight: Int = 0
		for (child in children) {
			var childPreferredWidth: Int = innerWidth
			var childPreferredHeight: Int = innerHeight
			if (!wrap) {
				if (orientation == VERTICAL) {
					childPreferredHeight = 0
				} else {
					childPreferredWidth = 0
				}
			}
			childSize.set(0, 0)
			child.render(g, cx, cy, childPreferredWidth, childPreferredHeight, childSize)
			if (orientation == VERTICAL) {
				down += childSize.height + gapY
				cy = baseY + down
				across = maxOf(across, childSize.width)
			} else {
				across += childSize.width + gapX
				cx = baseX + across
				down = maxOf(down, childSize.height)
			}
			totalWidth = maxOf(totalWidth, across)
			totalHeight = maxOf(totalHeight, down)
			if (!wrap) {
				continue
			}
			if (orientation == VERTICAL) {
				if (innerHeight > 0 && down >= innerHeight) {
					down = 0
					cy = baseY
					val step: Int = childSize.width + gapX
					cx += step
					across += step
				}
			} else if (innerWidth > 0 && across >= innerWidth) {
				across = 0
				cx = baseX
				val step: Int = childSize.height + gapY
				cy += step
				down += step
			}
		}
		if (orientation == HORIZONTAL) {
			totalWidth -= gapX
		} else {
			totalHeight -= gapY
		}
		childWidth = totalWidth
		childHeight = totalHeight
		return size.set(width, height)
	}

	/** RuneLite's LineComponent: [left] text, and [right] text ending at the line's far side; split into more lines when they do not fit. */
	class LineComponent(
		@JvmField var left: String?,
		@JvmField var leftColour: Int,
		@JvmField var right: String?,
		@JvmField var rightColour: Int,
	) : PanelChild {
		override fun render(g: Graphics, x: Int, y: Int, preferredWidth: Int, preferredHeight: Int, out: Size) {
			val left: String = this.left ?: ""
			val right: String = this.right ?: ""
			val lineHeight: Int = g.fontHeight()
			var baseline: Int = y + lineHeight
			val leftWidth: Int = g.textWidth(left)
			val rightWidth: Int = g.textWidth(right)
			if (preferredWidth < leftWidth + rightWidth) {
				var leftRoom: Int = preferredWidth
				var rightRoom: Int = 0
				if (right.isNotEmpty()) {
					rightRoom = preferredWidth / 3
					leftRoom -= rightRoom
				}
				val leftLines: List<String> = lineBreak(g, left, leftRoom)
				val rightLines: List<String> = lineBreak(g, right, rightRoom)
				val count: Int = maxOf(leftLines.size, rightLines.size)
				for (i in 0 until count) {
					if (i < leftLines.size) {
						g.text(x, baseline, leftLines[i], leftColour, SHADOW)
					}
					if (i < rightLines.size) {
						g.text(x + preferredWidth - g.textWidth(rightLines[i]), baseline, rightLines[i], rightColour, SHADOW)
					}
					baseline += lineHeight
				}
				out.set(preferredWidth, baseline - (y + lineHeight))
				return
			}
			if (left.isNotEmpty()) {
				g.text(x, baseline, left, leftColour, SHADOW)
			}
			if (right.isNotEmpty()) {
				g.text(x + preferredWidth - rightWidth, baseline, right, rightColour, SHADOW)
			}
			out.set(preferredWidth, lineHeight)
		}
	}

	/** RuneLite's TitleComponent: [text] centred across the line. */
	class TitleComponent(
		@JvmField var text: String,
		@JvmField var colour: Int,
	) : PanelChild {
		override fun render(g: Graphics, x: Int, y: Int, preferredWidth: Int, preferredHeight: Int, out: Size) {
			g.text(x + (preferredWidth - g.textWidth(text)) / 2, y + g.fontHeight(), text, colour, SHADOW)
			out.set(preferredWidth, g.fontHeight())
		}
	}

	companion object {
		const val VERTICAL: Int = 0

		const val HORIZONTAL: Int = 1

		/** RuneLite's ComponentConstants. */
		const val STANDARD_WIDTH: Int = 129

		const val STANDARD_BORDER: Int = 4

		const val SHADOW: Int = -0x1000000

		/**
		 * RuneLite's BackgroundComponent: [argb] filled, with an outline a fifth darker round the outside
		 * and one a fifth lighter just inside it, both more opaque than the fill.
		 */
		@JvmStatic
		fun background(g: Graphics, x: Int, y: Int, width: Int, height: Int, argb: Int) {
			val alpha: Int = minOf(255, ((argb ushr 24) * 1.4f).toInt())
			val red: Int = (argb shr 16) and 0xFF
			val green: Int = (argb shr 8) and 0xFF
			val blue: Int = argb and 0xFF
			val outside: Int = (alpha shl 24) or ((red * 0.8f).toInt() shl 16) or ((green * 0.8f).toInt() shl 8) or (blue * 0.8f).toInt()
			val inside: Int = (alpha shl 24) or (minOf(255, (red * 1.2f).toInt()) shl 16) or (minOf(255, (green * 1.2f).toInt()) shl 8) or minOf(255, (blue * 1.2f).toInt())
			g.fillRect(x, y, width, height, argb)
			g.drawRect(x, y, width, height, outside)
			g.drawRect(x + 1, y + 1, width - 2, height - 2, inside)
		}

		/** RuneLite's LineComponent.lineBreakText: [text] split at spaces into lines no wider than [maxWidth]. */
		@JvmStatic
		fun lineBreak(g: Graphics, text: String, maxWidth: Int): List<String> {
			val words: List<String> = text.split(' ')
			val lines: ArrayList<String> = ArrayList()
			if (text.isEmpty()) {
				return lines
			}
			val line: StringBuilder = StringBuilder(words[0])
			var room: Int = maxWidth - g.textWidth(words[0])
			val space: Int = g.textWidth(" ")
			for (i in 1 until words.size) {
				val word: String = words[i]
				val width: Int = g.textWidth(word)
				if (width + space > room) {
					lines.add(line.toString())
					line.clear()
					line.append(word)
					room = maxWidth - width
				} else {
					line.append(' ').append(word)
					room -= space + width
				}
			}
			lines.add(line.toString())
			return lines
		}
	}
}

/**
 * RuneLite's OverlayPanel: a [PanelOverlay] that is a [panelComponent], resizable. A subclass adds this
 * frame's lines to [panelComponent] in its own [render] and returns `super.render(g, view)`, which draws
 * them (as wide as the player made the panel, on the background they chose) and clears them for the next
 * frame unless [clearChildren] is false. Since API 12.
 */
abstract class OverlayPanel(name: String) : PanelOverlay(name) {
	@JvmField
	val panelComponent: PanelComponent = PanelComponent()

	@JvmField
	var clearChildren: Boolean = true

	init {
		resizable = true
	}

	override fun render(g: Graphics, view: Viewport): Size? {
		val oldWidth: Int = panelComponent.preferredWidth
		val oldHeight: Int = panelComponent.preferredHeight
		if (preferredWidth > 0 || preferredHeight > 0) {
			panelComponent.preferredWidth = preferredWidth
			panelComponent.preferredHeight = preferredHeight
		}
		val oldBackground: Int = panelComponent.background
		if (oldBackground == STANDARD_BACKGROUND) {
			panelComponent.background = backgroundColour
		}
		val size: Size?
		try {
			size = panelComponent.render(g, 0, 0)
		} finally {
			if (clearChildren) {
				panelComponent.children.clear()
			}
		}
		panelComponent.preferredWidth = oldWidth
		panelComponent.preferredHeight = oldHeight
		panelComponent.background = oldBackground
		return size
	}
}
