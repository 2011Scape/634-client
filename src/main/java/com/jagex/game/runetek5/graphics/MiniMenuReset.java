package com.jagex.game.runetek5.graphics;

import com.jagex.game.runetek5.client.menu.MiniMenuEntry;
import com.jagex.game.runetek5.ResumePauseButtonSender;
import com.jagex.game.runetek5.client.menu.ColoredMiniMenuEntry;
import com.jagex.game.runetek5.graphics.gl.GlxMatrixScratch;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.menu.MiniMenu;

public final class MiniMenuReset {

	@OriginalMember(owner = "client!nca", name = "b", descriptor = "(B)V")
	public static void resetEntries() {
		for (@Pc(10) ColoredMiniMenuEntry miniMenuEntry = (ColoredMiniMenuEntry) MiniMenu.sortedColoredEntries.first(); miniMenuEntry != null; miniMenuEntry = (ColoredMiniMenuEntry) MiniMenu.sortedColoredEntries.next()) {
			if (miniMenuEntry.queueSize > 1) {
				miniMenuEntry.queueSize = 0;
				MiniMenu.coloredEntryCache.put(miniMenuEntry, ((MiniMenuEntry) miniMenuEntry.nodeQueue.sentinel.secondaryNext).bitpacked);
				miniMenuEntry.nodeQueue.clear();
			}
		}
		MiniMenu.coloredEntryCount = 0;
		MiniMenu.entryCount = 0;
		MiniMenu.entryList.clear();
		MiniMenu.coloredEntryMap.clear();
		MiniMenu.sortedColoredEntries.clear();
		MiniMenu.addEntry(MiniMenu.cancelOption);
	}
}
