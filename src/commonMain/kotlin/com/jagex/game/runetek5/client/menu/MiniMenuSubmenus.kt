package com.jagex.game.runetek5.client.menu

import com.jagex.game.runetek5.InterfaceRectangles
import com.jagex.game.runetek5.graphics.Toolkit
import com.jagex.game.runetek5.graphics.font.FontMetrics
import com.jagex.game.runetek5.plugin.PluginRuntime
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: submenus in the plain (uncollapsed) menu, for the plugins'
 * entries that open one (PluginRuntime.addSubmenu). The collapsed menu has its own submenus
 * (MiniMenu.activeSubmenu, the entries of one target grouped); a plugin's submenu is shown the same
 * way, in the same place beside the menu, so a click on it goes through the menu's own submenu click
 * (MiniMenuInputTick). These are the pieces the plain menu lacks: opening one on hover and drawing it.
 */
object MiniMenuSubmenus {

	/** The plain menu's row [row] from the top: its text's baseline. */
	private fun rowBaseline(row: Int): Int {
		return MiniMenu.menuOpenY + row * 16 + (if (FontMetrics.useBoldFont) 33 else 31)
	}

	/** While the plain menu is open with the mouse on it: a hovered plugin entry with a submenu opens it. */
	@JvmStatic
	fun hoverPlain(mouseY: Int) {
		var row: Int = -1
		for (i in 0 until MiniMenu.entryCount) {
			val baseline: Int = rowBaseline(i)
			if (baseline - 13 < mouseY && mouseY < baseline + (if (FontMetrics.useBoldFont) 4 else 3)) {
				row = i
				break
			}
		}
		if (row < 0) {
			return
		}
		// the list's last entry is the top row
		var entry: MiniMenuEntry? = MiniMenu.entryList.last() as MiniMenuEntry?
		var i: Int = 0
		while (entry != null && i < row) {
			entry = MiniMenu.entryList.previous() as MiniMenuEntry?
			i++
		}
		val submenu: ColoredMiniMenuEntry = PluginRuntime.submenuOf(entry) ?: return
		if (MiniMenu.activeSubmenu !== submenu) {
			MiniMenu.openSubmenu(submenu, mouseY, rowBaseline(row) - 13)
		}
	}

	/** The open submenu over the plain menu in the simple style (MiniMenu.renderOpenMenuSimple's collapsed branch, for the plain one). */
	@JvmStatic
	fun drawSimple(toolkit: Toolkit?, mouseX: Int, mouseY: Int) {
		val submenu: ColoredMiniMenuEntry = MiniMenu.activeSubmenu ?: return
		MiniMenu.drawSimpleMenuBox(MiniMenu.activeSubmenuWidth, MiniMenu.activeSubmenuY, toolkit, MiniMenu.activeSubmenuX, MiniMenu.activeSubmenuHeight)
		MiniMenu.minimenuHeader!!.drawString(submenu.tagStart, -10660793, MiniMenu.activeSubmenuY + 14, MiniMenu.activeSubmenuX + 3, -1)
		var index: Int = 0
		var entry: MiniMenuEntry? = submenu.nodeQueue.first() as MiniMenuEntry?
		while (entry != null) {
			val y: Int = MiniMenu.activeSubmenuY + index * 16 + 31
			index++
			MiniMenu.drawMenuEntry(MiniMenu.activeSubmenuY, -256, y, toolkit, entry, MiniMenu.activeSubmenuWidth, mouseY, -1, MiniMenu.activeSubmenuHeight, MiniMenu.activeSubmenuX, mouseX)
			entry = submenu.nodeQueue.next() as MiniMenuEntry?
		}
		InterfaceRectangles.markRectanglesForFlip(MiniMenu.activeSubmenuWidth, MiniMenu.activeSubmenuY, MiniMenu.activeSubmenuX, MiniMenu.activeSubmenuHeight)
	}

	/** The open submenu over the plain menu in the styled look (MiniMenu.renderOpenMenuStyled's collapsed branch, for the plain one). */
	@JvmStatic
	fun drawStyled(toolkit: Toolkit?, mouseX: Int, mouseY: Int) {
		val submenu: ColoredMiniMenuEntry = MiniMenu.activeSubmenu ?: return
		MiniMenu.drawStyledMenuHeader(MiniMenu.activeSubmenuWidth, MiniMenu.activeSubmenuX, toolkit, submenu.tagStart, MiniMenu.activeSubmenuY, MiniMenu.activeSubmenuHeight)
		var index: Int = 0
		var entry: MiniMenuEntry? = submenu.nodeQueue.first() as MiniMenuEntry?
		while (entry != null) {
			val y: Int = index * 16 + MiniMenu.activeSubmenuY + 20 + 13
			if (MiniMenu.activeSubmenuX < mouseX && MiniMenu.activeSubmenuX + MiniMenu.activeSubmenuWidth > mouseX && mouseY > y - 13 && y + 4 > mouseY && entry.showOtherLevelIcon) {
				toolkit!!.aa(MiniMenu.activeSubmenuX, y - 12, MiniMenu.activeSubmenuWidth, 16, ((255 - MiniMenu.menuHighlightTransparency) shl 24) or MiniMenu.menuHighlightColour, 1)
			}
			index++
			entry = submenu.nodeQueue.next() as MiniMenuEntry?
		}
		MiniMenu.drawStyledMenuBorder(MiniMenu.activeSubmenuX, MiniMenu.activeSubmenuWidth, MiniMenu.activeSubmenuHeight, MiniMenu.activeSubmenuY, toolkit)
		index = 0
		entry = submenu.nodeQueue.first() as MiniMenuEntry?
		while (entry != null) {
			val y: Int = index * 16 + MiniMenu.activeSubmenuY + 13 + 20
			index++
			MiniMenu.drawMenuEntry(MiniMenu.activeSubmenuY, MiniMenu.menuHoverTextColour or 0xFF000000.toInt(), y, toolkit, entry, MiniMenu.activeSubmenuWidth, mouseY, MiniMenu.menuTextColour or 0xFF000000.toInt(), MiniMenu.activeSubmenuHeight, MiniMenu.activeSubmenuX, mouseX)
			entry = submenu.nodeQueue.next() as MiniMenuEntry?
		}
		InterfaceRectangles.markRectanglesForFlip(MiniMenu.activeSubmenuWidth, MiniMenu.activeSubmenuY, MiniMenu.activeSubmenuX, MiniMenu.activeSubmenuHeight)
	}
}
