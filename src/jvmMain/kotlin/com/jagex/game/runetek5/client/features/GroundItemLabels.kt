package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.MapRegion
import com.jagex.game.runetek5.OverlayManager
import com.jagex.game.runetek5.cache.node.Node
import com.jagex.game.runetek5.cache.node.ObjStack
import com.jagex.game.runetek5.cache.node.ObjStackEntry
import com.jagex.game.runetek5.config.TypeLists
import com.jagex.game.runetek5.config.item.ItemType
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate
import com.jagex.game.runetek5.rendering.Renderers

/**
 * Not part of the original client: each pile of items on the ground in the scene shows the names
 * of its items, and their counts when more than one, stacked above the tile, most valuable at the
 * bottom, as the client orders a pile (ObjStack.addGroundItem). On only with
 * `client.groundItemLabels=true`; the console command `grounditems` turns it on and off
 * while playing.
 */
object GroundItemLabels {

	@JvmField
	var enabled: Boolean = java.lang.Boolean.getBoolean("client.groundItemLabels")

	/** The most items listed for one pile; more show as "and N more". */
	private const val MAX_LINES: Int = 4

	private const val LINE_HEIGHT: Int = 12

	private const val COLOUR: Int = 0xFFFF9040.toInt()

	private const val SHADOW: Int = 0xFF000000.toInt()

	private const val TILE_SIZE: Int = 512

	/** How many piles the last frame labelled, for the smoke test. */
	@JvmField
	var drawnLastFrame: Int = 0

	/** The bottom line of each pile the last frame labelled, and where, for the smoke test. */
	@JvmField
	val lastTexts: Array<String?> = arrayOfNulls<String>(64)

	@JvmField
	val lastX: IntArray = IntArray(64)

	@JvmField
	val lastY: IntArray = IntArray(64)

	/** The line an item on the ground shows: its name, and its count when more than one. */
	@JvmStatic
	fun line(entry: ObjStackEntry?): String? {
		val type: ItemType? = TypeLists.OBJ!!.list(entry!!.id)
		return if (entry.count > 1) type!!.name + " (" + entry.count + ")" else type!!.name
	}

	/** Labels the piles on the local player's level in the game view the scene was just drawn into. */
	@JvmStatic
	fun draw(areaX: Int, areaY: Int) {
		drawnLastFrame = 0
		if (!enabled || Renderers.FONT_RENDERER == null) {
			return
		}
		val level: Int = PlayerUpdate.localPlayer!!.level.toInt()
		var node: Node? = ObjStack.GROUND_ITEMS.first()
		while (node != null) {
			// the key packs level << 28 | absolute y << 14 | absolute x (ObjStack.addGroundItem)
			val key: Long = node.key
			if ((key ushr 28 and 0x3L).toInt() != level) {
				node = ObjStack.GROUND_ITEMS.next()
				continue
			}
			val sceneX: Int = (key and 0x3FFFL).toInt() - MapRegion.sceneBaseX
			val sceneY: Int = (key shr 14 and 0x3FFFL).toInt() - MapRegion.sceneBaseY
			if (sceneX < 0 || sceneY < 0 || sceneX >= MapRegion.sceneSizeX || sceneY >= MapRegion.sceneSizeY) {
				node = ObjStack.GROUND_ITEMS.next()
				continue
			}
			OverlayManager.projectToScreenWithOffset(level, 0, 0, sceneX * TILE_SIZE + TILE_SIZE / 2, 0, sceneY * TILE_SIZE + TILE_SIZE / 2, 0)
			if (OverlayManager.projectedScreenPosition[0] < 0) {
				node = ObjStack.GROUND_ITEMS.next()
				continue
			}
			val x: Int = areaX + OverlayManager.projectedScreenPosition[0]
			val y: Int = areaY + OverlayManager.projectedScreenPosition[1]
			val pile: ObjStack = node as ObjStack
			var lines: Int = 0
			var more: Int = 0
			var bottom: String? = null
			var entry: ObjStackEntry? = pile.groundItems.first() as ObjStackEntry?
			while (entry != null) {
				if (lines == MAX_LINES) {
					more++
					entry = pile.groundItems.next() as ObjStackEntry?
					continue
				}
				val text: String? = line(entry)
				if (bottom == null) {
					bottom = text
				}
				Renderers.FONT_RENDERER!!.renderCentered(x, COLOUR, text, SHADOW, y - lines * LINE_HEIGHT)
				lines++
				entry = pile.groundItems.next() as ObjStackEntry?
			}
			if (more > 0) {
				Renderers.FONT_RENDERER!!.renderCentered(x, COLOUR, "and " + more + " more", SHADOW, y - lines * LINE_HEIGHT)
			}
			if (bottom != null && drawnLastFrame < lastTexts.size) {
				lastTexts[drawnLastFrame] = bottom
				lastX[drawnLastFrame] = x
				lastY[drawnLastFrame] = y
				drawnLastFrame++
			}
			node = ObjStack.GROUND_ITEMS.next()
		}
	}
}
