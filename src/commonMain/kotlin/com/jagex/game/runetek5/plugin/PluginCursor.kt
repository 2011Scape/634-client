package com.jagex.game.runetek5.plugin

import awt.Canvas
import awt.Cursor
import awt.Point
import awt.image.BufferedImage
import com.jagex.game.runetek5.client.GameShell
import com.jagex.game.runetek5.client.input.mouse.CursorManager
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: a plugin's mouse cursor (ClientApi.setCursor), RuneLite's
 * ClientUI.setCursor. While one is set, the game's own cursors (CursorManager.setCursor, called each
 * frame with the cursor the game wants) give way to it; it is put on the canvas again whenever the canvas
 * is a new one or something else (the overlays' resize cursors) changed it. Taking it away puts the
 * game's back at its next frame.
 */
object PluginCursor {

	/** What the game's CursorManager.currentCursor is while the plugin's cursor is on. */
	private const val PLUGIN_CURSOR: Int = -3

	private var cursor: Cursor? = null

	private var canvas: Canvas? = null

	@JvmStatic
	fun set(image: PluginImage, hotX: Int, hotY: Int) {
		val canvas: Canvas = GameShell.gameCanvas ?: return
		// type 2, TYPE_INT_ARGB, as the game's own cursors (CursorManager.setcustomcursor)
		val buffered = BufferedImage(image.width, image.height, 2)
		buffered.setRGB(0, 0, image.width, image.height, image.pixels, 0, image.width)
		cursor = canvas.getToolkit().createCustomCursor(buffered, Point(hotX.coerceIn(0, image.width - 1), hotY.coerceIn(0, image.height - 1)), "plugin")
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
		canvas?.setCursor(null)
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
		this.canvas = canvas
		CursorManager.currentCursor = PLUGIN_CURSOR
	}
}
