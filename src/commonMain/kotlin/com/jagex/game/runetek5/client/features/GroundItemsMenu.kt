package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.MenuEntry
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: what Ground Items does to the finished right-click menu, as
 * RuneLite's onMenuEntryAdded and onClientTick do: a highlighted (or, when asked, hidden) item's
 * option or name in its colour, a stack's quantity after its name, a hidden item sunk below the
 * other entries, and identical entries merged into one with " x N".
 */
object GroundItemsMenu {

	@JvmStatic
	fun menuBuilt(client: ClientApi) {
		val plugin: GroundItems = GroundItems
		if (!plugin.enabled) {
			return
		}
		val sink: ArrayList<MenuEntry> = ArrayList()
		for (i in 0 until client.menuEntryCount()) {
			val entry: MenuEntry = client.menuEntry(i) ?: continue
			if (GroundItems.isGroundItemOption(entry.clickType())) {
				val item: GroundItems.Item? = GroundItems.itemOf(client, entry)
				if (item != null && treat(client, i, entry, item)) {
					sink.add(entry)
				}
			}
		}
		if (plugin.collapseEntries) {
			collapse(client)
		}
		for (entry in sink) {
			for (i in 0 until client.menuEntryCount()) {
				if (client.menuEntry(i) === entry) {
					client.deprioritizeMenuEntry(i)
					break
				}
			}
		}
	}

	/** One ground item entry: colours and quantity; true when it is to be sunk. */
	private fun treat(client: ClientApi, i: Int, entry: MenuEntry, item: GroundItems.Item): Boolean {
		val plugin: GroundItems = GroundItems
		GroundItems.judge(item)
		val canBeRecoloured: Boolean = item.highlighted || (item.hidden && plugin.recolorMenuHiddenItems)
		val menuHighlights: Boolean = plugin.itemHighlightMode == GroundItems.HIGHLIGHT_MENU || plugin.itemHighlightMode == GroundItems.HIGHLIGHT_BOTH
		var target: String = entry.target() ?: ""
		if (menuHighlights && canBeRecoloured && item.colour != plugin.defaultColour) {
			val tag: String = GroundItems.colourTag(item.colour)
			if (plugin.menuHighlightMode == GroundItems.MENU_BOTH || plugin.menuHighlightMode == GroundItems.MENU_OPTION) {
				client.renameMenuEntry(i, tag + (entry.option() ?: ""))
			}
			if (plugin.menuHighlightMode == GroundItems.MENU_BOTH || plugin.menuHighlightMode == GroundItems.MENU_NAME) {
				target = recolourName(target, tag)
			}
		}
		if (plugin.showMenuItemQuantities && item.stackable && item.quantity > 1) {
			target = target + " (" + item.quantity + ")"
		}
		if (target != entry.target()) {
			client.retargetMenuEntry(i, target)
		}
		return item.hidden && !item.highlighted && plugin.deprioritizeHiddenItems
	}

	/** The name's colour tag (the last one, as `<col=ff9040>Logs` or `Use -> <col=ff9040>Logs` end) swapped for [tag]. */
	@JvmStatic
	fun recolourName(target: String, tag: String): String {
		val close: Int = target.lastIndexOf('>')
		val open: Int = target.lastIndexOf("<col=", close)
		if (close < 0 || open < 0) {
			return tag + target
		}
		return target.substring(0, open) + tag + target.substring(close + 1)
	}

	/** RuneLite's collapseEntries: a ground item entry the same as one above it (option, name, action, item, tile) goes, and the first says how many there were. */
	private fun collapse(client: ClientApi) {
		var i: Int = 0
		while (i < client.menuEntryCount()) {
			val entry: MenuEntry? = client.menuEntry(i)
			if (entry != null && (GroundItems.isGroundItemOption(entry.clickType()) || GroundItems.isGroundItemExamine(entry.clickType()))) {
				collapseInto(client, i, entry)
			}
			i++
		}
	}

	/** Takes out the entries below [i] that are the same as it, and counts them in its name. */
	private fun collapseInto(client: ClientApi, i: Int, entry: MenuEntry) {
		var count: Int = 1
		var j: Int = i + 1
		while (j < client.menuEntryCount()) {
			val other: MenuEntry? = client.menuEntry(j)
			if (other != null && same(entry, other)) {
				client.removeMenuEntry(j)
				count++
			} else {
				j++
			}
		}
		if (count > 1) {
			client.retargetMenuEntry(i, (entry.target() ?: "") + " x " + count)
		}
	}

	private fun same(a: MenuEntry, b: MenuEntry): Boolean {
		return a.clickType() == b.clickType() && a.entityUid() == b.entityUid() && a.param0() == b.param0() && a.param1() == b.param1() && a.option() == b.option() && a.target() == b.target()
	}
}
