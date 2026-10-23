package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.Widget
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: the items the interfaces show, for the plugins that draw over them
 * (RuneLite's WidgetItemOverlay): the backpack, the bank and the bank's backpack, the deposit box and the
 * worn equipment, each item with its place on the screen. Only what was drawn this frame
 * (Widget.isDrawnThisFrame, API 25), so call it at FrameDraw.
 */
object ItemGrids {

	/** Receives an item shown: its id, its quantity and its slot's box on the screen. */
	fun interface ItemVisitor {
		fun visit(itemId: Int, quantity: Int, x: Int, y: Int, width: Int, height: Int)
	}

	/** The grids of items: the backpack (149:0), the bank (762:93), the bank's backpack (763:0), the deposit box (11:17). */
	@JvmField
	val GRIDS: Array<IntArray> = arrayOf(intArrayOf(149, 0), intArrayOf(762, 93), intArrayOf(763, 0), intArrayOf(11, 17))

	/** The worn equipment tab, its slots each a component of their own. */
	const val EQUIPMENT: Int = 387

	private const val EQUIPMENT_COMPONENTS: Int = 64

	@JvmStatic
	fun forEach(client: ClientApi, visitor: ItemVisitor) {
		for (grid in GRIDS) {
			val parent: Widget? = client.widget(grid[0], grid[1])
			if (parent != null && parent.isDrawnThisFrame()) {
				for (i in 0 until parent.childCount()) {
					visitSlot(parent.child(i), visitor)
				}
			}
		}
		for (c in 0 until EQUIPMENT_COMPONENTS) {
			visitSlot(client.widget(EQUIPMENT, c), visitor)
		}
	}

	private fun visitSlot(slot: Widget?, visitor: ItemVisitor) {
		if (slot == null || slot.itemId() < 0 || slot.isHidden() || !slot.isDrawnThisFrame() || slot.width() <= 0) {
			return
		}
		visitor.visit(slot.itemId(), slot.itemQuantity(), slot.screenX(), slot.screenY(), slot.width(), slot.height())
	}
}
