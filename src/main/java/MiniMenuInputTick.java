import com.jagex.game.runetek5.core.constants.MiniMenuAction;
import com.jagex.game.runetek5.client.menu.MiniMenu;
import com.jagex.game.runetek5.client.menu.MiniMenuEntry;
import com.jagex.game.runetek5.Preferences;
import com.jagex.game.runetek5.DraggedComponentLayer;
import com.jagex.game.runetek5.core.datastruct.key.DequeIterator;
import com.jagex.game.runetek5.cache.node.QueueIterator;
import com.jagex.game.runetek5.client.menu.ColoredMiniMenuEntry;
import com.jagex.game.runetek5.client.input.mouse.MouseRecord;
import com.jagex.game.runetek5.graphics.gl.GlxMatrixScratch;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.event.keyboard.SimpleKeyboardMonitorConstants;
import com.jagex.game.runetek5.client.event.mouse.MouseLog;
import com.jagex.game.runetek5.client.features.ClientFeatures;
import com.jagex.game.runetek5.graphics.font.FontMetrics;
import com.jagex.game.runetek5.graphics.ifcomponent.InterfaceComponentRenderer;
import com.jagex.game.runetek5.client.input.keyboard.KeyRecorder;
import com.jagex.game.runetek5.client.GameShell;
import com.jagex.game.runetek5.InterfaceDrag;
import com.jagex.game.runetek5.client.input.mouse.MouseManager;
import com.jagex.game.runetek5.client.ClientState;

public final class MiniMenuInputTick {

	@OriginalMember(owner = "client!uga", name = "c", descriptor = "(I)V")
	public static void processMenuInput() {
		if (!MiniMenu.isOpen) {
			MiniMenu.isCollapsed = MiniMenu.maxVisibleEntries != -1 && MiniMenu.entryCount >= MiniMenu.maxVisibleEntries || GameShell.screenCanvasHeight < MiniMenu.entryCount * 16 + (FontMetrics.useBoldFont ? 26 : 22);
		}
		MiniMenu.aClass341_46.clear();
		MiniMenu.aClass341_36.clear();
		@Pc(54) int local54;
		for (@Pc(49) MiniMenuEntry miniMenuEntryInner = (MiniMenuEntry) MiniMenu.entryList.first(); miniMenuEntryInner != null; miniMenuEntryInner = (MiniMenuEntry) MiniMenu.entryList.next()) {
			int entryClickType = miniMenuEntryInner.clickType;
			if (entryClickType < 1000) {
				miniMenuEntryInner.unlink();
				if (entryClickType == MiniMenuAction.TGT_GROUND || entryClickType == MiniMenuAction.TGT_LOC || entryClickType == MiniMenuAction.TGT_NPC || entryClickType == MiniMenuAction.TGT_OBJ || entryClickType == MiniMenuAction.TGT_PLAYER || entryClickType == MiniMenuAction.TGT_SELF || entryClickType == MiniMenuAction.TGT_BUTTON) {
					MiniMenu.aClass341_36.addLast(miniMenuEntryInner);
				} else {
					MiniMenu.aClass341_46.addLast(miniMenuEntryInner);
				}
			}
		}
		MiniMenu.aClass341_46.transferAllTo(MiniMenu.entryList);
		MiniMenu.aClass341_36.transferAllTo(MiniMenu.entryList);
		if (MiniMenu.entryCount > 1) {
			if (MiniMenu.shiftClickEnabled && KeyRecorder.keyRecorder.isPressed(SimpleKeyboardMonitorConstants.KEY_CODE_SHIFT) && MiniMenu.entryCount > 2) {
				MiniMenu.aClass2_Sub6_Sub3_1 = (MiniMenuEntry) MiniMenu.entryList.sentinel.previous.previous;
			} else {
				MiniMenu.aClass2_Sub6_Sub3_1 = (MiniMenuEntry) MiniMenu.entryList.sentinel.previous;
			}
			if (ClientFeatures.shiftDrop && KeyRecorder.keyRecorder.isPressed(81)) {
				// Not part of the original client: with Shift held, a left click on an inventory item drops it.
				MiniMenuEntry drop = ClientFeatures.shiftDropEntry(MiniMenu.entryList);
				if (drop != null) {
					MiniMenu.aClass2_Sub6_Sub3_1 = drop;
				}
			}
			MiniMenu.aClass2_Sub6_Sub3_3 = (MiniMenuEntry) MiniMenu.entryList.sentinel.previous;
		} else {
			MiniMenu.aClass2_Sub6_Sub3_1 = null;
			MiniMenu.aClass2_Sub6_Sub3_3 = null;
		}
		int clickType = -1;
		@Pc(158) MouseRecord mouseEvent = (MouseRecord) MouseManager.MOUSE_MOVEMENTS.first();
		if (mouseEvent != null) {
			clickType = mouseEvent.getClickType();
		}
		if (!MiniMenu.isOpen) {
			if (clickType == MouseLog.TYPE_PRESS_LEFT && (MiniMenu.menuOpenMode == 1 && MiniMenu.entryCount > 2 || MiniMenu.isTopEntryInterfaceAction())) {
				clickType = MouseLog.TYPE_PRESS_RIGHT;
			}
			if (clickType == MouseLog.TYPE_PRESS_RIGHT && MiniMenu.entryCount > 0 && mouseEvent != null) {
				if (InterfaceDrag.component == null && Preferences.favoriteWorlds == 0) {
					MiniMenu.openContextMenu(mouseEvent.getMouseX(), mouseEvent.getMouseY());
				} else {
					MiniMenu.deferredClickType = 2;
				}
			}
			if (clickType == MouseLog.TYPE_PRESS_LEFT) {
				if (MiniMenu.aClass2_Sub6_Sub3_1 != null) {
					MiniMenuDefaultClick.clickDefaultEntry();
				} else if (MiniMenu.interfaceSelectionActive) {
					Static657.cancelInterfaceSelection();
				}
			}
			if (InterfaceDrag.component != null || Preferences.favoriteWorlds != 0) {
				return;
			}
			MiniMenu.deferredEntry = null;
			MiniMenu.deferredClickType = 0;
			return;
		}
		@Pc(173) int mouseX;
		@Pc(177) int mouseY;
		@Pc(256) int local256;
		@Pc(258) int local258;
		if (clickType == -1) {
			mouseX = MouseRecord.mouseRecorder.getMouseX();
			mouseY = MouseRecord.mouseRecorder.getMouseY();
			@Pc(179) boolean isInSubmenuBounds = false;
			if (MiniMenu.activeSubmenu != null) {
				if (MiniMenu.activeSubmenuX - 10 <= mouseX && mouseX <= MiniMenu.activeSubmenuX + MiniMenu.activeSubmenuWidth + 10 && MiniMenu.activeSubmenuY - 10 <= mouseY && mouseY <= MiniMenu.activeSubmenuHeight + MiniMenu.activeSubmenuY + 10) {
					isInSubmenuBounds = true;
				} else {
					MiniMenu.closeSubmenu();
				}
			}
			if (!isInSubmenuBounds) {
				if (MiniMenu.menuOpenX - 10 > mouseX || mouseX > MiniMenu.menuOpenX + MiniMenu.menuOpenWidth + 10 || mouseY < MiniMenu.menuOpenY - 10 || MiniMenu.menuOpenY + InterfaceComponentRenderer.menuHeight + 10 < mouseY) {
					MiniMenu.close();
				} else if (MiniMenu.isCollapsed) {
					int hoveredRow = -1;
					int hoveredRowY = -1;
					@Pc(272) int local272;
					for (@Pc(260) int i = 0; i < MiniMenu.coloredEntryCount; i++) {
						if (FontMetrics.useBoldFont) {
							int entryY = i * 16 + MiniMenu.menuOpenY + 33;
							if (entryY - 13 < mouseY && entryY + 4 > mouseY) {
								hoveredRowY = entryY - 13;
								hoveredRow = i;
								break;
							}
						} else {
							int entryY = i * 16 + MiniMenu.menuOpenY + 31;
							if (mouseY > entryY - 13 && mouseY < entryY + 3) {
								hoveredRowY = entryY - 13;
								hoveredRow = i;
								break;
							}
						}
					}
					if (hoveredRow != -1) {
						int entryPosition = 0;
						@Pc(343) QueueIterator queueIterator = new QueueIterator(MiniMenu.sortedColoredEntries);
						for (@Pc(348) ColoredMiniMenuEntry miniMenuEntry = (ColoredMiniMenuEntry) queueIterator.first(); miniMenuEntry != null; miniMenuEntry = (ColoredMiniMenuEntry) queueIterator.next()) {
							if (entryPosition == hoveredRow) {
								if (miniMenuEntry.queueSize > 1) {
									MiniMenu.openSubmenu(miniMenuEntry, mouseY, hoveredRowY);
								}
								break;
							}
							entryPosition++;
						}
					}
				}
			}
		}
		if (clickType != MouseLog.TYPE_PRESS_LEFT) {
			return;
		}
		mouseX = mouseEvent.getMouseX();
		mouseY = mouseEvent.getMouseY();
		@Pc(414) int hoveredIndex;
		@Pc(487) QueueIterator queueIterator2;
		@Pc(492) MiniMenuEntry miniMenuEntryInner2;
		if (MiniMenu.activeSubmenu != null && mouseX >= MiniMenu.activeSubmenuX && MiniMenu.activeSubmenuWidth + MiniMenu.activeSubmenuX >= mouseX && mouseY >= MiniMenu.activeSubmenuY && MiniMenu.activeSubmenuY + MiniMenu.activeSubmenuHeight >= mouseY) {
			hoveredIndex = -1;
			for (int entryIndex = 0; entryIndex < MiniMenu.activeSubmenu.queueSize; entryIndex++) {
				if (FontMetrics.useBoldFont) {
					int entryY = MiniMenu.activeSubmenuY + entryIndex * 16 + 33;
					if (mouseY > entryY - 13 && entryY + 4 > mouseY) {
						hoveredIndex = entryIndex;
					}
				} else {
					int entryY = entryIndex * 16 + MiniMenu.activeSubmenuY + 31;
					if (entryY - 13 < mouseY && entryY + 3 > mouseY) {
						hoveredIndex = entryIndex;
					}
				}
			}
			if (hoveredIndex != -1) {
				int entryPosition = 0;
				queueIterator2 = new QueueIterator(MiniMenu.activeSubmenu.nodeQueue);
				for (miniMenuEntryInner2 = (MiniMenuEntry) queueIterator2.first(); miniMenuEntryInner2 != null; miniMenuEntryInner2 = (MiniMenuEntry) queueIterator2.next()) {
					if (hoveredIndex == entryPosition) {
						MiniMenuActionExecutor.processMenuClick(miniMenuEntryInner2, mouseY, mouseX);
						break;
					}
					entryPosition++;
				}
			}
			MiniMenu.close();
			return;
		}
		if (mouseX < MiniMenu.menuOpenX || mouseX > MiniMenu.menuOpenX + MiniMenu.menuOpenWidth || mouseY < MiniMenu.menuOpenY || mouseY > MiniMenu.menuOpenY + InterfaceComponentRenderer.menuHeight) {
			return;
		}
		if (MiniMenu.isCollapsed) {
			hoveredIndex = -1;
			for (int entryIndex = 0; entryIndex < MiniMenu.coloredEntryCount; entryIndex++) {
				if (FontMetrics.useBoldFont) {
					int entryY = MiniMenu.menuOpenY + entryIndex * 16 + 33;
					if (entryY - 13 < mouseY && mouseY < entryY + 4) {
						hoveredIndex = entryIndex;
						break;
					}
				} else {
					int entryY = MiniMenu.menuOpenY + entryIndex * 16 + 31;
					if (entryY - 13 < mouseY && entryY + 3 > mouseY) {
						hoveredIndex = entryIndex;
						break;
					}
				}
			}
			if (hoveredIndex != -1) {
				int entryPosition = 0;
				queueIterator2 = new QueueIterator(MiniMenu.sortedColoredEntries);
				for (@Pc(632) ColoredMiniMenuEntry miniMenuEntry2 = (ColoredMiniMenuEntry) queueIterator2.first(); miniMenuEntry2 != null; miniMenuEntry2 = (ColoredMiniMenuEntry) queueIterator2.next()) {
					if (hoveredIndex == entryPosition) {
						MiniMenuActionExecutor.processMenuClick((MiniMenuEntry) miniMenuEntry2.nodeQueue.sentinel.secondaryNext, mouseY, mouseX);
						MiniMenu.close();
						return;
					}
					entryPosition++;
				}
				return;
			}
		} else {
			hoveredIndex = -1;
			for (int entryIndex = 0; entryIndex < MiniMenu.entryCount; entryIndex++) {
				if (FontMetrics.useBoldFont) {
					int entryY = MiniMenu.menuOpenY + (MiniMenu.entryCount - (1 - -entryIndex)) * 16 + 33;
					if (mouseY > entryY - 13 && mouseY < entryY + 4) {
						hoveredIndex = entryIndex;
					}
				} else {
					int entryY = MiniMenu.menuOpenY + (MiniMenu.entryCount - entryIndex - 1) * 16 + 31;
					if (mouseY > entryY - 13 && entryY + 3 > mouseY) {
						hoveredIndex = entryIndex;
					}
				}
			}
			if (hoveredIndex != -1) {
				int entryPosition = 0;
				@Pc(756) DequeIterator dequeIterator = new DequeIterator(MiniMenu.entryList);
				for (miniMenuEntryInner2 = (MiniMenuEntry) dequeIterator.first(); miniMenuEntryInner2 != null; miniMenuEntryInner2 = (MiniMenuEntry) dequeIterator.next()) {
					if (hoveredIndex == entryPosition) {
						MiniMenuActionExecutor.processMenuClick(miniMenuEntryInner2, mouseY, mouseX);
						break;
					}
					entryPosition++;
				}
			}
			MiniMenu.close();
		}
		return;
	}
}
