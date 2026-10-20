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

	private fun currentFont(): AbstractFontRenderer? {
		return font ?: Renderers.FONT_RENDERER
	}

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
		val font: AbstractFontRenderer = currentFont() ?: return
		font.drawString(text, argb, y, x, shadowArgb)
	}

	override fun textCentered(x: Int, y: Int, text: String, argb: Int, shadowArgb: Int) {
		val font: AbstractFontRenderer = currentFont() ?: return
		font.renderCentered(x, argb, text, shadowArgb, y)
	}

	override fun textRight(x: Int, y: Int, text: String, argb: Int, shadowArgb: Int) {
		val font: AbstractFontRenderer = currentFont() ?: return
		font.renderRightAligned(text, y, argb, x, shadowArgb)
	}

	override fun textWidth(text: String): Int {
		val font: AbstractFontRenderer = currentFont() ?: return 0
		return font.textWidth(text)
	}

	override fun fontReady(): Boolean {
		return Renderers.FONT_RENDERER != null
	}

	override fun thickLine(x1: Int, y1: Int, x2: Int, y2: Int, width: Int, argb: Int) {
		val toolkit: Toolkit = ClientState.activeToolkit ?: return
		toolkit.strongLine(x1, y1, x2, y2, argb, if (width < 1) 1 else width)
	}

	override fun fillCircle(x: Int, y: Int, radius: Int, argb: Int) {
		val toolkit: Toolkit = ClientState.activeToolkit ?: return
		toolkit.za(x, y, radius, argb, 1)
	}

	override fun polygon(xs: IntArray, ys: IntArray, count: Int, argb: Int) {
		val toolkit: Toolkit = ClientState.activeToolkit ?: return
		if (count < 2 || count > xs.size || count > ys.size) {
			return
		}
		for (i in 0 until count) {
			val j: Int = if (i + 1 == count) 0 else i + 1
			toolkit.line(xs[i], ys[i], xs[j], ys[j], argb, 1)
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
			polygonScratch[i * 2] = xs[i]
			polygonScratch[i * 2 + 1] = ys[i]
		}
		WorldMapPolygonFill.fillPolygon(toolkit, polygonScratch, count * 2, argb, null, null)
	}

	override fun clip(x: Int, y: Int, width: Int, height: Int) {
		val toolkit: Toolkit = ClientState.activeToolkit ?: return
		if (!clipped) {
			toolkit.K(savedClip)
			clipped = true
		}
		toolkit.KA(x, y, x + width, y + height)
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
		this.font = when (font) {
			Font.REGULAR -> FontManager.aClass68_4
			Font.BOLD -> MiniMenu.minimenuHeader
			else -> null
		}
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
		sprite.render(x, y)
		return true
	}

	override fun spriteScaled(id: Int, x: Int, y: Int, width: Int, height: Int): Boolean {
		val sprite: NativeSprite = sprite(id) ?: return false
		sprite.drawScaledColored(x, y, width, height, 1, 0, 1)
		return true
	}

	override fun spriteWidth(id: Int): Int {
		val sprite: NativeSprite = sprite(id) ?: return 0
		return sprite.getWidth()
	}

	override fun spriteHeight(id: Int): Int {
		val sprite: NativeSprite = sprite(id) ?: return 0
		return sprite.getHeight()
	}

	override fun itemIcon(itemId: Int, quantity: Int, x: Int, y: Int): Boolean {
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
		icon.render(x, y)
		return true
	}

	/** After each overlay and each draw event: the clip and the font back to the client's. */
	fun reset() {
		unclip()
		font = null
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
