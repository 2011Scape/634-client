package com.jagex.game.runetek5.plugin

import awt.Canvas
import awt.Cursor
import awt.Point
import awt.event.MouseEvent
import awt.event.MouseListener
import awt.image.BufferedImage
import com.jagex.game.runetek5.client.GameShell
import com.jagex.game.runetek5.AbstractMouseManager
import com.jagex.game.runetek5.client.input.mouse.CursorManager
import com.jagex.game.runetek5.client.input.mouse.MouseRecord
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: a plugin's mouse cursor (ClientApi.setCursor), RuneLite's
 * ClientUI.setCursor. While one is set, the game's own cursors (CursorManager.setCursor, called each
 * frame with the cursor the game wants) give way to it; it is put on the canvas again whenever the canvas
 * is a new one or something else (the overlays' resize cursors) changed it. Taking it away puts the
 * game's back at its next frame.
 *
 * Where the platform's cursors have few colours (AWT on X11 and XWayland has two, a foreground and a
 * background, so a coloured cursor came out wrong), the cursor on the canvas is a clear one and the
 * picture is drawn at the mouse as the last thing in each frame instead (drawSoftware), while the mouse
 * is over the canvas.
 */
object PluginCursor {

	/** What the game's CursorManager.currentCursor is while the plugin's cursor is on. */
	private const val PLUGIN_CURSOR: Int = -3

	private var cursor: Cursor? = null

	private var canvas: Canvas? = null

	/** The picture drawn at the mouse in place of a cursor of too few colours, null for none; and its hot spot. */
	private var software: PluginImage? = null
	private var softwareHotX: Int = 0
	private var softwareHotY: Int = 0

	/** For the smoke test: the frames the picture was drawn at the mouse, and where last. */
	@JvmField var softwareFrames: Int = 0
	@JvmField var softwareX: Int = -1
	@JvmField var softwareY: Int = -1

	/** Whether the plugin's cursor is drawn by the client (the platform's has too few colours). */
	@JvmStatic
	fun isSoftware(): Boolean = software != null

	/** Whether the mouse is over the canvas, as only the canvas's own enter and exit say. */
	private var mouseInside: Boolean = true

	private val presence: MouseListener = object : MouseListener {
		override fun mouseClicked(e: MouseEvent) = Unit

		override fun mousePressed(e: MouseEvent) = Unit

		override fun mouseReleased(e: MouseEvent) = Unit

		override fun mouseEntered(e: MouseEvent) {
			mouseInside = true
		}

		override fun mouseExited(e: MouseEvent) {
			mouseInside = false
		}
	}

	@JvmStatic
	fun set(image: PluginImage, hotX: Int, hotY: Int) {
		val canvas: Canvas = GameShell.gameCanvas ?: return
		val x: Int = hotX.coerceIn(0, image.width - 1)
		val y: Int = hotY.coerceIn(0, image.height - 1)
		// type 2, TYPE_INT_ARGB, as the game's own cursors (CursorManager.setcustomcursor)
		val buffered = BufferedImage(image.width, image.height, 2)
		if (maximumCursorColours() < FULL_COLOUR) {
			// a clear cursor, and the picture drawn at the mouse
			software = image
			softwareHotX = x
			softwareHotY = y
		} else {
			software = null
			buffered.setRGB(0, 0, image.width, image.height, image.pixels, 0, image.width)
		}
		cursor = canvas.getToolkit().createCustomCursor(buffered, Point(x, y), "plugin")
		this.canvas = null
		CursorManager.currentCursor = PLUGIN_CURSOR
		apply()
	}

	/** Whether a plugin's cursor is on. */
	@JvmStatic
	fun isSet(): Boolean = cursor != null

	@JvmStatic
	fun reset() {
		if (cursor == null) {
			return
		}
		cursor = null
		software = null
		canvas?.setCursor(null)
		canvas?.removeMouseListener(presence)
		canvas = null
		// the game's own cursor goes back on at its next frame
		CursorManager.currentCursor = -2
	}

	/** Called by CursorManager.setCursor: true when the plugin's cursor stands, and the game's must not be set. */
	@JvmStatic
	fun override(): Boolean {
		if (cursor == null) {
			return false
		}
		if (CursorManager.currentCursor != PLUGIN_CURSOR || canvas !== GameShell.gameCanvas) {
			apply()
		}
		return true
	}

	private fun apply() {
		val canvas: Canvas = GameShell.gameCanvas ?: return
		canvas.setCursor(cursor)
		if (this.canvas !== canvas) {
			this.canvas?.removeMouseListener(presence)
			canvas.addMouseListener(presence)
			mouseInside = true
		}
		this.canvas = canvas
		CursorManager.currentCursor = PLUGIN_CURSOR
	}

	/** After the plugins' FrameDraw, the last of a frame: the picture of a cursor the platform cannot show, at the mouse. */
	@JvmStatic
	fun drawSoftware() {
		val image: PluginImage = software ?: return
		val mouse: AbstractMouseManager = MouseRecord.mouseRecorder ?: return
		if (!mouseInside || canvas !== GameShell.gameCanvas) {
			return
		}
		softwareX = mouse.getMouseX() - softwareHotX
		softwareY = mouse.getMouseY() - softwareHotY
		image.sprite()?.render(softwareX, softwareY)
		softwareFrames++
	}

	/** A palette that shows a picture as it is. */
	private const val FULL_COLOUR: Int = 256
}
