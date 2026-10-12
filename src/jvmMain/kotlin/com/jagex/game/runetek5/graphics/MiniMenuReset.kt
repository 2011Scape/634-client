package com.jagex.game.runetek5.graphics

import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.client.menu.MiniMenuEntry
import com.jagex.game.runetek5.client.menu.ColoredMiniMenuEntry
import com.jagex.game.runetek5.client.menu.MiniMenu

object MiniMenuReset {

	@OriginalMember(owner = "client!nca", name = "b", descriptor = "(B)V")
	@JvmStatic
	fun resetEntries() {
		@Pc(10) var miniMenuEntry: ColoredMiniMenuEntry? = MiniMenu.sortedColoredEntries.first() as ColoredMiniMenuEntry?
		while (miniMenuEntry != null) {
			if (miniMenuEntry.queueSize > 1) {
				miniMenuEntry.queueSize = 0
				MiniMenu.coloredEntryCache.put(miniMenuEntry, (miniMenuEntry.nodeQueue.sentinel.secondaryNext as MiniMenuEntry).bitpacked)
				miniMenuEntry.nodeQueue.clear()
			}
			miniMenuEntry = MiniMenu.sortedColoredEntries.next() as ColoredMiniMenuEntry?
		}
		MiniMenu.coloredEntryCount = 0
		MiniMenu.entryCount = 0
		MiniMenu.entryList.clear()
		MiniMenu.coloredEntryMap.clear()
		MiniMenu.sortedColoredEntries.clear()
		MiniMenu.addEntry(MiniMenu.cancelOption)
	}
}
