package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.plugin.api.Graphics
import com.jagex.game.runetek5.plugin.api.OverlayOrigin
import com.jagex.game.runetek5.plugin.api.OverlayOriginX
import com.jagex.game.runetek5.plugin.api.OverlayOriginY
import com.jagex.game.runetek5.plugin.api.OverlayPosition
import com.jagex.game.runetek5.plugin.api.PanelOverlay
import com.jagex.game.runetek5.plugin.api.Size
import com.jagex.game.runetek5.plugin.api.Viewport
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: RuneLite's SnapCorner, one corner of the game view the panels stack
 * in: where the next panel goes ([nextDrawPosition]), moved along by each one drawn ([shift]), and, while
 * the player manages the panels, a box to drop a panel on that can itself be dragged elsewhere
 * ([CornerOverlay]), its place kept as `snapcorner_<POSITION>_location`.
 */
class SnapCorner(
	@JvmField val position: OverlayPosition,
	private val mode: Int,
	@JvmField val originX: OverlayOriginX,
	@JvmField val originY: OverlayOriginY,
	private val offX: Int,
	private val offY: Int,
) {
	/** The offset from the origin now (the player may have dragged it). */
	@JvmField var curX: Int = offX
	@JvmField var curY: Int = offY

	@JvmField var inDragMode: Boolean = false

	/** The corner's point, the point the next panel goes at, the size of what is stacked, and last frame's. */
	private var cx: Int = 0
	private var cy: Int = 0
	private var px: Int = 0
	private var py: Int = 0
	private var sx: Int = 0
	private var sy: Int = 0
	@JvmField var lastWidth: Int = 0
	@JvmField var lastHeight: Int = 0

	private var cornerOverlay: CornerOverlay? = null

	fun isRepositioned(): Boolean = curX != offX || curY != offY

	fun reset() {
		curX = offX
		curY = offY
	}

	/** At the start of a frame: the corner at ([x], [y]), nothing stacked in it yet. */
	fun setPosition(x: Int, y: Int) {
		cx = x
		cy = y
		px = x
		py = y
		lastWidth = sx
		lastHeight = sy
		sx = 0
		sy = 0
	}

	/** A panel was drawn at ([x], [y]), [width] by [height]: the next one goes past it. */
	fun shift(x: Int, y: Int, width: Int, height: Int, padding: Int) {
		when (mode and 0xF0) {
			EXPAND_LEFT -> px = minOf(px, x - padding)
			EXPAND_RIGHT -> px = maxOf(px, x + width + padding)
			EXPAND_UP -> py = minOf(py, y - padding)
			EXPAND_DOWN -> py = maxOf(py, y + height + padding)
		}
		when (mode and 0xF0) {
			EXPAND_UP, EXPAND_DOWN -> {
				sx = maxOf(sx, width)
				sy += height + padding
			}
			EXPAND_LEFT, EXPAND_RIGHT -> {
				sx += width + padding
				sy = maxOf(sy, height)
			}
		}
	}

	/** Where the next panel, [width] by [height], is drawn. */
	fun nextDrawPosition(width: Int, height: Int, out: IntArray) {
		out[0] = px
		out[1] = py
		if (mode and ALIGNMENT_CENTER_HORIZONTAL != 0) {
			out[0] -= width / 2
		} else if (mode and ALIGNMENT_RIGHT != 0) {
			out[0] -= width
		}
		if (mode and ALIGNMENT_BOTTOM != 0) {
			out[1] -= height
		}
	}

	private fun boxWidth(): Int = maxOf(lastWidth, SIZE) + BOX_PADDING

	private fun boxHeight(): Int = maxOf(lastHeight, SIZE) + BOX_PADDING

	/** RuneLite's translateOffsetForAlignment: the box's top-left corner from the corner's point. */
	fun alignOffset(point: IntArray) {
		if (mode and ALIGNMENT_CENTER_HORIZONTAL != 0) {
			point[0] -= boxWidth() / 2
		} else if (mode and ALIGNMENT_RIGHT != 0) {
			point[0] -= boxWidth()
		}
		if (mode and ALIGNMENT_BOTTOM != 0) {
			point[1] -= boxHeight()
		}
	}

	/** The box this frame: x, y, width, height. */
	fun cornerBox(out: IntArray) {
		out[0] = cx
		out[1] = cy
		out[2] = boxWidth()
		out[3] = boxHeight()
		alignOffset(out)
	}

	private val box: IntArray = IntArray(4)

	fun cornerContains(x: Int, y: Int): Boolean {
		cornerBox(box)
		return x >= box[0] && y >= box[1] && x < box[0] + box[2] && y < box[1] + box[3]
	}

	fun overlay(): CornerOverlay {
		var overlay: CornerOverlay? = cornerOverlay
		if (overlay == null) {
			overlay = CornerOverlay()
			cornerOverlay = overlay
		}
		overlay.place()
		return overlay
	}

	fun load(config: PluginConfigImpl) {
		val text: String = config.get(KEY_PREFIX + position.name + KEY_SUFFIX, "")
		val at: Int = text.indexOf(':')
		if (at > 0) {
			val x: Int? = text.substring(0, at).toIntOrNull()
			val y: Int? = text.substring(at + 1).toIntOrNull()
			if (x != null && y != null) {
				curX = x
				curY = y
			}
		}
	}

	fun save(config: PluginConfigImpl) {
		if (isRepositioned()) {
			config.set(KEY_PREFIX + position.name + KEY_SUFFIX, curX.toString() + ":" + curY)
		} else {
			config.remove(KEY_PREFIX + position.name + KEY_SUFFIX)
		}
	}

	/** RuneLite's CornerOverlay: the corner's box while managing, filled while a panel is dragged or the mouse is over it, dragged to move the corner. */
	inner class CornerOverlay : PanelOverlay("SnapCorner_" + position.name) {
		private var mouseOver: Boolean = false

		private val size: Size = Size()

		init {
			snappable = false
			origin = OverlayOrigin.MANUAL
			originX = this@SnapCorner.originX
			originY = this@SnapCorner.originY
		}

		/** The box's place from the corner's offset. */
		fun place() {
			hasPreferredLocation = true
			val p: IntArray = intArrayOf(curX, curY)
			alignOffset(p)
			preferredX = p[0]
			preferredY = p[1]
		}

		override fun onMoved() {
			// RuneLite's translateLocationForAlignment: the corner's point from the box's place
			var x: Int = preferredX
			var y: Int = preferredY
			if (mode and ALIGNMENT_CENTER_HORIZONTAL != 0) {
				x += boxWidth() / 2
			} else if (mode and ALIGNMENT_RIGHT != 0) {
				x += boxWidth()
			}
			if (mode and ALIGNMENT_BOTTOM != 0) {
				y += boxHeight()
			}
			curX = x
			curY = y
		}

		override fun onReset() {
			curX = offX
			curY = offY
			place()
			origin = OverlayOrigin.MANUAL
			originX = this@SnapCorner.originX
			originY = this@SnapCorner.originY
		}

		override fun onMouseOver() {
			mouseOver = true
		}

		override fun render(g: Graphics, view: Viewport): Size? {
			place()
			if (mouseOver || inDragMode) {
				g.fillRect(0, 0, boxWidth(), boxHeight(), if (mouseOver) ACTIVE_COLOUR else COLOUR)
			}
			mouseOver = false
			return size.set(boxWidth(), boxHeight())
		}
	}

	companion object {
		const val ALIGNMENT_RIGHT: Int = 0x1
		const val ALIGNMENT_BOTTOM: Int = 0x2
		const val ALIGNMENT_CENTER_HORIZONTAL: Int = 0x4
		const val EXPAND_LEFT: Int = 0x10
		const val EXPAND_RIGHT: Int = 0x20
		const val EXPAND_UP: Int = 0x40
		const val EXPAND_DOWN: Int = 0x80

		private const val SIZE: Int = 80
		private const val BOX_PADDING: Int = 10
		private const val COLOUR: Int = 0x3200FFFF
		private const val ACTIVE_COLOUR: Int = 0x6400FF00

		/** RuneLite's SnapCorners BORDER and MOUSEOVERTEXT_OFFSET. */
		private const val BORDER: Int = 5

		/**
		 * The top-left corner's top: under the game's mouse-over text, which 634 draws larger than OSRS
		 * (the panels sat at 34 before they could be moved).
		 */
		private const val TOP_LEFT_Y: Int = 34

		/** The top-left corner's left, where the panels sat before they could be moved. */
		private const val TOP_LEFT_X: Int = 4

		private const val KEY_PREFIX: String = "snapcorner_"
		private const val KEY_SUFFIX: String = "_location"

		/** The corners in OverlayPosition's order, as RuneLite's SnapCorners makes them. */
		@JvmStatic
		fun all(): Array<SnapCorner> = arrayOf(
			SnapCorner(OverlayPosition.TOP_LEFT, EXPAND_DOWN, OverlayOriginX.HUD_CONTAINER_LEFT, OverlayOriginY.HUD_CONTAINER_TOP, TOP_LEFT_X, TOP_LEFT_Y),
			SnapCorner(OverlayPosition.TOP_CENTER, ALIGNMENT_CENTER_HORIZONTAL or EXPAND_DOWN, OverlayOriginX.HUD_CONTAINER_CENTER, OverlayOriginY.TOP, 0, BORDER),
			SnapCorner(OverlayPosition.TOP_RIGHT, ALIGNMENT_RIGHT or EXPAND_DOWN, OverlayOriginX.HUD_CONTAINER_RIGHT, OverlayOriginY.TOP, -BORDER, BORDER),
			SnapCorner(OverlayPosition.BOTTOM_LEFT, ALIGNMENT_BOTTOM or EXPAND_RIGHT, OverlayOriginX.CHATBOX_LEFT, OverlayOriginY.CHATBOX_TOP, BORDER, -BORDER),
			SnapCorner(OverlayPosition.BOTTOM_RIGHT, ALIGNMENT_RIGHT or ALIGNMENT_BOTTOM or EXPAND_LEFT, OverlayOriginX.HUD_CONTAINER_RIGHT, OverlayOriginY.HUD_CONTAINER_BOTTOM, -BORDER, -BORDER),
			SnapCorner(OverlayPosition.ABOVE_CHATBOX_RIGHT, ALIGNMENT_RIGHT or ALIGNMENT_BOTTOM or EXPAND_UP, OverlayOriginX.CHATBOX_RIGHT, OverlayOriginY.CHATBOX_TOP, -BORDER, -BORDER),
			SnapCorner(OverlayPosition.CANVAS_TOP_RIGHT, ALIGNMENT_RIGHT or EXPAND_DOWN, OverlayOriginX.RIGHT, OverlayOriginY.TOP, 0, 0),
		)
	}
}
