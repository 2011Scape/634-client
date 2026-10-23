package com.jagex.game.runetek5.plugin.api

import kotlin.jvm.JvmField

/**
 * Drawing in the client's 2D, for overlays and frame listeners. Only valid inside a draw callback.
 * Colours are ARGB; the alpha blends (an opaque colour has `0xFF` on top). Text uses the client's
 * small font; `y` is the text's baseline.
 */
interface Graphics {
	/** A one pixel line between two screen points, blended by the colour's alpha. */
	fun line(x1: Int, y1: Int, x2: Int, y2: Int, argb: Int)

	/** A filled rectangle, blended by the colour's alpha. */
	fun fillRect(x: Int, y: Int, width: Int, height: Int, argb: Int)

	/** A one pixel outline just inside the rectangle. */
	fun drawRect(x: Int, y: Int, width: Int, height: Int, argb: Int)

	/** Text starting at [x]. */
	fun text(x: Int, y: Int, text: String, argb: Int, shadowArgb: Int)

	/** Text centred on [x]. */
	fun textCentered(x: Int, y: Int, text: String, argb: Int, shadowArgb: Int)

	/** Text ending at [x]. */
	fun textRight(x: Int, y: Int, text: String, argb: Int, shadowArgb: Int)

	/** How wide [text] draws in the current font, in pixels. */
	fun textWidth(text: String): Int

	/** The current font's line height, baseline to baseline: put the first baseline one of these below a panel's top, and each next line one lower. */
	fun fontHeight(): Int

	/** Whether the font exists yet: false before the first toolkit has loaded its fonts. */
	fun fontReady(): Boolean

	/** A line [width] pixels wide. */
	fun thickLine(x1: Int, y1: Int, x2: Int, y2: Int, width: Int, argb: Int)

	/** A filled circle about ([x], [y]), blended by the colour's alpha. */
	fun fillCircle(x: Int, y: Int, radius: Int, argb: Int)

	/** The outline through the first [count] points of [xs] and [ys], closed. */
	fun polygon(xs: IntArray, ys: IntArray, count: Int, argb: Int)

	/** The inside of the polygon through the first [count] points of [xs] and [ys], blended by the colour's alpha. */
	fun fillPolygon(xs: IntArray, ys: IntArray, count: Int, argb: Int)

	/** Confines what is drawn next to the rectangle, until [unclip] or the end of the overlay. */
	fun clip(x: Int, y: Int, width: Int, height: Int)

	/** Back to drawing anywhere. */
	fun unclip()

	/** The font the text methods use from now on, a [Font]; back to [Font.SMALL] at the end of the overlay. */
	fun font(font: Int)

	/** Draws the cache's sprite [id] with its top-left corner at ([x], [y]). False, and nothing drawn, until the sprite is loaded. */
	fun sprite(id: Int, x: Int, y: Int): Boolean

	/** Draws sprite [id] stretched to [width] by [height]. */
	fun spriteScaled(id: Int, x: Int, y: Int, width: Int, height: Int): Boolean

	/** The width of sprite [id] as [sprite] draws it (its padding included), or 0 until it is loaded. */
	fun spriteWidth(id: Int): Int

	/** The height of sprite [id] as [sprite] draws it (its padding included), or 0 until it is loaded. */
	fun spriteHeight(id: Int): Int

	/**
	 * Draws the 36 by 32 icon of [quantity] of item [itemId], as an inventory shows it, with its
	 * top-left corner at ([x], [y]); a [quantity] above the stack the client can show is drawn as that stack. False,
	 * and nothing drawn, for the frame or two the icon takes to render.
	 */
	fun itemIcon(itemId: Int, quantity: Int, x: Int, y: Int): Boolean

	/** [itemIcon] drawn see-through, [alpha] 0 (not seen) to 255 (as itemIcon). Since API 31. */
	fun itemIconAlpha(itemId: Int, quantity: Int, x: Int, y: Int, alpha: Int): Boolean

	/**
	 * Moves what is drawn from now on by ([dx], [dy]), added to every point, clip included, until the end
	 * of the overlay; how a [PanelOverlay] draws at (0, 0). Since API 12.
	 */
	fun translate(dx: Int, dy: Int)

	/** Draws [image] with its top-left corner at ([x], [y]). False, and nothing drawn, when there is no toolkit to draw with. Since API 12. */
	fun image(image: Image, x: Int, y: Int): Boolean

	/** Draws [image] stretched to [width] by [height]. Since API 12. */
	fun imageScaled(image: Image, x: Int, y: Int, width: Int, height: Int): Boolean
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
