package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.cache.node.JavaObjSprite
import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.client.menu.MiniMenu
import com.jagex.game.runetek5.config.Resource
import com.jagex.game.runetek5.config.TypeLists
import com.jagex.game.runetek5.config.objtype.ObjNumMode
import com.jagex.game.runetek5.config.objtype.ObjTypeList
import com.jagex.game.runetek5.config.rsinterface.Component
import com.jagex.game.runetek5.graphics.SpriteDataProvider
import com.jagex.game.runetek5.graphics.Toolkit
import com.jagex.game.runetek5.graphics.font.AbstractFontRenderer
import com.jagex.game.runetek5.graphics.font.FontManager
import com.jagex.game.runetek5.graphics.sprite.NativeSprite
import com.jagex.game.runetek5.graphics.sprite.SpriteType
import com.jagex.game.runetek5.plugin.api.Font
import com.jagex.game.runetek5.plugin.api.Graphics
import com.jagex.game.runetek5.plugin.api.Image
import kotlin.jvm.JvmField
import com.jagex.game.runetek5.rendering.Renderers
import com.jagex.game.runetek5.statics.WorldMapPolygonFill

/**
 * Not part of the original client: the plugin API's drawing, through the active toolkit, its fonts
 * and its sprites (plugin.api.Graphics). The sprites a plugin asks for are kept per toolkit; an item
 * icon goes through the client's own batch (Component.ITEMS, rendered at the end of the cycle) and
 * is there the next frame. [reset] runs after every overlay, so a clip or a font never outlives it.
 */
object ToolkitGraphics : Graphics {

	private var font: AbstractFontRenderer? = null

	private val savedClip: IntArray = IntArray(4)

	private var clipped: Boolean = false

	private var polygonScratch: IntArray = IntArray(64)

	/** The most sprites kept for the plugins; beyond it the cache starts afresh (a plugin that walks the whole sprite archive gets no further). */
	private const val MAX_SPRITES: Int = 128

	private val spriteIds: IntArray = IntArray(MAX_SPRITES)

	private val spriteRefs: Array<NativeSprite?> = arrayOfNulls<NativeSprite?>(MAX_SPRITES)

	private var spriteCount: Int = 0

	private var spriteToolkit: Toolkit? = null

	/** The item icons asked for this cycle and not yet rendered, so that each is queued once: item id shifted left 32, or the quantity. */
	private const val MAX_PENDING_ICONS: Int = 64

	private val pendingIcons: LongArray = LongArray(MAX_PENDING_ICONS)

	private var pendingIconCount: Int = 0

	private const val ICON_SHADOW: Int = 0xFF000000.toInt()

	private const val OPAQUE: Int = 255

	/** What [translate] added to every point, until [reset]. */
	private var tx: Int = 0

	private var ty: Int = 0

	/** The [Font] text starts in after [reset]: the Overlays settings' dynamic overlay font, small unless the player chose another. */
	@JvmField
	var defaultFont: Int = Font.SMALL

	private fun currentFont(): AbstractFontRenderer? {
		return font ?: fontFor(defaultFont) ?: Renderers.FONT_RENDERER
	}

	private fun fontFor(font: Int): AbstractFontRenderer? {
		return when (font) {
			Font.REGULAR -> FontManager.aClass68_4
			Font.BOLD -> MiniMenu.minimenuHeader
			else -> null
		}
	}

	override fun line(x1: Int, y1: Int, x2: Int, y2: Int, argb: Int) {
		val toolkit: Toolkit = ClientState.activeToolkit ?: return
		toolkit.line(x1 + tx, y1 + ty, x2 + tx, y2 + ty, argb, 1)
	}

	override fun fillRect(x: Int, y: Int, width: Int, height: Int, argb: Int) {
		val toolkit: Toolkit = ClientState.activeToolkit ?: return
		toolkit.aa(x + tx, y + ty, width, height, argb, 1)
	}

	override fun drawRect(x: Int, y: Int, width: Int, height: Int, argb: Int) {
		val toolkit: Toolkit = ClientState.activeToolkit ?: return
		toolkit.outlineRect(x + tx, y + ty, width, height, argb, 1)
	}

	override fun text(x: Int, y: Int, text: String, argb: Int, shadowArgb: Int) {
		val font: AbstractFontRenderer = currentFont() ?: return
		font.drawString(text, argb, y + ty, x + tx, shadowArgb)
	}

	override fun textCentered(x: Int, y: Int, text: String, argb: Int, shadowArgb: Int) {
		val font: AbstractFontRenderer = currentFont() ?: return
		font.renderCentered(x + tx, argb, text, shadowArgb, y + ty)
	}

	override fun textRight(x: Int, y: Int, text: String, argb: Int, shadowArgb: Int) {
		val font: AbstractFontRenderer = currentFont() ?: return
		font.renderRightAligned(text, y + ty, argb, x + tx, shadowArgb)
	}

	override fun textWidth(text: String): Int {
		val font: AbstractFontRenderer = currentFont() ?: return 0
		return font.textWidth(text)
	}

	override fun fontHeight(): Int {
		val font: AbstractFontRenderer = currentFont() ?: return 0
		return font.lineHeight()
	}

	override fun fontReady(): Boolean {
		return Renderers.FONT_RENDERER != null
	}

	override fun thickLine(x1: Int, y1: Int, x2: Int, y2: Int, width: Int, argb: Int) {
		val toolkit: Toolkit = ClientState.activeToolkit ?: return
		// an alpha of 0 was always drawn solid (strongLine ignored alpha): keep that for the plugins that pass RGB alone
		val colour: Int = if (argb ushr 24 == 0) argb or (0xFF shl 24) else argb
		val dx: Double = (x2 - x1).toDouble()
		val dy: Double = (y2 - y1).toDouble()
		val length: Double = kotlin.math.sqrt(dx * dx + dy * dy)
		if (width <= 1 || length < 1.0) {
			// as Graphics.line: blended by its alpha
			toolkit.line(x1 + tx, y1 + ty, x2 + tx, y2 + ty, colour, 1)
			return
		}
		// Not part of the original client: a wide line as a filled quad with square ends half its width past each
		// point, so the corners of an outline meet. OpenGL's core profile (634HD) draws glLineWidth wider than 1 as
		// 1, so strongLine's width is lost there; a quad is the same on every toolkit, and blends by its alpha.
		val half: Double = width / 2.0
		val ux: Double = dx / length * half
		val uy: Double = dy / length * half
		val nx: Double = -uy
		val ny: Double = ux
		val ax: Double = x1 - ux
		val ay: Double = y1 - uy
		val bx: Double = x2 + ux
		val by: Double = y2 + uy
		lineXs[0] = kotlin.math.round(ax + nx).toInt()
		lineYs[0] = kotlin.math.round(ay + ny).toInt()
		lineXs[1] = kotlin.math.round(bx + nx).toInt()
		lineYs[1] = kotlin.math.round(by + ny).toInt()
		lineXs[2] = kotlin.math.round(bx - nx).toInt()
		lineYs[2] = kotlin.math.round(by - ny).toInt()
		lineXs[3] = kotlin.math.round(ax - nx).toInt()
		lineYs[3] = kotlin.math.round(ay - ny).toInt()
		fillPolygon(lineXs, lineYs, 4, colour)
	}

	private val lineXs: IntArray = IntArray(4)
	private val lineYs: IntArray = IntArray(4)

	override fun fillCircle(x: Int, y: Int, radius: Int, argb: Int) {
		val toolkit: Toolkit = ClientState.activeToolkit ?: return
		toolkit.za(x + tx, y + ty, radius, argb, 1)
	}

	override fun polygon(xs: IntArray, ys: IntArray, count: Int, argb: Int) {
		val toolkit: Toolkit = ClientState.activeToolkit ?: return
		if (count < 2 || count > xs.size || count > ys.size) {
			return
		}
		for (i in 0 until count) {
			val j: Int = if (i + 1 == count) 0 else i + 1
			toolkit.line(xs[i] + tx, ys[i] + ty, xs[j] + tx, ys[j] + ty, argb, 1)
		}
	}

	override fun fillPolygon(xs: IntArray, ys: IntArray, count: Int, argb: Int) {
		val toolkit: Toolkit = ClientState.activeToolkit ?: return
		if (count < 3 || count > xs.size || count > ys.size) {
			return
		}
		if (polygonScratch.size < count * 2) {
			polygonScratch = IntArray(count * 2)
		}
		for (i in 0 until count) {
			polygonScratch[i * 2] = xs[i] + tx
			polygonScratch[i * 2 + 1] = ys[i] + ty
		}
		WorldMapPolygonFill.fillPolygon(toolkit, polygonScratch, count * 2, argb, null, null)
	}

	override fun clip(x: Int, y: Int, width: Int, height: Int) {
		val toolkit: Toolkit = ClientState.activeToolkit ?: return
		if (!clipped) {
			toolkit.K(savedClip)
			clipped = true
		}
		toolkit.KA(x + tx, y + ty, x + tx + width, y + ty + height)
	}

	override fun unclip() {
		if (!clipped) {
			return
		}
		clipped = false
		val toolkit: Toolkit = ClientState.activeToolkit ?: return
		toolkit.KA(savedClip[0], savedClip[1], savedClip[2], savedClip[3])
	}

	override fun font(font: Int) {
		this.font = fontFor(font) ?: Renderers.FONT_RENDERER
	}

	private fun sprite(id: Int): NativeSprite? {
		val toolkit: Toolkit = ClientState.activeToolkit ?: return null
		if (toolkit !== spriteToolkit) {
			// another toolkit's sprites are no good to this one
			clearSprites()
			spriteToolkit = toolkit
		}
		for (i in 0 until spriteCount) {
			if (spriteIds[i] == id) {
				return spriteRefs[i]
			}
		}
		if (id < 0) {
			return null
		}
		val type: SpriteType = SpriteDataProvider.listSingle(Resource.SPRITES, id, 0) ?: return null
		val made: NativeSprite = toolkit.generateSprite(type, true) ?: return null
		if (spriteCount == MAX_SPRITES) {
			clearSprites()
		}
		spriteIds[spriteCount] = id
		spriteRefs[spriteCount] = made
		spriteCount++
		return made
	}

	private fun clearSprites() {
		for (i in 0 until spriteCount) {
			spriteRefs[i] = null
		}
		spriteCount = 0
	}

	override fun sprite(id: Int, x: Int, y: Int): Boolean {
		val sprite: NativeSprite = sprite(id) ?: return false
		sprite.render(x + tx, y + ty)
		return true
	}

	override fun spriteScaled(id: Int, x: Int, y: Int, width: Int, height: Int): Boolean {
		val sprite: NativeSprite = sprite(id) ?: return false
		sprite.drawScaledColored(x + tx, y + ty, width, height, 1, 0, 1)
		return true
	}

	override fun spriteWidth(id: Int): Int {
		val sprite: NativeSprite = sprite(id) ?: return 0
		// the whole canvas, padding and all, as sprite() draws it: getWidth() is the trimmed image only
		return sprite.scaleWidth()
	}

	override fun spriteHeight(id: Int): Int {
		val sprite: NativeSprite = sprite(id) ?: return 0
		return sprite.scaleHeight()
	}

	override fun itemIcon(itemId: Int, quantity: Int, x: Int, y: Int): Boolean = itemIconAlpha(itemId, quantity, x, y, OPAQUE)

	override fun itemIconAlpha(itemId: Int, quantity: Int, x: Int, y: Int, alpha: Int): Boolean {
		val toolkit: Toolkit = ClientState.activeToolkit ?: return false
		val types: ObjTypeList = TypeLists.OBJ ?: return false
		if (itemId < 0) {
			return false
		}
		val icon: NativeSprite? = types.getCachedSprite(0, itemId, quantity, ICON_SHADOW, ObjNumMode.SHOWCOUNT_IFNOT1, toolkit, null)
		if (icon == null) {
			val key: Long = (itemId.toLong() shl 32) or (quantity.toLong() and 0xFFFFFFFFL)
			for (i in 0 until pendingIconCount) {
				if (pendingIcons[i] == key) {
					return false // queued already this cycle
				}
			}
			if (pendingIconCount < MAX_PENDING_ICONS) {
				pendingIcons[pendingIconCount++] = key
				// the same order of values as Component.queueItemSpriteIfMissing: the class's names are not its meanings
				Component.ITEMS.addLast(JavaObjSprite(itemId, quantity, 0, ICON_SHADOW, ObjNumMode.SHOWCOUNT_IFNOT1, false))
			}
			return false
		}
		if (alpha >= OPAQUE) {
			icon.render(x + tx, y + ty)
		} else if (alpha > 0) {
			// as the interfaces draw a see-through sprite: the alpha in the tint's top byte
			icon.render(x + tx, y + ty, 0, (alpha shl 24) or 0xFFFFFF, 1)
		}
		return true
	}

	override fun translate(dx: Int, dy: Int) {
		tx += dx
		ty += dy
	}

	override fun image(image: Image, x: Int, y: Int): Boolean {
		val sprite: NativeSprite = (image as? PluginImage)?.sprite() ?: return false
		sprite.render(x + tx, y + ty)
		return true
	}

	override fun imageScaled(image: Image, x: Int, y: Int, width: Int, height: Int): Boolean {
		val sprite: NativeSprite = (image as? PluginImage)?.sprite() ?: return false
		sprite.drawScaledColored(x + tx, y + ty, width, height, 1, 0, 1)
		return true
	}

	/** After each overlay and each draw event: the clip and the font back to the client's. */
	fun reset() {
		unclip()
		font = null
		tx = 0
		ty = 0
	}

	/** Each client cycle: the item batch has run, so a missing icon may be asked for again. */
	fun cycle() {
		pendingIconCount = 0
	}

	/** The toolkit changed: its sprites go with it. */
	fun toolkitChanged() {
		clearSprites()
		spriteToolkit = null
	}
}
