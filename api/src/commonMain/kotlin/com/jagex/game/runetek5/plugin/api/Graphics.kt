package com.jagex.game.runetek5.plugin.api

import kotlin.jvm.JvmField

/**
 * Drawing in the client's 2D, for overlays and frame listeners. Only valid inside a draw callback.
 * Colours are ARGB; the alpha blends (an opaque colour has `0xFF` on top). Text uses the client's
 * small font; `y` is the text's baseline.
 */
interface Graphics {
	fun line(x1: Int, y1: Int, x2: Int, y2: Int, argb: Int)

	fun fillRect(x: Int, y: Int, width: Int, height: Int, argb: Int)

	fun drawRect(x: Int, y: Int, width: Int, height: Int, argb: Int)

	/** Text starting at [x]. */
	fun text(x: Int, y: Int, text: String, argb: Int, shadowArgb: Int)

	/** Text centred on [x]. */
	fun textCentered(x: Int, y: Int, text: String, argb: Int, shadowArgb: Int)

	/** Text ending at [x]. */
	fun textRight(x: Int, y: Int, text: String, argb: Int, shadowArgb: Int)

	fun textWidth(text: String): Int

	/** Whether the font exists yet: false before the first toolkit has loaded its fonts. */
	fun fontReady(): Boolean
}

/** Where the game view is on the screen: its top-left corner and size, in screen pixels. */
class Viewport {
	@JvmField
	var x: Int = 0

	@JvmField
	var y: Int = 0

	@JvmField
	var width: Int = 0

	@JvmField
	var height: Int = 0
}
